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
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;

/**
 * This building will be barricaded, have a lot of canned food but also lot of zombies inside it
 */
public final class RBSafehouse extends RandomizedBuildingBase {
    @Override
    public void randomizeBuilding(BuildingDef def) {
        def.bAlarmed = false;
        def.setHasBeenVisited(true);
        ItemPickerJava.ItemPickerRoom itemPickerRoom = ItemPickerJava.rooms.get("SafehouseLoot");
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int0 = def.x - 1; int0 < def.x2 + 1; int0++) {
            for (int int1 = def.y - 1; int1 < def.y2 + 1; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square0 = cell.getGridSquare(int0, int1, int2);
                    if (square0 != null) {
                        for (int int3 = 0; int3 < square0.getObjects().size(); int3++) {
                            IsoObject object = square0.getObjects().get(int3);
                            if (object instanceof IsoDoor && ((IsoDoor)object).isBarricadeAllowed() && !SpawnPoints.instance.isSpawnBuilding(def)) {
                                IsoGridSquare square1 = square0.getRoom() == null ? square0 : ((IsoDoor)object).getOppositeSquare();
                                if (square1 != null && square1.getRoom() == null) {
                                    boolean boolean0 = square1 != square0;
                                    IsoBarricade barricade0 = IsoBarricade.AddBarricadeToObject((IsoDoor)object, boolean0);
                                    if (barricade0 != null) {
                                        int int4 = Rand.Next(1, 4);

                                        for (int int5 = 0; int5 < int4; int5++) {
                                            barricade0.addPlank(null, null);
                                        }

                                        if (GameServer.bServer) {
                                            barricade0.transmitCompleteItemToClients();
                                        }
                                    }
                                }
                            }

                            if (object instanceof IsoWindow) {
                                IsoGridSquare square2 = square0.getRoom() == null ? square0 : ((IsoWindow)object).getOppositeSquare();
                                if (((IsoWindow)object).isBarricadeAllowed() && int2 == 0 && square2 != null && square2.getRoom() == null) {
                                    boolean boolean1 = square2 != square0;
                                    IsoBarricade barricade1 = IsoBarricade.AddBarricadeToObject((IsoWindow)object, boolean1);
                                    if (barricade1 != null) {
                                        int int6 = Rand.Next(1, 4);

                                        for (int int7 = 0; int7 < int6; int7++) {
                                            barricade1.addPlank(null, null);
                                        }

                                        if (GameServer.bServer) {
                                            barricade1.transmitCompleteItemToClients();
                                        }
                                    }
                                } else {
                                    ((IsoWindow)object).addSheet(null);
                                    ((IsoWindow)object).HasCurtains().ToggleDoor(null);
                                }
                            }

                            if (object.getContainer() != null
                                && square0.getRoom() != null
                                && square0.getRoom().getBuilding().getDef() == def
                                && Rand.Next(100) <= 70
                                && square0.getRoom().getName() != null
                                && itemPickerRoom.Containers.containsKey(object.getContainer().getType())) {
                                object.getContainer().clear();
                                ItemPickerJava.fillContainerType(itemPickerRoom, object.getContainer(), "", null);
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
        this.addZombies(def);
    }

    private void addZombies(BuildingDef buildingDef) {
        this.addZombies(buildingDef, 0, null, null, null);
        if (Rand.Next(5) == 0) {
            this.addZombies(buildingDef, 1, "Survivalist", null, null);
        }

        if (Rand.Next(100) <= 60) {
            RandomizedBuildingBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
        }

        if (Rand.Next(100) <= 60) {
            RandomizedBuildingBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
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

    public RBSafehouse() {
        this.name = "Safehouse";
        this.setChance(10);
    }
}
