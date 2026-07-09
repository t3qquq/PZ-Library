// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpawnPoints;

/**
 * Building full of toilet paper
 */
public final class RBOther extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        def.setHasBeenVisited(true);
        def.setAllExplored(true);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                            IsoObject object = square.getObjects().get(int3);
                            if (object.getContainer() != null) {
                                object.getContainer().emptyIt();
                                object.getContainer().AddItems("Base.ToiletPaper", Rand.Next(10, 30));
                                ItemPickerJava.updateOverlaySprite(object);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isValid(BuildingDef buildingDef, boolean boolean0) {
        if (!super.isValid(buildingDef, boolean0)) {
            return false;
        } else {
            return buildingDef.getRooms().size() > 10 ? false : !SpawnPoints.instance.isSpawnBuilding(buildingDef);
        }
    }

    public RBOther() {
        this.name = "Other";
        this.setChance(1);
        this.setUnique(true);
    }
}
