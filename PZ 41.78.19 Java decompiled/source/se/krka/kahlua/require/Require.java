// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.require;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;

public class Require implements JavaFunction {
    private final LuaSourceProvider luaSourceProvider;

    public void install(KahluaTable table) {
        table.rawset("require", this);
        table.rawset(this, new HashMap());
    }

    public Require(LuaSourceProvider luaSourceProviderx) {
        this.luaSourceProvider = luaSourceProviderx;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int var2) {
        KahluaTable table = luaCallFrame.getEnvironment();
        Map map = (Map)luaCallFrame.getThread().tableget(table, this);
        String string0 = KahluaUtil.getStringArg(luaCallFrame, 1, "require");
        Require.Result result = (Require.Result)map.get(string0);
        if (result == null) {
            this.setState(map, string0, Require.Result.LOADING);
            Reader reader = this.luaSourceProvider.getLuaSource(string0);
            if (reader == null) {
                this.error(map, string0, "Does not exist: " + string0);
            }

            try {
                LuaClosure luaClosure = LuaCompiler.loadis(reader, string0, table);
                this.setState(map, string0, Require.Result.LOADING);
                luaCallFrame.getThread().call(luaClosure, null, null, null);
                this.setState(map, string0, Require.Result.LOADED);
                return 0;
            } catch (IOException iOException) {
                this.error(map, string0, "Error in: " + string0 + ": " + iOException.getMessage());
            } catch (RuntimeException runtimeException) {
                String string1 = "Error in: " + string0 + ": " + runtimeException.getMessage();
                this.setState(map, string0, Require.Result.error(string1));
                throw new RuntimeException(string1, runtimeException);
            }
        }

        if (result == Require.Result.LOADING) {
            this.error(map, string0, "Circular dependency found for: " + string0);
        }

        if (result.state == Require.State.BROKEN) {
            KahluaUtil.fail(result.errorMessage);
        }

        return 0;
    }

    private void error(Map<String, Require.Result> map, String string0, String string1) {
        this.setState(map, string0, Require.Result.error(string1));
        KahluaUtil.fail(string1);
    }

    private void setState(Map<String, Require.Result> map, String string, Require.Result result) {
        map.put(string, result);
    }

    private static class Result {
        public final String errorMessage;
        public final Require.State state;
        public static final Require.Result LOADING = new Require.Result(null, Require.State.LOADING);
        public static final Require.Result LOADED = new Require.Result(null, Require.State.LOADED);

        private Result(String string, Require.State statex) {
            this.errorMessage = string;
            this.state = statex;
        }

        public static Require.Result error(String string) {
            return new Require.Result(string, Require.State.BROKEN);
        }
    }

    private static enum State {
        LOADING,
        LOADED,
        BROKEN;
    }
}
