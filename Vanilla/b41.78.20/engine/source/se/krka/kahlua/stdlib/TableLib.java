/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaArray;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public final class TableLib
implements JavaFunction {
    private static final int CONCAT = 0;
    private static final int INSERT = 1;
    private static final int REMOVE = 2;
    private static final int NEWARRAY = 3;
    private static final int PAIRS = 4;
    private static final int ISEMPTY = 5;
    private static final int WIPE = 6;
    private static final int NUM_FUNCTIONS = 7;
    private static final String[] names = new String[7];
    private static final TableLib[] functions;
    private final int index;

    public TableLib(int n) {
        this.index = n;
    }

    public static void register(Platform platform, KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = platform.newTable();
        for (int i = 0; i < 7; ++i) {
            kahluaTable2.rawset(names[i], (Object)functions[i]);
        }
        kahluaTable.rawset("table", (Object)kahluaTable2);
    }

    public String toString() {
        if (this.index < names.length) {
            return "table." + names[this.index];
        }
        return super.toString();
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        switch (this.index) {
            case 0: {
                return TableLib.concat(luaCallFrame, n);
            }
            case 1: {
                return TableLib.insert(luaCallFrame, n);
            }
            case 2: {
                return TableLib.remove(luaCallFrame, n);
            }
            case 3: {
                return this.newarray(luaCallFrame, n);
            }
            case 4: {
                return this.pairs(luaCallFrame, n);
            }
            case 5: {
                return this.isempty(luaCallFrame, n);
            }
            case 6: {
                return this.wipe(luaCallFrame, n);
            }
        }
        return 0;
    }

    private int wipe(LuaCallFrame luaCallFrame, int n) {
        KahluaTable kahluaTable = TableLib.getTable(luaCallFrame, n);
        kahluaTable.wipe();
        return 0;
    }

    private int isempty(LuaCallFrame luaCallFrame, int n) {
        KahluaTable kahluaTable = TableLib.getTable(luaCallFrame, n);
        return luaCallFrame.push(KahluaUtil.toBoolean(kahluaTable.isEmpty()));
    }

    private int pairs(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "Not enough arguments");
        Object object = luaCallFrame.get(0);
        KahluaUtil.luaAssert(object instanceof KahluaTable, "Expected a table");
        KahluaTable kahluaTable = (KahluaTable)object;
        return luaCallFrame.push(kahluaTable.iterator());
    }

    private int newarray(LuaCallFrame luaCallFrame, int n) {
        Object object = KahluaUtil.getOptionalArg(luaCallFrame, 1);
        KahluaArray kahluaArray = new KahluaArray();
        if (object instanceof KahluaTable && n == 1) {
            int n2;
            KahluaTable kahluaTable = (KahluaTable)object;
            for (int i = n2 = kahluaTable.len(); i >= 1; --i) {
                kahluaArray.rawset(i, kahluaTable.rawget(i));
            }
        } else {
            for (int i = n; i >= 1; --i) {
                kahluaArray.rawset(i, luaCallFrame.get(i - 1));
            }
        }
        return luaCallFrame.push(kahluaArray);
    }

    private static int concat(LuaCallFrame luaCallFrame, int n) {
        int n2;
        Comparable<Double> comparable;
        KahluaTable kahluaTable = TableLib.getTable(luaCallFrame, n);
        String string = "";
        if (n >= 2) {
            string = KahluaUtil.rawTostring(luaCallFrame.get(1));
        }
        int n3 = 1;
        if (n >= 3) {
            Double d = KahluaUtil.rawTonumber(luaCallFrame.get(2));
            n3 = d.intValue();
        }
        if (n >= 4) {
            comparable = KahluaUtil.rawTonumber(luaCallFrame.get(3));
            n2 = ((Double)comparable).intValue();
        } else {
            n2 = kahluaTable.len();
        }
        comparable = new StringBuilder();
        for (int i = n3; i <= n2; ++i) {
            if (i > n3) {
                ((StringBuilder)comparable).append(string);
            }
            Double d = KahluaUtil.toDouble(i);
            Object object = kahluaTable.rawget(d);
            ((StringBuilder)comparable).append(KahluaUtil.rawTostring(object));
        }
        return luaCallFrame.push(((StringBuilder)comparable).toString());
    }

    public static void insert(KahluaThread kahluaThread, KahluaTable kahluaTable, Object object) {
        TableLib.append(kahluaThread, kahluaTable, object);
    }

    public static void append(KahluaThread kahluaThread, KahluaTable kahluaTable, Object object) {
        int n = 1 + kahluaTable.len();
        kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble(n), object);
    }

    public static void rawappend(KahluaTable kahluaTable, Object object) {
        int n = 1 + kahluaTable.len();
        kahluaTable.rawset(KahluaUtil.toDouble(n), object);
    }

    public static void insert(KahluaThread kahluaThread, KahluaTable kahluaTable, int n, Object object) {
        int n2;
        for (int i = n2 = kahluaTable.len(); i >= n; --i) {
            kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble(i + 1), kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble(i)));
        }
        kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble(n), object);
    }

    public static void rawinsert(KahluaTable kahluaTable, int n, Object object) {
        int n2 = kahluaTable.len();
        if (n <= n2) {
            Double d = KahluaUtil.toDouble(n2 + 1);
            for (int i = n2; i >= n; --i) {
                Double d2 = KahluaUtil.toDouble(i);
                kahluaTable.rawset(d, kahluaTable.rawget(d2));
                d = d2;
            }
            kahluaTable.rawset(d, object);
        } else {
            kahluaTable.rawset(KahluaUtil.toDouble(n), object);
        }
    }

    private static int insert(LuaCallFrame luaCallFrame, int n) {
        Object object;
        KahluaUtil.luaAssert(n >= 2, "Not enough arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
        int n2 = kahluaTable.len() + 1;
        if (n > 2) {
            n2 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
            object = luaCallFrame.get(2);
        } else {
            object = luaCallFrame.get(1);
        }
        TableLib.insert(luaCallFrame.getThread(), kahluaTable, n2, object);
        return 0;
    }

    public static Object remove(KahluaThread kahluaThread, KahluaTable kahluaTable) {
        return TableLib.remove(kahluaThread, kahluaTable, kahluaTable.len());
    }

    public static Object remove(KahluaThread kahluaThread, KahluaTable kahluaTable, int n) {
        Object object = kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble(n));
        int n2 = kahluaTable.len();
        for (int i = n; i < n2; ++i) {
            kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble(i), kahluaThread.tableget(kahluaTable, KahluaUtil.toDouble(i + 1)));
        }
        kahluaThread.tableSet(kahluaTable, KahluaUtil.toDouble(n2), null);
        return object;
    }

    private static int remove(LuaCallFrame luaCallFrame, int n) {
        KahluaTable kahluaTable = TableLib.getTable(luaCallFrame, n);
        int n2 = kahluaTable.len();
        if (n > 1) {
            n2 = KahluaUtil.rawTonumber(luaCallFrame.get(1)).intValue();
        }
        luaCallFrame.push(TableLib.remove(luaCallFrame.getThread(), kahluaTable, n2));
        return 1;
    }

    private static KahluaTable getTable(LuaCallFrame luaCallFrame, int n) {
        KahluaUtil.luaAssert(n >= 1, "expected table, got no arguments");
        KahluaTable kahluaTable = (KahluaTable)luaCallFrame.get(0);
        return kahluaTable;
    }

    static {
        TableLib.names[0] = "concat";
        TableLib.names[1] = "insert";
        TableLib.names[2] = "remove";
        TableLib.names[3] = "newarray";
        TableLib.names[4] = "pairs";
        TableLib.names[5] = "isempty";
        TableLib.names[6] = "wipe";
        functions = new TableLib[7];
        for (int i = 0; i < 7; ++i) {
            TableLib.functions[i] = new TableLib(i);
        }
    }
}

