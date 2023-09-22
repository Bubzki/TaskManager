package bubzki.organizer.controller;

import javafx.fxml.FXML;
import bubzki.organizer.model.Task;

/**
 * The class that is responsible for the main functionality of the app.
 */
public class ActionController extends Controller {
    /**
     * The method that adds task to list and table. Function of button <code>Add</code>.
     */
    @FXML
    private void addButtonAction() {
        Task temp;
        if (activateGroup.getSelectedToggle() != null) {
            try {
                if (!textFieldIsEmpty(titleField)) {
                    if (repeatRadioTrue.isSelected()) {
                        temp = new Task(titleField.getText(), startTimeField.getDateTimeValue(), endTimeField.getDateTimeValue(), intervalField.getValue());
                    } else if (repeatRadioFalse.isSelected()) {
                        temp = new Task(titleField.getText(), startTimeField.getDateTimeValue());
                    } else {
                        throw new IllegalArgumentException("Select whether the task is repeated.");
                    }
                    temp.setActive(activeRadioTrue.isSelected());
                    list.add(temp);
                    logger.info("Task was added.");
                    unselectColumn();
                    notificator.updateNotificator(list);
                    loadMainTable();
                } else {
                    throw new IllegalArgumentException("Title filed must be filled in.");
                }
            } catch (IllegalArgumentException e) {
                showError(e);
                logger.error("Add task error.", e);
            }
        } else {
            showError("Unable to add task.", "Please, select an activity status for the task.");
        }
    }

    /**
     * The method that edits task from list and table. Function of button <code>Edit</code>.
     */
    @FXML
    private void editButtonAction() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            if (activateGroup.getSelectedToggle() != null) {
                try {
                    if (!textFieldIsEmpty(titleField)) {
                        temp.setTitle(titleField.getText());
                        if (repeatRadioTrue.isSelected()) {
                            temp.setTime(startTimeField.getDateTimeValue(), endTimeField.getDateTimeValue(), intervalField.getValue());
                        } else {
                            temp.setTime(startTimeField.getDateTimeValue());
                        }
                        temp.setActive(activeRadioTrue.isSelected());
                        logger.info("Task was edited.");
                        unselectColumn();
                        notificator.updateNotificator(list);
                        mainTable.refresh();
                    } else {
                        throw new IllegalArgumentException("Title filed must be filled in.");
                    }
                } catch (IllegalArgumentException e) {
                    showError(e);
                    logger.error("Edit task error.", e);
                }
            } else {
                showError("Unable to edit task.", "Please, select an activity status for the task.");
            }
        } else {
            showError("Unable to edit task.", "Please, select a row to edit.");
        }
    }

    /**
     * The method that removes task from list and table. Function of button <code>Remove</code>.
     */
    @FXML
    private void removeButtonAction() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            list.remove(temp);
            logger.info("Task was removed.");
            unselectColumn();
            notificator.updateNotificator(list);
            loadMainTable();
        } else {
            showError("Unable to remove task.", "Please, select a row to remove.");
        }
    }

    /**
     * The method that resets the Main tab. Function of button <code>Reset</code>.
     */
    @FXML
    private void resetButtonAction() {
        unselectColumn();
        repeatRadioTrue.setSelected(false);
        repeatRadioFalse.setSelected(true);
        loadMainTable();
    }

    /**
     * The method that loads calendar of tasks in table
     * and save input data from date fields in Calendar tab. Function of button <code>Filter</code>.
     */
    @FXML
    private void calendarButtonAction() {
        try {
            cachedFromField = fromField.getDateTimeValue();
            cachedToField = toField.getDateTimeValue();
            loadCalendarTable();
            logger.info("Calendar was loaded.");
        } catch (IllegalArgumentException e) {
            showError(e);
            logger.error("Calendar error.", e);
        }
    }

    /**
     * The method that refreshes table in Calendar tab.
     */
    @FXML
    private void refreshButtonAction() {
        calendarTable.getSelectionModel().clearSelection();
        if (cachedFromField != null && cachedToField != null) {
            fromField.setDateTimeValue(cachedFromField);
            toField.setDateTimeValue(cachedToField);
            loadCalendarTable();
        }
    }
}
