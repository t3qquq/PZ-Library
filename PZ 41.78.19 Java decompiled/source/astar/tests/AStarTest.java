// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.AStar;
import astar.ISearchNode;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class AStarTest {
    @Test
    public void SearchNodeTest2D() {
        GoalNode2D goalNode2D = new GoalNode2D(3, 3);
        SearchNode2D searchNode2D = new SearchNode2D(1, 1, null, goalNode2D);
        ArrayList arrayList = new AStar().shortestPath(searchNode2D, goalNode2D);
        Assert.assertEquals(arrayList.size(), 5L);
    }

    @Test
    public void SearchNodeCityTest() {
        SearchNodeCity searchNodeCity = new SearchNodeCity("Saarbr\u00c3\u00bccken");
        ArrayList arrayList = new AStar().shortestPath(searchNodeCity, new GoalNodeCity("W\u00c3\u00bcrzburg"));
        double double0 = 1.0E-5;
        Assert.assertEquals(((ISearchNode)arrayList.get(0)).f(), 222.0, double0);
        Assert.assertEquals(((ISearchNode)arrayList.get(1)).f(), 228.0, double0);
        Assert.assertEquals(((ISearchNode)arrayList.get(2)).f(), 269.0, double0);
        Assert.assertEquals(((ISearchNode)arrayList.get(3)).f(), 289.0, double0);
        Assert.assertEquals(arrayList.toString(), "[Saarbr\u00c3\u00bccken,f:222.0, Kaiserslautern,f:228.0, Frankfurt,f:269.0, W\u00c3\u00bcrzburg,f:289.0]");
    }
}
