// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create a dead survivor with alcohol bottles around him
 */
public final class RDSDeadDrunk extends RandomizedDeadSurvivorBase {
    final ArrayList<String> alcoholList = new ArrayList<>();

    public RDSDeadDrunk() {
        this.name = "Dead Drunk";
        this.setChance(10);
        this.alcoholList.add("Base.WhiskeyFull");
        this.alcoholList.add("Base.WhiskeyEmpty");
        this.alcoholList.add("Base.Wine");
        this.alcoholList.add("Base.WineEmpty");
        this.alcoholList.add("Base.Wine2");
        this.alcoholList.add("Base.WineEmpty2");
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, 0);
        if (deadBody != null) {
            int int0 = Rand.Next(2, 4);

            for (int int1 = 0; int1 < int0; int1++) {
                InventoryItem item = InventoryItemFactory.CreateItem(this.alcoholList.get(Rand.Next(0, this.alcoholList.size())));
                deadBody.getSquare().AddWorldInventoryItem(item, Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), 0.0F);
                def.bAlarmed = false;
            }

            deadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.WhiskeyEmpty"));
        }
    }
}
