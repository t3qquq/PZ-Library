/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.profiler.StacktraceElement;

public class StacktraceCounter {
    private final Map<StacktraceElement, StacktraceCounter> children = new HashMap<StacktraceElement, StacktraceCounter>();
    private long time = 0L;

    public void addTime(long l) {
        this.time += l;
    }

    public StacktraceCounter getOrCreateChild(StacktraceElement stacktraceElement) {
        StacktraceCounter stacktraceCounter = this.children.get(stacktraceElement);
        if (stacktraceCounter == null) {
            stacktraceCounter = new StacktraceCounter();
            this.children.put(stacktraceElement, stacktraceCounter);
        }
        return stacktraceCounter;
    }

    public long getTime() {
        return this.time;
    }

    public Map<StacktraceElement, StacktraceCounter> getChildren() {
        return this.children;
    }
}

