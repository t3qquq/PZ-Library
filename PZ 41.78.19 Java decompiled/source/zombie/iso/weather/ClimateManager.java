// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.dbg.ClimMngrDebug;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.fx.SteppedUpdateFloat;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.vehicles.BaseVehicle;

/**
 * TurboTuTone.
 */
public class ClimateManager {
    private boolean DISABLE_SIMULATION = false;
    private boolean DISABLE_FX_UPDATE = false;
    private boolean DISABLE_WEATHER_GENERATION = false;
    public static final int FRONT_COLD = -1;
    public static final int FRONT_STATIONARY = 0;
    public static final int FRONT_WARM = 1;
    public static final float MAX_WINDSPEED_KPH = 120.0F;
    public static final float MAX_WINDSPEED_MPH = 74.5645F;
    private ErosionSeason season;
    private long lastMinuteStamp = -1L;
    private KahluaTable modDataTable = null;
    private float airMass;
    private float airMassDaily;
    private float airMassTemperature;
    private float baseTemperature;
    private float snowFall = 0.0F;
    private float snowStrength = 0.0F;
    private float snowMeltStrength = 0.0F;
    private float snowFracNow = 0.0F;
    boolean canDoWinterSprites = false;
    private float windPower = 0.0F;
    private WeatherPeriod weatherPeriod;
    private ThunderStorm thunderStorm;
    private double simplexOffsetA = 0.0;
    private double simplexOffsetB = 0.0;
    private double simplexOffsetC = 0.0;
    private double simplexOffsetD = 0.0;
    private boolean dayDoFog = false;
    private float dayFogStrength = 0.0F;
    private GameTime gt;
    private double worldAgeHours;
    private boolean tickIsClimateTick = false;
    private boolean tickIsDayChange = false;
    private int lastHourStamp = -1;
    private boolean tickIsHourChange = false;
    private boolean tickIsTenMins = false;
    private ClimateManager.AirFront currentFront = new ClimateManager.AirFront();
    private ClimateColorInfo colDay;
    private ClimateColorInfo colDusk;
    private ClimateColorInfo colDawn;
    private ClimateColorInfo colNight;
    private ClimateColorInfo colNightNoMoon;
    private ClimateColorInfo colNightMoon;
    private ClimateColorInfo colTemp;
    private ClimateColorInfo colFog;
    private ClimateColorInfo colFogLegacy;
    private ClimateColorInfo colFogNew;
    private ClimateColorInfo fogTintStorm;
    private ClimateColorInfo fogTintTropical;
    private static ClimateManager instance = new ClimateManager();
    public static boolean WINTER_IS_COMING = false;
    public static boolean THE_DESCENDING_FOG = false;
    public static boolean A_STORM_IS_COMING = false;
    private ClimateValues climateValues;
    private ClimateForecaster climateForecaster;
    private ClimateHistory climateHistory;
    float dayLightLagged = 0.0F;
    float nightLagged = 0.0F;
    protected ClimateManager.ClimateFloat desaturation;
    protected ClimateManager.ClimateFloat globalLightIntensity;
    protected ClimateManager.ClimateFloat nightStrength;
    protected ClimateManager.ClimateFloat precipitationIntensity;
    protected ClimateManager.ClimateFloat temperature;
    protected ClimateManager.ClimateFloat fogIntensity;
    protected ClimateManager.ClimateFloat windIntensity;
    protected ClimateManager.ClimateFloat windAngleIntensity;
    protected ClimateManager.ClimateFloat cloudIntensity;
    protected ClimateManager.ClimateFloat ambient;
    protected ClimateManager.ClimateFloat viewDistance;
    protected ClimateManager.ClimateFloat dayLightStrength;
    protected ClimateManager.ClimateFloat humidity;
    protected ClimateManager.ClimateColor globalLight;
    protected ClimateManager.ClimateColor colorNewFog;
    protected ClimateManager.ClimateBool precipitationIsSnow;
    public static final int FLOAT_DESATURATION = 0;
    public static final int FLOAT_GLOBAL_LIGHT_INTENSITY = 1;
    public static final int FLOAT_NIGHT_STRENGTH = 2;
    public static final int FLOAT_PRECIPITATION_INTENSITY = 3;
    public static final int FLOAT_TEMPERATURE = 4;
    public static final int FLOAT_FOG_INTENSITY = 5;
    public static final int FLOAT_WIND_INTENSITY = 6;
    public static final int FLOAT_WIND_ANGLE_INTENSITY = 7;
    public static final int FLOAT_CLOUD_INTENSITY = 8;
    public static final int FLOAT_AMBIENT = 9;
    public static final int FLOAT_VIEW_DISTANCE = 10;
    public static final int FLOAT_DAYLIGHT_STRENGTH = 11;
    public static final int FLOAT_HUMIDITY = 12;
    public static final int FLOAT_MAX = 13;
    private final ClimateManager.ClimateFloat[] climateFloats = new ClimateManager.ClimateFloat[13];
    public static final int COLOR_GLOBAL_LIGHT = 0;
    public static final int COLOR_NEW_FOG = 1;
    public static final int COLOR_MAX = 2;
    private final ClimateManager.ClimateColor[] climateColors = new ClimateManager.ClimateColor[2];
    public static final int BOOL_IS_SNOW = 0;
    public static final int BOOL_MAX = 1;
    private final ClimateManager.ClimateBool[] climateBooleans = new ClimateManager.ClimateBool[1];
    public static final float AVG_FAV_AIR_TEMPERATURE = 22.0F;
    private static double windNoiseOffset = 0.0;
    private static double windNoiseBase = 0.0;
    private static double windNoiseFinal = 0.0;
    private static double windTickFinal = 0.0;
    private ClimateColorInfo colFlare = new ClimateColorInfo(1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
    private boolean flareLaunched = false;
    private SteppedUpdateFloat flareIntensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
    private float flareIntens;
    private float flareMaxLifeTime;
    private float flareLifeTime;
    private int nextRandomTargetIntens = 10;
    float fogLerpValue = 0.0F;
    private ClimateManager.SeasonColor seasonColorDawn;
    private ClimateManager.SeasonColor seasonColorDay;
    private ClimateManager.SeasonColor seasonColorDusk;
    private ClimateManager.DayInfo previousDay;
    private ClimateManager.DayInfo currentDay;
    private ClimateManager.DayInfo nextDay;
    public static final byte PacketUpdateClimateVars = 0;
    public static final byte PacketWeatherUpdate = 1;
    public static final byte PacketThunderEvent = 2;
    public static final byte PacketFlare = 3;
    public static final byte PacketAdminVarsUpdate = 4;
    public static final byte PacketRequestAdminVars = 5;
    public static final byte PacketClientChangedAdminVars = 6;
    public static final byte PacketClientChangedWeather = 7;
    private float networkLerp = 0.0F;
    private long networkUpdateStamp = 0L;
    private float networkLerpTime = 5000.0F;
    private float networkLerpTimeBase = 5000.0F;
    private float networkAdjustVal = 0.0F;
    private boolean networkPrint = false;
    private ClimateManager.ClimateNetInfo netInfo = new ClimateManager.ClimateNetInfo();
    private ClimateValues climateValuesFronts;
    private static float[] windAngles = new float[]{22.5F, 67.5F, 112.5F, 157.5F, 202.5F, 247.5F, 292.5F, 337.5F, 382.5F};
    private static String[] windAngleStr = new String[]{"SE", "S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public float getMaxWindspeedKph() {
        return 120.0F;
    }

    public float getMaxWindspeedMph() {
        return 74.5645F;
    }

    public static float ToKph(float val) {
        return val * 120.0F;
    }

    public static float ToMph(float val) {
        return val * 74.5645F;
    }

    public static ClimateManager getInstance() {
        return instance;
    }

    public static void setInstance(ClimateManager inst) {
        instance = inst;
    }

    public ClimateManager() {
        this.colDay = new ClimateColorInfo();
        this.colDawn = new ClimateColorInfo();
        this.colDusk = new ClimateColorInfo();
        this.colNight = new ClimateColorInfo();
        this.colNightMoon = new ClimateColorInfo();
        this.colFog = new ClimateColorInfo();
        this.colTemp = new ClimateColorInfo();
        this.colDay = new ClimateColorInfo();
        this.colDawn = new ClimateColorInfo();
        this.colDusk = new ClimateColorInfo();
        this.colNight = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
        this.colNightNoMoon = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
        this.colNightMoon = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
        this.colFog = new ClimateColorInfo(0.4F, 0.4F, 0.4F, 0.8F, 0.4F, 0.4F, 0.4F, 0.8F);
        this.colFogLegacy = new ClimateColorInfo(0.3F, 0.3F, 0.3F, 0.8F, 0.3F, 0.3F, 0.3F, 0.8F);
        this.colFogNew = new ClimateColorInfo(0.5F, 0.5F, 0.55F, 0.4F, 0.5F, 0.5F, 0.55F, 0.8F);
        this.fogTintStorm = new ClimateColorInfo(0.5F, 0.45F, 0.4F, 1.0F, 0.5F, 0.45F, 0.4F, 1.0F);
        this.fogTintTropical = new ClimateColorInfo(0.8F, 0.75F, 0.55F, 1.0F, 0.8F, 0.75F, 0.55F, 1.0F);
        this.colTemp = new ClimateColorInfo();
        this.simplexOffsetA = Rand.Next(0, 8000);
        this.simplexOffsetB = Rand.Next(8000, 16000);
        this.simplexOffsetC = Rand.Next(0, -8000);
        this.simplexOffsetD = Rand.Next(-8000, -16000);
        this.initSeasonColors();
        this.setup();
        this.climateValues = new ClimateValues(this);
        this.thunderStorm = new ThunderStorm(this);
        this.weatherPeriod = new WeatherPeriod(this, this.thunderStorm);
        this.climateForecaster = new ClimateForecaster();
        this.climateHistory = new ClimateHistory();

        try {
            LuaEventManager.triggerEvent("OnClimateManagerInit", this);
        } catch (Exception exception) {
            System.out.print(exception.getMessage());
            System.out.print(exception.getStackTrace());
        }
    }

    public ClimateColorInfo getColNight() {
        return this.colNight;
    }

    public ClimateColorInfo getColNightNoMoon() {
        return this.colNightNoMoon;
    }

    public ClimateColorInfo getColNightMoon() {
        return this.colNightMoon;
    }

    public ClimateColorInfo getColFog() {
        return this.colFog;
    }

    public ClimateColorInfo getColFogLegacy() {
        return this.colFogLegacy;
    }

    public ClimateColorInfo getColFogNew() {
        return this.colFogNew;
    }

    public ClimateColorInfo getFogTintStorm() {
        return this.fogTintStorm;
    }

    public ClimateColorInfo getFogTintTropical() {
        return this.fogTintTropical;
    }

    private void setup() {
        for (int int0 = 0; int0 < this.climateFloats.length; int0++) {
            this.climateFloats[int0] = new ClimateManager.ClimateFloat();
        }

        for (int int1 = 0; int1 < this.climateColors.length; int1++) {
            this.climateColors[int1] = new ClimateManager.ClimateColor();
        }

        for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
            this.climateBooleans[int2] = new ClimateManager.ClimateBool();
        }

        this.desaturation = this.initClimateFloat(0, "DESATURATION");
        this.globalLightIntensity = this.initClimateFloat(1, "GLOBAL_LIGHT_INTENSITY");
        this.nightStrength = this.initClimateFloat(2, "NIGHT_STRENGTH");
        this.precipitationIntensity = this.initClimateFloat(3, "PRECIPITATION_INTENSITY");
        this.temperature = this.initClimateFloat(4, "TEMPERATURE");
        this.temperature.min = -80.0F;
        this.temperature.max = 80.0F;
        this.fogIntensity = this.initClimateFloat(5, "FOG_INTENSITY");
        this.windIntensity = this.initClimateFloat(6, "WIND_INTENSITY");
        this.windAngleIntensity = this.initClimateFloat(7, "WIND_ANGLE_INTENSITY");
        this.windAngleIntensity.min = -1.0F;
        this.cloudIntensity = this.initClimateFloat(8, "CLOUD_INTENSITY");
        this.ambient = this.initClimateFloat(9, "AMBIENT");
        this.viewDistance = this.initClimateFloat(10, "VIEW_DISTANCE");
        this.viewDistance.min = 0.0F;
        this.viewDistance.max = 100.0F;
        this.dayLightStrength = this.initClimateFloat(11, "DAYLIGHT_STRENGTH");
        this.humidity = this.initClimateFloat(12, "HUMIDITY");
        this.globalLight = this.initClimateColor(0, "GLOBAL_LIGHT");
        this.colorNewFog = this.initClimateColor(1, "COLOR_NEW_FOG");
        this.colorNewFog.internalValue.setExterior(0.9F, 0.9F, 0.95F, 1.0F);
        this.colorNewFog.internalValue.setInterior(0.9F, 0.9F, 0.95F, 1.0F);
        this.precipitationIsSnow = this.initClimateBool(0, "IS_SNOW");
    }

