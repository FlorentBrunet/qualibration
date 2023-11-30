package eu.brnt.qualibration.util;

import org.ejml.data.DMatrixRMaj;

import static org.ejml.dense.row.CommonOps_DDRM.*;

public final class ScaleTranslateUtil {

    private ScaleTranslateUtil() {
        throw new RuntimeException(ScaleTranslateUtil.class.getName() + " must not be instantiated");
    }

    public static DMatrixRMaj fromPoints(double[] x, double[] y, double[] xp, double[] yp) {
        int nPoints = x.length;

        if (nPoints < 2)
            throw new IllegalArgumentException("At least 2 point correspondences are required");
        if (y.length != nPoints || xp.length != nPoints || yp.length != nPoints)
            throw new IllegalArgumentException("Point correspondences must have the same number of points");

        DMatrixRMaj M = new DMatrixRMaj(2 * nPoints, 4);
        DMatrixRMaj right = new DMatrixRMaj(2 * nPoints, 1);

        for (int i = 0; i < nPoints; i++) {
            M.set(2 * i, 0, x[i]);
            M.set(2 * i, 1, 0);
            M.set(2 * i, 2, 1);
            M.set(2 * i, 3, 0);

            M.set(2 * i + 1, 0, 0);
            M.set(2 * i + 1, 1, y[i]);
            M.set(2 * i + 1, 2, 0);
            M.set(2 * i + 1, 3, 1);

            right.set(2 * i, 0, xp[i]);
            right.set(2 * i + 1, 0, yp[i]);
        }

        DMatrixRMaj Mt = transpose(M, null);
        DMatrixRMaj Mt_M = mult(Mt, M, null);
        DMatrixRMaj Mt_M_inv = new DMatrixRMaj(4, 4);
        invert(Mt_M, Mt_M_inv);
        DMatrixRMaj Mt_M_inv_Mt = mult(Mt_M_inv, Mt, null);
        DMatrixRMaj tmp = mult(Mt_M_inv_Mt, right, null);

        return new DMatrixRMaj(3, 3, true,
                tmp.get(0, 0), 0, tmp.get(2, 0),
                0, tmp.get(1, 0), tmp.get(3, 0),
                0, 0, 1
        );
    }
}
