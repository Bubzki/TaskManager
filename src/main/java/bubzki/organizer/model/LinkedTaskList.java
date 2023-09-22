package bubzki.organizer.model;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LinkedTaskList extends AbstractTaskList {
    private int size;
    private Node first;
    private Node last;

    /**
     * The class for creating nodes that are components of a linked list.
     * A node has an object stored in a list and links to the next and previous node.
     */
    private static class Node {
        Task item;
        Node next;
        Node previous;

        /**
         * Constructor that creates a <code>node</code> with links
         * to previous & next <code>node</code>,
         * and an element of {@link Task} type.
         *
         * @param item the element of {@link Task} type
         * @param previous the link to previous <code>node</code>
         * @param next the link to next <code>node</code>
         */
        Node(Task item, Node previous, Node next) {
            this.item = item;
            this.previous = previous;
            this.next = next;
        }

        /**
         * The method that clones all nodes at once.
         *
         * @return the first cloning node
         *
         * @throws CloneNotSupportedException if class Task and deeper classes...
         * <ul>
         * <li>don't support cloning;</li>
         * <li>don't implement {@link Cloneable}.</li>
         * </ul>
         */
        private Node nodeClone() throws CloneNotSupportedException {
            Node clone = new Node(item.clone(), null, null);
            for (Node tempNext = next, temp = clone; tempNext != null; tempNext = tempNext.next, temp = temp.next) {
                tempNext = new Node(tempNext.item.clone(), temp, tempNext.next);
                temp.next = tempNext;
            }
            return clone;
        }

        /**
         * The method that returns last node.
         *
         * @return the last cloning node
         */
        private Node lastNodeClone() {
            Node clone = this;
            for (Node temp = next; temp != null; temp = temp.next) {
                if (temp.next == null) {
                    clone = temp;
                    break;
                }
            }
            return clone;
        }
    }

    /**
     * The method searches for the node with the specified index.
     *
     * @param index the specified task index
     *
     * @return a node with the specified task index
     */
    private Node getNode(int index) {
        Node temp;
        if (index + 1 <= (Math.round((float)size / 2.))) {
            temp = first;
            for (int i = 0; i < index; ++i) {
                temp = temp.next;
            }
        } else {
            temp = last;
            for (int i = size - 1; i > index; --i) {
                temp = temp.previous;
            }
        }
        return temp;
    }

    /**
     * The method that creates a non-null node with the specified task.
     *
     * @param item task that need add to node
     */
    private void createNode(Task item) {
        Node last = this.last;
        Node newNode = new Node(item, last, null);
        this.last = newNode;
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        size++;
    }

    /**
     * The method that deletes a non-null node with the specified task.
     *
     * @param node with the specified task that should be deleted
     */
    private void deleteNode(Node node) {
        if (size > 1) {
            if (node.previous == null) {
                first = node.next;
                node.next.previous = null;
                node.next = null;
            } else if (node.next == null) {
                last = node.previous;
                node.previous.next = null;
                node.previous = null;
            } else {
                node.previous.next = node.next;
                node.next.previous = node.previous;
                node.next = null;
                node.previous = null;
            }
        } else {
            first = null;
            last = null;
        }
        node.item = null;
        size--;
    }

    /**
     * The method that add a task to the list.
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
        createNode(task);
    }

    /**
     * The method that removes a task from the list.
     * If there were several such tasks in the list,
     * it will delete such task, which was added the first.
     *
     * @param task a specified task that needs to remove
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
            for (Node temp = first; temp != null; temp = temp.next) {
                if (task.equals(temp.item)) {
                    deleteNode(temp);
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
        return getNode(index).item;
    }

    @Override
    protected LinkedTaskList getTaskList() {
        return new LinkedTaskList();
    }

    /**
     * Returns an iterator over elements of type {@code Task}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return new Iterator<Task>() {
            private Node currentElement;
            private Node nextElement = first;

            @Override
            public boolean hasNext() {
                return nextElement != null;
            }

            @Override
            public Task next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("Iteration has no more elements.");
                }
                currentElement = nextElement;
                nextElement = nextElement.next;
                return currentElement.item;
            }

            @Override
            public void remove() throws IllegalStateException {
                if (currentElement == null) {
                    throw new IllegalStateException();
                }
                LinkedTaskList.this.remove(currentElement.item);
                currentElement = null;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder tempString = new StringBuilder("LinkedTaskList(" + size() + "): [");
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
        LinkedTaskList tempLinked = (LinkedTaskList) o;
        if (size() != tempLinked.size()) {
            return false;
        }
        for (Iterator<Task> i1 = this.iterator(), i2 = tempLinked.iterator(); i1.hasNext() && i2.hasNext();) {
            if (!i1.next().equals(i2.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.first == null) {
            return 0;
        }
        int result = Objects.hash(size);
        int tempResult = 1;
        for (Task temp : this) {
            tempResult = 31 * tempResult + (temp == null ? 0 : temp.hashCode());
        }
        result = 31 * result + tempResult;
        return result;
    }

    @Override
    public LinkedTaskList clone() throws CloneNotSupportedException {
        LinkedTaskList clone = (LinkedTaskList) super.clone();
        if (first != null) {
            clone.first = first.nodeClone();
            clone.last = clone.first.lastNodeClone();
        }
        return clone;
    }

    @Override
    public Spliterator<Task> spliterator() {
        return Spliterators.spliterator(iterator(), size, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @Override
    public Stream<Task> getStream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
