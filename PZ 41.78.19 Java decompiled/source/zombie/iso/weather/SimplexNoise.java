// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

public class SimplexNoise {
    private static SimplexNoise.Grad[] grad3 = new SimplexNoise.Grad[]{
        new SimplexNoise.Grad(1.0, 1.0, 0.0),
        new SimplexNoise.Grad(-1.0, 1.0, 0.0),
        new SimplexNoise.Grad(1.0, -1.0, 0.0),
        new SimplexNoise.Grad(-1.0, -1.0, 0.0),
        new SimplexNoise.Grad(1.0, 0.0, 1.0),
        new SimplexNoise.Grad(-1.0, 0.0, 1.0),
        new SimplexNoise.Grad(1.0, 0.0, -1.0),
        new SimplexNoise.Grad(-1.0, 0.0, -1.0),
        new SimplexNoise.Grad(0.0, 1.0, 1.0),
        new SimplexNoise.Grad(0.0, -1.0, 1.0),
        new SimplexNoise.Grad(0.0, 1.0, -1.0),
        new SimplexNoise.Grad(0.0, -1.0, -1.0)
    };
    private static SimplexNoise.Grad[] grad4 = new SimplexNoise.Grad[]{
        new SimplexNoise.Grad(0.0, 1.0, 1.0, 1.0),
        new SimplexNoise.Grad(0.0, 1.0, 1.0, -1.0),
        new SimplexNoise.Grad(0.0, 1.0, -1.0, 1.0),
        new SimplexNoise.Grad(0.0, 1.0, -1.0, -1.0),
        new SimplexNoise.Grad(0.0, -1.0, 1.0, 1.0),
        new SimplexNoise.Grad(0.0, -1.0, 1.0, -1.0),
        new SimplexNoise.Grad(0.0, -1.0, -1.0, 1.0),
        new SimplexNoise.Grad(0.0, -1.0, -1.0, -1.0),
        new SimplexNoise.Grad(1.0, 0.0, 1.0, 1.0),
        new SimplexNoise.Grad(1.0, 0.0, 1.0, -1.0),
        new SimplexNoise.Grad(1.0, 0.0, -1.0, 1.0),
        new SimplexNoise.Grad(1.0, 0.0, -1.0, -1.0),
        new SimplexNoise.Grad(-1.0, 0.0, 1.0, 1.0),
        new SimplexNoise.Grad(-1.0, 0.0, 1.0, -1.0),
        new SimplexNoise.Grad(-1.0, 0.0, -1.0, 1.0),
        new SimplexNoise.Grad(-1.0, 0.0, -1.0, -1.0),
        new SimplexNoise.Grad(1.0, 1.0, 0.0, 1.0),
        new SimplexNoise.Grad(1.0, 1.0, 0.0, -1.0),
        new SimplexNoise.Grad(1.0, -1.0, 0.0, 1.0),
        new SimplexNoise.Grad(1.0, -1.0, 0.0, -1.0),
        new SimplexNoise.Grad(-1.0, 1.0, 0.0, 1.0),
        new SimplexNoise.Grad(-1.0, 1.0, 0.0, -1.0),
        new SimplexNoise.Grad(-1.0, -1.0, 0.0, 1.0),
        new SimplexNoise.Grad(-1.0, -1.0, 0.0, -1.0),
        new SimplexNoise.Grad(1.0, 1.0, 1.0, 0.0),
        new SimplexNoise.Grad(1.0, 1.0, -1.0, 0.0),
        new SimplexNoise.Grad(1.0, -1.0, 1.0, 0.0),
        new SimplexNoise.Grad(1.0, -1.0, -1.0, 0.0),
        new SimplexNoise.Grad(-1.0, 1.0, 1.0, 0.0),
        new SimplexNoise.Grad(-1.0, 1.0, -1.0, 0.0),
        new SimplexNoise.Grad(-1.0, -1.0, 1.0, 0.0),
        new SimplexNoise.Grad(-1.0, -1.0, -1.0, 0.0)
    };
    private static short[] p = new short[]{
        151,
        160,
        137,
        91,
        90,
        15,
        131,
        13,
        201,
        95,
        96,
        53,
        194,
        233,
        7,
        225,
        140,
        36,
        103,
        30,
        69,
        142,
        8,
        99,
        37,
        240,
        21,
        10,
        23,
        190,
        6,
        148,
        247,
        120,
        234,
        75,
        0,
        26,
        197,
        62,
        94,
        252,
        219,
        203,
        117,
        35,
        11,
        32,
        57,
        177,
        33,
        88,
        237,
        149,
        56,
        87,
        174,
        20,
        125,
        136,
        171,
        168,
        68,
        175,
        74,
        165,
        71,
        134,
        139,
        48,
        27,
        166,
        77,
        146,
        158,
        231,
        83,
        111,
        229,
        122,
        60,
        211,
        133,
        230,
        220,
        105,
        92,
        41,
        55,
        46,
        245,
        40,
        244,
        102,
        143,
        54,
        65,
        25,
        63,
        161,
        1,
        216,
        80,
        73,
        209,
        76,
        132,
        187,
        208,
        89,
        18,
        169,
        200,
        196,
        135,
        130,
        116,
        188,
        159,
        86,
        164,
        100,
        109,
        198,
        173,
        186,
        3,
        64,
        52,
        217,
        226,
        250,
        124,
        123,
        5,
        202,
        38,
        147,
        118,
        126,
        255,
        82,
        85,
        212,
        207,
        206,
        59,
        227,
        47,
        16,
        58,
        17,
        182,
        189,
        28,
        42,
        223,
        183,
        170,
        213,
        119,
        248,
        152,
        2,
        44,
        154,
        163,
        70,
        221,
        153,
        101,
        155,
        167,
        43,
        172,
        9,
        129,
        22,
        39,
        253,
        19,
        98,
        108,
        110,
        79,
        113,
        224,
        232,
        178,
        185,
        112,
        104,
        218,
        246,
        97,
        228,
        251,
        34,
        242,
        193,
        238,
        210,
        144,
        12,
        191,
        179,
        162,
        241,
        81,
        51,
        145,
        235,
        249,
        14,
        239,
        107,
        49,
        192,
        214,
        31,
        181,
        199,
        106,
        157,
        184,
        84,
        204,
        176,
        115,
        121,
        50,
        45,
        127,
        4,
        150,
        254,
        138,
        236,
        205,
        93,
        222,
        114,
        67,
        29,
        24,
        72,
        243,
        141,
        128,
        195,
        78,
        66,
        215,
        61,
        156,
        180
    };
    private static short[] perm = new short[512];
    private static short[] permMod12 = new short[512];
    private static final double F2;
    private static final double G2;
    private static final double F3 = 0.3333333333333333;
    private static final double G3 = 0.16666666666666666;
    private static final double F4;
    private static final double G4;

