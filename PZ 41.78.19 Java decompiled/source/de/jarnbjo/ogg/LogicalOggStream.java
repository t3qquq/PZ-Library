// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;

public interface LogicalOggStream {
    String FORMAT_UNKNOWN = "application/octet-stream";
    String FORMAT_VORBIS = "audio/x-vorbis";
    String FORMAT_FLAC = "audio/x-flac";
    String FORMAT_THEORA = "video/x-theora";

    OggPage getNextOggPage() throws OggFormatException, IOException;

    byte[] getNextOggPacket() throws OggFormatException, IOException;

    boolean isOpen();

    void close() throws IOException;

    void reset() throws OggFormatException, IOException;

    long getMaximumGranulePosition();

    void setTime(long var1) throws IOException;

    long getTime();

    String getFormat();
}
