// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.spnetwork.SinglePlayerClient;

public final class CGlobalObjectNetwork {
    private static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(1048576);
    private static final ByteBufferWriter BYTE_BUFFER_WRITER = new ByteBufferWriter(BYTE_BUFFER);
    private static KahluaTable tempTable;

    public static void receive(ByteBuffer byteBuffer) throws IOException {
        byte byte0 = byteBuffer.get();
        switch (byte0) {
            case 1:
                receiveServerCommand(byteBuffer);
            case 2:
            default:
                break;
            case 3:
                receiveNewLuaObjectAt(byteBuffer);
                break;
            case 4:
                receiveRemoveLuaObjectAt(byteBuffer);
                break;
            case 5:
                receiveUpdateLuaObjectAt(byteBuffer);
        }
    }

    private static void receiveServerCommand(ByteBuffer byteBuffer) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        boolean boolean0 = byteBuffer.get() == 1;
        KahluaTable table = null;
        if (boolean0) {
            table = LuaManager.platform.newTable();

            try {
                TableNetworkUtils.load(table, byteBuffer);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return;
            }
        }

        CGlobalObjects.receiveServerCommand(string0, string1, table);
    }

    private static void receiveNewLuaObjectAt(ByteBuffer byteBuffer) throws IOException {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        if (tempTable == null) {
            tempTable = LuaManager.platform.newTable();
        }

        TableNetworkUtils.load(tempTable, byteBuffer);
        CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
        if (cGlobalObjectSystem != null) {
            cGlobalObjectSystem.receiveNewLuaObjectAt(int0, int1, byte0, tempTable);
        }
    }

    private static void receiveRemoveLuaObjectAt(ByteBuffer byteBuffer) {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
        if (cGlobalObjectSystem != null) {
            cGlobalObjectSystem.receiveRemoveLuaObjectAt(int0, int1, byte0);
        }
    }

    private static void receiveUpdateLuaObjectAt(ByteBuffer byteBuffer) throws IOException {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        if (tempTable == null) {
            tempTable = LuaManager.platform.newTable();
        }

        TableNetworkUtils.load(tempTable, byteBuffer);
        CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByName(string);
        if (cGlobalObjectSystem != null) {
            cGlobalObjectSystem.receiveUpdateLuaObjectAt(int0, int1, byte0, tempTable);
        }
    }

    private static void sendPacket(ByteBuffer byteBuffer) {
        if (GameServer.bServer) {
            throw new IllegalStateException("can't call this method on the server");
        } else {
            if (GameClient.bClient) {
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                byteBuffer.flip();
                byteBufferWriter0.bb.put(byteBuffer);
                PacketTypes.PacketType.GlobalObjects.send(GameClient.connection);
            } else {
                ByteBufferWriter byteBufferWriter1 = SinglePlayerClient.connection.startPacket();
                byteBuffer.flip();
                byteBufferWriter1.bb.put(byteBuffer);
                SinglePlayerClient.connection.endPacketImmediate();
            }
        }
    }

    public static void sendClientCommand(IsoPlayer player, String string0, String string1, KahluaTable table) {
        BYTE_BUFFER.clear();
        writeClientCommand(player, string0, string1, table, BYTE_BUFFER_WRITER);
        sendPacket(BYTE_BUFFER);
    }

    private static void writeClientCommand(IsoPlayer player, String string0, String string1, KahluaTable table, ByteBufferWriter byteBufferWriter) {
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
        byteBufferWriter.putByte((byte)2);
        byteBufferWriter.putUTF(string0);
        byteBufferWriter.putUTF(string1);
        if (table != null && !table.isEmpty()) {
            byteBufferWriter.putByte((byte)1);

            try {
                KahluaTableIterator kahluaTableIterator = table.iterator();

                while (kahluaTableIterator.advance()) {
                    if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                        DebugLog.log("ERROR: sendClientCommand: can't save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
                    }
                }

                TableNetworkUtils.save(table, byteBufferWriter.bb);
            } catch (IOException iOException) {
                ExceptionLogger.logException(iOException);
            }
        } else {
            byteBufferWriter.putByte((byte)0);
        }
    }

    public static void Reset() {
        if (tempTable != null) {
            tempTable.wipe();
            tempTable = null;
        }
    }
}
