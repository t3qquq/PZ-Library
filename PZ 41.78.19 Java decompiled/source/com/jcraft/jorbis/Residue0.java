// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class Residue0 extends FuncResidue {
    private static int[][][] _01inverse_partword = new int[2][][];
    static int[][] _2inverse_partword = null;

    static synchronized int _01inverse(Block block, Object object, float[][] floats, int int5, int int16) {
        Residue0.LookResidue0 lookResidue0 = (Residue0.LookResidue0)object;
        Residue0.InfoResidue0 infoResidue0 = lookResidue0.info;
        int int0 = infoResidue0.grouping;
        int int1 = lookResidue0.phrasebook.dim;
        int int2 = infoResidue0.end - infoResidue0.begin;
        int int3 = int2 / int0;
        int int4 = (int3 + int1 - 1) / int1;
        if (_01inverse_partword.length < int5) {
            _01inverse_partword = new int[int5][][];
        }

        for (int int6 = 0; int6 < int5; int6++) {
            if (_01inverse_partword[int6] == null || _01inverse_partword[int6].length < int4) {
                _01inverse_partword[int6] = new int[int4][];
            }
        }

        for (int int7 = 0; int7 < lookResidue0.stages; int7++) {
            int int8 = 0;

            for (int int9 = 0; int8 < int3; int9++) {
                if (int7 == 0) {
                    for (int int10 = 0; int10 < int5; int10++) {
                        int int11 = lookResidue0.phrasebook.decode(block.opb);
                        if (int11 == -1) {
                            return 0;
                        }

                        _01inverse_partword[int10][int9] = lookResidue0.decodemap[int11];
                        if (_01inverse_partword[int10][int9] == null) {
                            return 0;
                        }
                    }
                }

                for (int int12 = 0; int12 < int1 && int8 < int3; int8++) {
                    for (int int13 = 0; int13 < int5; int13++) {
                        int int14 = infoResidue0.begin + int8 * int0;
                        int int15 = _01inverse_partword[int13][int9][int12];
                        if ((infoResidue0.secondstages[int15] & 1 << int7) != 0) {
                            CodeBook codeBook = lookResidue0.fullbooks[lookResidue0.partbooks[int15][int7]];
                            if (codeBook != null) {
                                if (int16 == 0) {
                                    if (codeBook.decodevs_add(floats[int13], int14, block.opb, int0) == -1) {
                                        return 0;
                                    }
                                } else if (int16 == 1 && codeBook.decodev_add(floats[int13], int14, block.opb, int0) == -1) {
                                    return 0;
                                }
                            }
                        }
                    }

                    int12++;
                }
            }
        }

        return 0;
    }

    static synchronized int _2inverse(Block block, Object object, float[][] floats, int int12) {
        Residue0.LookResidue0 lookResidue0 = (Residue0.LookResidue0)object;
        Residue0.InfoResidue0 infoResidue0 = lookResidue0.info;
        int int0 = infoResidue0.grouping;
        int int1 = lookResidue0.phrasebook.dim;
        int int2 = infoResidue0.end - infoResidue0.begin;
        int int3 = int2 / int0;
        int int4 = (int3 + int1 - 1) / int1;
        if (_2inverse_partword == null || _2inverse_partword.length < int4) {
            _2inverse_partword = new int[int4][];
        }

        for (int int5 = 0; int5 < lookResidue0.stages; int5++) {
            int int6 = 0;

            for (int int7 = 0; int6 < int3; int7++) {
                if (int5 == 0) {
                    int int8 = lookResidue0.phrasebook.decode(block.opb);
                    if (int8 == -1) {
                        return 0;
                    }

                    _2inverse_partword[int7] = lookResidue0.decodemap[int8];
                    if (_2inverse_partword[int7] == null) {
                        return 0;
                    }
                }

                for (int int9 = 0; int9 < int1 && int6 < int3; int6++) {
                    int int10 = infoResidue0.begin + int6 * int0;
                    int int11 = _2inverse_partword[int7][int9];
                    if ((infoResidue0.secondstages[int11] & 1 << int5) != 0) {
                        CodeBook codeBook = lookResidue0.fullbooks[lookResidue0.partbooks[int11][int5]];
                        if (codeBook != null && codeBook.decodevv_add(floats, int10, int12, block.opb, int0) == -1) {
                            return 0;
                        }
                    }

                    int9++;
                }
            }
        }

        return 0;
    }

    @Override
    void free_info(Object var1) {
    }

    @Override
    void free_look(Object var1) {
    }

    @Override
    int inverse(Block block, Object object, float[][] floats, int[] ints, int int2) {
        int int0 = 0;

        for (int int1 = 0; int1 < int2; int1++) {
            if (ints[int1] != 0) {
                floats[int0++] = floats[int1];
            }
        }

        return int0 != 0 ? _01inverse(block, object, floats, int0, 0) : 0;
    }

    @Override
    Object look(DspState dspState, InfoMode infoMode, Object object) {
        Residue0.InfoResidue0 infoResidue0 = (Residue0.InfoResidue0)object;
        Residue0.LookResidue0 lookResidue0 = new Residue0.LookResidue0();
        int int0 = 0;
        int int1 = 0;
        lookResidue0.info = infoResidue0;
        lookResidue0.map = infoMode.mapping;
        lookResidue0.parts = infoResidue0.partitions;
        lookResidue0.fullbooks = dspState.fullbooks;
        lookResidue0.phrasebook = dspState.fullbooks[infoResidue0.groupbook];
        int int2 = lookResidue0.phrasebook.dim;
        lookResidue0.partbooks = new int[lookResidue0.parts][];

        for (int int3 = 0; int3 < lookResidue0.parts; int3++) {
            int int4 = infoResidue0.secondstages[int3];
            int int5 = Util.ilog(int4);
            if (int5 != 0) {
                if (int5 > int1) {
                    int1 = int5;
                }

                lookResidue0.partbooks[int3] = new int[int5];

                for (int int6 = 0; int6 < int5; int6++) {
                    if ((int4 & 1 << int6) != 0) {
                        lookResidue0.partbooks[int3][int6] = infoResidue0.booklist[int0++];
                    }
                }
            }
        }

        lookResidue0.partvals = (int)Math.rint(Math.pow(lookResidue0.parts, int2));
        lookResidue0.stages = int1;
        lookResidue0.decodemap = new int[lookResidue0.partvals][];

        for (int int7 = 0; int7 < lookResidue0.partvals; int7++) {
            int int8 = int7;
            int int9 = lookResidue0.partvals / lookResidue0.parts;
            lookResidue0.decodemap[int7] = new int[int2];

            for (int int10 = 0; int10 < int2; int10++) {
                int int11 = int8 / int9;
                int8 -= int11 * int9;
                int9 /= lookResidue0.parts;
                lookResidue0.decodemap[int7][int10] = int11;
            }
        }

        return lookResidue0;
    }

    @Override
    void pack(Object object, Buffer buffer) {
        Residue0.InfoResidue0 infoResidue0 = (Residue0.InfoResidue0)object;
        int int0 = 0;
        buffer.write(infoResidue0.begin, 24);
        buffer.write(infoResidue0.end, 24);
        buffer.write(infoResidue0.grouping - 1, 24);
        buffer.write(infoResidue0.partitions - 1, 6);
        buffer.write(infoResidue0.groupbook, 8);

        for (int int1 = 0; int1 < infoResidue0.partitions; int1++) {
            int int2 = infoResidue0.secondstages[int1];
            if (Util.ilog(int2) > 3) {
                buffer.write(int2, 3);
                buffer.write(1, 1);
                buffer.write(int2 >>> 3, 5);
            } else {
                buffer.write(int2, 4);
            }

            int0 += Util.icount(int2);
        }

        for (int int3 = 0; int3 < int0; int3++) {
            buffer.write(infoResidue0.booklist[int3], 8);
        }
    }

    @Override
    Object unpack(Info info, Buffer buffer) {
        int int0 = 0;
        Residue0.InfoResidue0 infoResidue0 = new Residue0.InfoResidue0();
        infoResidue0.begin = buffer.read(24);
        infoResidue0.end = buffer.read(24);
        infoResidue0.grouping = buffer.read(24) + 1;
        infoResidue0.partitions = buffer.read(6) + 1;
        infoResidue0.groupbook = buffer.read(8);

        for (int int1 = 0; int1 < infoResidue0.partitions; int1++) {
            int int2 = buffer.read(3);
            if (buffer.read(1) != 0) {
                int2 |= buffer.read(5) << 3;
            }

            infoResidue0.secondstages[int1] = int2;
            int0 += Util.icount(int2);
        }

        for (int int3 = 0; int3 < int0; int3++) {
            infoResidue0.booklist[int3] = buffer.read(8);
        }

        if (infoResidue0.groupbook >= info.books) {
            this.free_info(infoResidue0);
            return null;
        } else {
            for (int int4 = 0; int4 < int0; int4++) {
                if (infoResidue0.booklist[int4] >= info.books) {
                    this.free_info(infoResidue0);
                    return null;
                }
            }

            return infoResidue0;
        }
    }

    class InfoResidue0 {
        float[] ampmax = new float[64];
        int begin;
        int[] blimit = new int[64];
        int[] booklist = new int[256];
        int end;
        float[] entmax = new float[64];
        int groupbook;
        int grouping;
        int partitions;
        int[] secondstages = new int[64];
        int[] subgrp = new int[64];
    }

    class LookResidue0 {
        int[][] decodemap;
        int frames;
        CodeBook[] fullbooks;
        Residue0.InfoResidue0 info;
        int map;
        int[][] partbooks;
        int parts;
        int partvals;
        int phrasebits;
        CodeBook phrasebook;
        int postbits;
        int stages;
    }
}
