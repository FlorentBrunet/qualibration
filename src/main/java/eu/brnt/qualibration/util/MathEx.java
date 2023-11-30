package eu.brnt.qualibration.util;

/**
 * Small but util math functions.
 */
public final class MathEx {

    private MathEx() {
        throw new RuntimeException(MathEx.class.getName() + " must not be instantiated");
    }

    public static boolean isEven(int value) {
        return (value % 2) == 0;
    }

    public static boolean isOdd(int value) {
        return !isEven(value);
    }

    public static float sq(float val) {
        return val * val;
    }

    public static double sq(double val) {
        return val * val;
    }

    public static float sqrt(float val) {
        return (float) Math.sqrt(val);
    }

    public static double sqrt(double val) {
        return Math.sqrt(val);
    }

    public static float norm2(float x, float y) {
        return sqrt(sq(x) + sq(y));
    }

    public static double norm2(double x, double y) {
        return sqrt(sq(x) + sq(y));
    }

    public static int clip(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clip(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double clip(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double clipLower(double value, double min) {
        return Math.max(value, min);
    }

    public static double clipUpper(double value, double max) {
        return Math.min(value, max);
    }

    public static int roundAndClip(float value, int min, int max) {
        return Math.min(Math.max(Math.round(value), min), max);
    }

    public static long roundAndClip(double value, int min, int max) {
        return Math.min(Math.max(Math.round(value), min), max);
    }

    public static double roundNth(double value, int nth) {
        double fact = Math.pow(10.0, nth);
        return Math.round(value * fact) / fact;
    }

    public static int sign(int number) {
        return Integer.compare(number, 0);
    }

    public static double[] linspace(double min, double max, int n) {
        double[] values = new double[n];

        for (int i = 0; i < n; i++) {
            values[i] = min + i * (max - min) / (n - 1);
        }

        return values;
    }

    public static float[] linspace(float min, float max, int n) {
        float[] values = new float[n];

        for (int i = 0; i < n; i++) {
            values[i] = min + i * (max - min) / (n - 1.0f);
        }

        return values;
    }

    public static Double min(double... values) {
        if (values == null || values.length == 0)
            return null;

        double min = values[0];

        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public static Double max(double... values) {
        if (values == null || values.length == 0)
            return null;

        double max = values[0];

        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }
}
