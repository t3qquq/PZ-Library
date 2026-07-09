// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.ASearchNode;
import astar.ISearchNode;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchNodeCity extends ASearchNode {
    private String name;
    private SearchNodeCity parent;
    private SearchNodeCity goal;
    private HashMap<String, HashMap<String, Integer>> adjacencyMatrix = new HashMap<>();

    public SearchNodeCity(String string) {
        this.name = string;
        this.fillAdjacencyMatrix();
    }

    private void fillAdjacencyMatrix() {
        HashMap hashMap0 = new HashMap();
        hashMap0.put("Kaiserslautern", 70);
        hashMap0.put("Karlsruhe", 145);
        HashMap hashMap1 = new HashMap();
        hashMap1.put("Frankfurt", 103);
        hashMap1.put("Ludwigshafen", 53);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("Heilbronn", 84);
        HashMap hashMap3 = new HashMap();
        hashMap3.put("W\u00c3\u00bcrzburg", 116);
        HashMap hashMap4 = new HashMap();
        hashMap4.put("W\u00c3\u00bcrzburg", 183);
        HashMap hashMap5 = new HashMap();
        hashMap5.put("W\u00c3\u00bcrzburg", 102);
        this.adjacencyMatrix.put("Saarbr\u00c3\u00bccken", hashMap0);
        this.adjacencyMatrix.put("Kaiserslautern", hashMap1);
        this.adjacencyMatrix.put("Frankfurt", hashMap3);
        this.adjacencyMatrix.put("Karlsruhe", hashMap2);
        this.adjacencyMatrix.put("Ludwigshafen", hashMap4);
        this.adjacencyMatrix.put("Heilbronn", hashMap5);
    }

    @Override
    public double h() {
        String string = this.name;
        switch (string) {
            case "Saarbr\u00c3\u00bccken":
                return 222.0;
            case "Kaiserslautern":
                return 158.0;
            case "Karlsruhe":
                return 140.0;
            case "Frankfurt":
                return 96.0;
            case "Ludwigshafen":
                return 108.0;
            case "Heilbronn":
                return 87.0;
            case "W\u00c3\u00bcrzburg":
                return 0.0;
            default:
                return 0.0;
        }
    }

    @Override
    public double c(ISearchNode iSearchNode) {
        return this.adjacencyMatrix.get(this.name).get(this.castToSearchNodeCity(iSearchNode).getName()).intValue();
    }

    @Override
    public void getSuccessors(ArrayList<ISearchNode> arrayList) {
        for (String string : this.adjacencyMatrix.get(this.name).keySet()) {
            arrayList.add(new SearchNodeCity(string));
        }
    }

    public ArrayList<ISearchNode> getSuccessors() {
        ArrayList arrayList = new ArrayList();

        for (String string : this.adjacencyMatrix.get(this.name).keySet()) {
            arrayList.add(new SearchNodeCity(string));
        }

        return arrayList;
    }

    public SearchNodeCity getParent() {
        return this.parent;
    }

    @Override
    public void setParent(ISearchNode iSearchNode) {
        this.parent = this.castToSearchNodeCity(iSearchNode);
    }

    @Override
    public Integer keyCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SearchNodeCity searchNodeCity0 ? this.name == searchNodeCity0.getName() : false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public String getName() {
        return this.name;
    }

    private SearchNodeCity castToSearchNodeCity(ISearchNode iSearchNode) {
        return (SearchNodeCity)iSearchNode;
    }

    @Override
    public String toString() {
        return this.name + ",f:" + this.f();
    }
}
