// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
    public static boolean rewriteEvents = false;
    private final int index;
    private static final int LOADSTRING = 0;
    private static final int LOADSTREAM = 1;
    private static final String[] names = new String[]{"loadstring", "loadstream"};
    private static final LuaCompiler[] functions = new LuaCompiler[names.length];

    private LuaCompiler(int int0) {
        this.index = int0;
    }

    public static void register(KahluaTable table) {
        for (int int0 = 0; int0 < names.length; int0++) {
            table.rawset(names[int0], functions[int0]);
        }
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return this.loadstring(luaCallFrame, int0);
            case 1:
                return loadstream(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    public static int loadstream(LuaCallFrame luaCallFrame, int int0) {
        try {
            KahluaUtil.luaAssert(int0 >= 2, "not enough arguments");
            Object object = luaCallFrame.get(0);
            KahluaUtil.luaAssert(object != null, "No input given");
            String string = (String)luaCallFrame.get(1);
            if (object instanceof Reader) {
                return luaCallFrame.push(loadis((Reader)object, string, null, luaCallFrame.getEnvironment()));
            } else if (object instanceof InputStream) {
                return luaCallFrame.push(loadis((InputStream)object, string, null, luaCallFrame.getEnvironment()));
            } else {
                KahluaUtil.fail("Invalid type to loadstream: " + object.getClass());
                return 0;
            }
        } catch (RuntimeException runtimeException) {
            return luaCallFrame.push(null, runtimeException.getMessage());
        } catch (IOException iOException) {
            return luaCallFrame.push(null, iOException.getMessage());
        }
    }

    private int loadstring(LuaCallFrame luaCallFrame, int int0) {
        try {
            KahluaUtil.luaAssert(int0 >= 1, "not enough arguments");
            String string0 = (String)luaCallFrame.get(0);
            KahluaUtil.luaAssert(string0 != null, "No source given");
            String string1 = null;
            if (int0 >= 2) {
                string1 = (String)luaCallFrame.get(1);
            }

            return luaCallFrame.push(loadstring(string0, string1, luaCallFrame.getEnvironment()));
        } catch (RuntimeException runtimeException) {
            return luaCallFrame.push(null, runtimeException.getMessage());
        } catch (IOException iOException) {
            return luaCallFrame.push(null, iOException.getMessage());
        }
    }

    public static LuaClosure loadis(InputStream inputStream, String string, KahluaTable table) throws IOException {
        return loadis(inputStream, string, null, table);
    }

    public static LuaClosure loadis(Reader reader, String string, KahluaTable table) throws IOException {
        return loadis(reader, string, null, table);
    }

    public static LuaClosure loadstring(String string1, String string0, KahluaTable table) throws IOException {
        return loadis(new ByteArrayInputStream(string1.getBytes("UTF-8")), string0, string1, table);
    }

    private static LuaClosure loadis(Reader reader, String string0, String string1, KahluaTable table) throws IOException {
        return new LuaClosure(LexState.compile(reader.read(), reader, string0, string1), table);
    }

    private static LuaClosure loadis(InputStream inputStream, String string0, String string1, KahluaTable table) throws IOException {
        return loadis(new InputStreamReader(inputStream), string0, string1, table);
    }

    static {
        for (int int0 = 0; int0 < names.length; int0++) {
            functions[int0] = new LuaCompiler(int0);
        }
    }
}
