// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point3i extends Tuple3i implements Serializable {
    static final long serialVersionUID = 6149289077348153921L;

    public Point3i(int int0, int int1, int int2) {
        super(int0, int1, int2);
    }

    public Point3i(int[] ints) {
        super(ints);
    }

    public Point3i(Tuple3i tuple3i) {
        super(tuple3i);
    }

    public Point3i() {
    }
}
