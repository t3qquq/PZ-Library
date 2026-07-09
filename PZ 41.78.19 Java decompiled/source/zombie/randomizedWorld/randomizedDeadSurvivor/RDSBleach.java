// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create a dead survivor in the kitchen with empty bleach bottle around him
 */
public final class RDSBleach extends RandomizedDeadSurvivorBase {
    public RDSBleach() {
        this.name = "Suicide by Bleach";
        this.setChance(10);
        this.setMinimumDays(60);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createRandomDeadBody(roomDef, 0);
        if (deadBody != null) {
            int int0 = Rand.Next(1, 3);

            for (int int1 = 0; int1 < int0; int1++) {
                InventoryItem item = InventoryItemFactory.CreateItem("Base.BleachEmpty");
                deadBody.getSquare().AddWorldInventoryItem(item, Rand.Next(0.5F, 1.0F), Rand.Next(0.5F, 1.0F), 0.0F);
            }

            deadBody.setPrimaryHandItem(InventoryItemFactory.CreateItem("Base.BleachEmpty"));
            def.bAlarmed = false;
        }
    }
}
