// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

abstract class PriorityQ {
    public static final int INIT_SIZE = 32;

    public static boolean LEQ(PriorityQ.Leq var0, Object object1, Object object0) {
        return Geom.VertLeq((GLUvertex)object1, (GLUvertex)object0);
    }

    static PriorityQ pqNewPriorityQ(PriorityQ.Leq leq) {
        return new PriorityQSort(leq);
    }

    abstract void pqDeletePriorityQ();

    abstract boolean pqInit();

    abstract int pqInsert(Object var1);

    abstract Object pqExtractMin();

    abstract void pqDelete(int var1);

    abstract Object pqMinimum();

    abstract boolean pqIsEmpty();

    public interface Leq {
        boolean leq(Object var1, Object var2);
    }

    public static class PQhandleElem {
        Object key;
        int node;
    }

    public static class PQnode {
        int handle;
    }
}
