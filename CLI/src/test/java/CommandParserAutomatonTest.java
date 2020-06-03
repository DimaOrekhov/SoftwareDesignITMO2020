import org.junit.*;
import ru.itmo.mit.cli.execution.*;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.CommandParserAutomaton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandParserAutomatonTest {

    private static final CommandParserAutomaton commandParser = new CommandParserAutomaton();
    private static final String catSomeFile = " cat hello_world.cpp";
    private static final Command catSomeFileRes = new CatCommand(List.of("hello_world.cpp"));
    private final String echoHelloWorld = " echo  hello   world ";
    private final Command echoHelloWorldRes = new EchoCommand(List.of("hello", "world"));
    private static final String exit = "    exit    ";
    private static final Command exitRes = new ExitCommand(new LinkedList<>());
    private static final String assignment = "  x=hello world   ";
    private static final Command assignmentRes = new AssignmentCommand(List.of("x", "hello", "world"));
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
        assertEquals(asPiped(catSomeFileRes), commandParser.parseCommand(catSomeFile));
        assertEquals(asPiped(echoHelloWorldRes), commandParser.parseCommand(echoHelloWorld));
        assertEquals(asPiped(assignmentRes), commandParser.parseCommand(assignment));
        assertEquals(asPiped(exitRes), commandParser.parseCommand(exit));
        assertEquals(asPiped(unknownCommandRes), commandParser.parseCommand(unknownCommand));
    }

    @Test
    public void singleQuotedCommandTest() {
        assertEquals(asPiped(assignmentQuotedRes), commandParser.parseCommand(assignmentQuoted));
    }

    @Test
    public void pipedSimpleCasesTest() {

    }
}
