package bubzki.organizer.notification;

import bubzki.organizer.RunOrganizer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import bubzki.organizer.model.Task;
import bubzki.organizer.model.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The class that is responsible for the functionality of notifications.
 */
public class Notificator extends Thread {
    private Iterable<Task> tasksList;
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final static int DEFAULT_NOTIFICATION_TIME = 60;
    private int notificationTime;
    private final Image icon;

    /**
     * The constructor with list for notification and icon for alert.
     *
     * @param tasksList list with tasks.
     * @param icon icon that will display in alert.
     */
    public Notificator(Iterable<Task> tasksList, Image icon) {
        this.tasksList = tasksList;
        this.icon = icon;
        notificationTime = DEFAULT_NOTIFICATION_TIME;
    }

    /**
     * The constructor with list for notification.
     *
     * @param tasksList list with tasks.
     */
    public Notificator(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
        this.icon = new Image(Objects.requireNonNull(RunOrganizer.class.getResource("OrganizerIcon.png")).toExternalForm());
        notificationTime = DEFAULT_NOTIFICATION_TIME;
    }

    /**
     * The method that checks whether tasks are suitable for displaying a notification.
     *
     * @return tasks with start time if are suitable for notification, <code>null</code> if none suitable.
     */
    private Map.Entry<LocalDateTime, Set<Task>> notifyTask() {
        for (Map.Entry<LocalDateTime, Set<Task>> entry : Tasks.calendar(tasksList, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1).plusSeconds(notificationTime)).entrySet()) {
            if (entry.getKey().withNano(0).isEqual(LocalDateTime.now().plusSeconds(notificationTime).withNano(0))) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map.Entry<LocalDateTime, Set<Task>> entry = notifyTask();
                if (entry != null) {
                    Platform.runLater(() -> {
                        showAlert(entry);
                    });
                }
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The method that creates and displays notification alert.
     *
     * @param result tasks with start time.
     */
    private void showAlert(Map.Entry<LocalDateTime, Set<Task>> result) {
        StringBuilder message = new StringBuilder();
        String verb;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(icon);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.initModality(Modality.NONE);
        if (result.getValue().size() > 1) {
            message.append("Tasks ");
            verb = " are";
        } else {
            message.append("Task ");
            verb = " is";
        }
        for (Task temp : result.getValue()) {
            message.append("\"").append(temp.getTitle()).append("\", ");
        }
        message.delete(message.length() - 2,  message.length())
                .append(verb)
                .append(" scheduled in ")
                .append(notificationTime).append(" seconds at ")
                .append(result.getKey().format(DATE_TIME_FORMATTER));
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    /**
     * The method that sets time when the notification about the task will come.
     *
     * @param seconds time in seconds.
     */
    public void setNotificationTime(int seconds) {
        notificationTime = seconds;
    }

    /**
     * Setter for the list of task for notification.
     *
     * @param tasksList list of tasks.
     */
    public void setTasksList(Iterable<Task> tasksList) {
        this.tasksList = tasksList;
    }

}
