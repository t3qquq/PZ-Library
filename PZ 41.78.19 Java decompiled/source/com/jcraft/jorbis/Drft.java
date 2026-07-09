// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

class Drft {
    static int[] ntryh = new int[]{4, 2, 3, 5};
    static float tpi = (float) (Math.PI * 2);
    static float hsqt2 = 0.70710677F;
    static float taui = 0.8660254F;
    static float taur = -0.5F;
    static float sqrt2 = 1.4142135F;
    int n;
    int[] splitcache;
    float[] trigcache;

    static void dradb2(int int2, int int1, float[] floats0, float[] floats1, float[] floats2, int int11) {
        int int0 = int1 * int2;
        int int3 = 0;
        int int4 = 0;
        int int5 = (int2 << 1) - 1;

        for (int int6 = 0; int6 < int1; int6++) {
            floats1[int3] = floats0[int4] + floats0[int5 + int4];
            floats1[int3 + int0] = floats0[int4] - floats0[int5 + int4];
            int4 = (int3 += int2) << 1;
        }

        if (int2 >= 2) {
            if (int2 != 2) {
                int3 = 0;
                int4 = 0;

                for (int int7 = 0; int7 < int1; int7++) {
                    int5 = int3;
                    int int8 = int4;
                    int int9 = int4 + (int2 << 1);
                    int int10 = int0 + int3;

                    for (byte byte0 = 2; byte0 < int2; byte0 += 2) {
                        int5 += 2;
                        int8 += 2;
                        int9 -= 2;
                        int10 += 2;
                        floats1[int5 - 1] = floats0[int8 - 1] + floats0[int9 - 1];
                        float float0 = floats0[int8 - 1] - floats0[int9 - 1];
                        floats1[int5] = floats0[int8] - floats0[int9];
                        float float1 = floats0[int8] + floats0[int9];
                        floats1[int10 - 1] = floats2[int11 + byte0 - 2] * float0 - floats2[int11 + byte0 - 1] * float1;
                        floats1[int10] = floats2[int11 + byte0 - 2] * float1 + floats2[int11 + byte0 - 1] * float0;
                    }

                    int4 = (int3 += int2) << 1;
                }

                if (int2 % 2 == 1) {
                    return;
                }
            }

            int3 = int2 - 1;
            int4 = int2 - 1;

            for (int int12 = 0; int12 < int1; int12++) {
                floats1[int3] = floats0[int4] + floats0[int4];
                floats1[int3 + int0] = -(floats0[int4 + 1] + floats0[int4 + 1]);
                int3 += int2;
                int4 += int2 << 1;
            }
        }
    }

    static void dradb3(int int2, int int1, float[] floats0, float[] floats1, float[] floats2, int int15, float[] floats3, int int16) {
        int int0 = int1 * int2;
        int int3 = 0;
        int int4 = int0 << 1;
        int int5 = int2 << 1;
        int int6 = int2 + (int2 << 1);
        int int7 = 0;

        for (int int8 = 0; int8 < int1; int8++) {
            float float0 = floats0[int5 - 1] + floats0[int5 - 1];
            float float1 = floats0[int7] + taur * float0;
            floats1[int3] = floats0[int7] + float0;
            float float2 = taui * (floats0[int5] + floats0[int5]);
            floats1[int3 + int0] = float1 - float2;
            floats1[int3 + int4] = float1 + float2;
            int3 += int2;
            int5 += int6;
            int7 += int6;
        }

        if (int2 != 1) {
            int3 = 0;
            int5 = int2 << 1;

            for (int int9 = 0; int9 < int1; int9++) {
                int int10 = int3 + (int3 << 1);
                int int11 = int7 = int10 + int5;
                int int12 = int3;
                int int13;
                int int14 = (int13 = int3 + int0) + int0;

                for (byte byte0 = 2; byte0 < int2; byte0 += 2) {
                    int7 += 2;
                    int11 -= 2;
                    int10 += 2;
                    int12 += 2;
                    int13 += 2;
                    int14 += 2;
                    float float3 = floats0[int7 - 1] + floats0[int11 - 1];
                    float float4 = floats0[int10 - 1] + taur * float3;
                    floats1[int12 - 1] = floats0[int10 - 1] + float3;
                    float float5 = floats0[int7] - floats0[int11];
                    float float6 = floats0[int10] + taur * float5;
                    floats1[int12] = floats0[int10] + float5;
                    float float7 = taui * (floats0[int7 - 1] - floats0[int11 - 1]);
                    float float8 = taui * (floats0[int7] + floats0[int11]);
                    float float9 = float4 - float8;
                    float float10 = float4 + float8;
                    float float11 = float6 + float7;
                    float float12 = float6 - float7;
                    floats1[int13 - 1] = floats2[int15 + byte0 - 2] * float9 - floats2[int15 + byte0 - 1] * float11;
                    floats1[int13] = floats2[int15 + byte0 - 2] * float11 + floats2[int15 + byte0 - 1] * float9;
                    floats1[int14 - 1] = floats3[int16 + byte0 - 2] * float10 - floats3[int16 + byte0 - 1] * float12;
                    floats1[int14] = floats3[int16 + byte0 - 2] * float12 + floats3[int16 + byte0 - 1] * float10;
                }

                int3 += int2;
            }
        }
    }

