package bubzki.organizer.controller;

import bubzki.organizer.RunOrganizer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import bubzki.organizer.model.AbstractTaskList;
import bubzki.organizer.view.View;

import java.util.*;

/**
 * The class that manages the JavaFX window.
 */
public class Controller extends View {
    protected final Image icon = new Image(Objects.requireNonNull(RunOrganizer.class.getResource("OrganizerIcon.png")).toExternalForm());

    protected final LoadController loadController = new LoadController(this);
    protected final NotificatorController notificator = new NotificatorController(this);

    /**
     * The method that initializes JavaFX window:
     * <ul>
     * <li>loads data from the file;</li>
     * <li>turn on the notification;</li>
     * <li>loads data to table in Main tab.</li>
     * </ul>
     */
    @FXML
    protected void initialize() {
        initializeView();
        loadController.readingData();
        notificator.runNotificator();
        loadMainTable();
    }

    /**
     * The method that checks text field.
     *
     * @param textField text field that need to check.
     * @return <code>true</code> if the text field is not empty or not null, in other situations is <code>false</code>.
     */
    protected boolean textFieldIsEmpty(TextField textField) {
        if (textField.getText() == null) {
            return true;
        }
        else {
            return textField.getText().isEmpty();
        }
    }

    /**
     * The method that writes data to the file.
     */
    public void writingData() {
        loadController.writingData();
    }

    /**
     * Getter for the list of task.
     *
     * @return task list from the {@link View}.
     */
    public AbstractTaskList getTaskList() {
       return list;
    }

}
