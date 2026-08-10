// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.hash;

import java.io.Serializable;

public class DefaultIntHashFunction implements IntHashFunction, Serializable {
    private static final long serialVersionUID = 1L;
    public static final IntHashFunction INSTANCE = new DefaultIntHashFunction();

    protected DefaultIntHashFunction() {
    }

    @Override
    public int hash(int int0) {
        return int0;
    }
}
