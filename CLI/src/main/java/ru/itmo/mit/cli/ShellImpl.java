package ru.itmo.mit.cli;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.domain.*;

public class ShellImpl implements Shell {

    private final Substitutor substitutor;
    private final CommandParser commandParser;
    private final Environment environment;

    public ShellImpl(Substitutor substitutor,
                     CommandParser commandParser,
                     Environment environment) {
        this.substitutor = substitutor;
        this.commandParser = commandParser;
        this.environment = environment;
    }

    public void interpret(String inputString) {
        ParsingResult<String> substitutorResult = substitutor.substitute(inputString);
        if (!processParsingResult(substitutorResult)) {
            return;
        }
        String commandString = ((SuccessfulParsing<String>) substitutorResult).getResult();
        ParsingResult<PipedCommands> commandParserResult = commandParser.parseCommand(commandString);
        if (!processParsingResult(commandParserResult)) {
            return;
        }
        PipedCommands commands = ((SuccessfulParsing<PipedCommands>) commandParserResult).getResult();
        environment.executeCommands(commands);
    }

    private boolean processParsingResult(ParsingResult result) {
        if (result instanceof SuccessfulParsing) {
            return true;
        }
        String errorMessage = ((FailedParsing) result).getErrorMessage();
        environment.println(errorMessage);
        return false;
    }
}
