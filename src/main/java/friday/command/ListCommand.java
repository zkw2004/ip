package friday.command;

import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
