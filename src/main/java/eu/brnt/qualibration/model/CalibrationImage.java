package eu.brnt.qualibration.model;

import boofcv.alg.geo.calibration.CalibrationObservation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;

@Data
@Slf4j
public class CalibrationImage {

    private final File file;

    private final BufferedImage image;

    private boolean fxImageLoaded = false;
    private WritableImage fxImage;

    private boolean grayF32Loaded = false;
    private GrayF32 grayF32;

    private Boolean hasAutoDetectedPoints = null;
    private CalibrationObservation detectedPoints;

    public synchronized BufferedImage getImage() {
        return image;
    }

    public synchronized WritableImage getFxImage() {
        if (!fxImageLoaded) {
            fxImageLoaded = true;
            BufferedImage image = getImage();
            if (image != null) {
                fxImage = SwingFXUtils.toFXImage(image, null);
            }
        }
        return fxImage;
    }

    public synchronized GrayF32 getGrayF32() {
        if (!grayF32Loaded) {
            grayF32Loaded = true;
            BufferedImage image = getImage();
            if (image != null) {
                grayF32 = ConvertBufferedImage.convertFrom(getImage(), (GrayF32) null);
            }
        }
        return grayF32;
    }

    public void setHasAutoDetectedPoints(boolean hasAutoDetectedPoints) {
        this.hasAutoDetectedPoints = hasAutoDetectedPoints;
    }

    public Boolean getHasAutoDetectedPoints() {
        return this.hasAutoDetectedPoints;
    }

    public void setDetectedPoints(CalibrationObservation detectedPoints) {
        this.detectedPoints = detectedPoints;
    }

    public CalibrationObservation getDetectedPoints() {
        return this.detectedPoints;
    }
}
