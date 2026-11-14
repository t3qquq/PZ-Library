// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.luaj.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.luaj.kahluafork.compiler.LexState;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;

public class LuaCompiler implements JavaFunction {
    public static boolean rewriteEvents;
    private final int index;
    private static final int LOADSTRING = 0;
    private static final int LOADSTREAM = 1;
    private static final String[] names = new String[]{"loadstring", "loadstream"};
    private static final LuaCompiler[] functions = new LuaCompiler[names.length];

    private LuaCompiler(int index) {
        this.index = index;
    }

    public static void register(KahluaTable env) {
        for (int i = 0; i < names.length; i++) {
            env.rawset(names[i], functions[i]);
        }
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        switch (this.index) {
            case 0:
                return this.loadstring(callFrame, nArguments);
            case 1:
                return loadstream(callFrame, nArguments);
            default:
                return 0;
        }
    }

    public static int loadstream(LuaCallFrame callFrame, int nArguments) {
        try {
            KahluaUtil.luaAssert(nArguments >= 2, "not enough arguments");
            Object input = callFrame.get(0);
            KahluaUtil.luaAssert(input != null, "No input given");
            String name = (String)callFrame.get(1);
            if (input instanceof Reader reader) {
                return callFrame.push(loadis(reader, name, null, callFrame.getEnvironment()));
            } else if (input instanceof InputStream inputStream) {
                return callFrame.push(loadis(inputStream, name, null, callFrame.getEnvironment()));
            } else {
                KahluaUtil.fail("Invalid type to loadstream: " + input.getClass());
                return 0;
            }
        } catch (RuntimeException | IOException e) {
            return callFrame.push(null, e.getMessage());
        }
    }

    private int loadstring(LuaCallFrame callFrame, int nArguments) {
        try {
            KahluaUtil.luaAssert(nArguments >= 1, "not enough arguments");
            String source = (String)callFrame.get(0);
            KahluaUtil.luaAssert(source != null, "No source given");
            String name = null;
            if (nArguments >= 2) {
                name = (String)callFrame.get(1);
            }

            return callFrame.push(loadstring(source, name, callFrame.getEnvironment()));
        } catch (RuntimeException | IOException e) {
            return callFrame.push(null, e.getMessage());
        }
    }

    public static LuaClosure loadis(InputStream inputStream, String name, KahluaTable environment) throws IOException {
        return loadis(inputStream, name, null, environment);
    }

    public static LuaClosure loadis(Reader reader, String name, KahluaTable environment) throws IOException {
        return loadis(reader, name, null, environment);
    }

    public static LuaClosure loadstring(String source, String name, KahluaTable environment) throws IOException {
        return loadis(new ByteArrayInputStream(source.getBytes("UTF-8")), name, source, environment);
    }

    private static LuaClosure loadis(Reader reader, String name, String source, KahluaTable environment) throws IOException {
        return new LuaClosure(LexState.compile(reader.read(), reader, name, source), environment);
    }

    private static LuaClosure loadis(InputStream inputStream, String name, String source, KahluaTable environment) throws IOException {
        return loadis(new InputStreamReader(inputStream), name, source, environment);
    }

    static {
        for (int i = 0; i < names.length; i++) {
            functions[i] = new LuaCompiler(i);
        }
    }
}
