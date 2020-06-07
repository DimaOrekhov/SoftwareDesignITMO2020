package ru.itmo.mit.cli.execution;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    public static final String END_OF_COMMAND = "endOfCommand";
    private static final InputStream emptyStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    public static InputStream getEmptyInputStream() {
        return emptyStream;
    }

    public static boolean isInstanceOfEmptyInputStream(InputStream inputStream) {
        return emptyStream.equals(inputStream);
    }

    public static class NonClosingInputStreamWrapper extends InputStream {

        private final InputStream wrappedStream;

        public NonClosingInputStreamWrapper(InputStream wrappedStream) {
            this.wrappedStream = wrappedStream;
        }

        @Override
        public int read() throws IOException {
            return wrappedStream.read();
        }

        @Override
        public void close() throws IOException {
        }
    }
}
