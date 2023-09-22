package bubzki.organizer.model;

import java.io.*;
import java.time.*;
import java.util.Objects;

public class Task implements Cloneable, Externalizable {
    private String title;
    private LocalDateTime time;
    private LocalDateTime start;
    private LocalDateTime end;
    private Duration interval;
    private boolean active;
    private boolean repeated;

    /**
     * Default constructor for serialization.
     */
    public Task() {
    }

    /**
     * Constructor that creates an inactive task
     * which is executed in the set period of time
     * without a repeating and has the given name.
     *
     * @param title  the task name
     * @param time  the notification time
     *
     * @throws IllegalArgumentException if <code>time</code> is <code>null</code>
     */
    public Task(String title, LocalDateTime time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("Time must not be null.");
        }
        this.title = title;
        this.time = time;
        this.active = false;
        this.repeated = false;
    }

    /**
     * Constructor that creates an inactive task
     * which is executed in the set period of time (both start and end inclusive)
     * with the seated interval and has the given name.
     *
     * @param title the task name
     * @param start the notification start time
     * @param end the notification end time
     * @param interval Time interval after which it is necessary to repeat task notification.
     *
     * @throws IllegalArgumentException if...
     * <ul>
     * <li>timestamps are <code>null</code>;</li>
     * <li><code>start</code> is greater than <code>end</code>;</li>
     * <li><code>interval</code> isn't positive.</li>
     * </ul>
     */
    public Task(String title, LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Timestamps must not be null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The end of task must be greater than the start of task.");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero.");
        }
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = Duration.ofSeconds(interval);
        this.active = false;
        this.repeated = true;
    }

    /**
     * Getter for the task title.
     *
     * @return the task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for a name of task.
     *
     * @param title a task name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the task status.
     *
     * @return the task status
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for a task status.
     *
     * @param active a task status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for the time of a non-repeating task.
     *
     * @return <code>time</code> if a task is non-repeating and <code>start</code> if is repeating
     */
    public LocalDateTime getTime() {
        return (isRepeated() ? start : time);
    }

    /**
     * Setter for the time of a non-repeating task.
     * If a task is repeating, then it should become non-repeating.
     *
     * @param time a notification time
     *
     * @throws IllegalArgumentException if <code>time</code> is negative
     */
    public void setTime(LocalDateTime time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("Time must not be null.");
        }
        if (isRepeated()) {
            this.repeated = false;
            this.start = null;
            this.end = null;
            this.interval = null;
        }
        this.time = time;
    }

    /**
     * Getter for the start time of a repeating task start time.
     *
     * @return <code>time</code> if a task is non-repeating and <code>start</code> if is repeating
     */
    public LocalDateTime getStartTime() {
        return (isRepeated() ? start : time);
    }

    /**
     * Getter for the end time of a repeating task.
     *
     * @return <code>time</code> if a task is non-repeating and <code>end</code> if is repeating
     */
    public LocalDateTime getEndTime() {
        return (isRepeated() ? end : time);
    }

    /**
     * Getter for the interval time of a repeating task.
     *
     * @return zero if a task is non-repeating and <code>interval</code> if is repeating
     */
    public int getRepeatInterval() {
        return (isRepeated() ? (int) interval.getSeconds() : 0);
    }

    /**
     * Setter for the time of a repeating task.
     * If a task is non-repeating, then it should become repeating.
     *
     * @param start the notification start time
     * @param end the notification end time
     * @param interval Time interval after which it is necessary to repeat task notification.
     *
     * @throws IllegalArgumentException if...
     * <ul>
     * <li>timestamps are <code>null</code>;</li>
     * <li><code>start</code> is greater than <code>end</code>;</li>
     * <li><code>interval</code> isn't positive.</li>
     * </ul>
     */
    public void setTime(LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Timestamps must not be null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The end of task must be greater than the start of task.");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero.");
        }
        if (!isRepeated()) {
            this.repeated = true;
            this.time = null;
        }
        this.start = start;
        this.end = end;
        this.interval = Duration.ofSeconds(interval);
    }

    /**
     * The method for checking the repeatability of a task.
     *
     * @return the information about repeatability of a task
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * The method which returns the time of the next task execution.
     *
     * @param current the specified time
     *
     * @return Return the time of the next task execution after the specified time,
     * if after the specified time a task wasn't executed, then the method returns <code>null</code>.
     *
     * @throws IllegalArgumentException if <code>current</code> is negative
     */
    public LocalDateTime nextTimeAfter(LocalDateTime current) throws IllegalArgumentException {
        if (current == null) {
            throw new IllegalArgumentException("Specified time must not be null.");
        }
        if (isActive()) {
            if (!isRepeated()) {
                return (current.isAfter(time) || current.isEqual(time) ? null : time);
            } else {
                if (current.isBefore(end)) {
                    for (LocalDateTime i = start; i.isBefore(end) || i.isEqual(end); i = i.plus(interval)) {
                        if (current.isBefore(i)) {
                            return i;
                        }
                    }
                }
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (isRepeated()) {
            return "Task \"" + title + "\": {start = " + start
                    + "; end = " + end + "; interval = " + interval.getSeconds()
                    + "; active -> " + active + "; repeated -> " + repeated + "}";
        } else {
            return "Task \"" + title + "\": {time = " + time
                    + "; active -> " + active + "; repeated -> " + repeated + "}";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(time, task.time) && Objects.equals(start, task.start) && Objects.equals(end, task.end)
                && Objects.equals(interval, task.interval) && active == task.active
                && repeated == task.repeated && Objects.equals(title, task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time, start, end, interval, active, repeated);
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(title);
        out.writeBoolean(active);
        if (interval == null) {
            out.writeLong(0L);
        } else {
            out.writeLong(interval.getSeconds());
        }
        if (this.isRepeated()) {
            out.writeByte(start.getSecond());
            out.writeByte(start.getMinute());
            out.writeByte(start.getHour());
            out.writeByte(start.getDayOfMonth());
            out.writeByte(start.getMonthValue());
            out.writeInt(start.getYear());

            out.writeByte(end.getSecond());
            out.writeByte(end.getMinute());
            out.writeByte(end.getHour());
            out.writeByte(end.getDayOfMonth());
            out.writeByte(end.getMonthValue());
            out.writeInt(end.getYear());
        } else {
            out.writeByte(time.getSecond());
            out.writeByte(time.getMinute());
            out.writeByte(time.getHour());
            out.writeByte(time.getDayOfMonth());
            out.writeByte(time.getMonthValue());
            out.writeInt(time.getYear());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        title = in.readUTF();
        active = in.readBoolean();
        long interval = in.readLong();
        if (interval != 0L) {
            byte[] startTimes = readExternalTimeOfBytes(in);
            int startYear = in.readInt();
            byte[] endTimes = readExternalTimeOfBytes(in);
            int endYear = in.readInt();
            start = LocalDateTime.of(startYear, startTimes[4], startTimes[3], startTimes[2], startTimes[1], startTimes[0]);
            end = LocalDateTime.of(endYear, endTimes[4], endTimes[3], endTimes[2], endTimes[1], endTimes[0]);
            this.interval = Duration.ofSeconds(interval);
            repeated = true;
        } else {
            byte[] times = readExternalTimeOfBytes(in);
            int timeYear = in.readInt();
            time = LocalDateTime.of(timeYear, times[4], times[3], times[2], times[1], times[0]);
            repeated = false;
        }
    }

    /**
     * Method that returns an array of read elements of type <code>bytes</code>.
     *
     * @param in the input stream
     * @return the array of <code>bytes</code> elements
     * @throws IOException if was failed or interrupted I/O operations.
     */
    private byte[] readExternalTimeOfBytes(ObjectInput in) throws IOException {
        byte[] times = new byte[5];
        for (int i = 0; i < times.length; ++i) {
            times[i] = in.readByte();
        }
        return times;
    }
}
