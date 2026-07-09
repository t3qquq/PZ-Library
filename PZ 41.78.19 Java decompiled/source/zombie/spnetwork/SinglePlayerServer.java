// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.spnetwork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.globalObjects.SGlobalObjectNetwork;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.vehicles.BaseVehicle;

public final class SinglePlayerServer {
    private static final ArrayList<ZomboidNetData> MainLoopNetData = new ArrayList<>();
    public static final SinglePlayerServer.UdpEngineServer udpEngine = new SinglePlayerServer.UdpEngineServer();

    public static void addIncoming(short short0, ByteBuffer byteBuffer, UdpConnection udpConnection) {
        ZomboidNetData zomboidNetData;
        if (byteBuffer.remaining() > 2048) {
            zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
        } else {
            zomboidNetData = ZomboidNetDataPool.instance.get();
        }

        zomboidNetData.read(short0, byteBuffer, udpConnection);
        synchronized (MainLoopNetData) {
            MainLoopNetData.add(zomboidNetData);
        }
    }

    private static void sendObjectChange(IsoObject object, String string, KahluaTable table, UdpConnection udpConnection) {
        if (object.getSquare() != null) {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.ObjectChange.doPacket(byteBufferWriter);
            if (object instanceof IsoPlayer) {
                byteBufferWriter.putByte((byte)1);
                byteBufferWriter.putShort(((IsoPlayer)object).OnlineID);
            } else if (object instanceof BaseVehicle) {
                byteBufferWriter.putByte((byte)2);
                byteBufferWriter.putShort(((BaseVehicle)object).getId());
            } else if (object instanceof IsoWorldInventoryObject) {
                byteBufferWriter.putByte((byte)3);
                byteBufferWriter.putInt(object.getSquare().getX());
                byteBufferWriter.putInt(object.getSquare().getY());
                byteBufferWriter.putInt(object.getSquare().getZ());
                byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().getID());
            } else {
                byteBufferWriter.putByte((byte)0);
                byteBufferWriter.putInt(object.getSquare().getX());
                byteBufferWriter.putInt(object.getSquare().getY());
                byteBufferWriter.putInt(object.getSquare().getZ());
                byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
            }

            byteBufferWriter.putUTF(string);
            object.saveChange(string, table, byteBufferWriter.bb);
            udpConnection.endPacketImmediate();
        }
    }

    public static void sendObjectChange(IsoObject object, String string, KahluaTable table) {
        if (object != null) {
            for (int int0 = 0; int0 < udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = udpEngine.connections.get(int0);
                if (udpConnection.ReleventTo(object.getX(), object.getY())) {
                    sendObjectChange(object, string, table, udpConnection);
                }
            }
        }
    }

    public static void sendObjectChange(IsoObject object0, String string, Object... objects) {
        if (objects.length == 0) {
            sendObjectChange(object0, string, (KahluaTable)null);
        } else if (objects.length % 2 == 0) {
            KahluaTable table = LuaManager.platform.newTable();

            for (byte byte0 = 0; byte0 < objects.length; byte0 += 2) {
                Object object1 = objects[byte0 + 1];
                if (object1 instanceof Float) {
                    table.rawset(objects[byte0], ((Float)object1).doubleValue());
                } else if (object1 instanceof Integer) {
                    table.rawset(objects[byte0], ((Integer)object1).doubleValue());
                } else if (object1 instanceof Short) {
                    table.rawset(objects[byte0], ((Short)object1).doubleValue());
                } else {
                    table.rawset(objects[byte0], object1);
                }
            }

            sendObjectChange(object0, string, table);
        }
    }

    public static void sendServerCommand(String string0, String string1, KahluaTable table, UdpConnection udpConnection) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
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
                iOException.printStackTrace();
            }
        } else {
            byteBufferWriter.putByte((byte)0);
        }

        udpConnection.endPacketImmediate();
    }

    public static void sendServerCommand(String string0, String string1, KahluaTable table) {
        for (int int0 = 0; int0 < udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = udpEngine.connections.get(int0);
            sendServerCommand(string0, string1, table, udpConnection);
        }
    }

    public static void update() {
        if (!GameClient.bClient) {
            for (short short0 = 0; short0 < IsoPlayer.numPlayers; short0++) {
                if (IsoPlayer.players[short0] != null) {
                    IsoPlayer.players[short0].setOnlineID(short0);
                }
            }

            synchronized (MainLoopNetData) {
                for (int int0 = 0; int0 < MainLoopNetData.size(); int0++) {
                    ZomboidNetData zomboidNetData = MainLoopNetData.get(int0);
                    mainLoopDealWithNetData(zomboidNetData);
                    MainLoopNetData.remove(int0--);
                }
            }
        }
    }

    private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
        ByteBuffer byteBuffer = zomboidNetData.buffer;

        try {
            PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
            switch (packetType) {
                case ClientCommand:
                    receiveClientCommand(byteBuffer, zomboidNetData.connection);
                    break;
                case GlobalObjects:
                    receiveGlobalObjects(byteBuffer, zomboidNetData.connection);
            }
        } finally {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }
    }

    private static IsoPlayer getAnyPlayerFromConnection(UdpConnection udpConnection) {
        for (int int0 = 0; int0 < 4; int0++) {
            if (udpConnection.players[int0] != null) {
                return udpConnection.players[int0];
            }
        }

        return null;
    }

    private static IsoPlayer getPlayerFromConnection(UdpConnection udpConnection, int int0) {
        return int0 >= 0 && int0 < 4 ? udpConnection.players[int0] : null;
    }

    private static void receiveClientCommand(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        byte byte0 = byteBuffer.get();
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

        IsoPlayer player = getPlayerFromConnection(udpConnection, byte0);
        if (byte0 == -1) {
            player = getAnyPlayerFromConnection(udpConnection);
        }

        if (player == null) {
            DebugLog.log("receiveClientCommand: player is null");
        } else {
            LuaEventManager.triggerEvent("OnClientCommand", string0, string1, player, table);
        }
    }

    private static void receiveGlobalObjects(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        byte byte0 = byteBuffer.get();
        IsoPlayer player = getPlayerFromConnection(udpConnection, byte0);
        if (byte0 == -1) {
            player = getAnyPlayerFromConnection(udpConnection);
        }

        if (player == null) {
            DebugLog.log("receiveGlobalObjects: player is null");
        } else {
            SGlobalObjectNetwork.receive(byteBuffer, player);
        }
    }

    public static void Reset() {
        for (ZomboidNetData zomboidNetData : MainLoopNetData) {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }

        MainLoopNetData.clear();
    }

    public static final class UdpEngineServer extends UdpEngine {
        public final ArrayList<UdpConnection> connections = new ArrayList<>();

        UdpEngineServer() {
            this.connections.add(new UdpConnection(this));
        }

        @Override
        public void Send(ByteBuffer byteBuffer) {
            SinglePlayerClient.udpEngine.Receive(byteBuffer);
        }

        @Override
        public void Receive(ByteBuffer byteBuffer) {
            int int0 = byteBuffer.get() & 255;
            short short0 = byteBuffer.getShort();
            SinglePlayerServer.addIncoming(short0, byteBuffer, SinglePlayerServer.udpEngine.connections.get(0));
        }
    }
}
