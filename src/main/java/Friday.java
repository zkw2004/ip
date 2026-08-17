import java.util.Scanner;
import java.util.ArrayList;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    public static void main(String[] args) {
        ArrayList<String> tasks = new ArrayList<>();
        ArrayList<Boolean> isDone = new ArrayList<>();

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
                    String marker = " ";
                    if (isDone.get(i)) { //check whether that task is done
                        marker = "X";
                    }
                    System.out.println(String.format(" %d.[%s] %s", i + 1, marker, tasks.get(i)));
                }
                System.out.println(line);
                continue;
            }
            else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int index = taskNumber - 1;
                isDone.set(index, true);
                System.out.println(line);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println(String.format("   [X] %s", tasks.get(index)));
                System.out.println(line);
                continue;

            }
            else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int index = taskNumber - 1;
                isDone.set(index, false);
                System.out.println(line);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println(String.format("   [ ] %s", tasks.get(index)));
                System.out.println(line);
                continue;

            }
            else if (command.equals("bye")) {
                System.out.println(line);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            tasks.add(command);
            isDone.add(false);
            System.out.println(line);
            System.out.println(" added: " + command);
            System.out.println(line);
        }
        scanner.close();
    }
}
