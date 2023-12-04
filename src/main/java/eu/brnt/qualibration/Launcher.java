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

        // Project project = new Project(null);
        //
        // File dir = new File("/Users/florent/Downloads/calib_example/jpg");
        // File[] files = dir.listFiles();
        // Arrays.sort(files, Comparator.comparing(File::getName));
        // for (File file : files) {
        //     if (file.isFile()) {
        //         try {
        //             BufferedImage image = ImageIO.read(file);
        //             project.addCalibrationImage(file, image);
        //         } catch (Throwable ignored) {
        //         }
        //     }
        // }
        //
        // {
        //     CalibrationImage img = project.getCalibrationImages().get(0);
        //     ImageResults errors = new ImageResults(2);
        //     errors.meanError = 0.1;
        //     errors.maxError = 0.2;
        //     errors.biasX = 0.3;
        //     errors.biasY = 0.4;
        //     errors.pointError[0] = 1;
        //     errors.pointError[1] = 2;
        //     errors.residuals[0] = -0.1;
        //     errors.residuals[1] = 0.2;
        //     errors.residuals[2] = -0.3;
        //     errors.residuals[3] = 0.4;
        //     img.setErrors(errors);
        // }
        //
        // viewFactory.showErrorsWindow(null, project);
    }

    @Override
    public void stop() throws Exception {
        log.info("Launcher stopping...");
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}
