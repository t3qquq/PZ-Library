// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.profiling;

import zombie.GameProfiler;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;

public abstract class AbstractPerformanceProfileProbe {
    public final String Name;
    private boolean m_isEnabled = true;
    private boolean m_isRunning = false;
    private boolean m_isProfilerRunning = false;

    protected AbstractPerformanceProfileProbe(String string) {
        this.Name = string;
    }

    protected abstract void onStart();

    protected abstract void onEnd();

    public void start() {
        if (this.m_isRunning) {
            throw new RuntimeException("start() already called. " + this.getClass().getSimpleName() + " is Non-reentrant. Please call end() first.");
        } else {
            this.m_isProfilerRunning = this.isEnabled() && GameProfiler.isRunning();
            if (this.m_isProfilerRunning) {
                this.m_isRunning = true;
                this.onStart();
            }
        }
    }

    public boolean isEnabled() {
        return this.m_isEnabled;
    }

    public void setEnabled(boolean boolean0) {
        this.m_isEnabled = boolean0;
    }

    public void end() {
        if (this.m_isProfilerRunning) {
            if (!this.m_isRunning) {
                throw new RuntimeException("end() called without calling start().");
            } else {
                this.onEnd();
                this.m_isRunning = false;
            }
        }
    }

    public void invokeAndMeasure(Runnable runnable) {
        try {
            this.start();
            runnable.run();
        } finally {
            this.end();
        }
    }

    public <T1> void invokeAndMeasure(T1 object, Invokers.Params1.ICallback<T1> iCallback) {
        Lambda.capture(
            this,
            object,
            iCallback,
            (genericStack, abstractPerformanceProfileProbe, objectx, iCallbackx) -> abstractPerformanceProfileProbe.invokeAndMeasure(
                genericStack.invoker(objectx, iCallbackx)
            )
        );
    }

    public <T1, T2> void invokeAndMeasure(T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
        Lambda.capture(
            this,
            object0,
            object1,
            iCallback,
            (genericStack, abstractPerformanceProfileProbe, object0x, object1x, iCallbackx) -> abstractPerformanceProfileProbe.invokeAndMeasure(
                genericStack.invoker(object0x, object1x, iCallbackx)
            )
        );
    }
}
