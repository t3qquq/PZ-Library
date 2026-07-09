// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import java.util.ArrayList;
import zombie.GameWindow;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;

public final class KnownBlockedEdges {
    public int x;
    public int y;
    public int z;
    public boolean w;
    public boolean n;
    static final ObjectPool<KnownBlockedEdges> pool = new ObjectPool<>(KnownBlockedEdges::new);

    public KnownBlockedEdges init(KnownBlockedEdges other) {
        return this.init(other.x, other.y, other.z, other.w, other.n);
    }

    public KnownBlockedEdges init(int _x, int _y, int _z) {
        return this.init(_x, _y, _z, false, false);
    }

    public KnownBlockedEdges init(int _x, int _y, int _z, boolean _w, boolean _n) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.w = _w;
        this.n = _n;
        return this;
    }

    public boolean isBlocked(int otherX, int otherY) {
        return this.x > otherX && this.w ? true : this.y > otherY && this.n;
    }

    public static KnownBlockedEdges alloc() {
        assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;

        return pool.alloc();
    }

    public static void releaseAll(ArrayList<KnownBlockedEdges> objs) {
        assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;

        pool.release(objs);
    }

    public void release() {
        assert GameServer.bServer || Thread.currentThread() == GameWindow.GameThread;

        pool.release(this);
    }
}
