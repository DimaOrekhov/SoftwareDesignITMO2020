package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ru.itmo.mit.cli.execution.ExecutionErrorMessages.fileNotFound;

public class CatCommand extends Command {

    public CatCommand(List<String> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) {
        if (args.size() != 0) {
            // Prioritizing arguments over input just like Bash
            LinkedList<InputStream> streams =new LinkedList<InputStream>();
            for (String fileName : args) {
                Path filePath = Paths.get(fileName);
                String workingDirectory = environment.getWorkingDirectory().toString();
                File file = filePath.isAbsolute() ?
                        new File(fileName) :
                        new File(Paths.get(workingDirectory, fileName).toString());
                try {
                    streams.add(new FileInputStream(file));
                }
                catch (FileNotFoundException e) {
                    return new FailedToExecute(fileNotFound(fileName));
                }
            }
            inStream = new SequenceInputStream(Collections.enumeration(streams));
        }

        try {
            inStream.transferTo(outStream);
        }
        catch (IOException e) {

        }
        return CommandExecuted.getInstance();
    }
}
