// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.List;

public class BufferedProfiler implements Profiler {
    private final List<Sample> buffer = new ArrayList<>();

    @Override
    public void getSample(Sample sample) {
        this.buffer.add(sample);
    }

    public void sendTo(Profiler profiler) {
        for (Sample sample : this.buffer) {
            profiler.getSample(sample);
        }
    }
}
