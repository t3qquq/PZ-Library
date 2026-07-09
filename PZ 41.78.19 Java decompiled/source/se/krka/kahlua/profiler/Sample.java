// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import java.util.List;

public class Sample {
    private final List<StacktraceElement> list;
    private final long time;

    public Sample(List<StacktraceElement> listx, long long0) {
        this.list = listx;
        this.time = long0;
    }

    public List<StacktraceElement> getList() {
        return this.list;
    }

    public long getTime() {
        return this.time;
    }
}
