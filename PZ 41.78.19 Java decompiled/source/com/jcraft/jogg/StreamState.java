// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jogg;

public class StreamState {
    public int e_o_s;
    int b_o_s;
    byte[] body_data;
    int body_fill;
    int body_storage;
    long[] granule_vals;
    long granulepos;
    byte[] header = new byte[282];
    int header_fill;
    int lacing_fill;
    int lacing_packet;
    int lacing_returned;
    int lacing_storage;
    int[] lacing_vals;
    long packetno;
    int pageno;
    int serialno;
    private int body_returned;

    public StreamState() {
        this.init();
    }

    StreamState(int int0) {
        this();
        this.init(int0);
    }

    public void clear() {
        this.body_data = null;
        this.lacing_vals = null;
        this.granule_vals = null;
    }

    public int eof() {
        return this.e_o_s;
    }

    public int flush(Page page) {
        int int0 = 0;
        int int1 = this.lacing_fill > 255 ? 255 : this.lacing_fill;
        int int2 = 0;
        int int3 = 0;
        long long0 = this.granule_vals[0];
        if (int1 == 0) {
            return 0;
        } else {
            if (this.b_o_s == 0) {
                long0 = 0L;

                for (int0 = 0; int0 < int1; int0++) {
                    if ((this.lacing_vals[int0] & 0xFF) < 255) {
                        int0++;
                        break;
                    }
                }
            } else {
                for (int0 = 0; int0 < int1 && int3 <= 4096; int0++) {
                    int3 += this.lacing_vals[int0] & 0xFF;
                    long0 = this.granule_vals[int0];
                }
            }

            System.arraycopy("OggS".getBytes(), 0, this.header, 0, 4);
            this.header[4] = 0;
            this.header[5] = 0;
            if ((this.lacing_vals[0] & 256) == 0) {
                this.header[5] = (byte)(this.header[5] | 1);
            }

            if (this.b_o_s == 0) {
                this.header[5] = (byte)(this.header[5] | 2);
            }

            if (this.e_o_s != 0 && this.lacing_fill == int0) {
                this.header[5] = (byte)(this.header[5] | 4);
            }

            this.b_o_s = 1;

            for (int int4 = 6; int4 < 14; int4++) {
                this.header[int4] = (byte)long0;
                long0 >>>= 8;
            }

            int int5 = this.serialno;

            for (int int6 = 14; int6 < 18; int6++) {
                this.header[int6] = (byte)int5;
                int5 >>>= 8;
            }

            if (this.pageno == -1) {
                this.pageno = 0;
            }

            int5 = this.pageno++;

            for (int int7 = 18; int7 < 22; int7++) {
                this.header[int7] = (byte)int5;
                int5 >>>= 8;
            }

            this.header[22] = 0;
            this.header[23] = 0;
            this.header[24] = 0;
            this.header[25] = 0;
            this.header[26] = (byte)int0;

            for (int int8 = 0; int8 < int0; int8++) {
                this.header[int8 + 27] = (byte)this.lacing_vals[int8];
                int2 += this.header[int8 + 27] & 255;
            }

            page.header_base = this.header;
            page.header = 0;
            page.header_len = this.header_fill = int0 + 27;
            page.body_base = this.body_data;
            page.body = this.body_returned;
            page.body_len = int2;
            this.lacing_fill -= int0;
            System.arraycopy(this.lacing_vals, int0, this.lacing_vals, 0, this.lacing_fill * 4);
            System.arraycopy(this.granule_vals, int0, this.granule_vals, 0, this.lacing_fill * 8);
            this.body_returned += int2;
            page.checksum();
            return 1;
        }
    }

    public void init(int int3) {
        if (this.body_data == null) {
            this.init();
        } else {
            for (int int0 = 0; int0 < this.body_data.length; int0++) {
                this.body_data[int0] = 0;
            }

            for (int int1 = 0; int1 < this.lacing_vals.length; int1++) {
                this.lacing_vals[int1] = 0;
            }

            for (int int2 = 0; int2 < this.granule_vals.length; int2++) {
                this.granule_vals[int2] = 0L;
            }
        }

        this.serialno = int3;
    }

