/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.krka.kahlua.converter.JavaToLuaConverter;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.converter.LuaToJavaConverter;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.Platform;

public class KahluaTableConverter {
    private final Platform platform;

    public KahluaTableConverter(Platform platform) {
        this.platform = platform;
    }

    public void install(final KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addJavaConverter(new CollectionToLuaConverter<Collection>(kahluaConverterManager, Collection.class));
        kahluaConverterManager.addLuaConverter(new CollectionToJavaConverter<Collection>(Collection.class));
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Map>(){

            @Override
            public Object fromJavaToLua(Map map) {
                Map map2 = map;
                KahluaTable kahluaTable = KahluaTableConverter.this.platform.newTable();
                for (Map.Entry entry : map2.entrySet()) {
                    Object object = kahluaConverterManager.fromJavaToLua(entry.getKey());
                    Object object2 = kahluaConverterManager.fromJavaToLua(entry.getValue());
                    kahluaTable.rawset(object, object2);
                }
                return kahluaTable;
            }

            @Override
            public Class<Map> getJavaType() {
                return Map.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<KahluaTable, Map>(){

            @Override
            public Map fromLuaToJava(KahluaTable kahluaTable, Class<Map> clazz) throws IllegalArgumentException {
                KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
                HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
                while (kahluaTableIterator.advance()) {
                    Object object = kahluaTableIterator.getKey();
                    Object object2 = kahluaTableIterator.getValue();
                    hashMap.put(object, object2);
                }
                return hashMap;
            }

            @Override
            public Class<Map> getJavaType() {
                return Map.class;
            }

            @Override
            public Class<KahluaTable> getLuaType() {
                return KahluaTable.class;
            }
        });
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Object>(){

            @Override
            public Object fromJavaToLua(Object object) {
                if (object.getClass().isArray()) {
                    KahluaTable kahluaTable = KahluaTableConverter.this.platform.newTable();
                    int n = Array.getLength(object);
                    for (int i = 0; i < n; ++i) {
                        Object object2 = Array.get(object, i);
                        kahluaTable.rawset(i + 1, kahluaConverterManager.fromJavaToLua(object2));
                    }
                    return kahluaTable;
                }
                return null;
            }

            @Override
            public Class<Object> getJavaType() {
                return Object.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<KahluaTable, Object>(){

            @Override
            public Object fromLuaToJava(KahluaTable kahluaTable, Class<Object> clazz) throws IllegalArgumentException {
                if (clazz.isArray()) {
                    List list = kahluaConverterManager.fromLuaToJava(kahluaTable, List.class);
                    return list.toArray();
                }
                return null;
            }

            @Override
            public Class<Object> getJavaType() {
                return Object.class;
            }

            @Override
            public Class<KahluaTable> getLuaType() {
                return KahluaTable.class;
            }
        });
    }

    private class CollectionToLuaConverter<T extends Iterable>
    implements JavaToLuaConverter<T> {
        private final Class<T> clazz;
        private final KahluaConverterManager manager;

        public CollectionToLuaConverter(KahluaConverterManager kahluaConverterManager, Class<T> clazz) {
            this.manager = kahluaConverterManager;
            this.clazz = clazz;
        }

        @Override
        public Object fromJavaToLua(T t) {
            KahluaTable kahluaTable = KahluaTableConverter.this.platform.newTable();
            int n = 0;
            for (Object t2 : t) {
                kahluaTable.rawset(++n, this.manager.fromJavaToLua(t2));
            }
            return kahluaTable;
        }

        @Override
        public Class<T> getJavaType() {
            return this.clazz;
        }
    }

    private static class CollectionToJavaConverter<T>
    implements LuaToJavaConverter<KahluaTable, T> {
        private final Class<T> javaClass;

        private CollectionToJavaConverter(Class<T> clazz) {
            this.javaClass = clazz;
        }

        @Override
        public T fromLuaToJava(KahluaTable kahluaTable, Class<T> clazz) throws IllegalArgumentException {
            int n = kahluaTable.len();
            ArrayList<Object> arrayList = new ArrayList<Object>(n);
            for (int i = 1; i <= n; ++i) {
                Object object = kahluaTable.rawget(i);
                arrayList.add(object);
            }
            return (T)arrayList;
        }

        @Override
        public Class<T> getJavaType() {
            return this.javaClass;
        }

        @Override
        public Class<KahluaTable> getLuaType() {
            return KahluaTable.class;
        }
    }
}

