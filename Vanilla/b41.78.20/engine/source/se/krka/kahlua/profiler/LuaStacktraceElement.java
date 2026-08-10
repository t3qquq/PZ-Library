/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import se.krka.kahlua.profiler.StacktraceElement;
import se.krka.kahlua.vm.Prototype;

public class LuaStacktraceElement
implements StacktraceElement {
    private final int pc;
    private final Prototype prototype;

    public LuaStacktraceElement(int n, Prototype prototype) {
        this.pc = n;
        this.prototype = prototype;
    }

    public int getLine() {
        if (this.pc >= 0 && this.pc < this.prototype.lines.length) {
            return this.prototype.lines[this.pc];
        }
        return 0;
    }

    public String getSource() {
        return this.prototype.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LuaStacktraceElement)) {
            return false;
        }
        LuaStacktraceElement luaStacktraceElement = (LuaStacktraceElement)object;
        if (this.getLine() != luaStacktraceElement.getLine()) {
            return false;
        }
        return this.prototype.equals(luaStacktraceElement.prototype);
    }

    public int hashCode() {
        int n = this.getLine();
        n = 31 * n + this.prototype.hashCode();
        return n;
    }

    public String toString() {
        return this.name();
    }

    @Override
    public String name() {
        return this.getSource() + ":" + this.getLine();
    }

    @Override
    public String type() {
        return "lua";
    }
}

