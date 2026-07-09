// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;

public class Project extends Util {
    private static final float[] IDENTITY_MATRIX = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer finalMatrix = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
    private static final float[] in = new float[4];
    private static final float[] out = new float[4];
    private static final float[] forward = new float[3];
    private static final float[] side = new float[3];
    private static final float[] up = new float[3];

    private static void __gluMakeIdentityf(FloatBuffer floatBuffer) {
        int int0 = floatBuffer.position();
        floatBuffer.put(IDENTITY_MATRIX);
        floatBuffer.position(int0);
    }

    private static void __gluMultMatrixVecf(FloatBuffer floatBuffer, float[] floats0, float[] floats1) {
        for (int int0 = 0; int0 < 4; int0++) {
            floats1[int0] = floats0[0] * floatBuffer.get(floatBuffer.position() + 0 + int0)
                + floats0[1] * floatBuffer.get(floatBuffer.position() + 4 + int0)
                + floats0[2] * floatBuffer.get(floatBuffer.position() + 8 + int0)
                + floats0[3] * floatBuffer.get(floatBuffer.position() + 12 + int0);
        }
    }

    private static boolean __gluInvertMatrixf(FloatBuffer floatBuffer1, FloatBuffer floatBuffer2) {
        FloatBuffer floatBuffer0 = tempMatrix;

        for (int int0 = 0; int0 < 16; int0++) {
            floatBuffer0.put(int0, floatBuffer1.get(int0 + floatBuffer1.position()));
        }

        __gluMakeIdentityf(floatBuffer2);

        for (int int1 = 0; int1 < 4; int1++) {
            int int2 = int1;

            for (int int3 = int1 + 1; int3 < 4; int3++) {
                if (Math.abs(floatBuffer0.get(int3 * 4 + int1)) > Math.abs(floatBuffer0.get(int1 * 4 + int1))) {
                    int2 = int3;
                }
            }

            if (int2 != int1) {
                for (int int4 = 0; int4 < 4; int4++) {
                    float float0 = floatBuffer0.get(int1 * 4 + int4);
                    floatBuffer0.put(int1 * 4 + int4, floatBuffer0.get(int2 * 4 + int4));
                    floatBuffer0.put(int2 * 4 + int4, float0);
                    float0 = floatBuffer2.get(int1 * 4 + int4);
                    floatBuffer2.put(int1 * 4 + int4, floatBuffer2.get(int2 * 4 + int4));
                    floatBuffer2.put(int2 * 4 + int4, float0);
                }
            }

            if (floatBuffer0.get(int1 * 4 + int1) == 0.0F) {
                return false;
            }

            float float1 = floatBuffer0.get(int1 * 4 + int1);

            for (int int5 = 0; int5 < 4; int5++) {
                floatBuffer0.put(int1 * 4 + int5, floatBuffer0.get(int1 * 4 + int5) / float1);
                floatBuffer2.put(int1 * 4 + int5, floatBuffer2.get(int1 * 4 + int5) / float1);
            }

            for (int int6 = 0; int6 < 4; int6++) {
                if (int6 != int1) {
                    float1 = floatBuffer0.get(int6 * 4 + int1);

                    for (int int7 = 0; int7 < 4; int7++) {
                        floatBuffer0.put(int6 * 4 + int7, floatBuffer0.get(int6 * 4 + int7) - floatBuffer0.get(int1 * 4 + int7) * float1);
                        floatBuffer2.put(int6 * 4 + int7, floatBuffer2.get(int6 * 4 + int7) - floatBuffer2.get(int1 * 4 + int7) * float1);
                    }
                }
            }
        }

        return true;
    }

    private static void __gluMultMatricesf(FloatBuffer floatBuffer2, FloatBuffer floatBuffer1, FloatBuffer floatBuffer0) {
        for (int int0 = 0; int0 < 4; int0++) {
            for (int int1 = 0; int1 < 4; int1++) {
                floatBuffer0.put(
                    floatBuffer0.position() + int0 * 4 + int1,
                    floatBuffer2.get(floatBuffer2.position() + int0 * 4 + 0) * floatBuffer1.get(floatBuffer1.position() + 0 + int1)
                        + floatBuffer2.get(floatBuffer2.position() + int0 * 4 + 1) * floatBuffer1.get(floatBuffer1.position() + 4 + int1)
                        + floatBuffer2.get(floatBuffer2.position() + int0 * 4 + 2) * floatBuffer1.get(floatBuffer1.position() + 8 + int1)
                        + floatBuffer2.get(floatBuffer2.position() + int0 * 4 + 3) * floatBuffer1.get(floatBuffer1.position() + 12 + int1)
                );
            }
        }
    }

    public static void gluPerspective(float float1, float float6, float float4, float float3) {
        float float0 = float1 / 2.0F * (float) Math.PI / 180.0F;
        float float2 = float3 - float4;
        float float5 = (float)Math.sin(float0);
        if (float2 != 0.0F && float5 != 0.0F && float6 != 0.0F) {
            float float7 = (float)Math.cos(float0) / float5;
            __gluMakeIdentityf(matrix);
            matrix.put(0, float7 / float6);
            matrix.put(5, float7);
            matrix.put(10, -(float3 + float4) / float2);
            matrix.put(11, -1.0F);
            matrix.put(14, -2.0F * float4 * float3 / float2);
            matrix.put(15, 0.0F);
            GL11.glMultMatrixf(matrix);
        }
    }

