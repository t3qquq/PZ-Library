// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.lwjglx.BufferUtils;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.znet.ZNetStatistics;
import zombie.debug.DebugLog;
import zombie.network.GameClient;

/**
 * Created by LEMMYPC on 10/01/14.
 */
public class RakNetPeerInterface {
    private static Thread mainThread;
    public static final int ID_NEW_INCOMING_CONNECTION = 19;
    public static final int ID_DISCONNECTION_NOTIFICATION = 21;
    public static final int ID_INCOMPATIBLE_PROTOCOL_VERSION = 25;
    public static final int ID_CONNECTED_PING = 0;
    public static final int ID_UNCONNECTED_PING = 1;
    public static final int ID_CONNECTION_LOST = 22;
    public static final int ID_ALREADY_CONNECTED = 18;
    public static final int ID_REMOTE_DISCONNECTION_NOTIFICATION = 31;
    public static final int ID_REMOTE_CONNECTION_LOST = 32;
    public static final int ID_REMOTE_NEW_INCOMING_CONNECTION = 33;
    public static final int ID_CONNECTION_BANNED = 23;
    public static final int ID_CONNECTION_ATTEMPT_FAILED = 17;
    public static final int ID_NO_FREE_INCOMING_CONNECTIONS = 20;
    public static final int ID_CONNECTION_REQUEST_ACCEPTED = 16;
    public static final int ID_INVALID_PASSWORD = 24;
    public static final int ID_PING = 28;
    public static final int ID_RAKVOICE_OPEN_CHANNEL_REQUEST = 44;
    public static final int ID_RAKVOICE_OPEN_CHANNEL_REPLY = 45;
    public static final int ID_RAKVOICE_CLOSE_CHANNEL = 46;
    public static final int ID_USER_PACKET_ENUM = 134;
    public static final int PacketPriority_IMMEDIATE = 0;
    public static final int PacketPriority_HIGH = 1;
    public static final int PacketPriority_MEDIUM = 2;
    public static final int PacketPriority_LOW = 3;
    public static final int PacketReliability_UNRELIABLE = 0;
    public static final int PacketReliability_UNRELIABLE_SEQUENCED = 1;
    public static final int PacketReliability_RELIABLE = 2;
    public static final int PacketReliability_RELIABLE_ORDERED = 3;
    public static final int PacketReliability_RELIABLE_SEQUENCED = 4;
    public static final int PacketReliability_UNRELIABLE_WITH_ACK_RECEIPT = 5;
    public static final int PacketReliability_RELIABLE_WITH_ACK_RECEIPT = 6;
    public static final int PacketReliability_RELIABLE_ORDERED_WITH_ACK_RECEIPT = 7;
    public static final byte ConnectionType_Disconnected = 0;
    public static final byte ConnectionType_UDPRakNet = 1;
    public static final byte ConnectionType_Steam = 2;
    ByteBuffer receiveBuf = BufferUtils.createByteBuffer(1000000);
    ByteBuffer sendBuf = BufferUtils.createByteBuffer(1000000);
    Lock sendLock = new ReentrantLock();

    public static void init() {
        mainThread = Thread.currentThread();
    }

    public native void Init(boolean steamMode);

    private native int Startup(int var1, String var2);

    public int Startup(int maxConnections) {
        return this.Startup(maxConnections, Core.getInstance().getVersion());
    }

    public native void Shutdown();

    public native void SetServerIP(String ip);

    public native void SetServerPort(int port, int UDPPort);

    public native void SetClientPort(int port);

    public native int Connect(String var1, int var2, String var3, boolean var4);

    public native int ConnectToSteamServer(long var1, String var3, boolean var4);

    public native String GetServerIP();

    public native long GetClientSteamID(long guid);

    public native long GetClientOwnerSteamID(long guid);

    public native void SetIncomingPassword(String password);

    public native void SetTimeoutTime(int time);

    public native void SetMaximumIncomingConnections(int num);

    public native void SetOccasionalPing(boolean bPing);

    public native void SetUnreliableTimeout(int timeout);

    private native boolean TryReceive();

    private native int nativeGetData(ByteBuffer var1);

    public boolean Receive(ByteBuffer buffer) {
        if (this.TryReceive()) {
            try {
                buffer.clear();
                this.receiveBuf.clear();
                int int0 = this.nativeGetData(this.receiveBuf);
                buffer.put(this.receiveBuf);
                buffer.flip();
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public int Send(ByteBuffer data, int PacketPriority, int PacketReliability, byte orderingChannel, long guid, boolean broadcast) {
        this.sendLock.lock();
        this.sendBuf.clear();
        if (data.remaining() > this.sendBuf.remaining()) {
            System.out.println("Packet data too big.");
            this.sendLock.unlock();
            return 0;
        } else {
            try {
                this.sendBuf.put(data);
                this.sendBuf.flip();
                int int0 = this.sendNative(this.sendBuf, this.sendBuf.remaining(), PacketPriority, PacketReliability, orderingChannel, guid, broadcast);
                this.sendLock.unlock();
                return int0;
            } catch (Exception exception) {
                System.out.println("Other weird packet data error.");
                exception.printStackTrace();
                this.sendLock.unlock();
                return 0;
            }
        }
    }

    public int SendRaw(ByteBuffer data, int PacketPriority, int PacketReliability, byte orderingChannel, long guid, boolean broadcast) {
        try {
            return this.sendNative(data, data.remaining(), PacketPriority, PacketReliability, orderingChannel, guid, broadcast);
        } catch (Exception exception) {
            System.out.println("Other weird packet data error.");
            exception.printStackTrace();
            return 0;
        }
    }

    private native int sendNative(ByteBuffer var1, int var2, int var3, int var4, byte var5, long var6, boolean var8);

    public native long getGuidFromIndex(int id);

    public native long getGuidOfPacket();

    public native String getIPFromGUID(long guid);

    public native void disconnect(long connectedGUID, String message);

    private void connectionStateChangedCallback(String string0, String string1) {
        Thread thread = Thread.currentThread();
        if (thread == mainThread) {
            LuaEventManager.triggerEvent("OnConnectionStateChanged", string0, string1);
        } else {
            DebugLog.Multiplayer.debugln("state=\"%s\", message=\"%s\", thread=%s", string0, string1, thread);
        }

        if (GameClient.bClient && "Connected".equals(string0)) {
            GameClient.instance.udpEngine.connected();
        }
    }

    public native ZNetStatistics GetNetStatistics(long guid);

    public native int GetAveragePing(long guid);

    public native int GetLastPing(long guid);

    public native int GetLowestPing(long guid);

    public native int GetMTUSize(long guid);

    public native int GetConnectionsNumber();

    public native byte GetConnectionType(long guid);
}
