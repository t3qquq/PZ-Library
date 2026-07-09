// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.CharacterSmartTexture;
import zombie.characterTextures.ItemSmartTexture;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.ClothingDecal;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureCombiner;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.popman.ObjectPool;
import zombie.util.Lambda;
import zombie.util.StringUtils;

public final class ModelInstanceTextureCreator extends TextureDraw.GenericDrawer {
    private boolean bZombie;
    public int renderRefCount;
    private final CharacterMask mask = new CharacterMask();
    private final boolean[] holeMask = new boolean[BloodBodyPartType.MAX.index()];
    private final ItemVisuals itemVisuals = new ItemVisuals();
    private final ModelInstanceTextureCreator.CharacterData chrData = new ModelInstanceTextureCreator.CharacterData();
    private final ArrayList<ModelInstanceTextureCreator.ItemData> itemData = new ArrayList<>();
    private final CharacterSmartTexture characterSmartTexture = new CharacterSmartTexture();
    private final ItemSmartTexture itemSmartTexture = new ItemSmartTexture(null);
    private final ArrayList<Texture> tempTextures = new ArrayList<>();
    private boolean bRendered = false;
    private final ArrayList<Texture> texturesNotReady = new ArrayList<>();
    public int testNotReady = -1;
    private static final ObjectPool<ModelInstanceTextureCreator> pool = new ObjectPool<>(ModelInstanceTextureCreator::new);

    public void init(IsoGameCharacter chr) {
        ModelManager.ModelSlot modelSlot = chr.legsSprite.modelSlot;
        HumanVisual humanVisual = ((IHumanVisual)chr).getHumanVisual();
        chr.getItemVisuals(this.itemVisuals);
        this.init(humanVisual, this.itemVisuals, modelSlot.model);
        this.itemVisuals.clear();
    }

    public void init(BaseVisual baseVisual, ItemVisuals _itemVisuals, ModelInstance chrModelInstance) {
        if (baseVisual instanceof HumanVisual) {
            this.init((HumanVisual)baseVisual, _itemVisuals, chrModelInstance);
        } else {
            throw new IllegalArgumentException("unhandled BaseVisual " + baseVisual);
        }
    }

    public void init(HumanVisual humanVisual, ItemVisuals _itemVisuals, ModelInstance chrModelInstance) {
        boolean boolean0 = DebugLog.isEnabled(DebugType.Clothing);
        this.bRendered = false;
        this.bZombie = humanVisual.isZombie();
        CharacterMask characterMask = this.mask;
        characterMask.setAllVisible(true);
        String string0 = "media/textures/Body/Masks";
        Arrays.fill(this.holeMask, false);
        ModelInstanceTextureCreator.ItemData.pool.release(this.itemData);
        this.itemData.clear();
        this.texturesNotReady.clear();
        BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");

        for (int int0 = _itemVisuals.size() - 1; int0 >= 0; int0--) {
            ItemVisual itemVisual0 = _itemVisuals.get(int0);
            ClothingItem clothingItem0 = itemVisual0.getClothingItem();
            if (clothingItem0 == null) {
                if (boolean0) {
                    DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual0);
                }
            } else if (!clothingItem0.isReady()) {
                if (boolean0) {
                    DebugLog.Clothing.warn("ClothingItem not ready for ItemVisual:" + itemVisual0);
                }
            } else if (!PopTemplateManager.instance.isItemModelHidden(bodyLocationGroup, _itemVisuals, itemVisual0)) {
                ModelInstance modelInstance0 = this.findModelInstance(chrModelInstance.sub, itemVisual0);
                if (modelInstance0 == null) {
                    String string1 = clothingItem0.getModel(humanVisual.isFemale());
                    if (!StringUtils.isNullOrWhitespace(string1)) {
                        if (boolean0) {
                            DebugLog.Clothing.warn("ModelInstance not found for ItemVisual:" + itemVisual0);
                        }
                        continue;
                    }
                }

                this.addClothingItem(modelInstance0, itemVisual0, clothingItem0, characterMask, string0);

                for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); int1++) {
                    BloodBodyPartType bloodBodyPartType0 = BloodBodyPartType.FromIndex(int1);
                    if (itemVisual0.getHole(bloodBodyPartType0) > 0.0F && characterMask.isBloodBodyPartVisible(bloodBodyPartType0)) {
                        this.holeMask[int1] = true;
                    }
                }

