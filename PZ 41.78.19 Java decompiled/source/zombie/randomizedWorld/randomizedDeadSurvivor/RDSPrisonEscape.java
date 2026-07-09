// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;

/**
 * 2-3 zombies in inmate jumpsuits with some duffel bags on them with ropes,  duct tape, etc.
 */
public final class RDSPrisonEscape extends RandomizedDeadSurvivorBase {
    public RDSPrisonEscape() {
        this.name = "Prison Escape";
        this.setChance(3);
        this.setMaximumDays(90);
        this.setUnique(true);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        this.addZombies(def, Rand.Next(2, 4), "InmateEscaped", 0, roomDef);
        def.bAlarmed = false;
    }
}
