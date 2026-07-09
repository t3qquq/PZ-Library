// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkTeleport;
import zombie.characters.NetworkZombieVariables;
import zombie.characters.Safety;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatManager;
import zombie.commands.PlayerType;
import zombie.commands.serverCommands.LogCommand;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.raknet.VoiceManager;
import zombie.core.raknet.VoiceManagerData;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.ZNet;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.gameStates.IngameState;
import zombie.globalObjects.CGlobalObjectNetwork;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridOcclusionData;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectSyncRequests;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.packets.ActionPacket;
import zombie.network.packets.AddXp;
import zombie.network.packets.CleanBurn;
import zombie.network.packets.DeadPlayerPacket;
import zombie.network.packets.DeadZombiePacket;
import zombie.network.packets.Disinfect;
import zombie.network.packets.EventPacket;
import zombie.network.packets.PlaySoundPacket;
import zombie.network.packets.PlayWorldSoundPacket;
import zombie.network.packets.PlayerDataRequestPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.RemoveBullet;
import zombie.network.packets.RemoveCorpseFromMap;
import zombie.network.packets.RemoveGlass;
import zombie.network.packets.RequestDataPacket;
import zombie.network.packets.SafetyPacket;
import zombie.network.packets.StartFire;
import zombie.network.packets.Stitch;
import zombie.network.packets.StopSoundPacket;
import zombie.network.packets.SyncClothingPacket;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.network.packets.SyncNonPvpZonePacket;
import zombie.network.packets.SyncSafehousePacket;
import zombie.network.packets.ValidatePacket;
import zombie.network.packets.VehicleAuthorizationPacket;
import zombie.network.packets.WaveSignal;
import zombie.network.packets.hit.HitCharacterPacket;
import zombie.network.packets.hit.PlayerHitPlayerPacket;
import zombie.network.packets.hit.PlayerHitSquarePacket;
import zombie.network.packets.hit.PlayerHitVehiclePacket;
import zombie.network.packets.hit.PlayerHitZombiePacket;
import zombie.network.packets.hit.VehicleHitPacket;
import zombie.network.packets.hit.VehicleHitPlayerPacket;
import zombie.network.packets.hit.VehicleHitZombiePacket;
import zombie.network.packets.hit.ZombieHitPlayerPacket;
import zombie.popman.MPDebugInfo;
import zombie.popman.NetworkZombieSimulator;
import zombie.popman.ZombieCountOptimiser;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.savefile.ClientPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.util.AddCoopPlayer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.world.moddata.GlobalModData;
import zombie.worldMap.WorldMapRemotePlayer;
import zombie.worldMap.WorldMapRemotePlayers;

public class GameClient {
    public static final GameClient instance = new GameClient();
    public static final int DEFAULT_PORT = 16361;
    public static boolean bClient = false;
    public static UdpConnection connection;
    public static int count = 0;
    public static String ip = "localhost";
    public static String ServerName = "";
    public static String localIP = "";
    public static String password = "testpass";
    public static boolean allChatMuted = false;
    public static String username = "lemmy101";
    public static String serverPassword = "";
    public static boolean useSteamRelay = false;
    public UdpEngine udpEngine;
    public byte ID = -1;
    public float timeSinceKeepAlive = 0.0F;
    UpdateLimit itemSendFrequency = new UpdateLimit(3000L);
    public static int port = GameServer.DEFAULT_PORT;
    public boolean bPlayerConnectSent = false;
    private boolean bClientStarted = false;
    private int ResetID = 0;
    private boolean bConnectionLost = false;
    public static String checksum = "";
    public static boolean checksumValid = false;
    public static List<Long> pingsList = new ArrayList<>();
    public static String GameMap;
    public static boolean bFastForward;
    public static final ClientServerMap[] loadedCells = new ClientServerMap[4];
    public int DEBUG_PING = 5;
    public IsoObjectSyncRequests objectSyncReq = new IsoObjectSyncRequests();
    public ObjectsSyncRequests worldObjectsSyncReq = new ObjectsSyncRequests(true);
    public static boolean bCoopInvite;
    private ArrayList<IsoPlayer> connectedPlayers = new ArrayList<>();
    private static boolean isPaused = false;
    private final ArrayList<IsoPlayer> players = new ArrayList<>();
    public boolean idMapDirty = true;
    private static final int sendZombieWithoutNeighbor = 4000;
    private static final int sendZombieWithNeighbor = 200;
    public final UpdateLimit sendZombieTimer = new UpdateLimit(4000L);
    public final UpdateLimit sendZombieRequestsTimer = new UpdateLimit(200L);
    private final UpdateLimit UpdateChannelsRoamingLimit = new UpdateLimit(3010L);
    private long disconnectTime = System.currentTimeMillis();
    private static final long disconnectTimeLimit = 10000L;
    public static long steamID = 0L;
    public static final Map<Short, Vector2> positions = new HashMap<>(ServerOptions.getInstance().getMaxPlayers());
    private int safehouseUpdateTimer = 0;
    @Deprecated
    private boolean delayPacket = false;
    private final ArrayList<Integer> delayedDisconnect = new ArrayList<>();
    static TShortArrayList tempShortList = new TShortArrayList();
    private volatile GameClient.RequestState request;
    public KahluaTable ServerSpawnRegions = null;
    static final ConcurrentLinkedQueue<ZomboidNetData> MainLoopNetDataQ = new ConcurrentLinkedQueue<>();
    static final ArrayList<ZomboidNetData> MainLoopNetData = new ArrayList<>();
    static final ArrayList<ZomboidNetData> LoadingMainLoopNetData = new ArrayList<>();
    static final ArrayList<ZomboidNetData> DelayedCoopNetData = new ArrayList<>();
    public boolean bConnected = false;
    UpdateLimit PlayerUpdateReliableLimit = new UpdateLimit(2000L);
    public int TimeSinceLastUpdate = 0;
    ByteBuffer staticTest = ByteBuffer.allocate(20000);
    ByteBufferWriter wr = new ByteBufferWriter(this.staticTest);
    long StartHeartMilli = 0L;
    long EndHeartMilli = 0L;
    public int ping = 0;
    public static float ServerPredictedAhead = 0.0F;
    public static final HashMap<Short, IsoPlayer> IDToPlayerMap = new HashMap<>();
    public static final TShortObjectHashMap<IsoZombie> IDToZombieMap = new TShortObjectHashMap<>();
    public static boolean bIngame;
    public static boolean askPing = false;
    public final ArrayList<String> ServerMods = new ArrayList<>();
    public ErosionConfig erosionConfig;
    public static Calendar startAuth = null;
    public static String poisonousBerry = null;
    public static String poisonousMushroom = null;
    final ArrayList<ZomboidNetData> incomingNetData = new ArrayList<>();
    private final HashMap<ItemContainer, ArrayList<InventoryItem>> itemsToSend = new HashMap<>();
    private final HashMap<ItemContainer, ArrayList<InventoryItem>> itemsToSendRemove = new HashMap<>();
    KahluaTable dbSchema;

    public IsoPlayer getPlayerByOnlineID(short id) {
        return IDToPlayerMap.get(id);
    }

    public void init() {
        LoadingMainLoopNetData.clear();
        MainLoopNetDataQ.clear();
        MainLoopNetData.clear();
        DelayedCoopNetData.clear();
        bIngame = false;
        IDToPlayerMap.clear();
        IDToZombieMap.clear();
        pingsList.clear();
        this.itemsToSend.clear();
        this.itemsToSendRemove.clear();
        IDToZombieMap.setAutoCompactionFactor(0.0F);
        this.bPlayerConnectSent = false;
        this.bConnectionLost = false;
        this.delayedDisconnect.clear();
        GameWindow.bServerDisconnected = false;
        this.ServerSpawnRegions = null;
        this.startClient();
    }

