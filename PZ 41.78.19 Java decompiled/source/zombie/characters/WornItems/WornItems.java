// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.WornItems;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.InventoryContainer;
import zombie.util.StringUtils;

public final class WornItems {
    protected final BodyLocationGroup group;
    protected final ArrayList<WornItem> items = new ArrayList<>();

    public WornItems(BodyLocationGroup _group) {
        this.group = _group;
    }

    public WornItems(WornItems other) {
        this.group = other.group;
        this.copyFrom(other);
    }

    public void copyFrom(WornItems other) {
        if (this.group != other.group) {
            throw new RuntimeException("group=" + this.group.id + " other.group=" + other.group.id);
        } else {
            this.items.clear();
            this.items.addAll(other.items);
        }
    }

    public BodyLocationGroup getBodyLocationGroup() {
        return this.group;
    }

    public WornItem get(int index) {
        return this.items.get(index);
    }

    public void setItem(String location, InventoryItem item) {
        this.group.checkValid(location);
        if (!this.group.isMultiItem(location)) {
            int int0 = this.indexOf(location);
            if (int0 != -1) {
                this.items.remove(int0);
            }
        }

        for (int int1 = 0; int1 < this.items.size(); int1++) {
            WornItem wornItem0 = this.items.get(int1);
            if (this.group.isExclusive(location, wornItem0.location)) {
                this.items.remove(int1--);
            }
        }

        if (item != null) {
            this.remove(item);
            int int2 = this.items.size();

            for (int int3 = 0; int3 < this.items.size(); int3++) {
                WornItem wornItem1 = this.items.get(int3);
                if (this.group.indexOf(wornItem1.getLocation()) > this.group.indexOf(location)) {
                    int2 = int3;
                    break;
                }
            }

            WornItem wornItem2 = new WornItem(location, item);
            this.items.add(int2, wornItem2);
        }
    }

    public InventoryItem getItem(String location) {
        this.group.checkValid(location);
        int int0 = this.indexOf(location);
        return int0 == -1 ? null : this.items.get(int0).item;
    }

    public InventoryItem getItemByIndex(int index) {
        return index >= 0 && index < this.items.size() ? this.items.get(index).getItem() : null;
    }

    public void remove(InventoryItem item) {
        int int0 = this.indexOf(item);
        if (int0 != -1) {
            this.items.remove(int0);
        }
    }

    public void clear() {
        this.items.clear();
    }

    public String getLocation(InventoryItem item) {
        int int0 = this.indexOf(item);
        return int0 == -1 ? null : this.items.get(int0).getLocation();
    }

    public boolean contains(InventoryItem item) {
        return this.indexOf(item) != -1;
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public void forEach(Consumer<WornItem> c) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            c.accept(this.items.get(int0));
        }
    }

    public void setFromItemVisuals(ItemVisuals itemVisuals) {
        this.clear();

        for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
            ItemVisual itemVisual = itemVisuals.get(int0);
            String string = itemVisual.getItemType();
            InventoryItem item = InventoryItemFactory.CreateItem(string);
            if (item != null) {
                if (item.getVisual() != null) {
                    item.getVisual().copyFrom(itemVisual);
                    item.synchWithVisual();
                }

                if (item instanceof Clothing && !StringUtils.isNullOrWhitespace(item.getBodyLocation())) {
                    this.setItem(item.getBodyLocation(), item);
                } else if (item instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)item).canBeEquipped())) {
                    this.setItem(((InventoryContainer)item).canBeEquipped(), item);
                }
            }
        }
    }

    public void getItemVisuals(ItemVisuals itemVisuals) {
        itemVisuals.clear();

        for (int int0 = 0; int0 < this.items.size(); int0++) {
            InventoryItem item = this.items.get(int0).getItem();
            ItemVisual itemVisual = item.getVisual();
            if (itemVisual != null) {
                itemVisual.setInventoryItem(item);
                itemVisuals.add(itemVisual);
            }
        }
    }

    public void addItemsToItemContainer(ItemContainer container) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            InventoryItem item = this.items.get(int0).getItem();
            int int1 = item.getVisual().getHolesNumber();
            item.setCondition(item.getConditionMax() - int1 * 3);
            container.AddItem(item);
        }
    }

    private int indexOf(String string) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            WornItem wornItem = this.items.get(int0);
            if (wornItem.location.equals(string)) {
                return int0;
            }
        }

        return -1;
    }

    private int indexOf(InventoryItem item) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            WornItem wornItem = this.items.get(int0);
            if (wornItem.getItem() == item) {
                return int0;
            }
        }

        return -1;
    }
}