    public int getFloatMax() {
        return 13;
    }

    private ClimateManager.ClimateFloat initClimateFloat(int int0, String string) {
        if (int0 >= 0 && int0 < 13) {
            return this.climateFloats[int0].init(int0, string);
        } else {
            DebugLog.log("Climate: cannot get float override id.");
            return null;
        }
    }

    public ClimateManager.ClimateFloat getClimateFloat(int id) {
        if (id >= 0 && id < 13) {
            return this.climateFloats[id];
        } else {
            DebugLog.log("Climate: cannot get float override id.");
            return null;
        }
    }

    public int getColorMax() {
        return 2;
    }

    private ClimateManager.ClimateColor initClimateColor(int int0, String string) {
        if (int0 >= 0 && int0 < 2) {
            return this.climateColors[int0].init(int0, string);
        } else {
            DebugLog.log("Climate: cannot get float override id.");
            return null;
        }
    }

    public ClimateManager.ClimateColor getClimateColor(int id) {
        if (id >= 0 && id < 2) {
            return this.climateColors[id];
        } else {
            DebugLog.log("Climate: cannot get float override id.");
            return null;
        }
    }

    public int getBoolMax() {
        return 1;
    }

    private ClimateManager.ClimateBool initClimateBool(int int0, String string) {
        if (int0 >= 0 && int0 < 1) {
            return this.climateBooleans[int0].init(int0, string);
        } else {
            DebugLog.log("Climate: cannot get boolean id.");
            return null;
        }
    }

    public ClimateManager.ClimateBool getClimateBool(int id) {
        if (id >= 0 && id < 1) {
            return this.climateBooleans[id];
        } else {
            DebugLog.log("Climate: cannot get boolean id.");
            return null;
        }
    }

    public void setEnabledSimulation(boolean b) {
        if (!GameClient.bClient && !GameServer.bServer) {
            this.DISABLE_SIMULATION = !b;
        } else {
            this.DISABLE_SIMULATION = false;
        }
    }

    public boolean getEnabledSimulation() {
        return !this.DISABLE_SIMULATION;
    }

    public boolean getEnabledFxUpdate() {
        return !this.DISABLE_FX_UPDATE;
    }

    public void setEnabledFxUpdate(boolean b) {
        if (!GameClient.bClient && !GameServer.bServer) {
            this.DISABLE_FX_UPDATE = !b;
        } else {
            this.DISABLE_FX_UPDATE = false;
        }
    }

    public boolean getEnabledWeatherGeneration() {
        return this.DISABLE_WEATHER_GENERATION;
    }

    public void setEnabledWeatherGeneration(boolean b) {
        this.DISABLE_WEATHER_GENERATION = !b;
    }

    public Color getGlobalLightInternal() {
        return this.globalLight.internalValue.getExterior();
    }

    public ClimateColorInfo getGlobalLight() {
        return this.globalLight.finalValue;
    }

    public float getGlobalLightIntensity() {
        return this.globalLightIntensity.finalValue;
    }

    public ClimateColorInfo getColorNewFog() {
        return this.colorNewFog.finalValue;
    }

    public void setNightStrength(float b) {
        this.nightStrength.finalValue = clamp(0.0F, 1.0F, b);
    }

    public float getDesaturation() {
        return this.desaturation.finalValue;
    }

    public void setDesaturation(float _desaturation) {
        this.desaturation.finalValue = _desaturation;
    }

    public float getAirMass() {
        return this.airMass;
    }

    public float getAirMassDaily() {
        return this.airMassDaily;
    }

    public float getAirMassTemperature() {
        return this.airMassTemperature;
    }

    public float getDayLightStrength() {
        return this.dayLightStrength.finalValue;
    }

    public float getNightStrength() {
        return this.nightStrength.finalValue;
    }

    public float getDayMeanTemperature() {
        return this.currentDay.season.getDayMeanTemperature();
    }

    public float getTemperature() {
        return this.temperature.finalValue;
    }

    public float getBaseTemperature() {
        return this.baseTemperature;
    }

    public float getSnowStrength() {
        return this.snowStrength;
    }

    public boolean getPrecipitationIsSnow() {
        return this.precipitationIsSnow.finalValue;
    }

    public float getPrecipitationIntensity() {
        return this.precipitationIntensity.finalValue;
    }

    public float getFogIntensity() {
        return this.fogIntensity.finalValue;
    }

    public float getWindIntensity() {
        return this.windIntensity.finalValue;
    }

    public float getWindAngleIntensity() {
        return this.windAngleIntensity.finalValue;
    }

    public float getCorrectedWindAngleIntensity() {
        return (this.windAngleIntensity.finalValue + 1.0F) * 0.5F;
    }

    public float getWindPower() {
        return this.windPower;
    }

    public float getWindspeedKph() {
        return this.windPower * 120.0F;
    }

    public float getCloudIntensity() {
        return this.cloudIntensity.finalValue;
    }

    public float getAmbient() {
        return this.ambient.finalValue;
    }

    public float getViewDistance() {
        return this.viewDistance.finalValue;
    }

    public float getHumidity() {
        return this.humidity.finalValue;
    }

    public float getWindAngleDegrees() {
        float float0;
        if (this.windAngleIntensity.finalValue > 0.0F) {
            float0 = lerp(this.windAngleIntensity.finalValue, 45.0F, 225.0F);
        } else if (this.windAngleIntensity.finalValue > -0.25F) {
            float0 = lerp(Math.abs(this.windAngleIntensity.finalValue), 45.0F, 0.0F);
        } else {
            float0 = lerp(Math.abs(this.windAngleIntensity.finalValue) - 0.25F, 360.0F, 180.0F);
        }

        if (float0 > 360.0F) {
            float0 -= 360.0F;
        }

        if (float0 < 0.0F) {
            float0 += 360.0F;
        }

        return float0;
    }

    public float getWindAngleRadians() {
        return (float)Math.toRadians(this.getWindAngleDegrees());
    }

    public float getWindSpeedMovement() {
        float float0 = this.getWindIntensity();
        if (float0 < 0.15F) {
            float0 = 0.0F;
        } else {
            float0 = (float0 - 0.15F) / 0.85F;
        }

        return float0;
    }

    public float getWindForceMovement(IsoGameCharacter character, float angle) {
        if (character.square != null && !character.square.isInARoom()) {
            float float0 = angle - this.getWindAngleRadians();
            if (float0 > Math.PI * 2) {
                float0 = (float)(float0 - (Math.PI * 2));
            }

            if (float0 < 0.0F) {
                float0 = (float)(float0 + (Math.PI * 2));
            }

            if (float0 > Math.PI) {
                float0 = (float)(Math.PI - (float0 - Math.PI));
            }

            return (float)(float0 / Math.PI);
        } else {
            return 0.0F;
        }
    }

    public boolean isRaining() {
        return this.getPrecipitationIntensity() > 0.0F && !this.getPrecipitationIsSnow();
    }

    public float getRainIntensity() {
        return this.isRaining() ? this.getPrecipitationIntensity() : 0.0F;
    }

    public boolean isSnowing() {
        return this.getPrecipitationIntensity() > 0.0F && this.getPrecipitationIsSnow();
    }

    public float getSnowIntensity() {
        return this.isSnowing() ? this.getPrecipitationIntensity() : 0.0F;
    }

    public void setAmbient(float f) {
        this.ambient.finalValue = f;
    }

    public void setViewDistance(float f) {
        this.viewDistance.finalValue = f;
    }

    public void setDayLightStrength(float f) {
        this.dayLightStrength.finalValue = f;
    }

    public void setPrecipitationIsSnow(boolean b) {
        this.precipitationIsSnow.finalValue = b;
    }

    public ClimateManager.DayInfo getCurrentDay() {
        return this.currentDay;
    }

    public ClimateManager.DayInfo getPreviousDay() {
        return this.previousDay;
    }

    public ClimateManager.DayInfo getNextDay() {
        return this.nextDay;
    }

    public ErosionSeason getSeason() {
        return this.currentDay != null && this.currentDay.getSeason() != null ? this.currentDay.getSeason() : this.season;
    }

    public float getFrontStrength() {
        if (this.currentFront == null) {
            return 0.0F;
        } else {
            if (Core.bDebug) {
                this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
            }

            return this.currentFront.strength;
        }
    }

