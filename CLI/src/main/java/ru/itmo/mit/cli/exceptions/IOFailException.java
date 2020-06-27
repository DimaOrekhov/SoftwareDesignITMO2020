package ru.itmo.mit.cli.exceptions;

public class IOFailException extends RuntimeException {

    public IOFailException(Throwable throwable) {
        super(throwable);
    }

}
