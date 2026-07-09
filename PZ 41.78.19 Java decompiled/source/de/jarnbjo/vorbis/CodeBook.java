// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import de.jarnbjo.util.io.HuffmanNode;
import java.io.IOException;
import java.util.Arrays;

class CodeBook {
    private HuffmanNode huffmanRoot;
    private int dimensions;
    private int entries;
    private int[] entryLengths;
    private float[][] valueVector;
    private static long totalTime = 0L;

    protected CodeBook(BitInputStream bitInputStream) throws VorbisFormatException, IOException {
        if (bitInputStream.getInt(24) != 5653314) {
            throw new VorbisFormatException("The code book sync pattern is not correct.");
        } else {
            this.dimensions = bitInputStream.getInt(16);
            this.entries = bitInputStream.getInt(24);
            this.entryLengths = new int[this.entries];
            boolean boolean0 = bitInputStream.getBit();
            if (boolean0) {
                int int0 = bitInputStream.getInt(5) + 1;
                int int1 = 0;

                while (int1 < this.entryLengths.length) {
                    int int2 = bitInputStream.getInt(Util.ilog(this.entryLengths.length - int1));
                    if (int1 + int2 > this.entryLengths.length) {
                        throw new VorbisFormatException("The codebook entry length list is longer than the actual number of entry lengths.");
                    }

                    Arrays.fill(this.entryLengths, int1, int1 + int2, int0);
                    int0++;
                    int1 += int2;
                }
            } else {
                boolean boolean1 = bitInputStream.getBit();
                if (boolean1) {
                    for (int int3 = 0; int3 < this.entryLengths.length; int3++) {
                        if (bitInputStream.getBit()) {
                            this.entryLengths[int3] = bitInputStream.getInt(5) + 1;
                        } else {
                            this.entryLengths[int3] = -1;
                        }
                    }
                } else {
                    for (int int4 = 0; int4 < this.entryLengths.length; int4++) {
                        this.entryLengths[int4] = bitInputStream.getInt(5) + 1;
                    }
                }
            }

            if (!this.createHuffmanTree(this.entryLengths)) {
                throw new VorbisFormatException("An exception was thrown when building the codebook Huffman tree.");
            } else {
                int int5 = bitInputStream.getInt(4);
                switch (int5) {
                    case 1:
                    case 2:
                        float float0 = Util.float32unpack(bitInputStream.getInt(32));
                        float float1 = Util.float32unpack(bitInputStream.getInt(32));
                        int int6 = bitInputStream.getInt(4) + 1;
                        boolean boolean2 = bitInputStream.getBit();
                        int int7 = 0;
                        if (int5 == 1) {
                            int7 = Util.lookup1Values(this.entries, this.dimensions);
                        } else {
                            int7 = this.entries * this.dimensions;
                        }

                        int[] ints = new int[int7];

                        for (int int8 = 0; int8 < ints.length; int8++) {
                            ints[int8] = bitInputStream.getInt(int6);
                        }

                        this.valueVector = new float[this.entries][this.dimensions];
                        if (int5 != 1) {
                            throw new UnsupportedOperationException();
                        } else {
                            for (int int9 = 0; int9 < this.entries; int9++) {
                                float float2 = 0.0F;
                                int int10 = 1;

                                for (int int11 = 0; int11 < this.dimensions; int11++) {
                                    int int12 = int9 / int10 % int7;
                                    this.valueVector[int9][int11] = ints[int12] * float1 + float0 + float2;
                                    if (boolean2) {
                                        float2 = this.valueVector[int9][int11];
                                    }

                                    int10 *= int7;
                                }
                            }
                        }
                    case 0:
                        return;
                    default:
                        throw new VorbisFormatException("Unsupported codebook lookup type: " + int5);
                }
            }
        }
    }

    private boolean createHuffmanTree(int[] ints) {
        this.huffmanRoot = new HuffmanNode();

        for (int int0 = 0; int0 < ints.length; int0++) {
            int int1 = ints[int0];
            if (int1 > 0 && !this.huffmanRoot.setNewValue(int1, int0)) {
                return false;
            }
        }

        return true;
    }

    protected int getDimensions() {
        return this.dimensions;
    }

    protected int getEntries() {
        return this.entries;
    }

    protected HuffmanNode getHuffmanRoot() {
        return this.huffmanRoot;
    }

    protected int readInt(BitInputStream bitInputStream) throws IOException {
        return bitInputStream.getInt(this.huffmanRoot);
    }

    protected void readVvAdd(float[][] floats0, BitInputStream bitInputStream, int int3, int int4) throws VorbisFormatException, IOException {
        int int0 = 0;
        int int1 = floats0.length;
        if (int1 != 0) {
            int int2 = (int3 + int4) / int1;
            int int5 = int3 / int1;

            while (int5 < int2) {
                float[] floats1 = this.valueVector[bitInputStream.getInt(this.huffmanRoot)];

                for (int int6 = 0; int6 < this.dimensions; int6++) {
                    int int7 = int0++;
                    floats0[int7][int5] = floats0[int7][int5] + floats1[int6];
                    if (int0 == int1) {
                        int0 = 0;
                        int5++;
                    }
                }
            }
        }
    }
}
