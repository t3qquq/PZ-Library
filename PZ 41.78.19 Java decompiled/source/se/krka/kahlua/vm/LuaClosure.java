// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.io.File;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.gameStates.ChooseGameInfo;

public final class LuaClosure {
    public Prototype prototype;
    public KahluaTable env;
    public UpValue[] upvalues;
    public String debugName;

    public LuaClosure(Prototype arg0, KahluaTable arg1) {
        this.prototype = arg0;
        if (LuaCompiler.rewriteEvents) {
            LuaEventManager.reroute(arg0, this);
            MapObjects.reroute(arg0, this);
        }

        this.env = arg1;
        this.upvalues = new UpValue[arg0.numUpvalues];
    }

    @Override
    public String toString() {
        return this.prototype.lines.length > 0
            ? "function " + this.prototype.toString() + ":" + this.prototype.lines[0]
            : "function[" + Integer.toString(this.hashCode(), 36) + "]";
    }

    public String toString2(int arg0) {
        if (this.prototype.lines.length > 0) {
            if (arg0 == 0) {
                arg0 = 1;
            }

            if (this.prototype.filename == null) {
                return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[arg0 - 1];
            } else {
                String string0 = " | Vanilla";
                String string1 = this.prototype.filename;
                string1 = string1.replace("/", File.separator);
                if (string1.contains(File.separator + "mods" + File.separator)) {
                    String string2 = string1.substring(0, string1.indexOf(File.separator + "media"));
                    ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.getModInfoForDir(string2);
                    string0 = " | MOD: " + mod.getName();
                    KahluaTable table = (KahluaTable)LuaManager.env.rawget("PauseBuggedModList");
                    if (table != null) {
                        table.rawset(mod.getName(), true);
                    }
                }

                return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[arg0 - 1] + string0;
            }
        } else {
            return "function[" + Integer.toString(this.hashCode(), 36) + "]";
        }
    }
}
