package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.exceptions.IOFailException;
import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Environment interface implementation
 */
public class EnvironmentImpl implements Environment {

    private final Path workingDirectory;
    private final Namespace namespace;
    private final OutputStream finalStream;
    private final Charset charset;

    /**
     *
     * @param workingDirectory starting working directory
     * @param namespace underlying Namespace
     * @param finalStream stdout of Environment
     * @param charset encoding of Environment
     */
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

    @Override
    public void modifyNamespace(String varName, String varValue) {
        namespace.put(varName, varValue);
    }

    /**
     * Executes chain of piped commands
     * @param commands
     */
    @Override
    public void executeCommands(PipedCommands commands) {
        InputStream prevIstream = System.in;
        int i = 0;
        for (Command command : commands.getCommandList()) {
            // In case command is the last in a chain
            // redirect its stdout right into finalStream
            if (i == commands.getCommandList().size() - 1) {
                try {
                    command.execute(this, prevIstream, finalStream);
                }
                catch (IOException e) {
                    throw new IOFailException(e);
                }
                i++;
                continue;
            }
            // Otherwise, create two piped streams
            try (PipedOutputStream outStream = new PipedOutputStream();
                 PipedInputStream inputStream = new PipedInputStream(outStream)) {

                CommandExecutionResult result = command.execute(this,
                        prevIstream,
                        outStream);
                processExecutionResult(result);
                outStream.close();
                // Transfer contents of PipedInputStream to ByteArrayInputStream,
                // so it could be used by next piped command
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                inputStream.transferTo(byteArrayOutputStream);
                prevIstream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                throw new IOFailException(e);
            }
            i++;
        }
    }

    /**
     * Prints into environment's final stream adding trailing new line character
     * @param text
     */
    @Override
    public void println(String text) {
        try {
            finalStream.write(text.getBytes(charset));
            finalStream.write("\n".getBytes(charset));
        }
        catch (IOException e) {
            throw new IOFailException(e);
        }
    }

    /**
     * If an error has occurred during execution
     * this method prints the error message
     */
    private void processExecutionResult(CommandExecutionResult result) {
        if (result instanceof FailedToExecute) {
            println(((FailedToExecute) result).getErrorMessage());
        }
    }
}
