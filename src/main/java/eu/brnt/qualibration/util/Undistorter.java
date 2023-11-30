package eu.brnt.qualibration.util;

import boofcv.struct.calib.CameraPinholeBrown;
import eu.brnt.qualibration.model.Couple;
import eu.brnt.qualibration.model.UndistMargins;
import lombok.extern.slf4j.Slf4j;
import org.ejml.data.DMatrixRMaj;

import static org.ejml.dense.row.CommonOps_DDRM.invert;

@Slf4j
public class Undistorter {

    private final CameraPinholeBrown cpb;
    private final UndistMargins margins;
    private final boolean verbose;

    private DMatrixRMaj invK;

    public Undistorter(CameraPinholeBrown cpb, UndistMargins margins, boolean verbose) {
        this.cpb = cpb;
        this.margins = margins;
        this.verbose = verbose;
    }

    public Undistorter(CameraPinholeBrown cpb, UndistMargins margins) {
        this(cpb, margins, false);
    }

    private DMatrixRMaj getInvK() {
        if (invK == null) {
            DMatrixRMaj K = new DMatrixRMaj(3, 3, true,
                    cpb.getFx(), cpb.getSkew(), cpb.getCx(),
                    0, cpb.getFy(), cpb.getCy(),
                    0, 0, 1
            );
            invK = new DMatrixRMaj(3, 3);
            invert(K, invK);
        }
        return invK;
    }

    /**
     * Take a point (cushionX,cushionY) in the cushion and map it to the original image.
     */
    public Couple<Double, Double> apply(double cushionX, double cushionY) {
        DMatrixRMaj invK = getInvK();

        final double rayX = invK.get(0, 0) * cushionX + invK.get(0, 1) * cushionY + invK.get(0, 2);
        final double rayY = invK.get(1, 1) * cushionY + invK.get(1, 2);

        final Couple<Double, Double> distRays = applyDistToRay(cpb.getT1(), cpb.getT2(), cpb.getRadial(), rayX, rayY);

        final double xd = distRays.getFirst();
        final double yd = distRays.getSecond();

        final double xp = cpb.getFx() * xd + cpb.getCx();
        final double yp = cpb.getFy() * yd + cpb.getCy();

        return new Couple<>(xp, yp);
    }

    private static final double EPS = 1.0e-4;
    private static final double TARGET_ERR = 1.0e-6;
    private static final double MAX_ITER = 50;

    /**
     * Take a point (origX,origY) in the original image and map it into the "cushion".
     */
    public Couple<Double, Double> applyInv(double origX, double origY) {
        long t0 = System.currentTimeMillis();

        // (x, y) is the current solution that we will refine iteratively
        // We initialize with (origX,origY) since we expect the solution to be not so far away from the antecedent point
        double x = origX;
        double y = origY;

        for (int i = 0; i < MAX_ITER; i++) {
            Couple<Double, Double> tmp0 = apply(x, y);
            double x0 = tmp0.getFirst();
            double y0 = tmp0.getSecond();

            double err_x = origX - x0;
            double err_y = origY - y0;
            double err = Math.sqrt(MathEx.sq(err_x) + MathEx.sq(err_y));

            if (verbose) log.info("err={}", err);

            if (err < TARGET_ERR) {
                break;
            }

            Couple<Double, Double> tmp1 = apply(x + EPS, y);
            double x1 = tmp1.getFirst();
            double y1 = tmp1.getSecond();

            Couple<Double, Double> tmp2 = apply(x, y + EPS);
            double x2 = tmp2.getFirst();
            double y2 = tmp2.getSecond();

            double j11 = (x1 - x0) / EPS;
            double j12 = (x2 - x0) / EPS;
            double j21 = (y1 - y0) / EPS;
            double j22 = (y2 - y0) / EPS;

            double jtj11 = j11 * j11 + j21 * j21 + 0.01;
            double jtj12 = j11 * j12 + j21 * j22;
            double jtj21 = j12 * j11 + j22 * j21;
            double jtj22 = j12 * j12 + j22 * j22 + 0.01;

            double det_jtj = jtj11 * jtj22 - jtj21 * jtj12;

            double jtj_inv_11 = jtj22 / det_jtj;
            double jtj_inv_12 = -jtj12 / det_jtj;
            double jtj_inv_21 = -jtj21 / det_jtj;
            double jtj_inv_22 = jtj11 / det_jtj;

            double jtj_inv_jt_11 = jtj_inv_11 * j11 + jtj_inv_12 * j12;
            double jtj_inv_jt_12 = jtj_inv_11 * j21 + jtj_inv_12 * j22;
            double jtj_inv_jt_21 = jtj_inv_21 * j11 + jtj_inv_22 * j12;
            double jtj_inv_jt_22 = jtj_inv_21 * j21 + jtj_inv_22 * j22;

            double delta_x = jtj_inv_jt_11 * err_x + jtj_inv_jt_12 * err_y;
            double delta_y = jtj_inv_jt_21 * err_x + jtj_inv_jt_22 * err_y;

            x += delta_x;
            y += delta_y;
        }

        if (verbose) log.info("Found antecedent in {} ms", System.currentTimeMillis() - t0);

        final int top = margins.getTop();
        final int left = margins.getLeft();

        return new Couple<>(x + left, y + top);
    }

    private static Couple<Double, Double> applyDistToRay(double tng1, double tng2, double[] radial, double x, double y) {
        final double r = MathEx.norm2(x, y);
        final double rr = MathEx.sq(r);

        final double xx = MathEx.sq(x);
        final double yy = MathEx.sq(y);
        final double xy = x * y;

        final double dx = tng2 * (rr + 2 * xx) + 2 * tng1 * xy;
        final double dy = tng1 * (rr + 2 * yy) + 2 * tng2 * xy;

        float fact = 1;
        if (radial != null && radial.length > 0) {
            for (int j = 0; j < radial.length; j++) {
                fact += radial[j] * Math.pow(rr, j + 1);
            }
        }
        double xd = fact * x + dx;
        double yd = fact * y + dy;

        return new Couple<>(xd, yd);
    }
}
