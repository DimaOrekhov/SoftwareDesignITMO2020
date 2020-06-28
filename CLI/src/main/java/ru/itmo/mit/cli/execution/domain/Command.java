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

/**
 * Command abstract class
 */
public abstract class Command {

    protected final List<CommandWord> args;

    /**
     * @param args List of command's arguments
     */
    protected Command(List<CommandWord> args) {
        this.args = args;
    }

    /**
     * Execute command in some environment with given streams as stdin and stdout
     * @param environment Environment in which command is to be executed
     * @param inStream command's stdin
     * @param outStream command's stdout
     * @return object representing result of an execution
     * @throws IOException
     */
    public abstract CommandExecutionResult execute(Environment environment,
                                                   InputStream inStream,
                                                   OutputStream outStream) throws IOException;

    /**
     * @return Name of a command
     */
    public abstract String getCommandName();

    /**
     * Equals method requires two command to have same class to be equal and same list of arguments
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this.getClass().equals(obj.getClass())) {
            return this.args.equals(((Command)obj).args);
        }
        return false;
    }

    /**
     * hashCode method matching for overridden equals
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.args);
    }

    /**
     * @return concatenation of command's name with its arguments with space character
     * used as a delimiter
     */
    @Override
    public String toString() {
        return getCommandName()+ " " + args.stream()
                .map(CommandWord::getRawValue)
                .collect(Collectors.joining(" "));
    }

    /**
     * Represents command with its arguments as an array of Strings
     *
     * Used in OtherCommand and GrepCommand
     * @param withName true when name of the command is needed to be in the String array
     * @return
     */
    protected String[] commandAsStringArray(boolean withName) {
        int size = withName ? args.size() + 1: args.size();
        String[] result = new String[size];
        int i = 0;
        if (withName) {
            result[i] = getCommandName();
            i++;
        }
        for (CommandWord arg: args) {
            result[i] = arg.getEscapedAndStrippedValue();
            i++;
        }
        return result;
    }
}
