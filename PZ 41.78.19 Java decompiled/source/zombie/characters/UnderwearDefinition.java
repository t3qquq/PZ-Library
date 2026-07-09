// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;

public class UnderwearDefinition {
    public static final UnderwearDefinition instance = new UnderwearDefinition();
    public boolean m_dirty = true;
    private static final ArrayList<UnderwearDefinition.OutfitUnderwearDefinition> m_outfitDefinition = new ArrayList<>();
    private static int baseChance = 50;

    public void checkDirty() {
        this.init();
    }

    private void init() {
        m_outfitDefinition.clear();
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("UnderwearDefinition");
        if (kahluaTableImpl0 != null) {
            baseChance = kahluaTableImpl0.rawgetInt("baseChance");
            KahluaTableIterator kahluaTableIterator0 = kahluaTableImpl0.iterator();

            while (kahluaTableIterator0.advance()) {
                ArrayList arrayList = null;
                KahluaTableImpl kahluaTableImpl1 = Type.tryCastTo(kahluaTableIterator0.getValue(), KahluaTableImpl.class);
                if (kahluaTableImpl1 != null) {
                    KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(kahluaTableImpl1.rawget("top"), KahluaTableImpl.class);
                    if (kahluaTableImpl2 != null) {
                        arrayList = new ArrayList();
                        KahluaTableIterator kahluaTableIterator1 = kahluaTableImpl2.iterator();

                        while (kahluaTableIterator1.advance()) {
                            KahluaTableImpl kahluaTableImpl3 = Type.tryCastTo(kahluaTableIterator1.getValue(), KahluaTableImpl.class);
                            if (kahluaTableImpl3 != null) {
                                arrayList.add(new UnderwearDefinition.StringChance(kahluaTableImpl3.rawgetStr("name"), kahluaTableImpl3.rawgetFloat("chance")));
                            }
                        }
                    }

                    UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition = new UnderwearDefinition.OutfitUnderwearDefinition(
                        arrayList, kahluaTableImpl1.rawgetStr("bottom"), kahluaTableImpl1.rawgetInt("chanceToSpawn"), kahluaTableImpl1.rawgetStr("gender")
                    );
                    m_outfitDefinition.add(outfitUnderwearDefinition);
                }
            }
        }
    }

    public static void addRandomUnderwear(IsoZombie zombie0) {
        instance.checkDirty();
        if (Rand.Next(100) <= baseChance) {
            ArrayList arrayList = new ArrayList();
            int int0 = 0;

            for (int int1 = 0; int1 < m_outfitDefinition.size(); int1++) {
                UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition0 = m_outfitDefinition.get(int1);
                if (zombie0.isFemale() && outfitUnderwearDefinition0.female || !zombie0.isFemale() && !outfitUnderwearDefinition0.female) {
                    arrayList.add(outfitUnderwearDefinition0);
                    int0 += outfitUnderwearDefinition0.chanceToSpawn;
                }
            }

            int int2 = OutfitRNG.Next(int0);
            UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition1 = null;
            int int3 = 0;

            for (int int4 = 0; int4 < arrayList.size(); int4++) {
                UnderwearDefinition.OutfitUnderwearDefinition outfitUnderwearDefinition2 = (UnderwearDefinition.OutfitUnderwearDefinition)arrayList.get(int4);
                int3 += outfitUnderwearDefinition2.chanceToSpawn;
                if (int2 < int3) {
                    outfitUnderwearDefinition1 = outfitUnderwearDefinition2;
                    break;
                }
            }

            if (outfitUnderwearDefinition1 != null) {
                Item item = ScriptManager.instance.FindItem(outfitUnderwearDefinition1.bottom);
                ItemVisual itemVisual0 = null;
                if (item != null) {
                    itemVisual0 = zombie0.getHumanVisual().addClothingItem(zombie0.getItemVisuals(), item);
                }

                if (outfitUnderwearDefinition1.top != null) {
                    String string = null;
                    int2 = OutfitRNG.Next(outfitUnderwearDefinition1.topTotalChance);
                    int3 = 0;

                    for (int int5 = 0; int5 < outfitUnderwearDefinition1.top.size(); int5++) {
                        UnderwearDefinition.StringChance stringChance = outfitUnderwearDefinition1.top.get(int5);
                        int3 = (int)(int3 + stringChance.chance);
                        if (int2 < int3) {
                            string = stringChance.str;
                            break;
                        }
                    }

                    if (string != null) {
                        item = ScriptManager.instance.FindItem(string);
                        if (item != null) {
                            ItemVisual itemVisual1 = zombie0.getHumanVisual().addClothingItem(zombie0.getItemVisuals(), item);
                            if (Rand.Next(100) < 60 && itemVisual1 != null && itemVisual0 != null) {
                                itemVisual1.setTint(itemVisual0.getTint());
                            }
                        }
                    }
                }
            }
        }
    }

    public static final class OutfitUnderwearDefinition {
        public ArrayList<UnderwearDefinition.StringChance> top;
        public int topTotalChance = 0;
        public String bottom;
        public int chanceToSpawn;
        public boolean female = false;

        public OutfitUnderwearDefinition(ArrayList<UnderwearDefinition.StringChance> arrayList, String string0, int int1, String string1) {
            this.top = arrayList;
            if (arrayList != null) {
                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    this.topTotalChance = (int)(this.topTotalChance + ((UnderwearDefinition.StringChance)arrayList.get(int0)).chance);
                }
            }

            this.bottom = string0;
            this.chanceToSpawn = int1;
            if ("female".equals(string1)) {
                this.female = true;
            }
        }
    }

    private static final class StringChance {
        String str;
        float chance;

        public StringChance(String string, float float0) {
            this.str = string;
            this.chance = float0;
        }
    }
}
