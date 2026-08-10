/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.converter;

import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.converter.JavaToLuaConverter;
import se.krka.kahlua.converter.LuaToJavaConverter;
import se.krka.kahlua.converter.MultiJavaToLuaConverter;
import se.krka.kahlua.converter.MultiLuaToJavaConverter;

public class KahluaConverterManager {
    private static final Map<Class, Class> PRIMITIVE_CLASS = new HashMap<Class, Class>();
    private static final Map<Class, LuaToJavaConverter> LUA_NULL_MAP;
    private final Map<Class, Map<Class, LuaToJavaConverter>> luaToJava = new HashMap<Class, Map<Class, LuaToJavaConverter>>();
    private final Map<Class, Map<Class, LuaToJavaConverter>> luatoJavaCache = new HashMap<Class, Map<Class, LuaToJavaConverter>>();
    private static final JavaToLuaConverter NULL_CONVERTER;
    private final Map<Class, JavaToLuaConverter> javaToLua = new HashMap<Class, JavaToLuaConverter>();
    private final Map<Class, JavaToLuaConverter> javaToLuaCache = new HashMap<Class, JavaToLuaConverter>();

    public void addLuaConverter(LuaToJavaConverter luaToJavaConverter) {
        Class clazz;
        Map<Class, LuaToJavaConverter> map = this.getOrCreate(this.luaToJava, luaToJavaConverter.getLuaType());
        LuaToJavaConverter luaToJavaConverter2 = map.get(clazz = luaToJavaConverter.getJavaType());
        if (luaToJavaConverter2 != null) {
            if (luaToJavaConverter2 instanceof MultiLuaToJavaConverter) {
                ((MultiLuaToJavaConverter)luaToJavaConverter2).add(luaToJavaConverter);
            } else {
                MultiLuaToJavaConverter multiLuaToJavaConverter = new MultiLuaToJavaConverter(luaToJavaConverter.getLuaType(), clazz);
                multiLuaToJavaConverter.add(luaToJavaConverter2);
                multiLuaToJavaConverter.add(luaToJavaConverter);
                map.put(clazz, multiLuaToJavaConverter);
            }
        } else {
            map.put(clazz, luaToJavaConverter);
        }
        this.luatoJavaCache.clear();
    }

    public void addJavaConverter(JavaToLuaConverter javaToLuaConverter) {
        Class clazz = javaToLuaConverter.getJavaType();
        JavaToLuaConverter javaToLuaConverter2 = this.javaToLua.get(clazz);
        if (javaToLuaConverter2 != null) {
            if (javaToLuaConverter2 instanceof MultiJavaToLuaConverter) {
                ((MultiJavaToLuaConverter)javaToLuaConverter2).add(javaToLuaConverter);
            } else {
                MultiJavaToLuaConverter multiJavaToLuaConverter = new MultiJavaToLuaConverter(clazz);
                multiJavaToLuaConverter.add(javaToLuaConverter2);
                multiJavaToLuaConverter.add(javaToLuaConverter);
                this.javaToLua.put(clazz, multiJavaToLuaConverter);
            }
        } else {
            this.javaToLua.put(clazz, javaToLuaConverter);
        }
        this.javaToLuaCache.clear();
    }

    private Map<Class, LuaToJavaConverter> getOrCreate(Map<Class, Map<Class, LuaToJavaConverter>> map, Class clazz) {
        Map<Class, LuaToJavaConverter> map2 = map.get(clazz);
        if (map2 == null) {
            map2 = new HashMap<Class, LuaToJavaConverter>();
            map.put(clazz, map2);
        }
        return map2;
    }

    public <T> T fromLuaToJava(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        if (clazz.isPrimitive()) {
            clazz = PRIMITIVE_CLASS.get(clazz);
        }
        if (clazz.isInstance(object)) {
            return (T)object;
        }
        Class<?> clazz2 = object.getClass();
        Map<Class, LuaToJavaConverter> map = this.getLuaCache(clazz2);
        for (Class clazz3 = clazz; clazz3 != null; clazz3 = clazz3.getSuperclass()) {
            Object t;
            LuaToJavaConverter luaToJavaConverter = map.get(clazz3);
            if (luaToJavaConverter == null || (t = luaToJavaConverter.fromLuaToJava(object, clazz)) == null) continue;
            return t;
        }
        return this.tryInterfaces(map, clazz, object);
    }

