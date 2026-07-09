// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class PriorityQHeap extends PriorityQ {
    PriorityQ.PQnode[] nodes;
    PriorityQ.PQhandleElem[] handles;
    int size = 0;
    int max = 32;
    int freeList;
    boolean initialized;
    PriorityQ.Leq leq;

    PriorityQHeap(PriorityQ.Leq leqx) {
        this.nodes = new PriorityQ.PQnode[33];

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            this.nodes[int0] = new PriorityQ.PQnode();
        }

        this.handles = new PriorityQ.PQhandleElem[33];

        for (int int1 = 0; int1 < this.handles.length; int1++) {
            this.handles[int1] = new PriorityQ.PQhandleElem();
        }

        this.initialized = false;
        this.freeList = 0;
        this.leq = leqx;
        this.nodes[1].handle = 1;
        this.handles[1].key = null;
    }

    @Override
    void pqDeletePriorityQ() {
        this.handles = null;
        this.nodes = null;
    }

    void FloatDown(int int1) {
        PriorityQ.PQnode[] pQnodes = this.nodes;
        PriorityQ.PQhandleElem[] pQhandleElems = this.handles;
        int int0 = pQnodes[int1].handle;

        while (true) {
            int int2 = int1 << 1;
            if (int2 < this.size && LEQ(this.leq, pQhandleElems[pQnodes[int2 + 1].handle].key, pQhandleElems[pQnodes[int2].handle].key)) {
                int2++;
            }

            assert int2 <= this.max;

            int int3 = pQnodes[int2].handle;
            if (int2 > this.size || LEQ(this.leq, pQhandleElems[int0].key, pQhandleElems[int3].key)) {
                pQnodes[int1].handle = int0;
                pQhandleElems[int0].node = int1;
                return;
            }

            pQnodes[int1].handle = int3;
            pQhandleElems[int3].node = int1;
            int1 = int2;
        }
    }

    void FloatUp(int int1) {
        PriorityQ.PQnode[] pQnodes = this.nodes;
        PriorityQ.PQhandleElem[] pQhandleElems = this.handles;
        int int0 = pQnodes[int1].handle;

        while (true) {
            int int2 = int1 >> 1;
            int int3 = pQnodes[int2].handle;
            if (int2 == 0 || LEQ(this.leq, pQhandleElems[int3].key, pQhandleElems[int0].key)) {
                pQnodes[int1].handle = int0;
                pQhandleElems[int0].node = int1;
                return;
            }

            pQnodes[int1].handle = int3;
            pQhandleElems[int3].node = int1;
            int1 = int2;
        }
    }

    @Override
    boolean pqInit() {
        for (int int0 = this.size; int0 >= 1; int0--) {
            this.FloatDown(int0);
        }

        this.initialized = true;
        return true;
    }

    @Override
    int pqInsert(Object object) {
        int int0 = ++this.size;
        if (int0 * 2 > this.max) {
            PriorityQ.PQnode[] pQnodes0 = this.nodes;
            PriorityQ.PQhandleElem[] pQhandleElems0 = this.handles;
            this.max <<= 1;
            PriorityQ.PQnode[] pQnodes1 = new PriorityQ.PQnode[this.max + 1];
            System.arraycopy(this.nodes, 0, pQnodes1, 0, this.nodes.length);

            for (int int1 = this.nodes.length; int1 < pQnodes1.length; int1++) {
                pQnodes1[int1] = new PriorityQ.PQnode();
            }

            this.nodes = pQnodes1;
            if (this.nodes == null) {
                this.nodes = pQnodes0;
                return Integer.MAX_VALUE;
            }

            PriorityQ.PQhandleElem[] pQhandleElems1 = new PriorityQ.PQhandleElem[this.max + 1];
            System.arraycopy(this.handles, 0, pQhandleElems1, 0, this.handles.length);

            for (int int2 = this.handles.length; int2 < pQhandleElems1.length; int2++) {
                pQhandleElems1[int2] = new PriorityQ.PQhandleElem();
            }

            this.handles = pQhandleElems1;
            if (this.handles == null) {
                this.handles = pQhandleElems0;
                return Integer.MAX_VALUE;
            }
        }

        int int3;
        if (this.freeList == 0) {
            int3 = int0;
        } else {
            int3 = this.freeList;
            this.freeList = this.handles[int3].node;
        }

        this.nodes[int0].handle = int3;
        this.handles[int3].node = int0;
        this.handles[int3].key = object;
        if (this.initialized) {
            this.FloatUp(int0);
        }

        assert int3 != Integer.MAX_VALUE;

        return int3;
    }

    @Override
    Object pqExtractMin() {
        PriorityQ.PQnode[] pQnodes = this.nodes;
        PriorityQ.PQhandleElem[] pQhandleElems = this.handles;
        int int0 = pQnodes[1].handle;
        Object object = pQhandleElems[int0].key;
        if (this.size > 0) {
            pQnodes[1].handle = pQnodes[this.size].handle;
            pQhandleElems[pQnodes[1].handle].node = 1;
            pQhandleElems[int0].key = null;
            pQhandleElems[int0].node = this.freeList;
            this.freeList = int0;
            if (--this.size > 0) {
                this.FloatDown(1);
            }
        }

        return object;
    }

    @Override
    void pqDelete(int int0) {
        PriorityQ.PQnode[] pQnodes = this.nodes;
        PriorityQ.PQhandleElem[] pQhandleElems = this.handles;

        assert int0 >= 1 && int0 <= this.max && pQhandleElems[int0].key != null;

        int int1 = pQhandleElems[int0].node;
        pQnodes[int1].handle = pQnodes[this.size].handle;
        pQhandleElems[pQnodes[int1].handle].node = int1;
        if (int1 <= --this.size) {
            if (int1 > 1 && !LEQ(this.leq, pQhandleElems[pQnodes[int1 >> 1].handle].key, pQhandleElems[pQnodes[int1].handle].key)) {
                this.FloatUp(int1);
            } else {
                this.FloatDown(int1);
            }
        }

        pQhandleElems[int0].key = null;
        pQhandleElems[int0].node = this.freeList;
        this.freeList = int0;
    }

    @Override
    Object pqMinimum() {
        return this.handles[this.nodes[1].handle].key;
    }

    @Override
    boolean pqIsEmpty() {
        return this.size == 0;
    }
}
