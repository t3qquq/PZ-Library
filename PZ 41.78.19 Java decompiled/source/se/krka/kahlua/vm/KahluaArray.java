// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class KahluaArray implements KahluaTable {
    private KahluaTable metatable;
    private Object[] data = new Object[16];
    private int len = 0;
    private boolean recalculateLen;

    @Override
    public String getString(String string) {
        return (String)this.rawget(string);
    }

    @Override
    public int size() {
        return this.len();
    }

    @Override
    public int len() {
        if (this.recalculateLen) {
            int int0 = this.len - 1;
            Object[] objects = this.data;

            while (int0 >= 0 && objects[int0] == null) {
                int0--;
            }

            this.len = int0 + 1;
            this.recalculateLen = false;
        }

        return this.len;
    }

    @Override
    public KahluaTableIterator iterator() {
        return new KahluaTableIterator() {
            private Double curKey;
            private Object curValue;
            private int index = 1;

            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                return this.advance() ? luaCallFrame.push(this.getKey(), this.getValue()) : 0;
            }

            @Override
            public boolean advance() {
                while (this.index <= KahluaArray.this.len()) {
                    Object object = KahluaArray.this.rawget(this.index);
                    if (object != null) {
                        int int0 = this.index++;
                        this.curKey = KahluaUtil.toDouble((long)int0);
                        this.curValue = object;
                        return true;
                    }

                    this.index++;
                }

                return false;
            }

            @Override
            public Object getKey() {
                return this.curKey;
            }

            @Override
            public Object getValue() {
                return this.curValue;
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return this.len() == 0;
    }

    @Override
    public void wipe() {
        for (int int0 = 0; int0 < this.data.length; int0++) {
            this.data[int0] = null;
        }

        this.len = 0;
    }

    @Override
    public Object rawget(int int0) {
        return int0 >= 1 && int0 <= this.len ? this.data[int0 - 1] : null;
    }

    @Override
    public void rawset(int int0, Object object) {
        if (int0 <= 0) {
            KahluaUtil.fail("Index out of range: " + int0);
        }

        if (int0 >= this.len) {
            if (object == null) {
                if (int0 == this.len) {
                    this.data[int0 - 1] = object;
                    this.recalculateLen = true;
                }

                return;
            }

            if (this.data.length < int0) {
                int int1 = 2 * int0;
                int int2 = int1 - 1;
                Object[] objects = new Object[int2];
                System.arraycopy(this.data, 0, objects, 0, this.len);
                this.data = objects;
            }

            this.len = int0;
        }

        this.data[int0 - 1] = object;
    }

    private int getKeyIndex(Object object) {
        return object instanceof Double double0 ? double0.intValue() : -1;
    }

    @Override
    public Object rawget(Object object) {
        int int0 = this.getKeyIndex(object);
        return this.rawget(int0);
    }

    @Override
    public void rawset(Object object0, Object object1) {
        int int0 = this.getKeyIndex(object0);
        if (int0 == -1) {
            KahluaUtil.fail("Invalid table key: " + object0);
        }

        this.rawset(int0, object1);
    }

    public Object next(Object object) {
        int int0;
        if (object == null) {
            int0 = 0;
        } else {
            int0 = this.getKeyIndex(object);
            if (int0 <= 0 || int0 > this.len) {
                KahluaUtil.fail("invalid key to 'next'");
                return null;
            }
        }

        while (int0 < this.len) {
            if (this.data[int0] != null) {
                return KahluaUtil.toDouble((long)(int0 + 1));
            }

            int0++;
        }

        return null;
    }

    @Override
    public KahluaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public void setMetatable(KahluaTable table) {
        this.metatable = table;
    }

    public Class<?> getJavaClass() {
        return null;
    }

    @Override
    public void save(ByteBuffer var1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load(ByteBuffer var1, int var2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(DataOutputStream var1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load(DataInputStream var1, int var2) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
