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
 * Create some zombies male zombies with 1 naked female, some alcohol around
 */
public final class RDSStagDo extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> items = new ArrayList<>();
    private final ArrayList<String> otherItems = new ArrayList<>();

    public RDSStagDo() {
        this.name = "Stag Do";
        this.setChance(2);
        this.setMaximumDays(60);
        this.otherItems.add("Base.Cigarettes");
        this.otherItems.add("Base.WhiskeyFull");
        this.otherItems.add("Base.Wine");
        this.otherItems.add("Base.Wine2");
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
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
        this.addZombies(def, Rand.Next(5, 7), null, 0, roomDef);
        this.addZombies(def, 1, "NakedVeil", 100, roomDef);
        this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(roomDef, this.otherItems, Rand.Next(2, 6));
        def.bAlarmed = false;
    }
}
