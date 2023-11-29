package eu.brnt.qualibration.util;

import boofcv.struct.image.InterleavedF32;
import boofcv.struct.image.InterleavedU8;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Mainly interpolation on images.
 */
public final class ImageEx {

    public static List<float[]> bilinear(InterleavedF32 img, List<Float> x, List<Float> y) {
        float[] defaultValue = new float[img.getNumBands()];
        return bilinear(img, x, y, defaultValue);
    }

    public static float[][] bilinear(InterleavedF32 img, float[] x, float[] y) {
        float[] defaultValue = new float[img.getNumBands()];
        return bilinear(img, x, y, defaultValue);
    }

    public static float[][] bilinearU8(InterleavedU8 img, float[] x, float[] y) {
        float[] defaultValue = new float[img.getNumBands()];
        return bilinearU8(img, x, y, defaultValue);
    }

    public static float[][] bilinear(InterleavedF32 img, float[] x, float[] y, float[] defaultValue) {
        final int nPoints = x.length;

        final int nBands = img.getNumBands();

        final int width = img.getWidth();
        final int height = img.getHeight();

        float[][] values = new float[nPoints][nBands];

        float[] v00 = new float[nBands];
        float[] v01 = new float[nBands];
        float[] v10 = new float[nBands];
        float[] v11 = new float[nBands];

        for (int i = 0; i < nPoints; i++) {
            float cx = x[i];
            int rx = (int) cx;
            cx -= rx;
            float omcx = 1.0f - cx;

            float cy = y[i];
            int ry = (int) cy;
            cy -= ry;

            if ((rx >= 0) && (rx < width - 1) && (ry >= 0) && (ry < height - 1)) {
                // This is the general case (i.e. the 'middle' of the image)
                // The other cases are deported in the function 'other_cases'
                // (it makes the inner code of the loop smaller and consequently
                // reduces the number of 'instruction cache misses')

                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx + 1, ry, v10);
                img.unsafe_get(rx, ry + 1, v01);
                img.unsafe_get(rx + 1, ry + 1, v11);

                for (int j = 0; j < nBands; j++) {
                    values[i][j] = (v00[j] * omcx + v10[j] * cx) * (1.0f - cy) + (v01[j] * omcx + v11[j] * cx) * cy;
                }
            } else {
                otherCases(img, width, height, nBands, defaultValue, rx, ry, cx, cy, values[i]);
            }
        }

