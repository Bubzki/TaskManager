package bubzki.organizer.controller;

import bubzki.organizer.model.TaskIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The class that is responsible for loading data from a file.
 */
public class LoadController {
    private final Controller controller;
    private final static String PATH_TO_LIST = "data/tasks.bin";

    protected LoadController(Controller controller) {
        this.controller = controller;
    }

    /**
     * The method that reads data from file to the list.
     */
    protected void readingData() {
        Path path = Paths.get(PATH_TO_LIST);
        if (Files.notExists(path)) {
            try {
                if (Files.notExists(path.getParent())) {
                    Files.createDirectory(path.getParent());
                }
                Files.createFile(path);
            } catch (IOException e) {
                controller.logger.error("Reading data error.", e);
                controller.showError("Unsuccessful reading from file \"" + path + "\".", e);
            }
        } else {
            TaskIO.readBinary(controller.getTaskList(), path.toFile());
        }
    }

    /**
     * The method that writes data from list to the file.
     */
    protected void writingData() {
        Path path = Paths.get(PATH_TO_LIST);
        if (Files.notExists(path)) {
            try {
                if (Files.notExists(path.getParent())) {
                    Files.createDirectory(path.getParent());
                }
                Files.createFile(path);
            } catch (IOException e) {
                controller.logger.error("Writing data error.", e);
                controller.showError("Unsuccessful writing to file \"" + path + "\".", e);
            }
        }
        TaskIO.writeBinary(controller.getTaskList(), path.toFile());
    }

}
