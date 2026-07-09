// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.io;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;

public final class BitHeader {
    private static final ConcurrentLinkedDeque<BitHeader.BitHeaderByte> pool_byte = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<BitHeader.BitHeaderShort> pool_short = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<BitHeader.BitHeaderInt> pool_int = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<BitHeader.BitHeaderLong> pool_long = new ConcurrentLinkedDeque<>();
    public static boolean DEBUG = true;

    private static BitHeader.BitHeaderBase getHeader(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean0) {
        if (headerSize == BitHeader.HeaderSize.Byte) {
            BitHeader.BitHeaderByte bitHeaderByte = pool_byte.poll();
            if (bitHeaderByte == null) {
                bitHeaderByte = new BitHeader.BitHeaderByte();
            }

            bitHeaderByte.setBuffer(byteBuffer);
            bitHeaderByte.setWrite(boolean0);
            return bitHeaderByte;
        } else if (headerSize == BitHeader.HeaderSize.Short) {
            BitHeader.BitHeaderShort bitHeaderShort = pool_short.poll();
            if (bitHeaderShort == null) {
                bitHeaderShort = new BitHeader.BitHeaderShort();
            }

            bitHeaderShort.setBuffer(byteBuffer);
            bitHeaderShort.setWrite(boolean0);
            return bitHeaderShort;
        } else if (headerSize == BitHeader.HeaderSize.Integer) {
            BitHeader.BitHeaderInt bitHeaderInt = pool_int.poll();
            if (bitHeaderInt == null) {
                bitHeaderInt = new BitHeader.BitHeaderInt();
            }

            bitHeaderInt.setBuffer(byteBuffer);
            bitHeaderInt.setWrite(boolean0);
            return bitHeaderInt;
        } else if (headerSize == BitHeader.HeaderSize.Long) {
            BitHeader.BitHeaderLong bitHeaderLong = pool_long.poll();
            if (bitHeaderLong == null) {
                bitHeaderLong = new BitHeader.BitHeaderLong();
            }

            bitHeaderLong.setBuffer(byteBuffer);
            bitHeaderLong.setWrite(boolean0);
            return bitHeaderLong;
        } else {
            return null;
        }
    }

    private BitHeader() {
    }

    public static void debug_print() {
        if (DEBUG) {
            DebugLog.log("*********************************************");
            DebugLog.log("ByteHeader = " + pool_byte.size());
            DebugLog.log("ShortHeader = " + pool_short.size());
            DebugLog.log("IntHeader = " + pool_int.size());
            DebugLog.log("LongHeader = " + pool_long.size());
        }
    }

    public static BitHeaderWrite allocWrite(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer) {
        return allocWrite(headerSize, byteBuffer, false);
    }

    public static BitHeaderWrite allocWrite(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean0) {
        BitHeader.BitHeaderBase bitHeaderBase = getHeader(headerSize, byteBuffer, true);
        if (!boolean0) {
            bitHeaderBase.create();
        }

        return bitHeaderBase;
    }

    public static BitHeaderRead allocRead(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer) {
        return allocRead(headerSize, byteBuffer, false);
    }

    public static BitHeaderRead allocRead(BitHeader.HeaderSize headerSize, ByteBuffer byteBuffer, boolean boolean0) {
        BitHeader.BitHeaderBase bitHeaderBase = getHeader(headerSize, byteBuffer, false);
        if (!boolean0) {
            bitHeaderBase.read();
        }

        return bitHeaderBase;
    }

    public abstract static class BitHeaderBase implements BitHeaderRead, BitHeaderWrite {
        protected boolean isWrite;
        protected ByteBuffer buffer;
        protected int start_pos = -1;

        protected void setBuffer(ByteBuffer byteBuffer) {
            this.buffer = byteBuffer;
        }

        protected void setWrite(boolean boolean0) {
            this.isWrite = boolean0;
        }

        @Override
        public int getStartPosition() {
            return this.start_pos;
        }

        protected void reset() {
            this.buffer = null;
            this.isWrite = false;
            this.start_pos = -1;
            this.reset_header();
        }

        @Override
        public abstract int getLen();

        @Override
        public abstract void release();

        protected abstract void reset_header();

        protected abstract void write_header();

        protected abstract void read_header();

        protected abstract void addflags_header(int var1);

        protected abstract void addflags_header(long var1);

        protected abstract boolean hasflags_header(int var1);

        protected abstract boolean hasflags_header(long var1);

        protected abstract boolean equals_header(int var1);

        protected abstract boolean equals_header(long var1);

        @Override
        public void create() {
            if (this.isWrite) {
                this.start_pos = this.buffer.position();
                this.reset_header();
                this.write_header();
            } else {
                throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
            }
        }

        @Override
        public void write() {
            if (this.isWrite) {
                int int0 = this.buffer.position();
                this.buffer.position(this.start_pos);
                this.write_header();
                this.buffer.position(int0);
            } else {
                throw new RuntimeException("BitHeader -> Cannot write to a non write Header.");
            }
        }

        @Override
        public void read() {
            if (!this.isWrite) {
                this.start_pos = this.buffer.position();
                this.read_header();
            } else {
                throw new RuntimeException("BitHeader -> Cannot read from a non read Header.");
            }
        }

        @Override
        public void addFlags(int int0) {
            if (this.isWrite) {
                this.addflags_header(int0);
            } else {
                throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
            }
        }

        @Override
        public void addFlags(long long0) {
            if (this.isWrite) {
                this.addflags_header(long0);
            } else {
                throw new RuntimeException("BitHeader -> Cannot set bits on a non write Header.");
            }
        }

