package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.builders.CommandBuilderImpl;
import ru.itmo.mit.cli.execution.builders.CommandBuilder;
import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilder;
import ru.itmo.mit.cli.parsing.domain.AutomatonOutputHandler;
import ru.itmo.mit.cli.parsing.domain.CommandToken;

public class PipedCommandsBuilderAsHandler implements AutomatonOutputHandler<CommandToken> {

    private final PipedCommandsBuilder commandsBuilder;
    private CommandBuilder currCommandBuilder;

    public PipedCommandsBuilderAsHandler(PipedCommandsBuilder commandsBuilder) {
        this.commandsBuilder = commandsBuilder;
        currCommandBuilder = new CommandBuilderImpl();
    }

    @Override
    public void handle(CommandToken stateResult) {
        switch (stateResult.getTokenType()) {
            case PIPE: {
                commandsBuilder.addCommand(currCommandBuilder.build());
                currCommandBuilder = new CommandBuilderImpl();
                break;
            }
            case COMMAND: {
                currCommandBuilder.setCommandName(stateResult.getValue());
                break;
            }
            case ARGUMENT: {
                currCommandBuilder.addArgument(stateResult.getValue());
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
