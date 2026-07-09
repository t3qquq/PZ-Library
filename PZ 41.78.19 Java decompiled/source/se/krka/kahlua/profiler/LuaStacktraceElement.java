// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.Prototype;

public class LuaStacktraceElement implements StacktraceElement {
    private final int pc;
    private final Prototype prototype;

    public LuaStacktraceElement(int int0, Prototype prototypex) {
        this.pc = int0;
        this.prototype = prototypex;
    }

    public int getLine() {
        return this.pc >= 0 && this.pc < this.prototype.lines.length ? this.prototype.lines[this.pc] : 0;
    }

    public String getSource() {
        return this.prototype.name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof LuaStacktraceElement luaStacktraceElement1)) {
            return false;
        } else {
            return this.getLine() != luaStacktraceElement1.getLine() ? false : this.prototype.equals(luaStacktraceElement1.prototype);
        }
    }

    @Override
    public int hashCode() {
        int int0 = this.getLine();
        return 31 * int0 + this.prototype.hashCode();
    }

    @Override
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
