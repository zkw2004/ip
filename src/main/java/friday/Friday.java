package friday;

import java.io.IOException;

import friday.command.Command;
import friday.exception.FridayException;
import friday.model.TaskList;
import friday.parser.Parser;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    /**
     * Creates the application coordinator.
     */
    public Friday() {
    }

    /**
     * Starts the chatbot and processes user commands until the user exits.
     *
     * @param args Command-line arguments, which are not used by this program.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showGreeting();

        Storage storage = new Storage("data/friday.txt");
        Parser parser = new Parser();
        TaskList tasks;
        boolean canSaveTasks;
        try {
            Storage.LoadResult loadResult = storage.load();
            // Storage.load() must always return a result when it completes normally.
            assert loadResult != null : "A successful load must provide a result.";
            tasks = new TaskList(loadResult.getTasks());
            for (String warning : loadResult.getWarnings()) {
                ui.showError(warning);
            }
            canSaveTasks = true;
        } catch (IOException e) {
            tasks = new TaskList();
            canSaveTasks = false;
            ui.showError("I couldn't read the saved tasks, so I've started with an empty list. "
                    + "Saving is disabled for this session to protect the existing data.");
        }

        // The command loop must never start without a task list to operate on.
        assert tasks != null : "Tasks must be initialized before processing commands.";

        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                // Ui.readCommand() is expected to return a line whenever input is available.
                assert fullCommand != null : "An available command must not be null.";
                Command command = parser.parse(fullCommand);
                // Parser.parse() either returns a command or throws FridayException.
                assert command != null : "A valid input must produce a command.";
                command.execute(tasks, ui, storage);
                if (command.changesTasks()) {
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
                }
                if (command.isExit()) {
                    break;
                }
            } catch (FridayException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Attempts to save the task list and reports a friendly error if writing
     * fails, allowing the chatbot to continue running with its in-memory list.
     *
     * @param ui UI used to report save failures.
     * @param storage Storage used to persist tasks.
     * @param tasks The complete task list to save.
     * @param canSaveTasks Whether loading succeeded and saving is safe for this session.
     */
    private static void saveTasksSafely(Ui ui, Storage storage, TaskList tasks, boolean canSaveTasks) {
        assert ui != null : "UI must be available when reporting save failures.";
        assert storage != null : "Storage must be available when saving tasks.";
        assert tasks != null : "Tasks must be available when saving.";

        if (!canSaveTasks) {
            ui.showError("I couldn't save your tasks because the existing data file could not be read.");
            return;
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showError("I couldn't save your tasks. Your latest changes might not be available next time.");
        }
    }

}
