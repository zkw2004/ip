package friday.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import friday.model.Task;
import friday.model.TaskList;

/**
 * Handles Friday's console input and user-facing output.
 *
 * Keeping console details here lets {@link Friday} coordinate application
 * flow without knowing how messages are formatted or how commands are read.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Friday";
    private static final String BANNER = """
             _____    _     _
            |  ___| _(_) __| | __ _ _   _
            | |_ | '__| |/ _` |/ _` | | | |
            |  _|| |  | | (_| | (_| | |_| |
            |_|  |_|  |_|\\__,_|\\__,_|\\__, |
                                     |___/
            """;

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI backed by standard input and output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI with explicit input and output streams.
     *
     * @param scanner Source used for console input.
     * @param output Destination used for user-facing output.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Prints the greeting shown at the start of every session.
     */
    public void showGreeting() {
        output.println(LINE);
        output.println(BANNER);
        output.println("Hello! I'm " + NAME + ".");
        output.println("What can I do for you?");
        output.println(LINE);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return true when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command from standard input.
     *
     * @return The next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints all tasks currently stored in the list.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        output.println(LINE);
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(String.format(" %d. %s", i + 1, tasks.get(i)));
        }
        output.println(LINE);
    }

    /**
     * Prints tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks Tasks selected by the find command.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output.println(LINE);
        output.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println(String.format(" %d. %s", i + 1, matchingTasks.get(i)));
        }
        if (matchingTasks.isEmpty()) {
            output.println(" No matching tasks found.");
        }
        output.println(LINE);
    }

    /**
     * Prints the standard response after a task is added successfully.
     *
     * @param task The task that was added.
     * @param taskCount The total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(LINE);
        output.println(" Got it. I've added this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Prints the standard response after a task is deleted successfully.
     *
     * @param task The task that was removed.
     * @param taskCount The number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(LINE);
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Prints the response after changing a task's completion status.
     *
     * @param task The task whose status was changed.
     * @param markDone Whether the task was marked done or unmarked.
     */
    public void showTaskStatus(Task task, boolean markDone) {
        output.println(LINE);
        if (markDone) {
            output.println(" Nice! I've marked this task as done:");
        } else {
            output.println(" OK, I've marked this task as not done yet:");
        }
        output.println(" " + task);
        output.println(LINE);
    }

    /**
     * Prints a user-friendly error message.
     *
     * @param message The message to display.
     */
    public void showError(String message) {
        output.println(LINE);
        output.println(" OOPS!!! " + message);
        output.println(LINE);
    }

    /**
     * Prints the farewell message before the chatbot exits.
     */
    public void showExitMessage() {
        output.println(LINE);
        output.println(" Bye. Hope to see you again soon!");
        output.println(LINE);
    }

    /**
     * Releases the console input resource.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Flushes output so callers using a buffered stream can read the complete response.
     */
    public void flush() {
        output.flush();
    }
}
