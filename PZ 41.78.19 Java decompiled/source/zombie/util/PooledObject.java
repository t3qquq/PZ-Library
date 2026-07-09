// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public abstract class PooledObject implements IPooledObject {
    private boolean m_isFree = true;
    private Pool<IPooledObject> m_pool;

    @Override
    public final Pool<IPooledObject> getPool() {
        return this.m_pool;
    }

    @Override
    public final void setPool(Pool<IPooledObject> pool) {
        this.m_pool = pool;
    }

    @Override
    public final void release() {
        if (this.m_pool != null) {
            this.m_pool.release(this);
        } else {
            this.onReleased();
        }
    }

    @Override
    public final boolean isFree() {
        return this.m_isFree;
    }

    @Override
    public final void setFree(boolean boolean0) {
        this.m_isFree = boolean0;
    }
}
