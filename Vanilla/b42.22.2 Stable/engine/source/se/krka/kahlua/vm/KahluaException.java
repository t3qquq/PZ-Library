// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

import java.io.File;
import zombie.util.list.PZArrayUtil;

public class KahluaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public final Object errorMessage;
    private final String source;
    private final int lineNumber;

    public KahluaException(Object errorMessage) {
        this.errorMessage = errorMessage;
        this.source = null;
        this.lineNumber = -1;
    }

    public KahluaException(Object errorMessage, String source, int lineNumber) {
        this.errorMessage = errorMessage;
        this.source = source;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return this.errorMessage == null ? "nil" : this.errorMessage.toString();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        if (this.source == null) {
            return super.getStackTrace();
        }

        String fileName = new File(this.source).getName();
        StackTraceElement sourceElement = new StackTraceElement("Lua", this.source, fileName, this.lineNumber);
        StackTraceElement[] parentStackTrace = super.getStackTrace();
        return (StackTraceElement[])PZArrayUtil.insertAt(parentStackTrace, 0, sourceElement);
    }
}
