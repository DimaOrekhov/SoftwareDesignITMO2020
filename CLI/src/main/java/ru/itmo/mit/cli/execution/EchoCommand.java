package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static ru.itmo.mit.cli.execution.ExecutionErrorMessages.WRITE_FAIL;

public class EchoCommand extends Command {

    public EchoCommand(List<String> args) {
        super(args);
    }

    @Override
    public CommandExecutionResult execute(Environment environment, InputStream inStream, OutputStream outStream) {
       try {
/*           for (String arg : args) {
               outStream.write(arg.getBytes(environment.getCharset()));
           }*/
           String writeString = String.join(" ", args) + "\n";
           outStream.write(writeString.getBytes(environment.getCharset()));
           return CommandExecuted.getInstance();
       }
       catch (IOException e) {
           return new FailedToExecute(WRITE_FAIL);
       }
    }
}
