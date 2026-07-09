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
 * Create some zombies in varsity outfit + 2 naked zombies in bedroom
 */
public final class RDSPokerNight extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> items = new ArrayList<>();
    private String money = null;
    private String card = null;

    public RDSPokerNight() {
        this.name = "Poker Night";
        this.setChance(4);
        this.setMaximumDays(60);
        this.items.add("Base.Cigarettes");
        this.items.add("Base.WhiskeyFull");
        this.items.add("Base.Wine");
        this.items.add("Base.Wine2");
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
        this.money = "Base.Money";
        this.card = "Base.CardDeck";
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

            if (this.getRoom(def, "kitchen") != null) {
                return true;
            } else {
                this.debugLine = "No kitchen";
                return false;
            }
        }
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getRoom(def, "kitchen");
        this.addZombies(def, Rand.Next(3, 5), null, 10, roomDef);
        this.addZombies(def, 1, "PokerDealer", 0, roomDef);
        this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(roomDef, this.money, Rand.Next(8, 13));
        this.addRandomItemsOnGround(roomDef, this.card, 1);
        def.bAlarmed = false;
    }
}
