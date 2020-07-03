package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static ru.itmo.mit.cli.execution.ExecutionErrorMessages.ASSIGNMENT_LACKS_ARGUMENTS;

/**
 * AssignmentCommand, modifies Namespace of an Environment
 */
public class AssignmentCommand extends Command {

    public AssignmentCommand(List<CommandWord> args) {
        super(args);
    }

    /**
     * First argument of a command is used as a variable name,
     * second one as value
     *
     * Excess arguments are ignored
     * Returns error message, in case there are not enough argument to perform assignment
     *
     * @param environment Environment in which command is to be executed
     * @param inStream command's stdin, ignored
     * @param outStream command's stdout, ignored
     * @return
     * @throws IOException
     */
    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        if (args.size() >= 2) {
            environment.modifyNamespace(args.get(0).getEscapedAndStrippedValue(),
                    args.get(1).getEscapedAndStrippedValue());
            return CommandExecuted.getInstance();
        }
        else {
            return new FailedToExecute(ASSIGNMENT_LACKS_ARGUMENTS);
        }
    }

    @Override
    public String getCommandName() {
        return "=";
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(
                args.get(0) == null ? "" : args.get(0)
        )
                .append("=");
        for (int i = 1; i < args.size(); i++) {
            stringBuilder
                    .append(args.get(i).getEscapedAndStrippedValue())
                    .append(" ");
        }
        return stringBuilder.toString();
    }
}
