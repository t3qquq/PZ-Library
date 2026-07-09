// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

public class HashMap {
    private int capacity = 2;
    private int elements = 0;
    private HashMap.Bucket[] buckets = new HashMap.Bucket[this.capacity];

    public HashMap() {
        for (int int0 = 0; int0 < this.capacity; int0++) {
            this.buckets[int0] = new HashMap.Bucket();
        }
    }

    public void clear() {
        this.elements = 0;

        for (int int0 = 0; int0 < this.capacity; int0++) {
            this.buckets[int0].clear();
        }
    }

    private void grow() {
        HashMap.Bucket[] bucketsx = this.buckets;
        this.capacity *= 2;
        this.elements = 0;
        this.buckets = new HashMap.Bucket[this.capacity];

        for (int int0 = 0; int0 < this.capacity; int0++) {
            this.buckets[int0] = new HashMap.Bucket();
        }

        for (int int1 = 0; int1 < bucketsx.length; int1++) {
            HashMap.Bucket bucket = bucketsx[int1];

            for (int int2 = 0; int2 < bucket.size(); int2++) {
                if (bucket.keys[int2] != null) {
                    this.put(bucket.keys[int2], bucket.values[int2]);
                }
            }
        }
    }

    public Object get(Object key) {
        HashMap.Bucket bucket = this.buckets[Math.abs(key.hashCode()) % this.capacity];

        for (int int0 = 0; int0 < bucket.size(); int0++) {
            if (bucket.keys[int0] != null && bucket.keys[int0].equals(key)) {
                return bucket.values[int0];
            }
        }

        return null;
    }

    public Object remove(Object key) {
        HashMap.Bucket bucket = this.buckets[Math.abs(key.hashCode()) % this.capacity];
        Object object = bucket.remove(key);
        if (object != null) {
            this.elements--;
            return object;
        } else {
            return null;
        }
    }

    public Object put(Object key, Object value) {
        if (this.elements + 1 >= this.buckets.length) {
            this.grow();
        }

        Object object = this.remove(key);
        HashMap.Bucket bucket = this.buckets[Math.abs(key.hashCode()) % this.capacity];
        bucket.put(key, value);
        this.elements++;
        return object;
    }

    public int size() {
        return this.elements;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public HashMap.Iterator iterator() {
        return new HashMap.Iterator(this);
    }

    @Override
    public String toString() {
        Object object = new String();

        for (int int0 = 0; int0 < this.buckets.length; int0++) {
            HashMap.Bucket bucket = this.buckets[int0];

            for (int int1 = 0; int1 < bucket.size(); int1++) {
                if (bucket.keys[int1] != null) {
                    if (object.length() > 0) {
                        object = object + ", ";
                    }

                    object = object + bucket.keys[int1] + "=" + bucket.values[int1];
                }
            }
        }

        return "HashMap[" + object + "]";
    }

    private static class Bucket {
        public Object[] keys;
        public Object[] values;
        public int count;
        public int nextIndex;

        public void put(Object arg0, Object arg1) throws IllegalStateException {
            if (this.keys == null) {
                this.grow();
                this.keys[0] = arg0;
                this.values[0] = arg1;
                this.nextIndex = 1;
                this.count = 1;
            } else {
                if (this.count == this.keys.length) {
                    this.grow();
                }

                for (int int0 = 0; int0 < this.keys.length; int0++) {
                    if (this.keys[int0] == null) {
                        this.keys[int0] = arg0;
                        this.values[int0] = arg1;
                        this.count++;
                        this.nextIndex = Math.max(this.nextIndex, int0 + 1);
                        return;
                    }
                }

                throw new IllegalStateException("bucket is full");
            }
        }

        public Object remove(Object arg0) {
            for (int int0 = 0; int0 < this.nextIndex; int0++) {
                if (this.keys[int0] != null && this.keys[int0].equals(arg0)) {
                    Object object = this.values[int0];
                    this.keys[int0] = null;
                    this.values[int0] = null;
                    this.count--;
                    return object;
                }
            }

            return null;
        }

        private void grow() {
            if (this.keys == null) {
                this.keys = new Object[2];
                this.values = new Object[2];
            } else {
                Object[] objects0 = this.keys;
                Object[] objects1 = this.values;
                this.keys = new Object[objects0.length * 2];
                this.values = new Object[objects1.length * 2];
                System.arraycopy(objects0, 0, this.keys, 0, objects0.length);
                System.arraycopy(objects1, 0, this.values, 0, objects1.length);
            }
        }

        public int size() {
            return this.nextIndex;
        }

        public void clear() {
            for (int int0 = 0; int0 < this.nextIndex; int0++) {
                this.keys[int0] = null;
                this.values[int0] = null;
            }

            this.count = 0;
            this.nextIndex = 0;
        }
    }

    public static class Iterator {
        private HashMap hashMap;
        private int bucketIdx;
        private int keyValuePairIdx;
        private int elementIdx;
        private Object currentKey;
        private Object currentValue;

        public Iterator(HashMap hashmap) {
            this.hashMap = hashmap;
            this.reset();
        }

        public HashMap.Iterator reset() {
            this.bucketIdx = 0;
            this.keyValuePairIdx = 0;
            this.elementIdx = 0;
            this.currentKey = null;
            this.currentValue = null;
            return this;
        }

        public boolean hasNext() {
            return this.elementIdx < this.hashMap.elements;
        }

        public boolean advance() {
            while (this.bucketIdx < this.hashMap.buckets.length) {
                HashMap.Bucket bucket = this.hashMap.buckets[this.bucketIdx];
                if (this.keyValuePairIdx == bucket.size()) {
                    this.keyValuePairIdx = 0;
                    this.bucketIdx++;
                } else {
                    while (this.keyValuePairIdx < bucket.size()) {
                        if (bucket.keys[this.keyValuePairIdx] != null) {
                            this.currentKey = bucket.keys[this.keyValuePairIdx];
                            this.currentValue = bucket.values[this.keyValuePairIdx];
                            this.keyValuePairIdx++;
                            this.elementIdx++;
                            return true;
                        }

                        this.keyValuePairIdx++;
                    }

                    this.keyValuePairIdx = 0;
                    this.bucketIdx++;
                }
            }

            return false;
        }

        public Object getKey() {
            return this.currentKey;
        }

        public Object getValue() {
            return this.currentValue;
        }
    }
}
