// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public final class PNGSize {
    private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static final int IHDR = 1229472850;
    public int width;
    public int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private InputStream input;
    private final CRC32 crc = new CRC32();
    private final byte[] buffer = new byte[4096];
    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;

    public void readSize(String string) {
        try (
            FileInputStream fileInputStream = new FileInputStream(string);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            this.readSize(bufferedInputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void readSize(InputStream inputStream) throws IOException {
        this.input = inputStream;
        this.readFully(this.buffer, 0, SIGNATURE.length);
        if (!this.checkSignature(this.buffer)) {
            throw new IOException("Not a valid PNG file");
        } else {
            this.openChunk(1229472850);
            this.readIHDR();
            this.closeChunk();
        }
    }

    private void readIHDR() throws IOException {
        this.checkChunkLength(13);
        this.readChunk(this.buffer, 0, 13);
        this.width = this.readInt(this.buffer, 0);
        this.height = this.readInt(this.buffer, 4);
        this.bitdepth = this.buffer[8] & 255;
        this.colorType = this.buffer[9] & 255;
    }

    private void openChunk() throws IOException {
        this.readFully(this.buffer, 0, 8);
        this.chunkLength = this.readInt(this.buffer, 0);
        this.chunkType = this.readInt(this.buffer, 4);
        this.chunkRemaining = this.chunkLength;
        this.crc.reset();
        this.crc.update(this.buffer, 4, 4);
    }

    private void openChunk(int int0) throws IOException {
        this.openChunk();
        if (this.chunkType != int0) {
            throw new IOException("Expected chunk: " + Integer.toHexString(int0));
        }
    }

    private void closeChunk() throws IOException {
        if (this.chunkRemaining > 0) {
            this.skip(this.chunkRemaining + 4);
        } else {
            this.readFully(this.buffer, 0, 4);
            int int0 = this.readInt(this.buffer, 0);
            int int1 = (int)this.crc.getValue();
            if (int1 != int0) {
                throw new IOException("Invalid CRC");
            }
        }

        this.chunkRemaining = 0;
        this.chunkLength = 0;
        this.chunkType = 0;
    }

    private void checkChunkLength(int int0) throws IOException {
        if (this.chunkLength != int0) {
            throw new IOException("Chunk has wrong size");
        }
    }

    private int readChunk(byte[] bytes, int int1, int int0) throws IOException {
        if (int0 > this.chunkRemaining) {
            int0 = this.chunkRemaining;
        }

        this.readFully(bytes, int1, int0);
        this.crc.update(bytes, int1, int0);
        this.chunkRemaining -= int0;
        return int0;
    }

    private void readFully(byte[] bytes, int int1, int int2) throws IOException {
        do {
            int int0 = this.input.read(bytes, int1, int2);
            if (int0 < 0) {
                throw new EOFException();
            }

            int1 += int0;
            int2 -= int0;
        } while (int2 > 0);
    }

    private int readInt(byte[] bytes, int int0) {
        return bytes[int0] << 24 | (bytes[int0 + 1] & 0xFF) << 16 | (bytes[int0 + 2] & 0xFF) << 8 | bytes[int0 + 3] & 0xFF;
    }

    private void skip(long long0) throws IOException {
        while (long0 > 0L) {
            long long1 = this.input.skip(long0);
            if (long1 < 0L) {
                throw new EOFException();
            }

            long0 -= long1;
        }
    }

    private boolean checkSignature(byte[] bytes) {
        for (int int0 = 0; int0 < SIGNATURE.length; int0++) {
            if (bytes[int0] != SIGNATURE[int0]) {
                return false;
            }
        }

        return true;
    }
}
