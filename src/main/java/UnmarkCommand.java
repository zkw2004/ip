/**
 * Marks one task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates an unmark command.
     *
     * @param taskNumber One-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FridayException {
        Task task = getTask(tasks, taskNumber);
        task.unmarkAsDone();
        ui.showTaskStatus(task, false);
    }

    @Override
    public boolean changesTasks() {
        return true;
    }
}
