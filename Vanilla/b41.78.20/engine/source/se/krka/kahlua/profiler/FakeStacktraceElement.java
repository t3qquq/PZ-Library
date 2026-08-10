/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import se.krka.kahlua.profiler.StacktraceElement;

public class FakeStacktraceElement
implements StacktraceElement {
    private final String name;
    private final String type;

    public FakeStacktraceElement(String string, String string2) {
        this.name = string;
        this.type = string2;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String type() {
        return this.type;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FakeStacktraceElement)) {
            return false;
        }
        FakeStacktraceElement fakeStacktraceElement = (FakeStacktraceElement)object;
        if (!this.name.equals(fakeStacktraceElement.name)) {
            return false;
        }
        return this.type.equals(fakeStacktraceElement.type);
    }

    public int hashCode() {
        int n = this.name.hashCode();
        n = 31 * n + this.type.hashCode();
        return n;
    }

    public String toString() {
        return this.name;
    }
}

