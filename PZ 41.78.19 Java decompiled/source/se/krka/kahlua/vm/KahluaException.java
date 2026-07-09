// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

public class KahluaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public Object errorMessage;

    public KahluaException(Object object) {
        this.errorMessage = object;
    }

    @Override
    public String getMessage() {
        return this.errorMessage == null ? "nil" : this.errorMessage.toString();
    }
}
