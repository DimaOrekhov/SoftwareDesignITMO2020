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
            List<StreamStats> streamStats = new LinkedList<>();
            for (CommandWord arg: args) {
                String fileName = arg.getEscapedAndStrippedValue();
                Path absFilePath = getAbsolutePath(fileName, environment);
                try (FileInputStream fileInputStream = new FileInputStream(absFilePath.toString())) {
                    StreamStats result = getStreamStats(fileInputStream,
                            environment.getCharset(), fileName, null);
                    streamStats.add(result);
                    outStream.write(result.toString().getBytes(environment.getCharset()));
                } catch (IOException e) {
                    filesNotFound.add(ExecutionErrorMessages.fileNotFound(fileName));
                }
            }
            if (args.size() > 1) {
                String totalString = StreamStats.sumUp(streamStats).toString();
                outStream.write(
                        totalString.getBytes(environment.getCharset())
                );
            }
            return filesNotFound.size() == 0 ? CommandExecuted.getInstance() :
                    new FailedToExecute(String.join("\n", filesNotFound));
        }

        String result = getStreamStats(inStream,
                environment.getCharset(),
                "",
                inStream.equals(System.in) ? StreamUtils.END_OF_COMMAND : null).toString();
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
    private StreamStats getStreamStats(InputStream stream,
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
            if (!line.equals("")) {
                wordCount += line.split("\\s").length;
            }
            if (!isFileStream) {
                byteCount += line.getBytes(charset).length;
            }
            line = reader.readLine();
        }
        return new StreamStats(lineCount, wordCount, byteCount, streamName);
    }

    /**
     * Utility data class, representing statistic of a stream
     */
    private static class StreamStats {

        private final long lineCount;
        private final long wordCount;
        private final long byteCount;
        private final String streamName;

        private StreamStats(String streamName) {
            lineCount = 0;
            wordCount = 0;
            byteCount = 0;
            this.streamName = streamName;
        }

        public StreamStats(long lineCount, long wordCount, long byteCount, String streamName) {
            this.lineCount = lineCount;
            this.wordCount = wordCount;
            this.byteCount = byteCount;
            this.streamName = streamName;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            return stringBuilder.append(lineCount)
                    .append('\t')
                    .append(wordCount)
                    .append('\t')
                    .append(byteCount)
                    .append('\t')
                    .append(streamName)
                    .append('\n')
                    .toString();
        }

        /**
         * Creates new StreamStats object summing up two existing
         * streamName filled is inherited from an object on which
         * this method is called
         *
         * Used in sumUp method below
         *
         * @param other
         * @return
         */
        private StreamStats add(StreamStats other) {
            return new StreamStats(lineCount + other.lineCount,
                    wordCount + other.wordCount,
                    byteCount + other.byteCount,
                    streamName);
        }

        /**
         * Used to get a summary of multiple StreamStats objects
         * @param streamStats
         * @return
         */
        public static StreamStats sumUp(List<StreamStats> streamStats) {
            return streamStats.stream().reduce(new StreamStats("total"),
                    StreamStats::add);
        }
    }
}
