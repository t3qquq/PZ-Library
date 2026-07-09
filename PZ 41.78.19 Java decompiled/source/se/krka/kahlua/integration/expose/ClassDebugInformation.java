// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;

public class ClassDebugInformation {
    private final Map<String, MethodDebugInformation> methods = new HashMap<>();

    public ClassDebugInformation(Class<?> clazz, ClassParameterInformation classParameterInformation) {
        this.addContent(clazz, classParameterInformation);
        this.addConstructors(clazz, classParameterInformation);
    }

    private void addContent(Class<?> clazz0, ClassParameterInformation classParameterInformation) {
        if (clazz0 != null) {
            this.addContent(clazz0.getSuperclass(), classParameterInformation);

            for (Class clazz1 : clazz0.getInterfaces()) {
                this.addContent(clazz1, classParameterInformation);
            }

            for (Method method : clazz0.getDeclaredMethods()) {
                LuaMethod luaMethod = AnnotationUtil.getAnnotation(method, LuaMethod.class);
                String string0 = method.getName();
                int int0 = method.getModifiers();
                Type[] types = method.getGenericParameterTypes();
                String string1 = DescriptorUtil.getDescriptor(method);
                Type type = method.getGenericReturnType();
                Annotation[][] annotations = method.getParameterAnnotations();
                Desc desc = AnnotationUtil.getAnnotation(method, Desc.class);
                this.addMethod(
                    classParameterInformation, types, string1, type, annotations, getName(luaMethod, string0), !isGlobal(luaMethod, isStatic(int0)), desc
                );
            }
        }
    }

    private void addConstructors(Class<?> clazz, ClassParameterInformation classParameterInformation) {
        for (Constructor constructor : clazz.getConstructors()) {
            LuaConstructor luaConstructor = constructor.getAnnotation(LuaConstructor.class);
            String string0 = "new";
            Type[] types = constructor.getGenericParameterTypes();
            String string1 = DescriptorUtil.getDescriptor(constructor);
            Annotation[][] annotations = constructor.getParameterAnnotations();
            Desc desc = constructor.getAnnotation(Desc.class);
            this.addMethod(classParameterInformation, types, string1, clazz, annotations, getName(luaConstructor, string0), true, desc);
        }
    }

    private void addMethod(
        ClassParameterInformation classParameterInformation,
        Type[] types,
        String string0,
        Type type1,
        Annotation[][] annotations,
        String string6,
        boolean boolean0,
        Desc desc
    ) {
        MethodParameterInformation methodParameterInformation = classParameterInformation.methods.get(string0);
        if (!this.methods.containsKey(string0)) {
            if (methodParameterInformation != null) {
                ArrayList arrayList = new ArrayList();

                for (int int0 = 0; int0 < types.length; int0++) {
                    Type type0 = types[int0];
                    String string1 = methodParameterInformation.getName(int0);
                    String string2 = TypeUtil.getClassName(type0);
                    String string3 = this.getDescription(annotations[int0]);
                    arrayList.add(new MethodParameter(string1, string2, string3));
                }

                String string4 = TypeUtil.getClassName(type1);
                String string5 = getDescription(desc);
                MethodDebugInformation methodDebugInformation = new MethodDebugInformation(string6, boolean0, arrayList, string4, string5);
                this.methods.put(string0, methodDebugInformation);
            }
        }
    }

    private String getDescription(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation != null && annotation instanceof Desc) {
                return getDescription((Desc)annotation);
            }
        }

        return null;
    }

    private static String getDescription(Desc desc) {
        return desc != null ? desc.value() : null;
    }

    private static boolean isStatic(int int0) {
        return (int0 & 8) != 0;
    }

    private static boolean isGlobal(LuaMethod luaMethod, boolean boolean0) {
        return luaMethod != null ? luaMethod.global() : boolean0;
    }

    private static String getName(LuaMethod luaMethod, String string1) {
        if (luaMethod != null) {
            String string0 = luaMethod.name();
            if (string0 != null && string0.length() > 0) {
                return string0;
            }
        }

        return string1;
    }

    private static String getName(LuaConstructor luaConstructor, String string1) {
        if (luaConstructor != null) {
            String string0 = luaConstructor.name();
            if (string0 != null && string0.length() > 0) {
                return string0;
            }
        }

        return string1;
    }

    public Map<String, MethodDebugInformation> getMethods() {
        return this.methods;
    }
}
