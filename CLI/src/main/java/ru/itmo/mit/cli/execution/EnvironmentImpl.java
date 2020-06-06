package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class EnvironmentImpl implements Environment {

    private final Path workingDirectory;
    private final Namespace namespace;
    private final OutputStream finalStream;
    private final Charset charset;

    public EnvironmentImpl(Path workingDirectory,
                           Namespace namespace,
                           OutputStream finalStream,
                           Charset charset) {
        this.workingDirectory = workingDirectory;
        this.namespace = namespace;
        this.finalStream = finalStream;
        this.charset = charset;
    }

    @Override
    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public OutputStream getFinalStream() {
        return finalStream;
    }

    @Override
    public void modifyNamespace(String varName, String varValue) {
        namespace.put(varName, varValue);
    }

    @Override
    public void executeCommands(PipedCommands commands) {
        InputStream prevIstream = getEmptyInputStream();
        for (Command command : commands.getCommandList()) {
            try (PipedOutputStream outStream = new PipedOutputStream();
                 PipedInputStream inputStream = new PipedInputStream(outStream)) {
                CommandExecutionResult result = command.execute(this,
                        prevIstream,
                        outStream);
                if (!processExecutionResult(result)) {
                    return;
                }
                //outStream.flush();
                outStream.close();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                inputStream.transferTo(byteArrayOutputStream);
                prevIstream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }
            catch (IOException e) {
                return; // Add exception processing
            }
        }
        toFinalStream(prevIstream);
    }

    private InputStream getEmptyInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    @Override
    public void println(String text) {
        try {
            finalStream.write(text.getBytes(charset));
            finalStream.write("\n".getBytes(charset));
        }
        catch (IOException e) {
            // Do something
        }
    }

    private void toFinalStream(InputStream istream) {
        try {
            finalStream.write(istream.readAllBytes());
        }
        catch (IOException e) {
            // Do something
        }
    }

    private boolean processExecutionResult(CommandExecutionResult result) {
        if (result instanceof CommandExecuted) {
            return true;
        }
        else if (result instanceof FailedToExecute) {
            println(((FailedToExecute) result).getErrorMessage());
        }
        return false;
    }
}
