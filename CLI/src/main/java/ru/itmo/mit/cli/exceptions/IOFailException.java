package ru.itmo.mit.cli.exceptions;

/**
 * RuntimeException indicating failure of IO
 * This error is independent of user's input and the program itself
 */
public class IOFailException extends RuntimeException {

    public IOFailException(Throwable throwable) {
        super(throwable);
    }

}
