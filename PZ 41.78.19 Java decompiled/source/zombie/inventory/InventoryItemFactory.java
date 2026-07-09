// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.world.ItemInfo;
import zombie.world.WorldDictionary;

public final class InventoryItemFactory {
    public static InventoryItem CreateItem(String string) {
        return CreateItem(string, 1.0F);
    }

    public static InventoryItem CreateItem(String string, Food food1) {
        InventoryItem item = CreateItem(string, 1.0F);
        Food food0 = Type.tryCastTo(item, Food.class);
        if (food0 == null) {
            return null;
        } else {
            food0.setBaseHunger(food1.getBaseHunger());
            food0.setHungChange(food1.getHungChange());
            food0.setBoredomChange(food1.getBoredomChangeUnmodified());
            food0.setUnhappyChange(food1.getUnhappyChangeUnmodified());
            food0.setCarbohydrates(food1.getCarbohydrates());
            food0.setLipids(food1.getLipids());
            food0.setProteins(food1.getProteins());
            food0.setCalories(food1.getCalories());
            return item;
        }
    }

    public static InventoryItem CreateItem(String string, float float0) {
        return CreateItem(string, float0, true);
    }

    public static InventoryItem CreateItem(String string1, float float0, boolean boolean1) {
        InventoryItem item0 = null;
        Item item1 = null;
        boolean boolean0 = false;
        String string0 = null;

        try {
            if (string1.startsWith("Moveables.") && !string1.equalsIgnoreCase("Moveables.Moveable")) {
                String[] strings = string1.split("\\.");
                string0 = strings[1];
                boolean0 = true;
                string1 = "Moveables.Moveable";
            }

            item1 = ScriptManager.instance.FindItem(string1, boolean1);
        } catch (Exception exception) {
            DebugLog.log("couldn't find item " + string1);
        }

        if (item1 == null) {
            return null;
        } else {
            item0 = item1.InstanceItem(null);
            if (GameClient.bClient && (Core.getInstance().getPoisonousBerry() == null || Core.getInstance().getPoisonousBerry().isEmpty())) {
                Core.getInstance().setPoisonousBerry(GameClient.poisonousBerry);
            }

            if (GameClient.bClient && (Core.getInstance().getPoisonousMushroom() == null || Core.getInstance().getPoisonousMushroom().isEmpty())) {
                Core.getInstance().setPoisonousMushroom(GameClient.poisonousMushroom);
            }

            if (string1.equals(Core.getInstance().getPoisonousBerry())) {
                ((Food)item0).Poison = true;
                ((Food)item0).setPoisonLevelForRecipe(1);
                ((Food)item0).setPoisonDetectionLevel(1);
                ((Food)item0).setPoisonPower(5);
                ((Food)item0).setUseForPoison(new Float(Math.abs(((Food)item0).getHungChange()) * 100.0F).intValue());
            }

            if (string1.equals(Core.getInstance().getPoisonousMushroom())) {
                ((Food)item0).Poison = true;
                ((Food)item0).setPoisonLevelForRecipe(2);
                ((Food)item0).setPoisonDetectionLevel(2);
                ((Food)item0).setPoisonPower(10);
                ((Food)item0).setUseForPoison(new Float(Math.abs(((Food)item0).getHungChange()) * 100.0F).intValue());
            }

            item0.id = Rand.Next(2146250223) + 1233423;
            if (item0 instanceof Drainable) {
                ((Drainable)item0).setUsedDelta(float0);
            }

            if (boolean0) {
                item0.type = string0;
                item0.fullType = item0.module + "." + string0;
                if (item0 instanceof Moveable && !((Moveable)item0).ReadFromWorldSprite(string0) && item0 instanceof Radio) {
                    DebugLog.log("InventoryItemFactory -> Radio item = " + (string1 != null ? string1 : "unknown"));
                }
            }

            return item0;
        }
    }

    public static InventoryItem CreateItem(String string0, float float0, String string1) {
        InventoryItem item0 = null;
        Item item1 = ScriptManager.instance.getItem(string0);
        if (item1 == null) {
            DebugLog.log(string0 + " item not found.");
            return null;
        } else {
            item0 = item1.InstanceItem(string1);
            if (item0 == null) {
            }

            if (item0 instanceof Drainable) {
                ((Drainable)item0).setUsedDelta(float0);
            }

            return item0;
        }
    }

    public static InventoryItem CreateItem(String string0, String string1, String string2, String string3) {
        InventoryItem item = new InventoryItem(string0, string1, string2, string3);
        item.id = Rand.Next(2146250223) + 1233423;
        return item;
    }

    public static InventoryItem CreateItem(short short0) {
        ItemInfo itemInfo = WorldDictionary.getItemInfoFromID(short0);
        if (itemInfo != null && itemInfo.isValid()) {
            String string = itemInfo.getFullType();
            if (string != null) {
                InventoryItem item = CreateItem(string, 1.0F, false);
                if (item != null) {
                    return item;
                }

                DebugLog.log(
                    "InventoryItemFactory.CreateItem() unknown item type \""
                        + (string != null ? string : "unknown")
                        + "\", registry id = \""
                        + short0
                        + "\". Make sure all mods used in save are installed."
                );
            } else {
                DebugLog.log(
                    "InventoryItemFactory.CreateItem() unknown item with registry ID \"" + short0 + "\". Make sure all mods used in save are installed."
                );
            }
        } else if (itemInfo == null) {
            DebugLog.log("InventoryItemFactory.CreateItem() unknown item with registry ID \"" + short0 + "\". Make sure all mods used in save are installed.");
        } else {
            DebugLog.log("InventoryItemFactory.CreateItem() cannot create item: " + itemInfo.ToString());
        }

        return null;
    }
}
