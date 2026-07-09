// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ByteBufferBackedInputStream extends InputStream {
    final ByteBuffer buf;

    public ByteBufferBackedInputStream(ByteBuffer byteBuffer) {
        Objects.requireNonNull(byteBuffer);
        this.buf = byteBuffer;
    }

    @Override
    public int read() throws IOException {
        return !this.buf.hasRemaining() ? -1 : this.buf.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int int1, int int0) throws IOException {
        if (!this.buf.hasRemaining()) {
            return -1;
        } else {
            int0 = Math.min(int0, this.buf.remaining());
            this.buf.get(bytes, int1, int0);
            return int0;
        }
    }
}
