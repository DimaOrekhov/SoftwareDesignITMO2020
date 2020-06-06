package ru.itmo.mit.cli;

import org.junit.*;
import ru.itmo.mit.cli.execution.*;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.CommandParserAutomaton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.itmo.mit.cli.TestUtils.assertEqualsParsingResult;


public class CommandParserAutomatonTest {

    private static final CommandParserAutomaton commandParser = new CommandParserAutomaton();
    private static final String cat = "cat";
    private static final Command catRes = new CatCommand(new LinkedList<>());
    private static final String catSomeFile = " cat hello_world.cpp";
    private static final Command catSomeFileRes = new CatCommand(List.of("hello_world.cpp"));
    private final String echoHelloWorld = " echo  hello   world ";
    private final Command echoHelloWorldRes = new EchoCommand(List.of("hello", "world"));
    private static final String exit = "    exit    ";
    private static final Command exitRes = new ExitCommand(new LinkedList<>());
    private static final String assignment = "  x=hello world   ";
    private static final Command assignmentRes = new AssignmentCommand(List.of("x", "hello", "world"));
    private static final String wc = " wc";
    private static final Command wcRes = new WcCommand(new LinkedList<>());
    private static final String pwd = "pwd   ";
    private static final Command pwdRes = new PwdCommand(new LinkedList<>());
    private static final String unknownCommand = "python3 main.py";
    private static final Command unknownCommandRes = new OtherCommand("python3",
            List.of("main.py"));
    private static final String assignmentQuoted = "  x=\"hello world\"";
    private static final Command assignmentQuotedRes = new AssignmentCommand(List.of("x", "hello world"));

    private static PipedCommands asPiped(Command...commands) {
        return new PipedCommandsImpl(Arrays.asList(commands));
    }

    @Test
    public void singleSimpleCommandTest() {
        assertEqualsParsingResult(asPiped(catSomeFileRes), commandParser.parseCommand(catSomeFile));
        assertEqualsParsingResult(asPiped(echoHelloWorldRes), commandParser.parseCommand(echoHelloWorld));
        assertEqualsParsingResult(asPiped(assignmentRes), commandParser.parseCommand(assignment));
        assertEqualsParsingResult(asPiped(exitRes), commandParser.parseCommand(exit));
        assertEqualsParsingResult(asPiped(wcRes), commandParser.parseCommand(wc));
        assertEqualsParsingResult(asPiped(pwdRes), commandParser.parseCommand(pwd));
        assertEqualsParsingResult(asPiped(unknownCommandRes), commandParser.parseCommand(unknownCommand));
    }

    @Test
    public void singleQuotedCommandTest() {
        assertEqualsParsingResult(asPiped(assignmentQuotedRes), commandParser.parseCommand(assignmentQuoted));
    }

    @Test
    public void pipedSimpleCasesTest() {
        assertEqualsParsingResult(asPiped(catSomeFileRes, wcRes),
                commandParser.parseCommand(catSomeFile + "|" + wc));
        assertEqualsParsingResult(asPiped(pwdRes, catRes, wcRes),
                commandParser.parseCommand("pwd|cat|wc"));
    }
}
