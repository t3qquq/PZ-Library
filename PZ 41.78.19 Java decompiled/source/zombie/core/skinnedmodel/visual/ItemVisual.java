// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import zombie.GameWindow;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

public final class ItemVisual {
    private String m_fullType;
    private String m_clothingItemName;
    private String m_alternateModelName;
    public static final float NULL_HUE = Float.POSITIVE_INFINITY;
    public float m_Hue = Float.POSITIVE_INFINITY;
    public ImmutableColor m_Tint = null;
    public int m_BaseTexture = -1;
    public int m_TextureChoice = -1;
    public String m_Decal = null;
    private byte[] blood;
    private byte[] dirt;
    private byte[] holes;
    private byte[] basicPatches;
    private byte[] denimPatches;
    private byte[] leatherPatches;
    private InventoryItem inventoryItem = null;
    private static final int LASTSTAND_VERSION1 = 1;
    private static final int LASTSTAND_VERSION = 1;

    public ItemVisual() {
    }

    public ItemVisual(ItemVisual other) {
        this.copyFrom(other);
    }

    public void setItemType(String fullType) {
        Objects.requireNonNull(fullType);

        assert fullType.contains(".");

        this.m_fullType = fullType;
    }

    public String getItemType() {
        return this.m_fullType;
    }

    public void setAlternateModelName(String name) {
        this.m_alternateModelName = name;
    }

    public String getAlternateModelName() {
        return this.m_alternateModelName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ m_clothingItemName:\"" + this.m_clothingItemName + "\"}";
    }

    public String getClothingItemName() {
        return this.m_clothingItemName;
    }

    public void setClothingItemName(String name) {
        this.m_clothingItemName = name;
    }

    public Item getScriptItem() {
        return StringUtils.isNullOrWhitespace(this.m_fullType) ? null : ScriptManager.instance.getItem(this.m_fullType);
    }

    public ClothingItem getClothingItem() {
        Item item = this.getScriptItem();
        if (item == null) {
            return null;
        } else {
            if (!StringUtils.isNullOrWhitespace(this.m_alternateModelName)) {
                if ("LeftHand".equalsIgnoreCase(this.m_alternateModelName)) {
                    return item.replaceSecondHand.clothingItem;
                }

                if ("RightHand".equalsIgnoreCase(this.m_alternateModelName)) {
                    return item.replacePrimaryHand.clothingItem;
                }
            }

            return item.getClothingItemAsset();
        }
    }

    public void getClothingItemCombinedMask(CharacterMask in_out_mask) {
        ClothingItem.tryGetCombinedMask(this.getClothingItem(), in_out_mask);
    }

    public void setHue(float hue) {
        hue = Math.max(hue, -1.0F);
        hue = Math.min(hue, 1.0F);
        this.m_Hue = hue;
    }

    public float getHue(ClothingItem clothingItem) {
        if (clothingItem.m_AllowRandomHue) {
            if (this.m_Hue == Float.POSITIVE_INFINITY) {
                this.m_Hue = OutfitRNG.Next(200) / 100.0F - 1.0F;
            }

            return this.m_Hue;
        } else {
            return this.m_Hue = 0.0F;
        }
    }

    public void setTint(ImmutableColor tint) {
        this.m_Tint = tint;
    }

    public ImmutableColor getTint(ClothingItem clothingItem) {
        if (clothingItem.m_AllowRandomTint) {
            if (this.m_Tint == null) {
                this.m_Tint = OutfitRNG.randomImmutableColor();
            }

            return this.m_Tint;
        } else {
            return this.m_Tint = ImmutableColor.white;
        }
    }

    public ImmutableColor getTint() {
        return this.m_Tint;
    }

    public String getBaseTexture(ClothingItem clothingItem) {
        if (clothingItem.m_BaseTextures.isEmpty()) {
            this.m_BaseTexture = -1;
            return null;
        } else {
            if (this.m_BaseTexture < 0 || this.m_BaseTexture >= clothingItem.m_BaseTextures.size()) {
                this.m_BaseTexture = OutfitRNG.Next(clothingItem.m_BaseTextures.size());
            }

            return clothingItem.m_BaseTextures.get(this.m_BaseTexture);
        }
    }

