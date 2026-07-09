// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.luaj.kahluafork.compiler;

public class Token {
    int token;
    double r;
    String ts;

    public void set(Token token0) {
        this.token = token0.token;
        this.r = token0.r;
        this.ts = token0.ts;
    }
}
