// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

class MdctFloat {
    private static final float cPI3_8 = 0.38268343F;
    private static final float cPI2_8 = 0.70710677F;
    private static final float cPI1_8 = 0.9238795F;
    private int n;
    private int log2n;
    private float[] trig;
    private int[] bitrev;
    private float[] equalizer;
    private float scale;
    private int itmp1;
    private int itmp2;
    private int itmp3;
    private int itmp4;
    private int itmp5;
    private int itmp6;
    private int itmp7;
    private int itmp8;
    private int itmp9;
    private float dtmp1;
    private float dtmp2;
    private float dtmp3;
    private float dtmp4;
    private float dtmp5;
    private float dtmp6;
    private float dtmp7;
    private float dtmp8;
    private float dtmp9;
    private float[] _x = new float[1024];
    private float[] _w = new float[1024];

    protected MdctFloat(int int0) {
        this.bitrev = new int[int0 / 4];
        this.trig = new float[int0 + int0 / 4];
        int int1 = int0 >>> 1;
        this.log2n = (int)Math.rint(Math.log(int0) / Math.log(2.0));
        this.n = int0;
        byte byte0 = 0;
        byte byte1 = 1;
        int int2 = byte0 + int0 / 2;
        int int3 = int2 + 1;
        int int4 = int2 + int0 / 2;
        int int5 = int4 + 1;

        for (int int6 = 0; int6 < int0 / 4; int6++) {
            this.trig[byte0 + int6 * 2] = (float)Math.cos(Math.PI / int0 * (4 * int6));
            this.trig[byte1 + int6 * 2] = (float)(-Math.sin(Math.PI / int0 * (4 * int6)));
            this.trig[int2 + int6 * 2] = (float)Math.cos(Math.PI / (2 * int0) * (2 * int6 + 1));
            this.trig[int3 + int6 * 2] = (float)Math.sin(Math.PI / (2 * int0) * (2 * int6 + 1));
        }

        for (int int7 = 0; int7 < int0 / 8; int7++) {
            this.trig[int4 + int7 * 2] = (float)Math.cos(Math.PI / int0 * (4 * int7 + 2));
            this.trig[int5 + int7 * 2] = (float)(-Math.sin(Math.PI / int0 * (4 * int7 + 2)));
        }

        int int8 = (1 << this.log2n - 1) - 1;
        int int9 = 1 << this.log2n - 2;

        for (int int10 = 0; int10 < int0 / 8; int10++) {
            int int11 = 0;

            for (int int12 = 0; int9 >>> int12 != 0; int12++) {
                if ((int9 >>> int12 & int10) != 0) {
                    int11 |= 1 << int12;
                }
            }

            this.bitrev[int10 * 2] = ~int11 & int8;
            this.bitrev[int10 * 2 + 1] = int11;
        }

        this.scale = 4.0F / int0;
    }

    protected void setEqualizer(float[] floats) {
        this.equalizer = floats;
    }

    protected float[] getEqualizer() {
        return this.equalizer;
    }

    protected synchronized void imdct(float[] floats1, float[] floats5, int[] ints) {
        float[] floats0 = floats1;
        if (this._x.length < this.n / 2) {
            this._x = new float[this.n / 2];
        }

        if (this._w.length < this.n / 2) {
            this._w = new float[this.n / 2];
        }

        float[] floats2 = this._x;
        float[] floats3 = this._w;
        int int0 = this.n >> 1;
        int int1 = this.n >> 2;
        int int2 = this.n >> 3;
        if (this.equalizer != null) {
            for (int int3 = 0; int3 < this.n; int3++) {
                floats1[int3] *= this.equalizer[int3];
            }
        }

        int int4 = -1;
        int int5 = 0;
        int int6 = int0;

        for (int int7 = 0; int7 < int2; int7++) {
            int int8 = int4 + 2;
            this.dtmp1 = floats0[int8];
            int4 = int8 + 2;
            this.dtmp2 = floats0[int4];
            this.dtmp3 = this.trig[--int6];
            this.dtmp4 = this.trig[--int6];
            floats2[int5++] = -this.dtmp2 * this.dtmp3 - this.dtmp1 * this.dtmp4;
            floats2[int5++] = this.dtmp1 * this.dtmp3 - this.dtmp2 * this.dtmp4;
        }

        int4 = int0;

        for (int int9 = 0; int9 < int2; int9++) {
            int int10 = int4 - 2;
            this.dtmp1 = floats0[int10];
            int4 = int10 - 2;
            this.dtmp2 = floats0[int4];
            this.dtmp3 = this.trig[--int6];
            this.dtmp4 = this.trig[--int6];
            floats2[int5++] = this.dtmp2 * this.dtmp3 + this.dtmp1 * this.dtmp4;
            floats2[int5++] = this.dtmp2 * this.dtmp4 - this.dtmp1 * this.dtmp3;
        }

        float[] floats4 = this.kernel(floats2, floats3, this.n, int0, int1, int2);
        int5 = 0;
        int6 = int0;
        int int11 = int1;
        int int12 = int1 - 1;
        int int13 = int1 + int0;
        int int14 = int13 - 1;

        for (int int15 = 0; int15 < int1; int15++) {
            this.dtmp1 = floats4[int5++];
            this.dtmp2 = floats4[int5++];
            this.dtmp3 = this.trig[int6++];
            this.dtmp4 = this.trig[int6++];
            float float0 = this.dtmp1 * this.dtmp4 - this.dtmp2 * this.dtmp3;
            float float1 = -(this.dtmp1 * this.dtmp3 + this.dtmp2 * this.dtmp4);
            ints[int11] = (int)(-float0 * floats5[int11]);
            ints[int12] = (int)(float0 * floats5[int12]);
            ints[int13] = (int)(float1 * floats5[int13]);
            ints[int14] = (int)(float1 * floats5[int14]);
            int11++;
            int12--;
            int13++;
            int14--;
        }
    }

    private float[] kernel(float[] floats0, float[] floats1, int int9, int int5, int int1, int int20) {
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
                    this.dtmp1 = floats1[int13];
                    this.dtmp2 = floats1[int3];
                    float float4 = this.dtmp1 - this.dtmp2;
                    floats0[int13] = this.dtmp1 + this.dtmp2;
                    this.dtmp1 = floats1[++int13];
                    this.dtmp2 = floats1[++int3];
                    float float5 = this.dtmp1 - this.dtmp2;
                    floats0[int13] = this.dtmp1 + this.dtmp2;
                    floats0[int3] = float5 * float2 - float4 * float3;
                    floats0[int3 - 1] = float4 * float2 + float5 * float3;
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
            float float6 = floats1[int21] - floats1[int22 + 1];
            float float7 = floats1[int21 - 1] + floats1[int22];
            float float8 = floats1[int21] + floats1[int22 + 1];
            float float9 = floats1[int21 - 1] - floats1[int22];
            float float10 = float6 * this.trig[int15];
            float float11 = float7 * this.trig[int15++];
            float float12 = float6 * this.trig[int15];
            float float13 = float7 * this.trig[int15++];
            floats0[int17++] = (float8 + float12 + float11) * 16383.0F;
            floats0[int18--] = (-float9 + float13 - float10) * 16383.0F;
            floats0[int17++] = (float9 + float13 - float10) * 16383.0F;
            floats0[int18--] = (float8 - float12 - float11) * 16383.0F;
        }

        return floats0;
    }
}
