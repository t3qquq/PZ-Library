// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

final class PngInputStream extends InputStream implements DataInput {
    private final CRC32 crc = new CRC32();
    private final InputStream in;
    private final DataInputStream data;
    private final byte[] tmp = new byte[4096];
    private long total;
    private int length;
    private int left;

    public PngInputStream(InputStream inputStream) throws IOException {
        this.in = inputStream;
        this.data = new DataInputStream(this);
        this.left = 8;
        long long0 = this.readLong();
        if (long0 != -8552249625308161526L) {
            throw new PngException("Improper signature, expected 0x" + Long.toHexString(-8552249625308161526L) + ", got 0x" + Long.toHexString(long0), true);
        } else {
            this.total += 8L;
        }
    }

    public int startChunk() throws IOException {
        this.left = 8;
        this.length = this.readInt();
        if (this.length < 0) {
            throw new PngException("Bad chunk length: " + (4294967295L & this.length), true);
        } else {
            this.crc.reset();
            int int0 = this.readInt();
            this.left = this.length;
            this.total += 8L;
            return int0;
        }
    }

    public int endChunk(int int0) throws IOException {
        if (this.getRemaining() != 0) {
            throw new PngException(PngConstants.getChunkName(int0) + " read " + (this.length - this.left) + " bytes, expected " + this.length, true);
        } else {
            this.left = 4;
            int int1 = (int)this.crc.getValue();
            int int2 = this.readInt();
            if (int1 != int2) {
                throw new PngException("Bad CRC value for " + PngConstants.getChunkName(int0) + " chunk", true);
            } else {
                this.total = this.total + (this.length + 4);
                return int1;
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (this.left == 0) {
            return -1;
        } else {
            int int0 = this.in.read();
            if (int0 != -1) {
                this.crc.update(int0);
                this.left--;
            }

            return int0;
        }
    }

    @Override
    public int read(byte[] bytes, int int2, int int0) throws IOException {
        if (int0 == 0) {
            return 0;
        } else if (this.left == 0) {
            return -1;
        } else {
            int int1 = this.in.read(bytes, int2, Math.min(this.left, int0));
            if (int1 != -1) {
                this.crc.update(bytes, int2, int1);
                this.left -= int1;
            }

            return int1;
        }
    }

    @Override
    public long skip(long long0) throws IOException {
        int int0 = this.read(this.tmp, 0, (int)Math.min((long)this.tmp.length, long0));
        return int0 < 0 ? 0L : int0;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("do not close me");
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.readUnsignedByte() != 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int int0 = this.read();
        if (int0 < 0) {
            throw new EOFException();
        } else {
            return int0;
        }
    }

    @Override
    public byte readByte() throws IOException {
        return (byte)this.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int int0 = this.read();
        int int1 = this.read();
        if ((int0 | int1) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 8) + (int1 << 0);
        }
    }

    @Override
    public short readShort() throws IOException {
        return (short)this.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char)this.readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        int int0 = this.read();
        int int1 = this.read();
        int int2 = this.read();
        int int3 = this.read();
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 24) + (int1 << 16) + (int2 << 8) + (int3 << 0);
        }
    }

    @Override
    public long readLong() throws IOException {
        return (4294967295L & this.readInt()) << 32 | 4294967295L & this.readInt();
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public void readFully(byte[] bytes) throws IOException {
        this.data.readFully(bytes, 0, bytes.length);
    }

    @Override
    public void readFully(byte[] bytes, int int0, int int1) throws IOException {
        this.data.readFully(bytes, int0, int1);
    }

    @Override
    public int skipBytes(int int0) throws IOException {
        return this.data.skipBytes(int0);
    }

    @Override
    public String readLine() throws IOException {
        return this.data.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return this.data.readUTF();
    }

    public int getRemaining() {
        return this.left;
    }

    public long getOffset() {
        return this.total;
    }
}
