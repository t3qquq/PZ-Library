// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import org.junit.Assert;
import org.junit.Test;

public class test_ObjectsSyncRequests_getObjectInsertIndex extends Assert {
    @Test
    public void test_getInsertIndex() {
        long[] longs0 = new long[]{13L, 88L, 51L};
        long[] longs1 = new long[]{8L, 13L, 52L, 21L, 88L, 36L, 51L, 15L};
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 8L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 13L));
        assertEquals(1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 52L));
        assertEquals(1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 21L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 88L));
        assertEquals(2L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 36L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 51L));
        assertEquals(3L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 15L));
    }

    @Test
    public void test_getInsertIndex2() {
        long[] longs0 = new long[0];
        long[] longs1 = new long[]{81L, 45L, 72L};
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 8L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 13L));
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 81L));
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 45L));
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 72L));
    }

    @Test
    public void test_getInsertIndex3() {
        long[] longs0 = new long[]{71L, 66L, 381L};
        long[] longs1 = new long[]{55L, 81L, 71L, 41L, 66L, 381L, 68L};
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 55L));
        assertEquals(0L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 81L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 71L));
        assertEquals(1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 41L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 66L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 381L));
        assertEquals(3L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 68L));
        assertEquals(-1L, ObjectsSyncRequests.getObjectInsertIndex(longs0, longs1, 33L));
    }
}
