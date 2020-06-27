package ru.itmo.mit.cli.execution.domain;

import ru.itmo.mit.cli.execution.commands.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Command {

    protected final List<CommandWord> args;

    protected Command(List<CommandWord> args) {
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
        String commandName = classToName.getOrDefault(this.getClass(), "");
        return commandName + " " + args.stream()
                .map(CommandWord::getRawValue)
                .collect(Collectors.joining(" "));
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
