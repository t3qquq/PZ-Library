// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.vehicles.VehiclePart;

public final class ItemUser {
    private static final ArrayList<InventoryItem> tempItems = new ArrayList<>();

    public static void UseItem(InventoryItem item0) {
        DrainableComboItem drainableComboItem = Type.tryCastTo(item0, DrainableComboItem.class);
        if (drainableComboItem != null) {
            drainableComboItem.setDelta(drainableComboItem.getDelta() - drainableComboItem.getUseDelta());
            if (drainableComboItem.uses > 1) {
                int int0 = drainableComboItem.uses - 1;
                drainableComboItem.uses = 1;
                CreateItem(drainableComboItem.getFullType(), tempItems);
                byte byte0 = 0;
                if (byte0 < tempItems.size()) {
                    InventoryItem item1 = tempItems.get(byte0);
                    item1.setUses(int0);
                    AddItem(drainableComboItem, item1);
                }
            }

            if (drainableComboItem.getDelta() <= 1.0E-4F) {
                drainableComboItem.setDelta(0.0F);
                if (drainableComboItem.getReplaceOnDeplete() == null) {
                    UseItem(drainableComboItem, false, false);
                } else {
                    String string = drainableComboItem.getReplaceOnDepleteFullType();
                    CreateItem(string, tempItems);

                    for (int int1 = 0; int1 < tempItems.size(); int1++) {
                        InventoryItem item2 = tempItems.get(int1);
                        item2.setFavorite(drainableComboItem.isFavorite());
                        AddItem(drainableComboItem, item2);
                    }

                    RemoveItem(drainableComboItem);
                }
            }

            drainableComboItem.updateWeight();
        } else {
            UseItem(item0, false, false);
        }
    }

    public static void UseItem(InventoryItem item0, boolean boolean0, boolean boolean1) {
        if (item0.isDisappearOnUse() || boolean0) {
            item0.uses--;
            if (item0.replaceOnUse != null && !boolean1 && !boolean0) {
                String string = item0.replaceOnUse;
                if (!string.contains(".")) {
                    string = item0.module + "." + string;
                }

                CreateItem(string, tempItems);

                for (int int0 = 0; int0 < tempItems.size(); int0++) {
                    InventoryItem item1 = tempItems.get(int0);
                    item1.setConditionFromModData(item0);
                    AddItem(item0, item1);
                    item1.setFavorite(item0.isFavorite());
                }
            }

            if (item0.uses <= 0) {
                if (item0.keepOnDeplete) {
                    return;
                }

                RemoveItem(item0);
            } else if (GameClient.bClient && !item0.isInPlayerInventory()) {
                GameClient.instance.sendItemStats(item0);
            }
        }
    }

    public static void CreateItem(String string, ArrayList<InventoryItem> arrayList) {
        arrayList.clear();
        Item item0 = ScriptManager.instance.FindItem(string);
        if (item0 == null) {
            DebugLog.General.warn("ERROR: ItemUses.CreateItem: can't find " + string);
        } else {
            int int0 = item0.getCount();

            for (int int1 = 0; int1 < int0; int1++) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(string);
                if (item1 == null) {
                    return;
                }

                arrayList.add(item1);
            }
        }
    }

    public static void AddItem(InventoryItem item0, InventoryItem item1) {
        IsoWorldInventoryObject worldInventoryObject = item0.getWorldItem();
        if (worldInventoryObject != null && worldInventoryObject.getWorldObjectIndex() == -1) {
            worldInventoryObject = null;
        }

        if (worldInventoryObject != null) {
            worldInventoryObject.getSquare().AddWorldInventoryItem(item1, 0.0F, 0.0F, 0.0F, true);
        } else {
            if (item0.container != null) {
                VehiclePart part = item0.container.vehiclePart;
                if (!item0.isInPlayerInventory() && GameClient.bClient) {
                    item0.container.addItemOnServer(item1);
                }

                item0.container.AddItem(item1);
                if (part != null) {
                    part.setContainerContentAmount(part.getItemContainer().getCapacityWeight());
                }
            }
        }
    }

    public static void RemoveItem(InventoryItem item) {
        IsoWorldInventoryObject worldInventoryObject = item.getWorldItem();
        if (worldInventoryObject != null && worldInventoryObject.getWorldObjectIndex() == -1) {
            worldInventoryObject = null;
        }

        if (worldInventoryObject != null) {
            worldInventoryObject.getSquare().transmitRemoveItemFromSquare(worldInventoryObject);
            if (item.container != null) {
                item.container.Items.remove(item);
                item.container.setDirty(true);
                item.container.setDrawDirty(true);
                item.container = null;
            }
        } else {
            if (item.container != null) {
                IsoObject object = item.container.parent;
                VehiclePart part = item.container.vehiclePart;
                if (object instanceof IsoGameCharacter character) {
                    if (item instanceof Clothing) {
                        ((Clothing)item).Unwear();
                    }

                    character.removeFromHands(item);
                    if (character.getClothingItem_Back() == item) {
                        character.setClothingItem_Back(null);
                    }
                } else if (!item.isInPlayerInventory() && GameClient.bClient) {
                    item.container.removeItemOnServer(item);
                }

                item.container.Items.remove(item);
                item.container.setDirty(true);
                item.container.setDrawDirty(true);
                item.container = null;
                if (object instanceof IsoDeadBody) {
                    ((IsoDeadBody)object).checkClothing(item);
                }

                if (object instanceof IsoMannequin) {
                    ((IsoMannequin)object).checkClothing(item);
                }

                if (part != null) {
                    part.setContainerContentAmount(part.getItemContainer().getCapacityWeight());
                }
            }
        }
    }
}
