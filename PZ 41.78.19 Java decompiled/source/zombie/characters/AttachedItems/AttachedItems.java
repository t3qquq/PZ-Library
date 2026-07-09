// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.inventory.InventoryItem;

public final class AttachedItems {
    protected final AttachedLocationGroup group;
    protected final ArrayList<AttachedItem> items = new ArrayList<>();

    public AttachedItems(AttachedLocationGroup _group) {
        this.group = _group;
    }

    public AttachedItems(AttachedItems other) {
        this.group = other.group;
        this.copyFrom(other);
    }

    public void copyFrom(AttachedItems other) {
        if (this.group != other.group) {
            throw new RuntimeException("group=" + this.group.id + " other.group=" + other.group.id);
        } else {
            this.items.clear();
            this.items.addAll(other.items);
        }
    }

    public AttachedLocationGroup getGroup() {
        return this.group;
    }

    public AttachedItem get(int index) {
        return this.items.get(index);
    }

    public void setItem(String location, InventoryItem item) {
        this.group.checkValid(location);
        int int0 = this.indexOf(location);
        if (int0 != -1) {
            this.items.remove(int0);
        }

        if (item != null) {
            this.remove(item);
            int int1 = this.items.size();

            for (int int2 = 0; int2 < this.items.size(); int2++) {
                AttachedItem attachedItem0 = this.items.get(int2);
                if (this.group.indexOf(attachedItem0.getLocation()) > this.group.indexOf(location)) {
                    int1 = int2;
                    break;
                }
            }

            AttachedItem attachedItem1 = new AttachedItem(location, item);
            this.items.add(int1, attachedItem1);
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

    public void forEach(Consumer<AttachedItem> c) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            c.accept(this.items.get(int0));
        }
    }

    private int indexOf(String string) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            AttachedItem attachedItem = this.items.get(int0);
            if (attachedItem.location.equals(string)) {
                return int0;
            }
        }

        return -1;
    }

    private int indexOf(InventoryItem item) {
        for (int int0 = 0; int0 < this.items.size(); int0++) {
            AttachedItem attachedItem = this.items.get(int0);
            if (attachedItem.getItem() == item) {
                return int0;
            }
        }

        return -1;
    }
}
