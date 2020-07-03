package ru.itmo.mit.cli;

import org.junit.*;
import ru.itmo.mit.cli.execution.NamespaceImpl;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.parsing.SubstitutionAutomaton;

import static ru.itmo.mit.cli.TestUtils.assertEqualsParsingResult;


public class SubstitutionAutomatonTest {

    private final Namespace namespace = new NamespaceImpl() {
        {
            put("x", "Hello");
            put("y", "World");
            put("variable", "f");
            put("10", "12");
        }
    };

    private final String simpleString1 = "hello x world!!";
    private final String simpleString2 = "12345 10 12";
    private final String stringWithSub1 = "$x world";
    private final String stringWithSubResult1 = "Hello world";
    private final String stringWithSub2 = "$x$y!";
    private final String stringWithSubResult2 = "HelloWorld!";
    private final String stringWithSub3 = "$variable=$10";
    private final String stringWithSubResult3 = "f=12";
    private final String doubleQuotesAndSub = "\"$x World\"! What a good day, \"$y$\"!";
    private final String doubleQuotesAndSubResult = "\"Hello World\"! What a good day, \"World\"!";
    private final String singleQuotesAndSub = "$x '$x world'";
    private final String singleQuotesAndSubResult = "Hello '$x world'";
    private final String doubleInsideSingleQuotes = "echo 'asdasd \" asd $x asd\" asd'";
    private final String doubleInsideSingleQuotesResult = "echo 'asdasd \" asd $x asd\" asd'";
    private final String singleInsideDoubleQuotes = "echo \"asdasd ' asd $x asd' asd\"";
    private final String singleInsideDoubleQuotesResult = "echo \"asdasd ' asd Hello asd' asd\"";
    private final String escapeDollar = "\\$x $x";
    private final String escapeDollarResult = "\\$x Hello";
    private final String escapeQuotes = "hello \\' $x \\' ' $x '";
    private final String escapeQuotesResult = "hello \\' Hello \\' ' $x '";
    private final String escapeInsideDoubleQuotes = "\"\\$x\" \"$y\"";
    private final String escapeInsideDoubleQuotesResult = "\"\\$x\" \"World\"";
    private SubstitutionAutomaton substitutor = new SubstitutionAutomaton(namespace);

    @Test
    public void testSimpleStrings() {
        assertEqualsParsingResult(simpleString1, substitutor.substitute(simpleString1));
        assertEqualsParsingResult(simpleString2, substitutor.substitute(simpleString2));
    }

    @Test
    public void testSimpleSubstitution() {
        assertEqualsParsingResult(stringWithSubResult1, substitutor.substitute(stringWithSub1));
        assertEqualsParsingResult(stringWithSubResult2, substitutor.substitute(stringWithSub2));
        assertEqualsParsingResult(stringWithSubResult3, substitutor.substitute(stringWithSub3));
    }

    @Test
    public void testDoubleQuotes() {
        assertEqualsParsingResult(doubleQuotesAndSubResult, substitutor.substitute(doubleQuotesAndSub));
    }

    @Test
    public void testSingleQuotes() {
        assertEqualsParsingResult(singleQuotesAndSubResult, substitutor.substitute(singleQuotesAndSub));
    }

    @Test
    public void testNestedQuotes() {
        assertEqualsParsingResult(singleInsideDoubleQuotesResult,
                substitutor.substitute(singleInsideDoubleQuotes));
        assertEqualsParsingResult(doubleInsideSingleQuotesResult,
                substitutor.substitute(doubleInsideSingleQuotes));
    }

    @Test
    public void escapingTest() {
        assertEqualsParsingResult(escapeDollarResult,
                substitutor.substitute(escapeDollar));
        assertEqualsParsingResult(escapeQuotesResult,
                substitutor.substitute(escapeQuotes));
        assertEqualsParsingResult(escapeInsideDoubleQuotesResult,
                substitutor.substitute(escapeInsideDoubleQuotes));
    }
}