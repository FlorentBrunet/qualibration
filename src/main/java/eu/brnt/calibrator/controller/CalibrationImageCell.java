package eu.brnt.calibrator.controller;

import eu.brnt.calibrator.model.CalibrationImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CalibrationImageCell extends ListCell<CalibrationImage> {

    @FXML private Node rootNode;
    @FXML private Label nameLabel;
    @FXML private Label pathLabel;

    private FXMLLoader loader;

    @Override
    protected void updateItem(CalibrationImage item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/eu/brnt/calibrator/view/CalibrationImageCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

            nameLabel.setText(item.getFile().getName());
            pathLabel.setText(item.getFile().getAbsolutePath());

            setText(null);
            setGraphic(rootNode);
        }
    }
}
