package pdfsplitter;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OverwritingWindowController {
    public Label OverwriteWindLabel;

    public boolean contin;

    public void OverwriteOKBtnAction(ActionEvent actionEvent) {
        contin=true;
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.close();
    }

    public void OverwriteCancelBtnAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.close();
        contin = false;
    }
}
