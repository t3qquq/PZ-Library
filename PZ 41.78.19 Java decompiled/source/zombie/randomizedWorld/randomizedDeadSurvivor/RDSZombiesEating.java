// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create a dead survivor in the livingroom with zombies eating him
 */
public final class RDSZombiesEating extends RandomizedDeadSurvivorBase {
    public RDSZombiesEating() {
        this.name = "Eating zombies";
        this.setChance(7);
        this.setMaximumDays(60);
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return IsoWorld.getZombiesEnabled() && super.isValid(def, force);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef0 = this.getLivingRoomOrKitchen(def);
        IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef0, Rand.Next(5, 10));
        if (deadBody != null) {
            VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
            RoomDef roomDef1 = this.getRoom(def, "kitchen");
            RoomDef roomDef2 = this.getRoom(def, "livingroom");
            if ("kitchen".equals(roomDef0.name) && roomDef2 != null && Rand.Next(3) == 0) {
                deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef2, Rand.Next(5, 10));
                if (deadBody == null) {
                    return;
                }

                VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
            }

            if ("livingroom".equals(roomDef0.name) && roomDef1 != null && Rand.Next(3) == 0) {
                deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef1, Rand.Next(5, 10));
                if (deadBody == null) {
                    return;
                }

                VirtualZombieManager.instance.createEatingZombies(deadBody, Rand.Next(1, 3));
            }

            def.bAlarmed = false;
        }
    }
}
