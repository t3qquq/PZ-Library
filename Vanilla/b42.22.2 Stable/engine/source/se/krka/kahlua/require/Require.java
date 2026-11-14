// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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

    public void install(KahluaTable environment) {
        environment.rawset("require", this);
        environment.rawset(this, new HashMap());
    }

    public Require(LuaSourceProvider luaSourceProvider) {
        this.luaSourceProvider = luaSourceProvider;
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        KahluaTable env = callFrame.getEnvironment();
        Map<String, Require.Result> states = (Map<String, Require.Result>)callFrame.getThread().tableget(env, this);
        String path = KahluaUtil.getStringArg(callFrame, 1, "require");
        Require.Result result = states.get(path);
        if (result == null) {
            this.setState(states, path, Require.Result.LOADING);
            Reader source = this.luaSourceProvider.getLuaSource(path);
            if (source == null) {
                this.error(states, path, "Does not exist: " + path);
            }

            try {
                LuaClosure luaClosure = LuaCompiler.loadis(source, path, env);
                this.setState(states, path, Require.Result.LOADING);
                callFrame.getThread().call(luaClosure, null, null, null);
                this.setState(states, path, Require.Result.LOADED);
                return 0;
            } catch (IOException e) {
                this.error(states, path, "Error in: " + path + ": " + e.getMessage());
            } catch (RuntimeException e) {
                String msg = "Error in: " + path + ": " + e.getMessage();
                this.setState(states, path, Require.Result.error(msg));
                throw new RuntimeException(msg, e);
            }
        }

        if (result == Require.Result.LOADING) {
            this.error(states, path, "Circular dependency found for: " + path);
        }

        if (result.state == Require.State.BROKEN) {
            KahluaUtil.fail(result.errorMessage);
        }

        return 0;
    }

    private void error(Map<String, Require.Result> states, String path, String s) {
        this.setState(states, path, Require.Result.error(s));
        KahluaUtil.fail(s);
    }

    private void setState(Map<String, Require.Result> requireLookuptable, String path, Require.Result result) {
        requireLookuptable.put(path, result);
    }

    private static class Result {
        public final String errorMessage;
        public final Require.State state;
        public static final Require.Result LOADING = new Require.Result(null, Require.State.LOADING);
        public static final Require.Result LOADED = new Require.Result(null, Require.State.LOADED);

        private Result(String errorMessage, Require.State state) {
            this.errorMessage = errorMessage;
            this.state = state;
        }

        public static Require.Result error(String s) {
            return new Require.Result(s, Require.State.BROKEN);
        }
    }

    private enum State {
        LOADING,
        LOADED,
        BROKEN;
    }
}
