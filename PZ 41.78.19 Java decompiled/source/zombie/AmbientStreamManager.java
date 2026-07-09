// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import fmod.javafmod;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK;
import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK_TYPE;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.joml.Vector2f;
import zombie.Lua.LuaEventManager;
import zombie.audio.parameters.ParameterCameraZoom;
import zombie.audio.parameters.ParameterClosestWallDistance;
import zombie.audio.parameters.ParameterFogIntensity;
import zombie.audio.parameters.ParameterHardOfHearing;
import zombie.audio.parameters.ParameterInside;
import zombie.audio.parameters.ParameterMoodlePanic;
import zombie.audio.parameters.ParameterPowerSupply;
import zombie.audio.parameters.ParameterRainIntensity;
import zombie.audio.parameters.ParameterRoomSize;
import zombie.audio.parameters.ParameterRoomType;
import zombie.audio.parameters.ParameterSeason;
import zombie.audio.parameters.ParameterSnowIntensity;
import zombie.audio.parameters.ParameterStorm;
import zombie.audio.parameters.ParameterTemperature;
import zombie.audio.parameters.ParameterTimeOfDay;
import zombie.audio.parameters.ParameterWaterSupply;
import zombie.audio.parameters.ParameterWeatherEvent;
import zombie.audio.parameters.ParameterWindIntensity;
import zombie.audio.parameters.ParameterZone;
import zombie.audio.parameters.ParameterZoneWaterSide;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.input.Mouse;
import zombie.iso.Alarm;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.objects.RainManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;

public final class AmbientStreamManager extends BaseAmbientStreamManager {
    public static int OneInAmbienceChance = 2500;
    public static int MaxAmbientCount = 20;
    public static float MaxRange = 1000.0F;
    private final ArrayList<Alarm> alarmList = new ArrayList<>();
    public static BaseAmbientStreamManager instance;
    public final ArrayList<AmbientStreamManager.Ambient> ambient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.WorldSoundEmitter> worldEmitters = new ArrayList<>();
    public final ArrayDeque<AmbientStreamManager.WorldSoundEmitter> freeEmitters = new ArrayDeque<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> allAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> nightAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> dayAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> rainAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> indoorAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> outdoorAmbient = new ArrayList<>();
    public final ArrayList<AmbientStreamManager.AmbientLoop> windAmbient = new ArrayList<>();
    public boolean initialized = false;
    private FMODSoundEmitter electricityShutOffEmitter = null;
    private long electricityShutOffEvent = 0L;
    private int electricityShutOffState = -1;
    private final ParameterFogIntensity parameterFogIntensity = new ParameterFogIntensity();
    private final ParameterRainIntensity parameterRainIntensity = new ParameterRainIntensity();
    private final ParameterSeason parameterSeason = new ParameterSeason();
    private final ParameterSnowIntensity parameterSnowIntensity = new ParameterSnowIntensity();
    private final ParameterStorm parameterStorm = new ParameterStorm();
    private final ParameterTimeOfDay parameterTimeOfDay = new ParameterTimeOfDay();
    private final ParameterTemperature parameterTemperature = new ParameterTemperature();
    private final ParameterWeatherEvent parameterWeatherEvent = new ParameterWeatherEvent();
    private final ParameterWindIntensity parameterWindIntensity = new ParameterWindIntensity();
    private final ParameterZone parameterZoneDeepForest = new ParameterZone("ZoneDeepForest", "DeepForest");
    private final ParameterZone parameterZoneFarm = new ParameterZone("ZoneFarm", "Farm");
    private final ParameterZone parameterZoneForest = new ParameterZone("ZoneForest", "Forest");
    private final ParameterZone parameterZoneNav = new ParameterZone("ZoneNav", "Nav");
    private final ParameterZone parameterZoneTown = new ParameterZone("ZoneTown", "TownZone");
    private final ParameterZone parameterZoneTrailerPark = new ParameterZone("ZoneTrailerPark", "TrailerPark");
    private final ParameterZone parameterZoneVegetation = new ParameterZone("ZoneVegetation", "Vegitation");
    private final ParameterZoneWaterSide parameterZoneWaterSide = new ParameterZoneWaterSide();
    private final ParameterCameraZoom parameterCameraZoom = new ParameterCameraZoom();
    private final ParameterClosestWallDistance parameterClosestWallDistance = new ParameterClosestWallDistance();
    private final ParameterHardOfHearing parameterHardOfHearing = new ParameterHardOfHearing();
    private final ParameterInside parameterInside = new ParameterInside();
    private final ParameterMoodlePanic parameterMoodlePanic = new ParameterMoodlePanic();
    private final ParameterPowerSupply parameterPowerSupply = new ParameterPowerSupply();
    private final ParameterRoomSize parameterRoomSize = new ParameterRoomSize();
    private final ParameterRoomType parameterRoomType = new ParameterRoomType();
    private final ParameterWaterSupply parameterWaterSupply = new ParameterWaterSupply();
    private final Vector2 tempo = new Vector2();
    private final FMOD_STUDIO_EVENT_CALLBACK electricityShutOffEventCallback = new FMOD_STUDIO_EVENT_CALLBACK() {
        @Override
        public void timelineMarker(long var1, String string, int int0) {
            DebugLog.Sound.debugln("timelineMarker %s %d", string, int0);
            if ("ElectricityOff".equals(string)) {
                IsoWorld.instance.setHydroPowerOn(false);
                AmbientStreamManager.this.checkHaveElectricity();
            }
        }
    };

