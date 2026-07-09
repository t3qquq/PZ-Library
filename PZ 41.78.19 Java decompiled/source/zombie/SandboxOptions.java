// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerSettingsManager;
import zombie.sandbox.CustomBooleanSandboxOption;
import zombie.sandbox.CustomDoubleSandboxOption;
import zombie.sandbox.CustomEnumSandboxOption;
import zombie.sandbox.CustomIntegerSandboxOption;
import zombie.sandbox.CustomSandboxOption;
import zombie.sandbox.CustomSandboxOptions;
import zombie.sandbox.CustomStringSandboxOption;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class SandboxOptions {
    public static final SandboxOptions instance = new SandboxOptions();
    public int Speed = 3;
    public final SandboxOptions.EnumSandboxOption Zombies;
    public final SandboxOptions.EnumSandboxOption Distribution;
    public final SandboxOptions.EnumSandboxOption DayLength;
    public final SandboxOptions.EnumSandboxOption StartYear;
    public final SandboxOptions.EnumSandboxOption StartMonth;
    public final SandboxOptions.EnumSandboxOption StartDay;
    public final SandboxOptions.EnumSandboxOption StartTime;
    public final SandboxOptions.EnumSandboxOption WaterShut;
    public final SandboxOptions.EnumSandboxOption ElecShut;
    public final SandboxOptions.IntegerSandboxOption WaterShutModifier;
    public final SandboxOptions.IntegerSandboxOption ElecShutModifier;
    public final SandboxOptions.EnumSandboxOption FoodLoot;
    public final SandboxOptions.EnumSandboxOption LiteratureLoot;
    public final SandboxOptions.EnumSandboxOption MedicalLoot;
    public final SandboxOptions.EnumSandboxOption SurvivalGearsLoot;
    public final SandboxOptions.EnumSandboxOption CannedFoodLoot;
    public final SandboxOptions.EnumSandboxOption WeaponLoot;
    public final SandboxOptions.EnumSandboxOption RangedWeaponLoot;
    public final SandboxOptions.EnumSandboxOption AmmoLoot;
    public final SandboxOptions.EnumSandboxOption MechanicsLoot;
    public final SandboxOptions.EnumSandboxOption OtherLoot;
    public final SandboxOptions.EnumSandboxOption Temperature;
    public final SandboxOptions.EnumSandboxOption Rain;
    public final SandboxOptions.EnumSandboxOption ErosionSpeed;
    public final SandboxOptions.IntegerSandboxOption ErosionDays;
    public final SandboxOptions.DoubleSandboxOption XpMultiplier;
    public final SandboxOptions.BooleanSandboxOption XpMultiplierAffectsPassive;
    public final SandboxOptions.EnumSandboxOption Farming;
    public final SandboxOptions.EnumSandboxOption CompostTime;
    public final SandboxOptions.EnumSandboxOption StatsDecrease;
    public final SandboxOptions.EnumSandboxOption NatureAbundance;
    public final SandboxOptions.EnumSandboxOption Alarm;
    public final SandboxOptions.EnumSandboxOption LockedHouses;
    public final SandboxOptions.BooleanSandboxOption StarterKit;
    public final SandboxOptions.BooleanSandboxOption Nutrition;
    public final SandboxOptions.EnumSandboxOption FoodRotSpeed;
    public final SandboxOptions.EnumSandboxOption FridgeFactor;
    public final SandboxOptions.EnumSandboxOption LootRespawn;
    public final SandboxOptions.IntegerSandboxOption SeenHoursPreventLootRespawn;
    public final SandboxOptions.StringSandboxOption WorldItemRemovalList;
    public final SandboxOptions.DoubleSandboxOption HoursForWorldItemRemoval;
    public final SandboxOptions.BooleanSandboxOption ItemRemovalListBlacklistToggle;
    public final SandboxOptions.EnumSandboxOption TimeSinceApo;
    public final SandboxOptions.EnumSandboxOption PlantResilience;
    public final SandboxOptions.EnumSandboxOption PlantAbundance;
    public final SandboxOptions.EnumSandboxOption EndRegen;
    public final SandboxOptions.EnumSandboxOption Helicopter;
    public final SandboxOptions.EnumSandboxOption MetaEvent;
    public final SandboxOptions.EnumSandboxOption SleepingEvent;
    public final SandboxOptions.DoubleSandboxOption GeneratorFuelConsumption;
    public final SandboxOptions.EnumSandboxOption GeneratorSpawning;
    public final SandboxOptions.EnumSandboxOption SurvivorHouseChance;
    public final SandboxOptions.EnumSandboxOption AnnotatedMapChance;
    public final SandboxOptions.IntegerSandboxOption CharacterFreePoints;
    public final SandboxOptions.EnumSandboxOption ConstructionBonusPoints;
    public final SandboxOptions.EnumSandboxOption NightDarkness;
    public final SandboxOptions.EnumSandboxOption NightLength;
    public final SandboxOptions.BooleanSandboxOption BoneFracture;
    public final SandboxOptions.EnumSandboxOption InjurySeverity;
    public final SandboxOptions.DoubleSandboxOption HoursForCorpseRemoval;
    public final SandboxOptions.EnumSandboxOption DecayingCorpseHealthImpact;
    public final SandboxOptions.EnumSandboxOption BloodLevel;
    public final SandboxOptions.EnumSandboxOption ClothingDegradation;
    public final SandboxOptions.BooleanSandboxOption FireSpread;
    public final SandboxOptions.IntegerSandboxOption DaysForRottenFoodRemoval;
    public final SandboxOptions.BooleanSandboxOption AllowExteriorGenerator;
    public final SandboxOptions.EnumSandboxOption MaxFogIntensity;
    public final SandboxOptions.EnumSandboxOption MaxRainFxIntensity;
    public final SandboxOptions.BooleanSandboxOption EnableSnowOnGround;
    public final SandboxOptions.BooleanSandboxOption AttackBlockMovements;
    public final SandboxOptions.EnumSandboxOption VehicleStoryChance;
    public final SandboxOptions.EnumSandboxOption ZoneStoryChance;
    public final SandboxOptions.BooleanSandboxOption AllClothesUnlocked;
    public final SandboxOptions.BooleanSandboxOption EnableTaintedWaterText;
    public final SandboxOptions.BooleanSandboxOption EnableVehicles;
    public final SandboxOptions.EnumSandboxOption CarSpawnRate;
    public final SandboxOptions.DoubleSandboxOption ZombieAttractionMultiplier;
    public final SandboxOptions.BooleanSandboxOption VehicleEasyUse;
    public final SandboxOptions.EnumSandboxOption InitialGas;
    public final SandboxOptions.EnumSandboxOption FuelStationGas;
    public final SandboxOptions.EnumSandboxOption LockedCar;
    public final SandboxOptions.DoubleSandboxOption CarGasConsumption;
    public final SandboxOptions.EnumSandboxOption CarGeneralCondition;
    public final SandboxOptions.EnumSandboxOption CarDamageOnImpact;
    public final SandboxOptions.EnumSandboxOption DamageToPlayerFromHitByACar;
    public final SandboxOptions.BooleanSandboxOption TrafficJam;
    public final SandboxOptions.EnumSandboxOption CarAlarm;
    public final SandboxOptions.BooleanSandboxOption PlayerDamageFromCrash;
    public final SandboxOptions.DoubleSandboxOption SirenShutoffHours;
    public final SandboxOptions.EnumSandboxOption ChanceHasGas;
    public final SandboxOptions.EnumSandboxOption RecentlySurvivorVehicles;
    public final SandboxOptions.BooleanSandboxOption MultiHitZombies;
    public final SandboxOptions.EnumSandboxOption RearVulnerability;
    public final SandboxOptions.EnumSandboxOption EnablePoisoning;
    public final SandboxOptions.EnumSandboxOption MaggotSpawn;
    public final SandboxOptions.DoubleSandboxOption LightBulbLifespan;
    protected final ArrayList<SandboxOptions.SandboxOption> options = new ArrayList<>();
    protected final HashMap<String, SandboxOptions.SandboxOption> optionByName = new HashMap<>();
    public final SandboxOptions.Map Map = new SandboxOptions.Map();
    public final SandboxOptions.ZombieLore Lore = new SandboxOptions.ZombieLore();
    public final SandboxOptions.ZombieConfig zombieConfig = new SandboxOptions.ZombieConfig();
    public final int FIRST_YEAR = 1993;
    private final int SANDBOX_VERSION = 5;
    private final ArrayList<SandboxOptions.SandboxOption> m_customOptions = new ArrayList<>();

    public SandboxOptions() {
        this.Zombies = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Zombies", 6, 4).setTranslation("ZombieCount");
        this.Distribution = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Distribution", 2, 1).setTranslation("ZombieDistribution");
        this.DayLength = this.newEnumOption("DayLength", 26, 2);
        this.StartYear = this.newEnumOption("StartYear", 100, 1);
        this.StartMonth = this.newEnumOption("StartMonth", 12, 7);
        this.StartDay = this.newEnumOption("StartDay", 31, 23);
        this.StartTime = this.newEnumOption("StartTime", 9, 2);
        this.WaterShut = this.newEnumOption("WaterShut", 8, 2).setValueTranslation("Shutoff");
        this.ElecShut = this.newEnumOption("ElecShut", 8, 2).setValueTranslation("Shutoff");
        this.WaterShutModifier = (SandboxOptions.IntegerSandboxOption)this.newIntegerOption("WaterShutModifier", -1, Integer.MAX_VALUE, 14)
            .setTranslation("WaterShut");
        this.ElecShutModifier = (SandboxOptions.IntegerSandboxOption)this.newIntegerOption("ElecShutModifier", -1, Integer.MAX_VALUE, 14)
            .setTranslation("ElecShut");
        this.FoodLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FoodLoot", 7, 4).setValueTranslation("Rarity").setTranslation("LootFood");
        this.CannedFoodLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("CannedFoodLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootCannedFood");
        this.LiteratureLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("LiteratureLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootLiterature");
        this.SurvivalGearsLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("SurvivalGearsLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootSurvivalGears");
        this.MedicalLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("MedicalLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootMedical");
        this.WeaponLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("WeaponLoot", 7, 4).setValueTranslation("Rarity").setTranslation("LootWeapon");
        this.RangedWeaponLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("RangedWeaponLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootRangedWeapon");
        this.AmmoLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("AmmoLoot", 7, 4).setValueTranslation("Rarity").setTranslation("LootAmmo");
        this.MechanicsLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("MechanicsLoot", 7, 4)
            .setValueTranslation("Rarity")
            .setTranslation("LootMechanics");
        this.OtherLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("OtherLoot", 7, 4).setValueTranslation("Rarity").setTranslation("LootOther");
        this.Temperature = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Temperature", 5, 3).setTranslation("WorldTemperature");
        this.Rain = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Rain", 5, 3).setTranslation("RainAmount");
        this.ErosionSpeed = this.newEnumOption("ErosionSpeed", 5, 3);
        this.ErosionDays = this.newIntegerOption("ErosionDays", -1, 36500, 0);
        this.XpMultiplier = this.newDoubleOption("XpMultiplier", 0.001, 1000.0, 1.0);
        this.XpMultiplierAffectsPassive = this.newBooleanOption("XpMultiplierAffectsPassive", false);
        this.ZombieAttractionMultiplier = this.newDoubleOption("ZombieAttractionMultiplier", 0.0, 100.0, 1.0);
        this.VehicleEasyUse = this.newBooleanOption("VehicleEasyUse", false);
        this.Farming = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Farming", 5, 3).setTranslation("FarmingSpeed");
        this.CompostTime = this.newEnumOption("CompostTime", 8, 2);
        this.StatsDecrease = (SandboxOptions.EnumSandboxOption)this.newEnumOption("StatsDecrease", 5, 3).setTranslation("StatDecrease");
        this.NatureAbundance = (SandboxOptions.EnumSandboxOption)this.newEnumOption("NatureAbundance", 5, 3).setTranslation("NatureAmount");
        this.Alarm = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Alarm", 6, 4).setTranslation("HouseAlarmFrequency");
        this.LockedHouses = (SandboxOptions.EnumSandboxOption)this.newEnumOption("LockedHouses", 6, 4).setTranslation("LockedHouseFrequency");
        this.StarterKit = this.newBooleanOption("StarterKit", false);
        this.Nutrition = this.newBooleanOption("Nutrition", false);
        this.FoodRotSpeed = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FoodRotSpeed", 5, 3).setTranslation("FoodSpoil");
        this.FridgeFactor = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FridgeFactor", 5, 3).setTranslation("FridgeEffect");
        this.LootRespawn = this.newEnumOption("LootRespawn", 5, 1).setValueTranslation("Respawn");
        this.SeenHoursPreventLootRespawn = this.newIntegerOption("SeenHoursPreventLootRespawn", 0, Integer.MAX_VALUE, 0);
        this.WorldItemRemovalList = this.newStringOption("WorldItemRemovalList", "Base.Hat,Base.Glasses", -1);
        this.HoursForWorldItemRemoval = this.newDoubleOption("HoursForWorldItemRemoval", 0.0, 2.147483647E9, 24.0);
        this.ItemRemovalListBlacklistToggle = this.newBooleanOption("ItemRemovalListBlacklistToggle", false);
        this.TimeSinceApo = this.newEnumOption("TimeSinceApo", 13, 1);
        this.PlantResilience = this.newEnumOption("PlantResilience", 5, 3);
        this.PlantAbundance = this.newEnumOption("PlantAbundance", 5, 3).setValueTranslation("NatureAmount");
        this.EndRegen = (SandboxOptions.EnumSandboxOption)this.newEnumOption("EndRegen", 5, 3).setTranslation("EnduranceRegen");
        this.Helicopter = this.newEnumOption("Helicopter", 4, 2).setValueTranslation("HelicopterFreq");
        this.MetaEvent = this.newEnumOption("MetaEvent", 3, 2).setValueTranslation("MetaEventFreq");
        this.SleepingEvent = this.newEnumOption("SleepingEvent", 3, 1).setValueTranslation("MetaEventFreq");
        this.GeneratorSpawning = this.newEnumOption("GeneratorSpawning", 5, 3);
        this.GeneratorFuelConsumption = this.newDoubleOption("GeneratorFuelConsumption", 0.0, 100.0, 1.0);
        this.SurvivorHouseChance = this.newEnumOption("SurvivorHouseChance", 6, 3);
        this.VehicleStoryChance = this.newEnumOption("VehicleStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
        this.ZoneStoryChance = this.newEnumOption("ZoneStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
        this.AnnotatedMapChance = this.newEnumOption("AnnotatedMapChance", 6, 4);
        this.CharacterFreePoints = this.newIntegerOption("CharacterFreePoints", -100, 100, 0);
        this.ConstructionBonusPoints = this.newEnumOption("ConstructionBonusPoints", 5, 3);
        this.NightDarkness = this.newEnumOption("NightDarkness", 4, 3);
        this.NightLength = this.newEnumOption("NightLength", 5, 3);
        this.InjurySeverity = this.newEnumOption("InjurySeverity", 3, 2);
        this.BoneFracture = this.newBooleanOption("BoneFracture", true);
        this.HoursForCorpseRemoval = this.newDoubleOption("HoursForCorpseRemoval", -1.0, 2.147483647E9, -1.0);
        this.DecayingCorpseHealthImpact = this.newEnumOption("DecayingCorpseHealthImpact", 4, 3);
        this.BloodLevel = this.newEnumOption("BloodLevel", 5, 3);
        this.ClothingDegradation = this.newEnumOption("ClothingDegradation", 4, 3);
        this.FireSpread = this.newBooleanOption("FireSpread", true);
        this.DaysForRottenFoodRemoval = this.newIntegerOption("DaysForRottenFoodRemoval", -1, Integer.MAX_VALUE, -1);
        this.AllowExteriorGenerator = this.newBooleanOption("AllowExteriorGenerator", true);
        this.MaxFogIntensity = this.newEnumOption("MaxFogIntensity", 3, 1);
        this.MaxRainFxIntensity = this.newEnumOption("MaxRainFxIntensity", 3, 1);
        this.EnableSnowOnGround = this.newBooleanOption("EnableSnowOnGround", true);
        this.MultiHitZombies = this.newBooleanOption("MultiHitZombies", false);
        this.RearVulnerability = this.newEnumOption("RearVulnerability", 3, 3);
        this.AttackBlockMovements = this.newBooleanOption("AttackBlockMovements", true);
        this.AllClothesUnlocked = this.newBooleanOption("AllClothesUnlocked", false);
        this.EnableTaintedWaterText = this.newBooleanOption("EnableTaintedWaterText", true);
        this.CarSpawnRate = this.newEnumOption("CarSpawnRate", 5, 4);
        this.ChanceHasGas = this.newEnumOption("ChanceHasGas", 3, 2);
        this.InitialGas = this.newEnumOption("InitialGas", 6, 3);
        this.FuelStationGas = this.newEnumOption("FuelStationGas", 9, 5);
        this.CarGasConsumption = this.newDoubleOption("CarGasConsumption", 0.0, 100.0, 1.0);
        this.LockedCar = this.newEnumOption("LockedCar", 6, 4);
        this.CarGeneralCondition = this.newEnumOption("CarGeneralCondition", 5, 3);
        this.CarDamageOnImpact = this.newEnumOption("CarDamageOnImpact", 5, 3);
        this.DamageToPlayerFromHitByACar = this.newEnumOption("DamageToPlayerFromHitByACar", 5, 1);
        this.TrafficJam = this.newBooleanOption("TrafficJam", true);
        this.CarAlarm = (SandboxOptions.EnumSandboxOption)this.newEnumOption("CarAlarm", 6, 4).setTranslation("CarAlarmFrequency");
        this.PlayerDamageFromCrash = this.newBooleanOption("PlayerDamageFromCrash", true);
        this.SirenShutoffHours = this.newDoubleOption("SirenShutoffHours", 0.0, 168.0, 0.0);
        this.RecentlySurvivorVehicles = this.newEnumOption("RecentlySurvivorVehicles", 4, 3);
        this.EnableVehicles = this.newBooleanOption("EnableVehicles", true);
        this.EnablePoisoning = this.newEnumOption("EnablePoisoning", 3, 1);
        this.MaggotSpawn = this.newEnumOption("MaggotSpawn", 3, 1);
        this.LightBulbLifespan = this.newDoubleOption("LightBulbLifespan", 0.0, 1000.0, 1.0);
        CustomSandboxOptions.instance.initInstance(this);
        this.loadGameFile("Apocalypse");
        this.setDefaultsToCurrentValues();
    }

    public static SandboxOptions getInstance() {
        return instance;
    }

    public void toLua() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("SandboxVars");

        for (int int0 = 0; int0 < this.options.size(); int0++) {
            this.options.get(int0).toTable(table);
        }
    }

    public void updateFromLua() {
        if (Core.GameMode.equals("LastStand")) {
            GameTime.instance.multiplierBias = 1.2F;
        }

        KahluaTable table = (KahluaTable)LuaManager.env.rawget("SandboxVars");

        for (int int0 = 0; int0 < this.options.size(); int0++) {
            this.options.get(int0).fromTable(table);
        }

        switch (this.Speed) {
            case 1:
                GameTime.instance.multiplierBias = 0.8F;
                break;
            case 2:
                GameTime.instance.multiplierBias = 0.9F;
                break;
            case 3:
                GameTime.instance.multiplierBias = 1.0F;
                break;
            case 4:
                GameTime.instance.multiplierBias = 1.1F;
                break;
            case 5:
                GameTime.instance.multiplierBias = 1.2F;
        }

        if (this.Zombies.getValue() == 1) {
            VirtualZombieManager.instance.MaxRealZombies = 400;
        }

        if (this.Zombies.getValue() == 2) {
            VirtualZombieManager.instance.MaxRealZombies = 350;
        }

        if (this.Zombies.getValue() == 3) {
            VirtualZombieManager.instance.MaxRealZombies = 300;
        }

        if (this.Zombies.getValue() == 4) {
            VirtualZombieManager.instance.MaxRealZombies = 200;
        }

        if (this.Zombies.getValue() == 5) {
            VirtualZombieManager.instance.MaxRealZombies = 100;
        }

        if (this.Zombies.getValue() == 6) {
            VirtualZombieManager.instance.MaxRealZombies = 0;
        }

        VirtualZombieManager.instance.MaxRealZombies = 1;
        this.applySettings();
    }

    public void initSandboxVars() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("SandboxVars");

        for (int int0 = 0; int0 < this.options.size(); int0++) {
            SandboxOptions.SandboxOption sandboxOption = this.options.get(int0);
            sandboxOption.fromTable(table);
            sandboxOption.toTable(table);
        }
    }

    /**
     * Random the number of day for the water shut off
     */
    public int randomWaterShut(int waterShutoffModifier) {
        switch (waterShutoffModifier) {
            case 2:
                return Rand.Next(0, 30);
            case 3:
                return Rand.Next(0, 60);
            case 4:
                return Rand.Next(0, 180);
            case 5:
                return Rand.Next(0, 360);
            case 6:
                return Rand.Next(0, 1800);
            case 7:
                return Rand.Next(60, 180);
            case 8:
                return Rand.Next(180, 360);
            default:
                return -1;
        }
    }

    /**
     * Random the number of day for the selectricity shut off
     */
    public int randomElectricityShut(int electricityShutoffModifier) {
        switch (electricityShutoffModifier) {
            case 2:
                return Rand.Next(14, 30);
            case 3:
                return Rand.Next(14, 60);
            case 4:
                return Rand.Next(14, 180);
            case 5:
                return Rand.Next(14, 360);
            case 6:
                return Rand.Next(14, 1800);
            case 7:
                return Rand.Next(60, 180);
            case 8:
                return Rand.Next(180, 360);
            default:
                return -1;
        }
    }

    public int getTemperatureModifier() {
        return this.Temperature.getValue();
    }

    public int getRainModifier() {
        return this.Rain.getValue();
    }

    public int getErosionSpeed() {
        return this.ErosionSpeed.getValue();
    }

    public int getFoodLootModifier() {
        return this.FoodLoot.getValue();
    }

    public int getWeaponLootModifier() {
        return this.WeaponLoot.getValue();
    }

    public int getOtherLootModifier() {
        return this.OtherLoot.getValue();
    }

    public int getWaterShutModifier() {
        return this.WaterShutModifier.getValue();
    }

    public int getElecShutModifier() {
        return this.ElecShutModifier.getValue();
    }

    public int getTimeSinceApo() {
        return this.TimeSinceApo.getValue();
    }

    public double getEnduranceRegenMultiplier() {
        switch (this.EndRegen.getValue()) {
            case 1:
                return 1.8;
            case 2:
                return 1.3;
            case 3:
            default:
                return 1.0;
            case 4:
                return 0.7;
            case 5:
                return 0.4;
        }
    }

    public double getStatsDecreaseMultiplier() {
        switch (this.StatsDecrease.getValue()) {
            case 1:
                return 2.0;
            case 2:
                return 1.6;
            case 3:
            default:
                return 1.0;
            case 4:
                return 0.8;
            case 5:
                return 0.65;
        }
    }

    public int getDayLengthMinutes() {
        switch (this.DayLength.getValue()) {
            case 1:
                return 15;
            case 2:
                return 30;
            default:
                return (this.DayLength.getValue() - 2) * 60;
        }
    }

    public int getDayLengthMinutesDefault() {
        switch (this.DayLength.getDefaultValue()) {
            case 1:
                return 15;
            case 2:
                return 30;
            default:
                return (this.DayLength.getDefaultValue() - 2) * 60;
        }
    }

    public int getCompostHours() {
        switch (this.CompostTime.getValue()) {
            case 1:
                return 168;
            case 2:
                return 336;
            case 3:
                return 504;
            case 4:
                return 672;
            case 5:
                return 1008;
            case 6:
                return 1344;
            case 7:
                return 1680;
            case 8:
                return 2016;
            default:
                return 336;
        }
    }

    public void applySettings() {
        GameTime.instance.setStartYear(this.getFirstYear() + this.StartYear.getValue() - 1);
        GameTime.instance.setStartMonth(this.StartMonth.getValue() - 1);
        GameTime.instance.setStartDay(this.StartDay.getValue() - 1);
        GameTime.instance.setMinutesPerDay(this.getDayLengthMinutes());
        if (this.StartTime.getValue() == 1) {
            GameTime.instance.setStartTimeOfDay(7.0F);
        } else if (this.StartTime.getValue() == 2) {
            GameTime.instance.setStartTimeOfDay(9.0F);
        } else if (this.StartTime.getValue() == 3) {
            GameTime.instance.setStartTimeOfDay(12.0F);
        } else if (this.StartTime.getValue() == 4) {
            GameTime.instance.setStartTimeOfDay(14.0F);
        } else if (this.StartTime.getValue() == 5) {
            GameTime.instance.setStartTimeOfDay(17.0F);
        } else if (this.StartTime.getValue() == 6) {
            GameTime.instance.setStartTimeOfDay(21.0F);
        } else if (this.StartTime.getValue() == 7) {
            GameTime.instance.setStartTimeOfDay(0.0F);
        } else if (this.StartTime.getValue() == 8) {
            GameTime.instance.setStartTimeOfDay(2.0F);
        } else if (this.StartTime.getValue() == 9) {
            GameTime.instance.setStartTimeOfDay(5.0F);
        }
    }

    public void save(ByteBuffer output) throws IOException {
        output.put((byte)83);
        output.put((byte)65);
        output.put((byte)78);
        output.put((byte)68);
        output.putInt(195);
        output.putInt(5);
        output.putInt(this.options.size());

        for (int int0 = 0; int0 < this.options.size(); int0++) {
            SandboxOptions.SandboxOption sandboxOption = this.options.get(int0);
            GameWindow.WriteStringUTF(output, sandboxOption.asConfigOption().getName());
            GameWindow.WriteStringUTF(output, sandboxOption.asConfigOption().getValueAsString());
        }
    }

    public void load(ByteBuffer input) throws IOException {
        input.mark();
        byte byte0 = input.get();
        byte byte1 = input.get();
        byte byte2 = input.get();
        byte byte3 = input.get();
        int int0;
        if (byte0 == 83 && byte1 == 65 && byte2 == 78 && byte3 == 68) {
            int0 = input.getInt();
        } else {
            int0 = 41;
            input.reset();
        }

        if (int0 >= 88) {
            int int1 = 2;
            if (int0 >= 131) {
                int1 = input.getInt();
            }

            int int2 = input.getInt();

            for (int int3 = 0; int3 < int2; int3++) {
                String string0 = GameWindow.ReadStringUTF(input);
                String string1 = GameWindow.ReadStringUTF(input);
                string0 = this.upgradeOptionName(string0, int1);
                string1 = this.upgradeOptionValue(string0, string1, int1);
                SandboxOptions.SandboxOption sandboxOption = this.optionByName.get(string0);
                if (sandboxOption == null) {
                    DebugLog.log("ERROR unknown SandboxOption \"" + string0 + "\"");
                } else {
                    sandboxOption.asConfigOption().parse(string1);
                }
            }

            if (int0 < 157) {
                instance.CannedFoodLoot.setValue(instance.FoodLoot.getValue());
                instance.AmmoLoot.setValue(instance.WeaponLoot.getValue());
                instance.RangedWeaponLoot.setValue(instance.WeaponLoot.getValue());
                instance.MedicalLoot.setValue(instance.OtherLoot.getValue());
                instance.LiteratureLoot.setValue(instance.OtherLoot.getValue());
                instance.SurvivalGearsLoot.setValue(instance.OtherLoot.getValue());
                instance.MechanicsLoot.setValue(instance.OtherLoot.getValue());
            }
        }
    }

    public int getFirstYear() {
        return 1993;
    }

    private static String[] parseName(String string) {
        String[] strings0 = new String[]{null, string};
        if (string.contains(".")) {
            String[] strings1 = string.split("\\.");
            if (strings1.length == 2) {
                strings0[0] = strings1[0];
                strings0[1] = strings1[1];
            }
        }

        return strings0;
    }

    private SandboxOptions.BooleanSandboxOption newBooleanOption(String string, boolean boolean0) {
        return new SandboxOptions.BooleanSandboxOption(this, string, boolean0);
    }

    private SandboxOptions.DoubleSandboxOption newDoubleOption(String string, double double0, double double1, double double2) {
        return new SandboxOptions.DoubleSandboxOption(this, string, double0, double1, double2);
    }

    private SandboxOptions.EnumSandboxOption newEnumOption(String string, int int0, int int1) {
        return new SandboxOptions.EnumSandboxOption(this, string, int0, int1);
    }

    private SandboxOptions.IntegerSandboxOption newIntegerOption(String string, int int0, int int1, int int2) {
        return new SandboxOptions.IntegerSandboxOption(this, string, int0, int1, int2);
    }

    private SandboxOptions.StringSandboxOption newStringOption(String string0, String string1, int int0) {
        return new SandboxOptions.StringSandboxOption(this, string0, string1, int0);
    }

    protected SandboxOptions addOption(SandboxOptions.SandboxOption sandboxOption) {
        this.options.add(sandboxOption);
        this.optionByName.put(sandboxOption.asConfigOption().getName(), sandboxOption);
        return this;
    }

    public int getNumOptions() {
        return this.options.size();
    }

    public SandboxOptions.SandboxOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public SandboxOptions.SandboxOption getOptionByName(String name) {
        return this.optionByName.get(name);
    }

    public void set(String name, Object o) {
        if (name != null && o != null) {
            SandboxOptions.SandboxOption sandboxOption = this.optionByName.get(name);
            if (sandboxOption == null) {
                throw new IllegalArgumentException("unknown SandboxOption \"" + name + "\"");
            } else {
                sandboxOption.asConfigOption().setValueFromObject(o);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void copyValuesFrom(SandboxOptions other) {
        if (other == null) {
            throw new NullPointerException();
        } else {
            for (int int0 = 0; int0 < this.options.size(); int0++) {
                this.options.get(int0).asConfigOption().setValueFromObject(other.options.get(int0).asConfigOption().getValueAsObject());
            }
        }
    }

    public void resetToDefault() {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            this.options.get(int0).asConfigOption().resetToDefault();
        }
    }

    public void setDefaultsToCurrentValues() {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            this.options.get(int0).asConfigOption().setDefaultToCurrentValue();
        }
    }

    public SandboxOptions newCopy() {
        SandboxOptions sandboxOptions0 = new SandboxOptions();
        sandboxOptions0.copyValuesFrom(this);
        return sandboxOptions0;
    }

    public static boolean isValidPresetName(String name) {
        return name == null || name.isEmpty()
            ? false
            : !name.contains("/") && !name.contains("\\") && !name.contains(":") && !name.contains(";") && !name.contains("\"") && !name.contains(".");
    }

    private boolean readTextFile(String string0, boolean boolean0) {
        ConfigFile configFile = new ConfigFile();
        if (!configFile.read(string0)) {
            return false;
        } else {
            int int0 = configFile.getVersion();
            HashSet hashSet = null;
            if (boolean0 && int0 == 1) {
                hashSet = new HashSet();

                for (int int1 = 0; int1 < this.options.size(); int1++) {
                    if ("ZombieLore".equals(this.options.get(int1).getTableName())) {
                        hashSet.add(this.options.get(int1).getShortName());
                    }
                }
            }

            for (int int2 = 0; int2 < configFile.getOptions().size(); int2++) {
                ConfigOption configOption = configFile.getOptions().get(int2);
                String string1 = configOption.getName();
                String string2 = configOption.getValueAsString();
                if (hashSet != null && hashSet.contains(string1)) {
                    string1 = "ZombieLore." + string1;
                }

                if (boolean0 && int0 == 1) {
                    if ("WaterShutModifier".equals(string1)) {
                        string1 = "WaterShut";
                    } else if ("ElecShutModifier".equals(string1)) {
                        string1 = "ElecShut";
                    }
                }

                string1 = this.upgradeOptionName(string1, int0);
                string2 = this.upgradeOptionValue(string1, string2, int0);
                SandboxOptions.SandboxOption sandboxOption = this.optionByName.get(string1);
                if (sandboxOption != null) {
                    sandboxOption.asConfigOption().parse(string2);
                }
            }

            return true;
        }
    }

    private boolean writeTextFile(String string, int int0) {
        ConfigFile configFile = new ConfigFile();
        ArrayList arrayList = new ArrayList();

        for (SandboxOptions.SandboxOption sandboxOption : this.options) {
            arrayList.add(sandboxOption.asConfigOption());
        }

        return configFile.write(string, int0, arrayList);
    }

    public boolean loadServerTextFile(String serverName) {
        return this.readTextFile(ServerSettingsManager.instance.getNameInSettingsFolder(serverName + "_sandbox.ini"), false);
    }

    public boolean loadServerLuaFile(String serverName) {
        boolean boolean0 = this.readLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(serverName + "_SandboxVars.lua"));
        if (this.Lore.Speed.getValue() == 1) {
            this.Lore.Speed.setValue(2);
        }

        return boolean0;
    }

    public boolean saveServerLuaFile(String serverName) {
        return this.writeLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(serverName + "_SandboxVars.lua"), false);
    }

    public boolean loadPresetFile(String presetName) {
        return this.readTextFile(LuaManager.getSandboxCacheDir() + File.separator + presetName + ".cfg", true);
    }

    public boolean savePresetFile(String presetName) {
        return !isValidPresetName(presetName) ? false : this.writeTextFile(LuaManager.getSandboxCacheDir() + File.separator + presetName + ".cfg", 5);
    }

    public boolean loadGameFile(String presetName) {
        File file = ZomboidFileSystem.instance.getMediaFile("lua/shared/Sandbox/" + presetName + ".lua");
        if (!file.exists()) {
            throw new RuntimeException("media/lua/shared/Sandbox/" + presetName + ".lua not found");
        } else {
            try {
                LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
                Object object = LuaManager.RunLua(file.getAbsolutePath());
                if (!(object instanceof KahluaTable)) {
                    throw new RuntimeException(file.getName() + " must return a SandboxVars table");
                } else {
                    for (int int0 = 0; int0 < this.options.size(); int0++) {
                        this.options.get(int0).fromTable((KahluaTable)object);
                    }

                    return true;
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return false;
            }
        }
    }

    public boolean saveGameFile(String presetName) {
        return !Core.bDebug ? false : this.writeLuaFile("media/lua/shared/Sandbox/" + presetName + ".lua", true);
    }

    private void saveCurrentGameBinFile() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");

        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ) {
            synchronized (SliceY.SliceBufferLock) {
                SliceY.SliceBuffer.clear();
                this.save(SliceY.SliceBuffer);
                bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void handleOldZombiesFile1() {
        if (!GameServer.bServer) {
            String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini");
            ConfigFile configFile = new ConfigFile();
            if (configFile.read(string)) {
                for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                    ConfigOption configOption = configFile.getOptions().get(int0);
                    SandboxOptions.SandboxOption sandboxOption = this.optionByName.get("ZombieConfig." + configOption.getName());
                    if (sandboxOption != null) {
                        sandboxOption.asConfigOption().parse(configOption.getValueAsString());
                    }
                }
            }
        }
    }

    public void handleOldZombiesFile2() {
        if (!GameServer.bServer) {
            String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini");
            File file = new File(string);
            if (file.exists()) {
                try {
                    DebugLog.log("deleting " + file.getAbsolutePath());
                    file.delete();
                    this.saveCurrentGameBinFile();
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }
    }

    public void handleOldServerZombiesFile() {
        if (GameServer.bServer) {
            if (this.loadServerZombiesFile(GameServer.ServerName)) {
                String string = ServerSettingsManager.instance.getNameInSettingsFolder(GameServer.ServerName + "_zombies.ini");

                try {
                    File file = new File(string);
                    DebugLog.log("deleting " + file.getAbsolutePath());
                    file.delete();
                    this.saveServerLuaFile(GameServer.ServerName);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }
    }

    public boolean loadServerZombiesFile(String serverName) {
        String string = ServerSettingsManager.instance.getNameInSettingsFolder(serverName + "_zombies.ini");
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                ConfigOption configOption = configFile.getOptions().get(int0);
                SandboxOptions.SandboxOption sandboxOption = this.optionByName.get("ZombieConfig." + configOption.getName());
                if (sandboxOption != null) {
                    sandboxOption.asConfigOption().parse(configOption.getValueAsString());
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean readLuaFile(String string) {
        File file = new File(string).getAbsoluteFile();
        if (!file.exists()) {
            return false;
        } else {
            Object object0 = LuaManager.env.rawget("SandboxVars");
            KahluaTable table0 = null;
            if (object0 instanceof KahluaTable) {
                table0 = (KahluaTable)object0;
            }

            LuaManager.env.rawset("SandboxVars", null);

            boolean boolean0;
            try {
                LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
                Object object1 = LuaManager.RunLua(file.getAbsolutePath());
                Object object2 = LuaManager.env.rawget("SandboxVars");
                if (object2 != null) {
                    if (object2 instanceof KahluaTable table1) {
                        int int0 = 0;
                        Object object3 = table1.rawget("VERSION");
                        if (object3 != null) {
                            if (object3 instanceof Double) {
                                int0 = ((Double)object3).intValue();
                            } else {
                                DebugLog.log("ERROR: VERSION=\"" + object3 + "\" in " + string);
                            }

                            table1.rawset("VERSION", null);
                        }

                        KahluaTable table2 = this.upgradeLuaTable("", table1, int0);

                        for (int int1 = 0; int1 < this.options.size(); int1++) {
                            this.options.get(int1).fromTable(table2);
                        }
                    }

                    return true;
                }

                boolean0 = false;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return false;
            } finally {
                if (table0 != null) {
                    LuaManager.env.rawset("SandboxVars", table0);
                }
            }

            return boolean0;
        }
    }

    private boolean writeLuaFile(String string0, boolean boolean0) {
        if (StringUtils.containsDoubleDot(string0)) {
            throw new IllegalArgumentException("Unable to save options to filename: %s".formatted(string0));
        } else {
            File file = new File(string0).getAbsoluteFile();
            DebugLog.log("writing " + string0);

            try {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    HashMap hashMap = new HashMap();
                    ArrayList arrayList = new ArrayList();
                    hashMap.put("", new ArrayList());

                    for (SandboxOptions.SandboxOption sandboxOption0 : this.options) {
                        if (sandboxOption0.getTableName() == null) {
                            ((ArrayList)hashMap.get("")).add(sandboxOption0);
                        } else {
                            if (hashMap.get(sandboxOption0.getTableName()) == null) {
                                hashMap.put(sandboxOption0.getTableName(), new ArrayList());
                                arrayList.add(sandboxOption0.getTableName());
                            }

                            ((ArrayList)hashMap.get(sandboxOption0.getTableName())).add(sandboxOption0);
                        }
                    }

                    String string1 = System.lineSeparator();
                    if (boolean0) {
                        fileWriter.write("return {" + string1);
                    } else {
                        fileWriter.write("SandboxVars = {" + string1);
                    }

                    fileWriter.write("    VERSION = 5," + string1);

                    for (SandboxOptions.SandboxOption sandboxOption1 : (ArrayList)hashMap.get("")) {
                        if (!boolean0) {
                            String string2 = sandboxOption1.asConfigOption().getTooltip();
                            if (string2 != null) {
                                string2 = string2.replace("\\n", " ").replace("\\\"", "\"");
                                string2 = string2.replaceAll("\n", string1 + "    -- ");
                                fileWriter.write("    -- " + string2 + string1);
                            }

                            SandboxOptions.EnumSandboxOption enumSandboxOption = Type.tryCastTo(sandboxOption1, SandboxOptions.EnumSandboxOption.class);
                            if (enumSandboxOption != null) {
                                for (int int0 = 1; int0 < enumSandboxOption.getNumValues(); int0++) {
                                    try {
                                        String string3 = enumSandboxOption.getValueTranslationByIndexOrNull(int0);
                                        if (string3 != null) {
                                            fileWriter.write("    -- " + int0 + " = " + string3.replace("\\\"", "\"") + string1);
                                        }
                                    } catch (Exception exception0) {
                                        ExceptionLogger.logException(exception0);
                                    }
                                }
                            }
                        }

                        fileWriter.write(
                            "    " + sandboxOption1.asConfigOption().getName() + " = " + sandboxOption1.asConfigOption().getValueAsLuaString() + "," + string1
                        );
                    }

                    for (String string4 : arrayList) {
                        fileWriter.write("    " + string4 + " = {" + string1);

                        for (SandboxOptions.SandboxOption sandboxOption2 : (ArrayList)hashMap.get(string4)) {
                            if (!boolean0) {
                                String string5 = sandboxOption2.asConfigOption().getTooltip();
                                if (string5 != null) {
                                    string5 = string5.replace("\\n", " ").replace("\\\"", "\"");
                                    string5 = string5.replaceAll("\n", string1 + "        -- ");
                                    fileWriter.write("        -- " + string5 + string1);
                                }

                                if (sandboxOption2 instanceof SandboxOptions.EnumSandboxOption) {
                                    for (int int1 = 1; int1 < ((SandboxOptions.EnumSandboxOption)sandboxOption2).getNumValues(); int1++) {
                                        try {
                                            String string6 = ((SandboxOptions.EnumSandboxOption)sandboxOption2).getValueTranslationByIndexOrNull(int1);
                                            if (string6 != null) {
                                                fileWriter.write("        -- " + int1 + " = " + string6 + string1);
                                            }
                                        } catch (Exception exception1) {
                                            ExceptionLogger.logException(exception1);
                                        }
                                    }
                                }
                            }

                            fileWriter.write(
                                "        " + sandboxOption2.getShortName() + " = " + sandboxOption2.asConfigOption().getValueAsLuaString() + "," + string1
                            );
                        }

                        fileWriter.write("    }," + string1);
                    }

                    fileWriter.write("}" + string1);
                }

                return true;
            } catch (Exception exception2) {
                ExceptionLogger.logException(exception2);
                return false;
            }
        }
    }

    public void load() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");

        try {
            try (
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ) {
                synchronized (SliceY.SliceBufferLock) {
                    SliceY.SliceBuffer.clear();
                    int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                    SliceY.SliceBuffer.limit(int0);
                    this.load(SliceY.SliceBuffer);
                    this.handleOldZombiesFile1();
                    this.applySettings();
                    this.toLua();
                }
            }

            return;
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        this.resetToDefault();
        this.updateFromLua();
    }

    public void loadCurrentGameBinFile() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            synchronized (SliceY.SliceBufferLock) {
                SliceY.SliceBuffer.clear();
                int int0 = bufferedInputStream.read(SliceY.SliceBuffer.array());
                SliceY.SliceBuffer.limit(int0);
                this.load(SliceY.SliceBuffer);
            }

            this.toLua();
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    private String upgradeOptionName(String string, int var2) {
        return string;
    }

    private String upgradeOptionValue(String string0, String string1, int int0) {
        if (int0 < 3 && "DayLength".equals(string0)) {
            this.DayLength.parse(string1);
            if (this.DayLength.getValue() == 8) {
                this.DayLength.setValue(14);
            } else if (this.DayLength.getValue() == 9) {
                this.DayLength.setValue(26);
            }

            string1 = this.DayLength.getValueAsString();
        }

        if (int0 < 4 && "CarSpawnRate".equals(string0)) {
            try {
                int int1 = (int)Double.parseDouble(string1);
                if (int1 > 1) {
                    string1 = Integer.toString(int1 + 1);
                }
            } catch (NumberFormatException numberFormatException0) {
                numberFormatException0.printStackTrace();
            }
        }

        if (int0 < 5) {
            if ("FoodLoot".equals(string0)
                || "CannedFoodLoot".equals(string0)
                || "LiteratureLoot".equals(string0)
                || "SurvivalGearsLoot".equals(string0)
                || "MedicalLoot".equals(string0)
                || "WeaponLoot".equals(string0)
                || "RangedWeaponLoot".equals(string0)
                || "AmmoLoot".equals(string0)
                || "MechanicsLoot".equals(string0)
                || "OtherLoot".equals(string0)) {
                try {
                    int int2 = (int)Double.parseDouble(string1);
                    if (int2 > 0) {
                        string1 = Integer.toString(int2 + 2);
                    }
                } catch (NumberFormatException numberFormatException1) {
                    numberFormatException1.printStackTrace();
                }
            }

            if ("FuelStationGas".equals(string0)) {
                try {
                    int int3 = (int)Double.parseDouble(string1);
                    if (int3 > 1) {
                        string1 = Integer.toString(int3 + 1);
                    }
                } catch (NumberFormatException numberFormatException2) {
                    numberFormatException2.printStackTrace();
                }
            }

            if ("RecentlySurvivorVehicles".equals(string0)) {
                try {
                    int int4 = (int)Double.parseDouble(string1);
                    if (int4 > 0) {
                        string1 = Integer.toString(int4 + 1);
                    }
                } catch (NumberFormatException numberFormatException3) {
                    numberFormatException3.printStackTrace();
                }
            }
        }

        return string1;
    }

    private KahluaTable upgradeLuaTable(String object, KahluaTable table1, int int0) {
        KahluaTable table0 = LuaManager.platform.newTable();
        KahluaTableIterator kahluaTableIterator = table1.iterator();

        while (kahluaTableIterator.advance()) {
            if (!(kahluaTableIterator.getKey() instanceof String)) {
                throw new IllegalStateException("expected a String key");
            }

            if (kahluaTableIterator.getValue() instanceof KahluaTable) {
                KahluaTable table2 = this.upgradeLuaTable(object + kahluaTableIterator.getKey() + ".", (KahluaTable)kahluaTableIterator.getValue(), int0);
                table0.rawset(kahluaTableIterator.getKey(), table2);
            } else {
                String string0 = this.upgradeOptionName(object + kahluaTableIterator.getKey(), int0);
                String string1 = this.upgradeOptionValue(string0, kahluaTableIterator.getValue().toString(), int0);
                table0.rawset(string0.replace((CharSequence)object, ""), string1);
            }
        }

        return table0;
    }

    public void sendToServer() {
        if (GameClient.bClient) {
            GameClient.instance.sendSandboxOptionsToServer(this);
        }
    }

    public void newCustomOption(CustomSandboxOption customSandboxOption) {
        CustomBooleanSandboxOption customBooleanSandboxOption = Type.tryCastTo(customSandboxOption, CustomBooleanSandboxOption.class);
        if (customBooleanSandboxOption != null) {
            this.addCustomOption(
                new SandboxOptions.BooleanSandboxOption(this, customBooleanSandboxOption.m_id, customBooleanSandboxOption.defaultValue), customSandboxOption
            );
        } else {
            CustomDoubleSandboxOption customDoubleSandboxOption = Type.tryCastTo(customSandboxOption, CustomDoubleSandboxOption.class);
            if (customDoubleSandboxOption != null) {
                this.addCustomOption(
                    new SandboxOptions.DoubleSandboxOption(
                        this,
                        customDoubleSandboxOption.m_id,
                        customDoubleSandboxOption.min,
                        customDoubleSandboxOption.max,
                        customDoubleSandboxOption.defaultValue
                    ),
                    customSandboxOption
                );
            } else {
                CustomEnumSandboxOption customEnumSandboxOption = Type.tryCastTo(customSandboxOption, CustomEnumSandboxOption.class);
                if (customEnumSandboxOption != null) {
                    SandboxOptions.EnumSandboxOption enumSandboxOption = new SandboxOptions.EnumSandboxOption(
                        this, customEnumSandboxOption.m_id, customEnumSandboxOption.numValues, customEnumSandboxOption.defaultValue
                    );
                    if (customEnumSandboxOption.m_valueTranslation != null) {
                        enumSandboxOption.setValueTranslation(customEnumSandboxOption.m_valueTranslation);
                    }

                    this.addCustomOption(enumSandboxOption, customSandboxOption);
                } else {
                    CustomIntegerSandboxOption customIntegerSandboxOption = Type.tryCastTo(customSandboxOption, CustomIntegerSandboxOption.class);
                    if (customIntegerSandboxOption != null) {
                        this.addCustomOption(
                            new SandboxOptions.IntegerSandboxOption(
                                this,
                                customIntegerSandboxOption.m_id,
                                customIntegerSandboxOption.min,
                                customIntegerSandboxOption.max,
                                customIntegerSandboxOption.defaultValue
                            ),
                            customSandboxOption
                        );
                    } else {
                        CustomStringSandboxOption customStringSandboxOption = Type.tryCastTo(customSandboxOption, CustomStringSandboxOption.class);
                        if (customStringSandboxOption != null) {
                            this.addCustomOption(
                                new SandboxOptions.StringSandboxOption(this, customStringSandboxOption.m_id, customStringSandboxOption.defaultValue, -1),
                                customSandboxOption
                            );
                        } else {
                            throw new IllegalArgumentException("unhandled CustomSandboxOption " + customSandboxOption);
                        }
                    }
                }
            }
        }
    }

    private void addCustomOption(SandboxOptions.SandboxOption sandboxOption, CustomSandboxOption customSandboxOption) {
        sandboxOption.setCustom();
        if (customSandboxOption.m_page != null) {
            sandboxOption.setPageName(customSandboxOption.m_page);
        }

        if (customSandboxOption.m_translation != null) {
            sandboxOption.setTranslation(customSandboxOption.m_translation);
        }

        this.m_customOptions.add(sandboxOption);
    }

    private void removeCustomOptions() {
        this.options.removeAll(this.m_customOptions);

        for (SandboxOptions.SandboxOption sandboxOption : this.m_customOptions) {
            this.optionByName.remove(sandboxOption.asConfigOption().getName());
        }

        this.m_customOptions.clear();
    }

    public static void Reset() {
        instance.removeCustomOptions();
    }

    public boolean getAllClothesUnlocked() {
        return this.AllClothesUnlocked.getValue();
    }

    public static class BooleanSandboxOption extends BooleanConfigOption implements SandboxOptions.SandboxOption {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;

        public BooleanSandboxOption(SandboxOptions owner, String name, boolean defaultValue) {
            super(name, defaultValue);
            String[] strings = SandboxOptions.parseName(name);
            this.tableName = strings[0];
            this.shortName = strings[1];
            owner.addOption(this);
        }

        @Override
        public ConfigOption asConfigOption() {
            return this;
        }

        @Override
        public String getShortName() {
            return this.shortName;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public SandboxOptions.SandboxOption setTranslation(String _translation) {
            this.translation = _translation;
            return this;
        }

        @Override
        public String getTranslatedName() {
            return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
        }

        @Override
        public String getTooltip() {
            return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
        }

        @Override
        public void fromTable(KahluaTable table) {
            if (this.tableName == null || tablex.rawget(this.tableName) instanceof KahluaTable tablex) {
                Object object = tablex.rawget(this.getShortName());
                if (object != null) {
                    this.setValueFromObject(object);
                }
            }
        }

        @Override
        public void toTable(KahluaTable table) {
            if (this.tableName != null && !(tablex.rawget(this.tableName) instanceof KahluaTable tablex)) {
                KahluaTable _table = LuaManager.platform.newTable();
                tablex.rawset(this.tableName, _table);
                tablex = _table;
            }

            tablex.rawset(this.getShortName(), this.getValueAsObject());
        }

        @Override
        public void setCustom() {
            this.bCustom = true;
        }

        @Override
        public boolean isCustom() {
            return this.bCustom;
        }

        @Override
        public SandboxOptions.SandboxOption setPageName(String _pageName) {
            this.pageName = _pageName;
            return this;
        }

        @Override
        public String getPageName() {
            return this.pageName;
        }
    }

    public static class DoubleSandboxOption extends DoubleConfigOption implements SandboxOptions.SandboxOption {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;

        public DoubleSandboxOption(SandboxOptions owner, String name, double min, double max, double defaultValue) {
            super(name, min, max, defaultValue);
            String[] strings = SandboxOptions.parseName(name);
            this.tableName = strings[0];
            this.shortName = strings[1];
            owner.addOption(this);
        }

        @Override
        public ConfigOption asConfigOption() {
            return this;
        }

        @Override
        public String getShortName() {
            return this.shortName;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public SandboxOptions.SandboxOption setTranslation(String _translation) {
            this.translation = _translation;
            return this;
        }

        @Override
        public String getTranslatedName() {
            return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
        }

        @Override
        public String getTooltip() {
            String string0;
            if ("ZombieConfig".equals(this.tableName)) {
                string0 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
            } else {
                string0 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
            }

            String string1 = Translator.getText(
                "Sandbox_MinMaxDefault", String.format("%.02f", this.min), String.format("%.02f", this.max), String.format("%.02f", this.defaultValue)
            );
            if (string0 == null) {
                return string1;
            } else {
                return string1 == null ? string0 : string0 + "\\n" + string1;
            }
        }

        @Override
        public void fromTable(KahluaTable table) {
            if (this.tableName == null || tablex.rawget(this.tableName) instanceof KahluaTable tablex) {
                Object object = tablex.rawget(this.getShortName());
                if (object != null) {
                    this.setValueFromObject(object);
                }
            }
        }

        @Override
        public void toTable(KahluaTable table) {
            if (this.tableName != null && !(tablex.rawget(this.tableName) instanceof KahluaTable tablex)) {
                KahluaTable _table = LuaManager.platform.newTable();
                tablex.rawset(this.tableName, _table);
                tablex = _table;
            }

            tablex.rawset(this.getShortName(), this.getValueAsObject());
        }

        @Override
        public void setCustom() {
            this.bCustom = true;
        }

        @Override
        public boolean isCustom() {
            return this.bCustom;
        }

        @Override
        public SandboxOptions.SandboxOption setPageName(String _pageName) {
            this.pageName = _pageName;
            return this;
        }

        @Override
        public String getPageName() {
            return this.pageName;
        }
    }

    public static class EnumSandboxOption extends EnumConfigOption implements SandboxOptions.SandboxOption {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        protected String valueTranslation;

        public EnumSandboxOption(SandboxOptions owner, String name, int numValues, int defaultValue) {
            super(name, numValues, defaultValue);
            String[] strings = SandboxOptions.parseName(name);
            this.tableName = strings[0];
            this.shortName = strings[1];
            owner.addOption(this);
        }

        @Override
        public ConfigOption asConfigOption() {
            return this;
        }

        @Override
        public String getShortName() {
            return this.shortName;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public SandboxOptions.SandboxOption setTranslation(String _translation) {
            this.translation = _translation;
            return this;
        }

        @Override
        public String getTranslatedName() {
            return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
        }

        @Override
        public String getTooltip() {
            String string0 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
            String string1 = this.getValueTranslationByIndexOrNull(this.defaultValue);
            String string2 = string1 == null ? null : Translator.getText("Sandbox_Default", string1);
            if (string0 == null) {
                return string2;
            } else {
                return string2 == null ? string0 : string0 + "\\n" + string2;
            }
        }

        @Override
        public void fromTable(KahluaTable table) {
            if (this.tableName == null || tablex.rawget(this.tableName) instanceof KahluaTable tablex) {
                Object object = tablex.rawget(this.getShortName());
                if (object != null) {
                    this.setValueFromObject(object);
                }
            }
        }

        @Override
        public void toTable(KahluaTable table) {
            if (this.tableName != null && !(tablex.rawget(this.tableName) instanceof KahluaTable tablex)) {
                KahluaTable _table = LuaManager.platform.newTable();
                tablex.rawset(this.tableName, _table);
                tablex = _table;
            }

            tablex.rawset(this.getShortName(), this.getValueAsObject());
        }

        @Override
        public void setCustom() {
            this.bCustom = true;
        }

        @Override
        public boolean isCustom() {
            return this.bCustom;
        }

        @Override
        public SandboxOptions.SandboxOption setPageName(String _pageName) {
            this.pageName = _pageName;
            return this;
        }

        @Override
        public String getPageName() {
            return this.pageName;
        }

        public SandboxOptions.EnumSandboxOption setValueTranslation(String _translation) {
            this.valueTranslation = _translation;
            return this;
        }

        public String getValueTranslation() {
            return this.valueTranslation != null ? this.valueTranslation : (this.translation == null ? this.getShortName() : this.translation);
        }

        public String getValueTranslationByIndex(int index) {
            if (index >= 1 && index <= this.getNumValues()) {
                return Translator.getText("Sandbox_" + this.getValueTranslation() + "_option" + index);
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        public String getValueTranslationByIndexOrNull(int index) {
            if (index >= 1 && index <= this.getNumValues()) {
                return Translator.getTextOrNull("Sandbox_" + this.getValueTranslation() + "_option" + index);
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
    }

    public static class IntegerSandboxOption extends IntegerConfigOption implements SandboxOptions.SandboxOption {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;

        public IntegerSandboxOption(SandboxOptions owner, String name, int min, int max, int defaultValue) {
            super(name, min, max, defaultValue);
            String[] strings = SandboxOptions.parseName(name);
            this.tableName = strings[0];
            this.shortName = strings[1];
            owner.addOption(this);
        }

        @Override
        public ConfigOption asConfigOption() {
            return this;
        }

        @Override
        public String getShortName() {
            return this.shortName;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public SandboxOptions.SandboxOption setTranslation(String _translation) {
            this.translation = _translation;
            return this;
        }

        @Override
        public String getTranslatedName() {
            return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
        }

        @Override
        public String getTooltip() {
            String string0;
            if ("ZombieConfig".equals(this.tableName)) {
                string0 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
            } else {
                string0 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
            }

            String string1 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
            if (string0 == null) {
                return string1;
            } else {
                return string1 == null ? string0 : string0 + "\\n" + string1;
            }
        }

        @Override
        public void fromTable(KahluaTable table) {
            if (this.tableName == null || tablex.rawget(this.tableName) instanceof KahluaTable tablex) {
                Object object = tablex.rawget(this.getShortName());
                if (object != null) {
                    this.setValueFromObject(object);
                }
            }
        }

        @Override
        public void toTable(KahluaTable table) {
            if (this.tableName != null && !(tablex.rawget(this.tableName) instanceof KahluaTable tablex)) {
                KahluaTable _table = LuaManager.platform.newTable();
                tablex.rawset(this.tableName, _table);
                tablex = _table;
            }

            tablex.rawset(this.getShortName(), this.getValueAsObject());
        }

        @Override
        public void setCustom() {
            this.bCustom = true;
        }

        @Override
        public boolean isCustom() {
            return this.bCustom;
        }

        @Override
        public SandboxOptions.SandboxOption setPageName(String _pageName) {
            this.pageName = _pageName;
            return this;
        }

        @Override
        public String getPageName() {
            return this.pageName;
        }
    }

    public final class Map {
        public final SandboxOptions.BooleanSandboxOption AllowMiniMap = SandboxOptions.this.newBooleanOption("Map.AllowMiniMap", false);
        public final SandboxOptions.BooleanSandboxOption AllowWorldMap = SandboxOptions.this.newBooleanOption("Map.AllowWorldMap", true);
        public final SandboxOptions.BooleanSandboxOption MapAllKnown = SandboxOptions.this.newBooleanOption("Map.MapAllKnown", false);

        Map() {
        }
    }

    public interface SandboxOption {
        ConfigOption asConfigOption();

        String getShortName();

        String getTableName();

        SandboxOptions.SandboxOption setTranslation(String translation);

        String getTranslatedName();

        String getTooltip();

        void fromTable(KahluaTable table);

        void toTable(KahluaTable table);

        void setCustom();

        boolean isCustom();

        SandboxOptions.SandboxOption setPageName(String pageName);

        String getPageName();
    }

    public static class StringSandboxOption extends StringConfigOption implements SandboxOptions.SandboxOption {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;

        public StringSandboxOption(SandboxOptions owner, String name, String defaultValue, int maxLength) {
            super(name, defaultValue, maxLength);
            String[] strings = SandboxOptions.parseName(name);
            this.tableName = strings[0];
            this.shortName = strings[1];
            owner.addOption(this);
        }

        @Override
        public ConfigOption asConfigOption() {
            return this;
        }

        @Override
        public String getShortName() {
            return this.shortName;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public SandboxOptions.SandboxOption setTranslation(String _translation) {
            this.translation = _translation;
            return this;
        }

        @Override
        public String getTranslatedName() {
            return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
        }

        @Override
        public String getTooltip() {
            return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
        }

        @Override
        public void fromTable(KahluaTable table) {
            if (this.tableName == null || tablex.rawget(this.tableName) instanceof KahluaTable tablex) {
                Object object = tablex.rawget(this.getShortName());
                if (object != null) {
                    this.setValueFromObject(object);
                }
            }
        }

        @Override
        public void toTable(KahluaTable table) {
            if (this.tableName != null && !(tablex.rawget(this.tableName) instanceof KahluaTable tablex)) {
                KahluaTable _table = LuaManager.platform.newTable();
                tablex.rawset(this.tableName, _table);
                tablex = _table;
            }

            tablex.rawset(this.getShortName(), this.getValueAsObject());
        }

        @Override
        public void setCustom() {
            this.bCustom = true;
        }

        @Override
        public boolean isCustom() {
            return this.bCustom;
        }

        @Override
        public SandboxOptions.SandboxOption setPageName(String _pageName) {
            this.pageName = _pageName;
            return this;
        }

        @Override
        public String getPageName() {
            return this.pageName;
        }
    }

    public final class ZombieConfig {
        public final SandboxOptions.DoubleSandboxOption PopulationMultiplier = SandboxOptions.this.newDoubleOption(
            "ZombieConfig.PopulationMultiplier", 0.0, 4.0, 1.0
        );
        public final SandboxOptions.DoubleSandboxOption PopulationStartMultiplier = SandboxOptions.this.newDoubleOption(
            "ZombieConfig.PopulationStartMultiplier", 0.0, 4.0, 1.0
        );
        public final SandboxOptions.DoubleSandboxOption PopulationPeakMultiplier = SandboxOptions.this.newDoubleOption(
            "ZombieConfig.PopulationPeakMultiplier", 0.0, 4.0, 1.5
        );
        public final SandboxOptions.IntegerSandboxOption PopulationPeakDay = SandboxOptions.this.newIntegerOption("ZombieConfig.PopulationPeakDay", 1, 365, 28);
        public final SandboxOptions.DoubleSandboxOption RespawnHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnHours", 0.0, 8760.0, 72.0);
        public final SandboxOptions.DoubleSandboxOption RespawnUnseenHours = SandboxOptions.this.newDoubleOption(
            "ZombieConfig.RespawnUnseenHours", 0.0, 8760.0, 16.0
        );
        public final SandboxOptions.DoubleSandboxOption RespawnMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnMultiplier", 0.0, 1.0, 0.1);
        public final SandboxOptions.DoubleSandboxOption RedistributeHours = SandboxOptions.this.newDoubleOption(
            "ZombieConfig.RedistributeHours", 0.0, 8760.0, 12.0
        );
        public final SandboxOptions.IntegerSandboxOption FollowSoundDistance = SandboxOptions.this.newIntegerOption(
            "ZombieConfig.FollowSoundDistance", 10, 1000, 100
        );
        public final SandboxOptions.IntegerSandboxOption RallyGroupSize = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupSize", 0, 1000, 20);
        public final SandboxOptions.IntegerSandboxOption RallyTravelDistance = SandboxOptions.this.newIntegerOption(
            "ZombieConfig.RallyTravelDistance", 5, 50, 20
        );
        public final SandboxOptions.IntegerSandboxOption RallyGroupSeparation = SandboxOptions.this.newIntegerOption(
            "ZombieConfig.RallyGroupSeparation", 5, 25, 15
        );
        public final SandboxOptions.IntegerSandboxOption RallyGroupRadius = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupRadius", 1, 10, 3);

        private ZombieConfig() {
        }
    }

    public final class ZombieLore {
        public final SandboxOptions.EnumSandboxOption Speed = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Speed", 4, 2)
            .setTranslation("ZSpeed");
        public final SandboxOptions.EnumSandboxOption Strength = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Strength", 4, 2
            )
            .setTranslation("ZStrength");
        public final SandboxOptions.EnumSandboxOption Toughness = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Toughness", 4, 2
            )
            .setTranslation("ZToughness");
        public final SandboxOptions.EnumSandboxOption Transmission = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Transmission", 4, 1
            )
            .setTranslation("ZTransmission");
        public final SandboxOptions.EnumSandboxOption Mortality = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Mortality", 7, 5
            )
            .setTranslation("ZInfectionMortality");
        public final SandboxOptions.EnumSandboxOption Reanimate = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Reanimate", 6, 3
            )
            .setTranslation("ZReanimateTime");
        public final SandboxOptions.EnumSandboxOption Cognition = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.Cognition", 4, 3
            )
            .setTranslation("ZCognition");
        public final SandboxOptions.EnumSandboxOption CrawlUnderVehicle = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.CrawlUnderVehicle", 7, 5
            )
            .setTranslation("ZCrawlUnderVehicle");
        public final SandboxOptions.EnumSandboxOption Memory = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Memory", 5, 2)
            .setTranslation("ZMemory");
        public final SandboxOptions.EnumSandboxOption Sight = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Sight", 4, 2)
            .setTranslation("ZSight");
        public final SandboxOptions.EnumSandboxOption Hearing = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Hearing", 4, 2)
            .setTranslation("ZHearing");
        public final SandboxOptions.BooleanSandboxOption ThumpNoChasing = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpNoChasing", false);
        public final SandboxOptions.BooleanSandboxOption ThumpOnConstruction = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpOnConstruction", true);
        public final SandboxOptions.EnumSandboxOption ActiveOnly = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption(
                "ZombieLore.ActiveOnly", 3, 1
            )
            .setTranslation("ActiveOnly");
        public final SandboxOptions.BooleanSandboxOption TriggerHouseAlarm = SandboxOptions.this.newBooleanOption("ZombieLore.TriggerHouseAlarm", false);
        public final SandboxOptions.BooleanSandboxOption ZombiesDragDown = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesDragDown", true);
        public final SandboxOptions.BooleanSandboxOption ZombiesFenceLunge = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesFenceLunge", true);
        public final SandboxOptions.EnumSandboxOption DisableFakeDead = SandboxOptions.this.newEnumOption("ZombieLore.DisableFakeDead", 3, 1);

        private ZombieLore() {
        }
    }
}
