// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import zombie.util.list.PZArrayUtil;

/**
 * A thread-safe object pool. Useful for re-using memory without it falling into the garbage collector.   Beware: Once an item has been allocated, it MUST be released at some point by calling its release() function.          If not, the item's memory will never be recycled, and it will be considered a memory leak.
 */
public final class Pool<PO extends IPooledObject> {
    private final Supplier<PO> m_allocator;
    private final ThreadLocal<Pool.PoolStacks> m_stacks = ThreadLocal.withInitial(Pool.PoolStacks::new);

    public Pool(Supplier<PO> allocator) {
        this.m_allocator = allocator;
    }

    public final PO alloc() {
        Supplier supplier = this.m_allocator;
        Pool.PoolStacks poolStacks = this.m_stacks.get();
        THashSet tHashSet = poolStacks.inUse;
        List list = poolStacks.released;
        IPooledObject iPooledObject;
        if (!list.isEmpty()) {
            iPooledObject = (IPooledObject)list.remove(list.size() - 1);
        } else {
            iPooledObject = (IPooledObject)supplier.get();
            if (iPooledObject == null) {
                throw new NullPointerException("Allocator returned a nullPtr. This is not allowed.");
            }

            iPooledObject.setPool(this);
        }

        iPooledObject.setFree(false);
        tHashSet.add(iPooledObject);
        return (PO)iPooledObject;
    }

    public final void release(IPooledObject item) {
        Pool.PoolStacks poolStacks = this.m_stacks.get();
        THashSet tHashSet = poolStacks.inUse;
        List list = poolStacks.released;
        if (item.getPool() != this) {
            throw new UnsupportedOperationException("Cannot release item. Not owned by this pool.");
        } else if (item.isFree()) {
            throw new UnsupportedOperationException("Cannot release item. Already released.");
        } else {
            tHashSet.remove(item);
            item.setFree(true);
            list.add(item);
            item.onReleased();
        }
    }

    public static <E> E tryRelease(E object) {
        IPooledObject iPooledObject = Type.tryCastTo(object, IPooledObject.class);
        if (iPooledObject != null && !iPooledObject.isFree()) {
            iPooledObject.release();
        }

        return null;
    }

    public static <E extends IPooledObject> E tryRelease(E iPooledObject) {
        if (iPooledObject != null && !iPooledObject.isFree()) {
            iPooledObject.release();
        }

        return null;
    }

    public static <E extends IPooledObject> E[] tryRelease(E[] iPooledObjects) {
        PZArrayUtil.forEach(iPooledObjects, Pool::tryRelease);
        return null;
    }

    private static final class PoolStacks {
        final THashSet<IPooledObject> inUse = new THashSet<>();
        final List<IPooledObject> released = new ArrayList<>();

        PoolStacks() {
            this.inUse.setAutoCompactionFactor(0.0F);
        }
    }
}