    static void dradb4(int int2, int int1, float[] floats0, float[] floats1, float[] floats2, int int17, float[] floats3, int int19, float[] floats4, int int20) {
        int int0 = int1 * int2;
        int int3 = 0;
        int int4 = int2 << 2;
        int int5 = 0;
        int int6 = int2 << 1;

        for (int int7 = 0; int7 < int1; int7++) {
            int int8 = int5 + int6;
            float float0 = floats0[int8 - 1] + floats0[int8 - 1];
            float float1 = floats0[int8] + floats0[int8];
            int int9;
            float float2 = floats0[int5] - floats0[(int9 = int8 + int6) - 1];
            float float3 = floats0[int5] + floats0[int9 - 1];
            floats1[int3] = float3 + float0;
            int int10;
            floats1[int10 = int3 + int0] = float2 - float1;
            int int11;
            floats1[int11 = int10 + int0] = float3 - float0;
            floats1[int10 = int11 + int0] = float2 + float1;
            int3 += int2;
            int5 += int4;
        }

        if (int2 >= 2) {
            if (int2 != 2) {
                int3 = 0;

                for (int int12 = 0; int12 < int1; int12++) {
                    int int13;
                    int int14 = (int13 = int5 = (int4 = int3 << 2) + int6) + int6;
                    int int15 = int3;

                    for (byte byte0 = 2; byte0 < int2; byte0 += 2) {
                        int4 += 2;
                        int5 += 2;
                        int13 -= 2;
                        int14 -= 2;
                        int15 += 2;
                        float float4 = floats0[int4] + floats0[int14];
                        float float5 = floats0[int4] - floats0[int14];
                        float float6 = floats0[int5] - floats0[int13];
                        float float7 = floats0[int5] + floats0[int13];
                        float float8 = floats0[int4 - 1] - floats0[int14 - 1];
                        float float9 = floats0[int4 - 1] + floats0[int14 - 1];
                        float float10 = floats0[int5 - 1] - floats0[int13 - 1];
                        float float11 = floats0[int5 - 1] + floats0[int13 - 1];
                        floats1[int15 - 1] = float9 + float11;
                        float float12 = float9 - float11;
                        floats1[int15] = float5 + float6;
                        float float13 = float5 - float6;
                        float float14 = float8 - float7;
                        float float15 = float8 + float7;
                        float float16 = float4 + float10;
                        float float17 = float4 - float10;
                        int int16;
                        floats1[(int16 = int15 + int0) - 1] = floats2[int17 + byte0 - 2] * float14 - floats2[int17 + byte0 - 1] * float16;
                        floats1[int16] = floats2[int17 + byte0 - 2] * float16 + floats2[int17 + byte0 - 1] * float14;
                        int int18;
                        floats1[(int18 = int16 + int0) - 1] = floats3[int19 + byte0 - 2] * float12 - floats3[int19 + byte0 - 1] * float13;
                        floats1[int18] = floats3[int19 + byte0 - 2] * float13 + floats3[int19 + byte0 - 1] * float12;
                        floats1[(int16 = int18 + int0) - 1] = floats4[int20 + byte0 - 2] * float15 - floats4[int20 + byte0 - 1] * float17;
                        floats1[int16] = floats4[int20 + byte0 - 2] * float17 + floats4[int20 + byte0 - 1] * float15;
                    }

                    int3 += int2;
                }

                if (int2 % 2 == 1) {
                    return;
                }
            }

            int3 = int2;
            int4 = int2 << 2;
            int5 = int2 - 1;
            int int21 = int2 + (int2 << 1);

            for (int int22 = 0; int22 < int1; int22++) {
                float float18 = floats0[int3] + floats0[int21];
                float float19 = floats0[int21] - floats0[int3];
                float float20 = floats0[int3 - 1] - floats0[int21 - 1];
                float float21 = floats0[int3 - 1] + floats0[int21 - 1];
                floats1[int5] = float21 + float21;
                int int23;
                floats1[int23 = int5 + int0] = sqrt2 * (float20 - float18);
                int int24;
                floats1[int24 = int23 + int0] = float19 + float19;
                floats1[int23 = int24 + int0] = -sqrt2 * (float20 + float18);
                int5 += int2;
                int3 += int4;
                int21 += int4;
            }
        }
    }

