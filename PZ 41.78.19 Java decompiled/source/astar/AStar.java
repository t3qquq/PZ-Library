// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar;

import astar.datastructures.ClosedSet;
import astar.datastructures.ClosedSetHash;
import astar.datastructures.IClosedSet;
import astar.datastructures.IOpenSet;
import astar.datastructures.OpenSet;
import java.util.ArrayList;
import java.util.Comparator;

public class AStar {
    private int verbose = 0;
    private int maxSteps = -1;
    private int numSearchSteps;
    public ISearchNode bestNodeAfterSearch;
    private ArrayList<ISearchNode> successorNodes = new ArrayList<>();
    private final IOpenSet openSet = new OpenSet(new AStar.SearchNodeComparator());
    private final IClosedSet closedSetHash = new ClosedSetHash(new AStar.SearchNodeComparator());
    private final IClosedSet closedSetNoHash = new ClosedSet(new AStar.SearchNodeComparator());

    public ArrayList<ISearchNode> shortestPath(ISearchNode iSearchNode1, IGoalNode iGoalNode) {
        ISearchNode iSearchNode0 = this.search(iSearchNode1, iGoalNode);
        return iSearchNode0 == null ? null : path(iSearchNode0);
    }

    public ISearchNode search(ISearchNode iSearchNode0, IGoalNode iGoalNode) {
        boolean boolean0 = iSearchNode0.keyCode() != null;
        iSearchNode0.setDepth(0);
        this.openSet.clear();
        this.openSet.add(iSearchNode0);
        IClosedSet iClosedSet = boolean0 ? this.closedSetHash : this.closedSetNoHash;
        iClosedSet.clear();

        for (this.numSearchSteps = 0; this.openSet.size() > 0 && (this.maxSteps < 0 || this.numSearchSteps < this.maxSteps); this.numSearchSteps++) {
            ISearchNode iSearchNode1 = this.openSet.poll();
            if (iGoalNode.inGoal(iSearchNode1)) {
                this.bestNodeAfterSearch = iSearchNode1;
                return iSearchNode1;
            }

            this.successorNodes.clear();
            iSearchNode1.getSuccessors(this.successorNodes);

            for (int int0 = 0; int0 < this.successorNodes.size(); int0++) {
                ISearchNode iSearchNode2 = this.successorNodes.get(int0);
                if (!iClosedSet.contains(iSearchNode2)) {
                    ISearchNode iSearchNode3 = this.openSet.getNode(iSearchNode2);
                    boolean boolean1;
                    if (iSearchNode3 != null) {
                        iSearchNode2 = iSearchNode3;
                        boolean1 = true;
                    } else {
                        boolean1 = false;
                    }

                    double double0 = iSearchNode1.g() + iSearchNode1.c(iSearchNode2);
                    if (!boolean1 || !(double0 >= iSearchNode2.g())) {
                        iSearchNode2.setParent(iSearchNode1);
                        iSearchNode2.setDepth(iSearchNode1.getDepth() + 1);
                        if (boolean1) {
                            this.openSet.remove(iSearchNode2);
                            iSearchNode2.setG(double0);
                            this.openSet.add(iSearchNode2);
                        } else {
                            iSearchNode2.setG(double0);
                            this.openSet.add(iSearchNode2);
                        }
                    }
                }
            }

            iClosedSet.add(iSearchNode1);
        }

        this.bestNodeAfterSearch = iClosedSet.min();
        return null;
    }

    public static ArrayList<ISearchNode> path(ISearchNode iSearchNode0) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(iSearchNode0);
        ISearchNode iSearchNode1 = iSearchNode0;

        while (iSearchNode1.getParent() != null) {
            ISearchNode iSearchNode2 = iSearchNode1.getParent();
            arrayList.add(0, iSearchNode2);
            iSearchNode1 = iSearchNode2;
        }

        return arrayList;
    }

    public int numSearchSteps() {
        return this.numSearchSteps;
    }

    public ISearchNode bestNodeAfterSearch() {
        return this.bestNodeAfterSearch;
    }

    public void setMaxSteps(int int0) {
        this.maxSteps = int0;
    }

    static class SearchNodeComparator implements Comparator<ISearchNode> {
        public int compare(ISearchNode iSearchNode1, ISearchNode iSearchNode0) {
            return Double.compare(iSearchNode1.f(), iSearchNode0.f());
        }
    }
}
