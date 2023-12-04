package eu.brnt.qualibration.component;

import eu.brnt.qualibration.model.Couple;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ScaledCanvas extends Pane {

    private final Canvas canvas;
    private final Drawer drawer;

    private OnRedrawListener onRedrawListener;
    private OnMouseListener onMouseMovedListener;
    private OnMouseListener onMouseClickedListener;

    public ScaledCanvas() {
        canvas = new Canvas();
        drawer = new ScaledDrawer(canvas);
        getChildren().add(canvas);

        ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
            canvas.setWidth(getWidth());
            canvas.setHeight(getHeight());
            redraw();
        };

        widthProperty().addListener(resizeListener);
        heightProperty().addListener(resizeListener);

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (onMouseMovedListener != null) {
                double xPixel = event.getX();
                double yPixel = event.getY();
                Couple<Double, Double> res = drawer.pixelToUser(xPixel, yPixel);
                onMouseMovedListener.onEvent(xPixel, yPixel, res.getFirst(), res.getSecond());
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (onMouseClickedListener != null) {
                double xPixel = event.getX();
                double yPixel = event.getY();
                Couple<Double, Double> res = drawer.pixelToUser(xPixel, yPixel);
                onMouseClickedListener.onEvent(xPixel, yPixel, res.getFirst(), res.getSecond());
            }
        });
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public OnRedrawListener getOnRedrawListener() {
        return onRedrawListener;
    }

    public void setOnRedrawListener(OnRedrawListener onRedrawListener) {
        this.onRedrawListener = onRedrawListener;
    }

    public OnMouseListener getOnMouseMovedListener() {
        return onMouseMovedListener;
    }

    public void setOnMouseMovedListener(OnMouseListener mouseListener) {
        this.onMouseMovedListener = mouseListener;
    }

    public OnMouseListener getOnMouseClickedListener() {
        return onMouseClickedListener;
    }

    public void setOnMouseClickedListener(OnMouseListener onMouseClickedListener) {
        this.onMouseClickedListener = onMouseClickedListener;
    }

    public void fitRectangle(double x0, double y0, double x1, double y1) {
        drawer.fitRectangle(x0, y0, x1, y1);
        redraw();
    }

    public void redraw() {
        if (onRedrawListener != null) {
            onRedrawListener.onRedraw(drawer);
        } else {
            double w = canvas.getWidth();
            double h = canvas.getHeight();
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, w, h);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(0, 0, w, h);
            gc.strokeLine(0, h, w, 0);
        }
    }
}
