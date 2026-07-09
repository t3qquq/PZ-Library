// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderSettings;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.SpeedControls;
import zombie.ui.UIManager;

/**
 * TurboTuTone.
 */
public class ThunderStorm {
    public static int MAP_MIN_X = -3000;
    public static int MAP_MIN_Y = -3000;
    public static int MAP_MAX_X = 25000;
    public static int MAP_MAX_Y = 20000;
    private boolean hasActiveThunderClouds = false;
    private float cloudMaxRadius = 20000.0F;
    private ThunderStorm.ThunderEvent[] events = new ThunderStorm.ThunderEvent[30];
    private ThunderStorm.ThunderCloud[] clouds = new ThunderStorm.ThunderCloud[3];
    private ClimateManager climateManager;
    private ArrayList<ThunderStorm.ThunderCloud> cloudCache;
    private boolean donoise = false;
    private int strikeRadius = 4000;
    private final ThunderStorm.PlayerLightningInfo[] lightningInfos = new ThunderStorm.PlayerLightningInfo[4];
    private ThunderStorm.ThunderEvent networkThunderEvent = new ThunderStorm.ThunderEvent();
    private ThunderStorm.ThunderCloud dummyCloud;

    public ArrayList<ThunderStorm.ThunderCloud> getClouds() {
        if (this.cloudCache == null) {
            this.cloudCache = new ArrayList<>(this.clouds.length);

            for (int int0 = 0; int0 < this.clouds.length; int0++) {
                this.cloudCache.add(this.clouds[int0]);
            }
        }

        return this.cloudCache;
    }

    public ThunderStorm(ClimateManager climmgr) {
        this.climateManager = climmgr;

        for (int int0 = 0; int0 < this.events.length; int0++) {
            this.events[int0] = new ThunderStorm.ThunderEvent();
        }

        for (int int1 = 0; int1 < this.clouds.length; int1++) {
            this.clouds[int1] = new ThunderStorm.ThunderCloud();
        }

        for (int int2 = 0; int2 < 4; int2++) {
            this.lightningInfos[int2] = new ThunderStorm.PlayerLightningInfo();
        }
    }

    private ThunderStorm.ThunderEvent getFreeEvent() {
        for (int int0 = 0; int0 < this.events.length; int0++) {
            if (!this.events[int0].isRunning) {
                return this.events[int0];
            }
        }

        return null;
    }

    private ThunderStorm.ThunderCloud getFreeCloud() {
        for (int int0 = 0; int0 < this.clouds.length; int0++) {
            if (!this.clouds[int0].isRunning) {
                return this.clouds[int0];
            }
        }

        return null;
    }

    private ThunderStorm.ThunderCloud getCloud(int var1) {
        byte byte0 = 0;
        return byte0 < this.clouds.length ? this.clouds[byte0] : null;
    }

    public boolean HasActiveThunderClouds() {
        return this.hasActiveThunderClouds;
    }

    public void noise(String s) {
        if (this.donoise && (Core.bDebug || GameServer.bServer && GameServer.bDebug)) {
            DebugLog.log("thunderstorm: " + s);
        }
    }

    public void stopAllClouds() {
        for (int int0 = 0; int0 < this.clouds.length; int0++) {
            this.stopCloud(int0);
        }
    }

    public void stopCloud(int id) {
        ThunderStorm.ThunderCloud thunderCloud = this.getCloud(id);
        if (thunderCloud != null) {
            thunderCloud.isRunning = false;
        }
    }

    private static float addToAngle(float float0, float float1) {
        float0 += float1;
        if (float0 > 360.0F) {
            float0 -= 360.0F;
        } else if (float0 < 0.0F) {
            float0 += 360.0F;
        }

        return float0;
    }

    public static int getMapDiagonal() {
        int int0 = MAP_MAX_X - MAP_MIN_X;
        int int1 = MAP_MAX_Y - MAP_MIN_Y;
        int int2 = (int)Math.sqrt(Math.pow(int0, 2.0) + Math.pow(int1, 2.0));
        return int2 / 2;
    }

