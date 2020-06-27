package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.ExecutionErrorMessages;
import ru.itmo.mit.cli.execution.StreamUtils;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static ru.itmo.mit.cli.execution.EnvironmentUtils.getAbsolutePath;

/**
 * Counts lines, words and bytes of file arguments or piped input stream
 */
public class WcCommand extends Command {

    public WcCommand(List<CommandWord> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        // Prioritizing file arguments over inputStream:
        if (args.size() != 0) {
            List<String> filesNotFound = new LinkedList<>();
            for (CommandWord arg: args) {
                String fileName = arg.getEscapedAndStrippedValue();
                Path filePath = Paths.get(fileName);
                Path absFilePath = getAbsolutePath(filePath, environment);
                try (FileInputStream fileInputStream = new FileInputStream(absFilePath.toString())) {
                    String result = getStreamStats(fileInputStream,
                            environment.getCharset(), fileName, null);
                    outStream.write(result.getBytes(environment.getCharset()));
                } catch (IOException e) {
                    filesNotFound.add(ExecutionErrorMessages.fileNotFound(fileName));
                }
            }
            return filesNotFound.size() == 0 ? CommandExecuted.getInstance() :
                    new FailedToExecute(String.join("\n", filesNotFound));
        }

        String result;
        result = getStreamStats(inStream,
                environment.getCharset(),
                "",
                inStream.equals(System.in) ? StreamUtils.END_OF_COMMAND : null);
        outStream.write(result.getBytes(environment.getCharset()));
        return CommandExecuted.getInstance();
    }

    @Override
    public String getCommandName() {
        return "wc";
    }

    /**
     * Computes required statistics for a given stream
     * @param stream
     * @param charset character encoding to be used
     * @param streamName name of a stream
     * @param stopString string indicating end of stream
     * @return
     * @throws IOException
     */
    private String getStreamStats(InputStream stream,
                                  Charset charset,
                                  String streamName,
                                  String stopString) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        long lineCount = 0;
        long wordCount = 0;
        long byteCount = 0;
        final boolean isFileStream = stream instanceof FileInputStream;
        if (isFileStream) {
            byteCount = ((FileInputStream) stream).getChannel().size();
        }
        String line;
        line = reader.readLine();
        while (line != null && !line.equals(stopString)) {
            lineCount++;
            wordCount += line.split("\\s").length;
            if (!isFileStream) {
                byteCount += line.getBytes(charset).length;
            }
            line = reader.readLine();
        }
        return new StringBuilder()
                .append(lineCount)
                .append('\t')
                .append(wordCount)
                .append('\t')
                .append(byteCount)
                .append('\t')
                .append(streamName)
                .append('\n')
                .toString();
    }
}
