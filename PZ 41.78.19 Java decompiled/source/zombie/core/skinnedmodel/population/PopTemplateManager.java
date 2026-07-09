// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import java.util.Locale;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

public class PopTemplateManager {
    public static final PopTemplateManager instance = new PopTemplateManager();
    public final ArrayList<String> m_MaleSkins = new ArrayList<>();
    public final ArrayList<String> m_FemaleSkins = new ArrayList<>();
    public final ArrayList<String> m_MaleSkins_Zombie1 = new ArrayList<>();
    public final ArrayList<String> m_FemaleSkins_Zombie1 = new ArrayList<>();
    public final ArrayList<String> m_MaleSkins_Zombie2 = new ArrayList<>();
    public final ArrayList<String> m_FemaleSkins_Zombie2 = new ArrayList<>();
    public final ArrayList<String> m_MaleSkins_Zombie3 = new ArrayList<>();
    public final ArrayList<String> m_FemaleSkins_Zombie3 = new ArrayList<>();
    public final ArrayList<String> m_SkeletonMaleSkins_Zombie = new ArrayList<>();
    public final ArrayList<String> m_SkeletonFemaleSkins_Zombie = new ArrayList<>();
    public static final int SKELETON_BURNED_SKIN_INDEX = 0;
    public static final int SKELETON_NORMAL_SKIN_INDEX = 1;
    public static final int SKELETON_MUSCLE_SKIN_INDEX = 2;

    public void init() {
        ItemManager.init();

        for (int int0 = 1; int0 <= 5; int0++) {
            this.m_MaleSkins.add("MaleBody0" + int0);
        }

        for (int int1 = 1; int1 <= 5; int1++) {
            this.m_FemaleSkins.add("FemaleBody0" + int1);
        }

        for (int int2 = 1; int2 <= 4; int2++) {
            this.m_MaleSkins_Zombie1.add("M_ZedBody0" + int2 + "_level1");
            this.m_FemaleSkins_Zombie1.add("F_ZedBody0" + int2 + "_level1");
            this.m_MaleSkins_Zombie2.add("M_ZedBody0" + int2 + "_level2");
            this.m_FemaleSkins_Zombie2.add("F_ZedBody0" + int2 + "_level2");
            this.m_MaleSkins_Zombie3.add("M_ZedBody0" + int2 + "_level3");
            this.m_FemaleSkins_Zombie3.add("F_ZedBody0" + int2 + "_level3");
        }

        this.m_SkeletonMaleSkins_Zombie.add("SkeletonBurned");
        this.m_SkeletonMaleSkins_Zombie.add("Skeleton");
        this.m_SkeletonMaleSkins_Zombie.add("SkeletonMuscle");
        this.m_SkeletonFemaleSkins_Zombie.add("SkeletonBurned");
        this.m_SkeletonFemaleSkins_Zombie.add("Skeleton");
        this.m_SkeletonFemaleSkins_Zombie.add("SkeletonMuscle");
    }

