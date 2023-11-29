package eu.brnt.qualibration.model;

import lombok.Data;

import java.util.List;

@Data
public class CameraDefinition {

    private int cameraFileVersion;

    private String name;

    private int imageWidth;
    private int imageHeight;

    private double focalX;
    private double focalY;
    private double principalPointX;
    private double principalPointY;
    private List<Double> distortionCoefficients;

    private int distortionMarginTop;
    private int distortionMarginRight;
    private int distortionMarginBottom;
    private int distortionMarginLeft;

    private List<List<Double>> rectificationHomography;

    private int rectificationX0;
    private int rectificationY0;
    private int rectificationX1;
    private int rectificationY1;

    private List<List<Double>> roiX;
    private List<List<Double>> roiY;
}
