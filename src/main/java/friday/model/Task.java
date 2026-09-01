package friday.model;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }
    public void markAsDone() {
        this.isDone = true;
    }
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Converts the common task fields into the format used in the data file.
     * Subclasses add their task type and any additional fields.
     *
     * @return The completion status and description separated by pipes.
     */
    public String toFileString() {
        return String.format("%d | %s", isDone ? 1 : 0, escapeFileField(description));
    }

    /**
     * Escapes characters that have a special meaning in the task file format.
     * Backslashes are escaped first so newly added pipe escapes remain distinct.
     *
     * @param value A task field to store.
     * @return The field with backslashes and pipe characters escaped.
     */
    protected static String escapeFileField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }
}
