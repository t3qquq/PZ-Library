// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Lpc {
    Drft fft = new Drft();
    int ln;
    int m;

    static float FAST_HYPOT(float float1, float float0) {
        return (float)Math.sqrt(float1 * float1 + float0 * float0);
    }

    static float lpc_from_data(float[] floats1, float[] floats2, int int3, int int0) {
        float[] floats0 = new float[int0 + 1];
        int int1 = int0 + 1;

        while (int1-- != 0) {
            float float0 = 0.0F;

            for (int int2 = int1; int2 < int3; int2++) {
                float0 += floats1[int2] * floats1[int2 - int1];
            }

            floats0[int1] = float0;
        }

        float float1 = floats0[0];

        for (int int4 = 0; int4 < int0; int4++) {
            float float2 = -floats0[int4 + 1];
            if (float1 == 0.0F) {
                for (int int5 = 0; int5 < int0; int5++) {
                    floats2[int5] = 0.0F;
                }

                return 0.0F;
            }

            for (int int6 = 0; int6 < int4; int6++) {
                float2 -= floats2[int6] * floats0[int4 - int6];
            }

            float2 /= float1;
            floats2[int4] = float2;

            for (int1 = 0; int1 < int4 / 2; int1++) {
                float float3 = floats2[int1];
                floats2[int1] += float2 * floats2[int4 - 1 - int1];
                floats2[int4 - 1 - int1] = floats2[int4 - 1 - int1] + float2 * float3;
            }

            if (int4 % 2 != 0) {
                floats2[int1] += floats2[int1] * float2;
            }

            float1 = (float)(float1 * (1.0 - float2 * float2));
        }

        return float1;
    }

    void clear() {
        this.fft.clear();
    }

    void init(int int0, int int1) {
        this.ln = int0;
        this.m = int1;
        this.fft.init(int0 * 2);
    }

    float lpc_from_curve(float[] floats1, float[] floats2) {
        int int0 = this.ln;
        float[] floats0 = new float[int0 + int0];
        float float0 = (float)(0.5 / int0);

        for (int int1 = 0; int1 < int0; int1++) {
            floats0[int1 * 2] = floats1[int1] * float0;
            floats0[int1 * 2 + 1] = 0.0F;
        }

        floats0[int0 * 2 - 1] = floats1[int0 - 1] * float0;
        int0 *= 2;
        this.fft.backward(floats0);
        int int2 = 0;
        int int3 = int0 / 2;

        while (int2 < int0 / 2) {
            float float1 = floats0[int2];
            floats0[int2++] = floats0[int3];
            floats0[int3++] = float1;
        }

        return lpc_from_data(floats0, floats2, int0, this.m);
    }

    void lpc_to_curve(float[] floats0, float[] floats1, float float0) {
        for (int int0 = 0; int0 < this.ln * 2; int0++) {
            floats0[int0] = 0.0F;
        }

        if (float0 != 0.0F) {
            for (int int1 = 0; int1 < this.m; int1++) {
                floats0[int1 * 2 + 1] = floats1[int1] / (4.0F * float0);
                floats0[int1 * 2 + 2] = -floats1[int1] / (4.0F * float0);
            }

            this.fft.backward(floats0);
            int int2 = this.ln * 2;
            float float1 = (float)(1.0 / float0);
            floats0[0] = (float)(1.0 / (floats0[0] * 2.0F + float1));

            for (int int3 = 1; int3 < this.ln; int3++) {
                float float2 = floats0[int3] + floats0[int2 - int3];
                float float3 = floats0[int3] - floats0[int2 - int3];
                float float4 = float2 + float1;
                floats0[int3] = (float)(1.0 / FAST_HYPOT(float4, float3));
            }
        }
    }
}
