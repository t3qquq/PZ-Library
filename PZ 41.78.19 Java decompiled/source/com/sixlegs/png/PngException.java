// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.io.IOException;
import java.lang.reflect.Method;

public class PngException extends IOException {
    private static final Method initCause = getInitCause();
    private final boolean fatal;

    private static Method getInitCause() {
        try {
            return PngException.class.getMethod("initCause", Throwable.class);
        } catch (Exception exception) {
            return null;
        }
    }

    PngException(String string, boolean boolean0) {
        this(string, null, boolean0);
    }

    PngException(String string, Throwable throwable, boolean boolean0) {
        super(string);
        this.fatal = boolean0;
        if (throwable != null && initCause != null) {
            try {
                initCause.invoke(this, throwable);
            } catch (RuntimeException runtimeException) {
                throw runtimeException;
            } catch (Exception exception) {
                throw new IllegalStateException("Error invoking initCause: " + exception.getMessage());
            }
        }
    }

    public boolean isFatal() {
        return this.fatal;
    }
}
