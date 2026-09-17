package friday;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import friday.command.Command;
import friday.exception.FridayException;
import friday.model.TaskList;
import friday.parser.Parser;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Provides chatbot behavior independently of any user interface technology.
 *
 * The existing parser, command, and model classes remain the source of truth
 * for Friday's supported commands. Console-oriented command output is captured
 * here and returned as text for the JavaFX controller to display. Both
 * interfaces share file-backed task and archive storage.
 */
public class Chatbot {
    private static final String TASK_FILE = "data/friday.txt";
    private static final String ARCHIVE_FILE = "data/archive.txt";

    private final Parser parser;
    private final TaskList tasks;
    private final TaskList archivedTasks;
    private final Storage storage;
    private final Storage archiveStorage;
    private final boolean canSaveTasks;
    private final boolean canSaveArchivedTasks;
    private final List<String> startupWarnings;
    private final ByteArrayOutputStream responseBuffer;
    private final Ui responseUi;

    /**
     * Creates a chatbot using the application's default task files.
     */
    public Chatbot() {
        this(new Storage(TASK_FILE), new Storage(ARCHIVE_FILE));
    }

    /**
     * Creates a chatbot backed by the supplied task files.
     *
     * @param storage Storage for active tasks.
     * @param archiveStorage Storage for archived tasks.
     */
    public Chatbot(Storage storage, Storage archiveStorage) {
        this.storage = storage;
        this.archiveStorage = archiveStorage;
        parser = new Parser();
        Session activeSession = loadSession(storage, "saved tasks", "Saving is disabled for this session.");
        Session archiveSession = loadSession(archiveStorage, "archive",
                "Archive commands are disabled for this session.");
        tasks = activeSession.tasks();
        archivedTasks = archiveSession.tasks();
        canSaveTasks = activeSession.canSave();
        canSaveArchivedTasks = archiveSession.canSave();
        startupWarnings = new ArrayList<>();
        startupWarnings.addAll(activeSession.warnings());
        startupWarnings.addAll(archiveSession.warnings());
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
        return getResponseResult(input).text();
    }

    /**
     * Processes one user input and records whether the resulting response is an error.
     *
     * @param input Raw command entered by the user.
     * @return The response text together with its display state.
     */
    public Response getResponseResult(String input) {
        responseBuffer.reset();
        boolean isExit = false;
        boolean isError = false;
        try {
            Command command = parser.parse(input);
            isExit = command.isExit();
            Storage availableStorage = canSaveTasks ? storage : null;
            Storage availableArchiveStorage = canSaveArchivedTasks ? archiveStorage : null;
            command.execute(tasks, archivedTasks, responseUi, availableStorage, availableArchiveStorage);
            if (command.changesTasks()) {
                saveTasksSafely(tasks);
            }
        } catch (FridayException e) {
            responseUi.showError(e.getMessage());
            isError = true;
        }
        responseUi.flush();
        String response = responseBuffer.toString(StandardCharsets.UTF_8).trim();
        response = removeConsoleDividers(response);
        return new Response(response, isError, isExit);
    }

    /**
     * Returns warnings encountered while loading task data.
     *
     * @return Immutable startup warnings.
     */
    public List<String> getStartupWarnings() {
        return List.copyOf(startupWarnings);
    }

    private void saveTasksSafely(TaskList currentTasks) {
        if (!canSaveTasks) {
            responseUi.showError("I couldn't save your tasks because the existing data file could not be read.");
            return;
        }
        try {
            storage.save(currentTasks);
        } catch (IOException e) {
            responseUi.showError("I couldn't save your tasks. Your latest changes might not be available next time.");
        }
    }

    private static Session loadSession(Storage storage, String dataDescription, String disabledMessage) {
        try {
            Storage.LoadResult result = storage.load();
            TaskList taskList = new TaskList(result.getTasks());
            return new Session(taskList, true, result.getWarnings());
        } catch (IOException e) {
            String warning = "I couldn't read the " + dataDescription + ", so I've started with an empty list."
                    + " " + disabledMessage;
            return new Session(new TaskList(), false, List.of(warning));
        }
    }

    private static String removeConsoleDividers(String response) {
        return response.lines()
                .filter(line -> !line.equals("____________________________________________________________"))
                .collect(Collectors.joining("\n"))
                .trim();
    }

    private record Session(TaskList tasks, boolean canSave, List<String> warnings) {
    }

    /**
     * Describes a chatbot reply for a graphical user interface.
     *
     * @param text Text to show in the conversation.
     * @param isError Whether the response represents a user-correctable error.
     * @param isExit Whether the response came from the exit command.
     */
    public record Response(String text, boolean isError, boolean isExit) {
    }
}
