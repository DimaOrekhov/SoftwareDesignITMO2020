package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents some external command
 */
public class OtherCommand extends Command {

    private final CommandWord commandName;

    public OtherCommand(CommandWord commandName, List<CommandWord> commandArgs) {
        super(commandArgs);
        this.commandName = commandName;
    }

    /**
     * Launches represented command by creating Process
     *
     * @param environment Environment in which command is to be executed
     * @param inStream command's stdin
     * @param outStream command's stdout
     * @return
     * @throws IOException
     */
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
        // input redirection
        if (!inStream.equals(System.in)) {
            inStream.transferTo(process.getOutputStream());
        }
        process.getOutputStream().close();
        // output redirection
        process.getInputStream().transferTo(outStream);
        process.getErrorStream().transferTo(outStream);
        return CommandExecuted.getInstance();
    }

    @Override
    public String getCommandName() {
        return commandName.getEscapedAndStrippedValue();
    }

    private String[] commandAsStringArray() {
        String[] result = new String[args.size() + 1];
        result[0] = commandName.getRawValue();
        int i = 1;
        for (CommandWord arg: args) {
            result[i] = arg.getEscapedAndStrippedValue();
            i++;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return commandName.equals(((OtherCommand) obj).commandName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, args);
    }
}
