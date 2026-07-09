// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;

public class PacketTypes {
    public static final short ContainerDeadBody = 0;
    public static final short ContainerWorldObject = 1;
    public static final short ContainerObject = 2;
    public static final short ContainerVehicle = 3;
    public static final Map<Short, PacketTypes.PacketType> packetTypes = new TreeMap<>();
    public static final KahluaTable packetCountTable = LuaManager.platform.newTable();

    public static void doPingPacket(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(28);
    }

    public static KahluaTable getPacketCounts(int int0) {
        packetCountTable.wipe();
        if (GameClient.bClient) {
            for (PacketTypes.PacketType packetType : packetTypes.values()) {
                if (int0 == 1) {
                    packetCountTable.rawset(String.format("%03d-%s", packetType.id, packetType.name()), String.valueOf(packetType.serverPacketCount));
                } else {
                    packetCountTable.rawset(String.format("%03d-%s", packetType.id, packetType.name()), String.valueOf(packetType.clientPacketCount));
                }
            }
        }

        return packetCountTable;
    }

    static {
        for (PacketTypes.PacketType packetType0 : PacketTypes.PacketType.values()) {
            PacketTypes.PacketType packetType1 = packetTypes.put(packetType0.getId(), packetType0);
            if (packetType1 != null) {
                DebugLog.Multiplayer
                    .error(String.format("PacketType: duplicate \"%s\" \"%s\" id=%d", packetType1.name(), packetType0.name(), packetType0.getId()));
            }
        }
    }

    public interface CallbackClientProcess {
        void call(ByteBuffer bb, short packetType) throws IOException;
    }

    public interface CallbackServerProcess {
        void call(ByteBuffer bb, UdpConnection connection, short packetType) throws Exception;
    }

    private static class PacketAuthorization {
        private static void unauthorizedPacketPolicyLogUser(UdpConnection udpConnection, String string) {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
                PacketValidator.doLogUser(udpConnection, Userlog.UserlogType.UnauthorizedPacket, string, "Type8");
            }
        }

