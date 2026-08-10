/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.converter;

import se.krka.kahlua.converter.JavaToLuaConverter;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.converter.LuaToJavaConverter;

public class KahluaEnumConverter {
    private KahluaEnumConverter() {
    }

    public static void install(KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Enum>(){

            @Override
            public Object fromJavaToLua(Enum enum_) {
                return enum_.name();
            }

            @Override
            public Class<Enum> getJavaType() {
                return Enum.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<String, Enum>(){

            @Override
            public Enum fromLuaToJava(String string, Class<Enum> clazz) throws IllegalArgumentException {
                return Enum.valueOf(clazz, string);
            }

            @Override
            public Class<Enum> getJavaType() {
                return Enum.class;
            }

            @Override
            public Class<String> getLuaType() {
                return String.class;
            }
        });
    }
}

