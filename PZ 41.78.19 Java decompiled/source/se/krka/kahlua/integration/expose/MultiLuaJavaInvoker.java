// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class MultiLuaJavaInvoker implements JavaFunction {
    private final List<LuaJavaInvoker> invokers = new ArrayList<>();
    private static final Comparator<? super LuaJavaInvoker> COMPARATOR = new Comparator<LuaJavaInvoker>() {
        public int compare(LuaJavaInvoker luaJavaInvoker0, LuaJavaInvoker luaJavaInvoker1) {
            if (luaJavaInvoker1.getNumMethodParams() == luaJavaInvoker0.getNumMethodParams()) {
                boolean boolean0 = luaJavaInvoker0.isAllInt();
                boolean boolean1 = luaJavaInvoker1.isAllInt();
                return boolean0 ? 1 : (boolean1 ? -1 : 0);
            } else {
                return luaJavaInvoker1.getNumMethodParams() - luaJavaInvoker0.getNumMethodParams();
            }
        }
    };

    @Override
    public int call(LuaCallFrame luaCallFrame, int int3) {
        MethodArguments methodArguments = null;
        int int0 = this.invokers.size();
        int int1 = -1;

        for (int int2 = 0; int2 < int0; int2++) {
            LuaJavaInvoker luaJavaInvoker0 = this.invokers.get(int2);
            if (luaJavaInvoker0.matchesArgumentTypes(luaCallFrame, int3)) {
                methodArguments = luaJavaInvoker0.prepareCall(luaCallFrame, int3);
                boolean boolean0 = methodArguments.isValid();
                if (boolean0) {
                    int int4 = luaJavaInvoker0.call(methodArguments);
                    ReturnValues.put(methodArguments.getReturnValues());
                    MethodArguments.put(methodArguments);
                    return int4;
                }

                int1 = int2;
                break;
            }
        }

        if (int1 == -1) {
            for (int int5 = 0; int5 < int0; int5++) {
                LuaJavaInvoker luaJavaInvoker1 = this.invokers.get(int5);
                if (luaJavaInvoker1.matchesArgumentTypesOrPrimitives(luaCallFrame, int3)) {
                    methodArguments = luaJavaInvoker1.prepareCall(luaCallFrame, int3);
                    boolean boolean1 = methodArguments.isValid();
                    if (boolean1) {
                        int int6 = luaJavaInvoker1.call(methodArguments);
                        ReturnValues.put(methodArguments.getReturnValues());
                        MethodArguments.put(methodArguments);
                        return int6;
                    }

                    int1 = int5;
                    break;
                }
            }
        }

        for (int int7 = 0; int7 < int0; int7++) {
            if (int7 != int1) {
                LuaJavaInvoker luaJavaInvoker2 = this.invokers.get(int7);
                methodArguments = luaJavaInvoker2.prepareCall(luaCallFrame, int3);
                boolean boolean2 = methodArguments.isValid();
                if (boolean2) {
                    int int8 = luaJavaInvoker2.call(methodArguments);
                    ReturnValues.put(methodArguments.getReturnValues());
                    MethodArguments.put(methodArguments);
                    return int8;
                }

                MethodArguments.put(methodArguments);
            }
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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            MultiLuaJavaInvoker multiLuaJavaInvoker1 = (MultiLuaJavaInvoker)object;
            return this.invokers.equals(multiLuaJavaInvoker1.invokers);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.invokers.hashCode();
    }
}
