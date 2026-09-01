package friday.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import friday.command.AddCommand;
import friday.command.DeleteCommand;
import friday.command.ExitCommand;
import friday.command.ListCommand;
import friday.command.MarkCommand;
import friday.command.UnmarkCommand;
import friday.exception.FridayException;

/**
 * Tests command recognition and validation performed by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_bye_returnsExitCommand() throws FridayException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    void parse_list_returnsListCommand() throws FridayException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
    }

    @Test
    void parseTaskCommands_returnsMatchingCommandTypes() throws FridayException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 2"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 2"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 2"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 2019-06-06"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event project meeting /from 2019-08-06 14:00 /to 2019-08-06 16:00"));
    }

    @Test
    void parse_eventAcceptsCompactTwentyFourHourTime() throws FridayException {
        assertInstanceOf(AddCommand.class,
                parser.parse("event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    void parse_unknownCommand_throwsFridayException() {
        FridayException exception = assertThrows(FridayException.class, () -> parser.parse("later"));

        assertEquals("I'm sorry, but I don't know what that means :-(", exception.getMessage());
    }

    @Test
    void parse_emptyTodo_throwsFridayException() {
        FridayException exception = assertThrows(FridayException.class, () -> parser.parse("todo"));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    void parse_invalidTaskNumber_throwsFridayException() {
        FridayException exception = assertThrows(FridayException.class, () -> parser.parse("delete two"));

        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    void parse_invalidDeadlineDate_throwsFridayException() {
        FridayException exception = assertThrows(FridayException.class,
                () -> parser.parse("deadline return book /by 2019-02-29"));

        assertEquals("Use this format: deadline <description> /by <yyyy-MM-dd date>", exception.getMessage());
    }

    @Test
    void parse_invalidEventDateTime_throwsFridayException() {
        FridayException exception = assertThrows(FridayException.class,
                () -> parser.parse("event meeting /from 2019-08-06 2500 /to 2019-08-06 2600"));

        assertEquals("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                + "/to <yyyy-MM-dd HH:mm>", exception.getMessage());
    }

    @Test
    void parse_commandPrefixCollision_throwsFridayException() {
        assertThrows(FridayException.class, () -> parser.parse("todoagain"));
        assertThrows(FridayException.class, () -> parser.parse("deadlinereport /by Sunday"));
        assertThrows(FridayException.class, () -> parser.parse("eventmeeting /from 2019-08-06 1400 /to 1600"));
    }
}
