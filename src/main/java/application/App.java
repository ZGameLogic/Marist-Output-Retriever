package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Marist output catcher connects to the Marist file server,
 * downloads selected jobs, and formats them for submission
 * @author Ben Shabowski
 * @since 1.0
 *
 */
public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException  {
		// Main pane for JavaFX application
		AnchorPane mainPane = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/MaristGUI.fxml"));
		
		// Main scene for JavaFX application
		Scene mainScene = new Scene(mainPane);
		
		// Set the style sheet programmatically. This is done in the FXML as well, but this is here to ensure it works 
		mainScene.getStylesheets().add(getClass().getClassLoader().getResource("css/NIU.css").toString());
		
		primaryStage.setScene(mainScene);
		primaryStage.setTitle("Retrieve Jobs");
		primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/MaristIco.png").toString()));
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
