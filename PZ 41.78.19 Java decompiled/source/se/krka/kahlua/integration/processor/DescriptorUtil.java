// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import javax.lang.model.element.VariableElement;

public class DescriptorUtil {
    public static String getDescriptor(String string1, List<? extends VariableElement> list) {
        String string0 = "";

        for (VariableElement variableElement : list) {
            string0 = string0 + ":" + variableElement.asType().toString();
        }

        return string1 + string0;
    }

    public static String getDescriptor(Constructor constructor) {
        String string = getParameters(constructor.getParameterTypes());
        return "new" + string;
    }

    public static String getDescriptor(Method method) {
        String string = getParameters(method.getParameterTypes());
        return method.getName() + string;
    }

    private static String getParameters(Class<?>[] clazzs) {
        String string = "";

        for (Class clazz : clazzs) {
            string = string + ":" + clazz.getName();
        }

        return string;
    }
}
