/**
 * Entry point for the Friday chatbot application.
 */
public class Friday {
    public static void main(String[] args) {
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
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }
}
