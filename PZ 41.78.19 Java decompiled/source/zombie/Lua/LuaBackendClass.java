// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;

public class LuaBackendClass implements KahluaTable {
    KahluaTable table;
    KahluaTable typeTable;

    @Override
    public String getString(String string) {
        return (String)this.rawget(string);
    }

    public LuaBackendClass(String string) {
        this.typeTable = (KahluaTable)LuaManager.env.rawget(string);
    }

    public void callVoid(String string) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), this.table);
    }

    public void callVoid(String string, Object object) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object});
    }

    public void callVoid(String string, Object object0, Object object1) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object0, object1});
    }

    public void callVoid(String string, Object object0, Object object1, Object object2) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object0, object1, object2});
    }

    public void callVoid(String string, Object object0, Object object1, Object object2, Object object3) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object0, object1, object2, object3});
    }

    public void callVoid(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object0, object1, object2, object3, object4});
    }

    public Object call(String string) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table)[1];
    }

    public Object call(String string, Object object) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1];
    }

    public Object call(String string, Object object0, Object object1) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1)[1];
    }

    public Object call(String string, Object object0, Object object1, Object object2) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2)[1];
    }

    public Object call(String string, Object object0, Object object1, Object object2, Object object3) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3)[1];
    }

    public Object call(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3, object4)[1];
    }

    public int callInt(String string) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table)[1]).intValue();
    }

    public int callInt(String string, Object object) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1]).intValue();
    }

    public int callInt(String string, Object object0, Object object1) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1)[1]).intValue();
    }

    public int callInt(String string, Object object0, Object object1, Object object2) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2)[1]).intValue();
    }

    public int callInt(String string, Object object0, Object object1, Object object2, Object object3) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3)[1])
            .intValue();
    }

    public int callInt(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3, object4)[1])
            .intValue();
    }

    public float callFloat(String string) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table)[1]).floatValue();
    }

    public float callFloat(String string, Object object) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1]).floatValue();
    }

    public float callFloat(String string, Object object0, Object object1) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1)[1]).floatValue();
    }

    public float callFloat(String string, Object object0, Object object1, Object object2) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2)[1]).floatValue();
    }

    public float callFloat(String string, Object object0, Object object1, Object object2, Object object3) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3)[1])
            .floatValue();
    }

    public float callFloat(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3, object4)[1])
            .floatValue();
    }

    public boolean callBool(String string) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table)[1];
    }

    public boolean callBool(String string, Object object) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1];
    }

    public boolean callBool(String string, Object object0, Object object1) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1)[1];
    }

    public boolean callBool(String string, Object object0, Object object1, Object object2) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2)[1];
    }

    public boolean callBool(String string, Object object0, Object object1, Object object2, Object object3) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3)[1];
    }

    public boolean callBool(String string, Object object0, Object object1, Object object2, Object object3, Object object4) {
        return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object0, object1, object2, object3, object4)[1];
    }

    @Override
    public void setMetatable(KahluaTable tablex) {
        this.table.setMetatable(tablex);
    }

    @Override
    public KahluaTable getMetatable() {
        return this.table.getMetatable();
    }

    @Override
    public void rawset(Object object0, Object object1) {
        this.table.rawset(object0, object1);
    }

    @Override
    public Object rawget(Object object) {
        return this.table.rawget(object);
    }

    @Override
    public void rawset(int int0, Object object) {
        this.table.rawset(int0, object);
    }

    @Override
    public Object rawget(int int0) {
        return this.table.rawget(int0);
    }

    @Override
    public int len() {
        return this.table.len();
    }

    @Override
    public int size() {
        return this.table.len();
    }

    @Override
    public KahluaTableIterator iterator() {
        return this.table.iterator();
    }

    @Override
    public boolean isEmpty() {
        return this.table.isEmpty();
    }

    @Override
    public void wipe() {
        this.table.wipe();
    }

    @Override
    public void save(ByteBuffer byteBuffer) throws IOException {
        this.table.save(byteBuffer);
    }

    @Override
    public void load(ByteBuffer byteBuffer, int int0) throws IOException {
        this.table.load(byteBuffer, int0);
    }

    @Override
    public void save(DataOutputStream dataOutputStream) throws IOException {
        this.table.save(dataOutputStream);
    }

    @Override
    public void load(DataInputStream dataInputStream, int int0) throws IOException {
        this.table.load(dataInputStream, int0);
    }
}
