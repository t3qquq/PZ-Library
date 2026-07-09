// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Stack;

public class IntArrayCache {
    public static IntArrayCache instance = new IntArrayCache();
    TIntObjectHashMap<Stack<Integer[]>> Map = new TIntObjectHashMap<>();

    public void Init() {
        for (int int0 = 0; int0 < 100; int0++) {
            Stack stack = new Stack();

            for (int int1 = 0; int1 < 1000; int1++) {
                stack.push(new Integer[int0]);
            }
        }
    }

    public void put(Integer[] integers) {
        if (this.Map.containsKey(integers.length)) {
            this.Map.get(integers.length).push(integers);
        } else {
            Stack stack = new Stack();
            stack.push(integers);
            this.Map.put(integers.length, stack);
        }
    }

    public Integer[] get(int int0) {
        if (this.Map.containsKey(int0)) {
            Stack stack = this.Map.get(int0);
            if (!stack.isEmpty()) {
                return (Integer[])stack.pop();
            }
        }

        return new Integer[int0];
    }
}
