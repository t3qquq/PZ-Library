// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import zombie.GameWindow;
import zombie.SystemDisabler;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SafetySystemManager;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.areas.NonPvpZone;
import zombie.network.packets.hit.Character;
import zombie.network.packets.hit.Hit;
import zombie.network.packets.hit.IMovable;
import zombie.network.packets.hit.IPositional;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.Zombie;
import zombie.scripting.objects.Recipe;
import zombie.util.StringUtils;
import zombie.util.Type;

public class PacketValidator {
    private static final int SUSPICIOUS_ACTIVITIES_MAX = 4;
    private final UpdateLimit ulSuspiciousActivity = new UpdateLimit(150000L);
    public final HashMap<String, PacketValidator.RecipeDetails> details = new HashMap<>();
    public final HashMap<String, PacketValidator.RecipeDetails> detailsFromClient = new HashMap<>();
    private boolean failed = false;
    private static final long USER_LOG_INTERVAL_MS = 1000L;
    private static final int MAX_TYPE_3 = 10;
    private static final int MAX_TYPE_4 = 101;
    private final UdpConnection connection;
    private final UpdateLimit ulTimeMultiplier = new UpdateLimit(this.getTimeMultiplierTimeout());
    private final UpdateLimit ulRecipeChecksumInterval = new UpdateLimit(this.getChecksumInterval());
    private final UpdateLimit ulRecipeChecksumTimeout = new UpdateLimit(this.getChecksumTimeout());
    private int salt;
    private int suspiciousActivityCounter;
    private String suspiciousActivityDescription;

    public PacketValidator(UdpConnection udpConnection) {
        this.connection = udpConnection;
        this.suspiciousActivityCounter = 0;
    }

    public void reset() {
        this.salt = Rand.Next(Integer.MAX_VALUE);
    }

    private boolean isReady() {
        IsoPlayer player = GameServer.getPlayerByRealUserName(this.connection.username);
        return this.connection.isFullyConnected()
            && this.connection.isConnectionGraceIntervalTimeout()
            && !GameServer.bFastForward
            && player != null
            && player.isAlive();
    }

    public int getSalt() {
        return this.salt;
    }

    private long getChecksumDelay() {
        return (long)(1000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
    }

    private long getChecksumInterval() {
        return (long)(4000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
    }

    private long getChecksumTimeout() {
        return (long)(10000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
    }

    public void failChecksum() {
        if (ServerOptions.instance.AntiCheatProtectionType21.getValue() && checkUser(this.connection)) {
            DebugLog.Multiplayer.warn("Checksum fail for \"%s\" (Type21)", this.connection.username);
            this.failed = true;
        }

        this.ulRecipeChecksumTimeout.Reset(this.getChecksumDelay());
    }

    public boolean isFailed() {
        return this.failed;
    }

    private void timeoutChecksum() {
        if (this.failed) {
            doKickUser(this.connection, this.getClass().getSimpleName(), "Type21", this.getDescription());
        } else {
            if (ServerOptions.instance.AntiCheatProtectionType22.getValue() && checkUser(this.connection)) {
                doKickUser(this.connection, this.getClass().getSimpleName(), "Type22", null);
            }

            this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
        }
    }

    public void successChecksum() {
        this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
    }

    public void sendChecksum(boolean boolean0, boolean boolean1, boolean boolean2) {
        this.salt = Rand.Next(Integer.MAX_VALUE);
        GameServer.sendValidatePacket(this.connection, boolean0, boolean1, boolean2);
        this.ulRecipeChecksumInterval.Reset(this.getChecksumInterval());
    }

    private long getTimeMultiplierTimeout() {
        return (long)(10000.0 * ServerOptions.getInstance().AntiCheatProtectionType24ThresholdMultiplier.getValue());
    }

    public void failTimeMultiplier(float float0) {
        if (ServerOptions.instance.AntiCheatProtectionType23.getValue() && checkUser(this.connection)) {
            doKickUser(this.connection, this.getClass().getSimpleName(), "Type23", String.valueOf(float0));
        }

        this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
    }

    public void timeoutTimeMultiplier() {
        if (ServerOptions.instance.AntiCheatProtectionType24.getValue() && checkUser(this.connection)) {
            doKickUser(this.connection, this.getClass().getSimpleName(), "Type24", null);
        }

        this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
    }

    public void successTimeMultiplier() {
        this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
    }

    public void update() {
        if (GameServer.bServer) {
            if (this.ulSuspiciousActivity.Check()) {
                this.updateSuspiciousActivityCounter();
            }

            if (this.isReady()) {
                if (!this.failed && this.ulRecipeChecksumInterval.Check()) {
                    this.sendChecksum(false, false, false);
                }

                if (this.ulRecipeChecksumTimeout.Check()) {
                    this.timeoutChecksum();
                }

                if (this.ulTimeMultiplier.Check()) {
                    this.timeoutTimeMultiplier();
                }
            } else {
                this.ulRecipeChecksumInterval.Reset(this.getChecksumInterval());
                this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
                this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
                this.failed = false;
            }
        }
    }

    public static boolean checkPVP(UdpConnection udpConnection, Character character1, Character character0, String string) {
        boolean boolean0 = checkPVP(character1.getCharacter(), character0.getCharacter())
            || SafetySystemManager.checkUpdateDelay(character1.getCharacter(), character0.getCharacter());
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType1.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string, "Type1", null);
        }

        return boolean0;
    }

    public static boolean checkSpeed(UdpConnection udpConnection, IMovable iMovable, String string) {
        float float0 = iMovable.getSpeed();
        double double0 = iMovable.isVehicle() ? ServerOptions.instance.SpeedLimit.getValue() : 10.0;
        boolean boolean0 = float0 <= double0 * ServerOptions.instance.AntiCheatProtectionType2ThresholdMultiplier.getValue();
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType2.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string, "Type2", String.valueOf(float0));
        }