    private static int fastfloor(double double0) {
        int int0 = (int)double0;
        return double0 < int0 ? int0 - 1 : int0;
    }

    private static double dot(SimplexNoise.Grad grad, double double1, double double0) {
        return grad.x * double1 + grad.y * double0;
    }

    private static double dot(SimplexNoise.Grad grad, double double2, double double1, double double0) {
        return grad.x * double2 + grad.y * double1 + grad.z * double0;
    }

    private static double dot(SimplexNoise.Grad grad, double double3, double double2, double double1, double double0) {
        return grad.x * double3 + grad.y * double2 + grad.z * double1 + grad.w * double0;
    }

    public static double noise(double double1, double double2) {
        double double0 = (double1 + double2) * F2;
        int int0 = fastfloor(double1 + double0);
        int int1 = fastfloor(double2 + double0);
        double double3 = (int0 + int1) * G2;
        double double4 = int0 - double3;
        double double5 = int1 - double3;
        double double6 = double1 - double4;
        double double7 = double2 - double5;
        byte byte0;
        byte byte1;
        if (double6 > double7) {
            byte0 = 1;
            byte1 = 0;
        } else {
            byte0 = 0;
            byte1 = 1;
        }

        double double8 = double6 - byte0 + G2;
        double double9 = double7 - byte1 + G2;
        double double10 = double6 - 1.0 + 2.0 * G2;
        double double11 = double7 - 1.0 + 2.0 * G2;
        int int2 = int0 & 0xFF;
        int int3 = int1 & 0xFF;
        short short0 = permMod12[int2 + perm[int3]];
        short short1 = permMod12[int2 + byte0 + perm[int3 + byte1]];
        short short2 = permMod12[int2 + 1 + perm[int3 + 1]];
        double double12 = 0.5 - double6 * double6 - double7 * double7;
        double double13;
        if (double12 < 0.0) {
            double13 = 0.0;
        } else {
            double12 *= double12;
            double13 = double12 * double12 * dot(grad3[short0], double6, double7);
        }

        double double14 = 0.5 - double8 * double8 - double9 * double9;
        double double15;
        if (double14 < 0.0) {
            double15 = 0.0;
        } else {
            double14 *= double14;
            double15 = double14 * double14 * dot(grad3[short1], double8, double9);
        }

        double double16 = 0.5 - double10 * double10 - double11 * double11;
        double double17;
        if (double16 < 0.0) {
            double17 = 0.0;
        } else {
            double16 *= double16;
            double17 = double16 * double16 * dot(grad3[short2], double10, double11);
        }

        return 70.0 * (double13 + double15 + double17);
    }

