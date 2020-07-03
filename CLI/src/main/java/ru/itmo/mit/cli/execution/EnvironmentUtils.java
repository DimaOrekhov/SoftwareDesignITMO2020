package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;

import java.nio.file.Path;

/**
 * Utility function for working with environment
 */
public class EnvironmentUtils {

    /**
     * Resolves absolute path of a file
     * @param filePath
     * @param environment
     * @return
     */
    public static Path getAbsolutePath(Path filePath, Environment environment) {
        if (filePath.isAbsolute()) {
            return filePath;
        }
        return environment.getWorkingDirectory().resolve(filePath);
    }
}
