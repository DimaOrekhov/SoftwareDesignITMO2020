package ru.itmo.mit.cli.execution.commands;

import ru.itmo.mit.cli.execution.EnvironmentUtils;
import ru.itmo.mit.cli.execution.ExecutionErrorMessages;
import ru.itmo.mit.cli.execution.StreamUtils;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class WcCommand extends Command {

    public WcCommand(List<String> commandArgs) {
        super(commandArgs);
    }

    @Override
    public CommandExecutionResult execute(Environment environment,
                                          InputStream inStream,
                                          OutputStream outStream) throws IOException {
        // Prioritizing file arguments over inputStream:
        if (args.size() != 0) {
            List<String> filesNotFound = new LinkedList<>();
            for (String arg: args) {
                try {
                    Path filePath = EnvironmentUtils.getAbsolutePath(Paths.get(arg), environment);
                    String result = getStreamStats(new FileInputStream(new File(filePath.toString())),
                            environment.getCharset(), arg, null);
                    outStream.write(result.getBytes(environment.getCharset()));
                }
                catch (IOException e) {
                    filesNotFound.add(ExecutionErrorMessages.fileNotFound(arg));
                }
            }
            return filesNotFound.size() == 0 ? CommandExecuted.getInstance() :
                    new FailedToExecute(String.join("\n", filesNotFound));
        }
        String result;
        // Reading from stdin if command is first
        if (StreamUtils.isInstanceOfEmptyInputStream(inStream)) {
            inStream = System.in;
            result = getStreamStats(inStream,
                    environment.getCharset(), "", StreamUtils.END_OF_COMMAND);
        }
        // Else, reading from passed inStream
        else {
            result = getStreamStats(inStream,
                    environment.getCharset(), "", null);
        }
        outStream.write(result.getBytes(environment.getCharset()));
        return CommandExecuted.getInstance();
    }

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
