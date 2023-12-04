package eu.brnt.qualibration.controller;

import boofcv.abst.geo.calibration.ImageResults;
import eu.brnt.qualibration.model.CalibrationImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Locale;

@Slf4j
public class ImageCellForError extends ListCell<CalibrationImageForError> {

    @FXML private Node rootNode;
    @FXML private Label nameLabel;
    @FXML private Label markerLabel;
    @FXML private Label meanErrorLabel;
    @FXML private Label maxErrorLabel;
    @FXML private Label biasLabel;

    private FXMLLoader loader;

    @Override
    protected void updateItem(CalibrationImageForError item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/eu/brnt/qualibration/view/ImageCellForError.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

            CalibrationImage image = item.getCalibrationImage();
            nameLabel.setText(image.getFile().getName());

            ImageResults errors = image.getErrors();
            if (errors != null) {
                String marker = switch (item.getMarker()) {
                    case 'x' -> "×";
                    case '+' -> "+";
                    case 'o' -> "o";
                    default -> "";
                };
                markerLabel.setText(marker);
                markerLabel.setTextFill(item.getColor());
                meanErrorLabel.setText(String.format(Locale.ENGLISH, "mean = %.3f", errors.getMeanError()));
                maxErrorLabel.setText(String.format(Locale.ENGLISH, "max = %.3f", errors.getMaxError()));
                biasLabel.setText(String.format(Locale.ENGLISH, "bias = (%.3f, %.3f)", errors.getBiasX(), errors.getBiasY()));
            } else {
                markerLabel.setText(null);
                meanErrorLabel.setText("mean = --");
                maxErrorLabel.setText("max = --");
                biasLabel.setText("bias = --");
            }

            setText(null);
            setGraphic(rootNode);
        }
    }
}
