package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Environment;

import java.nio.file.Path;

public class EnvironmentUtils {

    public static Path getAbsolutePath(Path filePath, Environment environment) {
        if (filePath.isAbsolute()) {
            return filePath;
        }
        return environment.getWorkingDirectory().resolve(filePath);
    }
}
