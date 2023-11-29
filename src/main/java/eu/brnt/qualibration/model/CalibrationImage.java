package eu.brnt.qualibration.model;

import boofcv.alg.geo.calibration.CalibrationObservation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.image.GrayF32;
import eu.brnt.qualibration.util.UndistortUtil;
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

    private CameraPinholeBrown cpbForUndist;
    private UndistMargins marginsForUndist;

    private BufferedImage undistBufferedImage;

    private WritableImage undistFxImage;

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

    public BufferedImage getUndistBufferedImage(CameraPinholeBrown cpb, UndistMargins margins) {
        if (undistBufferedImage == null || !areCpbEquals(cpb, cpbForUndist) || !margins.equals(marginsForUndist)) {
            this.cpbForUndist = cpb;
            this.marginsForUndist = margins;
            this.undistBufferedImage = UndistortUtil.undistort(cpb, margins.getTop(), margins.getRight(), margins.getBottom(), margins.getLeft(), getImage());
        }
        return undistBufferedImage;
    }

    public WritableImage getUndistFxImage(CameraPinholeBrown cpb, UndistMargins margins) {
        if (undistFxImage == null || !areCpbEquals(cpb, cpbForUndist) || !margins.equals(marginsForUndist)) {
            BufferedImage undistBufferedImage = getUndistBufferedImage(cpb, margins);
            undistFxImage = SwingFXUtils.toFXImage(undistBufferedImage, null);
        }
        return undistFxImage;
    }


    private final static double TOL = 1.0e-16;

    private boolean areCpbEquals(CameraPinholeBrown cpb1, CameraPinholeBrown cpb2) {
        if (cpb1 == null || cpb2 == null) {
            return cpb1 == cpb2;
        }
        return eq(cpb1.fx, cpb2.fx) && eq(cpb1.fy, cpb2.fy)
                && eq(cpb1.skew, cpb2.skew)
                && eq(cpb1.cx, cpb2.cx) && eq(cpb1.cy, cpb2.cy)
                && eq(cpb1.width, cpb2.width) && eq(cpb1.height, cpb2.height)
                && eq(cpb1.t1, cpb2.t1) && eq(cpb1.t2, cpb2.t2)
                && eq(cpb1.radial, cpb2.radial);
    }

    private boolean eq(double d1, double d2) {
        return Math.abs(d1 - d2) < TOL;
    }

    private boolean eq(double[] d1, double[] d2) {
        if (d1 == null || d2 == null) {
            return d1 == d2;
        }
        if (d1.length != d2.length) {
            return false;
        }
        for (int i = 0; i < d1.length; i++) {
            if (!eq(d1[i], d2[i])) {
                return false;
            }
        }
        return true;
    }
}
