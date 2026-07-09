// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;

public final class CGlobalObjectSystem extends GlobalObjectSystem {
    public CGlobalObjectSystem(String name) {
        super(name);
    }

    @Override
    protected GlobalObject makeObject(int int0, int int1, int int2) {
        return new CGlobalObject(this, int0, int1, int2);
    }

    public void sendCommand(String command, IsoPlayer player, KahluaTable args) {
        CGlobalObjectNetwork.sendClientCommand(player, this.name, command, args);
    }

    public void receiveServerCommand(String command, KahluaTable args) {
        Object object = this.modData.rawget("OnServerCommand");
        if (object == null) {
            throw new IllegalStateException("OnServerCommand method undefined for system '" + this.name + "'");
        } else {
            LuaManager.caller.pcallvoid(LuaManager.thread, object, this.modData, command, args);
        }
    }

    public void receiveNewLuaObjectAt(int x, int y, int z, KahluaTable args) {
        Object object = this.modData.rawget("newLuaObjectAt");
        if (object == null) {
            throw new IllegalStateException("newLuaObjectAt method undefined for system '" + this.name + "'");
        } else {
            LuaManager.caller
                .pcall(LuaManager.thread, object, this.modData, BoxedStaticValues.toDouble(x), BoxedStaticValues.toDouble(y), BoxedStaticValues.toDouble(z));
            GlobalObject globalObject = this.getObjectAt(x, y, z);
            if (globalObject != null) {
                KahluaTableIterator kahluaTableIterator = args.iterator();

                while (kahluaTableIterator.advance()) {
                    globalObject.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
                }
            }
        }
    }

    public void receiveRemoveLuaObjectAt(int x, int y, int z) {
        Object object = this.modData.rawget("removeLuaObjectAt");
        if (object == null) {
            throw new IllegalStateException("removeLuaObjectAt method undefined for system '" + this.name + "'");
        } else {
            LuaManager.caller
                .pcall(LuaManager.thread, object, this.modData, BoxedStaticValues.toDouble(x), BoxedStaticValues.toDouble(y), BoxedStaticValues.toDouble(z));
        }
    }

    public void receiveUpdateLuaObjectAt(int x, int y, int z, KahluaTable args) {
        GlobalObject globalObject = this.getObjectAt(x, y, z);
        if (globalObject != null) {
            KahluaTableIterator kahluaTableIterator = args.iterator();

            while (kahluaTableIterator.advance()) {
                globalObject.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
            }

            Object object = this.modData.rawget("OnLuaObjectUpdated");
            if (object == null) {
                throw new IllegalStateException("OnLuaObjectUpdated method undefined for system '" + this.name + "'");
            } else {
                LuaManager.caller.pcall(LuaManager.thread, object, this.modData, globalObject.getModData());
            }
        }
    }

    @Override
    public void Reset() {
        super.Reset();
        this.modData.wipe();
    }
}
