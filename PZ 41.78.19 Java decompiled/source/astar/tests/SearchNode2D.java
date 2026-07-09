// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.ASearchNode;
import astar.ISearchNode;
import java.util.ArrayList;

public class SearchNode2D extends ASearchNode {
    private int x;
    private int y;
    private SearchNode2D parent;
    private GoalNode2D goal;

    public SearchNode2D(int int0, int int1, SearchNode2D searchNode2D1, GoalNode2D goalNode2D) {
        this.x = int0;
        this.y = int1;
        this.parent = searchNode2D1;
        this.goal = goalNode2D;
    }

    public SearchNode2D getParent() {
        return this.parent;
    }

    public ArrayList<ISearchNode> getSuccessors() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SearchNode2D(this.x - 1, this.y, this, this.goal));
        arrayList.add(new SearchNode2D(this.x + 1, this.y, this, this.goal));
        arrayList.add(new SearchNode2D(this.x, this.y + 1, this, this.goal));
        arrayList.add(new SearchNode2D(this.x, this.y - 1, this, this.goal));
        return arrayList;
    }

    @Override
    public double h() {
        return this.dist(this.goal.getX(), this.goal.getY());
    }

    @Override
    public double c(ISearchNode iSearchNode) {
        SearchNode2D searchNode2D0 = this.castToSearchNode2D(iSearchNode);
        return 1.0;
    }

    @Override
    public void getSuccessors(ArrayList<ISearchNode> arrayList) {
        arrayList.add(new SearchNode2D(this.x - 1, this.y, this, this.goal));
        arrayList.add(new SearchNode2D(this.x + 1, this.y, this, this.goal));
        arrayList.add(new SearchNode2D(this.x, this.y + 1, this, this.goal));
        arrayList.add(new SearchNode2D(this.x, this.y - 1, this, this.goal));
    }

    @Override
    public void setParent(ISearchNode iSearchNode) {
        this.parent = this.castToSearchNode2D(iSearchNode);
    }

    @Override
    public boolean equals(Object object) {
        return !(object instanceof SearchNode2D searchNode2D0) ? false : this.x == searchNode2D0.getX() && this.y == searchNode2D0.getY();
    }

    @Override
    public int hashCode() {
        return 41 * (41 + this.x + this.y);
    }

    public double dist(int int1, int int0) {
        return Math.sqrt(Math.pow(this.x - int1, 2.0) + Math.pow(this.y - int0, 2.0));
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "(" + Integer.toString(this.x) + ";" + Integer.toString(this.y) + ";h:" + Double.toString(this.h()) + ";g:" + Double.toString(this.g()) + ")";
    }

    private SearchNode2D castToSearchNode2D(ISearchNode iSearchNode) {
        return (SearchNode2D)iSearchNode;
    }

    @Override
    public Integer keyCode() {
        return null;
    }
}
