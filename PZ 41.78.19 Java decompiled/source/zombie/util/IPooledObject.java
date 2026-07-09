// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.List;

/**
 * The base interface of all pooled objects managed by zombie.util.Pool
 */
public interface IPooledObject {
    Pool<IPooledObject> getPool();

    void setPool(Pool<IPooledObject> pool);

    void release();

    boolean isFree();

    void setFree(boolean isFree);

    default void onReleased() {
    }

    static void release(IPooledObject[] iPooledObjects) {
        int int0 = 0;

        for (int int1 = iPooledObjects.length; int0 < int1; int0++) {
            Pool.tryRelease(iPooledObjects[int0]);
        }
    }

    static void tryReleaseAndBlank(IPooledObject[] iPooledObjects) {
        if (iPooledObjects != null) {
            releaseAndBlank(iPooledObjects);
        }
    }

    static void releaseAndBlank(IPooledObject[] iPooledObjects) {
        int int0 = 0;

        for (int int1 = iPooledObjects.length; int0 < int1; int0++) {
            iPooledObjects[int0] = Pool.tryRelease(iPooledObjects[int0]);
        }
    }

    static void release(List<? extends IPooledObject> list) {
        int int0 = 0;

        for (int int1 = list.size(); int0 < int1; int0++) {
            Pool.tryRelease((IPooledObject)list.get(int0));
        }

        list.clear();
    }
}
