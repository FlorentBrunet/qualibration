package eu.brnt.qualibration.controller;

import eu.brnt.qualibration.view.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationWindowController extends BaseController implements Initializable {

    @FXML private Node rootNode;

    public ConfigurationWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration - Configuration"));
    }
}
