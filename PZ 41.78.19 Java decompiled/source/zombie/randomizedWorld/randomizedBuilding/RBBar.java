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
public final class RBBar extends RandomizedBuildingBase {
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
                            if (object.getSprite() != null
                                && object.getSprite().getName() != null
                                && (object.getSprite().getName().equals("recreational_01_6") || object.getSprite().getName().equals("recreational_01_7"))) {
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("PoolBall", square, object);
                                }

                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Poolcue", square, object);
                                }
                            } else if (object.isTableSurface() && Rand.NextBool(2)) {
                                if (Rand.NextBool(3)) {
                                    this.addWorldItem("Cigarettes", square, object);
                                    if (Rand.NextBool(2)) {
                                        this.addWorldItem("Lighter", square, object);
                                    }
                                }

                                int int4 = Rand.Next(7);
                                switch (int4) {
                                    case 0:
                                        this.addWorldItem("WhiskeyFull", square, object);
                                        break;
                                    case 1:
                                        this.addWorldItem("Wine", square, object);
                                        break;
                                    case 2:
                                        this.addWorldItem("Wine2", square, object);
                                        break;
                                    case 3:
                                        this.addWorldItem("BeerCan", square, object);
                                        break;
                                    case 4:
                                        this.addWorldItem("BeerBottle", square, object);
                                }

                                if (Rand.NextBool(3)) {
                                    int int5 = Rand.Next(7);
                                    switch (int5) {
                                        case 0:
                                            this.addWorldItem("Crisps", square, object);
                                            break;
                                        case 1:
                                            this.addWorldItem("Crisps2", square, object);
                                            break;
                                        case 2:
                                            this.addWorldItem("Crisps3", square, object);
                                            break;
                                        case 3:
                                            this.addWorldItem("Crisps4", square, object);
                                            break;
                                        case 4:
                                            this.addWorldItem("Peanuts", square, object);
                                    }
                                }

                                if (Rand.NextBool(4)) {
                                    this.addWorldItem("CardDeck", square, object);
                                }
                            }
                        }

                        if (Rand.NextBool(20)
                            && square.getRoom() != null
                            && square.getRoom().getName().equals("bar")
                            && square.getObjects().size() == 1
                            && Rand.NextBool(8)) {
                            this.addWorldItem("Dart", square, null);
                        }
                    }
                }
            }
        }
    }

    public boolean roomValid(IsoGridSquare sq) {
        return sq.getRoom() != null && "bar".equals(sq.getRoom().getName());
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        return def.getRoom("bar") != null && def.getRoom("stripclub") == null || force;
    }

    public RBBar() {
        this.name = "Bar";
        this.setAlwaysDo(true);
    }
}