        @Override
        public boolean hasFlags(int int0) {
            return this.hasflags_header(int0);
        }

        @Override
        public boolean hasFlags(long long0) {
            return this.hasflags_header(long0);
        }

        @Override
        public boolean equals(int int0) {
            return this.equals_header(int0);
        }

        @Override
        public boolean equals(long long0) {
            return this.equals_header(long0);
        }
    }

    public static class BitHeaderByte extends BitHeader.BitHeaderBase {
        private ConcurrentLinkedDeque<BitHeader.BitHeaderByte> pool;
        private byte header;

        private BitHeaderByte() {
        }

        @Override
        public void release() {
            this.reset();
            BitHeader.pool_byte.offer(this);
        }

        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }

        @Override
        protected void reset_header() {
            this.header = 0;
        }

        @Override
        protected void write_header() {
            this.buffer.put(this.header);
        }

        @Override
        protected void read_header() {
            this.header = this.buffer.get();
        }

        @Override
        protected void addflags_header(int int0) {
            this.header = Bits.addFlags(this.header, int0);
        }

        @Override
        protected void addflags_header(long long0) {
            this.header = Bits.addFlags(this.header, long0);
        }

        @Override
        protected boolean hasflags_header(int int0) {
            return Bits.hasFlags(this.header, int0);
        }

        @Override
        protected boolean hasflags_header(long long0) {
            return Bits.hasFlags(this.header, long0);
        }

        @Override
        protected boolean equals_header(int int0) {
            return this.header == int0;
        }

        @Override
        protected boolean equals_header(long long0) {
            return this.header == long0;
        }
    }

    public static class BitHeaderInt extends BitHeader.BitHeaderBase {
        private ConcurrentLinkedDeque<BitHeader.BitHeaderInt> pool;
        private int header;

        private BitHeaderInt() {
        }

        @Override
        public void release() {
            this.reset();
            BitHeader.pool_int.offer(this);
        }

        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }

        @Override
        protected void reset_header() {
            this.header = 0;
        }

        @Override
        protected void write_header() {
            this.buffer.putInt(this.header);
        }

        @Override
        protected void read_header() {
            this.header = this.buffer.getInt();
        }

        @Override
        protected void addflags_header(int int0) {
            this.header = Bits.addFlags(this.header, int0);
        }

        @Override
        protected void addflags_header(long long0) {
            this.header = Bits.addFlags(this.header, long0);
        }

        @Override
        protected boolean hasflags_header(int int0) {
            return Bits.hasFlags(this.header, int0);
        }

        @Override
        protected boolean hasflags_header(long long0) {
            return Bits.hasFlags(this.header, long0);
        }

        @Override
        protected boolean equals_header(int int0) {
            return this.header == int0;
        }

        @Override
        protected boolean equals_header(long long0) {
            return this.header == long0;
        }
    }

    public static class BitHeaderLong extends BitHeader.BitHeaderBase {
        private ConcurrentLinkedDeque<BitHeader.BitHeaderLong> pool;
        private long header;

        private BitHeaderLong() {
        }

        @Override
        public void release() {
            this.reset();
            BitHeader.pool_long.offer(this);
        }

        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }

        @Override
        protected void reset_header() {
            this.header = 0L;
        }

        @Override
        protected void write_header() {
            this.buffer.putLong(this.header);
        }

        @Override
        protected void read_header() {
            this.header = this.buffer.getLong();
        }

        @Override
        protected void addflags_header(int int0) {
            this.header = Bits.addFlags(this.header, int0);
        }

        @Override
        protected void addflags_header(long long0) {
            this.header = Bits.addFlags(this.header, long0);
        }

        @Override
        protected boolean hasflags_header(int int0) {
            return Bits.hasFlags(this.header, int0);
        }

        @Override
        protected boolean hasflags_header(long long0) {
            return Bits.hasFlags(this.header, long0);
        }

        @Override
        protected boolean equals_header(int int0) {
            return this.header == int0;
        }

        @Override
        protected boolean equals_header(long long0) {
            return this.header == long0;
        }
    }

    public static class BitHeaderShort extends BitHeader.BitHeaderBase {
        private ConcurrentLinkedDeque<BitHeader.BitHeaderShort> pool;
        private short header;

        private BitHeaderShort() {
        }

        @Override
        public void release() {
            this.reset();
            BitHeader.pool_short.offer(this);
        }

        @Override
        public int getLen() {
            return Bits.getLen(this.header);
        }

        @Override
        protected void reset_header() {
            this.header = 0;
        }

        @Override
        protected void write_header() {
            this.buffer.putShort(this.header);
        }

        @Override
        protected void read_header() {
            this.header = this.buffer.getShort();
        }

        @Override
        protected void addflags_header(int int0) {
            this.header = Bits.addFlags(this.header, int0);
        }

        @Override
        protected void addflags_header(long long0) {
            this.header = Bits.addFlags(this.header, long0);
        }

        @Override
        protected boolean hasflags_header(int int0) {
            return Bits.hasFlags(this.header, int0);
        }

        @Override
        protected boolean hasflags_header(long long0) {
            return Bits.hasFlags(this.header, long0);
        }

        @Override
        protected boolean equals_header(int int0) {
            return this.header == int0;
        }

        @Override
        protected boolean equals_header(long long0) {
            return this.header == long0;
        }
    }

    public static enum HeaderSize {
        Byte,
        Short,
        Integer,
        Long;
    }
}
