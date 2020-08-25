package ru.itmo.mit.cli;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.domain.*;

/**
 * Implementation of Shell interface
 */
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

    /**
     * Given String representation of command this methods proceeds in
     * three stages:
     * 1. Substituting environment variables with their corresponding values
     * 2. Parsing String into an instance of PipedCommand class
     * 3. Executing commands inside an instance of Environment
     * If error occurs on stages 1 or 2, execution interrupts and error messages is
     * sent to environment
     * @param inputString Command string to be interpreted
     */
    public void interpret(String inputString) {
        ParsingResult<String> substitutorResult = substitutor.substitute(inputString);
        if (parsingFailed(substitutorResult)) {
            return;
        }
        String commandString = ((SuccessfulParsing<String>) substitutorResult).getResult();
        ParsingResult<PipedCommands> commandParserResult = commandParser.parseCommand(commandString);
        if (parsingFailed(commandParserResult)) {
            return;
        }
        PipedCommands commands = ((SuccessfulParsing<PipedCommands>) commandParserResult).getResult();
        environment.executeCommands(commands);
    }

    /**
     * In case of error messages prints them to environment
     * @param result Result of a parsing process, wrapped in a ParsingResult class,
     *               indicating, whether parsing has been successful
     * @return True in case of successful parsing, false otherwise
     */
    private boolean parsingFailed(ParsingResult result) {
        if (result instanceof SuccessfulParsing) {
            return false;
        }
        String errorMessage = ((FailedParsing) result).getErrorMessage();
        environment.println(errorMessage);
        return true;
    }
}
