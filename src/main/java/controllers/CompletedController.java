package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Setter;

@Setter
public class CompletedController extends Pane {
	
	private Stage stage;
	
    @FXML
    void exit(ActionEvent event) {
    	stage.hide();
    }

}
