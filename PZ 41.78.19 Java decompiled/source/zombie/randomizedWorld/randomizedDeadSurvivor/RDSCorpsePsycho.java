// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create corpses in a kitchen with one zombie as doctor
 */
public final class RDSCorpsePsycho extends RandomizedDeadSurvivorBase {
    public RDSCorpsePsycho() {
        this.name = "Corpse Psycho";
        this.setChance(1);
        this.setMinimumDays(120);
        this.setUnique(true);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getRoom(def, "kitchen");
        int int0 = Rand.Next(3, 7);

        for (int int1 = 0; int1 < int0; int1++) {
            IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, Rand.Next(5, 10));
            if (deadBody != null) {
                super.addBloodSplat(deadBody.getCurrentSquare(), Rand.Next(7, 12));
            }
        }

        ArrayList arrayList = super.addZombies(def, 1, "Doctor", null, roomDef);
        if (!arrayList.isEmpty()) {
            for (int int2 = 0; int2 < 8; int2++) {
                ((IsoZombie)arrayList.get(0)).addBlood(null, false, true, false);
            }

            def.bAlarmed = false;
        }
    }
}
