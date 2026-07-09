// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import java.util.HashSet;
import zombie.GameTime;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;

public class ItemTransactionManager {
    private static final HashSet<ItemTransactionManager.ItemRequest> requests = new HashSet<>();

    public static void update() {
        requests.removeIf(ItemTransactionManager.ItemRequest::isTimeout);
    }

    public static boolean isConsistent(int int0, int int1, int int2) {
        return requests.stream()
            .filter(
                itemRequest -> int0 == itemRequest.itemID
                    || int1 == itemRequest.itemID
                    || int2 == itemRequest.itemID
                    || int0 == itemRequest.srcID
                    || int0 == itemRequest.dstID
            )
            .noneMatch(itemRequest -> itemRequest.state == 1);
    }

    public static void receiveOnClient(ByteBuffer byteBuffer, short var1) {
        try {
            byte byte0 = byteBuffer.get();
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            DebugLog.Multiplayer.debugln("%d [ %d : %d => %d ]", byte0, int0, int1, int2);
            requests.stream()
                .filter(itemRequest -> int0 == itemRequest.itemID && int1 == itemRequest.srcID && int2 == itemRequest.dstID)
                .forEach(itemRequest -> itemRequest.setState(byte0));
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveOnClient: failed", LogSeverity.Error);
        }
    }

    public static void receiveOnServer(ByteBuffer byteBuffer, UdpConnection udpConnection, short var2) {
        try {
            byte byte0 = byteBuffer.get();
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            if (0 == byte0) {
                if (isConsistent(int0, int1, int2)) {
                    requests.add(new ItemTransactionManager.ItemRequest(int0, int1, int2));
                    sendItemTransaction(udpConnection, (byte)2, int0, int1, int2);
                    DebugLog.Multiplayer.trace("set accepted [ %d : %d => %d ]", int0, int1, int2);
                } else {
                    sendItemTransaction(udpConnection, (byte)1, int0, int1, int2);
                    DebugLog.Multiplayer.trace("set rejected [ %d : %d => %d ]", int0, int1, int2);
                }
            } else {
                requests.removeIf(itemRequest -> int0 == itemRequest.itemID && int1 == itemRequest.srcID && int2 == itemRequest.dstID);
                DebugLog.Multiplayer.trace("remove processed [ %d : %d => %d ]", int0, int1, int2);
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveOnClient: failed", LogSeverity.Error);
        }
    }

    public static void createItemTransaction(int int0, int int1, int int2) {
        if (isConsistent(int0, int1, int2)) {
            requests.add(new ItemTransactionManager.ItemRequest(int0, int1, int2));
            sendItemTransaction(GameClient.connection, (byte)0, int0, int1, int2);
        }
    }

    public static void removeItemTransaction(int int0, int int1, int int2) {
        if (requests.removeIf(itemRequest -> int0 == itemRequest.itemID && int1 == itemRequest.srcID && int2 == itemRequest.dstID)) {
            sendItemTransaction(GameClient.connection, (byte)2, int0, int1, int2);
        }
    }

    private static void sendItemTransaction(UdpConnection udpConnection, byte byte0, int int0, int int1, int int2) {
        if (udpConnection != null) {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();

            try {
                PacketTypes.PacketType.ItemTransaction.doPacket(byteBufferWriter);
                byteBufferWriter.putByte(byte0);
                byteBufferWriter.putInt(int0);
                byteBufferWriter.putInt(int1);
                byteBufferWriter.putInt(int2);
                PacketTypes.PacketType.ItemTransaction.send(udpConnection);
            } catch (Exception exception) {
                udpConnection.cancelPacket();
                DebugLog.Multiplayer.printException(exception, "SendItemTransaction: failed", LogSeverity.Error);
            }
        }
    }

    private static class ItemRequest {
        private static final byte StateUnknown = 0;
        private static final byte StateRejected = 1;
        private static final byte StateAccepted = 2;
        private final int itemID;
        private final int srcID;
        private final int dstID;
        private final long timestamp;
        private byte state;

        private ItemRequest(int int0, int int1, int int2) {
            this.itemID = int0;
            this.srcID = int1;
            this.dstID = int2;
            this.timestamp = GameTime.getServerTimeMills() + 5000L;
            this.state = (byte)(GameServer.bServer ? 1 : 0);
        }

        private void setState(byte byte0) {
            this.state = byte0;
        }

        private boolean isTimeout() {
            return GameTime.getServerTimeMills() > this.timestamp;
        }
    }
}
