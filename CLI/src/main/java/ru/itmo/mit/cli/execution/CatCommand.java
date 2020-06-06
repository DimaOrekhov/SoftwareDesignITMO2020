package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.*;

import javax.xml.catalog.CatalogException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

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
            Enumeration streams = Collections.enumeration(
                    args.stream().map(fileName -> {
                            Path filePath = Paths.get(fileName);
                            String workingDirectory = environment.getWorkingDirectory().toString();
                            File file = filePath.isAbsolute() ?
                                    new File(fileName) :
                                    new File(Paths.get(workingDirectory, fileName).toString());
                            try {
                                return new FileInputStream(file);
                            }
                            catch (FileNotFoundException e) {
                                fileNotFound(fileName);
                            }
                            return UtilClasses.getEmptyInputStream();
                        })
                        .collect(Collectors.toList())
                );
                inStream = new SequenceInputStream(streams);
            }
        return CommandExecuted.getInstance();
    }
}
