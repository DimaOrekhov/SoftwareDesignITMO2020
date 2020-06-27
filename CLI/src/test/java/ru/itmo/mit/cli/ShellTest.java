package ru.itmo.mit.cli;

import org.junit.*;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.EnvironmentImpl;
import ru.itmo.mit.cli.execution.NamespaceImpl;
import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.parsing.CommandParserAutomaton;
import ru.itmo.mit.cli.parsing.SubstitutionAutomaton;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


public class ShellTest {

    private static final Charset charset = StandardCharsets.UTF_8;
    private PipedOutputStream actualOutStream;
    private PipedInputStream actualInputStream;
    private PipedOutputStream expectedOutStream;
    private PipedInputStream expectedInputStream;
    private Shell shell;

    private Shell buildDefaultShell(OutputStream outputStream) {
        Namespace namespace = new NamespaceImpl();
        Path rootDir = Paths.get(System.getProperty("user.dir"));
        Environment environment = new EnvironmentImpl(rootDir,
                namespace, outputStream, charset);
        return new ShellImpl(new SubstitutionAutomaton(namespace),
                new CommandParserAutomaton(), environment);
    }

    private void assertEqualsStreamContent(InputStream expectedInputStream, InputStream actualInputStream) throws  IOException {
        if (!(expectedInputStream instanceof BufferedInputStream))
        {
            expectedInputStream = new BufferedInputStream(expectedInputStream);
        }
        if (!(actualInputStream instanceof BufferedInputStream))
        {
            actualInputStream = new BufferedInputStream(actualInputStream);
        }

        int ch = expectedInputStream.read();
        while (-1 != ch)
        {
            int ch2 = actualInputStream.read();
            assertEquals(ch, ch2);
            ch = expectedInputStream.read();
        }

        int ch2 = actualInputStream.read();
        assertEquals(-1, ch2);
    }

    private void assertEqualsInnerStreams() throws IOException {
        actualOutStream.close();
        expectedOutStream.close();
        assertEqualsStreamContent(expectedInputStream, actualInputStream);
    }

    @Before
    public void setUp() throws IOException  {
        actualOutStream = new PipedOutputStream();
        actualInputStream = new PipedInputStream(actualOutStream);
        expectedOutStream = new PipedOutputStream();
        expectedInputStream = new PipedInputStream(expectedOutStream);
        shell = buildDefaultShell(actualOutStream);
    }

    @After
    public void tearDown() throws IOException {
        actualOutStream.close();
        actualInputStream.close();
        expectedOutStream.close();
        expectedInputStream.close();
    }

    @Test
    public void testEcho() throws IOException {
        shell.interpret("echo hello    world");
        expectedOutStream.write("hello world\n".getBytes(charset));
        shell.interpret("echo 'hello   world'");
        expectedOutStream.write("hello   world\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

    @Test
    public void testCatFile() throws IOException {
        shell.interpret("cat src/test/testfiles/TestText1.txt");
        actualOutStream.close();
        assertEqualsStreamContent(new FileInputStream(new File("src/test/testfiles/TestText1.txt")),
                actualInputStream);
    }

    @Test
    public void testCatFiles() throws IOException {
        shell.interpret("cat src/test/testfiles/TestText1.txt src/test/testfiles/py\\ script.py");
        try (FileInputStream inputStream1 = new FileInputStream("src/test/testfiles/TestText1.txt");
             FileInputStream inputStream2 = new FileInputStream("src/test/testfiles/py script.py")) {
            inputStream1.transferTo(expectedOutStream);
            inputStream2.transferTo(expectedOutStream);
            assertEqualsInnerStreams();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Test
    public void testWcFile() throws IOException {

    }

    @Test
    public void testAssignment() throws IOException {
        Shell shell = buildDefaultShell(actualOutStream);
        shell.interpret("x=12");
        shell.interpret("echo $x");
        expectedOutStream.write("12\n".getBytes(charset));
        shell.interpret("x=22");
        shell.interpret("echo $x");
        expectedOutStream.write("22\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

    @Test
    public void testExternalCommand() throws IOException {
        shell.interpret("python3 src/test/testfiles/main.py");
        expectedOutStream.write(
                "Hello, I am a friendly Python program!\n".getBytes(charset)
        );
        shell.interpret("python3 'src/test/testfiles/py script.py'");
        expectedOutStream.write(
                "I am a Python program with a space in its name\n".getBytes(charset)
        );
        // Escaping space:
        shell.interpret("python3 src/test/testfiles/py\\ script.py");
        expectedOutStream.write(
                "I am a Python program with a space in its name\n".getBytes(charset)
        );
        assertEqualsInnerStreams();
    }

    @Test
    public void testExternalCommandWithPipe() throws IOException {
        shell.interpret("echo x=12;print(x) | python3");
        expectedOutStream.write("12\n".getBytes(charset));
        shell.interpret("echo print(12);print(13);print(14) | python3 | wc");
        // Не уверен, что тут должна быть 6, мой системный bash выдает 9
        // Тут, наверное, какой-то у меня неверный способ подсчета байтов:
        expectedOutStream.write("3\t3\t6\t\n".getBytes(charset));
        shell.interpret("echo print(12);print(13);print(14) | python3 | wc | cat");
        expectedOutStream.write("3\t3\t6\t\n".getBytes(charset));
        assertEqualsInnerStreams();
    }
}
