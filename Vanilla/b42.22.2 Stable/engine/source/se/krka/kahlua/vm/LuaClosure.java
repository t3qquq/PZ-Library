// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

import java.io.File;
import java.util.Objects;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.core.math.PZMath;
import zombie.gameStates.ChooseGameInfo.Mod;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public final class LuaClosure {
    public final Prototype prototype;
    public KahluaTable env;
    public final UpValue[] upvalues;
    public String debugName;
    private LuaClosure.DebugInfo debugInfo;

    public LuaClosure(Prototype prototype, KahluaTable env) {
        this.prototype = prototype;
        if (LuaCompiler.rewriteEvents) {
            LuaEventManager.reroute(prototype, this);
            MapObjects.reroute(prototype, this);
        }

        this.env = env;
        this.upvalues = new UpValue[prototype.numUpvalues];
    }

    @Override
    public String toString() {
        return this.prototype.lines.length > 0
            ? "function " + this.prototype + ":" + this.prototype.lines[0]
            : "function[" + Integer.toString(this.hashCode(), 36) + "]";
    }

    public String toString2(int line) {
        if (this.prototype.lines.length > 0) {
            if (line == 0) {
                line = 1;
            }

            if (this.isBuiltInFunction()) {
                return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[line - 1];
            }

            String sourceName = " | Vanilla";
            String val = this.prototype.filename;
            val = val.replace("/", File.separator);
            if (val.contains(File.separator + "mods" + File.separator)) {
                String dir = val.substring(0, val.indexOf(File.separator + "media"));
                String dir1 = dir.substring(0, dir.lastIndexOf(File.separator));
                Mod mod = ZomboidFileSystem.instance.getModInfoForDir(dir1);
                sourceName = " | MOD: " + mod.getName();
                KahluaTable tab = (KahluaTable)LuaManager.env.rawget("PauseBuggedModList");
                if (tab != null) {
                    tab.rawset(mod.getName(), true);
                }
            }

            return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[line - 1] + sourceName;
        } else {
            return "function[" + Integer.toString(this.hashCode(), 36) + "]";
        }
    }

    public StackTraceElement toStackTraceElement(int line) {
        return new StackTraceElement(
            this.getDebugInfo().getSourceName(),
            this.getDebugInfo().getFunctionName(),
            this.prototype.file,
            PZArrayUtil.getOrDefault(this.prototype.lines, PZMath.max(line - 1, 0), 0)
        );
    }

    private boolean isBuiltInFunction() {
        return this.prototype.lines.length > 0 && this.prototype.filename == null;
    }

    private LuaClosure.DebugInfo getDebugInfo() {
        return this.debugInfo = Objects.requireNonNullElse(this.debugInfo, new LuaClosure.DebugInfo());
    }

    private class DebugInfo {
        private String rawFileName;
        private String rawPrototypeName;
        private int rawLineCount;
        private String fileName;
        private String functionName;
        private String sourceName;
        private String modName;

        private boolean isDirty() {
            return !StringUtils.equals(this.rawFileName, this.getRawFileName())
                || !StringUtils.equals(this.rawPrototypeName, this.getRawPrototypeName())
                || this.rawLineCount != PZArrayUtil.lengthOf(LuaClosure.this.prototype.lines);
        }

        private void updateDirty() {
            if (this.isDirty()) {
                this.fileName = this.generateFileName();
                this.functionName = this.generateFunctionName();
                this.modName = this.generateModName();
                this.sourceName = "Lua(" + this.modName + ")";
                this.rawFileName = this.getRawFileName();
                this.rawPrototypeName = this.getRawPrototypeName();
                this.rawLineCount = PZArrayUtil.lengthOf(LuaClosure.this.prototype.lines);
            }
        }

        public String getSourceName() {
            this.updateDirty();
            return this.sourceName;
        }

        public String getModName() {
            this.updateDirty();
            return this.modName;
        }

        public String getFunctionName() {
            this.updateDirty();
            return this.functionName;
        }

        private String generateFileName() {
            return this.getRawFileName().replace("/", File.separator);
        }

        private String generateFunctionName() {
            if (LuaClosure.this.isBuiltInFunction()) {
                return LuaClosure.this.prototype.name;
            }

            if (PZArrayUtil.lengthOf(LuaClosure.this.prototype.lines) == 0) {
                return "function[" + Integer.toString(this.hashCode(), 36) + "]";
            }

            String functionName = LuaClosure.this.prototype.name;
            if (functionName == null) {
                functionName = this.fileName;
            }

            if (functionName.equals(this.fileName)) {
                int lastIndexOfSlash = functionName.lastIndexOf(File.separator);
                if (lastIndexOfSlash != -1) {
                    functionName = functionName.substring(lastIndexOfSlash + 1);
                }

                int lastIndexOfDot = functionName.lastIndexOf(46);
                if (lastIndexOfDot != -1) {
                    functionName = functionName.substring(0, lastIndexOfDot);
                }
            }

            return functionName;
        }

        private String generateModName() {
            if (LuaClosure.this.isBuiltInFunction()) {
                return "BuiltIn";
            }

            String sourceName = "Vanilla";
            if (this.fileName.contains(File.separator + "mods" + File.separator)) {
                String dir = this.fileName.substring(0, this.fileName.indexOf(File.separator + "media"));
                String dir1 = dir.substring(0, dir.lastIndexOf(File.separator));
                Mod mod = ZomboidFileSystem.instance.getModInfoForDir(dir1);
                sourceName = "(MOD:" + mod.getName() + ")";
                KahluaTable tab = (KahluaTable)LuaManager.env.rawget("PauseBuggedModList");
                if (tab != null) {
                    tab.rawset(mod.getName(), true);
                }
            }

            return sourceName;
        }

        private String getRawFileName() {
            return LuaClosure.this.prototype.filename != null
                ? LuaClosure.this.prototype.filename
                : Objects.requireNonNullElse(LuaClosure.this.prototype.file, "(Unknown File)");
        }

        private String getRawPrototypeName() {
            return LuaClosure.this.prototype.name;
        }
    }
}
