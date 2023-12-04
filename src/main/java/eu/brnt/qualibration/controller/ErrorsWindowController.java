package eu.brnt.qualibration.controller;

import boofcv.abst.geo.calibration.ImageResults;
import eu.brnt.qualibration.component.Drawer;
import eu.brnt.qualibration.component.ScaledCanvas;
import eu.brnt.qualibration.model.CalibrationImage;
import eu.brnt.qualibration.model.Project;
import eu.brnt.qualibration.model.configuration.Configuration;
import eu.brnt.qualibration.util.ColorUtil;
import eu.brnt.qualibration.util.MathEx;
import eu.brnt.qualibration.view.ViewFactory;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ErrorsWindowController extends BaseController implements Initializable {

    @FXML private SplitPane splitPane;
    @FXML private StackPane stackPane;

    @FXML private ListView<CalibrationImageForError> imageListView;

    @FXML private Label coordinatesLabel;

    private ScaledCanvas scaledCanvas;

    private final Configuration configuration;
    private final Project project;

    private final List<CalibrationImageForError> images;

    private final Double minErrX;
    private final Double minErrY;
    private final Double maxErrX;
    private final Double maxErrY;

    public ErrorsWindowController(ViewFactory viewFactory, String fxmlName, Configuration configuration, Project project) {
        super(viewFactory, fxmlName);
        this.configuration = configuration;
        this.project = project;

        this.images = new ArrayList<>(project.getCalibrationImages().size());

        Color[] palette = ColorUtil.getMatlabPalette();
        char[] markers = new char[]{'x', '+', 'o'};

        int counter = 0;
        Double xMin = null, yMin = null, xMax = null, yMax = null;
        for (CalibrationImage image : project.getCalibrationImages()) {
            ImageResults errors = image.getErrors();
            if (errors != null) {
                int nPoints = errors.pointError.length;
                for (int i = 0; i < nPoints; i++) {
                    double ex = errors.residuals[2 * i];
                    double ey = errors.residuals[2 * i + 1];
                    if (xMin == null || ex < xMin) xMin = ex;
                    if (yMin == null || ey < yMin) yMin = ey;
                    if (xMax == null || ex > xMax) xMax = ex;
                    if (yMax == null || ey > yMax) yMax = ey;
                }
                images.add(new CalibrationImageForError(image, palette[counter % palette.length], markers[counter % markers.length]));
                ++counter;
            } else {
                images.add(new CalibrationImageForError(image));
            }
        }
        if (xMin != null) {
            double x = Math.max(Math.abs(xMin), Math.abs(xMax));
            double mx = 0.1 * x;
            xMin = -x - mx;
            xMax = x + mx;

            double y = Math.max(Math.abs(yMin), Math.abs(yMax));
            double my = 0.1 * y;
            yMin = -y - my;
            yMax = y + my;
        }
        minErrX = xMin;
        minErrY = yMin;
        maxErrX = xMax;
        maxErrY = yMax;
    }

    private final InvalidationListener selectionChangedListener = obs -> {
        log.info("Selected image: {}", imageListView.getSelectionModel().getSelectedItems());
        scaledCanvas.redraw();
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ((Stage) splitPane.getScene().getWindow()).setTitle("Qualibration - Error Analysis"));

        scaledCanvas = new ScaledCanvas();
        double w = maxErrX - minErrX;
        double h = maxErrY - minErrY;
        double mx = 0.1 * w;
        double my = 0.1 * h;
        double m = Math.max(mx, my);
        scaledCanvas.fitRectangle(minErrX - m, maxErrY + m, maxErrX + m, minErrY - m);
        scaledCanvas.setOnRedrawListener(this::redraw);

        scaledCanvas.setOnMouseMovedListener((xPixel, yPixel, userX, userY) ->
                coordinatesLabel.setText(String.format(Locale.ENGLISH, "(%.2f, %.2f)", userX, userY)));

        scaledCanvas.setOnMouseClickedListener((xPixel, yPixel, userX, userY) -> {
            double radius = scaledCanvas.getDrawer().pixelLengthToUser(5);
            double bestDist = Double.MAX_VALUE;
            CalibrationImageForError bestImage = null;
            for (CalibrationImageForError image : images) {
                ImageResults errors = image.getCalibrationImage().getErrors();
                if (errors != null) {
                    int nPoints = errors.pointError.length;
                    for (int i = 0; i < nPoints; i++) {
                        double rx = errors.residuals[2 * i];
                        double ry = errors.residuals[2 * i + 1];
                        double dist = Math.sqrt(MathEx.sq(userX - rx) + MathEx.sq(userY - ry));
                        if (dist <= radius && dist < bestDist) {
                            bestDist = dist;
                            bestImage = image;
                            log.info("bestDist={} - image={}", bestDist, bestImage.getCalibrationImage().getFile().getName());
                        }
                    }
                }
            }
            if (bestImage != null) {
                imageListView.getSelectionModel().clearSelection();
                imageListView.getSelectionModel().select(bestImage);
                imageListView.scrollTo(bestImage);
            } else {
                imageListView.getSelectionModel().clearSelection();
                selectionChangedListener.invalidated(null);
            }
        });

        stackPane.getChildren().add(scaledCanvas);

        imageListView.setCellFactory(p -> new ImageCellForError());
        imageListView.setItems(FXCollections.observableList(images));
        imageListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imageListView.getSelectionModel().selectedItemProperty().addListener(selectionChangedListener);
    }

    @FXML
    void onClearSelectionClicked() {
        imageListView.getSelectionModel().clearSelection();
        selectionChangedListener.invalidated(null);
    }

    private void redraw(Drawer drawer) {
        long t0 = System.nanoTime();
        drawer.clearAll();

        if (minErrX == null) {
            drawEmptyCross(drawer);
        } else {
            Set<String> selectedNames = imageListView.getSelectionModel().getSelectedItems().stream().map(i -> i.getCalibrationImage().getFile().getName()).collect(Collectors.toSet());

            drawer.setStroke(Color.GRAY);
            drawer.strokeLineScaled(0, minErrY, 0, maxErrY);
            drawer.strokeLineScaled(minErrX, 0, maxErrX, 0);

            drawer.setStroke(Color.BLACK);
            drawer.strokeRectScaled(minErrX, minErrY, maxErrX, maxErrY);

            drawer.setTextBaseline(VPos.TOP);
            drawer.setTextAlign(TextAlignment.LEFT);
            drawer.fillText(String.format(Locale.ENGLISH, "%.2f", minErrX), minErrX, minErrY);

            drawer.setTextAlign(TextAlignment.RIGHT);
            drawer.fillText(String.format(Locale.ENGLISH, "%.2f", maxErrX), maxErrX, minErrY);

            double m = drawer.pixelLengthToUser(2);
            drawer.fillText(String.format(Locale.ENGLISH, "%.2f", maxErrY), minErrX - m, maxErrY);

            drawer.setTextBaseline(VPos.BOTTOM);
            drawer.fillText(String.format(Locale.ENGLISH, "%.2f", minErrY), minErrX - m, minErrY);

            drawer.setTextBaseline(VPos.TOP);
            drawer.setTextAlign(TextAlignment.CENTER);
            drawer.fillText("0", 0, minErrY);

            drawer.setTextBaseline(VPos.CENTER);
            drawer.setTextAlign(TextAlignment.RIGHT);
            drawer.fillText("0", minErrX - m, 0);

            for (CalibrationImageForError image : images) {
                ImageResults errors = image.getCalibrationImage().getErrors();
                if (errors != null) {
                    Color color;
                    double radius;
                    if (!selectedNames.isEmpty()) {
                        if (selectedNames.contains(image.getCalibrationImage().getFile().getName())) {
                            color = image.getColor();
                            radius = 3;
                        } else {
                            Color c = image.getColor();
                            color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.2);
                            radius = 2;
                        }
                    } else {
                        color = image.getColor();
                        radius = 2;
                    }

                    drawer.setStroke(color);

                    int nPoints = errors.pointError.length;
                    for (int i = 0; i < nPoints; i++) {
                        double ex = errors.residuals[2 * i];
                        double ey = errors.residuals[2 * i + 1];
                        drawer.fixedSizeMarker(ex, ey, radius, image.getMarker());
                    }
                }
            }
        }

        long nanos = System.nanoTime() - t0;
        log.info("redraw in {} ns ({} ms / FPS={})", nanos, nanos / 1.0e6, 1.0e9 / nanos);
    }

    private void drawEmptyCross(Drawer drawer) {
        drawer.setStroke(Color.RED);
        drawer.strokeLineScaled(0, 0, 1, 1);
        drawer.strokeLineScaled(0, 1, 1, 0);

        drawer.strokeLineScaled(0, 0, 1, 0);
        drawer.strokeLineScaled(1, 0, 1, 1);
        drawer.strokeLineScaled(1, 1, 0, 1);
        drawer.strokeLineScaled(0, 1, 0, 0);
    }
}
