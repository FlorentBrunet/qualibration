package eu.brnt.qualibration.view;

import eu.brnt.qualibration.controller.BaseController;
import eu.brnt.qualibration.controller.ConfigurationWindowController;
import eu.brnt.qualibration.controller.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ViewFactory {

    private final List<Stage> activeStages = new LinkedList<>();

    public void showMainWindow() {
        BaseController controller = new MainWindowController(this, "MainWindow.fxml");
        initializeStage(controller);
    }

    public void showConfigurationWindow(Window owner) {
        BaseController controller = new ConfigurationWindowController(this, "ConfigurationWindow.fxml");
        initializeStage(controller, Modality.WINDOW_MODAL, owner);
    }

    private void initializeStage(BaseController controller) {
        initializeStage(controller, null, null, null, null);
    }

    private void initializeStage(BaseController controller, Modality modality, Window ownerWindow) {
        initializeStage(controller, modality, ownerWindow, null, null);
    }

    private void initializeStage(BaseController controller, Modality modality, Window ownerWindow, Double stageX, Double stageY) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(controller.getFxmlName()));
        loader.setController(controller);
        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        activeStages.add(stage);

        if (stageX != null)
            stage.setX(stageX);
        if (stageY != null)
            stage.setY(stageY);

        if (modality != null) {
            stage.initModality(modality);
            stage.initOwner(ownerWindow);
            stage.setScene(scene);
            stage.showAndWait();
            activeStages.remove(stage);
        } else {
            stage.setScene(scene);
            stage.show();
        }
    }

    public void closeStage(Stage stage) {
        activeStages.remove(stage);
        stage.close();
    }
}
