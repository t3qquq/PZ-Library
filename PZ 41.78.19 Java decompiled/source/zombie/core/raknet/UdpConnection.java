// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import gnu.trove.list.array.TShortArrayList;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.SystemDisabler;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.utils.UpdateTimer;
import zombie.core.znet.ZNetStatistics;
import zombie.iso.IsoUtils;
import zombie.iso.Vector3;
import zombie.network.ClientServerMap;
import zombie.network.ConnectionManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.PacketValidator;
import zombie.network.PlayerDownloadServer;

public class UdpConnection {
    Lock bufferLock = new ReentrantLock();
    private ByteBuffer bb = ByteBuffer.allocate(1000000);
    private ByteBufferWriter bbw = new ByteBufferWriter(this.bb);
    Lock bufferLockPing = new ReentrantLock();
    private ByteBuffer bbPing = ByteBuffer.allocate(50);
    private ByteBufferWriter bbwPing = new ByteBufferWriter(this.bbPing);
    long connectedGUID = 0L;
    UdpEngine engine;
    public int index;
    public boolean allChatMuted = false;
    public String username;
    public String[] usernames = new String[4];
    public byte ReleventRange;
    public byte accessLevel = 1;
    public long lastUnauthorizedPacket = 0L;
    public String ip;
    public boolean preferredInQueue;
    public boolean wasInLoadingQueue;
    public String password;
    public boolean ping = false;
    public Vector3[] ReleventPos = new Vector3[4];
    public short[] playerIDs = new short[4];
    public IsoPlayer[] players = new IsoPlayer[4];
    public Vector3[] connectArea = new Vector3[4];
    public int ChunkGridWidth;
    public ClientServerMap[] loadedCells = new ClientServerMap[4];
    public PlayerDownloadServer playerDownloadServer;
    public UdpConnection.ChecksumState checksumState = UdpConnection.ChecksumState.Init;
    public long checksumTime;
    public boolean awaitingCoopApprove = false;
    public long steamID;
    public long ownerID;
    public String idStr;
    public boolean isCoopHost;
    public int maxPlayers;
    public final TShortArrayList chunkObjectState = new TShortArrayList();
    public UdpConnection.MPClientStatistic statistic = new UdpConnection.MPClientStatistic();
    public ZNetStatistics netStatistics;
    public final Deque<Long> pingHistory = new ArrayDeque<>();
    public final PacketValidator validator = new PacketValidator(this);
    private static final long CONNECTION_ATTEMPT_TIMEOUT = 5000L;
    public static final long CONNECTION_GRACE_INTERVAL = 60000L;
    public long connectionTimestamp;
    public UpdateTimer timerSendZombie = new UpdateTimer();
    private boolean bFullyConnected = false;
    public boolean isNeighborPlayer = false;

    public UdpConnection(UdpEngine _engine, long _connectedGUID, int _index) {
        this.engine = _engine;
        this.connectedGUID = _connectedGUID;
        this.index = _index;
        this.ReleventPos[0] = new Vector3();

        for (int int0 = 0; int0 < 4; int0++) {
            this.playerIDs[int0] = -1;
        }

        this.connectionTimestamp = System.currentTimeMillis();
        this.wasInLoadingQueue = false;
    }

    public RakNetPeerInterface getPeer() {
        return this.engine.peer;
    }

    public long getConnectedGUID() {
        return this.connectedGUID;
    }

    public String getServerIP() {
        return this.engine.getServerIP();
    }

    public ByteBufferWriter startPacket() {
        this.bufferLock.lock();
        this.bb.clear();
        return this.bbw;
    }

    public ByteBufferWriter startPingPacket() {
        this.bufferLockPing.lock();
        this.bbPing.clear();
        return this.bbwPing;
    }

    public boolean RelevantTo(float x, float y) {
        for (int int0 = 0; int0 < 4; int0++) {
            if (this.connectArea[int0] != null) {
                int int1 = (int)this.connectArea[int0].z;
                int int2 = (int)(this.connectArea[int0].x - int1 / 2) * 10;
                int int3 = (int)(this.connectArea[int0].y - int1 / 2) * 10;
                int int4 = int2 + int1 * 10;
                int int5 = int3 + int1 * 10;
                if (x >= int2 && x < int4 && y >= int3 && y < int5) {
                    return true;
                }
            }

            if (this.ReleventPos[int0] != null
                && Math.abs(this.ReleventPos[int0].x - x) <= this.ReleventRange * 10
                && Math.abs(this.ReleventPos[int0].y - y) <= this.ReleventRange * 10) {
                return true;
            }
        }

        return false;
    }

