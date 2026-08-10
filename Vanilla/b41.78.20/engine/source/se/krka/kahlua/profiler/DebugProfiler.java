/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.io.Writer;
import se.krka.kahlua.profiler.Profiler;
import se.krka.kahlua.profiler.Sample;
import se.krka.kahlua.profiler.StacktraceElement;

public class DebugProfiler
implements Profiler {
    private PrintWriter output;

    public DebugProfiler(Writer writer) {
        this.output = new PrintWriter(writer);
    }

    @Override
    public synchronized void getSample(Sample sample) {
        this.output.println("Sample: " + sample.getTime() + " ms");
        for (StacktraceElement stacktraceElement : sample.getList()) {
            this.output.println("\t" + stacktraceElement.name() + "\t" + stacktraceElement.type() + "\t" + stacktraceElement.hashCode());
        }
    }
}

