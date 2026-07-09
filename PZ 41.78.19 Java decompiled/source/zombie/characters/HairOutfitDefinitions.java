// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import java.util.Arrays;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class HairOutfitDefinitions {
    public static final HairOutfitDefinitions instance = new HairOutfitDefinitions();
    public boolean m_dirty = true;
    public String hairStyle;
    public int minWorldAge;
    public final ArrayList<HairOutfitDefinitions.HaircutDefinition> m_haircutDefinition = new ArrayList<>();
    public final ArrayList<HairOutfitDefinitions.HaircutOutfitDefinition> m_outfitDefinition = new ArrayList<>();
    private final ThreadLocal<ArrayList<HairStyle>> m_tempHairStyles = ThreadLocal.withInitial(ArrayList::new);

    public void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }

    private void init() {
        this.m_haircutDefinition.clear();
        this.m_outfitDefinition.clear();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("HairOutfitDefinitions");
        if (kahluaTableImpl0 != null) {
            KahluaTableImpl kahluaTableImpl1 = Type.tryCastTo(kahluaTableImpl0.rawget("haircutDefinition"), KahluaTableImpl.class);
            if (kahluaTableImpl1 != null) {
                KahluaTableIterator kahluaTableIterator = kahluaTableImpl1.iterator();

                while (kahluaTableIterator.advance()) {
                    KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
                    if (kahluaTableImpl2 != null) {
                        HairOutfitDefinitions.HaircutDefinition haircutDefinition = new HairOutfitDefinitions.HaircutDefinition(
                            kahluaTableImpl2.rawgetStr("name"),
                            kahluaTableImpl2.rawgetInt("minWorldAge"),
                            new ArrayList<>(Arrays.asList(kahluaTableImpl2.rawgetStr("onlyFor").split(",")))
                        );
                        this.m_haircutDefinition.add(haircutDefinition);
                    }
                }

                KahluaTableImpl kahluaTableImpl3 = Type.tryCastTo(kahluaTableImpl0.rawget("haircutOutfitDefinition"), KahluaTableImpl.class);
                if (kahluaTableImpl3 != null) {
                    kahluaTableIterator = kahluaTableImpl3.iterator();

                    while (kahluaTableIterator.advance()) {
                        KahluaTableImpl kahluaTableImpl4 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
                        if (kahluaTableImpl4 != null) {
                            HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = new HairOutfitDefinitions.HaircutOutfitDefinition(
                                kahluaTableImpl4.rawgetStr("outfit"),
                                initStringChance(kahluaTableImpl4.rawgetStr("haircut")),
                                initStringChance(kahluaTableImpl4.rawgetStr("beard")),
                                initStringChance(kahluaTableImpl4.rawgetStr("haircutColor"))
                            );
                            this.m_outfitDefinition.add(haircutOutfitDefinition);
                        }
                    }
                }
            }
        }
    }

    public boolean isHaircutValid(String string0, String string1) {
        instance.checkDirty();
        if (StringUtils.isNullOrEmpty(string0)) {
            return true;
        } else {
            for (int int0 = 0; int0 < instance.m_haircutDefinition.size(); int0++) {
                HairOutfitDefinitions.HaircutDefinition haircutDefinition = instance.m_haircutDefinition.get(int0);
                if (haircutDefinition.hairStyle.equals(string1)) {
                    if (!haircutDefinition.onlyFor.contains(string0)) {
                        return false;
                    }

                    if (IsoWorld.instance.getWorldAgeDays() < haircutDefinition.minWorldAge) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public void getValidHairStylesForOutfit(String string, ArrayList<HairStyle> arrayList1, ArrayList<HairStyle> arrayList0) {
        arrayList0.clear();

        for (int int0 = 0; int0 < arrayList1.size(); int0++) {
            HairStyle hairStylex = (HairStyle)arrayList1.get(int0);
            if (!hairStylex.isNoChoose() && this.isHaircutValid(string, hairStylex.name)) {
                arrayList0.add(hairStylex);
            }
        }
    }

    public String getRandomHaircut(String string0, ArrayList<HairStyle> arrayList1) {
        ArrayList arrayList0 = this.m_tempHairStyles.get();
        this.getValidHairStylesForOutfit(string0, arrayList1, arrayList0);
        if (arrayList0.isEmpty()) {
            return "";
        } else {
            String string1 = OutfitRNG.pickRandom(arrayList0).name;
            boolean boolean0 = false;

            for (int int0 = 0; int0 < instance.m_outfitDefinition.size() && !boolean0; int0++) {
                HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = instance.m_outfitDefinition.get(int0);
                if (haircutOutfitDefinition.outfit.equals(string0) && haircutOutfitDefinition.haircutChance != null) {
                    float float0 = OutfitRNG.Next(0.0F, 100.0F);
                    float float1 = 0.0F;

                    for (int int1 = 0; int1 < haircutOutfitDefinition.haircutChance.size(); int1++) {
                        HairOutfitDefinitions.StringChance stringChance = haircutOutfitDefinition.haircutChance.get(int1);
                        float1 += stringChance.chance;
                        if (float0 < float1) {
                            string1 = stringChance.str;
                            if ("null".equalsIgnoreCase(stringChance.str)) {
                                string1 = "";
                            }

                            if ("random".equalsIgnoreCase(stringChance.str)) {
                                string1 = OutfitRNG.pickRandom(arrayList0).name;
                            }

                            boolean0 = true;
                            break;
                        }
                    }
                }
            }

            return string1;
        }
    }

    public ImmutableColor getRandomHaircutColor(String string1) {
        ImmutableColor immutableColor = SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
        String string0 = null;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < instance.m_outfitDefinition.size() && !boolean0; int0++) {
            HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = instance.m_outfitDefinition.get(int0);
            if (haircutOutfitDefinition.outfit.equals(string1) && haircutOutfitDefinition.haircutColor != null) {
                float float0 = OutfitRNG.Next(0.0F, 100.0F);
                float float1 = 0.0F;

                for (int int1 = 0; int1 < haircutOutfitDefinition.haircutColor.size(); int1++) {
                    HairOutfitDefinitions.StringChance stringChance = haircutOutfitDefinition.haircutColor.get(int1);
                    float1 += stringChance.chance;
                    if (float0 < float1) {
                        string0 = stringChance.str;
                        if ("random".equalsIgnoreCase(stringChance.str)) {
                            immutableColor = SurvivorDesc.HairCommonColors.get(OutfitRNG.Next(SurvivorDesc.HairCommonColors.size()));
                            string0 = null;
                        }

                        boolean0 = true;
                        break;
                    }
                }
            }
        }

        if (!StringUtils.isNullOrEmpty(string0)) {
            String[] strings = string0.split(",");
            immutableColor = new ImmutableColor(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), Float.parseFloat(strings[2]));
        }

        return immutableColor;
    }

    public String getRandomBeard(String string1, ArrayList<BeardStyle> arrayList) {
        String string0 = OutfitRNG.pickRandom(arrayList).name;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < instance.m_outfitDefinition.size() && !boolean0; int0++) {
            HairOutfitDefinitions.HaircutOutfitDefinition haircutOutfitDefinition = instance.m_outfitDefinition.get(int0);
            if (haircutOutfitDefinition.outfit.equals(string1) && haircutOutfitDefinition.beardChance != null) {
                float float0 = OutfitRNG.Next(0.0F, 100.0F);
                float float1 = 0.0F;

                for (int int1 = 0; int1 < haircutOutfitDefinition.beardChance.size(); int1++) {
                    HairOutfitDefinitions.StringChance stringChance = haircutOutfitDefinition.beardChance.get(int1);
                    float1 += stringChance.chance;
                    if (float0 < float1) {
                        string0 = stringChance.str;
                        if ("null".equalsIgnoreCase(stringChance.str)) {
                            string0 = "";
                        }

                        if ("random".equalsIgnoreCase(stringChance.str)) {
                            string0 = OutfitRNG.pickRandom(arrayList).name;
                        }

                        boolean0 = true;
                        break;
                    }
                }
            }
        }

        return string0;
    }

    private static ArrayList<HairOutfitDefinitions.StringChance> initStringChance(String string0) {
        if (StringUtils.isNullOrWhitespace(string0)) {
            return null;
        } else {
            ArrayList arrayList = new ArrayList();
            String[] strings0 = string0.split(";");
            int int0 = 0;

            for (String string1 : strings0) {
                String[] strings1 = string1.split(":");
                HairOutfitDefinitions.StringChance stringChance0 = new HairOutfitDefinitions.StringChance();
                stringChance0.str = strings1[0];
                stringChance0.chance = Float.parseFloat(strings1[1]);
                int0 = (int)(int0 + stringChance0.chance);
                arrayList.add(stringChance0);
            }

            if (int0 < 100) {
                HairOutfitDefinitions.StringChance stringChance1 = new HairOutfitDefinitions.StringChance();
                stringChance1.str = "random";
                stringChance1.chance = 100 - int0;
                arrayList.add(stringChance1);
            }

            return arrayList;
        }
    }

    public static final class HaircutDefinition {
        public String hairStyle;
        public int minWorldAge;
        public ArrayList<String> onlyFor;

        public HaircutDefinition(String string, int int0, ArrayList<String> arrayList) {
            this.hairStyle = string;
            this.minWorldAge = int0;
            this.onlyFor = arrayList;
        }
    }

    public static final class HaircutOutfitDefinition {
        public String outfit;
        public ArrayList<HairOutfitDefinitions.StringChance> haircutChance;
        public ArrayList<HairOutfitDefinitions.StringChance> beardChance;
        public ArrayList<HairOutfitDefinitions.StringChance> haircutColor;

        public HaircutOutfitDefinition(
            String string,
            ArrayList<HairOutfitDefinitions.StringChance> arrayList0,
            ArrayList<HairOutfitDefinitions.StringChance> arrayList1,
            ArrayList<HairOutfitDefinitions.StringChance> arrayList2
        ) {
            this.outfit = string;
            this.haircutChance = arrayList0;
            this.beardChance = arrayList1;
            this.haircutColor = arrayList2;
        }
    }

    private static final class StringChance {
        String str;
        float chance;
    }
}
