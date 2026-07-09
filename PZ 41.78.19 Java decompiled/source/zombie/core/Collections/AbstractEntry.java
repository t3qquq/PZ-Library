// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Collections;

import java.util.Map.Entry;

abstract class AbstractEntry<TypeK, TypeV> implements Entry<TypeK, TypeV> {
    protected final TypeK _key;
    protected TypeV _val;

    public AbstractEntry(TypeK object0, TypeV object1) {
        this._key = (TypeK)object0;
        this._val = (TypeV)object1;
    }

    public AbstractEntry(Entry<TypeK, TypeV> entry) {
        this._key = (TypeK)entry.getKey();
        this._val = (TypeV)entry.getValue();
    }

    @Override
    public String toString() {
        return this._key + "=" + this._val;
    }

    @Override
    public TypeK getKey() {
        return this._key;
    }

    @Override
    public TypeV getValue() {
        return this._val;
    }

    @Override
    public boolean equals(Object object) {
        return !(object instanceof Entry entry) ? false : eq(this._key, entry.getKey()) && eq(this._val, entry.getValue());
    }

    @Override
    public int hashCode() {
        return (this._key == null ? 0 : this._key.hashCode()) ^ (this._val == null ? 0 : this._val.hashCode());
    }

    private static boolean eq(Object object0, Object object1) {
        return object0 == null ? object1 == null : object0.equals(object1);
    }
}
