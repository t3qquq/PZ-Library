/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.AnnotationUtil;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.MethodParameter;
import se.krka.kahlua.integration.expose.TypeUtil;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;

public class ClassDebugInformation {
    private final Map<String, MethodDebugInformation> methods = new HashMap<String, MethodDebugInformation>();

    public ClassDebugInformation(Class<?> clazz, ClassParameterInformation classParameterInformation) {
        this.addContent(clazz, classParameterInformation);
        this.addConstructors(clazz, classParameterInformation);
    }

    private void addContent(Class<?> clazz, ClassParameterInformation classParameterInformation) {
        if (clazz == null) {
            return;
        }
        this.addContent(clazz.getSuperclass(), classParameterInformation);
        for (Class<?> genericDeclaration : clazz.getInterfaces()) {
            this.addContent(genericDeclaration, classParameterInformation);
        }
        for (GenericDeclaration genericDeclaration : clazz.getDeclaredMethods()) {
            LuaMethod luaMethod = AnnotationUtil.getAnnotation((Method)genericDeclaration, LuaMethod.class);
            String string = ((Method)genericDeclaration).getName();
            int n = ((Method)genericDeclaration).getModifiers();
            Type[] typeArray = ((Method)genericDeclaration).getGenericParameterTypes();
            String string2 = DescriptorUtil.getDescriptor((Method)genericDeclaration);
            Type type = ((Method)genericDeclaration).getGenericReturnType();
            Annotation[][] annotationArray = ((Method)genericDeclaration).getParameterAnnotations();
            Desc desc = AnnotationUtil.getAnnotation((Method)genericDeclaration, Desc.class);
            this.addMethod(classParameterInformation, typeArray, string2, type, annotationArray, ClassDebugInformation.getName(luaMethod, string), !ClassDebugInformation.isGlobal(luaMethod, ClassDebugInformation.isStatic(n)), desc);
        }
    }

    private void addConstructors(Class<?> clazz, ClassParameterInformation classParameterInformation) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            LuaConstructor luaConstructor = constructor.getAnnotation(LuaConstructor.class);
            String string = "new";
            Type[] typeArray = constructor.getGenericParameterTypes();
            String string2 = DescriptorUtil.getDescriptor(constructor);
            Class<?> clazz2 = clazz;
            Annotation[][] annotationArray = constructor.getParameterAnnotations();
            Desc desc = constructor.getAnnotation(Desc.class);
            this.addMethod(classParameterInformation, typeArray, string2, clazz2, annotationArray, ClassDebugInformation.getName(luaConstructor, string), true, desc);
        }
    }

    private void addMethod(ClassParameterInformation classParameterInformation, Type[] typeArray, String string, Type type, Annotation[][] annotationArray, String string2, boolean bl, Desc desc) {
        Object object;
        Object object2;
        MethodParameterInformation methodParameterInformation = classParameterInformation.methods.get(string);
        if (this.methods.containsKey(string)) {
            return;
        }
        if (methodParameterInformation == null) {
            return;
        }
        ArrayList<MethodParameter> arrayList = new ArrayList<MethodParameter>();
        for (int i = 0; i < typeArray.length; ++i) {
            object2 = typeArray[i];
            object = methodParameterInformation.getName(i);
            String string3 = TypeUtil.getClassName((Type)object2);
            String string4 = this.getDescription(annotationArray[i]);
            arrayList.add(new MethodParameter((String)object, string3, string4));
        }
        String string5 = TypeUtil.getClassName(type);
        object2 = ClassDebugInformation.getDescription(desc);
        object = new MethodDebugInformation(string2, bl, arrayList, string5, (String)object2);
        this.methods.put(string, (MethodDebugInformation)object);
    }

    private String getDescription(Annotation[] annotationArray) {
        for (Annotation annotation : annotationArray) {
            if (annotation == null || !(annotation instanceof Desc)) continue;
            return ClassDebugInformation.getDescription((Desc)annotation);
        }
        return null;
    }

    private static String getDescription(Desc desc) {
        if (desc != null) {
            return desc.value();
        }
        return null;
    }

    private static boolean isStatic(int n) {
        return (n & 8) != 0;
    }

    private static boolean isGlobal(LuaMethod luaMethod, boolean bl) {
        if (luaMethod != null) {
            return luaMethod.global();
        }
        return bl;
    }

    private static String getName(LuaMethod luaMethod, String string) {
        String string2;
        if (luaMethod != null && (string2 = luaMethod.name()) != null && string2.length() > 0) {
            return string2;
        }
        return string;
    }

    private static String getName(LuaConstructor luaConstructor, String string) {
        String string2;
        if (luaConstructor != null && (string2 = luaConstructor.name()) != null && string2.length() > 0) {
            return string2;
        }
        return string;
    }

    public Map<String, MethodDebugInformation> getMethods() {
        return this.methods;
    }
}

