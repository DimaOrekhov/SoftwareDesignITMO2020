package ru.itmo.mit.cli.execution;

import java.io.IOException;
import java.io.InputStream;

public class UtilClasses {

    // Возможно, стоит сделать синглтоном?
    public static InputStream getEmptyInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

}
