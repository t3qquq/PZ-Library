// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import de.jarnbjo.util.io.ByteArrayBitInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import zombie.debug.DebugLog;

public class OggPage {
    private int version;
    private boolean continued;
    private boolean bos;
    private boolean eos;
    private long absoluteGranulePosition;
    private int streamSerialNumber;
    private int pageSequenceNumber;
    private int pageCheckSum;
    private int[] segmentOffsets;
    private int[] segmentLengths;
    private int totalLength;
    private byte[] header;
    private byte[] segmentTable;
    private byte[] data;

    protected OggPage() {
    }

    private OggPage(
        int int0,
        boolean boolean0,
        boolean boolean1,
        boolean boolean2,
        long long0,
        int int1,
        int int2,
        int int3,
        int[] ints0,
        int[] ints1,
        int int4,
        byte[] bytes0,
        byte[] bytes1,
        byte[] bytes2
    ) {
        this.version = int0;
        this.continued = boolean0;
        this.bos = boolean1;
        this.eos = boolean2;
        this.absoluteGranulePosition = long0;
        this.streamSerialNumber = int1;
        this.pageSequenceNumber = int2;
        this.pageCheckSum = int3;
        this.segmentOffsets = ints0;
        this.segmentLengths = ints1;
        this.totalLength = int4;
        this.header = bytes0;
        this.segmentTable = bytes1;
        this.data = bytes2;
    }

    public static OggPage create(RandomAccessFile randomAccessFile) throws IOException, EndOfOggStreamException, OggFormatException {
        return create(randomAccessFile, false);
    }

    public static OggPage create(RandomAccessFile randomAccessFile, boolean boolean0) throws IOException, EndOfOggStreamException, OggFormatException {
        return create((Object)randomAccessFile, boolean0);
    }

    public static OggPage create(InputStream inputStream) throws IOException, EndOfOggStreamException, OggFormatException {
        return create(inputStream, false);
    }

    public static OggPage create(InputStream inputStream, boolean boolean0) throws IOException, EndOfOggStreamException, OggFormatException {
        return create((Object)inputStream, boolean0);
    }

    public static OggPage create(byte[] bytes) throws IOException, EndOfOggStreamException, OggFormatException {
        return create(bytes, false);
    }

    public static OggPage create(byte[] bytes, boolean boolean0) throws IOException, EndOfOggStreamException, OggFormatException {
        return create(bytes, boolean0);
    }

    private static OggPage create(Object object, boolean boolean3) throws IOException, EndOfOggStreamException, OggFormatException {
        try {
            int int0 = 27;
            byte[] bytes0 = new byte[27];
            if (object instanceof RandomAccessFile randomAccessFile) {
                if (randomAccessFile.getFilePointer() == randomAccessFile.length()) {
                    return null;
                }

                randomAccessFile.readFully(bytes0);
            } else if (object instanceof InputStream) {
                readFully((InputStream)object, bytes0);
            } else if (object instanceof byte[]) {
                System.arraycopy((byte[])object, 0, bytes0, 0, 27);
            }

            ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(bytes0);
            int int1 = byteArrayBitInputStream.getInt(32);
            if (int1 != 1399285583) {
                String string = Integer.toHexString(int1);

                while (string.length() < 8) {
                    string = "0" + string;
                }

                string = string.substring(6, 8) + string.substring(4, 6) + string.substring(2, 4) + string.substring(0, 2);
                char char0 = (char)Integer.valueOf(string.substring(0, 2), 16).intValue();
                char char1 = (char)Integer.valueOf(string.substring(2, 4), 16).intValue();
                char char2 = (char)Integer.valueOf(string.substring(4, 6), 16).intValue();
                char char3 = (char)Integer.valueOf(string.substring(6, 8), 16).intValue();
                DebugLog.log("Ogg packet header is 0x" + string + " (" + char0 + char1 + char2 + char3 + "), should be 0x4f676753 (OggS)");
            }

            int int2 = byteArrayBitInputStream.getInt(8);
            byte byte0 = (byte)byteArrayBitInputStream.getInt(8);
            boolean boolean0 = (byte0 & 1) != 0;
            boolean boolean1 = (byte0 & 2) != 0;
            boolean boolean2 = (byte0 & 4) != 0;
            long long0 = byteArrayBitInputStream.getLong(64);
            int int3 = byteArrayBitInputStream.getInt(32);
            int int4 = byteArrayBitInputStream.getInt(32);
            int int5 = byteArrayBitInputStream.getInt(32);
            int int6 = byteArrayBitInputStream.getInt(8);
            int[] ints0 = new int[int6];
            int[] ints1 = new int[int6];
            int int7 = 0;
            byte[] bytes1 = new byte[int6];
            byte[] bytes2 = new byte[1];

            for (int int8 = 0; int8 < int6; int8++) {
                int int9 = 0;
                if (object instanceof RandomAccessFile) {
                    int9 = ((RandomAccessFile)object).readByte() & 255;
                } else if (object instanceof InputStream) {
                    int9 = ((InputStream)object).read();
                } else if (object instanceof byte[]) {
                    byte byte1 = ((byte[])object)[int0++];
                    int9 = byte1 & 255;
                }

                bytes1[int8] = (byte)int9;
                ints1[int8] = int9;
                ints0[int8] = int7;
                int7 += int9;
            }

            byte[] bytes3 = null;
            if (!boolean3) {
                bytes3 = new byte[int7];
                if (object instanceof RandomAccessFile) {
                    ((RandomAccessFile)object).readFully(bytes3);
                } else if (object instanceof InputStream) {
                    readFully((InputStream)object, bytes3);
                } else if (object instanceof byte[]) {
                    System.arraycopy(object, int0, bytes3, 0, int7);
                }
            }

            return new OggPage(int2, boolean0, boolean1, boolean2, long0, int3, int4, int5, ints0, ints1, int7, bytes0, bytes1, bytes3);
        } catch (EOFException eOFException) {
            throw new EndOfOggStreamException();
        }
    }

    private static void readFully(InputStream inputStream, byte[] bytes) throws IOException {
        int int0 = 0;

        while (int0 < bytes.length) {
            int int1 = inputStream.read(bytes, int0, bytes.length - int0);
            if (int1 == -1) {
                throw new EndOfOggStreamException();
            }

            int0 += int1;
        }
    }

    public long getAbsoluteGranulePosition() {
        return this.absoluteGranulePosition;
    }

    public int getStreamSerialNumber() {
        return this.streamSerialNumber;
    }

    public int getPageSequenceNumber() {
        return this.pageSequenceNumber;
    }

    public int getPageCheckSum() {
        return this.pageCheckSum;
    }

    public int getTotalLength() {
        return this.data != null ? 27 + this.segmentTable.length + this.data.length : this.totalLength;
    }

    public byte[] getData() {
        return this.data;
    }

    public byte[] getHeader() {
        return this.header;
    }

    public byte[] getSegmentTable() {
        return this.segmentTable;
    }

    public int[] getSegmentOffsets() {
        return this.segmentOffsets;
    }

    public int[] getSegmentLengths() {
        return this.segmentLengths;
    }

    public boolean isContinued() {
        return this.continued;
    }

    public boolean isFresh() {
        return !this.continued;
    }

    public boolean isBos() {
        return this.bos;
    }

    public boolean isEos() {
        return this.eos;
    }
}
