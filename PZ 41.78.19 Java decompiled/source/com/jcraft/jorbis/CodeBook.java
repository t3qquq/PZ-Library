// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

class CodeBook {
    StaticCodeBook c = new StaticCodeBook();
    int[] codelist;
    CodeBook.DecodeAux decode_tree;
    int dim;
    int entries;
    float[] valuelist;
    private int[] t = new int[15];

    static int[] make_words(int[] ints2, int int0) {
        int[] ints0 = new int[33];
        int[] ints1 = new int[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = ints2[int1];
            if (int2 > 0) {
                int int3 = ints0[int2];
                if (int2 < 32 && int3 >>> int2 != 0) {
                    return null;
                }

                ints1[int1] = int3;

                for (int int4 = int2; int4 > 0; int4--) {
                    if ((ints0[int4] & 1) != 0) {
                        if (int4 == 1) {
                            ints0[1]++;
                        } else {
                            ints0[int4] = ints0[int4 - 1] << 1;
                        }
                        break;
                    }

                    ints0[int4]++;
                }

                for (int int5 = int2 + 1; int5 < 33 && ints0[int5] >>> 1 == int3; int5++) {
                    int3 = ints0[int5];
                    ints0[int5] = ints0[int5 - 1] << 1;
                }
            }
        }

        for (int int6 = 0; int6 < int0; int6++) {
            int int7 = 0;

            for (int int8 = 0; int8 < ints2[int6]; int8++) {
                int7 <<= 1;
                int7 |= ints1[int6] >>> int8 & 1;
            }

            ints1[int6] = int7;
        }

        return ints1;
    }

    private static float dist(int int1, float[] floats1, int int3, float[] floats0, int int2) {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < int1; int0++) {
            float float1 = floats1[int3 + int0] - floats0[int0 * int2];
            float0 += float1 * float1;
        }

