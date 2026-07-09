// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;

/**
 * Zombies in party outfit, some food scattered around
 */
public final class RDSHouseParty extends RandomizedDeadSurvivorBase {
    final ArrayList<String> items = new ArrayList<>();

    public RDSHouseParty() {
        this.name = "House Party";
        this.setChance(4);
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
        this.items.add("Base.Cupcake");
        this.items.add("Base.Cupcake");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
        this.items.add("Base.CakeSlice");
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

            if (this.getRoom(def, "livingroom") != null) {
                return true;
            } else {
                this.debugLine = "No living room";
                return false;
            }
        }
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getRoom(def, "livingroom");
        this.addZombies(def, Rand.Next(5, 8), "Party", null, roomDef);
        this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(4, 7));
        def.bAlarmed = false;
    }
}
