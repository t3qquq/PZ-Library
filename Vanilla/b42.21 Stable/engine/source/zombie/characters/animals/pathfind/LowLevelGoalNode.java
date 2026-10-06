// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.characters.animals.pathfind;

import astar.IGoalNode;
import astar.ISearchNode;

public final class LowLevelGoalNode implements IGoalNode {
    LowLevelSearchNode searchNode;

    @Override
    public boolean inGoal(ISearchNode other) {
        return other == this.searchNode;
    }
}
