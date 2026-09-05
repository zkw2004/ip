package friday;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import friday.command.Command;
import friday.exception.FridayException;
import friday.model.TaskList;
import friday.parser.Parser;
import friday.ui.Ui;

/**
 * Provides chatbot behavior independently of any user interface technology.
 *
 * The existing parser, command, and model classes remain the source of truth
 * for Friday's supported commands. Console-oriented command output is captured
 * here and returned as text for the JavaFX controller to display.
 */
public class Chatbot {
    private final Parser parser;
    private final TaskList tasks;
    private final ByteArrayOutputStream responseBuffer;
    private final Ui responseUi;

    /**
     * Creates a chatbot with a fresh in-memory task list.
     */
    public Chatbot() {
        parser = new Parser();
        tasks = new TaskList();
        responseBuffer = new ByteArrayOutputStream();
        PrintStream responseStream = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8);
        responseUi = new Ui(new Scanner(""), responseStream);
    }

    /**
     * Processes one user input and returns Friday's response.
     *
     * @param input Raw text entered by the user.
     * @return The response generated for the input.
     */
    public String getResponse(String input) {
        responseBuffer.reset();
        try {
            Command command = parser.parse(input);
            command.execute(tasks, responseUi, null);
        } catch (FridayException e) {
            responseUi.showError(e.getMessage());
        }
        responseUi.flush();
        return responseBuffer.toString(StandardCharsets.UTF_8).trim();
    }
}
