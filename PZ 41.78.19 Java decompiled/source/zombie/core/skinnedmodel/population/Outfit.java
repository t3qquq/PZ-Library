// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlTransient;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;

public class Outfit implements Cloneable {
    public String m_Name = "Outfit";
    public boolean m_Top = true;
    public boolean m_Pants = true;
    public final ArrayList<String> m_TopTextures = new ArrayList<>();
    public final ArrayList<String> m_PantsTextures = new ArrayList<>();
    public final ArrayList<ClothingItemReference> m_items = new ArrayList<>();
    public boolean m_AllowPantsHue = true;
    public boolean m_AllowPantsTint = false;
    public boolean m_AllowTopTint = true;
    public boolean m_AllowTShirtDecal = true;
    @XmlTransient
    public String m_modID;
    @XmlTransient
    public boolean m_Immutable = false;
    @XmlTransient
    public final Outfit.RandomData RandomData = new Outfit.RandomData();

    public void setModID(String modID) {
        this.m_modID = modID;

        for (ClothingItemReference clothingItemReference : this.m_items) {
            clothingItemReference.setModID(modID);
        }
    }

    public void AddItem(ClothingItemReference item) {
        this.m_items.add(item);
    }

    public void Randomize() {
        if (this.m_Immutable) {
            throw new RuntimeException("trying to randomize an immutable Outfit");
        } else {
            for (int int0 = 0; int0 < this.m_items.size(); int0++) {
                ClothingItemReference clothingItemReference = this.m_items.get(int0);
                clothingItemReference.randomize();
            }

            this.RandomData.m_hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor(this.m_Name);
            this.RandomData.m_femaleHairName = HairStyles.instance.getRandomFemaleStyle(this.m_Name);
            this.RandomData.m_maleHairName = HairStyles.instance.getRandomMaleStyle(this.m_Name);
            this.RandomData.m_beardName = BeardStyles.instance.getRandomStyle(this.m_Name);
            this.RandomData.m_topTint = OutfitRNG.randomImmutableColor();
            this.RandomData.m_pantsTint = OutfitRNG.randomImmutableColor();
            if (OutfitRNG.Next(4) == 0) {
                this.RandomData.m_pantsHue = OutfitRNG.Next(200) / 100.0F - 1.0F;
            } else {
                this.RandomData.m_pantsHue = 0.0F;
            }

            this.RandomData.m_hasTop = OutfitRNG.Next(16) != 0;
            this.RandomData.m_hasTShirt = OutfitRNG.Next(2) == 0;
            this.RandomData.m_hasTShirtDecal = OutfitRNG.Next(4) == 0;
            if (this.m_Top) {
                this.RandomData.m_hasTop = true;
            }

            this.RandomData.m_topTexture = OutfitRNG.pickRandom(this.m_TopTextures);
            this.RandomData.m_pantsTexture = OutfitRNG.pickRandom(this.m_PantsTextures);
        }
    }

    public void randomizeItem(String itemGuid) {
        ClothingItemReference clothingItemReference = PZArrayUtil.find(this.m_items, clothingItemReferencex -> clothingItemReferencex.itemGUID.equals(itemGuid));
        if (clothingItemReference != null) {
            clothingItemReference.randomize();
        } else {
            DebugLog.Clothing.println("Outfit.randomizeItem> Could not find itemGuid: " + itemGuid);
        }
    }

    public CharacterMask GetMask() {
        CharacterMask characterMask = new CharacterMask();

        for (int int0 = 0; int0 < this.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference = this.m_items.get(int0);
            if (clothingItemReference.RandomData.m_Active) {
                ClothingItem.tryGetCombinedMask(clothingItemReference, characterMask);
            }
        }

        return characterMask;
    }

    public boolean containsItemGuid(String itemGuid) {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference = this.m_items.get(int0);
            if (clothingItemReference.itemGUID.equals(itemGuid)) {
                boolean0 = true;
                break;
            }
        }

        return boolean0;
    }

    public ClothingItemReference findItemByGUID(String itemGuid) {
        for (int int0 = 0; int0 < this.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference = this.m_items.get(int0);
            if (clothingItemReference.itemGUID.equals(itemGuid)) {
                return clothingItemReference;
            }
        }

        return null;
    }

    public Outfit clone() {
        try {
            Outfit outfit0 = new Outfit();
            outfit0.m_Name = this.m_Name;
            outfit0.m_Top = this.m_Top;
            outfit0.m_Pants = this.m_Pants;
            outfit0.m_PantsTextures.addAll(this.m_PantsTextures);
            outfit0.m_TopTextures.addAll(this.m_TopTextures);
            PZArrayUtil.copy(outfit0.m_items, this.m_items, ClothingItemReference::clone);
            outfit0.m_AllowPantsHue = this.m_AllowPantsHue;
            outfit0.m_AllowPantsTint = this.m_AllowPantsTint;
            outfit0.m_AllowTopTint = this.m_AllowTopTint;
            outfit0.m_AllowTShirtDecal = this.m_AllowTShirtDecal;
            return outfit0;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new RuntimeException("Outfit clone failed.", cloneNotSupportedException);
        }
    }

    public ClothingItemReference findHat() {
        for (ClothingItemReference clothingItemReference : this.m_items) {
            if (clothingItemReference.RandomData.m_Active) {
                ClothingItem clothingItem = clothingItemReference.getClothingItem();
                if (clothingItem != null && clothingItem.isHat()) {
                    return clothingItemReference;
                }
            }
        }

        return null;
    }

    public boolean isEmpty() {
        for (int int0 = 0; int0 < this.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference0 = this.m_items.get(int0);
            ClothingItem clothingItem = OutfitManager.instance.getClothingItem(clothingItemReference0.itemGUID);
            if (clothingItem != null && clothingItem.isEmpty()) {
                return true;
            }

            for (int int1 = 0; int1 < clothingItemReference0.subItems.size(); int1++) {
                ClothingItemReference clothingItemReference1 = clothingItemReference0.subItems.get(int1);
                clothingItem = OutfitManager.instance.getClothingItem(clothingItemReference1.itemGUID);
                if (clothingItem != null && clothingItem.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void loadItems() {
        for (int int0 = 0; int0 < this.m_items.size(); int0++) {
            ClothingItemReference clothingItemReference0 = this.m_items.get(int0);
            OutfitManager.instance.getClothingItem(clothingItemReference0.itemGUID);

            for (int int1 = 0; int1 < clothingItemReference0.subItems.size(); int1++) {
                ClothingItemReference clothingItemReference1 = clothingItemReference0.subItems.get(int1);
                OutfitManager.instance.getClothingItem(clothingItemReference1.itemGUID);
            }
        }
    }

    public static class RandomData {
        public ImmutableColor m_hairColor;
        public String m_maleHairName;
        public String m_femaleHairName;
        public String m_beardName;
        public ImmutableColor m_topTint;
        public ImmutableColor m_pantsTint;
        public float m_pantsHue;
        public boolean m_hasTop;
        public boolean m_hasTShirt;
        public boolean m_hasTShirtDecal;
        public String m_topTexture;
        public String m_pantsTexture;
    }
}
