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
import zombie.iso.objects.IsoDeadBody;

/**
 * This building will be 60% burnt (no fire started tho)  Add some burnt skeleton in it
 */
public final class RBBurntCorpse extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        def.setHasBeenVisited(true);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null && Rand.Next(100) < 60) {
                        square.Burn(false);
                    }
                }
            }
        }

        def.setAllExplored(true);
        def.bAlarmed = false;
        ArrayList arrayList = this.addZombies(def, Rand.Next(3, 7), null, null, null);
        if (arrayList != null) {
            for (int int3 = 0; int3 < arrayList.size(); int3++) {
                IsoZombie zombie0 = (IsoZombie)arrayList.get(int3);
                zombie0.setSkeleton(true);
                zombie0.getHumanVisual().setSkinTextureIndex(0);
                new IsoDeadBody(zombie0, false);
            }
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

    public RBBurntCorpse() {
        this.name = "Burnt with corpses";
        this.setChance(3);
    }
}
