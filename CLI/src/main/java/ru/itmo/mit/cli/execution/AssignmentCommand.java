package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static ru.itmo.mit.cli.execution.ExecutionErrorMessages.ASSIGNMENT_LACKS_ARGUMENTS;

public class AssignmentCommand extends Command {

    public AssignmentCommand(List<String> args) {
        super(args);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) {
        if (args.size() >= 2) {
            environment.modifyNamespace(args.get(0), args.get(1));
            return CommandExecuted.getInstance();
        }
        else {
            return new FailedToExecute(ASSIGNMENT_LACKS_ARGUMENTS);
        }
    }
}
