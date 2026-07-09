// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.NotificationEmitter;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MovingObjectUpdateScheduler;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManagerData;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.ZNetStatistics;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.iso.IsoWorld;
import zombie.iso.WorldStreamer;
import zombie.popman.NetworkZombieManager;
import zombie.popman.NetworkZombieSimulator;
import zombie.util.StringUtils;

public class MPStatistics {
    private static final float MEM_USAGE_THRESHOLD = 0.95F;
    private static final long REQUEST_TIMEOUT = 10000L;
    private static final long STATISTICS_INTERVAL = 2000L;
    private static final long PING_INTERVAL = 1000L;
    private static final long PING_PERIOD = 10000L;
    private static final long PING_LIMIT_PERIOD = 60000L;
    private static final long PING_INTERVAL_COUNT = 60L;
    private static final long PING_LIMIT_COUNT = 20L;
    private static final long PING_LOG_COUNT = 120L;
    private static final long MAX_PING_TO_SUM = 1000L;
    private static final KahluaTable statsTable = LuaManager.platform.newTable();
    private static final KahluaTable statusTable = LuaManager.platform.newTable();
    private static final UpdateLimit ulRequestTimeout = new UpdateLimit(10000L);
    private static final UpdateLimit ulStatistics = new UpdateLimit(2000L);
    private static final UpdateLimit ulPing = new UpdateLimit(1000L);
    private static boolean serverStatisticsEnabled = false;
    private static int serverPlayers = 0;
    private static int clientPlayers = 0;
    private static int clientLastPing = -1;
    private static int clientAvgPing = -1;
    private static int clientMinPing = -1;
    private static String clientVOIPSource = "";
    private static String clientVOIPFreq = "";
    private static long clientVOIPRX = 0L;
    private static long clientVOIPTX = 0L;
    private static long serverVOIPRX = 0L;
    private static long serverVOIPTX = 0L;
    private static int serverWaitingRequests = 0;
    private static int clientSentRequests = 0;
    private static int requested1 = 0;
    private static int requested2 = 0;
    private static int pending1 = 0;
    private static int pending2 = 0;
    private static long serverCPUCores = 0L;
    private static long serverCPULoad = 0L;
    private static long serverMemMax = 0L;
    private static long serverMemFree = 0L;
    private static long serverMemTotal = 0L;
    private static long serverMemUsed = 0L;
    private static long serverRX = 0L;
    private static long serverTX = 0L;
    private static long serverResent = 0L;
    private static double serverLoss = 0.0;
    private static float serverFPS = 0.0F;
    private static long serverNetworkingUpdates = 0L;
    private static long serverNetworkingFPS = 0L;
    private static String serverRevision = "";
    private static long clientCPUCores = 0L;
    private static long clientCPULoad = 0L;
    private static long clientMemMax = 0L;
    private static long clientMemFree = 0L;
    private static long clientMemTotal = 0L;
    private static long clientMemUsed = 0L;
    private static long clientRX = 0L;
    private static long clientTX = 0L;
    private static long clientResent = 0L;
    private static double clientLoss = 0.0;
    private static float clientFPS = 0.0F;
    private static int serverStoredChunks = 0;
    private static int serverRelevantChunks = 0;
    private static int serverZombiesTotal = 0;
    private static int serverZombiesLoaded = 0;
    private static int serverZombiesSimulated = 0;
    private static int serverZombiesCulled = 0;
    private static int serverZombiesAuthorized = 0;
    private static int serverZombiesUnauthorized = 0;
    private static int serverZombiesReusable = 0;
    private static int serverZombiesUpdated = 0;
    private static int clientStoredChunks = 0;
    private static int clientRelevantChunks = 0;
    private static int clientZombiesTotal = 0;
    private static int clientZombiesLoaded = 0;
    private static int clientZombiesSimulated = 0;
    private static int clientZombiesCulled = 0;
    private static int clientZombiesAuthorized = 0;
    private static int clientZombiesUnauthorized = 0;
    private static int clientZombiesReusable = 0;
    private static int clientZombiesUpdated = 0;
    private static long zombieUpdates = 0L;
    private static long serverMinPing = 0L;
    private static long serverMaxPing = 0L;
    private static long serverAvgPing = 0L;
    private static long serverLastPing = 0L;
    private static long serverLossPing = 0L;
    private static long serverHandledPingPeriodStart = 0L;
    private static int serverHandledPingPacketIndex = 0;
    private static final ArrayList<Long> serverHandledPingHistory = new ArrayList<>();
    private static final HashSet<Long> serverHandledLossPingHistory = new HashSet<>();
    static long pingIntervalCount = 60L;
    static long pingLimitCount = 20L;
    static long maxPingToSum = 1000L;

