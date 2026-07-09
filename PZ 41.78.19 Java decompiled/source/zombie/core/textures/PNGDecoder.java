// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import zombie.core.utils.BooleanGrid;

public final class PNGDecoder {
    private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static final int IHDR = 1229472850;
    private static final int PLTE = 1347179589;
    private static final int tRNS = 1951551059;
    private static final int IDAT = 1229209940;
    private static final int IEND = 1229278788;
    private static final byte COLOR_GREYSCALE = 0;
    private static final byte COLOR_TRUECOLOR = 2;
    private static final byte COLOR_INDEXED = 3;
    private static final byte COLOR_GREYALPHA = 4;
    private static final byte COLOR_TRUEALPHA = 6;
    private final InputStream input;
    private final CRC32 crc;
    private final byte[] buffer;
    private int chunkLength;
    private int chunkType;
    private int chunkRemaining;
    private int width;
    private int height;
    private int bitdepth;
    private int colorType;
    private int bytesPerPixel;
    private byte[] palette;
    private byte[] paletteA;
    private byte[] transPixel;
    int maskM = 0;
    public int maskID = 0;
    public BooleanGrid mask;
    public boolean bDoMask = false;
    public long readTotal = 0L;

    public PNGDecoder(InputStream inputStream, boolean boolean0) throws IOException {
        this.input = inputStream;
        this.crc = new CRC32();
        this.buffer = new byte[4096];
        this.bDoMask = boolean0;
        this.readFully(this.buffer, 0, SIGNATURE.length);
        if (!checkSignature(this.buffer)) {
            throw new IOException("Not a valid PNG file");
        } else {
            this.openChunk(1229472850);
            this.readIHDR();
            this.closeChunk();

            while (true) {
                this.openChunk();
                switch (this.chunkType) {
                    case 1229209940:
                        if (this.colorType == 3 && this.palette == null) {
                            throw new IOException("Missing PLTE chunk");
                        }

                        if (boolean0) {
                            this.mask = new BooleanGrid(this.width, this.height);
                        }

                        return;
                    case 1347179589:
                        this.readPLTE();
                        break;
                    case 1951551059:
                        this.readtRNS();
                }

                this.closeChunk();
            }
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean hasAlphaChannel() {
        return this.colorType == 6 || this.colorType == 4;
    }

    public boolean hasAlpha() {
        return this.hasAlphaChannel() || this.paletteA != null || this.transPixel != null;
    }

    public boolean isRGB() {
        return this.colorType == 6 || this.colorType == 2 || this.colorType == 3;
    }

    public void overwriteTRNS(byte byte0, byte byte1, byte byte2) {
        if (this.hasAlphaChannel()) {
            throw new UnsupportedOperationException("image has an alpha channel");
        } else {
            byte[] bytes = this.palette;
            if (bytes == null) {
                this.transPixel = new byte[]{0, byte0, 0, byte1, 0, byte2};
            } else {
                this.paletteA = new byte[bytes.length / 3];
                byte byte3 = 0;

                for (int int0 = 0; byte3 < bytes.length; int0++) {
                    if (bytes[byte3] != byte0 || bytes[byte3 + 1] != byte1 || bytes[byte3 + 2] != byte2) {
                        this.paletteA[int0] = -1;
                    }

                    byte3 += 3;
                }
            }
        }
    }

    public PNGDecoder.Format decideTextureFormat(PNGDecoder.Format format) {
        switch (this.colorType) {
            case 0:
                switch (format) {
                    case LUMINANCE:
                    case ALPHA:
                        return format;
                    default:
                        return PNGDecoder.Format.LUMINANCE;
                }
            case 1:
            case 5:
            default:
                throw new UnsupportedOperationException("Not yet implemented");
            case 2:
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA:
                    case RGB:
                        return format;
                    default:
                        return PNGDecoder.Format.RGB;
                }
            case 3:
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA:
                        return format;
                    default:
                        return PNGDecoder.Format.RGBA;
                }
            case 4:
                return PNGDecoder.Format.LUMINANCE_ALPHA;
            case 6:
                switch (format) {
                    case ABGR:
                    case RGBA:
                    case BGRA:
                    case RGB:
                        return format;
                    default:
                        return PNGDecoder.Format.RGBA;
                }
        }
    }

