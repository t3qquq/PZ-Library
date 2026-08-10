/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.converter;

import se.krka.kahlua.converter.JavaToLuaConverter;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.converter.LuaToJavaConverter;

public class KahluaNumberConverter {
    private KahluaNumberConverter() {
    }

    public static void install(KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Long>(){

            @Override
            public Long fromLuaToJava(Double d, Class<Long> clazz) {
                return new Long(d.longValue());
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
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Integer>(){

            @Override
            public Integer fromLuaToJava(Double d, Class<Integer> clazz) {
                return new Integer(d.intValue());
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
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Float>(){

            @Override
            public Float fromLuaToJava(Double d, Class<Float> clazz) {
                return new Float(d.floatValue());
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
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Byte>(){

            @Override
            public Byte fromLuaToJava(Double d, Class<Byte> clazz) {
                return new Byte(d.byteValue());
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
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Character>(){

            @Override
            public Character fromLuaToJava(Double d, Class<Character> clazz) {
                return new Character((char)d.intValue());
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
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<Double, Short>(){

            @Override
            public Short fromLuaToJava(Double d, Class<Short> clazz) {
                return new Short(d.shortValue());
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
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Double>(Double.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Float>(Float.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Integer>(Integer.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Long>(Long.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Short>(Short.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Byte>(Byte.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Character>(Character.class));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Double>(Double.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Float>(Float.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Integer>(Integer.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Long>(Long.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Short>(Short.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Byte>(Byte.TYPE));
        kahluaConverterManager.addJavaConverter(new NumberToLuaConverter<Character>(Character.TYPE));
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Boolean>(){

            @Override
            public Object fromJavaToLua(Boolean bl) {
                return (boolean)bl;
            }

            @Override
            public Class<Boolean> getJavaType() {
                return Boolean.class;
            }
        });
    }

    private static class NumberToLuaConverter<T extends Number>
    implements JavaToLuaConverter<T> {
        private final Class<T> clazz;

        public NumberToLuaConverter(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object fromJavaToLua(T t) {
            return new Double(((Number)t).doubleValue());
        }

        @Override
        public Class<T> getJavaType() {
            return this.clazz;
        }
    }
}