        return boolean0;
    }

    public static boolean checkLongDistance(UdpConnection udpConnection, IPositional iPositional0, IPositional iPositional1, String string) {
        float float0 = IsoUtils.DistanceTo(iPositional1.getX(), iPositional1.getY(), iPositional0.getX(), iPositional0.getY());
        boolean boolean0 = float0 <= udpConnection.ReleventRange * 10 * ServerOptions.instance.AntiCheatProtectionType3ThresholdMultiplier.getValue();
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType3.getValue() && checkUser(udpConnection)) {
            if (udpConnection.validator.checkSuspiciousActivity("Type3")) {
                doKickUser(udpConnection, string, "Type3", String.valueOf(float0));
            } else {
                doLogUser(udpConnection, Userlog.UserlogType.SuspiciousActivity, string, "Type3");
            }
        }

        return boolean0;
    }

    public static boolean checkDamage(UdpConnection udpConnection, Hit hit, String string) {
        float float0 = hit.getDamage();
        boolean boolean0 = float0 <= 101.0 * ServerOptions.instance.AntiCheatProtectionType4ThresholdMultiplier.getValue();
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType4.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string, "Type4", String.valueOf(float0));
        }

        return boolean0;
    }

    public static boolean checkOwner(UdpConnection udpConnection1, Zombie zombie1, String string) {
        IsoZombie zombie0 = (IsoZombie)zombie1.getCharacter();
        UdpConnection udpConnection0 = zombie0.authOwner;
        boolean boolean0 = udpConnection0 == udpConnection1 && System.currentTimeMillis() - zombie0.lastChangeOwner > 2000L;
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType5.getValue() && checkUser(udpConnection1)) {
            if (udpConnection1.validator.checkSuspiciousActivity("Type5")) {
                doKickUser(udpConnection1, string, "Type5", Optional.ofNullable(udpConnection0).map(udpConnection -> udpConnection.username).orElse(""));
            } else {
                doLogUser(udpConnection1, Userlog.UserlogType.SuspiciousActivity, string, "Type5");
            }
        }

        return boolean0;
    }

    public static boolean checkTarget(UdpConnection udpConnection, Player player1, String string) {
        IsoPlayer player0 = player1.getPlayer();
        boolean boolean0 = Arrays.stream(udpConnection.players).anyMatch(player1x -> player1x.getOnlineID() == player0.getOnlineID());
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType6.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string, "Type6", player0.getUsername());
        }

        return boolean0;
    }

    public static boolean checkSafehouseAuth(UdpConnection udpConnection, String string0, String string1) {
        boolean boolean0 = StringUtils.isNullOrEmpty(string0) || string0.equals(udpConnection.username) || udpConnection.accessLevel >= 16;
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType7.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string1, "Type7", string0);
        }

        return boolean0;
    }

    public static boolean checkShortDistance(UdpConnection udpConnection, IPositional iPositional0, IPositional iPositional1, String string) {
        float float0 = IsoUtils.DistanceTo(iPositional1.getX(), iPositional1.getY(), iPositional0.getX(), iPositional0.getY());
        boolean boolean0 = float0 <= 10.0 * ServerOptions.instance.AntiCheatProtectionType3ThresholdMultiplier.getValue();
        if (!boolean0 && ServerOptions.instance.AntiCheatProtectionType3.getValue() && checkUser(udpConnection)) {
            doKickUser(udpConnection, string, "Type3", String.valueOf(float0));
        }

        return boolean0;
    }

    private static boolean isUntouchable(UdpConnection udpConnection) {
        return !udpConnection.isFullyConnected()
            || PlayerType.isPrivileged(udpConnection.accessLevel)
            || Arrays.stream(udpConnection.players).filter(Objects::nonNull).anyMatch(IsoGameCharacter::isGodMod);
    }

    public static boolean checkUser(UdpConnection udpConnection) {
        return doAntiCheatProtection() && !isUntouchable(udpConnection);
    }

    public boolean checkSuspiciousActivity(String string) {
        if (this.suspiciousActivityCounter <= 4) {
            this.suspiciousActivityCounter++;
            this.suspiciousActivityDescription = String.format("player=\"%s\" type=\"%s\"", this.connection.username, string);
            DebugLog.Multiplayer.noise("SuspiciousActivity increase: counter=%d %s", this.suspiciousActivityCounter, this.suspiciousActivityDescription);
        }

        return this.suspiciousActivityCounter > 4;
    }

    public void updateSuspiciousActivityCounter() {
        if (this.suspiciousActivityCounter > 0) {
            this.suspiciousActivityCounter--;
            DebugLog.Multiplayer.warn("SuspiciousActivity decrease: counter=%d %s", this.suspiciousActivityCounter, this.suspiciousActivityDescription);
        } else {
            this.suspiciousActivityCounter = 0;
        }
    }

    public static void doLogUser(UdpConnection udpConnection, Userlog.UserlogType userlogType, String string0, String string1) {
        long long0 = System.currentTimeMillis();
        DebugLog.Multiplayer.warn("Log: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, string0, string1);
        if (long0 > udpConnection.lastUnauthorizedPacket) {
            udpConnection.lastUnauthorizedPacket = long0 + 1000L;
            ServerWorldDatabase.instance.addUserlog(udpConnection.username, userlogType, string0, "AntiCheat" + string1, 1);
        }
    }

    public static void doKickUser(UdpConnection udpConnection, String string0, String string1, String string2) {
        ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Kicked, string0, "AntiCheat" + string1, 1);
        DebugLog.Multiplayer.warn("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" description=\"%s\"", udpConnection.username, string0, string1, string2);
        GameServer.kick(udpConnection, "UI_Policy_Kick", string1);
        udpConnection.forceDisconnect(string0);
        GameServer.addDisconnect(udpConnection);
    }

    public static void doBanUser(UdpConnection udpConnection, String string0, String string1) throws Exception {
        ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Banned, string0, "AntiCheat" + string1, 1);
        DebugLog.Multiplayer.warn("Ban: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, string0, string1);
        ServerWorldDatabase.instance.banUser(udpConnection.username, true);
        if (SteamUtils.isSteamModeEnabled()) {
            String string2 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
            ServerWorldDatabase.instance.banSteamID(string2, string0, true);
        } else {
            ServerWorldDatabase.instance.banIp(udpConnection.ip, udpConnection.username, string0, true);
        }

        GameServer.kick(udpConnection, "UI_Policy_Ban", string1);
        udpConnection.forceDisconnect(string0);
        GameServer.addDisconnect(udpConnection);
    }

    private static boolean checkPVP(IsoGameCharacter character, IsoMovingObject movingObject) {
        IsoPlayer player0 = Type.tryCastTo(character, IsoPlayer.class);
        IsoPlayer player1 = Type.tryCastTo(movingObject, IsoPlayer.class);
        if (player1 != null) {
            if (player1.isGodMod()
                || !ServerOptions.instance.PVP.getValue()
                || ServerOptions.instance.SafetySystem.getValue()
                    && character.getSafety().isEnabled()
                    && ((IsoGameCharacter)movingObject).getSafety().isEnabled()) {
                return false;
            }

            if (NonPvpZone.getNonPvpZone((int)movingObject.getX(), (int)movingObject.getY()) != null) {
                return false;
            }

            if (player0 != null && NonPvpZone.getNonPvpZone((int)character.getX(), (int)character.getY()) != null) {
                return false;
            }

            if (player0 != null && !player0.factionPvp && !player1.factionPvp) {
                Faction faction0 = Faction.getPlayerFaction(player0);
                Faction faction1 = Faction.getPlayerFaction(player1);
                if (faction1 != null && faction0 == faction1) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean doAntiCheatProtection() {
        return !GameServer.bCoop && (!Core.bDebug || SystemDisabler.doKickInDebug);
    }

    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder("Recipes CRC details");
        if (GameServer.bServer) {
            Set set = this.details
                .entrySet()
                .stream()
                .filter(entry -> this.detailsFromClient.get(entry.getKey()) != null && this.detailsFromClient.get(entry.getKey()).crc == entry.getValue().crc)
                .map(Entry::getKey)
                .collect(Collectors.toSet());
            set.forEach(string -> {
                this.detailsFromClient.remove(string);
                this.details.remove(string);
            });
            stringBuilder.append("\nServer start size=").append(this.details.size());
            this.details.values().forEach(recipeDetails -> stringBuilder.append(recipeDetails.getDescription()));
            stringBuilder.append("\nServer end\nClient start size=").append(this.detailsFromClient.size());
            this.detailsFromClient.values().forEach(recipeDetails -> stringBuilder.append(recipeDetails.getDescription()));
            stringBuilder.append("\nClient end");
        }

        return stringBuilder.toString();
    }

    public static enum CheckState {
        None,
        Sent,
        Success;
    }

    public static class RecipeDetails {
        private final String name;
        private final long crc;
        private final int timeToMake;
        private final ArrayList<PacketValidator.RecipeDetails.Skill> skills = new ArrayList<>();
        private final ArrayList<String> items = new ArrayList<>();
        private final String type;
        private final String module;
        private final int count;

        public long getCRC() {
            return this.crc;
        }

        public RecipeDetails(
            String string0,
            long long0,
            int int0,
            ArrayList<Recipe.RequiredSkill> arrayList0,
            ArrayList<Recipe.Source> arrayList1,
            String string1,
            String string2,
            int int1
        ) {
            this.name = string0;
            this.crc = long0;
            this.timeToMake = int0;
            this.type = string1;
            this.module = string2;
            this.count = int1;
            if (arrayList0 != null) {
                for (Recipe.RequiredSkill requiredSkill : arrayList0) {
                    this.skills.add(new PacketValidator.RecipeDetails.Skill(requiredSkill.getPerk().getName(), requiredSkill.getLevel()));
                }
            }

            for (Recipe.Source source : arrayList1) {
                this.items.addAll(source.getItems());
            }
        }

        public RecipeDetails(ByteBuffer byteBuffer) {
            this.name = GameWindow.ReadString(byteBuffer);
            this.crc = byteBuffer.getLong();
            this.timeToMake = byteBuffer.getInt();
            this.type = GameWindow.ReadString(byteBuffer);
            this.module = GameWindow.ReadString(byteBuffer);
            this.count = byteBuffer.getInt();
            int int0 = byteBuffer.getInt();

            for (int int1 = 0; int1 < int0; int1++) {
                this.items.add(GameWindow.ReadString(byteBuffer));
            }

            int int2 = byteBuffer.getInt();

            for (int int3 = 0; int3 < int2; int3++) {
                this.skills.add(new PacketValidator.RecipeDetails.Skill(GameWindow.ReadString(byteBuffer), byteBuffer.getInt()));
            }
        }

        public void write(ByteBufferWriter byteBufferWriter) {
            byteBufferWriter.putUTF(this.name);
            byteBufferWriter.putLong(this.crc);
            byteBufferWriter.putInt(this.timeToMake);
            byteBufferWriter.putUTF(this.type);
            byteBufferWriter.putUTF(this.module);
            byteBufferWriter.putInt(this.count);
            byteBufferWriter.putInt(this.items.size());

            for (String string : this.items) {
                byteBufferWriter.putUTF(string);
            }

            byteBufferWriter.putInt(this.skills.size());

            for (PacketValidator.RecipeDetails.Skill skill : this.skills) {
                byteBufferWriter.putUTF(skill.name);
                byteBufferWriter.putInt(skill.value);
            }
        }

        public String getDescription() {
            return "\n\tRecipe: name=\""
                + this.name
                + "\" crc="
                + this.crc
                + " time="
                + this.timeToMake
                + " type=\""
                + this.type
                + "\" module=\""
                + this.module
                + "\" count="
                + this.count
                + " items=["
                + String.join(",", this.items)
                + "] skills=["
                + this.skills.stream().map(skill -> "\"" + skill.name + "\": " + skill.value).collect(Collectors.joining(","))
                + "]";
        }

        public static class Skill {
            private final String name;
            private final int value;

            public Skill(String string, int int0) {
                this.name = string;
                this.value = int0;
            }
        }
    }
}