        private static void unauthorizedPacketPolicyKickUser(UdpConnection udpConnection, String string) {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
                PacketValidator.doKickUser(udpConnection, string, "Type8", null);
            }
        }

        private static void unauthorizedPacketPolicyBanUser(UdpConnection udpConnection, String string) throws Exception {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
                PacketValidator.doBanUser(udpConnection, string, "Type8");
            }
        }

        private static boolean isAuthorized(UdpConnection udpConnection, PacketTypes.PacketType packetType) throws Exception {
            boolean boolean0 = (udpConnection.accessLevel & packetType.requiredAccessLevel) != 0;
            if ((!boolean0 || packetType.serverProcess == null) && (!Core.bDebug || SystemDisabler.doKickInDebug)) {
                DebugLog.Multiplayer
                    .warn(
                        String.format(
                            "Unauthorized packet %s (%s) was received from user=\"%s\" (%s) ip %s %s",
                            packetType.name(),
                            PlayerType.toString(packetType.requiredAccessLevel),
                            udpConnection.username,
                            PlayerType.toString(udpConnection.accessLevel),
                            udpConnection.ip,
                            SteamUtils.isSteamModeEnabled() ? udpConnection.steamID : ""
                        )
                    );
                packetType.unauthorizedPacketPolicy.apply(udpConnection, packetType.name());
            }

            return boolean0;
        }

        public static enum Policy {
            Log(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyLogUser),
            Kick(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyKickUser),
            Ban(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyBanUser);

            private final PacketTypes.PacketAuthorization.UnauthorizedPacketPolicy policy;

            private Policy(PacketTypes.PacketAuthorization.UnauthorizedPacketPolicy unauthorizedPacketPolicy) {
                this.policy = unauthorizedPacketPolicy;
            }

            private void apply(UdpConnection udpConnection, String string) throws Exception {
                this.policy.call(udpConnection, string);
            }
        }

        public interface UnauthorizedPacketPolicy {
            void call(UdpConnection var1, String var2) throws Exception;
        }
    }

    public static enum PacketType {
        Validate(
            1,
            0,
            3,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveValidatePacket,
            GameClient::receiveValidatePacket,
            GameClient::receiveValidatePacket
        ),
        Login(2, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveLogin, null, null),
        HumanVisual(3, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveHumanVisual, GameClient::receiveHumanVisual, null),
        KeepAlive(4, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveKeepAlive, GameClient::receiveKeepAlive, GameClient::skipPacket),
        Vehicles(5, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveVehicles, GameClient::receiveVehicles, null),
        PlayerConnect(6, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerConnect, GameClient::receivePlayerConnect, null),
        VehiclesUnreliable(7, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveVehicles, GameClient::receiveVehicles, null),
        VehicleAuthorization(8, 2, 3, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveVehicleAuthorization, null),
        MetaGrid(9, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveMetaGrid, null),
        Helicopter(11, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveHelicopter, null),
        SyncIsoObject(12, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncIsoObject, GameClient::receiveSyncIsoObject, null),
        PlayerTimeout(13, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerTimeout, GameClient::receivePlayerTimeout),
        ServerMap(15, 1, 3, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveServerMap, GameClient::receiveServerMapLoading),
        PassengerMap(16, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePassengerMap, GameClient::receivePassengerMap, null),
        AddItemToMap(17, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddItemToMap, GameClient::receiveAddItemToMap, null),
        SentChunk(18, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, null, null),
        SyncClock(19, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveSyncClock, null),
        AddInventoryItemToContainer(
            20,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveAddInventoryItemToContainer,
            GameClient::receiveAddInventoryItemToContainer,
            null
        ),
        RemoveInventoryItemFromContainer(
            22,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveRemoveInventoryItemFromContainer,
            GameClient::receiveRemoveInventoryItemFromContainer,
            null
        ),
        RemoveItemFromSquare(
            23, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveItemFromSquare, GameClient::receiveRemoveItemFromSquare, null
        ),
        RequestLargeAreaZip(24, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestLargeAreaZip, null, null),
        Equip(25, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEquip, GameClient::receiveEquip, null),
        HitCharacter(26, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveHitCharacter, GameClient::receiveHitCharacter, null),
        AddCoopPlayer(27, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddCoopPlayer, GameClient::receiveAddCoopPlayer, null),
        WeaponHit(28, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWeaponHit, null, null),
        @Deprecated
        KillZombie(30, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, null, null),
        SandboxOptions(31, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSandboxOptions, GameClient::receiveSandboxOptions, null),
        SmashWindow(32, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSmashWindow, GameClient::receiveSmashWindow, null),
        PlayerDeath(33, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDeath, GameClient::receivePlayerDeath, null),
        RequestZipList(34, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestZipList, null, null),
        ItemStats(35, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveItemStats, GameClient::receiveItemStats, null),
        NotRequiredInZip(36, 0, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveNotRequiredInZip, null, null),
        RequestData(
            37,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveRequestData,
            GameClient::receiveRequestData,
            GameClient::receiveRequestData
        ),
        GlobalObjects(38, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalObjects, GameClient::receiveGlobalObjects, null),
        ZombieDeath(39, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieDeath, GameClient::receiveZombieDeath, null),
        AccessDenied(40, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, null, GameClient::receiveAccessDenied),
        PlayerDamage(41, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDamage, GameClient::receivePlayerDamage, null),
        Bandage(42, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBandage, GameClient::receiveBandage, null),
        EatFood(43, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEatFood, null, null),
        RequestItemsForContainer(44, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestItemsForContainer, null, null),
        Drink(45, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveDrink, null, null),
        SyncAlarmClock(46, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncAlarmClock, GameClient::receiveSyncAlarmClock, null),
        PacketCounts(47, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePacketCounts, GameClient::receivePacketCounts, null),
        SendModData(48, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendModData, null, null),
        RemoveContestedItemsFromInventory(
            49, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveRemoveContestedItemsFromInventory, null
        ),
        ScoreboardUpdate(
            50, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveScoreboardUpdate, GameClient::receiveScoreboardUpdate, null
        ),
        ReceiveModData(51, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveReceiveModData, null),
        ServerQuit(52, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveServerQuit, null),
        PlaySound(53, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlaySound, GameClient::receivePlaySound, null),
        WorldSound(54, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWorldSound, GameClient::receiveWorldSound, null),
        AddAmbient(55, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddAmbient, null),
        SyncClothing(56, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncClothing, GameClient::receiveSyncClothing, null),
        ClientCommand(57, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveClientCommand, GameClient::receiveClientCommand, null),
        ObjectModData(58, 2, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveObjectModData, GameClient::receiveObjectModData, null),
        ObjectChange(59, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveObjectChange, null),
        BloodSplatter(60, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveBloodSplatter, null),
        ZombieSound(61, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveZombieSound, null),
        ZombieDescriptors(62, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveZombieDescriptors, null),
        SlowFactor(63, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveSlowFactor, null),
        Weather(64, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveWeather, null),
        WorldMapPlayerPosition(
            65,
            3,
            1,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveWorldMapPlayerPosition,
            GameClient::receiveWorldMapPlayerPosition,
            null
        ),
        @Deprecated
        RequestPlayerData(67, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestPlayerData, null, null),
        RemoveCorpseFromMap(
            68, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveCorpseFromMap, GameClient::receiveRemoveCorpseFromMap, null
        ),
        AddCorpseToMap(69, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddCorpseToMap, GameClient::receiveAddCorpseToMap, null),
        BecomeCorpse(70, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveBecomeCorpse, null),
        StartFire(75, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStartFire, GameClient::receiveStartFire, null),
        UpdateItemSprite(
            76, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUpdateItemSprite, GameClient::receiveUpdateItemSprite, null
        ),
        StartRain(77, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveStartRain, null),
        StopRain(78, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveStopRain, null),
        WorldMessage(79, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWorldMessage, GameClient::receiveWorldMessage, null),
        getModData(80, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetModData, null, null),
        ReceiveCommand(81, 2, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReceiveCommand, null, null),
        ReloadOptions(82, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveReloadOptions, null),
        Kicked(83, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveKicked, GameClient::receiveKicked),
        ExtraInfo(84, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveExtraInfo, GameClient::receiveExtraInfo, null),
        AddItemInInventory(85, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddItemInInventory, null),
        ChangeSafety(86, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangeSafety, GameClient::receiveChangeSafety, null),
        Ping(87, 0, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePing, GameClient::receivePing, GameClient::receivePing),
        @Deprecated
        WriteLog(88, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWriteLog, null, null),
        AddXP(89, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddXp, GameClient::receiveAddXp, null),
        UpdateOverlaySprite(
            90, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUpdateOverlaySprite, GameClient::receiveUpdateOverlaySprite, null
        ),
        Checksum(
            91,
            1,
            3,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveChecksum,
            GameClient::receiveChecksum,
            GameClient::receiveChecksumLoading
        ),
        ConstructedZone(92, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveConstructedZone, GameClient::receiveConstructedZone, null),
        RegisterZone(94, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRegisterZone, GameClient::receiveRegisterZone, null),
        @Deprecated
        WoundInfection(97, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWoundInfection, GameClient::receiveWoundInfection, null),
        Stitch(98, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStitch, GameClient::receiveStitch, null),
        Disinfect(99, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveDisinfect, GameClient::receiveDisinfect, null),
        @Deprecated
        AdditionalPain(100, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAdditionalPain, null),
        RemoveGlass(101, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveGlass, GameClient::receiveRemoveGlass, null),
        Splint(102, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSplint, GameClient::receiveSplint, null),
        RemoveBullet(103, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveBullet, GameClient::receiveRemoveBullet, null),
        CleanBurn(104, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveCleanBurn, GameClient::receiveCleanBurn, null),
        SyncThumpable(105, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncThumpable, GameClient::receiveSyncThumpable, null),
        SyncDoorKey(106, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncDoorKey, GameClient::receiveSyncDoorKey, null),
        @Deprecated
        AddXpCommand(107, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddXpCommand, null),
        Teleport(108, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTeleport, GameClient::receiveTeleport, null),
        RemoveBlood(109, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveBlood, GameClient::receiveRemoveBlood, null),
        AddExplosiveTrap(
            110, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddExplosiveTrap, GameClient::receiveAddExplosiveTrap, null
        ),
        BodyDamageUpdate(
            112, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBodyDamageUpdate, GameClient::receiveBodyDamageUpdate, null
        ),
        SyncSafehouse(114, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncSafehouse, GameClient::receiveSyncSafehouse, null),
        SledgehammerDestroy(115, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSledgehammerDestroy, null, null),
        StopFire(116, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStopFire, GameClient::receiveStopFire, null),
        Cataplasm(117, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveCataplasm, GameClient::receiveCataplasm, null),
        AddAlarm(118, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddAlarm, null),
        PlaySoundEveryPlayer(119, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlaySoundEveryPlayer, null),
        SyncFurnace(120, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncFurnace, GameClient::receiveSyncFurnace, null),
        SendCustomColor(
            121, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendCustomColor, GameClient::receiveSendCustomColor, null
        ),
        SyncCompost(122, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncCompost, GameClient::receiveSyncCompost, null),
        ChangePlayerStats(
            123, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangePlayerStats, GameClient::receiveChangePlayerStats, null
        ),
        @Deprecated
        AddXpFromPlayerStatsUI(124, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddXp, GameClient::receiveAddXp, null),
        SyncXP(126, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncXP, GameClient::receiveSyncXP, null),
        @Deprecated
        PacketTypeShort(127, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, null, null),
        Userlog(128, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUserlog, GameClient::receiveUserlog, null),
        AddUserlog(129, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddUserlog, null, null),
        RemoveUserlog(130, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveUserlog, null, null),
        AddWarningPoint(131, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddWarningPoint, null, null),
        MessageForAdmin(132, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveMessageForAdmin, null),
        WakeUpPlayer(133, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWakeUpPlayer, GameClient::receiveWakeUpPlayer, null),
        @Deprecated
        SendTransactionID(134, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, null, null),
        GetDBSchema(135, 1, 2, 60, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetDBSchema, GameClient::receiveGetDBSchema, null),
        GetTableResult(136, 1, 2, 60, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetTableResult, GameClient::receiveGetTableResult, null),
        ExecuteQuery(137, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveExecuteQuery, null, null),
        ChangeTextColor(
            138, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangeTextColor, GameClient::receiveChangeTextColor, null
        ),
        SyncNonPvpZone(139, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncNonPvpZone, GameClient::receiveSyncNonPvpZone, null),
        SyncFaction(140, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncFaction, GameClient::receiveSyncFaction, null),
        SendFactionInvite(
            141, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendFactionInvite, GameClient::receiveSendFactionInvite, null
        ),
        AcceptedFactionInvite(
            142,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveAcceptedFactionInvite,
            GameClient::receiveAcceptedFactionInvite,
            null
        ),
        AddTicket(143, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddTicket, null, null),
        ViewTickets(144, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveViewTickets, GameClient::receiveViewTickets, null),
        RemoveTicket(145, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveTicket, null, null),
        RequestTrading(146, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestTrading, GameClient::receiveRequestTrading, null),
        TradingUIAddItem(
            147, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIAddItem, GameClient::receiveTradingUIAddItem, null
        ),
        TradingUIRemoveItem(
            148, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIRemoveItem, GameClient::receiveTradingUIRemoveItem, null
        ),
        TradingUIUpdateState(
            149, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIUpdateState, GameClient::receiveTradingUIUpdateState, null
        ),
        @Deprecated
        SendItemListNet(150, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendItemListNet, GameClient::receiveSendItemListNet, null),
        ChunkObjectState(
            151, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChunkObjectState, GameClient::receiveChunkObjectState, null
        ),
        ReadAnnotedMap(152, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReadAnnotedMap, null, null),
        RequestInventory(
            153, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestInventory, GameClient::receiveRequestInventory, null
        ),
        SendInventory(154, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendInventory, GameClient::receiveSendInventory, null),
        InvMngReqItem(155, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngReqItem, GameClient::receiveInvMngReqItem, null),
        InvMngGetItem(156, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngGetItem, GameClient::receiveInvMngGetItem, null),
        InvMngRemoveItem(
            157, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngRemoveItem, GameClient::receiveInvMngRemoveItem, null
        ),
        StartPause(158, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveStartPause, null),
        StopPause(159, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveStopPause, null),
        TimeSync(160, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTimeSync, GameClient::receiveTimeSync, null),
        @Deprecated
        SyncIsoObjectReq(
            161, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncIsoObjectReq, GameClient::receiveSyncIsoObjectReq, null
        ),
        @Deprecated
        PlayerSave(162, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerSave, null, null),
        @Deprecated
        SyncWorldObjectsReq(163, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveSyncWorldObjectsReq, null),
        @Deprecated
        SyncObjects(164, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncObjects, GameClient::receiveSyncObjects, null),
        SendPlayerProfile(166, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendPlayerProfile, null, null),
        LoadPlayerProfile(
            167, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveLoadPlayerProfile, GameClient::receiveLoadPlayerProfile, null
        ),
        SpawnRegion(171, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveSpawnRegion, GameClient::receiveSpawnRegion),
        PlayerDamageFromCarCrash(172, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerDamageFromCarCrash, null),
        PlayerAttachedItem(
            173, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerAttachedItem, GameClient::receivePlayerAttachedItem, null
        ),
        ZombieHelmetFalling(
            174, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieHelmetFalling, GameClient::receiveZombieHelmetFalling, null
        ),
        AddBrokenGlass(175, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddBrokenGlass, null),
        SyncPerks(177, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncPerks, GameClient::receiveSyncPerks, null),
        SyncWeight(178, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncWeight, GameClient::receiveSyncWeight, null),
        SyncInjuries(179, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncInjuries, GameClient::receiveSyncInjuries, null),
        SyncEquippedRadioFreq(
            181,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveSyncEquippedRadioFreq,
            GameClient::receiveSyncEquippedRadioFreq,
            null
        ),
        InitPlayerChat(182, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveInitPlayerChat, null),
        PlayerJoinChat(183, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerJoinChat, null),
        PlayerLeaveChat(184, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerLeaveChat, null),
        ChatMessageFromPlayer(185, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChatMessageFromPlayer, null, null),
        ChatMessageToPlayer(186, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveChatMessageToPlayer, null),
        PlayerStartPMChat(187, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerStartPMChat, null, null),
        AddChatTab(189, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveAddChatTab, null),
        RemoveChatTab(190, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveRemoveChatTab, null),
        PlayerConnectedToChat(191, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerConnectedToChat, null),
        PlayerNotFound(192, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receivePlayerNotFound, null),
        SendSafehouseInvite(
            193, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendSafehouseInvite, GameClient::receiveSendSafehouseInvite, null
        ),
        AcceptedSafehouseInvite(
            194,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveAcceptedSafehouseInvite,
            GameClient::receiveAcceptedSafehouseInvite,
            null
        ),
        ClimateManagerPacket(
            200, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveClimateManagerPacket, GameClient::receiveClimateManagerPacket, null
        ),
        IsoRegionServerPacket(201, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveIsoRegionServerPacket, null),
        IsoRegionClientRequestFullUpdate(
            202, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveIsoRegionClientRequestFullUpdate, null, null
        ),
        EventPacket(210, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEventPacket, GameClient::receiveEventPacket, null),
        Statistic(211, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStatistic, GameClient::receiveStatistic, null),
        StatisticRequest(
            212, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStatisticRequest, GameClient::receiveStatisticRequest, null
        ),
        PlayerUpdateReliable(213, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, null),
        ActionPacket(214, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveActionPacket, GameClient::receiveActionPacket, null),
        ZombieControl(215, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveZombieControl, null),
        PlayWorldSound(216, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayWorldSound, GameClient::receivePlayWorldSound, null),
        StopSound(217, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStopSound, GameClient::receiveStopSound, null),
        PlayerUpdate(218, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, null),
        ZombieSimulation(
            219, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, null
        ),
        PingFromClient(220, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePingFromClient, GameClient::receivePingFromClient, null),
        ZombieSimulationReliable(
            221, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, null
        ),
        EatBody(222, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEatBody, GameClient::receiveEatBody, null),
        Thump(223, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveThump, GameClient::receiveThump, null),
        SyncRadioData(224, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncRadioData, GameClient::receiveSyncRadioData, null),
        LoginQueueRequest2(
            225,
            0,
            3,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            LoginQueue::receiveServerLoginQueueRequest,
            null,
            LoginQueue::receiveClientLoginQueueRequest
        ),
        LoginQueueDone2(226, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, LoginQueue::receiveLoginQueueDone, null, null),
        ItemTransaction(
            227, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, ItemTransactionManager::receiveOnServer, ItemTransactionManager::receiveOnClient, null
        ),
        KickOutOfSafehouse(228, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveKickOutOfSafehouse, GameClient::receiveTeleport, null),
        SneezeCough(229, 3, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSneezeCough, GameClient::receiveSneezeCough, null),
        BurnCorpse(230, 2, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBurnCorpse, null, null),
        WaveSignal(300, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWaveSignal, GameClient::receiveWaveSignal, null),
        PlayerListensChannel(301, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerListensChannel, null, null),
        RadioServerData(
            302, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRadioServerData, GameClient::receiveRadioServerData, null
        ),
        RadioDeviceDataState(
            303, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRadioDeviceDataState, GameClient::receiveRadioDeviceDataState, null
        ),
        SyncCustomLightSettings(
            304,
            1,
            2,
            63,
            PacketTypes.PacketAuthorization.Policy.Kick,
            GameServer::receiveSyncCustomLightSettings,
            GameClient::receiveSyncCustomLightSettings,
            null
        ),
        ReplaceOnCooked(305, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReplaceOnCooked, null, null),
        PlayerDataRequest(306, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDataRequest, null, null),
        GlobalModData(32000, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalModData, GameClient::receiveGlobalModData, null),
        GlobalModDataRequest(32001, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalModDataRequest, null, null),
        RadioPostSilenceEvent(32002, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, null, GameClient::receiveRadioPostSilence, null);

        private final short id;
        private final byte requiredAccessLevel;
        private final PacketTypes.PacketAuthorization.Policy unauthorizedPacketPolicy;
        public final int PacketPriority;
        public final int PacketReliability;
        public final byte OrderingChannel;
        private final PacketTypes.CallbackServerProcess serverProcess;
        private final PacketTypes.CallbackClientProcess mainLoopHandlePacketInternal;
        private final PacketTypes.CallbackClientProcess gameLoadingDealWithNetData;
        public int incomePackets;
        public int outcomePackets;
        public int incomeBytes;
        public int outcomeBytes;
        public long clientPacketCount;
        public long serverPacketCount;

        private PacketType(
            int int1,
            int int2,
            int int3,
            int int4,
            PacketTypes.PacketAuthorization.Policy policy,
            PacketTypes.CallbackServerProcess callbackServerProcess,
            PacketTypes.CallbackClientProcess callbackClientProcess0,
            PacketTypes.CallbackClientProcess callbackClientProcess1
        ) {
            this(int1, int2, int3, (byte)0, (byte)int4, policy, callbackServerProcess, callbackClientProcess0, callbackClientProcess1);
        }

        private PacketType(
            int int1,
            int int2,
            int int3,
            byte byte1,
            byte byte0,
            PacketTypes.PacketAuthorization.Policy policy,
            PacketTypes.CallbackServerProcess callbackServerProcess,
            PacketTypes.CallbackClientProcess callbackClientProcess0,
            PacketTypes.CallbackClientProcess callbackClientProcess1
        ) {
            this.id = (short)int1;
            this.requiredAccessLevel = byte0;
            this.unauthorizedPacketPolicy = policy;
            this.PacketPriority = int2;
            this.PacketReliability = int3;
            this.OrderingChannel = byte1;
            this.serverProcess = callbackServerProcess;
            this.mainLoopHandlePacketInternal = callbackClientProcess0;
            this.gameLoadingDealWithNetData = callbackClientProcess1;
            this.resetStatistics();
        }

        public void resetStatistics() {
            this.incomePackets = 0;
            this.outcomePackets = 0;
            this.incomeBytes = 0;
            this.outcomeBytes = 0;
            this.clientPacketCount = 0L;
            this.serverPacketCount = 0L;
        }

        public void send(UdpConnection connection) {
            connection.endPacket(this.PacketPriority, this.PacketReliability, this.OrderingChannel);
            DebugLog.Packet
                .noise(
                    "type=%s username=%s connection=%d ip=%s size=%d",
                    this.name(),
                    connection.username,
                    connection.getConnectedGUID(),
                    connection.ip,
                    connection.getBufferPosition()
                );
        }

        public void doPacket(ByteBufferWriter bb) {
            bb.putByte((byte)-122);
            bb.putShort(this.getId());
        }

        public short getId() {
            return this.id;
        }

        public void onServerPacket(ByteBuffer bb, UdpConnection connection) throws Exception {
            if (PacketTypes.PacketAuthorization.isAuthorized(connection, this)) {
                DebugLog.Packet
                    .noise("type=%s username=%s connection=%d ip=%s", this.name(), connection.username, connection.getConnectedGUID(), connection.ip);
                this.serverProcess.call(bb, connection, this.getId());
            }
        }

        public void onMainLoopHandlePacketInternal(ByteBuffer bb) throws IOException {
            DebugLog.Packet.noise("type=%s", this.name());
            this.mainLoopHandlePacketInternal.call(bb, this.getId());
        }

        public boolean onGameLoadingDealWithNetData(ByteBuffer bb) {
            DebugLog.Packet.noise("type=%s", this.name());
            if (this.gameLoadingDealWithNetData == null) {
                DebugLog.Network.noise("Delay processing packet of type %s while loading game", this.name());
                return false;
            } else {
                try {
                    this.gameLoadingDealWithNetData.call(bb, this.getId());
                    return true;
                } catch (Exception exception) {
                    return false;
                }
            }
        }

        public void onUnauthorized(UdpConnection connection) {
            DebugLog.Multiplayer
                .warn(
                    String.format(
                        "On unauthorized packet %s (%d) was received from user=\"%s\" (%d) ip %s %s",
                        this.name(),
                        this.requiredAccessLevel,
                        connection.username,
                        connection.accessLevel,
                        connection.ip,
                        SteamUtils.isSteamModeEnabled() ? connection.steamID : ""
                    )
                );

            try {
                this.unauthorizedPacketPolicy.apply(connection, this.name());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
