// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class ZomboidAbstractMap<K, V> implements Map<K, V> {
    transient volatile Set<K> keySet = null;
    transient volatile Collection<V> values = null;

    protected ZomboidAbstractMap() {
    }

    @Override
    public int size() {
        return this.entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsValue(Object object) {
        Iterator iterator = this.entrySet().iterator();
        if (object == null) {
            while (iterator.hasNext()) {
                Entry entry0 = (Entry)iterator.next();
                if (entry0.getValue() == null) {
                    return true;
                }
            }
        } else {
            while (iterator.hasNext()) {
                Entry entry1 = (Entry)iterator.next();
                if (object.equals(entry1.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean containsKey(Object object) {
        Iterator iterator = this.entrySet().iterator();
        if (object == null) {
            while (iterator.hasNext()) {
                Entry entry0 = (Entry)iterator.next();
                if (entry0.getKey() == null) {
                    return true;
                }
            }
        } else {
            while (iterator.hasNext()) {
                Entry entry1 = (Entry)iterator.next();
                if (object.equals(entry1.getKey())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(Object object) {
        Iterator iterator = this.entrySet().iterator();
        if (object == null) {
            while (iterator.hasNext()) {
                Entry entry0 = (Entry)iterator.next();
                if (entry0.getKey() == null) {
                    return (V)entry0.getValue();
                }
            }
        } else {
            while (iterator.hasNext()) {
                Entry entry1 = (Entry)iterator.next();
                if (object.equals(entry1.getKey())) {
                    return (V)entry1.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public V put(K var1, V var2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object object0) {
        Iterator iterator = this.entrySet().iterator();
        Entry entry0 = null;
        if (object0 == null) {
            while (entry0 == null && iterator.hasNext()) {
                Entry entry1 = (Entry)iterator.next();
                if (entry1.getKey() == null) {
                    entry0 = entry1;
                }
            }
        } else {
            while (entry0 == null && iterator.hasNext()) {
                Entry entry2 = (Entry)iterator.next();
                if (object0.equals(entry2.getKey())) {
                    entry0 = entry2;
                }
            }
        }

        Object object1 = null;
        if (entry0 != null) {
            object1 = entry0.getValue();
            iterator.remove();
        }

        return (V)object1;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry entry : map.entrySet()) {
            this.put((K)entry.getKey(), (V)entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.entrySet().clear();
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet<K>() {
                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<Entry<K, V>> i = ZomboidAbstractMap.this.entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return this.i.hasNext();
                        }

                        @Override
                        public K next() {
                            return this.i.next().getKey();
                        }

                        @Override
                        public void remove() {
                            this.i.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return ZomboidAbstractMap.this.size();
                }

                @Override
                public boolean contains(Object object) {
                    return ZomboidAbstractMap.this.containsKey(object);
                }
            };
        }

        return this.keySet;
    }

    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new AbstractCollection<V>() {
                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<K, V>> i = ZomboidAbstractMap.this.entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return this.i.hasNext();
                        }

                        @Override
                        public V next() {
                            return this.i.next().getValue();
                        }

                        @Override
                        public void remove() {
                            this.i.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return ZomboidAbstractMap.this.size();
                }

                @Override
                public boolean contains(Object object) {
                    return ZomboidAbstractMap.this.containsValue(object);
                }
            };
        }

        return this.values;
    }

    @Override
    public abstract Set<Entry<K, V>> entrySet();

    @Override
    public boolean equals(Object object0) {
        if (object0 == this) {
            return true;
        } else if (!(object0 instanceof Map map)) {
            return false;
        } else if (map.size() != this.size()) {
            return false;
        } else {
            try {
                for (Entry entry : this.entrySet()) {
                    Object object1 = entry.getKey();
                    Object object2 = entry.getValue();
                    if (object2 == null) {
                        if (map.get(object1) != null || !map.containsKey(object1)) {
                            return false;
                        }
                    } else if (!object2.equals(map.get(object1))) {
                        return false;
                    }
                }

                return true;
            } catch (ClassCastException classCastException) {
                return false;
            } catch (NullPointerException nullPointerException) {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int int0 = 0;
        Iterator iterator = this.entrySet().iterator();

        while (iterator.hasNext()) {
            int0 += ((Entry)iterator.next()).hashCode();
        }

        return int0;
    }

    @Override
    public String toString() {
        Iterator iterator = this.entrySet().iterator();
        if (!iterator.hasNext()) {
            return "{}";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('{');

            while (true) {
                Entry entry = (Entry)iterator.next();
                Object object0 = entry.getKey();
                Object object1 = entry.getValue();
                stringBuilder.append(object0 == this ? "(this Map)" : object0);
                stringBuilder.append('=');
                stringBuilder.append(object1 == this ? "(this Map)" : object1);
                if (!iterator.hasNext()) {
                    return stringBuilder.append('}').toString();
                }

                stringBuilder.append(", ");
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ZomboidAbstractMap zomboidAbstractMap0 = (ZomboidAbstractMap)super.clone();
        zomboidAbstractMap0.keySet = null;
        zomboidAbstractMap0.values = null;
        return zomboidAbstractMap0;
    }

    private static boolean eq(Object object0, Object object1) {
        return object0 == null ? object1 == null : object0.equals(object1);
    }

    public static class SimpleEntry<K, V> implements Entry<K, V>, Serializable {
        private static final long serialVersionUID = -8499721149061103585L;
        private final K key;
        private V value;

        public SimpleEntry(K object0, V object1) {
            this.key = (K)object0;
            this.value = (V)object1;
        }

        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key = (K)entry.getKey();
            this.value = (V)entry.getValue();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V object1) {
            Object object0 = this.value;
            this.value = (V)object1;
            return (V)object0;
        }

        @Override
        public boolean equals(Object object) {
            return !(object instanceof Entry entry)
                ? false
                : ZomboidAbstractMap.eq(this.key, entry.getKey()) && ZomboidAbstractMap.eq(this.value, entry.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable {
        private static final long serialVersionUID = 7138329143949025153L;
        private final K key;
        private final V value;

        public SimpleImmutableEntry(K object0, V object1) {
            this.key = (K)object0;
            this.value = (V)object1;
        }

        public SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) {
            this.key = (K)entry.getKey();
            this.value = (V)entry.getValue();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V var1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object object) {
            return !(object instanceof Entry entry)
                ? false
                : ZomboidAbstractMap.eq(this.key, entry.getKey()) && ZomboidAbstractMap.eq(this.value, entry.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
