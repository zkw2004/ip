package friday.command;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * An executable user command.
 *
 * Concrete commands contain the behavior for one command type, while Friday
 * remains responsible for coordinating parsing, persistence, and the loop.
 */
public abstract class Command {
    /**
     * Executes this command against the current application state.
     *
     * @param tasks Current task list.
     * @param ui UI used for user-facing responses.
     * @param storage Storage available to commands that need persistence.
     * @throws FridayException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws FridayException;

    /**
     * @return Whether this command should end the application loop.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * @return Whether this command changed the task list and should be saved.
     */
    public boolean changesTasks() {
        return false;
    }

    /**
     * Gets a task by its one-based user-facing number.
     *
     * @param tasks Current task list.
     * @param taskNumber One-based task number.
     * @return The requested task.
     * @throws FridayException If the number does not identify an existing task.
     */
    protected Task getTask(TaskList tasks, int taskNumber) throws FridayException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new FridayException("Please enter a valid task number.");
        }
        return tasks.get(index);
    }
}
