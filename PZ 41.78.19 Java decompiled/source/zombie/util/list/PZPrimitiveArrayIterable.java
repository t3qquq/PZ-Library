// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.list;

import java.util.Iterator;

public final class PZPrimitiveArrayIterable {
    public static Iterable<Float> fromArray(final float[] floats) {
        return new Iterable<Float>() {
            private final float[] m_list = floats;

            @Override
            public Iterator<Float> iterator() {
                return new Iterator<Float>() {
                    private int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return m_list.length > this.pos;
                    }

                    public Float next() {
                        return m_list[this.pos++];
                    }
                };
            }
        };
    }
}