    public void stopWeatherAndThunder() {
        if (!GameClient.bClient) {
            this.weatherPeriod.stopWeatherPeriod();
            this.thunderStorm.stopAllClouds();
            if (GameServer.bServer) {
                this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, null);
            }
        }
    }

    public ThunderStorm getThunderStorm() {
        return this.thunderStorm;
    }

    public WeatherPeriod getWeatherPeriod() {
        return this.weatherPeriod;
    }

    public boolean getIsThunderStorming() {
        return this.weatherPeriod.isRunning() && (this.weatherPeriod.isThunderStorm() || this.weatherPeriod.isTropicalStorm());
    }

    public float getWeatherInterference() {
        if (this.weatherPeriod.isRunning()) {
            return !this.weatherPeriod.isThunderStorm() && !this.weatherPeriod.isTropicalStorm() && !this.weatherPeriod.isBlizzard()
                ? 0.35F * this.weatherPeriod.getCurrentStrength()
                : 0.7F * this.weatherPeriod.getCurrentStrength();
        } else {
            return 0.0F;
        }
    }

    public KahluaTable getModData() {
        if (this.modDataTable == null) {
            this.modDataTable = LuaManager.platform.newTable();
        }

        return this.modDataTable;
    }

    public float getAirTemperatureForCharacter(IsoGameCharacter plr) {
        return this.getAirTemperatureForCharacter(plr, false);
    }

    public float getAirTemperatureForCharacter(IsoGameCharacter plr, boolean doWindChill) {
        if (plr.square != null) {
            return plr.getVehicle() != null
                ? this.getAirTemperatureForSquare(plr.square, plr.getVehicle(), doWindChill)
                : this.getAirTemperatureForSquare(plr.square, null, doWindChill);
        } else {
            return this.getTemperature();
        }
    }

    public float getAirTemperatureForSquare(IsoGridSquare square) {
        return this.getAirTemperatureForSquare(square, null);
    }

    public float getAirTemperatureForSquare(IsoGridSquare square, BaseVehicle vehicle) {
        return this.getAirTemperatureForSquare(square, vehicle, false);
    }

    public float getAirTemperatureForSquare(IsoGridSquare square, BaseVehicle vehicle, boolean doWindChill) {
        float float0 = this.getTemperature();
        if (square != null) {
            boolean boolean0 = square.isInARoom();
            if (boolean0 || vehicle != null) {
                boolean boolean1 = IsoWorld.instance.isHydroPowerOn();
                if (float0 <= 22.0F) {
                    float float1 = (22.0F - float0) / 8.0F;
                    if (vehicle == null) {
                        if (boolean0 && boolean1) {
                            float0 = 22.0F;
                        }

                        float1 = 22.0F - float0;
                        if (square.getZ() < 1) {
                            float0 += float1 * (0.4F + 0.2F * this.dayLightLagged);
                        } else {
                            float1 = (float)(float1 * 0.85);
                            float0 += float1 * (0.4F + 0.2F * this.dayLightLagged);
                        }
                    }
                } else {
                    float float2 = (float0 - 22.0F) / 3.5F;
                    if (vehicle == null) {
                        if (boolean0 && boolean1) {
                            float0 = 22.0F;
                        }

                        float2 = float0 - 22.0F;
                        if (square.getZ() < 1) {
                            float2 = (float)(float2 * 0.85);
                            float0 -= float2 * (0.4F + 0.2F * this.dayLightLagged);
                        } else {
                            float0 -= float2 * (0.4F + 0.2F * this.dayLightLagged + 0.2F * this.nightLagged);
                        }
                    } else {
                        float0 = float0 + float2 + float2 * this.dayLightLagged;
                    }
                }
            } else if (doWindChill) {
                float0 = Temperature.WindchillCelsiusKph(float0, this.getWindspeedKph());
            }

            float float3 = IsoWorld.instance.getCell().getHeatSourceHighestTemperature(float0, square.getX(), square.getY(), square.getZ());
            if (float3 > float0) {
                float0 = float3;
            }

            if (vehicle != null) {
                float0 += vehicle.getInsideTemperature();
            }
        }

        return float0;
    }

    public String getSeasonName() {
        return this.season.getSeasonName();
    }

    public float getSeasonProgression() {
        return this.season.getSeasonProgression();
    }

    public float getSeasonStrength() {
        return this.season.getSeasonStrength();
    }

    public void init(IsoMetaGrid metaGrid) {
        WorldFlares.Clear();
        this.season = ErosionMain.getInstance().getSeasons();
        ThunderStorm.MAP_MIN_X = metaGrid.minX * 300 - 4000;
        ThunderStorm.MAP_MAX_X = metaGrid.maxX * 300 + 4000;
        ThunderStorm.MAP_MIN_Y = metaGrid.minY * 300 - 4000;
        ThunderStorm.MAP_MAX_Y = metaGrid.maxY * 300 + 4000;
        windNoiseOffset = 0.0;
        WINTER_IS_COMING = IsoWorld.instance.getGameMode().equals("Winter is Coming");
        THE_DESCENDING_FOG = IsoWorld.instance.getGameMode().equals("The Descending Fog");
        A_STORM_IS_COMING = IsoWorld.instance.getGameMode().equals("A Storm is Coming");
        this.climateForecaster.init(this);
        this.climateHistory.init(this);
    }

    public void updateEveryTenMins() {
        this.tickIsTenMins = true;
    }

    public void update() {
        this.tickIsClimateTick = false;
        this.tickIsHourChange = false;
        this.tickIsDayChange = false;
        this.gt = GameTime.getInstance();
        this.worldAgeHours = this.gt.getWorldAgeHours();
        if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
            this.lastMinuteStamp = this.gt.getMinutesStamp();
            this.tickIsClimateTick = true;
            this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
            this.currentDay.hour = this.gt.getHour();
            this.currentDay.minutes = this.gt.getMinutes();
            if (this.gt.getHour() != this.lastHourStamp) {
                this.tickIsHourChange = true;
                this.lastHourStamp = this.gt.getHour();
            }

            if (this.gt.getTimeOfDay() > 12.0F) {
                ClimateMoon.updatePhase(this.currentDay.getYear(), this.currentDay.getMonth(), this.currentDay.getDay());
            }
        }

        if (this.DISABLE_SIMULATION) {
            IsoPlayer[] players = IsoPlayer.players;

            for (int int0 = 0; int0 < players.length; int0++) {
                IsoPlayer player = players[int0];
                if (player != null) {
                    player.dirtyRecalcGridStackTime = 1.0F;
                }
            }
        } else {
            if (this.tickIsDayChange && !GameClient.bClient) {
                this.climateForecaster.updateDayChange(this);
                this.climateHistory.updateDayChange(this);
            }

            if (GameClient.bClient) {
                this.networkLerp = 1.0F;
                long long0 = System.currentTimeMillis();
                if ((float)long0 < (float)this.networkUpdateStamp + this.networkLerpTime) {
                    this.networkLerp = (float)(long0 - this.networkUpdateStamp) / this.networkLerpTime;
                    if (this.networkLerp < 0.0F) {
                        this.networkLerp = 0.0F;
                    }
                }

                for (int int1 = 0; int1 < this.climateFloats.length; int1++) {
                    this.climateFloats[int1].interpolate = this.networkLerp;
                }

                for (int int2 = 0; int2 < this.climateColors.length; int2++) {
                    this.climateColors[int2].interpolate = this.networkLerp;
                }
            }

            if (this.tickIsClimateTick && !GameClient.bClient) {
                this.updateValues();
                this.weatherPeriod.update(this.worldAgeHours);
            }

            if (this.tickIsClimateTick) {
                LuaEventManager.triggerEvent("OnClimateTick", this);
            }

            for (int int3 = 0; int3 < this.climateColors.length; int3++) {
                this.climateColors[int3].calculate();
            }

            for (int int4 = 0; int4 < this.climateFloats.length; int4++) {
                this.climateFloats[int4].calculate();
            }

            for (int int5 = 0; int5 < this.climateBooleans.length; int5++) {
                this.climateBooleans[int5].calculate();
            }

            this.windPower = this.windIntensity.finalValue;
            this.updateWindTick();
            if (this.tickIsClimateTick) {
            }

            this.updateTestFlare();
            this.thunderStorm.update(this.worldAgeHours);
            if (GameClient.bClient) {
                this.updateSnow();
            } else if (this.tickIsClimateTick && !GameClient.bClient) {
                this.updateSnow();
            }

            if (!GameClient.bClient) {
                this.updateViewDistance();
            }

            if (this.tickIsClimateTick && Core.bDebug && !GameServer.bServer) {
                LuaEventManager.triggerEvent("OnClimateTickDebug", this);
            }

            if (this.tickIsClimateTick && GameServer.bServer && this.tickIsTenMins) {
                this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
                this.tickIsTenMins = false;
            }

            if (!this.DISABLE_FX_UPDATE) {
                this.updateFx();
            }
        }
    }

    public static double getWindNoiseBase() {
        return windNoiseBase;
    }

    public static double getWindNoiseFinal() {
        return windNoiseFinal;
    }

    public static double getWindTickFinal() {
        return windTickFinal;
    }

    private void updateWindTick() {
        if (!GameServer.bServer) {
            float float0 = this.windIntensity.finalValue;
            windNoiseOffset = windNoiseOffset + (4.0E-4 + 6.0E-4 * float0) * GameTime.getInstance().getMultiplier();
            windNoiseBase = SimplexNoise.noise(0.0, windNoiseOffset);
            windNoiseFinal = windNoiseBase;
            if (windNoiseFinal > 0.0) {
                windNoiseFinal *= 0.04 + 0.1 * float0;
            } else {
                windNoiseFinal *= 0.04 + 0.1 * float0 + 0.05F * (float0 * float0);
            }

            float0 = clamp01(float0 + (float)windNoiseFinal);
            windTickFinal = float0;
        }
    }

    public void updateOLD() {
        this.tickIsClimateTick = false;
        this.tickIsHourChange = false;
        this.tickIsDayChange = false;
        this.gt = GameTime.getInstance();
        this.worldAgeHours = this.gt.getWorldAgeHours();
        if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
            this.lastMinuteStamp = this.gt.getMinutesStamp();
            this.tickIsClimateTick = true;
            this.updateDayInfo(this.gt.getDay(), this.gt.getMonth(), this.gt.getYear());
            this.currentDay.hour = this.gt.getHour();
            this.currentDay.minutes = this.gt.getMinutes();
            if (this.gt.getHour() != this.lastHourStamp) {
                this.tickIsHourChange = true;
                this.lastHourStamp = this.gt.getHour();
            }
        }

        if (GameClient.bClient) {
            if (!this.DISABLE_SIMULATION) {
                this.networkLerp = 1.0F;
                long long0 = System.currentTimeMillis();
                if ((float)long0 < (float)this.networkUpdateStamp + this.networkLerpTime) {
                    this.networkLerp = (float)(long0 - this.networkUpdateStamp) / this.networkLerpTime;
                    if (this.networkLerp < 0.0F) {
                        this.networkLerp = 0.0F;
                    }
                }

                for (int int0 = 0; int0 < this.climateFloats.length; int0++) {
                    this.climateFloats[int0].interpolate = this.networkLerp;
                }

                for (int int1 = 0; int1 < this.climateColors.length; int1++) {
                    this.climateColors[int1].interpolate = this.networkLerp;
                }

                if (this.tickIsClimateTick) {
                    LuaEventManager.triggerEvent("OnClimateTick", this);
                }

                this.updateOnTick();
                this.updateTestFlare();
                this.thunderStorm.update(this.worldAgeHours);
                this.updateSnow();
                if (this.tickIsTenMins) {
                    this.tickIsTenMins = false;
                }
            }

            this.updateFx();
        } else {
            if (!this.DISABLE_SIMULATION) {
                if (this.tickIsClimateTick) {
                    this.updateValues();
                    this.weatherPeriod.update(this.gt.getWorldAgeHours());
                }

                this.updateOnTick();
                this.updateTestFlare();
                this.thunderStorm.update(this.worldAgeHours);
                if (this.tickIsClimateTick) {
                    this.updateSnow();
                    LuaEventManager.triggerEvent("OnClimateTick", this);
                }

                this.updateViewDistance();
                if (this.tickIsClimateTick && this.tickIsTenMins) {
                    if (GameServer.bServer) {
                        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
                    }

                    this.tickIsTenMins = false;
                }
            }

            if (!this.DISABLE_FX_UPDATE && this.tickIsClimateTick) {
                this.updateFx();
            }

            if (this.DISABLE_SIMULATION) {
                IsoPlayer[] players = IsoPlayer.players;

                for (int int2 = 0; int2 < players.length; int2++) {
                    IsoPlayer player = players[int2];
                    if (player != null) {
                        player.dirtyRecalcGridStackTime = 1.0F;
                    }
                }
            }
        }
    }

    private void updateFx() {
        IsoWeatherFX weatherFX = IsoWorld.instance.getCell().getWeatherFX();
        if (weatherFX != null) {
            weatherFX.setPrecipitationIntensity(this.precipitationIntensity.finalValue);
            weatherFX.setWindIntensity(this.windIntensity.finalValue);
            weatherFX.setWindPrecipIntensity((float)windTickFinal * (float)windTickFinal);
            weatherFX.setWindAngleIntensity(this.windAngleIntensity.finalValue);
            weatherFX.setFogIntensity(this.fogIntensity.finalValue);
            weatherFX.setCloudIntensity(this.cloudIntensity.finalValue);
            weatherFX.setPrecipitationIsSnow(this.precipitationIsSnow.finalValue);
            SkyBox.getInstance().update(this);
            IsoWater.getInstance().update(this);
            IsoPuddles.getInstance().update(this);
        }
    }

    private void updateSnow() {
        if (GameClient.bClient) {
            IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
            ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
        } else {
            if (!this.tickIsHourChange) {
                this.canDoWinterSprites = this.season.isSeason(5) || WINTER_IS_COMING;
                if (this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > this.snowFall) {
                    this.snowFall = this.precipitationIntensity.finalValue;
                }

                if (this.temperature.finalValue > 0.0F) {
                    float float0 = this.temperature.finalValue / 10.0F;
                    float0 = float0 * 0.2F + float0 * 0.8F * this.dayLightStrength.finalValue;
                    if (float0 > this.snowMeltStrength) {
                        this.snowMeltStrength = float0;
                    }
                }

                if (!this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > 0.0F) {
                    this.snowMeltStrength = this.snowMeltStrength + this.precipitationIntensity.finalValue;
                }
            } else {
                this.snowStrength = this.snowStrength + this.snowFall;
                this.snowStrength = this.snowStrength - this.snowMeltStrength;
                this.snowStrength = clamp(0.0F, 10.0F, this.snowStrength);
                this.snowFracNow = this.snowStrength > 7.5F ? 1.0F : this.snowStrength / 7.5F;
                IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
                ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
                this.snowFall = 0.0F;
                this.snowMeltStrength = 0.0F;
            }
        }
    }

    private void updateSnowOLD() {
    }

    public float getSnowFracNow() {
        return this.snowFracNow;
    }

    public void resetOverrides() {
        for (int int0 = 0; int0 < this.climateColors.length; int0++) {
            this.climateColors[int0].setEnableOverride(false);
        }

        for (int int1 = 0; int1 < this.climateFloats.length; int1++) {
            this.climateFloats[int1].setEnableOverride(false);
        }

        for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
            this.climateBooleans[int2].setEnableOverride(false);
        }
    }

    public void resetModded() {
        for (int int0 = 0; int0 < this.climateColors.length; int0++) {
            this.climateColors[int0].setEnableModded(false);
        }

        for (int int1 = 0; int1 < this.climateFloats.length; int1++) {
            this.climateFloats[int1].setEnableModded(false);
        }

        for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
            this.climateBooleans[int2].setEnableModded(false);
        }
    }

    public void resetAdmin() {
        for (int int0 = 0; int0 < this.climateColors.length; int0++) {
            this.climateColors[int0].setEnableAdmin(false);
        }

        for (int int1 = 0; int1 < this.climateFloats.length; int1++) {
            this.climateFloats[int1].setEnableAdmin(false);
        }

        for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
            this.climateBooleans[int2].setEnableAdmin(false);
        }
    }

    public void triggerWinterIsComingStorm() {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            ClimateManager.AirFront airFront = new ClimateManager.AirFront();
            airFront.copyFrom(this.currentFront);
            airFront.strength = 0.95F;
            airFront.type = 1;
            GameTime gameTime = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne());
        }
    }

    public boolean triggerCustomWeather(float strength, boolean warmFront) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            ClimateManager.AirFront airFront = new ClimateManager.AirFront();
            airFront.strength = strength;
            airFront.type = warmFront ? 1 : -1;
            GameTime gameTime = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne());
            return true;
        } else {
            return false;
        }
    }

    public boolean triggerCustomWeatherStage(int stage, float duration) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            ClimateManager.AirFront airFront = new ClimateManager.AirFront();
            airFront.strength = 0.95F;
            airFront.type = 1;
            GameTime gameTime = GameTime.getInstance();
            this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne(), stage, duration);
            return true;
        } else {
            return false;
        }
    }

    private void updateOnTick() {
        for (int int0 = 0; int0 < this.climateColors.length; int0++) {
            this.climateColors[int0].calculate();
        }

        for (int int1 = 0; int1 < this.climateFloats.length; int1++) {
            this.climateFloats[int1].calculate();
        }

        for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
            this.climateBooleans[int2].calculate();
        }
    }

    private void updateTestFlare() {
        WorldFlares.update();
    }

    public void launchFlare() {
        DebugLog.log("Launching improved flare.");
        IsoPlayer player = IsoPlayer.getInstance();
        float float0 = 0.0F;
        WorldFlares.launchFlare(7200.0F, (int)player.getX(), (int)player.getY(), 50, float0, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
        if (IsoPlayer.getInstance() != null && !this.flareLaunched) {
            this.flareLaunched = true;
            this.flareLifeTime = 0.0F;
            this.flareMaxLifeTime = 7200.0F;
            this.flareIntensity.overrideCurrentValue(1.0F);
            this.flareIntens = 1.0F;
            this.nextRandomTargetIntens = 10;
        }
    }

    protected double getAirMassNoiseFrequencyMod(int int0) {
        if (int0 == 1) {
            return 300.0;
        } else if (int0 == 2) {
            return 240.0;
        } else {
            if (int0 != 3) {
                if (int0 == 4) {
                    return 145.0;
                }

                if (int0 == 5) {
                    return 120.0;
                }
            }

            return 166.0;
        }
    }

    protected float getRainTimeMultiplierMod(int int0) {
        if (int0 == 1) {
            return 0.5F;
        } else if (int0 == 2) {
            return 0.75F;
        } else if (int0 == 4) {
            return 1.25F;
        } else {
            return int0 == 5 ? 1.5F : 1.0F;
        }
    }

    private void updateValues() {
        if (this.tickIsDayChange && Core.bDebug && !GameClient.bClient && !GameServer.bServer) {
            ErosionMain.getInstance().DebugUpdateMapNow();
        }

        this.climateValues.updateValues(this.worldAgeHours, this.gt.getTimeOfDay(), this.currentDay, this.nextDay);
        this.airMass = this.climateValues.getNoiseAirmass();
        this.airMassTemperature = this.climateValues.getAirMassTemperature();
        if (this.tickIsHourChange) {
            int int0 = this.airMass < 0.0F ? -1 : 1;
            if (this.currentFront.type != int0) {
                if (!this.DISABLE_WEATHER_GENERATION && (!WINTER_IS_COMING || WINTER_IS_COMING && GameTime.instance.getWorldAgeHours() > 96.0)) {
                    if (THE_DESCENDING_FOG) {
                        this.currentFront.type = -1;
                        this.currentFront.strength = Rand.Next(0.2F, 0.45F);
                        this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
                    } else {
                        this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
                        this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
                    }
                }

                this.currentFront.setFrontType(int0);
            }

            if (!WINTER_IS_COMING
                && !THE_DESCENDING_FOG
                && GameTime.instance.getWorldAgeHours() >= 72.0
                && GameTime.instance.getWorldAgeHours() <= 96.0
                && !this.DISABLE_WEATHER_GENERATION
                && !this.weatherPeriod.isRunning()
                && Rand.Next(0, 1000) < 50) {
            }

            if (this.tickIsDayChange) {
            }
        }

        this.dayDoFog = this.climateValues.isDayDoFog();
        this.dayFogStrength = this.climateValues.getDayFogStrength();
        if (PerformanceSettings.FogQuality == 2) {
            this.dayFogStrength = 0.5F + 0.5F * this.dayFogStrength;
        } else {
            this.dayFogStrength = 0.2F + 0.8F * this.dayFogStrength;
        }

        this.baseTemperature = this.climateValues.getBaseTemperature();
        this.dayLightLagged = this.climateValues.getDayLightLagged();
        this.nightLagged = this.climateValues.getDayLightLagged();
        this.temperature.internalValue = this.climateValues.getTemperature();
        this.precipitationIsSnow.internalValue = this.climateValues.isTemperatureIsSnow();
        this.humidity.internalValue = this.climateValues.getHumidity();
        this.windIntensity.internalValue = this.climateValues.getWindIntensity();
        this.windAngleIntensity.internalValue = this.climateValues.getWindAngleIntensity();
        this.windPower = this.windIntensity.internalValue;
        this.currentFront.setFrontWind(this.climateValues.getWindAngleDegrees());
        this.cloudIntensity.internalValue = this.climateValues.getCloudIntensity();
        this.precipitationIntensity.internalValue = 0.0F;
        this.nightStrength.internalValue = this.climateValues.getNightStrength();
        this.dayLightStrength.internalValue = this.climateValues.getDayLightStrength();
        this.ambient.internalValue = this.climateValues.getAmbient();
        this.desaturation.internalValue = this.climateValues.getDesaturation();
        int int1 = this.season.getSeason();
        float float0 = this.season.getSeasonProgression();
        float float1 = 0.0F;
        int int2 = 0;
        int int3 = 0;
        if (int1 == 2) {
            int2 = ClimateManager.SeasonColor.SPRING;
            int3 = ClimateManager.SeasonColor.SUMMER;
            float1 = 0.5F + float0 * 0.5F;
        } else if (int1 == 3) {
            int2 = ClimateManager.SeasonColor.SUMMER;
            int3 = ClimateManager.SeasonColor.FALL;
            float1 = float0 * 0.5F;
        } else if (int1 == 4) {
            if (float0 < 0.5F) {
                int2 = ClimateManager.SeasonColor.SUMMER;
                int3 = ClimateManager.SeasonColor.FALL;
                float1 = 0.5F + float0;
            } else {
                int2 = ClimateManager.SeasonColor.FALL;
                int3 = ClimateManager.SeasonColor.WINTER;
                float1 = float0 - 0.5F;
            }
        } else if (int1 == 5) {
            if (float0 < 0.5F) {
                int2 = ClimateManager.SeasonColor.FALL;
                int3 = ClimateManager.SeasonColor.WINTER;
                float1 = 0.5F + float0;
            } else {
                int2 = ClimateManager.SeasonColor.WINTER;
                int3 = ClimateManager.SeasonColor.SPRING;
                float1 = float0 - 0.5F;
            }
        } else if (int1 == 1) {
            if (float0 < 0.5F) {
                int2 = ClimateManager.SeasonColor.WINTER;
                int3 = ClimateManager.SeasonColor.SPRING;
                float1 = 0.5F + float0;
            } else {
                int2 = ClimateManager.SeasonColor.SPRING;
                int3 = ClimateManager.SeasonColor.SUMMER;
                float1 = float0 - 0.5F;
            }
        }

        float float2 = this.climateValues.getCloudyT();
        this.colDawn = this.seasonColorDawn.update(float2, float1, int2, int3);
        this.colDay = this.seasonColorDay.update(float2, float1, int2, int3);
        this.colDusk = this.seasonColorDusk.update(float2, float1, int2, int3);
        float float3 = this.climateValues.getTime();
        float float4 = this.climateValues.getDawn();
        float float5 = this.climateValues.getDusk();
        float float6 = this.climateValues.getNoon();
        float float7 = this.climateValues.getDayFogDuration();
        if (!THE_DESCENDING_FOG) {
            if (this.dayDoFog && this.dayFogStrength > 0.0F && float3 > float4 - 2.0F && float3 < float4 + float7) {
                float float8 = this.getTimeLerpHours(float3, float4 - 2.0F, float4 + float7, true);
                float8 = clamp(0.0F, 1.0F, float8 * (float7 / 3.0F));
                this.fogLerpValue = float8;
                this.cloudIntensity.internalValue = lerp(float8, this.cloudIntensity.internalValue, 0.0F);
                float float9 = this.dayFogStrength;
                this.fogIntensity.internalValue = clerp(float8, 0.0F, float9);
                if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
                    this.desaturation.internalValue = clerp(float8, this.desaturation.internalValue, 0.8F * float9);
                } else if (PerformanceSettings.FogQuality == 2) {
                    this.desaturation.internalValue = clerp(float8, this.desaturation.internalValue, 0.8F * float9);
                } else {
                    this.desaturation.internalValue = clerp(float8, this.desaturation.internalValue, 0.65F * float9);
                }
            } else {
                this.fogIntensity.internalValue = 0.0F;
            }
        } else {
            if (this.gt.getWorldAgeHours() < 72.0) {
                this.fogIntensity.internalValue = (float)this.gt.getWorldAgeHours() / 72.0F;
            } else {
                this.fogIntensity.internalValue = 1.0F;
            }

            this.cloudIntensity.internalValue = Math.min(this.cloudIntensity.internalValue, 1.0F - this.fogIntensity.internalValue);
            if (this.weatherPeriod.isRunning()) {
                this.fogIntensity.internalValue = Math.min(this.fogIntensity.internalValue, 0.6F);
            }

            if (PerformanceSettings.FogQuality == 2) {
                this.fogIntensity.internalValue *= 0.93F;
                this.desaturation.internalValue = 0.8F * this.fogIntensity.internalValue;
            } else {
                this.desaturation.internalValue = 0.65F * this.fogIntensity.internalValue;
            }
        }

        this.humidity.internalValue = clamp01(this.humidity.internalValue + this.fogIntensity.internalValue * 0.6F);
        float float10 = 0.6F * this.climateValues.getDayLightStrengthBase();
        float float11 = 0.4F;
        float float12 = 0.25F * this.climateValues.getDayLightStrengthBase();
        if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
            float12 = 0.8F * this.climateValues.getDayLightStrengthBase();
        }

        if (float3 < float4 || float3 > float5) {
            float float13 = 24.0F - float5 + float4;
            if (float3 > float5) {
                float float14 = (float3 - float5) / float13;
                this.colDusk.interp(this.colDawn, float14, this.globalLight.internalValue);
            } else {
                float float15 = (24.0F - float5 + float3) / float13;
                this.colDusk.interp(this.colDawn, float15, this.globalLight.internalValue);
            }

            this.globalLightIntensity.internalValue = lerp(this.climateValues.getLerpNight(), float12, float11);
        } else if (float3 < float6 + 2.0F) {
            float float16 = (float3 - float4) / (float6 + 2.0F - float4);
            this.colDawn.interp(this.colDay, float16, this.globalLight.internalValue);
            this.globalLightIntensity.internalValue = lerp(float16, float12, float10);
        } else {
            float float17 = (float3 - (float6 + 2.0F)) / (float5 - (float6 + 2.0F));
            this.colDay.interp(this.colDusk, float17, this.globalLight.internalValue);
            this.globalLightIntensity.internalValue = lerp(float17, float10, float12);
        }

        if (this.fogIntensity.internalValue > 0.0F) {
            if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
                this.globalLight.internalValue.interp(this.colFogLegacy, this.fogIntensity.internalValue, this.globalLight.internalValue);
            } else if (PerformanceSettings.FogQuality == 2) {
                this.globalLight.internalValue.interp(this.colFog, this.fogIntensity.internalValue, this.globalLight.internalValue);
            } else {
                this.globalLight.internalValue.interp(this.colFogNew, this.fogIntensity.internalValue, this.globalLight.internalValue);
            }

            this.globalLightIntensity.internalValue = clerp(this.fogLerpValue, this.globalLightIntensity.internalValue, 0.8F);
        }

        this.colNightNoMoon.interp(this.colNightMoon, ClimateMoon.getMoonFloat(), this.colNight);
        this.globalLight.internalValue.interp(this.colNight, this.nightStrength.internalValue, this.globalLight.internalValue);
        IsoPlayer[] players = IsoPlayer.players;

        for (int int4 = 0; int4 < players.length; int4++) {
            IsoPlayer player = players[int4];
            if (player != null) {
                player.dirtyRecalcGridStackTime = 1.0F;
            }
        }
    }

    private void updateViewDistance() {
        float float0 = this.dayLightStrength.finalValue;
        float float1 = this.fogIntensity.finalValue;
        float float2 = 19.0F - float1 * 8.0F;
        float float3 = float2 + 4.0F + 7.0F * float0 * (1.0F - float1);
        float2 *= 3.0F;
        float3 *= 3.0F;
        this.gt.setViewDistMin(float2);
        this.gt.setViewDistMax(float3);
        this.viewDistance.internalValue = float2 + (float3 - float2) * float0;
        this.viewDistance.finalValue = this.viewDistance.internalValue;
    }

    public void setSeasonColorDawn(int _temperature, int _season, float r, float g, float b, float a, boolean exterior) {
        if (exterior) {
            this.seasonColorDawn.setColorExterior(_temperature, _season, r, g, b, a);
        } else {
            this.seasonColorDawn.setColorInterior(_temperature, _season, r, g, b, a);
        }
    }

    public void setSeasonColorDay(int _temperature, int _season, float r, float g, float b, float a, boolean exterior) {
        if (exterior) {
            this.seasonColorDay.setColorExterior(_temperature, _season, r, g, b, a);
        } else {
            this.seasonColorDay.setColorInterior(_temperature, _season, r, g, b, a);
        }
    }

    public void setSeasonColorDusk(int _temperature, int _season, float r, float g, float b, float a, boolean exterior) {
        if (exterior) {
            this.seasonColorDusk.setColorExterior(_temperature, _season, r, g, b, a);
        } else {
            this.seasonColorDusk.setColorInterior(_temperature, _season, r, g, b, a);
        }
    }

    public ClimateColorInfo getSeasonColor(int segment, int _temperature, int _season) {
        ClimateManager.SeasonColor seasonColor = null;
        if (segment == 0) {
            seasonColor = this.seasonColorDawn;
        } else if (segment == 1) {
            seasonColor = this.seasonColorDay;
        } else if (segment == 2) {
            seasonColor = this.seasonColorDusk;
        }

        return seasonColor != null ? seasonColor.getColor(_temperature, _season) : null;
    }

    private void initSeasonColors() {
        ClimateManager.SeasonColor seasonColor = new ClimateManager.SeasonColor();
        seasonColor.setIgnoreNormal(true);
        this.seasonColorDawn = seasonColor;
        seasonColor = new ClimateManager.SeasonColor();
        seasonColor.setIgnoreNormal(true);
        this.seasonColorDay = seasonColor;
        seasonColor = new ClimateManager.SeasonColor();
        seasonColor.setIgnoreNormal(false);
        this.seasonColorDusk = seasonColor;
    }

    /**
     * IO
     */
    public void save(DataOutputStream output) throws IOException {
        if (GameClient.bClient && !GameServer.bServer) {
            output.writeByte(0);
        } else {
            output.writeByte(1);
            output.writeDouble(this.simplexOffsetA);
            output.writeDouble(this.simplexOffsetB);
            output.writeDouble(this.simplexOffsetC);
            output.writeDouble(this.simplexOffsetD);
            this.currentFront.save(output);
            output.writeFloat(this.snowFracNow);
            output.writeFloat(this.snowStrength);
            output.writeBoolean(this.canDoWinterSprites);
            output.writeBoolean(this.dayDoFog);
            output.writeFloat(this.dayFogStrength);
        }

        this.weatherPeriod.save(output);
        this.thunderStorm.save(output);
        if (GameServer.bServer) {
            this.desaturation.saveAdmin(output);
            this.globalLightIntensity.saveAdmin(output);
            this.nightStrength.saveAdmin(output);
            this.precipitationIntensity.saveAdmin(output);
            this.temperature.saveAdmin(output);
            this.fogIntensity.saveAdmin(output);
            this.windIntensity.saveAdmin(output);
            this.windAngleIntensity.saveAdmin(output);
            this.cloudIntensity.saveAdmin(output);
            this.ambient.saveAdmin(output);
            this.viewDistance.saveAdmin(output);
            this.dayLightStrength.saveAdmin(output);
            this.globalLight.saveAdmin(output);
            this.precipitationIsSnow.saveAdmin(output);
        }

        if (this.modDataTable != null) {
            output.writeByte(1);
            this.modDataTable.save(output);
        } else {
            output.writeByte(0);
        }

        if (GameServer.bServer) {
            this.humidity.saveAdmin(output);
        }
    }

    public void load(DataInputStream input, int worldVersion) throws IOException {
        boolean boolean0 = input.readByte() == 1;
        if (boolean0) {
            this.simplexOffsetA = input.readDouble();
            this.simplexOffsetB = input.readDouble();
            this.simplexOffsetC = input.readDouble();
            this.simplexOffsetD = input.readDouble();
            this.currentFront.load(input);
            this.snowFracNow = input.readFloat();
            this.snowStrength = input.readFloat();
            this.canDoWinterSprites = input.readBoolean();
            this.dayDoFog = input.readBoolean();
            this.dayFogStrength = input.readFloat();
        }

        this.weatherPeriod.load(input, worldVersion);
        this.thunderStorm.load(input);
        if (worldVersion >= 140 && GameServer.bServer) {
            this.desaturation.loadAdmin(input, worldVersion);
            this.globalLightIntensity.loadAdmin(input, worldVersion);
            this.nightStrength.loadAdmin(input, worldVersion);
            this.precipitationIntensity.loadAdmin(input, worldVersion);
            this.temperature.loadAdmin(input, worldVersion);
            this.fogIntensity.loadAdmin(input, worldVersion);
            this.windIntensity.loadAdmin(input, worldVersion);
            this.windAngleIntensity.loadAdmin(input, worldVersion);
            this.cloudIntensity.loadAdmin(input, worldVersion);
            this.ambient.loadAdmin(input, worldVersion);
            this.viewDistance.loadAdmin(input, worldVersion);
            this.dayLightStrength.loadAdmin(input, worldVersion);
            this.globalLight.loadAdmin(input, worldVersion);
            this.precipitationIsSnow.loadAdmin(input, worldVersion);
        }

        if (worldVersion >= 141 && input.readByte() == 1) {
            if (this.modDataTable == null) {
                this.modDataTable = LuaManager.platform.newTable();
            }

            this.modDataTable.load(input, worldVersion);
        }

        if (worldVersion >= 150 && GameServer.bServer) {
            this.humidity.loadAdmin(input, worldVersion);
        }

        this.climateValues = new ClimateValues(this);
    }

    public void postCellLoadSetSnow() {
        IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
        ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
    }

    public void forceDayInfoUpdate() {
        this.currentDay.day = -1;
        this.currentDay.month = -1;
        this.currentDay.year = -1;
        this.gt = GameTime.getInstance();
        this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
        this.currentDay.hour = this.gt.getHour();
        this.currentDay.minutes = this.gt.getMinutes();
    }

    private void updateDayInfo(int int2, int int1, int int0) {
        this.tickIsDayChange = false;
        if (this.currentDay == null || this.currentDay.day != int2 || this.currentDay.month != int1 || this.currentDay.year != int0) {
            this.tickIsDayChange = this.currentDay != null;
            if (this.currentDay == null) {
                this.currentDay = new ClimateManager.DayInfo();
            }

            this.setDayInfo(this.currentDay, int2, int1, int0, 0);
            if (this.previousDay == null) {
                this.previousDay = new ClimateManager.DayInfo();
                this.previousDay.season = this.season.clone();
            }

            this.setDayInfo(this.previousDay, int2, int1, int0, -1);
            if (this.nextDay == null) {
                this.nextDay = new ClimateManager.DayInfo();
                this.nextDay.season = this.season.clone();
            }

            this.setDayInfo(this.nextDay, int2, int1, int0, 1);
        }
    }

    protected void setDayInfo(ClimateManager.DayInfo dayInfo, int int2, int int1, int int0, int int3) {
        dayInfo.calendar = new GregorianCalendar(int0, int1, int2, 0, 0);
        dayInfo.calendar.add(5, int3);
        dayInfo.day = dayInfo.calendar.get(5);
        dayInfo.month = dayInfo.calendar.get(2);
        dayInfo.year = dayInfo.calendar.get(1);
        dayInfo.dateValue = dayInfo.calendar.getTime().getTime();
        if (dayInfo.season == null) {
            dayInfo.season = this.season.clone();
        }

        dayInfo.season.setDay(dayInfo.day, dayInfo.month, dayInfo.year);
    }

    protected final void transmitClimatePacket(ClimateManager.ClimateNetAuth climateNetAuth, byte byte0, UdpConnection udpConnection1) {
        if (GameClient.bClient || GameServer.bServer) {
            if (climateNetAuth == ClimateManager.ClimateNetAuth.Denied) {
                DebugLog.log("Denied ClimatePacket, id = " + byte0 + ", isClient = " + GameClient.bClient);
            } else {
                if (GameClient.bClient
                    && (climateNetAuth == ClimateManager.ClimateNetAuth.ClientOnly || climateNetAuth == ClimateManager.ClimateNetAuth.ClientAndServer)) {
                    try {
                        if (this.writePacketContents(GameClient.connection, byte0)) {
                            PacketTypes.PacketType.ClimateManagerPacket.send(GameClient.connection);
                        } else {
                            GameClient.connection.cancelPacket();
                        }
                    } catch (Exception exception0) {
                        DebugLog.log(exception0.getMessage());
                    }
                }

                if (GameServer.bServer
                    && (climateNetAuth == ClimateManager.ClimateNetAuth.ServerOnly || climateNetAuth == ClimateManager.ClimateNetAuth.ClientAndServer)) {
                    try {
                        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                            UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
                            if (udpConnection1 == null || udpConnection1 != udpConnection0) {
                                if (this.writePacketContents(udpConnection0, byte0)) {
                                    PacketTypes.PacketType.ClimateManagerPacket.send(udpConnection0);
                                } else {
                                    udpConnection0.cancelPacket();
                                }
                            }
                        }
                    } catch (Exception exception1) {
                        DebugLog.log(exception1.getMessage());
                    }
                }
            }
        }
    }

    private boolean writePacketContents(UdpConnection udpConnection, byte byte0) throws IOException {
        if (!GameClient.bClient && !GameServer.bServer) {
            return false;
        } else {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.ClimateManagerPacket.doPacket(byteBufferWriter);
            ByteBuffer byteBuffer = byteBufferWriter.bb;
            byteBuffer.put(byte0);
            switch (byte0) {
                case 0:
                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketUpdateClimateVars");
                    }

                    for (int int0 = 0; int0 < this.climateFloats.length; int0++) {
                        byteBuffer.putFloat(this.climateFloats[int0].finalValue);
                    }

                    for (int int1 = 0; int1 < this.climateColors.length; int1++) {
                        this.climateColors[int1].finalValue.write(byteBuffer);
                    }

                    for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
                        byteBuffer.put((byte)(this.climateBooleans[int2].finalValue ? 1 : 0));
                    }

                    byteBuffer.putFloat(this.airMass);
                    byteBuffer.putFloat(this.airMassDaily);
                    byteBuffer.putFloat(this.airMassTemperature);
                    byteBuffer.putFloat(this.snowFracNow);
                    byteBuffer.putFloat(this.snowStrength);
                    byteBuffer.putFloat(this.windPower);
                    byteBuffer.put((byte)(this.dayDoFog ? 1 : 0));
                    byteBuffer.putFloat(this.dayFogStrength);
                    byteBuffer.put((byte)(this.canDoWinterSprites ? 1 : 0));
                    this.weatherPeriod.writeNetWeatherData(byteBuffer);
                    return true;
                case 1:
                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketWeatherUpdate");
                    }

                    this.weatherPeriod.writeNetWeatherData(byteBuffer);
                    return true;
                case 2:
                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketThunderEvent");
                    }

                    this.thunderStorm.writeNetThunderEvent(byteBuffer);
                    return true;
                case 3:
                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketFlare");
                    }

                    return true;
                case 4:
                    if (!GameServer.bServer) {
                        return false;
                    }

                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketAdminVarsUpdate");
                    }

                    for (int int6 = 0; int6 < this.climateFloats.length; int6++) {
                        this.climateFloats[int6].writeAdmin(byteBuffer);
                    }

                    for (int int7 = 0; int7 < this.climateColors.length; int7++) {
                        this.climateColors[int7].writeAdmin(byteBuffer);
                    }

                    for (int int8 = 0; int8 < this.climateBooleans.length; int8++) {
                        this.climateBooleans[int8].writeAdmin(byteBuffer);
                    }

                    return true;
                case 5:
                    if (!GameClient.bClient) {
                        return false;
                    }

                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketRequestAdminVars");
                    }

                    byteBuffer.put((byte)1);
                    return true;
                case 6:
                    if (!GameClient.bClient) {
                        return false;
                    }

                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketClientChangedAdminVars");
                    }

                    for (int int3 = 0; int3 < this.climateFloats.length; int3++) {
                        this.climateFloats[int3].writeAdmin(byteBuffer);
                    }

                    for (int int4 = 0; int4 < this.climateColors.length; int4++) {
                        this.climateColors[int4].writeAdmin(byteBuffer);
                    }

                    for (int int5 = 0; int5 < this.climateBooleans.length; int5++) {
                        this.climateBooleans[int5].writeAdmin(byteBuffer);
                    }

                    return true;
                case 7:
                    if (!GameClient.bClient) {
                        return false;
                    }

                    if (this.networkPrint) {
                        DebugLog.log("clim: send PacketClientChangedWeather");
                    }

                    byteBuffer.put((byte)(this.netInfo.IsStopWeather ? 1 : 0));
                    byteBuffer.put((byte)(this.netInfo.IsTrigger ? 1 : 0));
                    byteBuffer.put((byte)(this.netInfo.IsGenerate ? 1 : 0));
                    byteBuffer.putFloat(this.netInfo.TriggerDuration);
                    byteBuffer.put((byte)(this.netInfo.TriggerStorm ? 1 : 0));
                    byteBuffer.put((byte)(this.netInfo.TriggerTropical ? 1 : 0));
                    byteBuffer.put((byte)(this.netInfo.TriggerBlizzard ? 1 : 0));
                    byteBuffer.putFloat(this.netInfo.GenerateStrength);
                    byteBuffer.putInt(this.netInfo.GenerateFront);
                    return true;
                default:
                    return false;
            }
        }
    }

    public final void receiveClimatePacket(ByteBuffer bb, UdpConnection ignoreConnection) throws IOException {
        if (GameClient.bClient || GameServer.bServer) {
            byte byte0 = bb.get();
            this.readPacketContents(bb, byte0, ignoreConnection);
        }
    }

    private boolean readPacketContents(ByteBuffer byteBuffer, byte byte0, UdpConnection var3) throws IOException {
        switch (byte0) {
            case 0:
                if (!GameClient.bClient) {
                    return false;
                }

                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketUpdateClimateVars");
                }

                for (int int0 = 0; int0 < this.climateFloats.length; int0++) {
                    ClimateManager.ClimateFloat climateFloat = this.climateFloats[int0];
                    climateFloat.internalValue = climateFloat.finalValue;
                    climateFloat.setOverride(byteBuffer.getFloat(), 0.0F);
                }

                for (int int1 = 0; int1 < this.climateColors.length; int1++) {
                    ClimateManager.ClimateColor climateColor = this.climateColors[int1];
                    climateColor.internalValue.setTo(climateColor.finalValue);
                    climateColor.setOverride(byteBuffer, 0.0F);
                }

                for (int int2 = 0; int2 < this.climateBooleans.length; int2++) {
                    ClimateManager.ClimateBool climateBool = this.climateBooleans[int2];
                    climateBool.setOverride(byteBuffer.get() == 1);
                }

                this.airMass = byteBuffer.getFloat();
                this.airMassDaily = byteBuffer.getFloat();
                this.airMassTemperature = byteBuffer.getFloat();
                this.snowFracNow = byteBuffer.getFloat();
                this.snowStrength = byteBuffer.getFloat();
                this.windPower = byteBuffer.getFloat();
                this.dayDoFog = byteBuffer.get() == 1;
                this.dayFogStrength = byteBuffer.getFloat();
                this.canDoWinterSprites = byteBuffer.get() == 1;
                long long0 = System.currentTimeMillis();
                if ((float)(long0 - this.networkUpdateStamp) < this.networkLerpTime) {
                    this.networkAdjustVal++;
                    if (this.networkAdjustVal > 10.0F) {
                        this.networkAdjustVal = 10.0F;
                    }
                } else {
                    this.networkAdjustVal--;
                    if (this.networkAdjustVal < 0.0F) {
                        this.networkAdjustVal = 0.0F;
                    }
                }

                if (this.networkAdjustVal > 0.0F) {
                    this.networkLerpTime = this.networkLerpTimeBase / this.networkAdjustVal;
                } else {
                    this.networkLerpTime = this.networkLerpTimeBase;
                }

                this.networkUpdateStamp = long0;
                this.weatherPeriod.readNetWeatherData(byteBuffer);
                return true;
            case 1:
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketWeatherUpdate");
                }

                this.weatherPeriod.readNetWeatherData(byteBuffer);
                return true;
            case 2:
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketThunderEvent");
                }

                this.thunderStorm.readNetThunderEvent(byteBuffer);
                return true;
            case 3:
                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketFlare");
                }

                return true;
            case 4:
                if (!GameClient.bClient) {
                    return false;
                }

                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketAdminVarsUpdate");
                }

                for (int int6 = 0; int6 < this.climateFloats.length; int6++) {
                    this.climateFloats[int6].readAdmin(byteBuffer);
                }

                for (int int7 = 0; int7 < this.climateColors.length; int7++) {
                    this.climateColors[int7].readAdmin(byteBuffer);
                }

                for (int int8 = 0; int8 < this.climateBooleans.length; int8++) {
                    this.climateBooleans[int8].readAdmin(byteBuffer);
                }

                return true;
            case 5:
                if (!GameServer.bServer) {
                    return false;
                }

                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketRequestAdminVars");
                }

                byteBuffer.get();
                this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)4, null);
                return true;
            case 6:
                if (!GameServer.bServer) {
                    return false;
                }

                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketClientChangedAdminVars");
                }

                for (int int3 = 0; int3 < this.climateFloats.length; int3++) {
                    this.climateFloats[int3].readAdmin(byteBuffer);
                }

                for (int int4 = 0; int4 < this.climateColors.length; int4++) {
                    this.climateColors[int4].readAdmin(byteBuffer);
                }

                for (int int5 = 0; int5 < this.climateBooleans.length; int5++) {
                    this.climateBooleans[int5].readAdmin(byteBuffer);
                    if (int5 == 0) {
                        DebugLog.log("Snow = " + this.climateBooleans[int5].adminValue + ", enabled = " + this.climateBooleans[int5].isAdminOverride);
                    }
                }

                this.serverReceiveClientChangeAdminVars();
                return true;
            case 7:
                if (!GameServer.bServer) {
                    return false;
                }

                if (this.networkPrint) {
                    DebugLog.log("clim: receive PacketClientChangedWeather");
                }

                this.netInfo.IsStopWeather = byteBuffer.get() == 1;
                this.netInfo.IsTrigger = byteBuffer.get() == 1;
                this.netInfo.IsGenerate = byteBuffer.get() == 1;
                this.netInfo.TriggerDuration = byteBuffer.getFloat();
                this.netInfo.TriggerStorm = byteBuffer.get() == 1;
                this.netInfo.TriggerTropical = byteBuffer.get() == 1;
                this.netInfo.TriggerBlizzard = byteBuffer.get() == 1;
                this.netInfo.GenerateStrength = byteBuffer.getFloat();
                this.netInfo.GenerateFront = byteBuffer.getInt();
                this.serverReceiveClientChangeWeather();
                return true;
            default:
                return false;
        }
    }

    private void serverReceiveClientChangeAdminVars() {
        if (GameServer.bServer) {
            if (this.networkPrint) {
                DebugLog.log("clim: serverReceiveClientChangeAdminVars");
            }

            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)4, null);
            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    private void serverReceiveClientChangeWeather() {
        if (GameServer.bServer) {
            if (this.networkPrint) {
                DebugLog.log("clim: serverReceiveClientChangeWeather");
            }

            if (this.netInfo.IsStopWeather) {
                if (this.networkPrint) {
                    DebugLog.log("clim: IsStopWeather");
                }

                this.stopWeatherAndThunder();
            } else if (this.netInfo.IsTrigger) {
                this.stopWeatherAndThunder();
                if (this.netInfo.TriggerStorm) {
                    if (this.networkPrint) {
                        DebugLog.log("clim: Trigger Storm");
                    }

                    this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
                } else if (this.netInfo.TriggerTropical) {
                    if (this.networkPrint) {
                        DebugLog.log("clim: Trigger Tropical");
                    }

                    this.triggerCustomWeatherStage(8, this.netInfo.TriggerDuration);
                } else if (this.netInfo.TriggerBlizzard) {
                    if (this.networkPrint) {
                        DebugLog.log("clim: Trigger Blizzard");
                    }

                    this.triggerCustomWeatherStage(7, this.netInfo.TriggerDuration);
                }
            } else if (this.netInfo.IsGenerate) {
                if (this.networkPrint) {
                    DebugLog.log("clim: IsGenerate");
                }

                this.stopWeatherAndThunder();
                this.triggerCustomWeather(this.netInfo.GenerateStrength, this.netInfo.GenerateFront == 0);
            }

            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    public void transmitServerStopWeather() {
        if (GameServer.bServer) {
            this.stopWeatherAndThunder();
            if (this.networkPrint) {
                DebugLog.log("clim: SERVER transmitStopWeather");
            }

            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    public void transmitServerTriggerStorm(float duration) {
        if (GameServer.bServer) {
            if (this.networkPrint) {
                DebugLog.log("clim: SERVER transmitTriggerStorm");
            }

            this.netInfo.TriggerDuration = duration;
            this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    public void transmitServerTriggerLightning(int x, int y, boolean doStrike, boolean doLightning, boolean doRumble) {
        if (GameServer.bServer) {
            if (this.networkPrint) {
                DebugLog.log("clim: SERVER transmitTriggerLightning");
            }

            this.thunderStorm.triggerThunderEvent(x, y, doStrike, doLightning, doRumble);
        }
    }

    public void transmitServerStartRain(float intensity) {
        if (GameServer.bServer) {
            this.precipitationIntensity.setAdminValue(clamp01(intensity));
            this.precipitationIntensity.setEnableAdmin(true);
            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    public void transmitServerStopRain() {
        if (GameServer.bServer) {
            this.precipitationIntensity.setEnableAdmin(false);
            this.updateOnTick();
            this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, null);
        }
    }

    public void transmitRequestAdminVars() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitRequestAdminVars");
        }

        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)5, null);
    }

    public void transmitClientChangeAdminVars() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitClientChangeAdminVars");
        }

        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)6, null);
    }

    public void transmitStopWeather() {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitStopWeather");
        }

        this.netInfo.reset();
        this.netInfo.IsStopWeather = true;
        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, null);
    }

    public void transmitTriggerStorm(float duration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerStorm");
        }

        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerStorm = true;
        this.netInfo.TriggerDuration = duration;
        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, null);
    }

    public void transmitTriggerTropical(float duration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerTropical");
        }

        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerTropical = true;
        this.netInfo.TriggerDuration = duration;
        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, null);
    }

    public void transmitTriggerBlizzard(float duration) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitTriggerBlizzard");
        }

        this.netInfo.reset();
        this.netInfo.IsTrigger = true;
        this.netInfo.TriggerBlizzard = true;
        this.netInfo.TriggerDuration = duration;
        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, null);
    }

    public void transmitGenerateWeather(float strength, int front) {
        if (this.networkPrint) {
            DebugLog.log("clim: transmitGenerateWeather");
        }

        this.netInfo.reset();
        this.netInfo.IsGenerate = true;
        this.netInfo.GenerateStrength = clamp01(strength);
        this.netInfo.GenerateFront = front;
        if (this.netInfo.GenerateFront < 0 || this.netInfo.GenerateFront > 1) {
            this.netInfo.GenerateFront = 0;
        }

        this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, null);
    }

    protected float getTimeLerpHours(float float0, float float1, float float2) {
        return this.getTimeLerpHours(float0, float1, float2, false);
    }

    protected float getTimeLerpHours(float float2, float float1, float float0, boolean boolean0) {
        return this.getTimeLerp(clamp(0.0F, 1.0F, float2 / 24.0F), clamp(0.0F, 1.0F, float1 / 24.0F), clamp(0.0F, 1.0F, float0 / 24.0F), boolean0);
    }

    protected float getTimeLerp(float float0, float float1, float float2) {
        return this.getTimeLerp(float0, float1, float2, false);
    }

    protected float getTimeLerp(float float2, float float0, float float1, boolean boolean1) {
        boolean boolean0 = float0 > float1;
        if (!boolean0) {
            if (!(float2 < float0) && !(float2 > float1)) {
                float float3 = float2 - float0;
                float float4 = float1 - float0;
                float float5 = float4 * 0.5F;
                if (float3 < float5) {
                    return boolean1 ? clerp(float3 / float5, 0.0F, 1.0F) : lerp(float3 / float5, 0.0F, 1.0F);
                } else {
                    return boolean1 ? clerp((float3 - float5) / float5, 1.0F, 0.0F) : lerp((float3 - float5) / float5, 1.0F, 0.0F);
                }
            } else {
                return 0.0F;
            }
        } else if (float2 < float0 && float2 > float1) {
            return 0.0F;
        } else {
            float float6 = 1.0F - float0;
            float float7 = float2 >= float0 ? float2 - float0 : float2 + float6;
            float float8 = float1 + float6;
            float float9 = float8 * 0.5F;
            if (float7 < float9) {
                return boolean1 ? clerp(float7 / float9, 0.0F, 1.0F) : lerp(float7 / float9, 0.0F, 1.0F);
            } else {
                return boolean1 ? clerp((float7 - float9) / float9, 1.0F, 0.0F) : lerp((float7 - float9) / float9, 1.0F, 0.0F);
            }
        }
    }

    public static float clamp01(float val) {
        return clamp(0.0F, 1.0F, val);
    }

    public static float clamp(float min, float max, float val) {
        val = Math.min(max, val);
        return Math.max(min, val);
    }

    public static int clamp(int min, int max, int val) {
        val = Math.min(max, val);
        return Math.max(min, val);
    }

    public static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    public static float clerp(float t, float a, float b) {
        float float0 = (float)(1.0 - Math.cos(t * Math.PI)) / 2.0F;
        return a * (1.0F - float0) + b * float0;
    }

    public static float normalizeRange(float v, float n) {
        return clamp(0.0F, 1.0F, v / n);
    }

    public static float posToPosNegRange(float v) {
        if (v > 0.5F) {
            return (v - 0.5F) * 2.0F;
        } else {
            return v < 0.5F ? -((0.5F - v) * 2.0F) : 0.0F;
        }
    }

    public void execute_Simulation() {
        if (Core.bDebug) {
            ClimMngrDebug climMngrDebug = new ClimMngrDebug();
            short short0 = 365;
            short short1 = 5000;
            climMngrDebug.SimulateDays(short0, short1);
        }
    }

    public void execute_Simulation(int RainModOverride) {
        if (Core.bDebug) {
            ClimMngrDebug climMngrDebug = new ClimMngrDebug();
            climMngrDebug.setRainModOverride(RainModOverride);
            short short0 = 365;
            short short1 = 5000;
            climMngrDebug.SimulateDays(short0, short1);
        }
    }

    public void triggerKateBobIntroStorm(int centerX, int centerY, double duration, float strength, float initialProgress, float angle, float initialPuddles) {
        this.triggerKateBobIntroStorm(centerX, centerY, duration, strength, initialProgress, angle, initialPuddles, null);
    }

    public void triggerKateBobIntroStorm(
        int centerX, int centerY, double duration, float strength, float initialProgress, float angle, float initialPuddles, ClimateColorInfo cloudcolor
    ) {
        if (!GameClient.bClient) {
            this.stopWeatherAndThunder();
            if (this.weatherPeriod.startCreateModdedPeriod(true, strength, angle)) {
                this.weatherPeriod.setKateBobStormProgress(initialProgress);
                this.weatherPeriod.setKateBobStormCoords(centerX, centerY);
                this.weatherPeriod.createAndAddStage(11, duration);
                this.weatherPeriod.createAndAddStage(2, duration / 2.0);
                this.weatherPeriod.createAndAddStage(4, duration / 4.0);
                this.weatherPeriod.endCreateModdedPeriod();
                if (cloudcolor != null) {
                    this.weatherPeriod.setCloudColor(cloudcolor);
                } else {
                    this.weatherPeriod.setCloudColor(this.weatherPeriod.getCloudColorBlueish());
                }

                IsoPuddles.PuddlesFloat puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(3);
                puddlesFloat.setFinalValue(initialPuddles);
                puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(1);
                puddlesFloat.setFinalValue(PZMath.clamp_01(initialPuddles * 1.2F));
            }
        }
    }

    public double getSimplexOffsetA() {
        return this.simplexOffsetA;
    }

    public double getSimplexOffsetB() {
        return this.simplexOffsetB;
    }

    public double getSimplexOffsetC() {
        return this.simplexOffsetC;
    }

    public double getSimplexOffsetD() {
        return this.simplexOffsetD;
    }

    public double getWorldAgeHours() {
        return this.worldAgeHours;
    }

    public ClimateValues getClimateValuesCopy() {
        return this.climateValues.getCopy();
    }

    public void CopyClimateValues(ClimateValues copy) {
        this.climateValues.CopyValues(copy);
    }

    public ClimateForecaster getClimateForecaster() {
        return this.climateForecaster;
    }

    public ClimateHistory getClimateHistory() {
        return this.climateHistory;
    }

    public void CalculateWeatherFrontStrength(int year, int month, int day, ClimateManager.AirFront front) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day, 0, 0);
        gregorianCalendar.add(5, -3);
        if (this.climateValuesFronts == null) {
            this.climateValuesFronts = this.climateValues.getCopy();
        }

        int int0 = front.type;

        for (int int1 = 0; int1 < 4; int1++) {
            this.climateValuesFronts.pollDate(gregorianCalendar);
            float float0 = this.climateValuesFronts.getAirFrontAirmass();
            int int2 = float0 < 0.0F ? -1 : 1;
            if (int2 == int0) {
                front.addDaySample(float0);
            }

            gregorianCalendar.add(5, 1);
        }

        DebugLog.log("Calculate weather front strength = " + front.getStrength());
    }

    public static String getWindAngleString(float angle) {
        for (int int0 = 0; int0 < windAngles.length; int0++) {
            if (angle < windAngles[int0]) {
                return windAngleStr[int0];
            }
        }

        return windAngleStr[windAngleStr.length - 1];
    }

    public void sendInitialState(UdpConnection connection) throws IOException {
        if (GameServer.bServer) {
            if (this.writePacketContents(connection, (byte)0)) {
                PacketTypes.PacketType.ClimateManagerPacket.send(connection);
            } else {
                connection.cancelPacket();
            }
        }
    }

    public boolean isUpdated() {
        return this.lastMinuteStamp != -1L;
    }

    public static class AirFront {
        private float days = 0.0F;
        private float maxNoise = 0.0F;
        private float totalNoise = 0.0F;
        private int type = 0;
        private float strength = 0.0F;
        private float tmpNoiseAbs = 0.0F;
        private float[] noiseCache = new float[2];
        private float noiseCacheValue = 0.0F;
        private float frontWindAngleDegrees = 0.0F;

        public float getDays() {
            return this.days;
        }

        public float getMaxNoise() {
            return this.maxNoise;
        }

        public float getTotalNoise() {
            return this.totalNoise;
        }

        public int getType() {
            return this.type;
        }

        public float getStrength() {
            return this.strength;
        }

        public float getAngleDegrees() {
            return this.frontWindAngleDegrees;
        }

        public AirFront() {
            this.reset();
        }

        public void setFrontType(int _type) {
            this.reset();
            this.type = _type;
        }

        protected void setFrontWind(float float0) {
            this.frontWindAngleDegrees = float0;
        }

        public void setStrength(float str) {
            this.strength = str;
        }

        protected void reset() {
            this.days = 0.0F;
            this.maxNoise = 0.0F;
            this.totalNoise = 0.0F;
            this.type = 0;
            this.strength = 0.0F;
            this.frontWindAngleDegrees = 0.0F;

            for (int int0 = 0; int0 < this.noiseCache.length; int0++) {
                this.noiseCache[int0] = -1.0F;
            }
        }

        public void save(DataOutputStream output) throws IOException {
            output.writeFloat(this.days);
            output.writeFloat(this.maxNoise);
            output.writeFloat(this.totalNoise);
            output.writeInt(this.type);
            output.writeFloat(this.strength);
            output.writeFloat(this.frontWindAngleDegrees);
            output.writeInt(this.noiseCache.length);

            for (int int0 = 0; int0 < this.noiseCache.length; int0++) {
                output.writeFloat(this.noiseCache[int0]);
            }
        }

        public void load(DataInputStream input) throws IOException {
            this.days = input.readFloat();
            this.maxNoise = input.readFloat();
            this.totalNoise = input.readFloat();
            this.type = input.readInt();
            this.strength = input.readFloat();
            this.frontWindAngleDegrees = input.readFloat();
            int int0 = input.readInt();
            int int1 = int0 > this.noiseCache.length ? int0 : this.noiseCache.length;

            for (int int2 = 0; int2 < int1; int2++) {
                if (int2 < int0) {
                    float float0 = input.readFloat();
                    if (int2 < this.noiseCache.length) {
                        this.noiseCache[int2] = float0;
                    }
                } else if (int2 < this.noiseCache.length) {
                    this.noiseCache[int2] = -1.0F;
                }
            }
        }

        public void addDaySample(float noiseval) {
            this.days++;
            if ((this.type != 1 || !(noiseval <= 0.0F)) && (this.type != -1 || !(noiseval >= 0.0F))) {
                this.tmpNoiseAbs = Math.abs(noiseval);
                if (this.tmpNoiseAbs > this.maxNoise) {
                    this.maxNoise = this.tmpNoiseAbs;
                }

                this.totalNoise = this.totalNoise + this.tmpNoiseAbs;
                this.noiseCacheValue = 0.0F;

                for (int int0 = this.noiseCache.length - 1; int0 >= 0; int0--) {
                    if (this.noiseCache[int0] > this.noiseCacheValue) {
                        this.noiseCacheValue = this.noiseCache[int0];
                    }

                    if (int0 < this.noiseCache.length - 1) {
                        this.noiseCache[int0 + 1] = this.noiseCache[int0];
                    }
                }

                this.noiseCache[0] = this.tmpNoiseAbs;
                if (this.tmpNoiseAbs > this.noiseCacheValue) {
                    this.noiseCacheValue = this.tmpNoiseAbs;
                }

                this.strength = this.noiseCacheValue * 0.75F + this.maxNoise * 0.25F;
            } else {
                this.strength = 0.0F;
            }
        }

        public void copyFrom(ClimateManager.AirFront other) {
            this.days = other.days;
            this.maxNoise = other.maxNoise;
            this.totalNoise = other.totalNoise;
            this.type = other.type;
            this.strength = other.strength;
            this.frontWindAngleDegrees = other.frontWindAngleDegrees;
        }
    }

    public static class ClimateBool {
        protected boolean internalValue;
        protected boolean finalValue;
        protected boolean isOverride;
        protected boolean override;
        private boolean isModded = false;
        private boolean moddedValue;
        private boolean isAdminOverride = false;
        private boolean adminValue;
        private int ID;
        private String name;

        public ClimateManager.ClimateBool init(int id, String _name) {
            this.ID = id;
            this.name = _name;
            return this;
        }

        public int getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public boolean getInternalValue() {
            return this.internalValue;
        }

        public boolean getOverride() {
            return this.override;
        }

        public void setOverride(boolean b) {
            this.isOverride = true;
            this.override = b;
        }

        public void setEnableOverride(boolean b) {
            this.isOverride = b;
        }

        public boolean isEnableOverride() {
            return this.isOverride;
        }

        public void setEnableAdmin(boolean b) {
            this.isAdminOverride = b;
        }

        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }

        public void setAdminValue(boolean b) {
            this.adminValue = b;
        }

        public boolean getAdminValue() {
            return this.adminValue;
        }

        public void setEnableModded(boolean b) {
            this.isModded = b;
        }

        public void setModdedValue(boolean b) {
            this.moddedValue = b;
        }

        public boolean getModdedValue() {
            return this.moddedValue;
        }

        public void setFinalValue(boolean b) {
            this.finalValue = b;
        }

        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue = this.adminValue;
            } else if (this.isModded) {
                this.finalValue = this.moddedValue;
            } else {
                this.finalValue = this.isOverride ? this.override : this.internalValue;
            }
        }

        private void writeAdmin(ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            byteBuffer.put((byte)(this.adminValue ? 1 : 0));
        }

        private void readAdmin(ByteBuffer byteBuffer) {
            this.isAdminOverride = byteBuffer.get() == 1;
            this.adminValue = byteBuffer.get() == 1;
        }

        private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            dataOutputStream.writeBoolean(this.adminValue);
        }

        private void loadAdmin(DataInputStream dataInputStream, int var2) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            this.adminValue = dataInputStream.readBoolean();
        }
    }

    public static class ClimateColor {
        protected ClimateColorInfo internalValue = new ClimateColorInfo();
        protected ClimateColorInfo finalValue = new ClimateColorInfo();
        protected boolean isOverride = false;
        protected ClimateColorInfo override = new ClimateColorInfo();
        protected float interpolate;
        private boolean isModded = false;
        private ClimateColorInfo moddedValue = new ClimateColorInfo();
        private float modInterpolate;
        private boolean isAdminOverride = false;
        private ClimateColorInfo adminValue = new ClimateColorInfo();
        private int ID;
        private String name;

        public ClimateManager.ClimateColor init(int id, String _name) {
            this.ID = id;
            this.name = _name;
            return this;
        }

        public int getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public ClimateColorInfo getInternalValue() {
            return this.internalValue;
        }

        public ClimateColorInfo getOverride() {
            return this.override;
        }

        public float getOverrideInterpolate() {
            return this.interpolate;
        }

        public void setOverride(ClimateColorInfo targ, float inter) {
            this.override.setTo(targ);
            this.interpolate = inter;
            this.isOverride = true;
        }

        public void setOverride(ByteBuffer input, float interp) {
            this.override.read(input);
            this.interpolate = interp;
            this.isOverride = true;
        }

        public void setEnableOverride(boolean b) {
            this.isOverride = b;
        }

        public boolean isEnableOverride() {
            return this.isOverride;
        }

        public void setEnableAdmin(boolean b) {
            this.isAdminOverride = b;
        }

        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }

        public void setAdminValue(float r, float g, float b, float a, float r1, float g1, float b1, float a1) {
            this.adminValue.getExterior().r = r;
            this.adminValue.getExterior().g = g;
            this.adminValue.getExterior().b = b;
            this.adminValue.getExterior().a = a;
            this.adminValue.getInterior().r = r1;
            this.adminValue.getInterior().g = g1;
            this.adminValue.getInterior().b = b1;
            this.adminValue.getInterior().a = a1;
        }

        public void setAdminValueExterior(float r, float g, float b, float a) {
            this.adminValue.getExterior().r = r;
            this.adminValue.getExterior().g = g;
            this.adminValue.getExterior().b = b;
            this.adminValue.getExterior().a = a;
        }

        public void setAdminValueInterior(float r, float g, float b, float a) {
            this.adminValue.getInterior().r = r;
            this.adminValue.getInterior().g = g;
            this.adminValue.getInterior().b = b;
            this.adminValue.getInterior().a = a;
        }

        public void setAdminValue(ClimateColorInfo targ) {
            this.adminValue.setTo(targ);
        }

        public ClimateColorInfo getAdminValue() {
            return this.adminValue;
        }

        public void setEnableModded(boolean b) {
            this.isModded = b;
        }

        public void setModdedValue(ClimateColorInfo targ) {
            this.moddedValue.setTo(targ);
        }

        public ClimateColorInfo getModdedValue() {
            return this.moddedValue;
        }

        public void setModdedInterpolate(float f) {
            this.modInterpolate = ClimateManager.clamp01(f);
        }

        public void setFinalValue(ClimateColorInfo targ) {
            this.finalValue.setTo(targ);
        }

        public ClimateColorInfo getFinalValue() {
            return this.finalValue;
        }

        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue.setTo(this.adminValue);
            } else {
                if (this.isModded && this.modInterpolate > 0.0F) {
                    this.internalValue.interp(this.moddedValue, this.modInterpolate, this.internalValue);
                }

                if (this.isOverride && this.interpolate > 0.0F) {
                    this.internalValue.interp(this.override, this.interpolate, this.finalValue);
                } else {
                    this.finalValue.setTo(this.internalValue);
                }
            }
        }

        private void writeAdmin(ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            this.adminValue.write(byteBuffer);
        }

        private void readAdmin(ByteBuffer byteBuffer) {
            this.isAdminOverride = byteBuffer.get() == 1;
            this.adminValue.read(byteBuffer);
        }

        private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            this.adminValue.save(dataOutputStream);
        }

        private void loadAdmin(DataInputStream dataInputStream, int int0) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            if (int0 < 143) {
                this.adminValue.getInterior().r = dataInputStream.readFloat();
                this.adminValue.getInterior().g = dataInputStream.readFloat();
                this.adminValue.getInterior().b = dataInputStream.readFloat();
                this.adminValue.getInterior().a = dataInputStream.readFloat();
                this.adminValue.getExterior().r = this.adminValue.getInterior().r;
                this.adminValue.getExterior().g = this.adminValue.getInterior().g;
                this.adminValue.getExterior().b = this.adminValue.getInterior().b;
                this.adminValue.getExterior().a = this.adminValue.getInterior().a;
            } else {
                this.adminValue.load(dataInputStream, int0);
            }
        }
    }

    public static class ClimateFloat {
        protected float internalValue;
        protected float finalValue;
        protected boolean isOverride = false;
        protected float override;
        protected float interpolate;
        private boolean isModded = false;
        private float moddedValue;
        private float modInterpolate;
        private boolean isAdminOverride = false;
        private float adminValue;
        private float min = 0.0F;
        private float max = 1.0F;
        private int ID;
        private String name;

        public ClimateManager.ClimateFloat init(int id, String _name) {
            this.ID = id;
            this.name = _name;
            return this;
        }

        public int getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public float getMin() {
            return this.min;
        }

        public float getMax() {
            return this.max;
        }

        public float getInternalValue() {
            return this.internalValue;
        }

        public float getOverride() {
            return this.override;
        }

        public float getOverrideInterpolate() {
            return this.interpolate;
        }

        public void setOverride(float targ, float inter) {
            this.override = targ;
            this.interpolate = inter;
            this.isOverride = true;
        }

        public void setEnableOverride(boolean b) {
            this.isOverride = b;
        }

        public boolean isEnableOverride() {
            return this.isOverride;
        }

        public void setEnableAdmin(boolean b) {
            this.isAdminOverride = b;
        }

        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }

        public void setAdminValue(float f) {
            this.adminValue = ClimateManager.clamp(this.min, this.max, f);
        }

        public float getAdminValue() {
            return this.adminValue;
        }

        public void setEnableModded(boolean b) {
            this.isModded = b;
        }

        public void setModdedValue(float f) {
            this.moddedValue = ClimateManager.clamp(this.min, this.max, f);
        }

        public float getModdedValue() {
            return this.moddedValue;
        }

        public void setModdedInterpolate(float f) {
            this.modInterpolate = ClimateManager.clamp01(f);
        }

        public void setFinalValue(float f) {
            this.finalValue = f;
        }

        public float getFinalValue() {
            return this.finalValue;
        }

        private void calculate() {
            if (this.isAdminOverride && !GameClient.bClient) {
                this.finalValue = this.adminValue;
            } else {
                if (this.isModded && this.modInterpolate > 0.0F) {
                    this.internalValue = ClimateManager.lerp(this.modInterpolate, this.internalValue, this.moddedValue);
                }

                if (this.isOverride && this.interpolate > 0.0F) {
                    this.finalValue = ClimateManager.lerp(this.interpolate, this.internalValue, this.override);
                } else {
                    this.finalValue = this.internalValue;
                }
            }
        }

        private void writeAdmin(ByteBuffer byteBuffer) {
            byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
            byteBuffer.putFloat(this.adminValue);
        }

        private void readAdmin(ByteBuffer byteBuffer) {
            this.isAdminOverride = byteBuffer.get() == 1;
            this.adminValue = byteBuffer.getFloat();
        }

        private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(this.isAdminOverride);
            dataOutputStream.writeFloat(this.adminValue);
        }

        private void loadAdmin(DataInputStream dataInputStream, int var2) throws IOException {
            this.isAdminOverride = dataInputStream.readBoolean();
            this.adminValue = dataInputStream.readFloat();
        }
    }

    public static enum ClimateNetAuth {
        Denied,
        ClientOnly,
        ServerOnly,
        ClientAndServer;
    }

    private static class ClimateNetInfo {
        public boolean IsStopWeather = false;
        public boolean IsTrigger = false;
        public boolean IsGenerate = false;
        public float TriggerDuration = 0.0F;
        public boolean TriggerStorm = false;
        public boolean TriggerTropical = false;
        public boolean TriggerBlizzard = false;
        public float GenerateStrength = 0.0F;
        public int GenerateFront = 0;

        private void reset() {
            this.IsStopWeather = false;
            this.IsTrigger = false;
            this.IsGenerate = false;
            this.TriggerDuration = 0.0F;
            this.TriggerStorm = false;
            this.TriggerTropical = false;
            this.TriggerBlizzard = false;
            this.GenerateStrength = 0.0F;
            this.GenerateFront = 0;
        }
    }

    /**
     * DAY INFO
     */
    public static class DayInfo {
        public int day;
        public int month;
        public int year;
        public int hour;
        public int minutes;
        public long dateValue;
        public GregorianCalendar calendar;
        public ErosionSeason season;

        public void set(int _day, int _month, int _year) {
            this.calendar = new GregorianCalendar(_year, _month, _day, 0, 0);
            this.dateValue = this.calendar.getTime().getTime();
            this.day = _day;
            this.month = _month;
            this.year = _year;
        }

        public int getDay() {
            return this.day;
        }

        public int getMonth() {
            return this.month;
        }

        public int getYear() {
            return this.year;
        }

        public int getHour() {
            return this.hour;
        }

        public int getMinutes() {
            return this.minutes;
        }

        public long getDateValue() {
            return this.dateValue;
        }

        public ErosionSeason getSeason() {
            return this.season;
        }
    }

    protected static class SeasonColor {
        public static int WARM = 0;
        public static int NORMAL = 1;
        public static int CLOUDY = 2;
        public static int SUMMER = 0;
        public static int FALL = 1;
        public static int WINTER = 2;
        public static int SPRING = 3;
        private ClimateColorInfo finalCol = new ClimateColorInfo();
        private ClimateColorInfo[] tempCol = new ClimateColorInfo[3];
        private ClimateColorInfo[][] colors = new ClimateColorInfo[3][4];
        private boolean ignoreNormal = true;

        public SeasonColor() {
            for (int int0 = 0; int0 < 3; int0++) {
                for (int int1 = 0; int1 < 4; int1++) {
                    this.colors[int0][int1] = new ClimateColorInfo();
                }

                this.tempCol[int0] = new ClimateColorInfo();
            }
        }

        public void setIgnoreNormal(boolean arg0) {
            this.ignoreNormal = arg0;
        }

        public ClimateColorInfo getColor(int arg0, int arg1) {
            return this.colors[arg0][arg1];
        }

        public void setColorInterior(int arg0, int arg1, float arg2, float arg3, float arg4, float arg5) {
            this.colors[arg0][arg1].getInterior().r = arg2;
            this.colors[arg0][arg1].getInterior().g = arg3;
            this.colors[arg0][arg1].getInterior().b = arg4;
            this.colors[arg0][arg1].getInterior().a = arg5;
        }

        public void setColorExterior(int arg0, int arg1, float arg2, float arg3, float arg4, float arg5) {
            this.colors[arg0][arg1].getExterior().r = arg2;
            this.colors[arg0][arg1].getExterior().g = arg3;
            this.colors[arg0][arg1].getExterior().b = arg4;
            this.colors[arg0][arg1].getExterior().a = arg5;
        }

        public ClimateColorInfo update(float arg0, float arg1, int arg2, int arg3) {
            for (int int0 = 0; int0 < 3; int0++) {
                if (!this.ignoreNormal || int0 != 1) {
                    this.colors[int0][arg2].interp(this.colors[int0][arg3], arg1, this.tempCol[int0]);
                }
            }

            if (!this.ignoreNormal) {
                if (arg0 < 0.5F) {
                    float float0 = arg0 * 2.0F;
                    this.tempCol[WARM].interp(this.tempCol[NORMAL], float0, this.finalCol);
                } else {
                    float float1 = 1.0F - (arg0 - 0.5F) * 2.0F;
                    this.tempCol[CLOUDY].interp(this.tempCol[NORMAL], float1, this.finalCol);
                }
            } else {
                this.tempCol[WARM].interp(this.tempCol[CLOUDY], arg0, this.finalCol);
            }

            return this.finalCol;
        }
    }
}
