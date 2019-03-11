package pdfsplitter;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SplitWindowController {

    public Label SplitWindowLabel;

    public boolean leftToBeFirst=true, quit = false;

    public void LeftToFirstRadioBtnAction(ActionEvent actionEvent) {

        if (!leftToBeFirst) leftToBeFirst=true;
    }

    public void RightToFirstRadioBtnAction(ActionEvent actionEvent) {

        if (leftToBeFirst) leftToBeFirst=false;
    }

    public void SplitBtnAction(ActionEvent actionEvent) {

        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.close();

    }

    public void CancelBtnAction(ActionEvent actionEvent) {

        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.close();
         quit =  true;

    }
}