    static void dradbg(
        int int6,
        int int5,
        int int7,
        int int15,
        float[] floats0,
        float[] floats4,
        float[] floats3,
        float[] floats1,
        float[] floats2,
        float[] floats5,
        int int26
    ) {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        float float0 = 0.0F;
        float float1 = 0.0F;
        int int4 = 0;
        short short0 = 100;

        while (true) {
            switch (short0) {
                case 100:
                    int2 = int5 * int6;
                    int1 = int7 * int6;
                    float float2 = tpi / int5;
                    float0 = (float)Math.cos(float2);
                    float1 = (float)Math.sin(float2);
                    int3 = int6 - 1 >>> 1;
                    int4 = int5;
                    int0 = int5 + 1 >>> 1;
                    if (int6 < int7) {
                        short0 = 103;
                        break;
                    }

                    int int8 = 0;
                    int int9 = 0;

                    for (int int10 = 0; int10 < int7; int10++) {
                        int int11 = int8;
                        int int12 = int9;

                        for (int int13 = 0; int13 < int6; int13++) {
                            floats1[int11] = floats0[int12];
                            int11++;
                            int12++;
                        }

                        int8 += int6;
                        int9 += int2;
                    }

                    short0 = 106;
                    break;
                case 103:
                    int int90 = 0;

                    for (int int91 = 0; int91 < int6; int91++) {
                        int int92 = int90;
                        int int93 = int90;

                        for (int int94 = 0; int94 < int7; int94++) {
                            floats1[int92] = floats0[int93];
                            int92 += int6;
                            int93 += int2;
                        }

                        int90++;
                    }
                case 106:
                    int int73 = 0;
                    int int74 = int4 * int1;
                    int int75;
                    int int76 = int75 = int6 << 1;

                    for (int int77 = 1; int77 < int0; int77++) {
                        int73 += int1;
                        int74 -= int1;
                        int int78 = int73;
                        int int79 = int74;
                        int int80 = int75;

                        for (int int81 = 0; int81 < int7; int81++) {
                            floats1[int78] = floats0[int80 - 1] + floats0[int80 - 1];
                            floats1[int79] = floats0[int80] + floats0[int80];
                            int78 += int6;
                            int79 += int6;
                            int80 += int2;
                        }

                        int75 += int76;
                    }

                    if (int6 == 1) {
                        short0 = 116;
                    } else {
                        if (int3 < int7) {
                            short0 = 112;
                            break;
                        }

                        int73 = 0;
                        int74 = int4 * int1;
                        int76 = 0;

                        for (int int82 = 1; int82 < int0; int82++) {
                            int73 += int1;
                            int74 -= int1;
                            int int83 = int73;
                            int int84 = int74;
                            int76 += int6 << 1;
                            int int85 = int76;

                            for (int int86 = 0; int86 < int7; int86++) {
                                int75 = int83;
                                int int87 = int84;
                                int int88 = int85;
                                int int89 = int85;

                                for (byte byte4 = 2; byte4 < int6; byte4 += 2) {
                                    int75 += 2;
                                    int87 += 2;
                                    int88 += 2;
                                    int89 -= 2;
                                    floats1[int75 - 1] = floats0[int88 - 1] + floats0[int89 - 1];
                                    floats1[int87 - 1] = floats0[int88 - 1] - floats0[int89 - 1];
                                    floats1[int75] = floats0[int88] - floats0[int89];
                                    floats1[int87] = floats0[int88] + floats0[int89];
                                }

                                int83 += int6;
                                int84 += int6;
                                int85 += int2;
                            }
                        }

                        short0 = 116;
                    }
                    break;
                case 112:
                    int int60 = 0;
                    int int61 = int4 * int1;
                    int int62 = 0;

                    for (int int63 = 1; int63 < int0; int63++) {
                        int60 += int1;
                        int61 -= int1;
                        int int64 = int60;
                        int int65 = int61;
                        int62 += int6 << 1;
                        int int66 = int62;
                        int int67 = int62;

                        for (byte byte3 = 2; byte3 < int6; byte3 += 2) {
                            int64 += 2;
                            int65 += 2;
                            int66 += 2;
                            int67 -= 2;
                            int int68 = int64;
                            int int69 = int65;
                            int int70 = int66;
                            int int71 = int67;

                            for (int int72 = 0; int72 < int7; int72++) {
                                floats1[int68 - 1] = floats0[int70 - 1] + floats0[int71 - 1];
                                floats1[int69 - 1] = floats0[int70 - 1] - floats0[int71 - 1];
                                floats1[int68] = floats0[int70] - floats0[int71];
                                floats1[int69] = floats0[int70] + floats0[int71];
                                int68 += int6;
                                int69 += int6;
                                int70 += int2;
                                int71 += int2;
                            }
                        }
                    }
                case 116:
                    float float3 = 1.0F;
                    float float4 = 0.0F;
                    int int35 = 0;
                    int int36;
                    int int37 = int36 = int4 * int15;
                    int int38 = (int5 - 1) * int15;

                    for (int int39 = 1; int39 < int0; int39++) {
                        int35 += int15;
                        int36 -= int15;
                        float float5 = float0 * float3 - float1 * float4;
                        float4 = float0 * float4 + float1 * float3;
                        float3 = float5;
                        int int40 = int35;
                        int int41 = int36;
                        int int42 = 0;
                        int int43 = int15;
                        int int44 = int38;

                        for (int int45 = 0; int45 < int15; int45++) {
                            floats3[int40++] = floats2[int42++] + float3 * floats2[int43++];
                            floats3[int41++] = float4 * floats2[int44++];
                        }

                        float float6 = float3;
                        float float7 = float4;
                        float float8 = float3;
                        float float9 = float4;
                        int42 = int15;
                        int43 = int37 - int15;

                        for (int int46 = 2; int46 < int0; int46++) {
                            int42 += int15;
                            int43 -= int15;
                            float float10 = float6 * float8 - float7 * float9;
                            float9 = float6 * float9 + float7 * float8;
                            float8 = float10;
                            int40 = int35;
                            int41 = int36;
                            int int47 = int42;
                            int int48 = int43;

                            for (int int49 = 0; int49 < int15; int49++) {
                                floats3[int40++] += float8 * floats2[int47++];
                                floats3[int41++] += float9 * floats2[int48++];
                            }
                        }
                    }

                    int35 = 0;

                    for (int int50 = 1; int50 < int0; int50++) {
                        int35 += int15;
                        int36 = int35;

                        for (int int51 = 0; int51 < int15; int51++) {
                            floats2[int51] += floats2[int36++];
                        }
                    }

                    int35 = 0;
                    int36 = int4 * int1;

                    for (int int52 = 1; int52 < int0; int52++) {
                        int35 += int1;
                        int36 -= int1;
                        int38 = int35;
                        int int53 = int36;

                        for (int int54 = 0; int54 < int7; int54++) {
                            floats1[int38] = floats4[int38] - floats4[int53];
                            floats1[int53] = floats4[int38] + floats4[int53];
                            int38 += int6;
                            int53 += int6;
                        }
                    }

                    if (int6 == 1) {
                        short0 = 132;
                    } else {
                        if (int3 < int7) {
                            short0 = 128;
                            break;
                        }

                        int35 = 0;
                        int36 = int4 * int1;

                        for (int int55 = 1; int55 < int0; int55++) {
                            int35 += int1;
                            int36 -= int1;
                            int38 = int35;
                            int int56 = int36;

                            for (int int57 = 0; int57 < int7; int57++) {
                                int int58 = int38;
                                int int59 = int56;

                                for (byte byte2 = 2; byte2 < int6; byte2 += 2) {
                                    int58 += 2;
                                    int59 += 2;
                                    floats1[int58 - 1] = floats4[int58 - 1] - floats4[int59];
                                    floats1[int59 - 1] = floats4[int58 - 1] + floats4[int59];
                                    floats1[int58] = floats4[int58] + floats4[int59 - 1];
                                    floats1[int59] = floats4[int58] - floats4[int59 - 1];
                                }

                                int38 += int6;
                                int56 += int6;
                            }
                        }

                        short0 = 132;
                    }
                    break;
                case 128:
                    int int27 = 0;
                    int int28 = int4 * int1;

                    for (int int29 = 1; int29 < int0; int29++) {
                        int27 += int1;
                        int28 -= int1;
                        int int30 = int27;
                        int int31 = int28;

                        for (byte byte1 = 2; byte1 < int6; byte1 += 2) {
                            int30 += 2;
                            int31 += 2;
                            int int32 = int30;
                            int int33 = int31;

                            for (int int34 = 0; int34 < int7; int34++) {
                                floats1[int32 - 1] = floats4[int32 - 1] - floats4[int33];
                                floats1[int33 - 1] = floats4[int32 - 1] + floats4[int33];
                                floats1[int32] = floats4[int32] + floats4[int33 - 1];
                                floats1[int33] = floats4[int32] - floats4[int33 - 1];
                                int32 += int6;
                                int33 += int6;
                            }
                        }
                    }
                case 132:
                    if (int6 == 1) {
                        return;
                    }

                    for (int int14 = 0; int14 < int15; int14++) {
                        floats3[int14] = floats2[int14];
                    }

                    int int16 = 0;

                    for (int int17 = 1; int17 < int5; int17++) {
                        int int18 = int16 += int1;

                        for (int int19 = 0; int19 < int7; int19++) {
                            floats4[int18] = floats1[int18];
                            int18 += int6;
                        }
                    }

                    if (int3 <= int7) {
                        int int20 = -int6 - 1;
                        int16 = 0;

                        for (int int21 = 1; int21 < int5; int21++) {
                            int20 += int6;
                            int16 += int1;
                            int int22 = int20;
                            int int23 = int16;

                            for (byte byte0 = 2; byte0 < int6; byte0 += 2) {
                                int23 += 2;
                                int22 += 2;
                                int int24 = int23;

                                for (int int25 = 0; int25 < int7; int25++) {
                                    floats4[int24 - 1] = floats5[int26 + int22 - 1] * floats1[int24 - 1] - floats5[int26 + int22] * floats1[int24];
                                    floats4[int24] = floats5[int26 + int22 - 1] * floats1[int24] + floats5[int26 + int22] * floats1[int24 - 1];
                                    int24 += int6;
                                }
                            }
                        }

                        return;
                    }

                    short0 = 139;
                    break;
                case 139:
                    int int95 = -int6 - 1;
                    int int96 = 0;

                    for (int int97 = 1; int97 < int5; int97++) {
                        int95 += int6;
                        int96 += int1;
                        int int98 = int96;

                        for (int int99 = 0; int99 < int7; int99++) {
                            int int100 = int95;
                            int int101 = int98;

                            for (byte byte5 = 2; byte5 < int6; byte5 += 2) {
                                int100 += 2;
                                int101 += 2;
                                floats4[int101 - 1] = floats5[int26 + int100 - 1] * floats1[int101 - 1] - floats5[int26 + int100] * floats1[int101];
                                floats4[int101] = floats5[int26 + int100 - 1] * floats1[int101] + floats5[int26 + int100] * floats1[int101 - 1];
                            }

                            int98 += int6;
                        }
                    }

                    return;
            }
        }
    }

