package bubzki.organizer.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

public class ArrayTaskList extends AbstractTaskList {
    private final static int DEFAULT_CAPACITY = 10;
    private final static float RATIO = 1.5f;
    private int size;
    private Task[] tasks = new Task[DEFAULT_CAPACITY];

    /**
     * The method that add a task to the list and
     * increases array capacity to 1.5 times if size is greater than capacity.
     *
     * @param task a specified task that needs to add
     *
     * @throws NullPointerException if task is null pointer
     */
    @Override
    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Cannot add null pointer.");
        }
        if (size == tasks.length) {
            Task[] tempTasks = new Task[Math.round(size * RATIO)];
            System.arraycopy(tasks, 0, tempTasks, 0, size);
            tasks = tempTasks;
        }
        tasks[size] = task;
        size++;
    }

    /**
     * The method that removes a task from the list.
     * If there were several such tasks in the list,
     * it will delete such task, which was added the first.
     *
     * @param task a specified task that needs to remove
     *
     * @return "true" if the task on the list, "false" if the task not on the list
     *
     * @throws NullPointerException if task is null pointer
     */
    @Override
    public boolean remove(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Cannot remove null pointer.");
        }
        if (size != 0) {
            for (int i = 0; i < size; ++i) {
                if (task.equals(tasks[i])) {
                    if (i != size - 1) {
                        System.arraycopy(tasks, i + 1, tasks, i, size - i - 1);
                    }
                    tasks[size - 1] = null;
                    size--;
                    trimCapacity();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The method that returns the number of tasks in the list.
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * The method that decreases array capacity by one third,
     * if the capacity is equal or more than 1.5 times the number of elements
     */
    private void trimCapacity() {
        if (size != 0 && ((float)tasks.length / (float)size) >= RATIO
                && tasks.length > DEFAULT_CAPACITY) {
            Task[] tempTasks;
            if ((tasks.length / RATIO) <= DEFAULT_CAPACITY) {
                tempTasks = new Task[DEFAULT_CAPACITY];
            } else {
                tempTasks = new Task[Math.round(tasks.length / RATIO)];
            }
            System.arraycopy(tasks, 0, tempTasks, 0, size);
            tasks = tempTasks;
        }
    }

    /**
     * The method that returns the task that is at the specified location in list,
     * the first task has an index of 0.
     *
     * @param index the specified task index
     *
     * @return a task with the specified index
     *
     * @throws IndexOutOfBoundsException if index is out of the list range.
     */
    @Override
    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index >= size) {
            throw new IndexOutOfBoundsException("The index is out of range.");
        }
        return tasks[index];
    }

    @Override
    protected ArrayTaskList getTaskList() {
        return new ArrayTaskList();
    }

    /**
     * Returns an iterator over elements of type {@code Task}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return new Iterator<Task>() {
            private int size = size();
            private int currentElement = -1;
            private int nextElement;

            @Override
            public boolean hasNext() {
                return size > nextElement && tasks[nextElement] != null;
            }

            @Override
            public Task next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("Iteration has no more elements.");
                }
                nextElement++;
                currentElement++;
                return tasks[currentElement];
            }

            @Override
            public void remove() throws IllegalStateException {
                if (currentElement == -1) {
                    throw new IllegalStateException();
                }
                ArrayTaskList.this.remove(tasks[currentElement]);
                currentElement--;
                nextElement--;
                size--;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder tempString = new StringBuilder("ArrayTaskList(" + size() + "): [");
        if (size() > 0) {
            for (Task temp : this) {
                tempString.append(temp.toString()).append(";").append("\n\t\t\t\t\t");
            }
            tempString.delete(tempString.length() - 7, tempString.length());
        }
        return tempString.append("]").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArrayTaskList tempArray = (ArrayTaskList) o;
        return size == tempArray.size && Arrays.equals(tasks, tempArray.tasks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(tasks);
        return result;
    }

    @Override
    public ArrayTaskList clone() throws CloneNotSupportedException {
        ArrayTaskList clone = (ArrayTaskList) super.clone();
        clone.tasks = tasks.clone();
        for (int i = 0; i < clone.size(); ++i) {
            clone.tasks[i] = clone.tasks[i].clone();
        }
        return clone;
    }

    @Override
    public Stream<Task> getStream() {
        return Arrays.stream(this.tasks).limit(size);
    }
}
