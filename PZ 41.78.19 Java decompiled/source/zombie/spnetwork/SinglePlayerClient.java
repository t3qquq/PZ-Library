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
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.globalObjects.CGlobalObjectNetwork;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public final class SinglePlayerClient {
    private static final ArrayList<ZomboidNetData> MainLoopNetData = new ArrayList<>();
    public static final UdpEngine udpEngine = new SinglePlayerClient.UdpEngineClient();
    public static final UdpConnection connection = new UdpConnection(udpEngine);

    public static void addIncoming(short short0, ByteBuffer byteBuffer) {
        ZomboidNetData zomboidNetData;
        if (byteBuffer.remaining() > 2048) {
            zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
        } else {
            zomboidNetData = ZomboidNetDataPool.instance.get();
        }

        zomboidNetData.read(short0, byteBuffer, connection);
        synchronized (MainLoopNetData) {
            MainLoopNetData.add(zomboidNetData);
        }
    }

    public static void update() throws Exception {
        if (!GameClient.bClient) {
            for (short short0 = 0; short0 < IsoPlayer.numPlayers; short0++) {
                if (IsoPlayer.players[short0] != null) {
                    IsoPlayer.players[short0].setOnlineID(short0);
                }
            }

            synchronized (MainLoopNetData) {
                for (int int0 = 0; int0 < MainLoopNetData.size(); int0++) {
                    ZomboidNetData zomboidNetData = MainLoopNetData.get(int0);

                    try {
                        mainLoopDealWithNetData(zomboidNetData);
                    } finally {
                        MainLoopNetData.remove(int0--);
                    }
                }
            }
        }
    }

    private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) throws Exception {
        ByteBuffer byteBuffer = zomboidNetData.buffer;

        try {
            PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
            switch (packetType) {
                case ClientCommand:
                    receiveServerCommand(byteBuffer);
                    break;
                case GlobalObjects:
                    CGlobalObjectNetwork.receive(byteBuffer);
                    break;
                case ObjectChange:
                    receiveObjectChange(byteBuffer);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + packetType);
            }
        } finally {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }
    }

    private static void delayPacket(int var0, int var1, int var2) {
    }

    private static IsoPlayer getPlayerByID(int int0) {
        return IsoPlayer.players[int0];
    }

    private static void receiveObjectChange(ByteBuffer byteBuffer) {
        byte byte0 = byteBuffer.get();
        if (byte0 == 1) {
            short short0 = byteBuffer.getShort();
            String string0 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string0);
            }

            IsoPlayer player = getPlayerByID(short0);
            if (player != null) {
                player.loadChange(string0, byteBuffer);
            }
        } else if (byte0 == 2) {
            short short1 = byteBuffer.getShort();
            String string1 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string1);
            }

            BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(short1);
            if (vehicle != null) {
                vehicle.loadChange(string1, byteBuffer);
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: unknown vehicle id=" + short1);
            }
        } else if (byte0 == 3) {
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            int int3 = byteBuffer.getInt();
            String string2 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string2);
            }

            IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (square0 == null) {
                delayPacket(int0, int1, int2);
                return;
            }

            for (int int4 = 0; int4 < square0.getWorldObjects().size(); int4++) {
                IsoWorldInventoryObject worldInventoryObject = square0.getWorldObjects().get(int4);
                if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().getID() == int3) {
                    worldInventoryObject.loadChange(string2, byteBuffer);
                    return;
                }
            }

            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: itemID=" + int3 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
            }
        } else {
            int int5 = byteBuffer.getInt();
            int int6 = byteBuffer.getInt();
            int int7 = byteBuffer.getInt();
            int int8 = byteBuffer.getInt();
            String string3 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string3);
            }

            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int7);
            if (square1 == null) {
                delayPacket(int5, int6, int7);
                return;
            }

            if (int8 >= 0 && int8 < square1.getObjects().size()) {
                IsoObject object = square1.getObjects().get(int8);
                object.loadChange(string3, byteBuffer);
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: index=" + int8 + " is invalid x,y,z=" + int5 + "," + int6 + "," + int7);
            }
        }
    }

    public static void sendClientCommand(IsoPlayer player, String string0, String string1, KahluaTable table) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
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
                iOException.printStackTrace();
            }
        } else {
            byteBufferWriter.putByte((byte)0);
        }

        connection.endPacketImmediate();
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
                exception.printStackTrace();
                return;
            }
        }

        LuaEventManager.triggerEvent("OnServerCommand", string0, string1, table);
    }

    public static void Reset() {
        for (ZomboidNetData zomboidNetData : MainLoopNetData) {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }

        MainLoopNetData.clear();
    }

    private static final class UdpEngineClient extends UdpEngine {
        @Override
        public void Send(ByteBuffer byteBuffer) {
            SinglePlayerServer.udpEngine.Receive(byteBuffer);
        }

        @Override
        public void Receive(ByteBuffer byteBuffer) {
            int int0 = byteBuffer.get() & 255;
            short short0 = byteBuffer.getShort();
            SinglePlayerClient.addIncoming(short0, byteBuffer);
        }
    }
}