    public void startThunderCloud(float str, float angle, float radius, float eventFreq, float thunderRatio, double duration, boolean targetRandomPlayer) {
        this.startThunderCloud(str, angle, radius, eventFreq, thunderRatio, duration, targetRandomPlayer);
    }

    public ThunderStorm.ThunderCloud startThunderCloud(
        float str, float angle, float radius, float eventFreq, float thunderRatio, double duration, boolean targetRandomPlayer, float percentageOffset
    ) {
        if (GameClient.bClient) {
            return null;
        } else {
            ThunderStorm.ThunderCloud thunderCloud = this.getFreeCloud();
            if (thunderCloud != null) {
                angle = addToAngle(angle, Rand.Next(-10.0F, 10.0F));
                thunderCloud.startTime = GameTime.instance.getWorldAgeHours();
                thunderCloud.endTime = thunderCloud.startTime + duration;
                thunderCloud.duration = duration;
                thunderCloud.strength = ClimateManager.clamp01(str);
                thunderCloud.angle = angle;
                thunderCloud.radius = radius;
                if (thunderCloud.radius > this.cloudMaxRadius) {
                    thunderCloud.radius = this.cloudMaxRadius;
                }

                thunderCloud.eventFrequency = eventFreq;
                thunderCloud.thunderRatio = ClimateManager.clamp01(thunderRatio);
                thunderCloud.percentageOffset = PZMath.clamp_01(percentageOffset);
                float float0 = addToAngle(angle, 180.0F);
                int int0 = MAP_MAX_X - MAP_MIN_X;
                int int1 = MAP_MAX_Y - MAP_MIN_Y;
                int int2 = Rand.Next(MAP_MIN_X + int0 / 5, MAP_MAX_X - int0 / 5);
                int int3 = Rand.Next(MAP_MIN_Y + int1 / 5, MAP_MAX_Y - int1 / 5);
                if (targetRandomPlayer) {
                    if (!GameServer.bServer) {
                        IsoPlayer player0 = IsoPlayer.getInstance();
                        if (player0 != null) {
                            int2 = (int)player0.getX();
                            int3 = (int)player0.getY();
                        }
                    } else {
                        if (GameServer.Players.isEmpty()) {
                            DebugLog.log("Thundercloud couldnt target player...");
                            return null;
                        }

                        ArrayList arrayList = GameServer.getPlayers();

                        for (int int4 = arrayList.size() - 1; int4 >= 0; int4--) {
                            if (((IsoPlayer)arrayList.get(int4)).getCurrentSquare() == null) {
                                arrayList.remove(int4);
                            }
                        }

                        if (!arrayList.isEmpty()) {
                            IsoPlayer player1 = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
                            int2 = player1.getCurrentSquare().getX();
                            int3 = player1.getCurrentSquare().getY();
                        }
                    }
                }

                thunderCloud.setCenter(int2, int3, angle);
                thunderCloud.isRunning = true;
                thunderCloud.suspendTimer.init(3);
                return thunderCloud;
            } else {
                return null;
            }
        }
    }

