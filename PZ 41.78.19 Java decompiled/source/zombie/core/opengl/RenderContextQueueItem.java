// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import java.util.function.BooleanSupplier;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;

public final class RenderContextQueueItem {
    private Runnable m_runnable;
    private boolean m_isFinished;
    private boolean m_isWaiting;
    private Throwable m_runnableThrown = null;
    private final Object m_waitLock = "RenderContextQueueItem Wait Lock";

    private RenderContextQueueItem() {
    }

    public static RenderContextQueueItem alloc(Runnable runnable) {
        RenderContextQueueItem renderContextQueueItem = new RenderContextQueueItem();
        renderContextQueueItem.resetInternal();
        renderContextQueueItem.m_runnable = runnable;
        return renderContextQueueItem;
    }

    private void resetInternal() {
        this.m_runnable = null;
        this.m_isFinished = false;
        this.m_runnableThrown = null;
    }

    public void waitUntilFinished(BooleanSupplier booleanSupplier) throws InterruptedException {
        while (!this.isFinished()) {
            if (!booleanSupplier.getAsBoolean()) {
                return;
            }

            synchronized (this.m_waitLock) {
                if (!this.isFinished()) {
                    this.m_waitLock.wait();
                }
            }
        }
    }

    public boolean isFinished() {
        return this.m_isFinished;
    }

    public void setWaiting() {
        this.m_isWaiting = true;
    }

    public boolean isWaiting() {
        return this.m_isWaiting;
    }

    public void invoke() {
        try {
            this.m_runnableThrown = null;
            this.m_runnable.run();
        } catch (Throwable throwable) {
            this.m_runnableThrown = throwable;
            DebugLog.General.error("%s thrown during invoke().", throwable.toString());
            ExceptionLogger.logException(throwable);
        } finally {
            synchronized (this.m_waitLock) {
                this.m_isFinished = true;
                this.m_waitLock.notifyAll();
            }
        }
    }

    public Throwable getThrown() {
        return this.m_runnableThrown;
    }

    public void notifyWaitingListeners() {
        synchronized (this.m_waitLock) {
            this.m_waitLock.notifyAll();
        }
    }
}
