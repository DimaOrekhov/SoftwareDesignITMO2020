package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class OtherCommand extends Command {

    public OtherCommand(List<String> commandArgs) {
        super(commandArgs);
    }

    @Override
    public void execute(InputStream inStream, OutputStream outStream) {

    }
}
