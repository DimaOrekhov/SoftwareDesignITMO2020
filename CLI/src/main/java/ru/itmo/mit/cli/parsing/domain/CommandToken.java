package ru.itmo.mit.cli.parsing.domain;

/**
 * Class representing single token of command, used during
 * command parsing process
 */
public class CommandToken {

    private final String value;
    private final CommandTokenType tokenType;

    /**
     * @param value raw String value of token
     * @param type type of token
     */
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
