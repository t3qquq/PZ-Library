// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

class Floor1 extends Floor implements Cloneable {
    private int[] partitionClassList;
    private int maximumClass;
    private int multiplier;
    private int rangeBits;
    private int[] classDimensions;
    private int[] classSubclasses;
    private int[] classMasterbooks;
    private int[][] subclassBooks;
    private int[] xList;
    private int[] yList;
    private int[] lowNeighbours;
    private int[] highNeighbours;
    private static final int[] RANGES = new int[]{256, 128, 86, 64};

    private Floor1() {
    }

    protected Floor1(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        this.maximumClass = -1;
        int int0 = bitInputStream.getInt(5);
        this.partitionClassList = new int[int0];

        for (int int1 = 0; int1 < this.partitionClassList.length; int1++) {
            this.partitionClassList[int1] = bitInputStream.getInt(4);
            if (this.partitionClassList[int1] > this.maximumClass) {
                this.maximumClass = this.partitionClassList[int1];
            }
        }

        this.classDimensions = new int[this.maximumClass + 1];
        this.classSubclasses = new int[this.maximumClass + 1];
        this.classMasterbooks = new int[this.maximumClass + 1];
        this.subclassBooks = new int[this.maximumClass + 1][];
        int int2 = 2;

        for (int int3 = 0; int3 <= this.maximumClass; int3++) {
            this.classDimensions[int3] = bitInputStream.getInt(3) + 1;
            int2 += this.classDimensions[int3];
            this.classSubclasses[int3] = bitInputStream.getInt(2);
            if (this.classDimensions[int3] > setupHeader.getCodeBooks().length || this.classSubclasses[int3] > setupHeader.getCodeBooks().length) {
                throw new VorbisFormatException("There is a class dimension or class subclasses entry higher than the number of codebooks in the setup header.");
            }

            if (this.classSubclasses[int3] != 0) {
                this.classMasterbooks[int3] = bitInputStream.getInt(8);
            }

            this.subclassBooks[int3] = new int[1 << this.classSubclasses[int3]];

            for (int int4 = 0; int4 < this.subclassBooks[int3].length; int4++) {
                this.subclassBooks[int3][int4] = bitInputStream.getInt(8) - 1;
            }
        }

        this.multiplier = bitInputStream.getInt(2) + 1;
        this.rangeBits = bitInputStream.getInt(4);
        boolean boolean0 = false;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Integer(0));
        arrayList.add(new Integer(1 << this.rangeBits));

        for (int int5 = 0; int5 < int0; int5++) {
            for (int int6 = 0; int6 < this.classDimensions[this.partitionClassList[int5]]; int6++) {
                arrayList.add(new Integer(bitInputStream.getInt(this.rangeBits)));
            }
        }

        this.xList = new int[arrayList.size()];
        this.lowNeighbours = new int[this.xList.length];
        this.highNeighbours = new int[this.xList.length];
        Iterator iterator = arrayList.iterator();

        for (int int7 = 0; int7 < this.xList.length; int7++) {
            this.xList[int7] = (Integer)iterator.next();
        }

