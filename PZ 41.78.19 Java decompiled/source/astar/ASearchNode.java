// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar;

public abstract class ASearchNode implements ISearchNode {
    private double g = 0.0;
    private int depth;

    @Override
    public double f() {
        return this.g() + this.h();
    }

    @Override
    public double g() {
        return this.g;
    }

    @Override
    public void setG(double double0) {
        this.g = double0;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    @Override
    public void setDepth(int int0) {
        this.depth = int0;
    }
}
