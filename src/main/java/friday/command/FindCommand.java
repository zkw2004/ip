package friday.command;

import java.util.List;

import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Finds tasks whose descriptions contain a requested keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword Text to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays tasks matching the command's keyword.
     *
     * @param tasks Current task list to search.
     * @param ui UI used to display matching tasks.
     * @param storage Storage available to the command, which is unused.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage, Storage archiveStorage) {
        List<Task> matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
