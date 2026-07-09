// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;

/**
 * This building will be almost empty of loot, and lot of the doors/windows will be broken
 */
public final class RBLooted extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                            IsoObject object = square.getObjects().get(int3);
                            if (Rand.Next(100) >= 85 && object instanceof IsoDoor && ((IsoDoor)object).isExteriorDoor(null)) {
                                ((IsoDoor)object).destroy();
                            }

                            if (Rand.Next(100) >= 85 && object instanceof IsoWindow) {
                                ((IsoWindow)object).smashWindow(false, false);
                            }

                            if (object.getContainer() != null && object.getContainer().getItems() != null) {
                                for (int int4 = 0; int4 < object.getContainer().getItems().size(); int4++) {
                                    if (Rand.Next(100) < 80) {
                                        object.getContainer().getItems().remove(int4);
                                        int4--;
                                    }
                                }

                                ItemPickerJava.updateOverlaySprite(object);
                                object.getContainer().setExplored(true);
                            }
                        }
                    }
                }
            }
        }

        def.setAllExplored(true);
        def.bAlarmed = false;
    }

    public RBLooted() {
        this.name = "Looted";
        this.setChance(10);
    }
}