    private <T> T tryInterfaces(Map<Class, LuaToJavaConverter> map, Class<T> clazz, Object object) {
        T object2;
        if (clazz == null) {
            return null;
        }
        LuaToJavaConverter luaToJavaConverter = map.get(clazz);
        if (luaToJavaConverter != null && (object2 = luaToJavaConverter.fromLuaToJava(object, clazz)) != null) {
            return object2;
        }
        for (Class<?> clazz2 : clazz.getInterfaces()) {
            Object obj = this.tryInterfaces(map, clazz2, object);
            if (obj == null) continue;
            return (T)obj;
        }
        return this.tryInterfaces(map, clazz.getSuperclass(), object);
    }

    private Map<Class, LuaToJavaConverter> createLuaCache(Class<?> clazz) {
        HashMap<Class, LuaToJavaConverter> hashMap = new HashMap<Class, LuaToJavaConverter>();
        this.luatoJavaCache.put(clazz, hashMap);
        hashMap.putAll(this.getLuaCache(clazz.getSuperclass()));
        for (Class<?> clazz2 : clazz.getInterfaces()) {
            hashMap.putAll(this.getLuaCache(clazz2));
        }
        Map<Class, LuaToJavaConverter> map = this.luaToJava.get(clazz);
        if (map != null) {
            hashMap.putAll(map);
        }
        return hashMap;
    }

    private Map<Class, LuaToJavaConverter> getLuaCache(Class<?> clazz) {
        if (clazz == null) {
            return LUA_NULL_MAP;
        }
        Map<Class, LuaToJavaConverter> map = this.luatoJavaCache.get(clazz);
        if (map == null) {
            map = this.createLuaCache(clazz);
        }
        return map;
    }

    public Object fromJavaToLua(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        JavaToLuaConverter javaToLuaConverter = this.getJavaCache(clazz);
        try {
            Object object2 = javaToLuaConverter.fromJavaToLua(object);
            if (object2 == null) {
                return object;
            }
            return object2;
        }
        catch (StackOverflowError stackOverflowError) {
            throw new RuntimeException("Could not convert " + object + ": it contained recursive elements.");
        }
    }

    private JavaToLuaConverter getJavaCache(Class clazz) {
        if (clazz == null) {
            return NULL_CONVERTER;
        }
        JavaToLuaConverter javaToLuaConverter = this.javaToLuaCache.get(clazz);
        if (javaToLuaConverter == null) {
            javaToLuaConverter = this.createJavaCache(clazz);
            this.javaToLuaCache.put(clazz, javaToLuaConverter);
        }
        return javaToLuaConverter;
    }

    private JavaToLuaConverter createJavaCache(Class clazz) {
        JavaToLuaConverter javaToLuaConverter = this.javaToLua.get(clazz);
        if (javaToLuaConverter != null) {
            return javaToLuaConverter;
        }
        for (Class<?> clazz2 : clazz.getInterfaces()) {
            javaToLuaConverter = this.getJavaCache(clazz2);
            if (javaToLuaConverter == NULL_CONVERTER) continue;
            return javaToLuaConverter;
        }
        return this.getJavaCache(clazz.getSuperclass());
    }

    static {
        PRIMITIVE_CLASS.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_CLASS.put(Byte.TYPE, Byte.class);
        PRIMITIVE_CLASS.put(Character.TYPE, Character.class);
        PRIMITIVE_CLASS.put(Short.TYPE, Short.TYPE);
        PRIMITIVE_CLASS.put(Integer.TYPE, Integer.class);
        PRIMITIVE_CLASS.put(Long.TYPE, Long.class);
        PRIMITIVE_CLASS.put(Float.TYPE, Float.class);
        PRIMITIVE_CLASS.put(Double.TYPE, Double.class);
        LUA_NULL_MAP = new HashMap<Class, LuaToJavaConverter>();
        NULL_CONVERTER = new JavaToLuaConverter<Object>(){

            @Override
            public Object fromJavaToLua(Object object) {
                return null;
            }

            @Override
            public Class<Object> getJavaType() {
                return Object.class;
            }
        };
    }
}