    static void dradf2(int int4, int int3, float[] floats0, float[] floats1, float[] floats2, int int11) {
        int int0 = 0;
        int int1;
        int int2 = int1 = int3 * int4;
        int int5 = int4 << 1;

        for (int int6 = 0; int6 < int3; int6++) {
            floats1[int0 << 1] = floats0[int0] + floats0[int1];
            floats1[(int0 << 1) + int5 - 1] = floats0[int0] - floats0[int1];
            int0 += int4;
            int1 += int4;
        }

        if (int4 >= 2) {
            if (int4 != 2) {
                int0 = 0;
                int1 = int2;

                for (int int7 = 0; int7 < int3; int7++) {
                    int5 = int1;
                    int int8 = (int0 << 1) + (int4 << 1);
                    int int9 = int0;
                    int int10 = int0 + int0;

                    for (byte byte0 = 2; byte0 < int4; byte0 += 2) {
                        int5 += 2;
                        int8 -= 2;
                        int9 += 2;
                        int10 += 2;
                        float float0 = floats2[int11 + byte0 - 2] * floats0[int5 - 1] + floats2[int11 + byte0 - 1] * floats0[int5];
                        float float1 = floats2[int11 + byte0 - 2] * floats0[int5] - floats2[int11 + byte0 - 1] * floats0[int5 - 1];
                        floats1[int10] = floats0[int9] + float1;
                        floats1[int8] = float1 - floats0[int9];
                        floats1[int10 - 1] = floats0[int9 - 1] + float0;
                        floats1[int8 - 1] = floats0[int9 - 1] - float0;
                    }

                    int0 += int4;
                    int1 += int4;
                }

                if (int4 % 2 == 1) {
                    return;
                }
            }

            int0 = int4;
            int5 = int1 = int4 - 1;
            int1 += int2;

            for (int int12 = 0; int12 < int3; int12++) {
                floats1[int0] = -floats0[int1];
                floats1[int0 - 1] = floats0[int5];
                int0 += int4 << 1;
                int1 += int4;
                int5 += int4;
            }
        }
    }

