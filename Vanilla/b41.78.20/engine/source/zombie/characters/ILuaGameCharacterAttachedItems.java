// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.inventory.InventoryItem;

public interface ILuaGameCharacterAttachedItems {
    AttachedItems getAttachedItems();

    void setAttachedItems(AttachedItems var1);

    InventoryItem getAttachedItem(String var1);

    void setAttachedItem(String var1, InventoryItem var2);

    void removeAttachedItem(InventoryItem var1);

    void clearAttachedItems();

    AttachedLocationGroup getAttachedLocationGroup();
}
