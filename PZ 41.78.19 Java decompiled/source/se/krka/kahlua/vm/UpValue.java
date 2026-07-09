// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

public final class UpValue {
    private Coroutine coroutine;
    private final int index;
    private Object value;

    public UpValue(Coroutine arg0, int arg1) {
        this.coroutine = arg0;
        this.index = arg1;
    }

    public int getIndex() {
        return this.index;
    }

    public final Object getValue() {
        return this.coroutine == null ? this.value : this.coroutine.objectStack[this.index];
    }

    public final void setValue(Object arg0) {
        if (this.coroutine == null) {
            this.value = arg0;
        } else {
            this.coroutine.objectStack[this.index] = arg0;
        }
    }

    public void close() {
        this.value = this.coroutine.objectStack[this.index];
        this.coroutine = null;
    }
}
