// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import se.krka.kahlua.vm.JavaFunction;

public class JavaStacktraceElement implements StacktraceElement {
    private final JavaFunction javaFunction;

    public JavaStacktraceElement(JavaFunction javaFunctionx) {
        this.javaFunction = javaFunctionx;
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return !(object instanceof JavaStacktraceElement javaStacktraceElement1) ? false : this.javaFunction == javaStacktraceElement1.javaFunction;
        }
    }

    @Override
    public int hashCode() {
        return this.javaFunction.hashCode();
    }
}
