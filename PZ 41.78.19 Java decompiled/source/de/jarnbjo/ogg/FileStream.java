// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FileStream implements PhysicalOggStream {
    private boolean closed = false;
    private RandomAccessFile source;
    private long[] pageOffsets;
    private long numberOfSamples = -1L;
    private HashMap logicalStreams = new HashMap();

    public FileStream(RandomAccessFile randomAccessFile) throws OggFormatException, IOException {
        this.source = randomAccessFile;
        ArrayList arrayList = new ArrayList();
        int int0 = 0;

        try {
            while (true) {
                arrayList.add(new Long(this.source.getFilePointer()));
                OggPage oggPage = this.getNextPage(int0 > 0);
                if (oggPage == null) {
                    break;
                }

                LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)this.getLogicalStream(oggPage.getStreamSerialNumber());
                if (logicalOggStreamImpl == null) {
                    logicalOggStreamImpl = new LogicalOggStreamImpl(this, oggPage.getStreamSerialNumber());
                    this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
                }

                if (int0 == 0) {
                    logicalOggStreamImpl.checkFormat(oggPage);
                }

                logicalOggStreamImpl.addPageNumberMapping(int0);
                logicalOggStreamImpl.addGranulePosition(oggPage.getAbsoluteGranulePosition());
                if (int0 > 0) {
                    this.source.seek(this.source.getFilePointer() + oggPage.getTotalLength());
                }

                int0++;
            }
        } catch (EndOfOggStreamException endOfOggStreamException) {
        } catch (IOException iOException) {
            throw iOException;
        }

        this.source.seek(0L);
        this.pageOffsets = new long[arrayList.size()];
        int int1 = 0;
        Iterator iterator = arrayList.iterator();

        while (iterator.hasNext()) {
            this.pageOffsets[int1++] = (Long)iterator.next();
        }
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
        this.source.close();
    }

    private OggPage getNextPage() throws EndOfOggStreamException, IOException, OggFormatException {
        return this.getNextPage(false);
    }

    private OggPage getNextPage(boolean boolean0) throws EndOfOggStreamException, IOException, OggFormatException {
        return OggPage.create(this.source, boolean0);
    }

    @Override
    public OggPage getOggPage(int int0) throws IOException {
        this.source.seek(this.pageOffsets[int0]);
        return OggPage.create(this.source);
    }

    private LogicalOggStream getLogicalStream(int int0) {
        return (LogicalOggStream)this.logicalStreams.get(new Integer(int0));
    }

    @Override
    public void setTime(long long0) throws IOException {
        for (LogicalOggStream logicalOggStream : this.logicalStreams.values()) {
            logicalOggStream.setTime(long0);
        }
    }

    @Override
    public boolean isSeekable() {
        return true;
    }
}
