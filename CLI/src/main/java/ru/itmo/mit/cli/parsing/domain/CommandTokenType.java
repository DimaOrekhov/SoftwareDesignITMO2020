package ru.itmo.mit.cli.parsing.domain;

/**
 * Four possible CommandToken types, seem pretty self-explanatory
 *
 * EMPTY represents some sequence of space characters
 */
public enum CommandTokenType {
    COMMAND,
    ARGUMENT,
    PIPE,
    EMPTY;
}
