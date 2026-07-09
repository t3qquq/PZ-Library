// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.util.ArrayList;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;

public final class KeyRing extends InventoryItem {
    private final ArrayList<Key> keys = new ArrayList<>();

    public KeyRing(String module, String name, String type, String tex) {
        super(module, name, type, tex);
        this.cat = ItemType.KeyRing;
    }

    @Override
    public int getSaveType() {
        return Item.Type.KeyRing.ordinal();
    }

    public void addKey(Key key) {
        this.keys.add(key);
    }

    public boolean containsKeyId(int keyId) {
        for (int int0 = 0; int0 < this.keys.size(); int0++) {
            if (this.keys.get(int0).getKeyId() == keyId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Key Ring";
    }

    public ArrayList<Key> getKeys() {
        return this.keys;
    }

    public void setKeys(ArrayList<Key> _keys) {
        _keys.clear();
        this.keys.addAll(_keys);
    }
}
