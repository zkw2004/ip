import java.io.IOException;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
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

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            try {
                Parser.Command parsedCommand = parser.parse(command);
                if (parsedCommand.getType() == Parser.CommandType.BYE) {
                    ui.showExitMessage();
                    break;
                } else if (parsedCommand.getType() == Parser.CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (parsedCommand.getType() == Parser.CommandType.DELETE) {
                    Task removedTask = deleteTask(tasks, parsedCommand.getTaskNumber());
                    ui.showTaskDeleted(removedTask, tasks.size());
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
                } else if (parsedCommand.getType() == Parser.CommandType.MARK) {
                    Task task = updateTaskStatus(tasks, parsedCommand.getTaskNumber(), true);
                    ui.showTaskStatus(task, true);
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
                } else if (parsedCommand.getType() == Parser.CommandType.UNMARK) {
                    Task task = updateTaskStatus(tasks, parsedCommand.getTaskNumber(), false);
                    ui.showTaskStatus(task, false);
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
                } else {
                    Task newTask = parsedCommand.getTask();
                    tasks.add(newTask);
                    ui.showTaskAdded(newTask, tasks.size());
                    saveTasksSafely(ui, storage, tasks, canSaveTasks);
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

    private static Task deleteTask(TaskList tasks, int taskNumber) throws FridayException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new FridayException("Please enter a valid task number.");
        }
        return tasks.remove(index);
    }

    /**
     * Marks or unmarks a task after validating the supplied task number.
     *
     * @param tasks The task list containing the task to update.
     * @param taskNumber The user-supplied task number.
     * @param markDone Whether the task should be marked done or not done.
     * @throws FridayException If the task number is not a valid existing task.
     */
    private static Task updateTaskStatus(TaskList tasks, int taskNumber, boolean markDone)
            throws FridayException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new FridayException("Please enter a valid task number.");
        }

        Task task = tasks.get(index);
        if (markDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
        return task;
    }
}
