// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.profiling;

import zombie.util.list.PZArrayUtil;

public class PerformanceProfileProbeList<Probe extends PerformanceProfileProbe> {
    final String m_prefix;
    final Probe[] layers;

    public static PerformanceProfileProbeList<PerformanceProfileProbe> construct(String string, int int0) {
        return new PerformanceProfileProbeList<>(string, int0, PerformanceProfileProbe.class, PerformanceProfileProbe::new);
    }

    public static <Probe extends PerformanceProfileProbe> PerformanceProfileProbeList<Probe> construct(
        String string, int int0, Class<Probe> clazz, PerformanceProfileProbeList.Constructor<Probe> constructor
    ) {
        return new PerformanceProfileProbeList<>(string, int0, clazz, constructor);
    }

    protected PerformanceProfileProbeList(String string, int int0, Class<Probe> clazz, PerformanceProfileProbeList.Constructor<Probe> constructor) {
        this.m_prefix = string;
        this.layers = (Probe[])PZArrayUtil.newInstance(clazz, int0 + 1);

        for (int int1 = 0; int1 < int0; int1++) {
            this.layers[int1] = (Probe)constructor.get(string + "_" + int1);
        }

        this.layers[int0] = (Probe)constructor.get(string + "_etc");
    }

    public int count() {
        return this.layers.length;
    }

    public Probe at(int int0) {
        return int0 < this.count() ? this.layers[int0] : this.layers[this.count() - 1];
    }

    public Probe start(int int0) {
        PerformanceProfileProbe performanceProfileProbe = this.at(int0);
        performanceProfileProbe.start();
        return (Probe)performanceProfileProbe;
    }

    public interface Constructor<Probe extends PerformanceProfileProbe> {
        Probe get(String var1);
    }
}
