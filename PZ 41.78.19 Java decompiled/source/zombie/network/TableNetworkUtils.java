// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;

public final class TableNetworkUtils {
    private static final byte SBYT_NO_SAVE = -1;
    private static final byte SBYT_STRING = 0;
    private static final byte SBYT_DOUBLE = 1;
    private static final byte SBYT_TABLE = 2;
    private static final byte SBYT_BOOLEAN = 3;
    private static final byte SBYT_ITEM = 4;
    private static final byte SBYT_DIRECTION = 5;

    public static void save(KahluaTable table, ByteBuffer byteBuffer) throws IOException {
        KahluaTableIterator kahluaTableIterator = table.iterator();
        int int0 = 0;

        while (kahluaTableIterator.advance()) {
            if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                int0++;
            }
        }

        kahluaTableIterator = table.iterator();
        byteBuffer.putInt(int0);

        while (kahluaTableIterator.advance()) {
            byte byte0 = getKeyByte(kahluaTableIterator.getKey());
            byte byte1 = getValueByte(kahluaTableIterator.getValue());
            if (byte0 != -1 && byte1 != -1) {
                save(byteBuffer, byte0, kahluaTableIterator.getKey());
                save(byteBuffer, byte1, kahluaTableIterator.getValue());
            }
        }
    }

    public static void saveSome(KahluaTable table, ByteBuffer byteBuffer, HashSet<? extends Object> hashSet) throws IOException {
        KahluaTableIterator kahluaTableIterator = table.iterator();
        int int0 = 0;

        while (kahluaTableIterator.advance()) {
            if (hashSet.contains(kahluaTableIterator.getKey()) && canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                int0++;
            }
        }

        kahluaTableIterator = table.iterator();
        byteBuffer.putInt(int0);

        while (kahluaTableIterator.advance()) {
            if (hashSet.contains(kahluaTableIterator.getKey())) {
                byte byte0 = getKeyByte(kahluaTableIterator.getKey());
                byte byte1 = getValueByte(kahluaTableIterator.getValue());
                if (byte0 != -1 && byte1 != -1) {
                    save(byteBuffer, byte0, kahluaTableIterator.getKey());
                    save(byteBuffer, byte1, kahluaTableIterator.getValue());
                }
            }
        }
    }

    private static void save(ByteBuffer byteBuffer, byte byte0, Object object) throws IOException, RuntimeException {
        byteBuffer.put(byte0);
        if (byte0 == 0) {
            GameWindow.WriteString(byteBuffer, (String)object);
        } else if (byte0 == 1) {
            byteBuffer.putDouble((Double)object);
        } else if (byte0 == 3) {
            byteBuffer.put((byte)((Boolean)object ? 1 : 0));
        } else if (byte0 == 2) {
            save((KahluaTable)object, byteBuffer);
        } else if (byte0 == 4) {
            ((InventoryItem)object).saveWithSize(byteBuffer, false);
        } else {
            if (byte0 != 5) {
                throw new RuntimeException("invalid lua table type " + byte0);
            }

            byteBuffer.put((byte)((IsoDirections)object).index());
        }
    }

    public static void load(KahluaTable table, ByteBuffer byteBuffer) throws IOException {
        int int0 = byteBuffer.getInt();
        table.wipe();

        for (int int1 = 0; int1 < int0; int1++) {
            byte byte0 = byteBuffer.get();
            Object object0 = load(byteBuffer, byte0);
            byte byte1 = byteBuffer.get();
            Object object1 = load(byteBuffer, byte1);
            table.rawset(object0, object1);
        }
    }

    public static Object load(ByteBuffer byteBuffer, byte byte0) throws IOException, RuntimeException {
        if (byte0 == 0) {
            return GameWindow.ReadString(byteBuffer);
        } else if (byte0 == 1) {
            return byteBuffer.getDouble();
        } else if (byte0 == 3) {
            return byteBuffer.get() == 1;
        } else if (byte0 == 2) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
            load(kahluaTableImpl, byteBuffer);
            return kahluaTableImpl;
        } else if (byte0 == 4) {
            InventoryItem item = null;

            try {
                item = InventoryItem.loadItem(byteBuffer, 195);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return item;
        } else if (byte0 == 5) {
            return IsoDirections.fromIndex(byteBuffer.get());
        } else {
            throw new RuntimeException("invalid lua table type " + byte0);
        }
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
        } else if (object instanceof KahluaTableImpl) {
            return 2;
        } else if (object instanceof InventoryItem) {
            return 4;
        } else {
            return (byte)(object instanceof IsoDirections ? 5 : -1);
        }
    }

    public static boolean canSave(Object object1, Object object0) {
        return getKeyByte(object1) != -1 && getValueByte(object0) != -1;
    }
}
