package ru.itmo.mit.cli.execution.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public abstract class Command {

    protected final List<String> args;

    protected Command(List<String> args) {
        this.args = args;
    }

    public abstract CommandExecutionResult execute(Environment environment,
                                                   InputStream inStream,
                                                   OutputStream outStream);

    @Override
    public boolean equals(Object obj) {
        if (this.getClass().equals(obj.getClass())) {
            return this.args.equals(((Command)obj).args);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.args);
    }
}
