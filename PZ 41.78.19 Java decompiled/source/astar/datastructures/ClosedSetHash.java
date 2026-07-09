// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import astar.ISearchNode;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Comparator;

public class ClosedSetHash implements IClosedSet {
    private TIntObjectHashMap<ISearchNode> hashMap = new TIntObjectHashMap<>();
    private Comparator<ISearchNode> comp;
    private static final ClosedSetHash.MinNodeProc minNodeProc = new ClosedSetHash.MinNodeProc();

    public ClosedSetHash(Comparator<ISearchNode> comparator) {
        this.comp = comparator;
    }

    @Override
    public boolean contains(ISearchNode iSearchNode) {
        return this.hashMap.containsKey(iSearchNode.keyCode());
    }

    @Override
    public void add(ISearchNode iSearchNode) {
        this.hashMap.put(iSearchNode.keyCode(), iSearchNode);
    }

    @Override
    public ISearchNode min() {
        minNodeProc.comp = this.comp;
        minNodeProc.candidate = null;
        this.hashMap.forEachValue(minNodeProc);
        return minNodeProc.candidate;
    }

    @Override
    public void clear() {
        this.hashMap.clear();
    }

    private static final class MinNodeProc implements TObjectProcedure<ISearchNode> {
        Comparator<ISearchNode> comp;
        ISearchNode candidate;

        public boolean execute(ISearchNode iSearchNode) {
            if (this.candidate == null) {
                this.candidate = iSearchNode;
                return true;
            } else {
                if (this.comp.compare(iSearchNode, this.candidate) < 0) {
                    this.candidate = iSearchNode;
                }

                return true;
            }
        }
    }
}
