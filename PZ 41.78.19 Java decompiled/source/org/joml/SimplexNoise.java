// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class SimplexNoise {
    private static final SimplexNoise.Vector3b[] grad3 = new SimplexNoise.Vector3b[]{
        new SimplexNoise.Vector3b(1, 1, 0),
        new SimplexNoise.Vector3b(-1, 1, 0),
        new SimplexNoise.Vector3b(1, -1, 0),
        new SimplexNoise.Vector3b(-1, -1, 0),
        new SimplexNoise.Vector3b(1, 0, 1),
        new SimplexNoise.Vector3b(-1, 0, 1),
        new SimplexNoise.Vector3b(1, 0, -1),
        new SimplexNoise.Vector3b(-1, 0, -1),
        new SimplexNoise.Vector3b(0, 1, 1),
        new SimplexNoise.Vector3b(0, -1, 1),
        new SimplexNoise.Vector3b(0, 1, -1),
        new SimplexNoise.Vector3b(0, -1, -1)
    };
    private static final SimplexNoise.Vector4b[] grad4 = new SimplexNoise.Vector4b[]{
        new SimplexNoise.Vector4b(0, 1, 1, 1),
        new SimplexNoise.Vector4b(0, 1, 1, -1),
        new SimplexNoise.Vector4b(0, 1, -1, 1),
        new SimplexNoise.Vector4b(0, 1, -1, -1),
        new SimplexNoise.Vector4b(0, -1, 1, 1),
        new SimplexNoise.Vector4b(0, -1, 1, -1),
        new SimplexNoise.Vector4b(0, -1, -1, 1),
        new SimplexNoise.Vector4b(0, -1, -1, -1),
        new SimplexNoise.Vector4b(1, 0, 1, 1),
        new SimplexNoise.Vector4b(1, 0, 1, -1),
        new SimplexNoise.Vector4b(1, 0, -1, 1),
        new SimplexNoise.Vector4b(1, 0, -1, -1),
        new SimplexNoise.Vector4b(-1, 0, 1, 1),
        new SimplexNoise.Vector4b(-1, 0, 1, -1),
        new SimplexNoise.Vector4b(-1, 0, -1, 1),
        new SimplexNoise.Vector4b(-1, 0, -1, -1),
        new SimplexNoise.Vector4b(1, 1, 0, 1),
        new SimplexNoise.Vector4b(1, 1, 0, -1),
        new SimplexNoise.Vector4b(1, -1, 0, 1),
        new SimplexNoise.Vector4b(1, -1, 0, -1),
        new SimplexNoise.Vector4b(-1, 1, 0, 1),
        new SimplexNoise.Vector4b(-1, 1, 0, -1),
        new SimplexNoise.Vector4b(-1, -1, 0, 1),
        new SimplexNoise.Vector4b(-1, -1, 0, -1),
        new SimplexNoise.Vector4b(1, 1, 1, 0),
        new SimplexNoise.Vector4b(1, 1, -1, 0),
        new SimplexNoise.Vector4b(1, -1, 1, 0),
        new SimplexNoise.Vector4b(1, -1, -1, 0),
        new SimplexNoise.Vector4b(-1, 1, 1, 0),
        new SimplexNoise.Vector4b(-1, 1, -1, 0),
        new SimplexNoise.Vector4b(-1, -1, 1, 0),
        new SimplexNoise.Vector4b(-1, -1, -1, 0)
    };
    private static final byte[] p = new byte[]{
        -105,
        -96,
        -119,
        91,
        90,
        15,
        -125,
        13,
        -55,
        95,
        96,
        53,
        -62,
        -23,
        7,
        -31,
        -116,
        36,
        103,
        30,
        69,
        -114,
        8,
        99,
        37,
        -16,
        21,
        10,
        23,
        -66,
        6,
        -108,
        -9,
        120,
        -22,
        75,
        0,
        26,
        -59,
        62,
        94,
        -4,
        -37,
        -53,
        117,
        35,
        11,
        32,
        57,
        -79,
        33,
        88,
        -19,
        -107,
        56,
        87,
        -82,
        20,
        125,
        -120,
        -85,
        -88,
        68,
        -81,
        74,
        -91,
        71,
        -122,
        -117,
        48,
        27,
        -90,
        77,
        -110,
        -98,
        -25,
        83,
        111,
        -27,
        122,
        60,
        -45,
        -123,
        -26,
        -36,
        105,
        92,
        41,
        55,
        46,
        -11,
        40,
        -12,
        102,
        -113,
        54,
        65,
        25,
        63,
        -95,
        1,
        -40,
        80,
        73,
        -47,
        76,
        -124,
        -69,
        -48,
        89,
        18,
        -87,
        -56,
        -60,
        -121,
        -126,
        116,
        -68,
        -97,
        86,
        -92,
        100,
        109,
        -58,
        -83,
        -70,
        3,
        64,
        52,
        -39,
        -30,
        -6,
        124,
        123,
        5,
        -54,
        38,
        -109,
        118,
        126,
        -1,
        82,
        85,
        -44,
        -49,
        -50,
        59,
        -29,
        47,
        16,
        58,
        17,
        -74,
        -67,
        28,
        42,
        -33,
        -73,
        -86,
        -43,
        119,
        -8,
        -104,
        2,
        44,
        -102,
        -93,
        70,
        -35,
        -103,
        101,
        -101,
        -89,
        43,
        -84,
        9,
        -127,
        22,
        39,
        -3,
        19,
        98,
        108,
        110,
        79,
        113,
        -32,
        -24,
        -78,
        -71,
        112,
        104,
        -38,
        -10,
        97,
        -28,
        -5,
        34,
        -14,
        -63,
        -18,
        -46,
        -112,
        12,
        -65,
        -77,
        -94,
        -15,
        81,
        51,
        -111,
        -21,
        -7,
        14,
        -17,
        107,
        49,
        -64,
        -42,
        31,
        -75,
        -57,
        106,
        -99,
        -72,
        84,
        -52,
        -80,
        115,
        121,
        50,
        45,
        127,
        4,
        -106,
        -2,
        -118,
        -20,
        -51,
        93,
        -34,
        114,
        67,
        29,
        24,
        72,
        -13,
        -115,
        -128,
        -61,
        78,
        66,
        -41,
        61,
        -100,
        -76
    };
    private static final byte[] perm = new byte[512];
    private static final byte[] permMod12 = new byte[512];
    private static final float F2 = 0.36602542F;
    private static final float G2 = 0.21132487F;
    private static final float F3 = 0.33333334F;
    private static final float G3 = 0.16666667F;
    private static final float F4 = 0.309017F;
    private static final float G4 = 0.1381966F;

    private static int fastfloor(float float0) {
        int int0 = (int)float0;
        return float0 < int0 ? int0 - 1 : int0;
    }

    private static float dot(SimplexNoise.Vector3b vector3b, float float1, float float0) {
        return vector3b.x * float1 + vector3b.y * float0;
    }

    private static float dot(SimplexNoise.Vector3b vector3b, float float2, float float1, float float0) {
        return vector3b.x * float2 + vector3b.y * float1 + vector3b.z * float0;
    }

    private static float dot(SimplexNoise.Vector4b vector4b, float float3, float float2, float float1, float float0) {
        return vector4b.x * float3 + vector4b.y * float2 + vector4b.z * float1 + vector4b.w * float0;
    }

    public static float noise(float float1, float float2) {
        float float0 = (float1 + float2) * 0.36602542F;
        int int0 = fastfloor(float1 + float0);
        int int1 = fastfloor(float2 + float0);
        float float3 = (int0 + int1) * 0.21132487F;
        float float4 = int0 - float3;
        float float5 = int1 - float3;
        float float6 = float1 - float4;
        float float7 = float2 - float5;
        byte byte0;
        byte byte1;
        if (float6 > float7) {
            byte0 = 1;
            byte1 = 0;
        } else {
            byte0 = 0;
            byte1 = 1;
        }

        float float8 = float6 - byte0 + 0.21132487F;
        float float9 = float7 - byte1 + 0.21132487F;
        float float10 = float6 - 1.0F + 0.42264974F;
        float float11 = float7 - 1.0F + 0.42264974F;
        int int2 = int0 & 0xFF;
        int int3 = int1 & 0xFF;
        int int4 = permMod12[int2 + perm[int3] & 0xFF] & 255;
        int int5 = permMod12[int2 + byte0 + perm[int3 + byte1] & 0xFF] & 255;
        int int6 = permMod12[int2 + 1 + perm[int3 + 1] & 0xFF] & 255;
        float float12 = 0.5F - float6 * float6 - float7 * float7;
        float float13;
        if (float12 < 0.0F) {
            float13 = 0.0F;
        } else {
            float12 *= float12;
            float13 = float12 * float12 * dot(grad3[int4], float6, float7);
        }

        float float14 = 0.5F - float8 * float8 - float9 * float9;
        float float15;
        if (float14 < 0.0F) {
            float15 = 0.0F;
        } else {
            float14 *= float14;
            float15 = float14 * float14 * dot(grad3[int5], float8, float9);
        }

        float float16 = 0.5F - float10 * float10 - float11 * float11;
        float float17;
        if (float16 < 0.0F) {
            float17 = 0.0F;
        } else {
            float16 *= float16;
            float17 = float16 * float16 * dot(grad3[int6], float10, float11);
        }

        return 70.0F * (float13 + float15 + float17);
    }

    public static float noise(float float2, float float3, float float1) {
        float float0 = (float2 + float3 + float1) * 0.33333334F;
        int int0 = fastfloor(float2 + float0);
        int int1 = fastfloor(float3 + float0);
        int int2 = fastfloor(float1 + float0);
        float float4 = (int0 + int1 + int2) * 0.16666667F;
        float float5 = int0 - float4;
        float float6 = int1 - float4;
        float float7 = int2 - float4;
        float float8 = float2 - float5;
        float float9 = float3 - float6;
        float float10 = float1 - float7;
        byte byte0;
        byte byte1;
        byte byte2;
        byte byte3;
        byte byte4;
        byte byte5;
        if (float8 >= float9) {
            if (float9 >= float10) {
                byte0 = 1;
                byte1 = 0;
                byte2 = 0;
                byte3 = 1;
                byte4 = 1;
                byte5 = 0;
            } else if (float8 >= float10) {
                byte0 = 1;
                byte1 = 0;
                byte2 = 0;
                byte3 = 1;
                byte4 = 0;
                byte5 = 1;
            } else {
                byte0 = 0;
                byte1 = 0;
                byte2 = 1;
                byte3 = 1;
                byte4 = 0;
                byte5 = 1;
            }
        } else if (float9 < float10) {
            byte0 = 0;
            byte1 = 0;
            byte2 = 1;
            byte3 = 0;
            byte4 = 1;
            byte5 = 1;
        } else if (float8 < float10) {
            byte0 = 0;
            byte1 = 1;
            byte2 = 0;
            byte3 = 0;
            byte4 = 1;
            byte5 = 1;
        } else {
            byte0 = 0;
            byte1 = 1;
            byte2 = 0;
            byte3 = 1;
            byte4 = 1;
            byte5 = 0;
        }

        float float11 = float8 - byte0 + 0.16666667F;
        float float12 = float9 - byte1 + 0.16666667F;
        float float13 = float10 - byte2 + 0.16666667F;
        float float14 = float8 - byte3 + 0.33333334F;
        float float15 = float9 - byte4 + 0.33333334F;
        float float16 = float10 - byte5 + 0.33333334F;
        float float17 = float8 - 1.0F + 0.5F;
        float float18 = float9 - 1.0F + 0.5F;
        float float19 = float10 - 1.0F + 0.5F;
        int int3 = int0 & 0xFF;
        int int4 = int1 & 0xFF;
        int int5 = int2 & 0xFF;
        int int6 = permMod12[int3 + perm[int4 + perm[int5] & 0xFF] & 0xFF] & 255;
        int int7 = permMod12[int3 + byte0 + perm[int4 + byte1 + perm[int5 + byte2] & 0xFF] & 0xFF] & 255;
        int int8 = permMod12[int3 + byte3 + perm[int4 + byte4 + perm[int5 + byte5] & 0xFF] & 0xFF] & 255;
        int int9 = permMod12[int3 + 1 + perm[int4 + 1 + perm[int5 + 1] & 0xFF] & 0xFF] & 255;
        float float20 = 0.6F - float8 * float8 - float9 * float9 - float10 * float10;
        float float21;
        if (float20 < 0.0F) {
            float21 = 0.0F;
        } else {
            float20 *= float20;
            float21 = float20 * float20 * dot(grad3[int6], float8, float9, float10);
        }

        float float22 = 0.6F - float11 * float11 - float12 * float12 - float13 * float13;
        float float23;
        if (float22 < 0.0F) {
            float23 = 0.0F;
        } else {
            float22 *= float22;
            float23 = float22 * float22 * dot(grad3[int7], float11, float12, float13);
        }

        float float24 = 0.6F - float14 * float14 - float15 * float15 - float16 * float16;
        float float25;
        if (float24 < 0.0F) {
            float25 = 0.0F;
        } else {
            float24 *= float24;
            float25 = float24 * float24 * dot(grad3[int8], float14, float15, float16);
        }

        float float26 = 0.6F - float17 * float17 - float18 * float18 - float19 * float19;
        float float27;
        if (float26 < 0.0F) {
            float27 = 0.0F;
        } else {
            float26 *= float26;
            float27 = float26 * float26 * dot(grad3[int9], float17, float18, float19);
        }

        return 32.0F * (float21 + float23 + float25 + float27);
    }

    public static float noise(float float3, float float4, float float2, float float1) {
        float float0 = (float3 + float4 + float2 + float1) * 0.309017F;
        int int0 = fastfloor(float3 + float0);
        int int1 = fastfloor(float4 + float0);
        int int2 = fastfloor(float2 + float0);
        int int3 = fastfloor(float1 + float0);
        float float5 = (int0 + int1 + int2 + int3) * 0.1381966F;
        float float6 = int0 - float5;
        float float7 = int1 - float5;
        float float8 = int2 - float5;
        float float9 = int3 - float5;
        float float10 = float3 - float6;
        float float11 = float4 - float7;
        float float12 = float2 - float8;
        float float13 = float1 - float9;
        int int4 = 0;
        int int5 = 0;
        int int6 = 0;
        int int7 = 0;
        if (float10 > float11) {
            int4++;
        } else {
            int5++;
        }

        if (float10 > float12) {
            int4++;
        } else {
            int6++;
        }

        if (float10 > float13) {
            int4++;
        } else {
            int7++;
        }

        if (float11 > float12) {
            int5++;
        } else {
            int6++;
        }

        if (float11 > float13) {
            int5++;
        } else {
            int7++;
        }

        if (float12 > float13) {
            int6++;
        } else {
            int7++;
        }

        int int8 = int4 >= 3 ? 1 : 0;
        int int9 = int5 >= 3 ? 1 : 0;
        int int10 = int6 >= 3 ? 1 : 0;
        int int11 = int7 >= 3 ? 1 : 0;
        int int12 = int4 >= 2 ? 1 : 0;
        int int13 = int5 >= 2 ? 1 : 0;
        int int14 = int6 >= 2 ? 1 : 0;
        int int15 = int7 >= 2 ? 1 : 0;
        int int16 = int4 >= 1 ? 1 : 0;
        int int17 = int5 >= 1 ? 1 : 0;
        int int18 = int6 >= 1 ? 1 : 0;
        int int19 = int7 >= 1 ? 1 : 0;
        float float14 = float10 - int8 + 0.1381966F;
        float float15 = float11 - int9 + 0.1381966F;
        float float16 = float12 - int10 + 0.1381966F;
        float float17 = float13 - int11 + 0.1381966F;
        float float18 = float10 - int12 + 0.2763932F;
        float float19 = float11 - int13 + 0.2763932F;
        float float20 = float12 - int14 + 0.2763932F;
        float float21 = float13 - int15 + 0.2763932F;
        float float22 = float10 - int16 + 0.41458982F;
        float float23 = float11 - int17 + 0.41458982F;
        float float24 = float12 - int18 + 0.41458982F;
        float float25 = float13 - int19 + 0.41458982F;
        float float26 = float10 - 1.0F + 0.5527864F;
        float float27 = float11 - 1.0F + 0.5527864F;
        float float28 = float12 - 1.0F + 0.5527864F;
        float float29 = float13 - 1.0F + 0.5527864F;
        int int20 = int0 & 0xFF;
        int int21 = int1 & 0xFF;
        int int22 = int2 & 0xFF;
        int int23 = int3 & 0xFF;
        int int24 = (perm[int20 + perm[int21 + perm[int22 + perm[int23] & 0xFF] & 0xFF] & 0xFF] & 255) % 32;
        int int25 = (perm[int20 + int8 + perm[int21 + int9 + perm[int22 + int10 + perm[int23 + int11] & 0xFF] & 0xFF] & 0xFF] & 255) % 32;
        int int26 = (perm[int20 + int12 + perm[int21 + int13 + perm[int22 + int14 + perm[int23 + int15] & 0xFF] & 0xFF] & 0xFF] & 255) % 32;
        int int27 = (perm[int20 + int16 + perm[int21 + int17 + perm[int22 + int18 + perm[int23 + int19] & 0xFF] & 0xFF] & 0xFF] & 255) % 32;
        int int28 = (perm[int20 + 1 + perm[int21 + 1 + perm[int22 + 1 + perm[int23 + 1] & 0xFF] & 0xFF] & 0xFF] & 255) % 32;
        float float30 = 0.6F - float10 * float10 - float11 * float11 - float12 * float12 - float13 * float13;
        float float31;
        if (float30 < 0.0F) {
            float31 = 0.0F;
        } else {
            float30 *= float30;
            float31 = float30 * float30 * dot(grad4[int24], float10, float11, float12, float13);
        }

        float float32 = 0.6F - float14 * float14 - float15 * float15 - float16 * float16 - float17 * float17;
        float float33;
        if (float32 < 0.0F) {
            float33 = 0.0F;
        } else {
            float32 *= float32;
            float33 = float32 * float32 * dot(grad4[int25], float14, float15, float16, float17);
        }

        float float34 = 0.6F - float18 * float18 - float19 * float19 - float20 * float20 - float21 * float21;
        float float35;
        if (float34 < 0.0F) {
            float35 = 0.0F;
        } else {
            float34 *= float34;
            float35 = float34 * float34 * dot(grad4[int26], float18, float19, float20, float21);
        }

        float float36 = 0.6F - float22 * float22 - float23 * float23 - float24 * float24 - float25 * float25;
        float float37;
        if (float36 < 0.0F) {
            float37 = 0.0F;
        } else {
            float36 *= float36;
            float37 = float36 * float36 * dot(grad4[int27], float22, float23, float24, float25);
        }

        float float38 = 0.6F - float26 * float26 - float27 * float27 - float28 * float28 - float29 * float29;
        float float39;
        if (float38 < 0.0F) {
            float39 = 0.0F;
        } else {
            float38 *= float38;
            float39 = float38 * float38 * dot(grad4[int28], float26, float27, float28, float29);
        }

        return 27.0F * (float31 + float33 + float35 + float37 + float39);
    }

    static {
        for (int int0 = 0; int0 < 512; int0++) {
            perm[int0] = p[int0 & 0xFF];
            permMod12[int0] = (byte)((perm[int0] & 255) % 12);
        }
    }

    private static class Vector3b {
        byte x;
        byte y;
        byte z;

        Vector3b(int int0, int int1, int int2) {
            this.x = (byte)int0;
            this.y = (byte)int1;
            this.z = (byte)int2;
        }
    }

    private static class Vector4b {
        byte x;
        byte y;
        byte z;
        byte w;

        Vector4b(int int0, int int1, int int2, int int3) {
            this.x = (byte)int0;
            this.y = (byte)int1;
            this.z = (byte)int2;
            this.w = (byte)int3;
        }
    }
}
