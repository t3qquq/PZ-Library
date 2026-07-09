// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class StaticCodeBook {
    static final int VQ_FEXP = 10;
    static final int VQ_FMAN = 21;
    static final int VQ_FEXP_BIAS = 768;
    int dim;
    int entries;
    int[] lengthlist;
    int maptype;
    int q_delta;
    int q_min;
    int q_quant;
    int q_sequencep;
    int[] quantlist;

    static long float32_pack(float float0) {
        int int0 = 0;
        if (float0 < 0.0F) {
            int0 = Integer.MIN_VALUE;
            float0 = -float0;
        }

        int int1 = (int)Math.floor(Math.log(float0) / Math.log(2.0));
        int int2 = (int)Math.rint(Math.pow(float0, 20 - int1));
        int1 = int1 + 768 << 21;
        return int0 | int1 | int2;
    }

    static float float32_unpack(int int0) {
        float float0 = int0 & 2097151;
        float float1 = (int0 & 2145386496) >>> 21;
        if ((int0 & -2147483648) != 0) {
            float0 = -float0;
        }

        return ldexp(float0, (int)float1 - 20 - 768);
    }

    static float ldexp(float float0, int int0) {
        return (float)(float0 * Math.pow(2.0, int0));
    }

    void clear() {
    }

    int pack(Buffer buffer) {
        boolean boolean0 = false;
        buffer.write(5653314, 24);
        buffer.write(this.dim, 16);
        buffer.write(this.entries, 24);
        int int0 = 1;

        while (int0 < this.entries && this.lengthlist[int0] >= this.lengthlist[int0 - 1]) {
            int0++;
        }

        if (int0 == this.entries) {
            boolean0 = true;
        }

        if (boolean0) {
            int int1 = 0;
            buffer.write(1, 1);
            buffer.write(this.lengthlist[0] - 1, 5);

            for (int0 = 1; int0 < this.entries; int0++) {
                int int2 = this.lengthlist[int0];
                int int3 = this.lengthlist[int0 - 1];
                if (int2 > int3) {
                    for (int int4 = int3; int4 < int2; int4++) {
                        buffer.write(int0 - int1, Util.ilog(this.entries - int1));
                        int1 = int0;
                    }
                }
            }

            buffer.write(int0 - int1, Util.ilog(this.entries - int1));
        } else {
            buffer.write(0, 1);
            int0 = 0;

            while (int0 < this.entries && this.lengthlist[int0] != 0) {
                int0++;
            }

            if (int0 == this.entries) {
                buffer.write(0, 1);

                for (int int5 = 0; int5 < this.entries; int5++) {
                    buffer.write(this.lengthlist[int5] - 1, 5);
                }
            } else {
                buffer.write(1, 1);

                for (int int6 = 0; int6 < this.entries; int6++) {
                    if (this.lengthlist[int6] == 0) {
                        buffer.write(0, 1);
                    } else {
                        buffer.write(1, 1);
                        buffer.write(this.lengthlist[int6] - 1, 5);
                    }
                }
            }
        }

        buffer.write(this.maptype, 4);
        switch (this.maptype) {
            case 1:
            case 2:
                if (this.quantlist == null) {
                    return -1;
                } else {
                    buffer.write(this.q_min, 32);
                    buffer.write(this.q_delta, 32);
                    buffer.write(this.q_quant - 1, 4);
                    buffer.write(this.q_sequencep, 1);
                    int int7 = 0;
                    switch (this.maptype) {
                        case 1:
                            int7 = this.maptype1_quantvals();
                            break;
                        case 2:
                            int7 = this.entries * this.dim;
                    }

                    for (int int8 = 0; int8 < int7; int8++) {
                        buffer.write(Math.abs(this.quantlist[int8]), this.q_quant);
                    }
                }
            case 0:
                return 0;
            default:
                return -1;
        }
    }

    int unpack(Buffer buffer) {
        if (buffer.read(24) != 5653314) {
            this.clear();
            return -1;
        } else {
            this.dim = buffer.read(16);
            this.entries = buffer.read(24);
            if (this.entries == -1) {
                this.clear();
                return -1;
            } else {
                switch (buffer.read(1)) {
                    case 0:
                        this.lengthlist = new int[this.entries];
                        if (buffer.read(1) != 0) {
                            for (int int4 = 0; int4 < this.entries; int4++) {
                                if (buffer.read(1) != 0) {
                                    int int5 = buffer.read(5);
                                    if (int5 == -1) {
                                        this.clear();
                                        return -1;
                                    }

                                    this.lengthlist[int4] = int5 + 1;
                                } else {
                                    this.lengthlist[int4] = 0;
                                }
                            }
                        } else {
                            for (int int6 = 0; int6 < this.entries; int6++) {
                                int int7 = buffer.read(5);
                                if (int7 == -1) {
                                    this.clear();
                                    return -1;
                                }

                                this.lengthlist[int6] = int7 + 1;
                            }
                        }
                        break;
                    case 1:
                        int int0 = buffer.read(5) + 1;
                        this.lengthlist = new int[this.entries];

                        for (int int1 = 0; int1 < this.entries; int0++) {
                            int int2 = buffer.read(Util.ilog(this.entries - int1));
                            if (int2 == -1) {
                                this.clear();
                                return -1;
                            }

                            for (int int3 = 0; int3 < int2; int1++) {
                                this.lengthlist[int1] = int0;
                                int3++;
                            }
                        }
                        break;
                    default:
                        return -1;
                }

                switch (this.maptype = buffer.read(4)) {
                    case 1:
                    case 2:
                        this.q_min = buffer.read(32);
                        this.q_delta = buffer.read(32);
                        this.q_quant = buffer.read(4) + 1;
                        this.q_sequencep = buffer.read(1);
                        int int8 = 0;
                        switch (this.maptype) {
                            case 1:
                                int8 = this.maptype1_quantvals();
                                break;
                            case 2:
                                int8 = this.entries * this.dim;
                        }

                        this.quantlist = new int[int8];

                        for (int int9 = 0; int9 < int8; int9++) {
                            this.quantlist[int9] = buffer.read(this.q_quant);
                        }

                        if (this.quantlist[int8 - 1] == -1) {
                            this.clear();
                            return -1;
                        }
                    case 0:
                        return 0;
                    default:
                        this.clear();
                        return -1;
                }
            }
        }
    }

    float[] unquantize() {
        if (this.maptype != 1 && this.maptype != 2) {
            return null;
        } else {
            float float0 = float32_unpack(this.q_min);
            float float1 = float32_unpack(this.q_delta);
            float[] floats = new float[this.entries * this.dim];
            switch (this.maptype) {
                case 1:
                    int int2 = this.maptype1_quantvals();

                    for (int int3 = 0; int3 < this.entries; int3++) {
                        float float4 = 0.0F;
                        int int4 = 1;

                        for (int int5 = 0; int5 < this.dim; int5++) {
                            int int6 = int3 / int4 % int2;
                            float float5 = this.quantlist[int6];
                            float5 = Math.abs(float5) * float1 + float0 + float4;
                            if (this.q_sequencep != 0) {
                                float4 = float5;
                            }

                            floats[int3 * this.dim + int5] = float5;
                            int4 *= int2;
                        }
                    }
                    break;
                case 2:
                    for (int int0 = 0; int0 < this.entries; int0++) {
                        float float2 = 0.0F;

                        for (int int1 = 0; int1 < this.dim; int1++) {
                            float float3 = this.quantlist[int0 * this.dim + int1];
                            float3 = Math.abs(float3) * float1 + float0 + float2;
                            if (this.q_sequencep != 0) {
                                float2 = float3;
                            }

                            floats[int0 * this.dim + int1] = float3;
                        }
                    }
            }

            return floats;
        }
    }

    private int maptype1_quantvals() {
        int int0 = (int)Math.floor(Math.pow(this.entries, 1.0 / this.dim));

        while (true) {
            int int1 = 1;
            int int2 = 1;

            for (int int3 = 0; int3 < this.dim; int3++) {
                int1 *= int0;
                int2 *= int0 + 1;
            }

            if (int1 <= this.entries && int2 > this.entries) {
                return int0;
            }

            if (int1 > this.entries) {
                int0--;
            } else {
                int0++;
            }
        }
    }
}
