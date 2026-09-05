package friday;

import java.io.InputStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Coordinates the controls in {@code MainWindow.fxml} with a chatbot.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage;
    private final Image chatbotImage;
    private Chatbot chatbot;

    /**
     * Creates the controller and loads the two avatars from the classpath.
     */
    public MainWindow() {
        userImage = loadImage("/images/tonystark.jpeg");
        chatbotImage = loadImage("/images/friday.png");
    }

    /**
     * Connects the view to the chatbot used for generating responses.
     *
     * @param chatbot Chatbot that processes user input.
     */
    public void setChatbot(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    /**
     * Keeps the newest dialog visible when the container grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
    }

    /**
     * Sends the current input to the chatbot and appends both messages.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String response = chatbot.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        dialogContainer.getChildren().add(DialogBox.getChatbotDialog(response, chatbotImage));
        userInput.clear();
    }

    /**
     * Loads an image resource and reports a useful error when it is missing.
     *
     * @param resourcePath Absolute classpath path of the image.
     * @return Loaded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            throw new IllegalStateException("Could not find image resource: " + resourcePath);
        }
        return new Image(imageStream);
    }
}
