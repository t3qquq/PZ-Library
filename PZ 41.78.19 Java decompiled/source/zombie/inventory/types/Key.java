// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;

public final class Key extends InventoryItem {
    private int keyId = -1;
    private boolean padlock = false;
    private int numberOfKey = 0;
    private boolean digitalPadlock = false;
    public static final Key[] highlightDoor = new Key[4];

    public Key(String module, String name, String type, String tex) {
        super(module, name, type, tex);
        this.cat = ItemType.Key;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Key.ordinal();
    }

    /**
     * Get the key number of the building and set it to the key
     */
    public void takeKeyId() {
        if (this.getContainer() != null
            && this.getContainer().getSourceGrid() != null
            && this.getContainer().getSourceGrid().getBuilding() != null
            && this.getContainer().getSourceGrid().getBuilding().def != null) {
            this.setKeyId(this.getContainer().getSourceGrid().getBuilding().def.getKeyId());
        }
    }

    public static void setHighlightDoors(int playerNum, InventoryItem item) {
        if (item instanceof Key && !((Key)item).isPadlock() && !((Key)item).isDigitalPadlock()) {
            highlightDoor[playerNum] = (Key)item;
        } else {
            highlightDoor[playerNum] = null;
        }
    }

    @Override
    public int getKeyId() {
        return this.keyId;
    }

    @Override
    public void setKeyId(int _keyId) {
        this.keyId = _keyId;
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Key";
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        output.putInt(this.getKeyId());
        output.put((byte)this.numberOfKey);
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.setKeyId(input.getInt());
        this.numberOfKey = input.get();
    }

    public boolean isPadlock() {
        return this.padlock;
    }

    public void setPadlock(boolean _padlock) {
        this.padlock = _padlock;
    }

    public int getNumberOfKey() {
        return this.numberOfKey;
    }

    public void setNumberOfKey(int _numberOfKey) {
        this.numberOfKey = _numberOfKey;
    }

    public boolean isDigitalPadlock() {
        return this.digitalPadlock;
    }

    public void setDigitalPadlock(boolean _digitalPadlock) {
        this.digitalPadlock = _digitalPadlock;
    }
}
