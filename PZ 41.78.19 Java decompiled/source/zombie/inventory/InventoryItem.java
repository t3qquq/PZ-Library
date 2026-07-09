// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.model.WorldItemAtlas;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.network.GameClient;
import zombie.radio.ZomboidRadio;
import zombie.radio.media.MediaData;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ItemReplacement;
import zombie.ui.ObjectTooltip;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.vehicles.VehiclePart;
import zombie.world.ItemInfo;
import zombie.world.WorldDictionary;

public class InventoryItem {
    protected IsoGameCharacter previousOwner = null;
    protected Item ScriptItem = null;
    protected ItemType cat = ItemType.None;
    protected ItemContainer container;
    protected int containerX = 0;
    protected int containerY = 0;
    protected String name;
    protected String replaceOnUse = null;
    protected String replaceOnUseFullType = null;
    protected int ConditionMax = 10;
    protected ItemContainer rightClickContainer = null;
    protected Texture texture;
    protected Texture texturerotten;
    protected Texture textureCooked;
    protected Texture textureBurnt;
    protected String type;
    protected String fullType;
    protected int uses = 1;
    protected float Age = 0.0F;
    protected float LastAged = -1.0F;
    protected boolean IsCookable = false;
    protected float CookingTime = 0.0F;
    protected float MinutesToCook = 60.0F;
    protected float MinutesToBurn = 120.0F;
    public boolean Cooked = false;
    protected boolean Burnt = false;
    protected int OffAge = 1000000000;
    protected int OffAgeMax = 1000000000;
    protected float Weight = 1.0F;
    protected float ActualWeight = 1.0F;
    protected String WorldTexture;
    protected String Description;
    protected int Condition = 10;
    protected String OffString = Translator.getText("Tooltip_food_Rotten");
    protected String FreshString = Translator.getText("Tooltip_food_Fresh");
    protected String StaleString = Translator.getText("Tooltip_food_Stale");
    protected String CookedString = Translator.getText("Tooltip_food_Cooked");
    protected String UnCookedString = Translator.getText("Tooltip_food_Uncooked");
    protected String FrozenString = Translator.getText("Tooltip_food_Frozen");
    protected String BurntString = Translator.getText("Tooltip_food_Burnt");
    private String brokenString = Translator.getText("Tooltip_broken");
    protected String module = "Base";
    protected float boredomChange = 0.0F;
    protected float unhappyChange = 0.0F;
    protected float stressChange = 0.0F;
    protected ArrayList<IsoObject> Taken = new ArrayList<>();
    protected IsoDirections placeDir = IsoDirections.Max;
    protected IsoDirections newPlaceDir = IsoDirections.Max;
    private KahluaTable table = null;
    public String ReplaceOnUseOn = null;
    public Color col = Color.white;
    public boolean IsWaterSource = false;
    public boolean CanStoreWater = false;
    public boolean CanStack = false;
    private boolean activated = false;
    private boolean isTorchCone = false;
    private int lightDistance = 0;
    private int Count = 1;
    public float fatigueChange = 0.0F;
    public IsoWorldInventoryObject worldItem = null;
    private String customMenuOption = null;
    private String tooltip = null;
    private String displayCategory = null;
    private int haveBeenRepaired = 1;
    private boolean broken = false;
    private String originalName = null;
    public int id = 0;
    public boolean RequiresEquippedBothHands;
    public ByteBuffer byteData;
    public ArrayList<String> extraItems = null;
    private boolean customName = false;
    private String breakSound = null;
    protected boolean alcoholic = false;
    private float alcoholPower = 0.0F;
    private float bandagePower = 0.0F;
    private float ReduceInfectionPower = 0.0F;
    private boolean customWeight = false;
    private boolean customColor = false;
    private int keyId = -1;
    private boolean taintedWater = false;
    private boolean remoteController = false;
    private boolean canBeRemote = false;
    private int remoteControlID = -1;
    private int remoteRange = 0;
    private float colorRed = 1.0F;
    private float colorGreen = 1.0F;
    private float colorBlue = 1.0F;
    private String countDownSound = null;
    private String explosionSound = null;
    private IsoGameCharacter equipParent = null;
    private String evolvedRecipeName = null;
    private float metalValue = 0.0F;
    private float itemHeat = 1.0F;
    private float meltingTime = 0.0F;
    private String worker;
    private boolean isWet = false;
    private float wetCooldown = -1.0F;
    private String itemWhenDry = null;
    private boolean favorite = false;
    protected ArrayList<String> requireInHandOrInventory = null;
    private String map = null;
    private String stashMap = null;
    public boolean keepOnDeplete = false;
    private boolean zombieInfected = false;
    private boolean rainFactorZero = false;
    private float itemCapacity = -1.0F;
    private int maxCapacity = -1;
    private float brakeForce = 0.0F;
    private int chanceToSpawnDamaged = 0;
    private float conditionLowerNormal = 0.0F;
    private float conditionLowerOffroad = 0.0F;
    private float wheelFriction = 0.0F;
    private float suspensionDamping = 0.0F;
    private float suspensionCompression = 0.0F;
    private float engineLoudness = 0.0F;
    protected ItemVisual visual = null;
    protected String staticModel = null;
    private ArrayList<String> iconsForTexture = null;
    private ArrayList<BloodClothingType> bloodClothingType = new ArrayList<>();
    private int stashChance = 80;
    private String ammoType = null;
    private int maxAmmo = 0;
    private int currentAmmoCount = 0;
    private String gunType = null;
    private String attachmentType = null;
    private ArrayList<String> attachmentsProvided = null;
    private int attachedSlot = -1;
    private String attachedSlotType = null;
    private String attachmentReplacement = null;
    private String attachedToModel = null;
    private String m_alternateModelName = null;
    private short registry_id = -1;
    public int worldZRotation = -1;
    public float worldScale = 1.0F;
    private short recordedMediaIndex = -1;
    private byte mediaType = -1;
    private boolean isInitialised = false;
    public WorldItemAtlas.ItemTexture atlasTexture = null;
    private final int maxTextLength = 256;
    public float jobDelta = 0.0F;
    public String jobType = null;
    static ByteBuffer tempBuffer = ByteBuffer.allocate(20000);
    public String mainCategory = null;
    private boolean canBeActivated;
    private float lightStrength;
    public String CloseKillMove = null;
    private boolean beingFilled = false;

    public int getSaveType() {
        throw new RuntimeException("InventoryItem.getSaveType() not implemented for " + this.getClass().getName());
    }

    public IsoWorldInventoryObject getWorldItem() {
        return this.worldItem;
    }

    public void setEquipParent(IsoGameCharacter parent) {
        this.equipParent = parent;
    }

    public IsoGameCharacter getEquipParent() {
        return this.equipParent == null || this.equipParent.getPrimaryHandItem() != this && this.equipParent.getSecondaryHandItem() != this
            ? null
            : this.equipParent;
    }

    public String getBringToBearSound() {
        return this.getScriptItem().getBringToBearSound();
    }

    public String getEquipSound() {
        return this.getScriptItem().getEquipSound();
    }

    public String getUnequipSound() {
        return this.getScriptItem().getUnequipSound();
    }

    public void setWorldItem(IsoWorldInventoryObject w) {
        this.worldItem = w;
    }

    public void setJobDelta(float delta) {
        this.jobDelta = delta;
    }

    public float getJobDelta() {
        return this.jobDelta;
    }

    public void setJobType(String _type) {
        this.jobType = _type;
    }

    public String getJobType() {
        return this.jobType;
    }

