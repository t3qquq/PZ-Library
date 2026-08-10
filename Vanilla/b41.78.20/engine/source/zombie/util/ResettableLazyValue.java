// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
