// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.util.Type;

public final class DefaultClothing {
    public static final DefaultClothing instance = new DefaultClothing();
    public final DefaultClothing.Clothing Pants = new DefaultClothing.Clothing();
    public final DefaultClothing.Clothing TShirt = new DefaultClothing.Clothing();
    public final DefaultClothing.Clothing TShirtDecal = new DefaultClothing.Clothing();
    public final DefaultClothing.Clothing Vest = new DefaultClothing.Clothing();
    public boolean m_dirty = true;

    private void checkDirty() {
        if (this.m_dirty) {
            this.m_dirty = false;
            this.init();
        }
    }

    private void init() {
        this.Pants.clear();
        this.TShirt.clear();
        this.TShirtDecal.clear();
        this.Vest.clear();
        KahluaTable table = Type.tryCastTo(LuaManager.env.rawget("DefaultClothing"), KahluaTable.class);
        if (table != null) {
            this.initClothing(table, this.Pants, "Pants");
            this.initClothing(table, this.TShirt, "TShirt");
            this.initClothing(table, this.TShirtDecal, "TShirtDecal");
            this.initClothing(table, this.Vest, "Vest");
        }
    }

    private void initClothing(KahluaTable table1, DefaultClothing.Clothing clothing, String string) {
        KahluaTable table0 = Type.tryCastTo(table1.rawget(string), KahluaTable.class);
        if (table0 != null) {
            this.tableToArrayList(table0, "hue", clothing.hue);
            this.tableToArrayList(table0, "texture", clothing.texture);
            this.tableToArrayList(table0, "tint", clothing.tint);
        }
    }

    private void tableToArrayList(KahluaTable table, String string, ArrayList<String> arrayList) {
        KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)table.rawget(string);
        if (kahluaTableImpl != null) {
            int int0 = 1;

            for (int int1 = kahluaTableImpl.len(); int0 <= int1; int0++) {
                Object object = kahluaTableImpl.rawget(int0);
                if (object != null) {
                    arrayList.add(object.toString());
                }
            }
        }
    }

    public String pickPantsHue() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.hue);
    }

    public String pickPantsTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.texture);
    }

    public String pickPantsTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Pants.tint);
    }

    public String pickTShirtTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirt.texture);
    }

    public String pickTShirtTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirt.tint);
    }

    public String pickTShirtDecalTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirtDecal.texture);
    }

    public String pickTShirtDecalTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.TShirtDecal.tint);
    }

    public String pickVestTexture() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Vest.texture);
    }

    public String pickVestTint() {
        this.checkDirty();
        return OutfitRNG.pickRandom(this.Vest.tint);
    }

    private static final class Clothing {
        final ArrayList<String> hue = new ArrayList<>();
        final ArrayList<String> texture = new ArrayList<>();
        final ArrayList<String> tint = new ArrayList<>();

        void clear() {
            this.hue.clear();
            this.texture.clear();
            this.tint.clear();
        }
    }
}
