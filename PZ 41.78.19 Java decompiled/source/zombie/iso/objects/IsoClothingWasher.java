// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;

public class IsoClothingWasher extends IsoObject {
    private final ClothingWasherLogic m_logic = new ClothingWasherLogic(this);

    public IsoClothingWasher(IsoCell cell) {
        super(cell);
    }

    public IsoClothingWasher(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
    }

    @Override
    public String getObjectName() {
        return "ClothingWasher";
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.m_logic.load(input, WorldVersion, IS_DEBUG_SAVE);
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        this.m_logic.save(output, IS_DEBUG_SAVE);
    }

    @Override
    public void update() {
        this.m_logic.update();
    }

    @Override
    public void addToWorld() {
        IsoCell cell = this.getCell();
        cell.addToProcessIsoObject(this);
    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        this.m_logic.saveChange(change, tbl, bb);
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        this.m_logic.loadChange(change, bb);
    }

    @Override
    public boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item) {
        return this.m_logic.isItemAllowedInContainer(container, item);
    }

    @Override
    public boolean isRemoveItemAllowedFromContainer(ItemContainer container, InventoryItem item) {
        return this.m_logic.isRemoveItemAllowedFromContainer(container, item);
    }

    public boolean isActivated() {
        return this.m_logic.isActivated();
    }

    public void setActivated(boolean activated) {
        this.m_logic.setActivated(activated);
    }
}
