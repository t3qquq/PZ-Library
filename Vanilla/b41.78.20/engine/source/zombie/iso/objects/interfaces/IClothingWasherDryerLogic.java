// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects.interfaces;

import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;

public interface IClothingWasherDryerLogic {
    void update();

    void saveChange(String change, KahluaTable tbl, ByteBuffer bb);

    void loadChange(String change, ByteBuffer bb);

    ItemContainer getContainer();

    boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item);

    boolean isRemoveItemAllowedFromContainer(ItemContainer container, InventoryItem item);

    boolean isActivated();

    void setActivated(boolean activated);

    void switchModeOn();

    void switchModeOff();
}
