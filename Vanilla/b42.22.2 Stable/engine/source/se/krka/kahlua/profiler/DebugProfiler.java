// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.io.Writer;

public class DebugProfiler implements Profiler {
    private final PrintWriter output;

    public DebugProfiler(Writer output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public synchronized void getSample(Sample sample) {
        this.output.println("Sample: " + sample.getTime() + " ms");

        for (StacktraceElement element : sample.getList()) {
            this.output.println("\t" + element.name() + "\t" + element.type() + "\t" + element.hashCode());
        }
    }
}
