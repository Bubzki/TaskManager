package bubzki.organizer.model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable<Task>, Cloneable, Externalizable {
    public abstract void add(Task task);

    public abstract boolean remove(Task task);

    public abstract int size();

    public abstract Task getTask(int index);

    /**
     * The method that finds a subset of tasks
     * that are scheduled to run at least once after time <code>from</code> and no later than <code>to</code>.
     *
     * @param from the start time of the period
     * @param to the end time of the period
     *
     * @return the subset of tasks that fit the specified time period
     *
     * @throws IllegalArgumentException if...
     * <ul>
     * <li>timestamps are null;</li>
     * <li><code>from</code> is greater than <code>to</code>.</li>
     * </ul>
     */
    public final AbstractTaskList incoming(LocalDateTime from, LocalDateTime to) throws IllegalArgumentException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Time \"to\" must be greater than \"from\".");
        }
        AbstractTaskList tempTaskList = getTaskList();
        getStream().filter((e) -> e.nextTimeAfter(from) != null && (e.nextTimeAfter(from).isBefore(to) || e.nextTimeAfter(from).isEqual(to)))
                .forEach(tempTaskList::add);
        return tempTaskList;
    }

    /**
     * The method that creates an object of the required type.
     *
     * @return the specified object
     */
    protected abstract AbstractTaskList getTaskList();

    @Override
    public AbstractTaskList clone() throws CloneNotSupportedException {
        return (AbstractTaskList) super.clone();
    }

    /**
     * The method that creates a stream of tasks from some list.
     *
     * @return the stream of tasks
     */
    public abstract Stream<Task> getStream();

    @Override
    public abstract Iterator<Task> iterator();

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size());
        for (Task temp : this) {
            temp.writeExternal(out);
        }
    }

   @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            Task temp = new Task();
            temp.readExternal(in);
            add(temp);
        }
    }
}
