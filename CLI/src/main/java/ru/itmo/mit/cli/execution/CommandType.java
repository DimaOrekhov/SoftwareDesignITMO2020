package ru.itmo.mit.cli.execution;

public enum CommandType {
    ASSIGN,
    CAT,
    ECHO,
    WC,
    PWD,
    EXIT,
    OTHER;

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
        }
        return OTHER;
    }
}
