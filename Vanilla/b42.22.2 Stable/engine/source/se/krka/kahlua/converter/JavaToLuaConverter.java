// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.converter;

public interface JavaToLuaConverter<JavaType> {
    Class<JavaType> getJavaType();

    Object fromJavaToLua(JavaType var1);
}
