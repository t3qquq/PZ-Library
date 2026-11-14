// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.debug.DebugLog;

public class TypeUtil {
    private static final Pattern pattern = Pattern.compile("([\\.a-z0-9]*)\\.([A-Za-z][A-Za-z0-9_]*)");

    public static String removePackages(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("$2");
    }

    public static String getClassName(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz.isArray() ? getClassName(clazz.getComponentType()) + "[]" : clazz.getName();
        } else if (type instanceof WildcardType wildcardType) {
            Type[] upper = wildcardType.getUpperBounds();
            Type[] lower = wildcardType.getLowerBounds();
            return handleBounds("?", upper, lower);
        } else if (type instanceof ParameterizedType paramType) {
            Type[] args = paramType.getActualTypeArguments();
            String raw = getClassName(paramType.getRawType());
            if (args.length == 0) {
                return raw;
            }

            StringBuilder builder = new StringBuilder(raw);
            builder.append("<");

            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(getClassName(args[i]));
            }

            builder.append(">");
            return builder.toString();
        } else if (type instanceof TypeVariable<?> typeVariable) {
            return typeVariable.getName();
        } else if (type instanceof GenericArrayType arrayType) {
            return getClassName(arrayType.getGenericComponentType()) + "[]";
        } else {
            DebugLog.log("got unknown: " + type + ", " + type.getClass());
            return "unknown";
        }
    }

    static String handleBounds(String s, Type[] upper, Type[] lower) {
        if (upper != null) {
            if (upper.length == 1 && upper[0] == Object.class) {
                return s;
            }

            if (upper.length >= 1) {
                StringBuilder list = new StringBuilder();
                boolean first = true;

                for (Type typeExtends : upper) {
                    if (first) {
                        first = false;
                    } else {
                        list.append(", ");
                    }

                    list.append(getClassName(typeExtends));
                }

                return s + " extends " + list.toString();
            }
        }

        if (lower != null && lower.length > 0) {
            StringBuilder list = new StringBuilder();
            boolean first = true;

            for (Type typeExtends : lower) {
                if (first) {
                    first = false;
                } else {
                    list.append(", ");
                }

                list.append(getClassName(typeExtends));
            }

            return s + " super " + list.toString();
        } else {
            return "unknown type";
        }
    }
}
