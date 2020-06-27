package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;


public class EchoCommand extends Command {

    public EchoCommand(List<CommandWord> args) {
        super(args);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        String writeString = String.join(" ", args.stream()
                        .map(CommandWord::getEscapedAndStrippedValue)
                        .collect(Collectors.toList())) + "\n";
        outStream.write(writeString.getBytes(environment.getCharset()));
        return CommandExecuted.getInstance();
    }
}
