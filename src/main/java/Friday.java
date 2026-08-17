import java.util.Scanner;

/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    public static void main(String[] args) {
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
            if (command.equals("bye")){
                System.out.println(line);
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            System.out.println(line);
            System.out.println(" " + command);
            System.out.println(line);
        }
        scanner.close();
    }
}
