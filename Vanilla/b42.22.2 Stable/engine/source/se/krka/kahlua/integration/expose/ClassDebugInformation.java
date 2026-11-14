// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;

public class ClassDebugInformation {
    private final Map<String, MethodDebugInformation> methods = new HashMap<>();

    public ClassDebugInformation(Class<?> clazz, ClassParameterInformation parameterInfo) {
        this.addContent(clazz, parameterInfo);
        this.addConstructors(clazz, parameterInfo);
    }

    private void addContent(Class<?> clazz, ClassParameterInformation parameterInfo) {
        if (clazz != null) {
            this.addContent(clazz.getSuperclass(), parameterInfo);

            for (Class<?> iface : clazz.getInterfaces()) {
                this.addContent(iface, parameterInfo);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                LuaMethod methodAnnotation = AnnotationUtil.getAnnotation(method, LuaMethod.class);
                String defaultName = method.getName();
                int modifiers = method.getModifiers();
                Type[] parameterTypes = method.getGenericParameterTypes();
                String descriptor = DescriptorUtil.getDescriptor(method);
                Type returnTypeClass = method.getGenericReturnType();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Desc descriptionAnnotation = AnnotationUtil.getAnnotation(method, Desc.class);
                this.addMethod(
                    parameterInfo,
                    parameterTypes,
                    descriptor,
                    returnTypeClass,
                    parameterAnnotations,
                    getName(methodAnnotation, defaultName),
                    !isGlobal(methodAnnotation, isStatic(modifiers)),
                    descriptionAnnotation
                );
            }
        }
    }

    private void addConstructors(Class<?> clazz, ClassParameterInformation parameterInfo) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            LuaConstructor methodAnnotation = constructor.getAnnotation(LuaConstructor.class);
            String defaultName = "new";
            Type[] parameterTypes = constructor.getGenericParameterTypes();
            String descriptor = DescriptorUtil.getDescriptor(constructor);
            Type returnTypeClass = clazz;
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            Desc descriptionAnnotation = constructor.getAnnotation(Desc.class);
            this.addMethod(
                parameterInfo, parameterTypes, descriptor, returnTypeClass, parameterAnnotations, getName(methodAnnotation, "new"), true, descriptionAnnotation
            );
        }
    }

    private void addMethod(
        ClassParameterInformation parameterInfo,
        Type[] parameterTypes,
        String descriptor,
        Type returnTypeClass,
        Annotation[][] parameterAnnotations,
        String luaName,
        boolean method,
        Desc descriptionAnnotation
    ) {
        MethodParameterInformation parameterNames = parameterInfo.methods.get(descriptor);
        if (!this.methods.containsKey(descriptor)) {
            if (parameterNames != null) {
                List<MethodParameter> parameters = new ArrayList<>();

                for (int i = 0; i < parameterTypes.length; i++) {
                    Type type = parameterTypes[i];
                    String name = parameterNames.getName(i);
                    String typeName = TypeUtil.getClassName(type);
                    String description = this.getDescription(parameterAnnotations[i]);
                    parameters.add(new MethodParameter(name, typeName, description));
                }

                String returnType = TypeUtil.getClassName(returnTypeClass);
                String returnDescription = getDescription(descriptionAnnotation);
                MethodDebugInformation debugInfo = new MethodDebugInformation(luaName, method, parameters, returnType, returnDescription);
                this.methods.put(descriptor, debugInfo);
            }
        }
    }

    private String getDescription(Annotation[] parameterAnnotation) {
        for (Annotation annotation : parameterAnnotation) {
            if (annotation != null && annotation instanceof Desc desc) {
                return getDescription(desc);
            }
        }

        return null;
    }

    private static String getDescription(Desc annotation) {
        return annotation != null ? annotation.value() : null;
    }

    private static boolean isStatic(int modifiers) {
        return (modifiers & 8) != 0;
    }

    private static boolean isGlobal(LuaMethod annotation, boolean defaultValue) {
        return annotation != null ? annotation.global() : defaultValue;
    }

    private static String getName(LuaMethod annotation, String defaultName) {
        if (annotation != null) {
            String name = annotation.name();
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }

        return defaultName;
    }

    private static String getName(LuaConstructor annotation, String defaultName) {
        if (annotation != null) {
            String name = annotation.name();
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }

        return defaultName;
    }

    public Map<String, MethodDebugInformation> getMethods() {
        return this.methods;
    }
}
