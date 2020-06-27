package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.StreamUtils;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ru.itmo.mit.cli.execution.ExecutionErrorMessages.fileNotFound;
import static ru.itmo.mit.cli.execution.EnvironmentUtils.getAbsolutePath;

/**
 * CatCommand - prints contents of file arguments or piped stream to
 * output stream
 */
public class CatCommand extends Command {

    public CatCommand(List<CommandWord> commandArgs) {
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
            LinkedList<InputStream> streams = new LinkedList<>();
            for (CommandWord fname : args) {
                String fileName = fname.getEscapedAndStrippedValue();
                Path filePath = Paths.get(fileName);
                Path absFilePath = getAbsolutePath(filePath, environment);
                try (FileInputStream fileInputStream = new FileInputStream(absFilePath.toString())) {
                    fileInputStream.transferTo(outStream);
                } catch (FileNotFoundException e) {
                    filesNotFound.add(fileNotFound(fileName));
                } catch (IOException e){
                    throw new IOException(e);
                }
            }
            return filesNotFound.size() == 0 ? CommandExecuted.getInstance() :
                    new FailedToExecute(String.join("\n", filesNotFound));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, environment.getCharset()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (System.in.equals(inStream) && line.equals(StreamUtils.END_OF_COMMAND)) {
                break;
            }
            outStream.write(line.getBytes(environment.getCharset()));
            outStream.write("\n".getBytes(environment.getCharset()));
        }
        if (filesNotFound.size() != 0) {
            return new FailedToExecute(String.join("\n", filesNotFound));
        }
        return CommandExecuted.getInstance();
    }
}
