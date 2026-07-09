// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.skills;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.Translator;
import zombie.core.math.PZMath;

public final class PerkFactory {
    public static final ArrayList<PerkFactory.Perk> PerkList = new ArrayList<>();
    private static final HashMap<String, PerkFactory.Perk> PerkById = new HashMap<>();
    private static final HashMap<String, PerkFactory.Perk> PerkByName = new HashMap<>();
    private static final PerkFactory.Perk[] PerkByIndex = new PerkFactory.Perk[256];
    private static int NextPerkID = 0;
    static float PerkXPReqMultiplier = 1.5F;

    public static String getPerkName(PerkFactory.Perk type) {
        return type.getName();
    }

    public static PerkFactory.Perk getPerkFromName(String name) {
        return PerkByName.get(name);
    }

    public static PerkFactory.Perk getPerk(PerkFactory.Perk perk) {
        return perk;
    }

    public static PerkFactory.Perk AddPerk(
        PerkFactory.Perk perk, String translation, int xp1, int xp2, int xp3, int xp4, int xp5, int xp6, int xp7, int xp8, int xp9, int xp10
    ) {
        return AddPerk(perk, translation, PerkFactory.Perks.None, xp1, xp2, xp3, xp4, xp5, xp6, xp7, xp8, xp9, xp10, false);
    }

    public static PerkFactory.Perk AddPerk(
        PerkFactory.Perk perk, String translation, int xp1, int xp2, int xp3, int xp4, int xp5, int xp6, int xp7, int xp8, int xp9, int xp10, boolean passiv
    ) {
        return AddPerk(perk, translation, PerkFactory.Perks.None, xp1, xp2, xp3, xp4, xp5, xp6, xp7, xp8, xp9, xp10, passiv);
    }

    public static PerkFactory.Perk AddPerk(
        PerkFactory.Perk perk,
        String translation,
        PerkFactory.Perk parent,
        int xp1,
        int xp2,
        int xp3,
        int xp4,
        int xp5,
        int xp6,
        int xp7,
        int xp8,
        int xp9,
        int xp10
    ) {
        return AddPerk(perk, translation, parent, xp1, xp2, xp3, xp4, xp5, xp6, xp7, xp8, xp9, xp10, false);
    }

    public static PerkFactory.Perk AddPerk(
        PerkFactory.Perk perk,
        String translation,
        PerkFactory.Perk parent,
        int xp1,
        int xp2,
        int xp3,
        int xp4,
        int xp5,
        int xp6,
        int xp7,
        int xp8,
        int xp9,
        int xp10,
        boolean passiv
    ) {
        perk.translation = translation;
        perk.name = Translator.getText("IGUI_perks_" + translation);
        perk.parent = parent;
        perk.passiv = passiv;
        perk.xp1 = (int)(xp1 * PerkXPReqMultiplier);
        perk.xp2 = (int)(xp2 * PerkXPReqMultiplier);
        perk.xp3 = (int)(xp3 * PerkXPReqMultiplier);
        perk.xp4 = (int)(xp4 * PerkXPReqMultiplier);
        perk.xp5 = (int)(xp5 * PerkXPReqMultiplier);
        perk.xp6 = (int)(xp6 * PerkXPReqMultiplier);
        perk.xp7 = (int)(xp7 * PerkXPReqMultiplier);
        perk.xp8 = (int)(xp8 * PerkXPReqMultiplier);
        perk.xp9 = (int)(xp9 * PerkXPReqMultiplier);
        perk.xp10 = (int)(xp10 * PerkXPReqMultiplier);
        PerkByName.put(perk.getName(), perk);
        PerkList.add(perk);
        return perk;
    }

