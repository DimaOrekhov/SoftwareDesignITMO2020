package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility function for working with environment
 */
public class EnvironmentUtils {

    /**
     * Resolves absolute path of a file
     * @param filePathAsString
     * @param environment
     * @return
     */
    public static Path getAbsolutePath(String filePathAsString, Environment environment) {
        Path filePath = Paths.get(filePathAsString);
        if (filePath.isAbsolute()) {
            return filePath;
        }
        return environment.getWorkingDirectory().resolve(filePath);
    }
}
