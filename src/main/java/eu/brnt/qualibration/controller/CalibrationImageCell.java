package eu.brnt.qualibration.controller;

import eu.brnt.qualibration.model.CalibrationImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CalibrationImageCell extends ListCell<CalibrationImage> {

    @FXML private Node rootNode;
    @FXML private Label nameLabel;
    @FXML private Label pathLabel;
    @FXML private Label pointsLabel;

    private FXMLLoader loader;

    @Override
    protected void updateItem(CalibrationImage item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/eu/brnt/qualibration/view/CalibrationImageCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

            nameLabel.setText(item.getFile().getName());
            pathLabel.setText(item.getFile().getAbsolutePath());

            Boolean hasAutoDetectedPoints = item.getHasAutoDetectedPoints();
            if (hasAutoDetectedPoints == null) {
                pointsLabel.setText("Points: not yet auto-detected");
                pointsLabel.setTextFill(Color.GRAY);
            } else {
                if (hasAutoDetectedPoints) {
                    pointsLabel.setText("Points: " + item.getDetectedPoints().size());
                    pointsLabel.setTextFill(Color.GREEN);
                } else {
                    pointsLabel.setText("Points: none found");
                    pointsLabel.setTextFill(Color.RED);
                }
            }

            setText(null);
            setGraphic(rootNode);
        }
    }
}
