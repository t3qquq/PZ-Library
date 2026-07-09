// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;

public final class HandWeapon extends InventoryItem {
    public float WeaponLength;
    public float SplatSize = 1.0F;
    private int ammoPerShoot = 1;
    private String magazineType = null;
    protected boolean angleFalloff = false;
    protected boolean bCanBarracade = false;
    protected float doSwingBeforeImpact = 0.0F;
    protected String impactSound = "BaseballBatHit";
    protected boolean knockBackOnNoDeath = true;
    protected float maxAngle = 1.0F;
    protected float maxDamage = 1.5F;
    protected int maxHitCount = 1000;
    protected float maxRange = 1.0F;
    protected boolean ranged = false;
    protected float minAngle = 0.5F;
    protected float minDamage = 0.4F;
    protected float minimumSwingTime = 0.5F;
    protected float minRange = 0.0F;
    protected float noiseFactor = 0.0F;
    protected String otherHandRequire = null;
    protected boolean otherHandUse = false;
    protected String physicsObject = null;
    protected float pushBackMod = 1.0F;
    protected boolean rangeFalloff = false;
    protected boolean shareDamage = true;
    protected int soundRadius = 0;
    protected int soundVolume = 0;
    protected boolean splatBloodOnNoDeath = false;
    protected int splatNumber = 2;
    protected String swingSound = "BaseballBatSwing";
    protected float swingTime = 1.0F;
    protected float toHitModifier = 1.0F;
    protected boolean useEndurance = true;
    protected boolean useSelf = false;
    protected String weaponSprite = null;
    private String originalWeaponSprite = null;
    protected float otherBoost = 1.0F;
    protected int DoorDamage = 1;
    protected String doorHitSound = "BaseballBatHit";
    protected int ConditionLowerChance = 10000;
    protected boolean MultipleHitConditionAffected = true;
    protected boolean shareEndurance = true;
    protected boolean AlwaysKnockdown = false;
    protected float EnduranceMod = 1.0F;
    protected float KnockdownMod = 1.0F;
    protected boolean CantAttackWithLowestEndurance = false;
    public boolean bIsAimedFirearm = false;
    public boolean bIsAimedHandWeapon = false;
    public String RunAnim = "Run";
    public String IdleAnim = "Idle";
    public float HitAngleMod = 0.0F;
    private String SubCategory = "";
    private ArrayList<String> Categories = null;
    private int AimingPerkCritModifier = 0;
    private float AimingPerkRangeModifier = 0.0F;
    private float AimingPerkHitChanceModifier = 0.0F;
    private int HitChance = 0;
    private float AimingPerkMinAngleModifier = 0.0F;
    private int RecoilDelay = 0;
    private boolean PiercingBullets = false;
    private float soundGain = 1.0F;
    private WeaponPart scope = null;
    private WeaponPart canon = null;
    private WeaponPart clip = null;
    private WeaponPart recoilpad = null;
    private WeaponPart sling = null;
    private WeaponPart stock = null;
    private int ClipSize = 0;
    private int reloadTime = 0;
    private int aimingTime = 0;
    private float minRangeRanged = 0.0F;
    private int treeDamage = 0;
    private String bulletOutSound = null;
    private String shellFallSound = null;
    private int triggerExplosionTimer = 0;
    private boolean canBePlaced = false;
    private int explosionRange = 0;
    private int explosionPower = 0;
    private int fireRange = 0;
    private int firePower = 0;
    private int smokeRange = 0;
    private int noiseRange = 0;
    private float extraDamage = 0.0F;
    private int explosionTimer = 0;
    private String placedSprite = null;
    private boolean canBeReused = false;
    private int sensorRange = 0;
    private float critDmgMultiplier = 2.0F;
    private float baseSpeed = 1.0F;
    private float bloodLevel = 0.0F;
    private String ammoBox = null;
    private String insertAmmoStartSound = null;
    private String insertAmmoSound = null;
    private String insertAmmoStopSound = null;
    private String ejectAmmoStartSound = null;
    private String ejectAmmoSound = null;
    private String ejectAmmoStopSound = null;
    private String rackSound = null;
    private String clickSound = "Stormy9mmClick";
    private boolean containsClip = false;
    private String weaponReloadType = "handgun";
    private boolean rackAfterShoot = false;
    private boolean roundChambered = false;
    private boolean bSpentRoundChambered = false;
    private int spentRoundCount = 0;
    private float jamGunChance = 5.0F;
    private boolean isJammed = false;
    private ArrayList<ModelWeaponPart> modelWeaponPart = null;
    private boolean haveChamber = true;
    private String bulletName = null;
    private String damageCategory = null;
    private boolean damageMakeHole = false;
    private String hitFloorSound = "BatOnFloor";
    private boolean insertAllBulletsReload = false;
    private String fireMode = null;
    private ArrayList<String> fireModePossibilities = null;
    public int ProjectileCount = 1;
    public float aimingMod = 1.0F;
    public float CriticalChance = 20.0F;
    private String hitSound = "BaseballBatHit";

    public float getSplatSize() {
        return this.SplatSize;
    }

    @Override
    public boolean CanStack(InventoryItem item) {
        return false;
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Weapon";
    }

    public HandWeapon(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        this.cat = ItemType.Weapon;
    }

    public HandWeapon(String module, String name, String itemType, Item item) {
        super(module, name, itemType, item);
        this.cat = ItemType.Weapon;
    }

