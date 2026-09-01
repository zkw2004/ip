public class Deadline extends Task {
    private String due_date;
    public Deadline(String description, String due_date) {
        super(description);
        this.due_date = due_date;
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + due_date;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + due_date + ")";
    }
}
