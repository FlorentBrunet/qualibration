package eu.brnt.qualibration.util;


import eu.brnt.qualibration.model.Couple;

public final class Util {

    private Util() {
        throw new RuntimeException(Util.class.getName() + " must not be instantiated");
    }

    public static Couple<float[], float[]> meshgridVec(int xMin, int xMax, int yMin, int yMax) {
        int len = (yMax - yMin + 1) * (xMax - xMin + 1);
        float[] xs = new float[len];
        float[] ys = new float[len];

        int k = 0;
        for (int ix = yMin; ix <= yMax; ix++) {
            for (int iy = xMin; iy <= xMax; iy++) {
                xs[k] = iy;
                ys[k] = ix;
                ++k;
            }
        }

        return new Couple<>(xs, ys);
    }
}
