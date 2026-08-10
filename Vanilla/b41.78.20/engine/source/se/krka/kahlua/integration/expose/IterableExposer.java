/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.util.Iterator;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;

public class IterableExposer {
    @LuaMethod(global=true)
    public Object iter(Iterable<?> iterable) {
        final Iterator<?> iterator = iterable.iterator();
        return new JavaFunction(){

            @Override
            public int call(LuaCallFrame luaCallFrame, int n) {
                if (!iterator.hasNext()) {
                    return 0;
                }
                return luaCallFrame.push(iterator.next());
            }
        };
    }
}

