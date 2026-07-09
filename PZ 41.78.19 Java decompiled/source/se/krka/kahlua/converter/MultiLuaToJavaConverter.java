// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.List;

public class MultiLuaToJavaConverter<LuaType, JavaType> implements LuaToJavaConverter<LuaType, JavaType> {
    private final List<LuaToJavaConverter<LuaType, JavaType>> converters = new ArrayList<>();
    private final Class<LuaType> luaType;
    private final Class<JavaType> javaType;

    public MultiLuaToJavaConverter(Class<LuaType> clazz0, Class<JavaType> clazz1) {
        this.luaType = clazz0;
        this.javaType = clazz1;
    }

    @Override
    public Class<LuaType> getLuaType() {
        return this.luaType;
    }

    @Override
    public Class<JavaType> getJavaType() {
        return this.javaType;
    }

    @Override
    public JavaType fromLuaToJava(LuaType object1, Class<JavaType> clazz) {
        for (LuaToJavaConverter luaToJavaConverter : this.converters) {
            Object object0 = luaToJavaConverter.fromLuaToJava(object1, clazz);
            if (object0 != null) {
                return (JavaType)object0;
            }
        }

        return null;
    }

    public void add(LuaToJavaConverter<LuaType, JavaType> luaToJavaConverter) {
        this.converters.add(luaToJavaConverter);
    }
}
