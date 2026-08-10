/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import se.krka.kahlua.profiler.FakeStacktraceElement;
import se.krka.kahlua.profiler.Profiler;
import se.krka.kahlua.profiler.Sample;
import se.krka.kahlua.profiler.StacktraceCounter;
import se.krka.kahlua.profiler.StacktraceElement;
import se.krka.kahlua.profiler.StacktraceNode;

public class AggregatingProfiler
implements Profiler {
    private final StacktraceCounter root = new StacktraceCounter();

    @Override
    public synchronized void getSample(Sample sample) {
        this.root.addTime(sample.getTime());
        StacktraceCounter stacktraceCounter = this.root;
        for (int i = sample.getList().size() - 1; i >= 0; --i) {
            StacktraceElement stacktraceElement = sample.getList().get(i);
            StacktraceCounter stacktraceCounter2 = stacktraceCounter.getOrCreateChild(stacktraceElement);
            stacktraceCounter2.addTime(sample.getTime());
            stacktraceCounter = stacktraceCounter2;
        }
    }

    public StacktraceNode toTree(int n, double d, int n2) {
        return StacktraceNode.createFrom(this.root, new FakeStacktraceElement("Root", "root"), n, d, n2);
    }
}

