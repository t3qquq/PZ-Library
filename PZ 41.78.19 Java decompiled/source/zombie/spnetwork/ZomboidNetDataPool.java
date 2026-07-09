// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.spnetwork;

import java.util.ArrayDeque;

public final class ZomboidNetDataPool {
    public static ZomboidNetDataPool instance = new ZomboidNetDataPool();
    private final ArrayDeque<ZomboidNetData> Pool = new ArrayDeque<>();

    public ZomboidNetData get() {
        synchronized (this.Pool) {
            return this.Pool.isEmpty() ? new ZomboidNetData() : this.Pool.pop();
        }
    }

    public void discard(ZomboidNetData zomboidNetData) {
        zomboidNetData.reset();
        if (zomboidNetData.buffer.capacity() == 2048) {
            synchronized (this.Pool) {
                this.Pool.add(zomboidNetData);
            }
        }
    }

    public ZomboidNetData getLong(int int0) {
        return new ZomboidNetData(int0);
    }
}
