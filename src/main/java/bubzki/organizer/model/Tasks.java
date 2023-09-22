package bubzki.organizer.model;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

public class Tasks {

    /**
     * The method that finds a subset of tasks
     * that are scheduled to run at least once after time <code>from</code> and no later than <code>to</code>.
     *
     * @param tasks the set of tasks that implements {@link Iterable}
     * @param from the start time of the period
     * @param to the end time of the period
     *
     * @return the subset of tasks that fit the specified time period and implements {@link Iterable}
     *
     * @throws IllegalArgumentException if...
     * <ul>
     * <li>timestamps are <code>null</code>;</li>
     * <li><code>from</code> is greater than <code>to</code>.</li>
     * </ul>
     */
    public static Iterable<Task> incoming(Iterable<Task> tasks, LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Time \"to\" must be greater than \"from\".");
        }
        Iterable<Task> clone;
        try {
            clone = (Iterable<Task>) tasks.getClass().getMethod("clone").invoke(tasks);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Cannot create new object for \"tasks\". Result and your iterable object will be changed too.");
            e.printStackTrace(System.out);
            clone = tasks;
        }
        for (Iterator<Task> it = clone.iterator(); it.hasNext();) {
            Task temp = it.next();
            if (temp.nextTimeAfter(from) == null || temp.nextTimeAfter(from).isAfter(to)) {
                it.remove();
            }
        }
        return clone;
    }

    /**
     * The method that will build a calendar of tasks for a given period - a table where each date
     * constitutes a set of tasks to be performed at this time, and one task may occur
     * according to several dates, if it is to be performed several times during the specified period
     *
     * @param tasks the set of tasks that implements {@link Iterable}
     * @param start the start time of the period
     * @param end the end time of the period
     *
     * @return sorted table of tasks and their dates of realization
     */
    public static SortedMap<LocalDateTime, Set<Task>> calendar(Iterable<Task> tasks, LocalDateTime start, LocalDateTime end) {
        SortedMap<LocalDateTime, Set<Task>> taskMap = new TreeMap<>();
        Set<LocalDateTime> timeSet = new HashSet<>();
        Iterable<Task> incomingTasks = Tasks.incoming(tasks, start, end);
        for (Task temp : incomingTasks) {
            if (temp.isRepeated()) {
                timeSet.addAll(Tasks.getTimelineOfRepeatedTask(temp, start, end));
            } else {
                timeSet.add(temp.getTime());
            }
        }
        for (LocalDateTime tempTime : timeSet) {
            Set<Task> tasksSet = new HashSet<>();
            taskMap.put(tempTime, tasksSet);
            for (Task tempTask : incomingTasks) {
                if (tempTask.isRepeated()) {
                    for (LocalDateTime tempTaskTime : Tasks.getTimelineOfRepeatedTask(tempTask, start, end)) {
                        if (tempTime.equals(tempTaskTime)) {
                            tasksSet.add(tempTask);
                            break;
                        }
                    }
                } else {
                    if (tempTime.equals(tempTask.getTime())) {
                        tasksSet.add(tempTask);
                    }
                }
            }
        }
        return taskMap;
    }

    /**
     * The method that returns a set of points in a repeating task's time interval,
     * that are fit the specified time period.
     *
     * @param repeatedTask the repeating task
     * @param start the start time of the period
     * @param end the end time of the period
     *
     * @return the set of points in a task's time interval
     */
    private static List<LocalDateTime> getTimelineOfRepeatedTask(Task repeatedTask, LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> taskTimingSet = new LinkedList<>();
        for (LocalDateTime i = repeatedTask.getStartTime();
                            i.isBefore(repeatedTask.getEndTime()) || i.isEqual(repeatedTask.getEndTime());
                             i = i.plusSeconds(repeatedTask.getRepeatInterval())) {
            if (i.isAfter(start) && (i.isBefore(end) || i.isEqual(end))) {
                taskTimingSet.add(i);
            }
        }
        return taskTimingSet;
    }
}
