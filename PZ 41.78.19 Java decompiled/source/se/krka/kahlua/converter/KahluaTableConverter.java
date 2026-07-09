// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.Platform;

public class KahluaTableConverter {
    private final Platform platform;

    public KahluaTableConverter(Platform platformx) {
        this.platform = platformx;
    }

    public void install(final KahluaConverterManager kahluaConverterManager) {
        kahluaConverterManager.addJavaConverter(new KahluaTableConverter.CollectionToLuaConverter<>(kahluaConverterManager, Collection.class));
        kahluaConverterManager.addLuaConverter(new KahluaTableConverter.CollectionToJavaConverter<>(Collection.class));
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Map>() {
            public Object fromJavaToLua(Map map) {
                KahluaTable table = KahluaTableConverter.this.platform.newTable();

                for (Entry entry : map.entrySet()) {
                    Object object0 = kahluaConverterManager.fromJavaToLua(entry.getKey());
                    Object object1 = kahluaConverterManager.fromJavaToLua(entry.getValue());
                    table.rawset(object0, object1);
                }

                return table;
            }

            @Override
            public Class<Map> getJavaType() {
                return Map.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<KahluaTable, Map>() {
            public Map fromLuaToJava(KahluaTable table, Class<Map> var2) throws IllegalArgumentException {
                KahluaTableIterator kahluaTableIterator = table.iterator();
                HashMap hashMap = new HashMap();

                while (kahluaTableIterator.advance()) {
                    Object object0 = kahluaTableIterator.getKey();
                    Object object1 = kahluaTableIterator.getValue();
                    hashMap.put(object0, object1);
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
        kahluaConverterManager.addJavaConverter(new JavaToLuaConverter<Object>() {
            @Override
            public Object fromJavaToLua(Object object0) {
                if (!object0.getClass().isArray()) {
                    return null;
                } else {
                    KahluaTable table = KahluaTableConverter.this.platform.newTable();
                    int int0 = Array.getLength(object0);

                    for (int int1 = 0; int1 < int0; int1++) {
                        Object object1 = Array.get(object0, int1);
                        table.rawset(int1 + 1, kahluaConverterManager.fromJavaToLua(object1));
                    }

                    return table;
                }
            }

            @Override
            public Class<Object> getJavaType() {
                return Object.class;
            }
        });
        kahluaConverterManager.addLuaConverter(new LuaToJavaConverter<KahluaTable, Object>() {
            public Object fromLuaToJava(KahluaTable table, Class<Object> clazz) throws IllegalArgumentException {
                if (clazz.isArray()) {
                    List list = kahluaConverterManager.fromLuaToJava(table, List.class);
                    return list.toArray();
                } else {
                    return null;
                }
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

    private static class CollectionToJavaConverter<T> implements LuaToJavaConverter<KahluaTable, T> {
        private final Class<T> javaClass;

        private CollectionToJavaConverter(Class<T> clazz) {
            this.javaClass = clazz;
        }

        public T fromLuaToJava(KahluaTable table, Class<T> var2) throws IllegalArgumentException {
            int int0 = table.len();
            ArrayList arrayList = new ArrayList(int0);

            for (int int1 = 1; int1 <= int0; int1++) {
                Object object = table.rawget(int1);
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

    private class CollectionToLuaConverter<T extends Iterable> implements JavaToLuaConverter<T> {
        private final Class<T> clazz;
        private final KahluaConverterManager manager;

        public CollectionToLuaConverter(KahluaConverterManager kahluaConverterManager, Class<T> clazzx) {
            this.manager = kahluaConverterManager;
            this.clazz = clazzx;
        }

        public Object fromJavaToLua(T iterable) {
            KahluaTable table = KahluaTableConverter.this.platform.newTable();
            int int0 = 0;

            for (Object object : iterable) {
                table.rawset(++int0, this.manager.fromJavaToLua(object));
            }

            return table;
        }

        @Override
        public Class<T> getJavaType() {
            return this.clazz;
        }
    }
}
