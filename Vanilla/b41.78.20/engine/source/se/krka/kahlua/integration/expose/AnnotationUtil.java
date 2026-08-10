/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtil {
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> clazz) {
        return AnnotationUtil.getAnnotation(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), clazz);
    }

    private static <T extends Annotation> T getAnnotation(Class<?> clazz, String string, Class<?>[] classArray, Class<T> clazz2) {
        if (clazz == null) {
            return null;
        }
        try {
            Method method = clazz.getMethod(string, classArray);
            T t = method.getAnnotation(clazz2);
            if (t != null) {
                return t;
            }
            for (Class<?> clazz3 : clazz.getInterfaces()) {
                t = AnnotationUtil.getAnnotation(clazz3, string, classArray, clazz2);
                if (t == null) continue;
                return t;
            }
            return AnnotationUtil.getAnnotation(clazz.getSuperclass(), string, classArray, clazz2);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }
}

