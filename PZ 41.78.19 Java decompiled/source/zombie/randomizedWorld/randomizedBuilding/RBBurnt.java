// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpawnPoints;

/**
 * This building will be 90% burnt (no fire started tho)
 */
public final class RBBurnt extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        def.setHasBeenVisited(true);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null && Rand.Next(100) < 90) {
                        square.Burn(false);
                    }
                }
            }
        }

        def.setAllExplored(true);
        def.bAlarmed = false;
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        if (!super.isValid(def, force)) {
            return false;
        } else {
            return def.getRooms().size() > 20 ? false : !SpawnPoints.instance.isSpawnBuilding(def);
        }
    }

    public RBBurnt() {
        this.name = "Burnt";
        this.setChance(3);
    }
}
