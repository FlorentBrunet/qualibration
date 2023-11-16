package eu.brnt.qualibration;

import boofcv.abst.fiducial.calib.ConfigGridDimen;
import boofcv.abst.geo.calibration.CalibrateMonoPlanar;
import boofcv.abst.geo.calibration.DetectSingleFiducialCalibration;
import boofcv.factory.fiducial.FactoryFiducialCalibration;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.image.GrayF32;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Main starting...");
        Launcher.main(args);
    }

    public static void main0(String[] args) {
        DetectSingleFiducialCalibration detector;
        List<String> images;

        // Regular Circle Example
//		detector = FactoryFiducialCalibration.circleRegularGrid(null, new ConfigGridDimen(/*numRows*/ 8, /*numCols*/ 10, 1.5, 2.5));
//		images = UtilIO.listByPrefix(UtilIO.pathExample("calibration/mono/Sony_DSC-HX5V_CircleRegular"),"image", null);

        // Hexagonal Circle Example
//		detector = FactoryFiducialCalibration.circleHexagonalGrid(null, new ConfigGridDimen(/*numRows*/ 24, /*numCols*/ 28, 1, 1.2));
//		images = UtilIO.listByPrefix(UtilIO.pathExample("calibration/mono/Sony_DSC-HX5V_CircleHexagonal"),"image", null);

        // Square Grid example
//		detector = FactoryFiducialCalibration.squareGrid(null, new ConfigGridDimen(/*numRows*/ 4, /*numCols*/ 3, 30, 30));
//		images = UtilIO.listByPrefix(UtilIO.pathExample("calibration/stereo/Bumblebee2_Square"),"left", null);

        // ECoCheck Example
//		detector = new MultiToSingleFiducialCalibration(FactoryFiducialCalibration.
//				ecocheck(null, ConfigECoCheckMarkers.
//						singleShape(/*numRows*/ 9, /*numCols*/ 7, /*num markers*/ 1, /* square size */ 30)));
//		images = UtilIO.listByPrefix(UtilIO.pathExample("calibration/stereo/Zed_ecocheck"), "left", null);

        // Chessboard Example
        detector = FactoryFiducialCalibration.chessboardX(
                null,
                new ConfigGridDimen(/*numRows*/ 14,/*numCols*/ 13,/*shapeSize*/ 30)
        );
        images = UtilIO.listByPrefix(UtilIO.pathExample("/Users/florent/Downloads/calib_example/jpg"), "Image", null);

        // Declare and setup the calibration algorithm
        var calibrator = new CalibrateMonoPlanar();

        // tell it type of target and which intrinsic parameters to estimate
        calibrator.configurePinhole(
                /*assumeZeroSkew*/ true,
                /*numRadialParam*/ 2,
                /*includeTangential*/ true
        );

        var usedImages = new ArrayList<String>();
        for (String n : images) {
            BufferedImage input = UtilImageIO.loadImageNotNull(n);
            GrayF32 image = ConvertBufferedImage.convertFrom(input, (GrayF32) null);
            if (detector.process(image)) {
                // Need to tell it the image shape and the layout once
                if (usedImages.isEmpty())
                    calibrator.initialize(image.getWidth(), image.getHeight(), List.of(detector.getLayout()));

                calibrator.addImage(detector.getDetectedPoints().copy());
                usedImages.add(n);
            } else {
                System.err.println("Failed to detect target in " + n);
            }
        }
        // process and compute intrinsic parameters
        CameraPinholeBrown intrinsic = calibrator.process();

        // save results to a file and print out
        //CalibrationIO.save(intrinsic, "intrinsic.yaml");

        System.out.println(calibrator.computeQualityText(usedImages));
        System.out.println();
        System.out.println("--- Intrinsic Parameters ---");
        System.out.println();
        intrinsic.print();
    }
}
