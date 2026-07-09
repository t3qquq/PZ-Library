// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.CharacterTimedActions;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.characters.IsoGameCharacter;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class LuaTimedActionNew extends BaseAction implements IPathfinder {
    KahluaTable table;

    public LuaTimedActionNew(KahluaTable _table, IsoGameCharacter chr) {
        super(chr);
        this.table = _table;
        Object object0 = _table.rawget("maxTime");
        this.MaxTime = LuaManager.converterManager.fromLuaToJava(object0, Integer.class);
        Object object1 = _table.rawget("stopOnWalk");
        Object object2 = _table.rawget("stopOnRun");
        Object object3 = _table.rawget("stopOnAim");
        Object object4 = _table.rawget("caloriesModifier");
        Object object5 = _table.rawget("useProgressBar");
        Object object6 = _table.rawget("forceProgressBar");
        Object object7 = _table.rawget("loopedAction");
        if (object1 != null) {
            this.StopOnWalk = LuaManager.converterManager.fromLuaToJava(object1, Boolean.class);
        }

        if (object2 != null) {
            this.StopOnRun = LuaManager.converterManager.fromLuaToJava(object2, Boolean.class);
        }

        if (object3 != null) {
            this.StopOnAim = LuaManager.converterManager.fromLuaToJava(object3, Boolean.class);
        }

        if (object4 != null) {
            this.caloriesModifier = LuaManager.converterManager.fromLuaToJava(object4, Float.class);
        }

        if (object5 != null) {
            this.UseProgressBar = LuaManager.converterManager.fromLuaToJava(object5, Boolean.class);
        }

        if (object6 != null) {
            this.ForceProgressBar = LuaManager.converterManager.fromLuaToJava(object6, Boolean.class);
        }

        if (object7 != null) {
            this.loopAction = LuaManager.converterManager.fromLuaToJava(object7, Boolean.class);
        }
    }

    @Override
    public void waitToStart() {
        Boolean boolean0 = LuaManager.caller.protectedCallBoolean(LuaManager.thread, this.table.rawget("waitToStart"), this.table);
        if (boolean0 == Boolean.FALSE) {
            super.waitToStart();
        }
    }

    @Override
    public void update() {
        super.update();
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("update"), this.table);
    }

    @Override
    public boolean valid() {
        Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("isValid"), this.table);
        return objects.length > 1 && objects[1] instanceof Boolean && (Boolean)objects[1];
    }

    @Override
    public void start() {
        super.start();
        this.CurrentTime = 0.0F;
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("start"), this.table);
    }

    @Override
    public void stop() {
        super.stop();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("stop"), this.table);
    }

    @Override
    public void perform() {
        super.perform();
        LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("perform"), this.table);
    }

    @Override
    public void Failed(Mover mover) {
        this.table.rawset("path", null);
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("failedPathfind"), this.table);
    }

    @Override
    public void Succeeded(Path path, Mover mover) {
        this.table.rawset("path", path);
        LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("succeededPathfind"), this.table);
    }

    public void Pathfind(IsoGameCharacter chr, int x, int y, int z) {
    }

    @Override
    public String getName() {
        return "timedActionPathfind";
    }

    public void setCurrentTime(float time) {
        this.CurrentTime = PZMath.clamp(time, 0.0F, (float)this.MaxTime);
    }

    public void setTime(int maxTime) {
        this.MaxTime = maxTime;
    }

    @Override
    public void OnAnimEvent(AnimEvent event) {
        Object object = this.table.rawget("animEvent");
        if (object != null) {
            LuaManager.caller.pcallvoid(LuaManager.thread, object, this.table, event.m_EventName, event.m_ParameterValue);
        }
    }

    public String getMetaType() {
        return this.table != null && this.table.getMetatable() != null ? this.table.getMetatable().getString("Type") : "";
    }
}
