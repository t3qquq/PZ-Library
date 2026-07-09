// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.traits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import zombie.characters.skills.PerkFactory;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.interfaces.IListBoxItem;

public final class TraitFactory {
    public static LinkedHashMap<String, TraitFactory.Trait> TraitMap = new LinkedHashMap<>();

    public static void init() {
    }

    public static void setMutualExclusive(String a, String b) {
        TraitMap.get(a).MutuallyExclusive.add(b);
        TraitMap.get(b).MutuallyExclusive.add(a);
    }

    public static void sortList() {
        LinkedList linkedList = new LinkedList<>(TraitMap.entrySet());
        Collections.sort(linkedList, new Comparator<Entry<String, TraitFactory.Trait>>() {
            public int compare(Entry<String, TraitFactory.Trait> entry1, Entry<String, TraitFactory.Trait> entry0) {
                return ((TraitFactory.Trait)entry1.getValue()).name.compareTo(((TraitFactory.Trait)entry0.getValue()).name);
            }
        });
        LinkedHashMap linkedHashMap = new LinkedHashMap();

        for (Entry entry : linkedList) {
            linkedHashMap.put((String)entry.getKey(), (TraitFactory.Trait)entry.getValue());
        }

        TraitMap = linkedHashMap;
    }

    public static TraitFactory.Trait addTrait(String type, String name, int cost, String desc, boolean profession) {
        TraitFactory.Trait trait = new TraitFactory.Trait(type, name, cost, desc, profession, false);
        TraitMap.put(type, trait);
        return trait;
    }

    public static TraitFactory.Trait addTrait(String type, String name, int cost, String desc, boolean profession, boolean removeInMP) {
        TraitFactory.Trait trait = new TraitFactory.Trait(type, name, cost, desc, profession, removeInMP);
        TraitMap.put(type, trait);
        return trait;
    }

    public static ArrayList<TraitFactory.Trait> getTraits() {
        ArrayList arrayList = new ArrayList();

        for (TraitFactory.Trait trait : TraitMap.values()) {
            arrayList.add(trait);
        }

        return arrayList;
    }

    public static TraitFactory.Trait getTrait(String name) {
        return TraitMap.containsKey(name) ? TraitMap.get(name) : null;
    }

    public static void Reset() {
        TraitMap.clear();
    }

    public static class Trait implements IListBoxItem {
        public String traitID;
        public String name;
        public int cost;
        public String description;
        public boolean prof;
        public Texture texture = null;
        private boolean removeInMP = false;
        private List<String> freeRecipes = new ArrayList<>();
        public ArrayList<String> MutuallyExclusive = new ArrayList<>(0);
        public HashMap<PerkFactory.Perk, Integer> XPBoostMap = new HashMap<>();

        public void addXPBoost(PerkFactory.Perk perk, int level) {
            if (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX) {
                this.XPBoostMap.put(perk, level);
            } else {
                DebugLog.General.warn("invalid perk passed to Trait.addXPBoost trait=%s perk=%s", this.name, perk);
            }
        }

        public List<String> getFreeRecipes() {
            return this.freeRecipes;
        }

        public void setFreeRecipes(List<String> _freeRecipes) {
            this.freeRecipes = _freeRecipes;
        }

        public Trait(String tr, String _name, int _cost, String desc, boolean _prof, boolean _removeInMP) {
            this.traitID = tr;
            this.name = _name;
            this.cost = _cost;
            this.description = desc;
            this.prof = _prof;
            this.texture = Texture.getSharedTexture("media/ui/Traits/trait_" + this.traitID.toLowerCase(Locale.ENGLISH) + ".png");
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture("media/ui/Traits/trait_generic.png");
            }

            this.removeInMP = _removeInMP;
        }

        public String getType() {
            return this.traitID;
        }

        public Texture getTexture() {
            return this.texture;
        }

        @Override
        public String getLabel() {
            return this.name;
        }

        @Override
        public String getLeftLabel() {
            return this.name;
        }

        @Override
        public String getRightLabel() {
            int int0 = this.cost;
            if (int0 == 0) {
                return "";
            } else {
                String string = "+";
                if (int0 > 0) {
                    string = "-";
                } else if (int0 == 0) {
                    string = "";
                }

                if (int0 < 0) {
                    int0 = -int0;
                }

                return string + new Integer(int0).toString();
            }
        }

        public int getCost() {
            return this.cost;
        }

        public boolean isFree() {
            return this.prof;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String desc) {
            this.description = desc;
        }

        public ArrayList<String> getMutuallyExclusiveTraits() {
            return this.MutuallyExclusive;
        }

        public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
            return this.XPBoostMap;
        }

        public boolean isRemoveInMP() {
            return this.removeInMP;
        }

        public void setRemoveInMP(boolean _removeInMP) {
            this.removeInMP = _removeInMP;
        }
    }
}
