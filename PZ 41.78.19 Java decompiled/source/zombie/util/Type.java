// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public class Type {
    public static <R, I> R tryCastTo(I object, Class<R> clazz) {
        return (R)(clazz.isInstance(object) ? clazz.cast(object) : null);
    }

    public static boolean asBoolean(Object object) {
        return asBoolean(object, false);
    }

    public static boolean asBoolean(Object object, boolean boolean0) {
        if (object == null) {
            return boolean0;
        } else {
            Boolean boolean1 = tryCastTo(object, Boolean.class);
            return boolean1 == null ? boolean0 : boolean1;
        }
    }
}
