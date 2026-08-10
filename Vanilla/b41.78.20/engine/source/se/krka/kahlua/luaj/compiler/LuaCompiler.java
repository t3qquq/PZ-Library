/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.luaj.kahluafork.compiler.LexState
 */
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

public class LuaCompiler
implements JavaFunction {
    public static boolean rewriteEvents = false;
    private final int index;
    private static final int LOADSTRING = 0;
    private static final int LOADSTREAM = 1;
    private static final String[] names = new String[]{"loadstring", "loadstream"};
    private static final LuaCompiler[] functions = new LuaCompiler[names.length];

    private LuaCompiler(int n) {
        this.index = n;
    }

    public static void register(KahluaTable kahluaTable) {
        for (int i = 0; i < names.length; ++i) {
            kahluaTable.rawset(names[i], (Object)functions[i]);
        }
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return this.loadstring(luaCallFrame, n);
            }
            case 1: {
                return LuaCompiler.loadstream(luaCallFrame, n);
            }
        }
        return 0;
    }

    public static int loadstream(LuaCallFrame luaCallFrame, int n) {
        try {
            KahluaUtil.luaAssert(n >= 2, "not enough arguments");
            Object object = luaCallFrame.get(0);
            KahluaUtil.luaAssert(object != null, "No input given");
            String string = (String)luaCallFrame.get(1);
            if (object instanceof Reader) {
                return luaCallFrame.push(LuaCompiler.loadis((Reader)object, string, null, luaCallFrame.getEnvironment()));
            }
            if (object instanceof InputStream) {
                return luaCallFrame.push(LuaCompiler.loadis((InputStream)object, string, null, luaCallFrame.getEnvironment()));
            }
            KahluaUtil.fail("Invalid type to loadstream: " + object.getClass());
            return 0;
        }
        catch (RuntimeException runtimeException) {
            return luaCallFrame.push(null, runtimeException.getMessage());
        }
        catch (IOException iOException) {
            return luaCallFrame.push(null, iOException.getMessage());
        }
    }

    private int loadstring(LuaCallFrame luaCallFrame, int n) {
        try {
            KahluaUtil.luaAssert(n >= 1, "not enough arguments");
            String string = (String)luaCallFrame.get(0);
            KahluaUtil.luaAssert(string != null, "No source given");
            String string2 = null;
            if (n >= 2) {
                string2 = (String)luaCallFrame.get(1);
            }
            return luaCallFrame.push(LuaCompiler.loadstring(string, string2, luaCallFrame.getEnvironment()));
        }
        catch (RuntimeException runtimeException) {
            return luaCallFrame.push(null, runtimeException.getMessage());
        }
        catch (IOException iOException) {
            return luaCallFrame.push(null, iOException.getMessage());
        }
    }

    public static LuaClosure loadis(InputStream inputStream, String string, KahluaTable kahluaTable) throws IOException {
        return LuaCompiler.loadis(inputStream, string, null, kahluaTable);
    }

    public static LuaClosure loadis(Reader reader, String string, KahluaTable kahluaTable) throws IOException {
        return LuaCompiler.loadis(reader, string, null, kahluaTable);
    }

    public static LuaClosure loadstring(String string, String string2, KahluaTable kahluaTable) throws IOException {
        return LuaCompiler.loadis(new ByteArrayInputStream(string.getBytes("UTF-8")), string2, string, kahluaTable);
    }

    private static LuaClosure loadis(Reader reader, String string, String string2, KahluaTable kahluaTable) throws IOException {
        return new LuaClosure(LexState.compile((int)reader.read(), (Reader)reader, (String)string, (String)string2), kahluaTable);
    }

    private static LuaClosure loadis(InputStream inputStream, String string, String string2, KahluaTable kahluaTable) throws IOException {
        return LuaCompiler.loadis(new InputStreamReader(inputStream), string, string2, kahluaTable);
    }

    static {
        for (int i = 0; i < names.length; ++i) {
            LuaCompiler.functions[i] = new LuaCompiler(i);
        }
    }
}

