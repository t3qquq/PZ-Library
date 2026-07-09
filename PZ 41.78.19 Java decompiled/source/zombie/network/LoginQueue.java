// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.gameStates.LoadingQueueState;

public class LoginQueue {
    private static ArrayList<UdpConnection> LoginQueue = new ArrayList<>();
    private static ArrayList<UdpConnection> PreferredLoginQueue = new ArrayList<>();
    private static UdpConnection currentLoginQueue;
    private static UpdateLimit UpdateLimit = new UpdateLimit(3050L);
    private static UpdateLimit LoginQueueTimeout = new UpdateLimit(15000L);

    public static void receiveClientLoginQueueRequest(ByteBuffer byteBuffer, short var1) {
        byte byte0 = byteBuffer.get();
        if (byte0 == LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal()) {
            LoadingQueueState.onConnectionImmediate();
        } else if (byte0 == LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal()) {
            int int0 = byteBuffer.getInt();
            LoadingQueueState.onPlaceInQueue(int0);
            LuaEventManager.triggerEvent("OnConnectionStateChanged", "FormatMessage", "PlaceInQueue", int0);
        }

        ConnectionManager.log("receive-packet", "login-queue-request", null);
    }

    public static void receiveLoginQueueDone(ByteBuffer byteBuffer, UdpConnection udpConnection, short var2) {
        long long0 = byteBuffer.getLong();
        LoggerManager.getLogger("user").write("player " + udpConnection.username + " loading time was: " + long0 + " ms");
        synchronized (LoginQueue) {
            if (currentLoginQueue == udpConnection) {
                currentLoginQueue = null;
            }

            loadNextPlayer();
        }

        ConnectionManager.log("receive-packet", "login-queue-done", udpConnection);
        udpConnection.validator.sendChecksum(true, false, false);
    }

    public static void receiveServerLoginQueueRequest(ByteBuffer var0, UdpConnection udpConnection, short var2) {
        LoggerManager.getLogger("user")
            .write(
                udpConnection.idStr
                    + " \""
                    + udpConnection.username
                    + "\" attempting to join used "
                    + (udpConnection.preferredInQueue ? "preferred " : "")
                    + "queue"
            );
        synchronized (LoginQueue) {
            if (!ServerOptions.getInstance().LoginQueueEnabled.getValue()
                || !udpConnection.preferredInQueue && currentLoginQueue == null && PreferredLoginQueue.isEmpty() && LoginQueue.isEmpty()
                || udpConnection.preferredInQueue && currentLoginQueue == null && PreferredLoginQueue.isEmpty()) {
                if (Core.bDebug) {
                    DebugLog.log("receiveServerLoginQueueRequest: ConnectionImmediate (ip:" + udpConnection.ip + ")");
                }

                currentLoginQueue = udpConnection;
                currentLoginQueue.wasInLoadingQueue = true;
                LoginQueueTimeout.Reset(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000);
                ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
                byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal());
                PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
            } else {
                if (Core.bDebug) {
                    DebugLog.log(
                        "receiveServerLoginQueueRequest: PlaceInQueue (ip:" + udpConnection.ip + " preferredInQueue:" + udpConnection.preferredInQueue + ")"
                    );
                }

                if (udpConnection.preferredInQueue) {
                    if (!PreferredLoginQueue.contains(udpConnection)) {
                        PreferredLoginQueue.add(udpConnection);
                    }
                } else if (!LoginQueue.contains(udpConnection)) {
                    LoginQueue.add(udpConnection);
                }

                sendPlaceInTheQueue();
            }
        }

