// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.IGoalNode;
import astar.ISearchNode;

public class GoalNodeCity implements IGoalNode {
    private String name;

    public GoalNodeCity(String string) {
        this.name = string;
    }

    @Override
    public boolean inGoal(ISearchNode iSearchNode) {
        return iSearchNode instanceof SearchNodeCity searchNodeCity ? this.name == searchNodeCity.getName() : false;
    }
}
