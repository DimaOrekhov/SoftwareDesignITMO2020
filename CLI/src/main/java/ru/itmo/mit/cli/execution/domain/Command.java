package ru.itmo.mit.cli.execution.domain;

import ru.itmo.mit.cli.execution.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Command {

    protected final List<String> args;

    protected Command(List<String> args) {
        this.args = args;
    }

    public abstract CommandExecutionResult execute(Environment environment,
                                                   InputStream inStream,
                                                   OutputStream outStream) throws IOException;

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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(classToName.getOrDefault(this.getClass(), ""))
                .append(" \'")
                .append(String.join("\' \'", args))
                .append("\'");
        return stringBuilder.toString();
    }

    private final static Map<Class<?>, String> classToName = new HashMap<>() {
        {
            put(CatCommand.class, "cat");
            put(EchoCommand.class, "echo");
            put(WcCommand.class, "wc");
            put(ExitCommand.class, "exit");
            put(PwdCommand.class, "pwd");
        }
    };
}
