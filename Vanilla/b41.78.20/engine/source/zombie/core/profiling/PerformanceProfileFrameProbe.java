// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
