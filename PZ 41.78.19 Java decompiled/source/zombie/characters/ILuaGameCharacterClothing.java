// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.WornItems;
import zombie.inventory.InventoryItem;

public interface ILuaGameCharacterClothing {
    void dressInNamedOutfit(String var1);

    void dressInPersistentOutfit(String var1);

    void dressInPersistentOutfitID(int var1);

    String getOutfitName();

    WornItems getWornItems();

    void setWornItems(WornItems var1);

    InventoryItem getWornItem(String var1);

    void setWornItem(String var1, InventoryItem var2);

    void removeWornItem(InventoryItem var1);

    void clearWornItems();

    BodyLocationGroup getBodyLocationGroup();

    void setClothingItem_Head(InventoryItem var1);

    void setClothingItem_Torso(InventoryItem var1);

    void setClothingItem_Back(InventoryItem var1);

    void setClothingItem_Hands(InventoryItem var1);

    void setClothingItem_Legs(InventoryItem var1);

    void setClothingItem_Feet(InventoryItem var1);

    void Dressup(SurvivorDesc var1);
}
