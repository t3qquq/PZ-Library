// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

import java.nio.FloatBuffer;

public class Convolution {
    public static void gaussianKernel(int int0, int int1, float float2, FloatBuffer floatBuffer) {
        if ((int0 & 1) == 0) {
            throw new IllegalArgumentException("rows must be an odd number");
        } else if ((int1 & 1) == 0) {
            throw new IllegalArgumentException("cols must be an odd number");
        } else if (floatBuffer == null) {
            throw new IllegalArgumentException("dest must not be null");
        } else if (floatBuffer.remaining() < int0 * int1) {
            throw new IllegalArgumentException("dest must have at least " + int0 * int1 + " remaining values");
        } else {
            float float0 = 0.0F;
            int int2 = floatBuffer.position();
            int int3 = 0;

            for (int int4 = -(int0 - 1) / 2; int4 <= (int0 - 1) / 2; int4++) {
                for (int int5 = -(int1 - 1) / 2; int5 <= (int1 - 1) / 2; int3++) {
                    float float1 = (float)org.joml.Math.exp(-(int4 * int4 + int5 * int5) / (2.0 * float2 * float2));
                    floatBuffer.put(int2 + int3, float1);
                    float0 += float1;
                    int5++;
                }
            }

            for (int int6 = 0; int6 < int0 * int1; int6++) {
                floatBuffer.put(int2 + int6, floatBuffer.get(int2 + int6) / float0);
            }
        }
    }

    public static void gaussianKernel(int int0, int int1, float float2, float[] floats) {
        if ((int0 & 1) == 0) {
            throw new IllegalArgumentException("rows must be an odd number");
        } else if ((int1 & 1) == 0) {
            throw new IllegalArgumentException("cols must be an odd number");
        } else if (floats == null) {
            throw new IllegalArgumentException("dest must not be null");
        } else if (floats.length < int0 * int1) {
            throw new IllegalArgumentException("dest must have a size of at least " + int0 * int1);
        } else {
            float float0 = 0.0F;
            int int2 = 0;

            for (int int3 = -(int0 - 1) / 2; int3 <= (int0 - 1) / 2; int3++) {
                for (int int4 = -(int1 - 1) / 2; int4 <= (int1 - 1) / 2; int2++) {
                    float float1 = (float)org.joml.Math.exp(-(int3 * int3 + int4 * int4) / (2.0 * float2 * float2));
                    floats[int2] = float1;
                    float0 += float1;
                    int4++;
                }
            }

            for (int int5 = 0; int5 < int0 * int1; int5++) {
                floats[int5] /= float0;
            }
        }
    }
}
