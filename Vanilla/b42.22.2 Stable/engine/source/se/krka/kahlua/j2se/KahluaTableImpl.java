// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
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
import zombie.Lua.LuaManager.GlobalObject;
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
    public static final int MAX_NESTING = 256;
    public static final String NESTING_EXCEEDED_MESSAGE = "lua table nesting exceeded";

    public KahluaTableImpl(Map<Object, Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setMetatable(KahluaTable metatable) {
        this.metatable = metatable;
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
    public void rawset(Object key, Object value) {
        if (this.reloadReplace != null) {
            this.reloadReplace.rawset(key, value);
        }

        Object lastVal = null;
        if (Core.debug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, key)) {
            lastVal = this.rawget(key);
        }

        if (value == null) {
            if (Core.debug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, key) && lastVal != null) {
                UIManager.debugBreakpoint(LuaManager.thread.currentfile, LuaManager.thread.lastLine);
            }

            this.delegate.remove(key);
        } else {
            if (Core.debug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, key) && !value.equals(lastVal)) {
                int a = GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
                if (a < 0) {
                    a = 0;
                }

                UIManager.debugBreakpoint(LuaManager.thread.currentfile, GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[a] - 1);
            }

            this.delegate.put(key, value);
        }
    }

    @Override
    public Object rawget(Object key) {
        if (this.reloadReplace != null) {
            return this.reloadReplace.rawget(key);
        }

        if (key == null) {
            return null;
        }

        if (Core.debug && LuaManager.thread != null && LuaManager.thread.hasReadDataBreakpoint(this, key)) {
            int a = GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
            if (a < 0) {
                a = 0;
            }

            UIManager.debugBreakpoint(LuaManager.thread.currentfile, GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[a] - 1);
        }

        return !this.delegate.containsKey(key) && this.metatable != null ? this.metatable.rawget(key) : this.delegate.get(key);
    }

    @Override
    public void rawset(int key, Object value) {
        this.rawset(KahluaUtil.toDouble(key), value);
    }

    public String rawgetStr(Object key) {
        return (String)this.rawget(key);
    }

    public KahluaTableImpl rawgetTable(Object key) {
        return (KahluaTableImpl)this.rawget(key);
    }

    public int rawgetInt(Object key) {
        Object value = this.rawget(key);
        if (value instanceof Double doubleKey) {
            return doubleKey.intValue();
        } else {
            return value instanceof Integer intKey ? intKey : -1;
        }
    }

    public boolean rawgetBool(Object key) {
        return this.rawget(key) instanceof Boolean ? (Boolean)this.rawget(key) : false;
    }

    public float rawgetFloat(Object key) {
        Object value = this.rawget(key);
        if (value instanceof Double d) {
            return d.floatValue();
        } else {
            return value instanceof Float f ? f : -1.0F;
        }
    }

    public float tryGetFloat(Object key, float defaultValue) {
        return this.rawget(key) instanceof Double d ? d.floatValue() : defaultValue;
    }

    @Override
    public Object rawget(int key) {
        return this.rawget(KahluaUtil.toDouble(key));
    }

    @Override
    public int len() {
        return KahluaUtil.len(this, 0, 2 * this.delegate.size());
    }

    @Override
    public KahluaTableIterator iterator() {
        final Object[] keys = this.delegate.isEmpty() ? null : this.delegate.keySet().toArray();
        return new KahluaTableIterator() {
            private Object curKey;
            private Object curValue;
            private int keyIndex;

            @Override
            public int call(LuaCallFrame callFrame, int nArguments) {
                return this.advance() ? callFrame.push(this.getKey(), this.getValue()) : 0;
            }

            @Override
            public boolean advance() {
                if (keys != null && this.keyIndex < keys.length) {
                    this.curKey = keys[this.keyIndex];
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
        return this.rawget("Type") instanceof String Type ? Type + " 0x" + System.identityHashCode(this) : "table 0x" + System.identityHashCode(this);
    }

    @Override
    public void save(ByteBuffer output) {
        KahluaTableIterator it = this.iterator();
        int count = 0;

        while (it.advance()) {
            if (canSave(it.getKey(), it.getValue())) {
                count++;
            }
        }

        it = this.iterator();
        output.putInt(count);

        while (it.advance()) {
            byte keyByte = getKeyByte(it.getKey());
            byte valueByte = getValueByte(it.getValue());
            if (keyByte != -1 && valueByte != -1) {
                this.save(output, keyByte, it.getKey());
                this.save(output, valueByte, it.getValue());
            }
        }
    }

    private void save(ByteBuffer output, byte sbyt, Object o) throws RuntimeException {
        output.put(sbyt);
        if (sbyt == 0) {
            GameWindow.WriteString(output, (String)o);
        } else if (sbyt == 1) {
            output.putDouble((Double)o);
        } else if (sbyt == 3) {
            output.put((byte)((Boolean)o ? 1 : 0));
        } else {
            if (sbyt != 2) {
                throw new RuntimeException("invalid lua table type " + sbyt);
            }

            ((KahluaTableImpl)o).save(output);
        }
    }

    @Override
    public void save(DataOutputStream output) throws IOException {
        KahluaTableIterator it = this.iterator();
        int count = 0;

        while (it.advance()) {
            if (canSave(it.getKey(), it.getValue())) {
                count++;
            }
        }

        it = this.iterator();
        output.writeInt(count);

        while (it.advance()) {
            byte keyByte = getKeyByte(it.getKey());
            byte valueByte = getValueByte(it.getValue());
            if (keyByte != -1 && valueByte != -1) {
                this.save(output, keyByte, it.getKey());
                this.save(output, valueByte, it.getValue());
            }
        }
    }

    private void save(DataOutputStream output, byte sbyt, Object o) throws IOException, RuntimeException {
        output.writeByte(sbyt);
        if (sbyt == 0) {
            GameWindow.WriteString(output, (String)o);
        } else if (sbyt == 1) {
            output.writeDouble((Double)o);
        } else if (sbyt == 3) {
            output.writeByte((Boolean)o ? 1 : 0);
        } else {
            if (sbyt != 2) {
                throw new RuntimeException("invalid lua table type " + sbyt);
            }

            ((KahluaTableImpl)o).save(output);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) {
        this.load(input, WorldVersion, 0);
    }

    public void load(ByteBuffer input, int WorldVersion, int depth) {
        int count = input.getInt();
        this.wipe();
        if (WorldVersion >= 25) {
            for (int n = 0; n < count; n++) {
                byte keyByte = input.get();
                Object key = this.load(input, WorldVersion, keyByte, depth);
                byte valueByte = input.get();
                Object value = this.load(input, WorldVersion, valueByte, depth);
                this.rawset(key, value);
            }
        } else {
            for (int n = 0; n < count; n++) {
                byte valueByte = input.get();
                String key = GameWindow.ReadString(input);
                Object value = this.load(input, WorldVersion, valueByte, depth);
                this.rawset(key, value);
            }
        }
    }

    public Object load(ByteBuffer input, int WorldVersion, byte sbyt, int depth) throws RuntimeException {
        if (sbyt == 0) {
            return GameWindow.ReadString(input);
        }

        if (sbyt == 1) {
            return input.getDouble();
        }

        if (sbyt == 3) {
            return input.get() != 0;
        }

        if (sbyt == 2) {
            if (depth + 1 >= 256) {
                throw new RuntimeException("lua table nesting exceeded");
            }

            KahluaTableImpl v = (KahluaTableImpl)LuaManager.platform.newTable();
            v.load(input, WorldVersion, depth + 1);
            return v;
        } else {
            throw new RuntimeException("invalid lua table type " + sbyt);
        }
    }

    @Override
    public void load(DataInputStream input, int WorldVersion) throws IOException {
        this.load(input, WorldVersion, 0);
    }

    public void load(DataInputStream input, int WorldVersion, int depth) throws IOException {
        int count = input.readInt();
        if (WorldVersion >= 25) {
            for (int n = 0; n < count; n++) {
                byte keyByte = input.readByte();
                Object key = this.load(input, WorldVersion, keyByte, depth);
                byte valueByte = input.readByte();
                Object value = this.load(input, WorldVersion, valueByte, depth);
                this.rawset(key, value);
            }
        } else {
            for (int n = 0; n < count; n++) {
                byte valueByte = input.readByte();
                String key = GameWindow.ReadString(input);
                Object value = this.load(input, WorldVersion, valueByte, depth);
                this.rawset(key, value);
            }
        }
    }

    public Object load(DataInputStream input, int WorldVersion, byte sbyt, int depth) throws IOException, RuntimeException {
        if (sbyt == 0) {
            return GameWindow.ReadString(input);
        }

        if (sbyt == 1) {
            return input.readDouble();
        }

        if (sbyt == 3) {
            return input.readByte() == 1;
        }

        if (sbyt == 2) {
            if (depth + 1 >= 256) {
                throw new RuntimeException("lua table nesting exceeded");
            }

            KahluaTableImpl v = (KahluaTableImpl)LuaManager.platform.newTable();
            v.load(input, WorldVersion, depth + 1);
            return v;
        } else {
            throw new RuntimeException("invalid lua table type " + sbyt);
        }
    }

    @Override
    public String getString(String string) {
        return (String)this.rawget(string);
    }

    public KahluaTableImpl getRewriteTable() {
        return (KahluaTableImpl)this.reloadReplace;
    }

    public void setRewriteTable(Object value) {
        this.reloadReplace = (KahluaTableImpl)value;
    }

    private static byte getKeyByte(Object o) {
        if (o instanceof String) {
            return 0;
        } else {
            return (byte)(o instanceof Double ? 1 : -1);
        }
    }

    private static byte getValueByte(Object o) {
        if (o instanceof String) {
            return 0;
        } else if (o instanceof Double) {
            return 1;
        } else if (o instanceof Boolean) {
            return 3;
        } else {
            return (byte)(o instanceof KahluaTableImpl ? 2 : -1);
        }
    }

    public static boolean canSave(Object key, Object value) {
        return getKeyByte(key) != -1 && getValueByte(value) != -1;
    }
}
