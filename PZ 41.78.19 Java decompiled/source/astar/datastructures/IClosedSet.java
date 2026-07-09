// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import astar.ISearchNode;

public interface IClosedSet {
    boolean contains(ISearchNode var1);

    void add(ISearchNode var1);

    ISearchNode min();

    void clear();
}
