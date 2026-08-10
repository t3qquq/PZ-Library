/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.vm;

public class KahluaException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public Object errorMessage;

    public KahluaException(Object object) {
        this.errorMessage = object;
    }

    @Override
    public String getMessage() {
        if (this.errorMessage == null) {
            return "nil";
        }
        return this.errorMessage.toString();
    }
}

