package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CommandImpl extends Command {

    public CommandImpl(String commandName, List<String> arguments) {
        super(commandName, arguments);
    }

    @Override
    public void execute(InputStream inStream, OutputStream outStream) {

    }
}
