package ru.itmo.mit.cli.execution;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    private static final InputStream emptyStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    public static InputStream getEmptyInputStream() {
        return emptyStream;
    }


}
