// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.converter;

public interface LuaToJavaConverter<LuaType, JavaType> {
    Class<LuaType> getLuaType();

    Class<JavaType> getJavaType();

    JavaType fromLuaToJava(LuaType var1, Class<JavaType> var2);
}
