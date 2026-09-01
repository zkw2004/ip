import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Scanner;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Friday";
    private static final DateTimeFormatter DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_COMPACT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
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
                if (command.equals("bye")) {
                    printExitMessage();
                    break;
                } else if (command.equals("list")) {
                    printTaskList(tasks);
                } else if (command.startsWith("delete ")) {
                    deleteTask(tasks, command.substring(7));
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else if (command.startsWith("mark ")) {
                    updateTaskStatus(tasks, command.substring(5), true);
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else if (command.startsWith("unmark ")) {
                    updateTaskStatus(tasks, command.substring(7), false);
                    saveTasksSafely(storage, tasks, canSaveTasks);
                } else {
                    Task newTask = parseTask(command);
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

    private static void deleteTask(TaskList tasks, String taskNumberText) throws FridayException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new FridayException("Please enter a valid task number.");
        }

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
    private static void updateTaskStatus(TaskList tasks, String taskNumberText, boolean markDone)
            throws FridayException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(taskNumberText.trim());
        } catch (NumberFormatException e) {
            throw new FridayException("Please enter a valid task number.");
        }

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
     * Parses a task-creation command and returns the corresponding task object.
     *
     * @param command The full user input command.
     * @return The task created from the command.
     * @throws FridayException If the command format or task details are invalid.
     */
    private static Task parseTask(String command) throws FridayException {
        if (isCommandWord(command, "todo")) {
            return parseTodo(command);
        } else if (isCommandWord(command, "deadline")) {
            return parseDeadline(command);
        } else if (isCommandWord(command, "event")) {
            return parseEvent(command);
        }

        throw new FridayException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Checks whether the user input starts with a full command word rather than
     * just sharing the same prefix.
     *
     * @param command The full user input.
     * @param keyword The supported command word to check.
     * @return True if the input is exactly the command word or starts with it followed by a space.
     */
    private static boolean isCommandWord(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Parses a todo command after checking that its description is present.
     *
     * @param command The full todo command.
     * @return A new todo task.
     * @throws FridayException If the description is missing.
     */
    private static Task parseTodo(String command) throws FridayException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new FridayException("The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Parses a deadline command after validating its description and /by section.
     *
     * @param command The full deadline command.
     * @return A new deadline task.
     * @throws FridayException If the command is missing required deadline details.
     */
    private static Task parseDeadline(String command) throws FridayException {
        String details = command.substring(8).trim();
        String[] parts = details.split(" /by ", 2);

        if (parts.length < 2) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new FridayException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }

        try {
            return new Deadline(description, LocalDate.parse(by, DATE_INPUT_FORMAT));
        } catch (DateTimeParseException e) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }
    }

    /**
     * Parses an event command after validating its description and time range.
     *
     * @param command The full event command.
     * @return A new event task.
     * @throws FridayException If the command is missing required event details.
     */
    private static Task parseEvent(String command) throws FridayException {
        String details = command.substring(5).trim();
        String[] firstSplit = details.split(" /from ", 2);

        if (firstSplit.length < 2) {
            throw new FridayException("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                    + "/to <yyyy-MM-dd HH:mm>");
        }

        String description = firstSplit[0].trim();
        String[] secondSplit = firstSplit[1].split(" /to ", 2);

        if (secondSplit.length < 2) {
            throw new FridayException("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                    + "/to <yyyy-MM-dd HH:mm>");
        }

        String fromText = secondSplit[0].trim();
        String toText = secondSplit[1].trim();

        if (description.isEmpty()) {
            throw new FridayException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new FridayException("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                    + "/to <yyyy-MM-dd HH:mm>");
        }

        return new Event(description, parseEventDateTime(fromText), parseEventDateTime(toText));
    }

    /**
     * Parses an event date-time in the documented format, also accepting the
     * compact 24-hour form without a colon.
     *
     * @param value The user-supplied date-time text.
     * @return The parsed date-time.
     * @throws FridayException If neither supported format matches.
     */
    private static LocalDateTime parseEventDateTime(String value) throws FridayException {
        try {
            return LocalDateTime.parse(value, EVENT_INPUT_FORMAT);
        } catch (DateTimeParseException firstFailure) {
            try {
                return LocalDateTime.parse(value, EVENT_COMPACT_INPUT_FORMAT);
            } catch (DateTimeParseException secondFailure) {
                throw new FridayException("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                        + "/to <yyyy-MM-dd HH:mm>");
            }
        }
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
