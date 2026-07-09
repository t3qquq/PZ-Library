// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

public interface JavaToLuaConverter<JavaType> {
    Class<JavaType> getJavaType();

    Object fromJavaToLua(JavaType var1);
}
