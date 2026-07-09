// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;

public class IsoStackedWasherDryer extends IsoObject {
    private final ClothingWasherLogic m_washer = new ClothingWasherLogic(this);
    private final ClothingDryerLogic m_dryer = new ClothingDryerLogic(this);

    public IsoStackedWasherDryer(IsoCell cell) {
        super(cell);
    }

    public IsoStackedWasherDryer(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
    }

    @Override
    public String getObjectName() {
        return "StackedWasherDryer";
    }

    @Override
    public void createContainersFromSpriteProperties() {
        super.createContainersFromSpriteProperties();
        PropertyContainer propertyContainer = this.getProperties();
        if (propertyContainer != null) {
            if (this.getContainerByType("clothingwasher") == null) {
                ItemContainer container0 = new ItemContainer("clothingwasher", this.getSquare(), this);
                if (propertyContainer.Is("ContainerCapacity")) {
                    container0.Capacity = PZMath.tryParseInt(propertyContainer.Val("ContainerCapacity"), 20);
                }

                if (this.getContainer() == null) {
                    this.setContainer(container0);
                } else {
                    this.addSecondaryContainer(container0);
                }
            }

            if (this.getContainerByType("clothingdryer") == null) {
                ItemContainer container1 = new ItemContainer("clothingdryer", this.getSquare(), this);
                if (propertyContainer.Is("ContainerCapacity")) {
                    container1.Capacity = PZMath.tryParseInt(propertyContainer.Val("ContainerCapacity"), 20);
                }

                if (this.getContainer() == null) {
                    this.setContainer(container1);
                } else {
                    this.addSecondaryContainer(container1);
                }
            }
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.m_washer.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.m_dryer.load(input, WorldVersion, IS_DEBUG_SAVE);
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        this.m_washer.save(output, IS_DEBUG_SAVE);
        this.m_dryer.save(output, IS_DEBUG_SAVE);
    }

    @Override
    public void update() {
        this.m_washer.update();
        this.m_dryer.update();
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
        this.m_washer.saveChange(change, tbl, bb);
        this.m_dryer.saveChange(change, tbl, bb);
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        this.m_washer.loadChange(change, bb);
        this.m_dryer.loadChange(change, bb);
    }

    @Override
    public boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item) {
        return this.m_washer.isItemAllowedInContainer(container, item) || this.m_dryer.isItemAllowedInContainer(container, item);
    }

    @Override
    public boolean isRemoveItemAllowedFromContainer(ItemContainer container, InventoryItem item) {
        return this.m_washer.isRemoveItemAllowedFromContainer(container, item) || this.m_dryer.isRemoveItemAllowedFromContainer(container, item);
    }

    public boolean isWasherActivated() {
        return this.m_washer.isActivated();
    }

    public void setWasherActivated(boolean activated) {
        this.m_washer.setActivated(activated);
    }

    public boolean isDryerActivated() {
        return this.m_dryer.isActivated();
    }

    public void setDryerActivated(boolean activated) {
        this.m_dryer.setActivated(activated);
    }
}
