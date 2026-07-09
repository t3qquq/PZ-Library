// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;
import java.util.HashMap;

abstract class Residue {
    protected int begin;
    protected int end;
    protected int partitionSize;
    protected int classifications;
    protected int classBook;
    protected int[] cascade;
    protected int[][] books;
    protected HashMap looks = new HashMap();

    protected Residue() {
    }

    protected Residue(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        this.begin = bitInputStream.getInt(24);
        this.end = bitInputStream.getInt(24);
        this.partitionSize = bitInputStream.getInt(24) + 1;
        this.classifications = bitInputStream.getInt(6) + 1;
        this.classBook = bitInputStream.getInt(8);
        this.cascade = new int[this.classifications];
        int int0 = 0;

        for (int int1 = 0; int1 < this.classifications; int1++) {
            int int2 = 0;
            int int3 = 0;
            int3 = bitInputStream.getInt(3);
            if (bitInputStream.getBit()) {
                int2 = bitInputStream.getInt(5);
            }

            this.cascade[int1] = int2 << 3 | int3;
            int0 += Util.icount(this.cascade[int1]);
        }

        this.books = new int[this.classifications][8];

        for (int int4 = 0; int4 < this.classifications; int4++) {
            for (int int5 = 0; int5 < 8; int5++) {
                if ((this.cascade[int4] & 1 << int5) != 0) {
                    this.books[int4][int5] = bitInputStream.getInt(8);
                    if (this.books[int4][int5] > setupHeader.getCodeBooks().length) {
                        throw new VorbisFormatException("Reference to invalid codebook entry in residue header.");
                    }
                }
            }
        }
    }

    protected static Residue createInstance(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        int int0 = bitInputStream.getInt(16);
        switch (int0) {
            case 0:
                return new Residue0(bitInputStream, setupHeader);
            case 1:
                return new Residue2(bitInputStream, setupHeader);
            case 2:
                return new Residue2(bitInputStream, setupHeader);
            default:
                throw new VorbisFormatException("Residue type " + int0 + " is not supported.");
        }
    }

    protected abstract int getType();

    protected abstract void decodeResidue(VorbisStream var1, BitInputStream var2, Mode var3, int var4, boolean[] var5, float[][] var6) throws VorbisFormatException, IOException;

    protected int getBegin() {
        return this.begin;
    }

    protected int getEnd() {
        return this.end;
    }

    protected int getPartitionSize() {
        return this.partitionSize;
    }

    protected int getClassifications() {
        return this.classifications;
    }

    protected int getClassBook() {
        return this.classBook;
    }

    protected int[] getCascade() {
        return this.cascade;
    }

    protected int[][] getBooks() {
        return this.books;
    }

    protected final void fill(Residue residue1) {
        residue1.begin = this.begin;
        residue1.books = this.books;
        residue1.cascade = this.cascade;
        residue1.classBook = this.classBook;
        residue1.classifications = this.classifications;
        residue1.end = this.end;
        residue1.partitionSize = this.partitionSize;
    }

    protected Residue.Look getLook(VorbisStream vorbisStream, Mode mode) {
        Residue.Look look = (Residue.Look)this.looks.get(mode);
        if (look == null) {
            look = new Residue.Look(vorbisStream, mode);
            this.looks.put(mode, look);
        }

        return look;
    }

    class Look {
        int map;
        int parts;
        int stages;
        CodeBook[] fullbooks;
        CodeBook phrasebook;
        int[][] partbooks;
        int partvals;
        int[][] decodemap;
        int postbits;
        int phrasebits;
        int frames;

        protected Look(VorbisStream vorbisStream, Mode mode) {
            int int0 = 0;
            boolean boolean0 = false;
            int int1 = 0;
            this.map = mode.getMapping();
            this.parts = Residue.this.getClassifications();
            this.fullbooks = vorbisStream.getSetupHeader().getCodeBooks();
            this.phrasebook = this.fullbooks[Residue.this.getClassBook()];
            int0 = this.phrasebook.getDimensions();
            this.partbooks = new int[this.parts][];

            for (int int2 = 0; int2 < this.parts; int2++) {
                int int3 = Util.ilog(Residue.this.getCascade()[int2]);
                if (int3 != 0) {
                    if (int3 > int1) {
                        int1 = int3;
                    }

                    this.partbooks[int2] = new int[int3];

                    for (int int4 = 0; int4 < int3; int4++) {
                        if ((Residue.this.getCascade()[int2] & 1 << int4) != 0) {
                            this.partbooks[int2][int4] = Residue.this.getBooks()[int2][int4];
                        }
                    }
                }
            }

            this.partvals = (int)Math.rint(Math.pow(this.parts, int0));
            this.stages = int1;
            this.decodemap = new int[this.partvals][];

            for (int int5 = 0; int5 < this.partvals; int5++) {
                int int6 = int5;
                int int7 = this.partvals / this.parts;
                this.decodemap[int5] = new int[int0];

                for (int int8 = 0; int8 < int0; int8++) {
                    int int9 = int6 / int7;
                    int6 -= int9 * int7;
                    int7 /= this.parts;
                    this.decodemap[int5][int8] = int9;
                }
            }
        }

        protected int[][] getDecodeMap() {
            return this.decodemap;
        }

        protected int getFrames() {
            return this.frames;
        }

        protected int getMap() {
            return this.map;
        }

        protected int[][] getPartBooks() {
            return this.partbooks;
        }

        protected int getParts() {
            return this.parts;
        }

        protected int getPartVals() {
            return this.partvals;
        }

        protected int getPhraseBits() {
            return this.phrasebits;
        }

        protected CodeBook getPhraseBook() {
            return this.phrasebook;
        }

        protected int getPostBits() {
            return this.postbits;
        }

        protected int getStages() {
            return this.stages;
        }
    }
}
