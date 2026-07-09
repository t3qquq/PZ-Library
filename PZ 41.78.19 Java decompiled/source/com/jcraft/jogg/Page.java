// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jogg;

public class Page {
    private static int[] crc_lookup = new int[256];
    public int body;
    public byte[] body_base;
    public int body_len;
    public int header;
    public byte[] header_base;
    public int header_len;

    private static int crc_entry(int int1) {
        int int0 = int1 << 24;

        for (int int2 = 0; int2 < 8; int2++) {
            if ((int0 & -2147483648) != 0) {
                int0 = int0 << 1 ^ 79764919;
            } else {
                int0 <<= 1;
            }
        }

        return int0 & -1;
    }

    public int bos() {
        return this.header_base[this.header + 5] & 2;
    }

    public Page copy() {
        return this.copy(new Page());
    }

    public Page copy(Page page1) {
        byte[] bytes = new byte[this.header_len];
        System.arraycopy(this.header_base, this.header, bytes, 0, this.header_len);
        page1.header_len = this.header_len;
        page1.header_base = bytes;
        page1.header = 0;
        bytes = new byte[this.body_len];
        System.arraycopy(this.body_base, this.body, bytes, 0, this.body_len);
        page1.body_len = this.body_len;
        page1.body_base = bytes;
        page1.body = 0;
        return page1;
    }

    public int eos() {
        return this.header_base[this.header + 5] & 4;
    }

    public long granulepos() {
        long long0 = this.header_base[this.header + 13] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 12] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 11] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 10] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 9] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 8] & 255;
        long0 = long0 << 8 | this.header_base[this.header + 7] & 255;
        return long0 << 8 | this.header_base[this.header + 6] & 255;
    }

    public int serialno() {
        return this.header_base[this.header + 14] & 0xFF
            | (this.header_base[this.header + 15] & 0xFF) << 8
            | (this.header_base[this.header + 16] & 0xFF) << 16
            | (this.header_base[this.header + 17] & 0xFF) << 24;
    }

    void checksum() {
        int int0 = 0;

        for (int int1 = 0; int1 < this.header_len; int1++) {
            int0 = int0 << 8 ^ crc_lookup[int0 >>> 24 & 0xFF ^ this.header_base[this.header + int1] & 255];
        }

        for (int int2 = 0; int2 < this.body_len; int2++) {
            int0 = int0 << 8 ^ crc_lookup[int0 >>> 24 & 0xFF ^ this.body_base[this.body + int2] & 255];
        }

        this.header_base[this.header + 22] = (byte)int0;
        this.header_base[this.header + 23] = (byte)(int0 >>> 8);
        this.header_base[this.header + 24] = (byte)(int0 >>> 16);
        this.header_base[this.header + 25] = (byte)(int0 >>> 24);
    }

    int continued() {
        return this.header_base[this.header + 5] & 1;
    }

    int pageno() {
        return this.header_base[this.header + 18] & 0xFF
            | (this.header_base[this.header + 19] & 0xFF) << 8
            | (this.header_base[this.header + 20] & 0xFF) << 16
            | (this.header_base[this.header + 21] & 0xFF) << 24;
    }

    int version() {
        return this.header_base[this.header + 4] & 0xFF;
    }

    static {
        for (int int0 = 0; int0 < crc_lookup.length; int0++) {
            crc_lookup[int0] = crc_entry(int0);
        }
    }
}
