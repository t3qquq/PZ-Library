// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import zombie.inventory.InventoryItem;

public final class AttachedItem {
    protected final String location;
    protected final InventoryItem item;

    public AttachedItem(String _location, InventoryItem _item) {
        if (_location == null) {
            throw new NullPointerException("location is null");
        } else if (_location.isEmpty()) {
            throw new IllegalArgumentException("location is empty");
        } else if (_item == null) {
            throw new NullPointerException("item is null");
        } else {
            this.location = _location;
            this.item = _item;
        }
    }

    public String getLocation() {
        return this.location;
    }

    public InventoryItem getItem() {
        return this.item;
    }
}
