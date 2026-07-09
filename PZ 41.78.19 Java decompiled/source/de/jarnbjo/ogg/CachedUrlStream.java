// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CachedUrlStream implements PhysicalOggStream {
    private boolean closed = false;
    private URLConnection source;
    private InputStream sourceStream;
    private Object drainLock = new Object();
    private RandomAccessFile drain;
    private byte[] memoryCache;
    private ArrayList pageOffsets = new ArrayList();
    private ArrayList pageLengths = new ArrayList();
    private long numberOfSamples = -1L;
    private long cacheLength;
    private HashMap logicalStreams = new HashMap();
    private CachedUrlStream.LoaderThread loaderThread;

    public CachedUrlStream(URL url) throws OggFormatException, IOException {
        this(url, null);
    }

    public CachedUrlStream(URL url, RandomAccessFile randomAccessFile) throws OggFormatException, IOException {
        this.source = url.openConnection();
        if (randomAccessFile == null) {
            int int0 = this.source.getContentLength();
            if (int0 == -1) {
                throw new IOException("The URLConncetion's content length must be set when operating with a in-memory cache.");
            }

            this.memoryCache = new byte[int0];
        }

        this.drain = randomAccessFile;
        this.sourceStream = this.source.getInputStream();
        this.loaderThread = new CachedUrlStream.LoaderThread(this.sourceStream, randomAccessFile, this.memoryCache);
        new Thread(this.loaderThread).start();

        while (!this.loaderThread.isBosDone() || this.pageOffsets.size() < 20) {
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

    public long getCacheLength() {
        return this.cacheLength;
    }

    @Override
    public OggPage getOggPage(int int0) throws IOException {
        synchronized (this.drainLock) {
            Long long0 = (Long)this.pageOffsets.get(int0);
            Long long1 = (Long)this.pageLengths.get(int0);
            if (long0 != null) {
                if (this.drain != null) {
                    this.drain.seek(long0);
                    return OggPage.create(this.drain);
                } else {
                    byte[] bytes = new byte[long1.intValue()];
                    System.arraycopy(this.memoryCache, long0.intValue(), bytes, 0, long1.intValue());
                    return OggPage.create(bytes);
                }
            } else {
                return null;
            }
        }
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

    public class LoaderThread implements Runnable {
        private InputStream source;
        private RandomAccessFile drain;
        private byte[] memoryCache;
        private boolean bosDone = false;
        private int pageNumber;

        public LoaderThread(InputStream inputStream, RandomAccessFile randomAccessFile, byte[] bytes) {
            this.source = inputStream;
            this.drain = randomAccessFile;
            this.memoryCache = bytes;
        }

        @Override
        public void run() {
            try {
                boolean boolean0 = false;
                byte[] bytes0 = new byte[8192];

                while (!boolean0) {
                    OggPage oggPage = OggPage.create(this.source);
                    synchronized (CachedUrlStream.this.drainLock) {
                        int int0 = CachedUrlStream.this.pageOffsets.size();
                        long long0 = int0 > 0
                            ? (Long)CachedUrlStream.this.pageOffsets.get(int0 - 1) + (Long)CachedUrlStream.this.pageLengths.get(int0 - 1)
                            : 0L;
                        byte[] bytes1 = oggPage.getHeader();
                        byte[] bytes2 = oggPage.getSegmentTable();
                        byte[] bytes3 = oggPage.getData();
                        if (this.drain != null) {
                            this.drain.seek(long0);
                            this.drain.write(bytes1);
                            this.drain.write(bytes2);
                            this.drain.write(bytes3);
                        } else {
                            System.arraycopy(bytes1, 0, this.memoryCache, (int)long0, bytes1.length);
                            System.arraycopy(bytes2, 0, this.memoryCache, (int)long0 + bytes1.length, bytes2.length);
                            System.arraycopy(bytes3, 0, this.memoryCache, (int)long0 + bytes1.length + bytes2.length, bytes3.length);
                        }

                        CachedUrlStream.this.pageOffsets.add(new Long(long0));
                        CachedUrlStream.this.pageLengths.add(new Long(bytes1.length + bytes2.length + bytes3.length));
                    }

                    if (!oggPage.isBos()) {
                        this.bosDone = true;
                    }

                    if (oggPage.isEos()) {
                        boolean0 = true;
                    }

                    LogicalOggStreamImpl logicalOggStreamImpl = (LogicalOggStreamImpl)CachedUrlStream.this.getLogicalStream(oggPage.getStreamSerialNumber());
                    if (logicalOggStreamImpl == null) {
                        logicalOggStreamImpl = new LogicalOggStreamImpl(CachedUrlStream.this, oggPage.getStreamSerialNumber());
                        CachedUrlStream.this.logicalStreams.put(new Integer(oggPage.getStreamSerialNumber()), logicalOggStreamImpl);
                        logicalOggStreamImpl.checkFormat(oggPage);
                    }

                    logicalOggStreamImpl.addPageNumberMapping(this.pageNumber);
                    logicalOggStreamImpl.addGranulePosition(oggPage.getAbsoluteGranulePosition());
                    this.pageNumber++;
                    CachedUrlStream.this.cacheLength = oggPage.getAbsoluteGranulePosition();
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
