package bubzki.organizer.view;

import bubzki.organizer.controller.Controller;
import bubzki.organizer.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The class that is responsible for displaying the JavaFX window.
 */
public abstract class View {
    protected final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    protected final static int MIN_SPINNER_VALUE = 0;
    protected final static int MAX_SPINNER_VALUE = Integer.MAX_VALUE;
    protected final static int INIT_SPINNER_VALUE = 0;

    protected LocalDateTime cachedFromField;
    protected LocalDateTime cachedToField;

    protected final AbstractTaskList list = TaskListFactory.createTaskList(ListTypes.types.ARRAY);

    public final Logger logger = Logger.getLogger(View.class);

    @FXML
    protected TableView<Task> mainTable;
    @FXML
    protected TableColumn<Task, String> titleMainColumn;
    @FXML
    protected TableColumn<Task, String> timeMainColumn;
    @FXML
    protected TableColumn<Task, String> startMainColumn;
    @FXML
    protected TableColumn<Task, String> endMainColumn;
    @FXML
    protected TableColumn<Task, String> activeMainColumn;
    @FXML
    protected TableColumn<Task, String> intervalMainColumn;

    @FXML
    protected TextField titleField;
    @FXML
    protected Label startTimeLabel;
    @FXML
    protected DateTimePicker startTimeField;
    @FXML
    protected Label endTimeLabel;
    @FXML
    protected DateTimePicker endTimeField;
    @FXML
    protected Label intervalLabel;
    @FXML
    protected Spinner<Integer> intervalField;

    @FXML
    protected ToggleGroup activateGroup;
    @FXML
    protected RadioButton activeRadioTrue;
    @FXML
    protected RadioButton activeRadioFalse;
    @FXML
    protected ToggleGroup repeatGroup;
    @FXML
    protected RadioButton repeatRadioTrue;
    @FXML
    protected RadioButton repeatRadioFalse;

    @FXML
    protected TableView<View.CalendarTableHelper> calendarTable;
    @FXML
    private TableColumn<View.CalendarTableHelper, String> titleCalendarColumn;
    @FXML
    private TableColumn<View.CalendarTableHelper, String> timeCalendarColumn;

    @FXML
    protected DateTimePicker fromField;
    @FXML
    protected DateTimePicker toField;

