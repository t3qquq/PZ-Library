// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

import java.util.HashMap;
import java.util.Map;

public class KahluaConverterManager {
    private static final Map<Class, Class> PRIMITIVE_CLASS = new HashMap<>();
    private static final Map<Class, LuaToJavaConverter> LUA_NULL_MAP = new HashMap<>();
    private final Map<Class, Map<Class, LuaToJavaConverter>> luaToJava = new HashMap<>();
    private final Map<Class, Map<Class, LuaToJavaConverter>> luatoJavaCache = new HashMap<>();
    private static final JavaToLuaConverter NULL_CONVERTER = new JavaToLuaConverter<Object>() {
        @Override
        public Object fromJavaToLua(Object var1) {
            return null;
        }

        @Override
        public Class<Object> getJavaType() {
            return Object.class;
        }
    };
    private final Map<Class, JavaToLuaConverter> javaToLua = new HashMap<>();
    private final Map<Class, JavaToLuaConverter> javaToLuaCache = new HashMap<>();

    public void addLuaConverter(LuaToJavaConverter arg0) {
        Map map = this.getOrCreate(this.luaToJava, arg0.getLuaType());
        Class clazz = arg0.getJavaType();
        LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)map.get(clazz);
        if (luaToJavaConverter != null) {
            if (luaToJavaConverter instanceof MultiLuaToJavaConverter) {
                ((MultiLuaToJavaConverter)luaToJavaConverter).add(arg0);
            } else {
                MultiLuaToJavaConverter multiLuaToJavaConverter = new MultiLuaToJavaConverter(arg0.getLuaType(), clazz);
                multiLuaToJavaConverter.add(luaToJavaConverter);
                multiLuaToJavaConverter.add(arg0);
                map.put(clazz, multiLuaToJavaConverter);
            }
        } else {
            map.put(clazz, arg0);
        }