    public static void init() {
        PerkFactory.Perks.None.parent = PerkFactory.Perks.None;
        PerkFactory.Perks.MAX.parent = PerkFactory.Perks.None;
        AddPerk(PerkFactory.Perks.Combat, "Combat", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Axe, "Axe", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Blunt, "Blunt", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.SmallBlunt, "SmallBlunt", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.LongBlade, "LongBlade", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.SmallBlade, "SmallBlade", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Spear, "Spear", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Maintenance, "Maintenance", PerkFactory.Perks.Combat, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Firearm, "Firearm", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Aiming, "Aiming", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Reloading, "Reloading", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Crafting, "Crafting", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Woodwork, "Carpentry", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Cooking, "Cooking", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Farming, "Farming", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Doctor, "Doctor", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Electricity, "Electricity", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.MetalWelding, "MetalWelding", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Mechanics, "Mechanics", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Tailoring, "Tailoring", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Survivalist, "Survivalist", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Fishing, "Fishing", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Trapping, "Trapping", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.PlantScavenging, "Foraging", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Passiv, "Passive", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000, true);
        AddPerk(PerkFactory.Perks.Fitness, "Fitness", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
        AddPerk(PerkFactory.Perks.Strength, "Strength", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
        AddPerk(PerkFactory.Perks.Agility, "Agility", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Sprinting, "Sprinting", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Lightfoot, "Lightfooted", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Nimble, "Nimble", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
        AddPerk(PerkFactory.Perks.Sneak, "Sneaking", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
    }

    public static void initTranslations() {
        PerkByName.clear();

        for (PerkFactory.Perk perk : PerkList) {
            perk.name = Translator.getText("IGUI_perks_" + perk.translation);
            PerkByName.put(perk.name, perk);
        }
    }

    public static void Reset() {
        NextPerkID = 0;

        for (int int0 = PerkByIndex.length - 1; int0 >= 0; int0--) {
            PerkFactory.Perk perk = PerkByIndex[int0];
            if (perk != null) {
                if (perk.isCustom()) {
                    PerkList.remove(perk);
                    PerkById.remove(perk.getId());
                    PerkByName.remove(perk.getName());
                    PerkByIndex[perk.index] = null;
                } else if (perk != PerkFactory.Perks.MAX && NextPerkID == 0) {
                    NextPerkID = int0 + 1;
                }
            }
        }

        PerkFactory.Perks.MAX.index = NextPerkID;
    }

    public static final class Perk {
        private final String id;
        private int index;
        private boolean bCustom = false;
        public String translation;
        public String name;
        public boolean passiv = false;
        public int xp1;
        public int xp2;
        public int xp3;
        public int xp4;
        public int xp5;
        public int xp6;
        public int xp7;
        public int xp8;
        public int xp9;
        public int xp10;
        public PerkFactory.Perk parent = PerkFactory.Perks.None;

        public Perk(String _id) {
            this.id = _id;
            this.index = PerkFactory.NextPerkID++;
            this.translation = _id;
            this.name = _id;
            PerkFactory.PerkById.put(_id, this);
            PerkFactory.PerkByIndex[this.index] = this;
            if (PerkFactory.Perks.MAX != null) {
                PerkFactory.Perks.MAX.index = PZMath.max(PerkFactory.Perks.MAX.index, this.index + 1);
            }
        }

        public Perk(String _id, PerkFactory.Perk _parent) {
            this(_id);
            this.parent = _parent;
        }

        public String getId() {
            return this.id;
        }

        public int index() {
            return this.index;
        }

        public void setCustom() {
            this.bCustom = true;
        }

        public boolean isCustom() {
            return this.bCustom;
        }

        public boolean isPassiv() {
            return this.passiv;
        }

        public PerkFactory.Perk getParent() {
            return this.parent;
        }

        public String getName() {
            return this.name;
        }

        public PerkFactory.Perk getType() {
            return this;
        }

        public int getXp1() {
            return this.xp1;
        }

        public int getXp2() {
            return this.xp2;
        }

        public int getXp3() {
            return this.xp3;
        }

        public int getXp4() {
            return this.xp4;
        }

        public int getXp5() {
            return this.xp5;
        }

        public int getXp6() {
            return this.xp6;
        }

        public int getXp7() {
            return this.xp7;
        }

        public int getXp8() {
            return this.xp8;
        }

        public int getXp9() {
            return this.xp9;
        }

        public int getXp10() {
            return this.xp10;
        }

        public float getXpForLevel(int level) {
            if (level == 1) {
                return this.xp1;
            } else if (level == 2) {
                return this.xp2;
            } else if (level == 3) {
                return this.xp3;
            } else if (level == 4) {
                return this.xp4;
            } else if (level == 5) {
                return this.xp5;
            } else if (level == 6) {
                return this.xp6;
            } else if (level == 7) {
                return this.xp7;
            } else if (level == 8) {
                return this.xp8;
            } else if (level == 9) {
                return this.xp9;
            } else {
                return level == 10 ? this.xp10 : -1.0F;
            }
        }

        public float getTotalXpForLevel(int level) {
            int int0 = 0;

            for (int int1 = 1; int1 <= level; int1++) {
                float float0 = this.getXpForLevel(int1);
                if (float0 != -1.0F) {
                    int0 = (int)(int0 + float0);
                }
            }

            return int0;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    public static final class Perks {
        public static final PerkFactory.Perk None = new PerkFactory.Perk("None");
        public static final PerkFactory.Perk Agility = new PerkFactory.Perk("Agility");
        public static final PerkFactory.Perk Cooking = new PerkFactory.Perk("Cooking");
        public static final PerkFactory.Perk Melee = new PerkFactory.Perk("Melee");
        public static final PerkFactory.Perk Crafting = new PerkFactory.Perk("Crafting");
        public static final PerkFactory.Perk Fitness = new PerkFactory.Perk("Fitness");
        public static final PerkFactory.Perk Strength = new PerkFactory.Perk("Strength");
        public static final PerkFactory.Perk Blunt = new PerkFactory.Perk("Blunt");
        public static final PerkFactory.Perk Axe = new PerkFactory.Perk("Axe");
        public static final PerkFactory.Perk Sprinting = new PerkFactory.Perk("Sprinting");
        public static final PerkFactory.Perk Lightfoot = new PerkFactory.Perk("Lightfoot");
        public static final PerkFactory.Perk Nimble = new PerkFactory.Perk("Nimble");
        public static final PerkFactory.Perk Sneak = new PerkFactory.Perk("Sneak");
        public static final PerkFactory.Perk Woodwork = new PerkFactory.Perk("Woodwork");
        public static final PerkFactory.Perk Aiming = new PerkFactory.Perk("Aiming");
        public static final PerkFactory.Perk Reloading = new PerkFactory.Perk("Reloading");
        public static final PerkFactory.Perk Farming = new PerkFactory.Perk("Farming");
        public static final PerkFactory.Perk Survivalist = new PerkFactory.Perk("Survivalist");
        public static final PerkFactory.Perk Fishing = new PerkFactory.Perk("Fishing");
        public static final PerkFactory.Perk Trapping = new PerkFactory.Perk("Trapping");
        public static final PerkFactory.Perk Passiv = new PerkFactory.Perk("Passiv");
        public static final PerkFactory.Perk Firearm = new PerkFactory.Perk("Firearm");
        public static final PerkFactory.Perk PlantScavenging = new PerkFactory.Perk("PlantScavenging");
        public static final PerkFactory.Perk Doctor = new PerkFactory.Perk("Doctor");
        public static final PerkFactory.Perk Electricity = new PerkFactory.Perk("Electricity");
        public static final PerkFactory.Perk Blacksmith = new PerkFactory.Perk("Blacksmith");
        public static final PerkFactory.Perk MetalWelding = new PerkFactory.Perk("MetalWelding");
        public static final PerkFactory.Perk Melting = new PerkFactory.Perk("Melting");
        public static final PerkFactory.Perk Mechanics = new PerkFactory.Perk("Mechanics");
        public static final PerkFactory.Perk Spear = new PerkFactory.Perk("Spear");
        public static final PerkFactory.Perk Maintenance = new PerkFactory.Perk("Maintenance");
        public static final PerkFactory.Perk SmallBlade = new PerkFactory.Perk("SmallBlade");
        public static final PerkFactory.Perk LongBlade = new PerkFactory.Perk("LongBlade");
        public static final PerkFactory.Perk SmallBlunt = new PerkFactory.Perk("SmallBlunt");
        public static final PerkFactory.Perk Combat = new PerkFactory.Perk("Combat");
        public static final PerkFactory.Perk Tailoring = new PerkFactory.Perk("Tailoring");
        public static final PerkFactory.Perk MAX = new PerkFactory.Perk("MAX");

        public static int getMaxIndex() {
            return MAX.index();
        }

        public static PerkFactory.Perk fromIndex(int value) {
            return value >= 0 && value <= PerkFactory.NextPerkID ? PerkFactory.PerkByIndex[value] : null;
        }

        public static PerkFactory.Perk FromString(String id) {
            return PerkFactory.PerkById.getOrDefault(id, MAX);
        }
    }
}