    public int packetin(Packet packet) {
        int int0 = packet.bytes / 255 + 1;
        if (this.body_returned != 0) {
            this.body_fill = this.body_fill - this.body_returned;
            if (this.body_fill != 0) {
                System.arraycopy(this.body_data, this.body_returned, this.body_data, 0, this.body_fill);
            }

            this.body_returned = 0;
        }

        this.body_expand(packet.bytes);
        this.lacing_expand(int0);
        System.arraycopy(packet.packet_base, packet.packet, this.body_data, this.body_fill, packet.bytes);
        this.body_fill = this.body_fill + packet.bytes;

        int int1;
        for (int1 = 0; int1 < int0 - 1; int1++) {
            this.lacing_vals[this.lacing_fill + int1] = 255;
            this.granule_vals[this.lacing_fill + int1] = this.granulepos;
        }

        this.lacing_vals[this.lacing_fill + int1] = packet.bytes % 255;
        this.granulepos = this.granule_vals[this.lacing_fill + int1] = packet.granulepos;
        this.lacing_vals[this.lacing_fill] = this.lacing_vals[this.lacing_fill] | 256;
        this.lacing_fill += int0;
        this.packetno++;
        if (packet.e_o_s != 0) {
            this.e_o_s = 1;
        }

        return 0;
    }

    public int packetout(Packet packet) {
        int int0 = this.lacing_returned;
        if (this.lacing_packet <= int0) {
            return 0;
        } else if ((this.lacing_vals[int0] & 1024) != 0) {
            this.lacing_returned++;
            this.packetno++;
            return -1;
        } else {
            int int1 = this.lacing_vals[int0] & 0xFF;
            int int2 = 0;
            packet.packet_base = this.body_data;
            packet.packet = this.body_returned;
            packet.e_o_s = this.lacing_vals[int0] & 512;
            packet.b_o_s = this.lacing_vals[int0] & 256;

            for (int2 += int1; int1 == 255; int2 += int1) {
                int int3 = this.lacing_vals[++int0];
                int1 = int3 & 0xFF;
                if ((int3 & 512) != 0) {
                    packet.e_o_s = 512;
                }
            }

            packet.packetno = this.packetno;
            packet.granulepos = this.granule_vals[int0];
            packet.bytes = int2;
            this.body_returned += int2;
            this.lacing_returned = int0 + 1;
            this.packetno++;
            return 1;
        }
    }

