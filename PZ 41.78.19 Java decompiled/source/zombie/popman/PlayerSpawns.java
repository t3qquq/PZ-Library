// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.util.ArrayList;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;

final class PlayerSpawns {
    private final ArrayList<PlayerSpawns.PlayerSpawn> playerSpawns = new ArrayList<>();

    public void addSpawn(int int0, int int1, int int2) {
        PlayerSpawns.PlayerSpawn playerSpawn = new PlayerSpawns.PlayerSpawn(int0, int1, int2);
        if (playerSpawn.building != null) {
            this.playerSpawns.add(playerSpawn);
        }
    }

    public void update() {
        long long0 = System.currentTimeMillis();

        for (int int0 = 0; int0 < this.playerSpawns.size(); int0++) {
            PlayerSpawns.PlayerSpawn playerSpawn = this.playerSpawns.get(int0);
            if (playerSpawn.counter == -1L) {
                playerSpawn.counter = long0;
            }

            if (playerSpawn.counter + 10000L <= long0) {
                this.playerSpawns.remove(int0--);
            }
        }
    }

    public boolean allowZombie(IsoGridSquare square) {
        for (int int0 = 0; int0 < this.playerSpawns.size(); int0++) {
            PlayerSpawns.PlayerSpawn playerSpawn = this.playerSpawns.get(int0);
            if (!playerSpawn.allowZombie(square)) {
                return false;
            }
        }

        return true;
    }

    private static class PlayerSpawn {
        public int x;
        public int y;
        public long counter;
        public BuildingDef building;

        public PlayerSpawn(int int0, int int1, int int2) {
            this.x = int0;
            this.y = int1;
            this.counter = -1L;
            RoomDef roomDef = IsoWorld.instance.getMetaGrid().getRoomAt(int0, int1, int2);
            if (roomDef != null) {
                this.building = roomDef.getBuilding();
            }
        }

        public boolean allowZombie(IsoGridSquare square) {
            if (this.building == null) {
                return true;
            } else {
                return square.getBuilding() != null && this.building == square.getBuilding().getDef()
                    ? false
                    : square.getX() < this.building.getX() - 15
                        || square.getX() >= this.building.getX2() + 15
                        || square.getY() < this.building.getY() - 15
                        || square.getY() >= this.building.getY2() + 15;
            }
        }
    }
}