    public String getTextureChoice(ClothingItem clothingItem) {
        if (clothingItem.textureChoices.isEmpty()) {
            this.m_TextureChoice = -1;
            return null;
        } else {
            if (this.m_TextureChoice < 0 || this.m_TextureChoice >= clothingItem.textureChoices.size()) {
                this.m_TextureChoice = OutfitRNG.Next(clothingItem.textureChoices.size());
            }

            return clothingItem.textureChoices.get(this.m_TextureChoice);
        }
    }

    public void setDecal(String decalName) {
        this.m_Decal = decalName;
    }

    public String getDecal(ClothingItem clothingItem) {
        if (StringUtils.isNullOrWhitespace(clothingItem.m_DecalGroup)) {
            return this.m_Decal = null;
        } else {
            if (this.m_Decal == null) {
                this.m_Decal = ClothingDecals.instance.getRandomDecal(clothingItem.m_DecalGroup);
            }

            return this.m_Decal;
        }
    }

    public void pickUninitializedValues(ClothingItem clothingItem) {
        if (clothingItem != null && clothingItem.isReady()) {
            this.getHue(clothingItem);
            this.getTint(clothingItem);
            this.getBaseTexture(clothingItem);
            this.getTextureChoice(clothingItem);
            this.getDecal(clothingItem);
        }
    }

    public void synchWithOutfit(ClothingItemReference itemRef) {
        ClothingItem clothingItem = itemRef.getClothingItem();
        this.m_clothingItemName = clothingItem.m_Name;
        this.m_Hue = itemRef.RandomData.m_Hue;
        this.m_Tint = itemRef.RandomData.m_Tint;
        this.m_BaseTexture = clothingItem.m_BaseTextures.indexOf(itemRef.RandomData.m_BaseTexture);
        this.m_TextureChoice = clothingItem.textureChoices.indexOf(itemRef.RandomData.m_TextureChoice);
        this.m_Decal = itemRef.RandomData.m_Decal;
    }

    public void clear() {
        this.m_fullType = null;
        this.m_clothingItemName = null;
        this.m_alternateModelName = null;
        this.m_Hue = Float.POSITIVE_INFINITY;
        this.m_Tint = null;
        this.m_BaseTexture = -1;
        this.m_TextureChoice = -1;
        this.m_Decal = null;
        if (this.blood != null) {
            Arrays.fill(this.blood, (byte)0);
        }

        if (this.dirt != null) {
            Arrays.fill(this.dirt, (byte)0);
        }

        if (this.holes != null) {
            Arrays.fill(this.holes, (byte)0);
        }

        if (this.basicPatches != null) {
            Arrays.fill(this.basicPatches, (byte)0);
        }

        if (this.denimPatches != null) {
            Arrays.fill(this.denimPatches, (byte)0);
        }

        if (this.leatherPatches != null) {
            Arrays.fill(this.leatherPatches, (byte)0);
        }
    }

    public void copyFrom(ItemVisual other) {
        if (other == null) {
            this.clear();
        } else {
            ClothingItem clothingItem = other.getClothingItem();
            if (clothingItem != null) {
                other.pickUninitializedValues(clothingItem);
            }

            this.m_fullType = other.m_fullType;
            this.m_clothingItemName = other.m_clothingItemName;
            this.m_alternateModelName = other.m_alternateModelName;
            this.m_Hue = other.m_Hue;
            this.m_Tint = other.m_Tint;
            this.m_BaseTexture = other.m_BaseTexture;
            this.m_TextureChoice = other.m_TextureChoice;
            this.m_Decal = other.m_Decal;
            this.copyBlood(other);
            this.copyHoles(other);
            this.copyPatches(other);
        }
    }

    public void save(ByteBuffer output) throws IOException {
        byte byte0 = 0;
        if (this.m_Tint != null) {
            byte0 = (byte)(byte0 | 1);
        }

        if (this.m_BaseTexture != -1) {
            byte0 = (byte)(byte0 | 2);
        }

        if (this.m_TextureChoice != -1) {
            byte0 = (byte)(byte0 | 4);
        }

        if (this.m_Hue != Float.POSITIVE_INFINITY) {
            byte0 = (byte)(byte0 | 8);
        }

        if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
            byte0 = (byte)(byte0 | 16);
        }

