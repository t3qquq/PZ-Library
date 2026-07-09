// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaArray;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public final class TableLib implements JavaFunction {
    private static final int CONCAT = 0;
    private static final int INSERT = 1;
    private static final int REMOVE = 2;
    private static final int NEWARRAY = 3;
    private static final int PAIRS = 4;
    private static final int ISEMPTY = 5;
    private static final int WIPE = 6;
    private static final int NUM_FUNCTIONS = 7;
    private static final String[] names = new String[7];
    private static final TableLib[] functions = new TableLib[7];
    private final int index;

    public TableLib(int int0) {
        this.index = int0;
    }

    public static void register(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();

        for (int int0 = 0; int0 < 7; int0++) {
            table0.rawset(names[int0], functions[int0]);
        }

        table1.rawset("table", table0);
    }

    @Override
    public String toString() {
        return this.index < names.length ? "table." + names[this.index] : super.toString();
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        switch (this.index) {
            case 0:
                return concat(luaCallFrame, int0);
            case 1:
                return insert(luaCallFrame, int0);
            case 2:
                return remove(luaCallFrame, int0);
            case 3:
                return this.newarray(luaCallFrame, int0);
            case 4:
                return this.pairs(luaCallFrame, int0);
            case 5:
                return this.isempty(luaCallFrame, int0);
            case 6:
                return this.wipe(luaCallFrame, int0);
            default:
                return 0;
        }
    }

    private int wipe(LuaCallFrame luaCallFrame, int int0) {
        KahluaTable table = getTable(luaCallFrame, int0);
        table.wipe();
        return 0;
    }

    private int isempty(LuaCallFrame luaCallFrame, int int0) {
        KahluaTable table = getTable(luaCallFrame, int0);
        return luaCallFrame.push(KahluaUtil.toBoolean(table.isEmpty()));
    }

    private int pairs(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        KahluaUtil.luaAssert(object instanceof KahluaTable, "Expected a table");
        KahluaTable table = (KahluaTable)object;
        return luaCallFrame.push(table.iterator());
    }

    private int newarray(LuaCallFrame luaCallFrame, int int0) {
        Object object = KahluaUtil.getOptionalArg(luaCallFrame, 1);
        KahluaArray kahluaArray = new KahluaArray();
        if (object instanceof KahluaTable && int0 == 1) {
            KahluaTable table = (KahluaTable)object;
            int int1 = table.len();

            for (int int2 = int1; int2 >= 1; int2--) {
                kahluaArray.rawset(int2, table.rawget(int2));
            }
        } else {
            for (int int3 = int0; int3 >= 1; int3--) {
                kahluaArray.rawset(int3, luaCallFrame.get(int3 - 1));
            }
        }

        return luaCallFrame.push(kahluaArray);
    }

    private static int concat(LuaCallFrame luaCallFrame, int int0) {
        KahluaTable table = getTable(luaCallFrame, int0);
        String string = "";
        if (int0 >= 2) {
            string = KahluaUtil.rawTostring(luaCallFrame.get(1));
        }

        int int1 = 1;
        if (int0 >= 3) {
            Double double0 = KahluaUtil.rawTonumber(luaCallFrame.get(2));
            int1 = double0.intValue();
        }

        int int2;
        if (int0 >= 4) {
            Double double1 = KahluaUtil.rawTonumber(luaCallFrame.get(3));
            int2 = double1.intValue();
        } else {
            int2 = table.len();
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int int3 = int1; int3 <= int2; int3++) {
            if (int3 > int1) {
                stringBuilder.append(string);
            }

            Double double2 = KahluaUtil.toDouble((long)int3);
            Object object = table.rawget(double2);
            stringBuilder.append(KahluaUtil.rawTostring(object));
        }

        return luaCallFrame.push(stringBuilder.toString());
    }

    public static void insert(KahluaThread kahluaThread, KahluaTable table, Object object) {
        append(kahluaThread, table, object);
    }

    public static void append(KahluaThread kahluaThread, KahluaTable table, Object object) {
        int int0 = 1 + table.len();
        kahluaThread.tableSet(table, KahluaUtil.toDouble((long)int0), object);
    }

    public static void rawappend(KahluaTable table, Object object) {
        int int0 = 1 + table.len();
        table.rawset(KahluaUtil.toDouble((long)int0), object);
    }

    public static void insert(KahluaThread kahluaThread, KahluaTable table, int int2, Object object) {
        int int0 = table.len();

        for (int int1 = int0; int1 >= int2; int1--) {
            kahluaThread.tableSet(table, KahluaUtil.toDouble((long)(int1 + 1)), kahluaThread.tableget(table, KahluaUtil.toDouble((long)int1)));
        }

        kahluaThread.tableSet(table, KahluaUtil.toDouble((long)int2), object);
    }

    public static void rawinsert(KahluaTable table, int int1, Object object) {
        int int0 = table.len();
        if (int1 <= int0) {
            Double double0 = KahluaUtil.toDouble((long)(int0 + 1));

            for (int int2 = int0; int2 >= int1; int2--) {
                Double double1 = KahluaUtil.toDouble((long)int2);
                table.rawset(double0, table.rawget(double1));
                double0 = double1;
            }

            table.rawset(double0, object);
        } else {
            table.rawset(KahluaUtil.toDouble((long)int1), object);
        }
    }

    private static int insert(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 2, "Not enough arguments");
        KahluaTable table = (KahluaTable)luaCallFrame.get(0);
        int int1 = table.len() + 1;
        Object object;
        if (int0 > 2) {
            int1 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
            object = luaCallFrame.get(2);
        } else {
            object = luaCallFrame.get(1);
        }

        insert(luaCallFrame.getThread(), table, int1, object);
        return 0;
    }

    public static Object remove(KahluaThread kahluaThread, KahluaTable table) {
        return remove(kahluaThread, table, table.len());
    }

    public static Object remove(KahluaThread kahluaThread, KahluaTable table, int int0) {
        Object object = kahluaThread.tableget(table, KahluaUtil.toDouble((long)int0));
        int int1 = table.len();

        for (int int2 = int0; int2 < int1; int2++) {
            kahluaThread.tableSet(table, KahluaUtil.toDouble((long)int2), kahluaThread.tableget(table, KahluaUtil.toDouble((long)(int2 + 1))));
        }

        kahluaThread.tableSet(table, KahluaUtil.toDouble((long)int1), null);
        return object;
    }

    private static int remove(LuaCallFrame luaCallFrame, int int0) {
        KahluaTable table = getTable(luaCallFrame, int0);
        int int1 = table.len();
        if (int0 > 1) {
            int1 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
        }

        luaCallFrame.push(remove(luaCallFrame.getThread(), table, int1));
        return 1;
    }

    private static KahluaTable getTable(LuaCallFrame luaCallFrame, int int0) {
        KahluaUtil.luaAssert(int0 >= 1, "expected table, got no arguments");
        return (KahluaTable)luaCallFrame.get(0);
    }

    static {
        names[0] = "concat";
        names[1] = "insert";
        names[2] = "remove";
        names[3] = "newarray";
        names[4] = "pairs";
        names[5] = "isempty";
        names[6] = "wipe";

        for (int int0 = 0; int0 < 7; int0++) {
            functions[int0] = new TableLib(int0);
        }
    }
}
