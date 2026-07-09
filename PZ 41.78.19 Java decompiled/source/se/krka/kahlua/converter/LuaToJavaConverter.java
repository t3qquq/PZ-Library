// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

public interface LuaToJavaConverter<LuaType, JavaType> {
    Class<LuaType> getLuaType();

    Class<JavaType> getJavaType();

    JavaType fromLuaToJava(LuaType var1, Class<JavaType> var2);
}
