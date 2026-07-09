// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public final class BufferedRandomAccessFile extends RandomAccessFile {
    private byte[] buffer;
    private int buf_end = 0;
    private int buf_pos = 0;
    private long real_pos = 0L;
    private final int BUF_SIZE;

    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws IOException {
        super(filename, mode);
        this.invalidate();
        this.BUF_SIZE = bufsize;
        this.buffer = new byte[this.BUF_SIZE];
    }

    public BufferedRandomAccessFile(File file, String mode, int bufsize) throws IOException {
        super(file, mode);
        this.invalidate();
        this.BUF_SIZE = bufsize;
        this.buffer = new byte[this.BUF_SIZE];
    }

    @Override
    public final int read() throws IOException {
        if (this.buf_pos >= this.buf_end && this.fillBuffer() < 0) {
            return -1;
        } else {
            return this.buf_end == 0 ? -1 : this.buffer[this.buf_pos++] & 0xFF;
        }
    }

    private int fillBuffer() throws IOException {
        int int0 = super.read(this.buffer, 0, this.BUF_SIZE);
        if (int0 >= 0) {
            this.real_pos += int0;
            this.buf_end = int0;
            this.buf_pos = 0;
        }

        return int0;
    }

    private void invalidate() throws IOException {
        this.buf_end = 0;
        this.buf_pos = 0;
        this.real_pos = super.getFilePointer();
    }

    @Override
    public int read(byte[] bytes, int int2, int int1) throws IOException {
        int int0 = this.buf_end - this.buf_pos;
        if (int1 <= int0) {
            System.arraycopy(this.buffer, this.buf_pos, bytes, int2, int1);
            this.buf_pos += int1;
            return int1;
        } else {
            for (int int3 = 0; int3 < int1; int3++) {
                int int4 = this.read();
                if (int4 == -1) {
                    return int3;
                }

                bytes[int2 + int3] = (byte)int4;
            }

            return int1;
        }
    }

    @Override
    public long getFilePointer() throws IOException {
        long long0 = this.real_pos;
        return long0 - this.buf_end + this.buf_pos;
    }

    @Override
    public void seek(long pos) throws IOException {
        int int0 = (int)(this.real_pos - pos);
        if (int0 >= 0 && int0 <= this.buf_end) {
            this.buf_pos = this.buf_end - int0;
        } else {
            super.seek(pos);
            this.invalidate();
        }
    }

    public final String getNextLine() throws IOException {
        String string = null;
        if (this.buf_end - this.buf_pos <= 0 && this.fillBuffer() < 0) {
            throw new IOException("error in filling buffer!");
        } else {
            int int0 = -1;

            for (int int1 = this.buf_pos; int1 < this.buf_end; int1++) {
                if (this.buffer[int1] == 10) {
                    int0 = int1;
                    break;
                }
            }

            if (int0 < 0) {
                StringBuilder stringBuilder = new StringBuilder(128);

                int int2;
                while ((int2 = this.read()) != -1 && int2 != 10) {
                    stringBuilder.append((char)int2);
                }

                return int2 == -1 && stringBuilder.length() == 0 ? null : stringBuilder.toString();
            } else {
                if (int0 > 0 && this.buffer[int0 - 1] == 13) {
                    string = new String(this.buffer, this.buf_pos, int0 - this.buf_pos - 1, StandardCharsets.UTF_8);
                } else {
                    string = new String(this.buffer, this.buf_pos, int0 - this.buf_pos, StandardCharsets.UTF_8);
                }

                this.buf_pos = int0 + 1;
                return string;
            }
        }
    }
}
