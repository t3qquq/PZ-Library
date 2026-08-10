/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodArguments;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class MultiLuaJavaInvoker
implements JavaFunction {
    private final List<LuaJavaInvoker> invokers = new ArrayList<LuaJavaInvoker>();
    private static final Comparator<? super LuaJavaInvoker> COMPARATOR = new Comparator<LuaJavaInvoker>(){

        @Override
        public int compare(LuaJavaInvoker luaJavaInvoker, LuaJavaInvoker luaJavaInvoker2) {
            if (luaJavaInvoker2.getNumMethodParams() == luaJavaInvoker.getNumMethodParams()) {
                boolean bl = luaJavaInvoker.isAllInt();
                boolean bl2 = luaJavaInvoker2.isAllInt();
                return bl ? 1 : (bl2 ? -1 : 0);
            }
            return luaJavaInvoker2.getNumMethodParams() - luaJavaInvoker.getNumMethodParams();
        }
    };

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        boolean bl;
        LuaJavaInvoker luaJavaInvoker;
        int n2;
        MethodArguments methodArguments = null;
        int n3 = this.invokers.size();
        int n4 = -1;
        for (n2 = 0; n2 < n3; ++n2) {
            luaJavaInvoker = this.invokers.get(n2);
            if (!luaJavaInvoker.matchesArgumentTypes(luaCallFrame, n)) continue;
            methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, n);
            bl = methodArguments.isValid();
            if (bl) {
                int n5 = luaJavaInvoker.call(methodArguments);
                ReturnValues.put(methodArguments.getReturnValues());
                MethodArguments.put(methodArguments);
                return n5;
            }
            n4 = n2;
            break;
        }
        if (n4 == -1) {
            for (n2 = 0; n2 < n3; ++n2) {
                luaJavaInvoker = this.invokers.get(n2);
                if (!luaJavaInvoker.matchesArgumentTypesOrPrimitives(luaCallFrame, n)) continue;
                methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, n);
                bl = methodArguments.isValid();
                if (bl) {
                    int n6 = luaJavaInvoker.call(methodArguments);
                    ReturnValues.put(methodArguments.getReturnValues());
                    MethodArguments.put(methodArguments);
                    return n6;
                }
                n4 = n2;
                break;
            }
        }
        for (n2 = 0; n2 < n3; ++n2) {
            if (n2 == n4) continue;
            luaJavaInvoker = this.invokers.get(n2);
            methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, n);
            bl = methodArguments.isValid();
            if (bl) {
                int n7 = luaJavaInvoker.call(methodArguments);
                ReturnValues.put(methodArguments.getReturnValues());
                MethodArguments.put(methodArguments);
                return n7;
            }
            MethodArguments.put(methodArguments);
        }
        if (methodArguments != null) {
            methodArguments.assertValid();
            MethodArguments.put(methodArguments);
        }
        throw new RuntimeException("No implementation found");
    }

    public void addInvoker(LuaJavaInvoker luaJavaInvoker) {
        if (!this.invokers.contains(luaJavaInvoker)) {
            this.invokers.add(luaJavaInvoker);
            Collections.sort(this.invokers, COMPARATOR);
        }
    }

    public List<LuaJavaInvoker> getInvokers() {
        return this.invokers;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MultiLuaJavaInvoker multiLuaJavaInvoker = (MultiLuaJavaInvoker)object;
        return this.invokers.equals(multiLuaJavaInvoker.invokers);
    }

    public int hashCode() {
        return this.invokers.hashCode();
    }
}

