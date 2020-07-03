package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.builders.CommandBuilderImpl;
import ru.itmo.mit.cli.execution.builders.CommandBuilder;
import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilder;
import ru.itmo.mit.cli.execution.domain.CommandWord;
import ru.itmo.mit.cli.parsing.domain.AutomatonOutputHandler;
import ru.itmo.mit.cli.parsing.domain.CommandToken;

/**
 * AutomatonOutputHandler with underlying PipedCommandsBuilder
 * Builds PipedCommands through series of handle calls with CommandToken as argument
 */
public class PipedCommandsBuilderAsHandler implements AutomatonOutputHandler<CommandToken> {

    private final PipedCommandsBuilder commandsBuilder;
    private CommandBuilder currCommandBuilder;

    public PipedCommandsBuilderAsHandler(PipedCommandsBuilder commandsBuilder) {
        this.commandsBuilder = commandsBuilder;
        currCommandBuilder = new CommandBuilderImpl();
    }

    /**
     * Processes CommandToken
     * Given PIPE token, build current command and adds it to chain of piped commands
     * Given COMMAND token, sets command name of a current command
     * Given ARGUMENT token, adds argument to a current command
     * Ignores EMPTY token
     * @param stateResult CommandToken to be processed
     */
    @Override
    public void handle(CommandToken stateResult) {
        switch (stateResult.getTokenType()) {
            case PIPE: {
                commandsBuilder.addCommand(currCommandBuilder.build());
                currCommandBuilder = new CommandBuilderImpl();
                break;
            }
            case COMMAND: {
                currCommandBuilder.setCommandName(new CommandWord(stateResult.getValue()));
                break;
            }
            case ARGUMENT: {
                currCommandBuilder.addArgument(new CommandWord(stateResult.getValue()));
                break;
            }
            case EMPTY: {
                break;
            }
        }
    }

    @Override
    public void finalizeHandler() {
        commandsBuilder.addCommand(currCommandBuilder.build());
    }
}
