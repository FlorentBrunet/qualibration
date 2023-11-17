package eu.brnt.qualibration.controller;

import boofcv.abst.fiducial.calib.ConfigGridDimen;
import boofcv.abst.geo.calibration.CalibrateMonoPlanar;
import boofcv.abst.geo.calibration.DetectSingleFiducialCalibration;
import boofcv.alg.geo.calibration.CalibrationObservation;
import boofcv.factory.fiducial.FactoryFiducialCalibration;
import boofcv.io.calibration.CalibrationIO;
import boofcv.struct.calib.CameraModel;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.geo.PointIndex2D_F64;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.brnt.qualibration.model.CalibrationImage;
import eu.brnt.qualibration.model.CameraDefinition;
import eu.brnt.qualibration.model.Project;
import eu.brnt.qualibration.view.ViewFactory;
import georegression.struct.point.Point2D_F64;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class MainWindowController extends BaseController implements Initializable {

    @FXML private Node rootNode;

    @FXML private ListView<CalibrationImage> imagesListView;

    @FXML private Spinner<Integer> rowsCountSpinner;
    @FXML private Spinner<Integer> columnsCountSpinner;
    @FXML private TextField squareSizeEditText;

    @FXML private CheckBox zeroSkewCheckBox;
    @FXML private Spinner<Integer> radialParamsSpinner;
    @FXML private CheckBox includeTangentialCheckBox;

    @FXML private Pane pane;
    @FXML private Canvas canvas;

    @FXML private TextArea resultTextArea;

    @FXML private ComboBox<String> formatComboBox;

    private Project project;
    private final UiState uiState = new UiState();

    private enum ImageSortOrder {
        FILE_NAME
    }

    private class UiState {
        private ImageSortOrder imageSortOrder = ImageSortOrder.FILE_NAME;
        private double zoomFactor = 1.0;

        public ImageSortOrder getImageSortOrder() {
            return imageSortOrder;
        }
    }

    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        project = null;

        Platform.runLater(() -> ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration"));

        imagesListView.setCellFactory(p -> new CalibrationImageCell());
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

        radialParamsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 8, 2)
        );

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            canvas.setWidth(pane.getWidth());
            canvas.setHeight(pane.getHeight());
            redraw();
        };

        pane.widthProperty().addListener(resizeListener);
        pane.heightProperty().addListener(resizeListener);

        formatComboBox.getItems().addAll("OpenCV", "BoofCV", "Internal");
        formatComboBox.getSelectionModel().select(0);
        formatComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateResult()
        );
    }

    @FXML
    void onOpenClicked() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open folder");
        File dir = chooser.showDialog(rootNode.getScene().getWindow());

        log.info("Selected dir: {}", dir);

        project = new Project(dir);

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        BufferedImage image = ImageIO.read(file);
                        if (image != null) {
                            log.info("Loaded image: {} ({}x{})", file.getAbsolutePath(), image.getWidth(), image.getHeight());
                            project.addCalibrationImage(file, image);
                        } else {
                            log.warn("Could not read file: " + file.getAbsolutePath());
                        }
                    } catch (Throwable t) {
                        log.warn("Could not read file: " + file.getAbsolutePath(), t);
                    }
                }
            }
        }

        reloadProject();
    }

    private void reloadProject() {
        if (project != null) {
            ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration - " + project.getRootDir().getAbsolutePath());

            Comparator<CalibrationImage> comparator = Comparator.comparing(o -> o.getFile().getName());
            List<CalibrationImage> sortedImages = project.getCalibrationImages().stream().sorted(comparator).collect(Collectors.toList());
            imagesListView.setItems(FXCollections.observableList(sortedImages));
        } else {
            ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration");
            imagesListView.getItems().clear();
        }
    }

    @FXML
    void onZoomInClicked() {
        uiState.zoomFactor += 0.1;
        redraw();
    }

    @FXML
    void onZoomOutClicked() {
        uiState.zoomFactor = Math.max(uiState.zoomFactor - 0.1, 0.1);
        redraw();
    }

    @FXML
    void onDetectCornersClicked() {
        if (project == null)
            return;

        Integer numRows = rowsCountSpinner.getValue();
        Integer numCols = columnsCountSpinner.getValue();

        int squareSize = Integer.parseInt(squareSizeEditText.getText());

        DetectSingleFiducialCalibration detector = FactoryFiducialCalibration.chessboardX(
                null,
                new ConfigGridDimen(numRows, numCols, squareSize)
        );
        project.setDetector(detector);

        for (CalibrationImage calibrationImage : project.getCalibrationImages()) {
            if (detector.process(calibrationImage.getGrayF32())) {
                CalibrationObservation obs = detector.getDetectedPoints().copy();
                log.info("Detected corners: {}", obs);
                calibrationImage.setHasAutoDetectedPoints(true);
                calibrationImage.setDetectedPoints(obs);
            } else {
                calibrationImage.setHasAutoDetectedPoints(false);
            }
        }

        imagesListView.refresh();
        redraw();
    }

    @FXML
    void onCalibrateClicked() {
        if (project == null)
            return;

        CalibrateMonoPlanar calibrator = new CalibrateMonoPlanar();

        calibrator.configurePinhole(
                /*assumeZeroSkew*/ zeroSkewCheckBox.isSelected(),
                /*numRadialParam*/ radialParamsSpinner.getValue(),
                /*includeTangential*/ includeTangentialCheckBox.isSelected()
        );

        boolean calibratorInitialized = false;
        for (CalibrationImage calibrationImage : project.getCalibrationImages()) {
            if (calibrationImage.getHasAutoDetectedPoints() == null || !calibrationImage.getHasAutoDetectedPoints())
                continue;

            if (!calibratorInitialized) {
                calibratorInitialized = true;

                calibrator.initialize(
                        calibrationImage.getImage().getWidth(),
                        calibrationImage.getImage().getHeight(),
                        List.of(project.getDetector().getLayout())
                );
            }

            calibrator.addImage(calibrationImage.getDetectedPoints().copy());
        }

        CameraModel intrinsic = calibrator.process();
        log.info("Intrinsic: {}", intrinsic);

        project.setCameraPinholeBrown((CameraPinholeBrown) intrinsic);

        updateResult();
    }

    private void updateResult() {
        if (project == null || project.getCameraPinholeBrown() == null)
            return;

        String selected = formatComboBox.getSelectionModel().getSelectedItem();

        StringWriter sw = new StringWriter();
        if ("OpenCV".equals(selected)) {
            CalibrationIO.saveOpencv(project.getCameraPinholeBrown(), sw);
        } else if ("BoofCV".equals(selected)) {
            CalibrationIO.save(project.getCameraPinholeBrown(), sw);
        } else {
            CameraDefinition def = project.toInternalCameraDefinition();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(def, sw);
        }
        resultTextArea.setText(sw.toString());
    }

    private void redraw() {
        CalibrationImage curCalibrationImage = imagesListView.getSelectionModel().getSelectedItem();

        final double width = canvas.getWidth();
        final double height = canvas.getHeight();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);

        if (curCalibrationImage == null) {
            drawEmptyCross(gc, width, height);
        } else {
            drawImage(gc, curCalibrationImage);
        }
    }

    private void drawEmptyCross(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.GRAY);

        gc.strokeLine(0.5, 0.5, width - 0.5, 0.5);
        gc.strokeLine(width - 0.5, 0.5, width - 0.5, height - 0.5);
        gc.strokeLine(width - 0.5, height - 0.5, 0.5, height - 0.5);
        gc.strokeLine(0.5, height - 0.5, 0.5, 0.5);

        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(width, 0, 0, height);
    }

    private void drawImage(GraphicsContext gc, CalibrationImage calibrationImage) {
        WritableImage curImage = calibrationImage.getFxImage();
        if (curImage == null) {
            gc.fillText("Not an image", 20, 20);
        } else {
            double zoom = uiState.zoomFactor;
            gc.drawImage(curImage, 0, 0, curImage.getWidth() * zoom, curImage.getHeight() * zoom);
            drawCalibrationPoints(gc, calibrationImage);
        }
    }

    private void drawCalibrationPoints(GraphicsContext gc, CalibrationImage calibrationImage) {
        if (calibrationImage == null || calibrationImage.getDetectedPoints() == null)
            return;

        CalibrationObservation observations = calibrationImage.getDetectedPoints();

        for (PointIndex2D_F64 indexedPoint : observations.getPoints()) {
            Point2D_F64 point = indexedPoint.getP();

            double x = point.getX();
            double y = point.getY();

            double zx = uiState.zoomFactor * x;
            double zy = uiState.zoomFactor * y;

            gc.setStroke(Color.RED);

            gc.strokeLine(zx, zy - 2, zx, zy + 2);
            gc.strokeLine(zx - 2, zy, zx + 2, zy);

            int index = indexedPoint.getIndex();
            gc.setFill(Color.RED);
            gc.fillText(String.valueOf(index), zx + 2, zy + 2);
        }
    }
}
