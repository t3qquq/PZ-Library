// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;

public class MPStatistic {
    public static MPStatistic instance;
    private static boolean doPrintStatistic = false;
    private static boolean doCSVStatistic = false;
    private static int Period = 0;
    public MPStatistic.TasksStatistic LoaderThreadTasks = new MPStatistic.TasksStatistic();
    public MPStatistic.TasksStatistic RecalcThreadTasks = new MPStatistic.TasksStatistic();
    public MPStatistic.SaveTasksStatistic SaveTasks = new MPStatistic.SaveTasksStatistic();
    public MPStatistic.ServerCellStatistic ServerMapToLoad = new MPStatistic.ServerCellStatistic();
    public MPStatistic.ServerCellStatistic ServerMapLoadedCells = new MPStatistic.ServerCellStatistic();
    public MPStatistic.ServerCellStatistic ServerMapLoaded2 = new MPStatistic.ServerCellStatistic();
    private int countServerChunkThreadSaveNow = 0;
    public MPStatistic.MainThreadStatistic Main = new MPStatistic.MainThreadStatistic();
    public MPStatistic.ThreadStatistic ServerLOS = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic LoaderThread = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic RecalcAllThread = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic SaveThread = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic PolyPathThread = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic WorldReuser = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic PlayerDownloadServer = new MPStatistic.ThreadStatistic();
    public MPStatistic.ThreadStatistic MapCollisionThread = new MPStatistic.ThreadStatistic();
    public MPStatistic.ProbeStatistic ChunkChecksum = new MPStatistic.ProbeStatistic();
    public MPStatistic.ProbeStatistic Bullet = new MPStatistic.ProbeStatistic();
    public MPStatistic.ProbeStatistic AnimationPlayerUpdate = new MPStatistic.ProbeStatistic();
    public MPStatistic.ProbeStatistic ServerMapPreupdate = new MPStatistic.ProbeStatistic();
    public MPStatistic.ProbeStatistic ServerMapPostupdate = new MPStatistic.ProbeStatistic();
    public MPStatistic.ProbeStatistic IngameStateUpdate = new MPStatistic.ProbeStatistic();
    private long packetLength = 0L;
    private int countIncomePackets = 0;
    private int countOutcomePackets = 0;
    private int countIncomeBytes = 0;
    private int countOutcomeBytes = 0;
    private int maxIncomeBytesPerSecond = 0;
    private int maxOutcomeBytesPerSecond = 0;
    private int currentIncomeBytesPerSecond = 0;
    private int currentOutcomeBytesPerSecond = 0;
    private long lastCalculateBPS = 0L;
    private long lastReport = 0L;
    private long minUpdatePeriod = 9999L;
    private long maxUpdatePeriod = 0L;
    private long avgUpdatePeriod = 0L;
    private long currentAvgUpdatePeriod = 0L;
    private long teleports = 0L;
    private long counter1 = 0L;
    private long counter2 = 0L;
    private long counter3 = 0L;
    private long updatePeriods = 0L;
    private int loadCellFromDisk = 0;
    private int saveCellToDisk = 0;
    public static boolean clientStatisticEnable = false;
    private PrintStream csvStatisticFile = null;
    private PrintStream csvIncomePacketsFile = null;
    private PrintStream csvIncomeBytesFile = null;
    private PrintStream csvOutcomePacketsFile = null;
    private PrintStream csvOutcomeBytesFile = null;
    private PrintStream csvConnectionsFile = null;
    private final ArrayList<Integer> csvConnections = new ArrayList<>();
    private KahluaTable table = null;

    public static MPStatistic getInstance() {
        if (instance == null) {
            instance = new MPStatistic();
        }

        return instance;
    }

    public void IncrementServerChunkThreadSaveNow() {
        this.countServerChunkThreadSaveNow++;
    }

    public void teleport() {
        this.teleports++;
    }

    public void count1(long long0) {
        this.counter1 += long0;
    }

    public void count2(long long0) {
        this.counter2 += long0;
    }

