// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.map;

import zombie.util.hash.DefaultIntHashFunction;

public abstract class AbstractIntKeyMap<V> implements IntKeyMap<V> {
    protected AbstractIntKeyMap() {
    }

    @Override
    public void clear() {
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            intKeyMapIterator.remove();
        }
    }

    @Override
    public V remove(int int0) {
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            if (intKeyMapIterator.getKey() == int0) {
                Object object = intKeyMapIterator.getValue();
                intKeyMapIterator.remove();
                return (V)object;
            }
        }

        return null;
    }

    @Override
    public void putAll(IntKeyMap<V> intKeyMap) {
        IntKeyMapIterator intKeyMapIterator = intKeyMap.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            this.put(intKeyMapIterator.getKey(), (V)intKeyMapIterator.getValue());
        }
    }

    @Override
    public boolean containsKey(int int0) {
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            if (intKeyMapIterator.getKey() == int0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V get(int int0) {
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            intKeyMapIterator.next();
            if (intKeyMapIterator.getKey() == int0) {
                return (V)intKeyMapIterator.getValue();
            }
        }

        return null;
    }

    @Override
    public boolean containsValue(Object object) {
        IntKeyMapIterator intKeyMapIterator = this.entries();
        if (object == null) {
            while (intKeyMapIterator.hasNext()) {
                intKeyMapIterator.next();
                if (object == null) {
                    return true;
                }
            }
        } else {
            while (intKeyMapIterator.hasNext()) {
                intKeyMapIterator.next();
                if (object.equals(intKeyMapIterator.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object object0) {
        if (!(object0 instanceof IntKeyMap intKeyMap)) {
            return false;
        } else if (this.size() != intKeyMap.size()) {
            return false;
        } else {
            IntKeyMapIterator intKeyMapIterator = this.entries();

            while (intKeyMapIterator.hasNext()) {
                intKeyMapIterator.next();
                int int0 = intKeyMapIterator.getKey();
                Object object1 = intKeyMapIterator.getValue();
                if (object1 == null) {
                    if (intKeyMap.get(int0) != null) {
                        return false;
                    }

                    if (!intKeyMap.containsKey(int0)) {
                        return false;
                    }
                } else if (!object1.equals(intKeyMap.get(int0))) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        int int0 = 0;

        for (IntKeyMapIterator intKeyMapIterator = this.entries();
            intKeyMapIterator.hasNext();
            int0 += DefaultIntHashFunction.INSTANCE.hash(intKeyMapIterator.getKey()) ^ intKeyMapIterator.getValue().hashCode()
        ) {
            intKeyMapIterator.next();
        }

        return int0;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public int size() {
        int int0 = 0;

        for (IntKeyMapIterator intKeyMapIterator = this.entries(); intKeyMapIterator.hasNext(); int0++) {
            intKeyMapIterator.next();
        }

        return int0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        IntKeyMapIterator intKeyMapIterator = this.entries();

        while (intKeyMapIterator.hasNext()) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(',');
            }

            intKeyMapIterator.next();
            stringBuilder.append(String.valueOf(intKeyMapIterator.getKey()));
            stringBuilder.append("->");
            stringBuilder.append(String.valueOf(intKeyMapIterator.getValue()));
        }

        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public void trimToSize() {
    }
}
