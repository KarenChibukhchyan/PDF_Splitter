package pdfsplitter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;


public class DialogWindowController {

    @FXML
    public Label MessageTextLabel;

    @FXML
    public Label TittleLabel;

    public Button DialogWindowBtn;


    public void DialogBtnPressed(ActionEvent actionEvent) {


        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.close();


    }
}