package bubzki.organizer.controller;

import bubzki.organizer.notification.Notificator;
import bubzki.organizer.model.Task;

/**
 * The class that manages notifications.
 */
public class NotificatorController {
    private final Notificator notificator;
    private final Controller controller;

    /**
     * The constructor that sets controller which will manage this controller.
     * @param controller controller that manages this object.
     */
    protected NotificatorController(Controller controller) {
        this.controller = controller;
        notificator = new Notificator(controller.getTaskList(), controller.icon);
    }

    /**
     * The method that runs daemon thread of notification.
     */
    protected void runNotificator() {
        notificator.setDaemon(true);
        notificator.start();
        controller.logger.debug("Notification is running.");
    }

    /**
     * The method that stops thread of notification.
     */
    protected void stopNotificator() {
        if (notificator.isAlive()) {
            notificator.stop();
        }
    }

    /**
     * The method that updates task list for notification.
     * @param list task list which notificator will operate.
     */
    public void updateNotificator(Iterable<Task> list) {
        notificator.setTasksList(list);
    }

    /**
     * The method that changes the time for how long the notification should arrive before the start of the task.
     * @param seconds time in seconds when the notification about the task will come.
     */
    protected void updateNotificationTime(int seconds) {
        notificator.setNotificationTime(seconds);
    }

}
