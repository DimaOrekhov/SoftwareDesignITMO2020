package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.parsing.domain.AutomatonOutputHandler;

/**
 * AutomatonOutputHandler with underlying StringBuilder
 * Concatenates all Strings that were passed to handle
 */
public class StringBuilderAsHandler implements AutomatonOutputHandler<String> {

    private final StringBuilder builder;

    public StringBuilderAsHandler(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void handle(String stateResult) {
        builder.append(stateResult);
    }

    @Override
    public void finalizeHandler() {
    }
}
