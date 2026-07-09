// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jogg;

import zombie.iso.areas.IsoArea;

public class Buffer {
    public static String version = "0a2a0q";
    private static final int BUFFER_INCREMENT = 256;
    private static final int[] mask = new int[]{
        0,
        1,
        3,
        7,
        15,
        31,
        63,
        127,
        255,
        511,
        1023,
        2047,
        4095,
        8191,
        16383,
        32767,
        65535,
        131071,
        262143,
        524287,
        1048575,
        2097151,
        4194303,
        8388607,
        16777215,
        33554431,
        67108863,
        134217727,
        268435455,
        536870911,
        1073741823,
        Integer.MAX_VALUE,
        -1
    };
    byte[] buffer = null;
    int endbit = 0;
    int endbyte = 0;
    int ptr = 0;
    int storage = 0;

    public static int ilog(int int1) {
        int int0 = 0;

        while (int1 > 0) {
            int0++;
            int1 >>>= 1;
        }

        return int0;
    }

    public static void report(String string) {
        System.err.println(string);
        System.exit(1);
    }

    public void adv(int int0) {
        int0 += this.endbit;
        this.ptr += int0 / 8;
        this.endbyte += int0 / 8;
        this.endbit = int0 & 7;
    }

    public void adv1() {
        this.endbit++;
        if (this.endbit > 7) {
            this.endbit = 0;
            this.ptr++;
            this.endbyte++;
        }
    }

    public int bits() {
        return this.endbyte * 8 + this.endbit;
    }

    public byte[] buffer() {
        return this.buffer;
    }

    public int bytes() {
        return this.endbyte + (this.endbit + 7) / 8;
    }

    public int look(int int1) {
        int int0 = mask[int1];
        int1 += this.endbit;
        if (this.endbyte + 4 >= this.storage && this.endbyte + (int1 - 1) / 8 >= this.storage) {
            return -1;
        } else {
            int int2 = (this.buffer[this.ptr] & 255) >>> this.endbit;
            if (int1 > 8) {
                int2 |= (this.buffer[this.ptr + 1] & 255) << 8 - this.endbit;
                if (int1 > 16) {
                    int2 |= (this.buffer[this.ptr + 2] & 255) << 16 - this.endbit;
                    if (int1 > 24) {
                        int2 |= (this.buffer[this.ptr + 3] & 255) << 24 - this.endbit;
                        if (int1 > 32 && this.endbit != 0) {
                            int2 |= (this.buffer[this.ptr + 4] & 255) << 32 - this.endbit;
                        }
                    }
                }
            }

            return int0 & int2;
        }
    }

    public int look1() {
        return this.endbyte >= this.storage ? -1 : this.buffer[this.ptr] >> this.endbit & 1;
    }

    public int read(int int1) {
        int int0 = mask[int1];
        int1 += this.endbit;
        if (this.endbyte + 4 >= this.storage) {
            byte byte0 = -1;
            if (this.endbyte + (int1 - 1) / 8 >= this.storage) {
                this.ptr += int1 / 8;
                this.endbyte += int1 / 8;
                this.endbit = int1 & 7;
                return byte0;
            }
        }

        int int2 = (this.buffer[this.ptr] & 255) >>> this.endbit;
        if (int1 > 8) {
            int2 |= (this.buffer[this.ptr + 1] & 255) << 8 - this.endbit;
            if (int1 > 16) {
                int2 |= (this.buffer[this.ptr + 2] & 255) << 16 - this.endbit;
                if (int1 > 24) {
                    int2 |= (this.buffer[this.ptr + 3] & 255) << 24 - this.endbit;
                    if (int1 > 32 && this.endbit != 0) {
                        int2 |= (this.buffer[this.ptr + 4] & 255) << 32 - this.endbit;
                    }
                }
            }
        }

        int2 &= int0;
        this.ptr += int1 / 8;
        this.endbyte += int1 / 8;
        this.endbit = int1 & 7;
        return int2;
    }

    public void read(byte[] bytes, int int1) {
        int int0 = 0;

        while (int1-- != 0) {
            bytes[int0++] = (byte)this.read(8);
        }
    }

