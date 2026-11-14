// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.JavaFunction;

public class JavaStacktraceElement implements StacktraceElement {
    private final JavaFunction javaFunction;

    public JavaStacktraceElement(JavaFunction javaFunction) {
        this.javaFunction = javaFunction;
    }

    @Override
    public String name() {
        return this.javaFunction.toString();
    }

    @Override
    public String type() {
        return "java";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof JavaStacktraceElement that ? this.javaFunction == that.javaFunction : false;
        }
    }

    @Override
    public int hashCode() {
        return this.javaFunction.hashCode();
    }
}
