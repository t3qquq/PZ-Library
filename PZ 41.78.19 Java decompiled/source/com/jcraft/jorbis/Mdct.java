// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Mdct {
    float[] _w = new float[1024];
    float[] _x = new float[1024];
    int[] bitrev;
    int log2n;
    int n;
    float scale;
    float[] trig;

    synchronized void backward(float[] floats2, float[] floats4) {
        if (this._x.length < this.n / 2) {
            this._x = new float[this.n / 2];
        }

        if (this._w.length < this.n / 2) {
            this._w = new float[this.n / 2];
        }

        float[] floats0 = this._x;
        float[] floats1 = this._w;
        int int0 = this.n >>> 1;
        int int1 = this.n >>> 2;
        int int2 = this.n >>> 3;
        int int3 = 1;
        int int4 = 0;
        int int5 = int0;

        for (int int6 = 0; int6 < int2; int6++) {
            int5 -= 2;
            floats0[int4++] = -floats2[int3 + 2] * this.trig[int5 + 1] - floats2[int3] * this.trig[int5];
            floats0[int4++] = floats2[int3] * this.trig[int5 + 1] - floats2[int3 + 2] * this.trig[int5];
            int3 += 4;
        }

        int3 = int0 - 4;

        for (int int7 = 0; int7 < int2; int7++) {
            int5 -= 2;
            floats0[int4++] = floats2[int3] * this.trig[int5 + 1] + floats2[int3 + 2] * this.trig[int5];
            floats0[int4++] = floats2[int3] * this.trig[int5] - floats2[int3 + 2] * this.trig[int5 + 1];
            int3 -= 4;
        }

        float[] floats3 = this.mdct_kernel(floats0, floats1, this.n, int0, int1, int2);
        byte byte0 = 0;
        int5 = int0;
        int int8 = int1;
        int int9 = int1 - 1;
        int int10 = int1 + int0;
        int int11 = int10 - 1;

        for (int int12 = 0; int12 < int1; int12++) {
            float float0 = floats3[byte0] * this.trig[int5 + 1] - floats3[byte0 + 1] * this.trig[int5];
            float float1 = -(floats3[byte0] * this.trig[int5] + floats3[byte0 + 1] * this.trig[int5 + 1]);
            floats4[int8] = -float0;
            floats4[int9] = float0;
            floats4[int10] = float1;
            floats4[int11] = float1;
            int8++;
            int9--;
            int10++;
            int11--;
            byte0 += 2;
            int5 += 2;
        }
    }

    void clear() {
    }

    void forward(float[] var1, float[] var2) {
    }

    void init(int int0) {
        this.bitrev = new int[int0 / 4];
        this.trig = new float[int0 + int0 / 4];
        this.log2n = (int)Math.rint(Math.log(int0) / Math.log(2.0));
        this.n = int0;
        byte byte0 = 0;
        byte byte1 = 1;
        int int1 = byte0 + int0 / 2;
        int int2 = int1 + 1;
        int int3 = int1 + int0 / 2;
        int int4 = int3 + 1;

        for (int int5 = 0; int5 < int0 / 4; int5++) {
            this.trig[byte0 + int5 * 2] = (float)Math.cos(Math.PI / int0 * (4 * int5));
            this.trig[byte1 + int5 * 2] = (float)(-Math.sin(Math.PI / int0 * (4 * int5)));
            this.trig[int1 + int5 * 2] = (float)Math.cos(Math.PI / (2 * int0) * (2 * int5 + 1));
            this.trig[int2 + int5 * 2] = (float)Math.sin(Math.PI / (2 * int0) * (2 * int5 + 1));
        }

        for (int int6 = 0; int6 < int0 / 8; int6++) {
            this.trig[int3 + int6 * 2] = (float)Math.cos(Math.PI / int0 * (4 * int6 + 2));
            this.trig[int4 + int6 * 2] = (float)(-Math.sin(Math.PI / int0 * (4 * int6 + 2)));
        }

        int int7 = (1 << this.log2n - 1) - 1;
        int int8 = 1 << this.log2n - 2;

        for (int int9 = 0; int9 < int0 / 8; int9++) {
            int int10 = 0;

            for (int int11 = 0; int8 >>> int11 != 0; int11++) {
                if ((int8 >>> int11 & int9) != 0) {
                    int10 |= 1 << int11;
                }
            }

            this.bitrev[int9 * 2] = ~int10 & int7;
            this.bitrev[int9 * 2 + 1] = int10;
        }

        this.scale = 4.0F / int0;
    }

    private float[] mdct_kernel(float[] floats0, float[] floats1, int int9, int int5, int int1, int int20) {
        int int0 = int1;
        int int2 = 0;
        int int3 = int1;
        int int4 = int5;

        for (int int6 = 0; int6 < int1; int6++) {
            float float0 = floats0[int0] - floats0[int2];
            floats1[int3 + int6] = floats0[int0++] + floats0[int2++];
            float float1 = floats0[int0] - floats0[int2];
            int4 -= 4;
            floats1[int6++] = float0 * this.trig[int4] + float1 * this.trig[int4 + 1];
            floats1[int6] = float1 * this.trig[int4] - float0 * this.trig[int4 + 1];
            floats1[int3 + int6] = floats0[int0++] + floats0[int2++];
        }

        for (int int7 = 0; int7 < this.log2n - 3; int7++) {
            int int8 = int9 >>> int7 + 2;
            int int10 = 1 << int7 + 3;
            int int11 = int5 - 2;
            int4 = 0;

            for (int int12 = 0; int12 < int8 >>> 2; int12++) {
                int int13 = int11;
                int3 = int11 - (int8 >> 1);
                float float2 = this.trig[int4];
                float float3 = this.trig[int4 + 1];
                int11 -= 2;
                int8++;

                for (int int14 = 0; int14 < 2 << int7; int14++) {
                    float float4 = floats1[int13] - floats1[int3];
                    floats0[int13] = floats1[int13] + floats1[int3];
                    float float5 = floats1[++int13];
                    int3++;
                    float float6 = float5 - floats1[int3];
                    floats0[int13] = floats1[int13] + floats1[int3];
                    floats0[int3] = float6 * float2 - float4 * float3;
                    floats0[int3 - 1] = float4 * float2 + float6 * float3;
                    int13 -= int8;
                    int3 -= int8;
                }

                int8--;
                int4 += int10;
            }

            float[] floats2 = floats1;
            floats1 = floats0;
            floats0 = floats2;
        }

        int int15 = int9;
        int int16 = 0;
        int int17 = 0;
        int int18 = int5 - 1;

        for (int int19 = 0; int19 < int20; int19++) {
            int int21 = this.bitrev[int16++];
            int int22 = this.bitrev[int16++];
            float float7 = floats1[int21] - floats1[int22 + 1];
            float float8 = floats1[int21 - 1] + floats1[int22];
            float float9 = floats1[int21] + floats1[int22 + 1];
            float float10 = floats1[int21 - 1] - floats1[int22];
            float float11 = float7 * this.trig[int15];
            float float12 = float8 * this.trig[int15++];
            float float13 = float7 * this.trig[int15];
            float float14 = float8 * this.trig[int15++];
            floats0[int17++] = (float9 + float13 + float12) * 0.5F;
            floats0[int18--] = (-float10 + float14 - float11) * 0.5F;
            floats0[int17++] = (float10 + float14 - float11) * 0.5F;
            floats0[int18--] = (float9 - float13 - float12) * 0.5F;
        }

        return floats0;
    }
}
