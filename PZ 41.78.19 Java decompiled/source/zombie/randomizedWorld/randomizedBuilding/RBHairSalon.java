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
public final class RBHairSalon extends RandomizedBuildingBase {
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
                            if (Rand.NextBool(3)
                                && object.getSurfaceOffsetNoTable() > 0.0F
                                && square.getProperties().Val("waterAmount") == null
                                && !object.hasWater()
                                && object.getProperties().Val("BedType") == null) {
                                int int4 = Rand.Next(12);
                                switch (int4) {
                                    case 0:
                                        this.addWorldItem("Comb", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 1:
                                        this.addWorldItem("HairDyeBlonde", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 2:
                                        this.addWorldItem("HairDyeBlack", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 3:
                                        this.addWorldItem("HairDyeWhite", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                    case 4:
                                    default:
                                        break;
                                    case 5:
                                        this.addWorldItem("HairDyePink", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 6:
                                        this.addWorldItem("HairDyeYellow", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 7:
                                        this.addWorldItem("HairDyeRed", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 8:
                                        this.addWorldItem("HairDyeGinger", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 9:
                                        this.addWorldItem("Hairgel", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 10:
                                        this.addWorldItem("Hairspray", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                        break;
                                    case 11:
                                        this.addWorldItem("Razor", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean roomValid(IsoGridSquare sq) {
        return sq.getRoom() != null && "aesthetic".equals(sq.getRoom().getName());
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("aesthetic") != null || force;
    }

    public RBHairSalon() {
        this.name = "Hair Salon";
        this.setAlwaysDo(true);
    }
}
