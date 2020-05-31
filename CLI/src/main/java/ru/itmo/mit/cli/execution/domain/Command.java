package ru.itmo.mit.cli.execution.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public abstract class Command {

    private final String commandName;
    private final List<String> args;

    public Command(String commandName, List<String> args) {
        this.commandName = commandName;
        this.args = args;
    }

    public abstract void execute(InputStream inStream, OutputStream outStream);
}