    public void startClient() {
        if (this.bClientStarted) {
            this.udpEngine.Connect(ip, port, serverPassword, useSteamRelay);
        } else {
            try {
                this.udpEngine = new UdpEngine(Rand.Next(10000) + 12345, 0, 1, null, false);
                if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
                    this.udpEngine.Connect("127.0.0.1", CoopMaster.instance.getServerPort(), serverPassword, false);
                } else {
                    this.udpEngine.Connect(ip, port, serverPassword, useSteamRelay);
                }

                this.bClientStarted = true;
            } catch (Exception exception) {
                DebugLog.Network.printException(exception, "Exception thrown during GameClient.startClient.", LogSeverity.Error);
            }
        }
    }

    static void receiveStatistic(ByteBuffer byteBuffer, short var1) {
        try {
            long long0 = byteBuffer.getLong();
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.Statistic.doPacket(byteBufferWriter);
            byteBufferWriter.putLong(long0);
            MPStatisticClient.getInstance().send(byteBufferWriter);
            PacketTypes.PacketType.Statistic.send(connection);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static void receiveStatisticRequest(ByteBuffer byteBuffer, short var1) {
        try {
            MPStatistic.getInstance().setStatisticTable(byteBuffer);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        LuaEventManager.triggerEvent("OnServerStatisticReceived");
    }

    static void receivePlayerUpdate(ByteBuffer byteBuffer, short var1) {
        PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
        playerPacket.parse(byteBuffer, connection);

        try {
            IsoPlayer player = IDToPlayerMap.get(playerPacket.id);
            if (player == null) {
                PlayerDataRequestPacket playerDataRequestPacket = new PlayerDataRequestPacket();
                playerDataRequestPacket.set(playerPacket.id);
                ByteBufferWriter byteBufferWriter = connection.startPacket();
                PacketTypes.PacketType.PlayerDataRequest.doPacket(byteBufferWriter);
                playerDataRequestPacket.write(byteBufferWriter);
                PacketTypes.PacketType.PlayerDataRequest.send(connection);
            } else {
                player.lastRemoteUpdate = System.currentTimeMillis();
                rememberPlayerPosition(player, playerPacket.realx, playerPacket.realy);
                if (!player.networkAI.isSetVehicleHit()) {
                    player.networkAI.parse(playerPacket);
                }

                player.bleedingLevel = playerPacket.bleedingLevel;
                if (player.getVehicle() == null
                    && !playerPacket.usePathFinder
                    && (
                        player.networkAI.distance.getLength() > 7.0F
                            || IsoUtils.DistanceTo(playerPacket.x, playerPacket.y, playerPacket.z, player.x, player.y, player.z) > 1.0F
                                && (int)player.z != playerPacket.z
                    )) {
                    NetworkTeleport.update(player, playerPacket);
                    NetworkTeleport.teleport(player, playerPacket, 1.0F);
                }

                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)playerPacket.x, (double)playerPacket.y, (double)playerPacket.z);
                if (square != null) {
                    if (player.isAlive() && !IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
                        IsoWorld.instance.CurrentCell.getObjectList().add(player);
                        player.setCurrent(square);
                    }
                } else if (IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
                    player.removeFromWorld();
                    player.removeFromSquare();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static void receiveZombieSimulation(ByteBuffer byteBuffer, short var1) {
        NetworkZombieSimulator.getInstance().clear();
        boolean boolean0 = byteBuffer.get() == 1;
        if (boolean0) {
            instance.sendZombieTimer.setUpdatePeriod(200L);
        } else {
            instance.sendZombieTimer.setUpdatePeriod(4000L);
        }

        short short0 = byteBuffer.getShort();

        for (short short1 = 0; short1 < short0; short1++) {
            short short2 = byteBuffer.getShort();
            IsoZombie zombie0 = IDToZombieMap.get(short2);
            if (zombie0 != null) {
                VirtualZombieManager.instance.removeZombieFromWorld(zombie0);
            }
        }

        short short3 = byteBuffer.getShort();

        for (short short4 = 0; short4 < short3; short4++) {
            short short5 = byteBuffer.getShort();
            NetworkZombieSimulator.getInstance().add(short5);
        }

        NetworkZombieSimulator.getInstance().added();
        NetworkZombieSimulator.getInstance().receivePacket(byteBuffer, connection);
    }

    static void receiveZombieControl(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        IsoZombie zombie0 = IDToZombieMap.get(short0);
        if (zombie0 != null) {
            NetworkZombieVariables.setInt(zombie0, short1, int0);
        }
    }

    public void Shutdown() {
        if (this.bClientStarted) {
            this.udpEngine.Shutdown();
            this.bClientStarted = false;
        }
    }

    public void update() {
        ZombieCountOptimiser.startCount();
        if (this.safehouseUpdateTimer == 0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
            this.safehouseUpdateTimer = 3000;
            SafeHouse.updateSafehousePlayersConnected();
        }

        if (this.safehouseUpdateTimer > 0) {
            this.safehouseUpdateTimer--;
        }

        for (ZomboidNetData zomboidNetData0 = MainLoopNetDataQ.poll(); zomboidNetData0 != null; zomboidNetData0 = MainLoopNetDataQ.poll()) {
            MainLoopNetData.add(zomboidNetData0);
        }

        synchronized (this.delayedDisconnect) {
            while (!this.delayedDisconnect.isEmpty()) {
                int int0 = this.delayedDisconnect.remove(0);
                switch (int0) {
                    case 17:
                        if (!SteamUtils.isSteamModeEnabled()) {
                            LuaEventManager.triggerEvent("OnConnectFailed", null);
                        }
                        break;
                    case 18:
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_AlreadyConnected"));
                    case 19:
                    case 20:
                    case 22:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    default:
                        break;
                    case 21:
                        LuaEventManager.triggerEvent("OnDisconnect");
                        break;
                    case 23:
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_Banned"));
                        break;
                    case 24:
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_InvalidServerPassword"));
                        break;
                    case 32:
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_ConnectionLost"));
                }
            }
        }

        if (!this.bConnectionLost) {
            if (!this.bPlayerConnectSent) {
                for (int int1 = 0; int1 < MainLoopNetData.size(); int1++) {
                    ZomboidNetData zomboidNetData1 = MainLoopNetData.get(int1);
                    if (!this.gameLoadingDealWithNetData(zomboidNetData1)) {
                        LoadingMainLoopNetData.add(zomboidNetData1);
                    }
                }

                MainLoopNetData.clear();
                WorldStreamer.instance.updateMain();
            } else {
                if (!LoadingMainLoopNetData.isEmpty()) {
                    DebugLog.log(DebugType.Network, "Processing delayed packets...");
                    MainLoopNetData.addAll(0, LoadingMainLoopNetData);
                    LoadingMainLoopNetData.clear();
                }

                if (!DelayedCoopNetData.isEmpty() && IsoWorld.instance.AddCoopPlayers.isEmpty()) {
                    DebugLog.log(DebugType.Network, "Processing delayed coop packets...");
                    MainLoopNetData.addAll(0, DelayedCoopNetData);
                    DelayedCoopNetData.clear();
                }

                long long0 = System.currentTimeMillis();

                for (int int2 = 0; int2 < MainLoopNetData.size(); int2++) {
                    ZomboidNetData zomboidNetData2 = MainLoopNetData.get(int2);
                    if (zomboidNetData2.time + this.DEBUG_PING <= long0) {
                        this.mainLoopDealWithNetData(zomboidNetData2);
                        MainLoopNetData.remove(int2--);
                    }
                }

                for (int int3 = 0; int3 < IsoWorld.instance.CurrentCell.getObjectList().size(); int3++) {
                    IsoMovingObject movingObject = IsoWorld.instance.CurrentCell.getObjectList().get(int3);
                    if (movingObject instanceof IsoPlayer && !((IsoPlayer)movingObject).isLocalPlayer() && !this.getPlayers().contains(movingObject)) {
                        if (Core.bDebug) {
                            DebugLog.log("Disconnected/Distant player " + ((IsoPlayer)movingObject).username + " in CurrentCell.getObjectList() removed");
                        }

                        IsoWorld.instance.CurrentCell.getObjectList().remove(int3--);
                    }
                }

                try {
                    this.sendAddedRemovedItems(false);
                } catch (Exception exception0) {
                    exception0.printStackTrace();
                    ExceptionLogger.logException(exception0);
                }

                try {
                    VehicleManager.instance.clientUpdate();
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }

                if (this.UpdateChannelsRoamingLimit.Check()) {
                    VoiceManager.getInstance().UpdateChannelsRoaming(connection);
                }

                this.objectSyncReq.sendRequests(connection);
                this.worldObjectsSyncReq.sendRequests(connection);
                WorldStreamer.instance.updateMain();
                MPStatisticClient.getInstance().update();
                this.timeSinceKeepAlive = this.timeSinceKeepAlive + GameTime.getInstance().getMultiplier();
                ChatManager.UpdateClient();
            }
        } else {
            if (!this.bPlayerConnectSent) {
                for (int int4 = 0; int4 < MainLoopNetData.size(); int4++) {
                    ZomboidNetData zomboidNetData3 = MainLoopNetData.get(int4);
                    this.gameLoadingDealWithNetData(zomboidNetData3);
                }

                MainLoopNetData.clear();
            } else {
                for (int int5 = 0; int5 < MainLoopNetData.size(); int5++) {
                    ZomboidNetData zomboidNetData4 = MainLoopNetData.get(int5);
                    if (zomboidNetData4.type == PacketTypes.PacketType.Kicked) {
                        String string0 = Translator.getText(GameWindow.ReadString(zomboidNetData4.buffer));
                        String string1 = Translator.getText(GameWindow.ReadString(zomboidNetData4.buffer));
                        GameWindow.kickReason = string0 + " " + string1;
                        DebugLog.Multiplayer.warn("ReceiveKickedDisconnect: " + string1);
                    }
                }

                MainLoopNetData.clear();
            }

            GameWindow.bServerDisconnected = true;
        }
    }

    public void smashWindow(IsoWindow isoWindow, int action) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SmashWindow.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoWindow.square.getX());
        byteBufferWriter.putInt(isoWindow.square.getY());
        byteBufferWriter.putInt(isoWindow.square.getZ());
        byteBufferWriter.putByte((byte)isoWindow.square.getObjects().indexOf(isoWindow));
        byteBufferWriter.putByte((byte)action);
        PacketTypes.PacketType.SmashWindow.send(connection);
    }

    public static void getCustomModData() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.getModData.doPacket(byteBufferWriter);
        PacketTypes.PacketType.getModData.send(connection);
    }

    static void receiveStitch(ByteBuffer byteBuffer, short var1) {
        Stitch stitch = new Stitch();
        stitch.parse(byteBuffer, connection);
        if (stitch.isConsistent() && stitch.validate(connection)) {
            stitch.process();
        }
    }

    static void receiveBandage(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            int int0 = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;
            float float0 = byteBuffer.getFloat();
            boolean boolean1 = byteBuffer.get() == 1;
            String string = GameWindow.ReadStringUTF(byteBuffer);
            player.getBodyDamage().SetBandaged(int0, boolean0, float0, boolean1, string);
        }
    }

    static void receivePingFromClient(ByteBuffer byteBuffer, short var1) {
        MPStatistics.parse(byteBuffer);
    }

    @Deprecated
    static void receiveWoundInfection(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            int int0 = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;
            player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0)).setInfectedWound(boolean0);
        }
    }

    static void receiveDisinfect(ByteBuffer byteBuffer, short var1) {
        Disinfect disinfect = new Disinfect();
        disinfect.parse(byteBuffer, connection);
        if (disinfect.isConsistent() && disinfect.validate(connection)) {
            disinfect.process();
        }
    }

    static void receiveSplint(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            int int0 = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;
            String string = boolean0 ? GameWindow.ReadStringUTF(byteBuffer) : null;
            float float0 = boolean0 ? byteBuffer.getFloat() : 0.0F;
            BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0));
            bodyPart.setSplint(boolean0, float0);
            bodyPart.setSplintItem(string);
        }
    }

    static void receiveRemoveGlass(ByteBuffer byteBuffer, short var1) {
        RemoveGlass removeGlass = new RemoveGlass();
        removeGlass.parse(byteBuffer, connection);
        if (removeGlass.isConsistent() && removeGlass.validate(connection)) {
            removeGlass.process();
        }
    }

    static void receiveRemoveBullet(ByteBuffer byteBuffer, short var1) {
        RemoveBullet removeBullet = new RemoveBullet();
        removeBullet.parse(byteBuffer, connection);
        if (removeBullet.isConsistent() && removeBullet.validate(connection)) {
            removeBullet.process();
        }
    }

    static void receiveCleanBurn(ByteBuffer byteBuffer, short var1) {
        CleanBurn cleanBurn = new CleanBurn();
        cleanBurn.parse(byteBuffer, connection);
        if (cleanBurn.isConsistent() && cleanBurn.validate(connection)) {
            cleanBurn.process();
        }
    }

    @Deprecated
    static void receiveAdditionalPain(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            int int0 = byteBuffer.getInt();
            float float0 = byteBuffer.getFloat();
            BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0));
            bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float0);
        }
    }

    @Deprecated
    private void delayPacket(int int1, int int2, int var3) {
        if (IsoWorld.instance != null) {
            for (int int0 = 0; int0 < IsoWorld.instance.AddCoopPlayers.size(); int0++) {
                AddCoopPlayer addCoopPlayer = IsoWorld.instance.AddCoopPlayers.get(int0);
                if (addCoopPlayer.isLoadingThisSquare(int1, int2)) {
                    this.delayPacket = true;
                    return;
                }
            }
        }
    }

    private void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
        ByteBuffer byteBuffer = zomboidNetData.buffer;
        int int0 = byteBuffer.position();
        this.delayPacket = false;
        if (zomboidNetData.type == null) {
            ZomboidNetDataPool.instance.discard(zomboidNetData);
        } else {
            zomboidNetData.type.clientPacketCount++;

            try {
                this.mainLoopHandlePacketInternal(zomboidNetData, byteBuffer);
                if (this.delayPacket) {
                    byteBuffer.position(int0);
                    DelayedCoopNetData.add(zomboidNetData);
                    return;
                }
            } catch (Exception exception) {
                DebugLog.Network.printException(exception, "Error with packet of type: " + zomboidNetData.type, LogSeverity.Error);
            }

            ZomboidNetDataPool.instance.discard(zomboidNetData);
        }
    }

    private void mainLoopHandlePacketInternal(ZomboidNetData zomboidNetData, ByteBuffer byteBuffer) throws IOException {
        if (DebugOptions.instance.Network.Client.MainLoop.getValue()) {
            zomboidNetData.type.onMainLoopHandlePacketInternal(byteBuffer);
        }
    }

    static void receiveAddBrokenGlass(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null) {
            square.addBrokenGlass();
        }
    }

    static void receivePlayerDamageFromCarCrash(ByteBuffer byteBuffer, short var1) {
        float float0 = byteBuffer.getFloat();
        if (IsoPlayer.getInstance().getVehicle() == null) {
            DebugLog.Multiplayer.error("Receive damage from car crash, can't find vehicle");
        } else {
            IsoPlayer.getInstance().getVehicle().addRandomDamageFromCrash(IsoPlayer.getInstance(), float0);
            LuaEventManager.triggerEvent("OnPlayerGetDamage", IsoPlayer.getInstance(), "CARCRASHDAMAGE", float0);
        }
    }

    static void receivePacketCounts(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            short short0 = byteBuffer.getShort();
            long long0 = byteBuffer.getLong();
            PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(short0);
            if (packetType != null) {
                packetType.serverPacketCount = long0;
            }
        }
    }

    public void requestPacketCounts() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PacketCounts.doPacket(byteBufferWriter);
        PacketTypes.PacketType.PacketCounts.send(connection);
    }

    public static boolean IsClientPaused() {
        return isPaused;
    }

    static void receiveStartPause(ByteBuffer var0, short var1) {
        isPaused = true;
        LuaEventManager.triggerEvent("OnServerStartSaving");
    }

    static void receiveStopPause(ByteBuffer var0, short var1) {
        isPaused = false;
        LuaEventManager.triggerEvent("OnServerFinishSaving");
    }

    static void receiveChatMessageToPlayer(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processChatMessagePacket(byteBuffer);
    }

    static void receivePlayerConnectedToChat(ByteBuffer var0, short var1) {
        ChatManager.getInstance().setFullyConnected();
    }

    static void receivePlayerJoinChat(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processJoinChatPacket(byteBuffer);
    }

    static void receiveInvMngRemoveItem(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        InventoryItem item = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(int0);
        if (item == null) {
            DebugLog.log("ERROR: invMngRemoveItem can not find " + int0 + " item.");
        } else {
            IsoPlayer.getInstance().removeWornItem(item);
            if (item.getCategory().equals("Clothing")) {
                LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            }

            if (item == IsoPlayer.getInstance().getPrimaryHandItem()) {
                IsoPlayer.getInstance().setPrimaryHandItem(null);
                LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            } else if (item == IsoPlayer.getInstance().getSecondaryHandItem()) {
                IsoPlayer.getInstance().setSecondaryHandItem(null);
                LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            }

            boolean boolean0 = IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(int0);
            if (!boolean0) {
                DebugLog.log("ERROR: GameClient.invMngRemoveItem can not remove item " + int0);
            }
        }
    }

    static void receiveInvMngGetItem(ByteBuffer byteBuffer, short var1) throws IOException {
        short short0 = byteBuffer.getShort();
        InventoryItem item = null;

        try {
            item = InventoryItem.loadItem(byteBuffer, 195);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (item != null) {
            IsoPlayer.getInstance().getInventory().addItem(item);
        }
    }

    static void receiveInvMngReqItem(ByteBuffer byteBuffer, short var1) throws IOException {
        int int0 = 0;
        String string = null;
        if (byteBuffer.get() == 1) {
            string = GameWindow.ReadString(byteBuffer);
        } else {
            int0 = byteBuffer.getInt();
        }

        short short0 = byteBuffer.getShort();
        InventoryItem item = null;
        if (string == null) {
            item = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(int0);
            if (item == null) {
                DebugLog.log("ERROR: invMngRemoveItem can not find " + int0 + " item.");
                return;
            }
        } else {
            item = InventoryItemFactory.CreateItem(string);
        }

        if (item != null) {
            if (string == null) {
                IsoPlayer.getInstance().removeWornItem(item);
                if (item.getCategory().equals("Clothing")) {
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                }

                if (item == IsoPlayer.getInstance().getPrimaryHandItem()) {
                    IsoPlayer.getInstance().setPrimaryHandItem(null);
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                } else if (item == IsoPlayer.getInstance().getSecondaryHandItem()) {
                    IsoPlayer.getInstance().setSecondaryHandItem(null);
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                }

                IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(item.getID());
            } else {
                IsoPlayer.getInstance().getInventory().RemoveOneOf(string.split("\\.")[1]);
            }

            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.InvMngGetItem.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(short0);
            item.saveWithSize(byteBufferWriter.bb, false);
            PacketTypes.PacketType.InvMngGetItem.send(connection);
        }
    }

    public static void invMngRequestItem(int itemId, String itemType, IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.InvMngReqItem.doPacket(byteBufferWriter);
        if (itemType != null) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putUTF(itemType);
        } else {
            byteBufferWriter.putByte((byte)0);
            byteBufferWriter.putInt(itemId);
        }

        byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
        byteBufferWriter.putShort(player.getOnlineID());
        PacketTypes.PacketType.InvMngReqItem.send(connection);
    }

    public static void invMngRequestRemoveItem(int itemId, IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.InvMngRemoveItem.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(itemId);
        byteBufferWriter.putShort(player.getOnlineID());
        PacketTypes.PacketType.InvMngRemoveItem.send(connection);
    }

    static void receiveSyncFaction(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        Faction faction = Faction.getFaction(string0);
        if (faction == null) {
            faction = new Faction(string0, string1);
            Faction.getFactions().add(faction);
        }

        faction.getPlayers().clear();
        if (byteBuffer.get() == 1) {
            faction.setTag(GameWindow.ReadString(byteBuffer));
            faction.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
        }

        for (int int1 = 0; int1 < int0; int1++) {
            faction.getPlayers().add(GameWindow.ReadString(byteBuffer));
        }

        faction.setOwner(string1);
        boolean boolean0 = byteBuffer.get() == 1;
        if (boolean0) {
            Faction.getFactions().remove(faction);
            DebugLog.log("faction: removed " + string0 + " owner=" + faction.getOwner());
        }

        LuaEventManager.triggerEvent("SyncFaction", string0);
    }

    static void receiveSyncNonPvpZone(ByteBuffer byteBuffer, short var1) {
        try {
            SyncNonPvpZonePacket syncNonPvpZonePacket = new SyncNonPvpZonePacket();
            syncNonPvpZonePacket.parse(byteBuffer, connection);
            if (syncNonPvpZonePacket.isConsistent()) {
                syncNonPvpZonePacket.process();
                if (Core.bDebug) {
                    DebugLog.Multiplayer.debugln("ReceiveSyncNonPvpZone: %s", syncNonPvpZonePacket.getDescription());
                }
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveSyncNonPvpZone: failed", LogSeverity.Error);
        }
    }

    static void receiveChangeTextColor(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            float float0 = byteBuffer.getFloat();
            float float1 = byteBuffer.getFloat();
            float float2 = byteBuffer.getFloat();
            player.setSpeakColourInfo(new ColorInfo(float0, float1, float2, 1.0F));
        }
    }

    static void receivePlaySoundEveryPlayer(ByteBuffer byteBuffer, short var1) {
        String string = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        DebugLog.log(DebugType.Sound, "sound: received " + string + " at " + int0 + "," + int1 + "," + int2);
        if (!Core.SoundDisabled) {
            FMOD_STUDIO_EVENT_DESCRIPTION fmod_studio_event_description = FMODManager.instance.getEventDescription(string);
            if (fmod_studio_event_description == null) {
                return;
            }

            long long0 = javafmod.FMOD_Studio_System_CreateEventInstance(fmod_studio_event_description.address);
            if (long0 <= 0L) {
                return;
            }

            javafmod.FMOD_Studio_EventInstance_SetVolume(long0, Core.getInstance().getOptionAmbientVolume() / 20.0F);
            javafmod.FMOD_Studio_EventInstance3D(long0, int0, int1, int2);
            javafmod.FMOD_Studio_StartEvent(long0);
            javafmod.FMOD_Studio_ReleaseEventInstance(long0);
        }
    }

    static void receiveCataplasm(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            int int0 = byteBuffer.getInt();
            float float0 = byteBuffer.getFloat();
            float float1 = byteBuffer.getFloat();
            float float2 = byteBuffer.getFloat();
            if (float0 > 0.0F) {
                player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0)).setPlantainFactor(float0);
            }

            if (float1 > 0.0F) {
                player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0)).setComfreyFactor(float1);
            }

            if (float2 > 0.0F) {
                player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int0)).setGarlicFactor(float2);
            }
        }
    }

    static void receiveStopFire(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null) {
            square.stopFire();
        }
    }

    static void receiveAddAlarm(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        DebugLog.log(DebugType.Multiplayer, "ReceiveAlarm at [ " + int0 + " , " + int1 + " ]");
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, 0);
        if (square != null && square.getBuilding() != null && square.getBuilding().getDef() != null) {
            square.getBuilding().getDef().bAlarmed = true;
            AmbientStreamManager.instance.doAlarm(square.room.def);
        }
    }

    static void receiveAddExplosiveTrap(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null) {
            InventoryItem item = null;

            try {
                item = InventoryItem.loadItem(byteBuffer, 195);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            HandWeapon weapon = item != null ? (HandWeapon)item : null;
            IsoTrap trap = new IsoTrap(weapon, square.getCell(), square);
            square.AddTileObject(trap);
            trap.triggerExplosion(weapon.getSensorRange() > 0);
        }
    }

    static void receiveTeleport(ByteBuffer byteBuffer, short var1) {
        byte byte0 = byteBuffer.get();
        IsoPlayer player = IsoPlayer.players[byte0];
        if (player != null && !player.isDead()) {
            if (player.getVehicle() != null) {
                player.getVehicle().exit(player);
                LuaEventManager.triggerEvent("OnExitVehicle", player);
            }

            player.setX(byteBuffer.getFloat());
            player.setY(byteBuffer.getFloat());
            player.setZ(byteBuffer.getFloat());
            player.setLx(player.getX());
            player.setLy(player.getY());
            player.setLz(player.getZ());
        }
    }

    static void receiveRemoveBlood(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        boolean boolean0 = byteBuffer.get() == 1;
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null) {
            square.removeBlood(true, boolean0);
        }
    }

    static void receiveSyncThumpable(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (byte0 >= 0 && byte0 < square.getObjects().size()) {
                IsoObject object = square.getObjects().get(byte0);
                if (object instanceof IsoThumpable thumpable) {
                    thumpable.lockedByCode = byteBuffer.getInt();
                    thumpable.lockedByPadlock = byteBuffer.get() == 1;
                    thumpable.keyId = byteBuffer.getInt();
                } else {
                    DebugLog.log("syncThumpable: expected IsoThumpable index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
                }
            } else {
                DebugLog.log("syncThumpable: index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
            }
        }
    }

    static void receiveSyncDoorKey(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (byte0 >= 0 && byte0 < square.getObjects().size()) {
                IsoObject object = square.getObjects().get(byte0);
                if (object instanceof IsoDoor door) {
                    door.keyId = byteBuffer.getInt();
                } else {
                    DebugLog.log("SyncDoorKey: expected IsoDoor index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
                }
            } else {
                DebugLog.log("SyncDoorKey: index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
            }
        }
    }

    static void receiveConstructedZone(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(int0, int1, int2);
        if (zone != null) {
            zone.setHaveConstruction(true);
        }
    }

    static void receiveAddCoopPlayer(ByteBuffer byteBuffer, short var1) {
        boolean boolean0 = byteBuffer.get() == 1;
        byte byte0 = byteBuffer.get();
        if (boolean0) {
            for (int int0 = 0; int0 < IsoWorld.instance.AddCoopPlayers.size(); int0++) {
                IsoWorld.instance.AddCoopPlayers.get(int0).accessGranted(byte0);
            }
        } else {
            String string = GameWindow.ReadStringUTF(byteBuffer);

            for (int int1 = 0; int1 < IsoWorld.instance.AddCoopPlayers.size(); int1++) {
                IsoWorld.instance.AddCoopPlayers.get(int1).accessDenied(byte0, string);
            }
        }
    }

    static void receiveZombieDescriptors(ByteBuffer byteBuffer, short var1) {
        try {
            SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
            descriptor.load(byteBuffer, 195);
            SharedDescriptors.registerPlayerZombieDescriptor(descriptor);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void checksumServer() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(checksum + ScriptManager.instance.getChecksum());
        PacketTypes.PacketType.Checksum.send(connection);
    }

    static void receiveRegisterZone(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        int int4 = byteBuffer.getInt();
        int int5 = byteBuffer.getInt();
        ArrayList arrayList = IsoWorld.instance.getMetaGrid().getZonesAt(int0, int1, int2);
        boolean boolean0 = false;

        for (IsoMetaGrid.Zone zone : arrayList) {
            if (string1.equals(zone.getType())) {
                boolean0 = true;
                zone.setName(string0);
                zone.setLastActionTimestamp(int5);
            }
        }

        if (!boolean0) {
            IsoWorld.instance.getMetaGrid().registerZone(string0, string1, int0, int1, int2, int3, int4);
        }
    }

    static void receiveAddXpCommand(ByteBuffer byteBuffer, short var1) {
        IsoPlayer player = IDToPlayerMap.get(byteBuffer.getShort());
        PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
        if (player != null && !player.isDead()) {
            player.getXp().AddXP(perk, (float)byteBuffer.getInt());
        }
    }

    public void sendAddXp(IsoPlayer otherPlayer, PerkFactory.Perk perk, int amount) {
        AddXp addXp = new AddXp();
        addXp.set(otherPlayer, perk, amount);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddXP.doPacket(byteBufferWriter);
        addXp.write(byteBufferWriter);
        PacketTypes.PacketType.AddXP.send(connection);
    }

    static void receiveSyncXP(ByteBuffer byteBuffer, short var1) {
        IsoPlayer player = IDToPlayerMap.get(byteBuffer.getShort());
        if (player != null && !player.isDead()) {
            try {
                player.getXp().load(byteBuffer, 195);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public void sendSyncXp(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncXP.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(player.getOnlineID());

        try {
            player.getXp().save(byteBufferWriter.bb);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        PacketTypes.PacketType.SyncXP.send(connection);
    }

    public void sendTransactionID(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendTransactionID.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(player.getOnlineID());
        byteBufferWriter.putInt(player.getTransactionID());
        PacketTypes.PacketType.SendTransactionID.send(connection);
    }

    static void receiveUserlog(ByteBuffer byteBuffer, short var1) {
        ArrayList arrayList = new ArrayList();
        int int0 = byteBuffer.getInt();
        String string = GameWindow.ReadString(byteBuffer);

        for (int int1 = 0; int1 < int0; int1++) {
            arrayList.add(
                new Userlog(
                    string,
                    Userlog.UserlogType.fromIndex(byteBuffer.getInt()).toString(),
                    GameWindow.ReadString(byteBuffer),
                    GameWindow.ReadString(byteBuffer),
                    byteBuffer.getInt(),
                    GameWindow.ReadString(byteBuffer)
                )
            );
        }

        LuaEventManager.triggerEvent("OnReceiveUserlog", string, arrayList);
    }

    static void receiveAddXp(ByteBuffer byteBuffer, short var1) {
        AddXp addXp = new AddXp();
        addXp.parse(byteBuffer, connection);
        if (addXp.isConsistent()) {
            addXp.process();
        }
    }

    static void receivePing(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = byteBuffer.getInt() - 1 + "/" + byteBuffer.getInt();
        LuaEventManager.triggerEvent("ServerPinged", string0, string1);
        connection.forceDisconnect("receive-ping");
        askPing = false;
    }

    static void receiveChecksumLoading(ByteBuffer byteBuffer, short var1) {
        NetChecksum.comparer.clientPacket(byteBuffer);
    }

    static void receiveServerMapLoading(ByteBuffer byteBuffer, short var1) {
        ClientServerMap.receivePacket(byteBuffer);
    }

    static void receiveChangeSafety(ByteBuffer byteBuffer, short var1) {
        try {
            SafetyPacket safetyPacket = new SafetyPacket();
            safetyPacket.parse(byteBuffer, connection);
            safetyPacket.log(null, "ReceiveChangeSafety");
            safetyPacket.process();
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveChangeSafety: failed", LogSeverity.Error);
        }
    }

    public static void sendChangeSafety(Safety safety) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ChangeSafety.doPacket(byteBufferWriter);

        try {
            SafetyPacket safetyPacket = new SafetyPacket(safety);
            safetyPacket.write(byteBufferWriter);
            PacketTypes.PacketType.ChangeSafety.send(connection);
            safetyPacket.log(null, "SendChangeSafety");
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendChangeSafety: failed", LogSeverity.Error);
        }
    }

    static void receiveAddItemInInventory(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        String string = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null && !player.isDead()) {
            player.getInventory().AddItems(string, int0);
        }
    }

    static void receiveKicked(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        ConnectionManager.log("kick", string1, connection);
        String string2 = Translator.getText(string0);
        if (!StringUtils.isNullOrEmpty(string1)) {
            string2 = string2 + " " + Translator.getText("UI_ValidationFailed_" + string1);
        }

        if (GameWindow.states.current == IngameState.instance) {
            if (!StringUtils.isNullOrEmpty(string2)) {
                ChatManager.getInstance().showServerChatMessage(string2);
            }
        } else {
            LuaEventManager.triggerEvent("OnConnectFailed", string2);
        }

        connection.username = null;
        GameWindow.kickReason = string2;
        GameWindow.bServerDisconnected = true;
        connection.forceDisconnect("receive-kick");
        DebugLog.Multiplayer.warn("ReceiveKicked: " + string1);
    }

    public void addDisconnectPacket(int packet) {
        synchronized (this.delayedDisconnect) {
            this.delayedDisconnect.add(packet);
        }

        ConnectionManager.log("disconnect", String.valueOf(packet), null);
    }

    public void connectionLost() {
        this.bConnectionLost = true;
        positions.clear();
        WorldMapRemotePlayers.instance.Reset();
    }

    public static void SendCommandToServer(String command) {
        if (ServerOptions.clientOptionsList == null) {
            ServerOptions.initClientCommandsHelp();
        }

        if (command.startsWith("/roll")) {
            try {
                int int0 = Integer.parseInt(command.split(" ")[1]);
                if (int0 > 100) {
                    ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                    return;
                }
            } catch (Exception exception) {
                ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                return;
            }

            if (!IsoPlayer.getInstance().getInventory().contains("Dice") && connection.accessLevel == 1) {
                ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                return;
            }
        }

        if (command.startsWith("/card") && !IsoPlayer.getInstance().getInventory().contains("CardDeck") && connection.accessLevel == 1) {
            ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("card"));
        } else if (!command.startsWith("/log ")) {
            ByteBufferWriter byteBufferWriter0 = connection.startPacket();
            PacketTypes.PacketType.ReceiveCommand.doPacket(byteBufferWriter0);
            byteBufferWriter0.putUTF(command);
            PacketTypes.PacketType.ReceiveCommand.send(connection);
        } else {
            String string = ChatManager.getInstance().getFocusTab().getTitleID();
            if ("UI_chat_admin_tab_title_id".equals(string)) {
                ByteBufferWriter byteBufferWriter1 = connection.startPacket();
                PacketTypes.PacketType.ReceiveCommand.doPacket(byteBufferWriter1);
                byteBufferWriter1.putUTF(command);
                PacketTypes.PacketType.ReceiveCommand.send(connection);
            } else if ("UI_chat_main_tab_title_id".equals(string)) {
                String[] strings = command.split(" ");
                if (strings.length == 3) {
                    DebugType debugType = LogCommand.getDebugType(strings[1]);
                    LogSeverity logSeverity = LogCommand.getLogSeverity(strings[2]);
                    if (debugType != null && logSeverity != null) {
                        DebugLog.enableLog(debugType, logSeverity);
                        ChatManager.getInstance()
                            .showServerChatMessage(
                                String.format("Client \"%s\" log level is \"%s\"", debugType.name().toLowerCase(), logSeverity.name().toLowerCase())
                            );
                        if (DebugType.Network.equals(debugType)) {
                            ZNet.SetLogLevel(DebugLog.getLogLevel(DebugType.Network));
                        }
                    } else {
                        ChatManager.getInstance()
                            .showServerChatMessage(
                                Translator.getText(
                                    "UI_ServerOptionDesc_SetLogLevel",
                                    debugType == null ? "\"type\"" : debugType.name().toLowerCase(),
                                    logSeverity == null ? "\"severity\"" : logSeverity.name().toLowerCase()
                                )
                            );
                    }
                }
            }
        }
    }

    public static void sendServerPing(long timestamp) {
        if (connection != null) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.PingFromClient.doPacket(byteBufferWriter);
            byteBufferWriter.putLong(timestamp);
            PacketTypes.PacketType.PingFromClient.send(connection);
            if (timestamp == -1L) {
                DebugLog.Multiplayer.debugln("Player \"%s\" toggled lua debugger", connection.username);
            }
        }
    }

    private boolean gameLoadingDealWithNetData(ZomboidNetData zomboidNetData) {
        ByteBuffer byteBuffer = zomboidNetData.buffer;

        try {
            return zomboidNetData.type.onGameLoadingDealWithNetData(byteBuffer);
        } catch (Exception exception) {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
            exception.printStackTrace();
            ZomboidNetDataPool.instance.discard(zomboidNetData);
            return true;
        }
    }

    static void receiveWorldMessage(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadStringUTF(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        string1 = string1.replaceAll("<", "&lt;");
        string1 = string1.replaceAll(">", "&gt;");
        ChatManager.getInstance().addMessage(string0, string1);
    }

    static void receiveReloadOptions(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
        }
    }

    static void receiveStartRain(ByteBuffer byteBuffer, short var1) {
        RainManager.setRandRainMin(byteBuffer.getInt());
        RainManager.setRandRainMax(byteBuffer.getInt());
        RainManager.startRaining();
        RainManager.RainDesiredIntensity = byteBuffer.getFloat();
    }

    static void receiveStopRain(ByteBuffer var0, short var1) {
        RainManager.stopRaining();
    }

    static void receiveWeather(ByteBuffer byteBuffer, short var1) {
        GameTime gameTime = GameTime.getInstance();
        gameTime.setDawn(byteBuffer.get() & 255);
        gameTime.setDusk(byteBuffer.get() & 255);
        gameTime.setThunderDay(byteBuffer.get() == 1);
        gameTime.setMoon(byteBuffer.getFloat());
        gameTime.setAmbientMin(byteBuffer.getFloat());
        gameTime.setAmbientMax(byteBuffer.getFloat());
        gameTime.setViewDistMin(byteBuffer.getFloat());
        gameTime.setViewDistMax(byteBuffer.getFloat());
        IsoWorld.instance.setGlobalTemperature(byteBuffer.getFloat());
        IsoWorld.instance.setWeather(GameWindow.ReadStringUTF(byteBuffer));
        ErosionMain.getInstance().receiveState(byteBuffer);
    }

    static void receiveWorldMapPlayerPosition(ByteBuffer byteBuffer, short var1) {
        tempShortList.clear();
        boolean boolean0 = byteBuffer.get() == 1;
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            short short1 = byteBuffer.getShort();
            WorldMapRemotePlayer worldMapRemotePlayer = WorldMapRemotePlayers.instance.getOrCreatePlayerByID(short1);
            if (boolean0) {
                short short2 = byteBuffer.getShort();
                String string0 = GameWindow.ReadStringUTF(byteBuffer);
                String string1 = GameWindow.ReadStringUTF(byteBuffer);
                String string2 = GameWindow.ReadStringUTF(byteBuffer);
                String string3 = GameWindow.ReadStringUTF(byteBuffer);
                float float0 = byteBuffer.getFloat();
                float float1 = byteBuffer.getFloat();
                boolean boolean1 = byteBuffer.get() == 1;
                worldMapRemotePlayer.setFullData(short2, string0, string1, string2, string3, float0, float1, boolean1);
                if (positions.containsKey(short1)) {
                    positions.get(short1).set(float0, float1);
                } else {
                    positions.put(short1, new Vector2(float0, float1));
                }
            } else {
                short short3 = byteBuffer.getShort();
                float float2 = byteBuffer.getFloat();
                float float3 = byteBuffer.getFloat();
                if (worldMapRemotePlayer.getChangeCount() != short3) {
                    tempShortList.add(short1);
                } else {
                    worldMapRemotePlayer.setPosition(float2, float3);
                    if (positions.containsKey(short1)) {
                        positions.get(short1).set(float2, float3);
                    } else {
                        positions.put(short1, new Vector2(float2, float3));
                    }
                }
            }
        }

        if (!tempShortList.isEmpty()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.WorldMapPlayerPosition.doPacket(byteBufferWriter);
            byteBufferWriter.putShort((short)tempShortList.size());

            for (int int1 = 0; int1 < tempShortList.size(); int1++) {
                byteBufferWriter.putShort(tempShortList.get(int1));
            }

            PacketTypes.PacketType.WorldMapPlayerPosition.send(connection);
        }
    }

    static void receiveSyncClock(ByteBuffer byteBuffer, short var1) {
        GameTime gameTime = GameTime.getInstance();
        boolean boolean0 = bFastForward;
        bFastForward = byteBuffer.get() == 1;
        float float0 = byteBuffer.getFloat();
        int int0 = byteBuffer.getInt();
        float float1 = gameTime.getTimeOfDay() - gameTime.getLastTimeOfDay();
        gameTime.setTimeOfDay(float0);
        gameTime.setLastTimeOfDay(float0 - float1);
        if (gameTime.getLastTimeOfDay() < 0.0F) {
            gameTime.setLastTimeOfDay(float0 - float1 + 24.0F);
        }

        gameTime.ServerLastTimeOfDay = gameTime.ServerTimeOfDay;
        gameTime.ServerTimeOfDay = float0;
        gameTime.setNightsSurvived(int0);
        if (gameTime.ServerLastTimeOfDay > gameTime.ServerTimeOfDay) {
            gameTime.ServerNewDays++;
        }
    }

    static void receiveClientCommand(ByteBuffer byteBuffer, short var1) {
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

    static void receiveGlobalObjects(ByteBuffer byteBuffer, short var1) throws IOException {
        CGlobalObjectNetwork.receive(byteBuffer);
    }

    public void setRequest(GameClient.RequestState _request) {
        this.request = _request;
    }

    static void receiveRequestData(ByteBuffer byteBuffer, short var1) {
        RequestDataPacket requestDataPacket = new RequestDataPacket();
        requestDataPacket.parse(byteBuffer, connection);
        if (requestDataPacket.isConsistent()) {
            requestDataPacket.process(connection);
        }
    }

    public void GameLoadingRequestData() {
        RequestDataPacket requestDataPacket = new RequestDataPacket();
        this.request = GameClient.RequestState.Start;

        while (this.request != GameClient.RequestState.Complete) {
            if (this.request == GameClient.RequestState.Start) {
                requestDataPacket.setRequest();
                ByteBufferWriter byteBufferWriter = connection.startPacket();
                PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
                requestDataPacket.write(byteBufferWriter);
                PacketTypes.PacketType.RequestData.send(connection);
                this.request = GameClient.RequestState.Loading;
            }

            try {
                Thread.sleep(30L);
            } catch (InterruptedException interruptedException) {
                DebugLog.Multiplayer.printException(interruptedException, "GameLoadingRequestData sleep error", LogSeverity.Error);
            }
        }
    }

    static void receiveMetaGrid(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        short short2 = byteBuffer.getShort();
        IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        if (short0 >= metaGrid.getMinX() && short0 <= metaGrid.getMaxX() && short1 >= metaGrid.getMinY() && short1 <= metaGrid.getMaxY()) {
            IsoMetaCell metaCell = metaGrid.getCellData(short0, short1);
            if (metaCell.info != null && short2 >= 0 && short2 < metaCell.info.RoomList.size()) {
                metaCell.info.getRoom(short2).def.bLightsActive = byteBuffer.get() == 1;
            }
        }
    }

    static void receiveSendCustomColor(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (square != null && int3 < square.getObjects().size()) {
                IsoObject object = square.getObjects().get(int3);
                if (object != null) {
                    object.setCustomColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()));
                }
            }
        }
    }

    static void receiveUpdateItemSprite(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        int int4 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (square == null) {
            instance.delayPacket(int1, int2, int3);
        } else {
            if (square != null && int4 < square.getObjects().size()) {
                try {
                    IsoObject object = square.getObjects().get(int4);
                    if (object != null) {
                        boolean boolean0 = object.sprite != null
                            && object.sprite.getProperties().Is("HitByCar")
                            && object.sprite.getProperties().Val("DamagedSprite") != null
                            && !object.sprite.getProperties().Val("DamagedSprite").isEmpty();
                        object.sprite = IsoSpriteManager.instance.getSprite(int0);
                        if (object.sprite == null && !string.isEmpty()) {
                            object.setSprite(string);
                        }

                        object.RemoveAttachedAnims();
                        int int5 = byteBuffer.get() & 255;

                        for (int int6 = 0; int6 < int5; int6++) {
                            int int7 = byteBuffer.getInt();
                            IsoSprite sprite = IsoSpriteManager.instance.getSprite(int7);
                            if (sprite != null) {
                                object.AttachExistingAnim(sprite, 0, 0, false, 0, false, 0.0F);
                            }
                        }

                        if (object instanceof IsoThumpable && boolean0 && (object.sprite == null || !object.sprite.getProperties().Is("HitByCar"))) {
                            ((IsoThumpable)object).setBlockAllTheSquare(false);
                        }

                        square.RecalcAllWithNeighbours(true);
                    }
                } catch (Exception exception) {
                }
            }
        }
    }

    static void receiveUpdateOverlaySprite(ByteBuffer byteBuffer, short var1) {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        float float2 = byteBuffer.getFloat();
        float float3 = byteBuffer.getFloat();
        int int3 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (square != null && int3 < square.getObjects().size()) {
                try {
                    IsoObject object = square.getObjects().get(int3);
                    if (object != null) {
                        object.setOverlaySprite(string, float0, float1, float2, float3, false);
                    }
                } catch (Exception exception) {
                }
            }
        }
    }

    private KahluaTable copyTable(KahluaTable table1) {
        KahluaTable table0 = LuaManager.platform.newTable();
        KahluaTableIterator kahluaTableIterator = table1.iterator();

        while (kahluaTableIterator.advance()) {
            Object object0 = kahluaTableIterator.getKey();
            Object object1 = kahluaTableIterator.getValue();
            if (object1 instanceof KahluaTable) {
                table0.rawset(object0, this.copyTable((KahluaTable)object1));
            } else {
                table0.rawset(object0, object1);
            }
        }

        return table0;
    }

    public KahluaTable getServerSpawnRegions() {
        return this.copyTable(this.ServerSpawnRegions);
    }

    static void receiveStartFire(ByteBuffer byteBuffer, short var1) {
        StartFire startFire = new StartFire();
        startFire.parse(byteBuffer, connection);
        if (startFire.isConsistent() && startFire.validate(connection)) {
            startFire.process();
        }
    }

    static void receiveBecomeCorpse(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        float float0 = byteBuffer.getFloat();
        byte byte0 = byteBuffer.get();
        IsoGameCharacter character = null;
        if (byte0 == 1) {
            character = IDToZombieMap.get(short1);
        } else if (byte0 == 2) {
            character = IDToPlayerMap.get(short1);
        }

        if (character != null) {
            IsoDeadBody deadBody = new IsoDeadBody(character);
            deadBody.setObjectID(short0);
            deadBody.setOnlineID(short1);
            deadBody.setReanimateTime(float0);
            IsoDeadBody.addDeadBodyID(short0, deadBody);
        }
    }

    static void receiveAddCorpseToMap(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
        object.loadFromRemoteBuffer(byteBuffer, false);
        ((IsoDeadBody)object).setObjectID(short0);
        ((IsoDeadBody)object).setOnlineID(short1);
        IsoDeadBody.addDeadBodyID(short0, (IsoDeadBody)object);
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            square.addCorpse((IsoDeadBody)object, true);
        }
    }

    static void receiveReceiveModData(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null
            && IsoWorld.instance.isValidSquare(int0, int1, int2)
            && IsoWorld.instance.CurrentCell.getChunkForGridSquare(int0, int1, int2) != null) {
            square = IsoGridSquare.getNew(IsoWorld.instance.getCell(), null, int0, int1, int2);
        }

        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            try {
                square.getModData().load(byteBuffer, 195);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            LuaEventManager.triggerEvent("onLoadModDataFromServer", square);
        }
    }

    static void receiveObjectModData(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        boolean boolean0 = byteBuffer.get() == 1;
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (square != null && int3 >= 0 && int3 < square.getObjects().size()) {
                IsoObject object = square.getObjects().get(int3);
                if (boolean0) {
                    try {
                        object.getModData().load(byteBuffer, 195);
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                } else {
                    object.getModData().wipe();
                }
            } else if (square != null) {
                DebugLog.log("receiveObjectModData: index=" + int3 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectModData: sq is null x,y,z=" + int0 + "," + int1 + "," + int2);
            }
        }
    }

    static void receiveObjectChange(ByteBuffer byteBuffer, short var1) {
        byte byte0 = byteBuffer.get();
        if (byte0 == 1) {
            short short0 = byteBuffer.getShort();
            String string0 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string0);
            }

            IsoPlayer player = IDToPlayerMap.get(short0);
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
                instance.delayPacket(int0, int1, int2);
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
        } else if (byte0 == 4) {
            int int5 = byteBuffer.getInt();
            int int6 = byteBuffer.getInt();
            int int7 = byteBuffer.getInt();
            int int8 = byteBuffer.getInt();
            String string3 = GameWindow.ReadString(byteBuffer);
            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int7);
            if (square1 == null) {
                instance.delayPacket(int5, int6, int7);
                return;
            }

            if (int8 >= 0 && int8 < square1.getStaticMovingObjects().size()) {
                IsoObject object0 = square1.getStaticMovingObjects().get(int8);
                object0.loadChange(string3, byteBuffer);
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: index=" + int8 + " is invalid x,y,z=" + int5 + "," + int6 + "," + int7);
            }
        } else {
            int int9 = byteBuffer.getInt();
            int int10 = byteBuffer.getInt();
            int int11 = byteBuffer.getInt();
            int int12 = byteBuffer.getInt();
            String string4 = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string4);
            }

            IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int9, int10, int11);
            if (square2 == null) {
                instance.delayPacket(int9, int10, int11);
                return;
            }

            if (square2 != null && int12 >= 0 && int12 < square2.getObjects().size()) {
                IsoObject object1 = square2.getObjects().get(int12);
                object1.loadChange(string4, byteBuffer);
            } else if (square2 != null) {
                if (Core.bDebug) {
                    DebugLog.log("receiveObjectChange: index=" + int12 + " is invalid x,y,z=" + int9 + "," + int10 + "," + int11);
                }
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: sq is null x,y,z=" + int9 + "," + int10 + "," + int11);
            }
        }
    }

    static void receiveKeepAlive(ByteBuffer byteBuffer, short var1) {
        MPDebugInfo.instance.clientPacket(byteBuffer);
    }

    static void receiveSmashWindow(ByteBuffer byteBuffer, short var1) {
        IsoObject object = instance.getIsoObjectRefFromByteBuffer(byteBuffer);
        if (object instanceof IsoWindow) {
            byte byte0 = byteBuffer.get();
            if (byte0 == 1) {
                ((IsoWindow)object).smashWindow(true);
            } else if (byte0 == 2) {
                ((IsoWindow)object).setGlassRemoved(true);
            }
        } else if (Core.bDebug) {
            DebugLog.log("SmashWindow not a window!");
        }
    }

    static void receiveRemoveContestedItemsFromInventory(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = byteBuffer.getInt();

            for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                IsoPlayer player = IsoPlayer.players[int3];
                if (player != null && !player.isDead()) {
                    player.getInventory().removeItemWithIDRecurse(int2);
                }
            }
        }
    }

    static void receiveServerQuit(ByteBuffer var0, short var1) {
        GameWindow.kickReason = "Server shut down safely. Players and map data saved.";
        GameWindow.bServerDisconnected = true;
        ConnectionManager.log("receive-packet", "server-quit", null);
    }

    static void receiveHitCharacter(ByteBuffer byteBuffer, short var1) {
        try {
            HitCharacterPacket hitCharacterPacket = HitCharacterPacket.process(byteBuffer);
            if (hitCharacterPacket != null) {
                hitCharacterPacket.parse(byteBuffer, connection);
                if (hitCharacterPacket.isConsistent()) {
                    DebugLog.Damage.trace(hitCharacterPacket.getDescription());
                    hitCharacterPacket.tryProcess();
                }
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveHitCharacter: failed", LogSeverity.Error);
        }
    }

    public static boolean sendHitCharacter(
        IsoGameCharacter wielder,
        IsoMovingObject target,
        HandWeapon weapon,
        float damage,
        boolean ignoreDamage,
        float range,
        boolean isCriticalHit,
        boolean helmetFall,
        boolean hitHead
    ) {
        boolean boolean0 = false;
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);

        try {
            Object object = null;
            if (wielder instanceof IsoZombie) {
                if (target instanceof IsoPlayer) {
                    boolean boolean1 = ((IsoPlayer)target).isLocalPlayer();
                    boolean boolean2 = !((IsoZombie)wielder).isRemoteZombie();
                    if (boolean2 && boolean1) {
                        ZombieHitPlayerPacket zombieHitPlayerPacket = new ZombieHitPlayerPacket();
                        zombieHitPlayerPacket.set((IsoZombie)wielder, (IsoPlayer)target);
                        object = zombieHitPlayerPacket;
                    } else {
                        DebugLog.Multiplayer
                            .warn(String.format("SendHitCharacter: Wielder or target is not local (wielder=%b, target=%b)", boolean2, boolean1));
                    }
                } else {
                    DebugLog.Multiplayer
                        .warn(
                            String.format(
                                "SendHitCharacter: unknown target type (wielder=%s, target=%s)", wielder.getClass().getName(), target.getClass().getName()
                            )
                        );
                }
            } else if (wielder instanceof IsoPlayer) {
                if (target == null) {
                    PlayerHitSquarePacket playerHitSquarePacket = new PlayerHitSquarePacket();
                    playerHitSquarePacket.set((IsoPlayer)wielder, weapon, isCriticalHit);
                    object = playerHitSquarePacket;
                } else if (target instanceof IsoPlayer) {
                    PlayerHitPlayerPacket playerHitPlayerPacket = new PlayerHitPlayerPacket();
                    playerHitPlayerPacket.set((IsoPlayer)wielder, (IsoPlayer)target, weapon, damage, ignoreDamage, range, isCriticalHit, hitHead);
                    object = playerHitPlayerPacket;
                } else if (target instanceof IsoZombie) {
                    PlayerHitZombiePacket playerHitZombiePacket = new PlayerHitZombiePacket();
                    playerHitZombiePacket.set((IsoPlayer)wielder, (IsoZombie)target, weapon, damage, ignoreDamage, range, isCriticalHit, helmetFall, hitHead);
                    object = playerHitZombiePacket;
                } else if (target instanceof BaseVehicle) {
                    PlayerHitVehiclePacket playerHitVehiclePacket = new PlayerHitVehiclePacket();
                    playerHitVehiclePacket.set((IsoPlayer)wielder, (BaseVehicle)target, weapon, isCriticalHit);
                    object = playerHitVehiclePacket;
                } else {
                    DebugLog.Multiplayer
                        .warn(
                            String.format(
                                "SendHitCharacter: unknown target type (wielder=%s, target=%s)", wielder.getClass().getName(), target.getClass().getName()
                            )
                        );
                }
            } else {
                DebugLog.Multiplayer
                    .warn(
                        String.format(
                            "SendHitCharacter: unknown wielder type (wielder=%s, target=%s)", wielder.getClass().getName(), target.getClass().getName()
                        )
                    );
            }

            if (object != null) {
                ((HitCharacterPacket)object).write(byteBufferWriter);
                PacketTypes.PacketType.HitCharacter.send(connection);
                DebugLog.Damage.trace(((HitCharacterPacket)object).getDescription());
                boolean0 = true;
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendHitCharacter: failed", LogSeverity.Error);
        }

        return boolean0;
    }

    public static void sendHitVehicle(
        IsoPlayer wielder,
        IsoGameCharacter target,
        BaseVehicle vehicle,
        float damage,
        boolean isTargetHitFromBehind,
        int vehicleDamage,
        float vehicleSpeed,
        boolean isVehicleHitFromBehind
    ) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);

        try {
            Object object = null;
            if (target instanceof IsoPlayer) {
                VehicleHitPlayerPacket vehicleHitPlayerPacket = new VehicleHitPlayerPacket();
                vehicleHitPlayerPacket.set(
                    wielder, (IsoPlayer)target, vehicle, damage, isTargetHitFromBehind, vehicleDamage, vehicleSpeed, isVehicleHitFromBehind
                );
                object = vehicleHitPlayerPacket;
            } else if (target instanceof IsoZombie) {
                VehicleHitZombiePacket vehicleHitZombiePacket = new VehicleHitZombiePacket();
                vehicleHitZombiePacket.set(
                    wielder, (IsoZombie)target, vehicle, damage, isTargetHitFromBehind, vehicleDamage, vehicleSpeed, isVehicleHitFromBehind
                );
                object = vehicleHitZombiePacket;
            } else {
                DebugLog.Multiplayer
                    .warn(
                        String.format("SendHitVehicle: unknown target type (wielder=%s, target=%s)", wielder.getClass().getName(), target.getClass().getName())
                    );
            }

            if (object != null) {
                ((VehicleHitPacket)object).write(byteBufferWriter);
                PacketTypes.PacketType.HitCharacter.send(connection);
                DebugLog.Damage.trace(((VehicleHitPacket)object).getDescription());
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendHitVehicle: failed", LogSeverity.Error);
        }
    }

    static void receiveZombieDeath(ByteBuffer byteBuffer, short var1) {
        try {
            DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.parse(byteBuffer, connection);
            if (Core.bDebug) {
                DebugLog.Multiplayer.debugln("ReceiveZombieDeath: %s", deadZombiePacket.getDescription());
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveZombieDeath: failed", LogSeverity.Error);
        }
    }

    public static void sendZombieDeath(IsoZombie zombie) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ZombieDeath.doPacket(byteBufferWriter);

        try {
            DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.set(zombie);
            deadZombiePacket.write(byteBufferWriter);
            PacketTypes.PacketType.ZombieDeath.send(connection);
            if (Core.bDebug) {
                DebugLog.Multiplayer.debugln("SendZombieDeath: %s", deadZombiePacket.getDescription());
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendZombieDeath: failed", LogSeverity.Error);
        }
    }

    static void receivePlayerDeath(ByteBuffer byteBuffer, short var1) {
        try {
            DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
            deadPlayerPacket.parse(byteBuffer, connection);
            if (Core.bDebug) {
                DebugLog.Multiplayer.debugln("ReceivePlayerDeath: %s", deadPlayerPacket.getDeathDescription());
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerDeath: failed", LogSeverity.Error);
        }
    }

    public static void sendPlayerDeath(IsoPlayer player) {
        player.setTransactionID(0);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerDeath.doPacket(byteBufferWriter);

        try {
            DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
            deadPlayerPacket.set(player);
            deadPlayerPacket.write(byteBufferWriter);
            PacketTypes.PacketType.PlayerDeath.send(connection);
            if (Core.bDebug) {
                DebugLog.Multiplayer.debugln("SendPlayerDeath: %s", deadPlayerPacket.getDeathDescription());
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerDeath: failed", LogSeverity.Error);
        }
    }

    static void receivePlayerDamage(ByteBuffer byteBuffer, short var1) {
        try {
            short short0 = byteBuffer.getShort();
            float float0 = byteBuffer.getFloat();
            IsoPlayer player = IDToPlayerMap.get(short0);
            if (player != null) {
                player.getBodyDamage().load(byteBuffer, IsoWorld.getWorldVersion());
                player.getStats().setPain(float0);
                if (Core.bDebug) {
                    DebugLog.Multiplayer.debugln("ReceivePlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
                }
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerDamage: failed", LogSeverity.Error);
        }
    }

    public static void sendPlayerDamage(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerDamage.doPacket(byteBufferWriter);

        try {
            byteBufferWriter.putShort((short)player.getPlayerNum());
            byteBufferWriter.putFloat(player.getStats().getPain());
            player.getBodyDamage().save(byteBufferWriter.bb);
            PacketTypes.PacketType.PlayerDamage.send(connection);
            if (Core.bDebug) {
                DebugLog.Multiplayer.debugln("SendPlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerDamage: failed", LogSeverity.Error);
        }
    }

    static void receiveSyncInjuries(ByteBuffer byteBuffer, short var1) {
        try {
            SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
            syncInjuriesPacket.parse(byteBuffer, connection);
            DebugLog.Damage.trace(syncInjuriesPacket.getDescription());
            syncInjuriesPacket.process();
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerInjuries: failed", LogSeverity.Error);
        }
    }

    public static void sendPlayerInjuries(IsoPlayer player) {
        SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
        syncInjuriesPacket.set(player);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncInjuries.doPacket(byteBufferWriter);

        try {
            syncInjuriesPacket.write(byteBufferWriter);
            PacketTypes.PacketType.SyncInjuries.send(connection);
            DebugLog.Damage.trace(syncInjuriesPacket.getDescription());
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerInjuries: failed", LogSeverity.Error);
        }
    }

    static void receiveRemoveCorpseFromMap(ByteBuffer byteBuffer, short var1) {
        RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
        removeCorpseFromMap.parse(byteBuffer, connection);
        if (removeCorpseFromMap.isConsistent()) {
            removeCorpseFromMap.process();
        }
    }

    public static void sendRemoveCorpseFromMap(IsoDeadBody deadBody) {
        RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
        removeCorpseFromMap.set(deadBody);
        DebugLog.Death.trace(removeCorpseFromMap.getDescription());
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveCorpseFromMap.doPacket(byteBufferWriter);
        removeCorpseFromMap.write(byteBufferWriter);
        PacketTypes.PacketType.RemoveCorpseFromMap.send(connection);
    }

    public static void sendEvent(IsoPlayer isoPlayer, String event) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EventPacket.doPacket(byteBufferWriter);

        try {
            EventPacket eventPacket = new EventPacket();
            if (eventPacket.set(isoPlayer, event)) {
                eventPacket.write(byteBufferWriter);
                PacketTypes.PacketType.EventPacket.send(connection);
            } else {
                connection.cancelPacket();
            }
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendEvent: failed", LogSeverity.Error);
        }
    }

    static void receiveEventPacket(ByteBuffer byteBuffer, short var1) {
        try {
            EventPacket eventPacket = new EventPacket();
            eventPacket.parse(byteBuffer, connection);
            eventPacket.tryProcess();
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveEvent: failed", LogSeverity.Error);
        }
    }

    public static void sendAction(BaseAction action, boolean operation) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ActionPacket.doPacket(byteBufferWriter);

        try {
            ActionPacket actionPacket = new ActionPacket();
            actionPacket.set(operation, action);
            actionPacket.write(byteBufferWriter);
            PacketTypes.PacketType.ActionPacket.send(connection);
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendAction: failed", LogSeverity.Error);
        }
    }

    static void receiveActionPacket(ByteBuffer byteBuffer, short var1) {
        try {
            ActionPacket actionPacket = new ActionPacket();
            actionPacket.parse(byteBuffer, connection);
            actionPacket.process();
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveAction: failed", LogSeverity.Error);
        }
    }

    public static void sendEatBody(IsoZombie zombie, IsoMovingObject target) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EatBody.doPacket(byteBufferWriter);

        try {
            byteBufferWriter.putShort(zombie.getOnlineID());
            if (target instanceof IsoDeadBody deadBody) {
                byteBufferWriter.putByte((byte)1);
                byteBufferWriter.putBoolean(zombie.getVariableBoolean("onknees"));
                byteBufferWriter.putFloat(zombie.getEatSpeed());
                byteBufferWriter.putFloat(zombie.getStateEventDelayTimer());
                byteBufferWriter.putInt(deadBody.getStaticMovingObjectIndex());
                byteBufferWriter.putFloat(deadBody.getSquare().getX());
                byteBufferWriter.putFloat(deadBody.getSquare().getY());
                byteBufferWriter.putFloat(deadBody.getSquare().getZ());
            } else if (target instanceof IsoPlayer) {
                byteBufferWriter.putByte((byte)2);
                byteBufferWriter.putBoolean(zombie.getVariableBoolean("onknees"));
                byteBufferWriter.putFloat(zombie.getEatSpeed());
                byteBufferWriter.putFloat(zombie.getStateEventDelayTimer());
                byteBufferWriter.putShort(((IsoPlayer)target).getOnlineID());
            } else {
                byteBufferWriter.putByte((byte)0);
            }

            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SendEatBody");
            }

            PacketTypes.PacketType.EatBody.send(connection);
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "SendEatBody: failed", LogSeverity.Error);
            connection.cancelPacket();
        }
    }

    public static void receiveEatBody(ByteBuffer bb, short packetType) {
        try {
            short short0 = bb.getShort();
            byte byte0 = bb.get();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("ReceiveEatBody: zombie=%d type=%d", short0, byte0));
            }

            IsoZombie zombie0 = IDToZombieMap.get(short0);
            if (zombie0 == null) {
                DebugLog.Multiplayer.error("ReceiveEatBody: zombie " + short0 + " not found");
                return;
            }

            if (byte0 == 1) {
                boolean boolean0 = bb.get() != 0;
                float float0 = bb.getFloat();
                float float1 = bb.getFloat();
                int int0 = bb.getInt();
                float float2 = bb.getFloat();
                float float3 = bb.getFloat();
                float float4 = bb.getFloat();
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float3, (double)float4);
                if (square == null) {
                    DebugLog.Multiplayer.error("ReceiveEatBody: incorrect square");
                    return;
                }

                if (int0 >= 0 && int0 < square.getStaticMovingObjects().size()) {
                    IsoDeadBody deadBody = (IsoDeadBody)square.getStaticMovingObjects().get(int0);
                    if (deadBody != null) {
                        zombie0.setTarget(null);
                        zombie0.setEatBodyTarget(deadBody, true, float0);
                        zombie0.setVariable("onknees", boolean0);
                        zombie0.setStateEventDelayTimer(float1);
                    } else {
                        DebugLog.Multiplayer.error("ReceiveEatBody: no corpse with index " + int0 + " on square");
                    }
                } else {
                    DebugLog.Multiplayer.error("ReceiveEatBody: no corpse on square");
                }
            } else if (byte0 == 2) {
                boolean boolean1 = bb.get() != 0;
                float float5 = bb.getFloat();
                float float6 = bb.getFloat();
                short short1 = bb.getShort();
                IsoPlayer player = IDToPlayerMap.get(short1);
                if (player == null) {
                    DebugLog.Multiplayer.error("ReceiveEatBody: player " + short1 + " not found");
                    return;
                }

                zombie0.setTarget(null);
                zombie0.setEatBodyTarget(player, true, float5);
                zombie0.setVariable("onknees", boolean1);
                zombie0.setStateEventDelayTimer(float6);
            } else {
                zombie0.setEatBodyTarget(null, false);
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveEatBody: failed", LogSeverity.Error);
        }
    }

    public static void sendThump(IsoGameCharacter zombie, Thumpable thumpable) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Thump.doPacket(byteBufferWriter);

        try {
            short short0 = zombie.getOnlineID();
            String string = zombie.getVariableString("ThumpType");
            byteBufferWriter.putShort(short0);
            byteBufferWriter.putByte((byte)NetworkVariables.ThumpType.fromString(string).ordinal());
            if (thumpable instanceof IsoObject object) {
                byteBufferWriter.putInt(object.getObjectIndex());
                byteBufferWriter.putFloat(object.getSquare().getX());
                byteBufferWriter.putFloat(object.getSquare().getY());
                byteBufferWriter.putFloat(object.getSquare().getZ());
            } else {
                byteBufferWriter.putInt(-1);
            }

            if (Core.bDebug) {
                DebugLog.log(
                    DebugType.Multiplayer,
                    String.format("SendThump: zombie=%d type=%s target=%s", short0, string, thumpable == null ? "null" : thumpable.getClass().getSimpleName())
                );
            }

            PacketTypes.PacketType.Thump.send(connection);
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "SendThump: failed", LogSeverity.Error);
            connection.cancelPacket();
        }
    }

    public static void receiveSyncRadioData(ByteBuffer bb, short packetType) {
        short short0 = bb.getShort();
        VoiceManagerData voiceManagerData = VoiceManagerData.get(short0);
        synchronized (voiceManagerData.radioData) {
            voiceManagerData.isCanHearAll = bb.get() == 1;
            short short1 = (short)bb.getInt();
            voiceManagerData.radioData.clear();

            for (int int0 = 0; int0 < short1 / 4; int0++) {
                int int1 = bb.getInt();
                int int2 = bb.getInt();
                int int3 = bb.getInt();
                int int4 = bb.getInt();
                voiceManagerData.radioData.add(new VoiceManagerData.RadioData(int1, int2, int3, int4));
            }
        }
    }

    public static void receiveThump(ByteBuffer bb, short packetType) {
        try {
            short short0 = bb.getShort();
            String string = NetworkVariables.ThumpType.fromByte(bb.get()).toString();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("ReceiveThump: zombie=%d type=%s", short0, string));
            }

            IsoZombie zombie0 = IDToZombieMap.get(short0);
            if (zombie0 == null) {
                DebugLog.Multiplayer.error("ReceiveThump: zombie " + short0 + " not found");
                return;
            }

            zombie0.setVariable("ThumpType", string);
            int int0 = bb.getInt();
            if (int0 == -1) {
                zombie0.setThumpTarget(null);
                return;
            }

            float float0 = bb.getFloat();
            float float1 = bb.getFloat();
            float float2 = bb.getFloat();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float0, (double)float1, (double)float2);
            if (square == null) {
                DebugLog.Multiplayer.error("ReceiveThump: incorrect square");
                return;
            }

            IsoObject object = square.getObjects().get(int0);
            if (object instanceof Thumpable) {
                zombie0.setThumpTarget(object);
            } else {
                DebugLog.Multiplayer.error("ReceiveThump: no thumpable with index " + int0 + " on square");
            }
        } catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveThump: failed", LogSeverity.Error);
        }
    }

    public void sendWorldSound(WorldSoundManager.WorldSound sound) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(sound.x);
        byteBufferWriter.putInt(sound.y);
        byteBufferWriter.putInt(sound.z);
        byteBufferWriter.putInt(sound.radius);
        byteBufferWriter.putInt(sound.volume);
        byteBufferWriter.putByte((byte)(sound.stresshumans ? 1 : 0));
        byteBufferWriter.putFloat(sound.zombieIgnoreDist);
        byteBufferWriter.putFloat(sound.stressMod);
        byteBufferWriter.putByte((byte)(sound.sourceIsZombie ? 1 : 0));
        PacketTypes.PacketType.WorldSound.send(connection);
    }

    static void receiveRemoveItemFromSquare(ByteBuffer byteBuffer, short var1) {
        if (IsoWorld.instance.CurrentCell != null) {
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            int int3 = byteBuffer.getInt();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (square == null) {
                instance.delayPacket(int0, int1, int2);
            } else {
                if (square != null && int3 >= 0 && int3 < square.getObjects().size()) {
                    IsoObject object = square.getObjects().get(int3);
                    square.RemoveTileObject(object);
                    if (object instanceof IsoWorldInventoryObject || object.getContainer() != null) {
                        LuaEventManager.triggerEvent("OnContainerUpdate", object);
                    }
                } else if (Core.bDebug) {
                    DebugLog.log("RemoveItemFromMap: sq is null or index is invalid");
                }
            }
        }
    }

    static void receiveLoadPlayerProfile(ByteBuffer byteBuffer, short var1) {
        ClientPlayerDB.getInstance().clientLoadNetworkCharacter(byteBuffer, connection);
    }

    public void sendLoginQueueRequest2() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
        PacketTypes.PacketType.LoginQueueRequest2.send(connection);
        ConnectionManager.log("send-packet", "login-queue-request", connection);
    }

    public void sendLoginQueueDone2(long dt) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.LoginQueueDone2.doPacket(byteBufferWriter);
        byteBufferWriter.putLong(dt);
        PacketTypes.PacketType.LoginQueueDone2.send(connection);
        ConnectionManager.log("send-packet", "login-queue-done", connection);
    }

    static void receiveRemoveInventoryItemFromContainer(ByteBuffer byteBuffer, short var1) {
        if (IsoWorld.instance.CurrentCell != null) {
            ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
            short short0 = byteBuffer.getShort();
            int int0 = byteBufferReader.getInt();
            int int1 = byteBufferReader.getInt();
            int int2 = byteBufferReader.getInt();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (square != null) {
                if (short0 == 0) {
                    byte byte0 = byteBufferReader.getByte();
                    int int3 = byteBuffer.getInt();
                    if (byte0 < 0 || byte0 >= square.getStaticMovingObjects().size()) {
                        DebugLog.log("ERROR: removeItemFromContainer: invalid corpse index");
                        return;
                    }

                    IsoObject object0 = square.getStaticMovingObjects().get(byte0);
                    if (object0 != null && object0.getContainer() != null) {
                        for (int int4 = 0; int4 < int3; int4++) {
                            int int5 = byteBufferReader.getInt();
                            object0.getContainer().removeItemWithID(int5);
                            object0.getContainer().setExplored(true);
                        }
                    }
                } else if (short0 == 1) {
                    int int6 = byteBufferReader.getInt();
                    int int7 = byteBuffer.getInt();
                    ItemContainer container0 = null;

                    for (int int8 = 0; int8 < square.getWorldObjects().size(); int8++) {
                        IsoWorldInventoryObject worldInventoryObject = square.getWorldObjects().get(int8);
                        if (worldInventoryObject != null
                            && worldInventoryObject.getItem() instanceof InventoryContainer
                            && worldInventoryObject.getItem().id == int6) {
                            container0 = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
                            break;
                        }
                    }

                    if (container0 == null) {
                        DebugLog.log("ERROR removeItemFromContainer can't find world item with id=" + int6);
                        return;
                    }

                    for (int int9 = 0; int9 < int7; int9++) {
                        int int10 = byteBufferReader.getInt();
                        container0.removeItemWithID(int10);
                        container0.setExplored(true);
                    }
                } else if (short0 == 2) {
                    byte byte1 = byteBufferReader.getByte();
                    byte byte2 = byteBufferReader.getByte();
                    int int11 = byteBuffer.getInt();
                    if (byte1 < 0 || byte1 >= square.getObjects().size()) {
                        DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
                        return;
                    }

                    IsoObject object1 = square.getObjects().get(byte1);
                    ItemContainer container1 = object1 != null ? object1.getContainerByIndex(byte2) : null;
                    if (container1 != null) {
                        for (int int12 = 0; int12 < int11; int12++) {
                            int int13 = byteBufferReader.getInt();
                            container1.removeItemWithID(int13);
                            container1.setExplored(true);
                        }
                    }
                } else if (short0 == 3) {
                    short short1 = byteBufferReader.getShort();
                    byte byte3 = byteBufferReader.getByte();
                    int int14 = byteBuffer.getInt();
                    BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(short1);
                    if (vehicle == null) {
                        DebugLog.log("ERROR: removeItemFromContainer: invalid vehicle id");
                        return;
                    }

                    VehiclePart part = vehicle.getPartByIndex(byte3);
                    if (part == null) {
                        DebugLog.log("ERROR: removeItemFromContainer: invalid part index");
                        return;
                    }

                    ItemContainer container2 = part.getItemContainer();
                    if (container2 == null) {
                        DebugLog.log("ERROR: removeItemFromContainer: part " + part.getId() + " has no container");
                        return;
                    }

                    if (container2 != null) {
                        for (int int15 = 0; int15 < int14; int15++) {
                            int int16 = byteBufferReader.getInt();
                            container2.removeItemWithID(int16);
                            container2.setExplored(true);
                        }

                        part.setContainerContentAmount(container2.getCapacityWeight());
                    }
                } else {
                    DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
                }
            } else {
                instance.delayPacket(int0, int1, int2);
            }
        }
    }

    static void receiveAddInventoryItemToContainer(ByteBuffer byteBuffer, short var1) {
        if (IsoWorld.instance.CurrentCell != null) {
            ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
            short short0 = byteBuffer.getShort();
            int int0 = byteBufferReader.getInt();
            int int1 = byteBufferReader.getInt();
            int int2 = byteBufferReader.getInt();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (square == null) {
                instance.delayPacket(int0, int1, int2);
            } else {
                ItemContainer container = null;
                VehiclePart part = null;
                if (short0 == 0) {
                    byte byte0 = byteBufferReader.getByte();
                    if (byte0 < 0 || byte0 >= square.getStaticMovingObjects().size()) {
                        DebugLog.log("ERROR: sendItemsToContainer: invalid corpse index");
                        return;
                    }

                    IsoObject object0 = square.getStaticMovingObjects().get(byte0);
                    if (object0 != null && object0.getContainer() != null) {
                        container = object0.getContainer();
                    }
                } else if (short0 == 1) {
                    int int3 = byteBufferReader.getInt();

                    for (int int4 = 0; int4 < square.getWorldObjects().size(); int4++) {
                        IsoWorldInventoryObject worldInventoryObject = square.getWorldObjects().get(int4);
                        if (worldInventoryObject != null
                            && worldInventoryObject.getItem() instanceof InventoryContainer
                            && worldInventoryObject.getItem().id == int3) {
                            container = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
                            break;
                        }
                    }

                    if (container == null) {
                        DebugLog.log("ERROR: sendItemsToContainer: can't find world item with id=" + int3);
                        return;
                    }
                } else if (short0 == 2) {
                    byte byte1 = byteBufferReader.getByte();
                    byte byte2 = byteBufferReader.getByte();
                    if (byte1 < 0 || byte1 >= square.getObjects().size()) {
                        DebugLog.log("ERROR: sendItemsToContainer: invalid object index");
                        return;
                    }

                    IsoObject object1 = square.getObjects().get(byte1);
                    container = object1 != null ? object1.getContainerByIndex(byte2) : null;
                } else if (short0 == 3) {
                    short short1 = byteBufferReader.getShort();
                    byte byte3 = byteBufferReader.getByte();
                    BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(short1);
                    if (vehicle == null) {
                        DebugLog.log("ERROR: sendItemsToContainer: invalid vehicle id");
                        return;
                    }

                    part = vehicle.getPartByIndex(byte3);
                    if (part == null) {
                        DebugLog.log("ERROR: sendItemsToContainer: invalid part index");
                        return;
                    }

                    container = part.getItemContainer();
                    if (container == null) {
                        DebugLog.log("ERROR: sendItemsToContainer: part " + part.getId() + " has no container");
                        return;
                    }
                } else {
                    DebugLog.log("ERROR: sendItemsToContainer: unknown container type");
                }

                if (container != null) {
                    try {
                        ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 195, null, null);

                        for (int int5 = 0; int5 < arrayList.size(); int5++) {
                            InventoryItem item = (InventoryItem)arrayList.get(int5);
                            if (item != null) {
                                if (container.containsID(item.id)) {
                                    if (short0 != 0) {
                                        System.out.println("Error: Dupe item ID. id = " + item.id);
                                    }
                                } else {
                                    container.addItem(item);
                                    container.setExplored(true);
                                    if (container.getParent() instanceof IsoMannequin) {
                                        ((IsoMannequin)container.getParent()).wearItem(item, null);
                                    }
                                }
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    if (part != null) {
                        part.setContainerContentAmount(container.getCapacityWeight());
                    }
                }
            }
        }
    }

    private void readItemStats(ByteBuffer byteBuffer, InventoryItem item) {
        int int0 = byteBuffer.getInt();
        float float0 = byteBuffer.getFloat();
        boolean boolean0 = byteBuffer.get() == 1;
        item.setUses(int0);
        if (item instanceof DrainableComboItem) {
            ((DrainableComboItem)item).setDelta(float0);
            ((DrainableComboItem)item).updateWeight();
        }

        if (boolean0 && item instanceof Food food) {
            food.setHungChange(byteBuffer.getFloat());
            food.setCalories(byteBuffer.getFloat());
            food.setCarbohydrates(byteBuffer.getFloat());
            food.setLipids(byteBuffer.getFloat());
            food.setProteins(byteBuffer.getFloat());
            food.setThirstChange(byteBuffer.getFloat());
            food.setFluReduction(byteBuffer.getInt());
            food.setPainReduction(byteBuffer.getFloat());
            food.setEndChange(byteBuffer.getFloat());
            food.setReduceFoodSickness(byteBuffer.getInt());
            food.setStressChange(byteBuffer.getFloat());
            food.setFatigueChange(byteBuffer.getFloat());
        }
    }

    static void receiveItemStats(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        switch (short0) {
            case 0:
                byte byte3 = byteBuffer.get();
                int int5 = byteBuffer.getInt();
                if (square != null && byte3 >= 0 && byte3 < square.getStaticMovingObjects().size()) {
                    IsoMovingObject movingObject = square.getStaticMovingObjects().get(byte3);
                    ItemContainer container2 = movingObject.getContainer();
                    if (container2 != null) {
                        InventoryItem item2 = container2.getItemWithID(int5);
                        if (item2 != null) {
                            instance.readItemStats(byteBuffer, item2);
                        }
                    }
                }
                break;
            case 1:
                int int6 = byteBuffer.getInt();
                if (square != null) {
                    for (int int7 = 0; int7 < square.getWorldObjects().size(); int7++) {
                        IsoWorldInventoryObject worldInventoryObject = square.getWorldObjects().get(int7);
                        if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().id == int6) {
                            instance.readItemStats(byteBuffer, worldInventoryObject.getItem());
                            break;
                        }

                        if (worldInventoryObject.getItem() instanceof InventoryContainer) {
                            ItemContainer container3 = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
                            InventoryItem item3 = container3.getItemWithID(int6);
                            if (item3 != null) {
                                instance.readItemStats(byteBuffer, item3);
                                break;
                            }
                        }
                    }
                }
                break;
            case 2:
                byte byte1 = byteBuffer.get();
                byte byte2 = byteBuffer.get();
                int int4 = byteBuffer.getInt();
                if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
                    IsoObject object = square.getObjects().get(byte1);
                    ItemContainer container1 = object.getContainerByIndex(byte2);
                    if (container1 != null) {
                        InventoryItem item1 = container1.getItemWithID(int4);
                        if (item1 != null) {
                            instance.readItemStats(byteBuffer, item1);
                        }
                    }
                }
                break;
            case 3:
                short short1 = byteBuffer.getShort();
                byte byte0 = byteBuffer.get();
                int int3 = byteBuffer.getInt();
                BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(short1);
                if (vehicle != null) {
                    VehiclePart part = vehicle.getPartByIndex(byte0);
                    if (part != null) {
                        ItemContainer container0 = part.getItemContainer();
                        if (container0 != null) {
                            InventoryItem item0 = container0.getItemWithID(int3);
                            if (item0 != null) {
                                instance.readItemStats(byteBuffer, item0);
                            }
                        }
                    }
                }
        }
    }

    public static boolean canSeePlayerStats() {
        return connection.accessLevel != 1;
    }

    public static boolean canModifyPlayerStats() {
        return (connection.accessLevel & 56) != 0;
    }

    public void sendPersonalColor(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ChangeTextColor.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)player.getPlayerNum());
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().r);
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().g);
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().b);
        PacketTypes.PacketType.ChangeTextColor.send(connection);
    }

    public void sendChangedPlayerStats(IsoPlayer otherPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ChangePlayerStats.doPacket(byteBufferWriter);
        otherPlayer.createPlayerStats(byteBufferWriter, username);
        PacketTypes.PacketType.ChangePlayerStats.send(connection);
    }

    static void receiveChangePlayerStats(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            String string = GameWindow.ReadString(byteBuffer);
            player.setPlayerStats(byteBuffer, string);
            allChatMuted = player.isAllChatMuted();
        }
    }

    public void writePlayerConnectData(ByteBufferWriter b, IsoPlayer player) {
        b.putByte((byte)player.PlayerIndex);
        b.putByte((byte)IsoChunkMap.ChunkGridWidth);
        b.putFloat(player.x);
        b.putFloat(player.y);
        b.putFloat(player.z);

        try {
            player.getDescriptor().save(b.bb);
            player.getHumanVisual().save(b.bb);
            ItemVisuals itemVisuals = new ItemVisuals();
            player.getItemVisuals(itemVisuals);
            itemVisuals.save(b.bb);
            player.getXp().save(b.bb);
        } catch (IOException iOException0) {
            iOException0.printStackTrace();
        }

        b.putBoolean(player.isAllChatMuted());
        b.putUTF(player.getTagPrefix());
        b.putFloat(player.getTagColor().r);
        b.putFloat(player.getTagColor().g);
        b.putFloat(player.getTagColor().b);
        b.putInt(player.getTransactionID());
        b.putDouble(player.getHoursSurvived());
        b.putInt(player.getZombieKills());
        b.putUTF(player.getDisplayName());
        b.putFloat(player.getSpeakColour().r);
        b.putFloat(player.getSpeakColour().g);
        b.putFloat(player.getSpeakColour().b);
        b.putBoolean(player.showTag);
        b.putBoolean(player.factionPvp);
        if (SteamUtils.isSteamModeEnabled()) {
            b.putUTF(SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()));
        }

        InventoryItem item0 = player.getPrimaryHandItem();
        if (item0 == null) {
            b.putByte((byte)0);
        } else {
            b.putByte((byte)1);

            try {
                item0.saveWithSize(b.bb, false);
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }
        }

        InventoryItem item1 = player.getSecondaryHandItem();
        if (item1 == null) {
            b.putByte((byte)0);
        } else if (item1 == item0) {
            b.putByte((byte)2);
        } else {
            b.putByte((byte)1);

            try {
                item1.saveWithSize(b.bb, false);
            } catch (IOException iOException2) {
                iOException2.printStackTrace();
            }
        }

        b.putInt(player.getAttachedItems().size());

        for (int int0 = 0; int0 < player.getAttachedItems().size(); int0++) {
            b.putUTF(player.getAttachedItems().get(int0).getLocation());
            b.putUTF(player.getAttachedItems().get(int0).getItem().getFullType());
        }

        b.putInt(player.getPerkLevel(PerkFactory.Perks.Sneak));
        connection.username = player.username;
    }

    public void sendPlayerConnect(IsoPlayer player) {
        player.setOnlineID((short)-1);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerConnect.doPacket(byteBufferWriter);
        this.writePlayerConnectData(byteBufferWriter, player);
        PacketTypes.PacketType.PlayerConnect.send(connection);
        allChatMuted = player.isAllChatMuted();
        sendPerks(player);
        player.updateEquippedRadioFreq();
        this.bPlayerConnectSent = true;
        ConnectionManager.log("send-packet", "player-connect", connection);
    }

    @Deprecated
    public void sendPlayerSave(IsoPlayer player) {
        if (connection != null) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.PlayerSave.doPacket(byteBufferWriter);
            byteBufferWriter.putByte((byte)player.PlayerIndex);
            byteBufferWriter.putShort(player.OnlineID);
            byteBufferWriter.putFloat(player.x);
            byteBufferWriter.putFloat(player.y);
            byteBufferWriter.putFloat(player.z);
            PacketTypes.PacketType.PlayerSave.send(connection);
        }
    }

    public void sendPlayer2(IsoPlayer isoPlayer) {
        if (bClient && isoPlayer.isLocalPlayer() && isoPlayer.networkAI.isNeedToUpdate()) {
            if (PlayerPacket.l_send.playerPacket.set(isoPlayer)) {
                ByteBufferWriter byteBufferWriter = connection.startPacket();
                PacketTypes.PacketType packetType;
                if (this.PlayerUpdateReliableLimit.Check()) {
                    packetType = PacketTypes.PacketType.PlayerUpdateReliable;
                } else {
                    packetType = PacketTypes.PacketType.PlayerUpdate;
                }

                packetType.doPacket(byteBufferWriter);
                PlayerPacket.l_send.playerPacket.write(byteBufferWriter);
                packetType.send(connection);
            }
        }
    }

    public void sendPlayer(IsoPlayer isoPlayer) {
        isoPlayer.networkAI.needToUpdate();
    }

    public void heartBeat() {
        count++;
    }

    public static IsoZombie getZombie(short id) {
        return IDToZombieMap.get(id);
    }

    public static void sendPlayerExtraInfo(IsoPlayer p) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ExtraInfo.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)p.getPlayerNum());
        byteBufferWriter.putByte((byte)(p.isGodMod() ? 1 : 0));
        byteBufferWriter.putByte((byte)(p.isGhostMode() ? 1 : 0));
        byteBufferWriter.putByte((byte)(p.isInvisible() ? 1 : 0));
        byteBufferWriter.putByte((byte)(p.isNoClip() ? 1 : 0));
        byteBufferWriter.putByte((byte)(p.isShowAdminTag() ? 1 : 0));
        byteBufferWriter.putByte((byte)(p.isCanHearAll() ? 1 : 0));
        PacketTypes.PacketType.ExtraInfo.send(connection);
    }

    static void receiveExtraInfo(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        String string = GameWindow.ReadString(byteBuffer);
        boolean boolean0 = byteBuffer.get() == 1;
        boolean boolean1 = byteBuffer.get() == 1;
        boolean boolean2 = byteBuffer.get() == 1;
        boolean boolean3 = byteBuffer.get() == 1;
        boolean boolean4 = byteBuffer.get() == 1;
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            player.accessLevel = string;
            player.setGodMod(boolean0);
            player.setInvisible(boolean2);
            player.setGhostMode(boolean1);
            player.setNoClip(boolean3);
            player.setShowAdminTag(boolean4);
            if (!player.bRemote) {
                connection.accessLevel = PlayerType.fromString(string);
            }
        }
    }

    public void setResetID(int resetID) {
        this.ResetID = 0;
        this.loadResetID();
        if (this.ResetID != resetID) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("map_symbols.bin");
            arrayList.add("map_visited.bin");
            arrayList.add("recorded_media.bin");

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                try {
                    File file0 = ZomboidFileSystem.instance.getFileInCurrentSave((String)arrayList.get(int0));
                    if (file0.exists()) {
                        File file1 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + (String)arrayList.get(int0));
                        if (file1.exists()) {
                            file1.delete();
                        }

                        file0.renameTo(file1);
                    }
                } catch (Exception exception0) {
                    ExceptionLogger.logException(exception0);
                }
            }

            DebugLog.log("server was reset, deleting " + Core.GameMode + File.separator + Core.GameSaveWorld);
            LuaManager.GlobalObject.deleteSave(Core.GameMode + File.separator + Core.GameSaveWorld);
            LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                try {
                    File file2 = ZomboidFileSystem.instance.getFileInCurrentSave((String)arrayList.get(int1));
                    File file3 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + (String)arrayList.get(int1));
                    if (file3.exists()) {
                        file3.renameTo(file2);
                    }
                } catch (Exception exception1) {
                    ExceptionLogger.logException(exception1);
                }
            }
        }

        this.ResetID = resetID;
        this.saveResetID();
    }

    public void loadResetID() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
        if (file.exists()) {
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            try {
                this.ResetID = dataInputStream.readInt();
            } catch (IOException iOException0) {
                iOException0.printStackTrace();
            }

            try {
                fileInputStream.close();
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }
        }
    }

    private void saveResetID() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        try {
            dataOutputStream.writeInt(this.ResetID);
        } catch (IOException iOException0) {
            iOException0.printStackTrace();
        }

        try {
            fileOutputStream.close();
        } catch (IOException iOException1) {
            iOException1.printStackTrace();
        }
    }

    static void receivePlayerConnect(ByteBuffer byteBuffer, short var1) {
        boolean boolean0 = false;
        short short0 = byteBuffer.getShort();
        byte byte0 = -1;
        if (short0 == -1) {
            boolean0 = true;
            byte0 = byteBuffer.get();
            short0 = byteBuffer.getShort();

            try {
                GameTime.getInstance().load(byteBuffer);
                GameTime.getInstance().ServerTimeOfDay = GameTime.getInstance().getTimeOfDay();
                GameTime.getInstance().ServerNewDays = 0;
                GameTime.getInstance().setMinutesPerDay(SandboxOptions.instance.getDayLengthMinutes());
                LuaEventManager.triggerEvent("OnGameTimeLoaded");
            } catch (IOException iOException0) {
                iOException0.printStackTrace();
            }
        } else if (IDToPlayerMap.containsKey(short0)) {
            return;
        }

        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        float float2 = byteBuffer.getFloat();
        IsoPlayer player = null;
        if (boolean0) {
            String string0 = GameWindow.ReadString(byteBuffer);

            for (int int0 = 0; int0 < IsoWorld.instance.AddCoopPlayers.size(); int0++) {
                IsoWorld.instance.AddCoopPlayers.get(int0).receivePlayerConnect(byte0);
            }

            player = IsoPlayer.players[byte0];
            player.username = string0;
            player.setOnlineID(short0);
        } else {
            String string1 = GameWindow.ReadString(byteBuffer);
            SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();

            try {
                survivorDesc.load(byteBuffer, 195, null);
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }

            try {
                player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float0, (int)float1, (int)float2);
                player.bRemote = true;
                player.lastRemoteUpdate = System.currentTimeMillis();
                player.getHumanVisual().load(byteBuffer, 195);
                player.getItemVisuals().load(byteBuffer, 195);
                player.username = string1;
                player.updateUsername();
                player.setSceneCulled(false);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            player.setX(float0);
            player.setY(float1);
            player.setZ(float2);
            player.networkAI.targetX = float0;
            player.networkAI.targetY = float1;
            player.networkAI.targetZ = (int)float2;
        }

        player.setOnlineID(short0);
        if (SteamUtils.isSteamModeEnabled()) {
            player.setSteamID(byteBuffer.getLong());
        }

        player.setGodMod(byteBuffer.get() == 1);
        player.setGhostMode(byteBuffer.get() == 1);
        player.getSafety().load(byteBuffer, IsoWorld.getWorldVersion());
        byte byte1 = byteBuffer.get();
        if (boolean0) {
            connection.accessLevel = byte1;
            DebugLog.General
                .warn(
                    "ReceivePlayerConnect: guid=%d mtu=%d connection-type=%s",
                    connection.getConnectedGUID(),
                    connection.getMTUSize(),
                    connection.getConnectionType().name()
                );
        }

        player.accessLevel = PlayerType.toString(byte1);
        player.setInvisible(byteBuffer.get() == 1);
        if (!boolean0) {
            try {
                player.getXp().load(byteBuffer, 195);
            } catch (IOException iOException2) {
                iOException2.printStackTrace();
            }
        }

        player.setTagPrefix(GameWindow.ReadString(byteBuffer));
        player.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
        player.setHoursSurvived(byteBuffer.getDouble());
        player.setZombieKills(byteBuffer.getInt());
        player.setDisplayName(GameWindow.ReadString(byteBuffer));
        player.setSpeakColour(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
        player.showTag = byteBuffer.get() == 1;
        player.factionPvp = byteBuffer.get() == 1;
        int int1 = byteBuffer.getInt();

        for (int int2 = 0; int2 < int1; int2++) {
            String string2 = GameWindow.ReadString(byteBuffer);
            InventoryItem item = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
            if (item != null) {
                player.setAttachedItem(string2, item);
            }
        }

        int int3 = byteBuffer.getInt();
        int int4 = byteBuffer.getInt();
        int int5 = byteBuffer.getInt();
        player.remoteSneakLvl = int3;
        player.remoteStrLvl = int4;
        player.remoteFitLvl = int5;
        if (Core.bDebug) {
            DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + short0 + (boolean0 ? " (local)" : " (remote)"));
        }

        if (!boolean0) {
            rememberPlayerPosition(player, float0, float1);
        }

        IDToPlayerMap.put(short0, player);
        instance.idMapDirty = true;
        LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
        if (boolean0) {
            getCustomModData();
        }

        if (!boolean0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
            SafeHouse safeHouse = SafeHouse.hasSafehouse(player);
            if (safeHouse != null) {
                safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
            }
        }

        if (boolean0) {
            String string3 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
            if (string3 != null && !string3.equals("")) {
                ChatManager.getInstance().showServerChatMessage(string3);
            }

            VoiceManager.getInstance().UpdateChannelsRoaming(connection);
        }
    }

    static void receiveScoreboardUpdate(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        instance.connectedPlayers = new ArrayList<>();
        ArrayList arrayList0 = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();

        for (int int1 = 0; int1 < int0; int1++) {
            String string0 = GameWindow.ReadString(byteBuffer);
            String string1 = GameWindow.ReadString(byteBuffer);
            arrayList0.add(string0);
            arrayList1.add(string1);
            instance.connectedPlayers.add(instance.getPlayerFromUsername(string0));
            if (SteamUtils.isSteamModeEnabled()) {
                String string2 = SteamUtils.convertSteamIDToString(byteBuffer.getLong());
                arrayList2.add(string2);
            }
        }

        LuaEventManager.triggerEvent("OnScoreboardUpdate", arrayList0, arrayList1, arrayList2);
    }

    public boolean receivePlayerConnectWhileLoading(ByteBuffer bb) {
        boolean boolean0 = false;
        short short0 = bb.getShort();
        byte byte0 = -1;
        if (short0 != -1) {
            return false;
        } else {
            if (short0 == -1) {
                boolean0 = true;
                byte0 = bb.get();
                short0 = bb.getShort();

                try {
                    GameTime.getInstance().load(bb);
                    LuaEventManager.triggerEvent("OnGameTimeLoaded");
                } catch (IOException iOException0) {
                    iOException0.printStackTrace();
                }
            }

            float float0 = bb.getFloat();
            float float1 = bb.getFloat();
            float float2 = bb.getFloat();
            IsoPlayer player = null;
            if (boolean0) {
                String string0 = GameWindow.ReadString(bb);
                player = IsoPlayer.players[byte0];
                player.username = string0;
                player.setOnlineID(short0);
            } else {
                String string1 = GameWindow.ReadString(bb);
                SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();

                try {
                    survivorDesc.load(bb, 195, null);
                } catch (IOException iOException1) {
                    iOException1.printStackTrace();
                }

                try {
                    player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float0, (int)float1, (int)float2);
                    player.getHumanVisual().load(bb, 195);
                    player.getItemVisuals().load(bb, 195);
                    player.username = string1;
                    player.updateUsername();
                    player.setSceneCulled(false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                player.bRemote = true;
                player.setX(float0);
                player.setY(float1);
                player.setZ(float2);
            }

            player.setOnlineID(short0);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + short0 + (boolean0 ? " (me)" : " (not me)"));
            }

            int int0 = bb.getInt();

            for (int int1 = 0; int1 < int0; int1++) {
                ServerOptions.instance.putOption(GameWindow.ReadString(bb), GameWindow.ReadString(bb));
            }

            player.setGodMod(bb.get() == 1);
            player.setGhostMode(bb.get() == 1);
            player.getSafety().load(bb, IsoWorld.getWorldVersion());
            player.accessLevel = GameWindow.ReadString(bb);
            player.setInvisible(bb.get() == 1);
            IDToPlayerMap.put(short0, player);
            this.idMapDirty = true;
            getCustomModData();
            String string2 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
            if (boolean0 && string2 != null && !string2.equals("")) {
                ChatManager.getInstance().showServerChatMessage(string2);
            }

            return true;
        }
    }

    public ArrayList<IsoPlayer> getPlayers() {
        if (!this.idMapDirty) {
            return this.players;
        } else {
            this.players.clear();
            this.players.addAll(IDToPlayerMap.values());
            this.idMapDirty = false;
            return this.players;
        }
    }

    private IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            this.delayPacket(int0, int1, int2);
            return null;
        } else {
            return byte0 >= 0 && byte0 < square.getObjects().size() ? square.getObjects().get(byte0) : null;
        }
    }

    public void sendWeaponHit(IsoPlayer player, HandWeapon weapon, IsoObject object) {
        if (player != null && object != null && player.isLocalPlayer()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.WeaponHit.doPacket(byteBufferWriter);
            byteBufferWriter.putInt(object.square.x);
            byteBufferWriter.putInt(object.square.y);
            byteBufferWriter.putInt(object.square.z);
            byteBufferWriter.putByte((byte)object.getObjectIndex());
            byteBufferWriter.putShort((short)player.getPlayerNum());
            byteBufferWriter.putUTF(weapon != null ? weapon.getFullType() : "");
            PacketTypes.PacketType.WeaponHit.send(connection);
        }
    }

    public static void receiveSyncCustomLightSettings(ByteBuffer bb, short packetType) {
        int int0 = bb.getInt();
        int int1 = bb.getInt();
        int int2 = bb.getInt();
        byte byte0 = bb.get();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null && byte0 >= 0 && byte0 < square.getObjects().size()) {
            if (square.getObjects().get(byte0) instanceof IsoLightSwitch) {
                ((IsoLightSwitch)square.getObjects().get(byte0)).receiveSyncCustomizedSettings(bb, null);
            } else {
                DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + int0 + "," + int1 + "," + int2);
            }
        } else if (square != null) {
            DebugLog.log("Sync Lightswitch custom settings: index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
        } else if (Core.bDebug) {
            DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + int0 + "," + int1 + "," + int2);
        }
    }

    static void receiveSyncIsoObjectReq(ByteBuffer byteBuffer, short short1) {
        if (SystemDisabler.doObjectStateSyncEnable) {
            short short0 = byteBuffer.getShort();

            for (int int0 = 0; int0 < short0; int0++) {
                receiveSyncIsoObject(byteBuffer, short1);
            }
        }
    }

    static void receiveSyncWorldObjectsReq(ByteBuffer byteBuffer, short var1) {
        DebugLog.log("SyncWorldObjectsReq client : ");
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            instance.worldObjectsSyncReq.receiveSyncIsoChunk(int1, int2);
            short short1 = byteBuffer.getShort();
            DebugLog.log("[" + int1 + "," + int2 + "]:" + short1 + " ");
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1 * 10, int2 * 10, 0);
            if (square == null) {
                return;
            }

            IsoChunk chunk = square.getChunk();
            chunk.ObjectsSyncCount++;
            chunk.recalcHashCodeObjects();
        }

        DebugLog.log(";\n");
    }

    static void receiveSyncObjects(ByteBuffer byteBuffer, short var1) {
        if (SystemDisabler.doWorldSyncEnable) {
            short short0 = byteBuffer.getShort();
            if (short0 == 2) {
                instance.worldObjectsSyncReq.receiveGridSquareHashes(byteBuffer);
            }

            if (short0 == 4) {
                instance.worldObjectsSyncReq.receiveGridSquareObjectHashes(byteBuffer);
            }

            if (short0 == 6) {
                instance.worldObjectsSyncReq.receiveObject(byteBuffer);
            }
        }
    }

    static void receiveSyncIsoObject(ByteBuffer byteBuffer, short var1) {
        if (DebugOptions.instance.Network.Client.SyncIsoObject.getValue()) {
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            byte byte0 = byteBuffer.get();
            byte byte1 = byteBuffer.get();
            byte byte2 = byteBuffer.get();
            if (byte1 != 2) {
                instance.objectSyncReq.receiveIsoSync(int0, int1, int2, byte0);
            }

            if (byte1 == 1) {
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                if (square == null) {
                    return;
                }

                if (byte0 >= 0 && byte0 < square.getObjects().size()) {
                    square.getObjects().get(byte0).syncIsoObject(true, byte2, null, byteBuffer);
                } else {
                    DebugLog.Network.warn("SyncIsoObject: index=" + byte0 + " is invalid x,y,z=" + int0 + "," + int1 + "," + int2);
                }
            }
        }
    }

    static void receiveSyncAlarmClock(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        if (short0 == AlarmClock.PacketPlayer) {
            short short1 = byteBuffer.getShort();
            int int0 = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;
            int int1 = boolean0 ? 0 : byteBuffer.getInt();
            int int2 = boolean0 ? 0 : byteBuffer.getInt();
            byte byte0 = boolean0 ? 0 : byteBuffer.get();
            IsoPlayer player = IDToPlayerMap.get(short1);
            if (player != null) {
                for (int int3 = 0; int3 < player.getInventory().getItems().size(); int3++) {
                    InventoryItem item = player.getInventory().getItems().get(int3);
                    if (item instanceof AlarmClock && item.getID() == int0) {
                        if (boolean0) {
                            ((AlarmClock)item).stopRinging();
                        } else {
                            ((AlarmClock)item).setAlarmSet(byte0 == 1);
                            ((AlarmClock)item).setHour(int1);
                            ((AlarmClock)item).setMinute(int2);
                        }
                        break;
                    }
                }
            }
        } else if (short0 == AlarmClock.PacketWorld) {
            int int4 = byteBuffer.getInt();
            int int5 = byteBuffer.getInt();
            int int6 = byteBuffer.getInt();
            int int7 = byteBuffer.getInt();
            boolean boolean1 = byteBuffer.get() == 1;
            int int8 = boolean1 ? 0 : byteBuffer.getInt();
            int int9 = boolean1 ? 0 : byteBuffer.getInt();
            byte byte1 = boolean1 ? 0 : byteBuffer.get();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int6);
            if (square != null) {
                for (int int10 = 0; int10 < square.getWorldObjects().size(); int10++) {
                    IsoWorldInventoryObject worldInventoryObject = square.getWorldObjects().get(int10);
                    if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof AlarmClock && worldInventoryObject.getItem().id == int7) {
                        AlarmClock alarmClock = (AlarmClock)worldInventoryObject.getItem();
                        if (boolean1) {
                            alarmClock.stopRinging();
                        } else {
                            alarmClock.setAlarmSet(byte1 == 1);
                            alarmClock.setHour(int8);
                            alarmClock.setMinute(int9);
                        }
                        break;
                    }
                }
            }
        }
    }

    static void receiveAddItemToMap(ByteBuffer byteBuffer, short var1) {
        if (IsoWorld.instance.CurrentCell != null) {
            IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
            object.loadFromRemoteBuffer(byteBuffer);
            if (object.square != null) {
                if (object instanceof IsoLightSwitch) {
                    ((IsoLightSwitch)object).addLightSourceFromSprite();
                }

                object.addToWorld();
                IsoWorld.instance.CurrentCell.checkHaveRoof(object.square.getX(), object.square.getY());
                if (!(object instanceof IsoWorldInventoryObject)) {
                    for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                        LosUtil.cachecleared[int0] = true;
                    }

                    IsoGridSquare.setRecalcLightTime(-1);
                    GameTime.instance.lightSourceUpdate = 100.0F;
                    MapCollisionData.instance.squareChanged(object.square);
                    PolygonalMap2.instance.squareChanged(object.square);
                    if (object == object.square.getPlayerBuiltFloor()) {
                        IsoGridOcclusionData.SquareChanged();
                    }

                    IsoGenerator.updateGenerator(object.getSquare());
                }

                if (object instanceof IsoWorldInventoryObject || object.getContainer() != null) {
                    LuaEventManager.triggerEvent("OnContainerUpdate", object);
                }
            }
        }
    }

    static void skipPacket(ByteBuffer var0, short var1) {
    }

    static void receiveAccessDenied(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String[] strings = string0.split("##");
        String string1 = strings.length > 0
            ? Translator.getText(
                "UI_OnConnectFailed_" + strings[0],
                strings.length > 1 ? strings[1] : null,
                strings.length > 2 ? strings[2] : null,
                strings.length > 3 ? strings[3] : null
            )
            : null;
        LuaEventManager.triggerEvent("OnConnectFailed", string1);
        DebugLog.Multiplayer.warn("ReceiveAccessDenied: " + string1);
    }

    static void receivePlayerTimeout(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        receivePlayerTimeout(short0);
    }

    public static void receivePlayerTimeout(short playerID) {
        WorldMapRemotePlayers.instance.removePlayerByID(playerID);
        positions.remove(playerID);
        IsoPlayer player = IDToPlayerMap.get(playerID);
        if (player != null) {
            DebugLog.log("Received timeout for player " + player.username + " id " + player.OnlineID);
            NetworkZombieSimulator.getInstance().clearTargetAuth(player);
            if (player.getVehicle() != null) {
                int int0 = player.getVehicle().getSeat(player);
                if (int0 != -1) {
                    player.getVehicle().clearPassenger(int0);
                }

                VehicleManager.instance.sendRequestGetPosition(player.getVehicle().VehicleID, PacketTypes.PacketType.Vehicles);
            }

            player.removeFromWorld();
            player.removeFromSquare();
            IDToPlayerMap.remove(player.OnlineID);
            instance.idMapDirty = true;
            LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
        }
    }

    public void disconnect() {
        this.resetDisconnectTimer();
        this.bConnected = false;
        if (IsoPlayer.getInstance() != null) {
            IsoPlayer.getInstance().setOnlineID((short)-1);
        }
    }

    public void resetDisconnectTimer() {
        this.disconnectTime = System.currentTimeMillis();
    }

    public String getReconnectCountdownTimer() {
        return String.valueOf((int)Math.ceil((10000L - System.currentTimeMillis() + this.disconnectTime) / 1000L));
    }

    public boolean canConnect() {
        return System.currentTimeMillis() - this.disconnectTime > 10000L;
    }

    public void addIncoming(short id, ByteBuffer bb) {
        if (connection != null) {
            if (id == PacketTypes.PacketType.SentChunk.getId()) {
                WorldStreamer.instance.receiveChunkPart(bb);
            } else if (id == PacketTypes.PacketType.NotRequiredInZip.getId()) {
                WorldStreamer.instance.receiveNotRequired(bb);
            } else if (id == PacketTypes.PacketType.LoadPlayerProfile.getId()) {
                ClientPlayerDB.getInstance().clientLoadNetworkCharacter(bb, connection);
            } else {
                ZomboidNetData zomboidNetData = null;
                if (bb.remaining() > 2048) {
                    zomboidNetData = ZomboidNetDataPool.instance.getLong(bb.remaining());
                } else {
                    zomboidNetData = ZomboidNetDataPool.instance.get();
                }

                zomboidNetData.read(id, bb, connection);
                zomboidNetData.time = System.currentTimeMillis();
                MainLoopNetDataQ.add(zomboidNetData);
            }
        }
    }

    public void doDisconnect(String string) {
        if (connection != null) {
            connection.forceDisconnect(string);
            this.bConnected = false;
            connection = null;
            bClient = false;
        } else {
            instance.Shutdown();
        }
    }

    public void removeZombieFromCache(IsoZombie z) {
        if (IDToZombieMap.containsKey(z.OnlineID)) {
            IDToZombieMap.remove(z.OnlineID);
        }
    }

    static void receiveEquip(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        byte byte0 = byteBuffer.get();
        byte byte1 = byteBuffer.get();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != IsoPlayer.getInstance()) {
            InventoryItem item = null;
            if (byte1 == 1) {
                try {
                    item = InventoryItem.loadItem(byteBuffer, 195);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            if (player != null && byte0 == 1 && byte1 == 2) {
                item = player.getPrimaryHandItem();
            }

            if (player != null) {
                if (byte0 == 0) {
                    player.setPrimaryHandItem(item);
                } else {
                    player.setSecondaryHandItem(item);
                }

                try {
                    if (item != null) {
                        item.setContainer(player.getInventory());
                        if (byte1 == 1 && byteBuffer.get() == 1) {
                            item.getVisual().load(byteBuffer, 195);
                        }
                    }
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
    }

    public void equip(IsoPlayer player, int i) {
        InventoryItem item = null;
        if (i == 0) {
            item = player.getPrimaryHandItem();
        } else {
            item = player.getSecondaryHandItem();
        }

        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)player.PlayerIndex);
        byteBufferWriter.putByte((byte)i);
        if (item == null) {
            byteBufferWriter.putByte((byte)0);
        } else if (i == 1 && player.getPrimaryHandItem() == player.getSecondaryHandItem()) {
            byteBufferWriter.putByte((byte)2);
        } else {
            byteBufferWriter.putByte((byte)1);

            try {
                item.saveWithSize(byteBufferWriter.bb, false);
                if (item.getVisual() != null) {
                    byteBufferWriter.bb.put((byte)1);
                    item.getVisual().save(byteBufferWriter.bb);
                } else {
                    byteBufferWriter.bb.put((byte)0);
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        PacketTypes.PacketType.Equip.send(connection);
    }

    public void sendWorldMessage(String line) {
        ChatManager.getInstance().showInfoMessage(line);
    }

    private void convertGameSaveWorldDirectory(String string0, String string1) {
        File file0 = new File(string0);
        if (file0.isDirectory()) {
            File file1 = new File(string1);
            boolean boolean0 = file0.renameTo(file1);
            if (boolean0) {
                DebugLog.log("CONVERT: The GameSaveWorld directory was renamed from " + string0 + " to " + string1);
            } else {
                DebugLog.log("ERROR: The GameSaveWorld directory cannot rename from " + string0 + " to " + string1);
            }
        }
    }

    public void doConnect(String string0, String string1, String string2, String string3, String string4, String string5, String string6, boolean boolean0) {
        username = string0.trim();
        password = string1.trim();
        ip = string2.trim();
        localIP = string3.trim();
        port = Integer.parseInt(string4.trim());
        serverPassword = string5.trim();
        ServerName = string6.trim();
        useSteamRelay = boolean0;
        instance.init();
        Core.GameSaveWorld = ip + "_" + port + "_" + ServerWorldDatabase.encrypt(string0);
        this.convertGameSaveWorldDirectory(
            ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + ip + "_" + port + "_" + string0, ZomboidFileSystem.instance.getCurrentSaveDir()
        );
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
        }
    }

    public void doConnectCoop(String serverSteamID) {
        username = SteamFriends.GetPersonaName();
        password = "";
        ip = serverSteamID;
        localIP = "";
        port = 0;
        serverPassword = "";
        this.init();
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
        }
    }

    public void scoreboardUpdate() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ScoreboardUpdate.doPacket(byteBufferWriter);
        PacketTypes.PacketType.ScoreboardUpdate.send(connection);
    }

    public void sendWorldSound(Object source, int x, int y, int z, int radius, int volume, boolean stressHumans, float zombieIgnoreDist, float stressMod) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(x);
        byteBufferWriter.putInt(y);
        byteBufferWriter.putInt(z);
        byteBufferWriter.putInt(radius);
        byteBufferWriter.putInt(volume);
        byteBufferWriter.putByte((byte)(stressHumans ? 1 : 0));
        byteBufferWriter.putFloat(zombieIgnoreDist);
        byteBufferWriter.putFloat(stressMod);
        byteBufferWriter.putByte((byte)(source instanceof IsoZombie ? 1 : 0));
        PacketTypes.PacketType.WorldSound.send(connection);
    }

    static void receivePlayWorldSound(ByteBuffer byteBuffer, short var1) {
        PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.parse(byteBuffer, connection);
        playWorldSoundPacket.process();
        DebugLog.log(DebugType.Sound, playWorldSoundPacket.getDescription());
    }

    static void receivePlaySound(ByteBuffer byteBuffer, short var1) {
        PlaySoundPacket playSoundPacket = new PlaySoundPacket();
        playSoundPacket.parse(byteBuffer, connection);
        playSoundPacket.process();
        DebugLog.log(DebugType.Sound, playSoundPacket.getDescription());
    }

    static void receiveStopSound(ByteBuffer byteBuffer, short var1) {
        StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.parse(byteBuffer, connection);
        stopSoundPacket.process();
        DebugLog.log(DebugType.Sound, stopSoundPacket.getDescription());
    }

    static void receiveWorldSound(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        int int4 = byteBuffer.getInt();
        boolean boolean0 = byteBuffer.get() == 1;
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        boolean boolean1 = byteBuffer.get() == 1;
        WorldSoundManager.instance.addSound(null, int0, int1, int2, int3, int4, boolean0, float0, float1, boolean1, false, true);
    }

    static void receiveAddAmbient(ByteBuffer byteBuffer, short var1) {
        String string = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        float float0 = byteBuffer.getFloat();
        DebugLog.log(DebugType.Sound, "ambient: received " + string + " at " + int0 + "," + int1 + " radius=" + int2);
        AmbientStreamManager.instance.addAmbient(string, int0, int1, int2, float0);
    }

    public void sendClientCommand(IsoPlayer player, String module, String command, KahluaTable args) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
        byteBufferWriter.putUTF(module);
        byteBufferWriter.putUTF(command);
        if (args != null && !args.isEmpty()) {
            byteBufferWriter.putByte((byte)1);

            try {
                KahluaTableIterator kahluaTableIterator = args.iterator();

                while (kahluaTableIterator.advance()) {
                    if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
                        DebugLog.log("ERROR: sendClientCommand: can't save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
                    }
                }

                TableNetworkUtils.save(args, byteBufferWriter.bb);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        } else {
            byteBufferWriter.putByte((byte)0);
        }

        PacketTypes.PacketType.ClientCommand.send(connection);
    }

    public void sendClientCommandV(IsoPlayer player, String module, String command, Object... objects) {
        if (objects.length == 0) {
            this.sendClientCommand(player, module, command, (KahluaTable)null);
        } else if (objects.length % 2 != 0) {
            DebugLog.log("ERROR: sendClientCommand called with wrong number of arguments (" + module + " " + command + ")");
        } else {
            KahluaTable table = LuaManager.platform.newTable();

            for (byte byte0 = 0; byte0 < objects.length; byte0 += 2) {
                Object object = objects[byte0 + 1];
                if (object instanceof Float) {
                    table.rawset(objects[byte0], ((Float)object).doubleValue());
                } else if (object instanceof Integer) {
                    table.rawset(objects[byte0], ((Integer)object).doubleValue());
                } else if (object instanceof Short) {
                    table.rawset(objects[byte0], ((Short)object).doubleValue());
                } else {
                    table.rawset(objects[byte0], object);
                }
            }

            this.sendClientCommand(player, module, command, table);
        }
    }

    public void sendClothing(IsoPlayer player, String location, InventoryItem item) {
        if (player != null && player.OnlineID != -1) {
            SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
            syncClothingPacket.set(player, location, item);
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.SyncClothing.doPacket(byteBufferWriter);
            syncClothingPacket.write(byteBufferWriter);
            PacketTypes.PacketType.SyncClothing.send(connection);
        }
    }

    static void receiveSyncClothing(ByteBuffer byteBuffer, short var1) {
        SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
        syncClothingPacket.parse(byteBuffer, connection);
    }

    public void sendAttachedItem(IsoPlayer player, String location, InventoryItem item) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerAttachedItem.doPacket(byteBufferWriter);

        try {
            byteBufferWriter.putByte((byte)player.PlayerIndex);
            GameWindow.WriteString(byteBufferWriter.bb, location);
            if (item != null) {
                byteBufferWriter.putByte((byte)1);
                GameWindow.WriteString(byteBufferWriter.bb, item.getFullType());
            } else {
                byteBufferWriter.putByte((byte)0);
            }

            PacketTypes.PacketType.PlayerAttachedItem.send(connection);
        } catch (Throwable throwable) {
            connection.cancelPacket();
            ExceptionLogger.logException(throwable);
        }
    }

    static void receivePlayerAttachedItem(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null && !player.isLocalPlayer()) {
            String string0 = GameWindow.ReadString(byteBuffer);
            boolean boolean0 = byteBuffer.get() == 1;
            if (boolean0) {
                String string1 = GameWindow.ReadString(byteBuffer);
                InventoryItem item = InventoryItemFactory.CreateItem(string1);
                if (item == null) {
                    return;
                }

                player.setAttachedItem(string0, item);
            } else {
                player.setAttachedItem(string0, null);
            }
        }
    }

    public void sendVisual(IsoPlayer player) {
        if (player != null && player.OnlineID != -1) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.HumanVisual.doPacket(byteBufferWriter);

            try {
                byteBufferWriter.putShort(player.OnlineID);
                player.getHumanVisual().save(byteBufferWriter.bb);
                PacketTypes.PacketType.HumanVisual.send(connection);
            } catch (Throwable throwable) {
                connection.cancelPacket();
                ExceptionLogger.logException(throwable);
            }
        }
    }

    static void receiveHumanVisual(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null && !player.isLocalPlayer()) {
            try {
                player.getHumanVisual().load(byteBuffer, 195);
                player.resetModelNextFrame();
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }
    }

    static void receiveBloodSplatter(ByteBuffer byteBuffer, short var1) {
        String string = GameWindow.ReadString(byteBuffer);
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        float float2 = byteBuffer.getFloat();
        float float3 = byteBuffer.getFloat();
        float float4 = byteBuffer.getFloat();
        boolean boolean0 = byteBuffer.get() == 1;
        boolean boolean1 = byteBuffer.get() == 1;
        byte byte0 = byteBuffer.get();
        IsoCell cell = IsoWorld.instance.CurrentCell;
        IsoGridSquare square = cell.getGridSquare((double)float0, (double)float1, (double)float2);
        if (square == null) {
            instance.delayPacket((int)float0, (int)float1, (int)float2);
        } else if (boolean1 && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            for (int int0 = -1; int0 <= 1; int0++) {
                for (int int1 = -1; int1 <= 1; int1++) {
                    if (int0 != 0 || int1 != 0) {
                        new IsoZombieGiblets(
                            IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, int0 * Rand.Next(0.25F, 0.5F), int1 * Rand.Next(0.25F, 0.5F)
                        );
                    }
                }
            }

            new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, cell, float0, float1, float2, float3 * 0.8F, float4 * 0.8F);
        } else {
            if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
                for (int int2 = 0; int2 < byte0; int2++) {
                    square.splatBlood(3, 0.3F);
                }

                square.getChunk().addBloodSplat(float0, float1, (int)float2, Rand.Next(20));
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.5F, float4 * 1.5F);
            }

            byte byte1 = 3;
            byte byte2 = 0;
            byte byte3 = 1;
            switch (SandboxOptions.instance.BloodLevel.getValue()) {
                case 1:
                    byte3 = 0;
                    break;
                case 2:
                    byte3 = 1;
                    byte1 = 5;
                    byte2 = 2;
                case 3:
                default:
                    break;
                case 4:
                    byte3 = 3;
                    byte1 = 2;
                    break;
                case 5:
                    byte3 = 10;
                    byte1 = 0;
            }

            for (int int3 = 0; int3 < byte3; int3++) {
                if (Rand.Next(boolean0 ? 8 : byte1) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.5F, float4 * 1.5F);
                }

                if (Rand.Next(boolean0 ? 8 : byte1) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.5F, float4 * 1.5F);
                }

                if (Rand.Next(boolean0 ? 8 : byte1) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.8F, float4 * 1.8F);
                }

                if (Rand.Next(boolean0 ? 8 : byte1) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.9F, float4 * 1.9F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 3.5F, float4 * 3.5F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 3.8F, float4 * 3.8F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 3.9F, float4 * 3.9F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 1.5F, float4 * 1.5F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 3.8F, float4 * 3.8F);
                }

                if (Rand.Next(boolean0 ? 4 : byte2) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float0, float1, float2, float3 * 3.9F, float4 * 3.9F);
                }

                if (Rand.Next(boolean0 ? 9 : 6) == 0) {
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, cell, float0, float1, float2, float3 * 0.8F, float4 * 0.8F);
                }
            }
        }
    }

    static void receiveZombieSound(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        byte byte0 = byteBuffer.get();
        IsoZombie.ZombieSound zombieSound = IsoZombie.ZombieSound.fromIndex(byte0);
        DebugLog.log(DebugType.Sound, "sound: received " + byte0 + " for zombie " + short0);
        IsoZombie zombie0 = IDToZombieMap.get(short0);
        if (zombie0 != null && zombie0.getCurrentSquare() != null) {
            float float0 = zombieSound.radius();
            switch (zombieSound) {
                case Burned:
                    String string4 = zombie0.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    zombie0.getEmitter().playVocals(string4);
                    break;
                case DeadCloseKilled:
                    zombie0.getEmitter().playSoundImpl("HeadStab", null);
                    String string3 = zombie0.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    zombie0.getEmitter().playVocals(string3);
                    zombie0.getEmitter().tick();
                    break;
                case DeadNotCloseKilled:
                    zombie0.getEmitter().playSoundImpl("HeadSmash", null);
                    String string2 = zombie0.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    zombie0.getEmitter().playVocals(string2);
                    zombie0.getEmitter().tick();
                    break;
                case Hurt:
                    zombie0.playHurtSound();
                    break;
                case Idle:
                    String string1 = zombie0.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
                    zombie0.getEmitter().playVocals(string1);
                    break;
                case Lunge:
                    String string0 = zombie0.isFemale() ? "FemaleZombieAttack" : "MaleZombieAttack";
                    zombie0.getEmitter().playVocals(string0);
                    break;
                default:
                    DebugLog.log("unhandled zombie sound " + zombieSound);
            }
        }
    }

    static void receiveSlowFactor(ByteBuffer byteBuffer, short var1) {
        byte byte0 = byteBuffer.get();
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        IsoPlayer player = IsoPlayer.players[byte0];
        if (player != null && !player.isDead()) {
            player.setSlowTimer(float0);
            player.setSlowFactor(float1);
            DebugLog.log(DebugType.Combat, "slowTimer=" + float0 + " slowFactor=" + float1);
        }
    }

    public void sendCustomColor(IsoObject item) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendCustomColor.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(item.getSquare().getX());
        byteBufferWriter.putInt(item.getSquare().getY());
        byteBufferWriter.putInt(item.getSquare().getZ());
        byteBufferWriter.putInt(item.getSquare().getObjects().indexOf(item));
        byteBufferWriter.putFloat(item.getCustomColor().r);
        byteBufferWriter.putFloat(item.getCustomColor().g);
        byteBufferWriter.putFloat(item.getCustomColor().b);
        byteBufferWriter.putFloat(item.getCustomColor().a);
        PacketTypes.PacketType.SendCustomColor.send(connection);
    }

    public void sendBandage(int onlineID, int i, boolean bandaged, float bandageLife, boolean isAlcoholic, String bandageType) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Bandage.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)onlineID);
        byteBufferWriter.putInt(i);
        byteBufferWriter.putBoolean(bandaged);
        byteBufferWriter.putFloat(bandageLife);
        byteBufferWriter.putBoolean(isAlcoholic);
        GameWindow.WriteStringUTF(byteBufferWriter.bb, bandageType);
        PacketTypes.PacketType.Bandage.send(connection);
    }

    public void sendStitch(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, InventoryItem item, boolean doIt) {
        Stitch stitch = new Stitch();
        stitch.set(wielder, target, bodyPart, item, doIt);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Stitch.doPacket(byteBufferWriter);
        stitch.write(byteBufferWriter);
        PacketTypes.PacketType.Stitch.send(connection);
    }

    @Deprecated
    public void sendWoundInfection(int onlineID, int i, boolean infected) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WoundInfection.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)onlineID);
        byteBufferWriter.putInt(i);
        byteBufferWriter.putBoolean(infected);
        PacketTypes.PacketType.WoundInfection.send(connection);
    }

    public void sendDisinfect(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, InventoryItem alcohol) {
        Disinfect disinfect = new Disinfect();
        disinfect.set(wielder, target, bodyPart, alcohol);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Disinfect.doPacket(byteBufferWriter);
        disinfect.write(byteBufferWriter);
        PacketTypes.PacketType.Disinfect.send(connection);
    }

    public void sendSplint(int onlineID, int i, boolean doIt, float factor, String splintItem) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Splint.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)onlineID);
        byteBufferWriter.putInt(i);
        byteBufferWriter.putBoolean(doIt);
        if (doIt) {
            if (splintItem == null) {
                splintItem = "";
            }

            byteBufferWriter.putUTF(splintItem);
            byteBufferWriter.putFloat(factor);
        }

        PacketTypes.PacketType.Splint.send(connection);
    }

    public void sendAdditionalPain(int onlineID, int i, float level) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AdditionalPain.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)onlineID);
        byteBufferWriter.putInt(i);
        byteBufferWriter.putFloat(level);
        PacketTypes.PacketType.AdditionalPain.send(connection);
    }

    public void sendRemoveGlass(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart, boolean handPain) {
        RemoveGlass removeGlass = new RemoveGlass();
        removeGlass.set(wielder, target, bodyPart, handPain);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveGlass.doPacket(byteBufferWriter);
        removeGlass.write(byteBufferWriter);
        PacketTypes.PacketType.RemoveGlass.send(connection);
    }

    public void sendRemoveBullet(IsoGameCharacter wielder, IsoGameCharacter target, BodyPart bodyPart) {
        RemoveBullet removeBullet = new RemoveBullet();
        removeBullet.set(wielder, target, bodyPart);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveBullet.doPacket(byteBufferWriter);
        removeBullet.write(byteBufferWriter);
        PacketTypes.PacketType.RemoveBullet.send(connection);
    }

    public void sendCleanBurn(IsoGameCharacter _wielder, IsoGameCharacter _target, BodyPart bodyPart, InventoryItem _bandage) {
        CleanBurn cleanBurn = new CleanBurn();
        cleanBurn.set(_wielder, _target, bodyPart, _bandage);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.CleanBurn.doPacket(byteBufferWriter);
        cleanBurn.write(byteBufferWriter);
        PacketTypes.PacketType.CleanBurn.send(connection);
    }

    public void eatFood(IsoPlayer player, Food food, float percentage) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EatFood.doPacket(byteBufferWriter);

        try {
            byteBufferWriter.putByte((byte)player.PlayerIndex);
            byteBufferWriter.putFloat(percentage);
            food.saveWithSize(byteBufferWriter.bb, false);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        PacketTypes.PacketType.EatFood.send(connection);
    }

    public void drink(IsoPlayer player, float drink) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Drink.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)player.PlayerIndex);
        byteBufferWriter.putFloat(drink);
        PacketTypes.PacketType.Drink.send(connection);
    }

    public void addToItemSendBuffer(IsoObject parent, ItemContainer container, InventoryItem item) {
        if (parent instanceof IsoWorldInventoryObject) {
            InventoryItem _item = ((IsoWorldInventoryObject)parent).getItem();
            if (item == null || _item == null || !(_item instanceof InventoryContainer) || container != ((InventoryContainer)_item).getInventory()) {
                DebugLog.log("ERROR: addToItemSendBuffer parent=" + parent + " item=" + item);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                } else {
                    return;
                }
            }
        } else if (parent instanceof BaseVehicle) {
            if (container.vehiclePart == null || container.vehiclePart.getItemContainer() != container || container.vehiclePart.getVehicle() != parent) {
                DebugLog.log("ERROR: addToItemSendBuffer parent=" + parent + " item=" + item);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }

                return;
            }
        } else if (parent == null || item == null || parent.getContainerIndex(container) == -1) {
            DebugLog.log("ERROR: addToItemSendBuffer parent=" + parent + " item=" + item);
            if (Core.bDebug) {
                throw new IllegalStateException();
            }

            return;
        }

        if (this.itemsToSendRemove.containsKey(container)) {
            ArrayList arrayList0 = this.itemsToSendRemove.get(container);
            if (arrayList0.remove(item)) {
                if (arrayList0.isEmpty()) {
                    this.itemsToSendRemove.remove(container);
                }

                return;
            }
        }

        if (this.itemsToSend.containsKey(container)) {
            this.itemsToSend.get(container).add(item);
        } else {
            ArrayList arrayList1 = new ArrayList();
            this.itemsToSend.put(container, arrayList1);
            arrayList1.add(item);
        }
    }

    public void addToItemRemoveSendBuffer(IsoObject parent, ItemContainer container, InventoryItem item) {
        if (parent instanceof IsoWorldInventoryObject) {
            InventoryItem _item = ((IsoWorldInventoryObject)parent).getItem();
            if (item == null || _item == null || !(_item instanceof InventoryContainer) || container != ((InventoryContainer)_item).getInventory()) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + parent + " item=" + item);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                } else {
                    return;
                }
            }
        } else if (parent instanceof BaseVehicle) {
            if (container.vehiclePart == null || container.vehiclePart.getItemContainer() != container || container.vehiclePart.getVehicle() != parent) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + parent + " item=" + item);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }

                return;
            }
        } else if (parent instanceof IsoDeadBody) {
            if (item == null || container != parent.getContainer()) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + parent + " item=" + item);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }

                return;
            }
        } else if (parent == null || item == null || parent.getContainerIndex(container) == -1) {
            DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + parent + " item=" + item);
            if (Core.bDebug) {
                throw new IllegalStateException();
            }

            return;
        }

        if (!SystemDisabler.doWorldSyncEnable) {
            if (this.itemsToSend.containsKey(container)) {
                ArrayList arrayList0 = this.itemsToSend.get(container);
                if (arrayList0.remove(item)) {
                    if (arrayList0.isEmpty()) {
                        this.itemsToSend.remove(container);
                    }

                    return;
                }
            }

            if (this.itemsToSendRemove.containsKey(container)) {
                this.itemsToSendRemove.get(container).add(item);
            } else {
                ArrayList arrayList1 = new ArrayList();
                arrayList1.add(item);
                this.itemsToSendRemove.put(container, arrayList1);
            }
        } else {
            Object object = container.getParent();
            if (container.getContainingItem() != null && container.getContainingItem().getWorldItem() != null) {
                object = container.getContainingItem().getWorldItem();
            }

            UdpConnection udpConnection = connection;
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
            if (object instanceof IsoDeadBody) {
                byteBufferWriter.putShort((short)0);
                byteBufferWriter.putInt(((IsoObject)object).square.getX());
                byteBufferWriter.putInt(((IsoObject)object).square.getY());
                byteBufferWriter.putInt(((IsoObject)object).square.getZ());
                byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
                byteBufferWriter.putInt(1);
                byteBufferWriter.putInt(item.id);
            } else if (object instanceof IsoWorldInventoryObject) {
                byteBufferWriter.putShort((short)1);
                byteBufferWriter.putInt(((IsoObject)object).square.getX());
                byteBufferWriter.putInt(((IsoObject)object).square.getY());
                byteBufferWriter.putInt(((IsoObject)object).square.getZ());
                byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
                byteBufferWriter.putInt(1);
                byteBufferWriter.putInt(item.id);
            } else if (object instanceof BaseVehicle) {
                byteBufferWriter.putShort((short)3);
                byteBufferWriter.putInt(((IsoObject)object).square.getX());
                byteBufferWriter.putInt(((IsoObject)object).square.getY());
                byteBufferWriter.putInt(((IsoObject)object).square.getZ());
                byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
                byteBufferWriter.putByte((byte)container.vehiclePart.getIndex());
                byteBufferWriter.putInt(1);
                byteBufferWriter.putInt(item.id);
            } else {
                byteBufferWriter.putShort((short)2);
                byteBufferWriter.putInt(((IsoObject)object).square.getX());
                byteBufferWriter.putInt(((IsoObject)object).square.getY());
                byteBufferWriter.putInt(((IsoObject)object).square.getZ());
                byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
                byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(container));
                byteBufferWriter.putInt(1);
                byteBufferWriter.putInt(item.id);
            }

            PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection);
        }
    }

    public void sendAddedRemovedItems(boolean force) {
        boolean boolean0 = force || this.itemSendFrequency.Check();
        if (!SystemDisabler.doWorldSyncEnable && !this.itemsToSendRemove.isEmpty() && boolean0) {
            for (Entry entry0 : this.itemsToSendRemove.entrySet()) {
                ItemContainer container0 = (ItemContainer)entry0.getKey();
                ArrayList arrayList0 = (ArrayList)entry0.getValue();
                Object object0 = container0.getParent();
                if (container0.getContainingItem() != null && container0.getContainingItem().getWorldItem() != null) {
                    object0 = container0.getContainingItem().getWorldItem();
                }

                if (object0 != null && ((IsoObject)object0).square != null) {
                    try {
                        ByteBufferWriter byteBufferWriter0 = connection.startPacket();
                        PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter0);
                        if (object0 instanceof IsoDeadBody) {
                            byteBufferWriter0.putShort((short)0);
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getX());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getY());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getZ());
                            byteBufferWriter0.putByte((byte)((IsoObject)object0).getStaticMovingObjectIndex());
                        } else if (object0 instanceof IsoWorldInventoryObject) {
                            byteBufferWriter0.putShort((short)1);
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getX());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getY());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getZ());
                            byteBufferWriter0.putInt(((IsoWorldInventoryObject)object0).getItem().id);
                        } else if (object0 instanceof BaseVehicle) {
                            byteBufferWriter0.putShort((short)3);
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getX());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getY());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getZ());
                            byteBufferWriter0.putShort(((BaseVehicle)object0).VehicleID);
                            byteBufferWriter0.putByte((byte)container0.vehiclePart.getIndex());
                        } else {
                            byteBufferWriter0.putShort((short)2);
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getX());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getY());
                            byteBufferWriter0.putInt(((IsoObject)object0).square.getZ());
                            byteBufferWriter0.putByte((byte)((IsoObject)object0).square.getObjects().indexOf(object0));
                            byteBufferWriter0.putByte((byte)((IsoObject)object0).getContainerIndex(container0));
                        }

                        byteBufferWriter0.putInt(arrayList0.size());

                        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
                            InventoryItem item = (InventoryItem)arrayList0.get(int0);
                            byteBufferWriter0.putInt(item.id);
                        }

                        PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(connection);
                    } catch (Exception exception0) {
                        DebugLog.log("sendAddedRemovedItems: itemsToSendRemove container:" + container0 + "." + object0 + " items:" + arrayList0);
                        if (arrayList0 != null) {
                            for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                                if (arrayList0.get(int1) == null) {
                                    DebugLog.log("item:null");
                                } else {
                                    DebugLog.log("item:" + ((InventoryItem)arrayList0.get(int1)).getName());
                                }
                            }

                            DebugLog.log("itemSize:" + arrayList0.size());
                        }

                        exception0.printStackTrace();
                        connection.cancelPacket();
                    }
                }
            }

            this.itemsToSendRemove.clear();
        }

        if (!this.itemsToSend.isEmpty() && boolean0) {
            for (Entry entry1 : this.itemsToSend.entrySet()) {
                ItemContainer container1 = (ItemContainer)entry1.getKey();
                ArrayList arrayList1 = (ArrayList)entry1.getValue();
                Object object1 = container1.getParent();
                if (container1.getContainingItem() != null && container1.getContainingItem().getWorldItem() != null) {
                    object1 = container1.getContainingItem().getWorldItem();
                }

                if (object1 != null && ((IsoObject)object1).square != null) {
                    try {
                        ByteBufferWriter byteBufferWriter1 = connection.startPacket();
                        PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter1);
                        if (object1 instanceof IsoDeadBody) {
                            byteBufferWriter1.putShort((short)0);
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getX());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getY());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getZ());
                            byteBufferWriter1.putByte((byte)((IsoObject)object1).getStaticMovingObjectIndex());

                            try {
                                CompressIdenticalItems.save(byteBufferWriter1.bb, arrayList1, null);
                            } catch (Exception exception1) {
                                exception1.printStackTrace();
                            }
                        } else if (object1 instanceof IsoWorldInventoryObject) {
                            byteBufferWriter1.putShort((short)1);
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getX());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getY());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getZ());
                            byteBufferWriter1.putInt(((IsoWorldInventoryObject)object1).getItem().id);

                            try {
                                CompressIdenticalItems.save(byteBufferWriter1.bb, arrayList1, null);
                            } catch (Exception exception2) {
                                exception2.printStackTrace();
                            }
                        } else if (object1 instanceof BaseVehicle) {
                            byteBufferWriter1.putShort((short)3);
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getX());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getY());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getZ());
                            byteBufferWriter1.putShort(((BaseVehicle)object1).VehicleID);
                            byteBufferWriter1.putByte((byte)container1.vehiclePart.getIndex());

                            try {
                                CompressIdenticalItems.save(byteBufferWriter1.bb, arrayList1, null);
                            } catch (Exception exception3) {
                                exception3.printStackTrace();
                            }
                        } else {
                            byteBufferWriter1.putShort((short)2);
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getX());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getY());
                            byteBufferWriter1.putInt(((IsoObject)object1).square.getZ());
                            byteBufferWriter1.putByte((byte)((IsoObject)object1).square.getObjects().indexOf(object1));
                            byteBufferWriter1.putByte((byte)((IsoObject)object1).getContainerIndex(container1));

                            try {
                                CompressIdenticalItems.save(byteBufferWriter1.bb, arrayList1, null);
                            } catch (Exception exception4) {
                                exception4.printStackTrace();
                            }
                        }

                        PacketTypes.PacketType.AddInventoryItemToContainer.send(connection);
                    } catch (Exception exception5) {
                        DebugLog.log("sendAddedRemovedItems: itemsToSend container:" + container1 + "." + object1 + " items:" + arrayList1);
                        if (arrayList1 != null) {
                            for (int int2 = 0; int2 < arrayList1.size(); int2++) {
                                if (arrayList1.get(int2) == null) {
                                    DebugLog.log("item:null");
                                } else {
                                    DebugLog.log("item:" + ((InventoryItem)arrayList1.get(int2)).getName());
                                }
                            }

                            DebugLog.log("itemSize:" + arrayList1.size());
                        }

                        exception5.printStackTrace();
                        connection.cancelPacket();
                    }
                }
            }

            this.itemsToSend.clear();
        }
    }

    public void checkAddedRemovedItems(IsoObject aboutToRemove) {
        if (aboutToRemove != null) {
            if (!this.itemsToSend.isEmpty() || !this.itemsToSendRemove.isEmpty()) {
                if (aboutToRemove instanceof IsoDeadBody) {
                    if (this.itemsToSend.containsKey(aboutToRemove.getContainer()) || this.itemsToSendRemove.containsKey(aboutToRemove.getContainer())) {
                        this.sendAddedRemovedItems(true);
                    }
                } else if (aboutToRemove instanceof IsoWorldInventoryObject) {
                    InventoryItem item = ((IsoWorldInventoryObject)aboutToRemove).getItem();
                    if (item instanceof InventoryContainer) {
                        ItemContainer container0 = ((InventoryContainer)item).getInventory();
                        if (this.itemsToSend.containsKey(container0) || this.itemsToSendRemove.containsKey(container0)) {
                            this.sendAddedRemovedItems(true);
                        }
                    }
                } else if (!(aboutToRemove instanceof BaseVehicle)) {
                    for (int int0 = 0; int0 < aboutToRemove.getContainerCount(); int0++) {
                        ItemContainer container1 = aboutToRemove.getContainerByIndex(int0);
                        if (this.itemsToSend.containsKey(container1) || this.itemsToSendRemove.containsKey(container1)) {
                            this.sendAddedRemovedItems(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void sendReplaceOnCooked(InventoryItem item) {
        IsoObject object = item.getOutermostContainer().getParent();
        if (object != null) {
            this.checkAddedRemovedItems(object);
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.ReplaceOnCooked.doPacket(byteBufferWriter);
            byteBufferWriter.putInt(object.square.getX());
            byteBufferWriter.putInt(object.square.getY());
            byteBufferWriter.putInt(object.square.getZ());
            byteBufferWriter.putByte((byte)object.square.getObjects().indexOf(object));
            byteBufferWriter.putByte((byte)object.getContainerIndex(item.getContainer()));
            byteBufferWriter.putInt(item.getID());
            PacketTypes.PacketType.ReplaceOnCooked.send(connection);
        }
    }

    private void writeItemStats(ByteBufferWriter byteBufferWriter, InventoryItem item) {
        byteBufferWriter.putInt(item.id);
        byteBufferWriter.putInt(item.getUses());
        byteBufferWriter.putFloat(item instanceof DrainableComboItem ? ((DrainableComboItem)item).getUsedDelta() : 0.0F);
        if (item instanceof Food food) {
            byteBufferWriter.putBoolean(true);
            byteBufferWriter.putFloat(food.getHungChange());
            byteBufferWriter.putFloat(food.getCalories());
            byteBufferWriter.putFloat(food.getCarbohydrates());
            byteBufferWriter.putFloat(food.getLipids());
            byteBufferWriter.putFloat(food.getProteins());
            byteBufferWriter.putFloat(food.getThirstChange());
            byteBufferWriter.putInt(food.getFluReduction());
            byteBufferWriter.putFloat(food.getPainReduction());
            byteBufferWriter.putFloat(food.getEndChange());
            byteBufferWriter.putInt(food.getReduceFoodSickness());
            byteBufferWriter.putFloat(food.getStressChange());
            byteBufferWriter.putFloat(food.getFatigueChange());
        } else {
            byteBufferWriter.putBoolean(false);
        }
    }

    public void sendItemStats(InventoryItem item) {
        if (item != null) {
            if (item.getWorldItem() != null && item.getWorldItem().getWorldObjectIndex() != -1) {
                IsoWorldInventoryObject worldInventoryObject = item.getWorldItem();
                ByteBufferWriter byteBufferWriter0 = connection.startPacket();
                PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter0);
                byteBufferWriter0.putShort((short)1);
                byteBufferWriter0.putInt(worldInventoryObject.square.getX());
                byteBufferWriter0.putInt(worldInventoryObject.square.getY());
                byteBufferWriter0.putInt(worldInventoryObject.square.getZ());
                this.writeItemStats(byteBufferWriter0, item);
                PacketTypes.PacketType.ItemStats.send(connection);
            } else if (item.getContainer() == null) {
                DebugLog.log("ERROR: sendItemStats(): item is neither in a container nor on the ground");
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
            } else {
                ItemContainer container = item.getContainer();
                Object object = container.getParent();
                if (container.getContainingItem() != null && container.getContainingItem().getWorldItem() != null) {
                    object = container.getContainingItem().getWorldItem();
                }

                if (object instanceof IsoWorldInventoryObject) {
                    InventoryItem _item = ((IsoWorldInventoryObject)object).getItem();
                    if (!(_item instanceof InventoryContainer) || container != ((InventoryContainer)_item).getInventory()) {
                        DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + item);
                        if (Core.bDebug) {
                            throw new IllegalStateException();
                        } else {
                            return;
                        }
                    }
                } else if (object instanceof BaseVehicle) {
                    if (container.vehiclePart == null || container.vehiclePart.getItemContainer() != container || container.vehiclePart.getVehicle() != object) {
                        DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + item);
                        if (Core.bDebug) {
                            throw new IllegalStateException();
                        }

                        return;
                    }
                } else if (object instanceof IsoDeadBody) {
                    if (container != ((IsoObject)object).getContainer()) {
                        DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + item);
                        if (Core.bDebug) {
                            throw new IllegalStateException();
                        }

                        return;
                    }
                } else if (object == null || ((IsoObject)object).getContainerIndex(container) == -1) {
                    DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + item);
                    if (Core.bDebug) {
                        throw new IllegalStateException();
                    }

                    return;
                }

                ByteBufferWriter byteBufferWriter1 = connection.startPacket();
                PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter1);
                if (object instanceof IsoDeadBody) {
                    byteBufferWriter1.putShort((short)0);
                    byteBufferWriter1.putInt(((IsoObject)object).square.getX());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getY());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getZ());
                    byteBufferWriter1.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
                } else if (object instanceof IsoWorldInventoryObject) {
                    byteBufferWriter1.putShort((short)1);
                    byteBufferWriter1.putInt(((IsoObject)object).square.getX());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getY());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getZ());
                } else if (object instanceof BaseVehicle) {
                    byteBufferWriter1.putShort((short)3);
                    byteBufferWriter1.putInt(((IsoObject)object).square.getX());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getY());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getZ());
                    byteBufferWriter1.putShort(((BaseVehicle)object).VehicleID);
                    byteBufferWriter1.putByte((byte)container.vehiclePart.getIndex());
                } else {
                    byteBufferWriter1.putShort((short)2);
                    byteBufferWriter1.putInt(((IsoObject)object).square.getX());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getY());
                    byteBufferWriter1.putInt(((IsoObject)object).square.getZ());
                    byteBufferWriter1.putByte((byte)((IsoObject)object).getObjectIndex());
                    byteBufferWriter1.putByte((byte)((IsoObject)object).getContainerIndex(container));
                }

                this.writeItemStats(byteBufferWriter1, item);
                PacketTypes.PacketType.ItemStats.send(connection);
            }
        }
    }

    public void PlayWorldSound(String name, int x, int y, byte z) {
        PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.set(name, x, y, z);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayWorldSound.doPacket(byteBufferWriter);
        playWorldSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.PlayWorldSound.send(connection);
    }

    public void PlaySound(String name, boolean loop, IsoMovingObject object) {
        PlaySoundPacket playSoundPacket = new PlaySoundPacket();
        playSoundPacket.set(name, loop, object);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlaySound.doPacket(byteBufferWriter);
        playSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.PlaySound.send(connection);
    }

    public void StopSound(IsoMovingObject object, String soundName, boolean trigger) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopSound.doPacket(byteBufferWriter);
        StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.set(object, soundName, trigger);
        stopSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.StopSound.send(connection);
    }

    public void startLocalServer() throws Exception {
        bClient = true;
        ip = "127.0.0.1";
        Thread thread = new Thread(
            ThreadGroups.Workers,
            () -> {
                String string0 = System.getProperty("file.separator");
                String string1 = System.getProperty("java.class.path");
                String string2 = System.getProperty("java.home") + string0 + "bin" + string0 + "java";
                ProcessBuilder processBuilder = new ProcessBuilder(
                    string2,
                    "-Xms2048m",
                    "-Xmx2048m",
                    "-Djava.library.path=../natives/",
                    "-cp",
                    "lwjgl.jar;lwjgl_util.jar;sqlitejdbc-v056.jar;../bin/",
                    "zombie.network.GameServer"
                );
                processBuilder.redirectErrorStream(true);
                Process process = null;

                try {
                    process = processBuilder.start();
                } catch (IOException iOException0) {
                    iOException0.printStackTrace();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                boolean boolean0 = false;

                try {
                    while (!inputStreamReader.ready()) {
                        int int0;
                        try {
                            while ((int0 = inputStreamReader.read()) != -1) {
                                System.out.print((char)int0);
                            }
                        } catch (IOException iOException1) {
                            iOException1.printStackTrace();
                        }

                        try {
                            inputStreamReader.close();
                        } catch (IOException iOException2) {
                            iOException2.printStackTrace();
                        }
                    }
                } catch (IOException iOException3) {
                    iOException3.printStackTrace();
                }
            }
        );
        thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        thread.start();
    }

    public static void sendPing() {
        if (bClient) {
            ByteBufferWriter byteBufferWriter = connection.startPingPacket();
            PacketTypes.doPingPacket(byteBufferWriter);
            byteBufferWriter.putLong(System.currentTimeMillis());
            byteBufferWriter.putLong(0L);
            connection.endPingPacket();
        }
    }

    public static void registerZone(IsoMetaGrid.Zone zone, boolean transmitToOthers) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RegisterZone.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(zone.name);
        byteBufferWriter.putUTF(zone.type);
        byteBufferWriter.putInt(zone.x);
        byteBufferWriter.putInt(zone.y);
        byteBufferWriter.putInt(zone.z);
        byteBufferWriter.putInt(zone.w);
        byteBufferWriter.putInt(zone.h);
        byteBufferWriter.putInt(zone.getLastActionTimestamp());
        byteBufferWriter.putBoolean(transmitToOthers);
        PacketTypes.PacketType.RegisterZone.send(connection);
    }

    static void receiveHelicopter(ByteBuffer byteBuffer, short var1) {
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        boolean boolean0 = byteBuffer.get() == 1;
        if (IsoWorld.instance != null && IsoWorld.instance.helicopter != null) {
            IsoWorld.instance.helicopter.clientSync(float0, float1, boolean0);
        }
    }

    static void receiveVehicles(ByteBuffer byteBuffer, short var1) {
        VehicleManager.instance.clientPacket(byteBuffer);
    }

    static void receiveVehicleAuthorization(ByteBuffer byteBuffer, short var1) {
        VehicleAuthorizationPacket vehicleAuthorizationPacket = new VehicleAuthorizationPacket();
        vehicleAuthorizationPacket.parse(byteBuffer, connection);
        if (vehicleAuthorizationPacket.isConsistent()) {
            vehicleAuthorizationPacket.process();
        }
    }

    static void receiveTimeSync(ByteBuffer byteBuffer, short var1) {
        GameTime.receiveTimeSync(byteBuffer, connection);
    }

    public static void sendSafehouse(SafeHouse safehouse, boolean remove) {
        SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
        syncSafehousePacket.set(safehouse, remove);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncSafehouse.doPacket(byteBufferWriter);
        syncSafehousePacket.write(byteBufferWriter);
        PacketTypes.PacketType.SyncSafehouse.send(connection);
    }

    static void receiveSyncSafehouse(ByteBuffer byteBuffer, short var1) {
        SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
        syncSafehousePacket.parse(byteBuffer, connection);
        syncSafehousePacket.process();
        LuaEventManager.triggerEvent("OnSafehousesChanged");
    }

    public static void sendKickOutOfSafehouse(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.KickOutOfSafehouse.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(player.getOnlineID());
        PacketTypes.PacketType.KickOutOfSafehouse.send(connection);
    }

    public IsoPlayer getPlayerFromUsername(String _username) {
        ArrayList arrayList = this.getPlayers();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoPlayer player = (IsoPlayer)arrayList.get(int0);
            if (player.getUsername().equals(_username)) {
                return player;
            }
        }

        return null;
    }

    public static void destroy(IsoObject obj) {
        if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.SledgehammerDestroy.doPacket(byteBufferWriter);
            IsoGridSquare square = obj.getSquare();
            byteBufferWriter.putInt(square.getX());
            byteBufferWriter.putInt(square.getY());
            byteBufferWriter.putInt(square.getZ());
            byteBufferWriter.putInt(square.getObjects().indexOf(obj));
            PacketTypes.PacketType.SledgehammerDestroy.send(connection);
            square.RemoveTileObject(obj);
        }
    }

    public static void sendTeleport(IsoPlayer player, float x, float y, float z) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, player.getUsername());
        byteBufferWriter.putFloat(x);
        byteBufferWriter.putFloat(y);
        byteBufferWriter.putFloat(z);
        PacketTypes.PacketType.Teleport.send(connection);
    }

    public static void sendStopFire(IsoGridSquare sq) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)0);
        byteBufferWriter.putInt(sq.getX());
        byteBufferWriter.putInt(sq.getY());
        byteBufferWriter.putInt(sq.getZ());
        PacketTypes.PacketType.StopFire.send(connection);
    }

    public static void sendStopFire(IsoGameCharacter chr) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
        if (chr instanceof IsoPlayer) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putShort(chr.getOnlineID());
        }

        if (chr instanceof IsoZombie) {
            byteBufferWriter.putByte((byte)2);
            byteBufferWriter.putShort(((IsoZombie)chr).OnlineID);
        }

        PacketTypes.PacketType.StopFire.send(connection);
    }

    public void sendCataplasm(int onlineID, int i, float plantainFactor, float comfreyFactor, float garlicFactor) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Cataplasm.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)onlineID);
        byteBufferWriter.putInt(i);
        byteBufferWriter.putFloat(plantainFactor);
        byteBufferWriter.putFloat(comfreyFactor);
        byteBufferWriter.putFloat(garlicFactor);
        PacketTypes.PacketType.Cataplasm.send(connection);
    }

    static void receiveBodyDamageUpdate(ByteBuffer byteBuffer, short var1) {
        BodyDamageSync.instance.clientPacket(byteBuffer);
    }

    public static void receiveRadioDeviceDataState(ByteBuffer bb, short packetType) {
        byte byte0 = bb.get();
        if (byte0 == 1) {
            int int0 = bb.getInt();
            int int1 = bb.getInt();
            int int2 = bb.getInt();
            int int3 = bb.getInt();
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (square != null && int3 >= 0 && int3 < square.getObjects().size()) {
                IsoObject object = square.getObjects().get(int3);
                if (object instanceof IsoWaveSignal) {
                    DeviceData deviceData0 = ((IsoWaveSignal)object).getDeviceData();
                    if (deviceData0 != null) {
                        try {
                            deviceData0.receiveDeviceDataStatePacket(bb, null);
                        } catch (Exception exception0) {
                            System.out.print(exception0.getMessage());
                        }
                    }
                }
            }
        } else if (byte0 == 0) {
            short short0 = bb.getShort();
            IsoPlayer player = IDToPlayerMap.get(short0);
            byte byte1 = bb.get();
            if (player != null) {
                Radio radio = null;
                if (byte1 == 1 && player.getPrimaryHandItem() instanceof Radio) {
                    radio = (Radio)player.getPrimaryHandItem();
                }

                if (byte1 == 2 && player.getSecondaryHandItem() instanceof Radio) {
                    radio = (Radio)player.getSecondaryHandItem();
                }

                if (radio != null && radio.getDeviceData() != null) {
                    try {
                        radio.getDeviceData().receiveDeviceDataStatePacket(bb, connection);
                    } catch (Exception exception1) {
                        System.out.print(exception1.getMessage());
                    }
                }
            }
        } else if (byte0 == 2) {
            short short1 = bb.getShort();
            short short2 = bb.getShort();
            BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(short1);
            if (vehicle != null) {
                VehiclePart part = vehicle.getPartByIndex(short2);
                if (part != null) {
                    DeviceData deviceData1 = part.getDeviceData();
                    if (deviceData1 != null) {
                        try {
                            deviceData1.receiveDeviceDataStatePacket(bb, null);
                        } catch (Exception exception2) {
                            System.out.print(exception2.getMessage());
                        }
                    }
                }
            }
        }
    }

    public static void sendRadioServerDataRequest() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RadioServerData.doPacket(byteBufferWriter);
        PacketTypes.PacketType.RadioServerData.send(connection);
    }

    public static void receiveRadioServerData(ByteBuffer bb, short packetType) {
        ZomboidRadio zomboidRadio = ZomboidRadio.getInstance();
        int int0 = bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            String string0 = GameWindow.ReadString(bb);
            int int2 = bb.getInt();

            for (int int3 = 0; int3 < int2; int3++) {
                int int4 = bb.getInt();
                String string1 = GameWindow.ReadString(bb);
                zomboidRadio.addChannelName(string1, int4, string0);
            }
        }

        zomboidRadio.setHasRecievedServerData(true);
        ZomboidRadio.POST_RADIO_SILENCE = bb.get() == 1;
    }

    public static void receiveRadioPostSilence(ByteBuffer bb, short packetType) {
        ZomboidRadio.POST_RADIO_SILENCE = bb.get() == 1;
    }

    public static void sendIsoWaveSignal(
        int sourceX, int sourceY, int channel, String msg, String guid, String codes, float r, float g, float b, int signalStrength, boolean isTV
    ) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WaveSignal.doPacket(byteBufferWriter);

        try {
            WaveSignal waveSignal = new WaveSignal();
            waveSignal.set(sourceX, sourceY, channel, msg, guid, codes, r, g, b, signalStrength, isTV);
            waveSignal.write(byteBufferWriter);
            PacketTypes.PacketType.WaveSignal.send(connection);
        } catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendIsoWaveSignal: failed", LogSeverity.Error);
        }
    }

    public static void receiveWaveSignal(ByteBuffer bb, short packetType) {
        if (ChatManager.getInstance().isWorking()) {
            WaveSignal waveSignal = new WaveSignal();
            waveSignal.parse(bb, connection);
            waveSignal.process(connection);
        }
    }

    public static void sendPlayerListensChannel(int channel, boolean listenmode, boolean isTV) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerListensChannel.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(channel);
        byteBufferWriter.putByte((byte)(listenmode ? 1 : 0));
        byteBufferWriter.putByte((byte)(isTV ? 1 : 0));
        PacketTypes.PacketType.PlayerListensChannel.send(connection);
    }

    static void receiveSyncFurnace(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            instance.delayPacket(int0, int1, int2);
        } else {
            if (square != null) {
                BSFurnace bSFurnace = null;

                for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                    if (square.getObjects().get(int3) instanceof BSFurnace) {
                        bSFurnace = (BSFurnace)square.getObjects().get(int3);
                        break;
                    }
                }

                if (bSFurnace == null) {
                    DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + int0 + "," + int1 + "," + int2);
                    return;
                }

                bSFurnace.fireStarted = byteBuffer.get() == 1;
                bSFurnace.fuelAmount = byteBuffer.getFloat();
                bSFurnace.fuelDecrease = byteBuffer.getFloat();
                bSFurnace.heat = byteBuffer.getFloat();
                bSFurnace.sSprite = GameWindow.ReadString(byteBuffer);
                bSFurnace.sLitSprite = GameWindow.ReadString(byteBuffer);
                bSFurnace.updateLight();
            }
        }
    }

    public static void sendFurnaceChange(BSFurnace furnace) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncFurnace.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(furnace.getSquare().getX());
        byteBufferWriter.putInt(furnace.getSquare().getY());
        byteBufferWriter.putInt(furnace.getSquare().getZ());
        byteBufferWriter.putByte((byte)(furnace.isFireStarted() ? 1 : 0));
        byteBufferWriter.putFloat(furnace.getFuelAmount());
        byteBufferWriter.putFloat(furnace.getFuelDecrease());
        byteBufferWriter.putFloat(furnace.getHeat());
        GameWindow.WriteString(byteBufferWriter.bb, furnace.sSprite);
        GameWindow.WriteString(byteBufferWriter.bb, furnace.sLitSprite);
        PacketTypes.PacketType.SyncFurnace.send(connection);
    }

    public static void sendCompost(IsoCompost isoCompost) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncCompost.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoCompost.getSquare().getX());
        byteBufferWriter.putInt(isoCompost.getSquare().getY());
        byteBufferWriter.putInt(isoCompost.getSquare().getZ());
        byteBufferWriter.putFloat(isoCompost.getCompost());
        PacketTypes.PacketType.SyncCompost.send(connection);
    }

    static void receiveSyncCompost(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square != null) {
            IsoCompost compost = square.getCompost();
            if (compost == null) {
                compost = new IsoCompost(square.getCell(), square);
                square.AddSpecialObject(compost);
            }

            compost.setCompost(byteBuffer.getFloat());
            compost.updateSprite();
        }
    }

    public void requestUserlog(String user) {
        if (canSeePlayerStats()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.Userlog.doPacket(byteBufferWriter);
            GameWindow.WriteString(byteBufferWriter.bb, user);
            PacketTypes.PacketType.Userlog.send(connection);
        }
    }

    public void addUserlog(String user, String type, String text) {
        if (canSeePlayerStats()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.AddUserlog.doPacket(byteBufferWriter);
            GameWindow.WriteString(byteBufferWriter.bb, user);
            GameWindow.WriteString(byteBufferWriter.bb, type);
            GameWindow.WriteString(byteBufferWriter.bb, text);
            PacketTypes.PacketType.AddUserlog.send(connection);
        }
    }

    public void removeUserlog(String user, String type, String text) {
        if (canModifyPlayerStats()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.RemoveUserlog.doPacket(byteBufferWriter);
            GameWindow.WriteString(byteBufferWriter.bb, user);
            GameWindow.WriteString(byteBufferWriter.bb, type);
            GameWindow.WriteString(byteBufferWriter.bb, text);
            PacketTypes.PacketType.RemoveUserlog.send(connection);
        }
    }

    public void addWarningPoint(String user, String reason, int amount) {
        if (canModifyPlayerStats()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.AddWarningPoint.doPacket(byteBufferWriter);
            GameWindow.WriteString(byteBufferWriter.bb, user);
            GameWindow.WriteString(byteBufferWriter.bb, reason);
            byteBufferWriter.putInt(amount);
            PacketTypes.PacketType.AddWarningPoint.send(connection);
        }
    }

    static void receiveMessageForAdmin(ByteBuffer byteBuffer, short var1) {
        if (canSeePlayerStats()) {
            String string = GameWindow.ReadString(byteBuffer);
            int int0 = byteBuffer.getInt();
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            LuaEventManager.triggerEvent("OnAdminMessage", string, int0, int1, int2);
        }
    }

    public void wakeUpPlayer(IsoPlayer chr) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WakeUpPlayer.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)chr.getPlayerNum());
        PacketTypes.PacketType.WakeUpPlayer.send(connection);
    }

    static void receiveWakeUpPlayer(ByteBuffer byteBuffer, short var1) {
        IsoPlayer player = IDToPlayerMap.get(byteBuffer.getShort());
        if (player != null) {
            SleepingEvent.instance.wakeUp(player, true);
        }
    }

    public void getDBSchema() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.GetDBSchema.doPacket(byteBufferWriter);
        PacketTypes.PacketType.GetDBSchema.send(connection);
    }

    static void receiveGetDBSchema(ByteBuffer byteBuffer, short var1) {
        if ((connection.accessLevel & 3) <= 0) {
            instance.dbSchema = LuaManager.platform.newTable();
            int int0 = byteBuffer.getInt();

            for (int int1 = 0; int1 < int0; int1++) {
                KahluaTable table0 = LuaManager.platform.newTable();
                String string0 = GameWindow.ReadString(byteBuffer);
                int int2 = byteBuffer.getInt();

                for (int int3 = 0; int3 < int2; int3++) {
                    KahluaTable table1 = LuaManager.platform.newTable();
                    String string1 = GameWindow.ReadString(byteBuffer);
                    String string2 = GameWindow.ReadString(byteBuffer);
                    table1.rawset("name", string1);
                    table1.rawset("type", string2);
                    table0.rawset(int3, table1);
                }

                instance.dbSchema.rawset(string0, table0);
            }

            LuaEventManager.triggerEvent("OnGetDBSchema", instance.dbSchema);
        }
    }

    public void getTableResult(String tableName, int numberPerPages) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.GetTableResult.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(numberPerPages);
        byteBufferWriter.putUTF(tableName);
        PacketTypes.PacketType.GetTableResult.send(connection);
    }

    static void receiveGetTableResult(ByteBuffer byteBuffer, short var1) {
        ArrayList arrayList0 = new ArrayList();
        int int0 = byteBuffer.getInt();
        String string0 = GameWindow.ReadString(byteBuffer);
        int int1 = byteBuffer.getInt();
        ArrayList arrayList1 = new ArrayList();

        for (int int2 = 0; int2 < int1; int2++) {
            DBResult dBResult = new DBResult();
            dBResult.setTableName(string0);
            int int3 = byteBuffer.getInt();

            for (int int4 = 0; int4 < int3; int4++) {
                String string1 = GameWindow.ReadString(byteBuffer);
                String string2 = GameWindow.ReadString(byteBuffer);
                dBResult.getValues().put(string1, string2);
                if (int2 == 0) {
                    arrayList1.add(string1);
                }
            }

            dBResult.setColumns(arrayList1);
            arrayList0.add(dBResult);
        }

        LuaEventManager.triggerEvent("OnGetTableResult", arrayList0, int0, string0);
    }

    public void executeQuery(String query, KahluaTable params) {
        if (connection.accessLevel == 32) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.ExecuteQuery.doPacket(byteBufferWriter);

            try {
                byteBufferWriter.putUTF(query);
                params.save(byteBufferWriter.bb);
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            } finally {
                PacketTypes.PacketType.ExecuteQuery.send(connection);
            }
        }
    }

    public ArrayList<IsoPlayer> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean remove) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncNonPvpZone.doPacket(byteBufferWriter);
        nonPvpZone.save(byteBufferWriter.bb);
        byteBufferWriter.putBoolean(remove);
        PacketTypes.PacketType.SyncNonPvpZone.send(connection);
    }

    public static void sendFaction(Faction faction, boolean remove) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncFaction.doPacket(byteBufferWriter);
        faction.writeToBuffer(byteBufferWriter, remove);
        PacketTypes.PacketType.SyncFaction.send(connection);
    }

    public static void sendFactionInvite(Faction faction, IsoPlayer host, String invited) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendFactionInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(faction.getName());
        byteBufferWriter.putUTF(host.getUsername());
        byteBufferWriter.putUTF(invited);
        PacketTypes.PacketType.SendFactionInvite.send(connection);
    }

    static void receiveSendFactionInvite(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        LuaEventManager.triggerEvent("ReceiveFactionInvite", string0, string1);
    }

    public static void acceptFactionInvite(Faction faction, String host) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AcceptedFactionInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(faction.getName());
        byteBufferWriter.putUTF(host);
        PacketTypes.PacketType.AcceptedFactionInvite.send(connection);
    }

    static void receiveAcceptedFactionInvite(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        Faction faction = Faction.getFaction(string0);
        if (faction != null) {
            faction.addPlayer(string1);
        }

        LuaEventManager.triggerEvent("AcceptedFactionInvite", string0, string1);
    }

    public static void addTicket(String author, String message, int ticketID) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddTicket.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(author);
        byteBufferWriter.putUTF(message);
        byteBufferWriter.putInt(ticketID);
        PacketTypes.PacketType.AddTicket.send(connection);
    }

    public static void getTickets(String author) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ViewTickets.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(author);
        PacketTypes.PacketType.ViewTickets.send(connection);
    }

    static void receiveViewTickets(ByteBuffer byteBuffer, short var1) {
        ArrayList arrayList = new ArrayList();
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            DBTicket dBTicket0 = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
            arrayList.add(dBTicket0);
            if (byteBuffer.get() == 1) {
                DBTicket dBTicket1 = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
                dBTicket1.setIsAnswer(true);
                dBTicket0.setAnswer(dBTicket1);
            }
        }

        LuaEventManager.triggerEvent("ViewTickets", arrayList);
    }

    static void receiveChecksum(ByteBuffer byteBuffer, short var1) {
        NetChecksum.comparer.clientPacket(byteBuffer);
    }

    public static void removeTicket(int ticketID) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveTicket.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(ticketID);
        PacketTypes.PacketType.RemoveTicket.send(connection);
    }

    public static boolean sendItemListNet(IsoPlayer sender, ArrayList<InventoryItem> items, IsoPlayer receiver, String sessionID, String custom) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendItemListNet.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)(receiver != null ? 1 : 0));
        if (receiver != null) {
            byteBufferWriter.putShort(receiver.getOnlineID());
        }

        byteBufferWriter.putByte((byte)(sender != null ? 1 : 0));
        if (sender != null) {
            byteBufferWriter.putShort(sender.getOnlineID());
        }

        GameWindow.WriteString(byteBufferWriter.bb, sessionID);
        byteBufferWriter.putByte((byte)(custom != null ? 1 : 0));
        if (custom != null) {
            GameWindow.WriteString(byteBufferWriter.bb, custom);
        }

        try {
            CompressIdenticalItems.save(byteBufferWriter.bb, items, null);
        } catch (Exception exception) {
            exception.printStackTrace();
            connection.cancelPacket();
            return false;
        }

        PacketTypes.PacketType.SendItemListNet.send(connection);
        return true;
    }

    static void receiveSendItemListNet(ByteBuffer byteBuffer, short var1) {
        IsoPlayer player0 = null;
        if (byteBuffer.get() != 1) {
            player0 = IDToPlayerMap.get(byteBuffer.getShort());
        }

        IsoPlayer player1 = null;
        if (byteBuffer.get() == 1) {
            player1 = IDToPlayerMap.get(byteBuffer.getShort());
        }

        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = null;
        if (byteBuffer.get() == 1) {
            string1 = GameWindow.ReadString(byteBuffer);
        }

        short short0 = byteBuffer.getShort();
        ArrayList arrayList = new ArrayList(short0);

        try {
            for (int int0 = 0; int0 < short0; int0++) {
                InventoryItem item = InventoryItem.loadItem(byteBuffer, 195);
                if (item != null) {
                    arrayList.add(item);
                }
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        LuaEventManager.triggerEvent("OnReceiveItemListNet", player1, arrayList, player0, string0, string1);
    }

    public void requestTrading(IsoPlayer you, IsoPlayer other) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(you.OnlineID);
        byteBufferWriter.putShort(other.OnlineID);
        byteBufferWriter.putByte((byte)0);
        PacketTypes.PacketType.RequestTrading.send(connection);
    }

    public void acceptTrading(IsoPlayer you, IsoPlayer other, boolean accept) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(other.OnlineID);
        byteBufferWriter.putShort(you.OnlineID);
        byteBufferWriter.putByte((byte)(accept ? 1 : 2));
        PacketTypes.PacketType.RequestTrading.send(connection);
    }

    static void receiveRequestTrading(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        byte byte0 = byteBuffer.get();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            if (byte0 == 0) {
                LuaEventManager.triggerEvent("RequestTrade", player);
            } else {
                LuaEventManager.triggerEvent("AcceptedTrade", byte0 == 1);
            }
        }
    }

    public void tradingUISendAddItem(IsoPlayer you, IsoPlayer other, InventoryItem item) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIAddItem.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(you.OnlineID);
        byteBufferWriter.putShort(other.OnlineID);

        try {
            item.saveWithSize(byteBufferWriter.bb, false);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        PacketTypes.PacketType.TradingUIAddItem.send(connection);
    }

    static void receiveTradingUIAddItem(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        Object object = null;

        try {
            object = InventoryItem.loadItem(byteBuffer, 195);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        if (object != null) {
            IsoPlayer player = IDToPlayerMap.get(short0);
            if (player != null) {
                LuaEventManager.triggerEvent("TradingUIAddItem", player, object);
            }
        }
    }

    public void tradingUISendRemoveItem(IsoPlayer you, IsoPlayer other, int index) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIRemoveItem.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(you.OnlineID);
        byteBufferWriter.putShort(other.OnlineID);
        byteBufferWriter.putInt(index);
        PacketTypes.PacketType.TradingUIRemoveItem.send(connection);
    }

    static void receiveTradingUIRemoveItem(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            LuaEventManager.triggerEvent("TradingUIRemoveItem", player, int0);
        }
    }

    public void tradingUISendUpdateState(IsoPlayer you, IsoPlayer other, int state) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIUpdateState.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(you.OnlineID);
        byteBufferWriter.putShort(other.OnlineID);
        byteBufferWriter.putInt(state);
        PacketTypes.PacketType.TradingUIUpdateState.send(connection);
    }

    static void receiveTradingUIUpdateState(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            LuaEventManager.triggerEvent("TradingUIUpdateState", player, int0);
        }
    }

    public static void sendBuildingStashToDo(String stashName) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ReadAnnotedMap.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(stashName);
        PacketTypes.PacketType.ReadAnnotedMap.send(connection);
    }

    public static void setServerStatisticEnable(boolean enable) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StatisticRequest.doPacket(byteBufferWriter);
        byteBufferWriter.putBoolean(enable);
        PacketTypes.PacketType.StatisticRequest.send(connection);
        MPStatistic.clientStatisticEnable = enable;
    }

    public static boolean getServerStatisticEnable() {
        return MPStatistic.clientStatisticEnable;
    }

    public static void sendRequestInventory(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
        byteBufferWriter.putShort(player.getOnlineID());
        PacketTypes.PacketType.RequestInventory.send(connection);
    }

    private int sendInventoryPutItems(ByteBufferWriter byteBufferWriter, LinkedHashMap<String, InventoryItem> linkedHashMap0, long long0) {
        int int0 = linkedHashMap0.size();
        Iterator iterator = linkedHashMap0.keySet().iterator();

        while (iterator.hasNext()) {
            InventoryItem item = (InventoryItem)linkedHashMap0.get(iterator.next());
            byteBufferWriter.putUTF(item.getModule());
            byteBufferWriter.putUTF(item.getType());
            byteBufferWriter.putLong(item.getID());
            byteBufferWriter.putLong(long0);
            byteBufferWriter.putBoolean(IsoPlayer.getInstance().isEquipped(item));
            if (item instanceof DrainableComboItem) {
                byteBufferWriter.putFloat(((DrainableComboItem)item).getUsedDelta());
            } else {
                byteBufferWriter.putFloat(item.getCondition());
            }

            byteBufferWriter.putInt(item.getCount());
            if (item instanceof DrainableComboItem) {
                byteBufferWriter.putUTF(Translator.getText("IGUI_ItemCat_Drainable"));
            } else {
                byteBufferWriter.putUTF(item.getCategory());
            }

            byteBufferWriter.putUTF(item.getContainer().getType());
            byteBufferWriter.putBoolean(item.getWorker() != null && item.getWorker().equals("inInv"));
            if (item instanceof InventoryContainer
                && ((InventoryContainer)item).getItemContainer() != null
                && !((InventoryContainer)item).getItemContainer().getItems().isEmpty()) {
                LinkedHashMap linkedHashMap1 = ((InventoryContainer)item).getItemContainer().getItems4Admin();
                int0 += linkedHashMap1.size();
                this.sendInventoryPutItems(byteBufferWriter, linkedHashMap1, item.getID());
            }
        }

        return int0;
    }

    static void receiveRequestInventory(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendInventory.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(short0);
        int int0 = byteBufferWriter.bb.position();
        byteBufferWriter.putInt(0);
        byteBufferWriter.putFloat(IsoPlayer.getInstance().getInventory().getCapacityWeight());
        byteBufferWriter.putFloat(IsoPlayer.getInstance().getMaxWeight());
        LinkedHashMap linkedHashMap = IsoPlayer.getInstance().getInventory().getItems4Admin();
        int int1 = instance.sendInventoryPutItems(byteBufferWriter, linkedHashMap, -1L);
        int int2 = byteBufferWriter.bb.position();
        byteBufferWriter.bb.position(int0);
        byteBufferWriter.putInt(int1);
        byteBufferWriter.bb.position(int2);
        PacketTypes.PacketType.SendInventory.send(connection);
    }

    static void receiveSendInventory(ByteBuffer byteBuffer, short var1) {
        int int0 = byteBuffer.getInt();
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        KahluaTable table0 = LuaManager.platform.newTable();
        table0.rawset("capacityWeight", (double)float0);
        table0.rawset("maxWeight", (double)float1);

        for (int int1 = 0; int1 < int0; int1++) {
            KahluaTable table1 = LuaManager.platform.newTable();
            String string0 = GameWindow.ReadStringUTF(byteBuffer) + "." + GameWindow.ReadStringUTF(byteBuffer);
            long long0 = byteBuffer.getLong();
            long long1 = byteBuffer.getLong();
            boolean boolean0 = byteBuffer.get() == 1;
            float float2 = byteBuffer.getFloat();
            int int2 = byteBuffer.getInt();
            String string1 = GameWindow.ReadStringUTF(byteBuffer);
            String string2 = GameWindow.ReadStringUTF(byteBuffer);
            boolean boolean1 = byteBuffer.get() == 1;
            table1.rawset("fullType", string0);
            table1.rawset("itemId", long0);
            table1.rawset("isEquip", boolean0);
            table1.rawset("var", Math.round(float2 * 100.0) / 100.0);
            table1.rawset("count", int2 + "");
            table1.rawset("cat", string1);
            table1.rawset("parrentId", long1);
            table1.rawset("hasParrent", long1 != -1L);
            table1.rawset("container", string2);
            table1.rawset("inInv", boolean1);
            table0.rawset(table0.size() + 1, table1);
        }

        LuaEventManager.triggerEvent("MngInvReceiveItems", table0);
    }

    public static void sendGetItemInvMng(long itemId) {
    }

    static void receiveSpawnRegion(ByteBuffer byteBuffer, short var1) {
        if (instance.ServerSpawnRegions == null) {
            instance.ServerSpawnRegions = LuaManager.platform.newTable();
        }

        int int0 = byteBuffer.getInt();
        KahluaTable table = LuaManager.platform.newTable();

        try {
            table.load(byteBuffer, 195);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        instance.ServerSpawnRegions.rawset(int0, table);
    }

    static void receivePlayerConnectLoading(ByteBuffer byteBuffer) throws IOException {
        int int0 = byteBuffer.position();
        if (!instance.receivePlayerConnectWhileLoading(byteBuffer)) {
            byteBuffer.position(int0);
            throw new IOException();
        }
    }

    static void receiveClimateManagerPacket(ByteBuffer byteBuffer, short var1) {
        ClimateManager climateManager = ClimateManager.getInstance();
        if (climateManager != null) {
            try {
                climateManager.receiveClimatePacket(byteBuffer, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static void receiveServerMap(ByteBuffer byteBuffer, short var1) {
        ClientServerMap.receivePacket(byteBuffer);
    }

    static void receivePassengerMap(ByteBuffer byteBuffer, short var1) {
        PassengerMap.clientReceivePacket(byteBuffer);
    }

    static void receiveIsoRegionServerPacket(ByteBuffer byteBuffer, short var1) {
        IsoRegions.receiveServerUpdatePacket(byteBuffer);
    }

    public static void sendIsoRegionDataRequest() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.doPacket(byteBufferWriter);
        PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.send(connection);
    }

    public void sendSandboxOptionsToServer(SandboxOptions options) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SandboxOptions.doPacket(byteBufferWriter);

        try {
            options.save(byteBufferWriter.bb);
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
        } finally {
            PacketTypes.PacketType.SandboxOptions.send(connection);
        }
    }

    static void receiveSandboxOptions(ByteBuffer byteBuffer, short var1) {
        try {
            SandboxOptions.instance.load(byteBuffer);
            SandboxOptions.instance.applySettings();
            SandboxOptions.instance.toLua();
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    static void receiveChunkObjectState(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(short0, short1);
        if (chunk != null) {
            try {
                chunk.loadObjectState(byteBuffer);
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }
    }

    static void receivePlayerLeaveChat(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processLeaveChatPacket(byteBuffer);
    }

    static void receiveInitPlayerChat(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processInitPlayerChatPacket(byteBuffer);
    }

    static void receiveAddChatTab(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processAddTabPacket(byteBuffer);
    }

    static void receiveRemoveChatTab(ByteBuffer byteBuffer, short var1) {
        ChatManager.getInstance().processRemoveTabPacket(byteBuffer);
    }

    static void receivePlayerNotFound(ByteBuffer byteBuffer, short var1) {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        ChatManager.getInstance().processPlayerNotFound(string);
    }

    public static void sendZombieHelmetFall(IsoPlayer player, IsoGameCharacter zombie, InventoryItem item) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ZombieHelmetFalling.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)player.PlayerIndex);
        byteBufferWriter.putShort(zombie.getOnlineID());
        byteBufferWriter.putUTF(item.getType());
        PacketTypes.PacketType.ZombieHelmetFalling.send(connection);
    }

    static void receiveZombieHelmetFalling(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        IsoZombie zombie0 = IDToZombieMap.get(short0);
        String string = GameWindow.ReadString(byteBuffer);
        if (zombie0 != null && !StringUtils.isNullOrEmpty(string)) {
            zombie0.helmetFall(true, string);
        }
    }

    public static void sendPerks(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncPerks.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)player.PlayerIndex);
        byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Sneak));
        byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Strength));
        byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Fitness));
        PacketTypes.PacketType.SyncPerks.send(connection);
    }

    static void receiveSyncPerks(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null && !player.isLocalPlayer()) {
            player.remoteSneakLvl = int0;
            player.remoteStrLvl = int1;
            player.remoteFitLvl = int2;
        }
    }

    public static void sendWeight(IsoPlayer player) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncWeight.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)player.PlayerIndex);
        byteBufferWriter.putDouble(player.getNutrition().getWeight());
        PacketTypes.PacketType.SyncWeight.send(connection);
    }

    static void receiveSyncWeight(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        double double0 = byteBuffer.getDouble();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null && !player.isLocalPlayer()) {
            player.getNutrition().setWeight(double0);
        }
    }

    static void receiveGlobalModData(ByteBuffer byteBuffer, short var1) {
        GlobalModData.instance.receive(byteBuffer);
    }

    public static void sendSafehouseInvite(SafeHouse safehouse, IsoPlayer host, String invited) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendSafehouseInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(safehouse.getTitle());
        byteBufferWriter.putUTF(host.getUsername());
        byteBufferWriter.putUTF(invited);
        byteBufferWriter.putInt(safehouse.getX());
        byteBufferWriter.putInt(safehouse.getY());
        byteBufferWriter.putInt(safehouse.getW());
        byteBufferWriter.putInt(safehouse.getH());
        PacketTypes.PacketType.SendSafehouseInvite.send(connection);
    }

    static void receiveSendSafehouseInvite(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        SafeHouse safeHouse = SafeHouse.getSafeHouse(int0, int1, int2, int3);
        LuaEventManager.triggerEvent("ReceiveSafehouseInvite", safeHouse, string1);
    }

    public static void acceptSafehouseInvite(SafeHouse safehouse, String host) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AcceptedSafehouseInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(safehouse.getTitle());
        byteBufferWriter.putUTF(host);
        byteBufferWriter.putUTF(username);
        byteBufferWriter.putInt(safehouse.getX());
        byteBufferWriter.putInt(safehouse.getY());
        byteBufferWriter.putInt(safehouse.getW());
        byteBufferWriter.putInt(safehouse.getH());
        PacketTypes.PacketType.AcceptedSafehouseInvite.send(connection);
    }

    static void receiveAcceptedSafehouseInvite(ByteBuffer byteBuffer, short var1) {
        String string0 = GameWindow.ReadString(byteBuffer);
        String string1 = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        SafeHouse safeHouse = SafeHouse.getSafeHouse(int0, int1, int2, int3);
        if (safeHouse != null) {
            safeHouse.addPlayer(string2);
        }

        LuaEventManager.triggerEvent("AcceptedSafehouseInvite", safeHouse.getTitle(), string1);
    }

    public static void sendEquippedRadioFreq(IsoPlayer plyr) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncEquippedRadioFreq.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)plyr.PlayerIndex);
        byteBufferWriter.putInt(plyr.invRadioFreq.size());

        for (int int0 = 0; int0 < plyr.invRadioFreq.size(); int0++) {
            byteBufferWriter.putInt(plyr.invRadioFreq.get(int0));
        }

        PacketTypes.PacketType.SyncEquippedRadioFreq.send(connection);
    }

    static void receiveSyncEquippedRadioFreq(ByteBuffer byteBuffer, short var1) {
        short short0 = byteBuffer.getShort();
        int int0 = byteBuffer.getInt();
        IsoPlayer player = IDToPlayerMap.get(short0);
        if (player != null) {
            player.invRadioFreq.clear();

            for (int int1 = 0; int1 < int0; int1++) {
                player.invRadioFreq.add(byteBuffer.getInt());
            }

            for (int int2 = 0; int2 < player.invRadioFreq.size(); int2++) {
                System.out.println(player.invRadioFreq.get(int2));
            }
        }
    }

    public static void sendSneezingCoughing(short playerId, int sneezingCoughing, byte sneezeVar) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SneezeCough.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(playerId);
        byte byte0 = 0;
        if (sneezingCoughing % 2 == 0) {
            byte0 = (byte)(byte0 | 1);
        }

        if (sneezingCoughing > 2) {
            byte0 = (byte)(byte0 | 2);
        }

        if (sneezeVar > 1) {
            byte0 = (byte)(byte0 | 4);
        }

        byteBufferWriter.putByte(byte0);
        PacketTypes.PacketType.SneezeCough.send(connection);
    }

    static void receiveSneezeCough(ByteBuffer byteBuffer, short var1) {
        IsoPlayer player = IDToPlayerMap.get(byteBuffer.getShort());
        if (player != null) {
            byte byte0 = byteBuffer.get();
            boolean boolean0 = (byte0 & 1) == 0;
            boolean boolean1 = (byte0 & 2) != 0;
            int int0 = (byte0 & 4) == 0 ? 1 : 2;
            player.setVariable("Ext", boolean0 ? "Sneeze" + int0 : "Cough");
            player.Say(Translator.getText("IGUI_PlayerText_" + (boolean0 ? "Sneeze" : "Cough") + (boolean1 ? "Muffled" : "")));
            player.reportEvent("EventDoExt");
        }
    }

    public static void sendBurnCorpse(short playerId, short objectId) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.BurnCorpse.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(playerId);
        byteBufferWriter.putShort(objectId);
        PacketTypes.PacketType.SneezeCough.send(connection);
    }

    private static void rememberPlayerPosition(IsoPlayer player, float float0, float float1) {
        if (player != null && !player.isLocalPlayer()) {
            if (positions.containsKey(player.getOnlineID())) {
                positions.get(player.getOnlineID()).set(float0, float1);
            } else {
                positions.put(player.getOnlineID(), new Vector2(float0, float1));
            }

            WorldMapRemotePlayer worldMapRemotePlayer = WorldMapRemotePlayers.instance.getPlayerByID(player.getOnlineID());
            if (worldMapRemotePlayer != null) {
                worldMapRemotePlayer.setPosition(float0, float1);
            }
        }
    }

    static void receiveValidatePacket(ByteBuffer byteBuffer, short var1) {
        ValidatePacket validatePacket = new ValidatePacket();
        validatePacket.parse(byteBuffer, connection);
        validatePacket.log(connection, "receive-packet");
        if (validatePacket.isConsistent()) {
            validatePacket.process(connection);
        }
    }

    public static void sendValidatePacket(ValidatePacket packet) {
        packet.log(connection, "send-packet");
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Validate.doPacket(byteBufferWriter);
        packet.write(byteBufferWriter);
        PacketTypes.PacketType.Validate.send(connection);
    }

    public static enum RequestState {
        Start,
        Loading,
        Complete;
    }
}
