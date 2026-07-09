// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;
import java.util.Collection;

public interface PhysicalOggStream {
    Collection getLogicalStreams();

    OggPage getOggPage(int var1) throws OggFormatException, IOException;

    boolean isOpen();

    void close() throws IOException;

    void setTime(long var1) throws OggFormatException, IOException;

    boolean isSeekable();
}
