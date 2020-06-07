package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandExecuted;
import ru.itmo.mit.cli.execution.domain.CommandExecutionResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExitCommand extends Command {

    public ExitCommand(List<String> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        System.exit(0);
        return CommandExecuted.getInstance();
    }

}
