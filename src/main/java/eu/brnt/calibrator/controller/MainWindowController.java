package eu.brnt.calibrator.controller;

import eu.brnt.calibrator.model.CalibrationImage;
import eu.brnt.calibrator.view.ViewFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MainWindowController extends BaseController implements Initializable {

    @FXML private SplitPane rootPane;

    @FXML private ListView<CalibrationImage> imagesListView;

    @FXML private Spinner<Integer> rowsCountSpinner;
    @FXML private Spinner<Integer> columnsCountSpinner;
    @FXML private TextField squareSize;

    @FXML private Pane pane;
    @FXML private Canvas canvas;

    private final List<CalibrationImage> calibrationImages = new LinkedList<>();

    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> ((Stage) rootPane.getScene().getWindow()).setTitle("Calibrator"));

        imagesListView.setCellFactory(p -> new CalibrationImageCell());
        imagesListView.setItems(FXCollections.observableList(calibrationImages));
        imagesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.info("Selected image: {}", newValue);
            redraw();
        });

        rowsCountSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5)
        );
        columnsCountSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 7)
        );

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            canvas.setWidth(pane.getWidth());
            canvas.setHeight(pane.getHeight());
            redraw();
        };

        pane.widthProperty().addListener(resizeListener);
        pane.heightProperty().addListener(resizeListener);
    }

    @FXML
    void onAddImageClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add image(s)");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.tif"));
        List<File> files = chooser.showOpenMultipleDialog(rootPane.getScene().getWindow());
        log.info("Selected files: {}", files);

        if (files != null && !files.isEmpty()) {
            Set<String> loadedFiles = calibrationImages.stream().map(ci -> ci.getFile().getAbsolutePath()).collect(Collectors.toSet());
            List<CalibrationImage> newImages = new ArrayList<>(calibrationImages.size() + files.size());
            newImages.addAll(calibrationImages);
            for (File file : files) {
                if (!loadedFiles.contains(file.getAbsolutePath())) {
                    newImages.add(new CalibrationImage(file));
                }
            }
            newImages.sort(Comparator.comparing(o -> o.getFile().getName()));
            calibrationImages.clear();
            calibrationImages.addAll(newImages);
            imagesListView.setItems(FXCollections.observableList(calibrationImages));
            redraw();
        }
    }

    @FXML
    void onRemoveImageClicked() {
        CalibrationImage selectedImage = imagesListView.getSelectionModel().getSelectedItem();
        if (selectedImage != null) {
            List<CalibrationImage> newImages = new ArrayList<>(calibrationImages.size());
            for (CalibrationImage image : calibrationImages) {
                if (!image.getFile().getAbsolutePath().equals(selectedImage.getFile().getAbsolutePath())) {
                    newImages.add(image);
                }
            }
            calibrationImages.clear();
            calibrationImages.addAll(newImages);
            imagesListView.setItems(FXCollections.observableList(calibrationImages));
            redraw();
        }
    }

    @FXML
    void onZoomInClicked() {
    }

    @FXML
    void onZoomOutClicked() {
    }

    private void redraw() {
        CalibrationImage curCalibrationImage = imagesListView.getSelectionModel().getSelectedItem();

        final double width = canvas.getWidth();
        final double height = canvas.getHeight();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);

        if (curCalibrationImage == null) {
            drawCross(gc, width, height);
        } else {
            WritableImage curImage = curCalibrationImage.getFxImage();
            if (curImage == null) {
                gc.fillText("Not an image", 20, 20);
            } else {
                gc.drawImage(curImage, 0, 0);
            }
        }
    }

    private void drawCross(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.GRAY);

        gc.strokeLine(0.5, 0.5, width - 0.5, 0.5);
        gc.strokeLine(width - 0.5, 0.5, width - 0.5, height - 0.5);
        gc.strokeLine(width - 0.5, height - 0.5, 0.5, height - 0.5);
        gc.strokeLine(0.5, height - 0.5, 0.5, 0.5);

        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(width, 0, 0, height);
    }
}