        this.luatoJavaCache.clear();
    }

    public void addJavaConverter(JavaToLuaConverter arg0) {
        Class clazz = arg0.getJavaType();
        JavaToLuaConverter javaToLuaConverter = this.javaToLua.get(clazz);
        if (javaToLuaConverter != null) {
            if (javaToLuaConverter instanceof MultiJavaToLuaConverter) {
                ((MultiJavaToLuaConverter)javaToLuaConverter).add(arg0);
            } else {
                MultiJavaToLuaConverter multiJavaToLuaConverter = new MultiJavaToLuaConverter(clazz);
                multiJavaToLuaConverter.add(javaToLuaConverter);
                multiJavaToLuaConverter.add(arg0);
                this.javaToLua.put(clazz, multiJavaToLuaConverter);
            }
        } else {
            this.javaToLua.put(clazz, arg0);
        }

        this.javaToLuaCache.clear();
    }

    private Map<Class, LuaToJavaConverter> getOrCreate(Map<Class, Map<Class, LuaToJavaConverter>> map, Class clazz) {
        Object object = (Map)map.get(clazz);
        if (object == null) {
            object = new HashMap();
            map.put(clazz, object);
        }

        return (Map<Class, LuaToJavaConverter>)object;
    }

    public <T> T fromLuaToJava(Object object0, Class<T> clazz0) {
        if (object0 == null) {
            return null;
        } else {
            if (clazz0.isPrimitive()) {
                clazz0 = PRIMITIVE_CLASS.get(clazz0);
            }

            if (clazz0.isInstance(object0)) {
                return (T)object0;
            } else {
                Class clazz1 = object0.getClass();
                Map map = this.getLuaCache(clazz1);

                for (Class clazz2 = clazz0; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
                    LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)map.get(clazz2);
                    if (luaToJavaConverter != null) {
                        Object object1 = luaToJavaConverter.fromLuaToJava(object0, clazz0);
                        if (object1 != null) {
                            return (T)object1;
                        }
                    }
                }

                return this.tryInterfaces(map, clazz0, object0);
            }
        }
    }

    private <T> T tryInterfaces(Map<Class, LuaToJavaConverter> map, Class<T> clazz0, Object object1) {
        if (clazz0 == null) {
            return null;
        } else {
            LuaToJavaConverter luaToJavaConverter = (LuaToJavaConverter)map.get(clazz0);
            if (luaToJavaConverter != null) {
                Object object0 = luaToJavaConverter.fromLuaToJava(object1, clazz0);
                if (object0 != null) {
                    return (T)object0;
                }
            }

            for (Class clazz1 : clazz0.getInterfaces()) {
                Object object2 = this.tryInterfaces(map, clazz1, object1);
                if (object2 != null) {
                    return (T)object2;
                }
            }

            return this.tryInterfaces(map, clazz0.getSuperclass(), object1);
        }
    }

    private Map<Class, LuaToJavaConverter> createLuaCache(Class<?> clazz0) {
        HashMap hashMap = new HashMap();
        this.luatoJavaCache.put(clazz0, hashMap);
        hashMap.putAll(this.getLuaCache(clazz0.getSuperclass()));

        for (Class clazz1 : clazz0.getInterfaces()) {
            hashMap.putAll(this.getLuaCache(clazz1));
        }

        Map map = this.luaToJava.get(clazz0);
        if (map != null) {
            hashMap.putAll(map);
        }

        return hashMap;
    }

    private Map<Class, LuaToJavaConverter> getLuaCache(Class<?> clazz) {
        if (clazz == null) {
            return LUA_NULL_MAP;
        } else {
            Map map = this.luatoJavaCache.get(clazz);
            if (map == null) {
                map = this.createLuaCache(clazz);
            }

            return map;
        }
    }

    public Object fromJavaToLua(Object arg0) {
        if (arg0 == null) {
            return null;
        } else {
            Class clazz = arg0.getClass();
            JavaToLuaConverter javaToLuaConverter = this.getJavaCache(clazz);

            try {
                Object object = javaToLuaConverter.fromJavaToLua(arg0);
                return object == null ? arg0 : object;
            } catch (StackOverflowError stackOverflowError) {
                throw new RuntimeException("Could not convert " + arg0 + ": it contained recursive elements.");
            }
        }
    }

    private JavaToLuaConverter getJavaCache(Class clazz) {
        if (clazz == null) {
            return NULL_CONVERTER;
        } else {
            JavaToLuaConverter javaToLuaConverter = this.javaToLuaCache.get(clazz);
            if (javaToLuaConverter == null) {
                javaToLuaConverter = this.createJavaCache(clazz);
                this.javaToLuaCache.put(clazz, javaToLuaConverter);
            }

            return javaToLuaConverter;
        }
    }

    private JavaToLuaConverter createJavaCache(Class clazz0) {
        JavaToLuaConverter javaToLuaConverter = this.javaToLua.get(clazz0);
        if (javaToLuaConverter != null) {
            return javaToLuaConverter;
        } else {
            for (Class clazz1 : clazz0.getInterfaces()) {
                javaToLuaConverter = this.getJavaCache(clazz1);
                if (javaToLuaConverter != NULL_CONVERTER) {
                    return javaToLuaConverter;
                }
            }

            return this.getJavaCache(clazz0.getSuperclass());
        }
    }

    static {
        PRIMITIVE_CLASS.put(boolean.class, Boolean.class);
        PRIMITIVE_CLASS.put(byte.class, Byte.class);
        PRIMITIVE_CLASS.put(char.class, Character.class);
        PRIMITIVE_CLASS.put(short.class, short.class);
        PRIMITIVE_CLASS.put(int.class, Integer.class);
        PRIMITIVE_CLASS.put(long.class, Long.class);
        PRIMITIVE_CLASS.put(float.class, Float.class);
        PRIMITIVE_CLASS.put(double.class, Double.class);
    }
}
