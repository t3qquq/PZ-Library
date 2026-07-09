// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import astar.ISearchNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClosedSet implements IClosedSet {
    private ArrayList<ISearchNode> list = new ArrayList<>();
    private Comparator<ISearchNode> comp;

    public ClosedSet(Comparator<ISearchNode> comparator) {
        this.comp = comparator;
    }

    @Override
    public boolean contains(ISearchNode iSearchNode) {
        return this.list.contains(iSearchNode);
    }

    @Override
    public void add(ISearchNode iSearchNode) {
        this.list.add(iSearchNode);
    }

    @Override
    public ISearchNode min() {
        return Collections.min(this.list, this.comp);
    }

    @Override
    public void clear() {
        this.list.clear();
    }
}
