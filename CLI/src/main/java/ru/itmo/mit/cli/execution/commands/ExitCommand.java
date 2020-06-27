package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Exits application
 */
public class ExitCommand extends Command {

    public ExitCommand(List<CommandWord> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        System.exit(0);
        return CommandExecuted.getInstance();
    }

    @Override
    public String getCommandName() {
        return "exit";
    }

}
