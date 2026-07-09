// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
    private ByteBuffer wrappedBuffer;
    private final boolean autoEnlarge;

    public ByteBufferOutputStream(ByteBuffer byteBuffer, boolean boolean0) {
        this.wrappedBuffer = byteBuffer;
        this.autoEnlarge = boolean0;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = this.wrappedBuffer.duplicate();
        byteBuffer.flip();
        return byteBuffer.asReadOnlyBuffer();
    }

    public ByteBuffer getWrappedBuffer() {
        return this.wrappedBuffer;
    }

    public void clear() {
        this.wrappedBuffer.clear();
    }

    public void flip() {
        this.wrappedBuffer.flip();
    }

    private void growTo(int int2) {
        int int0 = this.wrappedBuffer.capacity();
        int int1 = int0 << 1;
        if (int1 - int2 < 0) {
            int1 = int2;
        }

        if (int1 < 0) {
            if (int2 < 0) {
                throw new OutOfMemoryError();
            }

            int1 = Integer.MAX_VALUE;
        }

        ByteBuffer byteBuffer = this.wrappedBuffer;
        if (this.wrappedBuffer.isDirect()) {
            this.wrappedBuffer = ByteBuffer.allocateDirect(int1);
        } else {
            this.wrappedBuffer = ByteBuffer.allocate(int1);
        }

        byteBuffer.flip();
        this.wrappedBuffer.put(byteBuffer);
    }

    @Override
    public void write(int int0) {
        try {
            this.wrappedBuffer.put((byte)int0);
        } catch (BufferOverflowException bufferOverflowException) {
            if (!this.autoEnlarge) {
                throw bufferOverflowException;
            }

            int int1 = this.wrappedBuffer.capacity() * 2;
            this.growTo(int1);
            this.write(int0);
        }
    }

    @Override
    public void write(byte[] bytes) {
        int int0 = 0;

        try {
            int0 = this.wrappedBuffer.position();
            this.wrappedBuffer.put(bytes);
        } catch (BufferOverflowException bufferOverflowException) {
            if (!this.autoEnlarge) {
                throw bufferOverflowException;
            }

            int int1 = Math.max(this.wrappedBuffer.capacity() * 2, int0 + bytes.length);
            this.growTo(int1);
            this.write(bytes);
        }
    }

    @Override
    public void write(byte[] bytes, int int1, int int2) {
        int int0 = 0;

        try {
            int0 = this.wrappedBuffer.position();
            this.wrappedBuffer.put(bytes, int1, int2);
        } catch (BufferOverflowException bufferOverflowException) {
            if (!this.autoEnlarge) {
                throw bufferOverflowException;
            }

            int int3 = Math.max(this.wrappedBuffer.capacity() * 2, int0 + int2);
            this.growTo(int3);
            this.write(bytes, int1, int2);
        }
    }
}