    static void dradf4(int int2, int int1, float[] floats0, float[] floats1, float[] floats2, int int13, float[] floats3, int int14, float[] floats4, int int15) {
        int int0 = int1 * int2;
        int int3 = int0;
        int int4 = int0 << 1;
        int int5 = int0 + (int0 << 1);
        int int6 = 0;

        for (int int7 = 0; int7 < int1; int7++) {
            float float0 = floats0[int3] + floats0[int5];
            float float1 = floats0[int6] + floats0[int4];
            int int8;
            floats1[int8 = int6 << 2] = float0 + float1;
            floats1[(int2 << 2) + int8 - 1] = float1 - float0;
            int int9;
            floats1[(int9 = int8 + (int2 << 1)) - 1] = floats0[int6] - floats0[int4];
            floats1[int9] = floats0[int5] - floats0[int3];
            int3 += int2;
            int5 += int2;
            int6 += int2;
            int4 += int2;
        }

        if (int2 >= 2) {
            if (int2 != 2) {
                int3 = 0;

                for (int int10 = 0; int10 < int1; int10++) {
                    int5 = int3;
                    int4 = int3 << 2;
                    int int11;
                    int int12 = (int11 = int2 << 1) + int4;

                    for (byte byte0 = 2; byte0 < int2; byte0 += 2) {
                        int5 += 2;
                        int4 += 2;
                        int12 -= 2;
                        int6 = int5 + int0;
                        float float2 = floats2[int13 + byte0 - 2] * floats0[int6 - 1] + floats2[int13 + byte0 - 1] * floats0[int6];
                        float float3 = floats2[int13 + byte0 - 2] * floats0[int6] - floats2[int13 + byte0 - 1] * floats0[int6 - 1];
                        int6 += int0;
                        float float4 = floats3[int14 + byte0 - 2] * floats0[int6 - 1] + floats3[int14 + byte0 - 1] * floats0[int6];
                        float float5 = floats3[int14 + byte0 - 2] * floats0[int6] - floats3[int14 + byte0 - 1] * floats0[int6 - 1];
                        int6 += int0;
                        float float6 = floats4[int15 + byte0 - 2] * floats0[int6 - 1] + floats4[int15 + byte0 - 1] * floats0[int6];
                        float float7 = floats4[int15 + byte0 - 2] * floats0[int6] - floats4[int15 + byte0 - 1] * floats0[int6 - 1];
                        float float8 = float2 + float6;
                        float float9 = float6 - float2;
                        float float10 = float3 + float7;
                        float float11 = float3 - float7;
                        float float12 = floats0[int5] + float5;
                        float float13 = floats0[int5] - float5;
                        float float14 = floats0[int5 - 1] + float4;
                        float float15 = floats0[int5 - 1] - float4;
                        floats1[int4 - 1] = float8 + float14;
                        floats1[int4] = float10 + float12;
                        floats1[int12 - 1] = float15 - float11;
                        floats1[int12] = float9 - float13;
                        floats1[int4 + int11 - 1] = float11 + float15;
                        floats1[int4 + int11] = float9 + float13;
                        floats1[int12 + int11 - 1] = float14 - float8;
                        floats1[int12 + int11] = float10 - float12;
                    }

                    int3 += int2;
                }

                if ((int2 & 1) != 0) {
                    return;
                }
            }

            int5 = (int3 = int0 + int2 - 1) + (int0 << 1);
            int6 = int2 << 2;
            int4 = int2;
            int int16 = int2 << 1;
            int int17 = int2;

            for (int int18 = 0; int18 < int1; int18++) {
                float float16 = -hsqt2 * (floats0[int3] + floats0[int5]);
                float float17 = hsqt2 * (floats0[int3] - floats0[int5]);
                floats1[int4 - 1] = float17 + floats0[int17 - 1];
                floats1[int4 + int16 - 1] = floats0[int17 - 1] - float17;
                floats1[int4] = float16 - floats0[int3 + int0];
                floats1[int4 + int16] = float16 + floats0[int3 + int0];
                int3 += int2;
                int5 += int2;
                int4 += int6;
                int17 += int2;
            }
        }
    }

