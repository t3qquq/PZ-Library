// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
