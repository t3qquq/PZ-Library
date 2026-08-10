/*
 * Decompiled with CFR 0.152.
 */
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
        if (Lists[0].contains(returnValues)) {
            return;
        }
        Lists[0].add(returnValues);
    }

    ReturnValues(KahluaConverterManager kahluaConverterManager, LuaCallFrame luaCallFrame) {
        this.manager = kahluaConverterManager;
        this.callFrame = luaCallFrame;
    }

    public ReturnValues push(Object object) {
        this.args += this.callFrame.push(this.manager.fromJavaToLua(object));
        return this;
    }

    public ReturnValues push(Object ... objectArray) {
        for (Object object : objectArray) {
            this.push(object);
        }
        return this;
    }

    int getNArguments() {
        return this.args;
    }

    static {
        for (int i = 0; i < 1; ++i) {
            ReturnValues.Lists[i] = new ArrayList(100);
            for (int j = 0; j < 100; ++j) {
                Lists[i].add(new ReturnValues(null, null));
            }
        }
    }
}

