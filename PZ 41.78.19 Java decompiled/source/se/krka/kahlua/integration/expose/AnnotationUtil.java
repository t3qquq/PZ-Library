// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtil {
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> clazz) {
        return getAnnotation(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), clazz);
    }

    private static <T extends Annotation> T getAnnotation(Class<?> clazz0, String string, Class<?>[] clazzs, Class<T> clazz1) {
        if (clazz0 == null) {
            return null;
        } else {
            try {
                Method method = clazz0.getMethod(string, clazzs);
                Annotation annotation = method.getAnnotation(clazz1);
                if (annotation != null) {
                    return (T)annotation;
                } else {
                    for (Class clazz2 : clazz0.getInterfaces()) {
                        annotation = getAnnotation(clazz2, string, clazzs, clazz1);
                        if (annotation != null) {
                            return (T)annotation;
                        }
                    }

                    return getAnnotation(clazz0.getSuperclass(), string, clazzs, clazz1);
                }
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
    }
}
