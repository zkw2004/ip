import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
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

    /**
     * Starts the chatbot and processes user commands until the user exits.
     *
     * @param args Command-line arguments, which are not used by this program.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printGreeting();

        Storage storage = new Storage("data/friday.txt");
        Parser parser = new Parser();
        TaskList tasks;
        boolean canSaveTasks;
        try {
            Storage.LoadResult loadResult = storage.load();
            tasks = new TaskList(loadResult.getTasks());
            for (String warning : loadResult.getWarnings()) {
                printError(warning);
            }
            canSaveTasks = true;
        } catch (IOException e) {
            tasks = new TaskList();
            canSaveTasks = false;
            printError("I couldn't read the saved tasks, so I've started with an empty list. "
                    + "Saving is disabled for this session to protect the existing data.");
        }

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            try {
                Parser.Command parsedCommand = parser.parse(command);
                if (parsedCommand.getType() == Parser.CommandType.BYE) {
                    printExitMessage();
                    break;
                } else if (parsedCommand.getType() == Parser.CommandType.LIST) {
                    printTaskList(tasks);
                } else if (parsedCommand.getType() == Parser.CommandType.DELETE) {
                    deleteTask(tasks, parsedCommand.getTaskNumber());
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else if (parsedCommand.getType() == Parser.CommandType.MARK) {
                    updateTaskStatus(tasks, parsedCommand.getTaskNumber(), true);
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else if (parsedCommand.getType() == Parser.CommandType.UNMARK) {
                    updateTaskStatus(tasks, parsedCommand.getTaskNumber(), false);
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else {
                    Task newTask = parsedCommand.getTask();
                    tasks.add(newTask);
                    printTaskAdded(newTask, tasks.size());
                    saveTasksSafely(storage, tasks, canSaveTasks);
                }
            } catch (FridayException e) {
                printError(e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Attempts to save the task list and reports a friendly error if writing
     * fails, allowing the chatbot to continue running with its in-memory list.
     *
     * @param storage Storage used to persist tasks.
     * @param tasks The complete task list to save.
     * @param canSaveTasks Whether loading succeeded and saving is safe for this session.
     */
    private static void saveTasksSafely(Storage storage, TaskList tasks, boolean canSaveTasks) {
        if (!canSaveTasks) {
            printError("I couldn't save your tasks because the existing data file could not be read.");
            return;
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            printError("I couldn't save your tasks. Your latest changes might not be available next time.");
        }
    }

    private static void deleteTask(TaskList tasks, int taskNumber) throws FridayException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new FridayException("Please enter a valid task number.");
        }
        Task removedTask = tasks.remove(index);

        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
    }
    /**
     * Prints the chatbot greeting shown at the start of every session.
     */
    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Prints all tasks currently stored in the list.
     *
     * @param tasks The task list to display.
     */
    private static void printTaskList(TaskList tasks) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format(" %d. %s", i + 1, tasks.get(i)));
        }
        System.out.println(LINE);
    }

    /**
     * Marks or unmarks a task after validating the supplied task number.
     *
     * @param tasks The task list containing the task to update.
     * @param taskNumberText The user-supplied task number text.
     * @param markDone Whether the task should be marked done or not done.
     * @throws FridayException If the task number is not a valid existing task.
     */
    private static void updateTaskStatus(TaskList tasks, int taskNumber, boolean markDone)
            throws FridayException {
        int index = taskNumber - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new FridayException("Please enter a valid task number.");
        }

        Task task = tasks.get(index);
        if (markDone) {
            task.markAsDone();
            System.out.println(LINE);
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            task.unmarkAsDone();
            System.out.println(LINE);
            System.out.println(" OK, I've marked this task as not done yet:");
        }

        System.out.println(" " + task);
        System.out.println(LINE);
    }

    /**
     * Prints the standard response after a task is added successfully.
     *
     * @param task The task that was added.
     * @param taskCount The total number of tasks after the addition.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints the standard chatbot error box for invalid user input.
     *
     * @param message The user-facing error message to display.
     */
    private static void printError(String message) {
        System.out.println(LINE);
        System.out.println(" OOPS!!! " + message);
        System.out.println(LINE);
    }

    /**
     * Prints the farewell message before the chatbot exits.
     */
    private static void printExitMessage() {
        System.out.println(LINE);
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
