// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

import java.util.ArrayList;
import java.util.List;

public class MultiJavaToLuaConverter<JavaType> implements JavaToLuaConverter<JavaType> {
    private final List<JavaToLuaConverter> converters = new ArrayList<>();
    private final Class<JavaType> clazz;

    public MultiJavaToLuaConverter(Class<JavaType> clazzx) {
        this.clazz = clazzx;
    }

    @Override
    public Class<JavaType> getJavaType() {
        return this.clazz;
    }

    @Override
    public Object fromJavaToLua(JavaType object1) {
        for (JavaToLuaConverter javaToLuaConverter : this.converters) {
            Object object0 = javaToLuaConverter.fromJavaToLua(object1);
            if (object0 != null) {
                return object0;
            }
        }

        return null;
    }

    public void add(JavaToLuaConverter javaToLuaConverter) {
        this.converters.add(javaToLuaConverter);
    }
}
