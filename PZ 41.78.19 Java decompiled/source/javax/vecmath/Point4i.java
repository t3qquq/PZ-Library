// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point4i extends Tuple4i implements Serializable {
    static final long serialVersionUID = 620124780244617983L;

    public Point4i(int int0, int int1, int int2, int int3) {
        super(int0, int1, int2, int3);
    }

    public Point4i(int[] ints) {
        super(ints);
    }

    public Point4i(Tuple4i tuple4i) {
        super(tuple4i);
    }

    public Point4i() {
    }
}