    private static boolean isClientStatisticsEnabled() {
        boolean boolean0 = false;

        for (IsoPlayer player : IsoPlayer.players) {
            if (player != null && player.isShowMPInfos()) {
                boolean0 = true;
                break;
            }
        }

        return boolean0;
    }

    private static void getClientZombieStatistics() {
        int int0 = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - zombieUpdates, 1L);
        clientZombiesTotal = GameClient.IDToZombieMap.values().length;
        clientZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
        clientZombiesSimulated = clientZombiesUpdated / int0;
        clientZombiesAuthorized = NetworkZombieSimulator.getInstance().getAuthorizedZombieCount();
        clientZombiesUnauthorized = NetworkZombieSimulator.getInstance().getUnauthorizedZombieCount();
        clientZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
        clientZombiesCulled = 0;
        clientZombiesUpdated = 0;
        zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
        serverZombiesCulled = 0;
    }

    private static void getServerZombieStatistics() {
        int int0 = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - zombieUpdates, 1L);
        serverZombiesTotal = ServerMap.instance.ZombieMap.size();
        serverZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
        serverZombiesSimulated = serverZombiesUpdated / int0;
        serverZombiesAuthorized = 0;
        serverZombiesUnauthorized = NetworkZombieManager.getInstance().getUnauthorizedZombieCount();
        serverZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
        serverZombiesCulled = 0;
        serverZombiesUpdated = 0;
        zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
    }

    private static void getClientChunkStatistics() {
        try {
            WorldStreamer.instance.getStatistics();
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "Error getting chunk statistics", LogSeverity.Error);
        }
    }

    public static void countChunkRequests(int int0, int int1, int int2, int int3, int int4) {
        clientSentRequests = int0;
        requested1 = int1;
        requested2 = int2;
        pending1 = int3;
        pending2 = int4;
    }

    private static void resetStatistic() {
        if (GameClient.bClient) {
            GameClient.connection.netStatistics = null;
        } else if (GameServer.bServer) {
            for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                udpConnection.netStatistics = null;
            }
        }

        serverPlayers = 0;
        clientPlayers = 0;
        clientVOIPSource = "";
        clientVOIPFreq = "";
        clientVOIPRX = 0L;
        clientVOIPTX = 0L;
        serverVOIPRX = 0L;
        serverVOIPTX = 0L;
        serverCPUCores = 0L;
        serverCPULoad = 0L;
        serverRX = 0L;
        serverTX = 0L;
        serverResent = 0L;
        serverLoss = 0.0;
        serverFPS = 0.0F;
        serverNetworkingFPS = 0L;
        serverMemMax = 0L;
        serverMemFree = 0L;
        serverMemTotal = 0L;
        serverMemUsed = 0L;
        clientCPUCores = 0L;
        clientCPULoad = 0L;
        clientRX = 0L;
        clientTX = 0L;
        clientResent = 0L;
        clientLoss = 0.0;
        clientFPS = 0.0F;
        clientMemMax = 0L;
        clientMemFree = 0L;
        clientMemTotal = 0L;
        clientMemUsed = 0L;
        serverZombiesTotal = 0;
        serverZombiesLoaded = 0;
        serverZombiesSimulated = 0;
        serverZombiesCulled = 0;
        serverZombiesAuthorized = 0;
        serverZombiesUnauthorized = 0;
        serverZombiesReusable = 0;
        serverZombiesUpdated = 0;
        clientZombiesTotal = 0;
        clientZombiesLoaded = 0;
        clientZombiesSimulated = 0;
        clientZombiesCulled = 0;
        clientZombiesAuthorized = 0;
        clientZombiesUnauthorized = 0;
        clientZombiesReusable = 0;
        clientZombiesUpdated = 0;
        serverWaitingRequests = 0;
        clientSentRequests = 0;
        requested1 = 0;
        requested2 = 0;
        pending1 = 0;
        pending2 = 0;
    }

    private static void getClientStatistics() {
        try {
            clientVOIPRX = 0L;
            clientVOIPTX = 0L;
            clientRX = 0L;
            clientTX = 0L;
            clientResent = 0L;
            clientLoss = 0.0;
            ZNetStatistics zNetStatistics = GameClient.connection.getStatistics();
            if (zNetStatistics != null) {
                clientRX = zNetStatistics.lastActualBytesReceived / 1000L;
                clientTX = zNetStatistics.lastActualBytesSent / 1000L;
                clientResent = zNetStatistics.lastUserMessageBytesResent / 1000L;
                clientLoss = zNetStatistics.packetlossLastSecond / 1000.0;
            }

            long[] longs = new long[]{-1L, -1L};
            if (RakVoice.GetChannelStatistics(GameClient.connection.getConnectedGUID(), longs)) {
                clientVOIPRX = longs[0] / 2000L;
                clientVOIPTX = longs[1] / 2000L;
            }

            clientFPS = 60.0F / GameTime.instance.FPSMultiplier;
            clientCPUCores = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            clientCPULoad = (long)(((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100.0);
            clientMemMax = Runtime.getRuntime().maxMemory() / 1000L / 1000L;
            clientMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
            clientMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
            clientMemUsed = clientMemTotal - clientMemFree;
            clientPlayers = 0;

            for (IsoPlayer player : IsoPlayer.players) {
                if (player != null) {
                    clientPlayers++;
                }
            }
        } catch (Exception exception) {
        }
    }

    private static void getServerStatistics() {
        try {
            serverVOIPRX = 0L;
            serverVOIPTX = 0L;
            serverRX = 0L;
            serverTX = 0L;
            serverResent = 0L;
            serverLoss = 0.0;
            long[] longs = new long[]{-1L, -1L};

            for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                ZNetStatistics zNetStatistics = udpConnection.getStatistics();
                if (zNetStatistics != null) {
                    serverRX = serverRX + udpConnection.netStatistics.lastActualBytesReceived;
                    serverTX = serverTX + udpConnection.netStatistics.lastActualBytesSent;
                    serverResent = serverResent + udpConnection.netStatistics.lastUserMessageBytesResent;
                    serverLoss = serverLoss + udpConnection.netStatistics.packetlossLastSecond;
                }

                if (RakVoice.GetChannelStatistics(udpConnection.getConnectedGUID(), longs)) {
                    serverVOIPRX = serverVOIPRX + longs[0];
                    serverVOIPTX = serverVOIPTX + longs[1];
                }
            }

            serverRX /= 1000L;
            serverTX /= 1000L;
            serverResent /= 1000L;
            serverLoss /= 1000.0;
            serverVOIPRX /= 2000L;
            serverVOIPTX /= 2000L;
            serverFPS = 60.0F / GameTime.instance.FPSMultiplier;
            serverCPUCores = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            serverCPULoad = (long)(((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100.0);
            serverNetworkingFPS = 1000L * serverNetworkingUpdates / 2000L;
            serverNetworkingUpdates = 0L;
            serverMemMax = Runtime.getRuntime().maxMemory() / 1000L / 1000L;
            serverMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
            serverMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
            serverMemUsed = serverMemTotal - serverMemFree;
            serverPlayers = GameServer.IDToPlayerMap.size();
        } catch (Exception exception) {
        }
    }

    private static void resetPingCounters() {
        clientLastPing = -1;
        clientAvgPing = -1;
        clientMinPing = -1;
    }

    private static void getPing(UdpConnection udpConnection) {
        try {
            if (udpConnection != null) {
                clientLastPing = udpConnection.getLastPing();
                clientAvgPing = udpConnection.getAveragePing();
                clientMinPing = udpConnection.getLowestPing();
            }
        } catch (Exception exception) {
        }
    }

    static long checkLatest(UdpConnection udpConnection, long long1) {
        if (udpConnection.pingHistory.size() >= pingIntervalCount) {
            long long0 = udpConnection.pingHistory.stream().limit(pingIntervalCount).filter(long1x -> long1x > long1).count();
            if (long0 >= pingLimitCount) {
                return (long)Math.ceil(
                    (float)udpConnection.pingHistory.stream().limit(pingIntervalCount).mapToLong(long0x -> Math.min(maxPingToSum, long0x)).sum()
                        / (float)pingIntervalCount
                );
            }
        }

        return 0L;
    }

    private static void limitPing() {
        int int0 = ServerOptions.instance.PingLimit.getValue();

        for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
            serverAvgPing = udpConnection.getAveragePing();
            serverLastPing = udpConnection.getLastPing();
            udpConnection.pingHistory.addFirst(serverLastPing);
            long long0 = checkLatest(udpConnection, int0);
            if (doKick(udpConnection, long0)) {
                GameServer.kick(udpConnection, "UI_Policy_Kick", "UI_OnConnectFailed_Ping");
                udpConnection.forceDisconnect("kick-ping-limit");
                GameServer.addDisconnect(udpConnection);
                DebugLog.Multiplayer.warn("Kick: player=\"%s\" type=\"%s\"", udpConnection.username, "UI_OnConnectFailed_Ping");
                DebugLog.Multiplayer.debugln("Ping: limit=%d/%d average-%d=%d", int0, pingLimitCount, pingIntervalCount, long0);
                DebugLog.Multiplayer
                    .debugln("Ping: last-%d: %s", 120L, udpConnection.pingHistory.stream().map(Object::toString).collect(Collectors.joining(", ")));
            }

            if (udpConnection.pingHistory.size() > 120L) {
                udpConnection.pingHistory.removeLast();
            }
        }
    }

    public static boolean doKickWhileLoading(UdpConnection udpConnection, long long0) {
        int int0 = ServerOptions.instance.PingLimit.getValue();
        return int0 > ServerOptions.instance.PingLimit.getMin()
            && long0 > int0
            && !udpConnection.preferredInQueue
            && !PlayerType.isPrivileged(udpConnection.accessLevel);
    }

    public static boolean doKick(UdpConnection udpConnection, long long0) {
        return doKickWhileLoading(udpConnection, long0) && udpConnection.isFullyConnected() && udpConnection.isConnectionGraceIntervalTimeout();
    }

    private static void resetServerHandledPingCounters() {
        serverMinPing = 0L;
        serverMaxPing = 0L;
        serverAvgPing = 0L;
        serverLastPing = 0L;
        serverLossPing = 0L;
        serverHandledPingPeriodStart = 0L;
        serverHandledPingPacketIndex = 0;
        serverHandledPingHistory.clear();
        serverHandledLossPingHistory.clear();
    }

    private static void getServerHandledPing() {
        long long0 = System.currentTimeMillis();
        if (serverHandledPingPacketIndex == 10L) {
            serverMinPing = serverHandledPingHistory.stream().mapToLong(long0x -> long0x).min().orElse(0L);
            serverMaxPing = serverHandledPingHistory.stream().mapToLong(long0x -> long0x).max().orElse(0L);
            serverAvgPing = (long)serverHandledPingHistory.stream().mapToLong(long0x -> long0x).average().orElse(0.0);
            serverHandledPingHistory.clear();
            serverHandledPingPacketIndex = 0;
            int int0 = serverHandledLossPingHistory.size();
            serverHandledLossPingHistory.removeIf(long1 -> long0 > long1 + 10000L);
            serverLossPing = serverLossPing + (int0 - serverHandledLossPingHistory.size());
            serverHandledPingPeriodStart = long0;
        }

        GameClient.sendServerPing(long0);
        if (serverHandledLossPingHistory.size() > 1000) {
            serverHandledLossPingHistory.clear();
        }

        serverHandledLossPingHistory.add(long0);
        serverHandledPingPacketIndex++;
    }

    public static void setVOIPSource(VoiceManagerData.VoiceDataSource voiceDataSource, int int0) {
        clientVOIPSource = VoiceManagerData.VoiceDataSource.Unknown.equals(voiceDataSource) ? "" : voiceDataSource.name();
        clientVOIPFreq = int0 == 0 ? "" : String.valueOf(int0 / 1000.0F);
    }

    public static void countServerNetworkingFPS() {
        serverNetworkingUpdates++;
    }

    public static void increaseStoredChunk() {
        if (GameClient.bClient) {
            clientStoredChunks++;
        } else if (GameServer.bServer) {
            serverStoredChunks++;
        }

        decreaseRelevantChunk();
    }

    public static void decreaseStoredChunk() {
        if (GameClient.bClient) {
            clientStoredChunks--;
        } else if (GameServer.bServer) {
            serverStoredChunks--;
        }

        increaseRelevantChunk();
    }

    public static void increaseRelevantChunk() {
        if (GameClient.bClient) {
            clientRelevantChunks++;
        } else if (GameServer.bServer) {
            serverRelevantChunks++;
        }
    }

    public static void decreaseRelevantChunk() {
        if (GameClient.bClient) {
            clientRelevantChunks--;
        } else if (GameServer.bServer) {
            serverRelevantChunks--;
        }
    }

    public static void Init() {
        if (GameServer.bServer || GameClient.bClient) {
            try {
                for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
                    if (MemoryType.HEAP.equals(memoryPoolMXBean.getType()) && memoryPoolMXBean.isUsageThresholdSupported()) {
                        long long0 = memoryPoolMXBean.getCollectionUsageThreshold();
                        String string = System.getProperty("zomboid.thresholdm");
                        if (!StringUtils.isNullOrEmpty(string)) {
                            long0 = Long.parseLong(string) * 1000000L;
                        }

                        if (long0 == 0L) {
                            long0 = (long)((float)Runtime.getRuntime().maxMemory() * 0.95F);
                            memoryPoolMXBean.setUsageThreshold(long0);
                        }

                        if (long0 > 0L) {
                            ((NotificationEmitter)ManagementFactory.getMemoryMXBean())
                                .addNotificationListener(
                                    (var1x, var2x) -> DebugLog.Multiplayer
                                        .warn(
                                            "[%s] %s (%d) free=%s",
                                            MPStatistics.class.getSimpleName(),
                                            "java.management.memory.threshold.exceeded",
                                            memoryPoolMXBean.getUsageThresholdCount(),
                                            NumberFormat.getNumberInstance().format(Runtime.getRuntime().freeMemory())
                                        ),
                                    notification -> "java.management.memory.threshold.exceeded".equals(notification.getType()),
                                    null
                                );
                        }

                        DebugLog.log(
                            DebugType.Multiplayer,
                            String.format(
                                "[%s] mem usage notification threshold=%s", MPStatistics.class.getSimpleName(), NumberFormat.getNumberInstance().format(long0)
                            )
                        );
                        break;
                    }
                }
            } catch (Exception exception) {
                DebugLog.Multiplayer.printException(exception, String.format("[%s] init error", MPStatistics.class.getSimpleName()), LogSeverity.Error);
            }

            Reset();
        }
    }

    public static void Reset() {
        resetPingCounters();
        resetServerHandledPingCounters();
        resetStatistic();
    }

    public static void Update() {
        if (GameClient.bClient) {
            if (ulPing.Check()) {
                if (!isClientStatisticsEnabled() && !NetworkAIParams.isShowPingInfo()) {
                    resetPingCounters();
                    resetServerHandledPingCounters();
                } else {
                    getPing(GameClient.connection);
                    if (isClientStatisticsEnabled()) {
                        getServerHandledPing();
                    } else {
                        resetServerHandledPingCounters();
                    }
                }
            }

            if (isClientStatisticsEnabled()) {
                if (ulStatistics.Check()) {
                    getClientStatistics();
                    getClientZombieStatistics();
                    getClientChunkStatistics();
                }
            } else {
                resetStatistic();
            }
        } else if (GameServer.bServer) {
            if (ulPing.Check()) {
                limitPing();
            }

            if (ulRequestTimeout.Check()) {
                serverStatisticsEnabled = false;
            }

            if (serverStatisticsEnabled) {
                if (ulStatistics.Check()) {
                    getServerStatistics();
                    getServerZombieStatistics();
                }
            } else {
                resetStatistic();
            }
        }
    }

    public static void requested() {
        serverStatisticsEnabled = true;
        ulRequestTimeout.Reset(10000L);
    }

    public static void clientZombieCulled() {
        clientZombiesCulled++;
    }

    public static void serverZombieCulled() {
        serverZombiesCulled++;
    }

    public static void clientZombieUpdated() {
        clientZombiesUpdated++;
    }

    public static void serverZombieUpdated() {
        serverZombiesUpdated++;
    }

    public static void write(UdpConnection udpConnection, ByteBuffer byteBuffer) {
        byteBuffer.putLong(serverMemMax);
        byteBuffer.putLong(serverMemFree);
        byteBuffer.putLong(serverMemTotal);
        byteBuffer.putLong(serverMemUsed);
        byteBuffer.putLong(serverCPUCores);
        byteBuffer.putLong(serverCPULoad);
        byteBuffer.putLong(serverVOIPRX);
        byteBuffer.putLong(serverVOIPTX);
        byteBuffer.putLong(serverRX);
        byteBuffer.putLong(serverTX);
        byteBuffer.putLong(serverResent);
        byteBuffer.putDouble(serverLoss);
        byteBuffer.putFloat(serverFPS);
        byteBuffer.putLong(serverNetworkingFPS);
        byteBuffer.putInt(serverStoredChunks);
        byteBuffer.putInt(serverRelevantChunks);
        byteBuffer.putInt(serverZombiesTotal);
        byteBuffer.putInt(serverZombiesLoaded);
        byteBuffer.putInt(serverZombiesSimulated);
        byteBuffer.putInt(serverZombiesCulled);
        byteBuffer.putInt(NetworkZombieManager.getInstance().getAuthorizedZombieCount(udpConnection));
        byteBuffer.putInt(serverZombiesUnauthorized);
        byteBuffer.putInt(serverZombiesReusable);
        byteBuffer.putInt(udpConnection.playerDownloadServer.getWaitingRequests());
        byteBuffer.putInt(serverPlayers);
        GameWindow.WriteString(byteBuffer, "");
    }

    public static void parse(ByteBuffer byteBuffer) {
        long long0 = System.currentTimeMillis();
        long long1 = byteBuffer.getLong();
        serverMemMax = byteBuffer.getLong();
        serverMemFree = byteBuffer.getLong();
        serverMemTotal = byteBuffer.getLong();
        serverMemUsed = byteBuffer.getLong();
        serverCPUCores = byteBuffer.getLong();
        serverCPULoad = byteBuffer.getLong();
        serverVOIPRX = byteBuffer.getLong();
        serverVOIPTX = byteBuffer.getLong();
        serverRX = byteBuffer.getLong();
        serverTX = byteBuffer.getLong();
        serverResent = byteBuffer.getLong();
        serverLoss = byteBuffer.getDouble();
        serverFPS = byteBuffer.getFloat();
        serverNetworkingFPS = byteBuffer.getLong();
        serverStoredChunks = byteBuffer.getInt();
        serverRelevantChunks = byteBuffer.getInt();
        serverZombiesTotal = byteBuffer.getInt();
        serverZombiesLoaded = byteBuffer.getInt();
        serverZombiesSimulated = byteBuffer.getInt();
        serverZombiesCulled = serverZombiesCulled + byteBuffer.getInt();
        serverZombiesAuthorized = byteBuffer.getInt();
        serverZombiesUnauthorized = byteBuffer.getInt();
        serverZombiesReusable = byteBuffer.getInt();
        serverWaitingRequests = byteBuffer.getInt();
        serverPlayers = byteBuffer.getInt();
        serverRevision = GameWindow.ReadString(byteBuffer);
        serverHandledLossPingHistory.remove(long1);
        if (long1 >= serverHandledPingPeriodStart) {
            serverLastPing = long0 - long1;
            serverHandledPingHistory.add(serverLastPing);
        }
    }

    public static KahluaTable getLuaStatus() {
        statusTable.wipe();
        if (GameClient.bClient) {
            statusTable.rawset("serverTime", NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toSeconds(GameTime.getServerTime())));
            statusTable.rawset("svnRevision", "");
            statusTable.rawset("buildDate", "");
            statusTable.rawset("buildTime", "");
            statusTable.rawset("version", Core.getInstance().getVersion());
            statusTable.rawset("lastPing", String.valueOf(clientLastPing));
            statusTable.rawset("avgPing", String.valueOf(clientAvgPing));
            statusTable.rawset("minPing", String.valueOf(clientMinPing));
        }

        return statusTable;
    }

    public static KahluaTable getLuaStatistics() {
        statsTable.wipe();
        if (GameClient.bClient) {
            statsTable.rawset("clientTime", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
            statsTable.rawset("serverTime", NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toSeconds(GameTime.getServerTime())));
            statsTable.rawset("clientRevision", String.valueOf(""));
            statsTable.rawset("serverRevision", String.valueOf(serverRevision));
            statsTable.rawset("clientPlayers", String.valueOf(clientPlayers));
            statsTable.rawset("serverPlayers", String.valueOf(serverPlayers));
            statsTable.rawset("clientVOIPSource", String.valueOf(clientVOIPSource));
            statsTable.rawset("clientVOIPFreq", String.valueOf(clientVOIPFreq));
            statsTable.rawset("clientVOIPRX", String.valueOf(clientVOIPRX));
            statsTable.rawset("clientVOIPTX", String.valueOf(clientVOIPTX));
            statsTable.rawset("clientRX", String.valueOf(clientRX));
            statsTable.rawset("clientTX", String.valueOf(clientTX));
            statsTable.rawset("clientResent", String.valueOf(clientResent));
            statsTable.rawset("clientLoss", String.valueOf((int)clientLoss));
            statsTable.rawset("serverVOIPRX", String.valueOf(serverVOIPRX));
            statsTable.rawset("serverVOIPTX", String.valueOf(serverVOIPTX));
            statsTable.rawset("serverRX", String.valueOf(serverRX));
            statsTable.rawset("serverTX", String.valueOf(serverTX));
            statsTable.rawset("serverResent", String.valueOf(serverResent));
            statsTable.rawset("serverLoss", String.valueOf((int)serverLoss));
            statsTable.rawset("clientLastPing", String.valueOf(clientLastPing));
            statsTable.rawset("clientAvgPing", String.valueOf(clientAvgPing));
            statsTable.rawset("clientMinPing", String.valueOf(clientMinPing));
            statsTable.rawset("serverPingLast", String.valueOf(serverLastPing));
            statsTable.rawset("serverPingMin", String.valueOf(serverMinPing));
            statsTable.rawset("serverPingAvg", String.valueOf(serverAvgPing));
            statsTable.rawset("serverPingMax", String.valueOf(serverMaxPing));
            statsTable.rawset("serverPingLoss", String.valueOf(serverLossPing));
            statsTable.rawset("clientCPUCores", String.valueOf(clientCPUCores));
            statsTable.rawset("clientCPULoad", String.valueOf(clientCPULoad));
            statsTable.rawset("clientMemMax", String.valueOf(clientMemMax));
            statsTable.rawset("clientMemFree", String.valueOf(clientMemFree));
            statsTable.rawset("clientMemTotal", String.valueOf(clientMemTotal));
            statsTable.rawset("clientMemUsed", String.valueOf(clientMemUsed));
            statsTable.rawset("serverCPUCores", String.valueOf(serverCPUCores));
            statsTable.rawset("serverCPULoad", String.valueOf(serverCPULoad));
            statsTable.rawset("serverMemMax", String.valueOf(serverMemMax));
            statsTable.rawset("serverMemFree", String.valueOf(serverMemFree));
            statsTable.rawset("serverMemTotal", String.valueOf(serverMemTotal));
            statsTable.rawset("serverMemUsed", String.valueOf(serverMemUsed));
            statsTable.rawset("serverNetworkingFPS", String.valueOf((int)serverNetworkingFPS));
            statsTable.rawset("serverFPS", String.valueOf((int)serverFPS));
            statsTable.rawset("clientFPS", String.valueOf((int)clientFPS));
            statsTable.rawset("serverStoredChunks", String.valueOf(serverStoredChunks));
            statsTable.rawset("serverRelevantChunks", String.valueOf(serverRelevantChunks));
            statsTable.rawset("serverZombiesTotal", String.valueOf(serverZombiesTotal));
            statsTable.rawset("serverZombiesLoaded", String.valueOf(serverZombiesLoaded));
            statsTable.rawset("serverZombiesSimulated", String.valueOf(serverZombiesSimulated));
            statsTable.rawset("serverZombiesCulled", String.valueOf(serverZombiesCulled));
            statsTable.rawset("serverZombiesAuthorized", String.valueOf(serverZombiesAuthorized));
            statsTable.rawset("serverZombiesUnauthorized", String.valueOf(serverZombiesUnauthorized));
            statsTable.rawset("serverZombiesReusable", String.valueOf(serverZombiesReusable));
            statsTable.rawset("clientStoredChunks", String.valueOf(clientStoredChunks));
            statsTable.rawset("clientRelevantChunks", String.valueOf(clientRelevantChunks));
            statsTable.rawset("clientZombiesTotal", String.valueOf(clientZombiesTotal));
            statsTable.rawset("clientZombiesLoaded", String.valueOf(clientZombiesLoaded));
            statsTable.rawset("clientZombiesSimulated", String.valueOf(clientZombiesSimulated));
            statsTable.rawset("clientZombiesCulled", String.valueOf(clientZombiesCulled));
            statsTable.rawset("clientZombiesAuthorized", String.valueOf(clientZombiesAuthorized));
            statsTable.rawset("clientZombiesUnauthorized", String.valueOf(clientZombiesUnauthorized));
            statsTable.rawset("clientZombiesReusable", String.valueOf(clientZombiesReusable));
            statsTable.rawset("serverWaitingRequests", String.valueOf(serverWaitingRequests));
            statsTable.rawset("clientSentRequests", String.valueOf(clientSentRequests));
            statsTable.rawset("requested1", String.valueOf(requested1));
            statsTable.rawset("requested2", String.valueOf(requested2));
            statsTable.rawset("pending1", String.valueOf(pending1));
            statsTable.rawset("pending2", String.valueOf(pending2));
        }

        return statsTable;
    }
}
