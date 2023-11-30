package eu.brnt.qualibration.controller;

import boofcv.struct.calib.CameraPinholeBrown;
import eu.brnt.qualibration.model.Couple;
import eu.brnt.qualibration.model.Project;
import eu.brnt.qualibration.model.UndistMargins;
import eu.brnt.qualibration.model.ValueHolder;
import eu.brnt.qualibration.model.configuration.Configuration;
import eu.brnt.qualibration.util.MathEx;
import eu.brnt.qualibration.util.ScaleTranslateUtil;
import eu.brnt.qualibration.util.Undistorter;
import eu.brnt.qualibration.view.ViewFactory;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.ejml.data.DMatrixRMaj;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MarginsWindowController extends BaseController implements Initializable {

    @FXML private Node rootNode;

    @FXML private Pane pane;
    @FXML private Canvas canvas;

    @FXML private TextField topTextField;
    @FXML private TextField rightTextField;
    @FXML private TextField bottomTextField;
    @FXML private TextField leftTextField;

    private final Configuration configuration;
    private final Project project;
    private final UndistMargins margins;
    private final ValueHolder<UndistMargins> result;

    public MarginsWindowController(ViewFactory viewFactory, String fxmlName, Configuration configuration, Project project, UndistMargins margins, ValueHolder<UndistMargins> result) {
        super(viewFactory, fxmlName);
        this.configuration = configuration;
        this.project = project;
        this.margins = margins;
        this.result = result;
        this.result.setValue(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ((Stage) rootNode.getScene().getWindow()).setTitle("Qualibration - Margins"));

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            canvas.setWidth(pane.getWidth());
            canvas.setHeight(pane.getHeight());
            redraw();
        };

        pane.widthProperty().addListener(resizeListener);
        pane.heightProperty().addListener(resizeListener);

        topTextField.setText(String.valueOf(margins.getTop()));
        rightTextField.setText(String.valueOf(margins.getRight()));
        bottomTextField.setText(String.valueOf(margins.getBottom()));
        leftTextField.setText(String.valueOf(margins.getLeft()));

        InvalidationListener marginChangedListener = obs -> redraw();
        topTextField.textProperty().addListener(marginChangedListener);
        rightTextField.textProperty().addListener(marginChangedListener);
        bottomTextField.textProperty().addListener(marginChangedListener);
        leftTextField.textProperty().addListener(marginChangedListener);
    }

    @FXML
    void onSameClicked() {
        topTextField.setText("0");
        rightTextField.setText("0");
        bottomTextField.setText("0");
        leftTextField.setText("0");
    }

    private final static int N_POINTS_FIT = 201;

    @FXML
    void onFitOutsideClicked() {
        CameraPinholeBrown cpb = project.getCameraPinholeBrown();
        if (cpb == null)
            return;

        Undistorter undistorter = new Undistorter(cpb, new UndistMargins(0, 0, 0, 0));

        int top, right, bottom, left;

        // Margin top
        {
            double[] ys = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = i * (cpb.getWidth() - 1.0) / (N_POINTS_FIT - 1.0);
                double y = 0;
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                ys[i] = tmp.getSecond();
            }
            double Ymin = MathEx.min(ys);
            top = (int) Math.ceil(-Ymin);
        }

        // Margin right
        {
            double[] xs = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = cpb.getWidth() - 1;
                double y = i * (cpb.getHeight() - 1.0) / (N_POINTS_FIT - 1.0);
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                xs[i] = tmp.getFirst();
            }
            double Xmax = MathEx.max(xs);
            right = (int) Math.ceil(Xmax - (cpb.getWidth() - 1));
        }

        // Margin bottom
        {
            double[] ys = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = i * (cpb.getWidth() - 1.0) / (N_POINTS_FIT - 1.0);
                double y = cpb.getHeight() - 1;
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                ys[i] = tmp.getSecond();
            }
            double Ymax = MathEx.max(ys);
            bottom = (int) Math.ceil(Ymax - (cpb.getHeight() - 1));
        }

        // Margin left
        {
            double[] xs = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = 0;
                double y = i * (cpb.getHeight() - 1.0) / (N_POINTS_FIT - 1.0);
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                xs[i] = tmp.getFirst();
            }
            double Xmin = MathEx.min(xs);
            left = (int) Math.ceil(-Xmin);
        }

        topTextField.setText(String.valueOf(top));
        rightTextField.setText(String.valueOf(right));
        bottomTextField.setText(String.valueOf(bottom));
        leftTextField.setText(String.valueOf(left));
    }

    @FXML
    void onFitInsideClicked() {
        CameraPinholeBrown cpb = project.getCameraPinholeBrown();
        if (cpb == null)
            return;

        Undistorter undistorter = new Undistorter(cpb, new UndistMargins(0, 0, 0, 0));

        int top, right, bottom, left;

        // Margin top
        {
            double[] ys = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = i * (cpb.getWidth() - 1.0) / (N_POINTS_FIT - 1.0);
                double y = 0;
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                ys[i] = tmp.getSecond();
            }
            double Ymax = MathEx.max(ys);
            top = (int) Math.floor(-Ymax);
        }

        // Margin right
        {
            double[] xs = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = cpb.getWidth() - 1;
                double y = i * (cpb.getHeight() - 1.0) / (N_POINTS_FIT - 1.0);
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                xs[i] = tmp.getFirst();
            }
            double Xmin = MathEx.min(xs);
            right = (int) Math.floor(Xmin - (cpb.getWidth() - 1));
        }

        // Margin bottom
        {
            double[] ys = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = i * (cpb.getWidth() - 1.0) / (N_POINTS_FIT - 1.0);
                double y = cpb.getHeight() - 1;
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                ys[i] = tmp.getSecond();
            }
            double Ymin = MathEx.min(ys);
            bottom = (int) Math.floor(Ymin - (cpb.getHeight() - 1));
        }

        // Margin left
        {
            double[] xs = new double[N_POINTS_FIT];
            for (int i = 0; i < N_POINTS_FIT; i++) {
                double x = 0;
                double y = i * (cpb.getHeight() - 1.0) / (N_POINTS_FIT - 1.0);
                Couple<Double, Double> tmp = undistorter.applyInv(x, y);
                xs[i] = tmp.getFirst();
            }
            double Xmax = MathEx.max(xs);
            left = (int) Math.floor(-Xmax);
        }

        topTextField.setText(String.valueOf(top));
        rightTextField.setText(String.valueOf(right));
        bottomTextField.setText(String.valueOf(bottom));
        leftTextField.setText(String.valueOf(left));
    }

    @FXML
    void onMinus5Clicked() {
        increase(-5);
    }

    @FXML
    void onMinus1Clicked() {
        increase(-1);
    }

    @FXML
    void onPlus1Clicked() {
        increase(1);
    }

    @FXML
    void onPlus5Clicked() {
        increase(5);
    }

    private void increase(int val) {
        UndistMargins margins = getMargins();
        topTextField.setText(String.valueOf(margins.getTop() + val));
        rightTextField.setText(String.valueOf(margins.getRight() + val));
        bottomTextField.setText(String.valueOf(margins.getBottom() + val));
        leftTextField.setText(String.valueOf(margins.getLeft() + val));
    }

    @FXML
    void onCancelClicked() {
        this.result.setValue(null);
        viewFactory.closeStage((Stage) rootNode.getScene().getWindow());
    }

    @FXML
    void onApplyClicked() {
        result.setValue(getMargins());
        viewFactory.closeStage((Stage) rootNode.getScene().getWindow());
    }

    @SuppressWarnings("DuplicatedCode")
    private UndistMargins getMargins() {
        int top = 0, right = 0, bottom = 0, left = 0;
        try {
            top = Integer.parseInt(topTextField.getText());
        } catch (NumberFormatException e) {
            log.error("Invalid undist margin top value '{}'", topTextField.getText(), e);
        }
        try {
            right = Integer.parseInt(rightTextField.getText());
        } catch (NumberFormatException e) {
            log.error("Invalid undist margin right value '{}'", rightTextField.getText(), e);
        }
        try {
            bottom = Integer.parseInt(bottomTextField.getText());
        } catch (NumberFormatException e) {
            log.error("Invalid undist margin bottom value '{}'", bottomTextField.getText(), e);
        }
        try {
            left = Integer.parseInt(leftTextField.getText());
        } catch (NumberFormatException e) {
            log.error("Invalid undist margin left value '{}'", leftTextField.getText(), e);
        }
        return new UndistMargins(top, right, bottom, left);
    }

    private static final double EXTRA_MARGIN = 10;

    private static final int BORDER_N_POINTS = 101;

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void redraw() {
        final double canvasWidth = canvas.getWidth();
        final double canvasHeight = canvas.getHeight();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        CameraPinholeBrown cpb = project.getCameraPinholeBrown();

        // Special short-circuit case where the calibration has not been done
        if (cpb == null) {
            gc.setFill(Color.GRAY);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Calibration must have been done to show the illustration", canvasWidth / 2, canvasHeight / 2);
            return;
        }

        // The 1st step is to find the extent of what we want to display.
        // The display will extend from (xMin,yMin) to (xMax,yMax) expressed in the original image coordinate system.
        // To get those limits, we must look at the original image, the extent of the undistorted image, the final image
        // with the margins, and the fact that we add a little extra margin around everything for a better display.
        UndistMargins margins = getMargins();

        int top = margins.getTop();
        int right = margins.getRight();
        int bottom = margins.getBottom();
        int left = margins.getLeft();

        int imageWidth = cpb.getWidth();
        int imageHeight = cpb.getHeight();

        double[] allXs = new double[8 * BORDER_N_POINTS + 4];
        double[] allYs = new double[8 * BORDER_N_POINTS + 4];

        int k = 0;
        allXs[k] = -EXTRA_MARGIN;
        allYs[k++] = -EXTRA_MARGIN;

        allXs[k] = -left - EXTRA_MARGIN;
        allYs[k++] = -top - EXTRA_MARGIN;

        allXs[k] = imageWidth + EXTRA_MARGIN;
        allYs[k++] = imageHeight + EXTRA_MARGIN;

        allXs[k] = imageWidth + right + EXTRA_MARGIN;
        allYs[k++] = imageHeight + bottom + EXTRA_MARGIN;

        // We always use 0 margins to compute the undistorted points because the origin of the coordinate system in
        // which we work is the top left corner of the original image, the final image is displayed with a top left
        // corner which is generally not at (0,0) => this is different from when we actually make the undistorted images
        // where the origin of that image is necessarily at (0,0)
        Undistorter undistorter = new Undistorter(cpb, new UndistMargins(0, 0, 0, 0));

        // Look at the extent of the border of the undistorted image
        for (int i = 0; i < BORDER_N_POINTS; i++) {
            double x = i * (imageWidth - 1.0) / BORDER_N_POINTS;
            double y = 0;
            Couple<Double, Double> tmp = undistorter.applyInv(x, y);
            allXs[k] = tmp.getFirst() - EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() - EXTRA_MARGIN;
            allXs[k] = tmp.getFirst() + EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() + EXTRA_MARGIN;
        }

        for (int i = 0; i < BORDER_N_POINTS; i++) {
            double x = imageWidth - 1;
            double y = i * (imageHeight - 1.0) / BORDER_N_POINTS;
            Couple<Double, Double> tmp = undistorter.applyInv(x, y);
            allXs[k] = tmp.getFirst() - EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() - EXTRA_MARGIN;
            allXs[k] = tmp.getFirst() + EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() + EXTRA_MARGIN;
        }

        for (int i = 0; i < BORDER_N_POINTS; i++) {
            double x = (imageWidth - 1) - i * (imageWidth - 1.0) / BORDER_N_POINTS;
            double y = imageHeight - 1;
            Couple<Double, Double> tmp = undistorter.applyInv(x, y);
            allXs[k] = tmp.getFirst() - EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() - EXTRA_MARGIN;
            allXs[k] = tmp.getFirst() + EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() + EXTRA_MARGIN;
        }

        for (int i = 0; i < BORDER_N_POINTS; i++) {
            double x = 0;
            double y = (imageHeight - 1) - i * (imageHeight - 1.0) / BORDER_N_POINTS;
            Couple<Double, Double> tmp = undistorter.applyInv(x, y);
            allXs[k] = tmp.getFirst() - EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() - EXTRA_MARGIN;
            allXs[k] = tmp.getFirst() + EXTRA_MARGIN;
            allYs[k++] = tmp.getSecond() + EXTRA_MARGIN;
        }

        // Here we go, we finally have the extent of what we want to display (expressed in the original image
        // coordinate system)
        double xMin = MathEx.min(allXs);
        double xMax = MathEx.max(allXs);
        double yMin = MathEx.min(allYs);
        double yMax = MathEx.max(allYs);

        // Now we want to make our full drawing fit inside the JavaFX canvas
        final double W = canvasWidth;
        final double H = canvasHeight;
        final double w = xMax - xMin;
        final double h = yMax - yMin;

        double u0 = xMin, v0 = yMin, u1 = xMax, v1 = yMax;
        double u0p, v0p, u1p, v1p;
        if (w * H > h * W) {
            u0p = 0;
            v0p = (H - h * W / w) / 2.0;
            u1p = W;
            v1p = (H + h * W / w) / 2.0;
        } else {
            u0p = (W - w * H / h) / 2.0;
            v0p = 0;
            u1p = (W + w * H / h) / 2.0;
            v1p = H;
        }

        DMatrixRMaj img2canv = ScaleTranslateUtil.fromPoints(new double[]{u0, u1}, new double[]{v0, v1}, new double[]{u0p, u1p}, new double[]{v0p, v1p});
        double s = img2canv.get(0, 0);
        double tx = img2canv.get(0, 2);
        double ty = img2canv.get(1, 2);

        // Draw the original image
        double img_x0 = s * 0 + tx;
        double img_y0 = s * 0 + ty;
        double img_x1 = s * (imageWidth - 1) + tx;
        double img_y1 = s * (imageHeight - 1) + ty;
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.setLineWidth(3.0);
        gc.strokeRect(img_x0, img_y0, img_x1 - img_x0, img_y1 - img_y0);

        // Draw the undistorted image
        gc.setLineWidth(1.0);
        gc.setStroke(Color.CRIMSON);

        int nVert = configuration.getUndistGridConfig().getVertical();
        int nHoriz = configuration.getUndistGridConfig().getHorizontal();

        for (int i = 0; i < nVert; i++) {
            for (int j = 0; j < nHoriz; j++) {
                double x0 = j * (imageWidth - 1.0) / nHoriz;
                double x1 = (j + 1) * (imageWidth - 1.0) / nHoriz;
                double y0 = i * (imageHeight - 1.0) / nVert;
                double y1 = (i + 1) * (imageHeight - 1.0) / nVert;

                Couple<Double, Double> tmp = undistorter.applyInv(x0, y0);
                double x0p = s * tmp.getFirst() + tx;
                double y0p = s * tmp.getSecond() + ty;

                tmp = undistorter.applyInv(x1, y0);
                double x1p = s * tmp.getFirst() + tx;
                double y1p = s * tmp.getSecond() + ty;

                tmp = undistorter.applyInv(x1, y1);
                double x2p = s * tmp.getFirst() + tx;
                double y2p = s * tmp.getSecond() + ty;

                tmp = undistorter.applyInv(x0, y1);
                double x3p = s * tmp.getFirst() + tx;
                double y3p = s * tmp.getSecond() + ty;

                gc.strokeLine(x0p, y0p, x1p, y1p);
                gc.strokeLine(x1p, y1p, x2p, y2p);
                gc.strokeLine(x2p, y2p, x3p, y3p);
                gc.strokeLine(x3p, y3p, x0p, y0p);
            }
        }

        // Draw the final image size (original image + margins)
        double full_x0 = s * -left + tx;
        double full_y0 = s * -top + ty;
        double full_x1 = s * (imageWidth - 1 + right) + tx;
        double full_y1 = s * (imageHeight - 1 + bottom) + ty;
        gc.setLineWidth(1.0);
        gc.setStroke(Color.DARKORANGE);
        gc.strokeRect(full_x0, full_y0, full_x1 - full_x0, full_y1 - full_y0);
    }
}
