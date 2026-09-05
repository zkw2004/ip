package friday;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Reusable chat message control backed by {@code DialogBox.fxml}.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box and loads its FXML-defined child controls.
     *
     * @param text Message text to display.
     * @param image Avatar image to display.
     */
    public DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the dialog box view.", e);
        }
        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a user-aligned dialog.
     *
     * @param text User message.
     * @param image User avatar.
     * @return A dialog containing the user message.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a chatbot-aligned dialog.
     *
     * @param text Chatbot message.
     * @param image Chatbot avatar.
     * @return A dialog containing the chatbot message.
     */
    public static DialogBox getChatbotDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Reverses the child order and aligns the chatbot message on the left.
     */
    private void flip() {
        ObservableList<Node> children = getChildren();
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(children);
        Collections.reverse(reversedChildren);
        children.setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
    }
}
