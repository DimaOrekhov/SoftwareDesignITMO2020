package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandExecuted;
import ru.itmo.mit.cli.execution.domain.CommandExecutionResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CatCommand extends Command {

    public CatCommand(List<String> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) {
        return CommandExecuted.getInstance();
    }
}
