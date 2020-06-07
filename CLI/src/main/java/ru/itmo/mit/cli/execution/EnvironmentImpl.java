package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

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

    @Override
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
        InputStream prevIstream = StreamUtils.getEmptyInputStream();
        int i = 0;
        for (Command command : commands.getCommandList()) {
            if (i == commands.getCommandList().size() - 1) {
                try {
                    command.execute(this, prevIstream, finalStream);
                }
                catch (IOException e) {

                }
                i++;
                continue;
            }
            try (PipedOutputStream outStream = new PipedOutputStream();
                 PipedInputStream inputStream = new PipedInputStream(outStream)) {

                CommandExecutionResult result = command.execute(this,
                        prevIstream,
                        outStream);
                processExecutionResult(result);
                outStream.close();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                inputStream.transferTo(byteArrayOutputStream);
                prevIstream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }
            catch (IOException e) {
                throw new RuntimeException("IO fail");
            }
            i++;
        }
/*        try {
            prevIstream.transferTo(finalStream);
        }
        catch (IOException e) {
            throw new RuntimeException("IO fail");
        }*/
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

    private void processExecutionResult(CommandExecutionResult result) {
        // Just prints an error message
        if (result instanceof FailedToExecute) {
            println(((FailedToExecute) result).getErrorMessage());
        }
    }
}
