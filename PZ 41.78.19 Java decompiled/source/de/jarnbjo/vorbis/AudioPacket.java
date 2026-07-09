// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class AudioPacket {
    private int modeNumber;
    private Mode mode;
    private Mapping mapping;
    private int n;
    private boolean blockFlag;
    private boolean previousWindowFlag;
    private boolean nextWindowFlag;
    private int windowCenter;
    private int leftWindowStart;
    private int leftWindowEnd;
    private int leftN;
    private int rightWindowStart;
    private int rightWindowEnd;
    private int rightN;
    private float[] window;
    private float[][] pcm;
    private int[][] pcmInt;
    private Floor[] channelFloors;
    private boolean[] noResidues;
    private static final float[][] windows = new float[8][];

    protected AudioPacket(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
        SetupHeader setupHeader = vorbisStream.getSetupHeader();
        IdentificationHeader identificationHeader = vorbisStream.getIdentificationHeader();
        Mode[] modes = setupHeader.getModes();
        Mapping[] mappings = setupHeader.getMappings();
        Residue[] residues0 = setupHeader.getResidues();
        int int0 = identificationHeader.getChannels();
        if (bitInputStream.getInt(1) != 0) {
            throw new VorbisFormatException("Packet type mismatch when trying to create an audio packet.");
        } else {
            this.modeNumber = bitInputStream.getInt(Util.ilog(modes.length - 1));

            try {
                this.mode = modes[this.modeNumber];
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                throw new VorbisFormatException("Reference to invalid mode in audio packet.");
            }

            this.mapping = mappings[this.mode.getMapping()];
            int[] ints0 = this.mapping.getMagnitudes();
            int[] ints1 = this.mapping.getAngles();
            this.blockFlag = this.mode.getBlockFlag();
            int int1 = identificationHeader.getBlockSize0();
            int int2 = identificationHeader.getBlockSize1();
            this.n = this.blockFlag ? int2 : int1;
            if (this.blockFlag) {
                this.previousWindowFlag = bitInputStream.getBit();
                this.nextWindowFlag = bitInputStream.getBit();
            }

            this.windowCenter = this.n / 2;
            if (this.blockFlag && !this.previousWindowFlag) {
                this.leftWindowStart = this.n / 4 - int1 / 4;
                this.leftWindowEnd = this.n / 4 + int1 / 4;
                this.leftN = int1 / 2;
            } else {
                this.leftWindowStart = 0;
                this.leftWindowEnd = this.n / 2;
                this.leftN = this.windowCenter;
            }

            if (this.blockFlag && !this.nextWindowFlag) {
                this.rightWindowStart = this.n * 3 / 4 - int1 / 4;
                this.rightWindowEnd = this.n * 3 / 4 + int1 / 4;
                this.rightN = int1 / 2;
            } else {
                this.rightWindowStart = this.windowCenter;
                this.rightWindowEnd = this.n;
                this.rightN = this.n / 2;
            }

            this.window = this.getComputedWindow();
            this.channelFloors = new Floor[int0];
            this.noResidues = new boolean[int0];
            this.pcm = new float[int0][this.n];
            this.pcmInt = new int[int0][this.n];
            boolean boolean0 = true;

            for (int int3 = 0; int3 < int0; int3++) {
                int int4 = this.mapping.getMux()[int3];
                int int5 = this.mapping.getSubmapFloors()[int4];
                Floor floor = setupHeader.getFloors()[int5].decodeFloor(vorbisStream, bitInputStream);
                this.channelFloors[int3] = floor;
                this.noResidues[int3] = floor == null;
                if (floor != null) {
                    boolean0 = false;
                }
            }

            if (!boolean0) {
                for (int int6 = 0; int6 < ints0.length; int6++) {
                    if (!this.noResidues[ints0[int6]] || !this.noResidues[ints1[int6]]) {
                        this.noResidues[ints0[int6]] = false;
                        this.noResidues[ints1[int6]] = false;
                    }
                }

                Residue[] residues1 = new Residue[this.mapping.getSubmaps()];

                for (int int7 = 0; int7 < this.mapping.getSubmaps(); int7++) {
                    int int8 = 0;
                    boolean[] booleans = new boolean[int0];

                    for (int int9 = 0; int9 < int0; int9++) {
                        if (this.mapping.getMux()[int9] == int7) {
                            booleans[int8++] = this.noResidues[int9];
                        }
                    }

                    int int10 = this.mapping.getSubmapResidues()[int7];
                    Residue residue = residues0[int10];
                    residue.decodeResidue(vorbisStream, bitInputStream, this.mode, int8, booleans, this.pcm);
                }

                for (int int11 = this.mapping.getCouplingSteps() - 1; int11 >= 0; int11--) {
                    double double0 = 0.0;
                    double double1 = 0.0;
                    float[] floats0 = this.pcm[ints0[int11]];
                    float[] floats1 = this.pcm[ints1[int11]];

                    for (int int12 = 0; int12 < floats0.length; int12++) {
                        float float0 = floats1[int12];
                        float float1 = floats0[int12];
                        if (float0 > 0.0F) {
                            floats1[int12] = float1 > 0.0F ? float1 - float0 : float1 + float0;
                        } else {
                            floats0[int12] = float1 > 0.0F ? float1 + float0 : float1 - float0;
                            floats1[int12] = float1;
                        }
                    }
                }

                for (int int13 = 0; int13 < int0; int13++) {
                    if (this.channelFloors[int13] != null) {
                        this.channelFloors[int13].computeFloor(this.pcm[int13]);
                    }
                }

                for (int int14 = 0; int14 < int0; int14++) {
                    MdctFloat mdctFloat = this.blockFlag ? identificationHeader.getMdct1() : identificationHeader.getMdct0();
                    mdctFloat.imdct(this.pcm[int14], this.window, this.pcmInt[int14]);
                }
            }
        }
    }

    private float[] getComputedWindow() {
        int int0 = (this.blockFlag ? 4 : 0) + (this.previousWindowFlag ? 2 : 0) + (this.nextWindowFlag ? 1 : 0);
        float[] floats = windows[int0];
        if (floats == null) {
            floats = new float[this.n];

            for (int int1 = 0; int1 < this.leftN; int1++) {
                float float0 = (float)((int1 + 0.5) / this.leftN * Math.PI / 2.0);
                float0 = (float)Math.sin(float0);
                float0 *= float0;
                float0 = (float)(float0 * (float) (Math.PI / 2));
                float0 = (float)Math.sin(float0);
                floats[int1 + this.leftWindowStart] = float0;
            }

            int int2 = this.leftWindowEnd;

            while (int2 < this.rightWindowStart) {
                floats[int2++] = 1.0F;
            }

            for (int int3 = 0; int3 < this.rightN; int3++) {
                float float1 = (float)((this.rightN - int3 - 0.5) / this.rightN * Math.PI / 2.0);
                float1 = (float)Math.sin(float1);
                float1 *= float1;
                float1 = (float)(float1 * (float) (Math.PI / 2));
                float1 = (float)Math.sin(float1);
                floats[int3 + this.rightWindowStart] = float1;
            }

            windows[int0] = floats;
        }

        return floats;
    }

    protected int getNumberOfSamples() {
        return this.rightWindowStart - this.leftWindowStart;
    }

    protected int getPcm(AudioPacket audioPacket1, int[][] ints3) {
        int int0 = this.pcm.length;

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = 0;
            int int3 = audioPacket1.rightWindowStart;
            int[] ints0 = audioPacket1.pcmInt[int1];
            int[] ints1 = this.pcmInt[int1];
            int[] ints2 = ints3[int1];

            for (int int4 = this.leftWindowStart; int4 < this.leftWindowEnd; int4++) {
                int int5 = ints0[int3++] + ints1[int4];
                if (int5 > 32767) {
                    int5 = 32767;
                }

                if (int5 < -32768) {
                    int5 = -32768;
                }

                ints2[int2++] = int5;
            }
        }

        if (this.leftWindowEnd + 1 < this.rightWindowStart) {
            for (int int6 = 0; int6 < int0; int6++) {
                System.arraycopy(
                    this.pcmInt[int6], this.leftWindowEnd, ints3[int6], this.leftWindowEnd - this.leftWindowStart, this.rightWindowStart - this.leftWindowEnd
                );
            }
        }

        return this.rightWindowStart - this.leftWindowStart;
    }

    protected void getPcm(AudioPacket audioPacket1, byte[] bytes) {
        int int0 = this.pcm.length;

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = 0;
            int int3 = audioPacket1.rightWindowStart;
            int[] ints0 = audioPacket1.pcmInt[int1];
            int[] ints1 = this.pcmInt[int1];

            for (int int4 = this.leftWindowStart; int4 < this.leftWindowEnd; int4++) {
                int int5 = ints0[int3++] + ints1[int4];
                if (int5 > 32767) {
                    int5 = 32767;
                }

                if (int5 < -32768) {
                    int5 = -32768;
                }

                bytes[int2 + int1 * 2 + 1] = (byte)(int5 & 0xFF);
                bytes[int2 + int1 * 2] = (byte)(int5 >> 8 & 0xFF);
                int2 += int0 * 2;
            }

            int2 = (this.leftWindowEnd - this.leftWindowStart) * int0 * 2;

            for (int int6 = this.leftWindowEnd; int6 < this.rightWindowStart; int6++) {
                int int7 = ints1[int6];
                if (int7 > 32767) {
                    int7 = 32767;
                }

                if (int7 < -32768) {
                    int7 = -32768;
                }

                bytes[int2 + int1 * 2 + 1] = (byte)(int7 & 0xFF);
                bytes[int2 + int1 * 2] = (byte)(int7 >> 8 & 0xFF);
                int2 += int0 * 2;
            }
        }
    }

    protected float[] getWindow() {
        return this.window;
    }

    protected int getLeftWindowStart() {
        return this.leftWindowStart;
    }

    protected int getLeftWindowEnd() {
        return this.leftWindowEnd;
    }

    protected int getRightWindowStart() {
        return this.rightWindowStart;
    }

    protected int getRightWindowEnd() {
        return this.rightWindowEnd;
    }

    public int[][] getPcm() {
        return this.pcmInt;
    }

    public float[][] getFreqencyDomain() {
        return this.pcm;
    }
}
