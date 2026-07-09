// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.opengl;

import org.lwjgl.opengl.GL30;

public final class Util {
    private Util() {
    }

    public static void checkGLError() throws OpenGLException {
        int int0 = GL30.glGetError();
        if (int0 != 0) {
            throw new OpenGLException(int0);
        }
    }

    public static String translateGLErrorString(int int0) {
        switch (int0) {
            case 0:
                return "No error";
            case 1280:
                return "Invalid enum";
            case 1281:
                return "Invalid value";
            case 1282:
                return "Invalid operation";
            case 1283:
                return "Stack overflow";
            case 1284:
                return "Stack underflow";
            case 1285:
                return "Out of memory";
            case 1286:
                return "Invalid framebuffer operation";
            case 32817:
                return "Table too large";
            default:
                return null;
        }
    }
}
