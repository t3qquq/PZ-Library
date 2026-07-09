// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.caller.Caller;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class LuaJavaInvoker implements JavaFunction {
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

    public LuaJavaInvoker(
        LuaJavaClassExposer luaJavaClassExposer, KahluaConverterManager kahluaConverterManager, Class<?> clazzx, String string, Caller callerx
    ) {
        this.exposer = luaJavaClassExposer;
        this.manager = kahluaConverterManager;
        this.clazz = clazzx;
        this.name = string;
        this.caller = callerx;
        this.parameterTypes = callerx.getParameterTypes();
        this.varargType = callerx.getVarargType();
        this.hasSelf = callerx.hasSelf();
        this.needsReturnValues = callerx.needsMultipleReturnValues();
        this.hasVarargs = callerx.hasVararg();
        this.numMethodParams = this.parameterTypes.length + this.toInt(this.needsReturnValues) + this.toInt(this.hasVarargs);
    }

    private int toInt(boolean boolean0) {
        return boolean0 ? 1 : 0;
    }

    public MethodArguments prepareCall(LuaCallFrame luaCallFrame, int int3) {
        MethodArguments methodArguments = MethodArguments.get(this.numMethodParams);
        int int0 = 0;
        int int1 = 0;
        int int2 = this.toInt(this.hasSelf);
        if (this.hasSelf) {
            Object object0 = int3 <= 0 ? null : luaCallFrame.get(0);
            if (object0 == null || !this.clazz.isInstance(object0)) {
                methodArguments.fail(this.syntaxErrorMessage(this.name + ": Expected a method call but got a function call."));
                return methodArguments;
            }

            methodArguments.setSelf(object0);
            int1++;
        }

        ReturnValues returnValues = ReturnValues.get(this.manager, luaCallFrame);
        methodArguments.setReturnValues(returnValues);
        if (this.needsReturnValues) {
            methodArguments.getParams()[int0] = returnValues;
            int0++;
        }

        if (int3 - int1 < this.parameterTypes.length) {
            int int4 = this.parameterTypes.length;
            int int5 = int3 - int2;
            methodArguments.fail(null);
            return methodArguments;
        } else if (int1 != 0 && this.parameterTypes.length < int3 - int1) {
            int int6 = this.parameterTypes.length;
            int int7 = int3 - int2;
            methodArguments.fail(null);
            return methodArguments;
        } else {
            for (int int8 = 0; int8 < this.parameterTypes.length; int8++) {
                Object object1 = luaCallFrame.get(int1 + int8);
                int int9 = int1 + int8 - int2;
                Class clazzx = this.parameterTypes[int8];
                Object object2 = object1;
                if (!clazzx.isInstance(object1)) {
                    object2 = this.convert(object1, clazzx);
                }

                if (object1 != null && object2 == null) {
                    methodArguments.fail("");
                    return methodArguments;
                }

                methodArguments.getParams()[int0 + int8] = object2;
            }

            int0 += this.parameterTypes.length;
            int1 += this.parameterTypes.length;
            if (this.hasVarargs) {
                int int10 = int3 - int1;
                if (int10 < 0) {
                }

                Object[] objects = (Object[])Array.newInstance(this.varargType, int10);

                for (int int11 = 0; int11 < int10; int11++) {
                    Object object3 = luaCallFrame.get(int1 + int11);
                    int int12 = int1 + int11 - int2;
                    Object object4 = this.convert(object3, this.varargType);
                    objects[int11] = object4;
                    if (object3 != null && object4 == null) {
                        methodArguments.fail("");
                        return methodArguments;
                    }
                }

                methodArguments.getParams()[int0] = objects;
                int0++;
                int1 += int10;
            }

            return methodArguments;
        }
    }

    @Override
    public int call(LuaCallFrame luaCallFrame, int int0) {
        MethodArguments methodArguments = this.prepareCall(luaCallFrame, int0);
        methodArguments.assertValid();
        int int1 = this.call(methodArguments);
        ReturnValues.put(methodArguments.getReturnValues());
        MethodArguments.put(methodArguments);
        return int1;
    }

    public int call(MethodArguments methodArguments) {
        try {
            ReturnValues returnValues = methodArguments.getReturnValues();
            this.caller.call(methodArguments.getSelf(), returnValues, methodArguments.getParams());
            return returnValues.getNArguments();
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
        } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        } catch (InvocationTargetException invocationTargetException) {
            throw new RuntimeException(invocationTargetException.getCause());
        } catch (InstantiationException instantiationException) {
            throw new RuntimeException(instantiationException);
        }
    }

    private Object convert(Object object, Class<?> clazzx) {
        return object == null ? null : this.manager.fromLuaToJava(object, clazzx);
    }

    private String syntaxErrorMessage(String string1) {
        String string0 = this.getFunctionSyntax();
        if (string0 != null) {
            string1 = string1 + " Correct syntax: " + string0;
        }

        return string1;
    }

    private String newError(int int1, String string1) {
        int int0 = int1 + 1;
        String string0 = string1 + " at argument #" + int0;
        String string2 = this.getParameterName(int1);
        if (string2 != null) {
            string0 = string0 + ", " + string2;
        }

        return string0;
    }

    private String getFunctionSyntax() {
        MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
        return methodDebugInformation != null ? methodDebugInformation.getLuaDescription() : null;
    }

    public MethodDebugInformation getMethodDebugData() {
        ClassDebugInformation classDebugInformation = this.exposer.getDebugdata(this.clazz);
        return classDebugInformation == null ? null : classDebugInformation.getMethods().get(this.caller.getDescriptor());
    }

    private String getParameterName(int int0) {
        MethodDebugInformation methodDebugInformation = this.getMethodDebugData();
        return methodDebugInformation != null ? methodDebugInformation.getParameters().get(int0).getName() : null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getNumMethodParams() {
        return this.numMethodParams;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            LuaJavaInvoker luaJavaInvoker1 = (LuaJavaInvoker)object;
            if (!this.caller.equals(luaJavaInvoker1.caller)) {
                return false;
            } else {
                return !this.clazz.equals(luaJavaInvoker1.clazz) ? false : this.name.equals(luaJavaInvoker1.name);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int int0 = this.clazz.hashCode();
        int0 = 31 * int0 + this.name.hashCode();
        return 31 * int0 + this.caller.hashCode();
    }

    public boolean matchesArgumentTypes(LuaCallFrame luaCallFrame, int int1) {
        int int0 = 0;
        if (this.hasSelf) {
            Object object0 = int1 <= 0 ? null : luaCallFrame.get(0);
            if (object0 == null || !this.clazz.isInstance(object0)) {
                return false;
            }

            int0++;
        }

        if (this.parameterTypes.length != int1 - int0) {
            return false;
        } else {
            for (int int2 = 0; int2 < this.parameterTypes.length; int2++) {
                Object object1 = luaCallFrame.get(int0 + int2);
                Class clazzx = this.parameterTypes[int2];
                if (!clazzx.isInstance(object1)) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean matchesArgumentTypesOrPrimitives(LuaCallFrame luaCallFrame, int int1) {
        int int0 = 0;
        if (this.hasSelf) {
            Object object0 = int1 <= 0 ? null : luaCallFrame.get(0);
            if (object0 == null || !this.clazz.isInstance(object0)) {
                return false;
            }

            int0++;
        }

        if (this.parameterTypes.length != int1 - int0) {
            return false;
        } else {
            for (int int2 = 0; int2 < this.parameterTypes.length; int2++) {
                Object object1 = luaCallFrame.get(int0 + int2);
                Class clazzx = this.parameterTypes[int2];
                if (!clazzx.isInstance(object1)) {
                    if (clazzx.isPrimitive()) {
                        if (object1 == null) {
                            return false;
                        }

                        if (object1 instanceof Double) {
                            if (clazzx == void.class || clazzx == boolean.class) {
                                return false;
                            }
                        } else if (!(object1 instanceof Boolean) || clazzx != boolean.class) {
                            return false;
                        }
                    } else if (object1 != null) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public boolean isAllInt() {
        if (this.parameterTypes != null && this.parameterTypes.length != 0) {
            for (Class clazzx : this.parameterTypes) {
                if (clazzx != int.class) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
