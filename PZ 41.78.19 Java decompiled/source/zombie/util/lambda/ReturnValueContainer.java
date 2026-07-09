// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ReturnValueContainer<T> extends PooledObject {
    public T ReturnVal;
    private static final Pool<ReturnValueContainer<Object>> s_pool = new Pool<>(ReturnValueContainer::new);

    @Override
    public void onReleased() {
        this.ReturnVal = null;
    }

    public static <E> ReturnValueContainer<E> alloc() {
        return (ReturnValueContainer<E>)s_pool.alloc();
    }
}