        return float0;
    }

    int best(float[] floats, int int3) {
        int int0 = -1;
        float float0 = 0.0F;
        int int1 = 0;

        for (int int2 = 0; int2 < this.entries; int2++) {
            if (this.c.lengthlist[int2] > 0) {
                float float1 = dist(this.dim, this.valuelist, int1, floats, int3);
                if (int0 == -1 || float1 < float0) {
                    float0 = float1;
                    int0 = int2;
                }
            }

            int1 += this.dim;
        }

        return int0;
    }

    int besterror(float[] floats, int int1, int int2) {
        int int0 = this.best(floats, int1);
        switch (int2) {
            case 0:
                int int5 = 0;

                for (int int6 = 0; int5 < this.dim; int6 += int1) {
                    floats[int6] -= this.valuelist[int0 * this.dim + int5];
                    int5++;
                }
                break;
            case 1:
                int int3 = 0;

                for (int int4 = 0; int3 < this.dim; int4 += int1) {
                    float float0 = this.valuelist[int0 * this.dim + int3];
                    if (float0 == 0.0F) {
                        floats[int4] = 0.0F;
                    } else {
                        floats[int4] /= float0;
                    }

                    int3++;
                }
        }

        return int0;
    }

    void clear() {
    }

    int decode(Buffer buffer) {
        int int0 = 0;
        CodeBook.DecodeAux decodeAux = this.decode_tree;
        int int1 = buffer.look(decodeAux.tabn);
        if (int1 >= 0) {
            int0 = decodeAux.tab[int1];
            buffer.adv(decodeAux.tabl[int1]);
            if (int0 <= 0) {
                return -int0;
            }
        }

        do {
            switch (buffer.read1()) {
                case -1:
                default:
                    return -1;
                case 0:
                    int0 = decodeAux.ptr0[int0];
                    break;
                case 1:
                    int0 = decodeAux.ptr1[int0];
            }
        } while (int0 > 0);

        return -int0;
    }

    int decodev_add(float[] floats, int int6, Buffer buffer, int int1) {
        if (this.dim > 8) {
            int int0 = 0;

            while (int0 < int1) {
                int int2 = this.decode(buffer);
                if (int2 == -1) {
                    return -1;
                }

                int int3 = int2 * this.dim;
                int int4 = 0;

                while (int4 < this.dim) {
                    int int5 = int0++;
                    floats[int6 + int5] = floats[int6 + int5] + this.valuelist[int3 + int4++];
                }
            }
        } else {
            int int7 = 0;

            while (int7 < int1) {
                int int8 = this.decode(buffer);
                if (int8 == -1) {
                    return -1;
                }

                int int9 = int8 * this.dim;
                int int10 = 0;
                switch (this.dim) {
                    case 0:
                    default:
                        break;
                    case 8:
                        int int11 = int7++;
                        floats[int6 + int11] = floats[int6 + int11] + this.valuelist[int9 + int10++];
                    case 7:
                        int int12 = int7++;
                        floats[int6 + int12] = floats[int6 + int12] + this.valuelist[int9 + int10++];
                    case 6:
                        int int13 = int7++;
                        floats[int6 + int13] = floats[int6 + int13] + this.valuelist[int9 + int10++];
                    case 5:
                        int int14 = int7++;
                        floats[int6 + int14] = floats[int6 + int14] + this.valuelist[int9 + int10++];
                    case 4:
                        int int15 = int7++;
                        floats[int6 + int15] = floats[int6 + int15] + this.valuelist[int9 + int10++];
                    case 3:
                        int int16 = int7++;
                        floats[int6 + int16] = floats[int6 + int16] + this.valuelist[int9 + int10++];
                    case 2:
                        int int17 = int7++;
                        floats[int6 + int17] = floats[int6 + int17] + this.valuelist[int9 + int10++];
                    case 1:
                        int int18 = int7++;
                        floats[int6 + int18] = floats[int6 + int18] + this.valuelist[int9 + int10++];
                }
            }
        }

        return 0;
    }

    int decodev_set(float[] floats, int int5, Buffer buffer, int int1) {
        int int0 = 0;

        while (int0 < int1) {
            int int2 = this.decode(buffer);
            if (int2 == -1) {
                return -1;
            }

            int int3 = int2 * this.dim;
            int int4 = 0;

            while (int4 < this.dim) {
                floats[int5 + int0++] = this.valuelist[int3 + int4++];
            }
        }

        return 0;
    }

    int decodevs(float[] floats, int int4, Buffer buffer, int int5, int int1) {
        int int0 = this.decode(buffer);
        if (int0 == -1) {
            return -1;
        } else {
            switch (int1) {
                case -1:
                    int int8 = 0;
                    int int9 = 0;

                    while (int8 < this.dim) {
                        floats[int4 + int9] = this.valuelist[int0 * this.dim + int8];
                        int8++;
                        int9 += int5;
                    }
                    break;
                case 0:
                    int int6 = 0;
                    int int7 = 0;

                    while (int6 < this.dim) {
                        floats[int4 + int7] = floats[int4 + int7] + this.valuelist[int0 * this.dim + int6];
                        int6++;
                        int7 += int5;
                    }
                    break;
                case 1:
                    int int2 = 0;
                    int int3 = 0;

                    while (int2 < this.dim) {
                        floats[int4 + int3] = floats[int4 + int3] * this.valuelist[int0 * this.dim + int2];
                        int2++;
                        int3 += int5;
                    }
            }

            return int0;
        }
    }

    synchronized int decodevs_add(float[] floats, int int7, Buffer buffer, int int1) {
        int int0 = int1 / this.dim;
        if (this.t.length < int0) {
            this.t = new int[int0];
        }

        for (int int2 = 0; int2 < int0; int2++) {
            int int3 = this.decode(buffer);
            if (int3 == -1) {
                return -1;
            }

            this.t[int2] = int3 * this.dim;
        }

        int int4 = 0;

        for (int int5 = 0; int4 < this.dim; int5 += int0) {
            for (int int6 = 0; int6 < int0; int6++) {
                floats[int7 + int5 + int6] = floats[int7 + int5 + int6] + this.valuelist[this.t[int6] + int4];
            }

            int4++;
        }

        return 0;
    }

    int decodevv_add(float[][] floats, int int2, int int3, Buffer buffer, int int4) {
        int int0 = 0;
        int int1 = int2 / int3;

        while (int1 < (int2 + int4) / int3) {
            int int5 = this.decode(buffer);
            if (int5 == -1) {
                return -1;
            }

            int int6 = int5 * this.dim;

            for (int int7 = 0; int7 < this.dim; int7++) {
                int int8 = int0++;
                floats[int8][int1] = floats[int8][int1] + this.valuelist[int6 + int7];
                if (int0 == int3) {
                    int0 = 0;
                    int1++;
                }
            }
        }

        return 0;
    }

    int encode(int int0, Buffer buffer) {
        buffer.write(this.codelist[int0], this.c.lengthlist[int0]);
        return this.c.lengthlist[int0];
    }

    int encodev(int int1, float[] floats, Buffer buffer) {
        for (int int0 = 0; int0 < this.dim; int0++) {
            floats[int0] = this.valuelist[int1 * this.dim + int0];
        }

        return this.encode(int1, buffer);
    }

    int encodevs(float[] floats, Buffer buffer, int int1, int int2) {
        int int0 = this.besterror(floats, int1, int2);
        return this.encode(int0, buffer);
    }

    int errorv(float[] floats) {
        int int0 = this.best(floats, 1);

        for (int int1 = 0; int1 < this.dim; int1++) {
            floats[int1] = this.valuelist[int0 * this.dim + int1];
        }

        return int0;
    }

    int init_decode(StaticCodeBook staticCodeBook) {
        this.c = staticCodeBook;
        this.entries = staticCodeBook.entries;
        this.dim = staticCodeBook.dim;
        this.valuelist = staticCodeBook.unquantize();
        this.decode_tree = this.make_decode_tree();
        if (this.decode_tree == null) {
            this.clear();
            return -1;
        } else {
            return 0;
        }
    }

    CodeBook.DecodeAux make_decode_tree() {
        int int0 = 0;
        CodeBook.DecodeAux decodeAux = new CodeBook.DecodeAux();
        int[] ints0 = decodeAux.ptr0 = new int[this.entries * 2];
        int[] ints1 = decodeAux.ptr1 = new int[this.entries * 2];
        int[] ints2 = make_words(this.c.lengthlist, this.c.entries);
        if (ints2 == null) {
            return null;
        } else {
            decodeAux.aux = this.entries * 2;

            for (int int1 = 0; int1 < this.entries; int1++) {
                if (this.c.lengthlist[int1] > 0) {
                    int int2 = 0;

                    int int3;
                    for (int3 = 0; int3 < this.c.lengthlist[int1] - 1; int3++) {
                        int int4 = ints2[int1] >>> int3 & 1;
                        if (int4 == 0) {
                            if (ints0[int2] == 0) {
                                ints0[int2] = ++int0;
                            }

                            int2 = ints0[int2];
                        } else {
                            if (ints1[int2] == 0) {
                                ints1[int2] = ++int0;
                            }

                            int2 = ints1[int2];
                        }
                    }

                    if ((ints2[int1] >>> int3 & 1) == 0) {
                        ints0[int2] = -int1;
                    } else {
                        ints1[int2] = -int1;
                    }
                }
            }

            decodeAux.tabn = Util.ilog(this.entries) - 4;
            if (decodeAux.tabn < 5) {
                decodeAux.tabn = 5;
            }

            int int5 = 1 << decodeAux.tabn;
            decodeAux.tab = new int[int5];
            decodeAux.tabl = new int[int5];

            for (int int6 = 0; int6 < int5; int6++) {
                int int7 = 0;
                int int8 = 0;

                for (int8 = 0; int8 < decodeAux.tabn && (int7 > 0 || int8 == 0); int8++) {
                    if ((int6 & 1 << int8) != 0) {
                        int7 = ints1[int7];
                    } else {
                        int7 = ints0[int7];
                    }
                }

                decodeAux.tab[int6] = int7;
                decodeAux.tabl[int6] = int8;
            }

            return decodeAux;
        }
    }

    class DecodeAux {
        int aux;
        int[] ptr0;
        int[] ptr1;
        int[] tab;
        int[] tabl;
        int tabn;
    }
}
