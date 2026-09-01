package friday.command;

import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;
import friday.exception.FridayException;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a delete command.
     *
     * @param taskNumber One-based number of the task to remove.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FridayException {
        Task task = getTask(tasks, taskNumber);
        tasks.remove(taskNumber - 1);
        ui.showTaskDeleted(task, tasks.size());
    }

    @Override
    public boolean changesTasks() {
        return true;
    }
}
