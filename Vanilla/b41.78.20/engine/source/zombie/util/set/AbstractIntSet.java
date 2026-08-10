// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.set;

import zombie.util.AbstractIntCollection;
import zombie.util.IntIterator;
import zombie.util.hash.DefaultIntHashFunction;

public abstract class AbstractIntSet extends AbstractIntCollection implements IntSet {
    protected AbstractIntSet() {
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IntSet intSet)) {
            return false;
        } else {
            return intSet.size() != this.size() ? false : this.containsAll(intSet);
        }
    }

    @Override
    public int hashCode() {
        int int0 = 0;
        IntIterator intIterator = this.iterator();

        while (intIterator.hasNext()) {
            int0 += DefaultIntHashFunction.INSTANCE.hash(intIterator.next());
        }

        return int0;
    }
}
