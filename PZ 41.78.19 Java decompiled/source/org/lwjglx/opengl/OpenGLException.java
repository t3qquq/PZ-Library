// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.opengl;

public class OpenGLException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OpenGLException(int int0) {
        this(createErrorMessage(int0));
    }

    private static String createErrorMessage(int int0) {
        String string = Util.translateGLErrorString(int0);
        return string + " (" + int0 + ")";
    }

    public OpenGLException() {
    }

    public OpenGLException(String string) {
        super(string);
    }

    public OpenGLException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public OpenGLException(Throwable throwable) {
        super(throwable);
    }
}
