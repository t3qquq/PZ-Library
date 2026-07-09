// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import java.util.HashMap;
import java.util.Map;

public class StacktraceCounter {
    private final Map<StacktraceElement, StacktraceCounter> children = new HashMap<>();
    private long time = 0L;

    public void addTime(long long0) {
        this.time += long0;
    }

    public StacktraceCounter getOrCreateChild(StacktraceElement stacktraceElement) {
        StacktraceCounter stacktraceCounter0 = this.children.get(stacktraceElement);
        if (stacktraceCounter0 == null) {
            stacktraceCounter0 = new StacktraceCounter();
            this.children.put(stacktraceElement, stacktraceCounter0);
        }

        return stacktraceCounter0;
    }

    public long getTime() {
        return this.time;
    }

    public Map<StacktraceElement, StacktraceCounter> getChildren() {
        return this.children;
    }
}