    public void count3(long long0) {
        this.counter3 += long0;
    }

    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putLong(this.minUpdatePeriod);
        byteBufferWriter.putLong(this.maxUpdatePeriod);
        byteBufferWriter.putLong(this.currentAvgUpdatePeriod / this.updatePeriods);
        byteBufferWriter.putLong(this.updatePeriods / Period);
        byteBufferWriter.putLong(this.teleports);
        byteBufferWriter.putLong(GameServer.udpEngine.connections.size());
        byteBufferWriter.putLong(this.counter1 / this.updatePeriods);
        byteBufferWriter.putLong(this.counter2 / this.updatePeriods);
        byteBufferWriter.putLong(this.counter3 / this.updatePeriods);
    }

    public void setPacketsLength(long long0) {
        this.packetLength = long0;
    }

    public void addIncomePacket(PacketTypes.PacketType packetType, int int0) {
        if (packetType != null) {
            packetType.incomePackets++;
            this.countIncomePackets++;
            packetType.incomeBytes += int0;
            this.countIncomeBytes += int0;
            this.currentIncomeBytesPerSecond += int0;
            this.calculateMaxBPS();
        }
    }

    public void addOutcomePacket(short short0, int int0) {
        PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(short0);
        if (packetType != null) {
            packetType.outcomePackets++;
            this.countOutcomePackets++;
            packetType.outcomeBytes += int0;
            this.countOutcomeBytes += int0;
            this.currentOutcomeBytesPerSecond += int0;
            this.calculateMaxBPS();
        }
    }

    void calculateMaxBPS() {
        if (System.currentTimeMillis() - this.lastCalculateBPS > 1000L) {
            this.lastCalculateBPS = System.currentTimeMillis();
            if (this.currentIncomeBytesPerSecond > this.maxIncomeBytesPerSecond) {
                this.maxIncomeBytesPerSecond = this.currentIncomeBytesPerSecond;
            }

            if (this.currentOutcomeBytesPerSecond > this.maxOutcomeBytesPerSecond) {
                this.maxOutcomeBytesPerSecond = this.currentOutcomeBytesPerSecond;
            }

            this.currentIncomeBytesPerSecond = 0;
            this.currentOutcomeBytesPerSecond = 0;
        }
    }

    public void IncrementLoadCellFromDisk() {
        this.loadCellFromDisk++;
    }

    public void IncrementSaveCellToDisk() {
        this.saveCellToDisk++;
    }

    public void process(long long0) {
        if (long0 > this.maxUpdatePeriod) {
            this.maxUpdatePeriod = long0;
        }

        if (long0 < this.minUpdatePeriod) {
            this.minUpdatePeriod = long0;
        }

        this.avgUpdatePeriod = (long)((float)this.avgUpdatePeriod + (float)(long0 - this.avgUpdatePeriod) * 0.05F);
        this.currentAvgUpdatePeriod += long0;
        this.updatePeriods++;
        if (Period != 0 && System.currentTimeMillis() - this.lastReport >= Period * 1000L) {
            this.lastReport = System.currentTimeMillis();
            this.printStatistic();
            this.printCSVStatistic();
            GameServer.sendShortStatistic();
            this.table = LuaManager.platform.newTable();
            this.table.rawset("lastReport", (double)this.lastReport);
            this.table.rawset("period", (double)Period);
            this.table.rawset("minUpdatePeriod", (double)this.minUpdatePeriod);
            this.table.rawset("maxUpdatePeriod", (double)this.maxUpdatePeriod);
            this.table.rawset("avgUpdatePeriod", (double)this.avgUpdatePeriod);
            this.maxUpdatePeriod = 0L;
            this.minUpdatePeriod = 9999L;
            this.currentAvgUpdatePeriod = 0L;
            this.updatePeriods = 0L;
            this.teleports = 0L;
            this.counter1 = 0L;
            this.counter2 = 0L;
            this.counter3 = 0L;
            this.table.rawset("loadCellFromDisk", (double)this.loadCellFromDisk);
            this.table.rawset("saveCellToDisk", (double)this.saveCellToDisk);
            this.loadCellFromDisk = 0;
            this.saveCellToDisk = 0;
            this.table.rawset("usedMemory", (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            this.table.rawset("totalMemory", (double)Runtime.getRuntime().totalMemory());
            this.table.rawset("freeMemory", (double)Runtime.getRuntime().freeMemory());
            this.table.rawset("countConnections", (double)GameServer.udpEngine.connections.size());
            KahluaTable table0 = LuaManager.platform.newTable();

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                KahluaTable table1 = LuaManager.platform.newTable();
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                table1.rawset("ip", udpConnection.ip);
                table1.rawset("username", udpConnection.username);
                table1.rawset("accessLevel", udpConnection.accessLevel);
                KahluaTable table2 = LuaManager.platform.newTable();

                for (int int1 = 0; int1 < udpConnection.players.length; int1++) {
                    if (udpConnection.players[int1] != null) {
                        KahluaTable table3 = LuaManager.platform.newTable();
                        table3.rawset("username", udpConnection.players[int1].username);
                        table3.rawset("x", (double)udpConnection.players[int1].x);
                        table3.rawset("y", (double)udpConnection.players[int1].y);
                        table3.rawset("z", (double)udpConnection.players[int1].z);
                        table2.rawset(int1, table3);
                    }
                }

                table1.rawset("users", table2);
                table1.rawset("diff", (double)udpConnection.statistic.diff);
                table1.rawset("pingAVG", (double)udpConnection.statistic.pingAVG);
                table1.rawset("remotePlayersCount", (double)udpConnection.statistic.remotePlayersCount);
                table1.rawset("remotePlayersDesyncAVG", (double)udpConnection.statistic.remotePlayersDesyncAVG);
                table1.rawset("remotePlayersDesyncMax", (double)udpConnection.statistic.remotePlayersDesyncMax);
                table1.rawset("remotePlayersTeleports", (double)udpConnection.statistic.remotePlayersTeleports);
                table1.rawset("zombiesCount", (double)udpConnection.statistic.zombiesCount);
                table1.rawset("zombiesLocalOwnership", (double)udpConnection.statistic.zombiesLocalOwnership);
                table1.rawset("zombiesDesyncAVG", (double)udpConnection.statistic.zombiesDesyncAVG);
                table1.rawset("zombiesDesyncMax", (double)udpConnection.statistic.zombiesDesyncMax);
                table1.rawset("zombiesTeleports", (double)udpConnection.statistic.zombiesTeleports);
                table1.rawset("FPS", (double)udpConnection.statistic.FPS);
                table1.rawset("FPSMin", (double)udpConnection.statistic.FPSMin);
                table1.rawset("FPSAvg", (double)udpConnection.statistic.FPSAvg);
                table1.rawset("FPSMax", (double)udpConnection.statistic.FPSMax);
                KahluaTable table4 = LuaManager.platform.newTable();
                short short0 = 0;

                for (int int2 = 0; int2 < 32; int2++) {
                    table4.rawset(int2, (double)udpConnection.statistic.FPSHistogramm[int2]);
                    if (short0 < udpConnection.statistic.FPSHistogramm[int2]) {
                        short0 = udpConnection.statistic.FPSHistogramm[int2];
                    }
                }

                table1.rawset("FPSHistogram", table4);
                table1.rawset("FPSHistogramMax", (double)short0);
                table0.rawset(int0, table1);
            }

            this.table.rawset("connections", table0);
            this.table.rawset("packetLength", (double)this.packetLength);
            this.table.rawset("countIncomePackets", (double)this.countIncomePackets);
            this.table.rawset("countIncomeBytes", (double)this.countIncomeBytes);
            this.table.rawset("maxIncomeBytesPerSecound", (double)this.maxIncomeBytesPerSecond);
            KahluaTable table5 = LuaManager.platform.newTable();
            int int3 = -1;

            for (PacketTypes.PacketType packetType0 : PacketTypes.packetTypes.values()) {
                if (packetType0.incomePackets > 0) {
                    KahluaTable table6 = LuaManager.platform.newTable();
                    table6.rawset("name", packetType0.name());
                    table6.rawset("count", (double)packetType0.incomePackets);
                    table6.rawset("bytes", (double)packetType0.incomeBytes);
                    table5.rawset(int3, table6);
                }

                packetType0.incomePackets = 0;
                packetType0.incomeBytes = 0;
            }

            this.table.rawset("incomePacketsTable", table5);
            this.countIncomePackets = 0;
            this.countIncomeBytes = 0;
            this.maxIncomeBytesPerSecond = 0;
            this.table.rawset("countOutcomePackets", (double)this.countOutcomePackets);
            this.table.rawset("countOutcomeBytes", (double)this.countOutcomeBytes);
            this.table.rawset("maxOutcomeBytesPerSecound", (double)this.maxOutcomeBytesPerSecond);
            KahluaTable table7 = LuaManager.platform.newTable();
            int3 = -1;

            for (PacketTypes.PacketType packetType1 : PacketTypes.packetTypes.values()) {
                if (packetType1.outcomePackets > 0) {
                    KahluaTable table8 = LuaManager.platform.newTable();
                    table8.rawset("name", packetType1.name());
                    table8.rawset("count", (double)packetType1.outcomePackets);
                    table8.rawset("bytes", (double)packetType1.outcomeBytes);
                    table7.rawset(int3++, table8);
                }

                packetType1.outcomePackets = 0;
                packetType1.outcomeBytes = 0;
            }

            this.table.rawset("outcomePacketsTable", table7);
            this.countOutcomePackets = 0;
            this.countOutcomeBytes = 0;
            this.maxOutcomeBytesPerSecond = 0;
            this.LoaderThreadTasks.Clear();
            this.RecalcThreadTasks.Clear();
            this.SaveTasks.Clear();
            this.ServerMapToLoad.Clear();
            this.ServerMapLoadedCells.Clear();
            this.ServerMapLoaded2.Clear();
            this.countServerChunkThreadSaveNow = 0;
            this.Main.Clear();
            this.ServerLOS.Clear();
            this.LoaderThread.Clear();
            this.RecalcAllThread.Clear();
            this.SaveThread.Clear();
            this.PolyPathThread.Clear();
            this.WorldReuser.Clear();
            this.PlayerDownloadServer.Clear();
            this.MapCollisionThread.Clear();
            this.ChunkChecksum.Clear();
            this.Bullet.Clear();
            this.AnimationPlayerUpdate.Clear();
            this.ServerMapPreupdate.Clear();
            this.ServerMapPostupdate.Clear();
            this.IngameStateUpdate.Clear();
            GameServer.getStatisticFromClients();
            GameServer.sendStatistic();
        }
    }

    private void printStatistic() {
        if (doPrintStatistic) {
            DebugLog.Statistic.println("=== STATISTICS ===");
            DebugLog.Statistic.println("UpdatePeriod (mils) min:" + this.minUpdatePeriod + " max:" + this.maxUpdatePeriod + " avg:" + this.avgUpdatePeriod);
            DebugLog.Statistic.println("Server cell disk operations load:" + this.loadCellFromDisk + " save:" + this.saveCellToDisk);
            DebugLog.Statistic
                .println(
                    "Memory (bytes):" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " of " + Runtime.getRuntime().totalMemory()
                );
            DebugLog.Statistic.println("== Connections:" + GameServer.udpEngine.connections.size() + " ==");

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                DebugLog.Statistic.println("Connection " + int0 + " " + udpConnection.ip + " " + udpConnection.username + " " + udpConnection.accessLevel);

                for (int int1 = 0; int1 < udpConnection.players.length; int1++) {
                    if (udpConnection.players[int1] != null) {
                        DebugLog.Statistic
                            .println(
                                "  User "
                                    + udpConnection.players[int1].username
                                    + " ("
                                    + udpConnection.players[int1].x
                                    + ", "
                                    + udpConnection.players[int1].y
                                    + ", "
                                    + udpConnection.players[int1].z
                                    + ")"
                            );
                    }
                }

                DebugLog.Statistic.println("  Ping:" + udpConnection.statistic.diff / 2 + " AVG:" + udpConnection.statistic.pingAVG);
                DebugLog.Statistic
                    .println(
                        "  Players count:"
                            + udpConnection.statistic.remotePlayersCount
                            + " desyncAVG:"
                            + udpConnection.statistic.remotePlayersDesyncAVG
                            + " desyncMAX:"
                            + udpConnection.statistic.remotePlayersDesyncMax
                            + " teleports:"
                            + udpConnection.statistic.remotePlayersTeleports
                    );
                DebugLog.Statistic
                    .println(
                        "  Zombies count:"
                            + udpConnection.statistic.zombiesCount
                            + " LocalOwnership:"
                            + udpConnection.statistic.zombiesLocalOwnership
                            + " desyncAVG:"
                            + udpConnection.statistic.zombiesDesyncAVG
                            + " desyncMAX:"
                            + udpConnection.statistic.zombiesDesyncMax
                            + " teleports:"
                            + udpConnection.statistic.zombiesTeleports
                    );
                DebugLog.Statistic
                    .println(
                        "  FPS:"
                            + udpConnection.statistic.FPS
                            + " Min:"
                            + udpConnection.statistic.FPSMin
                            + " Avg:"
                            + udpConnection.statistic.FPSAvg
                            + " Max:"
                            + udpConnection.statistic.FPSMax
                    );
            }

            DebugLog.Statistic.println("== Income Packets ==");
            DebugLog.Statistic.println("length of packet queue:" + this.packetLength);
            DebugLog.Statistic.println("count packets:" + this.countIncomePackets);
            DebugLog.Statistic.println("count bytes:" + this.countIncomeBytes);
            DebugLog.Statistic.println("max bps:" + this.maxIncomeBytesPerSecond);

            for (PacketTypes.PacketType packetType0 : PacketTypes.packetTypes.values()) {
                if (packetType0.incomePackets > 0) {
                    DebugLog.Statistic
                        .println(packetType0.name() + "(" + packetType0.getId() + ") count:" + packetType0.incomePackets + " bytes:" + packetType0.incomeBytes);
                }
            }

            DebugLog.Statistic.println("== Outcome Packets ==");
            DebugLog.Statistic.println("count packets:" + this.countOutcomePackets);
            DebugLog.Statistic.println("count bytes:" + this.countOutcomeBytes);
            DebugLog.Statistic.println("max bps:" + this.maxOutcomeBytesPerSecond);

            for (PacketTypes.PacketType packetType1 : PacketTypes.packetTypes.values()) {
                if (packetType1.outcomePackets > 0) {
                    DebugLog.Statistic
                        .println(
                            packetType1.name() + "(" + packetType1.getId() + ") count:" + packetType1.outcomePackets + " bytes:" + packetType1.outcomeBytes
                        );
                }
            }

            DebugLog.Statistic.println("=== END STATISTICS ===");
        }
    }

    public static String getStatisticDir() {
        String string = ZomboidFileSystem.instance.getCacheDirSub("Statistic");
        ZomboidFileSystem.ensureFolderExists(string);
        File file = new File(string);
        return file.getAbsolutePath();
    }

    private void removeCSVStatistics() {
        String string = getStatisticDir();

        try {
            File file0 = new File(string + File.separator + "Statistic.csv");
            file0.delete();
        } catch (Exception exception0) {
            DebugLog.Statistic.printException(exception0, "Delete file failed: Statistic.csv", LogSeverity.Error);
        }

        try {
            File file1 = new File(string + File.separator + "Connections.csv");
            file1.delete();
        } catch (Exception exception1) {
            DebugLog.Statistic.printException(exception1, "Delete file failed: Connections.csv", LogSeverity.Error);
        }

        try {
            File file2 = new File(string + File.separator + "IncomePackets.csv");
            file2.delete();
        } catch (Exception exception2) {
            DebugLog.Statistic.printException(exception2, "Delete file failed: IncomePackets.csv", LogSeverity.Error);
        }

        try {
            File file3 = new File(string + File.separator + "IncomeBytes.csv");
            file3.delete();
        } catch (Exception exception3) {
            DebugLog.Statistic.printException(exception3, "Delete file failed: IncomeBytes.csv", LogSeverity.Error);
        }

        try {
            File file4 = new File(string + File.separator + "OutcomePackets.csv");
            file4.delete();
        } catch (Exception exception4) {
            DebugLog.Statistic.printException(exception4, "Delete file failed: OutcomePackets.csv", LogSeverity.Error);
        }

        try {
            File file5 = new File(string + File.separator + "OutcomeBytes.csv");
            file5.delete();
        } catch (Exception exception5) {
            DebugLog.Statistic.printException(exception5, "Delete file failed: OutcomeBytes.csv", LogSeverity.Error);
        }
    }

    private void closeCSVStatistics() {
        if (this.csvStatisticFile != null) {
            this.csvStatisticFile.close();
        }

        this.csvStatisticFile = null;
        if (this.csvConnectionsFile != null) {
            this.csvConnectionsFile.close();
        }

        this.csvConnectionsFile = null;
        if (this.csvIncomePacketsFile != null) {
            this.csvIncomePacketsFile.close();
        }

        this.csvIncomePacketsFile = null;
        if (this.csvIncomeBytesFile != null) {
            this.csvIncomeBytesFile.close();
        }

        this.csvIncomeBytesFile = null;
        if (this.csvOutcomePacketsFile != null) {
            this.csvOutcomePacketsFile.close();
        }

        this.csvOutcomePacketsFile = null;
        if (this.csvOutcomeBytesFile != null) {
            this.csvOutcomeBytesFile.close();
        }

        this.csvOutcomeBytesFile = null;
    }

    private void openCSVStatistic() {
        if (doCSVStatistic) {
            String string = getStatisticDir();

            try {
                File file0 = new File(string + File.separator + "Statistic.csv");
                if (file0.exists()) {
                    this.csvStatisticFile = new PrintStream(new FileOutputStream(file0, true));
                } else {
                    this.csvStatisticFile = new PrintStream(file0);
                    this.csvStatisticFile
                        .println(
                            "lastReport; minUpdatePeriod; maxUpdatePeriod; avgUpdatePeriod; loadCellFromDisk; saveCellToDisk; countLoaderThreadTasksAdded; countLoaderThreadTasksProcessed; countRecalcThreadTasksAdded; countRecalcThreadTasksProcessed; countSaveUnloadedTasksAdded; countSaveLoadedTasksAdded; countSaveGameTimeTasksAdded; countQuitThreadTasksAdded; countSaveThreadTasksProcessed; countServerMapToLoadAdded; countServerMapToLoadCanceled; countServerMapLoadedCellsAdded; countServerMapLoadedCellsCanceled; countServerMapLoaded2Added; countServerMapLoaded2Canceled; countServerChunkThreadSaveNow; "
                                + this.Main.PrintTitle("MainThread")
                                + this.ServerLOS.PrintTitle("ServerLOS")
                                + this.LoaderThread.PrintTitle("LoaderThread")
                                + this.RecalcAllThread.PrintTitle("RecalcAllThread")
                                + this.SaveThread.PrintTitle("SaveThread")
                                + this.PolyPathThread.PrintTitle("PolyPathThread")
                                + this.WorldReuser.PrintTitle("WorldReuser")
                                + this.PlayerDownloadServer.PrintTitle("WorldReuser")
                                + this.MapCollisionThread.PrintTitle("MapCollisionThread")
                                + this.ChunkChecksum.PrintTitle("ChunkChecksum")
                                + this.Bullet.PrintTitle("Bullet")
                                + this.AnimationPlayerUpdate.PrintTitle("AnimationPlayerUpdate")
                                + this.ServerMapPreupdate.PrintTitle("ServerMapPreupdate")
                                + this.ServerMapPostupdate.PrintTitle("ServerMapPostupdate")
                                + this.IngameStateUpdate.PrintTitle("IngameStateUpdate")
                                + "totalMemory; freeMemory; countConnections; packetLength; countIncomePackets; countIncomeBytes; maxIncomeBytesPerSecound; countOutcomePackets; countOutcomeBytes; maxOutcomeBytesPerSecound"
                        );
                }
            } catch (FileNotFoundException fileNotFoundException0) {
                DebugLog.Statistic.printException(fileNotFoundException0, "Open file failed: Statistic.csv", LogSeverity.Error);
                if (this.csvStatisticFile != null) {
                    this.csvStatisticFile.close();
                }

                this.csvStatisticFile = null;
            }

            try {
                File file1 = new File(string + File.separator + "Connections.csv");
                if (file1.exists()) {
                    this.csvConnectionsFile = new PrintStream(new FileOutputStream(file1, true));
                } else {
                    this.csvConnectionsFile = new PrintStream(file1);
                    this.csvConnectionsFile.print("ip; ");
                    this.csvConnectionsFile.print("username; ");
                    this.csvConnectionsFile.print("accessLevel; ");
                    this.csvConnectionsFile.print("players.length; ");
                    this.csvConnectionsFile.print("ping; ");
                    this.csvConnectionsFile.print("pingAVG; ");
                    this.csvConnectionsFile.print("remotePlayersCount; ");
                    this.csvConnectionsFile.print("remotePlayersDesyncAVG; ");
                    this.csvConnectionsFile.print("remotePlayersDesyncMax; ");
                    this.csvConnectionsFile.print("remotePlayersTeleports; ");
                    this.csvConnectionsFile.print("zombiesCount; ");
                    this.csvConnectionsFile.print("zombiesLocalOwnership; ");
                    this.csvConnectionsFile.print("zombiesDesyncAVG; ");
                    this.csvConnectionsFile.print("zombiesDesyncMax; ");
                    this.csvConnectionsFile.print("zombiesTeleports; ");
                    this.csvConnectionsFile.print("FPS; ");
                    this.csvConnectionsFile.print("FPSMin; ");
                    this.csvConnectionsFile.print("FPSAvg; ");
                    this.csvConnectionsFile.print("FPSMax; ");

                    for (int int0 = 0; int0 < 32; int0++) {
                        this.csvConnectionsFile.print("FPSHistogramm[" + int0 + "]; ");
                    }

                    this.csvConnectionsFile.println();
                }
            } catch (FileNotFoundException fileNotFoundException1) {
                DebugLog.Statistic.printException(fileNotFoundException1, "Open file failed: Connections.csv", LogSeverity.Error);
                if (this.csvConnectionsFile != null) {
                    this.csvConnectionsFile.close();
                }

                this.csvConnectionsFile = null;
            }

            try {
                File file2 = new File(string + File.separator + "IncomePackets.csv");
                if (file2.exists()) {
                    this.csvIncomePacketsFile = new PrintStream(new FileOutputStream(file2, true));
                } else {
                    this.csvIncomePacketsFile = new PrintStream(file2);

                    for (PacketTypes.PacketType packetType0 : PacketTypes.packetTypes.values()) {
                        this.csvIncomePacketsFile.print(packetType0.name() + "(" + packetType0.getId() + "); ");
                    }

                    this.csvIncomePacketsFile.println();
                }
            } catch (FileNotFoundException fileNotFoundException2) {
                DebugLog.Statistic.printException(fileNotFoundException2, "Open file failed: IncomePackets.csv", LogSeverity.Error);
                if (this.csvIncomePacketsFile != null) {
                    this.csvIncomePacketsFile.close();
                }

                this.csvIncomePacketsFile = null;
            }

            try {
                File file3 = new File(string + File.separator + "IncomeBytes.csv");
                if (file3.exists()) {
                    this.csvIncomeBytesFile = new PrintStream(new FileOutputStream(file3, true));
                } else {
                    this.csvIncomeBytesFile = new PrintStream(file3);

                    for (PacketTypes.PacketType packetType1 : PacketTypes.packetTypes.values()) {
                        this.csvIncomeBytesFile.print(packetType1.name() + "(" + packetType1.getId() + "); ");
                    }

                    this.csvIncomeBytesFile.println();
                }
            } catch (FileNotFoundException fileNotFoundException3) {
                DebugLog.Statistic.printException(fileNotFoundException3, "Open file failed: IncomeBytes.csv", LogSeverity.Error);
                if (this.csvIncomeBytesFile != null) {
                    this.csvIncomeBytesFile.close();
                }

                this.csvIncomeBytesFile = null;
            }

            try {
                File file4 = new File(string + File.separator + "OutcomePackets.csv");
                if (file4.exists()) {
                    this.csvOutcomePacketsFile = new PrintStream(new FileOutputStream(file4, true));
                } else {
                    this.csvOutcomePacketsFile = new PrintStream(file4);

                    for (PacketTypes.PacketType packetType2 : PacketTypes.packetTypes.values()) {
                        this.csvOutcomePacketsFile.print(packetType2.name() + "(" + packetType2.getId() + "); ");
                    }

                    this.csvOutcomePacketsFile.println();
                }
            } catch (FileNotFoundException fileNotFoundException4) {
                DebugLog.Statistic.printException(fileNotFoundException4, "Open file failed: OutcomePackets.csv", LogSeverity.Error);
                if (this.csvOutcomePacketsFile != null) {
                    this.csvOutcomePacketsFile.close();
                }

                this.csvOutcomePacketsFile = null;
            }

            try {
                File file5 = new File(string + File.separator + "OutcomeBytes.csv");
                if (file5.exists()) {
                    this.csvOutcomeBytesFile = new PrintStream(new FileOutputStream(file5, true));
                } else {
                    this.csvOutcomeBytesFile = new PrintStream(file5);

                    for (PacketTypes.PacketType packetType3 : PacketTypes.packetTypes.values()) {
                        this.csvOutcomeBytesFile.print(packetType3.name() + "(" + packetType3.getId() + "); ");
                    }

                    this.csvOutcomeBytesFile.println();
                }
            } catch (FileNotFoundException fileNotFoundException5) {
                DebugLog.Statistic.printException(fileNotFoundException5, "Open file failed: OutcomeBytes.csv", LogSeverity.Error);
                if (this.csvOutcomeBytesFile != null) {
                    this.csvOutcomeBytesFile.close();
                }

                this.csvOutcomeBytesFile = null;
            }
        }
    }

    private void printCSVStatistic() {
        if (doCSVStatistic) {
            try {
                try {
                    this.openCSVStatistic();
                    if (this.csvStatisticFile != null) {
                        this.csvStatisticFile.print(System.currentTimeMillis() + ";");
                        this.csvStatisticFile.print(this.minUpdatePeriod + ";");
                        this.csvStatisticFile.print(this.maxUpdatePeriod + ";");
                        this.csvStatisticFile.print(this.avgUpdatePeriod + ";");
                        this.csvStatisticFile.print(this.loadCellFromDisk + ";");
                        this.csvStatisticFile.print(this.saveCellToDisk + ";");
                        this.csvStatisticFile.print(this.LoaderThreadTasks.Print());
                        this.csvStatisticFile.print(this.RecalcThreadTasks.Print());
                        this.csvStatisticFile.print(this.SaveTasks.Print());
                        this.csvStatisticFile.print(this.ServerMapToLoad.Print());
                        this.csvStatisticFile.print(this.ServerMapLoadedCells.Print());
                        this.csvStatisticFile.print(this.ServerMapLoaded2.Print());
                        this.csvStatisticFile.print(this.countServerChunkThreadSaveNow + ";");
                        this.csvStatisticFile.print(this.Main.Print());
                        this.csvStatisticFile.print(this.ServerLOS.Print());
                        this.csvStatisticFile.print(this.LoaderThread.Print());
                        this.csvStatisticFile.print(this.RecalcAllThread.Print());
                        this.csvStatisticFile.print(this.SaveThread.Print());
                        this.csvStatisticFile.print(this.PolyPathThread.Print());
                        this.csvStatisticFile.print(this.WorldReuser.Print());
                        this.csvStatisticFile.print(this.PlayerDownloadServer.Print());
                        this.csvStatisticFile.print(this.MapCollisionThread.Print());
                        this.csvStatisticFile.print(this.ChunkChecksum.Print());
                        this.csvStatisticFile.print(this.Bullet.Print());
                        this.csvStatisticFile.print(this.AnimationPlayerUpdate.Print());
                        this.csvStatisticFile.print(this.ServerMapPreupdate.Print());
                        this.csvStatisticFile.print(this.ServerMapPostupdate.Print());
                        this.csvStatisticFile.print(this.IngameStateUpdate.Print());
                        this.csvStatisticFile.print(Runtime.getRuntime().totalMemory() + ";");
                        this.csvStatisticFile.print(Runtime.getRuntime().freeMemory() + ";");
                        this.csvStatisticFile.print(GameServer.udpEngine.connections.size() + ";");
                        this.csvStatisticFile.print(this.packetLength + ";");
                        this.csvStatisticFile.print(this.countIncomePackets + ";");
                        this.csvStatisticFile.print(this.countIncomeBytes + ";");
                        this.csvStatisticFile.print(this.maxIncomeBytesPerSecond + ";");
                        this.csvStatisticFile.print(this.countOutcomePackets + ";");
                        this.csvStatisticFile.print(this.countOutcomeBytes + ";");
                        this.csvStatisticFile.println(this.maxOutcomeBytesPerSecond + ";");
                        this.csvStatisticFile.flush();
                    }

                    if (this.csvConnectionsFile != null) {
                        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                            UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);

                            try {
                                if (udpConnection0 != null
                                    && udpConnection0.username != null
                                    && !this.csvConnections.contains(udpConnection0.username.hashCode())) {
                                    this.csvConnections.add(udpConnection0.username.hashCode());
                                }
                            } catch (NullPointerException nullPointerException0) {
                                nullPointerException0.printStackTrace();
                                return;
                            }
                        }

                        for (int int1 = 0; int1 < this.csvConnections.size(); int1++) {
                            UdpConnection udpConnection1 = null;

                            for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); int2++) {
                                UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(int2);
                                if (udpConnection2 != null
                                    && udpConnection2.username != null
                                    && udpConnection2.username.hashCode() == this.csvConnections.get(int1)) {
                                    udpConnection1 = udpConnection2;
                                }
                            }

                            if (udpConnection1 == null) {
                                for (int int3 = 0; int3 < 51; int3++) {
                                    this.csvConnectionsFile.print("; ");
                                }
                            } else {
                                this.csvConnectionsFile.print(udpConnection1.ip + "; ");
                                this.csvConnectionsFile.print(udpConnection1.username + "; ");
                                this.csvConnectionsFile.print(udpConnection1.accessLevel + "; ");
                                this.csvConnectionsFile.print(udpConnection1.players.length + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.diff / 2 + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.pingAVG + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.remotePlayersCount + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.remotePlayersDesyncAVG + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.remotePlayersDesyncMax + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.remotePlayersTeleports + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.zombiesCount + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.zombiesLocalOwnership + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.zombiesDesyncAVG + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.zombiesDesyncMax + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.zombiesTeleports + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.FPS + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.FPSMin + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.FPSAvg + "; ");
                                this.csvConnectionsFile.print(udpConnection1.statistic.FPSMax + "; ");

                                for (int int4 = 0; int4 < 32; int4++) {
                                    this.csvConnectionsFile.print(udpConnection1.statistic.FPSHistogramm[int4] + "; ");
                                }
                            }

                            this.csvConnectionsFile.println();
                        }

                        this.csvConnectionsFile.flush();
                    }

                    if (this.csvIncomePacketsFile != null
                        && this.csvOutcomePacketsFile != null
                        && this.csvIncomeBytesFile != null
                        && this.csvOutcomeBytesFile != null) {
                        for (PacketTypes.PacketType packetType : PacketTypes.packetTypes.values()) {
                            this.csvIncomePacketsFile.print(packetType.incomePackets + ";");
                            this.csvIncomeBytesFile.print(packetType.incomeBytes + ";");
                            this.csvOutcomePacketsFile.print(packetType.outcomePackets + ";");
                            this.csvOutcomeBytesFile.print(packetType.outcomeBytes + ";");
                        }

                        this.csvIncomePacketsFile.println();
                        this.csvIncomeBytesFile.println();
                        this.csvOutcomePacketsFile.println();
                        this.csvOutcomeBytesFile.println();
                        this.csvIncomePacketsFile.flush();
                        this.csvIncomeBytesFile.flush();
                        this.csvOutcomePacketsFile.flush();
                        this.csvOutcomeBytesFile.flush();
                        return;
                    }
                } catch (NullPointerException nullPointerException1) {
                    nullPointerException1.printStackTrace();
                }
            } finally {
                this.closeCSVStatistics();
            }
        }
    }

    public void getStatisticTable(ByteBuffer byteBuffer) throws IOException {
        if (this.table != null) {
            this.table.save(byteBuffer);
        }
    }

    public void setStatisticTable(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.remaining() != 0) {
            this.table = LuaManager.platform.newTable();

            try {
                this.table.load(byteBuffer, 195);
                this.table.rawset("lastReportTime", (double)System.currentTimeMillis());
            } catch (Exception exception) {
                this.table = null;
                ExceptionLogger.logException(exception);
            }
        }
    }

    public KahluaTable getStatisticTableForLua() {
        return this.table;
    }

    public void printEnabled(boolean boolean0) {
        doPrintStatistic = boolean0;
    }

    public void writeEnabled(boolean boolean0) {
        doCSVStatistic = boolean0;
        if (boolean0) {
            this.removeCSVStatistics();
        }
    }

    public void setPeriod(int int0) {
        Period = Math.max(int0, 0);
        if (this.table != null) {
            this.table.rawset("period", (double)Period);
        }
    }

    public class MainThreadStatistic extends MPStatistic.ThreadStatistic {
        private long timeStartSleep = 0L;

        @Override
        public void Start() {
            if (this.timeStart == 0L) {
                this.timeStart = System.currentTimeMillis();
            } else {
                long long0 = System.currentTimeMillis() - this.timeStart;
                this.timeStart = System.currentTimeMillis();
                this.timeWork += long0;
                if (this.timeMax < long0) {
                    this.timeMax = long0;
                }

                this.timeCount++;
            }
        }

        @Override
        public void End() {
        }

        public void StartSleep() {
            this.timeStartSleep = System.currentTimeMillis();
        }

        public void EndSleep() {
            long long0 = System.currentTimeMillis() - this.timeStartSleep;
            this.timeSleep += long0;
            this.timeStart += long0;
        }
    }

    public static class ProbeStatistic {
        protected boolean started = false;
        protected long timeStart = 0L;
        protected long timeWork = 0L;
        protected long timeMax = 0L;
        protected long timeCount = 0L;

        public void Clear() {
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeCount = 0L;
        }

        public String PrintTitle(String string) {
            return string + "Work; " + string + "Max; " + string + "Count;";
        }

        public String Print() {
            return this.timeWork / 1000000L + "; " + this.timeMax / 1000000L + "; " + this.timeCount + "; ";
        }

        public void Start() {
            this.timeStart = System.nanoTime();
            this.timeCount++;
            this.started = true;
        }

        public void End() {
            if (this.started) {
                long long0 = System.nanoTime() - this.timeStart;
                this.timeWork += long0;
                if (this.timeMax < long0) {
                    this.timeMax = long0;
                }

                this.started = false;
            }
        }
    }

    public static class SaveTasksStatistic extends MPStatistic.TasksStatistic {
        private int SaveUnloadedTasksAdded = 0;
        private int SaveLoadedTasksAdded = 0;
        private int SaveGameTimeTasksAdded = 0;
        private int QuitThreadTasksAdded = 0;

        @Override
        public void Clear() {
            super.Clear();
            this.SaveUnloadedTasksAdded = 0;
            this.SaveLoadedTasksAdded = 0;
            this.SaveGameTimeTasksAdded = 0;
            this.QuitThreadTasksAdded = 0;
        }

        @Override
        public String PrintTitle(String string) {
            return string
                + "SaveUnloadedAdded; "
                + string
                + "SaveLoadedAdded; "
                + string
                + "SaveGameTimeAdded; "
                + string
                + "QuitThreadAdded; "
                + string
                + "Processed; ";
        }

        @Override
        public String Print() {
            return this.SaveUnloadedTasksAdded
                + "; "
                + this.SaveLoadedTasksAdded
                + "; "
                + this.SaveGameTimeTasksAdded
                + "; "
                + this.QuitThreadTasksAdded
                + "; "
                + this.processed
                + "; ";
        }

        public void SaveUnloadedTasksAdded() {
            this.SaveUnloadedTasksAdded++;
        }

        public void SaveLoadedTasksAdded() {
            this.SaveLoadedTasksAdded++;
        }

        public void SaveGameTimeTasksAdded() {
            this.SaveGameTimeTasksAdded++;
        }

        public void QuitThreadTasksAdded() {
            this.QuitThreadTasksAdded++;
        }
    }

    public static class ServerCellStatistic {
        protected long added = 0L;
        protected long canceled = 0L;

        public void Clear() {
            this.added = 0L;
            this.canceled = 0L;
        }

        public String PrintTitle(String string) {
            return string + "Added; " + string + "Canceled; ";
        }

        public String Print() {
            return this.added + "; " + this.canceled + "; ";
        }

        public void Added() {
            this.added++;
        }

        public void Added(int int0) {
            this.added += int0;
        }

        public void Canceled() {
            this.canceled++;
        }
    }

    public static class TasksStatistic {
        protected long added = 0L;
        protected long processed = 0L;

        public void Clear() {
            this.added = 0L;
            this.processed = 0L;
        }

        public String PrintTitle(String string) {
            return string + "Added; " + string + "Processed; ";
        }

        public String Print() {
            return this.added + "; " + this.processed + "; ";
        }

        public void Added() {
            this.added++;
        }

        public void Processed() {
            this.processed++;
        }
    }

    public static class ThreadStatistic {
        protected boolean started = false;
        protected long timeStart = 0L;
        protected long timeWork = 0L;
        protected long timeMax = 0L;
        protected long timeSleep = 0L;
        protected long timeCount = 0L;

        public void Clear() {
            this.timeWork = 0L;
            this.timeMax = 0L;
            this.timeSleep = 0L;
            this.timeCount = 0L;
        }

        public String PrintTitle(String string) {
            return string + "Work; " + string + "Max; " + string + "Sleep; " + string + "Count;";
        }

        public String Print() {
            return this.timeWork + "; " + this.timeMax + "; " + this.timeSleep + "; " + this.timeCount + "; ";
        }

        public void Start() {
            if (this.started) {
                this.End();
            }

            if (this.timeStart != 0L) {
                this.timeSleep = this.timeSleep + (System.currentTimeMillis() - this.timeStart);
            }

            this.timeStart = System.currentTimeMillis();
            this.timeCount++;
            this.started = true;
        }

        public void End() {
            if (this.timeStart != 0L && this.started) {
                long long0 = System.currentTimeMillis() - this.timeStart;
                this.timeStart = System.currentTimeMillis();
                this.timeWork += long0;
                if (this.timeMax < long0) {
                    this.timeMax = long0;
                }

                this.started = false;
            }
        }
    }
}
