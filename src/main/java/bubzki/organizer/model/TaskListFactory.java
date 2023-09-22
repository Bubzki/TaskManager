package bubzki.organizer.model;

public class TaskListFactory {
    /**
     * The method that creates an object of this type that was passed in parameters.
     *
     * @param type object type that need to create
     *
     * @return the specified object
     *
     * @throws IllegalArgumentException if specified type of object doesn't exist
     */
    public static AbstractTaskList createTaskList(ListTypes.types type) throws IllegalArgumentException {
        switch (type) {
            case ARRAY:
                return new ArrayTaskList();
            case LINKED:
                return new LinkedTaskList();
            default:
                throw new IllegalArgumentException("This type doesn't exist.");
        }
    }
}
