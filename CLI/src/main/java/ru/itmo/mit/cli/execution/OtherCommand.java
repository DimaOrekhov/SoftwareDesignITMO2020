package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandExecuted;
import ru.itmo.mit.cli.execution.domain.CommandExecutionResult;

import java.io.*;
import java.util.List;

public class OtherCommand extends Command {

    private final String commandName;

    public OtherCommand(String commandName, List<String> commandArgs) {
        super(commandArgs);
        this.commandName = commandName;
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        // Setting up a process:
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commandAsStringArray());
        processBuilder
                .environment()
                .putAll(environment.getNamespace());
        File workingDirecoty = new File(environment.getWorkingDirectory().toString());
        processBuilder.directory(workingDirecoty);
        // Running a process:
        Process process = processBuilder.start();
        inStream.transferTo(process.getOutputStream());
        process.getOutputStream().close();
        process.getInputStream().transferTo(outStream);
        process.getErrorStream().transferTo(outStream);
        return CommandExecuted.getInstance();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(commandName)
                .append(" \'")
                .append(String.join("\' \'", args))
                .append("\'");
        return stringBuilder.toString();
    }

    private String[] commandAsStringArray() {
        String[] result = new String[args.size() + 1];
        result[0] = commandName;
        int i = 1;
        for (String arg: args) {
            result[i] = arg;
            i++;
        }
        return result;
    }
}
