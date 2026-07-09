// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;

/**
 * Add some food on table
 */
public final class RBSpiffo extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null && this.roomValid(square)) {
                        for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                            IsoObject object = square.getObjects().get(int3);
                            if (Rand.NextBool(2) && this.isTableFor3DItems(object, square)) {
                                System.out.println("adding item on table " + object.getSprite().getName() + " coords: " + square.x + "," + square.y);
                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Burger", square, object);
                                }

                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Fries", square, object);
                                }

                                if (Rand.NextBool(2)) {
                                    this.addWorldItem("Ketchup", square, object);
                                }

                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Fork", square, object);
                                }

                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("ButterKnife", square, object);
                                }

                                if (Rand.NextBool(30)) {
                                    this.addWorldItem("MugSpiffo", square, object);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean roomValid(IsoGridSquare sq) {
        return sq.getRoom() != null && ("spiffo_dining".equals(sq.getRoom().getName()) || "burgerkitchen".equals(sq.getRoom().getName()));
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("spiffo_dining") != null || def.getRoom("burgerkitchen") != null || force;
    }

    public RBSpiffo() {
        this.name = "Spiffo Restaurant";
        this.setAlwaysDo(true);
    }
}
