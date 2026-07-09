// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;

public class Comment {
    private static byte[] _vorbis = "vorbis".getBytes();
    private static byte[] _vendor = "Xiphophorus libVorbis I 20000508".getBytes();
    private static final int OV_EIMPL = -130;
    public int[] comment_lengths;
    public int comments;
    public byte[][] user_comments;
    public byte[] vendor;

    static boolean tagcompare(byte[] bytes0, byte[] bytes1, int int1) {
        for (int int0 = 0; int0 < int1; int0++) {
            byte byte0 = bytes0[int0];
            byte byte1 = bytes1[int0];
            if (90 >= byte0 && byte0 >= 65) {
                byte0 = (byte)(byte0 - 65 + 97);
            }

            if (90 >= byte1 && byte1 >= 65) {
                byte1 = (byte)(byte1 - 65 + 97);
            }

            if (byte0 != byte1) {
                return false;
            }
        }

        return true;
    }

    public void add(String string) {
        this.add(string.getBytes());
    }

    public void add_tag(String string1, String string0) {
        if (string0 == null) {
            string0 = "";
        }

        this.add(string1 + "=" + string0);
    }

    public String getComment(int int0) {
        return this.comments <= int0 ? null : new String(this.user_comments[int0], 0, this.user_comments[int0].length - 1);
    }

    public String getVendor() {
        return new String(this.vendor, 0, this.vendor.length - 1);
    }

    public int header_out(Packet packet) {
        Buffer buffer = new Buffer();
        buffer.writeinit();
        if (this.pack(buffer) != 0) {
            return -130;
        } else {
            packet.packet_base = new byte[buffer.bytes()];
            packet.packet = 0;
            packet.bytes = buffer.bytes();
            System.arraycopy(buffer.buffer(), 0, packet.packet_base, 0, packet.bytes);
            packet.b_o_s = 0;
            packet.e_o_s = 0;
            packet.granulepos = 0L;
            return 0;
        }
    }

    public void init() {
        this.user_comments = null;
        this.comments = 0;
        this.vendor = null;
    }

    public String query(String string) {
        return this.query(string, 0);
    }

    public String query(String string, int int1) {
        int int0 = this.query(string.getBytes(), int1);
        if (int0 == -1) {
            return null;
        } else {
            byte[] bytes = this.user_comments[int0];

            for (int int2 = 0; int2 < this.comment_lengths[int0]; int2++) {
                if (bytes[int2] == 61) {
                    return new String(bytes, int2 + 1, this.comment_lengths[int0] - (int2 + 1));
                }
            }

            return null;
        }
    }

    @Override
    public String toString() {
        String string = "Vendor: " + new String(this.vendor, 0, this.vendor.length - 1);

        for (int int0 = 0; int0 < this.comments; int0++) {
            string = string + "\nComment: " + new String(this.user_comments[int0], 0, this.user_comments[int0].length - 1);
        }

        return string + "\n";
    }

    void clear() {
        for (int int0 = 0; int0 < this.comments; int0++) {
            this.user_comments[int0] = null;
        }

        this.user_comments = null;
        this.vendor = null;
    }

    int pack(Buffer buffer) {
        buffer.write(3, 8);
        buffer.write(_vorbis);
        buffer.write(_vendor.length, 32);
        buffer.write(_vendor);
        buffer.write(this.comments, 32);
        if (this.comments != 0) {
            for (int int0 = 0; int0 < this.comments; int0++) {
                if (this.user_comments[int0] != null) {
                    buffer.write(this.comment_lengths[int0], 32);
                    buffer.write(this.user_comments[int0]);
                } else {
                    buffer.write(0, 32);
                }
            }
        }

        buffer.write(1, 1);
        return 0;
    }

    int unpack(Buffer buffer) {
        int int0 = buffer.read(32);
        if (int0 < 0) {
            this.clear();
            return -1;
        } else {
            this.vendor = new byte[int0 + 1];
            buffer.read(this.vendor, int0);
            this.comments = buffer.read(32);
            if (this.comments < 0) {
                this.clear();
                return -1;
            } else {
                this.user_comments = new byte[this.comments + 1][];
                this.comment_lengths = new int[this.comments + 1];

                for (int int1 = 0; int1 < this.comments; int1++) {
                    int int2 = buffer.read(32);
                    if (int2 < 0) {
                        this.clear();
                        return -1;
                    }

                    this.comment_lengths[int1] = int2;
                    this.user_comments[int1] = new byte[int2 + 1];
                    buffer.read(this.user_comments[int1], int2);
                }

                if (buffer.read(1) != 1) {
                    this.clear();
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    private void add(byte[] bytes2) {
        byte[][] bytes0 = new byte[this.comments + 2][];
        if (this.user_comments != null) {
            System.arraycopy(this.user_comments, 0, bytes0, 0, this.comments);
        }

        this.user_comments = bytes0;
        int[] ints = new int[this.comments + 2];
        if (this.comment_lengths != null) {
            System.arraycopy(this.comment_lengths, 0, ints, 0, this.comments);
        }

        this.comment_lengths = ints;
        byte[] bytes1 = new byte[bytes2.length + 1];
        System.arraycopy(bytes2, 0, bytes1, 0, bytes2.length);
        this.user_comments[this.comments] = bytes1;
        this.comment_lengths[this.comments] = bytes2.length;
        this.comments++;
        this.user_comments[this.comments] = null;
    }

    private int query(byte[] bytes0, int int3) {
        boolean boolean0 = false;
        int int0 = 0;
        int int1 = bytes0.length + 1;
        byte[] bytes1 = new byte[int1];
        System.arraycopy(bytes0, 0, bytes1, 0, bytes0.length);
        bytes1[bytes0.length] = 61;

        for (int int2 = 0; int2 < this.comments; int2++) {
            if (tagcompare(this.user_comments[int2], bytes1, int1)) {
                if (int3 == int0) {
                    return int2;
                }

                int0++;
            }
        }

        return -1;
    }
}
