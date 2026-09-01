import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Friday";
    private static final Path DATA_FILE = Path.of("data", "friday.txt");
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

        ArrayList<Task> tasks;
        boolean canSaveTasks;
        try {
            tasks = loadTasks();
            canSaveTasks = true;
        } catch (IOException e) {
            tasks = new ArrayList<>();
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
                    saveTasksSafely(tasks, canSaveTasks);
                } else if (command.startsWith("mark ")) {
                    updateTaskStatus(tasks, command.substring(5), true);
                    saveTasksSafely(tasks, canSaveTasks);
                } else if (command.startsWith("unmark ")) {
                    updateTaskStatus(tasks, command.substring(7), false);
                    saveTasksSafely(tasks, canSaveTasks);
                } else {
                    Task newTask = parseTask(command);
                    tasks.add(newTask);
                    printTaskAdded(newTask, tasks.size());
                    saveTasksSafely(tasks, canSaveTasks);
                }
            } catch (FridayException e) {
                printError(e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Loads all tasks from the data file. A missing file represents an empty
     * task list, which is the expected situation on the first run.
     *
     * @return The tasks reconstructed from the data file.
     * @throws IOException If an existing data file cannot be read.
     */
    private static ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(DATA_FILE)) {
            return tasks;
        }

        List<String> taskLines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
        for (int i = 0; i < taskLines.size(); i++) {
            try {
                tasks.add(parseSavedTask(taskLines.get(i)));
            } catch (FridayException e) {
                printError(String.format("I skipped corrupted task data on line %d: %s", i + 1, e.getMessage()));
            }
        }
        return tasks;
    }

    /**
     * Reconstructs one task from its pipe-separated file representation.
     *
     * @param taskLine One line read from the data file.
     * @return The reconstructed task.
     * @throws FridayException If the line does not follow the expected file format.
     */
    private static Task parseSavedTask(String taskLine) throws FridayException {
        String[] fields = splitSavedTaskFields(taskLine);
        if (fields.length < 3) {
            throw new FridayException("expected a task type, status, and description.");
        }

        String taskType = fields[0];
        String status = fields[1];
        String description = fields[2];

        int expectedFieldCount;
        switch (taskType) {
        case "T":
            expectedFieldCount = 3;
            break;
        case "D":
            expectedFieldCount = 4;
            break;
        case "E":
            expectedFieldCount = 5;
            break;
        default:
            throw new FridayException("unknown task type '" + taskType + "'.");
        }

        if (fields.length != expectedFieldCount) {
            throw new FridayException("wrong number of fields for task type '" + taskType + "'.");
        }
        if (!status.equals("0") && !status.equals("1")) {
            throw new FridayException("completion status must be 0 or 1.");
        }
        if (description.isBlank()) {
            throw new FridayException("task description cannot be empty.");
        }

        Task task;
        switch (taskType) {
        case "T":
            task = new ToDo(description);
            break;
        case "D":
            if (fields[3].isBlank()) {
                throw new FridayException("deadline date cannot be empty.");
            }
            task = new Deadline(description, parseSavedDate(fields[3]));
            break;
        case "E":
            if (fields[3].isBlank() || fields[4].isBlank()) {
                throw new FridayException("event start and end times cannot be empty.");
            }
            task = new Event(description, parseSavedDateTime(fields[3]), parseSavedDateTime(fields[4]));
            break;
        default:
            throw new AssertionError("Task type was validated earlier.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the ISO date used by saved deadline records.
     *
     * @param value The saved date text.
     * @return The parsed date.
     * @throws FridayException If the date is not valid ISO text.
     */
    private static LocalDate parseSavedDate(String value) throws FridayException {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new FridayException("deadline date is not a valid yyyy-MM-dd date.");
        }
    }

    /**
     * Parses the ISO date-time used by saved event records.
     *
     * @param value The saved date-time text.
     * @return The parsed date-time.
     * @throws FridayException If the date-time is not valid ISO text.
     */
    private static LocalDateTime parseSavedDateTime(String value) throws FridayException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new FridayException("event time is not a valid saved date-time.");
        }
    }

    /**
     * Splits a saved task line at unescaped pipe separators and restores escaped
     * pipe and backslash characters inside individual fields.
     *
     * @param taskLine One line read from the task file.
     * @return The unescaped fields from the line.
     */
    private static String[] splitSavedTaskFields(String taskLine) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length(); i++) {
            char currentCharacter = taskLine.charAt(i);
            if (currentCharacter == '\\' && i + 1 < taskLine.length()) {
                char nextCharacter = taskLine.charAt(i + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    currentField.append(nextCharacter);
                    i++;
                    continue;
                }
            }

            boolean isSeparator = currentCharacter == ' '
                    && i + 2 < taskLine.length()
                    && taskLine.charAt(i + 1) == '|'
                    && taskLine.charAt(i + 2) == ' ';
            if (isSeparator) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += 2;
            } else {
                currentField.append(currentCharacter);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Attempts to save the task list and reports a friendly error if writing
     * fails, allowing the chatbot to continue running with its in-memory list.
     *
     * @param tasks The complete task list to save.
     * @param canSaveTasks Whether loading succeeded and saving is safe for this session.
     */
    private static void saveTasksSafely(ArrayList<Task> tasks, boolean canSaveTasks) {
        if (!canSaveTasks) {
            printError("I couldn't save your tasks because the existing data file could not be read.");
            return;
        }

        try {
            saveTasks(tasks);
        } catch (IOException e) {
            printError("I couldn't save your tasks. Your latest changes might not be available next time.");
        }
    }

    /**
     * Replaces the data file contents with the current task list. Each task is
     * stored on its own line in a pipe-separated format that can be loaded later.
     *
     * @param tasks The complete task list to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    private static void saveTasks(ArrayList<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        List<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }
        Files.write(DATA_FILE, taskLines, StandardCharsets.UTF_8);
    }

    private static void deleteTask(ArrayList<Task> tasks, String taskNumberText) throws FridayException {
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
    private static void printTaskList(ArrayList<Task> tasks) {
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
    private static void updateTaskStatus(ArrayList<Task> tasks, String taskNumberText, boolean markDone)
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