    public static BaseAmbientStreamManager getInstance() {
        return instance;
    }

    @Override
    public void update() {
        if (this.initialized) {
            if (!GameTime.isGamePaused()) {
                if (IsoPlayer.getInstance() != null) {
                    if (IsoPlayer.getInstance().getCurrentSquare() != null) {
                        this.updatePowerSupply();
                        this.parameterFogIntensity.update();
                        this.parameterRainIntensity.update();
                        this.parameterSeason.update();
                        this.parameterSnowIntensity.update();
                        this.parameterStorm.update();
                        this.parameterTemperature.update();
                        this.parameterTimeOfDay.update();
                        this.parameterWeatherEvent.update();
                        this.parameterWindIntensity.update();
                        this.parameterZoneDeepForest.update();
                        this.parameterZoneFarm.update();
                        this.parameterZoneForest.update();
                        this.parameterZoneNav.update();
                        this.parameterZoneVegetation.update();
                        this.parameterZoneTown.update();
                        this.parameterZoneTrailerPark.update();
                        this.parameterZoneWaterSide.update();
                        this.parameterCameraZoom.update();
                        this.parameterClosestWallDistance.update();
                        this.parameterHardOfHearing.update();
                        this.parameterInside.update();
                        this.parameterMoodlePanic.update();
                        this.parameterPowerSupply.update();
                        this.parameterRoomSize.update();
                        this.parameterRoomType.update();
                        this.parameterWaterSupply.update();
                        float float0 = GameTime.instance.getTimeOfDay();

                        for (int int0 = 0; int0 < this.worldEmitters.size(); int0++) {
                            AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = this.worldEmitters.get(int0);
                            if (worldSoundEmitter.daytime != null) {
                                IsoGridSquare square0 = IsoWorld.instance
                                    .CurrentCell
                                    .getGridSquare((double)worldSoundEmitter.x, (double)worldSoundEmitter.y, (double)worldSoundEmitter.z);
                                if (square0 == null) {
                                    worldSoundEmitter.fmodEmitter.stopAll();
                                    SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
                                    this.worldEmitters.remove(worldSoundEmitter);
                                    this.freeEmitters.add(worldSoundEmitter);
                                    int0--;
                                } else {
                                    if (float0 > worldSoundEmitter.dawn && float0 < worldSoundEmitter.dusk) {
                                        if (worldSoundEmitter.fmodEmitter.isEmpty()) {
                                            worldSoundEmitter.channel = worldSoundEmitter.fmodEmitter.playAmbientLoopedImpl(worldSoundEmitter.daytime);
                                        }
                                    } else if (!worldSoundEmitter.fmodEmitter.isEmpty()) {
                                        worldSoundEmitter.fmodEmitter.stopSound(worldSoundEmitter.channel);
                                        worldSoundEmitter.channel = 0L;
                                    }

                                    if (!worldSoundEmitter.fmodEmitter.isEmpty()
                                        && (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart())) {
                                        worldSoundEmitter.fmodEmitter.tick();
                                    }
                                }
                            } else if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().Traits.Deaf.isSet()) {
                                worldSoundEmitter.fmodEmitter.stopAll();
                                SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
                                this.worldEmitters.remove(worldSoundEmitter);
                                this.freeEmitters.add(worldSoundEmitter);
                                int0--;
                            } else {
                                IsoGridSquare square1 = IsoWorld.instance
                                    .CurrentCell
                                    .getGridSquare((double)worldSoundEmitter.x, (double)worldSoundEmitter.y, (double)worldSoundEmitter.z);
                                if (square1 != null && !worldSoundEmitter.fmodEmitter.isEmpty()) {
                                    worldSoundEmitter.fmodEmitter.x = worldSoundEmitter.x;
                                    worldSoundEmitter.fmodEmitter.y = worldSoundEmitter.y;
                                    worldSoundEmitter.fmodEmitter.z = worldSoundEmitter.z;
                                    if (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart()) {
                                        worldSoundEmitter.fmodEmitter.tick();
                                    }
                                } else {
                                    worldSoundEmitter.fmodEmitter.stopAll();
                                    SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
                                    this.worldEmitters.remove(worldSoundEmitter);
                                    this.freeEmitters.add(worldSoundEmitter);
                                    int0--;
                                }
                            }
                        }

                        float float1 = GameTime.instance.getNight();
                        boolean boolean0 = IsoPlayer.getInstance().getCurrentSquare().isInARoom();
                        boolean boolean1 = RainManager.isRaining();

                        for (int int1 = 0; int1 < this.allAmbient.size(); int1++) {
                            this.allAmbient.get(int1).targVol = 1.0F;
                        }

                        for (int int2 = 0; int2 < this.nightAmbient.size(); int2++) {
                            this.nightAmbient.get(int2).targVol *= float1;
                        }

                        for (int int3 = 0; int3 < this.dayAmbient.size(); int3++) {
                            this.dayAmbient.get(int3).targVol *= 1.0F - float1;
                        }

                        for (int int4 = 0; int4 < this.indoorAmbient.size(); int4++) {
                            this.indoorAmbient.get(int4).targVol *= boolean0 ? 0.8F : 0.0F;
                        }

                        for (int int5 = 0; int5 < this.outdoorAmbient.size(); int5++) {
                            this.outdoorAmbient.get(int5).targVol *= boolean0 ? 0.15F : 0.8F;
                        }

                        for (int int6 = 0; int6 < this.rainAmbient.size(); int6++) {
                            this.rainAmbient.get(int6).targVol *= boolean1 ? 1.0F : 0.0F;
                            if (this.rainAmbient.get(int6).channel != 0L) {
                                javafmod.FMOD_Studio_EventInstance_SetParameterByName(
                                    this.rainAmbient.get(int6).channel, "RainIntensity", ClimateManager.getInstance().getPrecipitationIntensity()
                                );
                            }
                        }

                        for (int int7 = 0; int7 < this.allAmbient.size(); int7++) {
                            this.allAmbient.get(int7).update();
                        }

                        for (int int8 = 0; int8 < this.alarmList.size(); int8++) {
                            this.alarmList.get(int8).update();
                            if (this.alarmList.get(int8).finished) {
                                this.alarmList.remove(int8);
                                int8--;
                            }
                        }

                        this.doOneShotAmbients();
                    }
                }
            }
        }
    }

    @Override
    public void doOneShotAmbients() {
        for (int int0 = 0; int0 < this.ambient.size(); int0++) {
            AmbientStreamManager.Ambient ambientx = this.ambient.get(int0);
            if (ambientx.finished()) {
                DebugLog.log(DebugType.Sound, "ambient: removing ambient sound " + ambientx.name);
                this.ambient.remove(int0--);
            } else {
                ambientx.update();
            }
        }
    }

    @Override
    public void addRandomAmbient() {
        if (!Core.GameMode.equals("LastStand") && !Core.GameMode.equals("Tutorial")) {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player0 = IsoPlayer.players[int0];
                if (player0 != null && player0.isAlive()) {
                    arrayList.add(player0);
                }
            }

            if (!arrayList.isEmpty()) {
                IsoPlayer player1 = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
                String string = "";
                if (GameTime.instance.getHour() > 7 && GameTime.instance.getHour() < 21) {
                    switch (Rand.Next(3)) {
                        case 0:
                            if (Rand.Next(10) < 2) {
                                string = "MetaDogBark";
                            }
                            break;
                        case 1:
                            if (Rand.Next(10) < 3) {
                                string = "MetaScream";
                            }
                    }
                } else {
                    switch (Rand.Next(5)) {
                        case 0:
                            if (Rand.Next(10) < 2) {
                                string = "MetaDogBark";
                            }
                            break;
                        case 1:
                            if (Rand.Next(13) < 3) {
                                string = "MetaScream";
                            }
                            break;
                        case 2:
                            string = "MetaOwl";
                            break;
                        case 3:
                            string = "MetaWolfHowl";
                    }
                }

                if (!string.isEmpty()) {
                    float float0 = player1.x;
                    float float1 = player1.y;
                    double double0 = Rand.Next((float) -Math.PI, (float) Math.PI);
                    this.tempo.x = (float)Math.cos(double0);
                    this.tempo.y = (float)Math.sin(double0);
                    this.tempo.setLength(1000.0F);
                    float0 += this.tempo.x;
                    float1 += this.tempo.y;
                    if (!GameClient.bClient) {
                        System.out.println("playing ambient: " + string + " at dist: " + Math.abs(float0 - player1.x) + "," + Math.abs(float1 - player1.y));
                        AmbientStreamManager.Ambient ambientx = new AmbientStreamManager.Ambient(string, float0, float1, 50.0F, Rand.Next(0.2F, 0.5F));
                        this.ambient.add(ambientx);
                    }
                }
            }
        }
    }

    @Override
    public void addBlend(String name, float vol, boolean bIndoors, boolean bRain, boolean bNight, boolean bDay) {
        AmbientStreamManager.AmbientLoop ambientLoop = new AmbientStreamManager.AmbientLoop(0.0F, name, vol);
        this.allAmbient.add(ambientLoop);
        if (bIndoors) {
            this.indoorAmbient.add(ambientLoop);
        } else {
            this.outdoorAmbient.add(ambientLoop);
        }

        if (bRain) {
            this.rainAmbient.add(ambientLoop);
        }

        if (bNight) {
            this.nightAmbient.add(ambientLoop);
        }

        if (bDay) {
            this.dayAmbient.add(ambientLoop);
        }
    }

    @Override
    public void init() {
        if (!this.initialized) {
            this.initialized = true;
        }
    }

    @Override
    public void doGunEvent() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player0 = IsoPlayer.players[int0];
            if (player0 != null && player0.isAlive()) {
                arrayList.add(player0);
            }
        }

        if (!arrayList.isEmpty()) {
            IsoPlayer player1 = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
            String string = null;
            switch (Rand.Next(6)) {
                case 0:
                    string = "MetaAssaultRifle1";
                    break;
                case 1:
                    string = "MetaPistol1";
                    break;
                case 2:
                    string = "MetaShotgun1";
                    break;
                case 3:
                    string = "MetaPistol2";
                    break;
                case 4:
                    string = "MetaPistol3";
                    break;
                case 5:
                    string = "MetaShotgun1";
            }

            float float0 = player1.x;
            float float1 = player1.y;
            short short0 = 600;
            double double0 = Rand.Next((float) -Math.PI, (float) Math.PI);
            this.tempo.x = (float)Math.cos(double0);
            this.tempo.y = (float)Math.sin(double0);
            this.tempo.setLength(short0 - 100);
            float0 += this.tempo.x;
            float1 += this.tempo.y;
            WorldSoundManager.instance.addSound(null, (int)float0, (int)float1, 0, short0, short0);
            float float2 = 1.0F;
            AmbientStreamManager.Ambient ambientx = new AmbientStreamManager.Ambient(string, float0, float1, 700.0F, float2);
            this.ambient.add(ambientx);
        }
    }

    @Override
    public void doAlarm(RoomDef room) {
        if (room != null && room.building != null && room.building.bAlarmed) {
            room.building.bAlarmed = false;
            room.building.setAllExplored(true);
            this.alarmList.add(new Alarm(room.x + room.getW() / 2, room.y + room.getH() / 2));
        }
    }

    @Override
    public void stop() {
        for (AmbientStreamManager.AmbientLoop ambientLoop : this.allAmbient) {
            ambientLoop.stop();
        }

        this.allAmbient.clear();
        this.ambient.clear();
        this.dayAmbient.clear();
        this.indoorAmbient.clear();
        this.nightAmbient.clear();
        this.outdoorAmbient.clear();
        this.rainAmbient.clear();
        this.windAmbient.clear();
        this.alarmList.clear();
        if (this.electricityShutOffEmitter != null) {
            this.electricityShutOffEmitter.stopAll();
            this.electricityShutOffEvent = 0L;
        }

        this.electricityShutOffState = -1;
        this.initialized = false;
    }

    @Override
    public void addAmbient(String name, int x, int y, int radius, float volume) {
        if (GameClient.bClient) {
            AmbientStreamManager.Ambient ambientx = new AmbientStreamManager.Ambient(name, x, y, radius, volume, true);
            this.ambient.add(ambientx);
        }
    }

    @Override
    public void addAmbientEmitter(float x, float y, int z, String name) {
        AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = this.freeEmitters.isEmpty()
            ? new AmbientStreamManager.WorldSoundEmitter()
            : this.freeEmitters.pop();
        worldSoundEmitter.x = x;
        worldSoundEmitter.y = y;
        worldSoundEmitter.z = z;
        worldSoundEmitter.daytime = null;
        if (worldSoundEmitter.fmodEmitter == null) {
            worldSoundEmitter.fmodEmitter = new FMODSoundEmitter();
        }

        worldSoundEmitter.fmodEmitter.x = x;
        worldSoundEmitter.fmodEmitter.y = y;
        worldSoundEmitter.fmodEmitter.z = z;
        worldSoundEmitter.channel = worldSoundEmitter.fmodEmitter.playAmbientLoopedImpl(name);
        worldSoundEmitter.fmodEmitter.randomStart();
        SoundManager.instance.registerEmitter(worldSoundEmitter.fmodEmitter);
        this.worldEmitters.add(worldSoundEmitter);
    }

    @Override
    public void addDaytimeAmbientEmitter(float x, float y, int z, String name) {
        AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = this.freeEmitters.isEmpty()
            ? new AmbientStreamManager.WorldSoundEmitter()
            : this.freeEmitters.pop();
        worldSoundEmitter.x = x;
        worldSoundEmitter.y = y;
        worldSoundEmitter.z = z;
        if (worldSoundEmitter.fmodEmitter == null) {
            worldSoundEmitter.fmodEmitter = new FMODSoundEmitter();
        }

        worldSoundEmitter.fmodEmitter.x = x;
        worldSoundEmitter.fmodEmitter.y = y;
        worldSoundEmitter.fmodEmitter.z = z;
        worldSoundEmitter.daytime = name;
        worldSoundEmitter.dawn = Rand.Next(7.0F, 8.0F);
        worldSoundEmitter.dusk = Rand.Next(19.0F, 20.0F);
        SoundManager.instance.registerEmitter(worldSoundEmitter.fmodEmitter);
        this.worldEmitters.add(worldSoundEmitter);
    }

    private void updatePowerSupply() {
        boolean boolean0 = GameTime.getInstance().NightsSurvived < SandboxOptions.getInstance().getElecShutModifier();
        if (this.electricityShutOffState == -1) {
            IsoWorld.instance.setHydroPowerOn(boolean0);
        }

        if (this.electricityShutOffState == 0 && boolean0) {
            IsoWorld.instance.setHydroPowerOn(true);
            this.checkHaveElectricity();
        }

        if (this.electricityShutOffState == 1 && !boolean0) {
            if (this.electricityShutOffEmitter == null) {
                this.electricityShutOffEmitter = new FMODSoundEmitter();
            }

            if (!this.electricityShutOffEmitter.isPlaying(this.electricityShutOffEvent)) {
                Vector2f vector2f = new Vector2f();
                this.getListenerPos(vector2f);
                BuildingDef buildingDef = this.getNearestBuilding(vector2f.x, vector2f.y, vector2f);
                if (buildingDef == null) {
                    this.electricityShutOffEmitter.setPos(-1000.0F, -1000.0F, 0.0F);
                } else {
                    this.electricityShutOffEmitter.setPos(vector2f.x, vector2f.y, 0.0F);
                }

                this.electricityShutOffEvent = this.electricityShutOffEmitter.playSound("WorldEventElectricityShutdown");
                if (this.electricityShutOffEvent != 0L) {
                    javafmod.FMOD_Studio_EventInstance_SetCallback(
                        this.electricityShutOffEvent,
                        this.electricityShutOffEventCallback,
                        FMOD_STUDIO_EVENT_CALLBACK_TYPE.FMOD_STUDIO_EVENT_CALLBACK_TIMELINE_MARKER.bit
                    );
                }
            }
        }

        this.electricityShutOffState = boolean0 ? 1 : 0;
        if (this.electricityShutOffEmitter != null) {
            this.electricityShutOffEmitter.tick();
        }
    }

    private void checkHaveElectricity() {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int0];
            if (!chunkMap.ignore) {
                for (int int1 = 0; int1 < 8; int1++) {
                    for (int int2 = chunkMap.getWorldYMinTiles(); int2 <= chunkMap.getWorldYMaxTiles(); int2++) {
                        for (int int3 = chunkMap.getWorldXMinTiles(); int3 <= chunkMap.getWorldXMaxTiles(); int3++) {
                            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int2, int1);
                            if (square != null) {
                                for (int int4 = 0; int4 < square.getObjects().size(); int4++) {
                                    IsoObject object = square.getObjects().get(int4);
                                    object.checkHaveElectricity();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public BuildingDef getNearestBuilding(float px, float py, Vector2f closestXY) {
        IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        int int0 = PZMath.fastfloor(px / 300.0F);
        int int1 = PZMath.fastfloor(py / 300.0F);
        BuildingDef buildingDef0 = null;
        float float0 = Float.MAX_VALUE;
        closestXY.set(0.0F);
        Vector2f vector2f = new Vector2f();

        for (int int2 = int1 - 1; int2 <= int1 + 1; int2++) {
            for (int int3 = int0 - 1; int3 <= int0 + 1; int3++) {
                IsoMetaCell metaCell = metaGrid.getCellData(int3, int2);
                if (metaCell != null && metaCell.info != null) {
                    for (BuildingDef buildingDef1 : metaCell.info.Buildings) {
                        float float1 = buildingDef1.getClosestPoint(px, py, vector2f);
                        if (float1 < float0) {
                            float0 = float1;
                            buildingDef0 = buildingDef1;
                            closestXY.set(vector2f);
                        }
                    }
                }
            }
        }

        return buildingDef0;
    }

    private void getListenerPos(Vector2f vector2f) {
        IsoPlayer player0 = null;
        vector2f.set(0.0F);

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player1 = IsoPlayer.players[int0];
            if (player1 != null && (player0 == null || player0.isDead() && player1.isAlive() || player0.Traits.Deaf.isSet() && !player1.Traits.Deaf.isSet())) {
                player0 = player1;
                vector2f.set(player1.getX(), player1.getY());
            }
        }
    }

    public static final class Ambient {
        public float x;
        public float y;
        public String name;
        float radius;
        float volume;
        int worldSoundRadius;
        int worldSoundVolume;
        public boolean trackMouse = false;
        final FMODSoundEmitter emitter = new FMODSoundEmitter();

        public Ambient(String string, float float0, float float1, float float2, float float3) {
            this(string, float0, float1, float2, float3, false);
        }

        public Ambient(String string, float float0, float float1, float float2, float float3, boolean var6) {
            this.name = string;
            this.x = float0;
            this.y = float1;
            this.radius = float2;
            this.volume = float3;
            this.emitter.x = float0;
            this.emitter.y = float1;
            this.emitter.z = 0.0F;
            this.emitter.playAmbientSound(string);
            this.update();
            LuaEventManager.triggerEvent("OnAmbientSound", string, float0, float1);
        }

        public boolean finished() {
            return this.emitter.isEmpty();
        }

        public void update() {
            this.emitter.tick();
            if (this.trackMouse && IsoPlayer.getInstance() != null) {
                float float0 = Mouse.getXA();
                float float1 = Mouse.getYA();
                float0 -= IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
                float1 -= IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
                float0 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
                float1 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
                int int0 = (int)IsoPlayer.getInstance().getZ();
                this.emitter.x = (int)IsoUtils.XToIso(float0, float1, int0);
                this.emitter.y = (int)IsoUtils.YToIso(float0, float1, int0);
            }

            if (!GameClient.bClient && this.worldSoundRadius > 0 && this.worldSoundVolume > 0) {
                WorldSoundManager.instance.addSound(null, (int)this.x, (int)this.y, 0, this.worldSoundRadius, this.worldSoundVolume);
            }
        }

        public void repeatWorldSounds(int int0, int int1) {
            this.worldSoundRadius = int0;
            this.worldSoundVolume = int1;
        }

        private IsoGameCharacter getClosestListener(float float4, float float5) {
            IsoPlayer player0 = null;
            float float0 = Float.MAX_VALUE;

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player1 = IsoPlayer.players[int0];
                if (player1 != null && player1.getCurrentSquare() != null) {
                    float float1 = player1.getX();
                    float float2 = player1.getY();
                    float float3 = IsoUtils.DistanceToSquared(float1, float2, float4, float5);
                    if (player1.Traits.HardOfHearing.isSet()) {
                        float3 *= 4.5F;
                    }

                    if (player1.Traits.Deaf.isSet()) {
                        float3 = Float.MAX_VALUE;
                    }

                    if (float3 < float0) {
                        player0 = player1;
                        float0 = float3;
                    }
                }
            }

            return player0;
        }
    }

    public static final class AmbientLoop {
        public static float volChangeAmount = 0.01F;
        public float targVol;
        public float currVol;
        public String name;
        public float volumedelta = 1.0F;
        public long channel = -1L;
        public final FMODSoundEmitter emitter = new FMODSoundEmitter();

        public AmbientLoop(float float1, String string, float float0) {
            this.volumedelta = float0;
            this.channel = this.emitter.playAmbientLoopedImpl(string);
            this.targVol = float1;
            this.currVol = 0.0F;
            this.update();
        }

        public void update() {
            if (this.targVol > this.currVol) {
                this.currVol = this.currVol + volChangeAmount;
                if (this.currVol > this.targVol) {
                    this.currVol = this.targVol;
                }
            }

            if (this.targVol < this.currVol) {
                this.currVol = this.currVol - volChangeAmount;
                if (this.currVol < this.targVol) {
                    this.currVol = this.targVol;
                }
            }

            this.emitter.setVolumeAll(this.currVol * this.volumedelta);
            this.emitter.tick();
        }

        public void stop() {
            this.emitter.stopAll();
        }
    }

    public static final class WorldSoundEmitter {
        public FMODSoundEmitter fmodEmitter;
        public float x;
        public float y;
        public float z;
        public long channel = -1L;
        public String daytime;
        public float dawn;
        public float dusk;
    }
}
