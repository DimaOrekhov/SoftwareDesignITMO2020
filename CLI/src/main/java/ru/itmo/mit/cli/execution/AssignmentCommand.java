package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;

import java.io.InputStream;
import java.io.OutputStream;

public class AssignmentCommand extends Command {

    @Override
    public void execute(InputStream inStream, OutputStream outStream) {
        // Просто опустоши inStream и все
    }
}
