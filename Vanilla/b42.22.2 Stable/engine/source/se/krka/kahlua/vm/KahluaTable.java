// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface KahluaTable {
    void setMetatable(KahluaTable arg0);

    KahluaTable getMetatable();

    void rawset(Object arg0, Object arg1);

    Object rawget(Object arg0);

    void rawset(int arg0, Object arg1);

    Object rawget(int arg0);

    int len();

    KahluaTableIterator iterator();

    boolean isEmpty();

    void wipe();

    int size();

    void save(ByteBuffer arg0) throws IOException;

    void load(ByteBuffer arg0, int arg1) throws IOException;

    void save(DataOutputStream arg0) throws IOException;

    void load(DataInputStream arg0, int arg1) throws IOException;

    String getString(String arg0);
}
