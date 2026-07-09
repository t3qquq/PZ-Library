// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

public class FakeStacktraceElement implements StacktraceElement {
    private final String name;
    private final String type;

    public FakeStacktraceElement(String string0, String string1) {
        this.name = string0;
        this.type = string1;
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof FakeStacktraceElement fakeStacktraceElement1)) {
            return false;
        } else {
            return !this.name.equals(fakeStacktraceElement1.name) ? false : this.type.equals(fakeStacktraceElement1.type);
        }
    }

    @Override
    public int hashCode() {
        int int0 = this.name.hashCode();
        return 31 * int0 + this.type.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
