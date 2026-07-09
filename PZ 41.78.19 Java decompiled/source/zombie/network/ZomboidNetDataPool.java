// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ZomboidNetDataPool {
    public static final ZomboidNetDataPool instance = new ZomboidNetDataPool();
    final ConcurrentLinkedQueue<ZomboidNetData> Pool = new ConcurrentLinkedQueue<>();

    public ZomboidNetData get() {
        ZomboidNetData zomboidNetData = this.Pool.poll();
        return zomboidNetData == null ? new ZomboidNetData() : zomboidNetData;
    }

    public void discard(ZomboidNetData zomboidNetData) {
        zomboidNetData.reset();
        if (zomboidNetData.buffer.capacity() == 2048) {
            this.Pool.add(zomboidNetData);
        }
    }

    public ZomboidNetData getLong(int int0) {
        return new ZomboidNetData(int0);
    }
}
