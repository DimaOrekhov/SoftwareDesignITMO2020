package ru.itmo.mit.cli.execution;

public class ExecutionErrorMessages {

    public static final String ASSIGNMENT_LACKS_ARGUMENTS =
            "Assignment command should have both left hand side and right hand side";
    public static final String GREP_USAGE =
            "Usage: grep [OPTION]... PATTERN [FILE]...";
    public static String fileNotFound(String fileName) {
        return fileName + ": No such file or directory";
    }

    public static String grepContextLenArgFormatError(String argValue) {
        return "grep: " + argValue + ": invalid context length argument";
    }

}
