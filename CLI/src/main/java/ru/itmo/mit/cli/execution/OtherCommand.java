package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandExecuted;
import ru.itmo.mit.cli.execution.domain.CommandExecutionResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class OtherCommand extends Command {

    private final String commandName;

    public OtherCommand(String commandName, List<String> commandArgs) {
        super(commandArgs);
        this.commandName = commandName;
    }

    @Override
    public CommandExecutionResult execute(Environment environment, InputStream inStream, OutputStream outStream) {
        return CommandExecuted.getInstance();
    }

    private String commandAsString() {
        return null;
    }
}
