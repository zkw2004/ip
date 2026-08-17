import java.util.Scanner;
import java.util.ArrayList;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    public static void main(String[] args) {
        ArrayList<String> tasks = new ArrayList<>();
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
                for (int i = 0; i < tasks.size(); i ++) {
                    System.out.println(String.format(" %d. %s", i + 1, tasks.get(i)));
                }
                System.out.println(line);
                continue;
            } else if (command.equals("bye")) {
                System.out.println(line);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            tasks.add(command);
            System.out.println(line);
            System.out.println(" added: " + command);
            System.out.println(line);
        }
        scanner.close();
    }
}
