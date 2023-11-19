package eu.brnt.qualibration.model.persist;

import eu.brnt.qualibration.model.CalibrationImage;
import eu.brnt.qualibration.model.Project;
import lombok.Data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Data
public class PersistedProject {

    private List<PersistedCalibrationImage> calibrationImages = new LinkedList<>();

    private int targetRowsCount;
    private int targetColumnsCount;
    private Double targetSquareSize;

    private boolean cameraModelAssumeZeroSkew;
    private int cameraModelRadialParameters;
    private boolean cameraModelIncludeTangential;

    public Project toProject(File rootDir) {
        return new Project(rootDir);
    }

    public static PersistedProject fromProject(Project project) {
        if (project == null)
            return null;

        PersistedProject pp = new PersistedProject();

        for (CalibrationImage ci : project.getCalibrationImages()) {
            pp.getCalibrationImages().add(PersistedCalibrationImage.fromCalibrationImage(ci));
        }

        pp.setTargetRowsCount(project.getTargetRowsCount());
        pp.setTargetColumnsCount(project.getTargetColumnsCount());
        pp.setTargetSquareSize(project.getTargetSquareSize());

        pp.setCameraModelAssumeZeroSkew(project.isCameraModelAssumeZeroSkew());
        pp.setCameraModelRadialParameters(project.getCameraModelRadialParameters());
        pp.setCameraModelIncludeTangential(project.isCameraModelIncludeTangential());

        return pp;
    }
}
