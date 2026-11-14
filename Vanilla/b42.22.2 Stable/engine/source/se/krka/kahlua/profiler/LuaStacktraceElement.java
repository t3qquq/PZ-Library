// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.Prototype;

public class LuaStacktraceElement implements StacktraceElement {
    private final int pc;
    private final Prototype prototype;

    public LuaStacktraceElement(int pc, Prototype prototype) {
        this.pc = pc;
        this.prototype = prototype;
    }

    public int getLine() {
        return this.pc >= 0 && this.pc < this.prototype.lines.length ? this.prototype.lines[this.pc] : 0;
    }

    public String getSource() {
        return this.prototype.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof LuaStacktraceElement that) {
            return this.getLine() != that.getLine() ? false : this.prototype.equals(that.prototype);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.getLine();
        return 31 * result + this.prototype.hashCode();
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
