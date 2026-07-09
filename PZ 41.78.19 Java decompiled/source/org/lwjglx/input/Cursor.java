// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.input;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjglx.BufferUtils;
import org.lwjglx.LWJGLException;

public class Cursor {
    public static final int CURSOR_ONE_BIT_TRANSPARENCY = 1;
    public static final int CURSOR_8_BIT_ALPHA = 2;
    public static final int CURSOR_ANIMATION = 4;
    private long cursorHandle;

    public Cursor(int arg0, int arg1, int arg2, int arg3, int arg4, IntBuffer arg5, IntBuffer arg6) throws LWJGLException {
        if (arg4 != 1) {
            System.out.println("ANIMATED CURSORS NOT YET SUPPORTED IN LWJGLX");
        } else {
            IntBuffer intBuffer = BufferUtils.createIntBuffer(arg5.limit());
            flipImages(arg0, arg1, arg4, arg5, intBuffer);
            ByteBuffer byteBuffer = convertARGBIntBuffertoRGBAByteBuffer(arg0, arg1, intBuffer);
            GLFWImage gLFWImage = GLFWImage.malloc();
            gLFWImage.width(arg0);
            gLFWImage.height(arg1);
            gLFWImage.pixels(byteBuffer);
            this.cursorHandle = GLFW.glfwCreateCursor(gLFWImage, arg2, arg3);
            if (this.cursorHandle == 0L) {
                throw new RuntimeException("Error creating GLFW cursor");
            }
        }
    }

    private static ByteBuffer convertARGBIntBuffertoRGBAByteBuffer(int int0, int int1, IntBuffer intBuffer) {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int0 * int1 * 4);

        for (int int2 = 0; int2 < intBuffer.limit(); int2++) {
            int int3 = intBuffer.get(int2);
            byte byte0 = (byte)(int3 >>> 24);
            byte byte1 = (byte)(int3 >>> 16);
            byte byte2 = (byte)(int3 >>> 8);
            byte byte3 = (byte)int3;
            byteBuffer.put(byte3);
            byteBuffer.put(byte2);
            byteBuffer.put(byte1);
            byteBuffer.put(byte0);
        }

        byteBuffer.flip();
        return byteBuffer;
    }

    public static int getMinCursorSize() {
        return 1;
    }

    public static int getMaxCursorSize() {
        return 512;
    }

    public static int getCapabilities() {
        return 2;
    }

    private static void flipImages(int int4, int int3, int int1, IntBuffer intBuffer0, IntBuffer intBuffer1) {
        for (int int0 = 0; int0 < int1; int0++) {
            int int2 = int0 * int4 * int3;
            flipImage(int4, int3, int2, intBuffer0, intBuffer1);
        }
    }

    private static void flipImage(int int4, int int1, int int3, IntBuffer intBuffer0, IntBuffer intBuffer1) {
        for (int int0 = 0; int0 < int1 >> 1; int0++) {
            int int2 = int0 * int4 + int3;
            int int5 = (int1 - int0 - 1) * int4 + int3;

            for (int int6 = 0; int6 < int4; int6++) {
                int int7 = int2 + int6;
                int int8 = int5 + int6;
                int int9 = intBuffer0.get(int7 + intBuffer0.position());
                intBuffer1.put(int7, intBuffer0.get(int8 + intBuffer0.position()));
                intBuffer1.put(int8, int9);
            }
        }
    }

    public long getHandle() {
        return this.cursorHandle;
    }

    public void destroy() {
        GLFW.glfwDestroyCursor(this.cursorHandle);
    }
}
