/**
 * Marks one task as completed.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command.
     *
     * @param taskNumber One-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FridayException {
        Task task = getTask(tasks, taskNumber);
        task.markAsDone();
        ui.showTaskStatus(task, true);
    }

    @Override
    public boolean changesTasks() {
        return true;
    }
}
