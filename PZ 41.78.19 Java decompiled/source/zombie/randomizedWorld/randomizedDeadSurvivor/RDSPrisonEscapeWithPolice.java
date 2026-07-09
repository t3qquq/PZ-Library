// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.vehicles.BaseVehicle;

/**
 * 2-3 zombies in inmate jumpsuits with some duffel bags on them with ropes,  duct tape, etc.  Cops in the house too with a police car waiting outside.
 */
public final class RDSPrisonEscapeWithPolice extends RandomizedDeadSurvivorBase {
    public RDSPrisonEscapeWithPolice() {
        this.name = "Prison Escape with Police";
        this.setChance(2);
        this.setMaximumDays(90);
        this.setUnique(true);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        this.addZombies(def, Rand.Next(2, 4), "InmateEscaped", 0, roomDef);
        ArrayList arrayList0 = this.addZombies(def, Rand.Next(2, 4), "Police", null, roomDef);
        BaseVehicle vehicle = this.spawnCarOnNearestNav("Base.CarLightsPolice", def);
        if (vehicle != null) {
            vehicle.setAlarmed(false);
        }

        if (vehicle != null) {
            IsoGridSquare square = vehicle.getSquare().getCell().getGridSquare(vehicle.getSquare().x - 2, vehicle.getSquare().y - 2, 0);
            ArrayList arrayList1 = this.addZombiesOnSquare(3, "Police", null, square);
            if (!arrayList0.isEmpty()) {
                arrayList0.addAll(arrayList1);
                ((IsoZombie)arrayList0.get(Rand.Next(arrayList0.size()))).addItemToSpawnAtDeath(vehicle.createVehicleKey());
                def.bAlarmed = false;
            }
        }
    }
}
