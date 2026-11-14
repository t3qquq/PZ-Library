// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.List;

public class MultiJavaToLuaConverter<JavaType> implements JavaToLuaConverter<JavaType> {
    private final List<JavaToLuaConverter> converters = new ArrayList<>();
    private final Class<JavaType> clazz;

    public MultiJavaToLuaConverter(Class<JavaType> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<JavaType> getJavaType() {
        return this.clazz;
    }

    @Override
    public Object fromJavaToLua(JavaType javaObject) {
        for (JavaToLuaConverter converter : this.converters) {
            Object res = converter.fromJavaToLua(javaObject);
            if (res != null) {
                return res;
            }
        }

        return null;
    }

    public void add(JavaToLuaConverter converter) {
        this.converters.add(converter);
    }
}
