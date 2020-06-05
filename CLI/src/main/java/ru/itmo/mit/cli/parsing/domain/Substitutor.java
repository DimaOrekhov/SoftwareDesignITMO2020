package ru.itmo.mit.cli.parsing.domain;

public interface Substitutor {

    ParsingResult<String> substitute(String inputString);

}
