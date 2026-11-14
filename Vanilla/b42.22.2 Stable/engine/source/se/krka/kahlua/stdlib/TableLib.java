// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaArray;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.core.BoxedStaticValues;

public final class TableLib implements JavaFunction {
    private static final int CONCAT = 0;
    private static final int INSERT = 1;
    private static final int REMOVE = 2;
    private static final int NEWARRAY = 3;
    private static final int PAIRS = 4;
    private static final int ISEMPTY = 5;
    private static final int WIPE = 6;
    private static final int IPAIRS = 7;
    private static final int NUM_FUNCTIONS = 8;
    private static final String[] names = new String[8];
    private static final TableLib[] functions = new TableLib[8];
    private final int index;
    private static final JavaFunction ipairs_iterator;

    public TableLib(int index) {
        this.index = index;
    }

    public static void register(Platform platform, KahluaTable environment) {
        KahluaTable table = platform.newTable();

        for (int i = 0; i < 8; i++) {
            table.rawset(names[i], functions[i]);
        }

        environment.rawset("table", table);
    }

    @Override
    public String toString() {
        return this.index < names.length ? "table." + names[this.index] : super.toString();
    }

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        switch (this.index) {
            case 0:
                return concat(callFrame, nArguments);
            case 1:
                return insert(callFrame, nArguments);
            case 2:
                return remove(callFrame, nArguments);
            case 3:
                return this.newarray(callFrame, nArguments);
            case 4:
                return this.pairs(callFrame, nArguments);
            case 5:
                return this.isempty(callFrame, nArguments);
            case 6:
                return this.wipe(callFrame, nArguments);
            case 7:
                return this.ipairs(callFrame, nArguments);
            default:
                return 0;
        }
    }

    private int wipe(LuaCallFrame callFrame, int nArguments) {
        KahluaTable table = getTable(callFrame, nArguments);
        table.wipe();
        return 0;
    }

    private int isempty(LuaCallFrame callFrame, int nArguments) {
        KahluaTable table = getTable(callFrame, nArguments);
        return callFrame.push(KahluaUtil.toBoolean(table.isEmpty()));
    }

    private int pairs(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        KahluaUtil.luaAssert(o instanceof KahluaTable, "Expected a table");
        KahluaTable t = (KahluaTable)o;
        return callFrame.push(t.iterator());
    }

    private int ipairs(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "Not enough arguments");
        Object o = callFrame.get(0);
        KahluaUtil.luaAssert(o instanceof KahluaTable, "Expected a table");
        KahluaTable t = (KahluaTable)o;
        callFrame.push(ipairs_iterator);
        callFrame.push(t);
        callFrame.push(BoxedStaticValues.toDouble(0.0));
        return 3;
    }

    private int newarray(LuaCallFrame callFrame, int arguments) {
        Object param = KahluaUtil.getOptionalArg(callFrame, 1);
        KahluaArray ret = new KahluaArray();
        if (param instanceof KahluaTable t && arguments == 1) {
            int n = t.len();

            for (int i = n; i >= 1; i--) {
                ret.rawset(i, t.rawget(i));
            }
        } else {
            for (int i = arguments; i >= 1; i--) {
                ret.rawset(i, callFrame.get(i - 1));
            }
        }

        return callFrame.push(ret);
    }

    private static int concat(LuaCallFrame callFrame, int nArguments) {
        KahluaTable table = getTable(callFrame, nArguments);
        String separator = "";
        if (nArguments >= 2) {
            separator = KahluaUtil.rawTostring(callFrame.get(1));
        }

        int first = 1;
        if (nArguments >= 3) {
            Double firstDouble = KahluaUtil.rawTonumber(callFrame.get(2));
            first = firstDouble.intValue();
        }

        int last;
        if (nArguments >= 4) {
            Double lastDouble = KahluaUtil.rawTonumber(callFrame.get(3));
            last = lastDouble.intValue();
        } else {
            last = table.len();
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = first; i <= last; i++) {
            if (i > first) {
                buffer.append(separator);
            }

            Double key = KahluaUtil.toDouble(i);
            Object value = table.rawget(key);
            buffer.append(KahluaUtil.rawTostring(value));
        }

        return callFrame.push(buffer.toString());
    }

    public static void insert(KahluaThread state, KahluaTable table, Object element) {
        append(state, table, element);
    }

    public static void append(KahluaThread state, KahluaTable table, Object element) {
        int position = 1 + table.len();
        state.tableSet(table, KahluaUtil.toDouble(position), element);
    }

    public static void rawappend(KahluaTable table, Object element) {
        int position = 1 + table.len();
        table.rawset(KahluaUtil.toDouble(position), element);
    }

    public static void insert(KahluaThread state, KahluaTable table, int position, Object element) {
        int len = table.len();

        for (int i = len; i >= position; i--) {
            state.tableSet(table, KahluaUtil.toDouble(i + 1), state.tableget(table, KahluaUtil.toDouble(i)));
        }

        state.tableSet(table, KahluaUtil.toDouble(position), element);
    }

    public static void rawinsert(KahluaTable table, int position, Object element) {
        int len = table.len();
        if (position <= len) {
            Double dest = KahluaUtil.toDouble(len + 1);

            for (int i = len; i >= position; i--) {
                Double src = KahluaUtil.toDouble(i);
                table.rawset(dest, table.rawget(src));
                dest = src;
            }

            table.rawset(dest, element);
        } else {
            table.rawset(KahluaUtil.toDouble(position), element);
        }
    }

    private static int insert(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
        KahluaTable t = (KahluaTable)callFrame.get(0);
        int pos = t.len() + 1;
        Object elem;
        if (nArguments > 2) {
            pos = KahluaUtil.rawTonumber(callFrame.get(1)).intValue();
            elem = callFrame.get(2);
        } else {
            elem = callFrame.get(1);
        }

        insert(callFrame.getThread(), t, pos, elem);
        return 0;
    }

    public static Object remove(KahluaThread state, KahluaTable table) {
        return remove(state, table, table.len());
    }

    public static Object remove(KahluaThread thread, KahluaTable table, int position) {
        Object ret = thread.tableget(table, KahluaUtil.toDouble(position));
        int len = table.len();

        for (int i = position; i < len; i++) {
            thread.tableSet(table, KahluaUtil.toDouble(i), thread.tableget(table, KahluaUtil.toDouble(i + 1)));
        }

        thread.tableSet(table, KahluaUtil.toDouble(len), null);
        return ret;
    }

    private static int remove(LuaCallFrame callFrame, int nArguments) {
        KahluaTable t = getTable(callFrame, nArguments);
        int pos = t.len();
        if (nArguments > 1) {
            pos = KahluaUtil.rawTonumber(callFrame.get(1)).intValue();
        }

        callFrame.push(remove(callFrame.getThread(), t, pos));
        return 1;
    }

    private static KahluaTable getTable(LuaCallFrame callFrame, int nArguments) {
        KahluaUtil.luaAssert(nArguments >= 1, "expected table, got no arguments");
        return (KahluaTable)callFrame.get(0);
    }

    static {
        names[0] = "concat";
        names[1] = "insert";
        names[2] = "remove";
        names[3] = "newarray";
        names[4] = "pairs";
        names[5] = "isempty";
        names[6] = "wipe";
        names[7] = "ipairs";

        for (int i = 0; i < 8; i++) {
            functions[i] = new TableLib(i);
        }

        ipairs_iterator = (callFrame, nArguments) -> {
            KahluaUtil.luaAssert(nArguments >= 2, "Not enough arguments");
            Object o = callFrame.get(0);
            KahluaUtil.luaAssert(o instanceof KahluaTable, "Expected a table");
            KahluaTable t = (KahluaTable)o;
            o = callFrame.get(1);
            KahluaUtil.luaAssert(o instanceof Double, "Expected a number");
            Double nextIndex = BoxedStaticValues.toDouble((Double)o + 1.0);
            o = t.rawget(nextIndex);
            return o != null ? callFrame.push(nextIndex, o) : 0;
        };
    }
}
