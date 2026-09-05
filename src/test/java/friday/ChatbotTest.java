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
}
