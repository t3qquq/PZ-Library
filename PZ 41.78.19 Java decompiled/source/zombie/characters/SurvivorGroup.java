// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import zombie.iso.BuildingDef;

public final class SurvivorGroup {
    public final ArrayList<SurvivorDesc> Members = new ArrayList<>();
    public String Order;
    public BuildingDef Safehouse;

    public void addMember(SurvivorDesc member) {
    }

    public void removeMember(SurvivorDesc member) {
    }

    public SurvivorDesc getLeader() {
        return null;
    }

    public boolean isLeader(SurvivorDesc member) {
        return false;
    }
}
