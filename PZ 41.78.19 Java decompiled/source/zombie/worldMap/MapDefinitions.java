// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class MapDefinitions {
    private static MapDefinitions instance;
    private final ArrayList<String> m_definitions = new ArrayList<>();

    public static MapDefinitions getInstance() {
        if (instance == null) {
            instance = new MapDefinitions();
        }

        return instance;
    }

    public String pickRandom() {
        if (this.m_definitions.isEmpty()) {
            this.initDefinitionsFromLua();
        }

        return this.m_definitions.isEmpty() ? "Default" : PZArrayUtil.pickRandom(this.m_definitions);
    }

    private void initDefinitionsFromLua() {
        KahluaTable table0 = Type.tryCastTo(LuaManager.env.rawget("LootMaps"), KahluaTable.class);
        if (table0 != null) {
            KahluaTable table1 = Type.tryCastTo(table0.rawget("Init"), KahluaTable.class);
            if (table1 != null) {
                KahluaTableIterator kahluaTableIterator = table1.iterator();

                while (kahluaTableIterator.advance()) {
                    String string = Type.tryCastTo(kahluaTableIterator.getKey(), String.class);
                    if (string != null) {
                        this.m_definitions.add(string);
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.m_definitions.clear();
            instance = null;
        }
    }
}
