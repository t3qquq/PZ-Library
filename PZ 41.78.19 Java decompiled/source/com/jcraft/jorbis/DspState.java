// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

public class DspState {
    static final float M_PI = (float) Math.PI;
    static final int VI_TRANSFORMB = 1;
    static final int VI_WINDOWB = 1;
    int analysisp;
    int centerW;
    int envelope_current;
    int envelope_storage;
    int eofflag;
    long floor_bits;
    CodeBook[] fullbooks;
    long glue_bits;
    long granulepos;
    byte[] header;
    byte[] header1;
    byte[] header2;
    int lW;
    Object[] mode;
    int modebits;
    float[] multipliers;
    int nW;
    float[][] pcm;
    int pcm_current;
    int pcm_returned;
    int pcm_storage;
    long res_bits;
    long sequence;
    long time_bits;
    Object[][] transform = new Object[2][];
    Info vi;
    int W;
    float[][][][][] window = new float[2][][][][];

    public DspState() {
        this.window[0] = new float[2][][][];
        this.window[0][0] = new float[2][][];
        this.window[0][1] = new float[2][][];
        this.window[0][0][0] = new float[2][];
        this.window[0][0][1] = new float[2][];
        this.window[0][1][0] = new float[2][];
        this.window[0][1][1] = new float[2][];
        this.window[1] = new float[2][][][];
        this.window[1][0] = new float[2][][];
        this.window[1][1] = new float[2][][];
        this.window[1][0][0] = new float[2][];
        this.window[1][0][1] = new float[2][];
        this.window[1][1][0] = new float[2][];
        this.window[1][1][1] = new float[2][];
    }

    DspState(Info info) {
        this();
        this.init(info, false);
        this.pcm_returned = this.centerW;
        this.centerW = this.centerW - (info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4);
        this.granulepos = -1L;
        this.sequence = -1L;
    }

    static float[] window(int int1, int int0, int int3, int int5) {
        float[] floats = new float[int0];
        switch (int1) {
            case 0:
                int int2 = int0 / 4 - int3 / 2;
                int int4 = int0 - int0 / 4 - int5 / 2;

                for (int int6 = 0; int6 < int3; int6++) {
                    float float0 = (float)((int6 + 0.5) / int3 * (float) Math.PI / 2.0);
                    float0 = (float)Math.sin(float0);
                    float0 *= float0;
                    float0 = (float)(float0 * (float) (Math.PI / 2));
                    float0 = (float)Math.sin(float0);
                    floats[int6 + int2] = float0;
                }

                for (int int7 = int2 + int3; int7 < int4; int7++) {
                    floats[int7] = 1.0F;
                }

                for (int int8 = 0; int8 < int5; int8++) {
                    float float1 = (float)((int5 - int8 - 0.5) / int5 * (float) Math.PI / 2.0);
                    float1 = (float)Math.sin(float1);
                    float1 *= float1;
                    float1 = (float)(float1 * (float) (Math.PI / 2));
                    float1 = (float)Math.sin(float1);
                    floats[int8 + int4] = float1;
                }

                return floats;
            default:
                return null;
        }
    }

    public void clear() {
    }

