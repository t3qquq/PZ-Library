// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class UncachedUrlStream implements PhysicalOggStream {
    private boolean closed = false;
    private URLConnection source;
    private InputStream sourceStream;
    private Object drainLock = new Object();
    private LinkedList pageCache = new LinkedList();
    private long numberOfSamples = -1L;
    private HashMap logicalStreams = new HashMap();
    private UncachedUrlStream.LoaderThread loaderThread;
    private static final int PAGECACHE_SIZE = 10;

    public UncachedUrlStream(URL url) throws OggFormatException, IOException {
        this.source = url.openConnection();
        this.sourceStream = this.source.getInputStream();
        this.loaderThread = new UncachedUrlStream.LoaderThread(this.sourceStream, this.pageCache);
        new Thread(this.loaderThread).start();

        while (!this.loaderThread.isBosDone() || this.pageCache.size() < 10) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException interruptedException) {
            }
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
        this.sourceStream.close();
    }

    @Override
    public OggPage getOggPage(int var1) throws IOException {
        while (this.pageCache.size() == 0) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException interruptedException) {
            }
        }

        synchronized (this.drainLock) {
            return (OggPage)this.pageCache.removeFirst();
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

    public class LoaderThread implements Runnable {
        private InputStream source;
        private LinkedList pageCache;
        private RandomAccessFile drain;
        private byte[] memoryCache;
        private boolean bosDone = false;
        private int pageNumber;

        public LoaderThread(InputStream inputStream, LinkedList linkedList) {
            this.source = inputStream;
            this.pageCache = linkedList;
        }

        @Override
        public void run() {
            try {
                boolean boolean0 = false;
                byte[] bytes = new byte[8192];

                while (!boolean0) {
                    OggPage oggPage = OggPage.create(this.source);
                    synchronized (UncachedUrlStream.this.drainLock) {
                        this.pageCache.add(oggPage);
                    }

                    if (!oggPage.isBos()) {
                        this.bosDone = true;
                    }

                    if (oggPage.isEos()) {
                        boolean0 = true;
                    }

                    LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)UncachedUrlStream.this.getLogicalStream(oggPage.getStreamSerialNumber());
                    if (logicalOggStreamImpl == null) {
                        logicalOggStreamImpl = new LogicalOggStreamImpl(UncachedUrlStream.this, oggPage.getStreamSerialNumber());
                        UncachedUrlStream.this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
                        logicalOggStreamImpl.checkFormat(oggPage);
                    }

                    this.pageNumber++;

                    while (this.pageCache.size() > 10) {
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException interruptedException) {
                        }
                    }
                }
            } catch (EndOfOggStreamException endOfOggStreamException) {
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        public boolean isBosDone() {
            return this.bosDone;
        }
    }
}
