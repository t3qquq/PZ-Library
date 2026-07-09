// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.luaj.kahluafork.compiler;

class InstructionPtr {
    final int[] code;
    final int idx;

    InstructionPtr(int[] ints, int int0) {
        this.code = ints;
        this.idx = int0;
    }

    int get() {
        return this.code[this.idx];
    }

    void set(int int0) {
        this.code[this.idx] = int0;
    }
}
