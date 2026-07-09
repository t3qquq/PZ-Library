// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.profiling;

import zombie.GameProfiler;

public class PerformanceProfileFrameProbe extends PerformanceProfileProbe {
    public PerformanceProfileFrameProbe(String string) {
        super(string);
    }

    @Override
    protected void onStart() {
        GameProfiler.getInstance().startFrame(this.Name);
        super.onStart();
    }

    @Override
    protected void onEnd() {
        super.onEnd();
        GameProfiler.getInstance().endFrame();
    }
}
