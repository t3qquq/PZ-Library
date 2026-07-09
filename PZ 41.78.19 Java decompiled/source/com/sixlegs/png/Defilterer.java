// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class Defilterer {
    private final InputStream in;
    private final int width;
    private final int bitDepth;
    private final int samples;
    private final PixelProcessor pp;
    private final int bpp;
    private final int[] row;
    private static int[][] bandOffsets = new int[][]{null, {0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}};

    public Defilterer(InputStream inputStream, int int0, int int1, int int2, PixelProcessor pixelProcessor) {
        this.in = inputStream;
        this.bitDepth = int0;
        this.samples = int1;
        this.width = int2;
        this.pp = pixelProcessor;
        this.bpp = Math.max(1, int0 * int1 >> 3);
        this.row = new int[int1 * int2];
    }

    public boolean defilter(int int10, int int6, int int11, int int12, int int1, int int0) throws IOException {
        if (int1 != 0 && int0 != 0) {
            int int2 = (this.bitDepth * this.samples * int1 + 7) / 8;
            boolean boolean0 = this.bitDepth == 16;
            WritableRaster writableRaster = createInputRaster(this.bitDepth, this.samples, this.width);
            DataBuffer dataBuffer = writableRaster.getDataBuffer();
            byte[] bytes0 = boolean0 ? null : ((DataBufferByte)dataBuffer).getData();
            short[] shorts = boolean0 ? ((DataBufferUShort)dataBuffer).getData() : null;
            int int3 = int2 + this.bpp;
            byte[] bytes1 = new byte[int3];
            byte[] bytes2 = new byte[int3];
            int int4 = 0;
            int int5 = int6;

            while (int4 < int0) {
                int int7 = this.in.read();
                if (int7 == -1) {
                    throw new EOFException("Unexpected end of image data");
                }

                readFully(this.in, bytes2, this.bpp, int2);
                defilter(bytes2, bytes1, this.bpp, int7);
                if (boolean0) {
                    int int8 = 0;

                    for (int int9 = this.bpp; int9 < int3; int9 += 2) {
                        shorts[int8] = (short)(bytes2[int9] << 8 | 255 & bytes2[int9 + 1]);
                        int8++;
                    }
                } else {
                    System.arraycopy(bytes2, this.bpp, bytes0, 0, int2);
                }

                writableRaster.getPixels(0, 0, int1, 1, this.row);
                if (!this.pp.process(this.row, int10, int11, int12, int5, int1)) {
                    return false;
                }

                byte[] bytes3 = bytes2;
                bytes2 = bytes1;
                bytes1 = bytes3;
                int4++;
                int5 += int12;
            }

            return true;
        } else {
            return true;
        }
    }

    private static void defilter(byte[] bytes0, byte[] bytes1, int int3, int int1) throws PngException {
        int int0 = bytes0.length;
        switch (int1) {
            case 0:
                break;
            case 1:
                int int16 = int3;

                for (int int17 = 0; int16 < int0; int17++) {
                    bytes0[int16] += bytes0[int17];
                    int16++;
                }
                break;
            case 2:
                for (int int15 = int3; int15 < int0; int15++) {
                    bytes0[int15] += bytes1[int15];
                }
                break;
            case 3:
                int int13 = int3;

                for (int int14 = 0; int13 < int0; int14++) {
                    bytes0[int13] = (byte)(bytes0[int13] + ((255 & bytes0[int14]) + (255 & bytes1[int13])) / 2);
                    int13++;
                }
                break;
            case 4:
                int int2 = int3;

                for (int int4 = 0; int2 < int0; int4++) {
                    byte byte0 = bytes0[int4];
                    byte byte1 = bytes1[int2];
                    byte byte2 = bytes1[int4];
                    int int5 = 255 & byte0;
                    int int6 = 255 & byte1;
                    int int7 = 255 & byte2;
                    int int8 = int5 + int6 - int7;
                    int int9 = int8 - int5;
                    if (int9 < 0) {
                        int9 = -int9;
                    }

                    int int10 = int8 - int6;
                    if (int10 < 0) {
                        int10 = -int10;
                    }

                    int int11 = int8 - int7;
                    if (int11 < 0) {
                        int11 = -int11;
                    }

                    int int12;
                    if (int9 <= int10 && int9 <= int11) {
                        int12 = int5;
                    } else if (int10 <= int11) {
                        int12 = int6;
                    } else {
                        int12 = int7;
                    }

                    bytes0[int2] = (byte)(bytes0[int2] + int12);
                    int2++;
                }
                break;
            default:
                throw new PngException("Unrecognized filter type " + int1, true);
        }
    }

    private static WritableRaster createInputRaster(int int2, int int3, int int1) {
        int int0 = (int2 * int3 * int1 + 7) / 8;
        Point point = new Point(0, 0);
        if (int2 < 8 && int3 == 1) {
            DataBufferByte dataBufferByte0 = new DataBufferByte(int0);
            return Raster.createPackedRaster(dataBufferByte0, int1, 1, int2, point);
        } else if (int2 <= 8) {
            DataBufferByte dataBufferByte1 = new DataBufferByte(int0);
            return Raster.createInterleavedRaster(dataBufferByte1, int1, 1, int0, int3, bandOffsets[int3], point);
        } else {
            DataBufferUShort dataBufferUShort = new DataBufferUShort(int0 / 2);
            return Raster.createInterleavedRaster(dataBufferUShort, int1, 1, int0 / 2, int3, bandOffsets[int3], point);
        }
    }

    private static void readFully(InputStream inputStream, byte[] bytes, int int3, int int1) throws IOException {
        int int0 = 0;

        while (int0 < int1) {
            int int2 = inputStream.read(bytes, int3 + int0, int1 - int0);
            if (int2 == -1) {
                throw new EOFException("Unexpected end of image data");
            }

            int0 += int2;
        }
    }
}
