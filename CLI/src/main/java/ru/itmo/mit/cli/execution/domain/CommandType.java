package ru.itmo.mit.cli.execution.domain;

/**
 * Possible command types
 */
public enum CommandType {
    ASSIGN,
    CAT,
    ECHO,
    WC,
    PWD,
    GREP,
    EXIT,
    OTHER;

    /**
     * @param commandName String representing name of a command
     * @return corresponding enum instance
     */
    public static CommandType fromString(String commandName) {
        switch (commandName) {
            case "=":
                return ASSIGN;
            case "cat":
                return CAT;
            case "echo":
                return ECHO;
            case "wc":
                return WC;
            case "pwd":
                return PWD;
            case "exit":
                return EXIT;
            case "grep":
                return GREP;
        }
        return OTHER;
    }
}
