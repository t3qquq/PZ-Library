// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.Supplier;

public class LazyValue<T> {
    protected static final Object UNSET = new Object();
    protected static final VarHandle HANDLE;
    private volatile Object value = UNSET;
    private final Supplier<T> supplier;

    public LazyValue(Supplier<T> supplierx) {
        this.supplier = Objects.requireNonNull(supplierx);
    }

    public T get() {
        Object object0 = (Object)HANDLE.getVolatile((LazyValue)this);
        if (object0 != UNSET) {
            return (T)object0;
        } else {
            Object object1 = this.supplier.get();
            Object object2 = (Object)HANDLE.compareAndExchange((LazyValue)this, (Object)UNSET, (Object)object1);
            return (T)(object2 == UNSET ? object1 : object2);
        }
    }

    static {
        try {
            HANDLE = MethodHandles.lookup().findVarHandle(LazyValue.class, "value", Object.class);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
