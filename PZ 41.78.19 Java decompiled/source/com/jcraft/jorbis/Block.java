// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;

public class Block {
    int eofflag;
    int floor_bits;
    int glue_bits;
    long granulepos;
    int lW;
    int mode;
    int nW;
    Buffer opb = new Buffer();
    float[][] pcm = new float[0][];
    int pcmend;
    int res_bits;
    long sequence;
    int time_bits;
    DspState vd;
    int W;

    public static String asdsadsa(String string, byte[] bytes, int int0) {
        return string + Integer.toString((bytes[int0] & 255) + 256, 16).substring(1);
    }

    public Block(DspState dspState) {
        this.vd = dspState;
        if (dspState.analysisp != 0) {
            this.opb.writeinit();
        }
    }

    public int clear() {
        if (this.vd != null && this.vd.analysisp != 0) {
            this.opb.writeclear();
        }

        return 0;
    }

    public void init(DspState dspState) {
        this.vd = dspState;
    }

    public int synthesis(Packet packet) {
        Info info = this.vd.vi;
        this.opb.readinit(packet.packet_base, packet.packet, packet.bytes);
        if (this.opb.read(1) != 0) {
            return -1;
        } else {
            int int0 = this.opb.read(this.vd.modebits);
            if (int0 == -1) {
                return -1;
            } else {
                this.mode = int0;
                this.W = info.mode_param[this.mode].blockflag;
                if (this.W != 0) {
                    this.lW = this.opb.read(1);
                    this.nW = this.opb.read(1);
                    if (this.nW == -1) {
                        return -1;
                    }
                } else {
                    this.lW = 0;
                    this.nW = 0;
                }

                this.granulepos = packet.granulepos;
                this.sequence = packet.packetno - 3L;
                this.eofflag = packet.e_o_s;
                this.pcmend = info.blocksizes[this.W];
                if (this.pcm.length < info.channels) {
                    this.pcm = new float[info.channels][];
                }

                for (int int1 = 0; int1 < info.channels; int1++) {
                    if (this.pcm[int1] != null && this.pcm[int1].length >= this.pcmend) {
                        for (int int2 = 0; int2 < this.pcmend; int2++) {
                            this.pcm[int1][int2] = 0.0F;
                        }
                    } else {
                        this.pcm[int1] = new float[this.pcmend];
                    }
                }

                int int3 = info.map_type[info.mode_param[this.mode].mapping];
                return FuncMapping.mapping_P[int3].inverse(this, this.vd.mode[this.mode]);
            }
        }
    }
}
