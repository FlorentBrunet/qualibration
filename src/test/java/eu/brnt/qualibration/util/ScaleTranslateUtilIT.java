package eu.brnt.qualibration.util;

import org.ejml.data.DMatrixRMaj;
import org.junit.jupiter.api.Test;

import static org.ejml.dense.row.CommonOps_DDRM.mult;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScaleTranslateUtilIT {

    @Test
    void scaleTranslate01() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1}, new double[]{0, 1}, new double[]{0, 1}, new double[]{0, 1});
        A.print();
        assertTrue(
                areMatrixEquals(
                        A,
                        new DMatrixRMaj(3, 3, true,
                                1, 0, 0,
                                0, 1, 0,
                                0, 0, 1
                        )
                )
        );
    }

    @Test
    void scaleTranslate02() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1}, new double[]{0, 1}, new double[]{0, 2}, new double[]{0, 2});
        A.print();
        assertTrue(
                areMatrixEquals(
                        A,
                        new DMatrixRMaj(3, 3, true,
                                2, 0, 0,
                                0, 2, 0,
                                0, 0, 1
                        )
                )
        );
    }

    @Test
    void scaleTranslate03() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1}, new double[]{0, 1}, new double[]{1, 2}, new double[]{1, 2});
        A.print();
        assertTrue(
                areMatrixEquals(
                        A,
                        new DMatrixRMaj(3, 3, true,
                                1, 0, 1,
                                0, 1, 1,
                                0, 0, 1
                        )
                )
        );
    }

    @Test
    void scaleTranslate04() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1}, new double[]{0, 1}, new double[]{2, 4}, new double[]{1, 5});
        A.print();
        assertTrue(
                areMatrixEquals(
                        A,
                        new DMatrixRMaj(3, 3, true,
                                2, 0, 2,
                                0, 4, 1,
                                0, 0, 1
                        )
                )
        );
    }

    @Test
    void scaleTranslate05() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1}, new double[]{0, 1}, new double[]{2, 4}, new double[]{1, 5});
        DMatrixRMaj Ainv = ScaleTranslateUtil.fromPoints(new double[]{2, 4}, new double[]{1, 5}, new double[]{0, 1}, new double[]{0, 1});
        DMatrixRMaj I = new DMatrixRMaj(3, 3);
        mult(A, Ainv, I);
        A.print();
        Ainv.print();
        I.print();
        assertTrue(
                areMatrixEquals(
                        I,
                        new DMatrixRMaj(3, 3, true,
                                1, 0, 0,
                                0, 1, 0,
                                0, 0, 1
                        )
                )
        );
    }

    @Test
    void scaleTranslate06() {
        DMatrixRMaj A = ScaleTranslateUtil.fromPoints(new double[]{0, 1, 2}, new double[]{0, 1, 2}, new double[]{0, 1, 2}, new double[]{0, 1, 2});
        A.print();
        assertTrue(
                areMatrixEquals(
                        A,
                        new DMatrixRMaj(3, 3, true,
                                1, 0, 0,
                                0, 1, 0,
                                0, 0, 1
                        )
                )
        );
    }

    private final static double EPS = 1.0e-12;

    private boolean areMatrixEquals(DMatrixRMaj a, DMatrixRMaj b) {
        if (a.getNumRows() != b.getNumRows() || a.getNumCols() != b.getNumCols())
            return false;

        for (int i = 0; i < a.getNumRows(); i++) {
            for (int j = 0; j < a.getNumCols(); j++) {
                double aij = a.get(i, j);
                double bij = b.get(i, j);
                if (Math.abs(aij - bij) > EPS)
                    return false;
            }
        }

        return true;
    }
}
