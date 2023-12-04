package eu.brnt.qualibration.component;

import eu.brnt.qualibration.model.Couple;
import javafx.geometry.VPos;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

public interface Drawer {

    void fitRectangle(double x0, double y0, double x1, double y1);

    Couple<Double, Double> pixelToUser(double xPixel, double yPixel);

    double pixelLengthToUser(double length);

    void clearAll();

    void clearRectScaled(double x, double y, double w, double h);

    void setStroke(Paint p);

    void strokeLineScaled(double x1, double y1, double x2, double y2);

    void strokeRectScaled(double x1, double y1, double w, double h);

    /**
     * Draws a marker of fixed size.
     * The center (x,y) is expressed in the user coordinate system.
     * The radius is expressed in pixels and is independent of the scale of the user coordinate system.
     * Shape is one of 'x', '+', 'o'
     */
    void fixedSizeMarker(double x, double y, double radiusPixel, char shape);

    void fillText(String text, double x, double y);

    void setTextBaseline(VPos baseline);

    void setTextAlign(TextAlignment align);
}
