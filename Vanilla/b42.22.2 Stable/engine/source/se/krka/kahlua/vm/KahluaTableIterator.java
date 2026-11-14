// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

public interface KahluaTableIterator extends JavaFunction {
    boolean advance();

    Object getKey();

    Object getValue();
}
