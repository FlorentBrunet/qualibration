package eu.brnt.qualibration;

import eu.brnt.qualibration.view.ViewFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher extends Application {

    public static void main(String[] args) {
        log.info("Launcher starting...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showMainWindow();
    }

    @Override
    public void stop() throws Exception {
        log.info("Launcher stopping...");
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}
