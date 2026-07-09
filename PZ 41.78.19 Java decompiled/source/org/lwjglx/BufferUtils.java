// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public final class BufferUtils {
    public static ByteBuffer createByteBuffer(int int0) {
        return ByteBuffer.allocateDirect(int0).order(ByteOrder.nativeOrder());
    }

    public static ShortBuffer createShortBuffer(int int0) {
        return createByteBuffer(int0 << 1).asShortBuffer();
    }

    public static CharBuffer createCharBuffer(int int0) {
        return createByteBuffer(int0 << 1).asCharBuffer();
    }

    public static IntBuffer createIntBuffer(int int0) {
        return createByteBuffer(int0 << 2).asIntBuffer();
    }

    public static LongBuffer createLongBuffer(int int0) {
        return createByteBuffer(int0 << 3).asLongBuffer();
    }

    public static FloatBuffer createFloatBuffer(int int0) {
        return createByteBuffer(int0 << 2).asFloatBuffer();
    }

    public static DoubleBuffer createDoubleBuffer(int int0) {
        return createByteBuffer(int0 << 3).asDoubleBuffer();
    }

    public static int getElementSizeExponent(Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            return 0;
        } else if (buffer instanceof ShortBuffer || buffer instanceof CharBuffer) {
            return 1;
        } else if (buffer instanceof FloatBuffer || buffer instanceof IntBuffer) {
            return 2;
        } else if (!(buffer instanceof LongBuffer) && !(buffer instanceof DoubleBuffer)) {
            throw new IllegalStateException("Unsupported buffer type: " + buffer);
        } else {
            return 3;
        }
    }

    public static int getOffset(Buffer buffer) {
        return buffer.position() << getElementSizeExponent(buffer);
    }

    public static void zeroBuffer(ByteBuffer byteBuffer) {
        zeroBuffer0(byteBuffer, byteBuffer.position(), byteBuffer.remaining());
    }

    public static void zeroBuffer(ShortBuffer shortBuffer) {
        zeroBuffer0(shortBuffer, shortBuffer.position() * 2L, shortBuffer.remaining() * 2L);
    }

    public static void zeroBuffer(CharBuffer charBuffer) {
        zeroBuffer0(charBuffer, charBuffer.position() * 2L, charBuffer.remaining() * 2L);
    }

    public static void zeroBuffer(IntBuffer intBuffer) {
        zeroBuffer0(intBuffer, intBuffer.position() * 4L, intBuffer.remaining() * 4L);
    }

    public static void zeroBuffer(FloatBuffer floatBuffer) {
        zeroBuffer0(floatBuffer, floatBuffer.position() * 4L, floatBuffer.remaining() * 4L);
    }

    public static void zeroBuffer(LongBuffer longBuffer) {
        zeroBuffer0(longBuffer, longBuffer.position() * 8L, longBuffer.remaining() * 8L);
    }

    public static void zeroBuffer(DoubleBuffer doubleBuffer) {
        zeroBuffer0(doubleBuffer, doubleBuffer.position() * 8L, doubleBuffer.remaining() * 8L);
    }

    private static native void zeroBuffer0(Buffer var0, long var1, long var3);

    static native long getBufferAddress(Buffer var0);
}
