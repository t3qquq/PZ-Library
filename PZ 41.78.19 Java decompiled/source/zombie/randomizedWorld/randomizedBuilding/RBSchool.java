// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;

/**
 * Add pen, pencils, books... on school desk
 */
public final class RBSchool extends RandomizedBuildingBase {
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
                            if (Rand.NextBool(3) && this.isTableFor3DItems(object, square)) {
                                int int4 = Rand.Next(0, 8);
                                switch (int4) {
                                    case 0:
                                        square.AddWorldInventoryItem(
                                            "Pen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 1:
                                        square.AddWorldInventoryItem(
                                            "Pencil", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 2:
                                        square.AddWorldInventoryItem(
                                            "Crayons", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 3:
                                        square.AddWorldInventoryItem(
                                            "RedPen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 4:
                                        square.AddWorldInventoryItem(
                                            "BluePen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 5:
                                        square.AddWorldInventoryItem(
                                            "Eraser", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                }

                                int int5 = Rand.Next(0, 6);
                                switch (int5) {
                                    case 0:
                                        square.AddWorldInventoryItem(
                                            "Doodle", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 1:
                                        square.AddWorldInventoryItem(
                                            "Book", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 2:
                                        square.AddWorldInventoryItem(
                                            "Notebook", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 3:
                                        square.AddWorldInventoryItem(
                                            "SheetPaper2", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                }
                            }
                        }

                        if (square.getRoom() != null && "classroom".equals(square.getRoom().getName())) {
                            if (Rand.NextBool(50)) {
                                int int6 = Rand.Next(0, 10);
                                switch (int6) {
                                    case 0:
                                        square.AddWorldInventoryItem("Doodle", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 1:
                                        square.AddWorldInventoryItem("Book", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 2:
                                        square.AddWorldInventoryItem("Notebook", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 3:
                                        square.AddWorldInventoryItem("SheetPaper2", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 4:
                                        square.AddWorldInventoryItem("Pen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 5:
                                        square.AddWorldInventoryItem("Pencil", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 6:
                                        square.AddWorldInventoryItem("Crayons", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 7:
                                        square.AddWorldInventoryItem("RedPen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 8:
                                        square.AddWorldInventoryItem("BluePen", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                        break;
                                    case 9:
                                        square.AddWorldInventoryItem("Eraser", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                                }
                            }

                            if (Rand.NextBool(120)) {
                                square.AddWorldInventoryItem("Bag_Schoolbag", Rand.Next(0.4F, 0.8F), Rand.Next(0.4F, 0.8F), 0.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean roomValid(IsoGridSquare sq) {
        return sq.getRoom() != null && "classroom".equals(sq.getRoom().getName());
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("classroom") != null || force;
    }

    public RBSchool() {
        this.name = "School";
        this.setAlwaysDo(true);
    }
}
