// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.io.Writer;

public class DebugProfiler implements Profiler {
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
