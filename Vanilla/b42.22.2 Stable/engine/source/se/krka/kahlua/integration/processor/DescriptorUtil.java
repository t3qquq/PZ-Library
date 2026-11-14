// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import javax.lang.model.element.VariableElement;

public class DescriptorUtil {
    public static String getDescriptor(String methodName, List<? extends VariableElement> parameters) {
        String parameterString = "";

        for (VariableElement parameter : parameters) {
            parameterString = parameterString + ":" + parameter.asType().toString();
        }

        return methodName + parameterString;
    }

    public static String getDescriptor(Constructor<?> constructor) {
        String parameters = getParameters(constructor.getParameterTypes());
        return "new" + parameters;
    }

    public static String getDescriptor(Method method) {
        String parameters = getParameters(method.getParameterTypes());
        return method.getName() + parameters;
    }

    private static String getParameters(Class<?>[] parameterTypes) {
        String parameters = "";

        for (Class<?> clazz : parameterTypes) {
            parameters = parameters + ":" + clazz.getName();
        }

        return parameters;
    }
}
