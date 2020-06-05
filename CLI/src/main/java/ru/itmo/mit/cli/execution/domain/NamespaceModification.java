package ru.itmo.mit.cli.execution.domain;

public class NamespaceModification extends CommandExecuted {

    private final String key;
    private final String value;

    public NamespaceModification(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