        for (int int8 = 0; int8 < this.xList.length; int8++) {
            this.lowNeighbours[int8] = Util.lowNeighbour(this.xList, int8);
            this.highNeighbours[int8] = Util.highNeighbour(this.xList, int8);
        }
    }

    @Override
    protected int getType() {
        return 1;
    }

    @Override
    protected Floor decodeFloor(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
        if (!bitInputStream.getBit()) {
            return null;
        } else {
            Floor1 floor10 = (Floor1)this.clone();
            floor10.yList = new int[this.xList.length];
            int int0 = RANGES[this.multiplier - 1];
            floor10.yList[0] = bitInputStream.getInt(Util.ilog(int0 - 1));
            floor10.yList[1] = bitInputStream.getInt(Util.ilog(int0 - 1));
            int int1 = 2;

            for (int int2 = 0; int2 < this.partitionClassList.length; int2++) {
                int int3 = this.partitionClassList[int2];
                int int4 = this.classDimensions[int3];
                int int5 = this.classSubclasses[int3];
                int int6 = (1 << int5) - 1;
                int int7 = 0;
                if (int5 > 0) {
                    int7 = bitInputStream.getInt(vorbisStream.getSetupHeader().getCodeBooks()[this.classMasterbooks[int3]].getHuffmanRoot());
                }

                for (int int8 = 0; int8 < int4; int8++) {
                    int int9 = this.subclassBooks[int3][int7 & int6];
                    int7 >>>= int5;
                    if (int9 >= 0) {
                        floor10.yList[int8 + int1] = bitInputStream.getInt(vorbisStream.getSetupHeader().getCodeBooks()[int9].getHuffmanRoot());
                    } else {
                        floor10.yList[int8 + int1] = 0;
                    }
                }

                int1 += int4;
            }

            return floor10;
        }
    }

    @Override
    protected void computeFloor(float[] floats0) {
        int int0 = floats0.length;
        int int1 = this.xList.length;
        boolean[] booleans = new boolean[int1];
        int int2 = RANGES[this.multiplier - 1];

        for (int int3 = 2; int3 < int1; int3++) {
            int int4 = this.lowNeighbours[int3];
            int int5 = this.highNeighbours[int3];
            int int6 = Util.renderPoint(this.xList[int4], this.xList[int5], this.yList[int4], this.yList[int5], this.xList[int3]);
            int int7 = this.yList[int3];
            int int8 = int2 - int6;
            int int9 = int8 < int6 ? int8 * 2 : int6 * 2;
            if (int7 != 0) {
                booleans[int4] = true;
                booleans[int5] = true;
                booleans[int3] = true;
                if (int7 >= int9) {
                    this.yList[int3] = int8 > int6 ? int7 - int6 + int6 : -int7 + int8 + int6 - 1;
                } else {
                    this.yList[int3] = (int7 & 1) == 1 ? int6 - (int7 + 1 >> 1) : int6 + (int7 >> 1);
                }
            } else {
                booleans[int3] = false;
                this.yList[int3] = int6;
            }
        }

        int[] ints = new int[int1];
        System.arraycopy(this.xList, 0, ints, 0, int1);
        sort(ints, this.yList, booleans);
        int int10 = 0;
        int int11 = 0;
        int int12 = 0;
        int int13 = this.yList[0] * this.multiplier;
        float[] floats1 = new float[floats0.length];
        float[] floats2 = new float[floats0.length];
        Arrays.fill(floats1, 1.0F);
        System.arraycopy(floats0, 0, floats2, 0, floats0.length);

        for (int int14 = 1; int14 < int1; int14++) {
            if (booleans[int14]) {
                int11 = this.yList[int14] * this.multiplier;
                int10 = ints[int14];
                Util.renderLine(int12, int13, int10, int11, floats0);
                Util.renderLine(int12, int13, int10, int11, floats1);
                int12 = int10;
                int13 = int11;
            }
        }

        float float0 = DB_STATIC_TABLE[int11];

        while (int10 < int0 / 2) {
            floats0[int10++] = float0;
        }
    }

    @Override
    public Object clone() {
        Floor1 floor10 = new Floor1();
        floor10.classDimensions = this.classDimensions;
        floor10.classMasterbooks = this.classMasterbooks;
        floor10.classSubclasses = this.classSubclasses;
        floor10.maximumClass = this.maximumClass;
        floor10.multiplier = this.multiplier;
        floor10.partitionClassList = this.partitionClassList;
        floor10.rangeBits = this.rangeBits;
        floor10.subclassBooks = this.subclassBooks;
        floor10.xList = this.xList;
        floor10.yList = this.yList;
        floor10.lowNeighbours = this.lowNeighbours;
        floor10.highNeighbours = this.highNeighbours;
        return floor10;
    }

    private static final void sort(int[] ints0, int[] ints1, boolean[] booleans) {
        byte byte0 = 0;
        int int0 = ints0.length;
        int int1 = int0 + byte0;

        for (int int2 = byte0; int2 < int1; int2++) {
            for (int int3 = int2; int3 > byte0 && ints0[int3 - 1] > ints0[int3]; int3--) {
                int int4 = ints0[int3];
                ints0[int3] = ints0[int3 - 1];
                ints0[int3 - 1] = int4;
                int4 = ints1[int3];
                ints1[int3] = ints1[int3 - 1];
                ints1[int3 - 1] = int4;
                boolean boolean0 = booleans[int3];
                booleans[int3] = booleans[int3 - 1];
                booleans[int3 - 1] = boolean0;
            }
        }
    }

    private static final void swap(int[] ints, int int1, int int2) {
        int int0 = ints[int1];
        ints[int1] = ints[int2];
        ints[int2] = int0;
    }

    private static final void swap(boolean[] booleans, int int0, int int1) {
        boolean boolean0 = booleans[int0];
        booleans[int0] = booleans[int1];
        booleans[int1] = boolean0;
    }
}
