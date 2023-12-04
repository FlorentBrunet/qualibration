package eu.brnt.qualibration.component;

import eu.brnt.qualibration.model.Couple;
import eu.brnt.qualibration.util.ScaleTranslateUtil;
import javafx.beans.InvalidationListener;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import org.ejml.data.DMatrixRMaj;

public class ScaledDrawer implements Drawer {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private double x0 = 0;
    private double y0 = 0;
    private double x1 = 1;
    private double y1 = 1;

    private record Mapping(double sx, double sy, double tx, double ty) {
    }

    private Mapping mapping = null;
    private boolean mappingUpToDate = false;

    public ScaledDrawer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        InvalidationListener invalidationListener = obs -> mappingUpToDate = false;
        this.canvas.widthProperty().addListener(invalidationListener);
        this.canvas.heightProperty().addListener(invalidationListener);
    }

    @Override
    public void fitRectangle(double x0, double y0, double x1, double y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        mappingUpToDate = false;
    }

    @Override
    public Couple<Double, Double> pixelToUser(double xPixel, double yPixel) {
        Mapping mapping = getMapping();
        double sx = mapping.sx;
        double sy = mapping.sy;
        double tx = mapping.tx;
        double ty = mapping.ty;
        return new Couple<>((xPixel - tx) / sx, (yPixel - ty) / sy);
    }

    @Override
    public double pixelLengthToUser(double length) {
        Mapping mapping = getMapping();
        double sx = Math.abs(mapping.sx);
        return length / sx;
    }

    @Override
    public void clearAll() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void clearRectScaled(double x, double y, double w, double h) {
        Mapping mapping = getMapping();
        gc.clearRect(
                mapping.sx * x + mapping.tx,
                mapping.sy * y + mapping.ty,
                mapping.sx * w,
                mapping.sy * h
        );
    }

    @Override
    public void setStroke(Paint p) {
        gc.setStroke(p);
    }

    @Override
    public void strokeLineScaled(double x1, double y1, double x2, double y2) {
        Mapping mapping = getMapping();
        gc.strokeLine(
                mapping.sx * x1 + mapping.tx,
                mapping.sy * y1 + mapping.ty,
                mapping.sx * x2 + mapping.tx,
                mapping.sy * y2 + mapping.ty
        );
    }

    @Override
    public void strokeRectScaled(double x1, double y1, double x2, double y2) {
        Mapping mapping = getMapping();
        double x1p = mapping.sx * x1 + mapping.tx;
        double y1p = mapping.sy * y1 + mapping.ty;
        double x2p = mapping.sx * x2 + mapping.tx;
        double y2p = mapping.sy * y2 + mapping.ty;
        double x0 = Math.min(x1p, x2p);
        double y0 = Math.min(y1p, y2p);
        double w = Math.abs(x1p - x2p);
        double h = Math.abs(y1p - y2p);
        gc.strokeRect(x0, y0, w, h);
    }

    @Override
    public void fixedSizeMarker(double x, double y, double radiusPixel, char shape) {
        Mapping mapping = getMapping();
        double xp = mapping.sx * x + mapping.tx;
        double yp = mapping.sy * y + mapping.ty;
        if (shape == 'x') {
            gc.strokeLine(xp - radiusPixel, yp - radiusPixel, xp + radiusPixel, yp + radiusPixel);
            gc.strokeLine(xp + radiusPixel, yp - radiusPixel, xp - radiusPixel, yp + radiusPixel);
        } else if (shape == '+') {
            gc.strokeLine(xp, yp - radiusPixel, xp, yp + radiusPixel);
            gc.strokeLine(xp - radiusPixel, yp, xp + radiusPixel, yp);
        } else if (shape == 'o') {
            gc.strokeOval(xp - radiusPixel, yp - radiusPixel, radiusPixel * 2, radiusPixel * 2);
        } else {
            throw new IllegalArgumentException("Unknown shape: " + shape);
        }
    }

    @Override
    public void fillText(String text, double x, double y) {
        Mapping mapping = getMapping();
        gc.fillText(
                text,
                mapping.sx * x + mapping.tx,
                mapping.sy * y + mapping.ty
        );
    }

    @Override
    public void setTextBaseline(VPos baseline) {
        gc.setTextBaseline(baseline);
    }

    @Override
    public void setTextAlign(TextAlignment align) {
        gc.setTextAlign(align);
    }

    private Mapping getMapping() {
        if (mapping == null || !mappingUpToDate) {
            double W = canvas.getWidth();
            double H = canvas.getHeight();
            double w = Math.abs(x1 - x0);
            double h = Math.abs(y1 - y0);

            double u0 = x0, v0 = y0, u1 = x1, v1 = y1;
            double u0p, v0p, u1p, v1p;
            if (w * H > h * W) {
                u0p = 0;
                v0p = (H - h * W / w) / 2;
                u1p = W;
                v1p = (H + h * W / w) / 2;
            } else {
                u0p = (W - w * H / h) / 2;
                v0p = 0;
                u1p = (W + w * H / h) / 2;
                v1p = H;
            }

            DMatrixRMaj M = ScaleTranslateUtil.fromPoints(
                    new double[]{u0, u1},
                    new double[]{v0, v1},
                    new double[]{u0p, u1p},
                    new double[]{v0p, v1p}
            );
            mapping = new Mapping(
                    M.get(0, 0),
                    M.get(1, 1),
                    M.get(0, 2),
                    M.get(1, 2)
            );
            mappingUpToDate = true;
        }

        return mapping;
    }
}
