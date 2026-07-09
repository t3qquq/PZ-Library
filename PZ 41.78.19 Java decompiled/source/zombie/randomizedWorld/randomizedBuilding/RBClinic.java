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
public final class RBClinic extends RandomizedBuildingBase {
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
                            if (Rand.NextBool(2)
                                && object.getSurfaceOffsetNoTable() > 0.0F
                                && object.getContainer() == null
                                && square.getProperties().Val("waterAmount") == null
                                && !object.hasWater()) {
                                int int4 = Rand.Next(1, 3);

                                for (int int5 = 0; int5 < int4; int5++) {
                                    int int6 = Rand.Next(12);
                                    switch (int6) {
                                        case 0:
                                            this.addWorldItem(
                                                "Scalpel", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 1:
                                            this.addWorldItem(
                                                "Bandage", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 2:
                                            this.addWorldItem(
                                                "Pills", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 3:
                                            this.addWorldItem(
                                                "AlcoholWipes", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 4:
                                            this.addWorldItem(
                                                "Bandaid", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 5:
                                            this.addWorldItem(
                                                "CottonBalls", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 6:
                                            this.addWorldItem(
                                                "Disinfectant", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 7:
                                            this.addWorldItem(
                                                "SutureNeedle", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 8:
                                            this.addWorldItem(
                                                "SutureNeedleHolder",
                                                square,
                                                Rand.Next(0.4F, 0.6F),
                                                Rand.Next(0.4F, 0.6F),
                                                object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 9:
                                            this.addWorldItem(
                                                "Tweezers", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 10:
                                            this.addWorldItem(
                                                "Gloves_Surgical",
                                                square,
                                                Rand.Next(0.4F, 0.6F),
                                                Rand.Next(0.4F, 0.6F),
                                                object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                            break;
                                        case 11:
                                            this.addWorldItem(
                                                "Hat_SurgicalMask_Blue",
                                                square,
                                                Rand.Next(0.4F, 0.6F),
                                                Rand.Next(0.4F, 0.6F),
                                                object.getSurfaceOffsetNoTable() / 96.0F
                                            );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean roomValid(IsoGridSquare sq) {
        return sq.getRoom() != null
            && ("hospitalroom".equals(sq.getRoom().getName()) || "clinic".equals(sq.getRoom().getName()) || "medical".equals(sq.getRoom().getName()));
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("medical") != null || def.getRoom("clinic") != null || force;
    }

    public RBClinic() {
        this.name = "Clinic (Vet, Doctor..)";
        this.setAlwaysDo(true);
    }
}
