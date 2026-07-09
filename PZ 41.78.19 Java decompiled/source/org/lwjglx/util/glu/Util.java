// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

public class Util {
    protected static int ceil(int int0, int int1) {
        return int0 % int1 == 0 ? int0 / int1 : int0 / int1 + 1;
    }

    protected static float[] normalize(float[] floats) {
        float float0 = (float)Math.sqrt(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
        if (float0 == 0.0) {
            return floats;
        } else {
            float0 = 1.0F / float0;
            floats[0] *= float0;
            floats[1] *= float0;
            floats[2] *= float0;
            return floats;
        }
    }

    protected static void cross(float[] floats1, float[] floats0, float[] floats2) {
        floats2[0] = floats1[1] * floats0[2] - floats1[2] * floats0[1];
        floats2[1] = floats1[2] * floats0[0] - floats1[0] * floats0[2];
        floats2[2] = floats1[0] * floats0[1] - floats1[1] * floats0[0];
    }

    protected static int compPerPix(int int0) {
        switch (int0) {
            case 6400:
            case 6401:
            case 6402:
            case 6403:
            case 6404:
            case 6405:
            case 6406:
            case 6409:
                return 1;
            case 6407:
            case 32992:
                return 3;
            case 6408:
            case 32993:
                return 4;
            case 6410:
                return 2;
            default:
                return -1;
        }
    }

    protected static int nearestPower(int int0) {
        byte byte0 = 1;
        if (int0 == 0) {
            return -1;
        } else {
            while (int0 != 1) {
                if (int0 == 3) {
                    return byte0 << 2;
                }

                int0 >>= 1;
                byte0 <<= 1;
            }

            return byte0;
        }
    }

    protected static int bytesPerPixel(int int0, int int1) {
        byte byte0 = switch (int0) {
            case 6400, 6401, 6402, 6403, 6404, 6405, 6406, 6409 -> 1;
            case 6407, 32992 -> 3;
            case 6408, 32993 -> 4;
            case 6410 -> 2;
            default -> 0;
        };

        return byte0 * switch (int1) {
            case 5120 -> 1;
            case 5121 -> 1;
            case 5122 -> 2;
            case 5123 -> 2;
            case 5124 -> 4;
            case 5125 -> 4;
            case 5126 -> 4;
            case 6656 -> 1;
            default -> 0;
        };
    }
}
