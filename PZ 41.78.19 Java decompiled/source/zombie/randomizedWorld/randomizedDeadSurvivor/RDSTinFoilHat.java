// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;

/**
 * Zombies family with tin foil hat
 */
public final class RDSTinFoilHat extends RandomizedDeadSurvivorBase {
    public RDSTinFoilHat() {
        this.name = "Tin foil hat family";
        this.setUnique(true);
        this.setChance(2);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        this.addZombies(def, Rand.Next(2, 5), "TinFoilHat", null, roomDef);
    }
}