    /**
     * The method that sets parameters to UI elements.
     */
    protected void initializeView() {
        titleMainColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        startMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getStartTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        endMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getEndTime();
            if (task.isRepeated()) {
                return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
            } else {
                return new SimpleObjectProperty<>("-");
            }
        });
        activeMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            boolean active = task.isActive();
            String str = active ? "+" : "-";
            return new SimpleObjectProperty<>(str);
        });
        intervalMainColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            int interval = task.getRepeatInterval();
            String str = interval == 0 ? "-" : Integer.toString(interval);
            return new SimpleObjectProperty<>(str);
        });
        repeatGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (repeatGroup.getSelectedToggle() == repeatRadioFalse) {
                startTimeLabel.setText("Time:");
                intervalLabel.setVisible(false);
                intervalField.setVisible(false);
                endTimeLabel.setVisible(false);
                endTimeField.setVisible(false);
            } else if (repeatGroup.getSelectedToggle() == repeatRadioTrue) {
                startTimeLabel.setText("Start time:");
                intervalLabel.setVisible(true);
                intervalField.setVisible(true);
                endTimeLabel.setVisible(true);
                endTimeField.setVisible(true);
            }
        });
        intervalField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_SPINNER_VALUE, MAX_SPINNER_VALUE, INIT_SPINNER_VALUE));
        titleCalendarColumn.setCellValueFactory(new PropertyValueFactory<>("titles"));
        timeCalendarColumn.setCellValueFactory(param -> {
            Controller.CalendarTableHelper helper = param.getValue();
            LocalDateTime time = helper.getTime();
            return new SimpleObjectProperty<>(time.format(DATE_TIME_FORMATTER));
        });
        repeatRadioFalse.setSelected(true);
        dateStyle();
    }

    /**
     * The method that loads tasks to the table in Main tab.
     */
    protected void loadMainTable() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        for (Task temp : list) {
            taskList.add(temp);
        }
        mainTable.setItems(taskList);
    }

    /**
     * The method that loads tasks to the table in Calendar tab.
     */
    protected void loadCalendarTable() {
        SortedMap<LocalDateTime, Set<Task>> map = Tasks.calendar(list, cachedFromField, cachedToField);
        List<CalendarTableHelper> calendarTableHelperList = new ArrayList<>(map.size());
        for (Map.Entry<LocalDateTime, Set<Task>> entry : map.entrySet()) {
            calendarTableHelperList.add(new View.CalendarTableHelper(entry.getKey(), entry.getValue()));
        }
        ObservableList<View.CalendarTableHelper> calendar = FXCollections.observableList(calendarTableHelperList);
        calendarTable.setItems(calendar);
    }

    /**
     * The class that transforms one element of {@link SortedMap} into
     * object {@link CalendarTableHelper} to represent on Calendar table.
     */
    protected static class CalendarTableHelper {
        private LocalDateTime time;
        private String titles;
        private Set<Task> tasks;

        public CalendarTableHelper(LocalDateTime time, Set<Task> tasks) {
            this.time = time;
            this.tasks = tasks;
            transformToString();
        }

        private void transformToString() {
            StringBuilder str = new StringBuilder();
            for (Task temp : tasks) {
                str.append(temp.getTitle()).append("\n");
            }
            titles = str.toString();
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }

        public void setTitles(String titles) {
            this.titles = titles;
        }

        public void setTasks(Set<Task> tasks) {
            this.tasks = tasks;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public String getTitles() {
            return titles;
        }

        public Set<Task> getTasks() {
            return tasks;
        }
    }

    /**
     * The method that sets to fields values from the selected task in the table.
     */
    @FXML
    private void selectColumn() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            titleField.setText(temp.getTitle());
            if (temp.isActive()) {
                activeRadioTrue.setSelected(true);
            } else {
                activeRadioFalse.setSelected(true);
            }
            if (temp.isRepeated()) {
                repeatRadioTrue.setSelected(true);
                startTimeField.setDateTimeValue(temp.getStartTime());
                endTimeField.setDateTimeValue(temp.getEndTime());
                intervalField.getValueFactory().setValue(temp.getRepeatInterval());
            } else {
                repeatRadioFalse.setSelected(true);
                startTimeField.setDateTimeValue(temp.getTime());
            }
        }
    }

    /**
     * The method that clears selection in table, fields and radio buttons from Main tab.
     */
    protected void unselectColumn() {
        mainTable.getSelectionModel().clearSelection();
        titleField.clear();
        startTimeField.setDateTimeValue(null);
        endTimeField.setDateTimeValue(null);
        intervalField.getValueFactory().setValue(INIT_SPINNER_VALUE);
        activeRadioTrue.setSelected(false);
        activeRadioFalse.setSelected(false);
    }

    /**
     * The method that creates and displays Error alert.
     *
     * @param headerMessage message in the header of Error alert.
     * @param contentMessage message in the content of Error alert.
     */
    public void showError(String headerMessage, String contentMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(headerMessage);
        alert.setContentText(contentMessage);
        alert.showAndWait();
    }

    /**
     * The method that creates and displays Error alert.
     *
     * @param exception exception from which the error message will be taken.
     */
    public void showError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    /**
     * The method that creates and displays Error alert.
     *
     * @param headerMessage message in the header of Error alert.
     * @param exception exception from which the stack trace will be taken.
     */
    public void showError(String headerMessage, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(titleField.getScene().getWindow());
        alert.setTitle("Error");
        alert.setHeaderText(headerMessage);
        Label label = new Label("Stack Trace:");
        VBox alertContent = new VBox();
        TextArea textArea = new TextArea();
        String contentMessage;
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            exception.printStackTrace(pw);
            contentMessage = sw.toString();
            textArea.setText(contentMessage);
        } catch (IOException e) {
            logger.error("Alert stack trace error.", e);
        }
        textArea.setEditable(false);
        alertContent.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(alertContent);
        alert.showAndWait();
    }

    /**
     * The method that sets date format to date fields.
     */
    protected void dateStyle() {
        startTimeField.setDateTimeValue(null);
        endTimeField.setDateTimeValue(null);
        toField.setDateTimeValue(null);
        fromField.setDateTimeValue(null);
        startTimeField.setFormat("dd.MM.yyyy HH:mm:ss");
        endTimeField.setFormat("dd.MM.yyyy HH:mm:ss");
        toField.setFormat("dd.MM.yyyy HH:mm:ss");
        fromField.setFormat("dd.MM.yyyy HH:mm:ss");
    }
}