    static void dradfg(
        int int5,
        int int1,
        int int8,
        int int30,
        float[] floats1,
        float[] floats4,
        float[] floats3,
        float[] floats0,
        float[] floats2,
        float[] floats5,
        int int61
    ) {
        int int0 = 0;
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = tpi / int1;
        float0 = (float)Math.cos(float2);
        float1 = (float)Math.sin(float2);
        int int2 = int1 + 1 >> 1;
        int int3 = int1;
        int int4 = int5;
        int int6 = int5 - 1 >> 1;
        int int7 = int8 * int5;
        int int9 = int1 * int5;
        short short0 = 100;

        while (true) {
            switch (short0) {
                case 101:
                    if (int5 == 1) {
                        short0 = 119;
                        break;
                    } else {
                        for (int int52 = 0; int52 < int30; int52++) {
                            floats2[int52] = floats3[int52];
                        }

                        int int53 = 0;

                        for (int int54 = 1; int54 < int1; int54++) {
                            int53 += int7;
                            int0 = int53;

                            for (int int55 = 0; int55 < int8; int55++) {
                                floats0[int0] = floats4[int0];
                                int0 += int5;
                            }
                        }

                        int int56 = -int5;
                        int53 = 0;
                        if (int6 > int8) {
                            for (int int57 = 1; int57 < int1; int57++) {
                                int53 += int7;
                                int56 += int5;
                                int0 = -int5 + int53;

                                for (int int58 = 0; int58 < int8; int58++) {
                                    int int59 = int56 - 1;
                                    int0 += int5;
                                    int int60 = int0;

                                    for (byte byte1 = 2; byte1 < int5; byte1 += 2) {
                                        int59 += 2;
                                        int60 += 2;
                                        floats0[int60 - 1] = floats5[int61 + int59 - 1] * floats4[int60 - 1] + floats5[int61 + int59] * floats4[int60];
                                        floats0[int60] = floats5[int61 + int59 - 1] * floats4[int60] - floats5[int61 + int59] * floats4[int60 - 1];
                                    }
                                }
                            }
                        } else {
                            for (int int62 = 1; int62 < int1; int62++) {
                                int56 += int5;
                                int int63 = int56 - 1;
                                int53 += int7;
                                int0 = int53;

                                for (byte byte2 = 2; byte2 < int5; byte2 += 2) {
                                    int63 += 2;
                                    int0 += 2;
                                    int int64 = int0;

                                    for (int int65 = 0; int65 < int8; int65++) {
                                        floats0[int64 - 1] = floats5[int61 + int63 - 1] * floats4[int64 - 1] + floats5[int61 + int63] * floats4[int64];
                                        floats0[int64] = floats5[int61 + int63 - 1] * floats4[int64] - floats5[int61 + int63] * floats4[int64 - 1];
                                        int64 += int5;
                                    }
                                }
                            }
                        }

                        int53 = 0;
                        int0 = int3 * int7;
                        if (int6 < int8) {
                            for (int int66 = 1; int66 < int2; int66++) {
                                int53 += int7;
                                int0 -= int7;
                                int int67 = int53;
                                int int68 = int0;

                                for (byte byte3 = 2; byte3 < int5; byte3 += 2) {
                                    int67 += 2;
                                    int68 += 2;
                                    int int69 = int67 - int5;
                                    int int70 = int68 - int5;

                                    for (int int71 = 0; int71 < int8; int71++) {
                                        int69 += int5;
                                        int70 += int5;
                                        floats4[int69 - 1] = floats0[int69 - 1] + floats0[int70 - 1];
                                        floats4[int70 - 1] = floats0[int69] - floats0[int70];
                                        floats4[int69] = floats0[int69] + floats0[int70];
                                        floats4[int70] = floats0[int70 - 1] - floats0[int69 - 1];
                                    }
                                }
                            }
                        } else {
                            for (int int72 = 1; int72 < int2; int72++) {
                                int53 += int7;
                                int0 -= int7;
                                int int73 = int53;
                                int int74 = int0;

                                for (int int75 = 0; int75 < int8; int75++) {
                                    int int76 = int73;
                                    int int77 = int74;

                                    for (byte byte4 = 2; byte4 < int5; byte4 += 2) {
                                        int76 += 2;
                                        int77 += 2;
                                        floats4[int76 - 1] = floats0[int76 - 1] + floats0[int77 - 1];
                                        floats4[int77 - 1] = floats0[int76] - floats0[int77];
                                        floats4[int76] = floats0[int76] + floats0[int77];
                                        floats4[int77] = floats0[int77 - 1] - floats0[int76 - 1];
                                    }

                                    int73 += int5;
                                    int74 += int5;
                                }
                            }
                        }
                    }
                case 119:
                    for (int int29 = 0; int29 < int30; int29++) {
                        floats3[int29] = floats2[int29];
                    }

                    int int31 = 0;
                    int0 = int3 * int30;

                    for (int int32 = 1; int32 < int2; int32++) {
                        int31 += int7;
                        int0 -= int7;
                        int int33 = int31 - int5;
                        int int34 = int0 - int5;

                        for (int int35 = 0; int35 < int8; int35++) {
                            int33 += int5;
                            int34 += int5;
                            floats4[int33] = floats0[int33] + floats0[int34];
                            floats4[int34] = floats0[int34] - floats0[int33];
                        }
                    }

                    float float3 = 1.0F;
                    float float4 = 0.0F;
                    int31 = 0;
                    int0 = int3 * int30;
                    int int36 = (int1 - 1) * int30;

                    for (int int37 = 1; int37 < int2; int37++) {
                        int31 += int30;
                        int0 -= int30;
                        float float5 = float0 * float3 - float1 * float4;
                        float4 = float0 * float4 + float1 * float3;
                        float3 = float5;
                        int int38 = int31;
                        int int39 = int0;
                        int int40 = int36;
                        int int41 = int30;

                        for (int int42 = 0; int42 < int30; int42++) {
                            floats2[int38++] = floats3[int42] + float3 * floats3[int41++];
                            floats2[int39++] = float4 * floats3[int40++];
                        }

                        float float6 = float3;
                        float float7 = float4;
                        float float8 = float3;
                        float float9 = float4;
                        int38 = int30;
                        int39 = (int3 - 1) * int30;

                        for (int int43 = 2; int43 < int2; int43++) {
                            int38 += int30;
                            int39 -= int30;
                            float float10 = float6 * float8 - float7 * float9;
                            float9 = float6 * float9 + float7 * float8;
                            float8 = float10;
                            int40 = int31;
                            int41 = int0;
                            int int44 = int38;
                            int int45 = int39;

                            for (int int46 = 0; int46 < int30; int46++) {
                                floats2[int40++] += float8 * floats3[int44++];
                                floats2[int41++] += float9 * floats3[int45++];
                            }
                        }
                    }

                    int31 = 0;

                    for (int int47 = 1; int47 < int2; int47++) {
                        int31 += int30;
                        int0 = int31;

                        for (int int48 = 0; int48 < int30; int48++) {
                            floats2[int48] += floats3[int0++];
                        }
                    }

                    if (int5 < int8) {
                        short0 = 132;
                        break;
                    }

                    int31 = 0;
                    int0 = 0;

                    for (int int49 = 0; int49 < int8; int49++) {
                        int36 = int31;
                        int int50 = int0;

                        for (int int51 = 0; int51 < int5; int51++) {
                            floats1[int50++] = floats0[int36++];
                        }

                        int31 += int5;
                        int0 += int9;
                    }

                    short0 = 135;
                    break;
                case 132:
                    for (int int26 = 0; int26 < int5; int26++) {
                        int int27 = int26;
                        int0 = int26;

                        for (int int28 = 0; int28 < int8; int28++) {
                            floats1[int0] = floats0[int27];
                            int27 += int5;
                            int0 += int9;
                        }
                    }
                case 135:
                    int int10 = 0;
                    int0 = int5 << 1;
                    int int11 = 0;
                    int int12 = int3 * int7;

                    for (int int13 = 1; int13 < int2; int13++) {
                        int10 += int0;
                        int11 += int7;
                        int12 -= int7;
                        int int14 = int10;
                        int int15 = int11;
                        int int16 = int12;

                        for (int int17 = 0; int17 < int8; int17++) {
                            floats1[int14 - 1] = floats0[int15];
                            floats1[int14] = floats0[int16];
                            int14 += int9;
                            int15 += int5;
                            int16 += int5;
                        }
                    }

                    if (int5 == 1) {
                        return;
                    }

                    if (int6 >= int8) {
                        int10 = -int5;
                        int11 = 0;
                        int12 = 0;
                        int int18 = int3 * int7;

                        for (int int19 = 1; int19 < int2; int19++) {
                            int10 += int0;
                            int11 += int0;
                            int12 += int7;
                            int18 -= int7;
                            int int20 = int10;
                            int int21 = int11;
                            int int22 = int12;
                            int int23 = int18;

                            for (int int24 = 0; int24 < int8; int24++) {
                                for (byte byte0 = 2; byte0 < int5; byte0 += 2) {
                                    int int25 = int4 - byte0;
                                    floats1[byte0 + int21 - 1] = floats0[byte0 + int22 - 1] + floats0[byte0 + int23 - 1];
                                    floats1[int25 + int20 - 1] = floats0[byte0 + int22 - 1] - floats0[byte0 + int23 - 1];
                                    floats1[byte0 + int21] = floats0[byte0 + int22] + floats0[byte0 + int23];
                                    floats1[int25 + int20] = floats0[byte0 + int23] - floats0[byte0 + int22];
                                }

                                int20 += int9;
                                int21 += int9;
                                int22 += int5;
                                int23 += int5;
                            }
                        }

                        return;
                    }

                    short0 = 141;
                    break;
                case 141:
                    int int78 = -int5;
                    int int79 = 0;
                    int int80 = 0;
                    int int81 = int3 * int7;

                    for (int int82 = 1; int82 < int2; int82++) {
                        int78 += int0;
                        int79 += int0;
                        int80 += int7;
                        int81 -= int7;

                        for (byte byte5 = 2; byte5 < int5; byte5 += 2) {
                            int int83 = int4 + int78 - byte5;
                            int int84 = byte5 + int79;
                            int int85 = byte5 + int80;
                            int int86 = byte5 + int81;

                            for (int int87 = 0; int87 < int8; int87++) {
                                floats1[int84 - 1] = floats0[int85 - 1] + floats0[int86 - 1];
                                floats1[int83 - 1] = floats0[int85 - 1] - floats0[int86 - 1];
                                floats1[int84] = floats0[int85] + floats0[int86];
                                floats1[int83] = floats0[int86] - floats0[int85];
                                int83 += int9;
                                int84 += int9;
                                int85 += int5;
                                int86 += int5;
                            }
                        }
                    }

                    return;
            }
        }
    }

