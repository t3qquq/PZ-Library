// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.professions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import zombie.characters.skills.PerkFactory;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.interfaces.IListBoxItem;

public final class ProfessionFactory {
    public static LinkedHashMap<String, ProfessionFactory.Profession> ProfessionMap = new LinkedHashMap<>();

    public static void init() {
    }

    public static ProfessionFactory.Profession addProfession(String type, String name, String IconPath, int points) {
        ProfessionFactory.Profession profession = new ProfessionFactory.Profession(type, name, IconPath, points, "");
        ProfessionMap.put(type, profession);
        return profession;
    }

    public static ProfessionFactory.Profession getProfession(String type) {
        for (ProfessionFactory.Profession profession : ProfessionMap.values()) {
            if (profession.type.equals(type)) {
                return profession;
            }
        }

        return null;
    }

    public static ArrayList<ProfessionFactory.Profession> getProfessions() {
        ArrayList arrayList = new ArrayList();

        for (ProfessionFactory.Profession profession : ProfessionMap.values()) {
            arrayList.add(profession);
        }

        return arrayList;
    }

    public static void Reset() {
        ProfessionMap.clear();
    }

    public static class Profession implements IListBoxItem {
        public String type;
        public String name;
        public int cost;
        public String description;
        public String IconPath;
        public Texture texture = null;
        public Stack<String> FreeTraitStack = new Stack<>();
        private List<String> freeRecipes = new ArrayList<>();
        public HashMap<PerkFactory.Perk, Integer> XPBoostMap = new HashMap<>();

        public Profession(String _type, String _name, String IconPathname, int _cost, String desc) {
            this.type = _type;
            this.name = _name;
            this.IconPath = IconPathname;
            if (!IconPathname.equals("")) {
                this.texture = Texture.trygetTexture(IconPathname);
            }

            this.cost = _cost;
            this.description = desc;
        }

        public Texture getTexture() {
            return this.texture;
        }

        public void addFreeTrait(String trait) {
            this.FreeTraitStack.add(trait);
        }

        public ArrayList<String> getFreeTraits() {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.FreeTraitStack);
            return arrayList;
        }

        @Override
        public String getLabel() {
            return this.getName();
        }

        public String getIconPath() {
            return this.IconPath;
        }

        @Override
        public String getLeftLabel() {
            return this.getName();
        }

        @Override
        public String getRightLabel() {
            int int0 = this.getCost();
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

                return string + int0;
            }
        }

        /**
         * @return the type
         */
        public String getType() {
            return this.type;
        }

        /**
         * 
         * @param _type the type to set
         */
        public void setType(String _type) {
            this.type = _type;
        }

        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * 
         * @param _name the name to set
         */
        public void setName(String _name) {
            this.name = _name;
        }

        /**
         * @return the cost
         */
        public int getCost() {
            return this.cost;
        }

        /**
         * 
         * @param _cost the cost to set
         */
        public void setCost(int _cost) {
            this.cost = _cost;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * 
         * @param _description the description to set
         */
        public void setDescription(String _description) {
            this.description = _description;
        }

        /**
         * 
         * @param _IconPath the IconPath to set
         */
        public void setIconPath(String _IconPath) {
            this.IconPath = _IconPath;
        }

        /**
         * @return the FreeTraitStack
         */
        public Stack<String> getFreeTraitStack() {
            return this.FreeTraitStack;
        }

        public void addXPBoost(PerkFactory.Perk perk, int level) {
            if (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX) {
                this.XPBoostMap.put(perk, level);
            } else {
                DebugLog.General.warn("invalid perk passed to Profession.addXPBoost profession=%s perk=%s", this.name, perk);
            }
        }

        public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
            return this.XPBoostMap;
        }

        /**
         * 
         * @param _FreeTraitStack the FreeTraitStack to set
         */
        public void setFreeTraitStack(Stack<String> _FreeTraitStack) {
            this.FreeTraitStack = _FreeTraitStack;
        }

        public List<String> getFreeRecipes() {
            return this.freeRecipes;
        }

        public void setFreeRecipes(List<String> _freeRecipes) {
            this.freeRecipes = _freeRecipes;
        }
    }
}
