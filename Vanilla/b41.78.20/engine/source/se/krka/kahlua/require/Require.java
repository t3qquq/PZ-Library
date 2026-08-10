/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.require;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.require.LuaSourceProvider;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;

public class Require
implements JavaFunction {
    private final LuaSourceProvider luaSourceProvider;

    public void install(KahluaTable kahluaTable) {
        kahluaTable.rawset("require", (Object)this);
        kahluaTable.rawset(this, new HashMap());
    }

    public Require(LuaSourceProvider luaSourceProvider) {
        this.luaSourceProvider = luaSourceProvider;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        String string;
        KahluaTable kahluaTable = luaCallFrame.getEnvironment();
        Map map = (Map)luaCallFrame.getThread().tableget(kahluaTable, this);
        Result result = (Result)map.get(string = KahluaUtil.getStringArg(luaCallFrame, 1, "require"));
        if (result == null) {
            this.setState(map, string, Result.LOADING);
            Reader reader = this.luaSourceProvider.getLuaSource(string);
            if (reader == null) {
                this.error(map, string, "Does not exist: " + string);
            }
            try {
                LuaClosure luaClosure = LuaCompiler.loadis(reader, string, kahluaTable);
                this.setState(map, string, Result.LOADING);
                luaCallFrame.getThread().call(luaClosure, null, null, null);
                this.setState(map, string, Result.LOADED);
                return 0;
            }
            catch (IOException iOException) {
                this.error(map, string, "Error in: " + string + ": " + iOException.getMessage());
            }
            catch (RuntimeException runtimeException) {
                String string2 = "Error in: " + string + ": " + runtimeException.getMessage();
                this.setState(map, string, Result.error(string2));
                throw new RuntimeException(string2, runtimeException);
            }
        }
        if (result == Result.LOADING) {
            this.error(map, string, "Circular dependency found for: " + string);
        }
        if (result.state == State.BROKEN) {
            KahluaUtil.fail(result.errorMessage);
        }
        return 0;
    }

    private void error(Map<String, Result> map, String string, String string2) {
        this.setState(map, string, Result.error(string2));
        KahluaUtil.fail(string2);
    }

    private void setState(Map<String, Result> map, String string, Result result) {
        map.put(string, result);
    }

    private static class Result {
        public final String errorMessage;
        public final State state;
        public static final Result LOADING = new Result(null, State.LOADING);
        public static final Result LOADED = new Result(null, State.LOADED);

        private Result(String string, State state) {
            this.errorMessage = string;
            this.state = state;
        }

        public static Result error(String string) {
            return new Result(string, State.BROKEN);
        }
    }

    private static enum State {
        LOADING,
        LOADED,
        BROKEN;

    }
}

