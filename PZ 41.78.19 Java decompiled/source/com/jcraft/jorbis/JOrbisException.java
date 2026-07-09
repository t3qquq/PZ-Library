// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

public class JOrbisException extends Exception {
    private static final long serialVersionUID = 1L;

    public JOrbisException() {
    }

    public JOrbisException(String string) {
        super("JOrbis: " + string);
    }
}
