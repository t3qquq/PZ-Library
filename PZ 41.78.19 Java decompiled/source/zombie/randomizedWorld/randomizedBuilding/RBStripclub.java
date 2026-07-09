// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;

/**
 * Add money/alcohol on table  Can also generate a rare male venue
 */
public final class RBStripclub extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        def.setHasBeenVisited(true);
        def.setAllExplored(true);
        IsoCell cell = IsoWorld.instance.CurrentCell;
        boolean boolean0 = Rand.NextBool(20);
        ArrayList arrayList = new ArrayList();

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = cell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                            IsoObject object = square.getObjects().get(int3);
                            if (Rand.NextBool(2) && "location_restaurant_pizzawhirled_01_16".equals(object.getSprite().getName())) {
                                int int4 = Rand.Next(1, 4);

                                for (int int5 = 0; int5 < int4; int5++) {
                                    square.AddWorldInventoryItem("Money", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                                }

                                int int6 = Rand.Next(1, 4);

                                for (int int7 = 0; int7 < int6; int7++) {
                                    int int8 = Rand.Next(1, 7);

                                    while (arrayList.contains(int8)) {
                                        int8 = Rand.Next(1, 7);
                                    }

                                    switch (int8) {
                                        case 1:
                                            square.AddWorldInventoryItem(
                                                boolean0 ? "Trousers" : "TightsFishnet_Ground", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F
                                            );
                                            arrayList.add(1);
                                            break;
                                        case 2:
                                            square.AddWorldInventoryItem("Vest_DefaultTEXTURE_TINT", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                                            arrayList.add(2);
                                            break;
                                        case 3:
                                            square.AddWorldInventoryItem(
                                                boolean0 ? "Jacket_Fireman" : "BunnySuitBlack", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F
                                            );
                                            arrayList.add(3);
                                            break;
                                        case 4:
                                            square.AddWorldInventoryItem(boolean0 ? "Hat_Cowboy" : "Garter", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                                            arrayList.add(4);
                                            break;
                                        case 5:
                                            if (!boolean0) {
                                                square.AddWorldInventoryItem("StockingsBlack", Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                                            }

                                            arrayList.add(5);
                                    }
                                }
                            }

                            if ("furniture_tables_high_01_16".equals(object.getSprite().getName())
                                || "furniture_tables_high_01_17".equals(object.getSprite().getName())
                                || "furniture_tables_high_01_18".equals(object.getSprite().getName())) {
                                int int9 = Rand.Next(1, 4);

                                for (int int10 = 0; int10 < int9; int10++) {
                                    square.AddWorldInventoryItem(
                                        "Money", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                    );
                                }

                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Cigarettes", square, object);
                                    if (Rand.NextBool(2)) {
                                        this.addWorldItem("Lighter", square, object);
                                    }
                                }

                                int int11 = Rand.Next(7);
                                switch (int11) {
                                    case 0:
                                        square.AddWorldInventoryItem(
                                            "WhiskeyFull", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 1:
                                        square.AddWorldInventoryItem(
                                            "Wine", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 2:
                                        square.AddWorldInventoryItem(
                                            "Wine2", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 3:
                                        square.AddWorldInventoryItem(
                                            "BeerCan", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                        break;
                                    case 4:
                                        square.AddWorldInventoryItem(
                                            "BeerBottle", Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), object.getSurfaceOffsetNoTable() / 96.0F
                                        );
                                }
                            }
                        }
                    }
                }
            }
        }

        RoomDef roomDef = def.getRoom("stripclub");
        if (boolean0) {
            this.addZombies(def, Rand.Next(2, 4), "WaiterStripper", 0, roomDef);
            this.addZombies(def, 1, "PoliceStripper", 0, roomDef);
            this.addZombies(def, 1, "FiremanStripper", 0, roomDef);
            this.addZombies(def, 1, "CowboyStripper", 0, roomDef);
            this.addZombies(def, Rand.Next(9, 15), null, 100, roomDef);
        } else {
            this.addZombies(def, Rand.Next(2, 4), "WaiterStripper", 100, roomDef);
            this.addZombies(def, Rand.Next(2, 5), "StripperNaked", 100, roomDef);
            this.addZombies(def, Rand.Next(2, 5), "StripperBlack", 100, roomDef);
            this.addZombies(def, Rand.Next(2, 5), "StripperWhite", 100, roomDef);
            this.addZombies(def, Rand.Next(9, 15), null, 0, roomDef);
        }
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("stripclub") != null || force;
    }

    public RBStripclub() {
        this.name = "Stripclub";
        this.setAlwaysDo(true);
    }
}
