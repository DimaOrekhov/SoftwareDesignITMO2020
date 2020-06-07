package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class EchoCommand extends Command {

    public EchoCommand(List<String> args) {
        super(args);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        String writeString = String.join(" ", args) + "\n";
        outStream.write(writeString.getBytes(environment.getCharset()));
        return CommandExecuted.getInstance();
    }
}