    public ModelInstance addClothingItem(IsoGameCharacter character, ModelManager.ModelSlot modelSlot, ItemVisual itemVisual, ClothingItem clothingItem) {
        String string0 = clothingItem.getModel(character.isFemale());
        if (StringUtils.isNullOrWhitespace(string0)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.debugln("No model specified by item: " + clothingItem.m_Name);
            }

            return null;
        } else {
            string0 = this.processModelFileName(string0);
            String string1 = itemVisual.getTextureChoice(clothingItem);
            ImmutableColor immutableColor = itemVisual.getTint(clothingItem);
            float float0 = itemVisual.getHue(clothingItem);
            String string2 = clothingItem.m_AttachBone;
            String string3 = clothingItem.m_Shader;
            ModelInstance modelInstance;
            if (string2 != null && string2.length() > 0) {
                modelInstance = ModelManager.instance.newStaticInstance(modelSlot, string0, string1, string2, string3);
            } else {
                modelInstance = ModelManager.instance.newAdditionalModelInstance(string0, string1, character, modelSlot.model.AnimPlayer, string3);
            }

            if (modelInstance == null) {
                return null;
            } else {
                this.postProcessNewItemInstance(modelInstance, modelSlot, immutableColor);
                modelInstance.setItemVisual(itemVisual);
                return modelInstance;
            }
        }
    }

    private void addHeadHairItem(IsoGameCharacter character, ModelManager.ModelSlot modelSlot, String string0, String string1, ImmutableColor immutableColor) {
        if (StringUtils.isNullOrWhitespace(string0)) {
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.warn("No model specified.");
            }
        } else {
            string0 = this.processModelFileName(string0);
            ModelInstance modelInstance = ModelManager.instance.newAdditionalModelInstance(string0, string1, character, modelSlot.model.AnimPlayer, null);
            if (modelInstance != null) {
                this.postProcessNewItemInstance(modelInstance, modelSlot, immutableColor);
            }
        }
    }

    private void addHeadHair(IsoGameCharacter character, ModelManager.ModelSlot modelSlot, HumanVisual humanVisual, ItemVisual itemVisual, boolean boolean0) {
        ImmutableColor immutableColor = humanVisual.getHairColor();
        if (boolean0) {
            immutableColor = humanVisual.getBeardColor();
        }

        if (character.isFemale()) {
            if (!boolean0) {
                HairStyle hairStyle0 = HairStyles.instance.FindFemaleStyle(humanVisual.getHairModel());
                if (hairStyle0 != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                    hairStyle0 = HairStyles.instance.getAlternateForHat(hairStyle0, itemVisual.getClothingItem().m_HatCategory);
                }

                if (hairStyle0 != null && hairStyle0.isValid()) {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.debugln("  Adding female hair: " + hairStyle0.name);
                    }

                    this.addHeadHairItem(character, modelSlot, hairStyle0.model, hairStyle0.texture, immutableColor);
                }
            }
        } else if (!boolean0) {
            HairStyle hairStyle1 = HairStyles.instance.FindMaleStyle(humanVisual.getHairModel());
            if (hairStyle1 != null && itemVisual != null && itemVisual.getClothingItem() != null) {
                hairStyle1 = HairStyles.instance.getAlternateForHat(hairStyle1, itemVisual.getClothingItem().m_HatCategory);
            }

            if (hairStyle1 != null && hairStyle1.isValid()) {
                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln("  Adding male hair: " + hairStyle1.name);
                }

                this.addHeadHairItem(character, modelSlot, hairStyle1.model, hairStyle1.texture, immutableColor);
            }
        } else {
            BeardStyle beardStyle = BeardStyles.instance.FindStyle(humanVisual.getBeardModel());
            if (beardStyle != null && beardStyle.isValid()) {
                if (itemVisual != null
                    && itemVisual.getClothingItem() != null
                    && !StringUtils.isNullOrEmpty(itemVisual.getClothingItem().m_HatCategory)
                    && itemVisual.getClothingItem().m_HatCategory.contains("nobeard")) {
                    return;
                }

                if (DebugLog.isEnabled(DebugType.Clothing)) {
                    DebugLog.Clothing.debugln("  Adding beard: " + beardStyle.name);
                }

                this.addHeadHairItem(character, modelSlot, beardStyle.model, beardStyle.texture, immutableColor);
            }
        }
    }

    public void populateCharacterModelSlot(IsoGameCharacter character, ModelManager.ModelSlot modelSlot) {
        if (!(character instanceof IHumanVisual)) {
            DebugLog.Clothing.warn("Supplied character is not an IHumanVisual. Ignored. " + character);
        } else {
            HumanVisual humanVisual = ((IHumanVisual)character).getHumanVisual();
            ItemVisuals itemVisuals = new ItemVisuals();
            character.getItemVisuals(itemVisuals);
            CharacterMask characterMask = HumanVisual.GetMask(itemVisuals);
            if (DebugLog.isEnabled(DebugType.Clothing)) {
                DebugLog.Clothing.debugln("characterType:" + character.getClass().getName() + ", name:" + character.getName());
            }

            if (characterMask.isPartVisible(CharacterMask.Part.Head)) {
                this.addHeadHair(character, modelSlot, humanVisual, itemVisuals.findHat(), false);
                this.addHeadHair(character, modelSlot, humanVisual, itemVisuals.findMask(), true);
            }

            for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
                ItemVisual itemVisual0 = itemVisuals.get(int0);
                ClothingItem clothingItem0 = itemVisual0.getClothingItem();
                if (clothingItem0 == null) {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual0);
                    }
                } else if (!this.isItemModelHidden(character.getBodyLocationGroup(), itemVisuals, itemVisual0)) {
                    this.addClothingItem(character, modelSlot, itemVisual0, clothingItem0);
                }
            }

            for (int int1 = humanVisual.getBodyVisuals().size() - 1; int1 >= 0; int1--) {
                ItemVisual itemVisual1 = humanVisual.getBodyVisuals().get(int1);
                ClothingItem clothingItem1 = itemVisual1.getClothingItem();
                if (clothingItem1 == null) {
                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                        DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual1);
                    }
                } else {
                    this.addClothingItem(character, modelSlot, itemVisual1, clothingItem1);
                }
            }

            character.postUpdateModelTextures();
            character.updateSpeedModifiers();
        }
    }

    public boolean isItemModelHidden(BodyLocationGroup bodyLocationGroup, ItemVisuals itemVisuals, ItemVisual itemVisual) {
        Item item0 = itemVisual.getScriptItem();
        if (item0 != null && bodyLocationGroup.getLocation(item0.getBodyLocation()) != null) {
            for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
                if (itemVisuals.get(int0) != itemVisual) {
                    Item item1 = itemVisuals.get(int0).getScriptItem();
                    if (item1 != null
                        && bodyLocationGroup.getLocation(item1.getBodyLocation()) != null
                        && bodyLocationGroup.isHideModel(item1.getBodyLocation(), item0.getBodyLocation())) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private String processModelFileName(String string) {
        string = string.replaceAll("\\\\", "/");
        return string.toLowerCase(Locale.ENGLISH);
    }

    private void postProcessNewItemInstance(ModelInstance modelInstance, ModelManager.ModelSlot modelSlot, ImmutableColor immutableColor) {
        modelInstance.depthBias = 0.0F;
        modelInstance.matrixModel = modelSlot.model;
        modelInstance.tintR = immutableColor.r;
        modelInstance.tintG = immutableColor.g;
        modelInstance.tintB = immutableColor.b;
        modelInstance.parent = modelSlot.model;
        modelInstance.AnimPlayer = modelSlot.model.AnimPlayer;
        modelSlot.model.sub.add(0, modelInstance);
        modelSlot.sub.add(0, modelInstance);
        modelInstance.setOwner(modelSlot);
    }
}