    public static double noise(double double2, double double3, double double1) {
        double double0 = (double2 + double3 + double1) * 0.3333333333333333;
        int int0 = fastfloor(double2 + double0);
        int int1 = fastfloor(double3 + double0);
        int int2 = fastfloor(double1 + double0);
        double double4 = (int0 + int1 + int2) * 0.16666666666666666;
        double double5 = int0 - double4;
        double double6 = int1 - double4;
        double double7 = int2 - double4;
        double double8 = double2 - double5;
        double double9 = double3 - double6;
        double double10 = double1 - double7;
        byte byte0;
        byte byte1;
        byte byte2;
        byte byte3;
        byte byte4;
        byte byte5;
        if (double8 >= double9) {
            if (double9 >= double10) {
                byte0 = 1;
                byte1 = 0;
                byte2 = 0;
                byte3 = 1;
                byte4 = 1;
                byte5 = 0;
            } else if (double8 >= double10) {
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
        } else if (double9 < double10) {
            byte0 = 0;
            byte1 = 0;
            byte2 = 1;
            byte3 = 0;
            byte4 = 1;
            byte5 = 1;
        } else if (double8 < double10) {
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

        double double11 = double8 - byte0 + 0.16666666666666666;
        double double12 = double9 - byte1 + 0.16666666666666666;
        double double13 = double10 - byte2 + 0.16666666666666666;
        double double14 = double8 - byte3 + 0.3333333333333333;
        double double15 = double9 - byte4 + 0.3333333333333333;
        double double16 = double10 - byte5 + 0.3333333333333333;
        double double17 = double8 - 1.0 + 0.5;
        double double18 = double9 - 1.0 + 0.5;
        double double19 = double10 - 1.0 + 0.5;
        int int3 = int0 & 0xFF;
        int int4 = int1 & 0xFF;
        int int5 = int2 & 0xFF;
        short short0 = permMod12[int3 + perm[int4 + perm[int5]]];
        short short1 = permMod12[int3 + byte0 + perm[int4 + byte1 + perm[int5 + byte2]]];
        short short2 = permMod12[int3 + byte3 + perm[int4 + byte4 + perm[int5 + byte5]]];
        short short3 = permMod12[int3 + 1 + perm[int4 + 1 + perm[int5 + 1]]];
        double double20 = 0.6 - double8 * double8 - double9 * double9 - double10 * double10;
        double double21;
        if (double20 < 0.0) {
            double21 = 0.0;
        } else {
            double20 *= double20;
            double21 = double20 * double20 * dot(grad3[short0], double8, double9, double10);
        }

        double double22 = 0.6 - double11 * double11 - double12 * double12 - double13 * double13;
        double double23;
        if (double22 < 0.0) {
            double23 = 0.0;
        } else {
            double22 *= double22;
            double23 = double22 * double22 * dot(grad3[short1], double11, double12, double13);
        }

        double double24 = 0.6 - double14 * double14 - double15 * double15 - double16 * double16;
        double double25;
        if (double24 < 0.0) {
            double25 = 0.0;
        } else {
            double24 *= double24;
            double25 = double24 * double24 * dot(grad3[short2], double14, double15, double16);
        }

        double double26 = 0.6 - double17 * double17 - double18 * double18 - double19 * double19;
        double double27;
        if (double26 < 0.0) {
            double27 = 0.0;
        } else {
            double26 *= double26;
            double27 = double26 * double26 * dot(grad3[short3], double17, double18, double19);
        }

        return 32.0 * (double21 + double23 + double25 + double27);
    }

    public static double noise(double double3, double double4, double double2, double double1) {
        double double0 = (double3 + double4 + double2 + double1) * F4;
        int int0 = fastfloor(double3 + double0);
        int int1 = fastfloor(double4 + double0);
        int int2 = fastfloor(double2 + double0);
        int int3 = fastfloor(double1 + double0);
        double double5 = (int0 + int1 + int2 + int3) * G4;
        double double6 = int0 - double5;
        double double7 = int1 - double5;
        double double8 = int2 - double5;
        double double9 = int3 - double5;
        double double10 = double3 - double6;
        double double11 = double4 - double7;
        double double12 = double2 - double8;
        double double13 = double1 - double9;
        int int4 = 0;
        int int5 = 0;
        int int6 = 0;
        int int7 = 0;
        if (double10 > double11) {
            int4++;
        } else {
            int5++;
        }

        if (double10 > double12) {
            int4++;
        } else {
            int6++;
        }

        if (double10 > double13) {
            int4++;
        } else {
            int7++;
        }

        if (double11 > double12) {
            int5++;
        } else {
            int6++;
        }

        if (double11 > double13) {
            int5++;
        } else {
            int7++;
        }

        if (double12 > double13) {
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
        double double14 = double10 - int8 + G4;
        double double15 = double11 - int9 + G4;
        double double16 = double12 - int10 + G4;
        double double17 = double13 - int11 + G4;
        double double18 = double10 - int12 + 2.0 * G4;
        double double19 = double11 - int13 + 2.0 * G4;
        double double20 = double12 - int14 + 2.0 * G4;
        double double21 = double13 - int15 + 2.0 * G4;
        double double22 = double10 - int16 + 3.0 * G4;
        double double23 = double11 - int17 + 3.0 * G4;
        double double24 = double12 - int18 + 3.0 * G4;
        double double25 = double13 - int19 + 3.0 * G4;
        double double26 = double10 - 1.0 + 4.0 * G4;
        double double27 = double11 - 1.0 + 4.0 * G4;
        double double28 = double12 - 1.0 + 4.0 * G4;
        double double29 = double13 - 1.0 + 4.0 * G4;
        int int20 = int0 & 0xFF;
        int int21 = int1 & 0xFF;
        int int22 = int2 & 0xFF;
        int int23 = int3 & 0xFF;
        int int24 = perm[int20 + perm[int21 + perm[int22 + perm[int23]]]] % 32;
        int int25 = perm[int20 + int8 + perm[int21 + int9 + perm[int22 + int10 + perm[int23 + int11]]]] % 32;
        int int26 = perm[int20 + int12 + perm[int21 + int13 + perm[int22 + int14 + perm[int23 + int15]]]] % 32;
        int int27 = perm[int20 + int16 + perm[int21 + int17 + perm[int22 + int18 + perm[int23 + int19]]]] % 32;
        int int28 = perm[int20 + 1 + perm[int21 + 1 + perm[int22 + 1 + perm[int23 + 1]]]] % 32;
        double double30 = 0.6 - double10 * double10 - double11 * double11 - double12 * double12 - double13 * double13;
        double double31;
        if (double30 < 0.0) {
            double31 = 0.0;
        } else {
            double30 *= double30;
            double31 = double30 * double30 * dot(grad4[int24], double10, double11, double12, double13);
        }

        double double32 = 0.6 - double14 * double14 - double15 * double15 - double16 * double16 - double17 * double17;
        double double33;
        if (double32 < 0.0) {
            double33 = 0.0;
        } else {
            double32 *= double32;
            double33 = double32 * double32 * dot(grad4[int25], double14, double15, double16, double17);
        }

        double double34 = 0.6 - double18 * double18 - double19 * double19 - double20 * double20 - double21 * double21;
        double double35;
        if (double34 < 0.0) {
            double35 = 0.0;
        } else {
            double34 *= double34;
            double35 = double34 * double34 * dot(grad4[int26], double18, double19, double20, double21);
        }

        double double36 = 0.6 - double22 * double22 - double23 * double23 - double24 * double24 - double25 * double25;
        double double37;
        if (double36 < 0.0) {
            double37 = 0.0;
        } else {
            double36 *= double36;
            double37 = double36 * double36 * dot(grad4[int27], double22, double23, double24, double25);
        }

        double double38 = 0.6 - double26 * double26 - double27 * double27 - double28 * double28 - double29 * double29;
        double double39;
        if (double38 < 0.0) {
            double39 = 0.0;
        } else {
            double38 *= double38;
            double39 = double38 * double38 * dot(grad4[int28], double26, double27, double28, double29);
        }

        return 27.0 * (double31 + double33 + double35 + double37 + double39);
    }

    static {
        for (int int0 = 0; int0 < 512; int0++) {
            perm[int0] = p[int0 & 0xFF];
            permMod12[int0] = (short)(perm[int0] % 12);
        }

        F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
        F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
        G4 = (5.0 - Math.sqrt(5.0)) / 20.0;
    }

    private static class Grad {
        double x;
        double y;
        double z;
        double w;

        Grad(double double0, double double1, double double2) {
            this.x = double0;
            this.y = double1;
            this.z = double2;
        }

        Grad(double double0, double double1, double double2, double double3) {
            this.x = double0;
            this.y = double1;
            this.z = double2;
            this.w = double3;
        }
    }
}
