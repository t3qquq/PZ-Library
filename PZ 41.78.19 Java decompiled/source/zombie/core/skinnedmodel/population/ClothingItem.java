// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.util.StringUtils;

public final class ClothingItem extends Asset {
    public String m_GUID;
    public String m_MaleModel;
    public String m_FemaleModel;
    public boolean m_Static = false;
    public ArrayList<String> m_BaseTextures = new ArrayList<>();
    public String m_AttachBone;
    public ArrayList<Integer> m_Masks = new ArrayList<>();
    public String m_MasksFolder = "media/textures/Body/Masks";
    public String m_UnderlayMasksFolder = "media/textures/Body/Masks";
    public ArrayList<String> textureChoices = new ArrayList<>();
    public boolean m_AllowRandomHue = false;
    public boolean m_AllowRandomTint = false;
    public String m_DecalGroup = null;
    public String m_Shader = null;
    public String m_HatCategory = null;
    public static final String s_masksFolderDefault = "media/textures/Body/Masks";
    public String m_Name;
    public static final AssetType ASSET_TYPE = new AssetType("ClothingItem");

    public ClothingItem(AssetPath path, AssetManager assetManager) {
        super(path, assetManager);
    }

    public ArrayList<String> getBaseTextures() {
        return this.m_BaseTextures;
    }

    public ArrayList<String> getTextureChoices() {
        return this.textureChoices;
    }

    public String GetATexture() {
        return this.textureChoices.size() == 0 ? null : OutfitRNG.pickRandom(this.textureChoices);
    }

    public boolean getAllowRandomHue() {
        return this.m_AllowRandomHue;
    }

    public boolean getAllowRandomTint() {
        return this.m_AllowRandomTint;
    }

    public String getDecalGroup() {
        return this.m_DecalGroup;
    }

    public boolean isHat() {
        return !StringUtils.isNullOrWhitespace(this.m_HatCategory) && !"nobeard".equals(this.m_HatCategory);
    }

    public boolean isMask() {
        return !StringUtils.isNullOrWhitespace(this.m_HatCategory) && !this.m_HatCategory.contains("hair");
    }

    public void getCombinedMask(CharacterMask in_out_mask) {
        in_out_mask.setPartsVisible(this.m_Masks, false);
    }

    public boolean hasModel() {
        return !StringUtils.isNullOrWhitespace(this.m_MaleModel) && !StringUtils.isNullOrWhitespace(this.m_FemaleModel);
    }

    public String getModel(boolean female) {
        return female ? this.m_FemaleModel : this.m_MaleModel;
    }

    public String getFemaleModel() {
        return this.m_FemaleModel;
    }

    public String getMaleModel() {
        return this.m_MaleModel;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ Name:" + this.m_Name + ", GUID:" + this.m_GUID + "}";
    }

    public static void tryGetCombinedMask(ClothingItemReference itemRef, CharacterMask in_out_mask) {
        tryGetCombinedMask(itemRef.getClothingItem(), in_out_mask);
    }

    public static void tryGetCombinedMask(ClothingItem item, CharacterMask in_out_mask) {
        if (item != null) {
            item.getCombinedMask(in_out_mask);
        }
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }
}
