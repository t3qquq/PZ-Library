// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.iso.sprite.IsoSprite;

public class IsoCombinationWasherDryer extends IsoObject {
    private final ClothingWasherLogic m_washer = new ClothingWasherLogic(this);
    private final ClothingDryerLogic m_dryer = new ClothingDryerLogic(this);
    private IClothingWasherDryerLogic m_logic = this.m_washer;

    public IsoCombinationWasherDryer(IsoCell cell) {
        super(cell);
    }

    public IsoCombinationWasherDryer(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
    }

    @Override
    public String getObjectName() {
        return "CombinationWasherDryer";
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.m_logic = (IClothingWasherDryerLogic)(input.get() == 0 ? this.m_washer : this.m_dryer);
        this.m_washer.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.m_dryer.load(input, WorldVersion, IS_DEBUG_SAVE);
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.m_logic == this.m_washer ? 0 : 1));
        this.m_washer.save(output, IS_DEBUG_SAVE);
        this.m_dryer.save(output, IS_DEBUG_SAVE);
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
        if ("mode".equals(change)) {
            bb.put((byte)(this.isModeWasher() ? 0 : 1));
        } else {
            this.m_logic.saveChange(change, tbl, bb);
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("mode".equals(change)) {
            if (bb.get() == 0) {
                this.setModeWasher();
            } else {
                this.setModeDryer();
            }
        } else {
            this.m_logic.loadChange(change, bb);
        }
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

    public void setModeWasher() {
        if (!this.isModeWasher()) {
            this.m_dryer.switchModeOff();
            this.m_logic = this.m_washer;
            this.getContainer().setType("clothingwasher");
            this.m_washer.switchModeOn();
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }

    public void setModeDryer() {
        if (!this.isModeDryer()) {
            this.m_washer.switchModeOff();
            this.m_logic = this.m_dryer;
            this.getContainer().setType("clothingdryer");
            this.m_dryer.switchModeOn();
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }

    public boolean isModeWasher() {
        return this.m_logic == this.m_washer;
    }

    public boolean isModeDryer() {
        return this.m_logic == this.m_dryer;
    }
}
