package ru.itmo.mit.cli.execution;

import jdk.jshell.execution.Util;
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
                                          OutputStream outStream) throws IOException {
        LinkedList<String> filesNotFound = new LinkedList<>();
        // If block below chains multiple FileInputStreams into one
        // InputStream and assigns it to inStream variable
        if (args.size() != 0) {
            // Prioritizing arguments over input just like Bash
            LinkedList<InputStream> streams = new LinkedList<InputStream>();
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
                    filesNotFound.add(fileNotFound(fileName));
                }
            }
            inStream = new SequenceInputStream(Collections.enumeration(streams));
        }
        // Check whether inStream is an instance of special
        // emptyStream
        if (StreamUtils.isInstanceOfEmptyInputStream(inStream)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in, environment.getCharset()));
            String line;
            while (!(line = reader.readLine()).equals(StreamUtils.END_OF_COMMAND)) {
                outStream.write(line.getBytes(environment.getCharset()));
                outStream.write("\n".getBytes(environment.getCharset()));
            }
        }
        // Transfer result to outStream
        inStream.transferTo(outStream);

        if (filesNotFound.size() != 0) {
            return new FailedToExecute(String.join("\n", filesNotFound));
        }
        return CommandExecuted.getInstance();
    }
}
