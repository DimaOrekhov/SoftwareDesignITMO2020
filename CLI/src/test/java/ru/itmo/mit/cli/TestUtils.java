package ru.itmo.mit.cli;

import ru.itmo.mit.cli.parsing.domain.ParsingResult;
import ru.itmo.mit.cli.parsing.domain.SuccessfulParsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    public static <T> T fromSuccess(ParsingResult<T> result) {
        assertTrue(result instanceof SuccessfulParsing);
        return ((SuccessfulParsing<T>) result).getResult();
    }

    public static <T> void assertEqualsParsingResult(T expected, ParsingResult<T> parsingResult) {
        assertEquals(expected, fromSuccess(parsingResult));
    }
}
