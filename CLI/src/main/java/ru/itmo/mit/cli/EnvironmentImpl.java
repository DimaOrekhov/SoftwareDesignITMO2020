package ru.itmo.mit.cli;

import ru.itmo.mit.cli.domain.Environment;
import ru.itmo.mit.cli.domain.Namespace;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;

public class EnvironmentImpl implements Environment {

    private Path workingDirectory;
    private Namespace namespace;
    private OutputStream finalStream;

    @Override
    public String getNextLine() {
        return null;
    }

    @Override
    public void feedLine(String line) {

    }

    @Override
    public void modifyNamespace(String varName, String varValue) {
        namespace.put(varName, varValue);
    }

    @Override
    public Iterator<String> iterator() {
        return null;
    }
}