        ConnectionManager.log("receive-packet", "login-queue-request", udpConnection);
    }

    private static void sendAccessDenied(UdpConnection udpConnection, String string) {
        if (Core.bDebug) {
            DebugLog.log("sendAccessDenied: (ip:" + udpConnection.ip + " message:" + string + ")");
        }

        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.AccessDenied.send(udpConnection);
        ConnectionManager.log("access-denied", "invalid-queue", udpConnection);
        udpConnection.forceDisconnect("queue-" + string);
    }

    private static void sendPlaceInTheQueue() {
        for (UdpConnection udpConnection0 : PreferredLoginQueue) {
            ByteBufferWriter byteBufferWriter0 = udpConnection0.startPacket();
            PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter0);
            byteBufferWriter0.putByte((byte)LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal());
            byteBufferWriter0.putInt(PreferredLoginQueue.indexOf(udpConnection0) + 1);
            PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection0);
        }

        for (UdpConnection udpConnection1 : LoginQueue) {
            ByteBufferWriter byteBufferWriter1 = udpConnection1.startPacket();
            PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter1);
            byteBufferWriter1.putByte((byte)LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal());
            byteBufferWriter1.putInt(PreferredLoginQueue.size() + LoginQueue.indexOf(udpConnection1) + 1);
            PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection1);
        }
    }

    private static void sendConnectRequest(UdpConnection udpConnection) {
        if (Core.bDebug) {
            DebugLog.log("sendApplyRequest: (ip:" + udpConnection.ip + ")");
        }

        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal());
        PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
        ConnectionManager.log("send-packet", "login-queue-request", udpConnection);
    }

    public static boolean receiveLogin(UdpConnection udpConnection) {
        if (!ServerOptions.getInstance().LoginQueueEnabled.getValue()) {
            return true;
        } else {
            if (Core.bDebug) {
                DebugLog.log("receiveLogin: (ip:" + udpConnection.ip + ")");
            }

            if (udpConnection != currentLoginQueue) {
                sendAccessDenied(currentLoginQueue, "QueueNotFound");
                if (Core.bDebug) {
                    DebugLog.log("receiveLogin: error");
                }

                return false;
            } else {
                if (Core.bDebug) {
                    DebugLog.log("receiveLogin: ok");
                }

                return true;
            }
        }
    }

    public static void disconnect(UdpConnection udpConnection) {
        if (Core.bDebug) {
            DebugLog.log("disconnect: (ip:" + udpConnection.ip + ")");
        }

        synchronized (LoginQueue) {
            if (udpConnection == currentLoginQueue) {
                currentLoginQueue = null;
            } else {
                if (LoginQueue.contains(udpConnection)) {
                    LoginQueue.remove(udpConnection);
                }

                if (PreferredLoginQueue.contains(udpConnection)) {
                    PreferredLoginQueue.remove(udpConnection);
                }
            }

            sendPlaceInTheQueue();
        }
    }

    public static boolean isInTheQueue(UdpConnection udpConnection) {
        if (!ServerOptions.getInstance().LoginQueueEnabled.getValue()) {
            return false;
        } else {
            synchronized (LoginQueue) {
                return udpConnection == currentLoginQueue || LoginQueue.contains(udpConnection) || PreferredLoginQueue.contains(udpConnection);
            }
        }
    }

    public static void update() {
        if (ServerOptions.getInstance().LoginQueueEnabled.getValue() && UpdateLimit.Check()) {
            synchronized (LoginQueue) {
                if (currentLoginQueue != null) {
                    if (currentLoginQueue.isFullyConnected()) {
                        if (Core.bDebug) {
                            DebugLog.log("update: isFullyConnected (ip:" + currentLoginQueue.ip + ")");
                        }

                        currentLoginQueue = null;
                    } else if (LoginQueueTimeout.Check()) {
                        if (Core.bDebug) {
                            DebugLog.log("update: timeout (ip:" + currentLoginQueue.ip + ")");
                        }

                        currentLoginQueue = null;
                    }
                }

                loadNextPlayer();
            }
        }
    }

    private static void loadNextPlayer() {
        if (!PreferredLoginQueue.isEmpty() && currentLoginQueue == null) {
            currentLoginQueue = PreferredLoginQueue.remove(0);
            currentLoginQueue.wasInLoadingQueue = true;
            if (Core.bDebug) {
                DebugLog.log("update: Next player from the preferred queue to connect (ip:" + currentLoginQueue.ip + ")");
            }

            LoginQueueTimeout.Reset(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000);
            sendConnectRequest(currentLoginQueue);
            sendPlaceInTheQueue();
        }

        if (!LoginQueue.isEmpty() && currentLoginQueue == null) {
            currentLoginQueue = LoginQueue.remove(0);
            currentLoginQueue.wasInLoadingQueue = true;
            if (Core.bDebug) {
                DebugLog.log("update: Next player to connect (ip:" + currentLoginQueue.ip + ")");
            }

            LoginQueueTimeout.Reset(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000);
            sendConnectRequest(currentLoginQueue);
            sendPlaceInTheQueue();
        }
    }

    public static String getDescription() {
        return "queue=["
            + LoginQueue.size()
            + "/"
            + PreferredLoginQueue.size()
            + "/\""
            + (currentLoginQueue == null ? "" : currentLoginQueue.getConnectedGUID())
            + "\"]";
    }

    public static enum LoginQueueMessageType {
        ConnectionImmediate,
        PlaceInQueue;
    }
}
