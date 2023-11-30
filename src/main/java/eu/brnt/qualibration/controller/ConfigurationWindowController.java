package eu.brnt.qualibration.controller;

import eu.brnt.qualibration.model.ValueHolder;
import eu.brnt.qualibration.model.configuration.*;
import eu.brnt.qualibration.view.ViewFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ConfigurationWindowController extends BaseController implements Initializable {

    @FXML private Node rootNode;

    @FXML private ComboBox<ObservationPointDisplayType> obsPointDispModeComboBox;
    @FXML private CheckBox obsPointShowIndexesCheckBox;
    @FXML private Spinner<Integer> obsPointFontSizeSpinner;
    @FXML private Spinner<Double> obsPointRadiusSpinner;
    @FXML private Spinner<Double> obsPointLineWidthSpinner;

    @FXML private Spinner<Integer> undistGridHorizSpinner;
    @FXML private Spinner<Integer> undistGridVertSpinner;

    private final Configuration configuration;
    private final ValueHolder<Configuration> result;

    public ConfigurationWindowController(ViewFactory viewFactory, String fxmlName, Configuration configuration, ValueHolder<Configuration> result) {
        super(viewFactory, fxmlName);
        this.configuration = configuration;
        this.result = result;
        this.result.setValue(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration - Configuration"));

        ObservationPointConfig obsPoint = configuration.getObservationPointConfig();
        obsPointDispModeComboBox.setItems(FXCollections.observableList(List.of(ObservationPointDisplayType.values())));
        obsPointDispModeComboBox.getSelectionModel().select(obsPoint.getObservationPointDisplayType());

        obsPointShowIndexesCheckBox.setSelected(obsPoint.isShowPointIndexes());

        obsPointFontSizeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 24, obsPoint.getFontSize())
        );

        ObservationPointRainbowConfig rainbow = obsPoint.getObservationPointRainbowConfig();
        obsPointRadiusSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 10, rainbow.getObservationPointRadius(), 0.5)
        );

        obsPointLineWidthSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 10, rainbow.getObservationPointLineWidth(), 0.5)
        );

        UndistGridConfig undistGrid = configuration.getUndistGridConfig();

        undistGridHorizSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 256, undistGrid.getHorizontal())
        );
        undistGridVertSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 256, undistGrid.getVertical())
        );
    }

    @FXML
    void onCancelClicked() {
        this.result.setValue(null);
        viewFactory.closeStage((Stage) rootNode.getScene().getWindow());
    }

    @FXML
    void onOkClicked() {
        Configuration config = new Configuration();

        ObservationPointConfig obsPoint = config.getObservationPointConfig();
        obsPoint.setObservationPointDisplayType(obsPointDispModeComboBox.getSelectionModel().getSelectedItem());
        obsPoint.setFontSize(obsPointFontSizeSpinner.getValue());
        obsPoint.setShowPointIndexes(obsPointShowIndexesCheckBox.isSelected());

        ObservationPointRainbowConfig rainbow = obsPoint.getObservationPointRainbowConfig();
        rainbow.setObservationPointRadius(obsPointRadiusSpinner.getValue());
        rainbow.setObservationPointLineWidth(obsPointLineWidthSpinner.getValue());

        UndistGridConfig undistGrid = config.getUndistGridConfig();
        undistGrid.setHorizontal(undistGridHorizSpinner.getValue());
        undistGrid.setVertical(undistGridVertSpinner.getValue());

        this.result.setValue(config);

        viewFactory.closeStage((Stage) rootNode.getScene().getWindow());
    }
}
