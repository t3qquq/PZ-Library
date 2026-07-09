// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.tests;

import astar.datastructures.HashPriorityQueue;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;

public class DatastructuresTest {
    @Test
    public void hashPriorityQueueTest() {
        class InconsistentComparator implements Comparator<Integer> {
            public int compare(Integer var1, Integer var2) {
                return 0;
            }
        }

        HashPriorityQueue hashPriorityQueue = new HashPriorityQueue<>(new InconsistentComparator());
        hashPriorityQueue.add(0, 0);
        hashPriorityQueue.add(1, 1);
        hashPriorityQueue.add(2, 2);
        hashPriorityQueue.add(3, 3);
        hashPriorityQueue.remove(1, 1);
        Assert.assertEquals(true, hashPriorityQueue.contains(0));
        Assert.assertEquals(false, hashPriorityQueue.contains(1));
        Assert.assertEquals(true, hashPriorityQueue.contains(2));
        Assert.assertEquals(true, hashPriorityQueue.contains(3));
        hashPriorityQueue.remove(0, 0);
        Assert.assertEquals(false, hashPriorityQueue.contains(0));
        Assert.assertEquals(true, hashPriorityQueue.contains(2));
        Assert.assertEquals(true, hashPriorityQueue.contains(3));
        hashPriorityQueue.remove(3, 3);
        Assert.assertEquals(true, hashPriorityQueue.contains(2));
        Assert.assertEquals(false, hashPriorityQueue.contains(3));
        hashPriorityQueue.clear();
        hashPriorityQueue.add(0, 0);
        hashPriorityQueue.add(1, 1);
        hashPriorityQueue.add(2, 2);
        hashPriorityQueue.add(3, 3);
        int int0 = (Integer)hashPriorityQueue.poll();
        Assert.assertEquals(0L, hashPriorityQueue.size());
    }
}
