package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Setter;

/**
 * Controller for completing a download. 
 * @author Ben Shabowski
 * @since 1.0
 */
@Setter
public class CompletedController extends Pane {
	
	// Stage that this pane lives on
	private Stage stage;
	
	/**
	 * Exits the program
	 * @param event
	 */
    @FXML
    void exit(ActionEvent event) {
    	stage.hide();
    }

}
