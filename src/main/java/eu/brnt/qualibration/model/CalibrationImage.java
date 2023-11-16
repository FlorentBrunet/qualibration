package eu.brnt.qualibration.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Data
@Slf4j
public class CalibrationImage {

    private final File file;

    private boolean imageLoaded = false;
    private BufferedImage image;

    private boolean fxImageLoaded = false;
    private WritableImage fxImage;

    public synchronized BufferedImage getImage() {
        if (!imageLoaded) {
            imageLoaded = true;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                log.error("Could not load image '{}': {}", file.getAbsolutePath(), e.getMessage(), e);
            }
        }
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
}
