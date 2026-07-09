// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;

/**
 * Create 1 to 2 zombies in the bathroom with some bathroom items on the ground
 */
public final class RDSBathroomZed extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> items = new ArrayList<>();

    public RDSBathroomZed() {
        this.name = "Bathroom Zed";
        this.setChance(12);
        this.items.add("Base.BathTowel");
        this.items.add("Base.Razor");
        this.items.add("Base.Lipstick");
        this.items.add("Base.Comb");
        this.items.add("Base.Hairspray");
        this.items.add("Base.Toothbrush");
        this.items.add("Base.Cologne");
        this.items.add("Base.Perfume");
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getRoom(def, "bathroom");
        int int0 = 1;
        if (roomDef.area > 6) {
            int0 = Rand.Next(1, 3);
        }

        this.addZombies(def, int0, Rand.Next(2) == 0 ? "Bathrobe" : "Naked", null, roomDef);
        this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(2, 5));
    }
}