    @Override
    public boolean IsWeapon() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Weapon.ordinal();
    }

    @Override
    public float getScore(SurvivorDesc desc) {
        float float0 = 0.0F;
        if (this.getAmmoType() != null && !this.getAmmoType().equals("none") && !this.container.contains(this.getAmmoType())) {
            float0 -= 100000.0F;
        }

        if (this.Condition == 0) {
            float0 -= 100000.0F;
        }

        float0 += this.maxDamage * 10.0F;
        float0 += this.maxAngle * 5.0F;
        float0 -= this.minimumSwingTime * 0.1F;
        float0 -= this.swingTime;
        if (desc != null && desc.getInstance().getThreatLevel() <= 2 && this.soundRadius > 5) {
            if (float0 > 0.0F && this.soundRadius > float0) {
                float0 = 1.0F;
            }

            float0 -= this.soundRadius;
        }

        return float0;
    }

    /**
     * @return the ActualWeight
     */
    @Override
    public float getActualWeight() {
        float float0 = this.getScriptItem().getActualWeight();
        float0 += this.getWeaponPartWeightModifier(this.canon);
        float0 += this.getWeaponPartWeightModifier(this.clip);
        float0 += this.getWeaponPartWeightModifier(this.recoilpad);
        float0 += this.getWeaponPartWeightModifier(this.scope);
        float0 += this.getWeaponPartWeightModifier(this.sling);
        return float0 + this.getWeaponPartWeightModifier(this.stock);
    }

    /**
     * @return the Weight
     */
    @Override
    public float getWeight() {
        return this.getActualWeight();
    }

    @Override
    public float getContentsWeight() {
        float float0 = 0.0F;
        if (this.haveChamber() && this.isRoundChambered() && !StringUtils.isNullOrWhitespace(this.getAmmoType())) {
            Item item0 = ScriptManager.instance.FindItem(this.getAmmoType());
            if (item0 != null) {
                float0 += item0.getActualWeight();
            }
        }

        if (this.isContainsClip() && !StringUtils.isNullOrWhitespace(this.getMagazineType())) {
            Item item1 = ScriptManager.instance.FindItem(this.getMagazineType());
            if (item1 != null) {
                float0 += item1.getActualWeight();
            }
        }

        return float0 + super.getContentsWeight();
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        float float0 = 1.0F;
        float float1 = 1.0F;
        float float2 = 0.8F;
        float float3 = 1.0F;
        ColorInfo colorInfo = new ColorInfo();
        ObjectTooltip.LayoutItem layoutItem = layout.addItem();
        layoutItem.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", float0, float1, float2, float3);
        float float4 = (float)this.Condition / this.ConditionMax;
        Core.getInstance().getBadHighlitedColor().interp(Core.getInstance().getGoodHighlitedColor(), float4, colorInfo);
        layoutItem.setProgress(float4, colorInfo.getR(), colorInfo.getG(), colorInfo.getB(), 1.0F);
        if (this.getMaxDamage() > 0.0F) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Damage") + ":", float0, float1, float2, float3);
            float4 = this.getMaxDamage() + this.getMinDamage();
            float float5 = 5.0F;
            float float6 = float4 / float5;
            Core.getInstance().getBadHighlitedColor().interp(Core.getInstance().getGoodHighlitedColor(), float6, colorInfo);
            layoutItem.setProgress(float6, colorInfo.getR(), colorInfo.getG(), colorInfo.getB(), 1.0F);
        }

        if (this.isRanged()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Range") + ":", float0, float1, float2, 1.0F);
            float4 = this.getMaxRange(IsoPlayer.getInstance());
            float float7 = 40.0F;
            float float8 = float4 / float7;
            Core.getInstance().getBadHighlitedColor().interp(Core.getInstance().getGoodHighlitedColor(), float8, colorInfo);
            layoutItem.setProgress(float8, colorInfo.getR(), colorInfo.getG(), colorInfo.getB(), 1.0F);
        }

        if (this.isTwoHandWeapon() && !this.isRequiresEquippedBothHands()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_item_TwoHandWeapon"), float0, float1, float2, float3);
        }

        if (!StringUtils.isNullOrEmpty(this.getFireMode())) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_item_FireMode") + ":", float0, float1, float2, float3);
            layoutItem.setValue(Translator.getText("ContextMenu_FireMode_" + this.getFireMode()), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.CantAttackWithLowestEndurance) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(
                Translator.getText("Tooltip_weapon_Unusable_at_max_exertion"),
                Core.getInstance().getBadHighlitedColor().getR(),
                Core.getInstance().getBadHighlitedColor().getG(),
                Core.getInstance().getBadHighlitedColor().getB(),
                1.0F
            );
        }

        String string0 = this.getAmmoType();
        if (Core.getInstance().isNewReloading()) {
            if (this.getMaxAmmo() > 0) {
                String string1 = String.valueOf(this.getCurrentAmmoCount());
                if (this.isRoundChambered()) {
                    string1 = string1 + "+1";
                }

                layoutItem = layout.addItem();
                if (this.bulletName == null) {
                    if (this.getMagazineType() != null) {
                        this.bulletName = InventoryItemFactory.CreateItem(this.getMagazineType()).getDisplayName();
                    } else {
                        this.bulletName = InventoryItemFactory.CreateItem(this.getAmmoType()).getDisplayName();
                    }
                }

                layoutItem.setLabel(this.bulletName + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem.setValue(string1 + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (this.isJammed()) {
                layoutItem = layout.addItem();
                layoutItem.setLabel(
                    Translator.getText("Tooltip_weapon_Jammed"),
                    Core.getInstance().getBadHighlitedColor().getR(),
                    Core.getInstance().getBadHighlitedColor().getG(),
                    Core.getInstance().getBadHighlitedColor().getB(),
                    1.0F
                );
            } else if (this.haveChamber() && !this.isRoundChambered() && this.getCurrentAmmoCount() > 0) {
                layoutItem = layout.addItem();
                String string2 = this.isSpentRoundChambered() ? "Tooltip_weapon_SpentRoundChambered" : "Tooltip_weapon_NoRoundChambered";
                layoutItem.setLabel(
                    Translator.getText(string2),
                    Core.getInstance().getBadHighlitedColor().getR(),
                    Core.getInstance().getBadHighlitedColor().getG(),
                    Core.getInstance().getBadHighlitedColor().getB(),
                    1.0F
                );
            } else if (this.getSpentRoundCount() > 0) {
                layoutItem = layout.addItem();
                layoutItem.setLabel(
                    Translator.getText("Tooltip_weapon_SpentRounds") + ":",
                    Core.getInstance().getBadHighlitedColor().getR(),
                    Core.getInstance().getBadHighlitedColor().getG(),
                    Core.getInstance().getBadHighlitedColor().getB(),
                    1.0F
                );
                layoutItem.setValue(this.getSpentRoundCount() + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
                if (this.isContainsClip()) {
                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_weapon_ContainsClip"), 1.0F, 1.0F, 0.8F, 1.0F);
                } else {
                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_weapon_NoClip"), 1.0F, 1.0F, 0.8F, 1.0F);
                }
            }
        } else {
            if (string0 == null && this.hasModData()) {
                Object object0 = this.getModData().rawget("defaultAmmo");
                if (object0 instanceof String) {
                    string0 = (String)object0;
                }
            }

            if (string0 != null) {
                Item item = ScriptManager.instance.FindItem(string0);
                if (item == null) {
                    item = ScriptManager.instance.FindItem(this.getModule() + "." + string0);
                }

                if (item != null) {
                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", float0, float1, float2, float3);
                    layoutItem.setValue(item.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
                }

                Object object1 = this.getModData().rawget("currentCapacity");
                Object object2 = this.getModData().rawget("maxCapacity");
                if (object1 instanceof Double && object2 instanceof Double) {
                    String string3 = ((Double)object1).intValue() + " / " + ((Double)object2).intValue();
                    Object object3 = this.getModData().rawget("roundChambered");
                    if (object3 instanceof Double && ((Double)object3).intValue() == 1) {
                        string3 = ((Double)object1).intValue() + "+1 / " + ((Double)object2).intValue();
                    } else {
                        Object object4 = this.getModData().rawget("emptyShellChambered");
                        if (object4 instanceof Double && ((Double)object4).intValue() == 1) {
                            string3 = ((Double)object1).intValue() + "+x / " + ((Double)object2).intValue();
                        }
                    }

                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    layoutItem.setValue(string3, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        ObjectTooltip.Layout _layout = tooltipUI.beginLayout();
        if (this.getStock() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Stock") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getStock().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getSling() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Sling") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getSling().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getScope() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Scope") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getScope().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getCanon() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Canon") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getCanon().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getClip() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Clip") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getClip().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getRecoilpad() != null) {
            layoutItem = _layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_RecoilPad") + ":", float0, float1, float2, float3);
            layoutItem.setValue(this.getRecoilpad().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (!_layout.items.isEmpty()) {
            layout.next = _layout;
            _layout.nextPadY = tooltipUI.getLineSpacing();
        } else {
            tooltipUI.endLayout(_layout);
        }
    }

    public float getDamageMod(IsoGameCharacter chr) {
        int int0 = chr.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt")) {
            if (int0 >= 3 && int0 <= 6) {
                return 1.1F;
            }

            if (int0 >= 7) {
                return 1.2F;
            }
        }

        int int1 = chr.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe")) {
            if (int1 >= 3 && int1 <= 6) {
                return 1.1F;
            }

            if (int1 >= 7) {
                return 1.2F;
            }
        }

        int int2 = chr.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear")) {
            if (int2 >= 3 && int2 <= 6) {
                return 1.1F;
            }

            if (int2 >= 7) {
                return 1.2F;
            }
        }

        return 1.0F;
    }

    public float getRangeMod(IsoGameCharacter chr) {
        int int0 = chr.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt") && int0 >= 7) {
            return 1.2F;
        } else {
            int int1 = chr.getPerkLevel(PerkFactory.Perks.Axe);
            if (this.ScriptItem.Categories.contains("Axe") && int1 >= 7) {
                return 1.2F;
            } else {
                int int2 = chr.getPerkLevel(PerkFactory.Perks.Spear);
                return this.ScriptItem.Categories.contains("Spear") && int2 >= 7 ? 1.2F : 1.0F;
            }
        }
    }

    public float getFatigueMod(IsoGameCharacter chr) {
        int int0 = chr.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt") && int0 >= 8) {
            return 0.8F;
        } else {
            int int1 = chr.getPerkLevel(PerkFactory.Perks.Axe);
            if (this.ScriptItem.Categories.contains("Axe") && int1 >= 8) {
                return 0.8F;
            } else {
                int int2 = chr.getPerkLevel(PerkFactory.Perks.Spear);
                return this.ScriptItem.Categories.contains("Spear") && int2 >= 8 ? 0.8F : 1.0F;
            }
        }
    }

    public float getKnockbackMod(IsoGameCharacter chr) {
        int int0 = chr.getPerkLevel(PerkFactory.Perks.Axe);
        return this.ScriptItem.Categories.contains("Axe") && int0 >= 6 ? 2.0F : 1.0F;
    }

    public float getSpeedMod(IsoGameCharacter chr) {
        if (this.ScriptItem.Categories.contains("Blunt")) {
            int int0 = chr.getPerkLevel(PerkFactory.Perks.Blunt);
            if (int0 >= 10) {
                return 0.65F;
            }

            if (int0 >= 9) {
                return 0.68F;
            }

            if (int0 >= 8) {
                return 0.71F;
            }

            if (int0 >= 7) {
                return 0.74F;
            }

            if (int0 >= 6) {
                return 0.77F;
            }

            if (int0 >= 5) {
                return 0.8F;
            }

            if (int0 >= 4) {
                return 0.83F;
            }

            if (int0 >= 3) {
                return 0.86F;
            }

            if (int0 >= 2) {
                return 0.9F;
            }

            if (int0 >= 1) {
                return 0.95F;
            }
        }

        if (this.ScriptItem.Categories.contains("Axe")) {
            int int1 = chr.getPerkLevel(PerkFactory.Perks.Axe);
            float float0 = 1.0F;
            if (chr.Traits.Axeman.isSet()) {
                float0 = 0.95F;
            }

            if (int1 >= 10) {
                return 0.65F * float0;
            } else if (int1 >= 9) {
                return 0.68F * float0;
            } else if (int1 >= 8) {
                return 0.71F * float0;
            } else if (int1 >= 7) {
                return 0.74F * float0;
            } else if (int1 >= 6) {
                return 0.77F * float0;
            } else if (int1 >= 5) {
                return 0.8F * float0;
            } else if (int1 >= 4) {
                return 0.83F * float0;
            } else if (int1 >= 3) {
                return 0.86F * float0;
            } else if (int1 >= 2) {
                return 0.9F * float0;
            } else {
                return int1 >= 1 ? 0.95F * float0 : 1.0F * float0;
            }
        } else {
            if (this.ScriptItem.Categories.contains("Spear")) {
                int int2 = chr.getPerkLevel(PerkFactory.Perks.Spear);
                if (int2 >= 10) {
                    return 0.65F;
                }

                if (int2 >= 9) {
                    return 0.68F;
                }

                if (int2 >= 8) {
                    return 0.71F;
                }

                if (int2 >= 7) {
                    return 0.74F;
                }

                if (int2 >= 6) {
                    return 0.77F;
                }

                if (int2 >= 5) {
                    return 0.8F;
                }

                if (int2 >= 4) {
                    return 0.83F;
                }

                if (int2 >= 3) {
                    return 0.86F;
                }

                if (int2 >= 2) {
                    return 0.9F;
                }

                if (int2 >= 1) {
                    return 0.95F;
                }
            }

            return 1.0F;
        }
    }

    public float getToHitMod(IsoGameCharacter chr) {
        int int0 = chr.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt")) {
            if (int0 == 1) {
                return 1.2F;
            }

            if (int0 == 2) {
                return 1.3F;
            }

            if (int0 == 3) {
                return 1.4F;
            }

            if (int0 == 4) {
                return 1.5F;
            }

            if (int0 == 5) {
                return 1.6F;
            }

            if (int0 == 6) {
                return 1.7F;
            }

            if (int0 == 7) {
                return 1.8F;
            }

            if (int0 == 8) {
                return 1.9F;
            }

            if (int0 == 9) {
                return 2.0F;
            }

            if (int0 == 10) {
                return 100.0F;
            }
        }

        int int1 = chr.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe")) {
            if (int1 == 1) {
                return 1.2F;
            }

            if (int1 == 2) {
                return 1.3F;
            }

            if (int1 == 3) {
                return 1.4F;
            }

            if (int1 == 4) {
                return 1.5F;
            }

            if (int1 == 5) {
                return 1.6F;
            }

            if (int1 == 6) {
                return 1.7F;
            }

            if (int1 == 7) {
                return 1.8F;
            }

            if (int1 == 8) {
                return 1.9F;
            }

            if (int1 == 9) {
                return 2.0F;
            }

            if (int1 == 10) {
                return 100.0F;
            }
        }

        int int2 = chr.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear")) {
            if (int2 == 1) {
                return 1.2F;
            }

            if (int2 == 2) {
                return 1.3F;
            }

            if (int2 == 3) {
                return 1.4F;
            }

            if (int2 == 4) {
                return 1.5F;
            }

            if (int2 == 5) {
                return 1.6F;
            }

            if (int2 == 6) {
                return 1.7F;
            }

            if (int2 == 7) {
                return 1.8F;
            }

            if (int2 == 8) {
                return 1.9F;
            }

            if (int2 == 9) {
                return 2.0F;
            }

            if (int2 == 10) {
                return 100.0F;
            }
        }

        return 1.0F;
    }

    /**
     * @return the angleFalloff
     */
    public boolean isAngleFalloff() {
        return this.angleFalloff;
    }

    /**
     * 
     * @param _angleFalloff the angleFalloff to set
     */
    public void setAngleFalloff(boolean _angleFalloff) {
        this.angleFalloff = _angleFalloff;
    }

    /**
     * @return the bCanBarracade
     */
    public boolean isCanBarracade() {
        return this.bCanBarracade;
    }

    /**
     * 
     * @param _bCanBarracade the bCanBarracade to set
     */
    public void setCanBarracade(boolean _bCanBarracade) {
        this.bCanBarracade = _bCanBarracade;
    }

    /**
     * @return the doSwingBeforeImpact
     */
    public float getDoSwingBeforeImpact() {
        return this.doSwingBeforeImpact;
    }

    /**
     * 
     * @param _doSwingBeforeImpact the doSwingBeforeImpact to set
     */
    public void setDoSwingBeforeImpact(float _doSwingBeforeImpact) {
        this.doSwingBeforeImpact = _doSwingBeforeImpact;
    }

    /**
     * @return the impactSound
     */
    public String getImpactSound() {
        return this.impactSound;
    }

    /**
     * 
     * @param _impactSound the impactSound to set
     */
    public void setImpactSound(String _impactSound) {
        this.impactSound = _impactSound;
    }

    /**
     * @return the knockBackOnNoDeath
     */
    public boolean isKnockBackOnNoDeath() {
        return this.knockBackOnNoDeath;
    }

    /**
     * 
     * @param _knockBackOnNoDeath the knockBackOnNoDeath to set
     */
    public void setKnockBackOnNoDeath(boolean _knockBackOnNoDeath) {
        this.knockBackOnNoDeath = _knockBackOnNoDeath;
    }

    /**
     * @return the maxAngle
     */
    public float getMaxAngle() {
        return this.maxAngle;
    }

    /**
     * 
     * @param _maxAngle the maxAngle to set
     */
    public void setMaxAngle(float _maxAngle) {
        this.maxAngle = _maxAngle;
    }

    /**
     * @return the maxDamage
     */
    public float getMaxDamage() {
        return this.maxDamage;
    }

    /**
     * 
     * @param _maxDamage the maxDamage to set
     */
    public void setMaxDamage(float _maxDamage) {
        this.maxDamage = _maxDamage;
    }

    /**
     * @return the maxHitCount
     */
    public int getMaxHitCount() {
        return this.maxHitCount;
    }

    /**
     * 
     * @param _maxHitCount the maxHitCount to set
     */
    public void setMaxHitCount(int _maxHitCount) {
        this.maxHitCount = _maxHitCount;
    }

    /**
     * @return the maxRange
     */
    public float getMaxRange() {
        return this.maxRange;
    }

    public float getMaxRange(IsoGameCharacter owner) {
        return this.isRanged() ? this.maxRange + this.getAimingPerkRangeModifier() * (owner.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F) : this.maxRange;
    }

    /**
     * 
     * @param _maxRange the maxRange to set
     */
    public void setMaxRange(float _maxRange) {
        this.maxRange = _maxRange;
    }

    /**
     * @return the ranged
     */
    public boolean isRanged() {
        return this.ranged;
    }

    /**
     * 
     * @param _ranged the ranged to set
     */
    public void setRanged(boolean _ranged) {
        this.ranged = _ranged;
    }

    /**
     * @return the minAngle
     */
    public float getMinAngle() {
        return this.minAngle;
    }

    /**
     * 
     * @param _minAngle the minAngle to set
     */
    public void setMinAngle(float _minAngle) {
        this.minAngle = _minAngle;
    }

    /**
     * @return the minDamage
     */
    public float getMinDamage() {
        return this.minDamage;
    }

    /**
     * 
     * @param _minDamage the minDamage to set
     */
    public void setMinDamage(float _minDamage) {
        this.minDamage = _minDamage;
    }

    /**
     * @return the minimumSwingTime
     */
    public float getMinimumSwingTime() {
        return this.minimumSwingTime;
    }

    /**
     * 
     * @param _minimumSwingTime the minimumSwingTime to set
     */
    public void setMinimumSwingTime(float _minimumSwingTime) {
        this.minimumSwingTime = _minimumSwingTime;
    }

    /**
     * @return the minRange
     */
    public float getMinRange() {
        return this.minRange;
    }

    /**
     * 
     * @param _minRange the minRange to set
     */
    public void setMinRange(float _minRange) {
        this.minRange = _minRange;
    }

    /**
     * @return the noiseFactor
     */
    public float getNoiseFactor() {
        return this.noiseFactor;
    }

    /**
     * 
     * @param _noiseFactor the noiseFactor to set
     */
    public void setNoiseFactor(float _noiseFactor) {
        this.noiseFactor = _noiseFactor;
    }

    /**
     * @return the otherHandRequire
     */
    public String getOtherHandRequire() {
        return this.otherHandRequire;
    }

    /**
     * 
     * @param _otherHandRequire the otherHandRequire to set
     */
    public void setOtherHandRequire(String _otherHandRequire) {
        this.otherHandRequire = _otherHandRequire;
    }

    /**
     * @return the otherHandUse
     */
    public boolean isOtherHandUse() {
        return this.otherHandUse;
    }

    /**
     * 
     * @param _otherHandUse the otherHandUse to set
     */
    public void setOtherHandUse(boolean _otherHandUse) {
        this.otherHandUse = _otherHandUse;
    }

    /**
     * @return the physicsObject
     */
    public String getPhysicsObject() {
        return this.physicsObject;
    }

    /**
     * 
     * @param _physicsObject the physicsObject to set
     */
    public void setPhysicsObject(String _physicsObject) {
        this.physicsObject = _physicsObject;
    }

    /**
     * @return the pushBackMod
     */
    public float getPushBackMod() {
        return this.pushBackMod;
    }

    /**
     * 
     * @param _pushBackMod the pushBackMod to set
     */
    public void setPushBackMod(float _pushBackMod) {
        this.pushBackMod = _pushBackMod;
    }

    /**
     * @return the rangeFalloff
     */
    public boolean isRangeFalloff() {
        return this.rangeFalloff;
    }

    /**
     * 
     * @param _rangeFalloff the rangeFalloff to set
     */
    public void setRangeFalloff(boolean _rangeFalloff) {
        this.rangeFalloff = _rangeFalloff;
    }

    /**
     * @return the shareDamage
     */
    public boolean isShareDamage() {
        return this.shareDamage;
    }

    /**
     * 
     * @param _shareDamage the shareDamage to set
     */
    public void setShareDamage(boolean _shareDamage) {
        this.shareDamage = _shareDamage;
    }

    /**
     * @return the soundRadius
     */
    public int getSoundRadius() {
        return this.soundRadius;
    }

    /**
     * 
     * @param _soundRadius the soundRadius to set
     */
    public void setSoundRadius(int _soundRadius) {
        this.soundRadius = _soundRadius;
    }

    /**
     * @return the soundVolume
     */
    public int getSoundVolume() {
        return this.soundVolume;
    }

    /**
     * 
     * @param _soundVolume the soundVolume to set
     */
    public void setSoundVolume(int _soundVolume) {
        this.soundVolume = _soundVolume;
    }

    /**
     * @return the splatBloodOnNoDeath
     */
    public boolean isSplatBloodOnNoDeath() {
        return this.splatBloodOnNoDeath;
    }

    /**
     * 
     * @param _splatBloodOnNoDeath the splatBloodOnNoDeath to set
     */
    public void setSplatBloodOnNoDeath(boolean _splatBloodOnNoDeath) {
        this.splatBloodOnNoDeath = _splatBloodOnNoDeath;
    }

    /**
     * @return the splatNumber
     */
    public int getSplatNumber() {
        return this.splatNumber;
    }

    /**
     * 
     * @param _splatNumber the splatNumber to set
     */
    public void setSplatNumber(int _splatNumber) {
        this.splatNumber = _splatNumber;
    }

    /**
     * @return the swingSound
     */
    public String getSwingSound() {
        return this.swingSound;
    }

    /**
     * 
     * @param _swingSound the swingSound to set
     */
    public void setSwingSound(String _swingSound) {
        this.swingSound = _swingSound;
    }

    /**
     * @return the swingTime
     */
    public float getSwingTime() {
        return this.swingTime;
    }

    /**
     * 
     * @param _swingTime the swingTime to set
     */
    public void setSwingTime(float _swingTime) {
        this.swingTime = _swingTime;
    }

    /**
     * @return the toHitModifier
     */
    public float getToHitModifier() {
        return this.toHitModifier;
    }

    /**
     * 
     * @param _toHitModifier the toHitModifier to set
     */
    public void setToHitModifier(float _toHitModifier) {
        this.toHitModifier = _toHitModifier;
    }

    /**
     * @return the useEndurance
     */
    public boolean isUseEndurance() {
        return this.useEndurance;
    }

    /**
     * 
     * @param _useEndurance the useEndurance to set
     */
    public void setUseEndurance(boolean _useEndurance) {
        this.useEndurance = _useEndurance;
    }

    /**
     * @return the useSelf
     */
    public boolean isUseSelf() {
        return this.useSelf;
    }

    /**
     * 
     * @param _useSelf the useSelf to set
     */
    public void setUseSelf(boolean _useSelf) {
        this.useSelf = _useSelf;
    }

    /**
     * @return the weaponSprite
     */
    public String getWeaponSprite() {
        return this.weaponSprite;
    }

    /**
     * 
     * @param _weaponSprite the weaponSprite to set
     */
    public void setWeaponSprite(String _weaponSprite) {
        this.weaponSprite = _weaponSprite;
    }

    /**
     * @return the otherBoost
     */
    public float getOtherBoost() {
        return this.otherBoost;
    }

    /**
     * 
     * @param _otherBoost the otherBoost to set
     */
    public void setOtherBoost(float _otherBoost) {
        this.otherBoost = _otherBoost;
    }

    /**
     * @return the DoorDamage
     */
    public int getDoorDamage() {
        return this.DoorDamage;
    }

    /**
     * 
     * @param _DoorDamage the DoorDamage to set
     */
    public void setDoorDamage(int _DoorDamage) {
        this.DoorDamage = _DoorDamage;
    }

    /**
     * @return the doorHitSound
     */
    public String getDoorHitSound() {
        return this.doorHitSound;
    }

    /**
     * 
     * @param _doorHitSound the doorHitSound to set
     */
    public void setDoorHitSound(String _doorHitSound) {
        this.doorHitSound = _doorHitSound;
    }

    /**
     * @return the ConditionLowerChance
     */
    public int getConditionLowerChance() {
        return this.ConditionLowerChance;
    }

    /**
     * 
     * @param _ConditionLowerChance the ConditionLowerChance to set
     */
    public void setConditionLowerChance(int _ConditionLowerChance) {
        this.ConditionLowerChance = _ConditionLowerChance;
    }

    /**
     * @return the MultipleHitConditionAffected
     */
    public boolean isMultipleHitConditionAffected() {
        return this.MultipleHitConditionAffected;
    }

    /**
     * 
     * @param _MultipleHitConditionAffected the MultipleHitConditionAffected to set
     */
    public void setMultipleHitConditionAffected(boolean _MultipleHitConditionAffected) {
        this.MultipleHitConditionAffected = _MultipleHitConditionAffected;
    }

    /**
     * @return the shareEndurance
     */
    public boolean isShareEndurance() {
        return this.shareEndurance;
    }

    /**
     * 
     * @param _shareEndurance the shareEndurance to set
     */
    public void setShareEndurance(boolean _shareEndurance) {
        this.shareEndurance = _shareEndurance;
    }

    /**
     * @return the AlwaysKnockdown
     */
    public boolean isAlwaysKnockdown() {
        return this.AlwaysKnockdown;
    }

    /**
     * 
     * @param _AlwaysKnockdown the AlwaysKnockdown to set
     */
    public void setAlwaysKnockdown(boolean _AlwaysKnockdown) {
        this.AlwaysKnockdown = _AlwaysKnockdown;
    }

    /**
     * @return the EnduranceMod
     */
    public float getEnduranceMod() {
        return this.EnduranceMod;
    }

    /**
     * 
     * @param _EnduranceMod the EnduranceMod to set
     */
    public void setEnduranceMod(float _EnduranceMod) {
        this.EnduranceMod = _EnduranceMod;
    }

    /**
     * @return the KnockdownMod
     */
    public float getKnockdownMod() {
        return this.KnockdownMod;
    }

    /**
     * 
     * @param _KnockdownMod the KnockdownMod to set
     */
    public void setKnockdownMod(float _KnockdownMod) {
        this.KnockdownMod = _KnockdownMod;
    }

    /**
     * @return the CantAttackWithLowestEndurance
     */
    public boolean isCantAttackWithLowestEndurance() {
        return this.CantAttackWithLowestEndurance;
    }

    /**
     * 
     * @param _CantAttackWithLowestEndurance the CantAttackWithLowestEndurance to set
     */
    public void setCantAttackWithLowestEndurance(boolean _CantAttackWithLowestEndurance) {
        this.CantAttackWithLowestEndurance = _CantAttackWithLowestEndurance;
    }

    public boolean isAimedFirearm() {
        return this.bIsAimedFirearm;
    }

    public boolean isAimedHandWeapon() {
        return this.bIsAimedHandWeapon;
    }

    public int getProjectileCount() {
        return this.ProjectileCount;
    }

    public void setProjectileCount(int count) {
        this.ProjectileCount = count;
    }

    public float getAimingMod() {
        return this.aimingMod;
    }

    public boolean isAimed() {
        return this.bIsAimedFirearm || this.bIsAimedHandWeapon;
    }

    public void setCriticalChance(float criticalChance) {
        this.CriticalChance = criticalChance;
    }

    public float getCriticalChance() {
        return this.CriticalChance;
    }

    public void setSubCategory(String subcategory) {
        this.SubCategory = subcategory;
    }

    public String getSubCategory() {
        return this.SubCategory;
    }

    public void setZombieHitSound(String _hitSound) {
        this.hitSound = _hitSound;
    }

    public String getZombieHitSound() {
        return this.hitSound;
    }

    public ArrayList<String> getCategories() {
        return this.Categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.Categories = categories;
    }

    public int getAimingPerkCritModifier() {
        return this.AimingPerkCritModifier;
    }

    public void setAimingPerkCritModifier(int aimingPerkCritModifier) {
        this.AimingPerkCritModifier = aimingPerkCritModifier;
    }

    public float getAimingPerkRangeModifier() {
        return this.AimingPerkRangeModifier;
    }

    public void setAimingPerkRangeModifier(float aimingPerkRangeModifier) {
        this.AimingPerkRangeModifier = aimingPerkRangeModifier;
    }

    public int getHitChance() {
        return this.HitChance;
    }

    public void setHitChance(int hitChance) {
        this.HitChance = hitChance;
    }

    public float getAimingPerkHitChanceModifier() {
        return this.AimingPerkHitChanceModifier;
    }

    public void setAimingPerkHitChanceModifier(float aimingPerkHitChanceModifier) {
        this.AimingPerkHitChanceModifier = aimingPerkHitChanceModifier;
    }

    public float getAimingPerkMinAngleModifier() {
        return this.AimingPerkMinAngleModifier;
    }

    public void setAimingPerkMinAngleModifier(float aimingPerkMinAngleModifier) {
        this.AimingPerkMinAngleModifier = aimingPerkMinAngleModifier;
    }

    public int getRecoilDelay() {
        return this.RecoilDelay;
    }

    public void setRecoilDelay(int recoilDelay) {
        this.RecoilDelay = recoilDelay;
    }

    public boolean isPiercingBullets() {
        return this.PiercingBullets;
    }

    public void setPiercingBullets(boolean piercingBullets) {
        this.PiercingBullets = piercingBullets;
    }

    public float getSoundGain() {
        return this.soundGain;
    }

    public void setSoundGain(float _soundGain) {
        this.soundGain = _soundGain;
    }

    public WeaponPart getScope() {
        return this.scope;
    }

    public void setScope(WeaponPart _scope) {
        this.scope = _scope;
    }

    public WeaponPart getClip() {
        return this.clip;
    }

    public void setClip(WeaponPart _clip) {
        this.clip = _clip;
    }

    public WeaponPart getCanon() {
        return this.canon;
    }

    public void setCanon(WeaponPart _canon) {
        this.canon = _canon;
    }

    public WeaponPart getRecoilpad() {
        return this.recoilpad;
    }

    public void setRecoilpad(WeaponPart _recoilpad) {
        this.recoilpad = _recoilpad;
    }

    public int getClipSize() {
        return this.ClipSize;
    }

    public void setClipSize(int capacity) {
        this.ClipSize = capacity;
        this.getModData().rawset("maxCapacity", BoxedStaticValues.toDouble(capacity));
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, output);
        if (this.maxRange != 1.0F) {
            bitHeaderWrite.addFlags(1);
            output.putFloat(this.maxRange);
        }

        if (this.minRangeRanged != 0.0F) {
            bitHeaderWrite.addFlags(2);
            output.putFloat(this.minRangeRanged);
        }

        if (this.ClipSize != 0) {
            bitHeaderWrite.addFlags(4);
            output.putInt(this.ClipSize);
        }

        if (this.minDamage != 0.4F) {
            bitHeaderWrite.addFlags(8);
            output.putFloat(this.minDamage);
        }

        if (this.maxDamage != 1.5F) {
            bitHeaderWrite.addFlags(16);
            output.putFloat(this.maxDamage);
        }

        if (this.RecoilDelay != 0) {
            bitHeaderWrite.addFlags(32);
            output.putInt(this.RecoilDelay);
        }

        if (this.aimingTime != 0) {
            bitHeaderWrite.addFlags(64);
            output.putInt(this.aimingTime);
        }

        if (this.reloadTime != 0) {
            bitHeaderWrite.addFlags(128);
            output.putInt(this.reloadTime);
        }

        if (this.HitChance != 0) {
            bitHeaderWrite.addFlags(256);
            output.putInt(this.HitChance);
        }

        if (this.minAngle != 0.5F) {
            bitHeaderWrite.addFlags(512);
            output.putFloat(this.minAngle);
        }

        if (this.getScope() != null) {
            bitHeaderWrite.addFlags(1024);
            output.putShort(this.getScope().getRegistry_id());
        }

        if (this.getClip() != null) {
            bitHeaderWrite.addFlags(2048);
            output.putShort(this.getClip().getRegistry_id());
        }

        if (this.getRecoilpad() != null) {
            bitHeaderWrite.addFlags(4096);
            output.putShort(this.getRecoilpad().getRegistry_id());
        }

        if (this.getSling() != null) {
            bitHeaderWrite.addFlags(8192);
            output.putShort(this.getSling().getRegistry_id());
        }

        if (this.getStock() != null) {
            bitHeaderWrite.addFlags(16384);
            output.putShort(this.getStock().getRegistry_id());
        }

        if (this.getCanon() != null) {
            bitHeaderWrite.addFlags(32768);
            output.putShort(this.getCanon().getRegistry_id());
        }

        if (this.getExplosionTimer() != 0) {
            bitHeaderWrite.addFlags(65536);
            output.putInt(this.getExplosionTimer());
        }

        if (this.maxAngle != 1.0F) {
            bitHeaderWrite.addFlags(131072);
            output.putFloat(this.maxAngle);
        }

        if (this.bloodLevel != 0.0F) {
            bitHeaderWrite.addFlags(262144);
            output.putFloat(this.bloodLevel);
        }

        if (this.containsClip) {
            bitHeaderWrite.addFlags(524288);
        }

        if (this.roundChambered) {
            bitHeaderWrite.addFlags(1048576);
        }

        if (this.isJammed) {
            bitHeaderWrite.addFlags(2097152);
        }

        if (!StringUtils.equals(this.weaponSprite, this.getScriptItem().getWeaponSprite())) {
            bitHeaderWrite.addFlags(4194304);
            GameWindow.WriteString(output, this.weaponSprite);
        }

        bitHeaderWrite.write();
        bitHeaderWrite.release();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.maxRange = 1.0F;
        this.minRangeRanged = 0.0F;
        this.ClipSize = 0;
        this.minDamage = 0.4F;
        this.maxDamage = 1.5F;
        this.RecoilDelay = 0;
        this.aimingTime = 0;
        this.reloadTime = 0;
        this.HitChance = 0;
        this.minAngle = 0.5F;
        this.scope = null;
        this.clip = null;
        this.recoilpad = null;
        this.sling = null;
        this.stock = null;
        this.canon = null;
        this.explosionTimer = 0;
        this.maxAngle = 1.0F;
        this.bloodLevel = 0.0F;
        this.containsClip = false;
        this.roundChambered = false;
        this.isJammed = false;
        this.weaponSprite = this.getScriptItem().getWeaponSprite();
        BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Integer, input);
        if (!bitHeaderRead.equals(0)) {
            if (bitHeaderRead.hasFlags(1)) {
                this.setMaxRange(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(2)) {
                this.setMinRangeRanged(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(4)) {
                this.setClipSize(input.getInt());
            }

            if (bitHeaderRead.hasFlags(8)) {
                this.setMinDamage(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(16)) {
                this.setMaxDamage(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(32)) {
                this.setRecoilDelay(input.getInt());
            }

            if (bitHeaderRead.hasFlags(64)) {
                this.setAimingTime(input.getInt());
            }

            if (bitHeaderRead.hasFlags(128)) {
                this.setReloadTime(input.getInt());
            }

            if (bitHeaderRead.hasFlags(256)) {
                this.setHitChance(input.getInt());
            }

            if (bitHeaderRead.hasFlags(512)) {
                this.setMinAngle(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(1024)) {
                InventoryItem item0 = InventoryItemFactory.CreateItem(input.getShort());
                if (item0 != null && item0 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item0, false);
                }
            }

            if (bitHeaderRead.hasFlags(2048)) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(input.getShort());
                if (item1 != null && item1 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item1, false);
                }
            }

            if (bitHeaderRead.hasFlags(4096)) {
                InventoryItem item2 = InventoryItemFactory.CreateItem(input.getShort());
                if (item2 != null && item2 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item2, false);
                }
            }

            if (bitHeaderRead.hasFlags(8192)) {
                InventoryItem item3 = InventoryItemFactory.CreateItem(input.getShort());
                if (item3 != null && item3 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item3, false);
                }
            }

            if (bitHeaderRead.hasFlags(16384)) {
                InventoryItem item4 = InventoryItemFactory.CreateItem(input.getShort());
                if (item4 != null && item4 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item4, false);
                }
            }

            if (bitHeaderRead.hasFlags(32768)) {
                InventoryItem item5 = InventoryItemFactory.CreateItem(input.getShort());
                if (item5 != null && item5 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)item5, false);
                }
            }

            if (bitHeaderRead.hasFlags(65536)) {
                this.setExplosionTimer(input.getInt());
            }

            if (bitHeaderRead.hasFlags(131072)) {
                this.setMaxAngle(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(262144)) {
                this.setBloodLevel(input.getFloat());
            }

            this.setContainsClip(bitHeaderRead.hasFlags(524288));
            if (StringUtils.isNullOrWhitespace(this.magazineType)) {
                this.setContainsClip(false);
            }

            this.setRoundChambered(bitHeaderRead.hasFlags(1048576));
            this.setJammed(bitHeaderRead.hasFlags(2097152));
            if (bitHeaderRead.hasFlags(4194304)) {
                this.setWeaponSprite(GameWindow.ReadStringUTF(input));
            }
        }

        bitHeaderRead.release();
    }

    public float getMinRangeRanged() {
        return this.minRangeRanged;
    }

    public void setMinRangeRanged(float _minRangeRanged) {
        this.minRangeRanged = _minRangeRanged;
    }

    public int getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime(int _reloadTime) {
        this.reloadTime = _reloadTime;
    }

    public WeaponPart getSling() {
        return this.sling;
    }

    public void setSling(WeaponPart _sling) {
        this.sling = _sling;
    }

    public int getAimingTime() {
        return this.aimingTime;
    }

    public void setAimingTime(int _aimingTime) {
        this.aimingTime = _aimingTime;
    }

    public WeaponPart getStock() {
        return this.stock;
    }

    public void setStock(WeaponPart _stock) {
        this.stock = _stock;
    }

    public int getTreeDamage() {
        return this.treeDamage;
    }

    public void setTreeDamage(int _treeDamage) {
        this.treeDamage = _treeDamage;
    }

    public String getBulletOutSound() {
        return this.bulletOutSound;
    }

    public void setBulletOutSound(String _bulletOutSound) {
        this.bulletOutSound = _bulletOutSound;
    }

    public String getShellFallSound() {
        return this.shellFallSound;
    }

    public void setShellFallSound(String _shellFallSound) {
        this.shellFallSound = _shellFallSound;
    }

    private void addPartToList(String string, ArrayList<WeaponPart> arrayList) {
        WeaponPart weaponPart = this.getWeaponPart(string);
        if (weaponPart != null) {
            arrayList.add(weaponPart);
        }
    }

    public ArrayList<WeaponPart> getAllWeaponParts() {
        return this.getAllWeaponParts(new ArrayList<>());
    }

    public ArrayList<WeaponPart> getAllWeaponParts(ArrayList<WeaponPart> result) {
        result.clear();
        this.addPartToList("Scope", result);
        this.addPartToList("Clip", result);
        this.addPartToList("Sling", result);
        this.addPartToList("Canon", result);
        this.addPartToList("Stock", result);
        this.addPartToList("RecoilPad", result);
        return result;
    }

    public void setWeaponPart(String type, WeaponPart part) {
        if (part == null || type.equalsIgnoreCase(part.getPartType())) {
            if ("Scope".equalsIgnoreCase(type)) {
                this.scope = part;
            } else if ("Clip".equalsIgnoreCase(type)) {
                this.clip = part;
            } else if ("Sling".equalsIgnoreCase(type)) {
                this.sling = part;
            } else if ("Canon".equalsIgnoreCase(type)) {
                this.canon = part;
            } else if ("Stock".equalsIgnoreCase(type)) {
                this.stock = part;
            } else if ("RecoilPad".equalsIgnoreCase(type)) {
                this.recoilpad = part;
            } else {
                DebugLog.log("ERROR: unknown WeaponPart type \"" + type + "\"");
            }
        }
    }

    public WeaponPart getWeaponPart(String type) {
        if ("Scope".equalsIgnoreCase(type)) {
            return this.scope;
        } else if ("Clip".equalsIgnoreCase(type)) {
            return this.clip;
        } else if ("Sling".equalsIgnoreCase(type)) {
            return this.sling;
        } else if ("Canon".equalsIgnoreCase(type)) {
            return this.canon;
        } else if ("Stock".equalsIgnoreCase(type)) {
            return this.stock;
        } else if ("RecoilPad".equalsIgnoreCase(type)) {
            return this.recoilpad;
        } else {
            DebugLog.log("ERROR: unknown WeaponPart type \"" + type + "\"");
            return null;
        }
    }

    public float getWeaponPartWeightModifier(String type) {
        return this.getWeaponPartWeightModifier(this.getWeaponPart(type));
    }

    public float getWeaponPartWeightModifier(WeaponPart part) {
        return part == null ? 0.0F : part.getWeightModifier();
    }

    public void attachWeaponPart(WeaponPart part) {
        this.attachWeaponPart(part, true);
    }

    public void attachWeaponPart(WeaponPart part, boolean doChange) {
        if (part != null) {
            WeaponPart weaponPart = this.getWeaponPart(part.getPartType());
            if (weaponPart != null) {
                this.detachWeaponPart(weaponPart);
            }

            this.setWeaponPart(part.getPartType(), part);
            if (doChange) {
                this.setMaxRange(this.getMaxRange() + part.getMaxRange());
                this.setMinRangeRanged(this.getMinRangeRanged() + part.getMinRangeRanged());
                this.setClipSize(this.getClipSize() + part.getClipSize());
                this.setReloadTime(this.getReloadTime() + part.getReloadTime());
                this.setRecoilDelay((int)(this.getRecoilDelay() + part.getRecoilDelay()));
                this.setAimingTime(this.getAimingTime() + part.getAimingTime());
                this.setHitChance(this.getHitChance() + part.getHitChance());
                this.setMinAngle(this.getMinAngle() + part.getAngle());
                this.setMinDamage(this.getMinDamage() + part.getDamage());
                this.setMaxDamage(this.getMaxDamage() + part.getDamage());
            }
        }
    }

    public void detachWeaponPart(WeaponPart part) {
        if (part != null) {
            WeaponPart weaponPart = this.getWeaponPart(part.getPartType());
            if (weaponPart == part) {
                this.setWeaponPart(part.getPartType(), null);
                this.setMaxRange(this.getMaxRange() - part.getMaxRange());
                this.setMinRangeRanged(this.getMinRangeRanged() - part.getMinRangeRanged());
                this.setClipSize(this.getClipSize() - part.getClipSize());
                this.setReloadTime(this.getReloadTime() - part.getReloadTime());
                this.setRecoilDelay((int)(this.getRecoilDelay() - part.getRecoilDelay()));
                this.setAimingTime(this.getAimingTime() - part.getAimingTime());
                this.setHitChance(this.getHitChance() - part.getHitChance());
                this.setMinAngle(this.getMinAngle() - part.getAngle());
                this.setMinDamage(this.getMinDamage() - part.getDamage());
                this.setMaxDamage(this.getMaxDamage() - part.getDamage());
            }
        }
    }

    public int getTriggerExplosionTimer() {
        return this.triggerExplosionTimer;
    }

    public void setTriggerExplosionTimer(int _triggerExplosionTimer) {
        this.triggerExplosionTimer = _triggerExplosionTimer;
    }

    public boolean canBePlaced() {
        return this.canBePlaced;
    }

    public void setCanBePlaced(boolean _canBePlaced) {
        this.canBePlaced = _canBePlaced;
    }

    public int getExplosionRange() {
        return this.explosionRange;
    }

    public void setExplosionRange(int _explosionRange) {
        this.explosionRange = _explosionRange;
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    public void setExplosionPower(int _explosionPower) {
        this.explosionPower = _explosionPower;
    }

    public int getFireRange() {
        return this.fireRange;
    }

    public void setFireRange(int _fireRange) {
        this.fireRange = _fireRange;
    }

    public int getSmokeRange() {
        return this.smokeRange;
    }

    public void setSmokeRange(int _smokeRange) {
        this.smokeRange = _smokeRange;
    }

    public int getFirePower() {
        return this.firePower;
    }

    public void setFirePower(int _firePower) {
        this.firePower = _firePower;
    }

    public int getNoiseRange() {
        return this.noiseRange;
    }

    public void setNoiseRange(int _noiseRange) {
        this.noiseRange = _noiseRange;
    }

    public int getNoiseDuration() {
        return this.getScriptItem().getNoiseDuration();
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setExtraDamage(float _extraDamage) {
        this.extraDamage = _extraDamage;
    }

    public int getExplosionTimer() {
        return this.explosionTimer;
    }

    public void setExplosionTimer(int _explosionTimer) {
        this.explosionTimer = _explosionTimer;
    }

    public String getPlacedSprite() {
        return this.placedSprite;
    }

    public void setPlacedSprite(String _placedSprite) {
        this.placedSprite = _placedSprite;
    }

    public boolean canBeReused() {
        return this.canBeReused;
    }

    public void setCanBeReused(boolean _canBeReused) {
        this.canBeReused = _canBeReused;
    }

    public int getSensorRange() {
        return this.sensorRange;
    }

    public void setSensorRange(int _sensorRange) {
        this.sensorRange = _sensorRange;
    }

    public String getRunAnim() {
        return this.RunAnim;
    }

    public float getCritDmgMultiplier() {
        return this.critDmgMultiplier;
    }

    public void setCritDmgMultiplier(float _critDmgMultiplier) {
        this.critDmgMultiplier = _critDmgMultiplier;
    }

    @Override
    public String getStaticModel() {
        return this.staticModel != null ? this.staticModel : this.weaponSprite;
    }

    public float getBaseSpeed() {
        return this.baseSpeed;
    }

    public void setBaseSpeed(float _baseSpeed) {
        this.baseSpeed = _baseSpeed;
    }

    public float getBloodLevel() {
        return this.bloodLevel;
    }

    public void setBloodLevel(float level) {
        this.bloodLevel = Math.max(0.0F, Math.min(1.0F, level));
    }

    public void setWeaponLength(float weaponLength) {
        this.WeaponLength = weaponLength;
    }

    public String getAmmoBox() {
        return this.ammoBox;
    }

    public void setAmmoBox(String _ammoBox) {
        this.ammoBox = _ammoBox;
    }

    public String getMagazineType() {
        return this.magazineType;
    }

    public void setMagazineType(String _magazineType) {
        this.magazineType = _magazineType;
    }

    public String getEjectAmmoStartSound() {
        return this.getScriptItem().getEjectAmmoStartSound();
    }

    public String getEjectAmmoSound() {
        return this.getScriptItem().getEjectAmmoSound();
    }

    public String getEjectAmmoStopSound() {
        return this.getScriptItem().getEjectAmmoStopSound();
    }

    public String getInsertAmmoStartSound() {
        return this.getScriptItem().getInsertAmmoStartSound();
    }

    public String getInsertAmmoSound() {
        return this.getScriptItem().getInsertAmmoSound();
    }

    public String getInsertAmmoStopSound() {
        return this.getScriptItem().getInsertAmmoStopSound();
    }

    public String getRackSound() {
        return this.rackSound;
    }

    public void setRackSound(String _rackSound) {
        this.rackSound = _rackSound;
    }

    public boolean isReloadable(IsoGameCharacter owner) {
        return this.isRanged();
    }

    public boolean isContainsClip() {
        return this.containsClip;
    }

    public void setContainsClip(boolean _containsClip) {
        this.containsClip = _containsClip;
    }

    /**
     * Get the magazine with the most bullets in it
     */
    public InventoryItem getBestMagazine(IsoGameCharacter owner) {
        if (StringUtils.isNullOrEmpty(this.getMagazineType())) {
            return null;
        } else {
            InventoryItem item = owner.getInventory()
                .getBestTypeRecurse(this.getMagazineType(), (item1, item0) -> item1.getCurrentAmmoCount() - item0.getCurrentAmmoCount());
            return item != null && item.getCurrentAmmoCount() != 0 ? item : null;
        }
    }

    public String getWeaponReloadType() {
        return this.weaponReloadType;
    }

    public void setWeaponReloadType(String _weaponReloadType) {
        this.weaponReloadType = _weaponReloadType;
    }

    public boolean isRackAfterShoot() {
        return this.rackAfterShoot;
    }

    public void setRackAfterShoot(boolean _rackAfterShoot) {
        this.rackAfterShoot = _rackAfterShoot;
    }

    public boolean isRoundChambered() {
        return this.roundChambered;
    }

    public void setRoundChambered(boolean _roundChambered) {
        this.roundChambered = _roundChambered;
    }

    public boolean isSpentRoundChambered() {
        return this.bSpentRoundChambered;
    }

    public void setSpentRoundChambered(boolean _roundChambered) {
        this.bSpentRoundChambered = _roundChambered;
    }

    public int getSpentRoundCount() {
        return this.spentRoundCount;
    }

    public void setSpentRoundCount(int count) {
        this.spentRoundCount = PZMath.clamp(count, 0, this.getMaxAmmo());
    }

    public boolean isManuallyRemoveSpentRounds() {
        return this.getScriptItem().isManuallyRemoveSpentRounds();
    }

    public int getAmmoPerShoot() {
        return this.ammoPerShoot;
    }

    public void setAmmoPerShoot(int _ammoPerShoot) {
        this.ammoPerShoot = _ammoPerShoot;
    }

    public float getJamGunChance() {
        return this.jamGunChance;
    }

    public void setJamGunChance(float _jamGunChance) {
        this.jamGunChance = _jamGunChance;
    }

    public boolean isJammed() {
        return this.isJammed;
    }

    public void setJammed(boolean _isJammed) {
        this.isJammed = _isJammed;
    }

    public String getClickSound() {
        return this.clickSound;
    }

    public void setClickSound(String _clickSound) {
        this.clickSound = _clickSound;
    }

    public ArrayList<ModelWeaponPart> getModelWeaponPart() {
        return this.modelWeaponPart;
    }

    public void setModelWeaponPart(ArrayList<ModelWeaponPart> _modelWeaponPart) {
        this.modelWeaponPart = _modelWeaponPart;
    }

    public String getOriginalWeaponSprite() {
        return this.originalWeaponSprite;
    }

    public void setOriginalWeaponSprite(String _originalWeaponSprite) {
        this.originalWeaponSprite = _originalWeaponSprite;
    }

    public boolean haveChamber() {
        return this.haveChamber;
    }

    public void setHaveChamber(boolean _haveChamber) {
        this.haveChamber = _haveChamber;
    }

    public String getDamageCategory() {
        return this.damageCategory;
    }

    public void setDamageCategory(String _damageCategory) {
        this.damageCategory = _damageCategory;
    }

    public boolean isDamageMakeHole() {
        return this.damageMakeHole;
    }

    public void setDamageMakeHole(boolean _damageMakeHole) {
        this.damageMakeHole = _damageMakeHole;
    }

    public String getHitFloorSound() {
        return this.hitFloorSound;
    }

    public void setHitFloorSound(String _hitFloorSound) {
        this.hitFloorSound = _hitFloorSound;
    }

    public boolean isInsertAllBulletsReload() {
        return this.insertAllBulletsReload;
    }

    public void setInsertAllBulletsReload(boolean _insertAllBulletsReload) {
        this.insertAllBulletsReload = _insertAllBulletsReload;
    }

    public String getFireMode() {
        return this.fireMode;
    }

    public void setFireMode(String _fireMode) {
        this.fireMode = _fireMode;
    }

    public ArrayList<String> getFireModePossibilities() {
        return this.fireModePossibilities;
    }

    public void setFireModePossibilities(ArrayList<String> _fireModePossibilities) {
        this.fireModePossibilities = _fireModePossibilities;
    }

    public void randomizeBullets() {
        if (this.isRanged() && !Rand.NextBool(4)) {
            this.setCurrentAmmoCount(Rand.Next(this.getMaxAmmo() - 2, this.getMaxAmmo()));
            if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
                this.setContainsClip(true);
            }

            if (this.haveChamber()) {
                this.setRoundChambered(true);
            }
        }
    }

    public float getStopPower() {
        return this.getScriptItem().stopPower;
    }

    public boolean isInstantExplosion() {
        return this.explosionTimer <= 0 && this.sensorRange <= 0 && this.getRemoteControlID() == -1;
    }
}
