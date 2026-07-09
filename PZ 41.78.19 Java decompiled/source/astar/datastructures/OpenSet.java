// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import astar.ISearchNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class OpenSet implements IOpenSet {
    private PriorityQueue<ISearchNode> Q;
    private ArrayList<ISearchNode> QA;

    public OpenSet(Comparator<ISearchNode> comparator) {
        this.Q = new PriorityQueue<>(1000, comparator);
        this.QA = new ArrayList<>(1000);
    }

    @Override
    public void add(ISearchNode iSearchNode) {
        this.Q.add(iSearchNode);
        this.QA.add(iSearchNode);
    }

    @Override
    public void remove(ISearchNode iSearchNode) {
        this.Q.remove(iSearchNode);
        this.QA.remove(iSearchNode);
    }

    @Override
    public ISearchNode poll() {
        return this.Q.poll();
    }

    @Override
    public ISearchNode getNode(ISearchNode iSearchNode1) {
        ArrayList arrayList = this.QA;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ISearchNode iSearchNode0 = (ISearchNode)arrayList.get(int0);
            if (iSearchNode0.equals(iSearchNode1)) {
                return iSearchNode0;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return this.Q.size();
    }

    @Override
    public void clear() {
        this.Q.clear();
        this.QA.clear();
    }
}
