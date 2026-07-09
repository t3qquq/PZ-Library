// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.util.io;

import java.io.IOException;

public final class HuffmanNode {
    private HuffmanNode parent;
    private int depth = 0;
    protected HuffmanNode o0;
    protected HuffmanNode o1;
    protected Integer value;
    private boolean full = false;

    public HuffmanNode() {
        this(null);
    }

    protected HuffmanNode(HuffmanNode huffmanNode1) {
        this.parent = huffmanNode1;
        if (huffmanNode1 != null) {
            this.depth = huffmanNode1.getDepth() + 1;
        }
    }

    protected HuffmanNode(HuffmanNode huffmanNode1, int int0) {
        this(huffmanNode1);
        this.value = new Integer(int0);
        this.full = true;
    }

    protected int read(BitInputStream bitInputStream) throws IOException {
        HuffmanNode huffmanNode0 = this;

        while (huffmanNode0.value == null) {
            huffmanNode0 = bitInputStream.getBit() ? huffmanNode0.o1 : huffmanNode0.o0;
        }

        return huffmanNode0.value;
    }

    protected HuffmanNode get0() {
        return this.o0 == null ? this.set0(new HuffmanNode(this)) : this.o0;
    }

    protected HuffmanNode get1() {
        return this.o1 == null ? this.set1(new HuffmanNode(this)) : this.o1;
    }

    protected Integer getValue() {
        return this.value;
    }

    private HuffmanNode getParent() {
        return this.parent;
    }

    protected int getDepth() {
        return this.depth;
    }

    private boolean isFull() {
        return this.full ? true : (this.full = this.o0 != null && this.o0.isFull() && this.o1 != null && this.o1.isFull());
    }

    private HuffmanNode set0(HuffmanNode huffmanNode0) {
        return this.o0 = huffmanNode0;
    }

    private HuffmanNode set1(HuffmanNode huffmanNode0) {
        return this.o1 = huffmanNode0;
    }

    private void setValue(Integer integer) {
        this.full = true;
        this.value = integer;
    }

    public boolean setNewValue(int int0, int int1) {
        if (this.isFull()) {
            return false;
        } else if (int0 == 1) {
            if (this.o0 == null) {
                this.set0(new HuffmanNode(this, int1));
                return true;
            } else if (this.o1 == null) {
                this.set1(new HuffmanNode(this, int1));
                return true;
            } else {
                return false;
            }
        } else {
            return this.get0().setNewValue(int0 - 1, int1) ? true : this.get1().setNewValue(int0 - 1, int1);
        }
    }
}
