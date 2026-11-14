// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.require;

import java.io.Reader;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;

public class Loadfile implements JavaFunction {
    private final LuaSourceProvider luaSourceProvider;

    public void install(KahluaTable environment) {
        environment.rawset("loadfile", this);
    }

    public Loadfile(LuaSourceProvider luaSourceProvider) {
        this.luaSourceProvider = luaSourceProvider;
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        String path = KahluaUtil.getStringArg(callFrame, 1, "loadfile");
        Reader source = this.luaSourceProvider.getLuaSource(path);
        if (source == null) {
            callFrame.pushNil();
            callFrame.push("Does not exist: " + path);
            return 2;
        } else {
            callFrame.setTop(2);
            callFrame.set(0, source);
            callFrame.set(1, path);
            return LuaCompiler.loadstream(callFrame, 2);
        }
    }
}