        output.put(byte0);
        GameWindow.WriteString(output, this.m_fullType);
        GameWindow.WriteString(output, this.m_alternateModelName);
        GameWindow.WriteString(output, this.m_clothingItemName);
        if (this.m_Tint != null) {
            output.put(this.m_Tint.getRedByte());
            output.put(this.m_Tint.getGreenByte());
            output.put(this.m_Tint.getBlueByte());
        }

        if (this.m_BaseTexture != -1) {
            output.put((byte)this.m_BaseTexture);
        }

        if (this.m_TextureChoice != -1) {
            output.put((byte)this.m_TextureChoice);
        }

        if (this.m_Hue != Float.POSITIVE_INFINITY) {
            output.putFloat(this.m_Hue);
        }

        if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
            GameWindow.WriteString(output, this.m_Decal);
        }

        if (this.blood != null) {
            output.put((byte)this.blood.length);

            for (int int0 = 0; int0 < this.blood.length; int0++) {
                output.put(this.blood[int0]);
            }
        } else {
            output.put((byte)0);
        }

        if (this.dirt != null) {
            output.put((byte)this.dirt.length);

            for (int int1 = 0; int1 < this.dirt.length; int1++) {
                output.put(this.dirt[int1]);
            }
        } else {
            output.put((byte)0);
        }

        if (this.holes != null) {
            output.put((byte)this.holes.length);

            for (int int2 = 0; int2 < this.holes.length; int2++) {
                output.put(this.holes[int2]);
            }
        } else {
            output.put((byte)0);
        }

        if (this.basicPatches != null) {
            output.put((byte)this.basicPatches.length);

            for (int int3 = 0; int3 < this.basicPatches.length; int3++) {
                output.put(this.basicPatches[int3]);
            }
        } else {
            output.put((byte)0);
        }

        if (this.denimPatches != null) {
            output.put((byte)this.denimPatches.length);

            for (int int4 = 0; int4 < this.denimPatches.length; int4++) {
                output.put(this.denimPatches[int4]);
            }
        } else {
            output.put((byte)0);
        }

        if (this.leatherPatches != null) {
            output.put((byte)this.leatherPatches.length);

            for (int int5 = 0; int5 < this.leatherPatches.length; int5++) {
                output.put(this.leatherPatches[int5]);
            }
        } else {
            output.put((byte)0);
        }
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        int int0 = input.get() & 255;
        if (WorldVersion >= 164) {
            this.m_fullType = GameWindow.ReadString(input);
            this.m_alternateModelName = GameWindow.ReadString(input);
        }

        this.m_clothingItemName = GameWindow.ReadString(input);
        if (WorldVersion < 164) {
            this.m_fullType = ScriptManager.instance.getItemTypeForClothingItem(this.m_clothingItemName);
        }

        if ((int0 & 1) != 0) {
            int int1 = input.get() & 255;
            int int2 = input.get() & 255;
            int int3 = input.get() & 255;
            this.m_Tint = new ImmutableColor(int1, int2, int3);
        }

        if ((int0 & 2) != 0) {
            this.m_BaseTexture = input.get();
        }

        if ((int0 & 4) != 0) {
            this.m_TextureChoice = input.get();
        }

        if (WorldVersion >= 146) {
            if ((int0 & 8) != 0) {
                this.m_Hue = input.getFloat();
            }

            if ((int0 & 16) != 0) {
                this.m_Decal = GameWindow.ReadString(input);
            }
        }

        byte byte0 = input.get();
        if (byte0 > 0 && this.blood == null) {
            this.blood = new byte[BloodBodyPartType.MAX.index()];
        }

        for (int int4 = 0; int4 < byte0; int4++) {
            byte byte1 = input.get();
            if (int4 < this.blood.length) {
                this.blood[int4] = byte1;
            }
        }

        if (WorldVersion >= 163) {
            byte0 = input.get();
            if (byte0 > 0 && this.dirt == null) {
                this.dirt = new byte[BloodBodyPartType.MAX.index()];
            }

            for (int int5 = 0; int5 < byte0; int5++) {
                byte byte2 = input.get();
                if (int5 < this.dirt.length) {
                    this.dirt[int5] = byte2;
                }
            }
        }

        byte0 = input.get();
        if (byte0 > 0 && this.holes == null) {
            this.holes = new byte[BloodBodyPartType.MAX.index()];
        }

        for (int int6 = 0; int6 < byte0; int6++) {
            byte byte3 = input.get();
            if (int6 < this.holes.length) {
                this.holes[int6] = byte3;
            }
        }

        if (WorldVersion >= 154) {
            byte0 = input.get();
            if (byte0 > 0 && this.basicPatches == null) {
                this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            for (int int7 = 0; int7 < byte0; int7++) {
                byte byte4 = input.get();
                if (int7 < this.basicPatches.length) {
                    this.basicPatches[int7] = byte4;
                }
            }
        }

        if (WorldVersion >= 155) {
            byte0 = input.get();
            if (byte0 > 0 && this.denimPatches == null) {
                this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            for (int int8 = 0; int8 < byte0; int8++) {
                byte byte5 = input.get();
                if (int8 < this.denimPatches.length) {
                    this.denimPatches[int8] = byte5;
                }
            }

            byte0 = input.get();
            if (byte0 > 0 && this.leatherPatches == null) {
                this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            for (int int9 = 0; int9 < byte0; int9++) {
                byte byte6 = input.get();
                if (int9 < this.leatherPatches.length) {
                    this.leatherPatches[int9] = byte6;
                }
            }
        }
    }

    public void setDenimPatch(BloodBodyPartType bodyPartType) {
        if (this.denimPatches == null) {
            this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
        }

        this.denimPatches[bodyPartType.index()] = -1;
    }

    public float getDenimPatch(BloodBodyPartType bodyPartType) {
        return this.denimPatches == null ? 0.0F : (this.denimPatches[bodyPartType.index()] & 255) / 255.0F;
    }

    public void setLeatherPatch(BloodBodyPartType bodyPartType) {
        if (this.leatherPatches == null) {
            this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
        }

        this.leatherPatches[bodyPartType.index()] = -1;
    }

    public float getLeatherPatch(BloodBodyPartType bodyPartType) {
        return this.leatherPatches == null ? 0.0F : (this.leatherPatches[bodyPartType.index()] & 255) / 255.0F;
    }

    public void setBasicPatch(BloodBodyPartType bodyPartType) {
        if (this.basicPatches == null) {
            this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
        }

        this.basicPatches[bodyPartType.index()] = -1;
    }

    public float getBasicPatch(BloodBodyPartType bodyPartType) {
        return this.basicPatches == null ? 0.0F : (this.basicPatches[bodyPartType.index()] & 255) / 255.0F;
    }

    public int getBasicPatchesNumber() {
        if (this.basicPatches == null) {
            return 0;
        } else {
            int int0 = 0;

            for (int int1 = 0; int1 < this.basicPatches.length; int1++) {
                if (this.basicPatches[int1] != 0) {
                    int0++;
                }
            }

            return int0;
        }
    }

    public void setHole(BloodBodyPartType bodyPartType) {
        if (this.holes == null) {
            this.holes = new byte[BloodBodyPartType.MAX.index()];
        }

        this.holes[bodyPartType.index()] = -1;
    }

    public float getHole(BloodBodyPartType bodyPartType) {
        return this.holes == null ? 0.0F : (this.holes[bodyPartType.index()] & 255) / 255.0F;
    }

    public int getHolesNumber() {
        if (this.holes == null) {
            return 0;
        } else {
            int int0 = 0;

            for (int int1 = 0; int1 < this.holes.length; int1++) {
                if (this.holes[int1] != 0) {
                    int0++;
                }
            }

            return int0;
        }
    }

    public void setBlood(BloodBodyPartType bodyPartType, float amount) {
        if (this.blood == null) {
            this.blood = new byte[BloodBodyPartType.MAX.index()];
        }

        amount = Math.max(0.0F, Math.min(1.0F, amount));
        this.blood[bodyPartType.index()] = (byte)(amount * 255.0F);
    }

    public float getBlood(BloodBodyPartType bodyPartType) {
        return this.blood == null ? 0.0F : (this.blood[bodyPartType.index()] & 255) / 255.0F;
    }

    public float getDirt(BloodBodyPartType bodyPartType) {
        return this.dirt == null ? 0.0F : (this.dirt[bodyPartType.index()] & 255) / 255.0F;
    }

    public void setDirt(BloodBodyPartType bodyPartType, float amount) {
        if (this.dirt == null) {
            this.dirt = new byte[BloodBodyPartType.MAX.index()];
        }

        amount = Math.max(0.0F, Math.min(1.0F, amount));
        this.dirt[bodyPartType.index()] = (byte)(amount * 255.0F);
    }

    public void copyBlood(ItemVisual other) {
        if (other.blood != null) {
            if (this.blood == null) {
                this.blood = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.blood, 0, this.blood, 0, this.blood.length);
        } else if (this.blood != null) {
            Arrays.fill(this.blood, (byte)0);
        }
    }

    public void copyDirt(ItemVisual other) {
        if (other.dirt != null) {
            if (this.dirt == null) {
                this.dirt = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.dirt, 0, this.dirt, 0, this.dirt.length);
        } else if (this.dirt != null) {
            Arrays.fill(this.dirt, (byte)0);
        }
    }

    public void copyHoles(ItemVisual other) {
        if (other.holes != null) {
            if (this.holes == null) {
                this.holes = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.holes, 0, this.holes, 0, this.holes.length);
        } else if (this.holes != null) {
            Arrays.fill(this.holes, (byte)0);
        }
    }

    public void copyPatches(ItemVisual other) {
        if (other.basicPatches != null) {
            if (this.basicPatches == null) {
                this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.basicPatches, 0, this.basicPatches, 0, this.basicPatches.length);
        } else if (this.basicPatches != null) {
            Arrays.fill(this.basicPatches, (byte)0);
        }

        if (other.denimPatches != null) {
            if (this.denimPatches == null) {
                this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.denimPatches, 0, this.denimPatches, 0, this.denimPatches.length);
        } else if (this.denimPatches != null) {
            Arrays.fill(this.denimPatches, (byte)0);
        }

        if (other.leatherPatches != null) {
            if (this.leatherPatches == null) {
                this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
            }

            System.arraycopy(other.leatherPatches, 0, this.leatherPatches, 0, this.leatherPatches.length);
        } else if (this.leatherPatches != null) {
            Arrays.fill(this.leatherPatches, (byte)0);
        }
    }

    public void removeHole(int bodyPartIndex) {
        if (this.holes != null) {
            this.holes[bodyPartIndex] = 0;
        }
    }

    public void removePatch(int bodyPartIndex) {
        if (this.basicPatches != null) {
            this.basicPatches[bodyPartIndex] = 0;
        }

        if (this.denimPatches != null) {
            this.denimPatches[bodyPartIndex] = 0;
        }

        if (this.leatherPatches != null) {
            this.leatherPatches[bodyPartIndex] = 0;
        }
    }

    public void removeBlood() {
        if (this.blood != null) {
            Arrays.fill(this.blood, (byte)0);
        }
    }

    public void removeDirt() {
        if (this.dirt != null) {
            Arrays.fill(this.dirt, (byte)0);
        }
    }

    public float getTotalBlood() {
        float float0 = 0.0F;
        if (this.blood != null) {
            for (int int0 = 0; int0 < this.blood.length; int0++) {
                float0 += (this.blood[int0] & 255) / 255.0F;
            }
        }

        return float0;
    }

    public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public void setInventoryItem(InventoryItem _inventoryItem) {
        this.inventoryItem = _inventoryItem;
    }

    public void setBaseTexture(int baseTexture) {
        this.m_BaseTexture = baseTexture;
    }

    public int getBaseTexture() {
        return this.m_BaseTexture;
    }

    public void setTextureChoice(int TextureChoice) {
        this.m_TextureChoice = TextureChoice;
    }

    public int getTextureChoice() {
        return this.m_TextureChoice;
    }

    private static StringBuilder toString(ImmutableColor immutableColor, StringBuilder stringBuilder) {
        stringBuilder.append(immutableColor.getRedByte() & 255);
        stringBuilder.append(",");
        stringBuilder.append(immutableColor.getGreenByte() & 255);
        stringBuilder.append(",");
        stringBuilder.append(immutableColor.getBlueByte() & 255);
        return stringBuilder;
    }

    private static ImmutableColor colorFromString(String string) {
        String[] strings = string.split(",");
        if (strings.length == 3) {
            try {
                int int0 = Integer.parseInt(strings[0]);
                int int1 = Integer.parseInt(strings[1]);
                int int2 = Integer.parseInt(strings[2]);
                return new ImmutableColor(int0 / 255.0F, int1 / 255.0F, int2 / 255.0F);
            } catch (NumberFormatException numberFormatException) {
            }
        }

        return null;
    }

    public String getLastStandString() {
        Item item = this.getScriptItem();
        if (item == null) {
            return null;
        } else {
            ClothingItem clothingItem = this.getClothingItem();
            if (clothingItem == null) {
                return null;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("version=");
                stringBuilder.append(1);
                stringBuilder.append(";");
                stringBuilder.append("type=");
                stringBuilder.append(this.inventoryItem.getFullType());
                stringBuilder.append(";");
                ImmutableColor immutableColor = this.getTint(clothingItem);
                stringBuilder.append("tint=");
                toString(immutableColor, stringBuilder);
                stringBuilder.append(";");
                int int0 = this.getBaseTexture();
                if (int0 != -1) {
                    stringBuilder.append("baseTexture=");
                    stringBuilder.append(int0);
                    stringBuilder.append(";");
                }

                int int1 = this.getTextureChoice();
                if (int1 != -1) {
                    stringBuilder.append("textureChoice=");
                    stringBuilder.append(int1);
                    stringBuilder.append(";");
                }

                float float0 = this.getHue(clothingItem);
                if (float0 != 0.0F) {
                    stringBuilder.append("hue=");
                    stringBuilder.append(float0);
                    stringBuilder.append(";");
                }

                String string = this.getDecal(clothingItem);
                if (!StringUtils.isNullOrWhitespace(string)) {
                    stringBuilder.append("decal=");
                    stringBuilder.append(string);
                    stringBuilder.append(";");
                }

                return stringBuilder.toString();
            }
        }
    }

    public static InventoryItem createLastStandItem(String saveStr) {
        saveStr = saveStr.trim();
        if (!StringUtils.isNullOrWhitespace(saveStr) && saveStr.startsWith("version=")) {
            InventoryItem item = null;
            ItemVisual itemVisual = null;
            int int0 = -1;
            String[] strings = saveStr.split(";");
            if (strings.length >= 2 && strings[1].trim().startsWith("type=")) {
                for (int int1 = 0; int1 < strings.length; int1++) {
                    int int2 = strings[int1].indexOf(61);
                    if (int2 != -1) {
                        String string0 = strings[int1].substring(0, int2).trim();
                        String string1 = strings[int1].substring(int2 + 1).trim();
                        switch (string0) {
                            case "version":
                                int0 = Integer.parseInt(string1);
                                if (int0 < 1 || int0 > 1) {
                                    return null;
                                }
                                break;
                            case "baseTexture":
                                try {
                                    itemVisual.setBaseTexture(Integer.parseInt(string1));
                                } catch (NumberFormatException numberFormatException2) {
                                }
                                break;
                            case "decal":
                                if (!StringUtils.isNullOrWhitespace(string1)) {
                                    itemVisual.setDecal(string1);
                                }
                                break;
                            case "hue":
                                try {
                                    itemVisual.setHue(Float.parseFloat(string1));
                                } catch (NumberFormatException numberFormatException1) {
                                }
                                break;
                            case "textureChoice":
                                try {
                                    itemVisual.setTextureChoice(Integer.parseInt(string1));
                                } catch (NumberFormatException numberFormatException0) {
                                }
                                break;
                            case "tint":
                                ImmutableColor immutableColor = colorFromString(string1);
                                if (immutableColor != null) {
                                    itemVisual.setTint(immutableColor);
                                }
                                break;
                            case "type":
                                item = InventoryItemFactory.CreateItem(string1);
                                if (item == null) {
                                    return null;
                                }

                                itemVisual = item.getVisual();
                                if (itemVisual == null) {
                                    return null;
                                }
                        }
                    }
                }

                return item;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
