// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

public final class KahluaTableImpl implements KahluaTable {
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
    public void setMetatable(KahluaTable arg0) {
        this.metatable = arg0;
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
    public void rawset(Object arg0, Object arg1) {
        if (this.reloadReplace != null) {
            this.reloadReplace.rawset(arg0, arg1);
        }

        Object object = null;
        if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, arg0)) {
            object = this.rawget(arg0);
        }

        if (arg1 == null) {
            if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, arg0) && object != null) {
                UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.lastLine);
            }

            this.delegate.remove(arg0);
        } else {
            if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, arg0) && !arg1.equals(object)) {
                int int0 = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
                if (int0 < 0) {
                    int0 = 0;
                }

                UIManager.debugBreakpoint(
                    LuaManager.thread.currentfile, LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[int0] - 1
                );
            }

            this.delegate.put(arg0, arg1);
        }
    }

    @Override
    public Object rawget(Object arg0) {
        if (this.reloadReplace != null) {
            return this.reloadReplace.rawget(arg0);
        } else if (arg0 == null) {
            return null;
        } else {
            if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasReadDataBreakpoint(this, arg0)) {
                int int0 = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
                if (int0 < 0) {
                    int0 = 0;
                }

                UIManager.debugBreakpoint(
                    LuaManager.thread.currentfile, LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[int0] - 1
                );
            }

            return !this.delegate.containsKey(arg0) && this.metatable != null ? this.metatable.rawget(arg0) : this.delegate.get(arg0);
        }
    }

    @Override
    public void rawset(int arg0, Object arg1) {
        this.rawset(KahluaUtil.toDouble((long)arg0), arg1);
    }

    public String rawgetStr(Object arg0) {
        return (String)this.rawget(arg0);
    }

    public int rawgetInt(Object arg0) {
        return this.rawget(arg0) instanceof Double ? ((Double)this.rawget(arg0)).intValue() : -1;
    }

    public boolean rawgetBool(Object arg0) {
        return this.rawget(arg0) instanceof Boolean ? (Boolean)this.rawget(arg0) : false;
    }

    public float rawgetFloat(Object arg0) {
        return this.rawget(arg0) instanceof Double ? ((Double)this.rawget(arg0)).floatValue() : -1.0F;
    }

    @Override
    public Object rawget(int arg0) {
        return this.rawget(KahluaUtil.toDouble((long)arg0));
    }

    @Override
    public int len() {
        return KahluaUtil.len(this, 0, 2 * this.delegate.size());
    }

    @Override
    public KahluaTableIterator iterator() {
        final Object[] objects = this.delegate.isEmpty() ? null : this.delegate.keySet().toArray();
        return new KahluaTableIterator() {
            private Object curKey;
            private Object curValue;
            private int keyIndex;

            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                return this.advance() ? luaCallFrame.push(this.getKey(), this.getValue()) : 0;
            }

            @Override
            public boolean advance() {
                if (objects != null && this.keyIndex < objects.length) {
                    this.curKey = objects[this.keyIndex];
                    this.curValue = KahluaTableImpl.this.delegate.get(this.curKey);
                    this.keyIndex++;
                    return true;
                } else {
                    this.curKey = null;
                    this.curValue = null;
                    return false;
                }
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

    @Override
    public String toString() {
        return "table 0x" + System.identityHashCode(this);
    }

    @Override
    public void save(ByteBuffer arg0) {
        KahluaTableIterator kahluaTableIterator = this.iterator();
        int int0 = 0;

        while (kahluaTableIterator.advance()) {
            if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                int0++;
            }
        }

        kahluaTableIterator = this.iterator();
        arg0.putInt(int0);

        while (kahluaTableIterator.advance()) {
            byte byte0 = getKeyByte(kahluaTableIterator.getKey());
            byte byte1 = getValueByte(kahluaTableIterator.getValue());
            if (byte0 != -1 && byte1 != -1) {
                this.save(arg0, byte0, kahluaTableIterator.getKey());
                this.save(arg0, byte1, kahluaTableIterator.getValue());
            }
        }
    }

    private void save(ByteBuffer byteBuffer, byte byte0, Object object) throws RuntimeException {
        byteBuffer.put(byte0);
        if (byte0 == 0) {
            GameWindow.WriteString(byteBuffer, (String)object);
        } else if (byte0 == 1) {
            byteBuffer.putDouble((Double)object);
        } else if (byte0 == 3) {
            byteBuffer.put((byte)((Boolean)object ? 1 : 0));
        } else {
            if (byte0 != 2) {
                throw new RuntimeException("invalid lua table type " + byte0);
            }

            ((KahluaTableImpl)object).save(byteBuffer);
        }
    }

    @Override
    public void save(DataOutputStream arg0) throws IOException {
        KahluaTableIterator kahluaTableIterator = this.iterator();
        int int0 = 0;

        while (kahluaTableIterator.advance()) {
            if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                int0++;
            }
        }

        kahluaTableIterator = this.iterator();
        arg0.writeInt(int0);

        while (kahluaTableIterator.advance()) {
            byte byte0 = getKeyByte(kahluaTableIterator.getKey());
            byte byte1 = getValueByte(kahluaTableIterator.getValue());
            if (byte0 != -1 && byte1 != -1) {
                this.save(arg0, byte0, kahluaTableIterator.getKey());
                this.save(arg0, byte1, kahluaTableIterator.getValue());
            }
        }
    }

    private void save(DataOutputStream dataOutputStream, byte byte0, Object object) throws IOException, RuntimeException {
        dataOutputStream.writeByte(byte0);
        if (byte0 == 0) {
            GameWindow.WriteString(dataOutputStream, (String)object);
        } else if (byte0 == 1) {
            dataOutputStream.writeDouble((Double)object);
        } else if (byte0 == 3) {
            dataOutputStream.writeByte((Boolean)object ? 1 : 0);
        } else {
            if (byte0 != 2) {
                throw new RuntimeException("invalid lua table type " + byte0);
            }

            ((KahluaTableImpl)object).save(dataOutputStream);
        }
    }

    @Override
    public void load(ByteBuffer arg0, int arg1) {
        int int0 = arg0.getInt();
        this.wipe();
        if (arg1 >= 25) {
            for (int int1 = 0; int1 < int0; int1++) {
                byte byte0 = arg0.get();
                Object object0 = this.load(arg0, arg1, byte0);
                byte byte1 = arg0.get();
                Object object1 = this.load(arg0, arg1, byte1);
                this.rawset(object0, object1);
            }
        } else {
            for (int int2 = 0; int2 < int0; int2++) {
                byte byte2 = arg0.get();
                String string = GameWindow.ReadString(arg0);
                Object object2 = this.load(arg0, arg1, byte2);
                this.rawset(string, object2);
            }
        }
    }

    public Object load(ByteBuffer arg0, int arg1, byte arg2) throws RuntimeException {
        if (arg2 == 0) {
            return GameWindow.ReadString(arg0);
        } else if (arg2 == 1) {
            return arg0.getDouble();
        } else if (arg2 == 3) {
            return arg0.get() == 1;
        } else if (arg2 == 2) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            kahluaTableImpl.load(arg0, arg1);
            return kahluaTableImpl;
        } else {
            throw new RuntimeException("invalid lua table type " + arg2);
        }
    }

    @Override
    public void load(DataInputStream arg0, int arg1) throws IOException {
        int int0 = arg0.readInt();
        if (arg1 >= 25) {
            for (int int1 = 0; int1 < int0; int1++) {
                byte byte0 = arg0.readByte();
                Object object0 = this.load(arg0, arg1, byte0);
                byte byte1 = arg0.readByte();
                Object object1 = this.load(arg0, arg1, byte1);
                this.rawset(object0, object1);
            }
        } else {
            for (int int2 = 0; int2 < int0; int2++) {
                byte byte2 = arg0.readByte();
                String string = GameWindow.ReadString(arg0);
                Object object2 = this.load(arg0, arg1, byte2);
                this.rawset(string, object2);
            }
        }
    }

    public Object load(DataInputStream arg0, int arg1, byte arg2) throws IOException, RuntimeException {
        if (arg2 == 0) {
            return GameWindow.ReadString(arg0);
        } else if (arg2 == 1) {
            return arg0.readDouble();
        } else if (arg2 == 3) {
            return arg0.readByte() == 1;
        } else if (arg2 == 2) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            kahluaTableImpl.load(arg0, arg1);
            return kahluaTableImpl;
        } else {
            throw new RuntimeException("invalid lua table type " + arg2);
        }
    }

    @Override
    public String getString(String arg0) {
        return (String)this.rawget(arg0);
    }

    public KahluaTableImpl getRewriteTable() {
        return (KahluaTableImpl)this.reloadReplace;
    }

    public void setRewriteTable(Object arg0) {
        this.reloadReplace = (KahluaTableImpl)arg0;
    }

    private static byte getKeyByte(Object object) {
        if (object instanceof String) {
            return 0;
        } else {
            return (byte)(object instanceof Double ? 1 : -1);
        }
    }

    private static byte getValueByte(Object object) {
        if (object instanceof String) {
            return 0;
        } else if (object instanceof Double) {
            return 1;
        } else if (object instanceof Boolean) {
            return 3;
        } else {
            return (byte)(object instanceof KahluaTableImpl ? 2 : -1);
        }
    }

    public static boolean canSave(Object arg0, Object arg1) {
        return getKeyByte(arg0) != -1 && getValueByte(arg1) != -1;
    }
}
