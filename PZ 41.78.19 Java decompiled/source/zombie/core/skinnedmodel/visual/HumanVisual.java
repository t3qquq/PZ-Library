// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameWindow;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.HairOutfitDefinitions;
import zombie.characters.SurvivorDesc;
import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoWorld;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ModelScript;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class HumanVisual extends BaseVisual {
    private final IHumanVisual owner;
    private ImmutableColor skinColor = ImmutableColor.white;
    private int skinTexture = -1;
    private String skinTextureName = null;
    public int zombieRotStage = -1;
    private ImmutableColor hairColor;
    private ImmutableColor beardColor;
    private ImmutableColor naturalHairColor;
    private ImmutableColor naturalBeardColor;
    private String hairModel;
    private String beardModel;
    private int bodyHair = -1;
    private final byte[] blood = new byte[BloodBodyPartType.MAX.index()];
    private final byte[] dirt = new byte[BloodBodyPartType.MAX.index()];
    private final byte[] holes = new byte[BloodBodyPartType.MAX.index()];
    private final ItemVisuals bodyVisuals = new ItemVisuals();
    private Outfit outfit = null;
    private String nonAttachedHair = null;
    private Model forceModel = null;
    private String forceModelScript = null;
    private static final ArrayList<String> itemVisualLocations = new ArrayList<>();
    private static final int LASTSTAND_VERSION1 = 1;
    private static final int LASTSTAND_VERSION = 1;

    public HumanVisual(IHumanVisual _owner) {
        this.owner = _owner;
        Arrays.fill(this.blood, (byte)0);
        Arrays.fill(this.dirt, (byte)0);
        Arrays.fill(this.holes, (byte)0);
    }

    public boolean isFemale() {
        return this.owner.isFemale();
    }

    public boolean isZombie() {
        return this.owner.isZombie();
    }

    public boolean isSkeleton() {
        return this.owner.isSkeleton();
    }

    public void setSkinColor(ImmutableColor color) {
        this.skinColor = color;
    }

    public ImmutableColor getSkinColor() {
        if (this.skinColor == null) {
            this.skinColor = new ImmutableColor(SurvivorDesc.getRandomSkinColor());
        }

        return this.skinColor;
    }

    public void setBodyHairIndex(int index) {
        this.bodyHair = index;
    }

    public int getBodyHairIndex() {
        return this.bodyHair;
    }

    public void setSkinTextureIndex(int index) {
        this.skinTexture = index;
    }

    public int getSkinTextureIndex() {
        return this.skinTexture;
    }

    public void setSkinTextureName(String textureName) {
        this.skinTextureName = textureName;
    }

    public float lerp(float start, float __end__, float delta) {
        if (delta < 0.0F) {
            delta = 0.0F;
        }

        if (delta >= 1.0F) {
            delta = 1.0F;
        }

        float float0 = __end__ - start;
        float float1 = float0 * delta;
        return start + float1;
    }

    public int pickRandomZombieRotStage() {
        int int0 = Math.max((int)IsoWorld.instance.getWorldAgeDays(), 0);
        float float0 = 20.0F;
        float float1 = 90.0F;
        float float2 = 100.0F;
        float float3 = 20.0F;
        float float4 = 10.0F;
        float float5 = 30.0F;
        if (int0 >= 180) {
            float3 = 0.0F;
            float5 = 10.0F;
        }

        float float6 = int0 - float0;
        float float7 = float6 / (float1 - float0);
        float float8 = this.lerp(float2, float3, float7);
        float float9 = this.lerp(float4, float5, float7);
        float float10 = OutfitRNG.Next(100);
        if (float10 < float8) {
            return 1;
        } else {
            return float10 < float9 + float8 ? 2 : 3;
        }
    }

    public String getSkinTexture() {
        if (this.skinTextureName != null) {
            return this.skinTextureName;
        } else {
            String string = "";
            ArrayList arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins : PopTemplateManager.instance.m_MaleSkins;
            if (this.owner.isZombie() && this.owner.isSkeleton()) {
                if (this.owner.isFemale()) {
                    arrayList = PopTemplateManager.instance.m_SkeletonFemaleSkins_Zombie;
                } else {
                    arrayList = PopTemplateManager.instance.m_SkeletonMaleSkins_Zombie;
                }
            } else if (this.owner.isZombie()) {
                if (this.zombieRotStage < 1 || this.zombieRotStage > 3) {
                    this.zombieRotStage = this.pickRandomZombieRotStage();
                }

                switch (this.zombieRotStage) {
                    case 1:
                        arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie1 : PopTemplateManager.instance.m_MaleSkins_Zombie1;
                        break;
                    case 2:
                        arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie2 : PopTemplateManager.instance.m_MaleSkins_Zombie2;
                        break;
                    case 3:
                        arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie3 : PopTemplateManager.instance.m_MaleSkins_Zombie3;
                }
            } else if (!this.owner.isFemale()) {
                string = !this.owner.isZombie() && this.bodyHair >= 0 ? "a" : "";
            }

            if (this.skinTexture == arrayList.size()) {
                this.skinTexture--;
            } else if (this.skinTexture < 0 || this.skinTexture > arrayList.size()) {
                this.skinTexture = OutfitRNG.Next(arrayList.size());
            }

            return (String)arrayList.get(this.skinTexture) + string;
        }
    }

    public void setHairColor(ImmutableColor color) {
        this.hairColor = color;
    }

    public ImmutableColor getHairColor() {
        if (this.hairColor == null) {
            this.hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor(this.outfit != null ? this.outfit.m_Name : null);
        }

        return this.hairColor;
    }

    public void setBeardColor(ImmutableColor color) {
        this.beardColor = color;
    }

    public ImmutableColor getBeardColor() {
        if (this.beardColor == null) {
            this.beardColor = this.getHairColor();
        }

        return this.beardColor;
    }

    public void setNaturalHairColor(ImmutableColor color) {
        this.naturalHairColor = color;
    }

    public ImmutableColor getNaturalHairColor() {
        if (this.naturalHairColor == null) {
            this.naturalHairColor = this.getHairColor();
        }

        return this.naturalHairColor;
    }

    public void setNaturalBeardColor(ImmutableColor color) {
        this.naturalBeardColor = color;
    }

    public ImmutableColor getNaturalBeardColor() {
        if (this.naturalBeardColor == null) {
            this.naturalBeardColor = this.getNaturalHairColor();
        }

        return this.naturalBeardColor;
    }

    public void setHairModel(String model) {
        this.hairModel = model;
    }

    public String getHairModel() {
        if (this.owner.isFemale()) {
            if (HairStyles.instance.FindFemaleStyle(this.hairModel) == null) {
                this.hairModel = HairStyles.instance.getRandomFemaleStyle(this.outfit != null ? this.outfit.m_Name : null);
            }
        } else if (HairStyles.instance.FindMaleStyle(this.hairModel) == null) {
            this.hairModel = HairStyles.instance.getRandomMaleStyle(this.outfit != null ? this.outfit.m_Name : null);
        }

        return this.hairModel;
    }

    public void setBeardModel(String model) {
        this.beardModel = model;
    }

    public String getBeardModel() {
        if (this.owner.isFemale()) {
            this.beardModel = null;
        } else if (BeardStyles.instance.FindStyle(this.beardModel) == null) {
            this.beardModel = BeardStyles.instance.getRandomStyle(this.outfit != null ? this.outfit.m_Name : null);
        }

        return this.beardModel;
    }

    public void setBlood(BloodBodyPartType bodyPartType, float amount) {
        amount = Math.max(0.0F, Math.min(1.0F, amount));
        this.blood[bodyPartType.index()] = (byte)(amount * 255.0F);
    }

    public float getBlood(BloodBodyPartType bodyPartType) {
        return (this.blood[bodyPartType.index()] & 255) / 255.0F;
    }

    public void setDirt(BloodBodyPartType bodyPartType, float amount) {
        amount = Math.max(0.0F, Math.min(1.0F, amount));
        this.dirt[bodyPartType.index()] = (byte)(amount * 255.0F);
    }

    public float getDirt(BloodBodyPartType bodyPartType) {
        return (this.dirt[bodyPartType.index()] & 255) / 255.0F;
    }

    public void setHole(BloodBodyPartType bodyPartType) {
        this.holes[bodyPartType.index()] = -1;
    }

    public float getHole(BloodBodyPartType bodyPartType) {
        return (this.holes[bodyPartType.index()] & 255) / 255.0F;
    }

    public void removeBlood() {
        Arrays.fill(this.blood, (byte)0);
    }

    public void removeDirt() {
        Arrays.fill(this.dirt, (byte)0);
    }

    public void randomBlood() {
        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            this.setBlood(BloodBodyPartType.FromIndex(int0), OutfitRNG.Next(0.0F, 1.0F));
        }
    }

    public void randomDirt() {
        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            this.setDirt(BloodBodyPartType.FromIndex(int0), OutfitRNG.Next(0.0F, 1.0F));
        }
    }

    public float getTotalBlood() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.blood.length; int0++) {
            float0 += (this.blood[int0] & 255) / 255.0F;
        }

        return float0;
    }

    @Override
    public void clear() {
        this.skinColor = ImmutableColor.white;
        this.skinTexture = -1;
        this.skinTextureName = null;
        this.zombieRotStage = -1;
        this.hairColor = null;
        this.beardColor = null;
        this.naturalHairColor = null;
        this.naturalBeardColor = null;
        this.hairModel = null;
        this.nonAttachedHair = null;
        this.beardModel = null;
        this.bodyHair = -1;
        Arrays.fill(this.blood, (byte)0);
        Arrays.fill(this.dirt, (byte)0);
        Arrays.fill(this.holes, (byte)0);
        this.bodyVisuals.clear();
        this.forceModel = null;
        this.forceModelScript = null;
    }

    @Override
    public void copyFrom(BaseVisual other_) {
        if (other_ == null) {
            this.clear();
        } else {
            HumanVisual humanVisual = Type.tryCastTo(other_, HumanVisual.class);
            if (humanVisual == null) {
                throw new IllegalArgumentException("expected HumanVisual, got " + other_);
            } else {
                humanVisual.getHairColor();
                humanVisual.getNaturalHairColor();
                humanVisual.getNaturalBeardColor();
                humanVisual.getHairModel();
                humanVisual.getBeardModel();
                humanVisual.getSkinTexture();
                this.skinColor = humanVisual.skinColor;
                this.skinTexture = humanVisual.skinTexture;
                this.skinTextureName = humanVisual.skinTextureName;
                this.zombieRotStage = humanVisual.zombieRotStage;
                this.hairColor = humanVisual.hairColor;
                this.beardColor = humanVisual.beardColor;
                this.naturalHairColor = humanVisual.naturalHairColor;
                this.naturalBeardColor = humanVisual.naturalBeardColor;
                this.hairModel = humanVisual.hairModel;
                this.nonAttachedHair = humanVisual.nonAttachedHair;
                this.beardModel = humanVisual.beardModel;
                this.bodyHair = humanVisual.bodyHair;
                this.outfit = humanVisual.outfit;
                System.arraycopy(humanVisual.blood, 0, this.blood, 0, this.blood.length);
                System.arraycopy(humanVisual.dirt, 0, this.dirt, 0, this.dirt.length);
                System.arraycopy(humanVisual.holes, 0, this.holes, 0, this.holes.length);
                this.bodyVisuals.clear();
                this.bodyVisuals.addAll(humanVisual.bodyVisuals);
                this.forceModel = humanVisual.forceModel;
                this.forceModelScript = humanVisual.forceModelScript;
            }
        }
    }

    @Override
    public void save(ByteBuffer output) throws IOException {
        byte byte0 = 0;
        if (this.hairColor != null) {
            byte0 = (byte)(byte0 | 4);
        }

        if (this.beardColor != null) {
            byte0 = (byte)(byte0 | 2);
        }

        if (this.skinColor != null) {
            byte0 = (byte)(byte0 | 8);
        }

        if (this.beardModel != null) {
            byte0 = (byte)(byte0 | 16);
        }

        if (this.hairModel != null) {
            byte0 = (byte)(byte0 | 32);
        }

        if (this.skinTextureName != null) {
            byte0 = (byte)(byte0 | 64);
        }

        output.put(byte0);
        if (this.hairColor != null) {
            output.put(this.hairColor.getRedByte());
            output.put(this.hairColor.getGreenByte());
            output.put(this.hairColor.getBlueByte());
        }

        if (this.beardColor != null) {
            output.put(this.beardColor.getRedByte());
            output.put(this.beardColor.getGreenByte());
            output.put(this.beardColor.getBlueByte());
        }

        if (this.skinColor != null) {
            output.put(this.skinColor.getRedByte());
            output.put(this.skinColor.getGreenByte());
            output.put(this.skinColor.getBlueByte());
        }

        output.put((byte)this.bodyHair);
        output.put((byte)this.skinTexture);
        output.put((byte)this.zombieRotStage);
        if (this.skinTextureName != null) {
            GameWindow.WriteString(output, this.skinTextureName);
        }

        if (this.beardModel != null) {
            GameWindow.WriteString(output, this.beardModel);
        }

        if (this.hairModel != null) {
            GameWindow.WriteString(output, this.hairModel);
        }

        output.put((byte)this.blood.length);

        for (int int0 = 0; int0 < this.blood.length; int0++) {
            output.put(this.blood[int0]);
        }

        output.put((byte)this.dirt.length);

        for (int int1 = 0; int1 < this.dirt.length; int1++) {
            output.put(this.dirt[int1]);
        }

        output.put((byte)this.holes.length);

        for (int int2 = 0; int2 < this.holes.length; int2++) {
            output.put(this.holes[int2]);
        }

        output.put((byte)this.bodyVisuals.size());

        for (int int3 = 0; int3 < this.bodyVisuals.size(); int3++) {
            ItemVisual itemVisual = this.bodyVisuals.get(int3);
            itemVisual.save(output);
        }

        GameWindow.WriteString(output, this.getNonAttachedHair());
        byte byte1 = 0;
        if (this.naturalHairColor != null) {
            byte1 = (byte)(byte1 | 4);
        }

        if (this.naturalBeardColor != null) {
            byte1 = (byte)(byte1 | 2);
        }

        output.put(byte1);
        if (this.naturalHairColor != null) {
            output.put(this.naturalHairColor.getRedByte());
            output.put(this.naturalHairColor.getGreenByte());
            output.put(this.naturalHairColor.getBlueByte());
        }

        if (this.naturalBeardColor != null) {
            output.put(this.naturalBeardColor.getRedByte());
            output.put(this.naturalBeardColor.getGreenByte());
            output.put(this.naturalBeardColor.getBlueByte());
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.clear();
        int int0 = input.get() & 255;
        if ((int0 & 4) != 0) {
            int int1 = input.get() & 255;
            int int2 = input.get() & 255;
            int int3 = input.get() & 255;
            this.hairColor = new ImmutableColor(int1, int2, int3);
        }

        if ((int0 & 2) != 0) {
            int int4 = input.get() & 255;
            int int5 = input.get() & 255;
            int int6 = input.get() & 255;
            this.beardColor = new ImmutableColor(int4, int5, int6);
        }

        if ((int0 & 8) != 0) {
            int int7 = input.get() & 255;
            int int8 = input.get() & 255;
            int int9 = input.get() & 255;
            this.skinColor = new ImmutableColor(int7, int8, int9);
        }

        this.bodyHair = input.get();
        this.skinTexture = input.get();
        if (WorldVersion >= 156) {
            this.zombieRotStage = input.get();
        }

        if ((int0 & 64) != 0) {
            this.skinTextureName = GameWindow.ReadString(input);
        }

        if ((int0 & 16) != 0) {
            this.beardModel = GameWindow.ReadString(input);
        }

        if ((int0 & 32) != 0) {
            this.hairModel = GameWindow.ReadString(input);
        }

        byte byte0 = input.get();

        for (int int10 = 0; int10 < byte0; int10++) {
            byte byte1 = input.get();
            if (int10 < this.blood.length) {
                this.blood[int10] = byte1;
            }
        }

        if (WorldVersion >= 163) {
            byte0 = input.get();

            for (int int11 = 0; int11 < byte0; int11++) {
                byte byte2 = input.get();
                if (int11 < this.dirt.length) {
                    this.dirt[int11] = byte2;
                }
            }
        }

        byte0 = input.get();

        for (int int12 = 0; int12 < byte0; int12++) {
            byte byte3 = input.get();
            if (int12 < this.holes.length) {
                this.holes[int12] = byte3;
            }
        }

        byte0 = input.get();

        for (int int13 = 0; int13 < byte0; int13++) {
            ItemVisual itemVisual = new ItemVisual();
            itemVisual.load(input, WorldVersion);
            this.bodyVisuals.add(itemVisual);
        }

        this.setNonAttachedHair(GameWindow.ReadString(input));
        if (WorldVersion >= 187) {
            int int14 = input.get() & 255;
            if ((int14 & 4) != 0) {
                int int15 = input.get() & 255;
                int int16 = input.get() & 255;
                int int17 = input.get() & 255;
                this.naturalHairColor = new ImmutableColor(int15, int16, int17);
            }

            if ((int14 & 2) != 0) {
                int int18 = input.get() & 255;
                int int19 = input.get() & 255;
                int int20 = input.get() & 255;
                this.naturalBeardColor = new ImmutableColor(int18, int19, int20);
            }
        }
    }

    @Override
    public Model getModel() {
        if (this.forceModel != null) {
            return this.forceModel;
        } else if (this.isSkeleton()) {
            return this.isFemale() ? ModelManager.instance.m_skeletonFemaleModel : ModelManager.instance.m_skeletonMaleModel;
        } else {
            return this.isFemale() ? ModelManager.instance.m_femaleModel : ModelManager.instance.m_maleModel;
        }
    }

    @Override
    public ModelScript getModelScript() {
        return this.forceModelScript != null
            ? ScriptManager.instance.getModelScript(this.forceModelScript)
            : ScriptManager.instance.getModelScript(this.isFemale() ? "FemaleBody" : "MaleBody");
    }

    public static CharacterMask GetMask(ItemVisuals itemVisuals) {
        CharacterMask characterMask = new CharacterMask();

        for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
            itemVisuals.get(int0).getClothingItemCombinedMask(characterMask);
        }

        return characterMask;
    }

    public void synchWithOutfit(Outfit _outfit) {
        if (_outfit != null) {
            this.hairColor = _outfit.RandomData.m_hairColor;
            this.beardColor = this.hairColor;
            this.hairModel = this.owner.isFemale() ? _outfit.RandomData.m_femaleHairName : _outfit.RandomData.m_maleHairName;
            this.beardModel = this.owner.isFemale() ? null : _outfit.RandomData.m_beardName;
            this.getSkinTexture();
        }
    }

    @Override
    public void dressInNamedOutfit(String outfitName, ItemVisuals itemVisuals) {
        itemVisuals.clear();
        if (!StringUtils.isNullOrWhitespace(outfitName)) {
            Outfit outfit0 = this.owner.isFemale() ? OutfitManager.instance.FindFemaleOutfit(outfitName) : OutfitManager.instance.FindMaleOutfit(outfitName);
            if (outfit0 != null) {
                Outfit outfit1 = outfit0.clone();
                outfit1.Randomize();
                this.dressInOutfit(outfit1, itemVisuals);
            }
        }
    }

    public void dressInClothingItem(String itemGUID, ItemVisuals itemVisuals) {
        this.dressInClothingItem(itemGUID, itemVisuals, true);
    }

    public void dressInClothingItem(String itemGUID, ItemVisuals itemVisuals, boolean clearCurrentVisuals) {
        if (clearCurrentVisuals) {
            this.clear();
            itemVisuals.clear();
        }

        ClothingItem clothingItem = OutfitManager.instance.getClothingItem(itemGUID);
        if (clothingItem != null) {
            Outfit outfitx = new Outfit();
            ClothingItemReference clothingItemReference = new ClothingItemReference();
            clothingItemReference.itemGUID = itemGUID;
            outfitx.m_items.add(clothingItemReference);
            outfitx.m_Pants = false;
            outfitx.m_Top = false;
            outfitx.Randomize();
            this.dressInOutfit(outfitx, itemVisuals);
        }
    }

    private void dressInOutfit(Outfit outfitx, ItemVisuals itemVisuals) {
        this.setOutfit(outfitx);
        this.getItemVisualLocations(itemVisuals, itemVisualLocations);
        if (outfitx.m_Pants) {
            String string0 = outfitx.m_AllowPantsHue
                ? DefaultClothing.instance.pickPantsHue()
                : (outfitx.m_AllowPantsTint ? DefaultClothing.instance.pickPantsTint() : DefaultClothing.instance.pickPantsTexture());
            this.addClothingItem(itemVisuals, itemVisualLocations, string0, null);
        }

        if (outfitx.m_Top && outfitx.RandomData.m_hasTop) {
            String string1;
            if (outfitx.RandomData.m_hasTShirt) {
                if (outfitx.RandomData.m_hasTShirtDecal && outfitx.GetMask().isTorsoVisible() && outfitx.m_AllowTShirtDecal) {
                    string1 = outfitx.m_AllowTopTint ? DefaultClothing.instance.pickTShirtDecalTint() : DefaultClothing.instance.pickTShirtDecalTexture();
                } else {
                    string1 = outfitx.m_AllowTopTint ? DefaultClothing.instance.pickTShirtTint() : DefaultClothing.instance.pickTShirtTexture();
                }
            } else {
                string1 = outfitx.m_AllowTopTint ? DefaultClothing.instance.pickVestTint() : DefaultClothing.instance.pickVestTexture();
            }

            this.addClothingItem(itemVisuals, itemVisualLocations, string1, null);
        }

        for (int int0 = 0; int0 < outfitx.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference = outfitx.m_items.get(int0);
            ClothingItem clothingItem = clothingItemReference.getClothingItem();
            if (clothingItem != null && clothingItem.isReady()) {
                this.addClothingItem(itemVisuals, itemVisualLocations, clothingItem.m_Name, clothingItemReference);
            }
        }

        outfitx.m_Pants = false;
        outfitx.m_Top = false;
        outfitx.RandomData.m_topTexture = null;
        outfitx.RandomData.m_pantsTexture = null;
    }

    public ItemVisuals getBodyVisuals() {
        return this.bodyVisuals;
    }

    public ItemVisual addBodyVisual(String clothingItemName) {
        return this.addBodyVisualFromClothingItemName(clothingItemName);
    }

    public ItemVisual addBodyVisualFromItemType(String itemType) {
        Item item = ScriptManager.instance.getItem(itemType);
        return item != null && !StringUtils.isNullOrWhitespace(item.getClothingItem()) ? this.addBodyVisualFromClothingItemName(item.getClothingItem()) : null;
    }

    public ItemVisual addBodyVisualFromClothingItemName(String clothingItemName) {
        if (StringUtils.isNullOrWhitespace(clothingItemName)) {
            return null;
        } else {
            Item item = ScriptManager.instance.getItemForClothingItem(clothingItemName);
            if (item == null) {
                return null;
            } else {
                ClothingItem clothingItem = item.getClothingItemAsset();
                if (clothingItem == null) {
                    return null;
                } else {
                    for (int int0 = 0; int0 < this.bodyVisuals.size(); int0++) {
                        if (this.bodyVisuals.get(int0).getClothingItemName().equals(clothingItemName)) {
                            return null;
                        }
                    }

                    ClothingItemReference clothingItemReference = new ClothingItemReference();
                    clothingItemReference.itemGUID = clothingItem.m_GUID;
                    clothingItemReference.randomize();
                    ItemVisual itemVisual = new ItemVisual();
                    itemVisual.setItemType(item.getFullName());
                    itemVisual.synchWithOutfit(clothingItemReference);
                    this.bodyVisuals.add(itemVisual);
                    return itemVisual;
                }
            }
        }
    }

    public ItemVisual removeBodyVisualFromItemType(String itemType) {
        for (int int0 = 0; int0 < this.bodyVisuals.size(); int0++) {
            ItemVisual itemVisual = this.bodyVisuals.get(int0);
            if (itemVisual.getItemType().equals(itemType)) {
                this.bodyVisuals.remove(int0);
                return itemVisual;
            }
        }

        return null;
    }

    public boolean hasBodyVisualFromItemType(String itemType) {
        for (int int0 = 0; int0 < this.bodyVisuals.size(); int0++) {
            ItemVisual itemVisual = this.bodyVisuals.get(int0);
            if (itemVisual.getItemType().equals(itemType)) {
                return true;
            }
        }

        return false;
    }

    private void getItemVisualLocations(ItemVisuals itemVisuals, ArrayList<String> arrayList) {
        arrayList.clear();

        for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
            ItemVisual itemVisual = itemVisuals.get(int0);
            Item item = itemVisual.getScriptItem();
            if (item == null) {
                arrayList.add(null);
            } else {
                String string = item.getBodyLocation();
                if (StringUtils.isNullOrWhitespace(string)) {
                    string = item.CanBeEquipped;
                }

                arrayList.add(string);
            }
        }
    }

    public ItemVisual addClothingItem(ItemVisuals itemVisuals, Item scriptItem) {
        if (scriptItem == null) {
            return null;
        } else {
            ClothingItem clothingItem = scriptItem.getClothingItemAsset();
            if (clothingItem == null) {
                return null;
            } else if (!clothingItem.isReady()) {
                return null;
            } else {
                this.getItemVisualLocations(itemVisuals, itemVisualLocations);
                return this.addClothingItem(itemVisuals, itemVisualLocations, clothingItem.m_Name, null);
            }
        }
    }

    private ItemVisual addClothingItem(ItemVisuals itemVisuals, ArrayList<String> arrayList, String string0, ClothingItemReference clothingItemReference) {
        assert itemVisuals.size() == arrayList.size();

        if (clothingItemReference != null && !clothingItemReference.RandomData.m_Active) {
            return null;
        } else if (StringUtils.isNullOrWhitespace(string0)) {
            return null;
        } else {
            Item item = ScriptManager.instance.getItemForClothingItem(string0);
            if (item == null) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.warn("Could not find item type for %s", string0);
                }

                return null;
            } else {
                ClothingItem clothingItem = item.getClothingItemAsset();
                if (clothingItem == null) {
                    return null;
                } else if (!clothingItem.isReady()) {
                    return null;
                } else {
                    String string1 = item.getBodyLocation();
                    if (StringUtils.isNullOrWhitespace(string1)) {
                        string1 = item.CanBeEquipped;
                    }

                    if (StringUtils.isNullOrWhitespace(string1)) {
                        return null;
                    } else {
                        if (clothingItemReference == null) {
                            clothingItemReference = new ClothingItemReference();
                            clothingItemReference.itemGUID = clothingItem.m_GUID;
                            clothingItemReference.randomize();
                        }

                        if (!clothingItemReference.RandomData.m_Active) {
                            return null;
                        } else {
                            BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
                            BodyLocation bodyLocation = bodyLocationGroup.getLocation(string1);
                            if (bodyLocation == null) {
                                DebugLog.General.error("The game can't found location '" + string1 + "' for the item '" + item.name + "'");
                                return null;
                            } else {
                                if (!bodyLocation.isMultiItem()) {
                                    int int0 = arrayList.indexOf(string1);
                                    if (int0 != -1) {
                                        itemVisuals.remove(int0);
                                        arrayList.remove(int0);
                                    }
                                }

                                for (int int1 = 0; int1 < itemVisuals.size(); int1++) {
                                    if (bodyLocationGroup.isExclusive(string1, (String)arrayList.get(int1))) {
                                        itemVisuals.remove(int1);
                                        arrayList.remove(int1);
                                        int1--;
                                    }
                                }

                                assert itemVisuals.size() == arrayList.size();

                                int int2 = bodyLocationGroup.indexOf(string1);
                                int int3 = itemVisuals.size();

                                for (int int4 = 0; int4 < itemVisuals.size(); int4++) {
                                    if (bodyLocationGroup.indexOf((String)arrayList.get(int4)) > int2) {
                                        int3 = int4;
                                        break;
                                    }
                                }

                                ItemVisual itemVisual = new ItemVisual();
                                itemVisual.setItemType(item.getFullName());
                                itemVisual.synchWithOutfit(clothingItemReference);
                                itemVisuals.add(int3, itemVisual);
                                arrayList.add(int3, string1);
                                return itemVisual;
                            }
                        }
                    }
                }
            }
        }
    }

    public Outfit getOutfit() {
        return this.outfit;
    }

    public void setOutfit(Outfit _outfit) {
        this.outfit = _outfit;
    }

    public String getNonAttachedHair() {
        return this.nonAttachedHair;
    }

    public void setNonAttachedHair(String _nonAttachedHair) {
        if (StringUtils.isNullOrWhitespace(_nonAttachedHair)) {
            _nonAttachedHair = null;
        }

        this.nonAttachedHair = _nonAttachedHair;
    }

    public void setForceModel(Model model) {
        this.forceModel = model;
    }

    public void setForceModelScript(String modelScript) {
        this.forceModelScript = modelScript;
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("version=");
        stringBuilder.append(1);
        stringBuilder.append(";");
        if (this.getHairColor() != null) {
            stringBuilder.append("hairColor=");
            toString(this.getHairColor(), stringBuilder);
            stringBuilder.append(";");
        }

        if (this.getBeardColor() != null) {
            stringBuilder.append("beardColor=");
            toString(this.getBeardColor(), stringBuilder);
            stringBuilder.append(";");
        }

        if (this.getNaturalHairColor() != null) {
            stringBuilder.append("naturalHairColor=");
            toString(this.getNaturalHairColor(), stringBuilder);
            stringBuilder.append(";");
        }

        if (this.getNaturalBeardColor() != null) {
            stringBuilder.append("naturalBeardColor=");
            toString(this.getNaturalBeardColor(), stringBuilder);
            stringBuilder.append(";");
        }

        if (this.getSkinColor() != null) {
            stringBuilder.append("skinColor=");
            toString(this.getSkinColor(), stringBuilder);
            stringBuilder.append(";");
        }

        stringBuilder.append("bodyHair=");
        stringBuilder.append(this.getBodyHairIndex());
        stringBuilder.append(";");
        stringBuilder.append("skinTexture=");
        stringBuilder.append(this.getSkinTextureIndex());
        stringBuilder.append(";");
        if (this.getSkinTexture() != null) {
            stringBuilder.append("skinTextureName=");
            stringBuilder.append(this.getSkinTexture());
            stringBuilder.append(";");
        }

        if (this.getHairModel() != null) {
            stringBuilder.append("hairModel=");
            stringBuilder.append(this.getHairModel());
            stringBuilder.append(";");
        }

        if (this.getBeardModel() != null) {
            stringBuilder.append("beardModel=");
            stringBuilder.append(this.getBeardModel());
            stringBuilder.append(";");
        }

        return stringBuilder.toString();
    }

    public boolean loadLastStandString(String saveStr) {
        saveStr = saveStr.trim();
        if (!StringUtils.isNullOrWhitespace(saveStr) && saveStr.startsWith("version=")) {
            int int0 = -1;
            String[] strings = saveStr.split(";");

            for (int int1 = 0; int1 < strings.length; int1++) {
                int int2 = strings[int1].indexOf(61);
                if (int2 != -1) {
                    String string0 = strings[int1].substring(0, int2).trim();
                    String string1 = strings[int1].substring(int2 + 1).trim();
                    switch (string0) {
                        case "version":
                            int0 = Integer.parseInt(string1);
                            if (int0 < 1 || int0 > 1) {
                                return false;
                            }
                            break;
                        case "beardColor":
                            ImmutableColor immutableColor4 = colorFromString(string1);
                            if (immutableColor4 != null) {
                                this.setBeardColor(immutableColor4);
                            }
                            break;
                        case "naturalBeardColor":
                            ImmutableColor immutableColor3 = colorFromString(string1);
                            if (immutableColor3 != null) {
                                this.setNaturalBeardColor(immutableColor3);
                            }
                            break;
                        case "beardModel":
                            this.setBeardModel(string1);
                            break;
                        case "bodyHair":
                            try {
                                this.setBodyHairIndex(Integer.parseInt(string1));
                            } catch (NumberFormatException numberFormatException1) {
                            }
                            break;
                        case "hairColor":
                            ImmutableColor immutableColor2 = colorFromString(string1);
                            if (immutableColor2 != null) {
                                this.setHairColor(immutableColor2);
                            }
                            break;
                        case "naturalHairColor":
                            ImmutableColor immutableColor1 = colorFromString(string1);
                            if (immutableColor1 != null) {
                                this.setNaturalHairColor(immutableColor1);
                            }
                            break;
                        case "hairModel":
                            this.setHairModel(string1);
                            break;
                        case "skinColor":
                            ImmutableColor immutableColor0 = colorFromString(string1);
                            if (immutableColor0 != null) {
                                this.setSkinColor(immutableColor0);
                            }
                            break;
                        case "skinTexture":
                            try {
                                this.setSkinTextureIndex(Integer.parseInt(string1));
                            } catch (NumberFormatException numberFormatException0) {
                            }
                            break;
                        case "skinTextureName":
                            this.setSkinTextureName(string1);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
