// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Well, it's friday the 13th... Basically.
 */
public final class RDSHockeyPsycho extends RandomizedDeadSurvivorBase {
    public RDSHockeyPsycho() {
        this.name = "Hockey Psycho (friday 13th!)";
        this.setUnique(true);
        this.setChance(1);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        ArrayList arrayList = this.addZombies(def, 1, "HockeyPsycho", 0, roomDef);
        if (arrayList != null && !arrayList.isEmpty()) {
            IsoZombie zombie0 = (IsoZombie)arrayList.get(0);
            zombie0.addBlood(BloodBodyPartType.Head, true, true, true);

            for (int int0 = 0; int0 < 10; int0++) {
                zombie0.addBlood(null, true, false, true);
                zombie0.addDirt(null, Rand.Next(0, 3), true);
            }
        }

        for (int int1 = 0; int1 < 10; int1++) {
            IsoDeadBody deadBody = createRandomDeadBody(this.getRandomRoom(def, 2), Rand.Next(5, 20));
            if (deadBody != null) {
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
                this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
            }
        }

        def.bAlarmed = false;
    }
}
