/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.debug.DebugLog
 */
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
        if (type instanceof Class) {
            Class clazz = (Class)type;
            if (clazz.isArray()) {
                return TypeUtil.getClassName(clazz.getComponentType()) + "[]";
            }
            return clazz.getName();
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            Type[] typeArray = wildcardType.getUpperBounds();
            Type[] typeArray2 = wildcardType.getLowerBounds();
            return TypeUtil.handleBounds("?", typeArray, typeArray2);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type[] typeArray = parameterizedType.getActualTypeArguments();
            String string = TypeUtil.getClassName(parameterizedType.getRawType());
            if (typeArray.length == 0) {
                return string;
            }
            StringBuilder stringBuilder = new StringBuilder(string);
            stringBuilder.append("<");
            for (int i = 0; i < typeArray.length; ++i) {
                if (i > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(TypeUtil.getClassName(typeArray[i]));
            }
            stringBuilder.append(">");
            return stringBuilder.toString();
        }
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)type;
            return typeVariable.getName();
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            return TypeUtil.getClassName(genericArrayType.getGenericComponentType()) + "[]";
        }
        DebugLog.log((String)("got unknown: " + type + ", " + type.getClass()));
        return "unknown";
    }

    static String handleBounds(String string, Type[] typeArray, Type[] typeArray2) {
        if (typeArray != null) {
            if (typeArray.length == 1 && typeArray[0] == Object.class) {
                return string;
            }
            if (typeArray.length >= 1) {
                StringBuilder stringBuilder = new StringBuilder();
                boolean bl = true;
                for (Type type : typeArray) {
                    if (bl) {
                        bl = false;
                    } else {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(TypeUtil.getClassName(type));
                }
                return string + " extends " + stringBuilder.toString();
            }
        }
        if (typeArray2 != null && typeArray2.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean bl = true;
            for (Type type : typeArray2) {
                if (bl) {
                    bl = false;
                } else {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(TypeUtil.getClassName(type));
            }
            return string + " super " + stringBuilder.toString();
        }
        return "unknown type";
    }
}

