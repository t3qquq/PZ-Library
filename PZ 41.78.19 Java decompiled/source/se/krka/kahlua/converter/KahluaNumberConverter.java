// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

public class KahluaNumberConverter {
    private KahluaNumberConverter() {
    }

    public static void install(KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Long>() {
            public Long fromLuaToJava(Double double0, Class<Long> var2) {
                return new Long(double0.longValue());
            }

            @Override
            public Class<Long> getJavaType() {
                return Long.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Integer>() {
            public Integer fromLuaToJava(Double double0, Class<Integer> var2) {
                return new Integer(double0.intValue());
            }

            @Override
            public Class<Integer> getJavaType() {
                return Integer.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Float>() {
            public Float fromLuaToJava(Double double0, Class<Float> var2) {
                return new Float(double0.floatValue());
            }

            @Override
            public Class<Float> getJavaType() {
                return Float.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Byte>() {
            public Byte fromLuaToJava(Double double0, Class<Byte> var2) {
                return new Byte(double0.byteValue());
            }

            @Override
            public Class<Byte> getJavaType() {
                return Byte.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Character>() {
            public Character fromLuaToJava(Double double0, Class<Character> var2) {
                return new Character((char)double0.intValue());
            }

            @Override
            public Class<Character> getJavaType() {
                return Character.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Short>() {
            public Short fromLuaToJava(Double double0, Class<Short> var2) {
                return new Short(double0.shortValue());
            }

            @Override
            public Class<Short> getJavaType() {
                return Short.class;
            }

            @Override
            public Class<Double> getLuaType() {
                return Double.class;
            }
        });
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Double.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Float.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Integer.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Long.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Short.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(Byte.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(Character.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(double.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(float.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(int.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(long.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(short.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter<>(byte.class));
        kahluaConverterManager.addJavaConverter(new KahluaNumberConverter.NumberToLuaConverter(char.class));
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Boolean>() {
            public Object fromJavaToLua(Boolean boolean0) {
                return boolean0;
            }

            @Override
            public Class<Boolean> getJavaType() {
                return Boolean.class;
            }
        });
    }

    private static class NumberToLuaConverter<T extends Number> implements JavaToLuaConverter<T> {
        private final Class<T> clazz;

        public NumberToLuaConverter(Class<T> clazzx) {
            this.clazz = clazzx;
        }

        public Object fromJavaToLua(T number) {
            return new Double(number.doubleValue());
        }

        @Override
        public Class<T> getJavaType() {
            return this.clazz;
        }
    }
}
