package ru.itmo.mit.cli.execution.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public abstract class Command {

    private final List<String> args;

    public Command() {
        args = new LinkedList<>();
    }

    public Command(List<String> args) {
        this.args = args;
    }

    public abstract void execute(InputStream inStream, OutputStream outStream);
}
