/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;

public class KahluaArray
implements KahluaTable {
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
            int n;
            Object[] objectArray = this.data;
            for (n = this.len - 1; n >= 0 && objectArray[n] == null; --n) {
            }
            this.len = n + 1;
            this.recalculateLen = false;
        }
        return this.len;
    }

    @Override
    public KahluaTableIterator iterator() {
        return new KahluaTableIterator(){
            private Double curKey;
            private Object curValue;
            private int index = 1;

            @Override
            public int call(LuaCallFrame luaCallFrame, int n) {
                if (this.advance()) {
                    return luaCallFrame.push(this.getKey(), this.getValue());
                }
                return 0;
            }

            @Override
            public boolean advance() {
                while (this.index <= KahluaArray.this.len()) {
                    Object object = KahluaArray.this.rawget(this.index);
                    if (object != null) {
                        int n = this.index++;
                        this.curKey = KahluaUtil.toDouble(n);
                        this.curValue = object;
                        return true;
                    }
                    ++this.index;
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
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = null;
        }
        this.len = 0;
    }

    @Override
    public Object rawget(int n) {
        if (n < 1 || n > this.len) {
            return null;
        }
        return this.data[n - 1];
    }

    @Override
    public void rawset(int n, Object object) {
        if (n <= 0) {
            KahluaUtil.fail("Index out of range: " + n);
        }
        if (n >= this.len) {
            if (object == null) {
                if (n == this.len) {
                    this.data[n - 1] = object;
                    this.recalculateLen = true;
                }
                return;
            }
            if (this.data.length < n) {
                int n2 = 2 * n;
                int n3 = n2 - 1;
                Object[] objectArray = new Object[n3];
                System.arraycopy(this.data, 0, objectArray, 0, this.len);
                this.data = objectArray;
            }
            this.len = n;
        }
        this.data[n - 1] = object;
    }

    private int getKeyIndex(Object object) {
        if (object instanceof Double) {
            Double d = (Double)object;
            return d.intValue();
        }
        return -1;
    }

    @Override
    public Object rawget(Object object) {
        int n = this.getKeyIndex(object);
        return this.rawget(n);
    }

    @Override
    public void rawset(Object object, Object object2) {
        int n = this.getKeyIndex(object);
        if (n == -1) {
            KahluaUtil.fail("Invalid table key: " + object);
        }
        this.rawset(n, object2);
    }

    public Object next(Object object) {
        int n;
        if (object == null) {
            n = 0;
        } else {
            n = this.getKeyIndex(object);
            if (n <= 0 || n > this.len) {
                KahluaUtil.fail("invalid key to 'next'");
                return null;
            }
        }
        while (n < this.len) {
            if (this.data[n] != null) {
                return KahluaUtil.toDouble(n + 1);
            }
            ++n;
        }
        return null;
    }

    @Override
    public KahluaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public void setMetatable(KahluaTable kahluaTable) {
        this.metatable = kahluaTable;
    }

    public Class<?> getJavaClass() {
        return null;
    }

    @Override
    public void save(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load(ByteBuffer byteBuffer, int n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(DataOutputStream dataOutputStream) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load(DataInputStream dataInputStream, int n) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

