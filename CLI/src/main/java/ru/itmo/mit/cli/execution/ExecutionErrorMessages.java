package ru.itmo.mit.cli.execution;

public class ExecutionErrorMessages {

    public static final String WRITE_FAIL = "Couldn't write to stream";
    public static final String ASSIGNMENT_LACKS_ARGUMENTS =
            "Assignment command should have both left hand side and right hand side";

    public static String fileNotFound(String fileName) {
        return fileName + ": No such file or directory";
    }

}