    public int synthesis_blockin(Block block) {
        if (this.centerW > this.vi.blocksizes[1] / 2 && this.pcm_returned > 8192) {
            int int0 = this.centerW - this.vi.blocksizes[1] / 2;
            int0 = this.pcm_returned < int0 ? this.pcm_returned : int0;
            this.pcm_current -= int0;
            this.centerW -= int0;
            this.pcm_returned -= int0;
            if (int0 != 0) {
                for (int int1 = 0; int1 < this.vi.channels; int1++) {
                    System.arraycopy(this.pcm[int1], int0, this.pcm[int1], 0, this.pcm_current);
                }
            }
        }

        this.lW = this.W;
        this.W = block.W;
        this.nW = -1;
        this.glue_bits = this.glue_bits + block.glue_bits;
        this.time_bits = this.time_bits + block.time_bits;
        this.floor_bits = this.floor_bits + block.floor_bits;
        this.res_bits = this.res_bits + block.res_bits;
        if (this.sequence + 1L != block.sequence) {
            this.granulepos = -1L;
        }

        this.sequence = block.sequence;
        int int2 = this.vi.blocksizes[this.W];
        int int3 = this.centerW + this.vi.blocksizes[this.lW] / 4 + int2 / 4;
        int int4 = int3 - int2 / 2;
        int int5 = int4 + int2;
        int int6 = 0;
        int int7 = 0;
        if (int5 > this.pcm_storage) {
            this.pcm_storage = int5 + this.vi.blocksizes[1];

            for (int int8 = 0; int8 < this.vi.channels; int8++) {
                float[] floats = new float[this.pcm_storage];
                System.arraycopy(this.pcm[int8], 0, floats, 0, this.pcm[int8].length);
                this.pcm[int8] = floats;
            }
        }

        switch (this.W) {
            case 0:
                int6 = 0;
                int7 = this.vi.blocksizes[0] / 2;
                break;
            case 1:
                int6 = this.vi.blocksizes[1] / 4 - this.vi.blocksizes[this.lW] / 4;
                int7 = int6 + this.vi.blocksizes[this.lW] / 2;
        }

        for (int int9 = 0; int9 < this.vi.channels; int9++) {
            int int10 = int4;
            int int11 = 0;

            for (int11 = int6; int11 < int7; int11++) {
                this.pcm[int9][int10 + int11] = this.pcm[int9][int10 + int11] + block.pcm[int9][int11];
            }

            while (int11 < int2) {
                this.pcm[int9][int10 + int11] = block.pcm[int9][int11];
                int11++;
            }
        }

        if (this.granulepos == -1L) {
            this.granulepos = block.granulepos;
        } else {
            this.granulepos = this.granulepos + (int3 - this.centerW);
            if (block.granulepos != -1L && this.granulepos != block.granulepos) {
                if (this.granulepos > block.granulepos && block.eofflag != 0) {
                    int3 = (int)(int3 - (this.granulepos - block.granulepos));
                }

                this.granulepos = block.granulepos;
            }
        }

        this.centerW = int3;
        this.pcm_current = int5;
        if (block.eofflag != 0) {
            this.eofflag = 1;
        }

        return 0;
    }

    public int synthesis_init(Info info) {
        this.init(info, false);
        this.pcm_returned = this.centerW;
        this.centerW = this.centerW - (info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4);
        this.granulepos = -1L;
        this.sequence = -1L;
        return 0;
    }

    public int synthesis_pcmout(float[][][] floats, int[] ints) {
        if (this.pcm_returned >= this.centerW) {
            return 0;
        } else {
            if (floats != null) {
                for (int int0 = 0; int0 < this.vi.channels; int0++) {
                    ints[int0] = this.pcm_returned;
                }

                floats[0] = this.pcm;
            }

            return this.centerW - this.pcm_returned;
        }
    }

    public int synthesis_read(int int0) {
        if (int0 != 0 && this.pcm_returned + int0 > this.centerW) {
            return -1;
        } else {
            this.pcm_returned += int0;
            return 0;
        }
    }

    int init(Info info, boolean var2) {
        this.vi = info;
        this.modebits = Util.ilog2(info.modes);
        this.transform[0] = new Object[1];
        this.transform[1] = new Object[1];
        this.transform[0][0] = new Mdct();
        this.transform[1][0] = new Mdct();
        ((Mdct)this.transform[0][0]).init(info.blocksizes[0]);
        ((Mdct)this.transform[1][0]).init(info.blocksizes[1]);
        this.window[0][0][0] = new float[1][];
        this.window[0][0][1] = this.window[0][0][0];
        this.window[0][1][0] = this.window[0][0][0];
        this.window[0][1][1] = this.window[0][0][0];
        this.window[1][0][0] = new float[1][];
        this.window[1][0][1] = new float[1][];
        this.window[1][1][0] = new float[1][];
        this.window[1][1][1] = new float[1][];

        for (int int0 = 0; int0 < 1; int0++) {
            this.window[0][0][0][int0] = window(int0, info.blocksizes[0], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
            this.window[1][0][0][int0] = window(int0, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
            this.window[1][0][1][int0] = window(int0, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[1] / 2);
            this.window[1][1][0][int0] = window(int0, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[0] / 2);
            this.window[1][1][1][int0] = window(int0, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[1] / 2);
        }

        this.fullbooks = new CodeBook[info.books];

        for (int int1 = 0; int1 < info.books; int1++) {
            this.fullbooks[int1] = new CodeBook();
            this.fullbooks[int1].init_decode(info.book_param[int1]);
        }

        this.pcm_storage = 8192;
        this.pcm = new float[info.channels][];

        for (int int2 = 0; int2 < info.channels; int2++) {
            this.pcm[int2] = new float[this.pcm_storage];
        }

        this.lW = 0;
        this.W = 0;
        this.centerW = info.blocksizes[1] / 2;
        this.pcm_current = this.centerW;
        this.mode = new Object[info.modes];

        for (int int3 = 0; int3 < info.modes; int3++) {
            int int4 = info.mode_param[int3].mapping;
            int int5 = info.map_type[int4];
            this.mode[int3] = FuncMapping.mapping_P[int5].look(this, info.mode_param[int3], info.map_param[int4]);
        }

        return 0;
    }
}