    public float getRelevantAndDistance(float x, float y, float z) {
        for (int int0 = 0; int0 < 4; int0++) {
            if (this.ReleventPos[int0] != null
                && Math.abs(this.ReleventPos[int0].x - x) <= this.ReleventRange * 10
                && Math.abs(this.ReleventPos[int0].y - y) <= this.ReleventRange * 10) {
                return IsoUtils.DistanceTo(this.ReleventPos[int0].x, this.ReleventPos[int0].y, x, y);
            }
        }

        return Float.POSITIVE_INFINITY;
    }

    public boolean RelevantToPlayerIndex(int n, float x, float y) {
        if (this.connectArea[n] != null) {
            int int0 = (int)this.connectArea[n].z;
            int int1 = (int)(this.connectArea[n].x - int0 / 2) * 10;
            int int2 = (int)(this.connectArea[n].y - int0 / 2) * 10;
            int int3 = int1 + int0 * 10;
            int int4 = int2 + int0 * 10;
            if (x >= int1 && x < int3 && y >= int2 && y < int4) {
                return true;
            }
        }

        return this.ReleventPos[n] != null
            && Math.abs(this.ReleventPos[n].x - x) <= this.ReleventRange * 10
            && Math.abs(this.ReleventPos[n].y - y) <= this.ReleventRange * 10;
    }

    public boolean RelevantTo(float x, float y, float radius) {
        for (int int0 = 0; int0 < 4; int0++) {
            if (this.connectArea[int0] != null) {
                int int1 = (int)this.connectArea[int0].z;
                int int2 = (int)(this.connectArea[int0].x - int1 / 2) * 10;
                int int3 = (int)(this.connectArea[int0].y - int1 / 2) * 10;
                int int4 = int2 + int1 * 10;
                int int5 = int3 + int1 * 10;
                if (x >= int2 && x < int4 && y >= int3 && y < int5) {
                    return true;
                }
            }

            if (this.ReleventPos[int0] != null && Math.abs(this.ReleventPos[int0].x - x) <= radius && Math.abs(this.ReleventPos[int0].y - y) <= radius) {
                return true;
            }
        }

        return false;
    }

    public void cancelPacket() {
        this.bufferLock.unlock();
    }

    public int getBufferPosition() {
        return this.bb.position();
    }

