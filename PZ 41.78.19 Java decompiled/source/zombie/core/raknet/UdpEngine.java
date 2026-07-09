// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.secure.PZcrypt;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.network.ConnectionManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PacketValidator;
import zombie.network.RequestDataManager;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;

public class UdpEngine {
    private int maxConnections = 0;
    private final Map<Long, UdpConnection> connectionMap = new HashMap<>();
    public final List<UdpConnection> connections = new ArrayList<>();
    protected final RakNetPeerInterface peer;
    final boolean bServer;
    Lock bufferLock = new ReentrantLock();
    private ByteBuffer bb = ByteBuffer.allocate(500000);
    private ByteBufferWriter bbw = new ByteBufferWriter(this.bb);
    public int port = 0;
    private final Thread thread;
    private boolean bQuit;
    UdpConnection[] connectionArray = new UdpConnection[256];
    ByteBuffer buf = ByteBuffer.allocate(1000000);

    public UdpEngine(int _port, int UDPPort, int _maxConnections, String serverPassword, boolean bListen) throws ConnectException {
        this.port = _port;
        this.peer = new RakNetPeerInterface();
        DebugLog.Network.println("Initialising RakNet...");
        this.peer.Init(SteamUtils.isSteamModeEnabled());
        this.peer.SetMaximumIncomingConnections(_maxConnections);
        this.bServer = bListen;
        if (this.bServer) {
            if (GameServer.IPCommandline != null) {
                this.peer.SetServerIP(GameServer.IPCommandline);
            }

            this.peer.SetServerPort(_port, UDPPort);
            this.peer.SetIncomingPassword(this.hashServerPassword(serverPassword));
        } else {
            this.peer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
        }

        this.peer.SetOccasionalPing(true);
        this.maxConnections = _maxConnections;
        int int0 = this.peer.Startup(_maxConnections);
        DebugLog.Network.println("RakNet.Startup() return code: %s (0 means success)", int0);
        if (int0 != 0) {
            throw new ConnectException("Connection Startup Failed. Code: " + int0);
        } else {
            if (bListen) {
                VoiceManager.instance.InitVMServer();
            }

            this.thread = new Thread(ThreadGroups.Network, this::threadRun, "UdpEngine");
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    private void threadRun() {
        while (!this.bQuit) {
            ByteBuffer byteBuffer = this.Receive();
            if (!this.bQuit) {
                try {
                    this.decode(byteBuffer);
                } catch (Exception exception) {
                    DebugLog.Network.printException(exception, "Exception thrown during decode.", LogSeverity.Error);
                }
                continue;
            }
            break;
        }
    }

    public void Shutdown() {
        DebugLog.log("waiting for UdpEngine thread termination");
        this.bQuit = true;

        while (this.thread.isAlive()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException interruptedException) {
            }
        }

        this.peer.Shutdown();
    }

    public void SetServerPassword(String password) {
        if (this.peer != null) {
            this.peer.SetIncomingPassword(password);
        }
    }

    public String hashServerPassword(String password) {
        return PZcrypt.hash(password, true);
    }

    public String getServerIP() {
        return this.peer.GetServerIP();
    }

    public long getClientSteamID(long guid) {
        return this.peer.GetClientSteamID(guid);
    }

    public long getClientOwnerSteamID(long guid) {
        return this.peer.GetClientOwnerSteamID(guid);
    }

    public ByteBufferWriter startPacket() {
        this.bufferLock.lock();
        this.bb.clear();
        return this.bbw;
    }

    public void endPacketBroadcast(PacketTypes.PacketType packetType) {
        this.bb.flip();
        this.peer.Send(this.bb, packetType.PacketPriority, packetType.PacketPriority, (byte)0, -1L, true);
        this.bufferLock.unlock();
    }

    public void endPacketBroadcastExcept(int priority, int reliability, UdpConnection connection) {
        this.bb.flip();
        this.peer.Send(this.bb, priority, reliability, (byte)0, connection.connectedGUID, true);
        this.bufferLock.unlock();
    }

    public void connected() {
        VoiceManager.instance.VoiceConnectReq(GameClient.connection.getConnectedGUID());
        if (GameClient.bClient && !GameClient.askPing) {
            GameClient.startAuth = Calendar.getInstance();
            ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
            PacketTypes.PacketType.Login.doPacket(byteBufferWriter0);
            byteBufferWriter0.putUTF(GameClient.username);
            byteBufferWriter0.putUTF(PZcrypt.hash(ServerWorldDatabase.encrypt(GameClient.password)));
            byteBufferWriter0.putUTF(Core.getInstance().getVersion());
            PacketTypes.PacketType.Login.send(GameClient.connection);
            RequestDataManager.getInstance().clear();
            ConnectionManager.log("send-packet", "login", GameClient.connection);
        } else if (GameClient.bClient && GameClient.askPing) {
            ByteBufferWriter byteBufferWriter1 = GameClient.connection.startPacket();
            PacketTypes.PacketType.Ping.doPacket(byteBufferWriter1);
            byteBufferWriter1.putUTF(GameClient.ip);
            PacketTypes.PacketType.Ping.send(GameClient.connection);
            RequestDataManager.getInstance().clear();
        }
    }

    private void decode(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.get() & 255;
        switch (int0) {
            case 0:
            case 1:
                break;
            case 16:
                int int9 = byteBuffer.get() & 255;
                long long6 = this.peer.getGuidOfPacket();
                if (GameClient.bClient) {
                    GameClient.connection = this.addConnection(int9, long6);
                    ConnectionManager.log("RakNet", "connection-request-accepted", this.connectionArray[int9]);
                    if (!SteamUtils.isSteamModeEnabled()) {
                        this.connected();
                    } else {
                        GameClient.steamID = SteamUser.GetSteamID();
                    }
                } else {
                    ConnectionManager.log("RakNet", "connection-request-accepted", this.connectionArray[int9]);
                }
                break;
            case 17:
                ConnectionManager.log("RakNet", "connection-attempt-failed", null);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 18:
                ConnectionManager.log("RakNet", "already-connected", null);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 19:
                int int1 = byteBuffer.get() & 255;
                long long0 = this.peer.getGuidOfPacket();
                this.addConnection(int1, long0);
                ConnectionManager.log("RakNet", "new-incoming-connection", this.connectionArray[int1]);
                break;
            case 20:
                int int5 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "no-free-incoming-connections", this.connectionArray[int5]);
                break;
            case 21:
                int int10 = byteBuffer.get() & 255;
                long long7 = this.peer.getGuidOfPacket();
                ConnectionManager.log("RakNet", "disconnection-notification", this.connectionArray[int10]);
                this.removeConnection(int10);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 22:
                int int2 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "connection-lost", this.connectionArray[int2]);
                this.removeConnection(int2);
                break;
            case 23:
                int int7 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "connection-banned", this.connectionArray[int7]);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 24:
                int int6 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "invalid-password", this.connectionArray[int6]);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 25:
                ConnectionManager.log("RakNet", "incompatible-protocol-version", null);
                String string = GameWindow.ReadString(byteBuffer);
                LuaEventManager.triggerEvent("OnConnectionStateChanged", "ClientVersionMismatch", string);
                break;
            case 31:
                int int3 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "remote-disconnection-notification", this.connectionArray[int3]);
                break;
            case 32:
                int int8 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "remote-connection-lost", this.connectionArray[int8]);
                if (GameClient.bClient) {
                    GameClient.instance.addDisconnectPacket(int0);
                }
                break;
            case 33:
                int int4 = byteBuffer.get() & 255;
                ConnectionManager.log("RakNet", "remote-new-incoming-connection", this.connectionArray[int4]);
                break;
            case 44:
                long long1 = this.peer.getGuidOfPacket();
                VoiceManager.instance.VoiceConnectAccept(long1);
                break;
            case 45:
                long long2 = this.peer.getGuidOfPacket();
                VoiceManager.instance.VoiceOpenChannelReply(long2, byteBuffer);
                break;
            case 46:
                long long3 = this.peer.getGuidOfPacket();
                UdpConnection udpConnection0 = this.connectionMap.get(long3);
                break;
            case 134:
                short short0 = byteBuffer.getShort();
                if (GameServer.bServer) {
                    long long5 = this.peer.getGuidOfPacket();
                    UdpConnection udpConnection2 = this.connectionMap.get(long5);
                    if (udpConnection2 == null) {
                        DebugLog.Network.warn("GOT PACKET FROM UNKNOWN CONNECTION guid=%s packetId=%s", long5, Integer.valueOf(short0));
                        return;
                    }

                    GameServer.addIncoming((short)short0, byteBuffer, udpConnection2);
                } else {
                    GameClient.instance.addIncoming((short)short0, byteBuffer);
                }
                break;
            default:
                DebugLog.Network.warn("Received unknown packet: %s", int0);
                if (GameServer.bServer) {
                    long long4 = this.peer.getGuidOfPacket();
                    UdpConnection udpConnection1 = this.connectionMap.get(long4);

                    try {
                        if (ServerOptions.instance.AntiCheatProtectionType10.getValue() && PacketValidator.checkUser(udpConnection1)) {
                            PacketValidator.doKickUser(udpConnection1, String.valueOf(int0), "Type10", null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
        }
    }

    private void removeConnection(int int0) {
        UdpConnection udpConnection = this.connectionArray[int0];
        if (udpConnection != null) {
            this.connectionArray[int0] = null;
            this.connectionMap.remove(udpConnection.getConnectedGUID());
            if (GameClient.bClient) {
                GameClient.instance.connectionLost();
            }

            if (GameServer.bServer) {
                GameServer.addDisconnect(udpConnection);
            }
        }
    }

    private UdpConnection addConnection(int int0, long long0) {
        UdpConnection udpConnection = new UdpConnection(this, long0, int0);
        this.connectionMap.put(long0, udpConnection);
        this.connectionArray[int0] = udpConnection;
        if (GameServer.bServer) {
            GameServer.addConnection(udpConnection);
        }

        return udpConnection;
    }

    public ByteBuffer Receive() {
        boolean boolean0 = false;

        do {
            boolean0 = this.peer.Receive(this.buf);
            if (boolean0) {
                return this.buf;
            }

            try {
                Thread.sleep(1L);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        } while (!this.bQuit && !boolean0);

        return this.buf;
    }

    public UdpConnection getActiveConnection(long connection) {
        return !this.connectionMap.containsKey(connection) ? null : this.connectionMap.get(connection);
    }

    public void Connect(String string0, int int0, String string1, boolean boolean0) {
        if (int0 == 0 && SteamUtils.isSteamModeEnabled()) {
            long long0 = 0L;

            try {
                long0 = SteamUtils.convertStringToSteamID(string0);
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
                LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
                return;
            }

            this.peer.ConnectToSteamServer(long0, this.hashServerPassword(string1), boolean0);
        } else {
            String string2;
            try {
                InetAddress inetAddress = InetAddress.getByName(string0);
                string2 = inetAddress.getHostAddress();
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
                LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_UnknownHost"));
                return;
            }

            this.peer.Connect(string2, int0, this.hashServerPassword(string1), boolean0);
        }
    }

    public void forceDisconnect(long connectedGUID, String message) {
        this.peer.disconnect(connectedGUID, message);
        this.removeConnection(connectedGUID);
    }

    private void removeConnection(long long0) {
        UdpConnection udpConnection = this.connectionMap.remove(long0);
        if (udpConnection != null) {
            this.removeConnection(udpConnection.index);
        }
    }

    public RakNetPeerInterface getPeer() {
        return this.peer;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public String getDescription() {
        return "connections=["
            + this.connections.size()
            + "/"
            + this.connectionMap.size()
            + "/"
            + Arrays.stream(this.connectionArray).filter(Objects::nonNull).count()
            + "/"
            + this.peer.GetConnectionsNumber()
            + "]";
    }
}
