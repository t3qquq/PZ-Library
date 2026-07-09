// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Lsp {
    static final float M_PI = (float) Math.PI;

    static void lsp_to_curve(float[] floats1, int[] ints, int int5, int int0, float[] floats0, int int2, float float5, float float4) {
        float float0 = (float) Math.PI / int0;

        for (int int1 = 0; int1 < int2; int1++) {
            floats0[int1] = Lookup.coslook(floats0[int1]);
        }

        int int3 = int2 / 2 * 2;
        int int4 = 0;

        while (int4 < int5) {
            int int6 = ints[int4];
            float float1 = 0.70710677F;
            float float2 = 0.70710677F;
            float float3 = Lookup.coslook(float0 * int6);

            for (byte byte0 = 0; byte0 < int3; byte0 += 2) {
                float2 *= floats0[byte0] - float3;
                float1 *= floats0[byte0 + 1] - float3;
            }

            if ((int2 & 1) != 0) {
                float2 *= floats0[int2 - 1] - float3;
                float2 *= float2;
                float1 *= float1 * (1.0F - float3 * float3);
            } else {
                float2 *= float2 * (1.0F + float3);
                float1 *= float1 * (1.0F - float3);
            }

            float2 = float1 + float2;
            int int7 = Float.floatToIntBits(float2);
            int int8 = 2147483647 & int7;
            int int9 = 0;
            if (int8 < 2139095040 && int8 != 0) {
                if (int8 < 8388608) {
                    float2 = (float)(float2 * 3.3554432E7);
                    int7 = Float.floatToIntBits(float2);
                    int8 = 2147483647 & int7;
                    int9 = -25;
                }

                int9 += (int8 >>> 23) - 126;
                int7 = int7 & -2139095041 | 1056964608;
                float2 = Float.intBitsToFloat(int7);
            }

            float2 = Lookup.fromdBlook(float5 * Lookup.invsqlook(float2) * Lookup.invsq2explook(int9 + int2) - float4);

            do {
                floats1[int4++] *= float2;
            } while (int4 >= int5 || ints[int4] != int6);
        }
    }
}