    public void decode(ByteBuffer byteBuffer, int int3, PNGDecoder.Format format) throws IOException {
        int int0 = byteBuffer.position();
        int int1 = (this.width * this.bitdepth + 7) / 8 * this.bytesPerPixel;
        byte[] bytes0 = new byte[int1 + 1];
        byte[] bytes1 = new byte[int1 + 1];
        byte[] bytes2 = this.bitdepth < 8 ? new byte[this.width + 1] : null;
        this.maskM = 0;
        Inflater inflater = new Inflater();

        try {
            for (int int2 = 0; int2 < this.height; int2++) {
                this.readChunkUnzip(inflater, bytes0, 0, bytes0.length);
                this.unfilter(bytes0, bytes1);
                byteBuffer.position(int0 + int2 * int3);
                label81:
                switch (this.colorType) {
                    case 0:
                        switch (format) {
                            case RGBA:
                                this.copyGREYtoRGBA(byteBuffer, bytes0);
                                break label81;
                            case BGRA:
                            case RGB:
                            default:
                                throw new UnsupportedOperationException("Unsupported format for this image");
                            case LUMINANCE:
                            case ALPHA:
                                this.copy(byteBuffer, bytes0);
                                break label81;
                        }
                    case 1:
                    case 5:
                    default:
                        throw new UnsupportedOperationException("Not yet implemented");
                    case 2:
                        switch (format) {
                            case ABGR:
                                this.copyRGBtoABGR(byteBuffer, bytes0);
                                break label81;
                            case RGBA:
                                this.copyRGBtoRGBA(byteBuffer, bytes0);
                                break label81;
                            case BGRA:
                                this.copyRGBtoBGRA(byteBuffer, bytes0);
                                break label81;
                            case RGB:
                                this.copy(byteBuffer, bytes0);
                                break label81;
                            default:
                                throw new UnsupportedOperationException("Unsupported format for this image");
                        }
                    case 3:
                        switch (this.bitdepth) {
                            case 1:
                                this.expand1(bytes0, bytes2);
                                break;
                            case 2:
                                this.expand2(bytes0, bytes2);
                                break;
                            case 3:
                            case 5:
                            case 6:
                            case 7:
                            default:
                                throw new UnsupportedOperationException("Unsupported bitdepth for this image");
                            case 4:
                                this.expand4(bytes0, bytes2);
                                break;
                            case 8:
                                bytes2 = bytes0;
                        }

                        switch (format) {
                            case ABGR:
                                this.copyPALtoABGR(byteBuffer, bytes2);
                                break label81;
                            case RGBA:
                                this.copyPALtoRGBA(byteBuffer, bytes2);
                                break label81;
                            case BGRA:
                                this.copyPALtoBGRA(byteBuffer, bytes2);
                                break label81;
                            default:
                                throw new UnsupportedOperationException("Unsupported format for this image");
                        }
                    case 4:
                        switch (format) {
                            case RGBA:
                                this.copyGREYALPHAtoRGBA(byteBuffer, bytes0);
                                break label81;
                            case LUMINANCE_ALPHA:
                                this.copy(byteBuffer, bytes0);
                                break label81;
                            default:
                                throw new UnsupportedOperationException("Unsupported format for this image");
                        }
                    case 6:
                        switch (format) {
                            case ABGR:
                                this.copyRGBAtoABGR(byteBuffer, bytes0);
                                break;
                            case RGBA:
                                this.copy(byteBuffer, bytes0);
                                break;
                            case BGRA:
                                this.copyRGBAtoBGRA(byteBuffer, bytes0);
                                break;
                            case RGB:
                                this.copyRGBAtoRGB(byteBuffer, bytes0);
                                break;
                            default:
                                throw new UnsupportedOperationException("Unsupported format for this image");
                        }
                }

                byte[] bytes3 = bytes0;
                bytes0 = bytes1;
                bytes1 = bytes3;
            }
        } finally {
            inflater.end();
        }
    }

    public void decodeFlipped(ByteBuffer byteBuffer, int int0, PNGDecoder.Format format) throws IOException {
        if (int0 <= 0) {
            throw new IllegalArgumentException("stride");
        } else {
            int int1 = byteBuffer.position();
            int int2 = (this.height - 1) * int0;
            byteBuffer.position(int1 + int2);
            this.decode(byteBuffer, -int0, format);
            byteBuffer.position(byteBuffer.position() + int2);
        }
    }

    private void copy(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.bDoMask) {
            byte byte0 = 1;

            for (int int0 = bytes.length; byte0 < int0; byte0 += 4) {
                if (bytes[byte0 + 3] % 255 != 0) {
                    this.mask.setValue(this.maskM % this.width, this.maskM / this.width, true);
                }

                this.maskM++;
            }
        }

