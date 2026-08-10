/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.profiler.Profiler;
import se.krka.kahlua.profiler.Sample;

public class BufferedProfiler
implements Profiler {
    private final List<Sample> buffer = new ArrayList<Sample>();

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

