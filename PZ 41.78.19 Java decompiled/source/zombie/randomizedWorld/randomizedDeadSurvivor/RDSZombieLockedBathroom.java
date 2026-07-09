// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.network.GameServer;

/**
 * Zombie inside the barricaded bathroom and a dead corpse in front of it with a pistol
 */
public final class RDSZombieLockedBathroom extends RandomizedDeadSurvivorBase {
    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        IsoDeadBody deadBody = null;

        for (int int0 = 0; int0 < def.rooms.size(); int0++) {
            RoomDef roomDef = def.rooms.get(int0);
            IsoGridSquare square0 = null;
            if ("bathroom".equals(roomDef.name)) {
                if (IsoWorld.getZombiesEnabled()) {
                    IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(roomDef.getX(), roomDef.getY(), roomDef.getZ());
                    if (square1 != null && square1.getRoom() != null) {
                        IsoRoom room = square1.getRoom();
                        square1 = room.getRandomFreeSquare();
                        if (square1 != null) {
                            VirtualZombieManager.instance.choices.clear();
                            VirtualZombieManager.instance.choices.add(square1);
                            IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
                            ZombieSpawnRecorder.instance.record(zombie0, this.getClass().getSimpleName());
                        }
                    }
                }

                for (int int1 = roomDef.x - 1; int1 < roomDef.x2 + 1; int1++) {
                    for (int int2 = roomDef.y - 1; int2 < roomDef.y2 + 1; int2++) {
                        square0 = IsoWorld.instance.getCell().getGridSquare(int1, int2, roomDef.getZ());
                        if (square0 != null) {
                            IsoDoor door = square0.getIsoDoor();
                            if (door != null && this.isDoorToRoom(door, roomDef)) {
                                if (door.IsOpen()) {
                                    door.ToggleDoor(null);
                                }

                                IsoBarricade barricade = IsoBarricade.AddBarricadeToObject(door, square0.getRoom().def == roomDef);
                                if (barricade != null) {
                                    barricade.addPlank(null, null);
                                    if (GameServer.bServer) {
                                        barricade.transmitCompleteItemToClients();
                                    }
                                }

                                deadBody = this.addDeadBodyTheOtherSide(door);
                                break;
                            }
                        }
                    }

                    if (deadBody != null) {
                        break;
                    }
                }

                if (deadBody != null) {
                    deadBody.setPrimaryHandItem(super.addWeapon("Base.Pistol", true));
                }

                return;
            }
        }
    }

    private boolean isDoorToRoom(IsoDoor door, RoomDef roomDef) {
        if (door != null && roomDef != null) {
            IsoGridSquare square0 = door.getSquare();
            IsoGridSquare square1 = door.getOppositeSquare();
            return square0 != null && square1 != null ? square0.getRoomID() == roomDef.ID != (square1.getRoomID() == roomDef.ID) : false;
        } else {
            return false;
        }
    }

    private boolean checkIsBathroom(IsoGridSquare square) {
        return square.getRoom() != null && "bathroom".equals(square.getRoom().getName());
    }

    private IsoDeadBody addDeadBodyTheOtherSide(IsoDoor door) {
        Object object = null;
        if (door.north) {
            object = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)door.getY(), (double)door.getZ());
            if (this.checkIsBathroom((IsoGridSquare)object)) {
                object = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)(door.getY() - 1.0F), (double)door.getZ());
            }
        } else {
            object = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)door.getY(), (double)door.getZ());
            if (this.checkIsBathroom((IsoGridSquare)object)) {
                object = IsoWorld.instance.getCell().getGridSquare((double)(door.getX() - 1.0F), (double)door.getY(), (double)door.getZ());
            }
        }

        return RandomizedDeadSurvivorBase.createRandomDeadBody(
            ((IsoGridSquare)object).getX(), ((IsoGridSquare)object).getY(), ((IsoGridSquare)object).getZ(), null, Rand.Next(5, 10)
        );
    }

    public RDSZombieLockedBathroom() {
        this.name = "Locked in Bathroom";
        this.setChance(5);
    }
}
