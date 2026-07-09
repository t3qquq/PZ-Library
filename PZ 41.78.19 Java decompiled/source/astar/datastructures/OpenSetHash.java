// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import astar.ISearchNode;
import java.util.Comparator;

public class OpenSetHash implements IOpenSet {
    private HashPriorityQueue<Integer, ISearchNode> hashQ;
    private Comparator<ISearchNode> comp;

    public OpenSetHash(Comparator<ISearchNode> comparator) {
        this.hashQ = new HashPriorityQueue<>(comparator);
        this.comp = comparator;
    }

    @Override
    public void add(ISearchNode iSearchNode) {
        this.hashQ.add(iSearchNode.keyCode(), iSearchNode);
    }

    @Override
    public void remove(ISearchNode iSearchNode) {
        this.hashQ.remove(iSearchNode.keyCode(), iSearchNode);
    }

    @Override
    public ISearchNode poll() {
        return this.hashQ.poll();
    }

    @Override
    public ISearchNode getNode(ISearchNode iSearchNode) {
        return this.hashQ.get(iSearchNode.keyCode());
    }

    @Override
    public int size() {
        return this.hashQ.size();
    }

    @Override
    public String toString() {
        return this.hashQ.getTreeMap().keySet().toString();
    }

    @Override
    public void clear() {
        this.hashQ.clear();
    }
}
