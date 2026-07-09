// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.util.ArrayList;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public final class WeaponPart extends InventoryItem {
    public static final String TYPE_CANON = "Canon";
    public static final String TYPE_CLIP = "Clip";
    public static final String TYPE_RECOILPAD = "RecoilPad";
    public static final String TYPE_SCOPE = "Scope";
    public static final String TYPE_SLING = "Sling";
    public static final String TYPE_STOCK = "Stock";
    private float maxRange = 0.0F;
    private float minRangeRanged = 0.0F;
    private float damage = 0.0F;
    private float recoilDelay = 0.0F;
    private int clipSize = 0;
    private int reloadTime = 0;
    private int aimingTime = 0;
    private int hitChance = 0;
    private float angle = 0.0F;
    private float weightModifier = 0.0F;
    private final ArrayList<String> mountOn = new ArrayList<>();
    private final ArrayList<String> mountOnDisplayName = new ArrayList<>();
    private String partType = null;

    public WeaponPart(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        this.cat = ItemType.Weapon;
    }

    @Override
    public int getSaveType() {
        return Item.Type.WeaponPart.ordinal();
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "WeaponPart";
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        ObjectTooltip.LayoutItem layoutItem = layout.addItem();
        layoutItem.setLabel(Translator.getText("Tooltip_weapon_Type") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
        layoutItem.setValue(Translator.getText("Tooltip_weapon_" + this.partType), 1.0F, 1.0F, 0.8F, 1.0F);
        layoutItem = layout.addItem();
        String string = Translator.getText("Tooltip_weapon_CanBeMountOn") + this.mountOnDisplayName.toString().replaceAll("\\[", "").replaceAll("\\]", "");
        layoutItem.setLabel(string, 1.0F, 1.0F, 0.8F, 1.0F);
    }

    public float getMinRangeRanged() {
        return this.minRangeRanged;
    }

    public void setMinRangeRanged(float _minRangeRanged) {
        this.minRangeRanged = _minRangeRanged;
    }

    public float getMaxRange() {
        return this.maxRange;
    }

    public void setMaxRange(float _maxRange) {
        this.maxRange = _maxRange;
    }

    public float getRecoilDelay() {
        return this.recoilDelay;
    }

    public void setRecoilDelay(float _recoilDelay) {
        this.recoilDelay = _recoilDelay;
    }

    public int getClipSize() {
        return this.clipSize;
    }

    public void setClipSize(int _clipSize) {
        this.clipSize = _clipSize;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float _damage) {
        this.damage = _damage;
    }

    public ArrayList<String> getMountOn() {
        return this.mountOn;
    }

    public void setMountOn(ArrayList<String> _mountOn) {
        this.mountOn.clear();
        this.mountOnDisplayName.clear();

        for (int int0 = 0; int0 < _mountOn.size(); int0++) {
            String string = (String)_mountOn.get(int0);
            if (!string.contains(".")) {
                string = this.getModule() + "." + string;
            }

            Item item = ScriptManager.instance.getItem(string);
            if (item != null) {
                this.mountOn.add(item.getFullName());
                this.mountOnDisplayName.add(item.getDisplayName());
            }
        }
    }

    public String getPartType() {
        return this.partType;
    }

    public void setPartType(String _partType) {
        this.partType = _partType;
    }

    public int getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime(int _reloadTime) {
        this.reloadTime = _reloadTime;
    }

    public int getAimingTime() {
        return this.aimingTime;
    }

    public void setAimingTime(int _aimingTime) {
        this.aimingTime = _aimingTime;
    }

    public int getHitChance() {
        return this.hitChance;
    }

    public void setHitChance(int _hitChance) {
        this.hitChance = _hitChance;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float _angle) {
        this.angle = _angle;
    }

    public float getWeightModifier() {
        return this.weightModifier;
    }

    public void setWeightModifier(float _weightModifier) {
        this.weightModifier = _weightModifier;
    }
}