    public void update(double currentTime) {
        if (!GameClient.bClient || GameServer.bServer) {
            this.hasActiveThunderClouds = false;

            for (int int0 = 0; int0 < this.clouds.length; int0++) {
                ThunderStorm.ThunderCloud thunderCloud = this.clouds[int0];
                if (thunderCloud.isRunning) {
                    if (currentTime < thunderCloud.endTime) {
                        float float0 = (float)((currentTime - thunderCloud.startTime) / thunderCloud.duration);
                        if (thunderCloud.percentageOffset > 0.0F) {
                            float0 = thunderCloud.percentageOffset + (1.0F - thunderCloud.percentageOffset) * float0;
                        }

                        thunderCloud.currentX = (int)ClimateManager.lerp(float0, thunderCloud.startX, thunderCloud.endX);
                        thunderCloud.currentY = (int)ClimateManager.lerp(float0, thunderCloud.startY, thunderCloud.endY);
                        thunderCloud.suspendTimer.update();
                        this.hasActiveThunderClouds = true;
                        if (thunderCloud.suspendTimer.finished()) {
                            float float1 = Rand.Next(3.5F - 3.0F * thunderCloud.strength, 24.0F - 20.0F * thunderCloud.strength);
                            thunderCloud.suspendTimer.init((int)(float1 * 60.0F));
                            float float2 = Rand.Next(0.0F, 1.0F);
                            if (float2 < 0.6F) {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0F) / 3;
                            } else if (float2 < 0.9F) {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0F) / 4 * 3;
                            } else {
                                this.strikeRadius = (int)(thunderCloud.radius / 2.0F);
                            }

                            if (Rand.Next(0.0F, 1.0F) < thunderCloud.thunderRatio) {
                                this.noise("trigger thunder event");
                                this.triggerThunderEvent(
                                    Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius),
                                    Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius),
                                    true,
                                    true,
                                    Rand.Next(0.0F, 1.0F) > 0.4F
                                );
                            } else {
                                this.triggerThunderEvent(
                                    Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius),
                                    Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius),
                                    false,
                                    false,
                                    true
                                );
                                this.noise("trigger rumble event");
                            }
                        }
                    } else {
                        thunderCloud.isRunning = false;
                    }
                }
            }
        }

        if (GameClient.bClient || !GameServer.bServer) {
            for (int int1 = 0; int1 < 4; int1++) {
                ThunderStorm.PlayerLightningInfo playerLightningInfo = this.lightningInfos[int1];
                if (playerLightningInfo.lightningState == ThunderStorm.LightningState.ApplyLightning) {
                    playerLightningInfo.timer.update();
                    if (!playerLightningInfo.timer.finished()) {
                        playerLightningInfo.lightningMod = ClimateManager.clamp01(playerLightningInfo.timer.ratio());
                        this.climateManager.dayLightStrength.finalValue = this.climateManager.dayLightStrength.finalValue
                            + (1.0F - this.climateManager.dayLightStrength.finalValue) * (1.0F - playerLightningInfo.lightningMod);
                        IsoPlayer player = IsoPlayer.players[int1];
                        if (player != null) {
                            player.dirtyRecalcGridStackTime = 1.0F;
                        }
                    } else {
                        this.noise("apply lightning done.");
                        playerLightningInfo.timer.init(2);
                        playerLightningInfo.lightningStrength = 0.0F;
                        playerLightningInfo.lightningState = ThunderStorm.LightningState.Idle;
                    }
                }
            }

            boolean boolean0 = SpeedControls.instance.getCurrentGameSpeed() > 1;
            boolean boolean1 = false;
            boolean boolean2 = false;

            for (int int2 = 0; int2 < this.events.length; int2++) {
                ThunderStorm.ThunderEvent thunderEvent = this.events[int2];
                if (thunderEvent.isRunning) {
                    thunderEvent.soundDelay.update();
                    if (thunderEvent.soundDelay.finished()) {
                        thunderEvent.isRunning = false;
                        boolean boolean3 = true;
                        if (UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() > 1) {
                            boolean3 = false;
                        }

                        if (boolean3 && !Core.SoundDisabled && FMODManager.instance.getNumListeners() > 0) {
                            if (thunderEvent.doStrike && (!boolean0 || !boolean1)) {
                                this.noise("thunder sound");
                                GameSound gameSound0 = GameSounds.getSound("Thunder");
                                GameSoundClip gameSoundClip0 = gameSound0 == null ? null : gameSound0.getRandomClip();
                                if (gameSoundClip0 != null && gameSoundClip0.eventDescription != null) {
                                    long long0 = gameSoundClip0.eventDescription.address;
                                    long long1 = javafmod.FMOD_Studio_System_CreateEventInstance(long0);
                                    javafmod.FMOD_Studio_EventInstance3D(long1, thunderEvent.eventX, thunderEvent.eventY, 100.0F);
                                    javafmod.FMOD_Studio_EventInstance_SetVolume(long1, gameSoundClip0.getEffectiveVolume());
                                    javafmod.FMOD_Studio_StartEvent(long1);
                                    javafmod.FMOD_Studio_ReleaseEventInstance(long1);
                                }
                            }

                            if (thunderEvent.doRumble && (!boolean0 || !boolean2)) {
                                this.noise("rumble sound");
                                GameSound gameSound1 = GameSounds.getSound("RumbleThunder");
                                GameSoundClip gameSoundClip1 = gameSound1 == null ? null : gameSound1.getRandomClip();
                                if (gameSoundClip1 != null && gameSoundClip1.eventDescription != null) {
                                    long long2 = gameSoundClip1.eventDescription.address;
                                    long long3 = javafmod.FMOD_Studio_System_CreateEventInstance(long2);
                                    javafmod.FMOD_Studio_EventInstance3D(long3, thunderEvent.eventX, thunderEvent.eventY, 200.0F);
                                    javafmod.FMOD_Studio_EventInstance_SetVolume(long3, gameSoundClip1.getEffectiveVolume());
                                    javafmod.FMOD_Studio_StartEvent(long3);
                                    javafmod.FMOD_Studio_ReleaseEventInstance(long3);
                                }
                            }
                        }
                    } else {
                        boolean1 = boolean1 || thunderEvent.doStrike;
                        boolean2 = boolean2 || thunderEvent.doRumble;
                    }
                }
            }
        }
    }

    public void applyLightningForPlayer(RenderSettings.PlayerRenderSettings renderSettings, int plrIndex, IsoPlayer player) {
        ThunderStorm.PlayerLightningInfo playerLightningInfo = this.lightningInfos[plrIndex];
        if (playerLightningInfo.lightningState == ThunderStorm.LightningState.ApplyLightning) {
            ClimateColorInfo climateColorInfo = renderSettings.CM_GlobalLight;
            playerLightningInfo.lightningColor.getExterior().r = climateColorInfo.getExterior().r
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().r);
            playerLightningInfo.lightningColor.getExterior().g = climateColorInfo.getExterior().g
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().g);
            playerLightningInfo.lightningColor.getExterior().b = climateColorInfo.getExterior().b
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().b);
            playerLightningInfo.lightningColor.getInterior().r = climateColorInfo.getInterior().r
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().r);
            playerLightningInfo.lightningColor.getInterior().g = climateColorInfo.getInterior().g
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().g);
            playerLightningInfo.lightningColor.getInterior().b = climateColorInfo.getInterior().b
                + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().b);
            playerLightningInfo.lightningColor.interp(renderSettings.CM_GlobalLight, playerLightningInfo.lightningMod, playerLightningInfo.outColor);
            renderSettings.CM_GlobalLight.getExterior().r = playerLightningInfo.outColor.getExterior().r;
            renderSettings.CM_GlobalLight.getExterior().g = playerLightningInfo.outColor.getExterior().g;
            renderSettings.CM_GlobalLight.getExterior().b = playerLightningInfo.outColor.getExterior().b;
            renderSettings.CM_GlobalLight.getInterior().r = playerLightningInfo.outColor.getInterior().r;
            renderSettings.CM_GlobalLight.getInterior().g = playerLightningInfo.outColor.getInterior().g;
            renderSettings.CM_GlobalLight.getInterior().b = playerLightningInfo.outColor.getInterior().b;
            renderSettings.CM_Ambient = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, renderSettings.CM_Ambient);
            renderSettings.CM_DayLightStrength = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, renderSettings.CM_DayLightStrength);
            renderSettings.CM_Desaturation = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0F, renderSettings.CM_Desaturation);
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                renderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, renderSettings.CM_GlobalLightIntensity);
            } else {
                renderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0F, renderSettings.CM_GlobalLightIntensity);
            }
        }
    }

    public boolean isModifyingNight() {
        return false;
    }

    public void triggerThunderEvent(int x, int y, boolean doStrike, boolean doLightning, boolean doRumble) {
        if (GameServer.bServer) {
            this.networkThunderEvent.eventX = x;
            this.networkThunderEvent.eventY = y;
            this.networkThunderEvent.doStrike = doStrike;
            this.networkThunderEvent.doLightning = doLightning;
            this.networkThunderEvent.doRumble = doRumble;
            this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)2, null);
        } else if (!GameClient.bClient) {
            this.enqueueThunderEvent(x, y, doStrike, doLightning, doRumble);
        }
    }

    public void writeNetThunderEvent(ByteBuffer output) throws IOException {
        output.putInt(this.networkThunderEvent.eventX);
        output.putInt(this.networkThunderEvent.eventY);
        output.put((byte)(this.networkThunderEvent.doStrike ? 1 : 0));
        output.put((byte)(this.networkThunderEvent.doLightning ? 1 : 0));
        output.put((byte)(this.networkThunderEvent.doRumble ? 1 : 0));
    }

    public void readNetThunderEvent(ByteBuffer input) throws IOException {
        int int0 = input.getInt();
        int int1 = input.getInt();
        boolean boolean0 = input.get() == 1;
        boolean boolean1 = input.get() == 1;
        boolean boolean2 = input.get() == 1;
        this.enqueueThunderEvent(int0, int1, boolean0, boolean1, boolean2);
    }

    public void enqueueThunderEvent(int x, int y, boolean doStrike, boolean doLightning, boolean doRumble) {
        LuaEventManager.triggerEvent("OnThunderEvent", x, y, doStrike, doLightning, doRumble);
        if (doStrike || doRumble) {
            int int0 = 9999999;

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                IsoPlayer player0 = IsoPlayer.players[int1];
                if (player0 != null) {
                    int int2 = this.GetDistance((int)player0.getX(), (int)player0.getY(), x, y);
                    if (int2 < int0) {
                        int0 = int2;
                    }

                    if (doLightning) {
                        this.lightningInfos[int1].distance = int2;
                        this.lightningInfos[int1].x = x;
                        this.lightningInfos[int1].y = y;
                    }
                }
            }

            this.noise("dist to player = " + int0);
            if (int0 < 10000) {
                ThunderStorm.ThunderEvent thunderEvent = this.getFreeEvent();
                if (thunderEvent != null) {
                    thunderEvent.doRumble = doRumble;
                    thunderEvent.doStrike = doStrike;
                    thunderEvent.eventX = x;
                    thunderEvent.eventY = y;
                    thunderEvent.isRunning = true;
                    thunderEvent.soundDelay.init((int)(int0 / 300.0F * 60.0F));
                    if (doLightning) {
                        for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                            IsoPlayer player1 = IsoPlayer.players[int3];
                            if (player1 != null && this.lightningInfos[int3].distance < 7500.0F) {
                                float float0 = 1.0F - this.lightningInfos[int3].distance / 7500.0F;
                                this.lightningInfos[int3].lightningState = ThunderStorm.LightningState.ApplyLightning;
                                if (float0 > this.lightningInfos[int3].lightningStrength) {
                                    this.lightningInfos[int3].lightningStrength = float0;
                                    this.lightningInfos[int3].timer.init(20 + (int)(80.0F * this.lightningInfos[int3].lightningStrength));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int GetDistance(int int2, int int0, int int3, int int1) {
        return (int)Math.sqrt(Math.pow(int2 - int3, 2.0) + Math.pow(int0 - int1, 2.0));
    }

    /**
     * IO
     */
    public void save(DataOutputStream output) throws IOException {
        if (GameClient.bClient && !GameServer.bServer) {
            output.writeByte(0);
        } else {
            output.writeByte(this.clouds.length);

            for (int int0 = 0; int0 < this.clouds.length; int0++) {
                ThunderStorm.ThunderCloud thunderCloud = this.clouds[int0];
                output.writeBoolean(thunderCloud.isRunning);
                if (thunderCloud.isRunning) {
                    output.writeInt(thunderCloud.startX);
                    output.writeInt(thunderCloud.startY);
                    output.writeInt(thunderCloud.endX);
                    output.writeInt(thunderCloud.endY);
                    output.writeFloat(thunderCloud.radius);
                    output.writeFloat(thunderCloud.angle);
                    output.writeFloat(thunderCloud.strength);
                    output.writeFloat(thunderCloud.thunderRatio);
                    output.writeDouble(thunderCloud.startTime);
                    output.writeDouble(thunderCloud.endTime);
                    output.writeDouble(thunderCloud.duration);
                    output.writeFloat(thunderCloud.percentageOffset);
                }
            }
        }
    }

    public void load(DataInputStream input) throws IOException {
        byte byte0 = input.readByte();
        if (byte0 != 0) {
            if (byte0 > this.clouds.length && this.dummyCloud == null) {
                this.dummyCloud = new ThunderStorm.ThunderCloud();
            }

            for (int int0 = 0; int0 < byte0; int0++) {
                boolean boolean0 = input.readBoolean();
                ThunderStorm.ThunderCloud thunderCloud;
                if (int0 >= this.clouds.length) {
                    thunderCloud = this.dummyCloud;
                } else {
                    thunderCloud = this.clouds[int0];
                }

                thunderCloud.isRunning = boolean0;
                if (boolean0) {
                    thunderCloud.startX = input.readInt();
                    thunderCloud.startY = input.readInt();
                    thunderCloud.endX = input.readInt();
                    thunderCloud.endY = input.readInt();
                    thunderCloud.radius = input.readFloat();
                    thunderCloud.angle = input.readFloat();
                    thunderCloud.strength = input.readFloat();
                    thunderCloud.thunderRatio = input.readFloat();
                    thunderCloud.startTime = input.readDouble();
                    thunderCloud.endTime = input.readDouble();
                    thunderCloud.duration = input.readDouble();
                    thunderCloud.percentageOffset = input.readFloat();
                }
            }
        }
    }

    private static enum LightningState {
        Idle,
        ApplyLightning;
    }

    private class PlayerLightningInfo {
        public ThunderStorm.LightningState lightningState = ThunderStorm.LightningState.Idle;
        public GameTime.AnimTimer timer = new GameTime.AnimTimer();
        public float lightningStrength = 1.0F;
        public float lightningMod = 0.0F;
        public ClimateColorInfo lightningColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
        public ClimateColorInfo outColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
        public int x = 0;
        public int y = 0;
        public int distance = 0;
    }

    public static class ThunderCloud {
        private int currentX;
        private int currentY;
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private double startTime;
        private double endTime;
        private double duration;
        private float strength;
        private float angle;
        private float radius;
        private float eventFrequency;
        private float thunderRatio;
        private float percentageOffset;
        private boolean isRunning = false;
        private GameTime.AnimTimer suspendTimer = new GameTime.AnimTimer();

        public int getCurrentX() {
            return this.currentX;
        }

        public int getCurrentY() {
            return this.currentY;
        }

        public float getRadius() {
            return this.radius;
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public float getStrength() {
            return this.strength;
        }

        public double lifeTime() {
            return (this.startTime - this.endTime) / this.duration;
        }

        public void setCenter(int centerX, int centerY, float _angle) {
            int int0 = ThunderStorm.getMapDiagonal();
            float float0 = ThunderStorm.addToAngle(_angle, 180.0F);
            int int1 = int0 + Rand.Next(1500, 7500);
            int int2 = (int)(centerX + int1 * Math.cos(Math.toRadians(float0)));
            int int3 = (int)(centerY + int1 * Math.sin(Math.toRadians(float0)));
            int1 = int0 + Rand.Next(1500, 7500);
            int int4 = (int)(centerX + int1 * Math.cos(Math.toRadians(_angle)));
            int int5 = (int)(centerY + int1 * Math.sin(Math.toRadians(_angle)));
            this.startX = int2;
            this.startY = int3;
            this.endX = int4;
            this.endY = int5;
            this.currentX = int2;
            this.currentY = int3;
        }
    }

    private static class ThunderEvent {
        private int eventX;
        private int eventY;
        private boolean doLightning = false;
        private boolean doRumble = false;
        private boolean doStrike = false;
        private GameTime.AnimTimer soundDelay = new GameTime.AnimTimer();
        private boolean isRunning = false;
    }
}
