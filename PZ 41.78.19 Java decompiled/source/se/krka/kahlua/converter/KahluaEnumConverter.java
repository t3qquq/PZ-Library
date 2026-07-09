// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

public class KahluaEnumConverter {
    private KahluaEnumConverter() {
    }

    public static void install(KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Enum>() {
            public Object fromJavaToLua(Enum _enum) {
                return _enum.name();
            }

            @Override
            public Class<Enum> getJavaType() {
                return Enum.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<String, Enum>() {
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
