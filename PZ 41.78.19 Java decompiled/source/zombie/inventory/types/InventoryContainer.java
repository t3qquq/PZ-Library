// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public final class InventoryContainer extends InventoryItem {
    ItemContainer container = new ItemContainer();
    int capacity = 0;
    int weightReduction = 0;
    private String CanBeEquipped = "";

    public InventoryContainer(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        this.container.containingItem = this;
        this.container.type = itemType;
        this.container.inventoryContainer = this;
    }

    @Override
    public boolean IsInventoryContainer() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Container.ordinal();
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Container";
    }

    public ItemContainer getInventory() {
        return this.container;
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        output.putInt(this.container.ID);
        output.putInt(this.weightReduction);
        this.container.save(output);
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        int int0 = input.getInt();
        this.setWeightReduction(input.getInt());
        if (this.container == null) {
            this.container = new ItemContainer();
        }

        this.container.clear();
        this.container.containingItem = this;
        this.container.setWeightReduction(this.weightReduction);
        this.container.Capacity = this.capacity;
        this.container.ID = int0;
        this.container.load(input, WorldVersion);
        this.synchWithVisual();
    }

    public int getCapacity() {
        return this.container.getCapacity();
    }

    public void setCapacity(int _capacity) {
        this.capacity = _capacity;
        if (this.container == null) {
            this.container = new ItemContainer();
        }

        this.container.Capacity = _capacity;
    }

    public float getInventoryWeight() {
        if (this.getInventory() == null) {
            return 0.0F;
        } else {
            float float0 = 0.0F;
            ArrayList arrayList = this.getInventory().getItems();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                InventoryItem item = (InventoryItem)arrayList.get(int0);
                if (this.isEquipped()) {
                    float0 += item.getEquippedWeight();
                } else {
                    float0 += item.getUnequippedWeight();
                }
            }

            return float0;
        }
    }

    public int getEffectiveCapacity(IsoGameCharacter chr) {
        return this.container.getEffectiveCapacity(chr);
    }

    public int getWeightReduction() {
        return this.weightReduction;
    }

    public void setWeightReduction(int _weightReduction) {
        _weightReduction = Math.min(_weightReduction, 100);
        _weightReduction = Math.max(_weightReduction, 0);
        this.weightReduction = _weightReduction;
        this.container.setWeightReduction(_weightReduction);
    }

    @Override
    public void updateAge() {
        ArrayList arrayList = this.getInventory().getItems();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ((InventoryItem)arrayList.get(int0)).updateAge();
        }
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI) {
        tooltipUI.render();
        super.DoTooltip(tooltipUI);
        int int0 = tooltipUI.getHeight().intValue();
        int0 -= tooltipUI.padBottom;
        if (tooltipUI.getWidth() < 160.0) {
            tooltipUI.setWidth(160.0);
        }

        if (!this.getItemContainer().getItems().isEmpty()) {
            byte byte0 = 5;
            int0 += 4;
            HashSet hashSet = new HashSet();

            for (int int1 = this.getItemContainer().getItems().size() - 1; int1 >= 0; int1--) {
                InventoryItem item = this.getItemContainer().getItems().get(int1);
                if (item.getName() != null) {
                    if (hashSet.contains(item.getName())) {
                        continue;
                    }

                    hashSet.add(item.getName());
                }

                tooltipUI.DrawTextureScaledAspect(item.getTex(), byte0, int0, 16.0, 16.0, 1.0, 1.0, 1.0, 1.0);
                byte0 += 17;
                if (byte0 + 16 > tooltipUI.width - tooltipUI.padRight) {
                    break;
                }
            }

            int0 += 16;
        }

        int0 += tooltipUI.padBottom;
        tooltipUI.setHeight(int0);
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        float float0 = 0.0F;
        float float1 = 0.6F;
        float float2 = 0.0F;
        float float3 = 0.7F;
        if (this.getEffectiveCapacity(tooltipUI.getCharacter()) != 0) {
            ObjectTooltip.LayoutItem layoutItem0 = layout.addItem();
            layoutItem0.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
            layoutItem0.setValueRightNoPlus(this.getEffectiveCapacity(tooltipUI.getCharacter()));
        }

        if (this.getWeightReduction() != 0) {
            ObjectTooltip.LayoutItem layoutItem1 = layout.addItem();
            layoutItem1.setLabel(Translator.getText("Tooltip_container_Weight_Reduction") + ":", 1.0F, 1.0F, 1.0F, 1.0F);
            layoutItem1.setValueRightNoPlus(this.getWeightReduction());
        }

        if (this.getBloodClothingType() != null) {
            float float4 = this.getBloodLevel();
            if (float4 != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem2 = layout.addItem();
                layoutItem2.setLabel(Translator.getText("Tooltip_clothing_bloody") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem2.setProgress(float4, float0, float1, float2, float3);
            }
        }
    }

    public void setBloodLevel(float delta) {
        ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            this.setBlood((BloodBodyPartType)arrayList.get(int0), PZMath.clamp(delta, 0.0F, 100.0F));
        }
    }

    public float getBloodLevel() {
        ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());
        float float0 = 0.0F;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            float0 += this.getBlood((BloodBodyPartType)arrayList.get(int0));
        }

        return float0;
    }

    public void setCanBeEquipped(String canBeEquipped) {
        this.CanBeEquipped = canBeEquipped == null ? "" : canBeEquipped;
    }

    public String canBeEquipped() {
        return this.CanBeEquipped;
    }

    public ItemContainer getItemContainer() {
        return this.container;
    }

    public void setItemContainer(ItemContainer cont) {
        this.container = cont;
    }

    @Override
    public float getContentsWeight() {
        return this.getInventory().getContentsWeight();
    }

    @Override
    public float getEquippedWeight() {
        float float0 = 1.0F;
        if (this.getWeightReduction() > 0) {
            float0 = 1.0F - this.getWeightReduction() / 100.0F;
        }

        return this.getActualWeight() * 0.3F + this.getContentsWeight() * float0;
    }

    public String getClothingExtraSubmenu() {
        return this.ScriptItem.clothingExtraSubmenu;
    }
}