    public void endPacket(int priority, int reliability, byte ordering) {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bb.flip();
        int int1 = this.engine.peer.Send(this.bb, priority, reliability, ordering, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPacket() {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bb.flip();
        int int1 = this.engine.peer.Send(this.bb, 1, 3, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPacketImmediate() {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bb.flip();
        int int1 = this.engine.peer.Send(this.bb, 0, 3, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPacketUnordered() {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bb.flip();
        int int1 = this.engine.peer.Send(this.bb, 2, 2, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPacketUnreliable() {
        this.bb.flip();
        int int0 = this.engine.peer.Send(this.bb, 2, 1, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPacketSuperHighUnreliable() {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bb.flip();
        int int1 = this.engine.peer.Send(this.bb, 0, 1, (byte)0, this.connectedGUID, false);
        this.bufferLock.unlock();
    }

    public void endPingPacket() {
        if (GameServer.bServer) {
            int int0 = this.bb.position();
            this.bb.position(1);
            MPStatistic.getInstance().addOutcomePacket(this.bb.getShort(), int0);
            this.bb.position(int0);
        }

        this.bbPing.flip();
        this.engine.peer.Send(this.bbPing, 0, 1, (byte)0, this.connectedGUID, false);
        this.bufferLockPing.unlock();
    }

    public InetSocketAddress getInetSocketAddress() {
        String string = this.engine.peer.getIPFromGUID(this.connectedGUID);
        if ("UNASSIGNED_SYSTEM_ADDRESS".equals(string)) {
            return null;
        } else {
            string = string.replace("|", "\u00c2\u00a3");
            String[] strings = string.split("\u00c2\u00a3");
            return new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
        }
    }

    public void forceDisconnect(String description) {
        if (!GameServer.bServer) {
            GameClient.instance.disconnect();
        }

        this.engine.forceDisconnect(this.getConnectedGUID(), description);
        ConnectionManager.log("force-disconnect", description, this);
    }

    public void setFullyConnected() {
        this.validator.reset();
        this.bFullyConnected = true;
        this.setConnectionTimestamp();
        ConnectionManager.log("fully-connected", "", this);
    }

    public void setConnectionTimestamp() {
        this.connectionTimestamp = System.currentTimeMillis();
    }

    public boolean isConnectionAttemptTimeout() {
        return System.currentTimeMillis() > this.connectionTimestamp + 5000L;
    }

    public boolean isConnectionGraceIntervalTimeout() {
        return System.currentTimeMillis() > this.connectionTimestamp + 60000L || Core.bDebug && SystemDisabler.doKickInDebug;
    }

    public boolean isFullyConnected() {
        return this.bFullyConnected;
    }

    public void calcCountPlayersInRelevantPosition() {
        if (this.isFullyConnected()) {
            boolean boolean0 = false;

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection1 = GameServer.udpEngine.connections.get(int0);
                if (udpConnection1.isFullyConnected() && udpConnection1 != this) {
                    for (int int1 = 0; int1 < udpConnection1.players.length; int1++) {
                        IsoPlayer player = udpConnection1.players[int1];
                        if (player != null && this.RelevantTo(player.x, player.y, 120.0F)) {
                            boolean0 = true;
                        }
                    }

                    if (boolean0) {
                        break;
                    }
                }
            }

            this.isNeighborPlayer = boolean0;
        }
    }

    public ZNetStatistics getStatistics() {
        try {
            this.netStatistics = this.engine.peer.GetNetStatistics(this.connectedGUID);
        } catch (Exception exception) {
            this.netStatistics = null;
        } finally {
            return this.netStatistics;
        }
    }

    public int getAveragePing() {
        return this.engine.peer.GetAveragePing(this.connectedGUID);
    }

    public int getLastPing() {
        return this.engine.peer.GetLastPing(this.connectedGUID);
    }

    public int getLowestPing() {
        return this.engine.peer.GetLowestPing(this.connectedGUID);
    }

    public int getMTUSize() {
        return this.engine.peer.GetMTUSize(this.connectedGUID);
    }

    public UdpConnection.ConnectionType getConnectionType() {
        return UdpConnection.ConnectionType.values()[this.engine.peer.GetConnectionType(this.connectedGUID)];
    }

    @Override
    public String toString() {
        return GameClient.bClient
            ? String.format(
                "guid=%s ip=%s steam-id=%s access=\"%s\" username=\"%s\" connection-type=\"%s\"",
                this.connectedGUID,
                this.ip == null ? GameClient.ip : this.ip,
                this.steamID == 0L ? GameClient.steamID : this.steamID,
                PlayerType.toString(this.accessLevel),
                this.username == null ? GameClient.username : this.username,
                this.getConnectionType().name()
            )
            : String.format(
                "guid=%s ip=%s steam-id=%s access=%s username=\"%s\" connection-type=\"%s\"",
                this.connectedGUID,
                this.ip,
                this.steamID,
                PlayerType.toString(this.accessLevel),
                this.username,
                this.getConnectionType().name()
            );
    }

    public boolean havePlayer(IsoPlayer p) {
        if (p == null) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.players.length; int0++) {
                if (this.players[int0] == p) {
                    return true;
                }
            }

            return false;
        }
    }

    public static enum ChecksumState {
        Init,
        Different,
        Done;
    }

    public static enum ConnectionType {
        Disconnected,
        UDPRakNet,
        Steam;
    }

    public class MPClientStatistic {
        public byte enable = 0;
        public int diff = 0;
        public float pingAVG = 0.0F;
        public int zombiesCount = 0;
        public int zombiesLocalOwnership = 0;
        public float zombiesDesyncAVG = 0.0F;
        public float zombiesDesyncMax = 0.0F;
        public int zombiesTeleports = 0;
        public int remotePlayersCount = 0;
        public float remotePlayersDesyncAVG = 0.0F;
        public float remotePlayersDesyncMax = 0.0F;
        public int remotePlayersTeleports = 0;
        public float FPS = 0.0F;
        public float FPSMin = 0.0F;
        public float FPSAvg = 0.0F;
        public float FPSMax = 0.0F;
        public short[] FPSHistogramm = new short[32];

        public void parse(ByteBuffer bb) {
            long long0 = bb.getLong();
            long long1 = System.currentTimeMillis();
            this.diff = (int)(long1 - long0);
            this.pingAVG = this.pingAVG + (this.diff * 0.5F - this.pingAVG) * 0.1F;
            this.zombiesCount = bb.getInt();
            this.zombiesLocalOwnership = bb.getInt();
            this.zombiesDesyncAVG = bb.getFloat();
            this.zombiesDesyncMax = bb.getFloat();
            this.zombiesTeleports = bb.getInt();
            this.remotePlayersCount = bb.getInt();
            this.remotePlayersDesyncAVG = bb.getFloat();
            this.remotePlayersDesyncMax = bb.getFloat();
            this.remotePlayersTeleports = bb.getInt();
            this.FPS = bb.getFloat();
            this.FPSMin = bb.getFloat();
            this.FPSAvg = bb.getFloat();
            this.FPSMax = bb.getFloat();

            for (int int0 = 0; int0 < 32; int0++) {
                this.FPSHistogramm[int0] = bb.getShort();
            }
        }
    }
}