    public int read1() {
        if (this.endbyte >= this.storage) {
            byte byte0 = -1;
            this.endbit++;
            if (this.endbit > 7) {
                this.endbit = 0;
                this.ptr++;
                this.endbyte++;
            }

            return byte0;
        } else {
            int int0 = this.buffer[this.ptr] >> this.endbit & 1;
            this.endbit++;
            if (this.endbit > 7) {
                this.endbit = 0;
                this.ptr++;
                this.endbyte++;
            }

            return int0;
        }
    }

    public int readB(int int1) {
        int int0 = 32 - int1;
        int1 += this.endbit;
        if (this.endbyte + 4 >= this.storage) {
            byte byte0 = -1;
            if (this.endbyte * 8 + int1 > this.storage * 8) {
                this.ptr += int1 / 8;
                this.endbyte += int1 / 8;
                this.endbit = int1 & 7;
                return byte0;
            }
        }

        int int2 = (this.buffer[this.ptr] & 255) << 24 + this.endbit;
        if (int1 > 8) {
            int2 |= (this.buffer[this.ptr + 1] & 255) << 16 + this.endbit;
            if (int1 > 16) {
                int2 |= (this.buffer[this.ptr + 2] & 255) << 8 + this.endbit;
                if (int1 > 24) {
                    int2 |= (this.buffer[this.ptr + 3] & 255) << this.endbit;
                    if (int1 > 32 && this.endbit != 0) {
                        int2 |= (this.buffer[this.ptr + 4] & 255) >> 8 - this.endbit;
                    }
                }
            }
        }

        int2 = int2 >>> (int0 >> 1) >>> (int0 + 1 >> 1);
        this.ptr += int1 / 8;
        this.endbyte += int1 / 8;
        this.endbit = int1 & 7;
        return int2;
    }

    public void readinit(byte[] bytes, int int0) {
        this.readinit(bytes, 0, int0);
    }

    public void readinit(byte[] bytes, int int0, int int1) {
        this.ptr = int0;
        this.buffer = bytes;
        this.endbit = this.endbyte = 0;
        this.storage = int1;
    }

    public void write(byte[] bytes) {
        for (int int0 = 0; int0 < bytes.length && bytes[int0] != 0; int0++) {
            this.write(bytes[int0], 8);
        }
    }

    public void write(int int0, int int1) {
        if (this.endbyte + 4 >= this.storage) {
            byte[] bytes = new byte[this.storage + 256];
            System.arraycopy(this.buffer, 0, bytes, 0, this.storage);
            this.buffer = bytes;
            this.storage += 256;
        }

        int0 &= mask[int1];
        int1 += this.endbit;
        this.buffer[this.ptr] = (byte)(this.buffer[this.ptr] | (byte)(int0 << this.endbit));
        if (int1 >= 8) {
            this.buffer[this.ptr + 1] = (byte)(int0 >>> 8 - this.endbit);
            if (int1 >= 16) {
                this.buffer[this.ptr + 2] = (byte)(int0 >>> 16 - this.endbit);
                if (int1 >= 24) {
                    this.buffer[this.ptr + 3] = (byte)(int0 >>> 24 - this.endbit);
                    if (int1 >= 32) {
                        if (this.endbit > 0) {
                            this.buffer[this.ptr + 4] = (byte)(int0 >>> 32 - this.endbit);
                        } else {
                            this.buffer[this.ptr + 4] = 0;
                        }
                    }
                }
            }
        }

        this.endbyte += int1 / 8;
        this.ptr += int1 / 8;
        this.endbit = int1 & 7;
    }

    public void writeclear() {
        this.buffer = null;
    }

    public void writeinit() {
        this.buffer = new byte[256];
        this.ptr = 0;
        this.buffer[0] = 0;
        this.storage = 256;
    }

    void reset() {
        this.ptr = 0;
        this.buffer[0] = 0;
        this.endbit = this.endbyte = 0;
    }

    static {
        if (!version.equals(IsoArea.version)) {
            System.exit(0);
        }
    }
}