    public boolean hasModData() {
        return this.table != null && !this.table.isEmpty();
    }

    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }

        return this.table;
    }

    public void storeInByteData(IsoObject o) {
        tempBuffer.clear();

        try {
            o.save(tempBuffer, false);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        tempBuffer.flip();
        if (this.byteData == null || this.byteData.capacity() < tempBuffer.limit() - 2 + 8) {
            this.byteData = ByteBuffer.allocate(tempBuffer.limit() - 2 + 8);
        }

        tempBuffer.get();
        tempBuffer.get();
        this.byteData.clear();
        this.byteData.put((byte)87);
        this.byteData.put((byte)86);
        this.byteData.put((byte)69);
        this.byteData.put((byte)82);
        this.byteData.putInt(195);
        this.byteData.put(tempBuffer);
        this.byteData.flip();
    }

    public ByteBuffer getByteData() {
        return this.byteData;
    }

    public boolean isRequiresEquippedBothHands() {
        return this.RequiresEquippedBothHands;
    }

    public float getA() {
        return this.col.a;
    }

    public float getR() {
        return this.col.r;
    }

    public float getG() {
        return this.col.g;
    }

    public float getB() {
        return this.col.b;
    }

    public InventoryItem(String _module, String _name, String _type, String tex) {
        this.col = Color.white;
        this.texture = Texture.trygetTexture(tex);
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
        }

        this.module = _module;
        this.name = _name;
        this.originalName = _name;
        this.type = _type;
        this.fullType = _module + "." + _type;
        this.WorldTexture = tex.replace("Item_", "media/inventory/world/WItem_");
        this.WorldTexture = this.WorldTexture + ".png";
    }

    public InventoryItem(String _module, String _name, String _type, Item item) {
        this.col = Color.white;
        this.texture = item.NormalTexture;
        this.module = _module;
        this.name = _name;
        this.originalName = _name;
        this.type = _type;
        this.fullType = _module + "." + _type;
        this.WorldTexture = item.WorldTextureName;
    }

    public String getType() {
        return this.type;
    }

    public Texture getTex() {
        return this.texture;
    }

    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Item";
    }

    public boolean IsRotten() {
        return this.Age > this.OffAge;
    }

    public float HowRotten() {
        if (this.OffAgeMax - this.OffAge == 0) {
            return this.Age > this.OffAge ? 1.0F : 0.0F;
        } else {
            return (this.Age - this.OffAge) / (this.OffAgeMax - this.OffAge);
        }
    }

    public boolean CanStack(InventoryItem item) {
        return false;
    }

    public boolean ModDataMatches(InventoryItem item) {
        KahluaTable table0 = item.getModData();
        KahluaTable table1 = item.getModData();
        if (table0 == null && table1 == null) {
            return true;
        } else if (table0 == null) {
            return false;
        } else if (table1 == null) {
            return false;
        } else if (table0.len() != table1.len()) {
            return false;
        } else {
            KahluaTableIterator kahluaTableIterator = table0.iterator();

            while (kahluaTableIterator.advance()) {
                Object object0 = table1.rawget(kahluaTableIterator.getKey());
                Object object1 = kahluaTableIterator.getValue();
                if (!object0.equals(object1)) {
                    return false;
                }
            }

            return true;
        }
    }

    public void DoTooltip(ObjectTooltip tooltipUI) {
        tooltipUI.render();
        UIFont uIFont = tooltipUI.getFont();
        int int0 = tooltipUI.getLineSpacing();
        int int1 = 5;
        String string0 = "";
        if (this.Burnt) {
            string0 = string0 + this.BurntString + " ";
        } else if (this.OffAge < 1000000000 && this.Age < this.OffAge) {
            string0 = string0 + this.FreshString + " ";
        } else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            string0 = string0 + this.OffString + " ";
        } else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAge) {
            string0 = string0 + this.StaleString + " ";
        }

        if (this.isCooked() && !this.Burnt && !this.hasTag("HideCooked")) {
            string0 = string0 + this.CookedString + " ";
        } else if (this.IsCookable && !this.Burnt && !(this instanceof DrainableComboItem) && !this.hasTag("HideCooked")) {
            string0 = string0 + this.UnCookedString + " ";
        }

        if (this instanceof Food && ((Food)this).isFrozen()) {
            string0 = string0 + this.FrozenString + " ";
        }

        string0 = string0.trim();
        String string1;
        if (string0.isEmpty()) {
            tooltipUI.DrawText(uIFont, string1 = this.getName(), 5.0, int1, 1.0, 1.0, 0.8F, 1.0);
        } else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            tooltipUI.DrawText(uIFont, string1 = Translator.getText("IGUI_FoodNaming", string0, this.name), 5.0, int1, 1.0, 0.1F, 0.1F, 1.0);
        } else {
            tooltipUI.DrawText(uIFont, string1 = Translator.getText("IGUI_FoodNaming", string0, this.name), 5.0, int1, 1.0, 1.0, 0.8F, 1.0);
        }

        tooltipUI.adjustWidth(5, string1);
        int1 += int0 + 5;
        if (this.extraItems != null) {
            tooltipUI.DrawText(uIFont, Translator.getText("Tooltip_item_Contains"), 5.0, int1, 1.0, 1.0, 0.8F, 1.0);
            int int2 = 5 + TextManager.instance.MeasureStringX(uIFont, Translator.getText("Tooltip_item_Contains")) + 4;
            int int3 = (int0 - 10) / 2;

            for (int int4 = 0; int4 < this.extraItems.size(); int4++) {
                InventoryItem item0 = InventoryItemFactory.CreateItem(this.extraItems.get(int4));
                if (!this.IsCookable && item0.IsCookable) {
                    item0.setCooked(true);
                }

                if (this.isCooked() && item0.IsCookable) {
                    item0.setCooked(true);
                }

                tooltipUI.DrawTextureScaled(item0.getTex(), int2, int1 + int3, 10.0, 10.0, 1.0);
                int2 += 11;
            }

            int1 = int1 + int0 + 5;
        }

        if (this instanceof Food && ((Food)this).spices != null) {
            tooltipUI.DrawText(uIFont, Translator.getText("Tooltip_item_Spices"), 5.0, int1, 1.0, 1.0, 0.8F, 1.0);
            int int5 = 5 + TextManager.instance.MeasureStringX(uIFont, Translator.getText("Tooltip_item_Spices")) + 4;
            int int6 = (int0 - 10) / 2;

            for (int int7 = 0; int7 < ((Food)this).spices.size(); int7++) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(((Food)this).spices.get(int7));
                tooltipUI.DrawTextureScaled(item1.getTex(), int5, int1 + int6, 10.0, 10.0, 1.0);
                int5 += 11;
            }

            int1 = int1 + int0 + 5;
        }

        ObjectTooltip.Layout layout = tooltipUI.beginLayout();
        layout.setMinLabelWidth(80);
        ObjectTooltip.LayoutItem layoutItem = layout.addItem();
        layoutItem.setLabel(Translator.getText("Tooltip_item_Weight") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
        boolean boolean0 = this.isEquipped();
        if (!(this instanceof HandWeapon) && !(this instanceof Clothing) && !(this instanceof DrainableComboItem) && !this.getFullType().contains("Walkie")) {
            float float0 = this.getUnequippedWeight();
            if (float0 > 0.0F && float0 < 0.01F) {
                float0 = 0.01F;
            }

            layoutItem.setValueRightNoPlus(float0);
        } else if (boolean0) {
            layoutItem.setValue(
                this.getCleanString(this.getEquippedWeight())
                    + "    ("
                    + this.getCleanString(this.getUnequippedWeight())
                    + " "
                    + Translator.getText("Tooltip_item_Unequipped")
                    + ")",
                1.0F,
                1.0F,
                1.0F,
                1.0F
            );
        } else if (this.getAttachedSlot() > -1) {
            layoutItem.setValue(
                this.getCleanString(this.getHotbarEquippedWeight())
                    + "    ("
                    + this.getCleanString(this.getUnequippedWeight())
                    + " "
                    + Translator.getText("Tooltip_item_Unequipped")
                    + ")",
                1.0F,
                1.0F,
                1.0F,
                1.0F
            );
        } else {
            layoutItem.setValue(
                this.getCleanString(this.getUnequippedWeight())
                    + "    ("
                    + this.getCleanString(this.getEquippedWeight())
                    + " "
                    + Translator.getText("Tooltip_item_Equipped")
                    + ")",
                1.0F,
                1.0F,
                1.0F,
                1.0F
            );
        }

        if (tooltipUI.getWeightOfStack() > 0.0F) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_item_StackWeight") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            float float1 = tooltipUI.getWeightOfStack();
            if (float1 > 0.0F && float1 < 0.01F) {
                float1 = 0.01F;
            }

            layoutItem.setValueRightNoPlus(float1);
        }

        if (this.getMaxAmmo() > 0 && !(this instanceof HandWeapon)) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValue(this.getCurrentAmmoCount() + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (!(this instanceof HandWeapon) && this.getAmmoType() != null) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("ContextMenu_AmmoType") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            String string2 = InventoryItemFactory.CreateItem(this.getAmmoType()).getDisplayName();
            layoutItem.setValue(Translator.getText(string2), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.gunType != null) {
            Item item2 = ScriptManager.instance.FindItem(this.getGunType());
            if (item2 == null) {
                item2 = ScriptManager.instance.FindItem(this.getModule() + "." + this.ammoType);
            }

            if (item2 != null) {
                layoutItem = layout.addItem();
                layoutItem.setLabel(Translator.getText("ContextMenu_GunType") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem.setValue(item2.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel("getActualWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.getActualWeight());
            layoutItem = layout.addItem();
            layoutItem.setLabel("getWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.getWeight());
            layoutItem = layout.addItem();
            layoutItem.setLabel("getEquippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.getEquippedWeight());
            layoutItem = layout.addItem();
            layoutItem.setLabel("getUnequippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.getUnequippedWeight());
            layoutItem = layout.addItem();
            layoutItem.setLabel("getContentsWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.getContentsWeight());
            if (this instanceof Key || "Doorknob".equals(this.type)) {
                layoutItem = layout.addItem();
                layoutItem.setLabel("DBG: keyId", 1.0F, 1.0F, 0.8F, 1.0F);
                layoutItem.setValueRightNoPlus(this.getKeyId());
            }

            layoutItem = layout.addItem();
            layoutItem.setLabel("ID", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.id);
            layoutItem = layout.addItem();
            layoutItem.setLabel("DictionaryID", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRightNoPlus(this.registry_id);
            ClothingItem clothingItem = this.getClothingItem();
            if (clothingItem != null) {
                layoutItem = layout.addItem();
                layoutItem.setLabel("ClothingItem", 1.0F, 1.0F, 1.0F, 1.0F);
                layoutItem.setValue(this.getClothingItem().m_Name, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        if (this.getFatigueChange() != 0.0F) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_item_Fatigue") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValueRight((int)(this.getFatigueChange() * 100.0F), false);
        }

        if (this instanceof DrainableComboItem) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("IGUI_invpanel_Remaining") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
            float float2 = ((DrainableComboItem)this).getUsedDelta();
            ColorInfo colorInfo0 = new ColorInfo();
            Core.getInstance().getBadHighlitedColor().interp(Core.getInstance().getGoodHighlitedColor(), float2, colorInfo0);
            layoutItem.setProgress(float2, colorInfo0.getR(), colorInfo0.getG(), colorInfo0.getB(), 1.0F);
        }

        if (this.isTaintedWater() && SandboxOptions.instance.EnableTaintedWaterText.getValue()) {
            layoutItem = layout.addItem();
            if (this.isCookable()) {
                layoutItem.setLabel(Translator.getText("Tooltip_item_TaintedWater"), 1.0F, 0.5F, 0.5F, 1.0F);
            } else {
                layoutItem.setLabel(Translator.getText("Tooltip_item_TaintedWater_Plastic"), 1.0F, 0.5F, 0.5F, 1.0F);
            }
        }

        this.DoTooltip(tooltipUI, layout);
        if (this.getRemoteControlID() != -1) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_TrapControllerID"), 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValue(Integer.toString(this.getRemoteControlID()), 1.0F, 1.0F, 0.8F, 1.0F);
        }

        if (!FixingManager.getFixes(this).isEmpty()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Repaired") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            if (this.getHaveBeenRepaired() == 1) {
                layoutItem.setValue(Translator.getText("Tooltip_never"), 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                layoutItem.setValue(this.getHaveBeenRepaired() - 1 + "x", 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        if (this.isEquippedNoSprint()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_CantSprintEquipped"), 1.0F, 0.1F, 0.1F, 1.0F);
        }

        if (this.isWet()) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_Wetness") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
            float float3 = this.getWetCooldown() / 10000.0F;
            ColorInfo colorInfo1 = new ColorInfo();
            Core.getInstance().getGoodHighlitedColor().interp(Core.getInstance().getBadHighlitedColor(), float3, colorInfo1);
            layoutItem.setProgress(float3, colorInfo1.getR(), colorInfo1.getG(), colorInfo1.getB(), 1.0F);
        }

        if (this.getMaxCapacity() > 0) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            float float4 = this.getMaxCapacity();
            if (this.isConditionAffectsCapacity()) {
                float4 = VehiclePart.getNumberByCondition(this.getMaxCapacity(), this.getCondition(), 5.0F);
            }

            if (this.getItemCapacity() > -1.0F) {
                layoutItem.setValue(this.getItemCapacity() + " / " + float4, 1.0F, 1.0F, 0.8F, 1.0F);
            } else {
                layoutItem.setValue("0 / " + float4, 1.0F, 1.0F, 0.8F, 1.0F);
            }
        }

        if (this.getConditionMax() > 0 && this.getMechanicType() > 0) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValue(this.getCondition() + " / " + this.getConditionMax(), 1.0F, 1.0F, 0.8F, 1.0F);
        }

        if (this.isRecordedMedia()) {
            MediaData mediaData = this.getMediaData();
            if (mediaData != null) {
                if (mediaData.getTranslatedTitle() != null) {
                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_media_title") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    layoutItem.setValue(mediaData.getTranslatedTitle(), 1.0F, 1.0F, 1.0F, 1.0F);
                    if (mediaData.getTranslatedSubTitle() != null) {
                        layoutItem = layout.addItem();
                        layoutItem.setLabel("", 1.0F, 1.0F, 0.8F, 1.0F);
                        layoutItem.setValue(mediaData.getTranslatedSubTitle(), 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }

                if (mediaData.getTranslatedAuthor() != null) {
                    layoutItem = layout.addItem();
                    layoutItem.setLabel(Translator.getText("Tooltip_media_author") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                    layoutItem.setValue(mediaData.getTranslatedAuthor(), 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        if (Core.getInstance().getOptionShowItemModInfo() && !this.isVanilla()) {
            layoutItem = layout.addItem();
            Color color = Colors.CornFlowerBlue;
            layoutItem.setLabel("Mod: " + this.getModName(), color.r, color.g, color.b, 1.0F);
            ItemInfo itemInfo = WorldDictionary.getItemInfoFromID(this.registry_id);
            if (itemInfo != null && itemInfo.getModOverrides() != null) {
                layoutItem = layout.addItem();
                float float5 = 0.5F;
                if (itemInfo.getModOverrides().size() == 1) {
                    layoutItem.setLabel(
                        "This item overrides: " + WorldDictionary.getModNameFromID(itemInfo.getModOverrides().get(0)), float5, float5, float5, 1.0F
                    );
                } else {
                    layoutItem.setLabel("This item overrides:", float5, float5, float5, 1.0F);

                    for (int int8 = 0; int8 < itemInfo.getModOverrides().size(); int8++) {
                        layoutItem = layout.addItem();
                        layoutItem.setLabel(" - " + WorldDictionary.getModNameFromID(itemInfo.getModOverrides().get(int8)), float5, float5, float5, 1.0F);
                    }
                }
            }
        }

        if (this.getTooltip() != null) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText(this.tooltip), 1.0F, 1.0F, 0.8F, 1.0F);
        }

        int1 = layout.render(5, int1, tooltipUI);
        tooltipUI.endLayout(layout);
        int1 += tooltipUI.padBottom;
        tooltipUI.setHeight(int1);
        if (tooltipUI.getWidth() < 150.0) {
            tooltipUI.setWidth(150.0);
        }
    }

    public String getCleanString(float weight) {
        float float0 = (int)((weight + 0.005) * 100.0) / 100.0F;
        return Float.toString(float0);
    }

    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
    }

    public void SetContainerPosition(int x, int y) {
        this.containerX = x;
        this.containerY = y;
    }

    public void Use() {
        this.Use(false);
    }

    public void UseItem() {
        this.Use(false);
    }

    public void Use(boolean bCrafting) {
        this.Use(bCrafting, false);
    }

    public void Use(boolean bCrafting, boolean bInContainer) {
        if (this.isDisappearOnUse() || bCrafting) {
            this.uses--;
            if (this.replaceOnUse != null && !bInContainer && !bCrafting && this.container != null) {
                String string = this.replaceOnUse;
                if (!this.replaceOnUse.contains(".")) {
                    string = this.module + "." + string;
                }

                InventoryItem item = this.container.AddItem(string);
                if (item != null) {
                    item.setConditionFromModData(this);
                }

                this.container.setDrawDirty(true);
                this.container.setDirty(true);
                item.setFavorite(this.isFavorite());
            }

            if (this.uses <= 0) {
                if (this.keepOnDeplete) {
                    return;
                }

                if (this.container != null) {
                    if (this.container.parent instanceof IsoGameCharacter && !(this instanceof HandWeapon)) {
                        IsoGameCharacter character = (IsoGameCharacter)this.container.parent;
                        character.removeFromHands(this);
                    }

                    this.container.Items.remove(this);
                    this.container.setDirty(true);
                    this.container.setDrawDirty(true);
                    this.container = null;
                }
            }
        }
    }

    public boolean shouldUpdateInWorld() {
        if (!GameClient.bClient && !this.rainFactorZero && this.canStoreWater() && this.hasReplaceType("WaterSource")) {
            IsoGridSquare square = this.getWorldItem().getSquare();
            return square != null && square.isOutside();
        } else {
            return false;
        }
    }

    public void update() {
        if (this.isWet()) {
            this.wetCooldown = this.wetCooldown - 1.0F * GameTime.instance.getMultiplier();
            if (this.wetCooldown <= 0.0F) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(this.itemWhenDry);
                if (this.isFavorite()) {
                    item1.setFavorite(true);
                }

                IsoWorldInventoryObject worldInventoryObject0 = this.getWorldItem();
                if (worldInventoryObject0 != null) {
                    IsoGridSquare square0 = worldInventoryObject0.getSquare();
                    square0.AddWorldInventoryItem(
                        item1, worldInventoryObject0.getX() % 1.0F, worldInventoryObject0.getY() % 1.0F, worldInventoryObject0.getZ() % 1.0F
                    );
                    square0.transmitRemoveItemFromSquare(worldInventoryObject0);
                    if (this.getContainer() != null) {
                        this.getContainer().setDirty(true);
                        this.getContainer().setDrawDirty(true);
                    }

                    square0.chunk.recalcHashCodeObjects();
                    this.setWorldItem(null);
                } else if (this.getContainer() != null) {
                    this.getContainer().addItem(item1);
                    this.getContainer().Remove(this);
                }

                this.setWet(false);
                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
        }

        if (!GameClient.bClient
            && !this.rainFactorZero
            && this.getWorldItem() != null
            && this.canStoreWater()
            && this.hasReplaceType("WaterSource")
            && RainManager.isRaining()) {
            IsoWorldInventoryObject worldInventoryObject1 = this.getWorldItem();
            IsoGridSquare square1 = worldInventoryObject1.getSquare();
            if (square1 != null && square1.isOutside()) {
                InventoryItem item2 = InventoryItemFactory.CreateItem(this.getReplaceType("WaterSource"));
                if (item2 == null) {
                    this.rainFactorZero = true;
                    return;
                }

                item2.setCondition(this.getCondition());
                if (item2 instanceof DrainableComboItem && item2.canStoreWater()) {
                    if (((DrainableComboItem)item2).getRainFactor() == 0.0F) {
                        this.rainFactorZero = true;
                        return;
                    }

                    ((DrainableComboItem)item2).setUsedDelta(0.0F);
                    worldInventoryObject1.swapItem(item2);
                }
            }
        }
    }

    public boolean finishupdate() {
        return !GameClient.bClient
                && !this.rainFactorZero
                && this.canStoreWater()
                && this.hasReplaceType("WaterSource")
                && this.getWorldItem() != null
                && this.getWorldItem().getObjectIndex() != -1
            ? false
            : !this.isWet();
    }

    public void updateSound(BaseSoundEmitter emitter) {
    }

    public String getFullType() {
        assert this.fullType != null && this.fullType.equals(this.module + "." + this.type);

        return this.fullType;
    }

    public void save(ByteBuffer output, boolean net) throws IOException {
        net = false;
        if (GameWindow.DEBUG_SAVE) {
            DebugLog.log(this.getFullType());
        }

        output.putShort(this.registry_id);
        output.put((byte)this.getSaveType());
        output.putInt(this.id);
        BitHeaderWrite bitHeaderWrite0 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        if (this.uses != 1) {
            bitHeaderWrite0.addFlags(1);
            if (this.uses > 32767) {
                output.putShort((short)32767);
            } else {
                output.putShort((short)this.uses);
            }
        }

        if (this.IsDrainable() && ((DrainableComboItem)this).getUsedDelta() < 1.0F) {
            bitHeaderWrite0.addFlags(2);
            float float0 = ((DrainableComboItem)this).getUsedDelta();
            byte byte0 = (byte)((byte)(float0 * 255.0F) + -128);
            output.put(byte0);
        }

        if (this.Condition != this.ConditionMax) {
            bitHeaderWrite0.addFlags(4);
            output.put((byte)this.getCondition());
        }

        if (this.visual != null) {
            bitHeaderWrite0.addFlags(8);
            this.visual.save(output);
        }

        if (this.isCustomColor() && (this.col.r != 1.0F || this.col.g != 1.0F || this.col.b != 1.0F || this.col.a != 1.0F)) {
            bitHeaderWrite0.addFlags(16);
            output.put(Bits.packFloatUnitToByte(this.getColor().r));
            output.put(Bits.packFloatUnitToByte(this.getColor().g));
            output.put(Bits.packFloatUnitToByte(this.getColor().b));
            output.put(Bits.packFloatUnitToByte(this.getColor().a));
        }

        if (this.itemCapacity != -1.0F) {
            bitHeaderWrite0.addFlags(32);
            output.putFloat(this.itemCapacity);
        }

        BitHeaderWrite bitHeaderWrite1 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, output);
        if (this.table != null && !this.table.isEmpty()) {
            bitHeaderWrite1.addFlags(1);
            this.table.save(output);
        }

        if (this.isActivated()) {
            bitHeaderWrite1.addFlags(2);
        }

        if (this.haveBeenRepaired != 1) {
            bitHeaderWrite1.addFlags(4);
            output.putShort((short)this.getHaveBeenRepaired());
        }

        if (this.name != null && !this.name.equals(this.originalName)) {
            bitHeaderWrite1.addFlags(8);
            GameWindow.WriteString(output, this.name);
        }

        if (this.byteData != null) {
            bitHeaderWrite1.addFlags(16);
            this.byteData.rewind();
            output.putInt(this.byteData.limit());
            output.put(this.byteData);
            this.byteData.flip();
        }

        if (this.extraItems != null && this.extraItems.size() > 0) {
            bitHeaderWrite1.addFlags(32);
            output.putInt(this.extraItems.size());

            for (int int0 = 0; int0 < this.extraItems.size(); int0++) {
                output.putShort(WorldDictionary.getItemRegistryID(this.extraItems.get(int0)));
            }
        }

        if (this.isCustomName()) {
            bitHeaderWrite1.addFlags(64);
        }

        if (this.isCustomWeight()) {
            bitHeaderWrite1.addFlags(128);
            output.putFloat(this.isCustomWeight() ? this.getActualWeight() : -1.0F);
        }

        if (this.keyId != -1) {
            bitHeaderWrite1.addFlags(256);
            output.putInt(this.getKeyId());
        }

        if (this.isTaintedWater()) {
            bitHeaderWrite1.addFlags(512);
        }

        if (this.remoteControlID != -1 || this.remoteRange != 0) {
            bitHeaderWrite1.addFlags(1024);
            output.putInt(this.getRemoteControlID());
            output.putInt(this.getRemoteRange());
        }

        if (this.colorRed != 1.0F || this.colorGreen != 1.0F || this.colorBlue != 1.0F) {
            bitHeaderWrite1.addFlags(2048);
            output.put(Bits.packFloatUnitToByte(this.colorRed));
            output.put(Bits.packFloatUnitToByte(this.colorGreen));
            output.put(Bits.packFloatUnitToByte(this.colorBlue));
        }

        if (this.worker != null) {
            bitHeaderWrite1.addFlags(4096);
            GameWindow.WriteString(output, this.getWorker());
        }

        if (this.wetCooldown != -1.0F) {
            bitHeaderWrite1.addFlags(8192);
            output.putFloat(this.wetCooldown);
        }

        if (this.isFavorite()) {
            bitHeaderWrite1.addFlags(16384);
        }

        if (this.stashMap != null) {
            bitHeaderWrite1.addFlags(32768);
            GameWindow.WriteString(output, this.stashMap);
        }

        if (this.isInfected()) {
            bitHeaderWrite1.addFlags(65536);
        }

        if (this.currentAmmoCount != 0) {
            bitHeaderWrite1.addFlags(131072);
            output.putInt(this.currentAmmoCount);
        }

        if (this.attachedSlot != -1) {
            bitHeaderWrite1.addFlags(262144);
            output.putInt(this.attachedSlot);
        }

        if (this.attachedSlotType != null) {
            bitHeaderWrite1.addFlags(524288);
            GameWindow.WriteString(output, this.attachedSlotType);
        }

        if (this.attachedToModel != null) {
            bitHeaderWrite1.addFlags(1048576);
            GameWindow.WriteString(output, this.attachedToModel);
        }

        if (this.maxCapacity != -1) {
            bitHeaderWrite1.addFlags(2097152);
            output.putInt(this.maxCapacity);
        }

        if (this.isRecordedMedia()) {
            bitHeaderWrite1.addFlags(4194304);
            output.putShort(this.recordedMediaIndex);
        }

        if (this.worldZRotation > -1) {
            bitHeaderWrite1.addFlags(8388608);
            output.putInt(this.worldZRotation);
        }

        if (this.worldScale != 1.0F) {
            bitHeaderWrite1.addFlags(16777216);
            output.putFloat(this.worldScale);
        }

        if (this.isInitialised) {
            bitHeaderWrite1.addFlags(33554432);
        }

        if (!bitHeaderWrite1.equals(0)) {
            bitHeaderWrite0.addFlags(64);
            bitHeaderWrite1.write();
        } else {
            output.position(bitHeaderWrite1.getStartPosition());
        }

        bitHeaderWrite0.write();
        bitHeaderWrite0.release();
        bitHeaderWrite1.release();
    }

    public static InventoryItem loadItem(ByteBuffer input, int WorldVersion) throws IOException {
        return loadItem(input, WorldVersion, true);
    }

    /**
     * Attempts loading the item including creation, uppon failure bytes might be skipped or the buffer position may be set to end item position.  Item needs to be saved with size.
     * @return InventoryItem, or null if the item failed loading or if Creating the item failed due to being obsolete etc.
     */
    public static InventoryItem loadItem(ByteBuffer input, int WorldVersion, boolean doSaveTypeCheck) throws IOException {
        int int0 = input.getInt();
        if (int0 <= 0) {
            throw new IOException("InventoryItem.loadItem() invalid item data length: " + int0);
        } else {
            int int1 = input.position();
            short short0 = input.getShort();
            byte byte0 = -1;
            if (WorldVersion >= 70) {
                byte0 = input.get();
                if (byte0 < 0) {
                    DebugLog.log("InventoryItem.loadItem() invalid item save-type " + byte0 + ", itemtype: " + WorldDictionary.getItemTypeDebugString(short0));
                    return null;
                }
            }

            InventoryItem item = InventoryItemFactory.CreateItem(short0);
            if (doSaveTypeCheck && byte0 != -1 && item != null && item.getSaveType() != byte0) {
                DebugLog.log(
                    "InventoryItem.loadItem() ignoring \"" + item.getFullType() + "\" because type changed from " + byte0 + " to " + item.getSaveType()
                );
                item = null;
            }

            if (item != null) {
                try {
                    item.load(input, WorldVersion);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                    item = null;
                }
            }

            if (item != null) {
                if (int0 != -1 && input.position() != int1 + int0) {
                    input.position(int1 + int0);
                    DebugLog.log(
                        "InventoryItem.loadItem() data length not matching, resetting buffer position to '"
                            + (int1 + int0)
                            + "'. itemtype: "
                            + WorldDictionary.getItemTypeDebugString(short0)
                    );
                    if (Core.bDebug) {
                        throw new IOException(
                            "InventoryItem.loadItem() read more data than save() wrote (" + WorldDictionary.getItemTypeDebugString(short0) + ")"
                        );
                    }
                }

                return item;
            } else {
                if (input.position() >= int1 + int0) {
                    if (input.position() >= int1 + int0) {
                        input.position(int1 + int0);
                        DebugLog.log(
                            "InventoryItem.loadItem() item == null, resetting buffer position to '"
                                + (int1 + int0)
                                + "'. itemtype: "
                                + WorldDictionary.getItemTypeDebugString(short0)
                        );
                    }
                } else {
                    while (input.position() < int1 + int0) {
                        input.get();
                    }

                    DebugLog.log("InventoryItem.loadItem() item == null, skipped bytes. itemtype: " + WorldDictionary.getItemTypeDebugString(short0));
                }

                return null;
            }
        }
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.id = input.getInt();
        BitHeaderRead bitHeaderRead0 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
        this.uses = 1;
        if (this.IsDrainable()) {
            ((DrainableComboItem)this).setUsedDelta(1.0F);
        }

        this.Condition = this.ConditionMax;
        this.customColor = false;
        this.col = Color.white;
        this.itemCapacity = -1.0F;
        this.activated = false;
        this.haveBeenRepaired = 1;
        this.customName = false;
        this.customWeight = false;
        this.keyId = -1;
        this.taintedWater = false;
        this.remoteControlID = -1;
        this.remoteRange = 0;
        this.colorRed = this.colorGreen = this.colorBlue = 1.0F;
        this.worker = null;
        this.wetCooldown = -1.0F;
        this.favorite = false;
        this.stashMap = null;
        this.zombieInfected = false;
        this.currentAmmoCount = 0;
        this.attachedSlot = -1;
        this.attachedSlotType = null;
        this.attachedToModel = null;
        this.maxCapacity = -1;
        this.recordedMediaIndex = -1;
        this.worldZRotation = -1;
        this.worldScale = 1.0F;
        this.isInitialised = false;
        if (!bitHeaderRead0.equals(0)) {
            if (bitHeaderRead0.hasFlags(1)) {
                this.uses = input.getShort();
            }

            if (bitHeaderRead0.hasFlags(2)) {
                byte byte0 = input.get();
                float float0 = PZMath.clamp((byte0 - -128) / 255.0F, 0.0F, 1.0F);
                ((DrainableComboItem)this).setUsedDelta(float0);
            }

            if (bitHeaderRead0.hasFlags(4)) {
                this.setCondition(input.get(), false);
            }

            if (bitHeaderRead0.hasFlags(8)) {
                this.visual = new ItemVisual();
                this.visual.load(input, WorldVersion);
            }

            if (bitHeaderRead0.hasFlags(16)) {
                float float1 = Bits.unpackByteToFloatUnit(input.get());
                float float2 = Bits.unpackByteToFloatUnit(input.get());
                float float3 = Bits.unpackByteToFloatUnit(input.get());
                float float4 = Bits.unpackByteToFloatUnit(input.get());
                this.setColor(new Color(float1, float2, float3, float4));
            }

            if (bitHeaderRead0.hasFlags(32)) {
                this.itemCapacity = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(64)) {
                BitHeaderRead bitHeaderRead1 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, input);
                if (bitHeaderRead1.hasFlags(1)) {
                    if (this.table == null) {
                        this.table = LuaManager.platform.newTable();
                    }

                    this.table.load(input, WorldVersion);
                }

                this.activated = bitHeaderRead1.hasFlags(2);
                if (bitHeaderRead1.hasFlags(4)) {
                    this.setHaveBeenRepaired(input.getShort());
                }

                if (bitHeaderRead1.hasFlags(8)) {
                    this.name = GameWindow.ReadString(input);
                }

                if (bitHeaderRead1.hasFlags(16)) {
                    int int0 = input.getInt();
                    this.byteData = ByteBuffer.allocate(int0);

                    for (int int1 = 0; int1 < int0; int1++) {
                        this.byteData.put(input.get());
                    }

                    this.byteData.flip();
                }

                if (bitHeaderRead1.hasFlags(32)) {
                    int int2 = input.getInt();
                    if (int2 > 0) {
                        this.extraItems = new ArrayList<>();

                        for (int int3 = 0; int3 < int2; int3++) {
                            short short0 = input.getShort();
                            String string = WorldDictionary.getItemTypeFromID(short0);
                            this.extraItems.add(string);
                        }
                    }
                }

                this.setCustomName(bitHeaderRead1.hasFlags(64));
                if (bitHeaderRead1.hasFlags(128)) {
                    float float5 = input.getFloat();
                    if (float5 >= 0.0F) {
                        this.setActualWeight(float5);
                        this.setWeight(float5);
                        this.setCustomWeight(true);
                    }
                }

                if (bitHeaderRead1.hasFlags(256)) {
                    this.setKeyId(input.getInt());
                }

                this.setTaintedWater(bitHeaderRead1.hasFlags(512));
                if (bitHeaderRead1.hasFlags(1024)) {
                    this.setRemoteControlID(input.getInt());
                    this.setRemoteRange(input.getInt());
                }

                if (bitHeaderRead1.hasFlags(2048)) {
                    float float6 = Bits.unpackByteToFloatUnit(input.get());
                    float float7 = Bits.unpackByteToFloatUnit(input.get());
                    float float8 = Bits.unpackByteToFloatUnit(input.get());
                    this.setColorRed(float6);
                    this.setColorGreen(float7);
                    this.setColorBlue(float8);
                    this.setColor(new Color(this.colorRed, this.colorGreen, this.colorBlue));
                }

                if (bitHeaderRead1.hasFlags(4096)) {
                    this.setWorker(GameWindow.ReadString(input));
                }

                if (bitHeaderRead1.hasFlags(8192)) {
                    this.setWetCooldown(input.getFloat());
                }

                this.setFavorite(bitHeaderRead1.hasFlags(16384));
                if (bitHeaderRead1.hasFlags(32768)) {
                    this.stashMap = GameWindow.ReadString(input);
                }

                this.setInfected(bitHeaderRead1.hasFlags(65536));
                if (bitHeaderRead1.hasFlags(131072)) {
                    this.setCurrentAmmoCount(input.getInt());
                }

                if (bitHeaderRead1.hasFlags(262144)) {
                    this.attachedSlot = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(524288)) {
                    if (WorldVersion < 179) {
                        short short1 = input.getShort();
                        this.attachedSlotType = null;
                    } else {
                        this.attachedSlotType = GameWindow.ReadString(input);
                    }
                }

                if (bitHeaderRead1.hasFlags(1048576)) {
                    this.attachedToModel = GameWindow.ReadString(input);
                }

                if (bitHeaderRead1.hasFlags(2097152)) {
                    this.maxCapacity = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(4194304)) {
                    this.setRecordedMediaIndex(input.getShort());
                }

                if (bitHeaderRead1.hasFlags(8388608)) {
                    this.setWorldZRotation(input.getInt());
                }

                if (bitHeaderRead1.hasFlags(16777216)) {
                    this.worldScale = input.getFloat();
                }

                this.setInitialised(bitHeaderRead1.hasFlags(33554432));
                bitHeaderRead1.release();
            }
        }

        bitHeaderRead0.release();
    }

    public boolean IsFood() {
        return false;
    }

    public boolean IsWeapon() {
        return false;
    }

    public boolean IsDrainable() {
        return false;
    }

    public boolean IsLiterature() {
        return false;
    }

    public boolean IsClothing() {
        return false;
    }

    public boolean IsInventoryContainer() {
        return false;
    }

    public boolean IsMap() {
        return false;
    }

    static InventoryItem LoadFromFile(DataInputStream dataInputStream) throws IOException {
        GameWindow.ReadString(dataInputStream);
        return null;
    }

    public ItemContainer getOutermostContainer() {
        if (this.container != null && !"floor".equals(this.container.type)) {
            ItemContainer containerx = this.container;

            while (
                containerx.getContainingItem() != null
                    && containerx.getContainingItem().getContainer() != null
                    && !"floor".equals(containerx.getContainingItem().getContainer().type)
            ) {
                containerx = containerx.getContainingItem().getContainer();
            }

            return containerx;
        } else {
            return null;
        }
    }

    public boolean isInLocalPlayerInventory() {
        if (!GameClient.bClient) {
            return false;
        } else {
            ItemContainer containerx = this.getOutermostContainer();
            if (containerx == null) {
                return false;
            } else {
                return containerx.getParent() instanceof IsoPlayer ? ((IsoPlayer)containerx.getParent()).isLocalPlayer() : false;
            }
        }
    }

    public boolean isInPlayerInventory() {
        ItemContainer containerx = this.getOutermostContainer();
        return containerx == null ? false : containerx.getParent() instanceof IsoPlayer;
    }

    public ItemReplacement getItemReplacementPrimaryHand() {
        return this.ScriptItem.replacePrimaryHand;
    }

    public ItemReplacement getItemReplacementSecondHand() {
        return this.ScriptItem.replaceSecondHand;
    }

    public ClothingItem getClothingItem() {
        if ("RightHand".equalsIgnoreCase(this.getAlternateModelName())) {
            return this.getItemReplacementPrimaryHand().clothingItem;
        } else {
            return "LeftHand".equalsIgnoreCase(this.getAlternateModelName())
                ? this.getItemReplacementSecondHand().clothingItem
                : this.ScriptItem.getClothingItemAsset();
        }
    }

    public String getAlternateModelName() {
        if (this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter) {
            IsoGameCharacter character = (IsoGameCharacter)this.getContainer().getParent();
            if (character.getPrimaryHandItem() == this && this.getItemReplacementPrimaryHand() != null) {
                return "RightHand";
            }

            if (character.getSecondaryHandItem() == this && this.getItemReplacementSecondHand() != null) {
                return "LeftHand";
            }
        }

        return this.m_alternateModelName;
    }

    public ItemVisual getVisual() {
        ClothingItem clothingItem = this.getClothingItem();
        if (clothingItem != null && clothingItem.isReady()) {
            if (this.visual == null) {
                this.visual = new ItemVisual();
                this.visual.setItemType(this.getFullType());
                this.visual.pickUninitializedValues(clothingItem);
            }

            this.visual.setClothingItemName(clothingItem.m_Name);
            this.visual.setAlternateModelName(this.getAlternateModelName());
            return this.visual;
        } else {
            this.visual = null;
            return null;
        }
    }

    public boolean allowRandomTint() {
        ClothingItem clothingItem = this.getClothingItem();
        return clothingItem != null ? clothingItem.m_AllowRandomTint : false;
    }

    public void synchWithVisual() {
        if (this instanceof Clothing || this instanceof InventoryContainer) {
            ItemVisual itemVisual = this.getVisual();
            if (itemVisual != null) {
                if (this instanceof Clothing && this.getBloodClothingType() != null) {
                    BloodClothingType.calcTotalBloodLevel((Clothing)this);
                }

                ClothingItem clothingItem = this.getClothingItem();
                if (clothingItem.m_AllowRandomTint) {
                    this.setColor(new Color(itemVisual.m_Tint.r, itemVisual.m_Tint.g, itemVisual.m_Tint.b));
                } else {
                    this.setColor(new Color(this.getColorRed(), this.getColorGreen(), this.getColorBlue()));
                }

                if ((clothingItem.m_BaseTextures.size() > 1 || itemVisual.m_TextureChoice > -1) && this.getIconsForTexture() != null) {
                    String string = null;
                    if (itemVisual.m_BaseTexture > -1 && this.getIconsForTexture().size() > itemVisual.m_BaseTexture) {
                        string = this.getIconsForTexture().get(itemVisual.m_BaseTexture);
                    } else if (itemVisual.m_TextureChoice > -1 && this.getIconsForTexture().size() > itemVisual.m_TextureChoice) {
                        string = this.getIconsForTexture().get(itemVisual.m_TextureChoice);
                    }

                    if (!StringUtils.isNullOrWhitespace(string)) {
                        this.texture = Texture.trygetTexture("Item_" + string);
                        if (this.texture == null) {
                            this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the containerX
     */
    public int getContainerX() {
        return this.containerX;
    }

    /**
     * 
     * @param _containerX the containerX to set
     */
    public void setContainerX(int _containerX) {
        this.containerX = _containerX;
    }

    /**
     * @return the containerY
     */
    public int getContainerY() {
        return this.containerY;
    }

    /**
     * 
     * @param _containerY the containerY to set
     */
    public void setContainerY(int _containerY) {
        this.containerY = _containerY;
    }

    /**
     * @return the DisappearOnUse
     */
    public boolean isDisappearOnUse() {
        return this.getScriptItem().isDisappearOnUse();
    }

    /**
     * @return the name
     */
    public String getName() {
        if (this.isBroken()) {
            return Translator.getText("IGUI_ItemNaming", this.brokenString, this.name);
        } else if (this.isTaintedWater() && SandboxOptions.instance.EnableTaintedWaterText.getValue()) {
            return Translator.getText("IGUI_ItemNameTaintedWater", this.name);
        } else if (this.getRemoteControlID() != -1) {
            return Translator.getText("IGUI_ItemNameControllerLinked", this.name);
        } else {
            return this.getMechanicType() > 0
                ? Translator.getText("IGUI_ItemNameMechanicalType", this.name, Translator.getText("IGUI_VehicleType_" + this.getMechanicType()))
                : this.name;
        }
    }

    /**
     * 
     * @param _name the name to set
     */
    public void setName(String _name) {
        if (_name.length() > 256) {
            _name = _name.substring(0, Math.min(_name.length(), 256));
        }

        this.name = _name;
    }

    /**
     * @return the replaceOnUse
     */
    public String getReplaceOnUse() {
        return this.replaceOnUse;
    }

    /**
     * 
     * @param _replaceOnUse the replaceOnUse to set
     */
    public void setReplaceOnUse(String _replaceOnUse) {
        this.replaceOnUse = _replaceOnUse;
        this.replaceOnUseFullType = StringUtils.moduleDotType(this.getModule(), _replaceOnUse);
    }

    public String getReplaceOnUseFullType() {
        return this.replaceOnUseFullType;
    }

    /**
     * @return the ConditionMax
     */
    public int getConditionMax() {
        return this.ConditionMax;
    }

    /**
     * 
     * @param _ConditionMax the ConditionMax to set
     */
    public void setConditionMax(int _ConditionMax) {
        this.ConditionMax = _ConditionMax;
    }

    /**
     * @return the rightClickContainer
     */
    public ItemContainer getRightClickContainer() {
        return this.rightClickContainer;
    }

    /**
     * 
     * @param _rightClickContainer the rightClickContainer to set
     */
    public void setRightClickContainer(ItemContainer _rightClickContainer) {
        this.rightClickContainer = _rightClickContainer;
    }

    /**
     * @return the swingAnim
     */
    public String getSwingAnim() {
        return this.getScriptItem().SwingAnim;
    }

    /**
     * @return the texture
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * 
     * @param _texture the texture to set
     */
    public void setTexture(Texture _texture) {
        this.texture = _texture;
    }

    /**
     * @return the texturerotten
     */
    public Texture getTexturerotten() {
        return this.texturerotten;
    }

    /**
     * 
     * @param _texturerotten the texturerotten to set
     */
    public void setTexturerotten(Texture _texturerotten) {
        this.texturerotten = _texturerotten;
    }

    /**
     * @return the textureCooked
     */
    public Texture getTextureCooked() {
        return this.textureCooked;
    }

    /**
     * 
     * @param _textureCooked the textureCooked to set
     */
    public void setTextureCooked(Texture _textureCooked) {
        this.textureCooked = _textureCooked;
    }

    /**
     * @return the textureBurnt
     */
    public Texture getTextureBurnt() {
        return this.textureBurnt;
    }

    /**
     * 
     * @param _textureBurnt the textureBurnt to set
     */
    public void setTextureBurnt(Texture _textureBurnt) {
        this.textureBurnt = _textureBurnt;
    }

    /**
     * 
     * @param _type the type to set
     */
    public void setType(String _type) {
        this.type = _type;
        this.fullType = this.module + "." + _type;
    }

    public int getCurrentUses() {
        return this.uses;
    }

    /**
     * @return the uses
     */
    @Deprecated
    public int getUses() {
        return 1;
    }

    /**
     * 
     * @param _uses the uses to set
     */
    @Deprecated
    public void setUses(int _uses) {
    }

    /**
     * @return the Age
     */
    public float getAge() {
        return this.Age;
    }

    /**
     * 
     * @param _Age the Age to set
     */
    public void setAge(float _Age) {
        this.Age = _Age;
    }

    public float getLastAged() {
        return this.LastAged;
    }

    public void setLastAged(float time) {
        this.LastAged = time;
    }

    public void updateAge() {
    }

    public void setAutoAge() {
    }

    /**
     * @return the IsCookable
     */
    public boolean isIsCookable() {
        return this.IsCookable;
    }

    /**
     * @return the IsCookable
     */
    public boolean isCookable() {
        return this.IsCookable;
    }

    /**
     * 
     * @param _IsCookable the IsCookable to set
     */
    public void setIsCookable(boolean _IsCookable) {
        this.IsCookable = _IsCookable;
    }

    /**
     * @return the CookingTime
     */
    public float getCookingTime() {
        return this.CookingTime;
    }

    /**
     * 
     * @param _CookingTime the CookingTime to set
     */
    public void setCookingTime(float _CookingTime) {
        this.CookingTime = _CookingTime;
    }

    /**
     * @return the MinutesToCook
     */
    public float getMinutesToCook() {
        return this.MinutesToCook;
    }

    /**
     * 
     * @param _MinutesToCook the MinutesToCook to set
     */
    public void setMinutesToCook(float _MinutesToCook) {
        this.MinutesToCook = _MinutesToCook;
    }

    /**
     * @return the MinutesToBurn
     */
    public float getMinutesToBurn() {
        return this.MinutesToBurn;
    }

    /**
     * 
     * @param _MinutesToBurn the MinutesToBurn to set
     */
    public void setMinutesToBurn(float _MinutesToBurn) {
        this.MinutesToBurn = _MinutesToBurn;
    }

    /**
     * @return the Cooked
     */
    public boolean isCooked() {
        return this.Cooked;
    }

    /**
     * 
     * @param _Cooked the Cooked to set
     */
    public void setCooked(boolean _Cooked) {
        this.Cooked = _Cooked;
    }

    /**
     * @return the Burnt
     */
    public boolean isBurnt() {
        return this.Burnt;
    }

    /**
     * 
     * @param _Burnt the Burnt to set
     */
    public void setBurnt(boolean _Burnt) {
        this.Burnt = _Burnt;
    }

    /**
     * @return the OffAge
     */
    public int getOffAge() {
        return this.OffAge;
    }

    /**
     * 
     * @param _OffAge the OffAge to set
     */
    public void setOffAge(int _OffAge) {
        this.OffAge = _OffAge;
    }

    /**
     * @return the OffAgeMax
     */
    public int getOffAgeMax() {
        return this.OffAgeMax;
    }

    /**
     * 
     * @param _OffAgeMax the OffAgeMax to set
     */
    public void setOffAgeMax(int _OffAgeMax) {
        this.OffAgeMax = _OffAgeMax;
    }

    /**
     * @return the Weight
     */
    public float getWeight() {
        return this.Weight;
    }

    /**
     * 
     * @param _Weight the Weight to set
     */
    public void setWeight(float _Weight) {
        this.Weight = _Weight;
    }

    /**
     * @return the ActualWeight
     */
    public float getActualWeight() {
        return this.getDisplayName().equals(this.getFullType()) ? 0.0F : this.ActualWeight;
    }

    /**
     * 
     * @param _ActualWeight the ActualWeight to set
     */
    public void setActualWeight(float _ActualWeight) {
        this.ActualWeight = _ActualWeight;
    }

    /**
     * @return the WorldTexture
     */
    public String getWorldTexture() {
        return this.WorldTexture;
    }

    /**
     * 
     * @param _WorldTexture the WorldTexture to set
     */
    public void setWorldTexture(String _WorldTexture) {
        this.WorldTexture = _WorldTexture;
    }

    /**
     * @return the Description
     */
    public String getDescription() {
        return this.Description;
    }

    /**
     * 
     * @param _Description the Description to set
     */
    public void setDescription(String _Description) {
        this.Description = _Description;
    }

    /**
     * @return the Condition
     */
    public int getCondition() {
        return this.Condition;
    }

    public void setCondition(int _Condition, boolean doSound) {
        _Condition = Math.max(0, _Condition);
        if (this.Condition > 0
            && _Condition <= 0
            && doSound
            && this.getBreakSound() != null
            && !this.getBreakSound().isEmpty()
            && IsoPlayer.getInstance() != null) {
            IsoPlayer.getInstance().playSound(this.getBreakSound());
        }

        this.Condition = _Condition;
        this.setBroken(_Condition <= 0);
    }

    /**
     * 
     * @param _Condition the Condition to set
     */
    public void setCondition(int _Condition) {
        this.setCondition(_Condition, true);
    }

    /**
     * @return the OffString
     */
    public String getOffString() {
        return this.OffString;
    }

    /**
     * 
     * @param _OffString the OffString to set
     */
    public void setOffString(String _OffString) {
        this.OffString = _OffString;
    }

    /**
     * @return the CookedString
     */
    public String getCookedString() {
        return this.CookedString;
    }

    /**
     * 
     * @param _CookedString the CookedString to set
     */
    public void setCookedString(String _CookedString) {
        this.CookedString = _CookedString;
    }

    /**
     * @return the UnCookedString
     */
    public String getUnCookedString() {
        return this.UnCookedString;
    }

    /**
     * 
     * @param _UnCookedString the UnCookedString to set
     */
    public void setUnCookedString(String _UnCookedString) {
        this.UnCookedString = _UnCookedString;
    }

    /**
     * @return the BurntString
     */
    public String getBurntString() {
        return this.BurntString;
    }

    /**
     * 
     * @param _BurntString the BurntString to set
     */
    public void setBurntString(String _BurntString) {
        this.BurntString = _BurntString;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return this.module;
    }

    /**
     * 
     * @param _module the module to set
     */
    public void setModule(String _module) {
        this.module = _module;
        this.fullType = _module + "." + this.type;
    }

    /**
     * @return the AlwaysWelcomeGift
     */
    public boolean isAlwaysWelcomeGift() {
        return this.getScriptItem().isAlwaysWelcomeGift();
    }

    /**
     * @return the CanBandage
     */
    public boolean isCanBandage() {
        return this.getScriptItem().isCanBandage();
    }

    /**
     * @return the boredomChange
     */
    public float getBoredomChange() {
        return this.boredomChange;
    }

    /**
     * 
     * @param _boredomChange the boredomChange to set
     */
    public void setBoredomChange(float _boredomChange) {
        this.boredomChange = _boredomChange;
    }

    /**
     * @return the unhappyChange
     */
    public float getUnhappyChange() {
        return this.unhappyChange;
    }

    /**
     * 
     * @param _unhappyChange the unhappyChange to set
     */
    public void setUnhappyChange(float _unhappyChange) {
        this.unhappyChange = _unhappyChange;
    }

    /**
     * @return the stressChange
     */
    public float getStressChange() {
        return this.stressChange;
    }

    /**
     * 
     * @param _stressChange the stressChange to set
     */
    public void setStressChange(float _stressChange) {
        this.stressChange = _stressChange;
    }

    public ArrayList<String> getTags() {
        return this.ScriptItem.getTags();
    }

    public boolean hasTag(String tag) {
        ArrayList arrayList = this.getTags();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            if (((String)arrayList.get(int0)).equalsIgnoreCase(tag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the Taken
     */
    public ArrayList<IsoObject> getTaken() {
        return this.Taken;
    }

    /**
     * 
     * @param _Taken the Taken to set
     */
    public void setTaken(ArrayList<IsoObject> _Taken) {
        this.Taken = _Taken;
    }

    /**
     * @return the placeDir
     */
    public IsoDirections getPlaceDir() {
        return this.placeDir;
    }

    /**
     * 
     * @param _placeDir the placeDir to set
     */
    public void setPlaceDir(IsoDirections _placeDir) {
        this.placeDir = _placeDir;
    }

    /**
     * @return the newPlaceDir
     */
    public IsoDirections getNewPlaceDir() {
        return this.newPlaceDir;
    }

    /**
     * 
     * @param _newPlaceDir the newPlaceDir to set
     */
    public void setNewPlaceDir(IsoDirections _newPlaceDir) {
        this.newPlaceDir = _newPlaceDir;
    }

    public void setReplaceOnUseOn(String _ReplaceOnUseOn) {
        this.ReplaceOnUseOn = _ReplaceOnUseOn;
    }

    public String getReplaceOnUseOn() {
        return this.ReplaceOnUseOn;
    }

    public String getReplaceOnUseOnString() {
        String string = this.getReplaceOnUseOn();
        if (string.split("-")[0].trim().contains("WaterSource")) {
            string = string.split("-")[1];
            if (!string.contains(".")) {
                string = this.getModule() + "." + string;
            }
        }

        return string;
    }

    public String getReplaceTypes() {
        return this.getScriptItem().getReplaceTypes();
    }

    public HashMap<String, String> getReplaceTypesMap() {
        return this.getScriptItem().getReplaceTypesMap();
    }

    public String getReplaceType(String key) {
        return this.getScriptItem().getReplaceType(key);
    }

    public boolean hasReplaceType(String key) {
        return this.getScriptItem().hasReplaceType(key);
    }

    public void setIsWaterSource(boolean _IsWaterSource) {
        this.IsWaterSource = _IsWaterSource;
    }

    /**
     * @return the IsWaterSource
     */
    public boolean isWaterSource() {
        return this.IsWaterSource;
    }

    boolean CanStackNoTemp(InventoryItem var1) {
        return false;
    }

    public void CopyModData(KahluaTable DefaultModData) {
        this.copyModData(DefaultModData);
    }

    public void copyModData(KahluaTable modData) {
        if (this.table != null) {
            this.table.wipe();
        }

        if (modData != null) {
            LuaManager.copyTable(this.getModData(), modData);
        }
    }

    public int getCount() {
        return this.Count;
    }

    public void setCount(int count) {
        this.Count = count;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean _activated) {
        this.activated = _activated;
        if (this.canEmitLight() && GameClient.bClient && this.getEquipParent() != null) {
            if (this.getEquipParent().getPrimaryHandItem() == this) {
                this.getEquipParent().reportEvent("EventSetActivatedPrimary");
            } else if (this.getEquipParent().getSecondaryHandItem() == this) {
                this.getEquipParent().reportEvent("EventSetActivatedSecondary");
            }
        }
    }

    public void setActivatedRemote(boolean _activated) {
        this.activated = _activated;
    }

    public void setCanBeActivated(boolean activatedItem) {
        this.canBeActivated = activatedItem;
    }

    public boolean canBeActivated() {
        return this.canBeActivated;
    }

    public void setLightStrength(float _lightStrength) {
        this.lightStrength = _lightStrength;
    }

    public float getLightStrength() {
        return this.lightStrength;
    }

    public boolean isTorchCone() {
        return this.isTorchCone;
    }

    public void setTorchCone(boolean _isTorchCone) {
        this.isTorchCone = _isTorchCone;
    }

    public float getTorchDot() {
        return this.getScriptItem().torchDot;
    }

    public int getLightDistance() {
        return this.lightDistance;
    }

    public void setLightDistance(int _lightDistance) {
        this.lightDistance = _lightDistance;
    }

    public boolean canEmitLight() {
        if (this.getLightStrength() <= 0.0F) {
            return false;
        } else {
            Drainable drainable = Type.tryCastTo(this, Drainable.class);
            return drainable == null || !(drainable.getUsedDelta() <= 0.0F);
        }
    }

    public boolean isEmittingLight() {
        return !this.canEmitLight() ? false : !this.canBeActivated() || this.isActivated();
    }

    public boolean canStoreWater() {
        return this.CanStoreWater;
    }

    public float getFatigueChange() {
        return this.fatigueChange;
    }

    public void setFatigueChange(float _fatigueChange) {
        this.fatigueChange = _fatigueChange;
    }

    /**
     * Return the real condition of the weapon, based on this calcul :  Condition/ConditionMax * 100
     * @return float
     */
    public float getCurrentCondition() {
        Float float0 = (float)this.Condition / this.ConditionMax;
        return Float.valueOf(float0 * 100.0F);
    }

    public void setColor(Color color) {
        this.col = color;
    }

    public Color getColor() {
        return this.col;
    }

    public ColorInfo getColorInfo() {
        return new ColorInfo(this.col.getRedFloat(), this.col.getGreenFloat(), this.col.getBlueFloat(), this.col.getAlphaFloat());
    }

    public boolean isTwoHandWeapon() {
        return this.getScriptItem().TwoHandWeapon;
    }

    public String getCustomMenuOption() {
        return this.customMenuOption;
    }

    public void setCustomMenuOption(String _customMenuOption) {
        this.customMenuOption = _customMenuOption;
    }

    public void setTooltip(String _tooltip) {
        this.tooltip = _tooltip;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String getDisplayCategory() {
        return this.displayCategory;
    }

    public void setDisplayCategory(String _displayCategory) {
        this.displayCategory = _displayCategory;
    }

    public int getHaveBeenRepaired() {
        return this.haveBeenRepaired;
    }

    public void setHaveBeenRepaired(int _haveBeenRepaired) {
        this.haveBeenRepaired = _haveBeenRepaired;
    }

    public boolean isBroken() {
        return this.broken;
    }

    public void setBroken(boolean _broken) {
        this.broken = _broken;
    }

    public String getDisplayName() {
        return this.name;
    }

    public boolean isTrap() {
        return this.getScriptItem().Trap;
    }

    public void addExtraItem(String _type) {
        if (this.extraItems == null) {
            this.extraItems = new ArrayList<>();
        }

        this.extraItems.add(_type);
    }

    public boolean haveExtraItems() {
        return this.extraItems != null;
    }

    public ArrayList<String> getExtraItems() {
        return this.extraItems;
    }

    public float getExtraItemsWeight() {
        if (!this.haveExtraItems()) {
            return 0.0F;
        } else {
            float float0 = 0.0F;

            for (int int0 = 0; int0 < this.extraItems.size(); int0++) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(this.extraItems.get(int0));
                float0 += item1.getActualWeight();
            }

            return float0 * 0.6F;
        }
    }

    public boolean isCustomName() {
        return this.customName;
    }

    public void setCustomName(boolean _customName) {
        this.customName = _customName;
    }

    public boolean isFishingLure() {
        return this.getScriptItem().FishingLure;
    }

    public void copyConditionModData(InventoryItem other) {
        if (other.hasModData()) {
            KahluaTableIterator kahluaTableIterator = other.getModData().iterator();

            while (kahluaTableIterator.advance()) {
                if (kahluaTableIterator.getKey() instanceof String && ((String)kahluaTableIterator.getKey()).startsWith("condition:")) {
                    this.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
                }
            }
        }
    }

    public void setConditionFromModData(InventoryItem other) {
        if (other.hasModData()) {
            Object object = other.getModData().rawget("condition:" + this.getType());
            if (object != null && object instanceof Double) {
                this.setCondition((int)Math.round((Double)object * this.getConditionMax()));
            }
        }
    }

    public String getBreakSound() {
        return this.breakSound;
    }

    public void setBreakSound(String _breakSound) {
        this.breakSound = _breakSound;
    }

    public String getPlaceOneSound() {
        return this.getScriptItem().getPlaceOneSound();
    }

    public String getPlaceMultipleSound() {
        return this.getScriptItem().getPlaceMultipleSound();
    }

    public String getSoundByID(String ID) {
        return this.getScriptItem().getSoundByID(ID);
    }

    public void setBeingFilled(boolean v) {
        this.beingFilled = v;
    }

    public boolean isBeingFilled() {
        return this.beingFilled;
    }

    public String getFillFromDispenserSound() {
        return this.getScriptItem().getFillFromDispenserSound();
    }

    public String getFillFromTapSound() {
        return this.getScriptItem().getFillFromTapSound();
    }

    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    public void setAlcoholic(boolean _alcoholic) {
        this.alcoholic = _alcoholic;
    }

    public float getAlcoholPower() {
        return this.alcoholPower;
    }

    public void setAlcoholPower(float _alcoholPower) {
        this.alcoholPower = _alcoholPower;
    }

    public float getBandagePower() {
        return this.bandagePower;
    }

    public void setBandagePower(float _bandagePower) {
        this.bandagePower = _bandagePower;
    }

    public float getReduceInfectionPower() {
        return this.ReduceInfectionPower;
    }

    public void setReduceInfectionPower(float reduceInfectionPower) {
        this.ReduceInfectionPower = reduceInfectionPower;
    }

    public final void saveWithSize(ByteBuffer output, boolean net) throws IOException {
        int int0 = output.position();
        output.putInt(0);
        int int1 = output.position();
        this.save(output, net);
        int int2 = output.position();
        output.position(int0);
        output.putInt(int2 - int1);
        output.position(int2);
    }

    public boolean isCustomWeight() {
        return this.customWeight;
    }

    public void setCustomWeight(boolean custom) {
        this.customWeight = custom;
    }

    public float getContentsWeight() {
        if (!StringUtils.isNullOrEmpty(this.getAmmoType())) {
            Item item1 = ScriptManager.instance.FindItem(this.getAmmoType());
            if (item1 != null) {
                return item1.getActualWeight() * this.getCurrentAmmoCount();
            }
        }

        return 0.0F;
    }

    public float getHotbarEquippedWeight() {
        return (this.getActualWeight() + this.getContentsWeight()) * 0.7F;
    }

    public float getEquippedWeight() {
        return (this.getActualWeight() + this.getContentsWeight()) * 0.3F;
    }

    public float getUnequippedWeight() {
        return this.getActualWeight() + this.getContentsWeight();
    }

    public boolean isEquipped() {
        return this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter
            ? ((IsoGameCharacter)this.getContainer().getParent()).isEquipped(this)
            : false;
    }

    public int getKeyId() {
        return this.keyId;
    }

    public void setKeyId(int _keyId) {
        this.keyId = _keyId;
    }

    public boolean isTaintedWater() {
        return this.taintedWater;
    }

    public void setTaintedWater(boolean _taintedWater) {
        this.taintedWater = _taintedWater;
    }

    public boolean isRemoteController() {
        return this.remoteController;
    }

    public void setRemoteController(boolean _remoteController) {
        this.remoteController = _remoteController;
    }

    public boolean canBeRemote() {
        return this.canBeRemote;
    }

    public void setCanBeRemote(boolean _canBeRemote) {
        this.canBeRemote = _canBeRemote;
    }

    public int getRemoteControlID() {
        return this.remoteControlID;
    }

    public void setRemoteControlID(int _remoteControlID) {
        this.remoteControlID = _remoteControlID;
    }

    public int getRemoteRange() {
        return this.remoteRange;
    }

    public void setRemoteRange(int _remoteRange) {
        this.remoteRange = _remoteRange;
    }

    public String getExplosionSound() {
        return this.explosionSound;
    }

    public void setExplosionSound(String _explosionSound) {
        this.explosionSound = _explosionSound;
    }

    public String getCountDownSound() {
        return this.countDownSound;
    }

    public void setCountDownSound(String sound) {
        this.countDownSound = sound;
    }

    public float getColorRed() {
        return this.colorRed;
    }

    public void setColorRed(float _colorRed) {
        this.colorRed = _colorRed;
    }

    public float getColorGreen() {
        return this.colorGreen;
    }

    public void setColorGreen(float _colorGreen) {
        this.colorGreen = _colorGreen;
    }

    public float getColorBlue() {
        return this.colorBlue;
    }

    public void setColorBlue(float _colorBlue) {
        this.colorBlue = _colorBlue;
    }

    public String getEvolvedRecipeName() {
        return this.evolvedRecipeName;
    }

    public void setEvolvedRecipeName(String _evolvedRecipeName) {
        this.evolvedRecipeName = _evolvedRecipeName;
    }

    public float getMetalValue() {
        return this.metalValue;
    }

    public void setMetalValue(float _metalValue) {
        this.metalValue = _metalValue;
    }

    public float getItemHeat() {
        return this.itemHeat;
    }

    public void setItemHeat(float _itemHeat) {
        if (_itemHeat > 2.0F) {
            _itemHeat = 2.0F;
        }

        if (_itemHeat < 0.0F) {
            _itemHeat = 0.0F;
        }

        this.itemHeat = _itemHeat;
    }

    public float getInvHeat() {
        return 1.0F - this.itemHeat;
    }

    public float getMeltingTime() {
        return this.meltingTime;
    }

    public void setMeltingTime(float _meltingTime) {
        if (_meltingTime > 100.0F) {
            _meltingTime = 100.0F;
        }

        if (_meltingTime < 0.0F) {
            _meltingTime = 0.0F;
        }

        this.meltingTime = _meltingTime;
    }

    public String getWorker() {
        return this.worker;
    }

    public void setWorker(String _worker) {
        this.worker = _worker;
    }

    public int getID() {
        return this.id;
    }

    public void setID(int itemId) {
        this.id = itemId;
    }

    public boolean isWet() {
        return this.isWet;
    }

    public void setWet(boolean _isWet) {
        this.isWet = _isWet;
    }

    public float getWetCooldown() {
        return this.wetCooldown;
    }

    public void setWetCooldown(float _wetCooldown) {
        this.wetCooldown = _wetCooldown;
    }

    public String getItemWhenDry() {
        return this.itemWhenDry;
    }

    public void setItemWhenDry(String _itemWhenDry) {
        this.itemWhenDry = _itemWhenDry;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean _favorite) {
        this.favorite = _favorite;
    }

    public ArrayList<String> getRequireInHandOrInventory() {
        return this.requireInHandOrInventory;
    }

    public void setRequireInHandOrInventory(ArrayList<String> _requireInHandOrInventory) {
        this.requireInHandOrInventory = _requireInHandOrInventory;
    }

    public boolean isCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(boolean _customColor) {
        this.customColor = _customColor;
    }

    public void doBuildingStash() {
        if (this.stashMap != null) {
            if (GameClient.bClient) {
                GameClient.sendBuildingStashToDo(this.stashMap);
            } else {
                StashSystem.prepareBuildingStash(this.stashMap);
            }
        }
    }

    public void setStashMap(String _stashMap) {
        this.stashMap = _stashMap;
    }

    public int getMechanicType() {
        return this.getScriptItem().vehicleType;
    }

    public float getItemCapacity() {
        return this.itemCapacity;
    }

    public void setItemCapacity(float capacity) {
        this.itemCapacity = capacity;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(int _maxCapacity) {
        this.maxCapacity = _maxCapacity;
    }

    public boolean isConditionAffectsCapacity() {
        return this.ScriptItem != null && this.ScriptItem.isConditionAffectsCapacity();
    }

    public float getBrakeForce() {
        return this.brakeForce;
    }

    public void setBrakeForce(float _brakeForce) {
        this.brakeForce = _brakeForce;
    }

    public int getChanceToSpawnDamaged() {
        return this.chanceToSpawnDamaged;
    }

    public void setChanceToSpawnDamaged(int _chanceToSpawnDamaged) {
        this.chanceToSpawnDamaged = _chanceToSpawnDamaged;
    }

    public float getConditionLowerNormal() {
        return this.conditionLowerNormal;
    }

    public void setConditionLowerNormal(float _conditionLowerNormal) {
        this.conditionLowerNormal = _conditionLowerNormal;
    }

    public float getConditionLowerOffroad() {
        return this.conditionLowerOffroad;
    }

    public void setConditionLowerOffroad(float _conditionLowerOffroad) {
        this.conditionLowerOffroad = _conditionLowerOffroad;
    }

    public float getWheelFriction() {
        return this.wheelFriction;
    }

    public void setWheelFriction(float _wheelFriction) {
        this.wheelFriction = _wheelFriction;
    }

    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }

    public void setSuspensionDamping(float _suspensionDamping) {
        this.suspensionDamping = _suspensionDamping;
    }

    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }

    public void setSuspensionCompression(float _suspensionCompression) {
        this.suspensionCompression = _suspensionCompression;
    }

    public void setInfected(boolean infected) {
        this.zombieInfected = infected;
    }

    public boolean isInfected() {
        return this.zombieInfected;
    }

    public float getEngineLoudness() {
        return this.engineLoudness;
    }

    public void setEngineLoudness(float _engineLoudness) {
        this.engineLoudness = _engineLoudness;
    }

    public String getStaticModel() {
        return this.getScriptItem().getStaticModel();
    }

    public ArrayList<String> getIconsForTexture() {
        return this.iconsForTexture;
    }

    public void setIconsForTexture(ArrayList<String> _iconsForTexture) {
        this.iconsForTexture = _iconsForTexture;
    }

    public float getScore(SurvivorDesc desc) {
        return 0.0F;
    }

    /**
     * @return the previousOwner
     */
    public IsoGameCharacter getPreviousOwner() {
        return this.previousOwner;
    }

    /**
     * 
     * @param _previousOwner the previousOwner to set
     */
    public void setPreviousOwner(IsoGameCharacter _previousOwner) {
        this.previousOwner = _previousOwner;
    }

    /**
     * @return the ScriptItem
     */
    public Item getScriptItem() {
        return this.ScriptItem;
    }

    /**
     * 
     * @param _ScriptItem the ScriptItem to set
     */
    public void setScriptItem(Item _ScriptItem) {
        this.ScriptItem = _ScriptItem;
    }

    /**
     * @return the cat
     */
    public ItemType getCat() {
        return this.cat;
    }

    /**
     * 
     * @param _cat the cat to set
     */
    public void setCat(ItemType _cat) {
        this.cat = _cat;
    }

    /**
     * @return the container
     */
    public ItemContainer getContainer() {
        return this.container;
    }

    /**
     * 
     * @param _container the container to set
     */
    public void setContainer(ItemContainer _container) {
        this.container = _container;
    }

    public ArrayList<BloodClothingType> getBloodClothingType() {
        return this.bloodClothingType;
    }

    public void setBloodClothingType(ArrayList<BloodClothingType> _bloodClothingType) {
        this.bloodClothingType = _bloodClothingType;
    }

    public void setBlood(BloodBodyPartType bodyPartType, float amount) {
        ItemVisual itemVisual = this.getVisual();
        if (itemVisual != null) {
            itemVisual.setBlood(bodyPartType, amount);
        }
    }

    public float getBlood(BloodBodyPartType bodyPartType) {
        ItemVisual itemVisual = this.getVisual();
        return itemVisual != null ? itemVisual.getBlood(bodyPartType) : 0.0F;
    }

    public void setDirt(BloodBodyPartType bodyPartType, float amount) {
        ItemVisual itemVisual = this.getVisual();
        if (itemVisual != null) {
            itemVisual.setDirt(bodyPartType, amount);
        }
    }

    public float getDirt(BloodBodyPartType bodyPartType) {
        ItemVisual itemVisual = this.getVisual();
        return itemVisual != null ? itemVisual.getDirt(bodyPartType) : 0.0F;
    }

    public String getClothingItemName() {
        return this.getScriptItem().ClothingItem;
    }

    public int getStashChance() {
        return this.stashChance;
    }

    public void setStashChance(int _stashChance) {
        this.stashChance = _stashChance;
    }

    public String getEatType() {
        return this.getScriptItem().eatType;
    }

    public boolean isUseWorldItem() {
        return this.getScriptItem().UseWorldItem;
    }

    public boolean isHairDye() {
        return this.getScriptItem().hairDye;
    }

    public String getAmmoType() {
        return this.ammoType;
    }

    public void setAmmoType(String _ammoType) {
        this.ammoType = _ammoType;
    }

    public int getMaxAmmo() {
        return this.maxAmmo;
    }

    public void setMaxAmmo(int maxAmmoCount) {
        this.maxAmmo = maxAmmoCount;
    }

    public int getCurrentAmmoCount() {
        return this.currentAmmoCount;
    }

    public void setCurrentAmmoCount(int ammo) {
        this.currentAmmoCount = ammo;
    }

    public String getGunType() {
        return this.gunType;
    }

    public void setGunType(String _gunType) {
        this.gunType = _gunType;
    }

    public boolean hasBlood() {
        if (this instanceof Clothing) {
            if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
                return false;
            }

            ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());
            if (arrayList == null) {
                return false;
            }

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                if (this.getBlood((BloodBodyPartType)arrayList.get(int0)) > 0.0F) {
                    return true;
                }
            }
        } else {
            if (this instanceof HandWeapon) {
                return ((HandWeapon)this).getBloodLevel() > 0.0F;
            }

            if (this instanceof InventoryContainer) {
                return ((InventoryContainer)this).getBloodLevel() > 0.0F;
            }
        }

        return false;
    }

    public boolean hasDirt() {
        if (this instanceof Clothing) {
            if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
                return false;
            }

            ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());
            if (arrayList == null) {
                return false;
            }

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                if (this.getDirt((BloodBodyPartType)arrayList.get(int0)) > 0.0F) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getAttachmentType() {
        return this.attachmentType;
    }

    public void setAttachmentType(String _attachmentType) {
        this.attachmentType = _attachmentType;
    }

    public int getAttachedSlot() {
        return this.attachedSlot;
    }

    public void setAttachedSlot(int _attachedSlot) {
        this.attachedSlot = _attachedSlot;
    }

    public ArrayList<String> getAttachmentsProvided() {
        return this.attachmentsProvided;
    }

    public void setAttachmentsProvided(ArrayList<String> _attachmentsProvided) {
        this.attachmentsProvided = _attachmentsProvided;
    }

    public String getAttachedSlotType() {
        return this.attachedSlotType;
    }

    public void setAttachedSlotType(String _attachedSlotType) {
        this.attachedSlotType = _attachedSlotType;
    }

    public String getAttachmentReplacement() {
        return this.attachmentReplacement;
    }

    public void setAttachmentReplacement(String attachementReplacement) {
        this.attachmentReplacement = attachementReplacement;
    }

    public String getAttachedToModel() {
        return this.attachedToModel;
    }

    public void setAttachedToModel(String _attachedToModel) {
        this.attachedToModel = _attachedToModel;
    }

    public String getFabricType() {
        return this.getScriptItem().fabricType;
    }

    public String getStringItemType() {
        Item item0 = ScriptManager.instance.FindItem(this.getFullType());
        if (item0 != null && item0.getType() != null) {
            if (item0.getType() == Item.Type.Food) {
                return item0.CannedFood ? "CannedFood" : "Food";
            } else if ("Ammo".equals(item0.getDisplayCategory())) {
                return "Ammo";
            } else if (item0.getType() == Item.Type.Weapon && !item0.isRanged()) {
                return "MeleeWeapon";
            } else if (item0.getType() != Item.Type.WeaponPart
                && (item0.getType() != Item.Type.Weapon || !item0.isRanged())
                && (item0.getType() != Item.Type.Normal || StringUtils.isNullOrEmpty(item0.getAmmoType()))) {
                if (item0.getType() == Item.Type.Literature) {
                    return "Literature";
                } else if (item0.Medical) {
                    return "Medical";
                } else if (item0.SurvivalGear) {
                    return "SurvivalGear";
                } else {
                    return item0.MechanicsItem ? "Mechanic" : "Other";
                }
            } else {
                return "RangedWeapon";
            }
        } else {
            return "Other";
        }
    }

    public boolean isProtectFromRainWhileEquipped() {
        return this.getScriptItem().ProtectFromRainWhenEquipped;
    }

    public boolean isEquippedNoSprint() {
        return this.getScriptItem().equippedNoSprint;
    }

    public String getBodyLocation() {
        return this.getScriptItem().BodyLocation;
    }

    public String getMakeUpType() {
        return this.getScriptItem().makeUpType;
    }

    public boolean isHidden() {
        return this.getScriptItem().isHidden();
    }

    public String getConsolidateOption() {
        return this.getScriptItem().consolidateOption;
    }

    public ArrayList<String> getClothingItemExtra() {
        return this.getScriptItem().clothingItemExtra;
    }

    public ArrayList<String> getClothingItemExtraOption() {
        return this.getScriptItem().clothingItemExtraOption;
    }

    public String getWorldStaticItem() {
        return this.getModData().rawget("Flatpack") == "true" ? "Flatpack" : this.getScriptItem().worldStaticModel;
    }

    public void setRegistry_id(Item itemscript) {
        if (itemscript.getFullName().equals(this.getFullType())) {
            this.registry_id = itemscript.getRegistry_id();
        } else if (Core.bDebug) {
            WorldDictionary.DebugPrintItem(itemscript);
            throw new RuntimeException("These types should always match");
        }
    }

    public short getRegistry_id() {
        return this.registry_id;
    }

    public String getModID() {
        return this.ScriptItem != null && this.ScriptItem.getModID() != null ? this.ScriptItem.getModID() : WorldDictionary.getItemModID(this.registry_id);
    }

    public String getModName() {
        return WorldDictionary.getModNameFromID(this.getModID());
    }

    public boolean isVanilla() {
        if (this.getModID() != null) {
            return this.getModID().equals("pz-vanilla");
        } else if (Core.bDebug) {
            WorldDictionary.DebugPrintItem(this);
            throw new RuntimeException("Item has no modID?");
        } else {
            return true;
        }
    }

    public short getRecordedMediaIndex() {
        return this.recordedMediaIndex;
    }

    public void setRecordedMediaIndex(short _id) {
        this.recordedMediaIndex = _id;
        if (this.recordedMediaIndex >= 0) {
            MediaData mediaData = ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex);
            this.mediaType = -1;
            if (mediaData != null) {
                this.name = mediaData.getTranslatedItemDisplayName();
                this.mediaType = mediaData.getMediaType();
            } else {
                this.recordedMediaIndex = -1;
            }
        } else {
            this.mediaType = -1;
            this.name = this.getScriptItem().getDisplayName();
        }
    }

    public void setRecordedMediaIndexInteger(int _id) {
        this.setRecordedMediaIndex((short)_id);
    }

    public boolean isRecordedMedia() {
        return this.recordedMediaIndex >= 0;
    }

    public MediaData getMediaData() {
        return this.isRecordedMedia() ? ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex) : null;
    }

    public byte getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(byte b) {
        this.mediaType = b;
    }

    public void setRecordedMediaData(MediaData data) {
        if (data != null && data.getIndex() >= 0) {
            this.setRecordedMediaIndex(data.getIndex());
        }
    }

    public void setWorldZRotation(int rot) {
        this.worldZRotation = rot;
    }

    public void setWorldScale(float scale) {
        this.worldScale = scale;
    }

    public String getLuaCreate() {
        return this.getScriptItem().getLuaCreate();
    }

    public boolean isInitialised() {
        return this.isInitialised;
    }

    public void setInitialised(boolean initialised) {
        this.isInitialised = initialised;
    }

    public void initialiseItem() {
        this.setInitialised(true);
        if (this.getLuaCreate() != null) {
            Object object = LuaManager.getFunctionObject(this.getLuaCreate());
            if (object != null) {
                LuaManager.caller.protectedCallVoid(LuaManager.thread, object, this);
            }
        }
    }

    public String getSoundParameter(String parameterName) {
        return this.getScriptItem().getSoundParameter(parameterName);
    }
}