                for (int int2 = 0; int2 < clothingItem0.m_Masks.size(); int2++) {
                    CharacterMask.Part part = CharacterMask.Part.fromInt(clothingItem0.m_Masks.get(int2));

                    for (BloodBodyPartType bloodBodyPartType1 : part.getBloodBodyPartTypes()) {
                        if (itemVisual0.getHole(bloodBodyPartType1) <= 0.0F) {
                            this.holeMask[bloodBodyPartType1.index()] = false;
                        }
                    }
                }

                itemVisual0.getClothingItemCombinedMask(characterMask);
                if (!StringUtils.equalsIgnoreCase(clothingItem0.m_UnderlayMasksFolder, "media/textures/Body/Masks")) {
                    string0 = clothingItem0.m_UnderlayMasksFolder;
                }
            }
        }

        this.chrData.modelInstance = chrModelInstance;
        this.chrData.mask.copyFrom(characterMask);
        this.chrData.maskFolder = string0;
        this.chrData.baseTexture = "media/textures/Body/" + humanVisual.getSkinTexture() + ".png";
        Arrays.fill(this.chrData.blood, 0.0F);

        for (int int3 = 0; int3 < BloodBodyPartType.MAX.index(); int3++) {
            BloodBodyPartType bloodBodyPartType2 = BloodBodyPartType.FromIndex(int3);
            this.chrData.blood[int3] = humanVisual.getBlood(bloodBodyPartType2);
            this.chrData.dirt[int3] = humanVisual.getDirt(bloodBodyPartType2);
        }

        Texture texture = getTextureWithFlags(this.chrData.baseTexture);
        if (texture != null && !texture.isReady()) {
            this.texturesNotReady.add(texture);
        }

        if (!this.chrData.mask.isAllVisible() && !this.chrData.mask.isNothingVisible()) {
            String string2 = this.chrData.maskFolder;
            Consumer consumer = Lambda.consumer(string2, this.texturesNotReady, (partx, string, arrayList) -> {
                Texture texturex = getTextureWithFlags(string + "/" + partx + ".png");
                if (texturex != null && !texturex.isReady()) {
                    arrayList.add(texturex);
                }
            });
            this.chrData.mask.forEachVisible(consumer);
        }

        texture = getTextureWithFlags("media/textures/BloodTextures/BloodOverlay.png");
        if (texture != null && !texture.isReady()) {
            this.texturesNotReady.add(texture);
        }

        texture = getTextureWithFlags("media/textures/BloodTextures/GrimeOverlay.png");
        if (texture != null && !texture.isReady()) {
            this.texturesNotReady.add(texture);
        }

        texture = getTextureWithFlags("media/textures/patches/patchesmask.png");
        if (texture != null && !texture.isReady()) {
            this.texturesNotReady.add(texture);
        }

        for (int int4 = 0; int4 < BloodBodyPartType.MAX.index(); int4++) {
            BloodBodyPartType bloodBodyPartType3 = BloodBodyPartType.FromIndex(int4);
            String string3 = "media/textures/BloodTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType3.index()] + ".png";
            texture = getTextureWithFlags(string3);
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            String string4 = "media/textures/HoleTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType3.index()] + ".png";
            texture = getTextureWithFlags(string4);
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            String string5 = "media/textures/patches/" + CharacterSmartTexture.BasicPatchesMaskFiles[bloodBodyPartType3.index()] + ".png";
            texture = getTextureWithFlags(string5);
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            String string6 = "media/textures/patches/" + CharacterSmartTexture.DenimPatchesMaskFiles[bloodBodyPartType3.index()] + ".png";
            texture = getTextureWithFlags(string6);
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }

            String string7 = "media/textures/patches/" + CharacterSmartTexture.LeatherPatchesMaskFiles[bloodBodyPartType3.index()] + ".png";
            texture = getTextureWithFlags(string7);
            if (texture != null && !texture.isReady()) {
                this.texturesNotReady.add(texture);
            }
        }

        characterMask.setAllVisible(true);
        string0 = "media/textures/Body/Masks";

        for (int int5 = humanVisual.getBodyVisuals().size() - 1; int5 >= 0; int5--) {
            ItemVisual itemVisual1 = humanVisual.getBodyVisuals().get(int5);
            ClothingItem clothingItem1 = itemVisual1.getClothingItem();
            if (clothingItem1 == null) {
                if (boolean0) {
                    DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual1);
                }
            } else if (!clothingItem1.isReady()) {
                if (boolean0) {
                    DebugLog.Clothing.warn("ClothingItem not ready for ItemVisual:" + itemVisual1);
                }
            } else {
                ModelInstance modelInstance1 = this.findModelInstance(chrModelInstance.sub, itemVisual1);
                if (modelInstance1 == null) {
                    String string8 = clothingItem1.getModel(humanVisual.isFemale());
                    if (!StringUtils.isNullOrWhitespace(string8)) {
                        if (boolean0) {
                            DebugLog.Clothing.warn("ModelInstance not found for ItemVisual:" + itemVisual1);
                        }
                        continue;
                    }
                }

                this.addClothingItem(modelInstance1, itemVisual1, clothingItem1, characterMask, string0);
            }
        }
    }

    private ModelInstance findModelInstance(ArrayList<ModelInstance> arrayList, ItemVisual itemVisual1) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ModelInstance modelInstance = (ModelInstance)arrayList.get(int0);
            ItemVisual itemVisual0 = modelInstance.getItemVisual();
            if (itemVisual0 != null && itemVisual0.getClothingItem() == itemVisual1.getClothingItem()) {
                return modelInstance;
            }
        }

        return null;
    }

    private void addClothingItem(ModelInstance modelInstance, ItemVisual itemVisual, ClothingItem clothingItem, CharacterMask characterMask, String string1) {
        String string0 = modelInstance == null ? itemVisual.getBaseTexture(clothingItem) : itemVisual.getTextureChoice(clothingItem);
        ImmutableColor immutableColor = itemVisual.getTint(clothingItem);
        float float0 = itemVisual.getHue(clothingItem);
        ModelInstanceTextureCreator.ItemData itemDatax = ModelInstanceTextureCreator.ItemData.pool.alloc();
        itemDatax.modelInstance = modelInstance;
        itemDatax.category = CharacterSmartTexture.ClothingItemCategory;
        itemDatax.mask.copyFrom(characterMask);
        itemDatax.maskFolder = clothingItem.m_MasksFolder;
        if (StringUtils.equalsIgnoreCase(itemDatax.maskFolder, "media/textures/Body/Masks")) {
            itemDatax.maskFolder = string1;
        }

        if (StringUtils.equalsIgnoreCase(itemDatax.maskFolder, "none")) {
            itemDatax.mask.setAllVisible(true);
        }

        if (itemDatax.maskFolder.contains("Clothes/Hat/Masks")) {
            itemDatax.mask.setAllVisible(true);
        }

        itemDatax.baseTexture = "media/textures/" + string0 + ".png";
        itemDatax.tint = immutableColor;
        itemDatax.hue = float0;
        itemDatax.decalTexture = null;
        Arrays.fill(itemDatax.basicPatches, 0.0F);
        Arrays.fill(itemDatax.denimPatches, 0.0F);
        Arrays.fill(itemDatax.leatherPatches, 0.0F);
        Arrays.fill(itemDatax.blood, 0.0F);
        Arrays.fill(itemDatax.dirt, 0.0F);
        Arrays.fill(itemDatax.hole, 0.0F);
        int int0 = ModelManager.instance.getTextureFlags();
        Texture texture = Texture.getSharedTexture(itemDatax.baseTexture, int0);
        if (texture != null && !texture.isReady()) {
            this.texturesNotReady.add(texture);
        }

        if (!itemDatax.mask.isAllVisible() && !itemDatax.mask.isNothingVisible()) {
            String string2 = itemDatax.maskFolder;
            Consumer consumer = Lambda.consumer(string2, this.texturesNotReady, (partx, string, arrayList) -> {
                Texture texturex = getTextureWithFlags(string + "/" + partx + ".png");
                if (texturex != null && !texturex.isReady()) {
                    arrayList.add(texturex);
                }
            });
            itemDatax.mask.forEachVisible(consumer);
        }

        if (Core.getInstance().isOptionSimpleClothingTextures(this.bZombie)) {
            this.itemData.add(itemDatax);
        } else {
            String string3 = itemVisual.getDecal(clothingItem);
            if (!StringUtils.isNullOrWhitespace(string3)) {
                ClothingDecal clothingDecal = ClothingDecals.instance.getDecal(string3);
                if (clothingDecal != null && clothingDecal.isValid()) {
                    itemDatax.decalTexture = clothingDecal.texture;
                    itemDatax.decalX = clothingDecal.x;
                    itemDatax.decalY = clothingDecal.y;
                    itemDatax.decalWidth = clothingDecal.width;
                    itemDatax.decalHeight = clothingDecal.height;
                    texture = getTextureWithFlags("media/textures/" + itemDatax.decalTexture + ".png");
                    if (texture != null && !texture.isReady()) {
                        this.texturesNotReady.add(texture);
                    }
                }
            }

            for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); int1++) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
                itemDatax.blood[int1] = itemVisual.getBlood(bloodBodyPartType);
                itemDatax.dirt[int1] = itemVisual.getDirt(bloodBodyPartType);
                itemDatax.basicPatches[int1] = itemVisual.getBasicPatch(bloodBodyPartType);
                itemDatax.denimPatches[int1] = itemVisual.getDenimPatch(bloodBodyPartType);
                itemDatax.leatherPatches[int1] = itemVisual.getLeatherPatch(bloodBodyPartType);
                itemDatax.hole[int1] = itemVisual.getHole(bloodBodyPartType);
                if (itemDatax.hole[int1] > 0.0F) {
                    String string4 = "media/textures/HoleTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()] + ".png";
                    texture = getTextureWithFlags(string4);
                    if (texture != null && !texture.isReady()) {
                        this.texturesNotReady.add(texture);
                    }
                }

                if (itemDatax.hole[int1] == 0.0F && this.holeMask[int1]) {
                    itemDatax.hole[int1] = -1.0F;
                    if (itemDatax.mask.isBloodBodyPartVisible(bloodBodyPartType)) {
                    }
                }
            }

            this.itemData.add(itemDatax);
        }
    }

    @Override
    public void render() {
        if (!this.bRendered) {
            for (int int0 = 0; int0 < this.texturesNotReady.size(); int0++) {
                Texture texture0 = this.texturesNotReady.get(int0);
                if (!texture0.isReady()) {
                    return;
                }
            }

            GL11.glPushAttrib(2048);

            try {
                this.tempTextures.clear();
                CharacterSmartTexture characterSmartTexturex = this.createFullCharacterTexture();

                assert characterSmartTexturex == this.characterSmartTexture;

                if (!(this.chrData.modelInstance.tex instanceof CharacterSmartTexture)) {
                    this.chrData.modelInstance.tex = new CharacterSmartTexture();
                }

                ((CharacterSmartTexture)this.chrData.modelInstance.tex).clear();
                this.applyCharacterTexture(characterSmartTexturex.result, (CharacterSmartTexture)this.chrData.modelInstance.tex);
                characterSmartTexturex.clear();
                this.tempTextures.add(characterSmartTexturex.result);
                characterSmartTexturex.result = null;
                characterSmartTexturex = (CharacterSmartTexture)this.chrData.modelInstance.tex;

                for (int int1 = this.itemData.size() - 1; int1 >= 0; int1--) {
                    ModelInstanceTextureCreator.ItemData itemDatax = this.itemData.get(int1);
                    Texture texture1;
                    if (this.isSimpleTexture(itemDatax)) {
                        int int2 = ModelManager.instance.getTextureFlags();
                        texture1 = Texture.getSharedTexture(itemDatax.baseTexture, int2);
                        if (!this.isItemSmartTextureRequired(itemDatax)) {
                            itemDatax.modelInstance.tex = texture1;
                            continue;
                        }
                    } else {
                        ItemSmartTexture itemSmartTexturex = this.createFullItemTexture(itemDatax);

                        assert itemSmartTexturex == this.itemSmartTexture;

                        texture1 = itemSmartTexturex.result;
                        this.tempTextures.add(itemSmartTexturex.result);
                        itemSmartTexturex.result = null;
                    }

                    if (itemDatax.modelInstance == null) {
                        this.applyItemTexture(itemDatax, texture1, characterSmartTexturex);
                    } else {
                        if (!(itemDatax.modelInstance.tex instanceof ItemSmartTexture)) {
                            itemDatax.modelInstance.tex = new ItemSmartTexture(null);
                        }

                        ((ItemSmartTexture)itemDatax.modelInstance.tex).clear();
                        this.applyItemTexture(itemDatax, texture1, (ItemSmartTexture)itemDatax.modelInstance.tex);
                        ((ItemSmartTexture)itemDatax.modelInstance.tex).calculate();
                        ((ItemSmartTexture)itemDatax.modelInstance.tex).clear();
                    }
                }

                characterSmartTexturex.calculate();
                characterSmartTexturex.clear();
                this.itemSmartTexture.clear();

                for (int int3 = 0; int3 < this.tempTextures.size(); int3++) {
                    for (int int4 = 0; int4 < this.itemData.size(); int4++) {
                        ModelInstance modelInstance = this.itemData.get(int4).modelInstance;

                        assert modelInstance == null || this.tempTextures.get(int3) != modelInstance.tex;
                    }

                    TextureCombiner.instance.releaseTexture(this.tempTextures.get(int3));
                }

                this.tempTextures.clear();
            } finally {
                GL11.glPopAttrib();
            }

            this.bRendered = true;
        }
    }

    private CharacterSmartTexture createFullCharacterTexture() {
        CharacterSmartTexture characterSmartTexturex = this.characterSmartTexture;
        characterSmartTexturex.clear();
        characterSmartTexturex.addTexture(this.chrData.baseTexture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0F);

        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int0);
            if (this.chrData.dirt[int0] > 0.0F) {
                characterSmartTexturex.addDirt(bloodBodyPartType, this.chrData.dirt[int0], null);
            }

            if (this.chrData.blood[int0] > 0.0F) {
                characterSmartTexturex.addBlood(bloodBodyPartType, this.chrData.blood[int0], null);
            }
        }

        characterSmartTexturex.calculate();
        return characterSmartTexturex;
    }

    private void applyCharacterTexture(Texture texture, CharacterSmartTexture characterSmartTexturex) {
        characterSmartTexturex.addMaskedTexture(
            this.chrData.mask, this.chrData.maskFolder, texture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0F
        );

        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int0);
            if (this.holeMask[int0]) {
                characterSmartTexturex.removeHole(texture, bloodBodyPartType);
            }
        }
    }

    private boolean isSimpleTexture(ModelInstanceTextureCreator.ItemData itemDatax) {
        if (itemDatax.hue != 0.0F) {
            return false;
        } else {
            ImmutableColor immutableColor = itemDatax.tint;
            if (itemDatax.modelInstance != null) {
                immutableColor = ImmutableColor.white;
            }

            if (!immutableColor.equals(ImmutableColor.white)) {
                return false;
            } else if (itemDatax.decalTexture != null) {
                return false;
            } else {
                for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
                    if (itemDatax.blood[int0] > 0.0F) {
                        return false;
                    }

                    if (itemDatax.dirt[int0] > 0.0F) {
                        return false;
                    }

                    if (itemDatax.hole[int0] > 0.0F) {
                        return false;
                    }

                    if (itemDatax.basicPatches[int0] > 0.0F) {
                        return false;
                    }

                    if (itemDatax.denimPatches[int0] > 0.0F) {
                        return false;
                    }

                    if (itemDatax.leatherPatches[int0] > 0.0F) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private ItemSmartTexture createFullItemTexture(ModelInstanceTextureCreator.ItemData itemDatax) {
        ItemSmartTexture itemSmartTexturex = this.itemSmartTexture;
        itemSmartTexturex.clear();
        ImmutableColor immutableColor = itemDatax.tint;
        if (itemDatax.modelInstance != null) {
            itemDatax.modelInstance.tintR = itemDatax.modelInstance.tintG = itemDatax.modelInstance.tintB = 1.0F;
        }

        itemSmartTexturex.addTexture(itemDatax.baseTexture, itemDatax.category, immutableColor, itemDatax.hue);
        if (itemDatax.decalTexture != null) {
            itemSmartTexturex.addRect(
                "media/textures/" + itemDatax.decalTexture + ".png", itemDatax.decalX, itemDatax.decalY, itemDatax.decalWidth, itemDatax.decalHeight
            );
        }

        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            if (itemDatax.blood[int0] > 0.0F) {
                BloodBodyPartType bloodBodyPartType0 = BloodBodyPartType.FromIndex(int0);
                itemSmartTexturex.addBlood("media/textures/BloodTextures/BloodOverlay.png", bloodBodyPartType0, itemDatax.blood[int0]);
            }

            if (itemDatax.dirt[int0] > 0.0F) {
                BloodBodyPartType bloodBodyPartType1 = BloodBodyPartType.FromIndex(int0);
                itemSmartTexturex.addDirt("media/textures/BloodTextures/GrimeOverlay.png", bloodBodyPartType1, itemDatax.dirt[int0]);
            }

            if (itemDatax.basicPatches[int0] > 0.0F) {
                BloodBodyPartType bloodBodyPartType2 = BloodBodyPartType.FromIndex(int0);
                itemSmartTexturex.setBasicPatches(bloodBodyPartType2);
            }

            if (itemDatax.denimPatches[int0] > 0.0F) {
                BloodBodyPartType bloodBodyPartType3 = BloodBodyPartType.FromIndex(int0);
                itemSmartTexturex.setDenimPatches(bloodBodyPartType3);
            }

            if (itemDatax.leatherPatches[int0] > 0.0F) {
                BloodBodyPartType bloodBodyPartType4 = BloodBodyPartType.FromIndex(int0);
                itemSmartTexturex.setLeatherPatches(bloodBodyPartType4);
            }
        }

        for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); int1++) {
            if (itemDatax.hole[int1] > 0.0F) {
                BloodBodyPartType bloodBodyPartType5 = BloodBodyPartType.FromIndex(int1);
                Texture texture = itemSmartTexturex.addHole(bloodBodyPartType5);

                assert texture != itemSmartTexturex.result;

                this.tempTextures.add(texture);
            }
        }

        itemSmartTexturex.calculate();
        return itemSmartTexturex;
    }

    private boolean isItemSmartTextureRequired(ModelInstanceTextureCreator.ItemData itemDatax) {
        if (itemDatax.modelInstance == null) {
            return true;
        } else if (itemDatax.modelInstance.tex instanceof ItemSmartTexture) {
            return true;
        } else {
            for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
                if (itemDatax.hole[int0] < 0.0F) {
                    return true;
                }
            }

            return !itemDatax.mask.isAllVisible();
        }
    }

    private void applyItemTexture(ModelInstanceTextureCreator.ItemData itemDatax, Texture texture, SmartTexture smartTexture) {
        smartTexture.addMaskedTexture(itemDatax.mask, itemDatax.maskFolder, texture, itemDatax.category, ImmutableColor.white, 0.0F);

        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            if (itemDatax.hole[int0] < 0.0F) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int0);
                smartTexture.removeHole(texture, bloodBodyPartType);
            }
        }
    }

    @Override
    public void postRender() {
        if (!this.bRendered) {
            if (this.chrData.modelInstance.character == null) {
                boolean boolean0 = true;
            } else {
                boolean boolean1 = true;
            }
        }

        for (int int0 = 0; int0 < this.itemData.size(); int0++) {
            this.itemData.get(int0).modelInstance = null;
        }

        this.chrData.modelInstance = null;
        this.texturesNotReady.clear();
        ModelInstanceTextureCreator.ItemData.pool.release(this.itemData);
        this.itemData.clear();
        pool.release(this);
    }

    public boolean isRendered() {
        return this.testNotReady > 0 ? false : this.bRendered;
    }

    private static Texture getTextureWithFlags(String string) {
        return Texture.getSharedTexture(string, ModelManager.instance.getTextureFlags());
    }

    public static ModelInstanceTextureCreator alloc() {
        return pool.alloc();
    }

    private static final class CharacterData {
        ModelInstance modelInstance;
        final CharacterMask mask = new CharacterMask();
        String maskFolder;
        String baseTexture;
        final float[] blood = new float[BloodBodyPartType.MAX.index()];
        final float[] dirt = new float[BloodBodyPartType.MAX.index()];
    }

    private static final class ItemData {
        ModelInstance modelInstance;
        final CharacterMask mask = new CharacterMask();
        String maskFolder;
        String baseTexture;
        int category;
        ImmutableColor tint;
        float hue;
        String decalTexture;
        int decalX;
        int decalY;
        int decalWidth;
        int decalHeight;
        final float[] blood = new float[BloodBodyPartType.MAX.index()];
        final float[] dirt = new float[BloodBodyPartType.MAX.index()];
        final float[] basicPatches = new float[BloodBodyPartType.MAX.index()];
        final float[] denimPatches = new float[BloodBodyPartType.MAX.index()];
        final float[] leatherPatches = new float[BloodBodyPartType.MAX.index()];
        final float[] hole = new float[BloodBodyPartType.MAX.index()];
        static final ObjectPool<ModelInstanceTextureCreator.ItemData> pool = new ObjectPool<>(ModelInstanceTextureCreator.ItemData::new);
    }
}
