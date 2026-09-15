package friday;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the user-interface-independent chatbot adapter.
 */
class ChatbotTest {
    /**
     * Verifies that chatbot commands use the existing task command behavior.
     */
    @Test
    void getResponse_addTask_returnsTaskConfirmation() {
        Chatbot chatbot = new Chatbot();

        String response = chatbot.getResponse("todo read book");

        assertTrue(response.contains("I've added this task"));
        assertTrue(response.contains("[T][ ] read book"));
    }

    /**
     * Verifies that archive commands are rejected by the in-memory JavaFX adapter.
     */
    @Test
    void getResponse_archiveCommand_reportsConsoleOnlyMessage() {
        Chatbot chatbot = new Chatbot();

        String response = chatbot.getResponse("archive all");

        assertTrue(response.contains("Archiving is available only in the console app."));
    }

    /**
     * Verifies that invalid GUI commands provide a response that can receive error styling.
     */
    @Test
    void getResponseResult_invalidCommand_marksResponseAsError() {
        Chatbot chatbot = new Chatbot();

        Chatbot.Response response = chatbot.getResponseResult("not-a-command");

        assertTrue(response.isError());
        assertTrue(response.text().contains("don't know what that means"));
    }
}
