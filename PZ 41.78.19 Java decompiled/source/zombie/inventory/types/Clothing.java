// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoCombinationWasherDryer;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public class Clothing extends InventoryItem {
    private float temperature;
    private float insulation = 0.0F;
    private float windresistance = 0.0F;
    private float waterResistance = 0.0F;
    HashMap<Integer, Clothing.ClothingPatch> patches;
    protected String SpriteName = null;
    protected String palette;
    public float bloodLevel = 0.0F;
    private float dirtyness = 0.0F;
    private float wetness = 0.0F;
    private float WeightWet = 0.0F;
    private float lastWetnessUpdate = -1.0F;
    private final String dirtyString = Translator.getText("IGUI_ClothingName_Dirty");
    private final String bloodyString = Translator.getText("IGUI_ClothingName_Bloody");
    private final String wetString = Translator.getText("IGUI_ClothingName_Wet");
    private final String soakedString = Translator.getText("IGUI_ClothingName_Soaked");
    private final String wornString = Translator.getText("IGUI_ClothingName_Worn");
    private int ConditionLowerChance = 10000;
    private float stompPower = 1.0F;
    private float runSpeedModifier = 1.0F;
    private float combatSpeedModifier = 1.0F;
    private Boolean removeOnBroken = false;
    private Boolean canHaveHoles = true;
    private float biteDefense = 0.0F;
    private float scratchDefense = 0.0F;
    private float bulletDefense = 0.0F;
    public static final int CONDITION_PER_HOLES = 3;
    private float neckProtectionModifier = 1.0F;
    private int chanceToFall = 0;

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Clothing";
    }

    public Clothing(String module, String name, String itemType, String texName, String _palette, String _SpriteName) {
        super(module, name, itemType, texName);
        this.SpriteName = _SpriteName;
        this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
        this.palette = _palette;
    }

    public Clothing(String module, String name, String itemType, Item item, String _palette, String _SpriteName) {
        super(module, name, itemType, item);
        this.SpriteName = _SpriteName;
        this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
        this.palette = _palette;
    }

    @Override
    public boolean IsClothing() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Clothing.ordinal();
    }

    public void Unwear() {
        if (this.container != null && this.container.parent instanceof IsoGameCharacter character) {
            character.removeWornItem(this);
            if (character instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", character);
            }

            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
        }
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        float float0 = 1.0F;
        float float1 = 1.0F;
        float float2 = 0.8F;
        float float3 = 1.0F;
        float float4 = 0.0F;
        float float5 = 0.6F;
        float float6 = 0.0F;
        float float7 = 0.7F;
        if (!this.isCosmetic()) {
            ObjectTooltip.LayoutItem layoutItem0 = layout.addItem();
            layoutItem0.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", float0, float1, float2, float3);
            float float8 = (float)this.Condition / this.ConditionMax;
            layoutItem0.setProgress(float8, float4, float5, float6, float7);
            layoutItem0 = layout.addItem();
            layoutItem0.setLabel(Translator.getText("Tooltip_item_Insulation") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
            float8 = this.getInsulation();
            if (float8 > 0.8F) {
                layoutItem0.setProgress(float8, 0.0F, 0.6F, 0.0F, 0.7F);
            } else if (float8 > 0.6F) {
                layoutItem0.setProgress(float8, 0.3F, 0.6F, 0.0F, 0.7F);
            } else if (float8 > 0.4F) {
                layoutItem0.setProgress(float8, 0.6F, 0.6F, 0.0F, 0.7F);
            } else if (float8 > 0.2F) {
                layoutItem0.setProgress(float8, 0.6F, 0.3F, 0.0F, 0.7F);
            } else {
                layoutItem0.setProgress(float8, 0.6F, 0.0F, 0.0F, 0.7F);
            }

            float8 = this.getWindresistance();
            if (float8 > 0.0F) {
                layoutItem0 = layout.addItem();
                layoutItem0.setLabel(Translator.getText("Tooltip_item_Windresist") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
                if (float8 > 0.8F) {
                    layoutItem0.setProgress(float8, 0.0F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.6F) {
                    layoutItem0.setProgress(float8, 0.3F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.4F) {
                    layoutItem0.setProgress(float8, 0.6F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.2F) {
                    layoutItem0.setProgress(float8, 0.6F, 0.3F, 0.0F, 0.7F);
                } else {
                    layoutItem0.setProgress(float8, 0.6F, 0.0F, 0.0F, 0.7F);
                }
            }

            float8 = this.getWaterResistance();
            if (float8 > 0.0F) {
                layoutItem0 = layout.addItem();
                layoutItem0.setLabel(Translator.getText("Tooltip_item_Waterresist") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
                if (float8 > 0.8F) {
                    layoutItem0.setProgress(float8, 0.0F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.6F) {
                    layoutItem0.setProgress(float8, 0.3F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.4F) {
                    layoutItem0.setProgress(float8, 0.6F, 0.6F, 0.0F, 0.7F);
                } else if (float8 > 0.2F) {
                    layoutItem0.setProgress(float8, 0.6F, 0.3F, 0.0F, 0.7F);
                } else {
                    layoutItem0.setProgress(float8, 0.6F, 0.0F, 0.0F, 0.7F);
                }
            }
        }

        if (this.bloodLevel != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem1 = layout.addItem();
            layoutItem1.setLabel(Translator.getText("Tooltip_clothing_bloody") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            float float9 = this.bloodLevel / 100.0F;
            layoutItem1.setProgress(float9, float4, float5, float6, float7);
        }

        if (this.dirtyness >= 1.0F) {
            ObjectTooltip.LayoutItem layoutItem2 = layout.addItem();
            layoutItem2.setLabel(Translator.getText("Tooltip_clothing_dirty") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            float float10 = this.dirtyness / 100.0F;
            layoutItem2.setProgress(float10, float4, float5, float6, float7);
        }

        if (this.wetness != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem3 = layout.addItem();
            layoutItem3.setLabel(Translator.getText("Tooltip_clothing_wet") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            float float11 = this.wetness / 100.0F;
            layoutItem3.setProgress(float11, float4, float5, float6, float7);
        }

        int int0 = 0;
        ItemVisual itemVisual = this.getVisual();

        for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); int1++) {
            if (itemVisual.getHole(BloodBodyPartType.FromIndex(int1)) > 0.0F) {
                int0++;
            }
        }

        if (int0 > 0) {
            ObjectTooltip.LayoutItem layoutItem4 = layout.addItem();
            layoutItem4.setLabel(Translator.getText("Tooltip_clothing_holes") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem4.setValueRightNoPlus(int0);
        }

        if (!this.isEquipped() && tooltipUI.getCharacter() != null) {
            float float12 = 0.0F;
            float float13 = 0.0F;
            float float14 = 0.0F;
            WornItems wornItems = tooltipUI.getCharacter().getWornItems();

            for (int int2 = 0; int2 < wornItems.size(); int2++) {
                WornItem wornItem = wornItems.get(int2);
                if (this.getBodyLocation().equals(wornItem.getLocation())
                    || wornItems.getBodyLocationGroup().isExclusive(this.getBodyLocation(), wornItem.getLocation())) {
                    float12 += ((Clothing)wornItem.getItem()).getBiteDefense();
                    float13 += ((Clothing)wornItem.getItem()).getScratchDefense();
                    float14 += ((Clothing)wornItem.getItem()).getBulletDefense();
                }
            }

            float float15 = this.getBiteDefense();
            if (float15 != float12) {
                ObjectTooltip.LayoutItem layoutItem5 = layout.addItem();
                if (float15 > 0.0F || float12 > 0.0F) {
                    layoutItem5.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    if (float15 > float12) {
                        layoutItem5.setValue((int)float15 + " (+" + (int)(float15 - float12) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
                    } else {
                        layoutItem5.setValue((int)float15 + " (-" + (int)(float12 - float15) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            } else if (this.getBiteDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem6 = layout.addItem();
                layoutItem6.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem6.setValueRightNoPlus((int)this.getBiteDefense());
            }

            float float16 = this.getScratchDefense();
            if (float16 != float13) {
                ObjectTooltip.LayoutItem layoutItem7 = layout.addItem();
                if (float16 > 0.0F || float13 > 0.0F) {
                    layoutItem7.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    if (float16 > float13) {
                        layoutItem7.setValue((int)float16 + " (+" + (int)(float16 - float13) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
                    } else {
                        layoutItem7.setValue((int)float16 + " (-" + (int)(float13 - float16) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            } else if (this.getScratchDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem8 = layout.addItem();
                layoutItem8.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem8.setValueRightNoPlus((int)this.getScratchDefense());
            }

            float float17 = this.getBulletDefense();
            if (float17 != float14) {
                ObjectTooltip.LayoutItem layoutItem9 = layout.addItem();
                if (float17 > 0.0F || float14 > 0.0F) {
                    layoutItem9.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    if (float17 > float14) {
                        layoutItem9.setValue((int)float17 + " (+" + (int)(float17 - float14) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
                    } else {
                        layoutItem9.setValue((int)float17 + " (-" + (int)(float14 - float17) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            } else if (this.getBulletDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem10 = layout.addItem();
                layoutItem10.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem10.setValueRightNoPlus((int)this.getBulletDefense());
            }
        } else {
            if (this.getBiteDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem11 = layout.addItem();
                layoutItem11.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem11.setValueRightNoPlus((int)this.getBiteDefense());
            }

            if (this.getScratchDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem12 = layout.addItem();
                layoutItem12.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem12.setValueRightNoPlus((int)this.getScratchDefense());
            }

            if (this.getBulletDefense() != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem13 = layout.addItem();
                layoutItem13.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem13.setValueRightNoPlus((int)this.getBulletDefense());
            }
        }

        if (this.getRunSpeedModifier() != 1.0F) {
            ObjectTooltip.LayoutItem layoutItem14 = layout.addItem();
            layoutItem14.setLabel(Translator.getText("Tooltip_RunSpeedModifier") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem14.setValueRightNoPlus(this.getRunSpeedModifier());
        }

        if (this.getCombatSpeedModifier() != 1.0F) {
            ObjectTooltip.LayoutItem layoutItem15 = layout.addItem();
            layoutItem15.setLabel(Translator.getText("Tooltip_CombatSpeedModifier") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem15.setValueRightNoPlus(this.getCombatSpeedModifier());
        }

        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            if (this.bloodLevel != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem16 = layout.addItem();
                layoutItem16.setLabel("DBG: bloodLevel:", 1.0F, 1.0F, 0.8F, 1.0F);
                int int3 = (int)Math.ceil(this.bloodLevel);
                layoutItem16.setValueRight(int3, false);
            }

            if (this.dirtyness != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem17 = layout.addItem();
                layoutItem17.setLabel("DBG: dirtyness:", 1.0F, 1.0F, 0.8F, 1.0F);
                int int4 = (int)Math.ceil(this.dirtyness);
                layoutItem17.setValueRight(int4, false);
            }

            if (this.wetness != 0.0F) {
                ObjectTooltip.LayoutItem layoutItem18 = layout.addItem();
                layoutItem18.setLabel("DBG: wetness:", 1.0F, 1.0F, 0.8F, 1.0F);
                int int5 = (int)Math.ceil(this.wetness);
                layoutItem18.setValueRight(int5, false);
            }
        }
    }

    public boolean isDirty() {
        return this.dirtyness > 15.0F;
    }

    public boolean isBloody() {
        return this.bloodLevel > 25.0F;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        String string = "";
        if (this.isDirty()) {
            string = string + this.dirtyString + ", ";
        }

        if (this.isBloody()) {
            string = string + this.bloodyString + ", ";
        }

        if (this.getWetness() >= 100.0F) {
            string = string + this.soakedString + ", ";
        } else if (this.getWetness() > 25.0F) {
            string = string + this.wetString + ", ";
        }

        if (this.getCondition() < this.getConditionMax() / 3) {
            string = string + this.wornString + ", ";
        }

        if (string.length() > 2) {
            string = string.substring(0, string.length() - 2);
        }

        string = string.trim();
        return string.isEmpty() ? this.name : Translator.getText("IGUI_ClothingNaming", string, this.name);
    }

    @Override
    public void update() {
        if (this.container == null || SandboxOptions.instance.ClothingDegradation.getValue() == 1) {
            ;
        }
    }

    public void updateWetness() {
        this.updateWetness(false);
    }

    public void updateWetness(boolean bIgnoreEquipped) {
        if (bIgnoreEquipped || !this.isEquipped()) {
            if (this.getBloodClothingType() == null) {
                this.setWetness(0.0F);
            } else {
                float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                if (this.lastWetnessUpdate < 0.0F) {
                    this.lastWetnessUpdate = float0;
                } else if (this.lastWetnessUpdate > float0) {
                    this.lastWetnessUpdate = float0;
                }

                float float1 = float0 - this.lastWetnessUpdate;
                if (!(float1 < 0.016666668F)) {
                    this.lastWetnessUpdate = float0;
                    switch (this.getWetDryState()) {
                        case Invalid:
                        default:
                            break;
                        case Dryer:
                            if (this.getWetness() > 0.0F) {
                                float float4 = float1 * 20.0F;
                                if (this.isEquipped()) {
                                    float4 *= 2.0F;
                                }

                                this.setWetness(this.getWetness() - float4);
                            }
                            break;
                        case Wetter:
                            if (this.getWetness() < 100.0F) {
                                float float2 = ClimateManager.getInstance().getRainIntensity();
                                if (float2 < 0.1F) {
                                    float2 = 0.0F;
                                }

                                float float3 = float2 * float1 * 100.0F;
                                this.setWetness(this.getWetness() + float3);
                            }
                    }
                }
            }
        }
    }

    public float getBulletDefense() {
        return this.bulletDefense;
    }

    public void setBulletDefense(float _bulletDefense) {
        this.bulletDefense = _bulletDefense;
    }

    private Clothing.WetDryState getWetDryState() {
        if (this.getWorldItem() == null) {
            if (this.container == null) {
                return Clothing.WetDryState.Invalid;
            } else if (this.container.parent instanceof IsoDeadBody deadBody) {
                if (deadBody.getSquare() == null) {
                    return Clothing.WetDryState.Invalid;
                } else if (deadBody.getSquare().isInARoom()) {
                    return Clothing.WetDryState.Dryer;
                } else {
                    return ClimateManager.getInstance().isRaining() ? Clothing.WetDryState.Wetter : Clothing.WetDryState.Dryer;
                }
            } else if (this.container.parent instanceof IsoGameCharacter character) {
                if (character.getCurrentSquare() == null) {
                    return Clothing.WetDryState.Invalid;
                } else if (character.getCurrentSquare().isInARoom() || character.getCurrentSquare().haveRoof) {
                    return Clothing.WetDryState.Dryer;
                } else if (!ClimateManager.getInstance().isRaining()) {
                    return Clothing.WetDryState.Dryer;
                } else if (!this.isEquipped()) {
                    return Clothing.WetDryState.Dryer;
                } else if (character.isAsleep() && character.getBed() != null && "Tent".equals(character.getBed().getName())) {
                    return Clothing.WetDryState.Dryer;
                } else {
                    BaseVehicle vehicle = character.getVehicle();
                    if (vehicle != null && vehicle.hasRoof(vehicle.getSeat(character))) {
                        VehiclePart part = vehicle.getPartById("Windshield");
                        if (part != null) {
                            VehicleWindow vehicleWindow = part.getWindow();
                            if (vehicleWindow != null && vehicleWindow.isHittable()) {
                                return Clothing.WetDryState.Dryer;
                            }
                        }
                    }

                    return Clothing.WetDryState.Wetter;
                }
            } else if (this.container.parent == null) {
                return Clothing.WetDryState.Dryer;
            } else if (this.container.parent instanceof IsoClothingDryer && ((IsoClothingDryer)this.container.parent).isActivated()) {
                return Clothing.WetDryState.Invalid;
            } else if (this.container.parent instanceof IsoClothingWasher && ((IsoClothingWasher)this.container.parent).isActivated()) {
                return Clothing.WetDryState.Invalid;
            } else {
                IsoCombinationWasherDryer combinationWasherDryer = Type.tryCastTo(this.container.parent, IsoCombinationWasherDryer.class);
                return combinationWasherDryer != null && combinationWasherDryer.isActivated() ? Clothing.WetDryState.Invalid : Clothing.WetDryState.Dryer;
            }
        } else if (this.getWorldItem().getSquare() == null) {
            return Clothing.WetDryState.Invalid;
        } else if (this.getWorldItem().getSquare().isInARoom()) {
            return Clothing.WetDryState.Dryer;
        } else {
            return ClimateManager.getInstance().isRaining() ? Clothing.WetDryState.Wetter : Clothing.WetDryState.Dryer;
        }
    }

    public void flushWetness() {
        if (!(this.lastWetnessUpdate < 0.0F)) {
            this.updateWetness(true);
            this.lastWetnessUpdate = -1.0F;
        }
    }

    @Override
    public boolean finishupdate() {
        return this.container != null && this.container.parent instanceof IsoGameCharacter ? !this.isEquipped() : true;
    }

    @Override
    public void Use(boolean bCrafting, boolean bInContainer) {
        if (this.uses <= 1) {
            this.Unwear();
        }

        super.Use(bCrafting, bInContainer);
    }

    @Override
    public boolean CanStack(InventoryItem item) {
        return this.ModDataMatches(item) && this.palette == null && ((Clothing)item).palette == null || this.palette.equals(((Clothing)item).palette);
    }

    public static Clothing CreateFromSprite(String Sprite) {
        try {
            Object object = null;
            return (Clothing)InventoryItemFactory.CreateItem(Sprite, 1.0F);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        if (this.getSpriteName() != null) {
            bitHeaderWrite.addFlags(1);
            GameWindow.WriteString(output, this.getSpriteName());
        }

        if (this.dirtyness != 0.0F) {
            bitHeaderWrite.addFlags(2);
            output.putFloat(this.dirtyness);
        }

        if (this.bloodLevel != 0.0F) {
            bitHeaderWrite.addFlags(4);
            output.putFloat(this.bloodLevel);
        }

        if (this.wetness != 0.0F) {
            bitHeaderWrite.addFlags(8);
            output.putFloat(this.wetness);
        }

        if (this.lastWetnessUpdate != 0.0F) {
            bitHeaderWrite.addFlags(16);
            output.putFloat(this.lastWetnessUpdate);
        }

        if (this.patches != null) {
            bitHeaderWrite.addFlags(32);
            output.put((byte)this.patches.size());

            for (int int0 : this.patches.keySet()) {
                output.put((byte)int0);
                this.patches.get(int0).save(output, false);
            }
        }

        bitHeaderWrite.write();
        bitHeaderWrite.release();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
        if (!bitHeaderRead.equals(0)) {
            if (bitHeaderRead.hasFlags(1)) {
                this.setSpriteName(GameWindow.ReadString(input));
            }

            if (bitHeaderRead.hasFlags(2)) {
                this.dirtyness = input.getFloat();
            }

            if (bitHeaderRead.hasFlags(4)) {
                this.bloodLevel = input.getFloat();
            }

            if (bitHeaderRead.hasFlags(8)) {
                this.wetness = input.getFloat();
            }

            if (bitHeaderRead.hasFlags(16)) {
                this.lastWetnessUpdate = input.getFloat();
            }

            if (bitHeaderRead.hasFlags(32)) {
                byte byte0 = input.get();

                for (int int0 = 0; int0 < byte0; int0++) {
                    byte byte1 = input.get();
                    Clothing.ClothingPatch clothingPatch = new Clothing.ClothingPatch();
                    clothingPatch.load(input, WorldVersion);
                    if (this.patches == null) {
                        this.patches = new HashMap<>();
                    }

                    this.patches.put(Integer.valueOf(byte1), clothingPatch);
                }
            }
        }

        bitHeaderRead.release();
        this.synchWithVisual();
    }

    /**
     * @return the SpriteName
     */
    public String getSpriteName() {
        return this.SpriteName;
    }

    /**
     * 
     * @param _SpriteName the SpriteName to set
     */
    public void setSpriteName(String _SpriteName) {
        this.SpriteName = _SpriteName;
    }

    /**
     * @return the palette
     */
    public String getPalette() {
        return this.palette == null ? "Trousers_White" : this.palette;
    }

    /**
     * 
     * @param _palette the palette to set
     */
    public void setPalette(String _palette) {
        this.palette = _palette;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(float _temperature) {
        this.temperature = _temperature;
    }

    public void setDirtyness(float delta) {
        this.dirtyness = PZMath.clamp(delta, 0.0F, 100.0F);
    }

    public void setBloodLevel(float delta) {
        this.bloodLevel = PZMath.clamp(delta, 0.0F, 100.0F);
    }

    public float getDirtyness() {
        return this.dirtyness;
    }

    public float getBloodlevel() {
        return this.bloodLevel;
    }

    public float getBloodlevelForPart(BloodBodyPartType part) {
        return this.getVisual().getBlood(part);
    }

    public float getBloodLevel() {
        return this.bloodLevel;
    }

    public float getBloodLevelForPart(BloodBodyPartType part) {
        return this.getVisual().getBlood(part);
    }

    /**
     * @return the Weight
     */
    @Override
    public float getWeight() {
        float float0 = this.getActualWeight();
        float float1 = this.getWeightWet();
        if (float1 <= 0.0F) {
            float1 = float0 * 1.25F;
        }

        return PZMath.lerp(float0, float1, this.getWetness() / 100.0F);
    }

    public void setWetness(float percent) {
        this.wetness = PZMath.clamp(percent, 0.0F, 100.0F);
    }

    public float getWetness() {
        return this.wetness;
    }

    public float getWeightWet() {
        return this.WeightWet;
    }

    public void setWeightWet(float weight) {
        this.WeightWet = weight;
    }

    public int getConditionLowerChance() {
        return this.ConditionLowerChance;
    }

    public void setConditionLowerChance(int conditionLowerChance) {
        this.ConditionLowerChance = conditionLowerChance;
    }

    /**
     * 
     * @param Condition the Condition to set
     */
    @Override
    public void setCondition(int Condition) {
        this.setCondition(Condition, true);
        if (Condition <= 0) {
            this.Unwear();
            if (this.getContainer() != null) {
                this.getContainer().setDrawDirty(true);
            }

            if (this.isRemoveOnBroken() && this.getContainer() != null) {
                this.container.Remove(this);
            }
        }
    }

    public float getClothingDirtynessIncreaseLevel() {
        if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
            return 2.5E-4F;
        } else {
            return SandboxOptions.instance.ClothingDegradation.getValue() == 4 ? 0.025F : 0.0025F;
        }
    }

    public float getInsulation() {
        return this.insulation;
    }

    public void setInsulation(float _insulation) {
        this.insulation = _insulation;
    }

    public float getStompPower() {
        return this.stompPower;
    }

    public void setStompPower(float _stompPower) {
        this.stompPower = _stompPower;
    }

    public float getRunSpeedModifier() {
        return this.runSpeedModifier;
    }

    public void setRunSpeedModifier(float _runSpeedModifier) {
        this.runSpeedModifier = _runSpeedModifier;
    }

    public float getCombatSpeedModifier() {
        return this.combatSpeedModifier;
    }

    public void setCombatSpeedModifier(float _combatSpeedModifier) {
        this.combatSpeedModifier = _combatSpeedModifier;
    }

    public Boolean isRemoveOnBroken() {
        return this.removeOnBroken;
    }

    public void setRemoveOnBroken(Boolean _removeOnBroken) {
        this.removeOnBroken = _removeOnBroken;
    }

    public Boolean getCanHaveHoles() {
        return this.canHaveHoles;
    }

    public void setCanHaveHoles(Boolean _canHaveHoles) {
        this.canHaveHoles = _canHaveHoles;
    }

    public boolean isCosmetic() {
        return this.getScriptItem().isCosmetic();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ clothingItemName=\"" + this.getClothingItemName() + "\" }";
    }

    public float getBiteDefense() {
        return this.getCondition() <= 0 ? 0.0F : this.biteDefense;
    }

    public void setBiteDefense(float _biteDefense) {
        this.biteDefense = _biteDefense;
    }

    public float getScratchDefense() {
        return this.getCondition() <= 0 ? 0.0F : this.scratchDefense;
    }

    public void setScratchDefense(float _scratchDefense) {
        this.scratchDefense = _scratchDefense;
    }

    public float getNeckProtectionModifier() {
        return this.neckProtectionModifier;
    }

    public void setNeckProtectionModifier(float _neckProtectionModifier) {
        this.neckProtectionModifier = _neckProtectionModifier;
    }

    public int getChanceToFall() {
        return this.chanceToFall;
    }

    public void setChanceToFall(int _chanceToFall) {
        this.chanceToFall = _chanceToFall;
    }

    public float getWindresistance() {
        return this.windresistance;
    }

    public void setWindresistance(float _windresistance) {
        this.windresistance = _windresistance;
    }

    public float getWaterResistance() {
        return this.waterResistance;
    }

    public void setWaterResistance(float _waterResistance) {
        this.waterResistance = _waterResistance;
    }

    public int getHolesNumber() {
        return this.getVisual() != null ? this.getVisual().getHolesNumber() : 0;
    }

    public int getPatchesNumber() {
        return this.patches.size();
    }

    public float getDefForPart(BloodBodyPartType part, boolean bite, boolean bullet) {
        if (this.getVisual().getHole(part) > 0.0F) {
            return 0.0F;
        } else {
            Clothing.ClothingPatch clothingPatch = this.getPatchType(part);
            float float0 = this.getScratchDefense();
            if (bite) {
                float0 = this.getBiteDefense();
            }

            if (bullet) {
                float0 = this.getBulletDefense();
            }

            if (part == BloodBodyPartType.Neck && this.getScriptItem().neckProtectionModifier < 1.0F) {
                float0 *= this.getScriptItem().neckProtectionModifier;
            }

            if (clothingPatch != null) {
                int int0 = clothingPatch.scratchDefense;
                if (bite) {
                    int0 = clothingPatch.biteDefense;
                }

                if (bullet) {
                    int0 = clothingPatch.biteDefense;
                }

                if (!clothingPatch.hasHole) {
                    float0 += int0;
                } else {
                    float0 = int0;
                }
            }

            return float0;
        }
    }

    /**
     * Used from lua tooltip when repairing clothing
     */
    public static int getBiteDefenseFromItem(IsoGameCharacter chr, InventoryItem fabric) {
        int int0 = Math.max(1, chr.getPerkLevel(PerkFactory.Perks.Tailoring));
        Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(fabric.getFabricType());
        return clothingPatchFabricType.maxBiteDef > 0 ? (int)Math.max(1.0F, clothingPatchFabricType.maxBiteDef * (int0 / 10.0F)) : 0;
    }

    /**
     * Used from lua tooltip when repairing clothing
     */
    public static int getScratchDefenseFromItem(IsoGameCharacter chr, InventoryItem fabric) {
        int int0 = Math.max(1, chr.getPerkLevel(PerkFactory.Perks.Tailoring));
        Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(fabric.getFabricType());
        return (int)Math.max(1.0F, clothingPatchFabricType.maxScratchDef * (int0 / 10.0F));
    }

    public Clothing.ClothingPatch getPatchType(BloodBodyPartType part) {
        return this.patches != null ? this.patches.get(part.index()) : null;
    }

    public void removePatch(BloodBodyPartType part) {
        if (this.patches != null) {
            this.getVisual().removePatch(part.index());
            Clothing.ClothingPatch clothingPatch = this.patches.get(part.index());
            if (clothingPatch != null && clothingPatch.hasHole) {
                this.getVisual().setHole(part);
                this.setCondition(this.getCondition() - clothingPatch.conditionGain);
            }

            this.patches.remove(part.index());
            if (GameClient.bClient && this.getContainer() != null && this.getContainer().getParent() instanceof IsoPlayer) {
                GameClient.instance.sendClothing((IsoPlayer)this.getContainer().getParent(), "", null);
            }
        }
    }

    public boolean canFullyRestore(IsoGameCharacter chr, BloodBodyPartType part, InventoryItem fabric) {
        return chr.getPerkLevel(PerkFactory.Perks.Tailoring) > 7
            && fabric.getFabricType().equals(this.getFabricType())
            && this.getVisual().getHole(part) > 0.0F;
    }

    public void addPatch(IsoGameCharacter chr, BloodBodyPartType part, InventoryItem fabric) {
        Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(fabric.getFabricType());
        if (this.canFullyRestore(chr, part, fabric)) {
            this.getVisual().removeHole(part.index());
            this.setCondition(this.getCondition() + this.getCondLossPerHole());
        } else {
            if (clothingPatchFabricType == Clothing.ClothingPatchFabricType.Cotton) {
                this.getVisual().setBasicPatch(part);
            } else if (clothingPatchFabricType == Clothing.ClothingPatchFabricType.Denim) {
                this.getVisual().setDenimPatch(part);
            } else {
                this.getVisual().setLeatherPatch(part);
            }

            if (this.patches == null) {
                this.patches = new HashMap<>();
            }

            int int0 = Math.max(1, chr.getPerkLevel(PerkFactory.Perks.Tailoring));
            float float0 = this.getVisual().getHole(part);
            int int1 = this.getCondLossPerHole();
            if (int0 < 3) {
                int1 -= 2;
            } else if (int0 < 6) {
                int1--;
            }

            Clothing.ClothingPatch clothingPatch = new Clothing.ClothingPatch(int0, clothingPatchFabricType.index, float0 > 0.0F);
            if (float0 > 0.0F) {
                int1 = Math.max(1, int1);
                this.setCondition(this.getCondition() + int1);
                clothingPatch.conditionGain = int1;
            }

            this.patches.put(part.index(), clothingPatch);
            this.getVisual().removeHole(part.index());
            if (GameClient.bClient && chr instanceof IsoPlayer) {
                GameClient.instance.sendClothing((IsoPlayer)chr, "", null);
            }
        }
    }

    public ArrayList<BloodBodyPartType> getCoveredParts() {
        ArrayList arrayList = this.getScriptItem().getBloodClothingType();
        return BloodClothingType.getCoveredParts(arrayList);
    }

    public int getNbrOfCoveredParts() {
        ArrayList arrayList = this.getScriptItem().getBloodClothingType();
        return BloodClothingType.getCoveredPartCount(arrayList);
    }

    public int getCondLossPerHole() {
        int int0 = this.getNbrOfCoveredParts();
        return PZMath.max(1, this.getConditionMax() / int0);
    }

    public void copyPatchesTo(Clothing newClothing) {
        newClothing.patches = this.patches;
    }

    public String getClothingExtraSubmenu() {
        return this.ScriptItem.clothingExtraSubmenu;
    }

    public boolean canBe3DRender() {
        return !StringUtils.isNullOrEmpty(this.getWorldStaticItem())
            ? true
            : "Bip01_Head".equalsIgnoreCase(this.getClothingItem().m_AttachBone) && (!this.isCosmetic() || "Eyes".equals(this.getBodyLocation()));
    }

    public boolean isWorn() {
        return this.container.parent instanceof IsoGameCharacter && ((IsoGameCharacter)this.container.parent).getWornItems().contains(this);
    }

    public class ClothingPatch {
        public int tailorLvl = 0;
        public int fabricType = 0;
        public int scratchDefense = 0;
        public int biteDefense = 0;
        public boolean hasHole;
        public int conditionGain = 0;

        public String getFabricTypeName() {
            return Translator.getText("IGUI_FabricType_" + this.fabricType);
        }

        public int getScratchDefense() {
            return this.scratchDefense;
        }

        public int getBiteDefense() {
            return this.biteDefense;
        }

        public int getFabricType() {
            return this.fabricType;
        }

        public ClothingPatch() {
        }

        public ClothingPatch(int int0, int int1, boolean boolean0) {
            this.tailorLvl = int0;
            this.fabricType = int1;
            this.hasHole = boolean0;
            Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromIndex(int1);
            this.scratchDefense = (int)Math.max(1.0F, clothingPatchFabricType.maxScratchDef * (int0 / 10.0F));
            if (clothingPatchFabricType.maxBiteDef > 0) {
                this.biteDefense = (int)Math.max(1.0F, clothingPatchFabricType.maxBiteDef * (int0 / 10.0F));
            }
        }

        public void save(ByteBuffer output, boolean net) throws IOException {
            output.put((byte)this.tailorLvl);
            output.put((byte)this.fabricType);
            output.put((byte)this.scratchDefense);
            output.put((byte)this.biteDefense);
            output.put((byte)(this.hasHole ? 1 : 0));
            output.putShort((short)this.conditionGain);
        }

        public void load(ByteBuffer input, int WorldVersion) throws IOException {
            this.tailorLvl = input.get();
            if (WorldVersion < 178) {
                this.fabricType = input.getShort();
            } else {
                this.fabricType = input.get();
            }

            this.scratchDefense = input.get();
            this.biteDefense = input.get();
            this.hasHole = input.get() == 1;
            this.conditionGain = input.getShort();
        }

        @Deprecated
        public void save_old(ByteBuffer output, boolean net) throws IOException {
            output.putInt(this.tailorLvl);
            output.putInt(this.fabricType);
            output.putInt(this.scratchDefense);
            output.putInt(this.biteDefense);
            output.put((byte)(this.hasHole ? 1 : 0));
            output.putInt(this.conditionGain);
        }

        @Deprecated
        public void load_old(ByteBuffer input, int WorldVersion, boolean net) throws IOException {
            this.tailorLvl = input.getInt();
            this.fabricType = input.getInt();
            this.scratchDefense = input.getInt();
            this.biteDefense = input.getInt();
            this.hasHole = input.get() == 1;
            this.conditionGain = input.getInt();
        }
    }

    public static enum ClothingPatchFabricType {
        Cotton(1, "Cotton", 5, 0),
        Denim(2, "Denim", 10, 5),
        Leather(3, "Leather", 20, 10);

        public int index;
        public String type;
        public int maxScratchDef;
        public int maxBiteDef;

        private ClothingPatchFabricType(int int1, String string1, int int2, int int3) {
            this.index = int1;
            this.type = string1;
            this.maxScratchDef = int2;
            this.maxBiteDef = int3;
        }

        public String getType() {
            return this.type;
        }

        public static Clothing.ClothingPatchFabricType fromType(String _type) {
            if (StringUtils.isNullOrEmpty(_type)) {
                return null;
            } else if (Cotton.type.equals(_type)) {
                return Cotton;
            } else if (Denim.type.equals(_type)) {
                return Denim;
            } else {
                return Leather.type.equals(_type) ? Leather : null;
            }
        }

        public static Clothing.ClothingPatchFabricType fromIndex(int _index) {
            if (_index == 1) {
                return Cotton;
            } else if (_index == 2) {
                return Denim;
            } else {
                return _index == 3 ? Leather : null;
            }
        }
    }

    private static enum WetDryState {
        Invalid,
        Dryer,
        Wetter;
    }
}
