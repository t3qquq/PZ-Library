// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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
        public int compare(LuaJavaInvoker o1, LuaJavaInvoker o2) {
            if (o2.getNumMethodParams() == o1.getNumMethodParams()) {
                boolean allInt1 = o1.isAllInt();
                boolean allInt2 = o2.isAllInt();
                return allInt1 ? 1 : (allInt2 ? -1 : 0);
            } else {
                return o2.getNumMethodParams() - o1.getNumMethodParams();
            }
        }
    };

    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        MethodArguments methodArguments = null;
        int inS = this.invokers.size();
        int best = -1;

        for (int n = 0; n < inS; n++) {
            LuaJavaInvoker invoker = this.invokers.get(n);
            if (invoker.matchesArgumentTypes(callFrame, nArguments)) {
                methodArguments = invoker.prepareCall(callFrame, nArguments);
                boolean bValid = methodArguments.isValid();
                if (bValid) {
                    int val = invoker.call(methodArguments);
                    ReturnValues.put(methodArguments.getReturnValues());
                    MethodArguments.put(methodArguments);
                    return val;
                }

                best = n;
                break;
            }
        }

        if (best == -1) {
            for (int n = 0; n < inS; n++) {
                LuaJavaInvoker invoker = this.invokers.get(n);
                if (invoker.matchesArgumentTypesOrPrimitives(callFrame, nArguments)) {
                    methodArguments = invoker.prepareCall(callFrame, nArguments);
                    boolean bValid = methodArguments.isValid();
                    if (bValid) {
                        int val = invoker.call(methodArguments);
                        ReturnValues.put(methodArguments.getReturnValues());
                        MethodArguments.put(methodArguments);
                        return val;
                    }

                    best = n;
                    break;
                }
            }
        }

        for (int n = 0; n < inS; n++) {
            if (n != best) {
                LuaJavaInvoker invoker = this.invokers.get(n);
                methodArguments = invoker.prepareCall(callFrame, nArguments);
                boolean bValid = methodArguments.isValid();
                if (bValid) {
                    int val = invoker.call(methodArguments);
                    ReturnValues.put(methodArguments.getReturnValues());
                    MethodArguments.put(methodArguments);
                    return val;
                }

                MethodArguments.put(methodArguments);
            }
        }

        if (methodArguments != null) {
            methodArguments.assertValid();
            MethodArguments.put(methodArguments);
        }

        throw new RuntimeException("No implementation found for function: " + this.getMethodSignature(callFrame, nArguments) + ")");
    }

    public String getMethodSignature(LuaCallFrame callFrame, int nArguments) {
        String methodName = this.getMethodName();
        String methodArgs = this.getParametersString(callFrame, nArguments);
        return methodName + "(" + methodArgs + ")";
    }

    public String getMethodName() {
        return !this.invokers.isEmpty() ? this.invokers.get(0).toString() : "!noname!";
    }

    public String getParametersString(LuaCallFrame callFrame, int nArguments) {
        StringBuilder returnVal = new StringBuilder();

        for (int i = 0; i < nArguments; i++) {
            Object o = callFrame.get(i);
            Class<?> parameterType = o != null ? o.getClass() : null;
            if (!returnVal.isEmpty()) {
                returnVal.append(", ");
            }

            returnVal.append(parameterType);
            returnVal.append(' ');
            returnVal.append(o);
        }

        return returnVal.toString();
    }

    public void addInvoker(LuaJavaInvoker invoker) {
        if (!this.invokers.contains(invoker)) {
            this.invokers.add(invoker);
            Collections.sort(this.invokers, COMPARATOR);
        }
    }

    public List<LuaJavaInvoker> getInvokers() {
        return this.invokers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            MultiLuaJavaInvoker that = (MultiLuaJavaInvoker)o;
            return this.invokers.equals(that.invokers);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.invokers.hashCode();
    }

    @Override
    public String toString() {
        return this.getMethodName();
    }
}