        return values;
    }

    public static float[][] bilinearU8(InterleavedU8 img, float[] x, float[] y, float[] defaultValue) {
        final int nPoints = x.length;

        final int nBands = img.getNumBands();

        final int width = img.getWidth();
        final int height = img.getHeight();

        float[][] values = new float[nPoints][nBands];

        int[] v00 = new int[nBands];
        int[] v01 = new int[nBands];
        int[] v10 = new int[nBands];
        int[] v11 = new int[nBands];

        for (int i = 0; i < nPoints; i++) {
            float cx = x[i];
            int rx = (int) cx;
            cx -= rx;
            float omcx = 1.0f - cx;

            float cy = y[i];
            int ry = (int) cy;
            cy -= ry;

            if ((rx >= 0) && (rx < width - 1) && (ry >= 0) && (ry < height - 1)) {
                // This is the general case (i.e. the 'middle' of the image)
                // The other cases are deported in the function 'other_cases'
                // (it makes the inner code of the loop smaller and consequently
                // reduces the number of 'instruction cache misses')

                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx + 1, ry, v10);
                img.unsafe_get(rx, ry + 1, v01);
                img.unsafe_get(rx + 1, ry + 1, v11);

                for (int j = 0; j < nBands; j++) {
                    values[i][j] = (v00[j] * omcx + v10[j] * cx) * (1.0f - cy) + (v01[j] * omcx + v11[j] * cx) * cy;
                }
            } else {
                otherCasesU8(img, width, height, nBands, defaultValue, rx, ry, cx, cy, values[i]);
            }
        }

        return values;
    }

    public static List<float[]> bilinear(InterleavedF32 img, List<Float> x, List<Float> y, float[] defaultValue) {
        List<float[]> values = new LinkedList<>();

        final int width = img.getWidth();
        final int height = img.getHeight();

        final int nBands = img.getNumBands();

        float[] v00 = new float[nBands];
        float[] v01 = new float[nBands];
        float[] v10 = new float[nBands];
        float[] v11 = new float[nBands];

        Iterator<Float> itX = x.iterator();
        Iterator<Float> itY = y.iterator();

        while (itX.hasNext()) {
            float cx = itX.next();
            int rx = (int) cx;
            cx -= rx;
            float omcx = 1.0f - cx;
            //rx--;

            float cy = itY.next();
            int ry = (int) cy;
            cy -= ry;
            //ry--;

            float[] value = new float[nBands];

            if ((rx >= 0) && (rx < width - 1) && (ry >= 0) && (ry < height - 1)) {
                // This is the general case (i.e. the 'middle' of the image)
                // The other cases are deported in the function 'other_cases'
                // (it makes the inner code of the loop smaller and consequently
                // reduces the number of 'instruction cache misses')

                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx + 1, ry, v10);
                img.unsafe_get(rx, ry + 1, v01);
                img.unsafe_get(rx + 1, ry + 1, v11);

                for (int i = 0; i < nBands; i++) {
                    value[i] = (v00[i] * omcx + v10[i] * cx) * (1.0f - cy) + (v01[i] * omcx + v11[i] * cx) * cy;
                }
            } else {
                otherCases(img, width, height, nBands, defaultValue, rx, ry, cx, cy, value);
            }
            values.add(value);
        }

        return values;
    }

    private static void otherCases(InterleavedF32 img, int width, int height, int nBands, float[] defaultValue, int rx, int ry, float cx, float cy, float[] value) {
        if ((rx == width - 1) && (ry < height - 1)) {
            // Right border
            if (cx > 0) {
                copyValue(defaultValue, value, nBands);
            } else {
                float[] v00 = new float[nBands];
                float[] v01 = new float[nBands];
                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx, ry + 1, v01);
                for (int i = 0; i < nBands; i++) {
                    value[i] = v00[i] * (1.0f - cy) + v01[i] * cy;
                }
            }

        } else if ((rx < width - 1) && (ry == height - 1)) {
            // Bottom border
            if (cy > 0) {
                copyValue(defaultValue, value, nBands);
            } else {
                float[] v00 = new float[nBands];
                float[] v10 = new float[nBands];
                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx + 1, ry, v10);
                for (int i = 0; i < nBands; i++) {
                    value[i] = v00[i] * (1.0f - cx) + v10[i] * cx;
                }
            }

        } else if ((rx == width - 1) && (ry == height - 1)) {
            // Bottom right pixel
            if ((cx > 0) || (cy > 0)) {
                copyValue(defaultValue, value, nBands);
            } else {
                img.unsafe_get(rx, ry, value);
            }

        } else {
            // Default value out of the bounds
            copyValue(defaultValue, value, nBands);
        }
    }

    private static void otherCasesU8(InterleavedU8 img, int width, int height, int nBands, float[] defaultValue, int rx, int ry, float cx, float cy, float[] value) {
        if ((rx == width - 1) && (ry < height - 1)) {
            // Right border
            if (cx > 0) {
                copyValue(defaultValue, value, nBands);
            } else {
                int[] v00 = new int[nBands];
                int[] v01 = new int[nBands];
                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx, ry + 1, v01);
                for (int i = 0; i < nBands; i++) {
                    value[i] = v00[i] * (1.0f - cy) + v01[i] * cy;
                }
            }

        } else if ((rx < width - 1) && (ry == height - 1)) {
            // Bottom border
            if (cy > 0) {
                copyValue(defaultValue, value, nBands);
            } else {
                int[] v00 = new int[nBands];
                int[] v10 = new int[nBands];
                img.unsafe_get(rx, ry, v00);
                img.unsafe_get(rx + 1, ry, v10);
                for (int i = 0; i < nBands; i++) {
                    value[i] = v00[i] * (1.0f - cx) + v10[i] * cx;
                }
            }

        } else if ((rx == width - 1) && (ry == height - 1)) {
            // Bottom right pixel
            if ((cx > 0) || (cy > 0)) {
                copyValue(defaultValue, value, nBands);
            } else {
                int[] v = new int[nBands];
                img.unsafe_get(rx, ry, v);
                for (int i = 0; i < nBands; i++) {
                    value[i] = v[i];
                }
            }

        } else {
            // Default value out of the bounds
            copyValue(defaultValue, value, nBands);
        }
    }

    private static void copyValue(float[] from, float[] to, int nBands) {
        System.arraycopy(from, 0, to, 0, nBands);
    }

    public static byte[] bilinearU8_BGR(byte[] imgData, int imgWidth, int imgHeight, int imgStride, float[] x, float[] y) {
        byte[] defaultValue = new byte[]{0, 0, 0};
        return bilinearU8_BGR(imgData, imgWidth, imgHeight, imgStride, x, y, defaultValue);
    }

    @SuppressWarnings({"DuplicatedCode", "UnnecessaryLocalVariable"})
    public static byte[] bilinearU8_BGR(byte[] imgData, int imgWidth, int imgHeight, int imgStride, float[] x, float[] y, byte[] defaultValue) {
        final int B = 0;
        final int G = 1;
        final int R = 2;

        final int nPoints = x.length;

        final int width = imgWidth;
        final int height = imgHeight;
        final int stride = imgStride;

        byte[] values = new byte[3 * nPoints];

        int v00b, v01b, v10b, v11b;
        int v00g, v01g, v10g, v11g;
        int v00r, v01r, v10r, v11r;

        byte[] bgr = new byte[3];

        for (int i = 0; i < nPoints; i++) {
            float cx = x[i];
            int rx = (int) cx;
            cx -= rx;
            float omcx = 1.0f - cx;

            float cy = y[i];
            int ry = (int) cy;
            cy -= ry;

            if ((rx >= 0) && (rx < width - 1) && (ry >= 0) && (ry < height - 1)) {
                // This is the general case (i.e. the 'middle' of the image)
                // The other cases are deported in the function 'other_cases'
                // (it makes the inner code of the loop smaller and consequently
                // reduces the number of 'instruction cache misses')

                v00b = ((int) imgData[ry * stride + 3 * rx + B]) & 0xFF;
                v00g = ((int) imgData[ry * stride + 3 * rx + G]) & 0xFF;
                v00r = ((int) imgData[ry * stride + 3 * rx + R]) & 0xFF;

                v10b = ((int) imgData[ry * stride + 3 * (rx + 1) + B]) & 0xFF;
                v10g = ((int) imgData[ry * stride + 3 * (rx + 1) + G]) & 0xFF;
                v10r = ((int) imgData[ry * stride + 3 * (rx + 1) + R]) & 0xFF;

                v01b = ((int) imgData[(ry + 1) * stride + 3 * rx + B]) & 0xFF;
                v01g = ((int) imgData[(ry + 1) * stride + 3 * rx + G]) & 0xFF;
                v01r = ((int) imgData[(ry + 1) * stride + 3 * rx + R]) & 0xFF;

                v11b = ((int) imgData[(ry + 1) * stride + 3 * (rx + 1) + B]) & 0xFF;
                v11g = ((int) imgData[(ry + 1) * stride + 3 * (rx + 1) + G]) & 0xFF;
                v11r = ((int) imgData[(ry + 1) * stride + 3 * (rx + 1) + R]) & 0xFF;

                values[3 * i + B] = (byte) ((int) ((v00b * omcx + v10b * cx) * (1.0f - cy) + (v01b * omcx + v11b * cx) * cy));
                values[3 * i + G] = (byte) ((int) ((v00g * omcx + v10g * cx) * (1.0f - cy) + (v01g * omcx + v11g * cx) * cy));
                values[3 * i + R] = (byte) ((int) ((v00r * omcx + v10r * cx) * (1.0f - cy) + (v01r * omcx + v11r * cx) * cy));
            } else {
                otherCasesU8_BGR(imgData, width, height, stride, defaultValue, rx, ry, cx, cy, bgr);
                values[3 * i + B] = bgr[B];
                values[3 * i + G] = bgr[G];
                values[3 * i + R] = bgr[R];
            }
        }

        return values;
    }

    @SuppressWarnings({"DuplicateExpressions", "DuplicatedCode"})
    private static void otherCasesU8_BGR(byte[] imgData, int width, int height, int stride, byte[] defaultValue, int rx, int ry, float cx, float cy, byte[] value) {
        final int B = 0;
        final int G = 1;
        final int R = 2;

        if ((rx == width - 1) && (ry < height - 1)) {
            // Right border
            if (cx > 0) {
                value[B] = defaultValue[B];
                value[G] = defaultValue[G];
                value[R] = defaultValue[R];
            } else {
                int v00b = ((int) imgData[ry * stride + 3 * rx + B]) & 0xFF;
                int v00g = ((int) imgData[ry * stride + 3 * rx + G]) & 0xFF;
                int v00r = ((int) imgData[ry * stride + 3 * rx + R]) & 0xFF;

                int v01b = ((int) imgData[(ry + 1) * stride + 3 * rx + B]) & 0xFF;
                int v01g = ((int) imgData[(ry + 1) * stride + 3 * rx + G]) & 0xFF;
                int v01r = ((int) imgData[(ry + 1) * stride + 3 * rx + R]) & 0xFF;

                value[B] = (byte) (int) (v00b * (1.0f - cy) + v01b * cy);
                value[G] = (byte) (int) (v00g * (1.0f - cy) + v01g * cy);
                value[R] = (byte) (int) (v00r * (1.0f - cy) + v01r * cy);
            }

        } else if ((rx < width - 1) && (ry == height - 1)) {
            // Bottom border
            if (cy > 0) {
                value[B] = defaultValue[B];
                value[G] = defaultValue[G];
                value[R] = defaultValue[R];
            } else {
                int v00b = ((int) imgData[ry * stride + 3 * rx + B]) & 0xFF;
                int v00g = ((int) imgData[ry * stride + 3 * rx + G]) & 0xFF;
                int v00r = ((int) imgData[ry * stride + 3 * rx + R]) & 0xFF;

                int v10b = ((int) imgData[ry * stride + 3 * (rx + 1) + B]) & 0xFF;
                int v10g = ((int) imgData[ry * stride + 3 * (rx + 1) + G]) & 0xFF;
                int v10r = ((int) imgData[ry * stride + 3 * (rx + 1) + R]) & 0xFF;

                value[B] = (byte) (int) (v00b * (1.0f - cx) + v10b * cx);
                value[G] = (byte) (int) (v00g * (1.0f - cx) + v10g * cx);
                value[R] = (byte) (int) (v00r * (1.0f - cx) + v10r * cx);
            }

        } else if ((rx == width - 1) && (ry == height - 1)) {
            // Bottom right pixel
            if ((cx > 0) || (cy > 0)) {
                value[B] = defaultValue[B];
                value[G] = defaultValue[G];
                value[R] = defaultValue[R];
            } else {
                value[B] = imgData[ry * stride + 3 * rx + B];
                value[G] = imgData[ry * stride + 3 * rx + G];
                value[R] = imgData[ry * stride + 3 * rx + R];
            }

        } else {
            // Default value out of the bounds
            value[B] = defaultValue[B];
            value[G] = defaultValue[G];
            value[R] = defaultValue[R];
        }
    }
}
