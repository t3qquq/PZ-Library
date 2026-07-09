// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class Floor0 extends FuncFloor {
    float[] lsp = null;

    static float fromdB(float float0) {
        return (float)Math.exp(float0 * 0.11512925);
    }

    static void lpc_to_curve(float[] floats1, float[] floats2, float float0, Floor0.LookFloor0 lookFloor0, String var4, int var5) {
        float[] floats0 = new float[Math.max(lookFloor0.ln * 2, lookFloor0.m * 2 + 2)];
        if (float0 == 0.0F) {
            for (int int0 = 0; int0 < lookFloor0.n; int0++) {
                floats1[int0] = 0.0F;
            }
        } else {
            lookFloor0.lpclook.lpc_to_curve(floats0, floats2, float0);

            for (int int1 = 0; int1 < lookFloor0.n; int1++) {
                floats1[int1] = floats0[lookFloor0.linearmap[int1]];
            }
        }
    }

    static void lsp_to_lpc(float[] floats6, float[] floats7, int int1) {
        int int0 = int1 / 2;
        float[] floats0 = new float[int0];
        float[] floats1 = new float[int0];
        float[] floats2 = new float[int0 + 1];
        float[] floats3 = new float[int0 + 1];
        float[] floats4 = new float[int0];
        float[] floats5 = new float[int0];

        for (int int2 = 0; int2 < int0; int2++) {
            floats0[int2] = (float)(-2.0 * Math.cos(floats6[int2 * 2]));
            floats1[int2] = (float)(-2.0 * Math.cos(floats6[int2 * 2 + 1]));
        }

        int int3;
        for (int3 = 0; int3 < int0; int3++) {
            floats2[int3] = 0.0F;
            floats3[int3] = 1.0F;
            floats4[int3] = 0.0F;
            floats5[int3] = 1.0F;
        }

        floats3[int3] = 1.0F;
        floats2[int3] = 1.0F;

        for (int int4 = 1; int4 < int1 + 1; int4++) {
            float float0 = 0.0F;
            float float1 = 0.0F;

            for (int3 = 0; int3 < int0; int3++) {
                float float2 = floats0[int3] * floats3[int3] + floats2[int3];
                floats2[int3] = floats3[int3];
                floats3[int3] = float1;
                float1 += float2;
                float2 = floats1[int3] * floats5[int3] + floats4[int3];
                floats4[int3] = floats5[int3];
                floats5[int3] = float0;
                float0 += float2;
            }

            floats7[int4 - 1] = (float1 + floats3[int3] + float0 - floats2[int3]) / 2.0F;
            floats3[int3] = float1;
            floats2[int3] = float0;
        }
    }

    static float toBARK(float float0) {
        return (float)(13.1 * Math.atan(7.4E-4 * float0) + 2.24 * Math.atan(float0 * float0 * 1.85E-8) + 1.0E-4 * float0);
    }

    @Override
    int forward(Block var1, Object var2, float[] var3, float[] var4, Object var5) {
        return 0;
    }

    @Override
    void free_info(Object var1) {
    }

    @Override
    void free_look(Object var1) {
    }

    @Override
    void free_state(Object var1) {
    }

    int inverse(Block block, Object object, float[] floats) {
        Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object;
        Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
        int int0 = block.opb.read(infoFloor0.ampbits);
        if (int0 > 0) {
            int int1 = (1 << infoFloor0.ampbits) - 1;
            float float0 = (float)int0 / int1 * infoFloor0.ampdB;
            int int2 = block.opb.read(Util.ilog(infoFloor0.numbooks));
            if (int2 != -1 && int2 < infoFloor0.numbooks) {
                synchronized (this) {
                    if (this.lsp != null && this.lsp.length >= lookFloor0.m) {
                        for (int int3 = 0; int3 < lookFloor0.m; int3++) {
                            this.lsp[int3] = 0.0F;
                        }
                    } else {
                        this.lsp = new float[lookFloor0.m];
                    }

                    CodeBook codeBook = block.vd.fullbooks[infoFloor0.books[int2]];
                    float float1 = 0.0F;

                    for (int int4 = 0; int4 < lookFloor0.m; int4++) {
                        floats[int4] = 0.0F;
                    }

                    for (int int5 = 0; int5 < lookFloor0.m; int5 += codeBook.dim) {
                        if (codeBook.decodevs(this.lsp, int5, block.opb, 1, -1) == -1) {
                            for (int int6 = 0; int6 < lookFloor0.n; int6++) {
                                floats[int6] = 0.0F;
                            }

                            return 0;
                        }
                    }

                    for (int int7 = 0; int7 < lookFloor0.m; float1 = this.lsp[int7 - 1]) {
                        for (int int8 = 0; int8 < codeBook.dim; int7++) {
                            this.lsp[int7] = this.lsp[int7] + float1;
                            int8++;
                        }
                    }

                    Lsp.lsp_to_curve(floats, lookFloor0.linearmap, lookFloor0.n, lookFloor0.ln, this.lsp, lookFloor0.m, float0, infoFloor0.ampdB);
                    return 1;
                }
            }
        }

        return 0;
    }

    @Override
    Object inverse1(Block block, Object object0, Object object1) {
        Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object0;
        Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
        float[] floats = null;
        if (object1 instanceof float[]) {
            floats = (float[])object1;
        }

        int int0 = block.opb.read(infoFloor0.ampbits);
        if (int0 > 0) {
            int int1 = (1 << infoFloor0.ampbits) - 1;
            float float0 = (float)int0 / int1 * infoFloor0.ampdB;
            int int2 = block.opb.read(Util.ilog(infoFloor0.numbooks));
            if (int2 != -1 && int2 < infoFloor0.numbooks) {
                CodeBook codeBook = block.vd.fullbooks[infoFloor0.books[int2]];
                float float1 = 0.0F;
                if (floats != null && floats.length >= lookFloor0.m + 1) {
                    for (int int3 = 0; int3 < floats.length; int3++) {
                        floats[int3] = 0.0F;
                    }
                } else {
                    floats = new float[lookFloor0.m + 1];
                }

                for (int int4 = 0; int4 < lookFloor0.m; int4 += codeBook.dim) {
                    if (codeBook.decodev_set(floats, int4, block.opb, codeBook.dim) == -1) {
                        return null;
                    }
                }

                for (int int5 = 0; int5 < lookFloor0.m; float1 = floats[int5 - 1]) {
                    for (int int6 = 0; int6 < codeBook.dim; int5++) {
                        floats[int5] += float1;
                        int6++;
                    }
                }

                floats[lookFloor0.m] = float0;
                return floats;
            }
        }

        return null;
    }

    @Override
    int inverse2(Block var1, Object object0, Object object1, float[] floats1) {
        Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object0;
        Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
        if (object1 != null) {
            float[] floats0 = (float[])object1;
            float float0 = floats0[lookFloor0.m];
            Lsp.lsp_to_curve(floats1, lookFloor0.linearmap, lookFloor0.n, lookFloor0.ln, floats0, lookFloor0.m, float0, infoFloor0.ampdB);
            return 1;
        } else {
            for (int int0 = 0; int0 < lookFloor0.n; int0++) {
                floats1[int0] = 0.0F;
            }

            return 0;
        }
    }

    @Override
    Object look(DspState dspState, InfoMode infoMode, Object object) {
        Info info = dspState.vi;
        Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
        Floor0.LookFloor0 lookFloor0 = new Floor0.LookFloor0();
        lookFloor0.m = infoFloor0.order;
        lookFloor0.n = info.blocksizes[infoMode.blockflag] / 2;
        lookFloor0.ln = infoFloor0.barkmap;
        lookFloor0.vi = infoFloor0;
        lookFloor0.lpclook.init(lookFloor0.ln, lookFloor0.m);
        float float0 = lookFloor0.ln / toBARK((float)(infoFloor0.rate / 2.0));
        lookFloor0.linearmap = new int[lookFloor0.n];

        for (int int0 = 0; int0 < lookFloor0.n; int0++) {
            int int1 = (int)Math.floor(toBARK((float)(infoFloor0.rate / 2.0 / lookFloor0.n * int0)) * float0);
            if (int1 >= lookFloor0.ln) {
                int1 = lookFloor0.ln;
            }

            lookFloor0.linearmap[int0] = int1;
        }

        return lookFloor0;
    }

    @Override
    void pack(Object object, Buffer buffer) {
        Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
        buffer.write(infoFloor0.order, 8);
        buffer.write(infoFloor0.rate, 16);
        buffer.write(infoFloor0.barkmap, 16);
        buffer.write(infoFloor0.ampbits, 6);
        buffer.write(infoFloor0.ampdB, 8);
        buffer.write(infoFloor0.numbooks - 1, 4);

        for (int int0 = 0; int0 < infoFloor0.numbooks; int0++) {
            buffer.write(infoFloor0.books[int0], 8);
        }
    }

    Object state(Object object) {
        Floor0.EchstateFloor0 echstateFloor0 = new Floor0.EchstateFloor0();
        Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
        echstateFloor0.codewords = new int[infoFloor0.order];
        echstateFloor0.curve = new float[infoFloor0.barkmap];
        echstateFloor0.frameno = -1L;
        return echstateFloor0;
    }

    @Override
    Object unpack(Info info, Buffer buffer) {
        Floor0.InfoFloor0 infoFloor0 = new Floor0.InfoFloor0();
        infoFloor0.order = buffer.read(8);
        infoFloor0.rate = buffer.read(16);
        infoFloor0.barkmap = buffer.read(16);
        infoFloor0.ampbits = buffer.read(6);
        infoFloor0.ampdB = buffer.read(8);
        infoFloor0.numbooks = buffer.read(4) + 1;
        if (infoFloor0.order >= 1 && infoFloor0.rate >= 1 && infoFloor0.barkmap >= 1 && infoFloor0.numbooks >= 1) {
            for (int int0 = 0; int0 < infoFloor0.numbooks; int0++) {
                infoFloor0.books[int0] = buffer.read(8);
                if (infoFloor0.books[int0] < 0 || infoFloor0.books[int0] >= info.books) {
                    return null;
                }
            }

            return infoFloor0;
        } else {
            return null;
        }
    }

    class EchstateFloor0 {
        long codes;
        int[] codewords;
        float[] curve;
        long frameno;
    }

    class InfoFloor0 {
        int ampbits;
        int ampdB;
        int barkmap;
        int[] books = new int[16];
        int numbooks;
        int order;
        int rate;
    }

    class LookFloor0 {
        int[] linearmap;
        int ln;
        Lpc lpclook = new Lpc();
        int m;
        int n;
        Floor0.InfoFloor0 vi;
    }
}