    static void drftb1(int int11, float[] floats1, float[] floats0, float[] floats2, int int10, int[] ints) {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        int int4 = ints[1];
        int int5 = 0;
        int int6 = 1;
        int int7 = 1;

        for (int int8 = 0; int8 < int4; int8++) {
            byte byte0 = 100;

            label68:
            while (true) {
                switch (byte0) {
                    case 100:
                        int1 = ints[int8 + 2];
                        int0 = int1 * int6;
                        int2 = int11 / int0;
                        int3 = int2 * int6;
                        if (int1 != 4) {
                            byte0 = 103;
                        } else {
                            int int12 = int7 + int2;
                            int int13 = int12 + int2;
                            if (int5 != 0) {
                                dradb4(int2, int6, floats0, floats1, floats2, int10 + int7 - 1, floats2, int10 + int12 - 1, floats2, int10 + int13 - 1);
                            } else {
                                dradb4(int2, int6, floats1, floats0, floats2, int10 + int7 - 1, floats2, int10 + int12 - 1, floats2, int10 + int13 - 1);
                            }

                            int5 = 1 - int5;
                            byte0 = 115;
                        }
                        break;
                    case 103:
                        if (int1 != 2) {
                            byte0 = 106;
                        } else {
                            if (int5 != 0) {
                                dradb2(int2, int6, floats0, floats1, floats2, int10 + int7 - 1);
                            } else {
                                dradb2(int2, int6, floats1, floats0, floats2, int10 + int7 - 1);
                            }

                            int5 = 1 - int5;
                            byte0 = 115;
                        }
                        break;
                    case 106:
                        if (int1 != 3) {
                            byte0 = 109;
                        } else {
                            int int9 = int7 + int2;
                            if (int5 != 0) {
                                dradb3(int2, int6, floats0, floats1, floats2, int10 + int7 - 1, floats2, int10 + int9 - 1);
                            } else {
                                dradb3(int2, int6, floats1, floats0, floats2, int10 + int7 - 1, floats2, int10 + int9 - 1);
                            }

                            int5 = 1 - int5;
                            byte0 = 115;
                        }
                        break;
                    case 109:
                        if (int5 != 0) {
                            dradbg(int2, int1, int6, int3, floats0, floats0, floats0, floats1, floats1, floats2, int10 + int7 - 1);
                        } else {
                            dradbg(int2, int1, int6, int3, floats1, floats1, floats1, floats0, floats0, floats2, int10 + int7 - 1);
                        }

                        if (int2 == 1) {
                            int5 = 1 - int5;
                        }
                    case 115:
                        break label68;
                }
            }

            int6 = int0;
            int7 += (int1 - 1) * int2;
        }

        if (int5 != 0) {
            for (int int14 = 0; int14 < int11; int14++) {
                floats1[int14] = floats0[int14];
            }
        }
    }

