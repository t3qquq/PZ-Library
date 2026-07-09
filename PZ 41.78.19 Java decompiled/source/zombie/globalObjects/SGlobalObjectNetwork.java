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
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.spnetwork.SinglePlayerServer;

public final class SGlobalObjectNetwork {
    public static final byte PACKET_ServerCommand = 1;
    public static final byte PACKET_ClientCommand = 2;
    public static final byte PACKET_NewLuaObjectAt = 3;
    public static final byte PACKET_RemoveLuaObjectAt = 4;
    public static final byte PACKET_UpdateLuaObjectAt = 5;
    private static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(1048576);
    private static final ByteBufferWriter BYTE_BUFFER_WRITER = new ByteBufferWriter(BYTE_BUFFER);

    public static void receive(ByteBuffer byteBuffer, IsoPlayer player) {
        byte byte0 = byteBuffer.get();
        switch (byte0) {
            case 2:
                receiveClientCommand(byteBuffer, player);
        }
    }

    private static void sendPacket(ByteBuffer byteBuffer) {
        if (GameServer.bServer) {
            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
                ByteBufferWriter byteBufferWriter0 = udpConnection0.startPacket();
                byteBuffer.flip();
                byteBufferWriter0.bb.put(byteBuffer);
                udpConnection0.endPacketImmediate();
            }
        } else {
            if (GameClient.bClient) {
                throw new IllegalStateException("can't call this method on the client");
            }

            for (int int1 = 0; int1 < SinglePlayerServer.udpEngine.connections.size(); int1++) {
                zombie.spnetwork.UdpConnection udpConnection1 = SinglePlayerServer.udpEngine.connections.get(int1);
                ByteBufferWriter byteBufferWriter1 = udpConnection1.startPacket();
                byteBuffer.flip();
                byteBufferWriter1.bb.put(byteBuffer);
                udpConnection1.endPacketImmediate();
            }
        }
    }

    private static void writeServerCommand(String string0, String string1, KahluaTable table, ByteBufferWriter byteBufferWriter) {
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putUTF(string0);
        byteBufferWriter.putUTF(string1);
        if (table != null && !table.isEmpty()) {
            byteBufferWriter.putByte((byte)1);

            try {
                KahluaTableIterator kahluaTableIterator = table.iterator();

                while (kahluaTableIterator.advance()) {
                    if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                        DebugLog.log("ERROR: sendServerCommand: can't save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
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

    public static void sendServerCommand(String string0, String string1, KahluaTable table) {
        BYTE_BUFFER.clear();
        writeServerCommand(string0, string1, table, BYTE_BUFFER_WRITER);
        sendPacket(BYTE_BUFFER);
    }

    public static void addGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
        BYTE_BUFFER.clear();
        ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)3);
        byteBufferWriter.putUTF(sGlobalObject.system.name);
        byteBufferWriter.putInt(sGlobalObject.getX());
        byteBufferWriter.putInt(sGlobalObject.getY());
        byteBufferWriter.putByte((byte)sGlobalObject.getZ());
        SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)sGlobalObject.system;
        TableNetworkUtils.saveSome(sGlobalObject.getModData(), byteBufferWriter.bb, sGlobalObjectSystem.objectSyncKeys);
        sendPacket(BYTE_BUFFER);
    }

    public static void removeGlobalObjectOnClient(GlobalObject globalObject) {
        BYTE_BUFFER.clear();
        ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)4);
        byteBufferWriter.putUTF(globalObject.system.name);
        byteBufferWriter.putInt(globalObject.getX());
        byteBufferWriter.putInt(globalObject.getY());
        byteBufferWriter.putByte((byte)globalObject.getZ());
        sendPacket(BYTE_BUFFER);
    }

    public static void updateGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
        BYTE_BUFFER.clear();
        ByteBufferWriter byteBufferWriter = BYTE_BUFFER_WRITER;
        PacketTypes.PacketType.GlobalObjects.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)5);
        byteBufferWriter.putUTF(sGlobalObject.system.name);
        byteBufferWriter.putInt(sGlobalObject.getX());
        byteBufferWriter.putInt(sGlobalObject.getY());
        byteBufferWriter.putByte((byte)sGlobalObject.getZ());
        SGlobalObjectSystem sGlobalObjectSystem = (SGlobalObjectSystem)sGlobalObject.system;
        TableNetworkUtils.saveSome(sGlobalObject.getModData(), byteBufferWriter.bb, sGlobalObjectSystem.objectSyncKeys);
        sendPacket(BYTE_BUFFER);
    }

    private static void receiveClientCommand(ByteBuffer byteBuffer, IsoPlayer player) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        boolean boolean0 = byteBuffer.get() == 1;
        KahluaTable table = null;
        if (boolean0) {
            table = LuaManager.platform.newTable();

            try {
                TableNetworkUtils.load(table, byteBuffer);
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }

        SGlobalObjects.receiveClientCommand(string0, string1, player, table);
    }
}
