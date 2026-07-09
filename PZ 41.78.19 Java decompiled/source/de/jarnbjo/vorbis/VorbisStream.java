// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.ogg.LogicalOggStream;
import de.jarnbjo.util.io.ByteArrayBitInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class VorbisStream {
    private LogicalOggStream oggStream;
    private IdentificationHeader identificationHeader;
    private CommentHeader commentHeader;
    private SetupHeader setupHeader;
    private AudioPacket lastAudioPacket;
    private AudioPacket nextAudioPacket;
    private LinkedList audioPackets = new LinkedList();
    private byte[] currentPcm;
    private int currentPcmIndex;
    private int currentPcmLimit;
    private static final int IDENTIFICATION_HEADER = 1;
    private static final int COMMENT_HEADER = 3;
    private static final int SETUP_HEADER = 5;
    private int bitIndex = 0;
    private byte lastByte = 0;
    private boolean initialized = false;
    private Object streamLock = new Object();
    private int pageCounter = 0;
    private int currentBitRate = 0;
    private long currentGranulePosition;
    public static final int BIG_ENDIAN = 0;
    public static final int LITTLE_ENDIAN = 1;

    public VorbisStream() {
    }

    public VorbisStream(LogicalOggStream logicalOggStream) throws VorbisFormatException, IOException {
        this.oggStream = logicalOggStream;

        for (int int0 = 0; int0 < 3; int0++) {
            ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(logicalOggStream.getNextOggPacket());
            int int1 = byteArrayBitInputStream.getInt(8);
            switch (int1) {
                case 1:
                    this.identificationHeader = new IdentificationHeader(byteArrayBitInputStream);
                case 2:
                case 4:
                default:
                    break;
                case 3:
                    this.commentHeader = new CommentHeader(byteArrayBitInputStream);
                    break;
                case 5:
                    this.setupHeader = new SetupHeader(this, byteArrayBitInputStream);
            }
        }

        if (this.identificationHeader == null) {
            throw new VorbisFormatException("The file has no identification header.");
        } else if (this.commentHeader == null) {
            throw new VorbisFormatException("The file has no commentHeader.");
        } else if (this.setupHeader == null) {
            throw new VorbisFormatException("The file has no setup header.");
        } else {
            this.currentPcm = new byte[this.identificationHeader.getChannels() * this.identificationHeader.getBlockSize1() * 2];
        }
    }

    public IdentificationHeader getIdentificationHeader() {
        return this.identificationHeader;
    }

    public CommentHeader getCommentHeader() {
        return this.commentHeader;
    }

    protected SetupHeader getSetupHeader() {
        return this.setupHeader;
    }

    public boolean isOpen() {
        return this.oggStream.isOpen();
    }

    public void close() throws IOException {
        this.oggStream.close();
    }

    public int readPcm(byte[] bytes, int int5, int int4) throws IOException {
        synchronized (this.streamLock) {
            int int0 = this.identificationHeader.getChannels();
            if (this.lastAudioPacket == null) {
                this.lastAudioPacket = this.getNextAudioPacket();
            }

            if (this.currentPcm == null || this.currentPcmIndex >= this.currentPcmLimit) {
                AudioPacket audioPacket = this.getNextAudioPacket();

                try {
                    audioPacket.getPcm(this.lastAudioPacket, this.currentPcm);
                    this.currentPcmLimit = audioPacket.getNumberOfSamples() * this.identificationHeader.getChannels() * 2;
                } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    return 0;
                }

                this.currentPcmIndex = 0;
                this.lastAudioPacket = audioPacket;
            }

            int int1 = 0;
            int int2 = 0;
            int int3 = 0;

            for (int2 = this.currentPcmIndex; int2 < this.currentPcmLimit && int3 < int4; int2++) {
                bytes[int5 + int3++] = this.currentPcm[int2];
                int1++;
            }

            this.currentPcmIndex = int2;
            return int1;
        }
    }

    private AudioPacket getNextAudioPacket() throws VorbisFormatException, IOException {
        this.pageCounter++;
        byte[] bytes = this.oggStream.getNextOggPacket();
        AudioPacket audioPacket = null;

        while (audioPacket == null) {
            try {
                audioPacket = new AudioPacket(this, new ByteArrayBitInputStream(bytes));
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            }
        }

        this.currentGranulePosition = this.currentGranulePosition + audioPacket.getNumberOfSamples();
        this.currentBitRate = bytes.length * 8 * this.identificationHeader.getSampleRate() / audioPacket.getNumberOfSamples();
        return audioPacket;
    }

    public long getCurrentGranulePosition() {
        return this.currentGranulePosition;
    }

    public int getCurrentBitRate() {
        return this.currentBitRate;
    }

    public byte[] processPacket(byte[] bytes0) throws VorbisFormatException, IOException {
        if (bytes0.length == 0) {
            throw new VorbisFormatException("Cannot decode a vorbis packet with length = 0");
        } else if ((bytes0[0] & 1) == 1) {
            ByteArrayBitInputStream byteArrayBitInputStream = new ByteArrayBitInputStream(bytes0);
            switch (byteArrayBitInputStream.getInt(8)) {
                case 1:
                    this.identificationHeader = new IdentificationHeader(byteArrayBitInputStream);
                case 2:
                case 4:
                default:
                    break;
                case 3:
                    this.commentHeader = new CommentHeader(byteArrayBitInputStream);
                    break;
                case 5:
                    this.setupHeader = new SetupHeader(this, byteArrayBitInputStream);
            }

            return null;
        } else if (this.identificationHeader != null && this.commentHeader != null && this.setupHeader != null) {
            AudioPacket audioPacket = new AudioPacket(this, new ByteArrayBitInputStream(bytes0));
            this.currentGranulePosition = this.currentGranulePosition + audioPacket.getNumberOfSamples();
            if (this.lastAudioPacket == null) {
                this.lastAudioPacket = audioPacket;
                return null;
            } else {
                byte[] bytes1 = new byte[this.identificationHeader.getChannels() * audioPacket.getNumberOfSamples() * 2];

                try {
                    audioPacket.getPcm(this.lastAudioPacket, bytes1);
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    Arrays.fill(bytes1, (byte)0);
                }

                this.lastAudioPacket = audioPacket;
                return bytes1;
            }
        } else {
            throw new VorbisFormatException("Cannot decode audio packet before all three header packets have been decoded.");
        }
    }
}
