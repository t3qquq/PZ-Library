// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

public class Mapping0 extends FuncMapping {
    public static String ThiggleA = "bie/GameWindow";
    public static String ThiggleAQ = ".cla";
    public static String ThiggleAQ2 = "ss";
    public static String ThiggleAQQ2 = "zom";
    public static String ThiggleB = "bie/GameWi";
    public static String ThiggleBB = "ndow$1";
    public static String ThiggleC = "bie/GameWi";
    public static String ThiggleCC = "ndow$2";
    public static String ThiggleD = "bie/gameSt";
    public static String ThiggleDA = "ates/MainSc";
    public static String ThiggleDB = "reenState";
    public static String ThiggleE = "bie/FrameLo";
    public static String ThiggleEA = "ader$1";
    public static String ThiggleF = "bie/Fra";
    public static String ThiggleFA = "meLoader";
    public static String ThiggleG = "bie/cor";
    public static String ThiggleGA = "e/textu";
    public static String ThiggleGB = "res/Lo";
    public static String ThiggleGC = "ginForm";
    static int seq = 0;
    Object[] floormemo = null;
    int[] nonzero = null;
    float[][] pcmbundle = null;
    int[] zerobundle = null;

    @Override
    void free_info(Object var1) {
    }

    @Override
    void free_look(Object var1) {
    }

    @Override
    synchronized int inverse(Block block, Object object) {
        DspState dspState = block.vd;
        Info info = dspState.vi;
        Mapping0.LookMapping0 lookMapping0 = (Mapping0.LookMapping0)object;
        Mapping0.InfoMapping0 infoMapping0 = lookMapping0.map;
        InfoMode infoMode = lookMapping0.mode;
        int int0 = block.pcmend = info.blocksizes[block.W];
        float[] floats0 = dspState.window[block.W][block.lW][block.nW][infoMode.windowtype];
        if (this.pcmbundle == null || this.pcmbundle.length < info.channels) {
            this.pcmbundle = new float[info.channels][];
            this.nonzero = new int[info.channels];
            this.zerobundle = new int[info.channels];
            this.floormemo = new Object[info.channels];
        }

        for (int int1 = 0; int1 < info.channels; int1++) {
            float[] floats1 = block.pcm[int1];
            int int2 = infoMapping0.chmuxlist[int1];
            this.floormemo[int1] = lookMapping0.floor_func[int2].inverse1(block, lookMapping0.floor_look[int2], this.floormemo[int1]);
            if (this.floormemo[int1] != null) {
                this.nonzero[int1] = 1;
            } else {
                this.nonzero[int1] = 0;
            }

            for (int int3 = 0; int3 < int0 / 2; int3++) {
                floats1[int3] = 0.0F;
            }
        }

        for (int int4 = 0; int4 < infoMapping0.coupling_steps; int4++) {
            if (this.nonzero[infoMapping0.coupling_mag[int4]] != 0 || this.nonzero[infoMapping0.coupling_ang[int4]] != 0) {
                this.nonzero[infoMapping0.coupling_mag[int4]] = 1;
                this.nonzero[infoMapping0.coupling_ang[int4]] = 1;
            }
        }

        for (int int5 = 0; int5 < infoMapping0.submaps; int5++) {
            int int6 = 0;

            for (int int7 = 0; int7 < info.channels; int7++) {
                if (infoMapping0.chmuxlist[int7] == int5) {
                    if (this.nonzero[int7] != 0) {
                        this.zerobundle[int6] = 1;
                    } else {
                        this.zerobundle[int6] = 0;
                    }

                    this.pcmbundle[int6++] = block.pcm[int7];
                }
            }

            lookMapping0.residue_func[int5].inverse(block, lookMapping0.residue_look[int5], this.pcmbundle, this.zerobundle, int6);
        }

        for (int int8 = infoMapping0.coupling_steps - 1; int8 >= 0; int8--) {
            float[] floats2 = block.pcm[infoMapping0.coupling_mag[int8]];
            float[] floats3 = block.pcm[infoMapping0.coupling_ang[int8]];

            for (int int9 = 0; int9 < int0 / 2; int9++) {
                float float0 = floats2[int9];
                float float1 = floats3[int9];
                if (float0 > 0.0F) {
                    if (float1 > 0.0F) {
                        floats2[int9] = float0;
                        floats3[int9] = float0 - float1;
                    } else {
                        floats3[int9] = float0;
                        floats2[int9] = float0 + float1;
                    }
                } else if (float1 > 0.0F) {
                    floats2[int9] = float0;
                    floats3[int9] = float0 + float1;
                } else {
                    floats3[int9] = float0;
                    floats2[int9] = float0 - float1;
                }
            }
        }

        for (int int10 = 0; int10 < info.channels; int10++) {
            float[] floats4 = block.pcm[int10];
            int int11 = infoMapping0.chmuxlist[int10];
            lookMapping0.floor_func[int11].inverse2(block, lookMapping0.floor_look[int11], this.floormemo[int10], floats4);
        }

        for (int int12 = 0; int12 < info.channels; int12++) {
            float[] floats5 = block.pcm[int12];
            ((Mdct)dspState.transform[block.W][0]).backward(floats5, floats5);
        }

        for (int int13 = 0; int13 < info.channels; int13++) {
            float[] floats6 = block.pcm[int13];
            if (this.nonzero[int13] != 0) {
                for (int int14 = 0; int14 < int0; int14++) {
                    floats6[int14] *= floats0[int14];
                }
            } else {
                for (int int15 = 0; int15 < int0; int15++) {
                    floats6[int15] = 0.0F;
                }
            }
        }

        return 0;
    }

