// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

public class AggregatingProfiler implements Profiler {
    private final StacktraceCounter root = new StacktraceCounter();

    @Override
    public synchronized void getSample(Sample sample) {
        this.root.addTime(sample.getTime());
        StacktraceCounter stacktraceCounter0 = this.root;

        for (int int0 = sample.getList().size() - 1; int0 >= 0; int0--) {
            StacktraceElement stacktraceElement = sample.getList().get(int0);
            StacktraceCounter stacktraceCounter1 = stacktraceCounter0.getOrCreateChild(stacktraceElement);
            stacktraceCounter1.addTime(sample.getTime());
            stacktraceCounter0 = stacktraceCounter1;
        }
    }

    public StacktraceNode toTree(int int0, double double0, int int1) {
        return StacktraceNode.createFrom(this.root, new FakeStacktraceElement("Root", "root"), int0, double0, int1);
    }
}
