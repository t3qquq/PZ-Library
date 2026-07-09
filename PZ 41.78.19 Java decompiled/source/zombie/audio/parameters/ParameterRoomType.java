// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.BuildingDef;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;

public final class ParameterRoomType extends FMODGlobalParameter {
    static ParameterRoomType instance;
    static ParameterRoomType.RoomType roomType = null;

    public ParameterRoomType() {
        super("RoomType");
        instance = this;
    }

    @Override
    public float calculateCurrentValue() {
        return this.getRoomType().label;
    }

    private ParameterRoomType.RoomType getRoomType() {
        if (roomType != null) {
            return roomType;
        } else {
            IsoGameCharacter character = this.getCharacter();
            if (character == null) {
                return ParameterRoomType.RoomType.Generic;
            } else {
                BuildingDef buildingDef = character.getCurrentBuildingDef();
                if (buildingDef == null) {
                    return ParameterRoomType.RoomType.Generic;
                } else {
                    IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
                    IsoMetaCell metaCell = metaGrid.getCellData(PZMath.fastfloor(character.x / 300.0F), PZMath.fastfloor(character.y / 300.0F));
                    if (metaCell != null && !metaCell.roomTones.isEmpty()) {
                        RoomDef roomDef0 = character.getCurrentRoomDef();
                        IsoMetaGrid.RoomTone roomTone0 = null;

                        for (int int0 = 0; int0 < metaCell.roomTones.size(); int0++) {
                            IsoMetaGrid.RoomTone roomTone1 = metaCell.roomTones.get(int0);
                            RoomDef roomDef1 = metaGrid.getRoomAt(roomTone1.x, roomTone1.y, roomTone1.z);
                            if (roomDef1 != null) {
                                if (roomDef1 == roomDef0) {
                                    return ParameterRoomType.RoomType.valueOf(roomTone1.enumValue);
                                }

                                if (roomTone1.entireBuilding && roomDef1.building == buildingDef) {
                                    roomTone0 = roomTone1;
                                }
                            }
                        }

                        return roomTone0 != null ? ParameterRoomType.RoomType.valueOf(roomTone0.enumValue) : ParameterRoomType.RoomType.Generic;
                    } else {
                        return ParameterRoomType.RoomType.Generic;
                    }
                }
            }
        }
    }

    private IsoGameCharacter getCharacter() {
        IsoPlayer player0 = null;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player1 = IsoPlayer.players[int0];
            if (player1 != null && (player0 == null || player0.isDead() && player1.isAlive() || player0.Traits.Deaf.isSet() && !player1.Traits.Deaf.isSet())) {
                player0 = player1;
            }
        }

        return player0;
    }

    public static void setRoomType(int _roomType) {
        try {
            roomType = ParameterRoomType.RoomType.values()[_roomType];
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            roomType = null;
        }
    }

    public static void render(IsoPlayer player) {
        if (instance != null) {
            if (player == instance.getCharacter()) {
                player.drawDebugTextBelow("RoomType : " + instance.getRoomType().name());
            }
        }
    }

    private static enum RoomType {
        Generic(0),
        Barn(1),
        Mall(2),
        Warehouse(3),
        Prison(4),
        Church(5),
        Office(6),
        Factory(7);

        final int label;

        private RoomType(int int1) {
            this.label = int1;
        }
    }
}
