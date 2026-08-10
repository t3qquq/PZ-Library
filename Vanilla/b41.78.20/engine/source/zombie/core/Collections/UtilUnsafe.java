// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UtilUnsafe {
    private UtilUnsafe() {
    }

    public static Unsafe getUnsafe() {
        if (UtilUnsafe.class.getClassLoader() == null) {
            return Unsafe.getUnsafe();
        } else {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                return (Unsafe)field.get(UtilUnsafe.class);
            } catch (Exception exception) {
                throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", exception);
            }
        }
    }
}
