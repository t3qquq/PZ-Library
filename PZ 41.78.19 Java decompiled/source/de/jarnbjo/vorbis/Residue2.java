// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class Residue2 extends Residue {
    private double[][] decodedVectors;

    private Residue2() {
    }

    protected Residue2(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        super(bitInputStream, setupHeader);
    }

    @Override
    protected int getType() {
        return 2;
    }

    @Override
    protected void decodeResidue(VorbisStream vorbisStream, BitInputStream bitInputStream, Mode mode, int var4, boolean[] booleans, float[][] floats1) throws VorbisFormatException, IOException {
        Residue.Look look = this.getLook(vorbisStream, mode);
        CodeBook codeBook0 = vorbisStream.getSetupHeader().getCodeBooks()[this.getClassBook()];
        int int0 = codeBook0.getDimensions();
        int int1 = this.getEnd() - this.getBegin();
        int int2 = int1 / this.getPartitionSize();
        int int3 = this.getPartitionSize();
        int int4 = look.getPhraseBook().getDimensions();
        int int5 = (int2 + int4 - 1) / int4;
        int int6 = 0;

        for (int int7 = 0; int7 < booleans.length; int7++) {
            if (!booleans[int7]) {
                int6++;
            }
        }

        float[][] floats0 = new float[int6][];
        int6 = 0;

        for (int int8 = 0; int8 < booleans.length; int8++) {
            if (!booleans[int8]) {
                floats0[int6++] = floats1[int8];
            }
        }

        int[][] ints = new int[int5][];

        for (int int9 = 0; int9 < look.getStages(); int9++) {
            int int10 = 0;

            for (int int11 = 0; int10 < int2; int11++) {
                if (int9 == 0) {
                    int int12 = bitInputStream.getInt(look.getPhraseBook().getHuffmanRoot());
                    if (int12 == -1) {
                        throw new VorbisFormatException("");
                    }

                    ints[int11] = look.getDecodeMap()[int12];
                    if (ints[int11] == null) {
                        throw new VorbisFormatException("");
                    }
                }

                for (int int13 = 0; int13 < int4 && int10 < int2; int10++) {
                    int int14 = this.begin + int10 * int3;
                    if ((this.cascade[ints[int11][int13]] & 1 << int9) != 0) {
                        CodeBook codeBook1 = vorbisStream.getSetupHeader().getCodeBooks()[look.getPartBooks()[ints[int11][int13]][int9]];
                        if (codeBook1 != null) {
                            codeBook1.readVvAdd(floats0, bitInputStream, int14, int3);
                        }
                    }

                    int13++;
                }
            }
        }
    }

    @Override
    public Object clone() {
        Residue2 residue20 = new Residue2();
        this.fill(residue20);
        return residue20;
    }

    protected double[][] getDecodedVectors() {
        return this.decodedVectors;
    }
}
