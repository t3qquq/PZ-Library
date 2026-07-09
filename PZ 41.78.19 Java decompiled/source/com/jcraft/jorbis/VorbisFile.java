// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class VorbisFile {
    static final int CHUNKSIZE = 8500;
    static final int SEEK_SET = 0;
    static final int SEEK_CUR = 1;
    static final int SEEK_END = 2;
    static final int OV_FALSE = -1;
    static final int OV_EOF = -2;
    static final int OV_HOLE = -3;
    static final int OV_EREAD = -128;
    static final int OV_EFAULT = -129;
    static final int OV_EIMPL = -130;
    static final int OV_EINVAL = -131;
    static final int OV_ENOTVORBIS = -132;
    static final int OV_EBADHEADER = -133;
    static final int OV_EVERSION = -134;
    static final int OV_ENOTAUDIO = -135;
    static final int OV_EBADPACKET = -136;
    static final int OV_EBADLINK = -137;
    static final int OV_ENOSEEK = -138;
    float bittrack;
    int current_link;
    int current_serialno;
    long[] dataoffsets;
    InputStream datasource;
    boolean decode_ready = false;
    long end;
    int links;
    long offset;
    long[] offsets;
    StreamState os = new StreamState();
    SyncState oy = new SyncState();
    long pcm_offset;
    long[] pcmlengths;
    float samptrack;
    boolean seekable = false;
    int[] serialnos;
    Comment[] vc;
    DspState vd = new DspState();
    Block vb = new Block(this.vd);
    Info[] vi;

    public VorbisFile(String string) throws JOrbisException {
        VorbisFile.SeekableInputStream seekableInputStream = null;

        try {
            seekableInputStream = new VorbisFile.SeekableInputStream(string);
            int int0 = this.open(seekableInputStream, null, 0);
            if (int0 == -1) {
                throw new JOrbisException("VorbisFile: open return -1");
            }
        } catch (Exception exception) {
            throw new JOrbisException("VorbisFile: " + exception.toString());
        } finally {
            if (seekableInputStream != null) {
                try {
                    seekableInputStream.close();
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
    }

    public VorbisFile(InputStream inputStream, byte[] bytes, int int1) throws JOrbisException {
        int int0 = this.open(inputStream, bytes, int1);
        if (int0 == -1) {
        }
    }

    static int fseek(InputStream inputStream, long long0, int int0) {
        if (inputStream instanceof VorbisFile.SeekableInputStream seekableInputStream) {
            try {
                if (int0 == 0) {
                    seekableInputStream.seek(long0);
                } else if (int0 == 2) {
                    seekableInputStream.seek(seekableInputStream.getLength() - long0);
                }
            } catch (Exception exception0) {
            }

            return 0;
        } else {
            try {
                if (int0 == 0) {
                    inputStream.reset();
                }

                inputStream.skip(long0);
                return 0;
            } catch (Exception exception1) {
                return -1;
            }
        }
    }

    static long ftell(InputStream inputStream) {
        try {
            if (inputStream instanceof VorbisFile.SeekableInputStream seekableInputStream) {
                return seekableInputStream.tell();
            }
        } catch (Exception exception) {
        }

        return 0L;
    }

    public int bitrate(int int0) {
        if (int0 >= this.links) {
            return -1;
        } else if (!this.seekable && int0 != 0) {
            return this.bitrate(0);
        } else if (int0 >= 0) {
            if (this.seekable) {
                return (int)Math.rint((float)((this.offsets[int0 + 1] - this.dataoffsets[int0]) * 8L) / this.time_total(int0));
            } else if (this.vi[int0].bitrate_nominal > 0) {
                return this.vi[int0].bitrate_nominal;
            } else if (this.vi[int0].bitrate_upper > 0) {
                return this.vi[int0].bitrate_lower > 0 ? (this.vi[int0].bitrate_upper + this.vi[int0].bitrate_lower) / 2 : this.vi[int0].bitrate_upper;
            } else {
                return -1;
            }
        } else {
            long long0 = 0L;

            for (int int1 = 0; int1 < this.links; int1++) {
                long0 += (this.offsets[int1 + 1] - this.dataoffsets[int1]) * 8L;
            }

            return (int)Math.rint((float)long0 / this.time_total(-1));
        }
    }

    public int bitrate_instant() {
        int int0 = this.seekable ? this.current_link : 0;
        if (this.samptrack == 0.0F) {
            return -1;
        } else {
            int int1 = (int)(this.bittrack / this.samptrack * this.vi[int0].rate + 0.5);
            this.bittrack = 0.0F;
            this.samptrack = 0.0F;
            return int1;
        }
    }

    public void close() throws IOException {
        this.datasource.close();
    }

    public Comment[] getComment() {
        return this.vc;
    }

    public Comment getComment(int int0) {
        if (this.seekable) {
            if (int0 < 0) {
                return this.decode_ready ? this.vc[this.current_link] : null;
            } else {
                return int0 >= this.links ? null : this.vc[int0];
            }
        } else {
            return this.decode_ready ? this.vc[0] : null;
        }
    }

    public Info[] getInfo() {
        return this.vi;
    }

    public Info getInfo(int int0) {
        if (this.seekable) {
            if (int0 < 0) {
                return this.decode_ready ? this.vi[this.current_link] : null;
            } else {
                return int0 >= this.links ? null : this.vi[int0];
            }
        } else {
            return this.decode_ready ? this.vi[0] : null;
        }
    }

    public int pcm_seek(long long1) {
        int int0 = -1;
        long long0 = this.pcm_total(-1);
        if (!this.seekable) {
            return -1;
        } else if (long1 >= 0L && long1 <= long0) {
            for (int0 = this.links - 1; int0 >= 0; int0--) {
                long0 -= this.pcmlengths[int0];
                if (long1 >= long0) {
                    break;
                }
            }

            long long2 = long1 - long0;
            long long3 = this.offsets[int0 + 1];
            long long4 = this.offsets[int0];
            int int1 = (int)long4;
            Page page = new Page();

            while (long4 < long3) {
                long long5;
                if (long3 - long4 < 8500L) {
                    long5 = long4;
                } else {
                    long5 = (long3 + long4) / 2L;
                }

                this.seek_helper(long5);
                int int2 = this.get_next_page(page, long3 - long5);
                if (int2 == -1) {
                    long3 = long5;
                } else {
                    long long6 = page.granulepos();
                    if (long6 < long2) {
                        int1 = int2;
                        long4 = this.offset;
                    } else {
                        long3 = long5;
                    }
                }
            }

            if (this.raw_seek(int1) != 0) {
                this.pcm_offset = -1L;
                this.decode_clear();
                return -1;
            } else if (this.pcm_offset >= long1) {
                this.pcm_offset = -1L;
                this.decode_clear();
                return -1;
            } else if (long1 > this.pcm_total(-1)) {
                this.pcm_offset = -1L;
                this.decode_clear();
                return -1;
            } else {
                while (this.pcm_offset < long1) {
                    int int3 = (int)(long1 - this.pcm_offset);
                    float[][][] floats = new float[1][][];
                    int[] ints = new int[this.getInfo(-1).channels];
                    int int4 = this.vd.synthesis_pcmout(floats, ints);
                    if (int4 > int3) {
                        int4 = int3;
                    }

                    this.vd.synthesis_read(int4);
                    this.pcm_offset += int4;
                    if (int4 < int3 && this.process_packet(1) == 0) {
                        this.pcm_offset = this.pcm_total(-1);
                    }
                }

                return 0;
            }
        } else {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
        }
    }

    public long pcm_tell() {
        return this.pcm_offset;
    }

    public long pcm_total(int int0) {
        if (!this.seekable || int0 >= this.links) {
            return -1L;
        } else if (int0 >= 0) {
            return this.pcmlengths[int0];
        } else {
            long long0 = 0L;

            for (int int1 = 0; int1 < this.links; int1++) {
                long0 += this.pcm_total(int1);
            }

            return long0;
        }
    }

    public int raw_seek(int int0) {
        if (!this.seekable) {
            return -1;
        } else if (int0 >= 0 && int0 <= this.offsets[this.links]) {
            this.pcm_offset = -1L;
            this.decode_clear();
            this.seek_helper(int0);
            switch (this.process_packet(1)) {
                case -1:
                    this.pcm_offset = -1L;
                    this.decode_clear();
                    return -1;
                case 0:
                    this.pcm_offset = this.pcm_total(-1);
                    return 0;
                default:
                    while (true) {
                        switch (this.process_packet(0)) {
                            case -1:
                                this.pcm_offset = -1L;
                                this.decode_clear();
                                return -1;
                            case 0:
                                return 0;
                        }
                    }
            }
        } else {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
        }
    }

    public long raw_tell() {
        return this.offset;
    }

    public long raw_total(int int0) {
        if (!this.seekable || int0 >= this.links) {
            return -1L;
        } else if (int0 >= 0) {
            return this.offsets[int0 + 1] - this.offsets[int0];
        } else {
            long long0 = 0L;

            for (int int1 = 0; int1 < this.links; int1++) {
                long0 += this.raw_total(int1);
            }

            return long0;
        }
    }

    public boolean seekable() {
        return this.seekable;
    }

    public int serialnumber(int int0) {
        if (int0 >= this.links) {
            return -1;
        } else if (!this.seekable && int0 >= 0) {
            return this.serialnumber(-1);
        } else {
            return int0 < 0 ? this.current_serialno : this.serialnos[int0];
        }
    }

    public int streams() {
        return this.links;
    }

    public float time_tell() {
        int int0 = -1;
        long long0 = 0L;
        float float0 = 0.0F;
        if (this.seekable) {
            long0 = this.pcm_total(-1);
            float0 = this.time_total(-1);

            for (int0 = this.links - 1; int0 >= 0; int0--) {
                long0 -= this.pcmlengths[int0];
                float0 -= this.time_total(int0);
                if (this.pcm_offset >= long0) {
                    break;
                }
            }
        }

        return float0 + (float)(this.pcm_offset - long0) / this.vi[int0].rate;
    }

    public float time_total(int int0) {
        if (!this.seekable || int0 >= this.links) {
            return -1.0F;
        } else if (int0 >= 0) {
            return (float)this.pcmlengths[int0] / this.vi[int0].rate;
        } else {
            float float0 = 0.0F;

            for (int int1 = 0; int1 < this.links; int1++) {
                float0 += this.time_total(int1);
            }

            return float0;
        }
    }

    int bisect_forward_serialno(long long5, long long3, long long1, int int1, int int3) {
        long long0 = long1;
        long long2 = long1;
        Page page = new Page();

        while (long3 < long0) {
            long long4;
            if (long0 - long3 < 8500L) {
                long4 = long3;
            } else {
                long4 = (long3 + long0) / 2L;
            }

            this.seek_helper(long4);
            int int0 = this.get_next_page(page, -1L);
            if (int0 == -128) {
                return -128;
            }

            if (int0 >= 0 && page.serialno() == int1) {
                long3 = int0 + page.header_len + page.body_len;
            } else {
                long0 = long4;
                if (int0 >= 0) {
                    long2 = int0;
                }
            }
        }

        this.seek_helper(long2);
        int int2 = this.get_next_page(page, -1L);
        if (int2 == -128) {
            return -128;
        } else {
            if (long3 < long1 && int2 != -1) {
                int2 = this.bisect_forward_serialno(long2, this.offset, long1, page.serialno(), int3 + 1);
                if (int2 == -128) {
                    return -128;
                }
            } else {
                this.links = int3 + 1;
                this.offsets = new long[int3 + 2];
                this.offsets[int3 + 1] = long3;
            }

            this.offsets[int3] = long5;
            return 0;
        }
    }

    int clear() {
        this.vb.clear();
        this.vd.clear();
        this.os.clear();
        if (this.vi != null && this.links != 0) {
            for (int int0 = 0; int0 < this.links; int0++) {
                this.vi[int0].clear();
                this.vc[int0].clear();
            }

            this.vi = null;
            this.vc = null;
        }

        if (this.dataoffsets != null) {
            this.dataoffsets = null;
        }

        if (this.pcmlengths != null) {
            this.pcmlengths = null;
        }

        if (this.serialnos != null) {
            this.serialnos = null;
        }

        if (this.offsets != null) {
            this.offsets = null;
        }

        this.oy.clear();
        return 0;
    }

    void decode_clear() {
        this.os.clear();
        this.vd.clear();
        this.vb.clear();
        this.decode_ready = false;
        this.bittrack = 0.0F;
        this.samptrack = 0.0F;
    }

    int fetch_headers(Info info, Comment comment, int[] ints, Page page1) {
        Page page0 = new Page();
        Packet packet = new Packet();
        if (page1 == null) {
            int int0 = this.get_next_page(page0, 8500L);
            if (int0 == -128) {
                return -128;
            }

            if (int0 < 0) {
                return -132;
            }

            page1 = page0;
        }

        if (ints != null) {
            ints[0] = page1.serialno();
        }

        this.os.init(page1.serialno());
        info.init();
        comment.init();
        int int1 = 0;

        while (int1 < 3) {
            this.os.pagein(page1);

            while (int1 < 3) {
                int int2 = this.os.packetout(packet);
                if (int2 == 0) {
                    break;
                }

                if (int2 == -1) {
                    info.clear();
                    comment.clear();
                    this.os.clear();
                    return -1;
                }

                if (info.synthesis_headerin(comment, packet) != 0) {
                    info.clear();
                    comment.clear();
                    this.os.clear();
                    return -1;
                }

                int1++;
            }

            if (int1 < 3 && this.get_next_page(page1, 1L) < 0) {
                info.clear();
                comment.clear();
                this.os.clear();
                return -1;
            }
        }

        return 0;
    }

    int host_is_big_endian() {
        return 1;
    }

    int open(InputStream inputStream, byte[] bytes, int int0) throws JOrbisException {
        return this.open_callbacks(inputStream, bytes, int0);
    }

    int open_callbacks(InputStream inputStream, byte[] bytes, int int1) throws JOrbisException {
        this.datasource = inputStream;
        this.oy.init();
        if (bytes != null) {
            int int0 = this.oy.buffer(int1);
            System.arraycopy(bytes, 0, this.oy.data, int0, int1);
            this.oy.wrote(int1);
        }

        int int2;
        if (inputStream instanceof VorbisFile.SeekableInputStream) {
            int2 = this.open_seekable();
        } else {
            int2 = this.open_nonseekable();
        }

        if (int2 != 0) {
            this.datasource = null;
            this.clear();
        }

        return int2;
    }

    int open_nonseekable() {
        this.links = 1;
        this.vi = new Info[this.links];
        this.vi[0] = new Info();
        this.vc = new Comment[this.links];
        this.vc[0] = new Comment();
        int[] ints = new int[1];
        if (this.fetch_headers(this.vi[0], this.vc[0], ints, null) == -1) {
            return -1;
        } else {
            this.current_serialno = ints[0];
            this.make_decode_ready();
            return 0;
        }
    }

    int open_seekable() throws JOrbisException {
        Info info = new Info();
        Comment comment = new Comment();
        Page page = new Page();
        int[] ints = new int[1];
        int int0 = this.fetch_headers(info, comment, ints, null);
        int int1 = ints[0];
        int int2 = (int)this.offset;
        this.os.clear();
        if (int0 == -1) {
            return -1;
        } else if (int0 < 0) {
            return int0;
        } else {
            this.seekable = true;
            fseek(this.datasource, 0L, 2);
            this.offset = ftell(this.datasource);
            long long0 = this.offset;
            long0 = this.get_prev_page(page);
            if (page.serialno() != int1) {
                if (this.bisect_forward_serialno(0L, 0L, long0 + 1L, int1, 0) < 0) {
                    this.clear();
                    return -128;
                }
            } else if (this.bisect_forward_serialno(0L, long0, long0 + 1L, int1, 0) < 0) {
                this.clear();
                return -128;
            }

            this.prefetch_all_headers(info, comment, int2);
            return 0;
        }
    }

    void prefetch_all_headers(Info info, Comment comment, int int1) throws JOrbisException {
        Page page = new Page();
        this.vi = new Info[this.links];
        this.vc = new Comment[this.links];
        this.dataoffsets = new long[this.links];
        this.pcmlengths = new long[this.links];
        this.serialnos = new int[this.links];

        label38:
        for (int int0 = 0; int0 < this.links; int0++) {
            if (info != null && comment != null && int0 == 0) {
                this.vi[int0] = info;
                this.vc[int0] = comment;
                this.dataoffsets[int0] = int1;
            } else {
                this.seek_helper(this.offsets[int0]);
                this.vi[int0] = new Info();
                this.vc[int0] = new Comment();
                if (this.fetch_headers(this.vi[int0], this.vc[int0], null, null) == -1) {
                    this.dataoffsets[int0] = -1L;
                } else {
                    this.dataoffsets[int0] = this.offset;
                    this.os.clear();
                }
            }

            long long0 = this.offsets[int0 + 1];
            this.seek_helper(long0);

            do {
                int int2 = this.get_prev_page(page);
                if (int2 == -1) {
                    this.vi[int0].clear();
                    this.vc[int0].clear();
                    continue label38;
                }
            } while (page.granulepos() == -1L);

            this.serialnos[int0] = page.serialno();
            this.pcmlengths[int0] = page.granulepos();
        }
    }

    int process_packet(int int4) {
        Page page = new Page();

        while (true) {
            if (this.decode_ready) {
                Packet packet = new Packet();
                int int0 = this.os.packetout(packet);
                if (int0 > 0) {
                    long long0 = packet.granulepos;
                    if (this.vb.synthesis(packet) == 0) {
                        int int1 = this.vd.synthesis_pcmout(null, null);
                        this.vd.synthesis_blockin(this.vb);
                        this.samptrack = this.samptrack + (this.vd.synthesis_pcmout(null, null) - int1);
                        this.bittrack = this.bittrack + packet.bytes * 8;
                        if (long0 != -1L && packet.e_o_s == 0) {
                            int1 = this.seekable ? this.current_link : 0;
                            int int2 = this.vd.synthesis_pcmout(null, null);
                            long0 -= int2;

                            for (int int3 = 0; int3 < int1; int3++) {
                                long0 += this.pcmlengths[int3];
                            }

                            this.pcm_offset = long0;
                        }

                        return 1;
                    }
                }
            }

            if (int4 == 0) {
                return 0;
            }

            if (this.get_next_page(page, -1L) < 0) {
                return 0;
            }

            this.bittrack = this.bittrack + page.header_len * 8;
            if (this.decode_ready && this.current_serialno != page.serialno()) {
                this.decode_clear();
            }

            if (!this.decode_ready) {
                if (!this.seekable) {
                    int[] ints = new int[1];
                    int int5 = this.fetch_headers(this.vi[0], this.vc[0], ints, page);
                    this.current_serialno = ints[0];
                    if (int5 != 0) {
                        return int5;
                    }

                    this.current_link++;
                    boolean boolean0 = false;
                } else {
                    this.current_serialno = page.serialno();
                    int int6 = 0;

                    while (int6 < this.links && this.serialnos[int6] != this.current_serialno) {
                        int6++;
                    }

                    if (int6 == this.links) {
                        return -1;
                    }

                    this.current_link = int6;
                    this.os.init(this.current_serialno);
                    this.os.reset();
                }

                this.make_decode_ready();
            }

            this.os.pagein(page);
        }
    }

    int read(byte[] bytes, int int6, int int13, int int5, int int8, int[] ints1) {
        int int0 = this.host_is_big_endian();
        int int1 = 0;

        while (true) {
            if (this.decode_ready) {
                float[][][] floats0 = new float[1][][];
                int[] ints0 = new int[this.getInfo(-1).channels];
                int int2 = this.vd.synthesis_pcmout(floats0, ints0);
                float[][] floats1 = floats0[0];
                if (int2 != 0) {
                    int int3 = this.getInfo(-1).channels;
                    int int4 = int5 * int3;
                    if (int2 > int6 / int4) {
                        int2 = int6 / int4;
                    }

                    if (int5 == 1) {
                        int int7 = int8 != 0 ? 0 : 128;

                        for (int int9 = 0; int9 < int2; int9++) {
                            for (int int10 = 0; int10 < int3; int10++) {
                                int int11 = (int)(floats1[int10][ints0[int10] + int9] * 128.0 + 0.5);
                                if (int11 > 127) {
                                    int11 = 127;
                                } else if (int11 < -128) {
                                    int11 = -128;
                                }

                                bytes[int1++] = (byte)(int11 + int7);
                            }
                        }
                    } else {
                        int int12 = int8 != 0 ? 0 : '\u8000';
                        if (int0 == int13) {
                            if (int8 != 0) {
                                for (int int14 = 0; int14 < int3; int14++) {
                                    int int15 = ints0[int14];
                                    int int16 = int14;

                                    for (int int17 = 0; int17 < int2; int17++) {
                                        int int18 = (int)(floats1[int14][int15 + int17] * 32768.0 + 0.5);
                                        if (int18 > 32767) {
                                            int18 = 32767;
                                        } else if (int18 < -32768) {
                                            int18 = -32768;
                                        }

                                        bytes[int16] = (byte)(int18 >>> 8);
                                        bytes[int16 + 1] = (byte)int18;
                                        int16 += int3 * 2;
                                    }
                                }
                            } else {
                                for (int int19 = 0; int19 < int3; int19++) {
                                    float[] floats2 = floats1[int19];
                                    int int20 = int19;

                                    for (int int21 = 0; int21 < int2; int21++) {
                                        int int22 = (int)(floats2[int21] * 32768.0 + 0.5);
                                        if (int22 > 32767) {
                                            int22 = 32767;
                                        } else if (int22 < -32768) {
                                            int22 = -32768;
                                        }

                                        bytes[int20] = (byte)(int22 + int12 >>> 8);
                                        bytes[int20 + 1] = (byte)(int22 + int12);
                                        int20 += int3 * 2;
                                    }
                                }
                            }
                        } else if (int13 != 0) {
                            for (int int23 = 0; int23 < int2; int23++) {
                                for (int int24 = 0; int24 < int3; int24++) {
                                    int int25 = (int)(floats1[int24][int23] * 32768.0 + 0.5);
                                    if (int25 > 32767) {
                                        int25 = 32767;
                                    } else if (int25 < -32768) {
                                        int25 = -32768;
                                    }

                                    int25 += int12;
                                    bytes[int1++] = (byte)(int25 >>> 8);
                                    bytes[int1++] = (byte)int25;
                                }
                            }
                        } else {
                            for (int int26 = 0; int26 < int2; int26++) {
                                for (int int27 = 0; int27 < int3; int27++) {
                                    int int28 = (int)(floats1[int27][int26] * 32768.0 + 0.5);
                                    if (int28 > 32767) {
                                        int28 = 32767;
                                    } else if (int28 < -32768) {
                                        int28 = -32768;
                                    }

                                    int28 += int12;
                                    bytes[int1++] = (byte)int28;
                                    bytes[int1++] = (byte)(int28 >>> 8);
                                }
                            }
                        }
                    }

                    this.vd.synthesis_read(int2);
                    this.pcm_offset += int2;
                    if (ints1 != null) {
                        ints1[0] = this.current_link;
                    }

                    return int2 * int4;
                }
            }

            switch (this.process_packet(1)) {
                case -1:
                    return -1;
                case 0:
                    return 0;
            }
        }
    }

    int time_seek(float float1) {
        int int0 = -1;
        long long0 = this.pcm_total(-1);
        float float0 = this.time_total(-1);
        if (!this.seekable) {
            return -1;
        } else if (!(float1 < 0.0F) && !(float1 > float0)) {
            for (int0 = this.links - 1; int0 >= 0; int0--) {
                long0 -= this.pcmlengths[int0];
                float0 -= this.time_total(int0);
                if (float1 >= float0) {
                    break;
                }
            }

            long long1 = (long)((float)long0 + (float1 - float0) * this.vi[int0].rate);
            return this.pcm_seek(long1);
        } else {
            this.pcm_offset = -1L;
            this.decode_clear();
            return -1;
        }
    }

    private int get_data() {
        int int0 = this.oy.buffer(8500);
        byte[] bytes = this.oy.data;
        int int1 = 0;

        try {
            int1 = this.datasource.read(bytes, int0, 8500);
        } catch (Exception exception) {
            return -128;
        }

        this.oy.wrote(int1);
        if (int1 == -1) {
            int1 = 0;
        }

        return int1;
    }

    private int get_next_page(Page page, long long0) {
        if (long0 > 0L) {
            long0 += this.offset;
        }

        while (long0 <= 0L || this.offset < long0) {
            int int0 = this.oy.pageseek(page);
            if (int0 < 0) {
                this.offset -= int0;
            } else {
                if (int0 != 0) {
                    int int1 = (int)this.offset;
                    this.offset += int0;
                    return int1;
                }

                if (long0 == 0L) {
                    return -1;
                }

                int int2 = this.get_data();
                if (int2 == 0) {
                    return -2;
                }

                if (int2 < 0) {
                    return -128;
                }
            }
        }

        return -1;
    }

    private int get_prev_page(Page page) throws JOrbisException {
        long long0 = this.offset;
        int int0 = -1;

        while (int0 == -1) {
            long0 -= 8500L;
            if (long0 < 0L) {
                long0 = 0L;
            }

            this.seek_helper(long0);

            while (this.offset < long0 + 8500L) {
                int int1 = this.get_next_page(page, long0 + 8500L - this.offset);
                if (int1 == -128) {
                    return -128;
                }

                if (int1 < 0) {
                    if (int0 == -1) {
                        throw new JOrbisException();
                    }
                    break;
                }

                int0 = int1;
            }
        }

        this.seek_helper(int0);
        int int2 = this.get_next_page(page, 8500L);
        return int2 < 0 ? -129 : int0;
    }

    private int make_decode_ready() {
        if (this.decode_ready) {
            System.exit(1);
        }

        this.vd.synthesis_init(this.vi[0]);
        this.vb.init(this.vd);
        this.decode_ready = true;
        return 0;
    }

    private void seek_helper(long long0) {
        fseek(this.datasource, long0, 0);
        this.offset = long0;
        this.oy.reset();
    }

    class SeekableInputStream extends InputStream {
        final String mode = "r";
        RandomAccessFile raf = null;

        SeekableInputStream(String string) throws IOException {
            this.raf = new RandomAccessFile(string, "r");
        }

        @Override
        public int available() throws IOException {
            return this.raf.length() == this.raf.getFilePointer() ? 0 : 1;
        }

        @Override
        public void close() throws IOException {
            this.raf.close();
        }

        public long getLength() throws IOException {
            return this.raf.length();
        }

        @Override
        public synchronized void mark(int var1) {
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read() throws IOException {
            return this.raf.read();
        }

        @Override
        public int read(byte[] bytes) throws IOException {
            return this.raf.read(bytes);
        }

        @Override
        public int read(byte[] bytes, int int0, int int1) throws IOException {
            return this.raf.read(bytes, int0, int1);
        }

        @Override
        public synchronized void reset() throws IOException {
        }

        public void seek(long long0) throws IOException {
            this.raf.seek(long0);
        }

        @Override
        public long skip(long long0) throws IOException {
            return this.raf.skipBytes((int)long0);
        }

        public long tell() throws IOException {
            return this.raf.getFilePointer();
        }
    }
}
