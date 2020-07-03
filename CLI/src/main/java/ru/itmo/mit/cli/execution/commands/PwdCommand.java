package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Prints current working directory of Environment to outStream
 */
public class PwdCommand extends Command {

    public PwdCommand(List<CommandWord> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        String workingDirectory = environment.getWorkingDirectory().toString() + "\n";
        outStream.write(workingDirectory.getBytes(environment.getCharset()));
        return CommandExecuted.getInstance();
    }

    @Override
    public String getCommandName() {
        return "pwd";
    }
}
