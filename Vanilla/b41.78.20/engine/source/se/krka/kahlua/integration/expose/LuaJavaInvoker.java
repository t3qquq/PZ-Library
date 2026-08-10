/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.ClassDebugInformation;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.integration.expose.MethodArguments;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.expose.caller.Caller;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class LuaJavaInvoker
implements JavaFunction {
    private final LuaJavaClassExposer exposer;
    private final KahluaConverterManager manager;
    private final Class<?> clazz;
    private final String name;
    private final Caller caller;
    private final Class<?>[] parameterTypes;
    private final int numMethodParams;
    private final Class<?> varargType;
    private final boolean hasSelf;
    private final boolean needsReturnValues;
    private final boolean hasVarargs;

    public LuaJavaInvoker(LuaJavaClassExposer luaJavaClassExposer, KahluaConverterManager kahluaConverterManager, Class<?> clazz, String string, Caller caller) {
        this.exposer = luaJavaClassExposer;
        this.manager = kahluaConverterManager;
        this.clazz = clazz;
        this.name = string;
        this.caller = caller;
        this.parameterTypes = caller.getParameterTypes();
        this.varargType = caller.getVarargType();
        this.hasSelf = caller.hasSelf();
        this.needsReturnValues = caller.needsMultipleReturnValues();
        this.hasVarargs = caller.hasVararg();
        this.numMethodParams = this.parameterTypes.length + this.toInt(this.needsReturnValues) + this.toInt(this.hasVarargs);
    }

    private int toInt(boolean bl) {
        return bl ? 1 : 0;
    }

    public MethodArguments prepareCall(LuaCallFrame luaCallFrame, int n) {
        Object object;
        int n2;
        Object[] objectArray;
        int n3;
        Object object2;
        MethodArguments methodArguments = MethodArguments.get(this.numMethodParams);
        int n4 = 0;
        int n5 = 0;
        int n6 = this.toInt(this.hasSelf);
        if (this.hasSelf) {
            Object object3 = object2 = n <= 0 ? null : luaCallFrame.get(0);
            if (object2 == null || !this.clazz.isInstance(object2)) {
                methodArguments.fail(this.syntaxErrorMessage(this.name + ": Expected a method call but got a function call."));
                return methodArguments;
            }
            methodArguments.setSelf(object2);
            ++n5;
        }
        object2 = ReturnValues.get(this.manager, luaCallFrame);
        methodArguments.setReturnValues((ReturnValues)object2);
        if (this.needsReturnValues) {
            methodArguments.getParams()[n4] = object2;
            ++n4;
        }
        if (n - n5 < this.parameterTypes.length) {
            int n7 = this.parameterTypes.length;
            int n8 = n - n6;
            methodArguments.fail(null);
            return methodArguments;
        }
        if (n5 != 0 && this.parameterTypes.length < n - n5) {
            int n9 = this.parameterTypes.length;
            int n10 = n - n6;
            methodArguments.fail(null);
            return methodArguments;
        }
        for (n3 = 0; n3 < this.parameterTypes.length; ++n3) {
            objectArray = luaCallFrame.get(n5 + n3);
            n2 = n5 + n3 - n6;
            object = this.parameterTypes[n3];
            Object object4 = objectArray;
            if (!((Class)object).isInstance(objectArray)) {
                object4 = this.convert(objectArray, (Class<?>)object);
            }
            if (objectArray != null && object4 == null) {
                methodArguments.fail("");
                return methodArguments;
            }
            methodArguments.getParams()[n4 + n3] = object4;
        }
        n4 += this.parameterTypes.length;
        n5 += this.parameterTypes.length;
        if (this.hasVarargs) {
            n3 = n - n5;
            if (n3 < 0) {
                // empty if block
            }
            objectArray = (Object[])Array.newInstance(this.varargType, n3);
            for (n2 = 0; n2 < n3; ++n2) {
                Object object5;
                object = luaCallFrame.get(n5 + n2);
                int n11 = n5 + n2 - n6;
                objectArray[n2] = object5 = this.convert(object, this.varargType);
                if (object == null || object5 != null) continue;
                methodArguments.fail("");
                return methodArguments;
            }
            methodArguments.getParams()[n4] = objectArray;
            ++n4;
            n5 += n3;
        }
        return methodArguments;
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int n) {
        MethodArguments methodArguments = this.prepareCall(luaCallFrame, n);
        methodArguments.assertValid();
        int n2 = this.call(methodArguments);
        ReturnValues.put(methodArguments.getReturnValues());
        MethodArguments.put(methodArguments);
        return n2;
    }

    public int call(MethodArguments methodArguments) {
        try {
            ReturnValues returnValues = methodArguments.getReturnValues();
            this.caller.call(methodArguments.getSelf(), returnValues, methodArguments.getParams());
            return returnValues.getNArguments();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new RuntimeException(invocationTargetException.getCause());
        }
        catch (InstantiationException instantiationException) {
            throw new RuntimeException(instantiationException);
        }
    }

    private Object convert(Object object, Class<?> clazz) {
        if (object == null) {
            return null;
        }
        Object obj = this.manager.fromLuaToJava(object, clazz);
        return obj;
    }

    private String syntaxErrorMessage(String object) {
        String string = this.getFunctionSyntax();
        if (string != null) {
            object = (String)object + " Correct syntax: " + string;
        }
        return object;
    }

    private String newError(int n, String string) {
        int n2 = n + 1;
        String string2 = string + " at argument #" + n2;
        String string3 = this.getParameterName(n);
        if (string3 != null) {
            string2 = string2 + ", " + string3;
        }
        return string2;
    }

    private String getFunctionSyntax() {
        MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
        if (methodDebugInformation != null) {
            return methodDebugInformation.getLuaDescription();
        }
        return null;
    }

    public MethodDebugInformation getMethodDebugData() {
        ClassDebugInformation classDebugInformation = this.exposer.getDebugdata(this.clazz);
        if (classDebugInformation == null) {
            return null;
        }
        return classDebugInformation.getMethods().get(this.caller.getDescriptor());
    }

    private String getParameterName(int n) {
        MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
        if (methodDebugInformation != null) {
            return methodDebugInformation.getParameters().get(n).getName();
        }
        return null;
    }

    public String toString() {
        return this.name;
    }

    public int getNumMethodParams() {
        return this.numMethodParams;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        LuaJavaInvoker luaJavaInvoker = (LuaJavaInvoker)object;
        if (!this.caller.equals(luaJavaInvoker.caller)) {
            return false;
        }
        if (!this.clazz.equals(luaJavaInvoker.clazz)) {
            return false;
        }
        return this.name.equals(luaJavaInvoker.name);
    }

    public int hashCode() {
        int n = this.clazz.hashCode();
        n = 31 * n + this.name.hashCode();
        n = 31 * n + this.caller.hashCode();
        return n;
    }

    public boolean matchesArgumentTypes(LuaCallFrame luaCallFrame, int n) {
        int n2 = 0;
        if (this.hasSelf) {
            Object object;
            Object object2 = object = n <= 0 ? null : luaCallFrame.get(0);
            if (object == null || !this.clazz.isInstance(object)) {
                return false;
            }
            ++n2;
        }
        if (this.parameterTypes.length != n - n2) {
            return false;
        }
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            Class<?> clazz = this.parameterTypes[i];
            Object object = luaCallFrame.get(n2 + i);
            if (clazz.isInstance(object)) continue;
            return false;
        }
        return true;
    }

    public boolean matchesArgumentTypesOrPrimitives(LuaCallFrame luaCallFrame, int n) {
        int n2 = 0;
        if (this.hasSelf) {
            Object object;
            Object object2 = object = n <= 0 ? null : luaCallFrame.get(0);
            if (object == null || !this.clazz.isInstance(object)) {
                return false;
            }
            ++n2;
        }
        if (this.parameterTypes.length != n - n2) {
            return false;
        }
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            Class<?> clazz = this.parameterTypes[i];
            Object object = luaCallFrame.get(n2 + i);
            if (clazz.isInstance(object)) continue;
            if (clazz.isPrimitive()) {
                if (object == null) {
                    return false;
                }
                if (object instanceof Double) {
                    if (clazz != Void.TYPE && clazz != Boolean.TYPE) continue;
                    return false;
                }
                if (object instanceof Boolean && clazz == Boolean.TYPE) {
                    continue;
                }
            } else if (object == null) continue;
            return false;
        }
        return true;
    }

    public boolean isAllInt() {
        if (this.parameterTypes == null || this.parameterTypes.length == 0) {
            return false;
        }
        for (Class<?> clazz : this.parameterTypes) {
            if (clazz == Integer.TYPE) continue;
            return false;
        }
        return true;
    }
}