    static void drftf1(int int3, float[] floats1, float[] floats0, float[] floats2, int[] ints) {
        int int0 = ints[1];
        int int1 = 1;
        int int2 = int3;
        int int4 = int3;

        for (int int5 = 0; int5 < int0; int5++) {
            int int6 = int0 - int5;
            int int7 = ints[int6 + 1];
            int int8 = int2 / int7;
            int int9 = int3 / int2;
            int int10 = int9 * int8;
            int4 -= (int7 - 1) * int9;
            int1 = 1 - int1;
            byte byte0 = 100;

            label59:
            while (true) {
                switch (byte0) {
                    case 100:
                        if (int7 != 4) {
                            byte0 = 102;
                        } else {
                            int int11 = int4 + int9;
                            int int12 = int11 + int9;
                            if (int1 != 0) {
                                dradf4(int9, int8, floats0, floats1, floats2, int4 - 1, floats2, int11 - 1, floats2, int12 - 1);
                            } else {
                                dradf4(int9, int8, floats1, floats0, floats2, int4 - 1, floats2, int11 - 1, floats2, int12 - 1);
                            }

                            byte0 = 110;
                        }
                    case 101:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    default:
                        break;
                    case 102:
                        if (int7 != 2) {
                            byte0 = 104;
                        } else if (int1 != 0) {
                            byte0 = 103;
                        } else {
                            dradf2(int9, int8, floats1, floats0, floats2, int4 - 1);
                            byte0 = 110;
                        }
                        break;
                    case 103:
                        dradf2(int9, int8, floats0, floats1, floats2, int4 - 1);
                    case 104:
                        if (int9 == 1) {
                            int1 = 1 - int1;
                        }

                        if (int1 != 0) {
                            byte0 = 109;
                        } else {
                            dradfg(int9, int7, int8, int10, floats1, floats1, floats1, floats0, floats0, floats2, int4 - 1);
                            int1 = 1;
                            byte0 = 110;
                        }
                        break;
                    case 109:
                        dradfg(int9, int7, int8, int10, floats0, floats0, floats0, floats1, floats1, floats2, int4 - 1);
                        int1 = 0;
                    case 110:
                        break label59;
                }
            }

            int2 = int8;
        }

        if (int1 != 1) {
            for (int int13 = 0; int13 < int3; int13++) {
                floats1[int13] = floats0[int13];
            }
        }
    }

    static void drfti1(int int3, float[] floats, int int16, int[] ints) {
        int int0 = 0;
        int int1 = -1;
        int int2 = int3;
        int int4 = 0;
        byte byte0 = 101;

        while (true) {
            switch (byte0) {
                case 101:
                    if (++int1 < 4) {
                        int0 = ntryh[int1];
                    } else {
                        int0 += 2;
                    }
                case 104:
                    int int17 = int2 / int0;
                    int int18 = int2 - int0 * int17;
                    if (int18 != 0) {
                        byte0 = 101;
                        break;
                    } else {
                        ints[++int4 + 1] = int0;
                        int2 = int17;
                        if (int0 != 2) {
                            byte0 = 107;
                            break;
                        } else if (int4 == 1) {
                            byte0 = 107;
                            break;
                        } else {
                            for (int int19 = 1; int19 < int4; int19++) {
                                int int20 = int4 - int19 + 1;
                                ints[int20 + 1] = ints[int20];
                            }

                            ints[2] = 2;
                        }
                    }
                case 107:
                    if (int2 == 1) {
                        ints[0] = int3;
                        ints[1] = int4;
                        float float0 = tpi / int3;
                        int int5 = 0;
                        int int6 = int4 - 1;
                        int int7 = 1;
                        if (int6 == 0) {
                            return;
                        }

                        for (int int8 = 0; int8 < int6; int8++) {
                            int int9 = ints[int8 + 2];
                            int int10 = 0;
                            int int11 = int7 * int9;
                            int int12 = int3 / int11;
                            int int13 = int9 - 1;

                            for (int int14 = 0; int14 < int13; int14++) {
                                int10 += int7;
                                int int15 = int5;
                                float float1 = int10 * float0;
                                float float2 = 0.0F;

                                for (byte byte1 = 2; byte1 < int12; byte1 += 2) {
                                    float float3 = ++float2 * float1;
                                    floats[int16 + int15++] = (float)Math.cos(float3);
                                    floats[int16 + int15++] = (float)Math.sin(float3);
                                }

                                int5 += int12;
                            }

                            int7 = int11;
                        }

                        return;
                    }

                    byte0 = 104;
            }
        }
    }

    static void fdrffti(int int0, float[] floats, int[] ints) {
        if (int0 != 1) {
            drfti1(int0, floats, int0, ints);
        }
    }

    void backward(float[] floats) {
        if (this.n != 1) {
            drftb1(this.n, floats, this.trigcache, this.trigcache, this.n, this.splitcache);
        }
    }

    void clear() {
        if (this.trigcache != null) {
            this.trigcache = null;
        }

        if (this.splitcache != null) {
            this.splitcache = null;
        }
    }

    void init(int int0) {
        this.n = int0;
        this.trigcache = new float[3 * int0];
        this.splitcache = new int[32];
        fdrffti(int0, this.trigcache, this.splitcache);
    }
}
