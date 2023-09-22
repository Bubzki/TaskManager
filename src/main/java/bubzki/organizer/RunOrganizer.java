package bubzki.organizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;
import bubzki.organizer.controller.Controller;

import java.util.Objects;

public class RunOrganizer extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		PropertyConfigurator.configure(RunOrganizer.class.getResource("log4j.properties"));
		FXMLLoader loader = new FXMLLoader(RunOrganizer.class.getResource("view.fxml"));
		Scene scene = new Scene(loader.load());
		Controller controller = loader.getController();
		controller.logger.debug("App is running.");
		stage.getIcons().add(new Image(Objects.requireNonNull(RunOrganizer.class.getResource("OrganizerIcon.png")).toExternalForm()));
		stage.setResizable(false);
		stage.setTitle("Personal Organizer");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(windowEvent -> {
			controller.writingData();
			controller.logger.debug("App is closed.");
		});
	}

	public static void main(String[] args) {
		launch();
	}
}
