package eu.brnt.qualibration.model;

import boofcv.abst.geo.calibration.DetectSingleFiducialCalibration;
import boofcv.struct.calib.CameraPinholeBrown;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Project {

    private final File rootDir;

    private final List<CalibrationImage> calibrationImages = new LinkedList<>();

    private int targetRowsCount;
    private int targetColumnsCount;
    private Double targetSquareSize;

    private DetectSingleFiducialCalibration detector;

    private boolean cameraModelAssumeZeroSkew;
    private int cameraModelRadialParameters;
    private boolean cameraModelIncludeTangential;

    private CameraPinholeBrown cameraPinholeBrown;

    private int undistMarginTop;
    private int undistMarginRight;
    private int undistMarginBottom;
    private int undistMarginLeft;


    public Project(File rootDir) {
        this.rootDir = rootDir;
    }

    public void addCalibrationImage(File file, BufferedImage image) {
        calibrationImages.add(new CalibrationImage(file, image));
    }

    public File getRootDir() {
        return rootDir;
    }

    public List<CalibrationImage> getCalibrationImages() {
        return calibrationImages;
    }

    public int getTargetRowsCount() {
        return targetRowsCount;
    }

    public void setTargetRowsCount(int targetRowsCount) {
        this.targetRowsCount = targetRowsCount;
    }

    public int getTargetColumnsCount() {
        return targetColumnsCount;
    }

    public void setTargetColumnsCount(int targetColumnsCount) {
        this.targetColumnsCount = targetColumnsCount;
    }

    public double getTargetSquareSize() {
        return targetSquareSize;
    }

    public void setTargetSquareSize(Double targetSquareSize) {
        this.targetSquareSize = targetSquareSize;
    }

    public DetectSingleFiducialCalibration getDetector() {
        return detector;
    }

    public void setDetector(DetectSingleFiducialCalibration detector) {
        this.detector = detector;
    }

    public boolean isCameraModelAssumeZeroSkew() {
        return cameraModelAssumeZeroSkew;
    }

    public void setCameraModelAssumeZeroSkew(boolean cameraModelAssumeZeroSkew) {
        this.cameraModelAssumeZeroSkew = cameraModelAssumeZeroSkew;
    }

    public int getCameraModelRadialParameters() {
        return cameraModelRadialParameters;
    }

    public void setCameraModelRadialParameters(int cameraModelRadialParameters) {
        this.cameraModelRadialParameters = cameraModelRadialParameters;
    }

    public boolean isCameraModelIncludeTangential() {
        return cameraModelIncludeTangential;
    }

    public void setCameraModelIncludeTangential(boolean cameraModelIncludeTangential) {
        this.cameraModelIncludeTangential = cameraModelIncludeTangential;
    }

    public CameraPinholeBrown getCameraPinholeBrown() {
        return cameraPinholeBrown;
    }

    public void setCameraPinholeBrown(CameraPinholeBrown cameraPinholeBrown) {
        this.cameraPinholeBrown = cameraPinholeBrown;
    }

    public int getUndistMarginTop() {
        return undistMarginTop;
    }

    public void setUndistMarginTop(int undistMarginTop) {
        this.undistMarginTop = undistMarginTop;
    }

    public int getUndistMarginRight() {
        return undistMarginRight;
    }

    public void setUndistMarginRight(int undistMarginRight) {
        this.undistMarginRight = undistMarginRight;
    }

    public int getUndistMarginBottom() {
        return undistMarginBottom;
    }

    public void setUndistMarginBottom(int undistMarginBottom) {
        this.undistMarginBottom = undistMarginBottom;
    }

    public int getUndistMarginLeft() {
        return undistMarginLeft;
    }

    public void setUndistMarginLeft(int undistMarginLeft) {
        this.undistMarginLeft = undistMarginLeft;
    }

    public CameraDefinition toInternalCameraDefinition() {
        if (cameraPinholeBrown == null)
            return null;

        CameraPinholeBrown cpb = cameraPinholeBrown;

        CameraDefinition def = new CameraDefinition();

        def.setCameraFileVersion(2);
        def.setName(rootDir.getAbsolutePath());

        def.setImageWidth(cpb.getWidth());
        def.setImageHeight(cpb.getHeight());

        def.setFocalX(cpb.getFx());
        def.setFocalY(cpb.getFy());

        def.setPrincipalPointX(cpb.getCx());
        def.setPrincipalPointY(cpb.getCy());

        double[] radial = cpb.getRadial();
        double r0 = 0, r1 = 0, r2 = 0;
        if (radial != null) {
            r0 = radial.length >= 1 ? radial[0] : 0;
            r1 = radial.length >= 2 ? radial[1] : 0;
            r2 = radial.length >= 3 ? radial[2] : 0;
        }
        double t1 = cpb.getT1();
        double t2 = cpb.getT2();
        def.setDistortionCoefficients(List.of(r0, r1, t1, t2, r2));

        def.setDistortionMarginLeft(this.undistMarginLeft);
        def.setDistortionMarginTop(this.undistMarginTop);
        def.setDistortionMarginRight(this.undistMarginRight);
        def.setDistortionMarginBottom(this.undistMarginBottom);

        def.setRectificationHomography(
                List.of(
                        List.of(1.0, 0.0, 0.0),
                        List.of(0.0, 1.0, 0.0),
                        List.of(0.0, 0.0, 1.0)
                )
        );

        def.setRectificationX0(0);
        def.setRectificationY0(0);
        def.setRectificationX1(cpb.getWidth() - 1);
        def.setRectificationY0(cpb.getHeight() - 1);

        double x0 = 0, y0 = 0, x1 = cpb.getWidth() - 1, y1 = cpb.getHeight() - 1;
        def.setRoiX(
                List.of(
                        List.of(x0, x1, x0),
                        List.of(x1, x0, x1)
                )
        );
        def.setRoiY(
                List.of(
                        List.of(y0, y0, y1),
                        List.of(y1, y1, y0)
                )
        );

        return def;
    }
}
