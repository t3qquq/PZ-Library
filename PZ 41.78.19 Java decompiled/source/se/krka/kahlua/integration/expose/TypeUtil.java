// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

    public static String removePackages(String string) {
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("$2");
    }

    public static String getClassName(Type type) {
        if (type instanceof Class clazz) {
            return clazz.isArray() ? getClassName(clazz.getComponentType()) + "[]" : clazz.getName();
        } else if (type instanceof WildcardType wildcardType) {
            Type[] types0 = wildcardType.getUpperBounds();
            Type[] types1 = wildcardType.getLowerBounds();
            return handleBounds("?", types0, types1);
        } else if (type instanceof ParameterizedType parameterizedType) {
            Type[] types2 = parameterizedType.getActualTypeArguments();
            String string = getClassName(parameterizedType.getRawType());
            if (types2.length == 0) {
                return string;
            } else {
                StringBuilder stringBuilder = new StringBuilder(string);
                stringBuilder.append("<");

                for (int int0 = 0; int0 < types2.length; int0++) {
                    if (int0 > 0) {
                        stringBuilder.append(", ");
                    }

                    stringBuilder.append(getClassName(types2[int0]));
                }

                stringBuilder.append(">");
                return stringBuilder.toString();
            }
        } else if (type instanceof TypeVariable typeVariable) {
            return typeVariable.getName();
        } else if (type instanceof GenericArrayType genericArrayType) {
            return getClassName(genericArrayType.getGenericComponentType()) + "[]";
        } else {
            DebugLog.log("got unknown: " + type + ", " + type.getClass());
            return "unknown";
        }
    }

    static String handleBounds(String string, Type[] types0, Type[] types1) {
        if (types0 != null) {
            if (types0.length == 1 && types0[0] == Object.class) {
                return string;
            }

            if (types0.length >= 1) {
                StringBuilder stringBuilder0 = new StringBuilder();
                boolean boolean0 = true;

                for (Type type0 : types0) {
                    if (boolean0) {
                        boolean0 = false;
                    } else {
                        stringBuilder0.append(", ");
                    }

                    stringBuilder0.append(getClassName(type0));
                }

                return string + " extends " + stringBuilder0.toString();
            }
        }

        if (types1 != null && types1.length > 0) {
            StringBuilder stringBuilder1 = new StringBuilder();
            boolean boolean1 = true;

            for (Type type1 : types1) {
                if (boolean1) {
                    boolean1 = false;
                } else {
                    stringBuilder1.append(", ");
                }

                stringBuilder1.append(getClassName(type1));
            }

            return string + " super " + stringBuilder1.toString();
        } else {
            return "unknown type";
        }
    }
}
