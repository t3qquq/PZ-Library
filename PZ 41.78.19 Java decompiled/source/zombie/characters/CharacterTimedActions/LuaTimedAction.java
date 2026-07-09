// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.CharacterTimedActions;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;

public final class LuaTimedAction extends BaseAction {
    KahluaTable table;
    public static Object[] statObj = new Object[6];

    public LuaTimedAction(KahluaTable _table, IsoGameCharacter chr) {
        super(chr);
        this.table = _table;
        Object object0 = _table.rawget("maxTime");
        this.MaxTime = LuaManager.converterManager.fromLuaToJava(object0, Integer.class);
        Object object1 = _table.rawget("stopOnWalk");
        Object object2 = _table.rawget("stopOnRun");
        Object object3 = _table.rawget("stopOnAim");
        Object object4 = _table.rawget("onUpdateFunc");
        if (object1 != null) {
            this.StopOnWalk = LuaManager.converterManager.fromLuaToJava(object1, Boolean.class);
        }

        if (object2 != null) {
            this.StopOnRun = LuaManager.converterManager.fromLuaToJava(object2, Boolean.class);
        }

        if (object3 != null) {
            this.StopOnAim = LuaManager.converterManager.fromLuaToJava(object3, Boolean.class);
        }
    }

    @Override
    public void update() {
        statObj[0] = this.table.rawget("character");
        statObj[1] = this.table.rawget("param1");
        statObj[2] = this.table.rawget("param2");
        statObj[3] = this.table.rawget("param3");
        statObj[4] = this.table.rawget("param4");
        statObj[5] = this.table.rawget("param5");
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("onUpdateFunc"), statObj);
        super.update();
    }

    @Override
    public boolean valid() {
        Object[] objects = LuaManager.caller
            .pcall(
                LuaManager.thread,
                this.table.rawget("isValidFunc"),
                this.table.rawget("character"),
                this.table.rawget("param1"),
                this.table.rawget("param2"),
                this.table.rawget("param3"),
                this.table.rawget("param4"),
                this.table.rawget("param5")
            );
        return objects.length > 0 && (Boolean)objects[0];
    }

    @Override
    public void start() {
        super.start();
        this.CurrentTime = 0.0F;
        LuaManager.caller
            .pcall(
                LuaManager.thread,
                this.table.rawget("startFunc"),
                this.table.rawget("character"),
                this.table.rawget("param1"),
                this.table.rawget("param2"),
                this.table.rawget("param3"),
                this.table.rawget("param4"),
                this.table.rawget("param5")
            );
    }

    @Override
    public void stop() {
        super.stop();
        LuaManager.caller
            .pcall(
                LuaManager.thread,
                this.table.rawget("onStopFunc"),
                this.table.rawget("character"),
                this.table.rawget("param1"),
                this.table.rawget("param2"),
                this.table.rawget("param3"),
                this.table.rawget("param4"),
                this.table.rawget("param5")
            );
    }

    @Override
    public void perform() {
        super.perform();
        LuaManager.caller
            .pcall(
                LuaManager.thread,
                this.table.rawget("performFunc"),
                this.table.rawget("character"),
                this.table.rawget("param1"),
                this.table.rawget("param2"),
                this.table.rawget("param3"),
                this.table.rawget("param4"),
                this.table.rawget("param5")
            );
    }
}
