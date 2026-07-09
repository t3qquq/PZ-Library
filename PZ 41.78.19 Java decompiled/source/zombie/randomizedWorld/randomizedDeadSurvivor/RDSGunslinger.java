// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create a dead survivor somewhere with lot of modified guns/ammo on him
 */
public final class RDSGunslinger extends RandomizedDeadSurvivorBase {
    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        IsoGridSquare square = def.getFreeSquareInRoom();
        if (square != null) {
            IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(square.getX(), square.getY(), square.getZ(), null, 0);
            if (deadBody != null) {
                deadBody.setPrimaryHandItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, false, false));
                int int0 = Rand.Next(1, 4);

                for (int int1 = 0; int1 < int0; int1++) {
                    deadBody.getContainer().AddItem(super.addRandomRangedWeapon(deadBody.getContainer(), true, true, true));
                }
            }
        }
    }

    public RDSGunslinger() {
        this.name = "Gunslinger";
        this.setChance(5);
    }
}