    public static void gluLookAt(float float1, float float3, float float5, float float0, float float2, float float4, float float6, float float7, float float8) {
        float[] floats0 = forward;
        float[] floats1 = side;
        float[] floats2 = up;
        floats0[0] = float0 - float1;
        floats0[1] = float2 - float3;
        floats0[2] = float4 - float5;
        floats2[0] = float6;
        floats2[1] = float7;
        floats2[2] = float8;
        normalize(floats0);
        cross(floats0, floats2, floats1);
        normalize(floats1);
        cross(floats1, floats0, floats2);
        __gluMakeIdentityf(matrix);
        matrix.put(0, floats1[0]);
        matrix.put(4, floats1[1]);
        matrix.put(8, floats1[2]);
        matrix.put(1, floats2[0]);
        matrix.put(5, floats2[1]);
        matrix.put(9, floats2[2]);
        matrix.put(2, -floats0[0]);
        matrix.put(6, -floats0[1]);
        matrix.put(10, -floats0[2]);
        GL11.glMultMatrixf(matrix);
        GL11.glTranslatef(-float1, -float3, -float5);
    }

    public static boolean gluProject(
        float float0, float float1, float float2, FloatBuffer floatBuffer0, FloatBuffer floatBuffer1, IntBuffer intBuffer, FloatBuffer floatBuffer2
    ) {
        float[] floats0 = in;
        float[] floats1 = out;
        floats0[0] = float0;
        floats0[1] = float1;
        floats0[2] = float2;
        floats0[3] = 1.0F;
        __gluMultMatrixVecf(floatBuffer0, floats0, floats1);
        __gluMultMatrixVecf(floatBuffer1, floats1, floats0);
        if (floats0[3] == 0.0) {
            return false;
        } else {
            floats0[3] = 1.0F / floats0[3] * 0.5F;
            floats0[0] = floats0[0] * floats0[3] + 0.5F;
            floats0[1] = floats0[1] * floats0[3] + 0.5F;
            floats0[2] = floats0[2] * floats0[3] + 0.5F;
            floatBuffer2.put(0, floats0[0] * intBuffer.get(intBuffer.position() + 2) + intBuffer.get(intBuffer.position() + 0));
            floatBuffer2.put(1, floats0[1] * intBuffer.get(intBuffer.position() + 3) + intBuffer.get(intBuffer.position() + 1));
            floatBuffer2.put(2, floats0[2]);
            return true;
        }
    }

    public static boolean gluUnProject(
        float float0, float float1, float float2, FloatBuffer floatBuffer0, FloatBuffer floatBuffer1, IntBuffer intBuffer, FloatBuffer floatBuffer2
    ) {
        float[] floats0 = in;
        float[] floats1 = out;
        __gluMultMatricesf(floatBuffer0, floatBuffer1, finalMatrix);
        if (!__gluInvertMatrixf(finalMatrix, finalMatrix)) {
            return false;
        } else {
            floats0[0] = float0;
            floats0[1] = float1;
            floats0[2] = float2;
            floats0[3] = 1.0F;
            floats0[0] = (floats0[0] - intBuffer.get(intBuffer.position() + 0)) / intBuffer.get(intBuffer.position() + 2);
            floats0[1] = (floats0[1] - intBuffer.get(intBuffer.position() + 1)) / intBuffer.get(intBuffer.position() + 3);
            floats0[0] = floats0[0] * 2.0F - 1.0F;
            floats0[1] = floats0[1] * 2.0F - 1.0F;
            floats0[2] = floats0[2] * 2.0F - 1.0F;
            __gluMultMatrixVecf(finalMatrix, floats0, floats1);
            if (floats1[3] == 0.0) {
                return false;
            } else {
                floats1[3] = 1.0F / floats1[3];
                floatBuffer2.put(floatBuffer2.position() + 0, floats1[0] * floats1[3]);
                floatBuffer2.put(floatBuffer2.position() + 1, floats1[1] * floats1[3]);
                floatBuffer2.put(floatBuffer2.position() + 2, floats1[2] * floats1[3]);
                return true;
            }
        }
    }

    public static void gluPickMatrix(float float3, float float2, float float1, float float0, IntBuffer intBuffer) {
        if (!(float1 <= 0.0F) && !(float0 <= 0.0F)) {
            GL11.glTranslatef(
                (intBuffer.get(intBuffer.position() + 2) - 2.0F * (float3 - intBuffer.get(intBuffer.position() + 0))) / float1,
                (intBuffer.get(intBuffer.position() + 3) - 2.0F * (float2 - intBuffer.get(intBuffer.position() + 1))) / float0,
                0.0F
            );
            GL11.glScalef(intBuffer.get(intBuffer.position() + 2) / float1, intBuffer.get(intBuffer.position() + 3) / float0, 1.0F);
        }
    }
}
