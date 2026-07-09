// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.list.PZArrayUtil;

/**
 * Need a garage with a size of at least 10 tiles  Spawn some rocker zombies & music instruments on ground & shelves  Corpse with a guitar in his hand
 */
public final class RDSBandPractice extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> instrumentsList = new ArrayList<>();

    public RDSBandPractice() {
        this.name = "Band Practice";
        this.setChance(10);
        this.setMaximumDays(60);
        this.instrumentsList.add("GuitarAcoustic");
        this.instrumentsList.add("GuitarElectricBlack");
        this.instrumentsList.add("GuitarElectricBlue");
        this.instrumentsList.add("GuitarElectricRed");
        this.instrumentsList.add("GuitarElectricBassBlue");
        this.instrumentsList.add("GuitarElectricBassBlack");
        this.instrumentsList.add("GuitarElectricBassRed");
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        this.spawnItemsInContainers(def, "BandPractice", 90);
        RoomDef roomDef = this.getRoom(def, "garagestorage");
        if (roomDef == null) {
            roomDef = this.getRoom(def, "shed");
        }

        if (roomDef == null) {
            roomDef = this.getRoom(def, "garage");
        }

        this.addZombies(def, Rand.Next(2, 4), "Rocker", 20, roomDef);
        IsoGridSquare square = getRandomSpawnSquare(roomDef);
        if (square != null) {
            square.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            if (Rand.Next(4) == 0) {
                square.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }

            if (Rand.Next(4) == 0) {
                square.AddWorldInventoryItem(PZArrayUtil.pickRandom(this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }

            def.bAlarmed = false;
        }
    }

    /**
     * Description copied from class: RandomizedBuildingBase
     */
    @Override
    public boolean isValid(BuildingDef def, boolean force) {
        this.debugLine = "";
        if (GameClient.bClient) {
            return false;
        } else if (def.isAllExplored() && !force) {
            return false;
        } else {
            if (!force) {
                for (int int0 = 0; int0 < GameServer.Players.size(); int0++) {
                    IsoPlayer player = GameServer.Players.get(int0);
                    if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == def) {
                        return false;
                    }
                }
            }

            boolean boolean0 = false;

            for (int int1 = 0; int1 < def.rooms.size(); int1++) {
                RoomDef roomDef = def.rooms.get(int1);
                if (("garagestorage".equals(roomDef.name) || "shed".equals(roomDef.name) || "garage".equals(roomDef.name)) && roomDef.area >= 9) {
                    boolean0 = true;
                    break;
                }
            }

            if (!boolean0) {
                this.debugLine = "No shed/garage or is too small";
            }

            return boolean0;
        }
    }
}
