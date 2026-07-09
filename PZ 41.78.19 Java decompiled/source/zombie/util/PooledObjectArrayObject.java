// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public final class PooledObjectArrayObject<T extends IPooledObject> extends PooledArrayObject<T> {
    @Override
    public void onReleased() {
        int int0 = 0;

        for (int int1 = this.length(); int0 < int1; int0++) {
            this.get(int0).release();
        }
    }
}
