// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.utils;

import java.util.ArrayList;

public class Noise2D {
    private ArrayList<Noise2D.Layer> layers = new ArrayList<>(3);
    private static final int[] perm = new int[]{
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

    private float lerp(float float1, float float0, float float2) {
        return float0 + float1 * (float2 - float0);
    }

    private float fade(float float0) {
        return float0 * float0 * float0 * (float0 * (float0 * 6.0F - 15.0F) + 10.0F);
    }

    private float noise(float float0, float float1, int[] ints) {
        int int0 = (int)Math.floor(float0 - Math.floor(float0 / 255.0F) * 255.0);
        int int1 = (int)Math.floor(float1 - Math.floor(float1 / 255.0F) * 255.0);
        float float2 = this.fade(float0 - (float)Math.floor(float0));
        float float3 = this.fade(float1 - (float)Math.floor(float1));
        int int2 = ints[int0] + int1;
        int int3 = ints[int0] + int1 + 1;
        int int4 = ints[int0 + 1] + int1;
        int int5 = ints[int0 + 1] + int1 + 1;
        return this.lerp(float3, this.lerp(float2, perm[ints[int2]], perm[ints[int4]]), this.lerp(float2, perm[ints[int3]], perm[ints[int5]]));
    }

    public float layeredNoise(float _x, float _y) {
        float float0 = 0.0F;
        float float1 = 0.0F;

        for (int int0 = 0; int0 < this.layers.size(); int0++) {
            Noise2D.Layer layer = this.layers.get(int0);
            float1 += layer.amp;
            float0 += this.noise(_x * layer.freq, _y * layer.freq, layer.p) * layer.amp;
        }

        return float0 / float1 / 255.0F;
    }

    public void addLayer(int _seed, float _freq, float _amp) {
        int int0 = (int)Math.floor(_seed - Math.floor(_seed / 256.0F) * 256.0);
        Noise2D.Layer layer = new Noise2D.Layer();
        layer.freq = _freq;
        layer.amp = _amp;

        for (int int1 = 0; int1 < 256; int1++) {
            int int2 = (int)Math.floor(int0 + int1 - Math.floor((int0 + int1) / 256.0F) * 256.0);
            layer.p[int2] = perm[int1];
            layer.p[256 + int2] = layer.p[int2];
        }

        this.layers.add(layer);
    }

    public void reset() {
        if (this.layers.size() > 0) {
            this.layers.clear();
        }
    }

    private class Layer {
        public float freq;
        public float amp;
        public int[] p = new int[512];
    }
}
