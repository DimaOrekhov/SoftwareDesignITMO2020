package ru.itmo.mit.cli.execution.commands;

import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.itmo.mit.cli.execution.ExecutionErrorMessages;
import ru.itmo.mit.cli.execution.StreamUtils;
import ru.itmo.mit.cli.execution.domain.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.itmo.mit.cli.execution.EnvironmentUtils.getAbsolutePath;

/**
 * Simple version of grep with three possible keys:
 * -i case insensitivity
 * -w whole word matching
 * -A n printing n lines after a match
 */
public class GrepCommand extends Command {

    private boolean caseInsensitive;
    private boolean wholeWordMatch;
    private int nLinesAfterMatch;
    private List<String> argList;
    private String parsingFailedMessage;
    private Pattern regEx;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    private static final Options options;
    private static final CommandLineParser commandLineParser;

    // Static initialization of Options and CommandLineParser classes
    static {
        options = new Options();
        // Insensitive key:
        options.addOption(Option.builder("i")
                .build());
        // Whole word search key:
        options.addOption(Option.builder("w")
                .build());
        // Show n lines after the match:
        options.addOption(Option.builder("A")
                .hasArg()
                .numberOfArgs(1)
                .build());
        commandLineParser = new DefaultParser();
    }

    /**
     * Argument parsing is done inside the constructor
     *
     * When some error occurs, message is saved into parsingFailedError
     * Non-null value of this variable prevents command from being executed
     *
     * @param arguments
     */
    public GrepCommand(List<CommandWord> arguments) {
        super(arguments);
        // Parsing arguments and setting up flags:
        String[] strArr = commandAsStringArray(false);
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, strArr);
        } catch (ParseException e) {
            parsingFailedMessage = ExecutionErrorMessages.GREP_USAGE;
            return;
        }

        caseInsensitive = commandLine.hasOption("i");
        wholeWordMatch = commandLine.hasOption("w");
        if (commandLine.hasOption("A")) {
            String stringValue = commandLine.getOptionValue("A");
            try {
                nLinesAfterMatch = Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                parsingFailedMessage = ExecutionErrorMessages.grepContextLenArgFormatError(stringValue);
            }
            if (nLinesAfterMatch < 0) {
                parsingFailedMessage = ExecutionErrorMessages.grepContextLenArgFormatError(stringValue);
                return;
            }
        }
        argList = commandLine.getArgList();
        if (argList.size() == 0) {
            // RegEx wasn't provided:
            parsingFailedMessage = ExecutionErrorMessages.GREP_USAGE;
        } else {
            regEx = caseInsensitive ? Pattern.compile(argList.get(0), Pattern.CASE_INSENSITIVE) :
                    Pattern.compile(argList.get(0));
            argList = argList.subList(1, argList.size());
        }
    }

    @Override
    public CommandExecutionResult execute(Environment environment, InputStream inStream, OutputStream outStream) throws IOException {
        if (parsingFailedMessage != null) {
            return new FailedToExecute(parsingFailedMessage);
        }

        if (argList.size() != 0) {
            // Grep over files:
            LinkedList<String> filesNotFound = new LinkedList<>();
            for (String fileName: argList) {
                Path absFilePath = getAbsolutePath(fileName, environment);
                try (FileInputStream fileInputStream = new FileInputStream(absFilePath.toString())){
                    processStream(fileInputStream, outStream, environment.getCharset(), null);
                } catch (FileNotFoundException e) {
                    filesNotFound.add(ExecutionErrorMessages.fileNotFound(fileName));
                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
            return filesNotFound.size() == 0 ? CommandExecuted.getInstance() :
                    new FailedToExecute(String.join("\n", filesNotFound));
        }

        // Process inStream, when file arguments are missing:
        processStream(inStream,
                outStream,
                environment.getCharset(),
                inStream.equals(System.in) ? StreamUtils.END_OF_COMMAND : null);
        return CommandExecuted.getInstance();
    }

    /**
     * Main processing function
     *
     * @param inputStream
     * @param outputStream
     * @param charset
     * @param stopString
     * @throws IOException
     */
    private void processStream(InputStream inputStream,
                               OutputStream outputStream,
                               Charset charset,
                               String stopString) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
        String line;
        int printNNextStrings = 0;
        int lineIdx = -1;
        int lastPrint = 0;
        // Matched substrings are colored red, if outputStream is System.out:
        boolean colorMatch = outputStream.equals(System.out);
        while ((line = reader.readLine())!= null) {
            lineIdx++;
            if (line.equals(stopString)) {
                break;
            }
            Matcher matcher = regEx.matcher(line);
            boolean lineMatched = false;
            // stringBuilder and lastMatchEnd used only when coloredMatch = true
            StringBuilder stringBuilder = new StringBuilder();
            int lastMatchEnd = 0;
            while (matcher.find()) {
                boolean currMatch = !wholeWordMatch || isWord(line, matcher.start(), matcher.end());
                lineMatched |= currMatch;
                if (colorMatch && currMatch) {
                    stringBuilder.append(line, lastMatchEnd, matcher.start());
                    stringBuilder.append(colorStringRed(line.substring(matcher.start(), matcher.end())));
                    lastMatchEnd = matcher.end();
                }
            }
            // Adding uncolored tail of the match:
            if (colorMatch && lastMatchEnd != line.length()) {
                stringBuilder.append(line.substring(lastMatchEnd));
            }
            String result = colorMatch ? stringBuilder.toString() : line;
            if (lineMatched) {
                if (nLinesAfterMatch!= 0 && lineIdx != 0 && lastPrint != lineIdx - 1) {
                    // Separating groups of matches, if last match was earlier that previous line
                    // Only happens when -A n option was specified with n > 0
                    outputStream.write("--\n".getBytes(charset));
                }
                outputStream.write(result.getBytes(charset));
                outputStream.write("\n".getBytes(charset));
                printNNextStrings = nLinesAfterMatch;
                lastPrint = lineIdx;
            } else if (printNNextStrings > 0) {
                outputStream.write(line.getBytes(charset));
                outputStream.write("\n".getBytes(charset));
                printNNextStrings--;
                lastPrint = lineIdx;
            }
        }
    }

    /**
     * Checks whether substring is a word, i.e. symbols to its left and right
     * are non-word character (neither letter, digit nor underscore)
     * @param text
     * @param start
     * @param end
     * @return
     */
    private static boolean isWord(String text, int start, int end) {
        boolean leftBorder = start == 0 || nonWordChar(text.charAt(start - 1));
        boolean rightBorder = end == text.length() || nonWordChar(text.charAt(end));
        return leftBorder && rightBorder;
    }

    /**
     * Checks whether character is a non-word character
     * @param character
     * @return
     */
    private static boolean nonWordChar(char character) {
        return !(Character.isLetter(character) || Character.isDigit(character) || character == '_');
    }

    /**
     * Make String red when printed out to System.out
     * @param string
     * @return
     */
    public static String colorStringRed(String string) {
        return ANSI_RED + string + ANSI_RESET;
    }

    @Override
    public String getCommandName() {
        return "grep";
    }
}
