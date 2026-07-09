// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.IGoalNode;
import astar.ISearchNode;

public class GoalNode2D implements IGoalNode {
    private int x;
    private int y;

    public GoalNode2D(int int0, int int1) {
        this.x = int0;
        this.y = int1;
    }

    @Override
    public boolean inGoal(ISearchNode iSearchNode) {
        return !(iSearchNode instanceof SearchNode2D searchNode2D) ? false : this.x == searchNode2D.getX() && this.y == searchNode2D.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
