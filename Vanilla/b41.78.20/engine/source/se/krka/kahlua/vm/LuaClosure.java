/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.Lua.LuaEventManager
 *  zombie.Lua.LuaManager
 *  zombie.Lua.MapObjects
 *  zombie.ZomboidFileSystem
 *  zombie.gameStates.ChooseGameInfo$Mod
 */
package se.krka.kahlua.vm;

import java.io.File;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Prototype;
import se.krka.kahlua.vm.UpValue;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.ZomboidFileSystem;
import zombie.gameStates.ChooseGameInfo;

public final class LuaClosure {
    public Prototype prototype;
    public KahluaTable env;
    public UpValue[] upvalues;
    public String debugName;

    public LuaClosure(Prototype prototype, KahluaTable kahluaTable) {
        this.prototype = prototype;
        if (LuaCompiler.rewriteEvents) {
            LuaEventManager.reroute((Prototype)prototype, (LuaClosure)this);
            MapObjects.reroute((Prototype)prototype, (LuaClosure)this);
        }
        this.env = kahluaTable;
        this.upvalues = new UpValue[prototype.numUpvalues];
    }

    public String toString() {
        if (this.prototype.lines.length > 0) {
            return "function " + this.prototype.toString() + ":" + this.prototype.lines[0];
        }
        return "function[" + Integer.toString(this.hashCode(), 36) + "]";
    }

    public String toString2(int n) {
        if (this.prototype.lines.length > 0) {
            if (n == 0) {
                n = 1;
            }
            if (this.prototype.filename == null) {
                return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[n - 1];
            }
            Object object = " | Vanilla";
            String string = this.prototype.filename;
            if ((string = string.replace("/", File.separator)).contains(File.separator + "mods" + File.separator)) {
                String string2 = string.substring(0, string.indexOf(File.separator + "media"));
                ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.getModInfoForDir(string2);
                object = " | MOD: " + mod.getName();
                KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("PauseBuggedModList");
                if (kahluaTable != null) {
                    kahluaTable.rawset(mod.getName(), (Object)true);
                }
            }
            return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[n - 1] + (String)object;
        }
        return "function[" + Integer.toString(this.hashCode(), 36) + "]";
    }
}

