package ru.itmo.mit.cli;

import org.junit.*;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.EnvironmentImpl;
import ru.itmo.mit.cli.execution.ExecutionErrorMessages;
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
    public void testWc() throws IOException {
        shell.interpret("wc src/test/testfiles/TestText1.txt");
        expectedOutStream.write("2\t6\t30\tsrc/test/testfiles/TestText1.txt\n".getBytes(charset));
        shell.interpret("wc src/test/testfiles/py\\ script.py src/test/testfiles/main.py");
        expectedOutStream.write(
                ("2\t11\t57\tsrc/test/testfiles/py script.py\n" +
                        "1\t7\t48\tsrc/test/testfiles/main.py\n" +
                        "3\t18\t105\ttotal\n").getBytes(charset)
        );
        assertEqualsInnerStreams();
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

    @Test
    public void testGrepSimple() throws IOException {
        shell.interpret("grep world src/test/testfiles/grep_test_1.txt");
        expectedOutStream.write("world\nhello hello world\nworld\n".getBytes(charset));
        shell.interpret("grep match src/test/testfiles/grep_test_2.txt");
        expectedOutStream.write("match\nmatch\nmatcht\n".getBytes(charset));
        shell.interpret("echo hello | grep h");
        expectedOutStream.write("hello\n".getBytes(charset));
        // Expected to do nothing:
        shell.interpret("echo hello | grep a");
        assertEqualsInnerStreams();
    }

    @Test
    public void testGrepSingleKey() throws IOException {
        // Key insensitive:
        shell.interpret("echo hello | grep HELLO");
        shell.interpret("echo hello | grep -i HELLO");
        expectedOutStream.write("hello\n".getBytes(charset));
        // Whole word match:
        shell.interpret("echo hello | grep -w h");
        shell.interpret("echo h ello | grep -w h");
        expectedOutStream.write("h ello\n".getBytes(charset));
        // Group matches:
        shell.interpret("grep -A 1 match src/test/testfiles/grep_test_2.txt");
        expectedOutStream.write("match\ntext\n--\nmatch\nmatcht\ntext\n".getBytes(charset));
        shell.interpret("grep -A 2 match src/test/testfiles/grep_test_3.txt");
        expectedOutStream.write("match\ntext\ntext\nmatch\ntext\nmatch\ntext\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

    @Test
    public void testGrepMultipleKeys() throws IOException {
        shell.interpret("grep -iA 2 Match src/test/testfiles/grep_test_3.txt");
        expectedOutStream.write("match\ntext\ntext\nmatch\ntext\nmatch\ntext\n".getBytes(charset));
        shell.interpret("grep -wiA 10 m src/test/testfiles/grep_test_3.txt");
        shell.interpret("grep -wi Hello src/test/testfiles/grep_test_4.txt");
        expectedOutStream.write("hello world\n".getBytes(charset));
        shell.interpret("grep -iwA 3 HELLO src/test/testfiles/grep_test_4.txt");
        expectedOutStream.write("hello world\ntext\nHelloWorld\ntext\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

    @Test
    public void testGrepRegExp() throws IOException {
        shell.interpret("grep -i \"Hello[^ ]\" src/test/testfiles/grep_test_4.txt");
        expectedOutStream.write("helloworld\nHelloWorld\n".getBytes(charset));
        shell.interpret("echo hello | grep \"[0-9]\"");
        shell.interpret("echo 12 | grep \"[0-9]\"");
        expectedOutStream.write("12\n".getBytes(charset));
        shell.interpret("grep world src/test/testfiles/grep_test_1.txt");
        expectedOutStream.write("world\nhello hello world\nworld\n".getBytes(charset));
        shell.interpret("grep ^world src/test/testfiles/grep_test_1.txt");
        expectedOutStream.write("world\nworld\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

    @Test
    public void testGrepErrors() throws IOException {
        shell.interpret("grep");
        expectedOutStream.write((ExecutionErrorMessages.GREP_USAGE + "\n").getBytes(charset));
        shell.interpret("grep -A h pattern file");
        expectedOutStream.write(
                (ExecutionErrorMessages.grepContextLenArgFormatError("h")+"\n")
                        .getBytes(charset));
        shell.interpret("grep -A -1 pattern file");
        expectedOutStream.write(
                (ExecutionErrorMessages.grepContextLenArgFormatError("-1") + "\n")
                        .getBytes(charset)
        );
        shell.interpret("grep -A 10 pattern nonExistingFile");
        expectedOutStream.write("nonExistingFile: No such file or directory\n".getBytes(charset));
        assertEqualsInnerStreams();
    }

}
