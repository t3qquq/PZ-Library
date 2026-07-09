// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.Iterator;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class IterableExposer {
    @LuaMethod(
        global = true
    )
    public Object iter(Iterable<?> iterable) {
        final Iterator iterator = iterable.iterator();
        return new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                return !iterator.hasNext() ? 0 : luaCallFrame.push(iterator.next());
            }
        };
    }
}
