package friday.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Moves one or all active tasks into the archive.
 */
public class ArchiveCommand extends Command {
    private final Integer taskNumber;

    /**
     * Creates a command that archives one active task.
     *
     * @param taskNumber One-based number of the task to archive.
     */
    public ArchiveCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Creates a command that archives all active tasks.
     */
    public ArchiveCommand() {
        this.taskNumber = null;
    }

    /**
     * Moves selected active tasks into archive storage before changing either list.
     *
     * @param tasks Current active task list.
     * @param archivedTasks Current archived task list.
     * @param ui UI used to display the result.
     * @param storage Storage for active tasks.
     * @param archiveStorage Storage for archived tasks.
     * @throws FridayException If the command cannot archive the selected tasks.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        requireConsoleStorage(storage, archiveStorage);
        if (taskNumber == null) {
            archiveAll(tasks, archivedTasks, ui, storage, archiveStorage);
            return;
        }

        Task task = getTask(tasks, taskNumber);
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        List<Task> updatedArchivedTasks = new ArrayList<>(archivedTasks.asList());
        updatedTasks.remove(taskNumber - 1);
        updatedArchivedTasks.add(task);
        saveMovedTasks(storage, archiveStorage, updatedTasks, updatedArchivedTasks);
        tasks.replaceWith(updatedTasks);
        archivedTasks.replaceWith(updatedArchivedTasks);
        ui.showTaskArchived(task);
    }

    private static void archiveAll(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        if (tasks.size() == 0) {
            ui.showNoTasksToArchive();
            return;
        }

        List<Task> updatedTasks = new ArrayList<>();
        List<Task> updatedArchivedTasks = new ArrayList<>(archivedTasks.asList());
        updatedArchivedTasks.addAll(tasks.asList());
        saveMovedTasks(storage, archiveStorage, updatedTasks, updatedArchivedTasks);
        int archivedCount = tasks.size();
        tasks.replaceWith(updatedTasks);
        archivedTasks.replaceWith(updatedArchivedTasks);
        ui.showTasksArchived(archivedCount);
    }

    private static void saveMovedTasks(Storage storage, Storage archiveStorage, List<Task> updatedTasks,
            List<Task> updatedArchivedTasks) throws FridayException {
        try {
            archiveStorage.save(new TaskList(updatedArchivedTasks));
            storage.save(new TaskList(updatedTasks));
        } catch (IOException e) {
            throw new FridayException("I couldn't update your archive. Your task list was not changed.");
        }
    }

    private static void requireConsoleStorage(Storage storage, Storage archiveStorage) throws FridayException {
        if (storage == null || archiveStorage == null) {
            throw new FridayException("Archiving is available only in the console app.");
        }
    }
}
