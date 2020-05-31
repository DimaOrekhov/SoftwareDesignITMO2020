package ru.itmo.mit.cli.parsing.domain;

public class CommandToken {

    private final String value;
    private final CommandTokenType tokenType;

    public CommandToken(String value, CommandTokenType type) {
        this.value = value;
        this.tokenType = type;
    }

    public String getValue() {
        return value;
    }

    public CommandTokenType getTokenType() {
        return tokenType;
    }

    public static CommandToken getEmptyToken() {
        return new CommandToken("", CommandTokenType.EMPTY);
    }
}
