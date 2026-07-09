// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.vm.LuaCallFrame;

public class ReturnValues {
    private KahluaConverterManager manager;
    private LuaCallFrame callFrame;
    private int args;
    static ArrayList[] Lists = new ArrayList[1];

    public static ReturnValues get(KahluaConverterManager kahluaConverterManager, LuaCallFrame luaCallFrame) {
        ReturnValues returnValues = null;
        if (Lists[0].isEmpty()) {
            returnValues = new ReturnValues(kahluaConverterManager, luaCallFrame);
        } else {
            returnValues = (ReturnValues)Lists[0].get(0);
            Lists[0].remove(returnValues);
        }

        returnValues.manager = kahluaConverterManager;
        returnValues.callFrame = luaCallFrame;
        return returnValues;
    }

    public static void put(ReturnValues returnValues) {
        returnValues.callFrame = null;
        returnValues.manager = null;
        returnValues.args = 0;
        if (!Lists[0].contains(returnValues)) {
            Lists[0].add(returnValues);
        }
    }

    ReturnValues(KahluaConverterManager kahluaConverterManager, LuaCallFrame luaCallFrame) {
        this.manager = kahluaConverterManager;
        this.callFrame = luaCallFrame;
    }

    public ReturnValues push(Object object) {
        this.args = this.args + this.callFrame.push(this.manager.fromJavaToLua(object));
        return this;
    }

    public ReturnValues push(Object... objects) {
        for (Object object : objects) {
            this.push(object);
        }

        return this;
    }

    int getNArguments() {
        return this.args;
    }

    static {
        for (int int0 = 0; int0 < 1; int0++) {
            Lists[int0] = new ArrayList(100);

            for (int int1 = 0; int1 < 100; int1++) {
                Lists[int0].add(new ReturnValues(null, null));
            }
        }
    }
}
