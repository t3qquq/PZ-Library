// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;

public final class SGlobalObject extends GlobalObject {
    private static KahluaTable tempTable;

    SGlobalObject(SGlobalObjectSystem sGlobalObjectSystem, int int0, int int1, int int2) {
        super(sGlobalObjectSystem, int0, int1, int2);
    }

    public void load(ByteBuffer bb, int WorldVersion) throws IOException {
        boolean boolean0 = bb.get() == 0;
        if (!boolean0) {
            this.modData.load(bb, WorldVersion);
        }
    }

    public void save(ByteBuffer bb) throws IOException {
        bb.putInt(this.x);
        bb.putInt(this.y);
        bb.put((byte)this.z);
        if (tempTable == null) {
            tempTable = LuaManager.platform.newTable();
        }

        tempTable.wipe();
        KahluaTableIterator kahluaTableIterator = this.modData.iterator();

        while (kahluaTableIterator.advance()) {
            Object object = kahluaTableIterator.getKey();
            if (((SGlobalObjectSystem)this.system).objectModDataKeys.contains(object)) {
                tempTable.rawset(object, this.modData.rawget(object));
            }
        }

        if (tempTable.isEmpty()) {
            bb.put((byte)0);
        } else {
            bb.put((byte)1);
            tempTable.save(bb);
            tempTable.wipe();
        }
    }
}
