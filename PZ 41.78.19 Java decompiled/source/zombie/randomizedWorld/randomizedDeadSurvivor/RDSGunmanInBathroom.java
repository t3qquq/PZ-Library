// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Just a dead survivor in a bathroom with pistol or shotgun on him
 */
public final class RDSGunmanInBathroom extends RandomizedDeadSurvivorBase {
    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = super.getRoom(def, "bathroom");
        IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, Rand.Next(5, 10));
        if (deadBody != null) {
            deadBody.setPrimaryHandItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, false, false));
            int int0 = Rand.Next(1, 4);

            for (int int1 = 0; int1 < int0; int1++) {
                deadBody.getContainer().AddItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, true, true));
            }
        }
    }

    public RDSGunmanInBathroom() {
        this.name = "Bathroom Gunman";
        this.setChance(5);
    }
}
