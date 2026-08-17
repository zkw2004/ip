import java.util.Scanner;
import java.util.ArrayList;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    public static void main(String[] args) {
        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String name = "Friday";
        String line = "____________________________________________________________";
        String banner = """
                 _____    _     _
                |  ___| _(_) __| | __ _ _   _
                | |_ | '__| |/ _` |/ _` | | | |
                |  _|| |  | | (_| | (_| | |_| |
                |_|  |_|  |_|\\__,_|\\__,_|\\__, |
                                         |___/
                """;

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm " + name + ".");
        System.out.println("What can I do for you?");
        System.out.println(line);

        while (true) {
            String command = scanner.nextLine();
            if (command.equals("list")) {
                System.out.println(line);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i ++) {
                    System.out.println(String.format(" %d. %s", i + 1, tasks.get(i)));
                }
                System.out.println(line);
                continue;
            }
            else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int index = taskNumber - 1;
                Task current_task = tasks.get(index);
                current_task.markAsDone();
                System.out.println(line);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println(String.format("%s", current_task));
                System.out.println(line);
                continue;

            }
            else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int index = taskNumber - 1;
                Task current_task = tasks.get(index);
                current_task.unmarkAsDone();
                System.out.println(line);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println(String.format("%s", current_task));
                System.out.println(line);
                continue;

            }
            else if (command.equals("bye")) {
                System.out.println(line);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            Task newTask;
            if (command.startsWith("deadline ")) {
                String details = command.substring(9);
                String[] parts = details.split(" /by ", 2);
                String description = parts[0];
                String by = parts[1];
                newTask = new Deadline(description, by);

            } else if (command.startsWith("event ")) {
                String details = command.substring(6);
                String[] firstSplit = details.split(" /from ", 2);
                String description = firstSplit[0];
                String[] secondSplit = firstSplit[1].split(" /to ", 2);
                String from = secondSplit[0];
                String to = secondSplit[1];
                newTask = new Event(description, from, to);

            } else {
                newTask = new ToDo(command);
            }

            tasks.add(newTask);
            System.out.println(line);
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + newTask);
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            System.out.println(line);
        }
        scanner.close();
    }
}
