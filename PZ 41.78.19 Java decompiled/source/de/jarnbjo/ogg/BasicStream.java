// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class BasicStream implements PhysicalOggStream {
    private boolean closed = false;
    private InputStream sourceStream;
    private Object drainLock = new Object();
    private LinkedList pageCache = new LinkedList();
    private long numberOfSamples = -1L;
    private int position = 0;
    private HashMap logicalStreams = new HashMap();
    private OggPage firstPage;
    int pageNumber = 2;

    public BasicStream(InputStream inputStream) throws OggFormatException, IOException {
        this.firstPage = OggPage.create(inputStream);
        this.position = this.position + this.firstPage.getTotalLength();
        LogicalOggStreamImpl logicalOggStreamImpl = new LogicalOggStreamImpl(this, this.firstPage.getStreamSerialNumber());
        this.logicalStreams.put(new Integer(this.firstPage.getStreamSerialNumber()), logicalOggStreamImpl);
        logicalOggStreamImpl.checkFormat(this.firstPage);
    }

    @Override
    public Collection getLogicalStreams() {
        return this.logicalStreams.values();
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.sourceStream.close();
    }

    public int getContentLength() {
        return -1;
    }

    public int getPosition() {
        return this.position;
    }

    @Override
    public OggPage getOggPage(int var1) throws IOException {
        if (this.firstPage != null) {
            OggPage oggPage0 = this.firstPage;
            this.firstPage = null;
            return oggPage0;
        } else {
            OggPage oggPage1 = OggPage.create(this.sourceStream);
            this.position = this.position + oggPage1.getTotalLength();
            return oggPage1;
        }
    }

    private LogicalOggStream getLogicalStream(int int0) {
        return (LogicalOggStream)this.logicalStreams.get(new Integer(int0));
    }

    @Override
    public void setTime(long var1) throws IOException {
        throw new UnsupportedOperationException("Method not supported by this class");
    }

    @Override
    public boolean isSeekable() {
        return false;
    }
}
