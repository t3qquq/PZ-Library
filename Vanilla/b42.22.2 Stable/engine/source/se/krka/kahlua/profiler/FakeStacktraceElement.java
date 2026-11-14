// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.profiler;

public class FakeStacktraceElement implements StacktraceElement {
    private final String name;
    private final String type;

    public FakeStacktraceElement(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof FakeStacktraceElement that) {
            return !this.name.equals(that.name) ? false : this.type.equals(that.type);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        return 31 * result + this.type.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
