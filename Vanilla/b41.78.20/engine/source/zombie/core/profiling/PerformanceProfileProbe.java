// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.profiling;

import java.util.Stack;
import zombie.GameProfiler;

public class PerformanceProfileProbe extends AbstractPerformanceProfileProbe {
    private final Stack<GameProfiler.ProfileArea> m_currentArea = new Stack<>();

    public PerformanceProfileProbe(String name) {
        super(name);
    }

    public PerformanceProfileProbe(String name, boolean isEnabled) {
        super(name);
        this.setEnabled(isEnabled);
    }

    @Override
    protected void onStart() {
        this.m_currentArea.push(GameProfiler.getInstance().start(this.Name));
    }

    @Override
    protected void onEnd() {
        GameProfiler.getInstance().end(this.m_currentArea.pop());
    }
}
