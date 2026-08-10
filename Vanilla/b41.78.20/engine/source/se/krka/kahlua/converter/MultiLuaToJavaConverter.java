/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.converter.LuaToJavaConverter;

public class MultiLuaToJavaConverter<LuaType, JavaType>
implements LuaToJavaConverter<LuaType, JavaType> {
    private final List<LuaToJavaConverter<LuaType, JavaType>> converters = new ArrayList<LuaToJavaConverter<LuaType, JavaType>>();
    private final Class<LuaType> luaType;
    private final Class<JavaType> javaType;

    public MultiLuaToJavaConverter(Class<LuaType> clazz, Class<JavaType> clazz2) {
        this.luaType = clazz;
        this.javaType = clazz2;
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
    public JavaType fromLuaToJava(LuaType LuaType, Class<JavaType> clazz) {
        for (LuaToJavaConverter<LuaType, JavaType> luaToJavaConverter : this.converters) {
            JavaType JavaType = luaToJavaConverter.fromLuaToJava(LuaType, clazz);
            if (JavaType == null) continue;
            return JavaType;
        }
        return null;
    }

    public void add(LuaToJavaConverter<LuaType, JavaType> luaToJavaConverter) {
        this.converters.add(luaToJavaConverter);
    }
}

