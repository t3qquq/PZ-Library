// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.stash;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;

public final class Stash {
    public String name;
    public String type;
    public String item;
    public String customName;
    public int buildingX;
    public int buildingY;
    public String spawnTable;
    public ArrayList<StashAnnotation> annotations;
    public boolean spawnOnlyOnZed;
    public int minDayToSpawn = -1;
    public int maxDayToSpawn = -1;
    public int minTrapToSpawn = -1;
    public int maxTrapToSpawn = -1;
    public int zombies;
    public ArrayList<StashContainer> containers;
    public int barricades;

    public Stash(String _name) {
        this.name = _name;
    }

    public void load(KahluaTableImpl stashDesc) {
        this.type = stashDesc.rawgetStr("type");
        this.item = stashDesc.rawgetStr("item");
        StashBuilding stashBuilding = new StashBuilding(this.name, stashDesc.rawgetInt("buildingX"), stashDesc.rawgetInt("buildingY"));
        StashSystem.possibleStashes.add(stashBuilding);
        this.buildingX = stashBuilding.buildingX;
        this.buildingY = stashBuilding.buildingY;
        this.spawnTable = stashDesc.rawgetStr("spawnTable");
        this.customName = Translator.getText(stashDesc.rawgetStr("customName"));
        this.zombies = stashDesc.rawgetInt("zombies");
        this.barricades = stashDesc.rawgetInt("barricades");
        this.spawnOnlyOnZed = stashDesc.rawgetBool("spawnOnlyOnZed");
        String string0 = stashDesc.rawgetStr("daysToSpawn");
        if (string0 != null) {
            String[] strings0 = string0.split("-");
            if (strings0.length == 2) {
                this.minDayToSpawn = Integer.parseInt(strings0[0]);
                this.maxDayToSpawn = Integer.parseInt(strings0[1]);
            } else {
                this.minDayToSpawn = Integer.parseInt(strings0[0]);
            }
        }

        String string1 = stashDesc.rawgetStr("traps");
        if (string1 != null) {
            String[] strings1 = string1.split("-");
            if (strings1.length == 2) {
                this.minTrapToSpawn = Integer.parseInt(strings1[0]);
                this.maxTrapToSpawn = Integer.parseInt(strings1[1]);
            } else {
                this.minTrapToSpawn = Integer.parseInt(strings1[0]);
                this.maxTrapToSpawn = this.minTrapToSpawn;
            }
        }

        KahluaTable table0 = (KahluaTable)stashDesc.rawget("containers");
        if (table0 != null) {
            this.containers = new ArrayList<>();
            KahluaTableIterator kahluaTableIterator0 = table0.iterator();

            while (kahluaTableIterator0.advance()) {
                KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)kahluaTableIterator0.getValue();
                StashContainer stashContainer = new StashContainer(
                    kahluaTableImpl0.rawgetStr("room"), kahluaTableImpl0.rawgetStr("containerSprite"), kahluaTableImpl0.rawgetStr("containerType")
                );
                stashContainer.contX = kahluaTableImpl0.rawgetInt("contX");
                stashContainer.contY = kahluaTableImpl0.rawgetInt("contY");
                stashContainer.contZ = kahluaTableImpl0.rawgetInt("contZ");
                stashContainer.containerItem = kahluaTableImpl0.rawgetStr("containerItem");
                if (stashContainer.containerItem != null && ScriptManager.instance.getItem(stashContainer.containerItem) == null) {
                    DebugLog.General.error("Stash containerItem \"%s\" doesn't exist.", stashContainer.containerItem);
                }

                this.containers.add(stashContainer);
            }
        }

        if ("Map".equals(this.type)) {
            KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)stashDesc.rawget("annotations");
            if (kahluaTableImpl1 != null) {
                this.annotations = new ArrayList<>();
                KahluaTableIterator kahluaTableIterator1 = kahluaTableImpl1.iterator();

                while (kahluaTableIterator1.advance()) {
                    KahluaTable table1 = (KahluaTable)kahluaTableIterator1.getValue();
                    StashAnnotation stashAnnotation = new StashAnnotation();
                    stashAnnotation.fromLua(table1);
                    this.annotations.add(stashAnnotation);
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getItem() {
        return this.item;
    }

    public int getBuildingX() {
        return this.buildingX;
    }

    public int getBuildingY() {
        return this.buildingY;
    }
}
