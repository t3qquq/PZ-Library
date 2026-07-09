// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.function.Supplier;

public class ResettableLazyValue<T> extends LazyValue<T> {
    public ResettableLazyValue(Supplier<T> supplier) {
        super(supplier);
    }

    public void reset() {
        HANDLE.setVolatile((ResettableLazyValue)this, (Object)UNSET);
    }
}