        byteBuffer.put(bytes, 1, bytes.length - 1);
    }

    private void copyRGBtoABGR(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.transPixel != null) {
            byte byte0 = this.transPixel[1];
            byte byte1 = this.transPixel[3];
            byte byte2 = this.transPixel[5];
            byte byte3 = 1;

            for (int int0 = bytes.length; byte3 < int0; byte3 += 3) {
                byte byte4 = bytes[byte3];
                byte byte5 = bytes[byte3 + 1];
                byte byte6 = bytes[byte3 + 2];
                byte byte7 = -1;
                if (byte4 == byte0 && byte5 == byte1 && byte6 == byte2) {
                    byte7 = 0;
                }

                byteBuffer.put(byte7).put(byte6).put(byte5).put(byte4);
            }
        } else {
            byte byte8 = 1;

            for (int int1 = bytes.length; byte8 < int1; byte8 += 3) {
                byteBuffer.put((byte)-1).put(bytes[byte8 + 2]).put(bytes[byte8 + 1]).put(bytes[byte8]);
            }
        }
    }

    private void copyRGBtoRGBA(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.transPixel != null) {
            byte byte0 = this.transPixel[1];
            byte byte1 = this.transPixel[3];
            byte byte2 = this.transPixel[5];
            byte byte3 = 1;

            for (int int0 = bytes.length; byte3 < int0; byte3 += 3) {
                byte byte4 = bytes[byte3];
                byte byte5 = bytes[byte3 + 1];
                byte byte6 = bytes[byte3 + 2];
                byte byte7 = -1;
                if (byte4 == byte0 && byte5 == byte1 && byte6 == byte2) {
                    byte7 = 0;
                }

                if (this.bDoMask && byte7 == 0) {
                    this.mask.setValue(this.maskID % this.width, this.maskID / this.width, true);
                    this.maskID++;
                }

                byteBuffer.put(byte4).put(byte5).put(byte6).put(byte7);
            }
        } else {
            byte byte8 = 1;

            for (int int1 = bytes.length; byte8 < int1; byte8 += 3) {
                byteBuffer.put(bytes[byte8]).put(bytes[byte8 + 1]).put(bytes[byte8 + 2]).put((byte)-1);
            }
        }
    }

    private void copyRGBtoBGRA(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.transPixel != null) {
            byte byte0 = this.transPixel[1];
            byte byte1 = this.transPixel[3];
            byte byte2 = this.transPixel[5];
            byte byte3 = 1;

            for (int int0 = bytes.length; byte3 < int0; byte3 += 3) {
                byte byte4 = bytes[byte3];
                byte byte5 = bytes[byte3 + 1];
                byte byte6 = bytes[byte3 + 2];
                byte byte7 = -1;
                if (byte4 == byte0 && byte5 == byte1 && byte6 == byte2) {
                    byte7 = 0;
                }

                byteBuffer.put(byte6).put(byte5).put(byte4).put(byte7);
            }
        } else {
            byte byte8 = 1;

            for (int int1 = bytes.length; byte8 < int1; byte8 += 3) {
                byteBuffer.put(bytes[byte8 + 2]).put(bytes[byte8 + 1]).put(bytes[byte8]).put((byte)-1);
            }
        }
    }

    private void copyRGBAtoABGR(ByteBuffer byteBuffer, byte[] bytes) {
        byte byte0 = 1;

        for (int int0 = bytes.length; byte0 < int0; byte0 += 4) {
            byteBuffer.put(bytes[byte0 + 3]).put(bytes[byte0 + 2]).put(bytes[byte0 + 1]).put(bytes[byte0]);
        }
    }

    private void copyRGBAtoBGRA(ByteBuffer byteBuffer, byte[] bytes) {
        byte byte0 = 1;

        for (int int0 = bytes.length; byte0 < int0; byte0 += 4) {
            byteBuffer.put(bytes[byte0 + 2]).put(bytes[byte0 + 1]).put(bytes[byte0]).put(bytes[byte0 + 3]);
        }
    }

    private void copyRGBAtoRGB(ByteBuffer byteBuffer, byte[] bytes) {
        byte byte0 = 1;

        for (int int0 = bytes.length; byte0 < int0; byte0 += 4) {
            byteBuffer.put(bytes[byte0]).put(bytes[byte0 + 1]).put(bytes[byte0 + 2]);
        }
    }

    private void copyPALtoABGR(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.paletteA != null) {
            int int0 = 1;

            for (int int1 = bytes.length; int0 < int1; int0++) {
                int int2 = bytes[int0] & 255;
                byte byte0 = this.palette[int2 * 3 + 0];
                byte byte1 = this.palette[int2 * 3 + 1];
                byte byte2 = this.palette[int2 * 3 + 2];
                byte byte3 = this.paletteA[int2];
                byteBuffer.put(byte3).put(byte2).put(byte1).put(byte0);
            }
        } else {
            int int3 = 1;

            for (int int4 = bytes.length; int3 < int4; int3++) {
                int int5 = bytes[int3] & 255;
                byte byte4 = this.palette[int5 * 3 + 0];
                byte byte5 = this.palette[int5 * 3 + 1];
                byte byte6 = this.palette[int5 * 3 + 2];
                byte byte7 = -1;
                byteBuffer.put(byte7).put(byte6).put(byte5).put(byte4);
            }
        }
    }

    private void copyPALtoRGBA(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.paletteA != null) {
            int int0 = 1;

            for (int int1 = bytes.length; int0 < int1; int0++) {
                int int2 = bytes[int0] & 255;
                byte byte0 = this.palette[int2 * 3 + 0];
                byte byte1 = this.palette[int2 * 3 + 1];
                byte byte2 = this.palette[int2 * 3 + 2];
                byte byte3 = this.paletteA[int2];
                byteBuffer.put(byte0).put(byte1).put(byte2).put(byte3);
            }
        } else {
            int int3 = 1;

            for (int int4 = bytes.length; int3 < int4; int3++) {
                int int5 = bytes[int3] & 255;
                byte byte4 = this.palette[int5 * 3 + 0];
                byte byte5 = this.palette[int5 * 3 + 1];
                byte byte6 = this.palette[int5 * 3 + 2];
                byte byte7 = -1;
                byteBuffer.put(byte4).put(byte5).put(byte6).put(byte7);
            }
        }
    }

    private void copyPALtoBGRA(ByteBuffer byteBuffer, byte[] bytes) {
        if (this.paletteA != null) {
            int int0 = 1;

            for (int int1 = bytes.length; int0 < int1; int0++) {
                int int2 = bytes[int0] & 255;
                byte byte0 = this.palette[int2 * 3 + 0];
                byte byte1 = this.palette[int2 * 3 + 1];
                byte byte2 = this.palette[int2 * 3 + 2];
                byte byte3 = this.paletteA[int2];
                byteBuffer.put(byte2).put(byte1).put(byte0).put(byte3);
            }
        } else {
            int int3 = 1;

            for (int int4 = bytes.length; int3 < int4; int3++) {
                int int5 = bytes[int3] & 255;
                byte byte4 = this.palette[int5 * 3 + 0];
                byte byte5 = this.palette[int5 * 3 + 1];
                byte byte6 = this.palette[int5 * 3 + 2];
                byte byte7 = -1;
                byteBuffer.put(byte6).put(byte5).put(byte4).put(byte7);
            }
        }
    }

    private void copyGREYtoRGBA(ByteBuffer byteBuffer, byte[] bytes) {
        int int0 = 1;

        for (int int1 = bytes.length; int0 < int1; int0++) {
            byte byte0 = bytes[int0];
            byte byte1 = -1;
            byteBuffer.put(byte0).put(byte0).put(byte0).put(byte1);
        }
    }

    private void copyGREYALPHAtoRGBA(ByteBuffer byteBuffer, byte[] bytes) {
        byte byte0 = 1;

        for (int int0 = bytes.length; byte0 < int0; byte0 += 2) {
            byte byte1 = bytes[byte0];
            byte byte2 = bytes[byte0 + 1];
            byteBuffer.put(byte1).put(byte1).put(byte1).put(byte2);
        }
    }

    private void expand4(byte[] bytes1, byte[] bytes0) {
        byte byte0 = 1;

        for (int int0 = bytes0.length; byte0 < int0; byte0 += 2) {
            int int1 = bytes1[1 + (byte0 >> 1)] & 255;
            switch (int0 - byte0) {
                default:
                    bytes0[byte0 + 1] = (byte)(int1 & 15);
                case 1:
                    bytes0[byte0] = (byte)(int1 >> 4);
            }
        }
    }

    private void expand2(byte[] bytes1, byte[] bytes0) {
        byte byte0 = 1;

        for (int int0 = bytes0.length; byte0 < int0; byte0 += 4) {
            int int1 = bytes1[1 + (byte0 >> 2)] & 255;
            switch (int0 - byte0) {
                default:
                    bytes0[byte0 + 3] = (byte)(int1 & 3);
                case 3:
                    bytes0[byte0 + 2] = (byte)(int1 >> 2 & 3);
                case 2:
                    bytes0[byte0 + 1] = (byte)(int1 >> 4 & 3);
                case 1:
                    bytes0[byte0] = (byte)(int1 >> 6);
            }
        }
    }

    private void expand1(byte[] bytes1, byte[] bytes0) {
        byte byte0 = 1;

        for (int int0 = bytes0.length; byte0 < int0; byte0 += 8) {
            int int1 = bytes1[1 + (byte0 >> 3)] & 255;
            switch (int0 - byte0) {
                default:
                    bytes0[byte0 + 7] = (byte)(int1 & 1);
                case 7:
                    bytes0[byte0 + 6] = (byte)(int1 >> 1 & 1);
                case 6:
                    bytes0[byte0 + 5] = (byte)(int1 >> 2 & 1);
                case 5:
                    bytes0[byte0 + 4] = (byte)(int1 >> 3 & 1);
                case 4:
                    bytes0[byte0 + 3] = (byte)(int1 >> 4 & 1);
                case 3:
                    bytes0[byte0 + 2] = (byte)(int1 >> 5 & 1);
                case 2:
                    bytes0[byte0 + 1] = (byte)(int1 >> 6 & 1);
                case 1:
                    bytes0[byte0] = (byte)(int1 >> 7);
            }
        }
    }

    private void unfilter(byte[] bytes0, byte[] bytes1) throws IOException {
        switch (bytes0[0]) {
            case 0:
                break;
            case 1:
                this.unfilterSub(bytes0);
                break;
            case 2:
                this.unfilterUp(bytes0, bytes1);
                break;
            case 3:
                this.unfilterAverage(bytes0, bytes1);
                break;
            case 4:
                this.unfilterPaeth(bytes0, bytes1);
                break;
            default:
                throw new IOException("invalide filter type in scanline: " + bytes0[0]);
        }
    }

    private void unfilterSub(byte[] bytes) {
        int int0 = this.bytesPerPixel;
        int int1 = int0 + 1;

        for (int int2 = bytes.length; int1 < int2; int1++) {
            bytes[int1] += bytes[int1 - int0];
        }
    }

    private void unfilterUp(byte[] bytes0, byte[] bytes1) {
        int int0 = this.bytesPerPixel;
        int int1 = 1;

        for (int int2 = bytes0.length; int1 < int2; int1++) {
            bytes0[int1] += bytes1[int1];
        }
    }

    private void unfilterAverage(byte[] bytes1, byte[] bytes0) {
        int int0 = this.bytesPerPixel;

        int int1;
        for (int1 = 1; int1 <= int0; int1++) {
            bytes1[int1] += (byte)((bytes0[int1] & 255) >>> 1);
        }

        for (int int2 = bytes1.length; int1 < int2; int1++) {
            bytes1[int1] += (byte)((bytes0[int1] & 255) + (bytes1[int1 - int0] & 255) >>> 1);
        }
    }

    private void unfilterPaeth(byte[] bytes1, byte[] bytes0) {
        int int0 = this.bytesPerPixel;

        int int1;
        for (int1 = 1; int1 <= int0; int1++) {
            bytes1[int1] += bytes0[int1];
        }

        for (int int2 = bytes1.length; int1 < int2; int1++) {
            int int3 = bytes1[int1 - int0] & 255;
            int int4 = bytes0[int1] & 255;
            int int5 = bytes0[int1 - int0] & 255;
            int int6 = int3 + int4 - int5;
            int int7 = int6 - int3;
            if (int7 < 0) {
                int7 = -int7;
            }

            int int8 = int6 - int4;
            if (int8 < 0) {
                int8 = -int8;
            }

            int int9 = int6 - int5;
            if (int9 < 0) {
                int9 = -int9;
            }

            if (int7 <= int8 && int7 <= int9) {
                int5 = int3;
            } else if (int8 <= int9) {
                int5 = int4;
            }

            bytes1[int1] += (byte)int5;
        }
    }

    private void readIHDR() throws IOException {
        this.checkChunkLength(13);
        this.readChunk(this.buffer, 0, 13);
        this.width = this.readInt(this.buffer, 0);
        this.height = this.readInt(this.buffer, 4);
        this.bitdepth = this.buffer[8] & 255;
        this.colorType = this.buffer[9] & 255;
        label43:
        switch (this.colorType) {
            case 0:
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }

                this.bytesPerPixel = 1;
                break;
            case 1:
            case 5:
            default:
                throw new IOException("unsupported color format: " + this.colorType);
            case 2:
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }

                this.bytesPerPixel = 3;
                break;
            case 3:
                switch (this.bitdepth) {
                    case 1:
                    case 2:
                    case 4:
                    case 8:
                        this.bytesPerPixel = 1;
                        break label43;
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    default:
                        throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }
            case 4:
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }

                this.bytesPerPixel = 2;
                break;
            case 6:
                if (this.bitdepth != 8) {
                    throw new IOException("Unsupported bit depth: " + this.bitdepth);
                }

                this.bytesPerPixel = 4;
        }

        if (this.buffer[10] != 0) {
            throw new IOException("unsupported compression method");
        } else if (this.buffer[11] != 0) {
            throw new IOException("unsupported filtering method");
        } else if (this.buffer[12] != 0) {
            throw new IOException("unsupported interlace method");
        }
    }

    private void readPLTE() throws IOException {
        int int0 = this.chunkLength / 3;
        if (int0 >= 1 && int0 <= 256 && this.chunkLength % 3 == 0) {
            this.palette = new byte[int0 * 3];
            this.readChunk(this.palette, 0, this.palette.length);
        } else {
            throw new IOException("PLTE chunk has wrong length");
        }
    }

    private void readtRNS() throws IOException {
        switch (this.colorType) {
            case 0:
                this.checkChunkLength(2);
                this.transPixel = new byte[2];
                this.readChunk(this.transPixel, 0, 2);
            case 1:
            default:
                break;
            case 2:
                this.checkChunkLength(6);
                this.transPixel = new byte[6];
                this.readChunk(this.transPixel, 0, 6);
                break;
            case 3:
                if (this.palette == null) {
                    throw new IOException("tRNS chunk without PLTE chunk");
                }

                this.paletteA = new byte[this.palette.length / 3];
                Arrays.fill(this.paletteA, (byte)-1);
                this.readChunk(this.paletteA, 0, this.paletteA.length);
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

    private void refillInflater(Inflater inflater) throws IOException {
        while (this.chunkRemaining == 0) {
            this.closeChunk();
            this.openChunk(1229209940);
        }

        int int0 = this.readChunk(this.buffer, 0, this.buffer.length);
        inflater.setInput(this.buffer, 0, int0);
    }

    private void readChunkUnzip(Inflater inflater, byte[] bytes, int int1, int int2) throws IOException {
        assert bytes != this.buffer;

        try {
            do {
                int int0 = inflater.inflate(bytes, int1, int2);
                if (int0 <= 0) {
                    if (inflater.finished()) {
                        throw new EOFException();
                    }

                    if (!inflater.needsInput()) {
                        throw new IOException("Can't inflate " + int2 + " bytes");
                    }

                    this.refillInflater(inflater);
                } else {
                    int1 += int0;
                    int2 -= int0;
                }
            } while (int2 > 0);
        } catch (DataFormatException dataFormatException) {
            throw (IOException)new IOException("inflate error").initCause(dataFormatException);
        }
    }

    private void readFully(byte[] bytes, int int1, int int2) throws IOException {
        do {
            int int0 = this.input.read(bytes, int1, int2);
            if (int0 < 0) {
                throw new EOFException();
            }

            int1 += int0;
            int2 -= int0;
            this.readTotal += int0;
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

    private static boolean checkSignature(byte[] bytes) {
        for (int int0 = 0; int0 < SIGNATURE.length; int0++) {
            if (bytes[int0] != SIGNATURE[int0]) {
                return false;
            }
        }

        return true;
    }

    public static enum Format {
        ALPHA(1, true),
        LUMINANCE(1, false),
        LUMINANCE_ALPHA(2, true),
        RGB(3, false),
        RGBA(4, true),
        BGRA(4, true),
        ABGR(4, true);

        final int numComponents;
        final boolean hasAlpha;

        private Format(int int1, boolean boolean0) {
            this.numComponents = int1;
            this.hasAlpha = boolean0;
        }

        public int getNumComponents() {
            return this.numComponents;
        }

        public boolean isHasAlpha() {
            return this.hasAlpha;
        }
    }
}
