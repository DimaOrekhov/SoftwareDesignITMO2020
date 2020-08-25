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
 * CatCommand - prints contents of files passed as arguments or piped stream to
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
        // Prioritizing arguments over command's stdin just like Bash:
        if (args.size() != 0) {
            LinkedList<String> filesNotFound = new LinkedList<>();
            for (CommandWord fname : args) {
                String fileName = fname.getEscapedAndStrippedValue();
                Path absFilePath = getAbsolutePath(fileName, environment);
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

        // In case no arguments have been passed processes inputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, environment.getCharset()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Work around for System.in processing
            if (System.in.equals(inStream) && line.equals(StreamUtils.END_OF_COMMAND)) {
                break;
            }
            outStream.write(line.getBytes(environment.getCharset()));
            outStream.write("\n".getBytes(environment.getCharset()));
        }
        return CommandExecuted.getInstance();
    }

    @Override
    public String getCommandName() {
        return "cat";
    }
}
