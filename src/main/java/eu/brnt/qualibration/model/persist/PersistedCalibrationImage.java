package eu.brnt.qualibration.model.persist;

import boofcv.alg.geo.calibration.CalibrationObservation;
import eu.brnt.qualibration.model.CalibrationImage;
import lombok.Data;

@Data
public class PersistedCalibrationImage {

    private String name;
    private Boolean hasAutoDetectedPoints;
    private CalibrationObservation detectedPoints;

    public static PersistedCalibrationImage fromCalibrationImage(CalibrationImage ci) {
        if (ci == null)
            return null;

        PersistedCalibrationImage pci = new PersistedCalibrationImage();
        pci.setName(ci.getFile().getName());
        pci.setHasAutoDetectedPoints(ci.getHasAutoDetectedPoints());
        if (ci.getDetectedPoints() != null)
            pci.setDetectedPoints(ci.getDetectedPoints().copy());

        return pci;
    }
}
