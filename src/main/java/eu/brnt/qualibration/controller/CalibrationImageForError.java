package eu.brnt.qualibration.controller;

import eu.brnt.qualibration.model.CalibrationImage;
import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public class CalibrationImageForError {

    private final CalibrationImage calibrationImage;
    private final Color color;
    private final Character marker;

    public CalibrationImageForError(CalibrationImage calibrationImage, Color color, Character marker) {
        this.calibrationImage = calibrationImage;
        this.color = color;
        this.marker = marker;
    }

    public CalibrationImageForError(CalibrationImage calibrationImage) {
        this(calibrationImage, null, null);
    }
}
