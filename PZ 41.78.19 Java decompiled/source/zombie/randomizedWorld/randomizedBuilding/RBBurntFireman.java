// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpawnPoints;
import zombie.vehicles.BaseVehicle;

/**
 * This building will be 70% burnt (no fire started tho)  Also spawn 1 to 3 fireman zombies inside it (65% of them to be male)
 */
public final class RBBurntFireman extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        int int0 = Rand.Next(1, 4);
        def.setHasBeenVisited(true);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int1 = def.x - 1; int1 < def.x2 + 1; int1++) {
            for (int int2 = def.y - 1; int2 < def.y2 + 1; int2++) {
                for (int int3 = 0; int3 < 8; int3++) {
                    IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
                    if (square != null && Rand.Next(100) < 70) {
                        square.Burn(false);
                    }
                }
            }
        }

        def.setAllExplored(true);
        ArrayList arrayList = this.addZombies(def, int0, "FiremanFullSuit", 35, this.getLivingRoomOrKitchen(def));

        for (int int4 = 0; int4 < arrayList.size(); int4++) {
            ((IsoZombie)arrayList.get(int4)).getInventory().setExplored(true);
        }

        BaseVehicle vehicle;
        if (Rand.NextBool(2)) {
            vehicle = this.spawnCarOnNearestNav("Base.PickUpVanLightsFire", def);
        } else {
            vehicle = this.spawnCarOnNearestNav("Base.PickUpTruckLightsFire", def);
        }

        if (vehicle != null) {
            vehicle.setAlarmed(false);
        }

        if (vehicle != null && !arrayList.isEmpty()) {
            ((IsoZombie)arrayList.get(Rand.Next(arrayList.size()))).addItemToSpawnAtDeath(vehicle.createVehicleKey());
        }
    }

    @Override
    public boolean isValid(BuildingDef buildingDef, boolean boolean0) {
        if (!super.isValid(buildingDef, boolean0)) {
            return false;
        } else {
            return buildingDef.getRooms().size() > 20 ? false : !SpawnPoints.instance.isSpawnBuilding(buildingDef);
        }
    }

    public RBBurntFireman() {
        this.name = "Burnt Fireman";
        this.setChance(2);
    }
}
