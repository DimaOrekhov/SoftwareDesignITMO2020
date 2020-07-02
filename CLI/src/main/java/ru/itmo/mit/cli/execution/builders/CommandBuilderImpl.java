package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.commands.*;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandType;
import ru.itmo.mit.cli.execution.domain.CommandWord;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of CommandBuilder class
 */
public class CommandBuilderImpl implements CommandBuilder {

    private CommandWord commandName;
    private final List<CommandWord> commandArgs;

    public CommandBuilderImpl() {
        commandName = CommandWord.EMPTY_WORD;
        commandArgs = new LinkedList<>();
    }

    @Override
    public CommandBuilder setCommandName(CommandWord commandName) {
        this.commandName = commandName;
        return this;
    }

    /**
     * @param arg argument to be added to list of arguments
     */
    @Override
    public CommandBuilder addArgument(CommandWord arg) {
        commandArgs.add(arg);
        return this;
    }

    /**
     * Chooses appropriate constructor based on a CommandName
     */
    @Override
    public Command build() {
        switch (CommandType.fromString(commandName.getEscapedAndStrippedValue())) {
            case ASSIGN:
                return new AssignmentCommand(commandArgs);
            case CAT:
                return new CatCommand(commandArgs);
            case ECHO:
                return new EchoCommand(commandArgs);
            case WC:
                return new WcCommand(commandArgs);
            case PWD:
                return new PwdCommand(commandArgs);
            case GREP:
                return new GrepCommand(commandArgs);
            case EXIT:
                return new ExitCommand(commandArgs);
            default:
                return new OtherCommand(commandName, commandArgs);
        }
    }
}
