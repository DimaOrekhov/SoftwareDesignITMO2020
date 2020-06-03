package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;

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
    public void execute(InputStream inStream, OutputStream outStream) {

    }

    private String commandAsString() {
        return null;
    }
}
