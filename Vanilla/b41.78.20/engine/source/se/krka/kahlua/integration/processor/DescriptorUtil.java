/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import javax.lang.model.element.VariableElement;

public class DescriptorUtil {
    public static String getDescriptor(String string, List<? extends VariableElement> list) {
        Object object = "";
        for (VariableElement variableElement : list) {
            object = (String)object + ":" + variableElement.asType().toString();
        }
        return string + (String)object;
    }

    public static String getDescriptor(Constructor constructor) {
        String string = DescriptorUtil.getParameters(constructor.getParameterTypes());
        return "new" + string;
    }

    public static String getDescriptor(Method method) {
        String string = DescriptorUtil.getParameters(method.getParameterTypes());
        return method.getName() + string;
    }

    private static String getParameters(Class<?>[] classArray) {
        Object object = "";
        for (Class<?> clazz : classArray) {
            object = (String)object + ":" + clazz.getName();
        }
        return object;
    }
}