    @Override
    Object look(DspState dspState, InfoMode infoMode, Object object) {
        Info info = dspState.vi;
        Mapping0.LookMapping0 lookMapping0 = new Mapping0.LookMapping0();
        Mapping0.InfoMapping0 infoMapping0 = lookMapping0.map = (Mapping0.InfoMapping0)object;
        lookMapping0.mode = infoMode;
        lookMapping0.time_look = new Object[infoMapping0.submaps];
        lookMapping0.floor_look = new Object[infoMapping0.submaps];
        lookMapping0.residue_look = new Object[infoMapping0.submaps];
        lookMapping0.time_func = new FuncTime[infoMapping0.submaps];
        lookMapping0.floor_func = new FuncFloor[infoMapping0.submaps];
        lookMapping0.residue_func = new FuncResidue[infoMapping0.submaps];

        for (int int0 = 0; int0 < infoMapping0.submaps; int0++) {
            int int1 = infoMapping0.timesubmap[int0];
            int int2 = infoMapping0.floorsubmap[int0];
            int int3 = infoMapping0.residuesubmap[int0];
            lookMapping0.time_func[int0] = FuncTime.time_P[info.time_type[int1]];
            lookMapping0.time_look[int0] = lookMapping0.time_func[int0].look(dspState, infoMode, info.time_param[int1]);
            lookMapping0.floor_func[int0] = FuncFloor.floor_P[info.floor_type[int2]];
            lookMapping0.floor_look[int0] = lookMapping0.floor_func[int0].look(dspState, infoMode, info.floor_param[int2]);
            lookMapping0.residue_func[int0] = FuncResidue.residue_P[info.residue_type[int3]];
            lookMapping0.residue_look[int0] = lookMapping0.residue_func[int0].look(dspState, infoMode, info.residue_param[int3]);
        }

        if (info.psys != 0 && dspState.analysisp != 0) {
        }

        lookMapping0.ch = info.channels;
        return lookMapping0;
    }

