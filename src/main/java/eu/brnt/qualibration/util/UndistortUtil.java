package eu.brnt.qualibration.util;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.calib.CameraPinholeBrown;
import boofcv.struct.image.InterleavedF32;
import eu.brnt.qualibration.model.Couple;
import lombok.extern.slf4j.Slf4j;
import org.ejml.data.DMatrixRMaj;

import java.awt.image.BufferedImage;

import static org.ejml.dense.row.CommonOps_DDRM.invert;

@Slf4j
public final class UndistortUtil {

    private UndistortUtil() {
        throw new RuntimeException(UndistortUtil.class.getName() + " must not be instantiated");
    }

    public static BufferedImage undistort(
            CameraPinholeBrown cam,
            int margTop, int margRight, int margBottom, int margLeft,
            BufferedImage image
    ) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final int totalWidth = imageWidth + margLeft + margRight;
        final int totalHeight = imageHeight + margTop + margBottom;

        long t0 = System.currentTimeMillis();
        final Couple<float[], float[]> grid = Util.meshgridVec(
                -margLeft,
                imageWidth + margRight - 1,
                -margTop,
                imageHeight + margBottom - 1
        );
        log.info("tMeshGrid={}", System.currentTimeMillis() - t0);
        final float[] x = grid.getFirst();
        final float[] y = grid.getSecond();

        final int nPoints = x.length;

        final float[] rayX = new float[nPoints];
        final float[] rayY = new float[nPoints];

        final float fx = (float) cam.getFx();
        final float fy = (float) cam.getFy();
        final float cx = (float) cam.getCx();
        final float cy = (float) cam.getCy();
        final float sk = (float) cam.getSkew();
        DMatrixRMaj K = new DMatrixRMaj(3, 3, true,
                fx, sk, cx,
                0, fy, cy,
                0, 0, 1
        );
        DMatrixRMaj invK = new DMatrixRMaj(3, 3);
        invert(K, invK);
        float k11 = (float) invK.get(0, 0);
        float k12 = (float) invK.get(0, 1);
        float k13 = (float) invK.get(0, 2);
        float k22 = (float) invK.get(1, 1);
        float k23 = (float) invK.get(1, 2);

        t0 = System.currentTimeMillis();
        for (int i = 0; i < x.length; i++) {
            rayX[i] = k11 * x[i] + k12 * y[i] + k13;
            rayY[i] = k22 * y[i] + k23;
        }
        log.info("tRays={}", System.currentTimeMillis() - t0);

        t0 = System.currentTimeMillis();
        final Couple<float[], float[]> distRays = applyDistToRays((float) cam.getT1(), (float) cam.getT2(), cam.getRadial(), rayX, rayY);
        log.info("tDistRays={}", System.currentTimeMillis() - t0);

        final float[] xd = distRays.getFirst();
        final float[] yd = distRays.getSecond();

        for (int i = 0; i < nPoints; i++) {
            xd[i] = fx * xd[i] + cx;
            yd[i] = fy * yd[i] + cy;
        }

        t0 = System.currentTimeMillis();
        int numBands = image.getColorModel().getNumComponents();
        InterleavedF32 input = new InterleavedF32(imageWidth, imageHeight, numBands);
        ConvertBufferedImage.convertFrom(image, input, true);
        log.info("tConvertInput={}", System.currentTimeMillis() - t0);

        t0 = System.currentTimeMillis();
        final float[][] val = ImageEx.bilinear(input, xd, yd);
        log.info("tBilinear={}", System.currentTimeMillis() - t0);

        t0 = System.currentTimeMillis();
        InterleavedF32 undist = new InterleavedF32(totalWidth, totalHeight, 3);
        if (numBands == 1) {
            int k = 0;
            for (int iy = 0; iy < totalHeight; iy++) {
                for (int ix = 0; ix < totalWidth; ix++) {
                    undist.unsafe_set(ix, iy, new float[]{val[k][0], val[k][0], val[k][0]});
                    ++k;
                }
            }
        } else {
            int k = 0;
            for (int iy = 0; iy < totalHeight; iy++) {
                for (int ix = 0; ix < totalWidth; ix++) {
                    undist.unsafe_set(ix, iy, val[k]);
                    ++k;
                }
            }
        }
        log.info("tFillUndist={}", System.currentTimeMillis() - t0);

        t0 = System.currentTimeMillis();
        BufferedImage result = ConvertBufferedImage.convertTo(undist, null, true);
        log.info("tConvertResult={}", System.currentTimeMillis() - t0);

        return result;
    }

    private static Couple<float[], float[]> applyDistToRays(float tng1, float tng2, double[] radial, float[] xs, float[] ys) {
        final int nRays = xs.length;

        float[] xd = new float[nRays];
        float[] yd = new float[nRays];

        for (int i = 0; i < nRays; i++) {
            final float x = xs[i];
            final float y = ys[i];

            final float r = MathEx.norm2(x, y);
            final float rr = MathEx.sq(r);

            final float xx = MathEx.sq(x);
            final float yy = MathEx.sq(y);
            final float xy = x * y;

            final float dx = tng2 * (rr + 2 * xx) + 2 * tng1 * xy;
            final float dy = tng1 * (rr + 2 * yy) + 2 * tng2 * xy;

            float fact = 1;
            if (radial != null && radial.length > 0) {
                for (int j = 0; j < radial.length; j++) {
                    fact += radial[j] * Math.pow(rr, j + 1);
                }
            }
            xd[i] = fact * x + dx;
            yd[i] = fact * y + dy;
        }

        return new Couple<>(xd, yd);
    }
}
