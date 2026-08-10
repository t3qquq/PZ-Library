/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.GameWindow
 *  zombie.Lua.LuaManager
 *  zombie.Lua.LuaManager$GlobalObject
 *  zombie.core.Core
 *  zombie.ui.UIManager
 */
package se.krka.kahlua.j2se;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.ui.UIManager;

public final class KahluaTableImpl
implements KahluaTable {
    public final Map<Object, Object> delegate;
    private KahluaTable metatable;
    private KahluaTable reloadReplace;
    private static final byte SBYT_NO_SAVE = -1;
    private static final byte SBYT_STRING = 0;
    private static final byte SBYT_DOUBLE = 1;
    private static final byte SBYT_TABLE = 2;
    private static final byte SBYT_BOOLEAN = 3;

    public KahluaTableImpl(Map<Object, Object> map) {
        this.delegate = map;
    }

    @Override
    public void setMetatable(KahluaTable kahluaTable) {
        this.metatable = kahluaTable;
    }

    @Override
    public KahluaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public void rawset(Object object, Object object2) {
        if (this.reloadReplace != null) {
            this.reloadReplace.rawset(object, object2);
        }
        Object object3 = null;
        if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object)) {
            object3 = this.rawget(object);
        }
        if (object2 == null) {
            if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object) && object3 != null) {
                UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)LuaManager.thread.lastLine);
            }
            this.delegate.remove(object);
            return;
        }
        if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object) && !object2.equals(object3)) {
            int n = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
            if (n < 0) {
                n = 0;
            }
            UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[n] - 1));
        }
        this.delegate.put(object, object2);
    }

    @Override
    public Object rawget(Object object) {
        if (this.reloadReplace != null) {
            return this.reloadReplace.rawget(object);
        }
        if (object == null) {
            return null;
        }
        if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasReadDataBreakpoint(this, object)) {
            int n = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
            if (n < 0) {
                n = 0;
            }
            UIManager.debugBreakpoint((String)LuaManager.thread.currentfile, (long)(LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[n] - 1));
        }
        if (!this.delegate.containsKey(object) && this.metatable != null) {
            return this.metatable.rawget(object);
        }
        return this.delegate.get(object);
    }

    @Override
    public void rawset(int n, Object object) {
        this.rawset(KahluaUtil.toDouble(n), object);
    }

    public String rawgetStr(Object object) {
        return (String)this.rawget(object);
    }

    public int rawgetInt(Object object) {
        if (this.rawget(object) instanceof Double) {
            return ((Double)this.rawget(object)).intValue();
        }
        return -1;
    }

    public boolean rawgetBool(Object object) {
        if (this.rawget(object) instanceof Boolean) {
            return (Boolean)this.rawget(object);
        }
        return false;
    }

    public float rawgetFloat(Object object) {
        if (this.rawget(object) instanceof Double) {
            return ((Double)this.rawget(object)).floatValue();
        }
        return -1.0f;
    }

    @Override
    public Object rawget(int n) {
        return this.rawget(KahluaUtil.toDouble(n));
    }

    @Override
    public int len() {
        return KahluaUtil.len(this, 0, 2 * this.delegate.size());
    }

    @Override
    public KahluaTableIterator iterator() {
        final Object[] objectArray = this.delegate.isEmpty() ? null : this.delegate.keySet().toArray();
        return new KahluaTableIterator(){
            private Object curKey;
            private Object curValue;
            private int keyIndex;

            @Override
            public int call(LuaCallFrame luaCallFrame, int n) {
                if (this.advance()) {
                    return luaCallFrame.push(this.getKey(), this.getValue());
                }
                return 0;
            }

            @Override
            public boolean advance() {
                if (objectArray != null && this.keyIndex < objectArray.length) {
                    this.curKey = objectArray[this.keyIndex];
                    this.curValue = KahluaTableImpl.this.delegate.get(this.curKey);
                    ++this.keyIndex;
                    return true;
                }
                this.curKey = null;
                this.curValue = null;
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
        return this.delegate.isEmpty();
    }

    @Override
    public void wipe() {
        this.delegate.clear();
    }

    public String toString() {
        return "table 0x" + System.identityHashCode(this);
    }

    @Override
    public void save(ByteBuffer byteBuffer) {
        KahluaTableIterator kahluaTableIterator = this.iterator();
        int n = 0;
        while (kahluaTableIterator.advance()) {
            if (!KahluaTableImpl.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) continue;
            ++n;
        }
        kahluaTableIterator = this.iterator();
        byteBuffer.putInt(n);
        while (kahluaTableIterator.advance()) {
            byte by = KahluaTableImpl.getKeyByte(kahluaTableIterator.getKey());
            byte by2 = KahluaTableImpl.getValueByte(kahluaTableIterator.getValue());
            if (by == -1 || by2 == -1) continue;
            this.save(byteBuffer, by, kahluaTableIterator.getKey());
            this.save(byteBuffer, by2, kahluaTableIterator.getValue());
        }
    }

    private void save(ByteBuffer byteBuffer, byte by, Object object) throws RuntimeException {
        byteBuffer.put(by);
        if (by == 0) {
            GameWindow.WriteString((ByteBuffer)byteBuffer, (String)((String)object));
        } else if (by == 1) {
            byteBuffer.putDouble((Double)object);
        } else if (by == 3) {
            byteBuffer.put((Boolean)object != false ? (byte)1 : 0);
        } else if (by == 2) {
            ((KahluaTableImpl)object).save(byteBuffer);
        } else {
            throw new RuntimeException("invalid lua table type " + by);
        }
    }

    @Override
    public void save(DataOutputStream dataOutputStream) throws IOException {
        KahluaTableIterator kahluaTableIterator = this.iterator();
        int n = 0;
        while (kahluaTableIterator.advance()) {
            if (!KahluaTableImpl.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) continue;
            ++n;
        }
        kahluaTableIterator = this.iterator();
        dataOutputStream.writeInt(n);
        while (kahluaTableIterator.advance()) {
            byte by = KahluaTableImpl.getKeyByte(kahluaTableIterator.getKey());
            byte by2 = KahluaTableImpl.getValueByte(kahluaTableIterator.getValue());
            if (by == -1 || by2 == -1) continue;
            this.save(dataOutputStream, by, kahluaTableIterator.getKey());
            this.save(dataOutputStream, by2, kahluaTableIterator.getValue());
        }
    }

    private void save(DataOutputStream dataOutputStream, byte by, Object object) throws IOException, RuntimeException {
        dataOutputStream.writeByte(by);
        if (by == 0) {
            GameWindow.WriteString((DataOutputStream)dataOutputStream, (String)((String)object));
        } else if (by == 1) {
            dataOutputStream.writeDouble((Double)object);
        } else if (by == 3) {
            dataOutputStream.writeByte((Boolean)object != false ? 1 : 0);
        } else if (by == 2) {
            ((KahluaTableImpl)object).save(dataOutputStream);
        } else {
            throw new RuntimeException("invalid lua table type " + by);
        }
    }

    @Override
    public void load(ByteBuffer byteBuffer, int n) {
        int n2 = byteBuffer.getInt();
        this.wipe();
        if (n >= 25) {
            for (int i = 0; i < n2; ++i) {
                byte by = byteBuffer.get();
                Object object = this.load(byteBuffer, n, by);
                byte by2 = byteBuffer.get();
                Object object2 = this.load(byteBuffer, n, by2);
                this.rawset(object, object2);
            }
        } else {
            for (int i = 0; i < n2; ++i) {
                byte by = byteBuffer.get();
                String string = GameWindow.ReadString((ByteBuffer)byteBuffer);
                Object object = this.load(byteBuffer, n, by);
                this.rawset(string, object);
            }
        }
    }

    public Object load(ByteBuffer byteBuffer, int n, byte by) throws RuntimeException {
        if (by == 0) {
            return GameWindow.ReadString((ByteBuffer)byteBuffer);
        }
        if (by == 1) {
            return byteBuffer.getDouble();
        }
        if (by == 3) {
            return byteBuffer.get() == 1;
        }
        if (by == 2) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            kahluaTableImpl.load(byteBuffer, n);
            return kahluaTableImpl;
        }
        throw new RuntimeException("invalid lua table type " + by);
    }

    @Override
    public void load(DataInputStream dataInputStream, int n) throws IOException {
        int n2 = dataInputStream.readInt();
        if (n >= 25) {
            for (int i = 0; i < n2; ++i) {
                byte by = dataInputStream.readByte();
                Object object = this.load(dataInputStream, n, by);
                byte by2 = dataInputStream.readByte();
                Object object2 = this.load(dataInputStream, n, by2);
                this.rawset(object, object2);
            }
        } else {
            for (int i = 0; i < n2; ++i) {
                byte by = dataInputStream.readByte();
                String string = GameWindow.ReadString((DataInputStream)dataInputStream);
                Object object = this.load(dataInputStream, n, by);
                this.rawset(string, object);
            }
        }
    }

    public Object load(DataInputStream dataInputStream, int n, byte by) throws IOException, RuntimeException {
        if (by == 0) {
            return GameWindow.ReadString((DataInputStream)dataInputStream);
        }
        if (by == 1) {
            return dataInputStream.readDouble();
        }
        if (by == 3) {
            return dataInputStream.readByte() == 1;
        }
        if (by == 2) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            kahluaTableImpl.load(dataInputStream, n);
            return kahluaTableImpl;
        }
        throw new RuntimeException("invalid lua table type " + by);
    }

    @Override
    public String getString(String string) {
        return (String)this.rawget(string);
    }

    public KahluaTableImpl getRewriteTable() {
        return (KahluaTableImpl)this.reloadReplace;
    }

    public void setRewriteTable(Object object) {
        this.reloadReplace = (KahluaTableImpl)object;
    }

    private static byte getKeyByte(Object object) {
        if (object instanceof String) {
            return 0;
        }
        if (object instanceof Double) {
            return 1;
        }
        return -1;
    }

    private static byte getValueByte(Object object) {
        if (object instanceof String) {
            return 0;
        }
        if (object instanceof Double) {
            return 1;
        }
        if (object instanceof Boolean) {
            return 3;
        }
        if (object instanceof KahluaTableImpl) {
            return 2;
        }
        return -1;
    }

    public static boolean canSave(Object object, Object object2) {
        return KahluaTableImpl.getKeyByte(object) != -1 && KahluaTableImpl.getValueByte(object2) != -1;
    }
}

