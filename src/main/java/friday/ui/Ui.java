package friday.ui;

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

    /**
     * Creates a UI backed by standard input and output.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Prints the greeting shown at the start of every session.
     */
    public void showGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
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
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format(" %d. %s", i + 1, tasks.get(i)));
        }
        System.out.println(LINE);
    }

    /**
     * Prints the standard response after a task is added successfully.
     *
     * @param task The task that was added.
     * @param taskCount The total number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints the standard response after a task is deleted successfully.
     *
     * @param task The task that was removed.
     * @param taskCount The number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints the response after changing a task's completion status.
     *
     * @param task The task whose status was changed.
     * @param markDone Whether the task was marked done or unmarked.
     */
    public void showTaskStatus(Task task, boolean markDone) {
        System.out.println(LINE);
        if (markDone) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println(" " + task);
        System.out.println(LINE);
    }

    /**
     * Prints a user-friendly error message.
     *
     * @param message The message to display.
     */
    public void showError(String message) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + message);
        System.out.println(LINE);
    }

    /**
     * Prints the farewell message before the chatbot exits.
     */
    public void showExitMessage() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Releases the console input resource.
     */
    public void close() {
        scanner.close();
    }
}
