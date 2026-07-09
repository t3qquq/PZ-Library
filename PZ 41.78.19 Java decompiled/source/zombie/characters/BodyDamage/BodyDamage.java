// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.BodyDamage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.ClothingWetness;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Literature;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public final class BodyDamage {
    public final ArrayList<BodyPart> BodyParts = new ArrayList<>(18);
    public final ArrayList<BodyPartLast> BodyPartsLastState = new ArrayList<>(18);
    public int DamageModCount = 60;
    public float InfectionGrowthRate = 0.001F;
    public float InfectionLevel = 0.0F;
    public boolean IsInfected;
    public float InfectionTime = -1.0F;
    public float InfectionMortalityDuration = -1.0F;
    public float FakeInfectionLevel = 0.0F;
    public boolean IsFakeInfected;
    public float OverallBodyHealth = 100.0F;
    public float StandardHealthAddition = 0.002F;
    public float ReducedHealthAddition = 0.0013F;
    public float SeverlyReducedHealthAddition = 8.0E-4F;
    public float SleepingHealthAddition = 0.02F;
    public float HealthFromFood = 0.015F;
    public float HealthReductionFromSevereBadMoodles = 0.0165F;
    public int StandardHealthFromFoodTime = 1600;
    public float HealthFromFoodTimer = 0.0F;
    public float BoredomLevel = 0.0F;
    public float BoredomDecreaseFromReading = 0.5F;
    public float InitialThumpPain = 14.0F;
    public float InitialScratchPain = 18.0F;
    public float InitialBitePain = 25.0F;
    public float InitialWoundPain = 80.0F;
    public float ContinualPainIncrease = 0.001F;
    public float PainReductionFromMeds = 30.0F;
    public float StandardPainReductionWhenWell = 0.01F;
    public int OldNumZombiesVisible = 0;
    public int CurrentNumZombiesVisible = 0;
    public float PanicIncreaseValue = 7.0F;
    public float PanicIncreaseValueFrame = 0.035F;
    public float PanicReductionValue = 0.06F;
    public float DrunkIncreaseValue = 20.5F;
    public float DrunkReductionValue = 0.0042F;
    public boolean IsOnFire = false;
    public boolean BurntToDeath = false;
    public float Wetness = 0.0F;
    public float CatchACold = 0.0F;
    public boolean HasACold = false;
    public float ColdStrength = 0.0F;
    public float ColdProgressionRate = 0.0112F;
    public int TimeToSneezeOrCough = 0;
    public int MildColdSneezeTimerMin = 600;
    public int MildColdSneezeTimerMax = 800;
    public int ColdSneezeTimerMin = 300;
    public int ColdSneezeTimerMax = 600;
    public int NastyColdSneezeTimerMin = 200;
    public int NastyColdSneezeTimerMax = 300;
    public int SneezeCoughActive = 0;
    public int SneezeCoughTime = 0;
    public int SneezeCoughDelay = 25;
    public float UnhappynessLevel = 0.0F;
    public float ColdDamageStage = 0.0F;
    public IsoGameCharacter ParentChar;
    private float FoodSicknessLevel = 0.0F;
    private int RemotePainLevel;
    private float Temperature = 37.0F;
    private float lastTemperature = 37.0F;
    private float PoisonLevel = 0.0F;
    private boolean reduceFakeInfection = false;
    private float painReduction = 0.0F;
    private float coldReduction = 0.0F;
    private Thermoregulator thermoregulator;
    public static final float InfectionLevelToZombify = 0.001F;
    static String behindStr = "BEHIND";
    static String leftStr = "LEFT";
    static String rightStr = "RIGHT";

    public BodyDamage(IsoGameCharacter ParentCharacter) {
        this.BodyParts.add(new BodyPart(BodyPartType.Hand_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Hand_R, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_R, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_R, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Torso_Upper, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Torso_Lower, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Head, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Neck, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Groin, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_R, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_R, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Foot_L, ParentCharacter));
        this.BodyParts.add(new BodyPart(BodyPartType.Foot_R, ParentCharacter));

        for (BodyPart bodyPart : this.BodyParts) {
            this.BodyPartsLastState.add(new BodyPartLast());
        }

        this.RestoreToFullHealth();
        this.ParentChar = ParentCharacter;
        if (this.ParentChar instanceof IsoPlayer) {
            this.thermoregulator = new Thermoregulator(this);
        }

        this.setBodyPartsLastState();
    }

    public BodyPart getBodyPart(BodyPartType type) {
        return this.BodyParts.get(BodyPartType.ToIndex(type));
    }

    public BodyPartLast getBodyPartsLastState(BodyPartType type) {
        return this.BodyPartsLastState.get(BodyPartType.ToIndex(type));
    }

    public void setBodyPartsLastState() {
        for (int int0 = 0; int0 < this.getBodyParts().size(); int0++) {
            BodyPart bodyPart = this.getBodyParts().get(int0);
            BodyPartLast bodyPartLast = this.BodyPartsLastState.get(int0);
            bodyPartLast.copy(bodyPart);
        }
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        for (int int0 = 0; int0 < this.getBodyParts().size(); int0++) {
            BodyPart bodyPart = this.getBodyParts().get(int0);
            bodyPart.SetBitten(input.get() == 1);
            bodyPart.setScratched(input.get() == 1, false);
            bodyPart.setBandaged(input.get() == 1, 0.0F);
            bodyPart.setBleeding(input.get() == 1);
            bodyPart.setDeepWounded(input.get() == 1);
            bodyPart.SetFakeInfected(input.get() == 1);
            bodyPart.SetInfected(input.get() == 1);
            bodyPart.SetHealth(input.getFloat());
            if (WorldVersion >= 37 && WorldVersion <= 43) {
                input.getInt();
            }

            if (WorldVersion >= 44) {
                if (bodyPart.bandaged()) {
                    bodyPart.setBandageLife(input.getFloat());
                }

                bodyPart.setInfectedWound(input.get() == 1);
                if (bodyPart.isInfectedWound()) {
                    bodyPart.setWoundInfectionLevel(input.getFloat());
                }

                bodyPart.setBiteTime(input.getFloat());
                bodyPart.setScratchTime(input.getFloat());
                bodyPart.setBleedingTime(input.getFloat());
                bodyPart.setAlcoholLevel(input.getFloat());
                bodyPart.setAdditionalPain(input.getFloat());
                bodyPart.setDeepWoundTime(input.getFloat());
                bodyPart.setHaveGlass(input.get() == 1);
                bodyPart.setGetBandageXp(input.get() == 1);
                if (WorldVersion >= 48) {
                    bodyPart.setStitched(input.get() == 1);
                    bodyPart.setStitchTime(input.getFloat());
                }

                bodyPart.setGetStitchXp(input.get() == 1);
                bodyPart.setGetSplintXp(input.get() == 1);
                bodyPart.setFractureTime(input.getFloat());
                bodyPart.setSplint(input.get() == 1, 0.0F);
                if (bodyPart.isSplint()) {
                    bodyPart.setSplintFactor(input.getFloat());
                }

                bodyPart.setHaveBullet(input.get() == 1, 0);
                bodyPart.setBurnTime(input.getFloat());
                bodyPart.setNeedBurnWash(input.get() == 1);
                bodyPart.setLastTimeBurnWash(input.getFloat());
                bodyPart.setSplintItem(GameWindow.ReadString(input));
                bodyPart.setBandageType(GameWindow.ReadString(input));
                bodyPart.setCutTime(input.getFloat());
                if (WorldVersion >= 153) {
                    bodyPart.setWetness(input.getFloat());
                }

                if (WorldVersion >= 167) {
                    bodyPart.setStiffness(input.getFloat());
                }
            }
        }

        this.setBodyPartsLastState();
        this.setInfectionLevel(input.getFloat());
        this.setFakeInfectionLevel(input.getFloat());
        this.setWetness(input.getFloat());
        this.setCatchACold(input.getFloat());
        this.setHasACold(input.get() == 1);
        this.setColdStrength(input.getFloat());
        this.setUnhappynessLevel(input.getFloat());
        this.setBoredomLevel(input.getFloat());
        this.setFoodSicknessLevel(input.getFloat());
        this.PoisonLevel = input.getFloat();
        float float0 = input.getFloat();
        this.setTemperature(float0);
        this.setReduceFakeInfection(input.get() == 1);
        this.setHealthFromFoodTimer(input.getFloat());
        this.painReduction = input.getFloat();
        this.coldReduction = input.getFloat();
        this.InfectionTime = input.getFloat();
        this.InfectionMortalityDuration = input.getFloat();
        this.ColdDamageStage = input.getFloat();
        this.calculateOverallHealth();
        if (WorldVersion >= 153 && input.get() == 1) {
            if (this.thermoregulator != null) {
                this.thermoregulator.load(input, WorldVersion);
            } else {
                Thermoregulator thermoregulatorx = new Thermoregulator(this);
                thermoregulatorx.load(input, WorldVersion);
                DebugLog.log("Couldnt load Thermoregulator, == null");
            }
        }
    }

    public void save(ByteBuffer output) throws IOException {
        for (int int0 = 0; int0 < this.getBodyParts().size(); int0++) {
            BodyPart bodyPart = this.getBodyParts().get(int0);
            output.put((byte)(bodyPart.bitten() ? 1 : 0));
            output.put((byte)(bodyPart.scratched() ? 1 : 0));
            output.put((byte)(bodyPart.bandaged() ? 1 : 0));
            output.put((byte)(bodyPart.bleeding() ? 1 : 0));
            output.put((byte)(bodyPart.deepWounded() ? 1 : 0));
            output.put((byte)(bodyPart.IsFakeInfected() ? 1 : 0));
            output.put((byte)(bodyPart.IsInfected() ? 1 : 0));
            output.putFloat(bodyPart.getHealth());
            if (bodyPart.bandaged()) {
                output.putFloat(bodyPart.getBandageLife());
            }

            output.put((byte)(bodyPart.isInfectedWound() ? 1 : 0));
            if (bodyPart.isInfectedWound()) {
                output.putFloat(bodyPart.getWoundInfectionLevel());
            }

            output.putFloat(bodyPart.getBiteTime());
            output.putFloat(bodyPart.getScratchTime());
            output.putFloat(bodyPart.getBleedingTime());
            output.putFloat(bodyPart.getAlcoholLevel());
            output.putFloat(bodyPart.getAdditionalPain());
            output.putFloat(bodyPart.getDeepWoundTime());
            output.put((byte)(bodyPart.haveGlass() ? 1 : 0));
            output.put((byte)(bodyPart.isGetBandageXp() ? 1 : 0));
            output.put((byte)(bodyPart.stitched() ? 1 : 0));
            output.putFloat(bodyPart.getStitchTime());
            output.put((byte)(bodyPart.isGetStitchXp() ? 1 : 0));
            output.put((byte)(bodyPart.isGetSplintXp() ? 1 : 0));
            output.putFloat(bodyPart.getFractureTime());
            output.put((byte)(bodyPart.isSplint() ? 1 : 0));
            if (bodyPart.isSplint()) {
                output.putFloat(bodyPart.getSplintFactor());
            }

            output.put((byte)(bodyPart.haveBullet() ? 1 : 0));
            output.putFloat(bodyPart.getBurnTime());
            output.put((byte)(bodyPart.isNeedBurnWash() ? 1 : 0));
            output.putFloat(bodyPart.getLastTimeBurnWash());
            GameWindow.WriteString(output, bodyPart.getSplintItem());
            GameWindow.WriteString(output, bodyPart.getBandageType());
            output.putFloat(bodyPart.getCutTime());
            output.putFloat(bodyPart.getWetness());
            output.putFloat(bodyPart.getStiffness());
        }

        output.putFloat(this.InfectionLevel);
        output.putFloat(this.getFakeInfectionLevel());
        output.putFloat(this.getWetness());
        output.putFloat(this.getCatchACold());
        output.put((byte)(this.isHasACold() ? 1 : 0));
        output.putFloat(this.getColdStrength());
        output.putFloat(this.getUnhappynessLevel());
        output.putFloat(this.getBoredomLevel());
        output.putFloat(this.getFoodSicknessLevel());
        output.putFloat(this.PoisonLevel);
        output.putFloat(this.Temperature);
        output.put((byte)(this.isReduceFakeInfection() ? 1 : 0));
        output.putFloat(this.HealthFromFoodTimer);
        output.putFloat(this.painReduction);
        output.putFloat(this.coldReduction);
        output.putFloat(this.InfectionTime);
        output.putFloat(this.InfectionMortalityDuration);
        output.putFloat(this.ColdDamageStage);
        output.put((byte)(this.thermoregulator != null ? 1 : 0));
        if (this.thermoregulator != null) {
            this.thermoregulator.save(output);
        }
    }

    public boolean IsFakeInfected() {
        return this.isIsFakeInfected();
    }

    public void OnFire(boolean OnFire) {
        this.setIsOnFire(OnFire);
    }

    public boolean IsOnFire() {
        return this.isIsOnFire();
    }

    public boolean WasBurntToDeath() {
        return this.isBurntToDeath();
    }

    public void IncreasePanicFloat(float delta) {
        float float0 = 1.0F;
        if (this.getParentChar().getBetaEffect() > 0.0F) {
            float0 -= this.getParentChar().getBetaDelta();
            if (float0 > 1.0F) {
                float0 = 1.0F;
            }

            if (float0 < 0.0F) {
                float0 = 0.0F;
            }
        }

        if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
            float0 *= 2.0F;
        }

        if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
            float0 *= 0.3F;
        }

        if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
            float0 = 0.0F;
        }

        Stats stats = this.ParentChar.getStats();
        stats.Panic = stats.Panic + this.getPanicIncreaseValueFrame() * delta * float0;
        if (this.getParentChar().getStats().Panic > 100.0F) {
            this.ParentChar.getStats().Panic = 100.0F;
        }
    }

    public void IncreasePanic(int NumNewZombiesSeen) {
        if (this.getParentChar().getVehicle() != null) {
            NumNewZombiesSeen /= 2;
        }

        float float0 = 1.0F;
        if (this.getParentChar().getBetaEffect() > 0.0F) {
            float0 -= this.getParentChar().getBetaDelta();
            if (float0 > 1.0F) {
                float0 = 1.0F;
            }

            if (float0 < 0.0F) {
                float0 = 0.0F;
            }
        }

        if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
            float0 *= 2.0F;
        }

        if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
            float0 *= 0.3F;
        }

        if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
            float0 = 0.0F;
        }

        Stats stats = this.ParentChar.getStats();
        stats.Panic = stats.Panic + this.getPanicIncreaseValue() * NumNewZombiesSeen * float0;
        if (this.getParentChar().getStats().Panic > 100.0F) {
            this.ParentChar.getStats().Panic = 100.0F;
        }
    }

    public void ReducePanic() {
        if (!(this.ParentChar.getStats().Panic <= 0.0F)) {
            float float0 = this.getPanicReductionValue() * (GameTime.getInstance().getMultiplier() / 1.6F);
            int int0 = (int)Math.floor(new Double(GameTime.instance.getNightsSurvived()) / 30.0);
            if (int0 > 5) {
                int0 = 5;
            }

            float0 += this.getPanicReductionValue() * int0;
            if (this.ParentChar.isAsleep()) {
                float0 *= 2.0F;
            }

            this.ParentChar.getStats().Panic -= float0;
            if (this.getParentChar().getStats().Panic < 0.0F) {
                this.ParentChar.getStats().Panic = 0.0F;
            }
        }
    }

    public void UpdatePanicState() {
        int int0 = this.getParentChar().getStats().NumVisibleZombies;
        if (int0 > this.getOldNumZombiesVisible()) {
            this.IncreasePanic(int0 - this.getOldNumZombiesVisible());
        } else {
            this.ReducePanic();
        }

        this.setOldNumZombiesVisible(int0);
    }

    public void JustDrankBooze(Food food, float percentage) {
        float float0 = 1.0F;
        if (this.getParentChar().Traits.HeavyDrinker.isSet()) {
            float0 = 0.3F;
        }

        if (this.getParentChar().Traits.LightDrinker.isSet()) {
            float0 = 4.0F;
        }

        if (food.getBaseHunger() != 0.0F) {
            percentage = food.getHungChange() * percentage / food.getBaseHunger() * 2.0F;
        }

        float0 *= percentage;
        if (food.getName().toLowerCase().contains("beer") || food.hasTag("LowAlcohol")) {
            float0 *= 0.25F;
        }

        if (this.getParentChar().getStats().hunger > 0.8) {
            float0 = (float)(float0 * 1.25);
        } else if (this.getParentChar().getStats().hunger > 0.6) {
            float0 = (float)(float0 * 1.1);
        }

        Stats stats = this.ParentChar.getStats();
        stats.Drunkenness = stats.Drunkenness + this.getDrunkIncreaseValue() * float0;
        if (this.getParentChar().getStats().Drunkenness > 100.0F) {
            this.ParentChar.getStats().Drunkenness = 100.0F;
        }

        this.getParentChar().SleepingTablet(0.02F * percentage);
        this.getParentChar().BetaAntiDepress(0.4F * percentage);
        this.getParentChar().BetaBlockers(0.2F * percentage);
        this.getParentChar().PainMeds(0.2F * percentage);
    }

    public void JustTookPill(InventoryItem Pill) {
        if ("PillsBeta".equals(Pill.getType())) {
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
                this.getParentChar().BetaBlockers(0.15F);
            } else {
                this.getParentChar().BetaBlockers(0.3F);
            }

            Pill.Use();
        } else if ("PillsAntiDep".equals(Pill.getType())) {
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
                this.getParentChar().BetaAntiDepress(0.15F);
            } else {
                this.getParentChar().BetaAntiDepress(0.3F);
            }

            Pill.Use();
        } else if ("PillsSleepingTablets".equals(Pill.getType())) {
            Pill.Use();
            this.getParentChar().SleepingTablet(0.1F);
            if (this.getParentChar() instanceof IsoPlayer) {
                ((IsoPlayer)this.getParentChar()).setSleepingPillsTaken(((IsoPlayer)this.getParentChar()).getSleepingPillsTaken() + 1);
            }
        } else if ("Pills".equals(Pill.getType())) {
            Pill.Use();
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
                this.getParentChar().PainMeds(0.15F);
            } else {
                this.getParentChar().PainMeds(0.45F);
            }
        } else if ("PillsVitamins".equals(Pill.getType())) {
            Pill.Use();
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0F) {
                Stats stats0 = this.getParentChar().getStats();
                stats0.fatigue = stats0.fatigue + Pill.getFatigueChange() / 2.0F;
            } else {
                Stats stats1 = this.getParentChar().getStats();
                stats1.fatigue = stats1.fatigue + Pill.getFatigueChange();
            }
        }
    }

    public void JustAteFood(Food NewFood, float percentage) {
        if (NewFood.getPoisonPower() > 0) {
            float float0 = NewFood.getPoisonPower() * percentage;
            if (this.getParentChar().Traits.IronGut.isSet()) {
                float0 /= 2.0F;
            }

            if (this.getParentChar().Traits.WeakStomach.isSet()) {
                float0 *= 2.0F;
            }

            this.PoisonLevel += float0;
            Stats stats = this.ParentChar.getStats();
            stats.Pain = stats.Pain + NewFood.getPoisonPower() * percentage / 6.0F;
        }

        if (NewFood.isTaintedWater()) {
            this.PoisonLevel += 20.0F * percentage;
            this.ParentChar.getStats().Pain += 10.0F * percentage / 6.0F;
        }

        if (NewFood.getReduceInfectionPower() > 0.0F) {
            this.getParentChar().setReduceInfectionPower(NewFood.getReduceInfectionPower());
        }

        this.setBoredomLevel(this.getBoredomLevel() + NewFood.getBoredomChange() * percentage);
        if (this.getBoredomLevel() < 0.0F) {
            this.setBoredomLevel(0.0F);
        }

        this.setUnhappynessLevel(this.getUnhappynessLevel() + NewFood.getUnhappyChange() * percentage);
        if (this.getUnhappynessLevel() < 0.0F) {
            this.setUnhappynessLevel(0.0F);
        }

        if (NewFood.isAlcoholic()) {
            this.JustDrankBooze(NewFood, percentage);
        }

        if (this.getParentChar().getStats().hunger <= 0.0F) {
            float float1 = Math.abs(NewFood.getHungerChange()) * percentage;
            this.setHealthFromFoodTimer((int)(this.getHealthFromFoodTimer() + float1 * this.getHealthFromFoodTimeByHunger()));
            if (NewFood.isCooked()) {
                this.setHealthFromFoodTimer((int)(this.getHealthFromFoodTimer() + float1 * this.getHealthFromFoodTimeByHunger()));
            }

            if (this.getHealthFromFoodTimer() > 11000.0F) {
                this.setHealthFromFoodTimer(11000.0F);
            }
        }

        if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
            if (!NewFood.isCooked() && NewFood.isbDangerousUncooked()) {
                this.setHealthFromFoodTimer(0.0F);
                byte byte0 = 75;
                if (NewFood.hasTag("Egg")) {
                    byte0 = 5;
                }

                if (this.getParentChar().Traits.IronGut.isSet()) {
                    byte0 /= 2;
                    if (NewFood.hasTag("Egg")) {
                        byte0 = 0;
                    }
                }

                if (this.getParentChar().Traits.WeakStomach.isSet()) {
                    byte0 *= 2;
                }

                if (byte0 > 0 && Rand.Next(100) < byte0 && !this.isInfected()) {
                    this.PoisonLevel += 15.0F * percentage;
                }
            }

            if (NewFood.getAge() >= NewFood.getOffAgeMax()) {
                float float2 = NewFood.getAge() - NewFood.getOffAgeMax();
                if (float2 == 0.0F) {
                    float2 = 1.0F;
                }

                if (float2 > 5.0F) {
                    float2 = 5.0F;
                }

                int int0;
                if (NewFood.getOffAgeMax() > NewFood.getOffAge()) {
                    int0 = (int)(float2 / (NewFood.getOffAgeMax() - NewFood.getOffAge()) * 100.0F);
                } else {
                    int0 = 100;
                }

                if (this.getParentChar().Traits.IronGut.isSet()) {
                    int0 /= 2;
                }

                if (this.getParentChar().Traits.WeakStomach.isSet()) {
                    int0 *= 2;
                }

                if (Rand.Next(100) < int0 && !this.isInfected()) {
                    this.PoisonLevel = this.PoisonLevel + 5.0F * Math.abs(NewFood.getHungChange() * 10.0F) * percentage;
                }
            }
        }
    }

    public void JustAteFood(Food NewFood) {
        this.JustAteFood(NewFood, 100.0F);
    }

    private float getHealthFromFoodTimeByHunger() {
        return 13000.0F;
    }

    public void JustReadSomething(Literature lit) {
        this.setBoredomLevel(this.getBoredomLevel() + lit.getBoredomChange());
        if (this.getBoredomLevel() < 0.0F) {
            this.setBoredomLevel(0.0F);
        }

        this.setUnhappynessLevel(this.getUnhappynessLevel() + lit.getUnhappyChange());
        if (this.getUnhappynessLevel() < 0.0F) {
            this.setUnhappynessLevel(0.0F);
        }
    }

    public void JustTookPainMeds() {
        Stats stats = this.ParentChar.getStats();
        stats.Pain = stats.Pain - this.getPainReductionFromMeds();
        if (this.getParentChar().getStats().Pain < 0.0F) {
            this.ParentChar.getStats().Pain = 0.0F;
        }
    }

    public void UpdateWetness() {
        IsoGridSquare square = this.getParentChar().getCurrentSquare();
        BaseVehicle vehicle = this.getParentChar().getVehicle();
        IsoGameCharacter character = this.getParentChar();
        boolean boolean0 = square == null || !square.isInARoom() && !square.haveRoof;
        if (vehicle != null && vehicle.hasRoof(vehicle.getSeat(this.getParentChar()))) {
            boolean0 = false;
        }

        ClothingWetness clothingWetness = this.getParentChar().getClothingWetness();
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;
        if (vehicle != null && ClimateManager.getInstance().isRaining()) {
            VehiclePart part = vehicle.getPartById("Windshield");
            if (part != null) {
                VehicleWindow vehicleWindow = part.getWindow();
                if (vehicleWindow != null && vehicleWindow.isDestroyed()) {
                    float float3 = ClimateManager.getInstance().getRainIntensity();
                    float3 *= float3;
                    float3 *= vehicle.getCurrentSpeedKmHour() / 50.0F;
                    if (float3 < 0.1F) {
                        float3 = 0.0F;
                    }

                    if (float3 > 1.0F) {
                        float3 = 1.0F;
                    }

                    float2 = float3 * 3.0F;
                    float0 = float3;
                }
            }
        }

        if (boolean0 && character.isAsleep() && character.getBed() != null && "Tent".equals(character.getBed().getName())) {
            boolean0 = false;
        }

        if (boolean0 && ClimateManager.getInstance().isRaining()) {
            float float4 = ClimateManager.getInstance().getRainIntensity();
            if (float4 < 0.1) {
                float4 = 0.0F;
            }

            float0 = float4;
        } else if (!boolean0 || !ClimateManager.getInstance().isRaining()) {
            float float5 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.getParentChar());
            float float6 = 0.1F;
            if (float5 > 5.0F) {
                float6 += (float5 - 5.0F) / 10.0F;
            }

            float6 -= float2;
            if (float6 < 0.0F) {
                float6 = 0.0F;
            }

            float1 = float6;
        }

        if (clothingWetness != null) {
            clothingWetness.updateWetness(float0, float1);
        }

        float float7 = 0.0F;
        if (this.BodyParts.size() > 0) {
            for (int int0 = 0; int0 < this.BodyParts.size(); int0++) {
                float7 += this.BodyParts.get(int0).getWetness();
            }

            float7 /= this.BodyParts.size();
        }

        this.Wetness = PZMath.clamp(float7, 0.0F, 100.0F);
        float float8 = 0.0F;
        if (this.thermoregulator != null) {
            float8 = this.thermoregulator.getCatchAColdDelta();
        }

        if (!this.isHasACold() && float8 > 0.1F) {
            if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                float8 *= 1.7F;
            }

            if (this.getParentChar().Traits.Resilient.isSet()) {
                float8 *= 0.45F;
            }

            if (this.getParentChar().Traits.Outdoorsman.isSet()) {
                float8 *= 0.1F;
            }

            this.setCatchACold(this.getCatchACold() + (float)ZomboidGlobals.CatchAColdIncreaseRate * float8 * GameTime.instance.getMultiplier());
            if (this.getCatchACold() >= 100.0F) {
                this.setCatchACold(0.0F);
                this.setHasACold(true);
                this.setColdStrength(20.0F);
                this.setTimeToSneezeOrCough(0);
            }
        }

        if (float8 <= 0.1F) {
            this.setCatchACold(this.getCatchACold() - (float)ZomboidGlobals.CatchAColdDecreaseRate);
            if (this.getCatchACold() <= 0.0F) {
                this.setCatchACold(0.0F);
            }
        }
    }

    public void TriggerSneezeCough() {
        if (this.getSneezeCoughActive() <= 0) {
            if (Rand.Next(100) > 50) {
                this.setSneezeCoughActive(1);
            } else {
                this.setSneezeCoughActive(2);
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
                this.setSneezeCoughActive(1);
            }

            this.setSneezeCoughTime(this.getSneezeCoughDelay());
            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
                this.setTimeToSneezeOrCough(this.getMildColdSneezeTimerMin() + Rand.Next(this.getMildColdSneezeTimerMax() - this.getMildColdSneezeTimerMin()));
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 3) {
                this.setTimeToSneezeOrCough(this.getColdSneezeTimerMin() + Rand.Next(this.getColdSneezeTimerMax() - this.getColdSneezeTimerMin()));
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 4) {
                this.setTimeToSneezeOrCough(
                    this.getNastyColdSneezeTimerMin() + Rand.Next(this.getNastyColdSneezeTimerMax() - this.getNastyColdSneezeTimerMin())
                );
            }

            boolean boolean0 = false;
            if (this.getParentChar().getPrimaryHandItem() == null
                || !this.getParentChar().getPrimaryHandItem().getType().equals("Tissue")
                    && !this.getParentChar().getPrimaryHandItem().getType().equals("ToiletPaper")) {
                if (this.getParentChar().getSecondaryHandItem() != null
                    && (
                        this.getParentChar().getSecondaryHandItem().getType().equals("Tissue")
                            || this.getParentChar().getSecondaryHandItem().getType().equals("ToiletPaper")
                    )
                    && ((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() > 0.0F) {
                    ((Drainable)this.getParentChar().getSecondaryHandItem())
                        .setUsedDelta(((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() - 0.1F);
                    if (((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() <= 0.0F) {
                        this.getParentChar().getSecondaryHandItem().Use();
                    }

                    boolean0 = true;
                }
            } else if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() > 0.0F) {
                ((Drainable)this.getParentChar().getPrimaryHandItem())
                    .setUsedDelta(((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() - 0.1F);
                if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() <= 0.0F) {
                    this.getParentChar().getPrimaryHandItem().Use();
                }

                boolean0 = true;
            }

            if (boolean0) {
                this.setSneezeCoughActive(this.getSneezeCoughActive() + 2);
            } else {
                byte byte0 = 20;
                byte byte1 = 20;
                if (this.getSneezeCoughActive() == 1) {
                    byte0 = 20;
                    byte1 = 25;
                }

                if (this.getSneezeCoughActive() == 2) {
                    byte0 = 35;
                    byte1 = 40;
                }

                WorldSoundManager.instance
                    .addSound(
                        this.getParentChar(),
                        (int)this.getParentChar().getX(),
                        (int)this.getParentChar().getY(),
                        (int)this.getParentChar().getZ(),
                        byte0,
                        byte1,
                        true
                    );
            }
        }
    }

    public int IsSneezingCoughing() {
        return this.getSneezeCoughActive();
    }

    public void UpdateCold() {
        if (this.isHasACold()) {
            boolean boolean0 = true;
            IsoGridSquare square = this.getParentChar().getCurrentSquare();
            if (square == null
                || !square.isInARoom()
                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 0
                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1
                || this.getParentChar().getStats().fatigue > 0.5F
                || this.getParentChar().getStats().hunger > 0.25F
                || this.getParentChar().getStats().thirst > 0.25F) {
                boolean0 = false;
            }

            if (this.getColdReduction() > 0.0F) {
                boolean0 = true;
                this.setColdReduction(this.getColdReduction() - 0.005F * GameTime.instance.getMultiplier());
                if (this.getColdReduction() < 0.0F) {
                    this.setColdReduction(0.0F);
                }
            }

            if (boolean0) {
                float float0 = 1.0F;
                if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                    float0 = 0.5F;
                }

                if (this.getParentChar().Traits.Resilient.isSet()) {
                    float0 = 1.5F;
                }

                this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * float0 * GameTime.instance.getMultiplier());
                if (this.getColdReduction() > 0.0F) {
                    this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * float0 * GameTime.instance.getMultiplier());
                }

                if (this.getColdStrength() < 0.0F) {
                    this.setColdStrength(0.0F);
                    this.setHasACold(false);
                    this.setCatchACold(0.0F);
                }
            } else {
                float float1 = 1.0F;
                if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                    float1 = 1.2F;
                }

                if (this.getParentChar().Traits.Resilient.isSet()) {
                    float1 = 0.8F;
                }

                this.setColdStrength(this.getColdStrength() + this.getColdProgressionRate() * float1 * GameTime.instance.getMultiplier());
                if (this.getColdStrength() > 100.0F) {
                    this.setColdStrength(100.0F);
                }
            }

            if (this.getSneezeCoughTime() > 0) {
                this.setSneezeCoughTime(this.getSneezeCoughTime() - 1);
                if (this.getSneezeCoughTime() == 0) {
                    this.setSneezeCoughActive(0);
                }
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) > 1
                && this.getTimeToSneezeOrCough() >= 0
                && !this.ParentChar.IsSpeaking()) {
                this.setTimeToSneezeOrCough(this.getTimeToSneezeOrCough() - 1);
                if (this.getTimeToSneezeOrCough() <= 0) {
                    this.TriggerSneezeCough();
                }
            }
        }
    }

    public float getColdStrength() {
        return this.isHasACold() ? this.ColdStrength : 0.0F;
    }

    public float getWetness() {
        return this.Wetness;
    }

    public void AddDamage(BodyPartType BodyPart, float Val) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).AddDamage(Val);
    }

    public void AddGeneralHealth(float Val) {
        int int0 = 0;

        for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); int1++) {
            if (this.getBodyParts().get(int1).getHealth() < 100.0F) {
                int0++;
            }
        }

        if (int0 > 0) {
            float float0 = Val / int0;

            for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); int2++) {
                if (this.getBodyParts().get(int2).getHealth() < 100.0F) {
                    this.getBodyParts().get(int2).AddHealth(float0);
                }
            }
        }
    }

    public void ReduceGeneralHealth(float Val) {
        if (this.getOverallBodyHealth() <= 10.0F) {
            this.getParentChar().forceAwake();
        }

        if (!(Val <= 0.0F)) {
            float float0 = Val / BodyPartType.ToIndex(BodyPartType.MAX);

            for (int int0 = 0; int0 < BodyPartType.ToIndex(BodyPartType.MAX); int0++) {
                this.getBodyParts().get(int0).ReduceHealth(float0 / BodyPartType.getDamageModifyer(int0));
            }
        }
    }

    public void AddDamage(int BodyPartIndex, float val) {
        this.getBodyParts().get(BodyPartIndex).AddDamage(val);
    }

    public void splatBloodFloorBig() {
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
    }

    /**
     * When hit by another player in MP
     */
    public void DamageFromWeapon(HandWeapon weapon) {
        if (GameServer.bServer) {
            if (weapon != null) {
                this.getParentChar().sendObjectChange("DamageFromWeapon", new Object[]{"weapon", weapon.getFullType()});
            }
        } else if (!(this.getParentChar() instanceof IsoPlayer) || ((IsoPlayer)this.getParentChar()).isLocalPlayer()) {
            int int0 = 0;
            byte byte0 = 1;
            boolean boolean0 = true;
            int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
            if (DebugOptions.instance.MultiplayerTorsoHit.getValue()) {
                int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Head));
            }

            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = false;
            boolean boolean4 = true;
            boolean boolean5 = true;
            if (weapon.getCategories().contains("Blunt") || weapon.getCategories().contains("SmallBlunt")) {
                boolean5 = false;
                byte0 = 0;
                boolean1 = true;
            } else if (!weapon.isAimedFirearm()) {
                byte0 = 1;
                boolean2 = true;
            } else {
                boolean3 = true;
                byte0 = 2;
            }

            BodyPart bodyPart = this.getBodyPart(BodyPartType.FromIndex(int0));
            float float0 = this.getParentChar().getBodyPartClothingDefense(bodyPart.getIndex(), boolean2, boolean3);
            if (Rand.Next(100) < float0) {
                boolean0 = false;
                this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int0), false);
            }

            if (boolean0) {
                this.getParentChar().addHole(BloodBodyPartType.FromIndex(int0));
                this.getParentChar().splatBloodFloorBig();
                this.getParentChar().splatBloodFloorBig();
                this.getParentChar().splatBloodFloorBig();
                if (boolean2) {
                    if (Rand.NextBool(6)) {
                        bodyPart.generateDeepWound();
                    } else if (Rand.NextBool(3)) {
                        bodyPart.setCut(true);
                    } else {
                        bodyPart.setScratched(true, true);
                    }
                } else if (boolean1) {
                    if (Rand.NextBool(4)) {
                        bodyPart.setCut(true);
                    } else {
                        bodyPart.setScratched(true, true);
                    }
                } else if (boolean3) {
                    bodyPart.setHaveBullet(true, 0);
                }

                float float1 = Rand.Next(weapon.getMinDamage(), weapon.getMaxDamage()) * 15.0F;
                if (int0 == BodyPartType.ToIndex(BodyPartType.Head)) {
                    float1 *= 4.0F;
                }

                if (int0 == BodyPartType.ToIndex(BodyPartType.Neck)) {
                    float1 *= 4.0F;
                }

                if (int0 == BodyPartType.ToIndex(BodyPartType.Torso_Upper)) {
                    float1 *= 2.0F;
                }

                if (GameClient.bClient) {
                    if (weapon.isRanged()) {
                        float1 = (float)(float1 * ServerOptions.getInstance().PVPFirearmDamageModifier.getValue());
                    } else {
                        float1 = (float)(float1 * ServerOptions.getInstance().PVPMeleeDamageModifier.getValue());
                    }
                }

                this.AddDamage(int0, float1);
                switch (byte0) {
                    case 0:
                        Stats stats0 = this.ParentChar.getStats();
                        stats0.Pain = stats0.Pain + this.getInitialThumpPain() * BodyPartType.getPainModifyer(int0);
                        break;
                    case 1:
                        Stats stats1 = this.ParentChar.getStats();
                        stats1.Pain = stats1.Pain + this.getInitialScratchPain() * BodyPartType.getPainModifyer(int0);
                        break;
                    case 2:
                        Stats stats2 = this.ParentChar.getStats();
                        stats2.Pain = stats2.Pain + this.getInitialBitePain() * BodyPartType.getPainModifyer(int0);
                }

                if (this.getParentChar().getStats().Pain > 100.0F) {
                    this.ParentChar.getStats().Pain = 100.0F;
                }

                if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
                    IsoPlayer player = (IsoPlayer)this.ParentChar;
                    player.updateMovementRates();
                    GameClient.sendPlayerInjuries(player);
                    GameClient.sendPlayerDamage(player);
                }
            }
        }
    }

    /**
     * This gonna decide the strength of the damage you'll get.  Getting surrounded can also trigger an instant death animation.
     */
    public boolean AddRandomDamageFromZombie(IsoZombie zombie, String hitReaction) {
        if (StringUtils.isNullOrEmpty(hitReaction)) {
            hitReaction = "Bite";
        }

        this.getParentChar().setVariable("hitpvp", false);
        if (GameServer.bServer) {
            this.getParentChar().sendObjectChange("AddRandomDamageFromZombie", new Object[]{"zombie", zombie.OnlineID});
            return true;
        } else {
            byte byte0 = 0;
            int int0 = 0;
            int int1 = 15 + this.getParentChar().getMeleeCombatMod();
            int int2 = 85;
            int int3 = 65;
            String string = this.getParentChar().testDotSide(zombie);
            boolean boolean0 = string.equals(behindStr);
            boolean boolean1 = string.equals(leftStr) || string.equals(rightStr);
            int int4 = this.getParentChar().getSurroundingAttackingZombies();
            int4 = Math.max(int4, 1);
            int1 -= (int4 - 1) * 10;
            int2 -= (int4 - 1) * 30;
            int3 -= (int4 - 1) * 15;
            byte byte1 = 3;
            if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
                byte1 = 2;
            }

            if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
                byte1 = 6;
            }

            if (this.ParentChar.Traits.ThickSkinned.isSet()) {
                int1 = (int)(int1 * 1.3);
            }

            if (this.ParentChar.Traits.ThinSkinned.isSet()) {
                int1 = (int)(int1 / 1.3);
            }

            if (!"EndDeath".equals(this.getParentChar().getHitReaction())) {
                if (!this.getParentChar().isGodMod()
                    && int4 >= byte1
                    && SandboxOptions.instance.Lore.ZombiesDragDown.getValue()
                    && !this.getParentChar().isSitOnGround()) {
                    int2 = 0;
                    int3 = 0;
                    int1 = 0;
                    this.getParentChar().setHitReaction("EndDeath");
                    this.getParentChar().setDeathDragDown(true);
                } else {
                    this.getParentChar().setHitReaction(hitReaction);
                }
            }

            if (boolean0) {
                int1 -= 15;
                int2 -= 25;
                int3 -= 35;
                if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
                    int1 += 15;
                    int2 += 25;
                    int3 += 35;
                }

                if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
                    int1 += 7;
                    int2 += 17;
                    int3 += 23;
                }

                if (int4 > 2) {
                    int2 -= 15;
                    int3 -= 15;
                }
            }

            if (boolean1) {
                int1 -= 30;
                int2 -= 7;
                int3 -= 27;
                if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
                    int1 += 30;
                    int2 += 7;
                    int3 += 27;
                }

                if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
                    int1 += 15;
                    int2 += 4;
                    int3 += 15;
                }
            }

            if (!zombie.bCrawling) {
                if (Rand.Next(10) == 0) {
                    int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
                } else {
                    int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
                }

                float float0 = 10.0F * int4;
                if (boolean0) {
                    float0 += 5.0F;
                }

                if (boolean1) {
                    float0 += 2.0F;
                }

                if (boolean0 && Rand.Next(100) < float0) {
                    int0 = BodyPartType.ToIndex(BodyPartType.Neck);
                }

                if (int0 == BodyPartType.ToIndex(BodyPartType.Head) || int0 == BodyPartType.ToIndex(BodyPartType.Neck)) {
                    byte byte2 = 70;
                    if (boolean0) {
                        byte2 = 90;
                    }

                    if (boolean1) {
                        byte2 = 80;
                    }

                    if (Rand.Next(100) > byte2) {
                        boolean boolean2 = false;

                        while (!boolean2) {
                            boolean2 = true;
                            int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Lower) + 1);
                            if (int0 == BodyPartType.ToIndex(BodyPartType.Head)
                                || int0 == BodyPartType.ToIndex(BodyPartType.Neck)
                                || int0 == BodyPartType.ToIndex(BodyPartType.Groin)) {
                                boolean2 = false;
                            }
                        }
                    }
                }
            } else {
                if (Rand.Next(2) != 0) {
                    return false;
                }

                if (Rand.Next(10) == 0) {
                    int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Groin), BodyPartType.ToIndex(BodyPartType.MAX));
                } else {
                    int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.MAX));
                }
            }

            if (zombie.inactive) {
                int1 += 20;
                int2 += 20;
                int3 += 20;
            }

            float float1 = Rand.Next(1000) / 1000.0F;
            float1 *= Rand.Next(10) + 10;
            if (GameServer.bServer && this.ParentChar instanceof IsoPlayer || Core.bDebug && this.ParentChar instanceof IsoPlayer) {
                DebugLog.log(
                    DebugType.Combat,
                    "zombie did "
                        + float1
                        + " dmg to "
                        + ((IsoPlayer)this.ParentChar).getDisplayName()
                        + " on body part "
                        + BodyPartType.getDisplayName(BodyPartType.FromIndex(int0))
                );
            }

            boolean boolean3 = false;
            boolean boolean4 = true;
            if (Rand.Next(100) > int1) {
                zombie.scratch = true;
                this.getParentChar().helmetFall(int0 == BodyPartType.ToIndex(BodyPartType.Neck) || int0 == BodyPartType.ToIndex(BodyPartType.Head));
                if (Rand.Next(100) > int3) {
                    zombie.scratch = false;
                    zombie.laceration = true;
                }

                if (Rand.Next(100) > int2) {
                    zombie.scratch = false;
                    zombie.laceration = false;
                    boolean4 = false;
                }

                if (zombie.scratch) {
                    Float float2 = this.getParentChar().getBodyPartClothingDefense(int0, false, false);
                    zombie.parameterZombieState.setState(ParameterZombieState.State.AttackScratch);
                    if (this.getHealth() > 0.0F) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieScratch", (IsoObject)null);
                    }

                    if (Rand.Next(100) < float2) {
                        this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int0), boolean4);
                        return false;
                    }

                    boolean boolean5 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int0), true);
                    if (boolean5) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
                    }

                    boolean3 = true;
                    this.AddDamage(int0, float1);
                    this.SetScratched(int0, true);
                    this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int0), true, false, true);
                    byte0 = 1;
                    if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                        DebugLog.log(DebugType.Combat, "zombie scratched " + ((IsoPlayer)this.ParentChar).username);
                    }
                } else if (zombie.laceration) {
                    Float float3 = this.getParentChar().getBodyPartClothingDefense(int0, false, false);
                    zombie.parameterZombieState.setState(ParameterZombieState.State.AttackLacerate);
                    if (this.getHealth() > 0.0F) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieScratch", (IsoObject)null);
                    }

                    if (Rand.Next(100) < float3) {
                        this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int0), boolean4);
                        return false;
                    }

                    boolean boolean6 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int0), true);
                    if (boolean6) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
                    }

                    boolean3 = true;
                    this.AddDamage(int0, float1);
                    this.SetCut(int0, true);
                    this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int0), true, false, true);
                    byte0 = 1;
                    if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                        DebugLog.log(DebugType.Combat, "zombie laceration " + ((IsoPlayer)this.ParentChar).username);
                    }
                } else {
                    Float float4 = this.getParentChar().getBodyPartClothingDefense(int0, true, false);
                    zombie.parameterZombieState.setState(ParameterZombieState.State.AttackBite);
                    if (this.getHealth() > 0.0F) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieBite", (IsoObject)null);
                    }

                    if (Rand.Next(100) < float4) {
                        this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int0), boolean4);
                        return false;
                    }

                    boolean boolean7 = this.getParentChar().addHole(BloodBodyPartType.FromIndex(int0), true);
                    if (boolean7) {
                        this.getParentChar().getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
                    }

                    boolean3 = true;
                    this.AddDamage(int0, float1);
                    this.SetBitten(int0, true);
                    if (int0 == BodyPartType.ToIndex(BodyPartType.Neck)) {
                        this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int0), false, true, true);
                        this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int0), false, true, true);
                        this.getParentChar().addBlood(BloodBodyPartType.Torso_Upper, false, true, false);
                    }

                    this.getParentChar().addBlood(BloodBodyPartType.FromIndex(int0), false, true, true);
                    if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                        DebugLog.log(DebugType.Combat, "zombie bite " + ((IsoPlayer)this.ParentChar).username);
                    }

                    byte0 = 2;
                    this.getParentChar().splatBloodFloorBig();
                    this.getParentChar().splatBloodFloorBig();
                    this.getParentChar().splatBloodFloorBig();
                }
            }

            if (!boolean3) {
                this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(int0), boolean4);
            }

            switch (byte0) {
                case 0:
                    Stats stats0 = this.ParentChar.getStats();
                    stats0.Pain = stats0.Pain + this.getInitialThumpPain() * BodyPartType.getPainModifyer(int0);
                    break;
                case 1:
                    Stats stats1 = this.ParentChar.getStats();
                    stats1.Pain = stats1.Pain + this.getInitialScratchPain() * BodyPartType.getPainModifyer(int0);
                    break;
                case 2:
                    Stats stats2 = this.ParentChar.getStats();
                    stats2.Pain = stats2.Pain + this.getInitialBitePain() * BodyPartType.getPainModifyer(int0);
            }

            if (this.getParentChar().getStats().Pain > 100.0F) {
                this.ParentChar.getStats().Pain = 100.0F;
            }

            if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
                IsoPlayer player = (IsoPlayer)this.ParentChar;
                player.updateMovementRates();
                GameClient.sendPlayerInjuries(player);
                GameClient.sendPlayerDamage(player);
            }

            return true;
        }
    }

    public boolean doesBodyPartHaveInjury(BodyPartType part) {
        return this.getBodyParts().get(BodyPartType.ToIndex(part)).HasInjury();
    }

    /**
     * Returns TRUE if either body part is injured. ie. A OR B
     */
    public boolean doBodyPartsHaveInjuries(BodyPartType partA, BodyPartType partB) {
        return this.doesBodyPartHaveInjury(partA) || this.doesBodyPartHaveInjury(partB);
    }

    /**
     * Returns TRUE if the specified body part's bleeding time is greater than 0.
     */
    public boolean isBodyPartBleeding(BodyPartType part) {
        return this.getBodyPart(part).getBleedingTime() > 0.0F;
    }

    /**
     * Returns TRUE if either body part is bleeding. ie. A OR B
     */
    public boolean areBodyPartsBleeding(BodyPartType partA, BodyPartType partB) {
        return this.isBodyPartBleeding(partA) || this.isBodyPartBleeding(partB);
    }

    public void DrawUntexturedQuad(int X, int Y, int Width, int Height, float r, float g, float b, float a) {
        SpriteRenderer.instance.renderi(null, X, Y, Width, Height, r, g, b, a, null);
    }

    public float getBodyPartHealth(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).getHealth();
    }

    public float getBodyPartHealth(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).getHealth();
    }

    public String getBodyPartName(BodyPartType BodyPart) {
        return BodyPartType.ToString(BodyPart);
    }

    public String getBodyPartName(int BodyPartIndex) {
        return BodyPartType.ToString(BodyPartType.FromIndex(BodyPartIndex));
    }

    public float getHealth() {
        return this.getOverallBodyHealth();
    }

    public float getInfectionLevel() {
        return this.InfectionLevel;
    }

    public float getApparentInfectionLevel() {
        float float0 = this.getFakeInfectionLevel() > this.InfectionLevel ? this.getFakeInfectionLevel() : this.InfectionLevel;
        return this.getFoodSicknessLevel() > float0 ? this.getFoodSicknessLevel() : float0;
    }

    public int getNumPartsBleeding() {
        int int0 = 0;

        for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); int1++) {
            if (this.getBodyParts().get(int1).bleeding()) {
                int0++;
            }
        }

        return int0;
    }

    public int getNumPartsScratched() {
        int int0 = 0;

        for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); int1++) {
            if (this.getBodyParts().get(int1).scratched()) {
                int0++;
            }
        }

        return int0;
    }

    public int getNumPartsBitten() {
        int int0 = 0;

        for (int int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); int1++) {
            if (this.getBodyParts().get(int1).bitten()) {
                int0++;
            }
        }

        return int0;
    }

    public boolean HasInjury() {
        for (int int0 = 0; int0 < BodyPartType.ToIndex(BodyPartType.MAX); int0++) {
            if (this.getBodyParts().get(int0).HasInjury()) {
                return true;
            }
        }

        return false;
    }

    public boolean IsBandaged(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).bandaged();
    }

    public boolean IsDeepWounded(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).deepWounded();
    }

    public boolean IsBandaged(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).bandaged();
    }

    public boolean IsBitten(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).bitten();
    }

    public boolean IsBitten(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).bitten();
    }

    public boolean IsBleeding(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).bleeding();
    }

    public boolean IsBleeding(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).bleeding();
    }

    public boolean IsBleedingStemmed(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).IsBleedingStemmed();
    }

    public boolean IsBleedingStemmed(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).IsBleedingStemmed();
    }

    public boolean IsCortorised(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).IsCortorised();
    }

    public boolean IsCortorised(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).IsCortorised();
    }

    public boolean IsInfected() {
        return this.IsInfected;
    }

    public boolean IsInfected(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).IsInfected();
    }

    public boolean IsInfected(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).IsInfected();
    }

    public boolean IsFakeInfected(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).IsFakeInfected();
    }

    public void DisableFakeInfection(int BodyPartIndex) {
        this.getBodyParts().get(BodyPartIndex).DisableFakeInfection();
    }

    public boolean IsScratched(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).scratched();
    }

    public boolean IsCut(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).getCutTime() > 0.0F;
    }

    public boolean IsScratched(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).scratched();
    }

    public boolean IsStitched(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).stitched();
    }

    public boolean IsStitched(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).stitched();
    }

    public boolean IsWounded(BodyPartType BodyPart) {
        return this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).deepWounded();
    }

    public boolean IsWounded(int BodyPartIndex) {
        return this.getBodyParts().get(BodyPartIndex).deepWounded();
    }

    public void RestoreToFullHealth() {
        for (int int0 = 0; int0 < BodyPartType.ToIndex(BodyPartType.MAX); int0++) {
            this.getBodyParts().get(int0).RestoreToFullHealth();
        }

        if (this.getParentChar() != null && this.getParentChar().getStats() != null) {
            this.getParentChar().getStats().resetStats();
        }

        this.setInfected(false);
        this.setIsFakeInfected(false);
        this.setOverallBodyHealth(100.0F);
        this.setInfectionLevel(0.0F);
        this.setFakeInfectionLevel(0.0F);
        this.setBoredomLevel(0.0F);
        this.setWetness(0.0F);
        this.setCatchACold(0.0F);
        this.setHasACold(false);
        this.setColdStrength(0.0F);
        this.setSneezeCoughActive(0);
        this.setSneezeCoughTime(0);
        this.setTemperature(37.0F);
        this.setUnhappynessLevel(0.0F);
        this.PoisonLevel = 0.0F;
        this.setFoodSicknessLevel(0.0F);
        this.Temperature = 37.0F;
        this.lastTemperature = this.Temperature;
        this.setInfectionTime(-1.0F);
        this.setInfectionMortalityDuration(-1.0F);
        if (this.thermoregulator != null) {
            this.thermoregulator.reset();
        }
    }

    public void SetBandaged(int BodyPartIndex, boolean Bandaged, float bandageLife, boolean isAlcoholic, String bandageType) {
        this.getBodyParts().get(BodyPartIndex).setBandaged(Bandaged, bandageLife, isAlcoholic, bandageType);
    }

    public void SetBitten(BodyPartType BodyPart, boolean Bitten) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).SetBitten(Bitten);
    }

    public void SetBitten(int BodyPartIndex, boolean Bitten) {
        this.getBodyParts().get(BodyPartIndex).SetBitten(Bitten);
    }

    public void SetBitten(int BodyPartIndex, boolean Bitten, boolean Infected) {
        this.getBodyParts().get(BodyPartIndex).SetBitten(Bitten, Infected);
    }

    public void SetBleeding(BodyPartType BodyPart, boolean Bleeding) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).setBleeding(Bleeding);
    }

    public void SetBleeding(int BodyPartIndex, boolean Bleeding) {
        this.getBodyParts().get(BodyPartIndex).setBleeding(Bleeding);
    }

    public void SetBleedingStemmed(BodyPartType BodyPart, boolean BleedingStemmed) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).SetBleedingStemmed(BleedingStemmed);
    }

    public void SetBleedingStemmed(int BodyPartIndex, boolean BleedingStemmed) {
        this.getBodyParts().get(BodyPartIndex).SetBleedingStemmed(BleedingStemmed);
    }

    public void SetCortorised(BodyPartType BodyPart, boolean Cortorised) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).SetCortorised(Cortorised);
    }

    public void SetCortorised(int BodyPartIndex, boolean Cortorised) {
        this.getBodyParts().get(BodyPartIndex).SetCortorised(Cortorised);
    }

    public BodyPart setScratchedWindow() {
        int int0 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.ForeArm_R) + 1);
        this.getBodyPart(BodyPartType.FromIndex(int0)).AddDamage(10.0F);
        this.getBodyPart(BodyPartType.FromIndex(int0)).SetScratchedWindow(true);
        return this.getBodyPart(BodyPartType.FromIndex(int0));
    }

    public void SetScratched(BodyPartType BodyPart, boolean Scratched) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).setScratched(Scratched, false);
    }

    public void SetScratched(int BodyPartIndex, boolean Scratched) {
        this.getBodyParts().get(BodyPartIndex).setScratched(Scratched, false);
    }

    public void SetScratchedFromWeapon(int BodyPartIndex, boolean Scratched) {
        this.getBodyParts().get(BodyPartIndex).SetScratchedWeapon(Scratched);
    }

    public void SetCut(int BodyPartIndex, boolean Cut) {
        this.getBodyParts().get(BodyPartIndex).setCut(Cut, false);
    }

    public void SetWounded(BodyPartType BodyPart, boolean Wounded) {
        this.getBodyParts().get(BodyPartType.ToIndex(BodyPart)).setDeepWounded(Wounded);
    }

    public void SetWounded(int BodyPartIndex, boolean Wounded) {
        this.getBodyParts().get(BodyPartIndex).setDeepWounded(Wounded);
    }

    public void ShowDebugInfo() {
        if (this.getDamageModCount() > 0) {
            this.setDamageModCount(this.getDamageModCount() - 1);
        }
    }

    public void UpdateBoredom() {
        if (!(this.getParentChar() instanceof IsoSurvivor)) {
            if (!(this.getParentChar() instanceof IsoPlayer) || !((IsoPlayer)this.getParentChar()).Asleep) {
                if (this.getParentChar().getCurrentSquare().isInARoom()) {
                    if (!this.getParentChar().isReading()) {
                        this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * GameTime.instance.getMultiplier()));
                    } else {
                        this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * GameTime.instance.getMultiplier()));
                    }

                    if (this.getParentChar().IsSpeaking() && !this.getParentChar().callOut) {
                        this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * GameTime.instance.getMultiplier()));
                    }

                    if (this.getParentChar().getNumSurvivorsInVicinity() > 0) {
                        this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.1F * GameTime.instance.getMultiplier()));
                    }
                } else if (this.getParentChar().getVehicle() != null) {
                    float float0 = this.getParentChar().getVehicle().getCurrentSpeedKmHour();
                    if (Math.abs(float0) <= 0.1F) {
                        if (this.getParentChar().isReading()) {
                            this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * GameTime.instance.getMultiplier()));
                        } else {
                            this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * GameTime.instance.getMultiplier()));
                        }
                    } else {
                        this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.5 * GameTime.instance.getMultiplier()));
                    }
                } else {
                    this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.1F * GameTime.instance.getMultiplier()));
                }

                if (this.getParentChar().getStats().Drunkenness > 20.0F) {
                    this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 2.0 * GameTime.instance.getMultiplier()));
                }

                if (this.getParentChar().getStats().Panic > 5.0F) {
                    this.setBoredomLevel(0.0F);
                }

                if (this.getBoredomLevel() > 100.0F) {
                    this.setBoredomLevel(100.0F);
                }

                if (this.getBoredomLevel() < 0.0F) {
                    this.setBoredomLevel(0.0F);
                }

                if (this.getUnhappynessLevel() > 100.0F) {
                    this.setUnhappynessLevel(100.0F);
                }

                if (this.getUnhappynessLevel() < 0.0F) {
                    this.setUnhappynessLevel(0.0F);
                }

                if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored) > 1 && !this.getParentChar().isReading()) {
                    this.setUnhappynessLevel(
                        (float)(
                            this.getUnhappynessLevel()
                                + ZomboidGlobals.UnhappinessIncrease
                                    * this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored)
                                    * GameTime.instance.getMultiplier()
                        )
                    );
                }

                if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress) > 1 && !this.getParentChar().isReading()) {
                    this.setUnhappynessLevel(
                        (float)(
                            this.getUnhappynessLevel()
                                + ZomboidGlobals.UnhappinessIncrease
                                    / 2.0
                                    * this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress)
                                    * GameTime.instance.getMultiplier()
                        )
                    );
                }

                if (this.getParentChar().Traits.Smoker.isSet()) {
                    this.getParentChar().setTimeSinceLastSmoke(this.getParentChar().getTimeSinceLastSmoke() + 1.0E-4F * GameTime.instance.getMultiplier());
                    if (this.getParentChar().getTimeSinceLastSmoke() > 1.0F) {
                        double double0 = Math.floor(this.getParentChar().getTimeSinceLastSmoke() / 10.0F) + 1.0;
                        if (double0 > 10.0) {
                            double0 = 10.0;
                        }

                        this.getParentChar()
                            .getStats()
                            .setStressFromCigarettes(
                                (float)(
                                    this.getParentChar().getStats().getStressFromCigarettes()
                                        + ZomboidGlobals.StressFromBiteOrScratch / 8.0 * double0 * GameTime.instance.getMultiplier()
                                )
                            );
                    }
                }
            }
        }
    }

    public float getUnhappynessLevel() {
        return this.UnhappynessLevel;
    }

    public float getBoredomLevel() {
        return this.BoredomLevel;
    }

    public void UpdateStrength() {
        if (this.getParentChar() == this.getParentChar()) {
            int int0 = 0;
            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4) {
                int0 += 3;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 2) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 3) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 2) {
                int0++;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 3) {
                int0 += 2;
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 4) {
                int0 += 3;
            }

            this.getParentChar().setMaxWeight((int)(this.getParentChar().getMaxWeightBase() * this.getParentChar().getWeightMod()) - int0);
            if (this.getParentChar().getMaxWeight() < 0) {
                this.getParentChar().setMaxWeight(0);
            }

            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
                this.getParentChar().setMaxWeight(this.getParentChar().getMaxWeight() + 2);
            }

            if (this.getParentChar() instanceof IsoPlayer) {
                this.getParentChar().setMaxWeight((int)(this.getParentChar().getMaxWeight() * ((IsoPlayer)this.getParentChar()).getMaxWeightDelta()));
            }
        }
    }

    public float pickMortalityDuration() {
        float float0 = 1.0F;
        if (this.getParentChar().Traits.Resilient.isSet()) {
            float0 = 1.25F;
        }

        if (this.getParentChar().Traits.ProneToIllness.isSet()) {
            float0 = 0.75F;
        }

        switch (SandboxOptions.instance.Lore.Mortality.getValue()) {
            case 1:
                return 0.0F;
            case 2:
                return Rand.Next(0.0F, 30.0F) / 3600.0F * float0;
            case 3:
                return Rand.Next(0.5F, 1.0F) / 60.0F * float0;
            case 4:
                return Rand.Next(3.0F, 12.0F) * float0;
            case 5:
                return Rand.Next(2.0F, 3.0F) * 24.0F * float0;
            case 6:
                return Rand.Next(1.0F, 2.0F) * 7.0F * 24.0F * float0;
            case 7:
                return -1.0F;
            default:
                return -1.0F;
        }
    }

    public void Update() {
        if (!(this.getParentChar() instanceof IsoZombie)) {
            if (GameServer.bServer) {
                this.RestoreToFullHealth();
                byte byte0 = ((IsoPlayer)this.getParentChar()).bleedingLevel;
                if (byte0 > 0) {
                    float float0 = 1.0F / byte0 * 200.0F * GameTime.instance.getInvMultiplier();
                    if (Rand.Next((int)float0) < float0 * 0.3F) {
                        this.getParentChar().splatBloodFloor();
                    }

                    if (Rand.Next((int)float0) == 0) {
                        this.getParentChar().splatBloodFloor();
                    }
                }
            } else if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && ((IsoPlayer)this.getParentChar()).bRemote) {
                if (this.getParentChar().isAlive()) {
                    this.RestoreToFullHealth();
                    byte byte1 = ((IsoPlayer)this.getParentChar()).bleedingLevel;
                    if (byte1 > 0) {
                        float float1 = 1.0F / byte1 * 200.0F * GameTime.instance.getInvMultiplier();
                        if (Rand.Next((int)float1) < float1 * 0.3F) {
                            this.getParentChar().splatBloodFloor();
                        }

                        if (Rand.Next((int)float1) == 0) {
                            this.getParentChar().splatBloodFloor();
                        }
                    }
                }
            } else if (this.getParentChar().isGodMod()) {
                this.RestoreToFullHealth();
                ((IsoPlayer)this.getParentChar()).bleedingLevel = 0;
            } else if (this.getParentChar().isInvincible()) {
                this.setOverallBodyHealth(100.0F);

                for (int int0 = 0; int0 < BodyPartType.MAX.index(); int0++) {
                    this.getBodyPart(BodyPartType.FromIndex(int0)).SetHealth(100.0F);
                }
            } else {
                float float2 = this.ParentChar.getStats().Pain;
                int int1 = this.getNumPartsBleeding() * 2;
                int1 += this.getNumPartsScratched();
                int1 += this.getNumPartsBitten() * 6;
                if (this.getHealth() >= 60.0F && int1 <= 3) {
                    int1 = 0;
                }

                ((IsoPlayer)this.getParentChar()).bleedingLevel = (byte)int1;
                if (int1 > 0) {
                    float float3 = 1.0F / int1 * 200.0F * GameTime.instance.getInvMultiplier();
                    if (Rand.Next((int)float3) < float3 * 0.3F) {
                        this.getParentChar().splatBloodFloor();
                    }

                    if (Rand.Next((int)float3) == 0) {
                        this.getParentChar().splatBloodFloor();
                    }
                }

                if (this.thermoregulator != null) {
                    this.thermoregulator.update();
                }

                this.UpdateWetness();
                this.UpdateCold();
                this.UpdateBoredom();
                this.UpdateStrength();
                this.UpdatePanicState();
                this.UpdateTemperatureState();
                this.UpdateIllness();
                if (this.getOverallBodyHealth() != 0.0F) {
                    if (this.PoisonLevel == 0.0F && this.getFoodSicknessLevel() > 0.0F) {
                        this.setFoodSicknessLevel(
                            this.getFoodSicknessLevel() - (float)(ZomboidGlobals.FoodSicknessDecrease * GameTime.instance.getMultiplier())
                        );
                    }

                    if (!this.isInfected()) {
                        for (int int2 = 0; int2 < BodyPartType.ToIndex(BodyPartType.MAX); int2++) {
                            if (this.IsInfected(int2)) {
                                this.setInfected(true);
                                if (this.IsFakeInfected(int2)) {
                                    this.DisableFakeInfection(int2);
                                    this.setInfectionLevel(this.getFakeInfectionLevel());
                                    this.setFakeInfectionLevel(0.0F);
                                    this.setIsFakeInfected(false);
                                    this.setReduceFakeInfection(false);
                                }
                            }
                        }

                        if (this.isInfected() && this.getInfectionTime() < 0.0F && SandboxOptions.instance.Lore.Mortality.getValue() != 7) {
                            this.setInfectionTime(this.getCurrentTimeForInfection());
                            this.setInfectionMortalityDuration(this.pickMortalityDuration());
                        }
                    }

                    if (!this.isInfected() && !this.isIsFakeInfected()) {
                        for (int int3 = 0; int3 < BodyPartType.ToIndex(BodyPartType.MAX); int3++) {
                            if (this.IsFakeInfected(int3)) {
                                this.setIsFakeInfected(true);
                                break;
                            }
                        }
                    }

                    if (this.isIsFakeInfected() && !this.isReduceFakeInfection() && this.getParentChar().getReduceInfectionPower() == 0.0F) {
                        this.setFakeInfectionLevel(this.getFakeInfectionLevel() + this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
                        if (this.getFakeInfectionLevel() > 100.0F) {
                            this.setFakeInfectionLevel(100.0F);
                            this.setReduceFakeInfection(true);
                        }
                    }

                    Stats stats = this.ParentChar.getStats();
                    stats.Drunkenness = stats.Drunkenness - this.getDrunkReductionValue() * GameTime.instance.getMultiplier();
                    if (this.getParentChar().getStats().Drunkenness < 0.0F) {
                        this.ParentChar.getStats().Drunkenness = 0.0F;
                    }

                    float float4 = 0.0F;
                    if (this.getHealthFromFoodTimer() > 0.0F) {
                        float4 += this.getHealthFromFood() * GameTime.instance.getMultiplier();
                        this.setHealthFromFoodTimer(this.getHealthFromFoodTimer() - 1.0F * GameTime.instance.getMultiplier());
                    }

                    byte byte2 = 0;
                    if (this.getParentChar() == this.getParentChar()
                        && (
                            this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2
                                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2
                                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2
                        )) {
                        byte2 = 1;
                    }

                    if (this.getParentChar() == this.getParentChar()
                        && (
                            this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3
                                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3
                                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3
                        )) {
                        byte2 = 2;
                    }

                    if (this.getParentChar() == this.getParentChar()
                        && (
                            this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4
                                || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4
                        )) {
                        byte2 = 3;
                    }

                    if (this.getParentChar().isAsleep()) {
                        byte2 = -1;
                    }

                    switch (byte2) {
                        case 0:
                            float4 += this.getStandardHealthAddition() * GameTime.instance.getMultiplier();
                            break;
                        case 1:
                            float4 += this.getReducedHealthAddition() * GameTime.instance.getMultiplier();
                            break;
                        case 2:
                            float4 += this.getSeverlyReducedHealthAddition() * GameTime.instance.getMultiplier();
                            break;
                        case 3:
                            float4 += 0.0F;
                    }

                    if (this.getParentChar().isAsleep()) {
                        if (GameClient.bClient) {
                            float4 += 15.0F * GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 3600.0F;
                        } else {
                            float4 += this.getSleepingHealthAddition() * GameTime.instance.getMultiplier();
                        }

                        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4
                            || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                            float4 = 0.0F;
                        }
                    }

                    this.AddGeneralHealth(float4);
                    float4 = 0.0F;
                    float float5 = 0.0F;
                    float float6 = 0.0F;
                    float float7 = 0.0F;
                    float float8 = 0.0F;
                    float float9 = 0.0F;
                    float float10 = 0.0F;
                    if (this.PoisonLevel > 0.0F) {
                        if (this.PoisonLevel > 10.0F && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) >= 1) {
                            float5 = 0.0035F * Math.min(this.PoisonLevel / 10.0F, 3.0F) * GameTime.instance.getMultiplier();
                            float4 += float5;
                        }

                        float float11 = 0.0F;
                        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
                            float11 = 1.5E-4F * this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten);
                        }

                        this.PoisonLevel = (float)(this.PoisonLevel - (float11 + ZomboidGlobals.PoisonLevelDecrease * GameTime.instance.getMultiplier()));
                        if (this.PoisonLevel < 0.0F) {
                            this.PoisonLevel = 0.0F;
                        }

                        this.setFoodSicknessLevel(
                            this.getFoodSicknessLevel()
                                + this.getInfectionGrowthRate() * (2 + Math.round(this.PoisonLevel / 10.0F)) * GameTime.instance.getMultiplier()
                        );
                        if (this.getFoodSicknessLevel() > 100.0F) {
                            this.setFoodSicknessLevel(100.0F);
                        }
                    }

                    if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
                        float6 = this.getHealthReductionFromSevereBadMoodles() / 50.0F * GameTime.instance.getMultiplier();
                        float4 += float6;
                    }

                    if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4 && this.FoodSicknessLevel > this.InfectionLevel) {
                        float7 = this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
                        float4 += float7;
                    }

                    if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
                        float8 = this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
                        float4 += float8;
                    }

                    if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                        float9 = this.getHealthReductionFromSevereBadMoodles() / 10.0F * GameTime.instance.getMultiplier();
                        float4 += float9;
                    }

                    if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 2
                        && this.getParentChar().getVehicle() == null
                        && !this.getParentChar().isAsleep()
                        && !this.getParentChar().isSitOnGround()
                        && this.getThermoregulator().getMetabolicTarget() != Metabolics.SeatedResting.getMet()
                        && this.getHealth() > 75.0F
                        && Rand.Next(Rand.AdjustForFramerate(10)) == 0) {
                        float10 = this.getHealthReductionFromSevereBadMoodles()
                            / ((5 - this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad)) / 10.0F)
                            * GameTime.instance.getMultiplier();
                        float4 += float10;
                    }

                    this.ReduceGeneralHealth(float4);
                    IsoGameCharacter character = this.getParentChar();
                    if (float5 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "POISON", float5);
                    }

                    if (float6 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "HUNGRY", float6);
                    }

                    if (float7 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "SICK", float7);
                    }

                    if (float8 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "BLEEDING", float8);
                    }

                    if (float9 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "THIRST", float9);
                    }

                    if (float10 > 0.0F) {
                        LuaEventManager.triggerEvent("OnPlayerGetDamage", character, "HEAVYLOAD", float10);
                    }

                    if (this.ParentChar.getPainEffect() > 0.0F) {
                        stats = this.ParentChar.getStats();
                        stats.Pain = stats.Pain - 0.023333333F * (GameTime.getInstance().getMultiplier() / 1.6F);
                        this.ParentChar.setPainEffect(this.ParentChar.getPainEffect() - GameTime.getInstance().getMultiplier() / 1.6F);
                    } else {
                        this.ParentChar.setPainDelta(0.0F);
                        float4 = 0.0F;

                        for (int int4 = 0; int4 < BodyPartType.ToIndex(BodyPartType.MAX); int4++) {
                            float4 += this.getBodyParts().get(int4).getPain() * BodyPartType.getPainModifyer(int4);
                        }

                        float4 -= this.getPainReduction();
                        if (float4 > this.ParentChar.getStats().Pain) {
                            stats = this.ParentChar.getStats();
                            stats.Pain = stats.Pain + (float4 - this.ParentChar.getStats().Pain) / 500.0F;
                        } else {
                            this.ParentChar.getStats().Pain = float4;
                        }
                    }

                    this.setPainReduction(this.getPainReduction() - 0.005F * GameTime.getInstance().getMultiplier());
                    if (this.getPainReduction() < 0.0F) {
                        this.setPainReduction(0.0F);
                    }

                    if (this.getParentChar().getStats().Pain > 100.0F) {
                        this.ParentChar.getStats().Pain = 100.0F;
                    }

                    if (this.isInfected()) {
                        int int5 = SandboxOptions.instance.Lore.Mortality.getValue();
                        if (int5 == 1) {
                            this.ReduceGeneralHealth(110.0F);
                            LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", 110);
                            this.setInfectionLevel(100.0F);
                        } else if (int5 != 7) {
                            float float12 = this.getCurrentTimeForInfection();
                            if (this.InfectionMortalityDuration < 0.0F) {
                                this.InfectionMortalityDuration = this.pickMortalityDuration();
                            }

                            if (this.InfectionTime < 0.0F) {
                                this.InfectionTime = float12;
                            }

                            if (this.InfectionTime > float12) {
                                this.InfectionTime = float12;
                            }

                            float6 = (float12 - this.InfectionTime) / this.InfectionMortalityDuration;
                            float6 = Math.min(float6, 1.0F);
                            this.setInfectionLevel(float6 * 100.0F);
                            if (float6 == 1.0F) {
                                this.ReduceGeneralHealth(110.0F);
                                LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", 110);
                            } else {
                                float6 *= float6;
                                float6 *= float6;
                                float7 = (1.0F - float6) * 100.0F;
                                float8 = this.getOverallBodyHealth() - float7;
                                if (float8 > 0.0F && float7 <= 99.0F) {
                                    this.ReduceGeneralHealth(float8);
                                    LuaEventManager.triggerEvent("OnPlayerGetDamage", this.ParentChar, "INFECTION", float8);
                                }
                            }
                        }
                    }

                    for (int int6 = 0; int6 < BodyPartType.ToIndex(BodyPartType.MAX); int6++) {
                        this.getBodyParts().get(int6).DamageUpdate();
                    }

                    this.calculateOverallHealth();
                    if (this.getOverallBodyHealth() <= 0.0F) {
                        if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && !((IsoPlayer)this.getParentChar()).bRemote) {
                            GameClient.sendPlayerDamage((IsoPlayer)this.getParentChar());
                        }

                        if (this.isIsOnFire()) {
                            this.setBurntToDeath(true);

                            for (int int7 = 0; int7 < BodyPartType.ToIndex(BodyPartType.MAX); int7++) {
                                this.getBodyParts().get(int7).SetHealth(Rand.Next(90));
                            }
                        } else {
                            this.setBurntToDeath(false);
                        }
                    }

                    if (this.isReduceFakeInfection() && this.getOverallBodyHealth() > 0.0F) {
                        this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier() * 2.0F);
                    }

                    if (this.getParentChar().getReduceInfectionPower() > 0.0F && this.getOverallBodyHealth() > 0.0F) {
                        this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
                        this.getParentChar()
                            .setReduceInfectionPower(
                                this.getParentChar().getReduceInfectionPower() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier()
                            );
                        if (this.getParentChar().getReduceInfectionPower() < 0.0F) {
                            this.getParentChar().setReduceInfectionPower(0.0F);
                        }
                    }

                    if (this.getFakeInfectionLevel() <= 0.0F) {
                        for (int int8 = 0; int8 < BodyPartType.ToIndex(BodyPartType.MAX); int8++) {
                            this.getBodyParts().get(int8).SetFakeInfected(false);
                        }

                        this.setIsFakeInfected(false);
                        this.setFakeInfectionLevel(0.0F);
                        this.setReduceFakeInfection(false);
                    }

                    if (float2 == this.ParentChar.getStats().Pain) {
                        stats = this.ParentChar.getStats();
                        stats.Pain = (float)(stats.Pain - 0.25 * (GameTime.getInstance().getMultiplier() / 1.6F));
                    }

                    if (this.ParentChar.getStats().Pain < 0.0F) {
                        this.ParentChar.getStats().Pain = 0.0F;
                    }
                }
            }
        }
    }

    private void calculateOverallHealth() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < BodyPartType.ToIndex(BodyPartType.MAX); int0++) {
            BodyPart bodyPart = this.getBodyParts().get(int0);
            float0 += (100.0F - bodyPart.getHealth()) * BodyPartType.getDamageModifyer(int0);
        }

        float0 += this.getDamageFromPills();
        if (float0 > 100.0F) {
            float0 = 100.0F;
        }

        this.setOverallBodyHealth(100.0F - float0);
    }

    public static float getSicknessFromCorpsesRate(int corpseCount) {
        if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
            return 0.0F;
        } else if (corpseCount > 5) {
            float float0 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.07F;
            switch (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue()) {
                case 2:
                    float0 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.01F;
                    break;
                case 4:
                    float0 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.11F;
            }

            int int0 = Math.min(corpseCount - 5, 20);
            return float0 * int0;
        } else {
            return 0.0F;
        }
    }

    private void UpdateIllness() {
        if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() != 1) {
            int int0 = FliesSound.instance.getCorpseCount(this.getParentChar());
            float float0 = getSicknessFromCorpsesRate(int0);
            if (float0 > 0.0F) {
                this.setFoodSicknessLevel(this.getFoodSicknessLevel() + float0 * GameTime.getInstance().getMultiplier());
            }
        }
    }

    private void UpdateTemperatureState() {
        float float0 = 0.06F;
        if (this.getParentChar() instanceof IsoPlayer) {
            if (this.ColdDamageStage > 0.0F) {
                float float1 = 100.0F - this.ColdDamageStage * 100.0F;
                if (this.OverallBodyHealth > float1) {
                    this.ReduceGeneralHealth(this.OverallBodyHealth - float1);
                }
            }

            ((IsoPlayer)this.getParentChar()).setMoveSpeed(float0);
        }
    }

    private float getDamageFromPills() {
        if (this.getParentChar() instanceof IsoPlayer) {
            IsoPlayer player = (IsoPlayer)this.getParentChar();
            if (player.getSleepingPillsTaken() == 10) {
                return 40.0F;
            }

            if (player.getSleepingPillsTaken() == 11) {
                return 80.0F;
            }

            if (player.getSleepingPillsTaken() >= 12) {
                return 100.0F;
            }
        }

        return 0.0F;
    }

    public boolean UseBandageOnMostNeededPart() {
        byte byte0 = 0;
        BodyPart bodyPart = null;

        for (int int0 = 0; int0 < this.getBodyParts().size(); int0++) {
            byte byte1 = 0;
            if (!this.getBodyParts().get(int0).bandaged()) {
                if (this.getBodyParts().get(int0).bleeding()) {
                    byte1 += 100;
                }

                if (this.getBodyParts().get(int0).scratched()) {
                    byte1 += 50;
                }

                if (this.getBodyParts().get(int0).bitten()) {
                    byte1 += 50;
                }

                if (byte1 > byte0) {
                    byte0 = byte1;
                    bodyPart = this.getBodyParts().get(int0);
                }
            }
        }

        if (byte0 > 0 && bodyPart != null) {
            bodyPart.setBandaged(true, 10.0F);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the BodyParts
     */
    public ArrayList<BodyPart> getBodyParts() {
        return this.BodyParts;
    }

    /**
     * @return the DamageModCount
     */
    public int getDamageModCount() {
        return this.DamageModCount;
    }

    /**
     * 
     * @param _DamageModCount the DamageModCount to set
     */
    public void setDamageModCount(int _DamageModCount) {
        this.DamageModCount = _DamageModCount;
    }

    /**
     * @return the InfectionGrowthRate
     */
    public float getInfectionGrowthRate() {
        return this.InfectionGrowthRate;
    }

    /**
     * 
     * @param _InfectionGrowthRate the InfectionGrowthRate to set
     */
    public void setInfectionGrowthRate(float _InfectionGrowthRate) {
        this.InfectionGrowthRate = _InfectionGrowthRate;
    }

    /**
     * 
     * @param _InfectionLevel the InfectionLevel to set
     */
    public void setInfectionLevel(float _InfectionLevel) {
        this.InfectionLevel = _InfectionLevel;
    }

    public boolean isInfected() {
        return this.IsInfected;
    }

    public void setInfected(boolean infected) {
        this.IsInfected = infected;
    }

    public float getInfectionTime() {
        return this.InfectionTime;
    }

    public void setInfectionTime(float worldHours) {
        this.InfectionTime = worldHours;
    }

    public float getInfectionMortalityDuration() {
        return this.InfectionMortalityDuration;
    }

    public void setInfectionMortalityDuration(float worldHours) {
        this.InfectionMortalityDuration = worldHours;
    }

    private float getCurrentTimeForInfection() {
        return this.getParentChar() instanceof IsoPlayer
            ? (float)((IsoPlayer)this.getParentChar()).getHoursSurvived()
            : (float)GameTime.getInstance().getWorldAgeHours();
    }

    /**
     * @return the inf
     */
    @Deprecated
    public boolean isInf() {
        return this.IsInfected;
    }

    /**
     * 
     * @param inf the inf to set
     */
    @Deprecated
    public void setInf(boolean inf) {
        this.IsInfected = inf;
    }

    /**
     * @return the FakeInfectionLevel
     */
    public float getFakeInfectionLevel() {
        return this.FakeInfectionLevel;
    }

    /**
     * 
     * @param _FakeInfectionLevel the FakeInfectionLevel to set
     */
    public void setFakeInfectionLevel(float _FakeInfectionLevel) {
        this.FakeInfectionLevel = _FakeInfectionLevel;
    }

    /**
     * @return the IsFakeInfected
     */
    public boolean isIsFakeInfected() {
        return this.IsFakeInfected;
    }

    /**
     * 
     * @param _IsFakeInfected the IsFakeInfected to set
     */
    public void setIsFakeInfected(boolean _IsFakeInfected) {
        this.IsFakeInfected = _IsFakeInfected;
        this.getBodyParts().get(0).SetFakeInfected(_IsFakeInfected);
    }

    /**
     * @return the OverallBodyHealth
     */
    public float getOverallBodyHealth() {
        return this.OverallBodyHealth;
    }

    /**
     * 
     * @param _OverallBodyHealth the OverallBodyHealth to set
     */
    public void setOverallBodyHealth(float _OverallBodyHealth) {
        this.OverallBodyHealth = _OverallBodyHealth;
    }

    /**
     * @return the StandardHealthAddition
     */
    public float getStandardHealthAddition() {
        return this.StandardHealthAddition;
    }

    /**
     * 
     * @param _StandardHealthAddition the StandardHealthAddition to set
     */
    public void setStandardHealthAddition(float _StandardHealthAddition) {
        this.StandardHealthAddition = _StandardHealthAddition;
    }

    /**
     * @return the ReducedHealthAddition
     */
    public float getReducedHealthAddition() {
        return this.ReducedHealthAddition;
    }

    /**
     * 
     * @param _ReducedHealthAddition the ReducedHealthAddition to set
     */
    public void setReducedHealthAddition(float _ReducedHealthAddition) {
        this.ReducedHealthAddition = _ReducedHealthAddition;
    }

    /**
     * @return the SeverlyReducedHealthAddition
     */
    public float getSeverlyReducedHealthAddition() {
        return this.SeverlyReducedHealthAddition;
    }

    /**
     * 
     * @param _SeverlyReducedHealthAddition the SeverlyReducedHealthAddition to set
     */
    public void setSeverlyReducedHealthAddition(float _SeverlyReducedHealthAddition) {
        this.SeverlyReducedHealthAddition = _SeverlyReducedHealthAddition;
    }

    /**
     * @return the SleepingHealthAddition
     */
    public float getSleepingHealthAddition() {
        return this.SleepingHealthAddition;
    }

    /**
     * 
     * @param _SleepingHealthAddition the SleepingHealthAddition to set
     */
    public void setSleepingHealthAddition(float _SleepingHealthAddition) {
        this.SleepingHealthAddition = _SleepingHealthAddition;
    }

    /**
     * @return the HealthFromFood
     */
    public float getHealthFromFood() {
        return this.HealthFromFood;
    }

    /**
     * 
     * @param _HealthFromFood the HealthFromFood to set
     */
    public void setHealthFromFood(float _HealthFromFood) {
        this.HealthFromFood = _HealthFromFood;
    }

    /**
     * @return the HealthReductionFromSevereBadMoodles
     */
    public float getHealthReductionFromSevereBadMoodles() {
        return this.HealthReductionFromSevereBadMoodles;
    }

    /**
     * 
     * @param _HealthReductionFromSevereBadMoodles the HealthReductionFromSevereBadMoodles to set
     */
    public void setHealthReductionFromSevereBadMoodles(float _HealthReductionFromSevereBadMoodles) {
        this.HealthReductionFromSevereBadMoodles = _HealthReductionFromSevereBadMoodles;
    }

    /**
     * @return the StandardHealthFromFoodTime
     */
    public int getStandardHealthFromFoodTime() {
        return this.StandardHealthFromFoodTime;
    }

    /**
     * 
     * @param _StandardHealthFromFoodTime the StandardHealthFromFoodTime to set
     */
    public void setStandardHealthFromFoodTime(int _StandardHealthFromFoodTime) {
        this.StandardHealthFromFoodTime = _StandardHealthFromFoodTime;
    }

    /**
     * @return the HealthFromFoodTimer
     */
    public float getHealthFromFoodTimer() {
        return this.HealthFromFoodTimer;
    }

    /**
     * 
     * @param _HealthFromFoodTimer the HealthFromFoodTimer to set
     */
    public void setHealthFromFoodTimer(float _HealthFromFoodTimer) {
        this.HealthFromFoodTimer = _HealthFromFoodTimer;
    }

    /**
     * 
     * @param _BoredomLevel the BoredomLevel to set
     */
    public void setBoredomLevel(float _BoredomLevel) {
        this.BoredomLevel = _BoredomLevel;
    }

    /**
     * @return the BoredomDecreaseFromReading
     */
    public float getBoredomDecreaseFromReading() {
        return this.BoredomDecreaseFromReading;
    }

    /**
     * 
     * @param _BoredomDecreaseFromReading the BoredomDecreaseFromReading to set
     */
    public void setBoredomDecreaseFromReading(float _BoredomDecreaseFromReading) {
        this.BoredomDecreaseFromReading = _BoredomDecreaseFromReading;
    }

    /**
     * @return the InitialThumpPain
     */
    public float getInitialThumpPain() {
        return this.InitialThumpPain;
    }

    /**
     * 
     * @param _InitialThumpPain the InitialThumpPain to set
     */
    public void setInitialThumpPain(float _InitialThumpPain) {
        this.InitialThumpPain = _InitialThumpPain;
    }

    /**
     * @return the InitialScratchPain
     */
    public float getInitialScratchPain() {
        return this.InitialScratchPain;
    }

    /**
     * 
     * @param _InitialScratchPain the InitialScratchPain to set
     */
    public void setInitialScratchPain(float _InitialScratchPain) {
        this.InitialScratchPain = _InitialScratchPain;
    }

    /**
     * @return the InitialBitePain
     */
    public float getInitialBitePain() {
        return this.InitialBitePain;
    }

    /**
     * 
     * @param _InitialBitePain the InitialBitePain to set
     */
    public void setInitialBitePain(float _InitialBitePain) {
        this.InitialBitePain = _InitialBitePain;
    }

    /**
     * @return the InitialWoundPain
     */
    public float getInitialWoundPain() {
        return this.InitialWoundPain;
    }

    /**
     * 
     * @param _InitialWoundPain the InitialWoundPain to set
     */
    public void setInitialWoundPain(float _InitialWoundPain) {
        this.InitialWoundPain = _InitialWoundPain;
    }

    /**
     * @return the ContinualPainIncrease
     */
    public float getContinualPainIncrease() {
        return this.ContinualPainIncrease;
    }

    /**
     * 
     * @param _ContinualPainIncrease the ContinualPainIncrease to set
     */
    public void setContinualPainIncrease(float _ContinualPainIncrease) {
        this.ContinualPainIncrease = _ContinualPainIncrease;
    }

    /**
     * @return the PainReductionFromMeds
     */
    public float getPainReductionFromMeds() {
        return this.PainReductionFromMeds;
    }

    /**
     * 
     * @param _PainReductionFromMeds the PainReductionFromMeds to set
     */
    public void setPainReductionFromMeds(float _PainReductionFromMeds) {
        this.PainReductionFromMeds = _PainReductionFromMeds;
    }

    /**
     * @return the StandardPainReductionWhenWell
     */
    public float getStandardPainReductionWhenWell() {
        return this.StandardPainReductionWhenWell;
    }

    /**
     * 
     * @param _StandardPainReductionWhenWell the StandardPainReductionWhenWell to set
     */
    public void setStandardPainReductionWhenWell(float _StandardPainReductionWhenWell) {
        this.StandardPainReductionWhenWell = _StandardPainReductionWhenWell;
    }

    /**
     * @return the OldNumZombiesVisible
     */
    public int getOldNumZombiesVisible() {
        return this.OldNumZombiesVisible;
    }

    /**
     * 
     * @param _OldNumZombiesVisible the OldNumZombiesVisible to set
     */
    public void setOldNumZombiesVisible(int _OldNumZombiesVisible) {
        this.OldNumZombiesVisible = _OldNumZombiesVisible;
    }

    /**
     * @return the CurrentNumZombiesVisible
     */
    public int getCurrentNumZombiesVisible() {
        return this.CurrentNumZombiesVisible;
    }

    /**
     * 
     * @param _CurrentNumZombiesVisible the CurrentNumZombiesVisible to set
     */
    public void setCurrentNumZombiesVisible(int _CurrentNumZombiesVisible) {
        this.CurrentNumZombiesVisible = _CurrentNumZombiesVisible;
    }

    /**
     * @return the PanicIncreaseValue
     */
    public float getPanicIncreaseValue() {
        return this.PanicIncreaseValue;
    }

    public float getPanicIncreaseValueFrame() {
        return this.PanicIncreaseValueFrame;
    }

    /**
     * 
     * @param _PanicIncreaseValue the PanicIncreaseValue to set
     */
    public void setPanicIncreaseValue(float _PanicIncreaseValue) {
        this.PanicIncreaseValue = _PanicIncreaseValue;
    }

    /**
     * @return the PanicReductionValue
     */
    public float getPanicReductionValue() {
        return this.PanicReductionValue;
    }

    /**
     * 
     * @param _PanicReductionValue the PanicReductionValue to set
     */
    public void setPanicReductionValue(float _PanicReductionValue) {
        this.PanicReductionValue = _PanicReductionValue;
    }

    /**
     * @return the DrunkIncreaseValue
     */
    public float getDrunkIncreaseValue() {
        return this.DrunkIncreaseValue;
    }

    /**
     * 
     * @param _DrunkIncreaseValue the DrunkIncreaseValue to set
     */
    public void setDrunkIncreaseValue(float _DrunkIncreaseValue) {
        this.DrunkIncreaseValue = _DrunkIncreaseValue;
    }

    /**
     * @return the DrunkReductionValue
     */
    public float getDrunkReductionValue() {
        return this.DrunkReductionValue;
    }

    /**
     * 
     * @param _DrunkReductionValue the DrunkReductionValue to set
     */
    public void setDrunkReductionValue(float _DrunkReductionValue) {
        this.DrunkReductionValue = _DrunkReductionValue;
    }

    /**
     * @return the IsOnFire
     */
    public boolean isIsOnFire() {
        return this.IsOnFire;
    }

    /**
     * 
     * @param _IsOnFire the IsOnFire to set
     */
    public void setIsOnFire(boolean _IsOnFire) {
        this.IsOnFire = _IsOnFire;
    }

    /**
     * @return the BurntToDeath
     */
    public boolean isBurntToDeath() {
        return this.BurntToDeath;
    }

    /**
     * 
     * @param _BurntToDeath the BurntToDeath to set
     */
    public void setBurntToDeath(boolean _BurntToDeath) {
        this.BurntToDeath = _BurntToDeath;
    }

    /**
     * 
     * @param _Wetness the Wetness to set
     */
    public void setWetness(float _Wetness) {
        float float0 = 0.0F;
        if (this.BodyParts.size() > 0) {
            for (int int0 = 0; int0 < this.BodyParts.size(); int0++) {
                BodyPart bodyPart = this.BodyParts.get(int0);
                bodyPart.setWetness(_Wetness);
                float0 += bodyPart.getWetness();
            }

            float0 /= this.BodyParts.size();
        }

        this.Wetness = PZMath.clamp(float0, 0.0F, 100.0F);
    }

    /**
     * @return the CatchACold
     */
    public float getCatchACold() {
        return this.CatchACold;
    }

    /**
     * 
     * @param _CatchACold the CatchACold to set
     */
    public void setCatchACold(float _CatchACold) {
        this.CatchACold = _CatchACold;
    }

    /**
     * @return the HasACold
     */
    public boolean isHasACold() {
        return this.HasACold;
    }

    /**
     * 
     * @param _HasACold the HasACold to set
     */
    public void setHasACold(boolean _HasACold) {
        this.HasACold = _HasACold;
    }

    /**
     * 
     * @param _ColdStrength the ColdStrength to set
     */
    public void setColdStrength(float _ColdStrength) {
        this.ColdStrength = _ColdStrength;
    }

    /**
     * @return the ColdProgressionRate
     */
    public float getColdProgressionRate() {
        return this.ColdProgressionRate;
    }

    /**
     * 
     * @param _ColdProgressionRate the ColdProgressionRate to set
     */
    public void setColdProgressionRate(float _ColdProgressionRate) {
        this.ColdProgressionRate = _ColdProgressionRate;
    }

    /**
     * @return the TimeToSneezeOrCough
     */
    public int getTimeToSneezeOrCough() {
        return this.TimeToSneezeOrCough;
    }

    /**
     * 
     * @param _TimeToSneezeOrCough the TimeToSneezeOrCough to set
     */
    public void setTimeToSneezeOrCough(int _TimeToSneezeOrCough) {
        this.TimeToSneezeOrCough = _TimeToSneezeOrCough;
    }

    /**
     * @return the MildColdSneezeTimerMin
     */
    public int getMildColdSneezeTimerMin() {
        return this.MildColdSneezeTimerMin;
    }

    /**
     * 
     * @param _MildColdSneezeTimerMin the MildColdSneezeTimerMin to set
     */
    public void setMildColdSneezeTimerMin(int _MildColdSneezeTimerMin) {
        this.MildColdSneezeTimerMin = _MildColdSneezeTimerMin;
    }

    /**
     * @return the MildColdSneezeTimerMax
     */
    public int getMildColdSneezeTimerMax() {
        return this.MildColdSneezeTimerMax;
    }

    /**
     * 
     * @param _MildColdSneezeTimerMax the MildColdSneezeTimerMax to set
     */
    public void setMildColdSneezeTimerMax(int _MildColdSneezeTimerMax) {
        this.MildColdSneezeTimerMax = _MildColdSneezeTimerMax;
    }

    /**
     * @return the ColdSneezeTimerMin
     */
    public int getColdSneezeTimerMin() {
        return this.ColdSneezeTimerMin;
    }

    /**
     * 
     * @param _ColdSneezeTimerMin the ColdSneezeTimerMin to set
     */
    public void setColdSneezeTimerMin(int _ColdSneezeTimerMin) {
        this.ColdSneezeTimerMin = _ColdSneezeTimerMin;
    }

    /**
     * @return the ColdSneezeTimerMax
     */
    public int getColdSneezeTimerMax() {
        return this.ColdSneezeTimerMax;
    }

    /**
     * 
     * @param _ColdSneezeTimerMax the ColdSneezeTimerMax to set
     */
    public void setColdSneezeTimerMax(int _ColdSneezeTimerMax) {
        this.ColdSneezeTimerMax = _ColdSneezeTimerMax;
    }

    /**
     * @return the NastyColdSneezeTimerMin
     */
    public int getNastyColdSneezeTimerMin() {
        return this.NastyColdSneezeTimerMin;
    }

    /**
     * 
     * @param _NastyColdSneezeTimerMin the NastyColdSneezeTimerMin to set
     */
    public void setNastyColdSneezeTimerMin(int _NastyColdSneezeTimerMin) {
        this.NastyColdSneezeTimerMin = _NastyColdSneezeTimerMin;
    }

    /**
     * @return the NastyColdSneezeTimerMax
     */
    public int getNastyColdSneezeTimerMax() {
        return this.NastyColdSneezeTimerMax;
    }

    /**
     * 
     * @param _NastyColdSneezeTimerMax the NastyColdSneezeTimerMax to set
     */
    public void setNastyColdSneezeTimerMax(int _NastyColdSneezeTimerMax) {
        this.NastyColdSneezeTimerMax = _NastyColdSneezeTimerMax;
    }

    /**
     * @return the SneezeCoughActive
     */
    public int getSneezeCoughActive() {
        return this.SneezeCoughActive;
    }

    /**
     * 
     * @param _SneezeCoughActive the SneezeCoughActive to set
     */
    public void setSneezeCoughActive(int _SneezeCoughActive) {
        this.SneezeCoughActive = _SneezeCoughActive;
    }

    /**
     * @return the SneezeCoughTime
     */
    public int getSneezeCoughTime() {
        return this.SneezeCoughTime;
    }

    /**
     * 
     * @param _SneezeCoughTime the SneezeCoughTime to set
     */
    public void setSneezeCoughTime(int _SneezeCoughTime) {
        this.SneezeCoughTime = _SneezeCoughTime;
    }

    /**
     * @return the SneezeCoughDelay
     */
    public int getSneezeCoughDelay() {
        return this.SneezeCoughDelay;
    }

    /**
     * 
     * @param _SneezeCoughDelay the SneezeCoughDelay to set
     */
    public void setSneezeCoughDelay(int _SneezeCoughDelay) {
        this.SneezeCoughDelay = _SneezeCoughDelay;
    }

    /**
     * 
     * @param _UnhappynessLevel the UnhappynessLevel to set
     */
    public void setUnhappynessLevel(float _UnhappynessLevel) {
        this.UnhappynessLevel = _UnhappynessLevel;
    }

    /**
     * @return the ParentChar
     */
    public IsoGameCharacter getParentChar() {
        return this.ParentChar;
    }

    /**
     * 
     * @param _ParentChar the ParentChar to set
     */
    public void setParentChar(IsoGameCharacter _ParentChar) {
        this.ParentChar = _ParentChar;
    }

    /**
     * @return the body temperature (updated by lua)
     */
    public float getTemperature() {
        return this.Temperature;
    }

    public void setTemperature(float t) {
        this.lastTemperature = this.Temperature;
        this.Temperature = t;
    }

    public float getTemperatureChangeTick() {
        return this.Temperature - this.lastTemperature;
    }

    public void setPoisonLevel(float poisonLevel) {
        this.PoisonLevel = poisonLevel;
    }

    public float getPoisonLevel() {
        return this.PoisonLevel;
    }

    public float getFoodSicknessLevel() {
        return this.FoodSicknessLevel;
    }

    public void setFoodSicknessLevel(float foodSicknessLevel) {
        this.FoodSicknessLevel = Math.max(foodSicknessLevel, 0.0F);
    }

    public boolean isReduceFakeInfection() {
        return this.reduceFakeInfection;
    }

    public void setReduceFakeInfection(boolean _reduceFakeInfection) {
        this.reduceFakeInfection = _reduceFakeInfection;
    }

    public void AddRandomDamage() {
        BodyPart bodyPart = this.getBodyParts().get(Rand.Next(this.getBodyParts().size()));
        switch (Rand.Next(4)) {
            case 0:
                bodyPart.generateDeepWound();
                if (Rand.Next(4) == 0) {
                    bodyPart.setInfectedWound(true);
                }
                break;
            case 1:
                bodyPart.generateDeepShardWound();
                if (Rand.Next(4) == 0) {
                    bodyPart.setInfectedWound(true);
                }
                break;
            case 2:
                bodyPart.setFractureTime(Rand.Next(30, 50));
                break;
            case 3:
                bodyPart.setBurnTime(Rand.Next(30, 50));
        }
    }

    public float getPainReduction() {
        return this.painReduction;
    }

    public void setPainReduction(float _painReduction) {
        this.painReduction = _painReduction;
    }

    public float getColdReduction() {
        return this.coldReduction;
    }

    public void setColdReduction(float _coldReduction) {
        this.coldReduction = _coldReduction;
    }

    public int getRemotePainLevel() {
        return this.RemotePainLevel;
    }

    public void setRemotePainLevel(int painLevel) {
        this.RemotePainLevel = painLevel;
    }

    public float getColdDamageStage() {
        return this.ColdDamageStage;
    }

    public void setColdDamageStage(float coldDamageStage) {
        this.ColdDamageStage = coldDamageStage;
    }

    public Thermoregulator getThermoregulator() {
        return this.thermoregulator;
    }

    public void decreaseBodyWetness(float amount) {
        float float0 = 0.0F;
        if (this.BodyParts.size() > 0) {
            for (int int0 = 0; int0 < this.BodyParts.size(); int0++) {
                BodyPart bodyPart = this.BodyParts.get(int0);
                bodyPart.setWetness(bodyPart.getWetness() - amount);
                float0 += bodyPart.getWetness();
            }

            float0 /= this.BodyParts.size();
        }

        this.Wetness = PZMath.clamp(float0, 0.0F, 100.0F);
    }

    public void increaseBodyWetness(float amount) {
        float float0 = 0.0F;
        if (this.BodyParts.size() > 0) {
            for (int int0 = 0; int0 < this.BodyParts.size(); int0++) {
                BodyPart bodyPart = this.BodyParts.get(int0);
                bodyPart.setWetness(bodyPart.getWetness() + amount);
                float0 += bodyPart.getWetness();
            }

            float0 /= this.BodyParts.size();
        }

        this.Wetness = PZMath.clamp(float0, 0.0F, 100.0F);
    }
}