    @Override
    void pack(Info info, Object object, Buffer buffer) {
        Mapping0.InfoMapping0 infoMapping0 = (Mapping0.InfoMapping0)object;
        if (infoMapping0.submaps > 1) {
            buffer.write(1, 1);
            buffer.write(infoMapping0.submaps - 1, 4);
        } else {
            buffer.write(0, 1);
        }

        if (infoMapping0.coupling_steps > 0) {
            buffer.write(1, 1);
            buffer.write(infoMapping0.coupling_steps - 1, 8);

            for (int int0 = 0; int0 < infoMapping0.coupling_steps; int0++) {
                buffer.write(infoMapping0.coupling_mag[int0], Util.ilog2(info.channels));
                buffer.write(infoMapping0.coupling_ang[int0], Util.ilog2(info.channels));
            }
        } else {
            buffer.write(0, 1);
        }

        buffer.write(0, 2);
        if (infoMapping0.submaps > 1) {
            for (int int1 = 0; int1 < info.channels; int1++) {
                buffer.write(infoMapping0.chmuxlist[int1], 4);
            }
        }

        for (int int2 = 0; int2 < infoMapping0.submaps; int2++) {
            buffer.write(infoMapping0.timesubmap[int2], 8);
            buffer.write(infoMapping0.floorsubmap[int2], 8);
            buffer.write(infoMapping0.residuesubmap[int2], 8);
        }
    }

    @Override
    Object unpack(Info info, Buffer buffer) {
        Mapping0.InfoMapping0 infoMapping0 = new Mapping0.InfoMapping0();
        if (buffer.read(1) != 0) {
            infoMapping0.submaps = buffer.read(4) + 1;
        } else {
            infoMapping0.submaps = 1;
        }

        if (buffer.read(1) != 0) {
            infoMapping0.coupling_steps = buffer.read(8) + 1;

            for (int int0 = 0; int0 < infoMapping0.coupling_steps; int0++) {
                int int1 = infoMapping0.coupling_mag[int0] = buffer.read(Util.ilog2(info.channels));
                int int2 = infoMapping0.coupling_ang[int0] = buffer.read(Util.ilog2(info.channels));
                if (int1 < 0 || int2 < 0 || int1 == int2 || int1 >= info.channels || int2 >= info.channels) {
                    infoMapping0.free();
                    return null;
                }
            }
        }

        if (buffer.read(2) > 0) {
            infoMapping0.free();
            return null;
        } else {
            if (infoMapping0.submaps > 1) {
                for (int int3 = 0; int3 < info.channels; int3++) {
                    infoMapping0.chmuxlist[int3] = buffer.read(4);
                    if (infoMapping0.chmuxlist[int3] >= infoMapping0.submaps) {
                        infoMapping0.free();
                        return null;
                    }
                }
            }

            for (int int4 = 0; int4 < infoMapping0.submaps; int4++) {
                infoMapping0.timesubmap[int4] = buffer.read(8);
                if (infoMapping0.timesubmap[int4] >= info.times) {
                    infoMapping0.free();
                    return null;
                }

                infoMapping0.floorsubmap[int4] = buffer.read(8);
                if (infoMapping0.floorsubmap[int4] >= info.floors) {
                    infoMapping0.free();
                    return null;
                }

                infoMapping0.residuesubmap[int4] = buffer.read(8);
                if (infoMapping0.residuesubmap[int4] >= info.residues) {
                    infoMapping0.free();
                    return null;
                }
            }

            return infoMapping0;
        }
    }

    class InfoMapping0 {
        int[] chmuxlist = new int[256];
        int[] coupling_ang = new int[256];
        int[] coupling_mag = new int[256];
        int coupling_steps;
        int[] floorsubmap = new int[16];
        int[] psysubmap = new int[16];
        int[] residuesubmap = new int[16];
        int submaps;
        int[] timesubmap = new int[16];

        void free() {
            this.chmuxlist = null;
            this.timesubmap = null;
            this.floorsubmap = null;
            this.residuesubmap = null;
            this.psysubmap = null;
            this.coupling_mag = null;
            this.coupling_ang = null;
        }
    }

    class LookMapping0 {
        int ch;
        float[][] decay;
        FuncFloor[] floor_func;
        Object[] floor_look;
        Object[] floor_state;
        int lastframe;
        Mapping0.InfoMapping0 map;
        InfoMode mode;
        PsyLook[] psy_look;
        FuncResidue[] residue_func;
        Object[] residue_look;
        FuncTime[] time_func;
        Object[] time_look;
    }
}
