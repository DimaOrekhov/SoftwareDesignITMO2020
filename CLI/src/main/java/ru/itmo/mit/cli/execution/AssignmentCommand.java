package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AssignmentCommand extends Command {

    public AssignmentCommand(List<String> args) {
        super(args);
    }

    @Override
    public void execute(InputStream inStream, OutputStream outStream) {
        // Просто опустоши inStream и все
    }
}