    public int pagein(Page page) {
        byte[] bytes0 = page.header_base;
        int int0 = page.header;
        byte[] bytes1 = page.body_base;
        int int1 = page.body;
        int int2 = page.body_len;
        int int3 = 0;
        int int4 = page.version();
        int int5 = page.continued();
        int int6 = page.bos();
        int int7 = page.eos();
        long long0 = page.granulepos();
        int int8 = page.serialno();
        int int9 = page.pageno();
        int int10 = bytes0[int0 + 26] & 255;
        int int11 = this.lacing_returned;
        int int12 = this.body_returned;
        if (int12 != 0) {
            this.body_fill -= int12;
            if (this.body_fill != 0) {
                System.arraycopy(this.body_data, int12, this.body_data, 0, this.body_fill);
            }

            this.body_returned = 0;
        }

        if (int11 != 0) {
            if (this.lacing_fill - int11 != 0) {
                System.arraycopy(this.lacing_vals, int11, this.lacing_vals, 0, this.lacing_fill - int11);
                System.arraycopy(this.granule_vals, int11, this.granule_vals, 0, this.lacing_fill - int11);
            }

            this.lacing_fill -= int11;
            this.lacing_packet -= int11;
            this.lacing_returned = 0;
        }

        if (int8 != this.serialno) {
            return -1;
        } else if (int4 > 0) {
            return -1;
        } else {
            this.lacing_expand(int10 + 1);
            if (int9 != this.pageno) {
                for (int int13 = this.lacing_packet; int13 < this.lacing_fill; int13++) {
                    this.body_fill = this.body_fill - (this.lacing_vals[int13] & 0xFF);
                }

                this.lacing_fill = this.lacing_packet;
                if (this.pageno != -1) {
                    this.lacing_vals[this.lacing_fill++] = 1024;
                    this.lacing_packet++;
                }

                if (int5 != 0) {
                    for (int6 = 0; int3 < int10; int3++) {
                        int12 = bytes0[int0 + 27 + int3] & 255;
                        int1 += int12;
                        int2 -= int12;
                        if (int12 < 255) {
                            int3++;
                            break;
                        }
                    }
                }
            }

            if (int2 != 0) {
                this.body_expand(int2);
                System.arraycopy(bytes1, int1, this.body_data, this.body_fill, int2);
                this.body_fill += int2;
            }

            int11 = -1;

            while (int3 < int10) {
                int12 = bytes0[int0 + 27 + int3] & 255;
                this.lacing_vals[this.lacing_fill] = int12;
                this.granule_vals[this.lacing_fill] = -1L;
                if (int6 != 0) {
                    this.lacing_vals[this.lacing_fill] = this.lacing_vals[this.lacing_fill] | 256;
                    int6 = 0;
                }

                if (int12 < 255) {
                    int11 = this.lacing_fill;
                }

                this.lacing_fill++;
                int3++;
                if (int12 < 255) {
                    this.lacing_packet = this.lacing_fill;
                }
            }

            if (int11 != -1) {
                this.granule_vals[int11] = long0;
            }

            if (int7 != 0) {
                this.e_o_s = 1;
                if (this.lacing_fill > 0) {
                    this.lacing_vals[this.lacing_fill - 1] = this.lacing_vals[this.lacing_fill - 1] | 512;
                }
            }

            this.pageno = int9 + 1;
            return 0;
        }
    }

    public int pageout(Page page) {
        return (this.e_o_s == 0 || this.lacing_fill == 0)
                && this.body_fill - this.body_returned <= 4096
                && this.lacing_fill < 255
                && (this.lacing_fill == 0 || this.b_o_s != 0)
            ? 0
            : this.flush(page);
    }

    public int reset() {
        this.body_fill = 0;
        this.body_returned = 0;
        this.lacing_fill = 0;
        this.lacing_packet = 0;
        this.lacing_returned = 0;
        this.header_fill = 0;
        this.e_o_s = 0;
        this.b_o_s = 0;
        this.pageno = -1;
        this.packetno = 0L;
        this.granulepos = 0L;
        return 0;
    }

    void body_expand(int int0) {
        if (this.body_storage <= this.body_fill + int0) {
            this.body_storage += int0 + 1024;
            byte[] bytes = new byte[this.body_storage];
            System.arraycopy(this.body_data, 0, bytes, 0, this.body_data.length);
            this.body_data = bytes;
        }
    }

    void destroy() {
        this.clear();
    }

    void init() {
        this.body_storage = 16384;
        this.body_data = new byte[this.body_storage];
        this.lacing_storage = 1024;
        this.lacing_vals = new int[this.lacing_storage];
        this.granule_vals = new long[this.lacing_storage];
    }

    void lacing_expand(int int0) {
        if (this.lacing_storage <= this.lacing_fill + int0) {
            this.lacing_storage += int0 + 32;
            int[] ints = new int[this.lacing_storage];
            System.arraycopy(this.lacing_vals, 0, ints, 0, this.lacing_vals.length);
            this.lacing_vals = ints;
            long[] longs = new long[this.lacing_storage];
            System.arraycopy(this.granule_vals, 0, longs, 0, this.granule_vals.length);
            this.granule_vals = longs;
        }
    }
}
