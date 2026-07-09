// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.BodyDamage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.WeaponType;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;

public class Thermoregulator_tryouts {
    private final BodyDamage bodyDamage;
    private final IsoGameCharacter character;
    private final IsoPlayer player;
    private final Stats stats;
    private final Nutrition nutrition;
    private final ClimateManager climate;
    private static final ItemVisuals itemVisuals = new ItemVisuals();
    private static final ItemVisuals itemVisualsCache = new ItemVisuals();
    private static final ArrayList<BloodBodyPartType> coveredParts = new ArrayList<>();
    private static float SIMULATION_MULTIPLIER = 1.0F;
    private float setPoint = 37.0F;
    private float metabolicRate = Metabolics.Default.getMet();
    private float metabolicRateReal = this.metabolicRate;
    private float metabolicTarget = Metabolics.Default.getMet();
    private double fluidsMultiplier = 1.0;
    private double energyMultiplier = 1.0;
    private double fatigueMultiplier = 1.0;
    private float bodyHeatDelta = 0.0F;
    private float coreHeatDelta = 0.0F;
    private boolean thermalChevronUp = true;
    private Thermoregulator_tryouts.ThermalNode core;
    private Thermoregulator_tryouts.ThermalNode[] nodes;
    private float totalHeatRaw = 0.0F;
    private float totalHeat = 0.0F;
    private float primTotal = 0.0F;
    private float secTotal = 0.0F;
    private float externalAirTemperature = 27.0F;
    private float airTemperature;
    private float airAndWindTemp;
    private float rateOfChangeCounter = 0.0F;
    private float coreCelciusCache = 37.0F;
    private float coreRateOfChange = 0.0F;
    private float thermalDamage = 0.0F;
    private float damageCounter = 0.0F;

    public Thermoregulator_tryouts(BodyDamage bodyDamagex) {
        this.bodyDamage = bodyDamagex;
        this.character = bodyDamagex.getParentChar();
        this.stats = this.character.getStats();
        if (this.character instanceof IsoPlayer) {
            this.player = (IsoPlayer)this.character;
            this.nutrition = ((IsoPlayer)this.character).getNutrition();
        } else {
            this.player = null;
            this.nutrition = null;
        }

        this.climate = ClimateManager.getInstance();
        this.initNodes();
    }

    public static void setSimulationMultiplier(float float0) {
        SIMULATION_MULTIPLIER = float0;
    }

    public void save(ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putFloat(this.setPoint);
        byteBuffer.putFloat(this.metabolicRate);
        byteBuffer.putFloat(this.metabolicTarget);
        byteBuffer.putFloat(this.bodyHeatDelta);
        byteBuffer.putFloat(this.coreHeatDelta);
        byteBuffer.putFloat(this.thermalDamage);
        byteBuffer.putFloat(this.damageCounter);
        byteBuffer.putInt(this.nodes.length);

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            byteBuffer.putInt(BodyPartType.ToIndex(thermalNode.bodyPartType));
            byteBuffer.putFloat(thermalNode.celcius);
            byteBuffer.putFloat(thermalNode.skinCelcius);
            byteBuffer.putFloat(thermalNode.heatDelta);
            byteBuffer.putFloat(thermalNode.primaryDelta);
            byteBuffer.putFloat(thermalNode.secondaryDelta);
        }
    }

    public void load(ByteBuffer byteBuffer, int var2) throws IOException {
        this.setPoint = byteBuffer.getFloat();
        this.metabolicRate = byteBuffer.getFloat();
        this.metabolicTarget = byteBuffer.getFloat();
        this.bodyHeatDelta = byteBuffer.getFloat();
        this.coreHeatDelta = byteBuffer.getFloat();
        this.thermalDamage = byteBuffer.getFloat();
        this.damageCounter = byteBuffer.getFloat();
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = byteBuffer.getInt();
            float float0 = byteBuffer.getFloat();
            float float1 = byteBuffer.getFloat();
            float float2 = byteBuffer.getFloat();
            float float3 = byteBuffer.getFloat();
            float float4 = byteBuffer.getFloat();
            Thermoregulator_tryouts.ThermalNode thermalNode = this.getNodeForType(BodyPartType.FromIndex(int2));
            if (thermalNode != null) {
                thermalNode.celcius = float0;
                thermalNode.skinCelcius = float1;
                thermalNode.heatDelta = float2;
                thermalNode.primaryDelta = float3;
                thermalNode.secondaryDelta = float4;
            } else {
                DebugLog.log("Couldnt load node: " + BodyPartType.ToString(BodyPartType.FromIndex(int2)));
            }
        }
    }

    public void reset() {
        this.setPoint = 37.0F;
        this.metabolicRate = Metabolics.Default.getMet();
        this.metabolicTarget = this.metabolicRate;
        this.core.celcius = 37.0F;
        this.bodyHeatDelta = 0.0F;
        this.coreHeatDelta = 0.0F;
        this.thermalDamage = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            if (thermalNode != this.core) {
                thermalNode.celcius = 35.0F;
            }

            thermalNode.primaryDelta = 0.0F;
            thermalNode.secondaryDelta = 0.0F;
            thermalNode.skinCelcius = 33.0F;
            thermalNode.heatDelta = 0.0F;
        }
    }

    private void initNodes() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.bodyDamage.getBodyParts().size(); int0++) {
            BodyPart bodyPart = this.bodyDamage.getBodyParts().get(int0);
            Thermoregulator_tryouts.ThermalNode thermalNode0 = null;
            switch (bodyPart.getType()) {
                case Torso_Upper:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(true, 37.0F, bodyPart, 0.25F);
                    this.core = thermalNode0;
                    break;
                case Head:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 1.0F);
                    break;
                case Neck:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.5F);
                    break;
                case Torso_Lower:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.25F);
                    break;
                case Groin:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.5F);
                    break;
                case UpperLeg_L:
                case UpperLeg_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.5F);
                    break;
                case LowerLeg_L:
                case LowerLeg_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.5F);
                    break;
                case Foot_L:
                case Foot_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.5F);
                    break;
                case UpperArm_L:
                case UpperArm_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.25F);
                    break;
                case ForeArm_L:
                case ForeArm_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 0.25F);
                    break;
                case Hand_L:
                case Hand_R:
                    thermalNode0 = new Thermoregulator_tryouts.ThermalNode(37.0F, bodyPart, 1.0F);
                    break;
                default:
                    DebugLog.log("Warning: couldnt init thermal node for body part '" + this.bodyDamage.getBodyParts().get(int0).getType() + "'.");
            }

            if (thermalNode0 != null) {
                arrayList.add(thermalNode0);
            }
        }

        this.nodes = new Thermoregulator_tryouts.ThermalNode[arrayList.size()];
        arrayList.toArray(this.nodes);

        for (int int1 = 0; int1 < this.nodes.length; int1++) {
            Thermoregulator_tryouts.ThermalNode thermalNode1 = this.nodes[int1];
            BodyPartType bodyPartType = BodyPartContacts.getParent(thermalNode1.bodyPartType);
            if (bodyPartType != null) {
                thermalNode1.upstream = this.getNodeForType(bodyPartType);
            }

            BodyPartType[] bodyPartTypes = BodyPartContacts.getChildren(thermalNode1.bodyPartType);
            if (bodyPartTypes != null && bodyPartTypes.length > 0) {
                thermalNode1.downstream = new Thermoregulator_tryouts.ThermalNode[bodyPartTypes.length];

                for (int int2 = 0; int2 < bodyPartTypes.length; int2++) {
                    thermalNode1.downstream[int2] = this.getNodeForType(bodyPartTypes[int2]);
                }
            }
        }

        this.core.celcius = this.setPoint;
    }

    public Thermoregulator_tryouts.ThermalNode getNodeForType(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            if (this.nodes[int0].bodyPartType == bodyPartType) {
                return this.nodes[int0];
            }
        }

        return null;
    }

    public Thermoregulator_tryouts.ThermalNode getNodeForBloodType(BloodBodyPartType bloodBodyPartType) {
        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            if (this.nodes[int0].bloodBPT == bloodBodyPartType) {
                return this.nodes[int0];
            }
        }

        return null;
    }

    public float getBodyHeatDelta() {
        return this.bodyHeatDelta;
    }

    public double getFluidsMultiplier() {
        return this.fluidsMultiplier;
    }

    public double getEnergyMultiplier() {
        return this.energyMultiplier;
    }

    public double getFatigueMultiplier() {
        return this.fatigueMultiplier;
    }

    public float getMovementModifier() {
        float float0 = 1.0F;
        if (this.player != null) {
            int int0 = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
            if (int0 == 2) {
                float0 = 0.66F;
            } else if (int0 == 3) {
                float0 = 0.33F;
            } else if (int0 == 4) {
                float0 = 0.0F;
            }

            int0 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
            if (int0 == 2) {
                float0 = 0.66F;
            } else if (int0 == 3) {
                float0 = 0.33F;
            } else if (int0 == 4) {
                float0 = 0.0F;
            }
        }

        return float0;
    }

    public float getCombatModifier() {
        float float0 = 1.0F;
        if (this.player != null) {
            int int0 = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
            if (int0 == 2) {
                float0 = 0.66F;
            } else if (int0 == 3) {
                float0 = 0.33F;
            } else if (int0 == 4) {
                float0 = 0.1F;
            }

            int0 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
            if (int0 == 2) {
                float0 = 0.66F;
            } else if (int0 == 3) {
                float0 = 0.33F;
            } else if (int0 == 4) {
                float0 = 0.1F;
            }
        }

        return float0;
    }

    public float getCoreTemperature() {
        return this.core.celcius;
    }

    public float getHeatGeneration() {
        return this.metabolicRateReal;
    }

    public float getMetabolicRate() {
        return this.metabolicRate;
    }

    public float getMetabolicTarget() {
        return this.metabolicTarget;
    }

    public float getMetabolicRateReal() {
        return this.metabolicRateReal;
    }

    public float getSetPoint() {
        return this.setPoint;
    }

    public float getCoreHeatDelta() {
        return this.coreHeatDelta;
    }

    public float getCoreRateOfChange() {
        return this.coreRateOfChange;
    }

    public float getExternalAirTemperature() {
        return this.externalAirTemperature;
    }

    public float getCoreTemperatureUI() {
        float float0 = PZMath.clamp(this.core.celcius, 20.0F, 42.0F);
        if (float0 < 37.0F) {
            float0 = (float0 - 20.0F) / 17.0F * 0.5F;
        } else {
            float0 = 0.5F + (float0 - 37.0F) / 5.0F * 0.5F;
        }

        return float0;
    }

    public float getHeatGenerationUI() {
        float float0 = PZMath.clamp(this.metabolicRateReal, 0.0F, Metabolics.MAX.getMet());
        if (float0 < Metabolics.Default.getMet()) {
            float0 = float0 / Metabolics.Default.getMet() * 0.5F;
        } else {
            float0 = 0.5F + (float0 - Metabolics.Default.getMet()) / (Metabolics.MAX.getMet() - Metabolics.Default.getMet()) * 0.5F;
        }

        return float0;
    }

    public boolean thermalChevronUp() {
        return this.thermalChevronUp;
    }

    public int thermalChevronCount() {
        if (this.coreRateOfChange > 0.01F) {
            return 3;
        } else if (this.coreRateOfChange > 0.001F) {
            return 2;
        } else {
            return this.coreRateOfChange > 1.0E-4F ? 1 : 0;
        }
    }

    public float getCatchAColdDelta() {
        float float0 = 0.0F;
        if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) < 1) {
            return float0;
        } else {
            for (int int0 = 0; int0 < this.nodes.length; int0++) {
                Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
                float float1 = 0.0F;
                if (thermalNode.skinCelcius < 33.0F) {
                    float1 = (thermalNode.skinCelcius - 20.0F) / 13.0F;
                    float1 = 1.0F - float1;
                    float1 *= float1;
                }

                float float2 = 0.25F * float1 * thermalNode.skinSurface;
                if (thermalNode.bodyWetness > 0.0F) {
                    float2 *= 1.0F + thermalNode.bodyWetness * 1.0F;
                }

                if (thermalNode.clothingWetness > 0.5F) {
                    float2 *= 1.0F + (thermalNode.clothingWetness - 0.5F) * 2.0F;
                }

                if (thermalNode.bodyPartType == BodyPartType.Neck) {
                    float2 *= 8.0F;
                } else if (thermalNode.bodyPartType == BodyPartType.Torso_Upper) {
                    float2 *= 16.0F;
                } else if (thermalNode.bodyPartType == BodyPartType.Head) {
                    float2 *= 4.0F;
                }

                float0 += float2;
            }

            if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 1) {
                float0 *= this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
            }

            return float0;
        }
    }

    public float getTimedActionTimeModifier() {
        float float0 = 1.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            float float1 = 0.0F;
            if (thermalNode.skinCelcius < 33.0F) {
                float1 = (thermalNode.skinCelcius - 20.0F) / 13.0F;
                float1 = 1.0F - float1;
                float1 *= float1;
            }

            float float2 = 0.25F * float1 * thermalNode.skinSurface;
            if (thermalNode.bodyPartType == BodyPartType.Hand_R || thermalNode.bodyPartType == BodyPartType.Hand_L) {
                float0 += 0.3F * float2;
            } else if (thermalNode.bodyPartType == BodyPartType.ForeArm_R || thermalNode.bodyPartType == BodyPartType.ForeArm_L) {
                float0 += 0.15F * float2;
            } else if (thermalNode.bodyPartType == BodyPartType.UpperArm_R || thermalNode.bodyPartType == BodyPartType.UpperArm_L) {
                float0 += 0.1F * float2;
            }
        }

        return float0;
    }

    public static float getSkinCelciusMin() {
        return 20.0F;
    }

    public static float getSkinCelciusFavorable() {
        return 33.0F;
    }

    public static float getSkinCelciusMax() {
        return 42.0F;
    }

    public void setMetabolicTarget(Metabolics metabolics) {
        this.setMetabolicTarget(metabolics.getMet());
    }

    public void setMetabolicTarget(float float0) {
        if (!(float0 < 0.0F) && !(float0 < this.metabolicTarget)) {
            this.metabolicTarget = float0;
            if (this.metabolicTarget > Metabolics.MAX.getMet()) {
                this.metabolicTarget = Metabolics.MAX.getMet();
            }
        }
    }

    private void updateCoreRateOfChange() {
        this.rateOfChangeCounter = this.rateOfChangeCounter + GameTime.instance.getMultiplier();
        if (this.rateOfChangeCounter > 100.0F) {
            this.rateOfChangeCounter = 0.0F;
            this.coreRateOfChange = this.core.celcius - this.coreCelciusCache;
            this.thermalChevronUp = this.coreRateOfChange >= 0.0F;
            this.coreRateOfChange = PZMath.abs(this.coreRateOfChange);
            this.coreCelciusCache = this.core.celcius;
        }
    }

    public float getSimulationMultiplier() {
        return SIMULATION_MULTIPLIER;
    }

    public float getDefaultMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.Default);
    }

    public float getMetabolicRateIncMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.MetabolicRateInc);
    }

    public float getMetabolicRateDecMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.MetabolicRateDec);
    }

    public float getBodyHeatMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.BodyHeat);
    }

    public float getCoreHeatExpandMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.CoreHeatExpand);
    }

    public float getCoreHeatContractMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.CoreHeatContract);
    }

    public float getSkinCelciusMultiplier() {
        return this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.SkinCelcius);
    }

    public float getTemperatureAir() {
        return this.climate.getAirTemperatureForCharacter(this.character, false);
    }

    public float getTemperatureAirAndWind() {
        return this.climate.getAirTemperatureForCharacter(this.character, true);
    }

    public float getDbg_totalHeatRaw() {
        return this.totalHeatRaw;
    }

    public float getDbg_totalHeat() {
        return this.totalHeat;
    }

    public float getCoreCelcius() {
        return this.core != null ? this.core.celcius : 0.0F;
    }

    public float getDbg_primTotal() {
        return this.primTotal;
    }

    public float getDbg_secTotal() {
        return this.secTotal;
    }

    private float getSimulationMultiplier(Thermoregulator_tryouts.Multiplier multiplier) {
        float float0 = GameTime.instance.getMultiplier();
        switch (multiplier) {
            case MetabolicRateInc:
                float0 *= 0.001F;
                break;
            case MetabolicRateDec:
                float0 *= 4.0E-4F;
                break;
            case BodyHeat:
                float0 *= 2.5E-4F;
                break;
            case CoreHeatExpand:
                float0 *= 5.0E-5F;
                break;
            case CoreHeatContract:
                float0 *= 5.0E-4F;
                break;
            case SkinCelcius:
            case SkinCelciusExpand:
                float0 *= 0.0025F;
                break;
            case SkinCelciusContract:
                float0 *= 0.005F;
                break;
            case PrimaryDelta:
                float0 *= 5.0E-4F;
                break;
            case SecondaryDelta:
                float0 *= 2.5E-4F;
            case Default:
        }

        return float0 * SIMULATION_MULTIPLIER;
    }

    public float getThermalDamage() {
        return this.thermalDamage;
    }

    private void updateThermalDamage(float float0) {
        this.damageCounter = this.damageCounter + GameTime.instance.getRealworldSecondsSinceLastUpdate();
        if (this.damageCounter > 1.0F) {
            this.damageCounter = 0.0F;
            if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) == 4 && float0 < 0.0F && this.core.celcius - this.coreCelciusCache <= 0.0F) {
                float float1 = (this.core.celcius - 20.0F) / 5.0F;
                float1 = 1.0F - float1;
                float float2 = 120.0F;
                float2 += 480.0F * float1;
                this.thermalDamage = this.thermalDamage + 1.0F / float2 * PZMath.clamp_01(PZMath.abs(float0) / 10.0F);
            } else if (this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4
                && float0 > 37.0F
                && this.core.celcius - this.coreCelciusCache >= 0.0F) {
                float float3 = (this.core.celcius - 41.0F) / 1.0F;
                float float4 = 120.0F;
                float4 += 480.0F * float3;
                this.thermalDamage = this.thermalDamage + 1.0F / float4 * PZMath.clamp_01((float0 - 37.0F) / 8.0F);
                this.thermalDamage = Math.min(this.thermalDamage, 0.3F);
            } else {
                this.thermalDamage -= 0.011111111F;
            }

            this.thermalDamage = PZMath.clamp_01(this.thermalDamage);
        }

        this.player.getBodyDamage().ColdDamageStage = this.thermalDamage;
    }

    public void update() {
        this.airTemperature = this.climate.getAirTemperatureForCharacter(this.character, false);
        this.airAndWindTemp = this.climate.getAirTemperatureForCharacter(this.character, true);
        this.externalAirTemperature = this.airTemperature;
        this.setPoint = 37.0F;
        if (this.stats.getSickness() > 0.0F) {
            this.setPoint = this.setPoint + this.stats.getSickness() * 2.0F;
        }

        this.updateCoreRateOfChange();
        this.updateMetabolicRate();
        this.updateClothing();
        this.updateTest();
    }

    private void updateMetabolicRate() {
        this.setMetabolicTarget(Metabolics.Default.getMet());
        if (this.player != null) {
            if (this.player.isAttacking()) {
                WeaponType weaponType = WeaponType.getWeaponType(this.player);
                switch (weaponType) {
                    case barehand:
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    case twohanded:
                        this.setMetabolicTarget(Metabolics.HeavyWork);
                        break;
                    case onehanded:
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    case heavy:
                        this.setMetabolicTarget(Metabolics.Running15kmh);
                        break;
                    case knife:
                        this.setMetabolicTarget(Metabolics.LightWork);
                        break;
                    case spear:
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    case handgun:
                        this.setMetabolicTarget(Metabolics.UsingTools);
                        break;
                    case firearm:
                        this.setMetabolicTarget(Metabolics.LightWork);
                        break;
                    case throwing:
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    case chainsaw:
                        this.setMetabolicTarget(Metabolics.Running15kmh);
                }
            }

            if (this.player.isPlayerMoving()) {
                if (this.player.isSprinting()) {
                    this.setMetabolicTarget(Metabolics.Running15kmh);
                } else if (this.player.isRunning()) {
                    this.setMetabolicTarget(Metabolics.Running10kmh);
                } else if (this.player.isSneaking()) {
                    this.setMetabolicTarget(Metabolics.Walking2kmh);
                } else if (this.player.CurrentSpeed > 0.0F) {
                    this.setMetabolicTarget(Metabolics.Walking5kmh);
                }
            }
        }

        float float0 = PZMath.clamp_01(1.0F - this.stats.getEndurance()) * Metabolics.DefaultExercise.getMet();
        this.setMetabolicTarget(float0 * this.getEnergy());
        float float1 = PZMath.clamp_01(this.player.getInventory().getCapacityWeight() / this.player.getMaxWeight());
        float float2 = 1.0F + float1 * float1 * 0.35F;
        this.setMetabolicTarget(this.metabolicTarget * float2);
        if (!PZMath.equal(this.metabolicRate, this.metabolicTarget)) {
            float float3 = this.metabolicTarget - this.metabolicRate;
            if (this.metabolicTarget > this.metabolicRate) {
                this.metabolicRate = this.metabolicRate + float3 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.MetabolicRateInc);
            } else {
                this.metabolicRate = this.metabolicRate + float3 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.MetabolicRateDec);
            }
        }

        float float4 = 1.0F;
        if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1) {
            float4 = this.getMovementModifier();
        }

        this.metabolicRateReal = this.metabolicRate * (0.2F + 0.8F * this.getEnergy() * float4);
        this.metabolicTarget = -1.0F;
    }

    private void updateTest() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            thermalNode.calculateInsulation();
            float float1 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float1 -= (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode.windresist);
            }

            thermalNode.heatDelta = float1 - thermalNode.skinCelcius;
            thermalNode.heatDelta = thermalNode.heatDelta / (1.0F + thermalNode.insulation);
            thermalNode.heatDelta /= 3.75F;
            float float2 = this.metabolicRateReal;
            float2 *= 1.0F + thermalNode.insulation * 0.25F;
            thermalNode.heatDelta += float2;
            float float3 = 1.0F;
            float0 += thermalNode.heatDelta * thermalNode.skinSurface * float3;
            float float4 = this.core.celcius - 20.0F;
            float float5 = this.core.celcius;
            if (float4 < this.airTemperature) {
                float4 = this.airTemperature;
            }

            if (float4 > float5) {
                float4 = float5;
            }

            float float6 = thermalNode.skinCelcius + thermalNode.heatDelta;
            float6 = PZMath.clamp(float6, float4, float5);
            float float7 = float6 - thermalNode.skinCelcius;
            float float8 = 0.0025F;
            if (thermalNode.skinCelcius < 33.0F && float7 < 0.0F || thermalNode.skinCelcius > 33.0F && float7 > 0.0F) {
                float8 = 2.5E-4F;
            }

            thermalNode.skinCelcius += float7 * float8;
        }

        this.coreHeatDelta = float0;
        float float9 = 0.0025F;
        if (this.core.celcius < this.setPoint && this.coreHeatDelta < 0.0F) {
            float9 = 5.0E-5F;
        }

        if (this.core.celcius > this.setPoint && this.coreHeatDelta > 0.0F) {
            float9 = 2.5E-4F;
        }

        this.core.celcius = this.core.celcius + this.coreHeatDelta * float9;
    }

    private void updateTest_4() {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            thermalNode.calculateInsulation();
            float float1 = 1.0F - thermalNode.getDistToCore();
            float float2 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float2 -= (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode.windresist);
            }

            thermalNode.heatDelta = float2 - thermalNode.skinCelcius;
            thermalNode.heatDelta = thermalNode.heatDelta / (1.0F + thermalNode.insulation);
            thermalNode.heatDelta /= 12.0F;
            float float3 = 1.0F;
            float0 += thermalNode.heatDelta * thermalNode.skinSurface * float3;
            float float4 = this.core.celcius - 20.0F;
            float float5 = this.core.celcius;
            if (float4 < this.airTemperature) {
                float4 = this.airTemperature;
            }

            if (float4 > float5) {
                float4 = float5;
            }

            float float6 = this.core.celcius - this.setPoint;
            if (float6 > 0.0F) {
                float6 *= 2.0F;
                float6 *= 1.0F + float1 * 0.25F;
            } else {
                float6 *= 1.0F + thermalNode.distToCore * 0.25F;
            }

            float float7 = thermalNode.skinCelcius + thermalNode.heatDelta + float6;
            float7 = PZMath.clamp(float7, float4, float5);
            float float8 = float7 - thermalNode.skinCelcius;
            float float9 = 0.0025F;
            if (thermalNode.skinCelcius < 33.0F && float8 < 0.0F || thermalNode.skinCelcius > 33.0F && float8 > 0.0F) {
                float9 = 2.5E-4F;
            }

            thermalNode.skinCelcius += float8 * float9;
        }

        float float10 = this.metabolicRateReal / Metabolics.Default.getMet() - 1.0F;
        if (float10 > 0.0F) {
            float10 /= Metabolics.Default.getMet() * 2.0F;
        }

        this.coreHeatDelta = float0 + float10;
        float float11 = 0.0025F;
        if (this.core.celcius < this.setPoint && this.coreHeatDelta < 0.0F) {
            float11 = 5.0E-5F;
        }

        if (this.core.celcius > this.setPoint && this.coreHeatDelta > 0.0F) {
            float11 = 2.5E-4F;
        }

        this.core.celcius = this.core.celcius + this.coreHeatDelta * float11;
    }

    private void updateTest_3() {
        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            thermalNode.calculateInsulation();
            float float0 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float0 -= (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode.windresist);
            }

            thermalNode.heatDelta = float0 - thermalNode.skinCelcius;
            thermalNode.heatDelta = thermalNode.heatDelta / (1.0F + thermalNode.insulation);
            float float1 = 1.0F + (1.0F - thermalNode.getDistToCore() * 0.5F);
            float float2 = this.metabolicRateReal * float1;
            float2 *= 1.0F + thermalNode.insulation * 0.25F;
            thermalNode.heatDelta += float2;
            float float3 = this.core.celcius - 20.0F;
            float float4 = this.core.celcius;
            if (float3 < this.airTemperature) {
                float3 = this.airTemperature;
            }

            if (float3 > float4) {
                float3 = float4;
            }

            float float5 = thermalNode.skinCelcius + thermalNode.heatDelta / 12.0F;
            float5 = PZMath.clamp(float5, float3, float4);
            float float6 = float5 - thermalNode.skinCelcius;
            thermalNode.skinCelcius = thermalNode.skinCelcius + float6 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.SkinCelcius);
        }
    }

    private void updateTest_2() {
        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode0 = this.nodes[int0];
            thermalNode0.calculateInsulation();
            float float0 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float0 = (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode0.windresist);
            }

            float float1 = float0 - 27.0F;
            thermalNode0.heatDelta = float1;
            thermalNode0.heatDelta = thermalNode0.heatDelta / (1.0F + thermalNode0.insulation);
            float float2 = 1.0F + (1.0F - thermalNode0.getDistToCore() * 0.8F);
            float float3 = this.metabolicRateReal * float2;
            float3 *= 1.0F + thermalNode0.insulation;
            thermalNode0.heatDelta += float3;
            float float4 = thermalNode0.heatDelta / 12.0F;
            float4 = PZMath.clamp(float4, -1.0F, 1.0F);
            float float5 = float4 - thermalNode0.primaryDelta;
            thermalNode0.primaryDelta = thermalNode0.primaryDelta + float5 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.PrimaryDelta);
            thermalNode0.primaryDelta = PZMath.clamp(thermalNode0.primaryDelta, -1.0F, 1.0F);
        }

        for (int int1 = 0; int1 < this.nodes.length; int1++) {
            Thermoregulator_tryouts.ThermalNode thermalNode1 = this.nodes[int1];
            float float6 = this.core.celcius - 20.0F;
            float float7 = this.core.celcius;
            if (float6 < this.airTemperature) {
                float6 = this.airTemperature;
            }

            float float8 = this.core.celcius - 4.0F;
            if (thermalNode1.primaryDelta < 0.0F) {
                float8 = 33.0F - 20.0F * PZMath.abs(thermalNode1.primaryDelta);
            } else {
                float8 = 33.0F + 20.0F * thermalNode1.primaryDelta;
            }

            float8 = PZMath.clamp(float8, float6, float7);
            float float9 = float8 - thermalNode1.skinCelcius;
            thermalNode1.skinCelcius = thermalNode1.skinCelcius + float9 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.SkinCelcius);
            thermalNode1.skinCelcius = float8;
        }
    }

    private void updateTest_OLD() {
        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode0 = this.nodes[int0];
            thermalNode0.calculateInsulation();
            float float0 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float0 = (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode0.windresist);
            }

            float float1 = float0 - thermalNode0.skinCelcius;
            thermalNode0.heatDelta = float1;
            thermalNode0.heatDelta = thermalNode0.heatDelta / (1.0F + thermalNode0.insulation);
            float float2 = 1.0F + (1.0F - thermalNode0.getDistToCore() * 0.8F);
            float float3 = this.metabolicRateReal * float2;
            float3 *= 1.0F + thermalNode0.insulation;
            thermalNode0.heatDelta += float3;
            thermalNode0.primaryDelta = thermalNode0.heatDelta / 12.0F;
            thermalNode0.primaryDelta = PZMath.clamp(thermalNode0.primaryDelta, -1.0F, 1.0F);
        }

        for (int int1 = 0; int1 < this.nodes.length; int1++) {
            Thermoregulator_tryouts.ThermalNode thermalNode1 = this.nodes[int1];
            float float4 = this.core.celcius - 20.0F;
            float float5 = this.core.celcius;
            if (float4 < this.airTemperature) {
                float4 = this.airTemperature;
            }

            float float6 = this.core.celcius - 4.0F;
            if (thermalNode1.primaryDelta < 0.0F) {
                float6 = 33.0F - 20.0F * PZMath.abs(thermalNode1.primaryDelta);
            } else {
                float6 = 33.0F + 20.0F * thermalNode1.primaryDelta;
            }

            float6 = PZMath.clamp(float6, float4, float5);
            float float7 = float6 - thermalNode1.skinCelcius;
            thermalNode1.skinCelcius = thermalNode1.skinCelcius + float7 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.SkinCelcius);
            thermalNode1.skinCelcius = float6;
        }
    }

    private void updateNodesHeatDelta() {
        float float0 = PZMath.clamp_01((float)((this.player.getNutrition().getWeight() / 75.0 - 0.5) * 0.666F));
        float0 = (float0 - 0.5F) * 2.0F;
        float float1 = this.stats.getFitness();
        float float2 = 1.0F;
        if (this.airAndWindTemp > this.setPoint - 2.0F) {
            if (this.airTemperature < this.setPoint + 2.0F) {
                float2 = (this.airTemperature - (this.setPoint - 2.0F)) / 4.0F;
                float2 = 1.0F - float2;
            } else {
                float2 = 0.0F;
            }
        }

        float float3 = 1.0F;
        if (this.climate.getHumidity() > 0.5F) {
            float float4 = (this.climate.getHumidity() - 0.5F) * 2.0F;
            float3 -= float4;
        }

        float float5 = 1.0F;
        if (this.core.celcius < 37.0F) {
            float5 = (this.core.celcius - 20.0F) / 17.0F;
            float5 *= float5;
        }

        float float6 = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            thermalNode.calculateInsulation();
            float float7 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float7 = (this.airTemperature - this.airAndWindTemp) / (1.0F + thermalNode.windresist);
            }

            float float8 = float7 - thermalNode.skinCelcius;
            if (float8 <= 0.0F) {
                float8 *= 1.0F + 0.75F * thermalNode.bodyWetness;
            } else {
                float8 /= 1.0F + 3.0F * thermalNode.bodyWetness;
            }

            float8 *= 0.3F;
            float8 /= 1.0F + thermalNode.insulation;
            thermalNode.heatDelta = float8 * thermalNode.skinSurface;
            if (thermalNode.primaryDelta > 0.0F) {
                float float9 = 0.2F + 0.8F * this.getBodyFluids();
                float float10 = Metabolics.Default.getMet() * thermalNode.primaryDelta * thermalNode.skinSurface / thermalNode.insulation;
                float10 *= float9 * (0.1F + 0.9F * float2);
                float10 *= float3;
                float10 *= 1.0F - 0.2F * float0;
                float10 *= 1.0F + 0.2F * float1;
                thermalNode.heatDelta -= float10;
            } else {
                float float11 = 0.2F + 0.8F * this.getEnergy();
                float float12 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.primaryDelta) * thermalNode.skinSurface;
                float12 *= float11;
                float12 *= 1.0F + 0.2F * float0;
                float12 *= 1.0F + 0.2F * float1;
                thermalNode.heatDelta += float12;
            }

            if (thermalNode.secondaryDelta > 0.0F) {
                float float13 = 0.1F + 0.9F * this.getBodyFluids();
                float float14 = Metabolics.MAX.getMet() * 0.75F * thermalNode.secondaryDelta * thermalNode.skinSurface / thermalNode.insulation;
                float14 *= float13;
                float14 *= 0.85F + 0.15F * float3;
                float14 *= 1.0F - 0.2F * float0;
                float14 *= 1.0F + 0.2F * float1;
                thermalNode.heatDelta -= float14;
            } else {
                float float15 = 0.1F + 0.9F * this.getEnergy();
                float float16 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.secondaryDelta) * thermalNode.skinSurface;
                float16 *= float15;
                float16 *= 1.0F + 0.2F * float0;
                float16 *= 1.0F + 0.2F * float1;
                thermalNode.heatDelta += float16;
            }

            float6 += thermalNode.heatDelta;
        }

        this.totalHeatRaw = float6;
        float6 += this.metabolicRateReal;
        this.totalHeat = float6;
    }

    private void updateNodesHeatDelta_OLD() {
        float float0 = (float)this.player.getNutrition().getWeight();
        float float1 = PZMath.clamp_01((float0 / 75.0F - 0.5F) * 0.666F);
        float float2 = (float1 - 0.5F) * 2.0F;
        float float3 = this.stats.getFitness();
        float float4 = 1.0F;
        if (this.airAndWindTemp > this.setPoint - 2.0F) {
            if (this.airTemperature < this.setPoint + 2.0F) {
                float4 = (this.airTemperature - (this.setPoint - 2.0F)) / 4.0F;
                float4 = 1.0F - float4;
            } else {
                float4 = 0.0F;
            }
        }

        float float5 = 1.0F;
        if (this.climate.getHumidity() > 0.5F) {
            float float6 = (this.climate.getHumidity() - 0.5F) * 2.0F;
            float5 -= float6;
        }

        float float7 = 1.0F;
        if (this.core.celcius < 37.0F) {
            float7 = (this.core.celcius - 20.0F) / 17.0F;
            float7 *= float7;
        }

        float float8 = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            thermalNode.calculateInsulation();
            float float9 = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                float float10 = this.airTemperature - this.airAndWindTemp;
                float10 /= 1.0F + thermalNode.windresist;
                float9 -= float10;
            }

            float float11 = 1.0F + thermalNode.insulation;
            float float12 = float9 - thermalNode.skinCelcius;
            if (float12 < 0.0F) {
                float12 *= 1.0F + 0.25F * thermalNode.bodyWetness;
            } else if (float12 > 0.0F) {
                float12 /= 1.0F + thermalNode.bodyWetness * 4.0F;
            }

            float12 *= 0.3F;
            float12 /= float11;
            thermalNode.heatDelta = float12 * thermalNode.skinSurface;
            if (thermalNode.primaryDelta > 0.0F) {
                float float13 = 0.2F + 0.8F * this.getBodyFluids();
                float float14 = Metabolics.Default.getMet() * 1.0F * thermalNode.primaryDelta * thermalNode.skinSurface / float11;
                float14 *= float13 * (0.1F + 0.9F * float4);
                float14 *= float5;
                float14 *= 1.0F - 0.2F * float2;
                float14 *= 1.0F + 0.2F * float3;
                thermalNode.heatDelta -= float14;
            } else {
                float float15 = 0.2F + 0.8F * this.getEnergy();
                float float16 = Metabolics.Default.getMet() * 1.0F * PZMath.abs(thermalNode.primaryDelta) * thermalNode.skinSurface;
                float16 *= float15;
                float16 *= 1.0F + 0.2F * float2;
                float16 *= 1.0F + 0.2F * float3;
                float16 *= 0.25F + 0.75F * float7;
                thermalNode.heatDelta += float16;
            }

            if (thermalNode.secondaryDelta > 0.0F) {
                float float17 = 0.1F + 0.9F * this.getBodyFluids();
                float float18 = Metabolics.MAX.getMet() * 0.75F * thermalNode.secondaryDelta * thermalNode.skinSurface / float11;
                float18 *= float17;
                float18 *= 0.75F + 0.25F * float5;
                float18 *= 1.0F - 0.2F * float2;
                float18 *= 1.0F + 0.2F * float3;
                thermalNode.heatDelta -= float18;
            } else {
                float float19 = 0.1F + 0.9F * this.getEnergy();
                float float20 = Metabolics.Default.getMet() * 1.0F * PZMath.abs(thermalNode.secondaryDelta) * thermalNode.skinSurface;
                float20 *= float19;
                float20 *= 1.0F + 0.2F * float2;
                float20 *= 1.0F + 0.2F * float3;
                float20 *= 0.25F + 0.75F * float7;
                thermalNode.heatDelta += float20;
            }

            float8 += thermalNode.heatDelta;
        }

        this.totalHeatRaw = float8;
        float8 += this.metabolicRateReal;
        this.totalHeat = float8;
    }

    private void updateHeatDeltas() {
        this.coreHeatDelta = this.totalHeat * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.BodyHeat);
        if (this.coreHeatDelta < 0.0F) {
            if (this.core.celcius > this.setPoint) {
                this.coreHeatDelta = this.coreHeatDelta * (1.0F + (this.core.celcius - this.setPoint) / 2.0F);
            }
        } else if (this.core.celcius < this.setPoint) {
            this.coreHeatDelta = this.coreHeatDelta * (1.0F + (this.setPoint - this.core.celcius) / 4.0F);
        }

        this.core.celcius = this.core.celcius + this.coreHeatDelta;
        this.core.celcius = PZMath.clamp(this.core.celcius, 20.0F, 42.0F);
        this.bodyDamage.setTemperature(this.core.celcius);
        this.bodyHeatDelta = 0.0F;
        if (this.core.celcius > this.setPoint) {
            this.bodyHeatDelta = this.core.celcius - this.setPoint;
        } else if (this.core.celcius < this.setPoint) {
            this.bodyHeatDelta = this.core.celcius - this.setPoint;
        }

        if (this.bodyHeatDelta < 0.0F) {
            float float0 = PZMath.abs(this.bodyHeatDelta);
            if (float0 <= 1.0F) {
                this.bodyHeatDelta *= 0.8F;
            } else {
                float0 = PZMath.clamp(float0, 1.0F, 11.0F) - 1.0F;
                float0 /= 10.0F;
                this.bodyHeatDelta = -0.8F + -0.2F * float0;
            }
        }

        this.bodyHeatDelta = PZMath.clamp(this.bodyHeatDelta, -1.0F, 1.0F);
    }

    private void updateNodes() {
        float float0 = 0.0F;
        float float1 = 0.0F;

        for (int int0 = 0; int0 < this.nodes.length; int0++) {
            Thermoregulator_tryouts.ThermalNode thermalNode = this.nodes[int0];
            float float2 = 1.0F + thermalNode.insulation;
            float float3 = this.metabolicRateReal / Metabolics.MAX.getMet();
            float3 *= float3;
            if (this.bodyHeatDelta < 0.0F) {
                float float4 = thermalNode.distToCore;
                thermalNode.primaryDelta = this.bodyHeatDelta * (1.0F + float4);
            } else {
                thermalNode.primaryDelta = this.bodyHeatDelta * (1.0F + (1.0F - thermalNode.distToCore));
            }

            thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0F, 1.0F);
            thermalNode.secondaryDelta = thermalNode.primaryDelta * PZMath.abs(thermalNode.primaryDelta) * PZMath.abs(thermalNode.primaryDelta);
            float0 += thermalNode.primaryDelta * thermalNode.skinSurface;
            float1 += thermalNode.secondaryDelta * thermalNode.skinSurface;
            if (this.stats.getDrunkenness() > 0.0F) {
                thermalNode.primaryDelta = thermalNode.primaryDelta + this.stats.getDrunkenness() * 0.02F;
            }

            thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0F, 1.0F);
            float float5 = this.core.celcius - 20.0F;
            float float6 = this.core.celcius;
            if (float5 < this.airTemperature) {
                if (this.airTemperature < 33.0F) {
                    float5 = this.airTemperature;
                } else {
                    float float7 = 0.4F + 0.6F * (1.0F - thermalNode.distToCore);
                    float float8 = (this.airTemperature - 33.0F) / 6.0F;
                    float5 = 33.0F;
                    float5 += 4.0F * float8 * float7;
                    float5 = PZMath.clamp(float5, 33.0F, this.airTemperature);
                    if (float5 > float6) {
                        float5 = float6 - 0.25F;
                    }
                }
            }

            float float9 = this.core.celcius - 4.0F;
            if (thermalNode.primaryDelta < 0.0F) {
                float float10 = 0.4F + 0.6F * thermalNode.distToCore;
                float float11 = float9 - 12.0F * float10 / float2;
                float9 = PZMath.c_lerp(float9, float11, PZMath.abs(thermalNode.primaryDelta));
            } else {
                float float12 = 0.4F + 0.6F * (1.0F - thermalNode.distToCore);
                float float13 = 4.0F * float12;
                float13 *= Math.max(float2 * 0.5F * float12, 1.0F);
                float float14 = Math.min(float9 + float13, float6);
                float9 = PZMath.c_lerp(float9, float14, thermalNode.primaryDelta);
            }

            float9 = PZMath.clamp(float9, float5, float6);
            float float15 = float9 - thermalNode.skinCelcius;
            float float16 = 1.0F;
            if (float15 < 0.0F && thermalNode.skinCelcius > 33.0F) {
                float16 = 3.0F;
            } else if (float15 > 0.0F && thermalNode.skinCelcius < 33.0F) {
                float16 = 3.0F;
            }

            thermalNode.skinCelcius = thermalNode.skinCelcius
                + float15 * float16 * this.getSimulationMultiplier(Thermoregulator_tryouts.Multiplier.SkinCelcius);
            if (thermalNode != this.core) {
                if (thermalNode.skinCelcius >= this.core.celcius) {
                    thermalNode.celcius = this.core.celcius;
                } else {
                    thermalNode.celcius = PZMath.lerp(thermalNode.skinCelcius, this.core.celcius, 0.5F);
                }
            }
        }

        this.primTotal = float0;
        this.secTotal = float1;
    }

    private void updateBodyMultipliers() {
        this.energyMultiplier = 1.0;
        this.fluidsMultiplier = 1.0;
        this.fatigueMultiplier = 1.0;
        float float0 = this.metabolicRateReal / Metabolics.Default.getMet();
        this.energyMultiplier = PZMath.clamp(float0 * float0, 1.0F, 5.0F);
        float float1 = PZMath.abs(this.primTotal);
        float1 *= float1;
        if (this.primTotal < 0.0F) {
            this.energyMultiplier += 0.75F * float1;
            this.fatigueMultiplier += 0.5F * float1;
        } else if (this.primTotal > 0.0F) {
            this.fluidsMultiplier += 0.75F * float1;
            this.fatigueMultiplier += 0.5F * float1;
        }

        float1 = PZMath.abs(this.secTotal);
        float1 *= float1;
        if (this.secTotal < 0.0F) {
            this.energyMultiplier += 8.0F * float1;
            this.fatigueMultiplier += 3.5F * float1;
        } else if (this.secTotal > 0.0F) {
            this.fluidsMultiplier += 6.0F * float1;
            this.fatigueMultiplier += 3.5F * float1;
        }
    }

    private void updateClothing() {
        this.character.getItemVisuals(itemVisuals);
        boolean boolean0 = itemVisuals.size() != itemVisualsCache.size();
        if (!boolean0) {
            for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
                if (int0 >= itemVisualsCache.size() || itemVisuals.get(int0) != itemVisualsCache.get(int0)) {
                    boolean0 = true;
                    break;
                }
            }
        }

        if (boolean0) {
            for (int int1 = 0; int1 < this.nodes.length; int1++) {
                this.nodes[int1].clothing.clear();
            }

            itemVisualsCache.clear();

            for (int int2 = 0; int2 < itemVisuals.size(); int2++) {
                ItemVisual itemVisual = itemVisuals.get(int2);
                InventoryItem item = itemVisual.getInventoryItem();
                itemVisualsCache.add(itemVisual);
                if (item instanceof Clothing clothing && (clothing.getInsulation() > 0.0F || clothing.getWindresistance() > 0.0F)) {
                    boolean boolean1 = false;
                    ArrayList arrayList = item.getBloodClothingType();
                    if (arrayList != null) {
                        coveredParts.clear();
                        BloodClothingType.getCoveredParts(arrayList, coveredParts);

                        for (int int3 = 0; int3 < coveredParts.size(); int3++) {
                            BloodBodyPartType bloodBodyPartType = coveredParts.get(int3);
                            if (bloodBodyPartType.index() >= 0 && bloodBodyPartType.index() < this.nodes.length) {
                                boolean1 = true;
                                this.nodes[bloodBodyPartType.index()].clothing.add(clothing);
                            }
                        }
                    }

                    if (!boolean1 && clothing.getBodyLocation() != null) {
                        String string = clothing.getBodyLocation().toLowerCase();
                        switch (string) {
                            case "hat":
                            case "mask":
                                this.nodes[BodyPartType.ToIndex(BodyPartType.Head)].clothing.add(clothing);
                        }
                    }
                }
            }
        }
    }

    public float getEnergy() {
        float float0 = 1.0F - this.stats.getHunger() * this.stats.getHunger();
        float float1 = 1.0F - this.stats.getFatigue() * this.stats.getFatigue();
        return 0.6F * float0 + 0.4F * float1;
    }

    public float getBodyFluids() {
        return 1.0F - this.stats.getThirst();
    }

    private static enum Multiplier {
        Default,
        MetabolicRateInc,
        MetabolicRateDec,
        BodyHeat,
        CoreHeatExpand,
        CoreHeatContract,
        SkinCelcius,
        SkinCelciusContract,
        SkinCelciusExpand,
        PrimaryDelta,
        SecondaryDelta;
    }

    public class ThermalNode {
        private final float distToCore;
        private final float skinSurface;
        private final BodyPartType bodyPartType;
        private final BloodBodyPartType bloodBPT;
        private final BodyPart bodyPart;
        private final boolean isCore;
        private final float insulationLayerMultiplierUI;
        private Thermoregulator_tryouts.ThermalNode upstream;
        private Thermoregulator_tryouts.ThermalNode[] downstream;
        private float insulation;
        private float windresist;
        private float celcius = 37.0F;
        private float skinCelcius = 33.0F;
        private float heatDelta = 0.0F;
        private float primaryDelta = 0.0F;
        private float secondaryDelta = 0.0F;
        private float clothingWetness = 0.0F;
        private float bodyWetness = 0.0F;
        private ArrayList<Clothing> clothing = new ArrayList<>();

        public ThermalNode(float float0, BodyPart bodyPartx, float float1) {
            this(false, float0, bodyPartx, float1);
        }

        public ThermalNode(boolean boolean0, float float0, BodyPart bodyPartx, float float1) {
            this.isCore = boolean0;
            this.celcius = float0;
            this.distToCore = BodyPartType.GetDistToCore(bodyPartx.Type);
            this.skinSurface = BodyPartType.GetSkinSurface(bodyPartx.Type);
            this.bodyPartType = bodyPartx.Type;
            this.bloodBPT = BloodBodyPartType.FromIndex(BodyPartType.ToIndex(bodyPartx.Type));
            this.bodyPart = bodyPartx;
            this.insulationLayerMultiplierUI = float1;
        }

        private void calculateInsulation() {
            int int0 = this.clothing.size();
            this.insulation = 0.0F;
            this.windresist = 0.0F;
            this.clothingWetness = 0.0F;
            this.bodyWetness = this.bodyPart != null ? this.bodyPart.getWetness() * 0.01F : 0.0F;
            this.bodyWetness = PZMath.clamp_01(this.bodyWetness);
            if (int0 > 0) {
                for (int int1 = 0; int1 < int0; int1++) {
                    Clothing clothingx = this.clothing.get(int1);
                    ItemVisual itemVisual = clothingx.getVisual();
                    float float0 = PZMath.clamp(clothingx.getWetness() * 0.01F, 0.0F, 1.0F);
                    this.clothingWetness += float0;
                    boolean boolean0 = itemVisual.getHole(this.bloodBPT) > 0.0F;
                    if (!boolean0) {
                        float float1 = Temperature.getTrueInsulationValue(clothingx.getInsulation());
                        float float2 = Temperature.getTrueWindresistanceValue(clothingx.getWindresistance());
                        float float3 = PZMath.clamp(clothingx.getCurrentCondition() * 0.01F, 0.0F, 1.0F);
                        float3 = 0.5F + 0.5F * float3;
                        float1 *= (1.0F - float0 * 0.75F) * float3;
                        float2 *= (1.0F - float0 * 0.45F) * float3;
                        this.insulation += float1;
                        this.windresist += float2;
                    }
                }

                this.clothingWetness /= int0;
                this.insulation += int0 * 0.05F;
                this.windresist += int0 * 0.05F;
            }
        }

        public boolean hasUpstream() {
            return this.upstream != null;
        }

        public boolean hasDownstream() {
            return this.downstream != null && this.downstream.length > 0;
        }

        public float getDistToCore() {
            return this.distToCore;
        }

        public float getSkinSurface() {
            return this.skinSurface;
        }

        public boolean isCore() {
            return this.isCore;
        }

        public float getInsulation() {
            return this.insulation;
        }

        public float getWindresist() {
            return this.windresist;
        }

        public float getCelcius() {
            return this.celcius;
        }

        public float getSkinCelcius() {
            return this.skinCelcius;
        }

        public float getHeatDelta() {
            return this.heatDelta;
        }

        public float getPrimaryDelta() {
            return this.primaryDelta;
        }

        public float getSecondaryDelta() {
            return this.secondaryDelta;
        }

        public float getClothingWetness() {
            return this.clothingWetness;
        }

        public float getBodyWetness() {
            return this.bodyWetness;
        }

        public float getBodyResponse() {
            return PZMath.lerp(this.primaryDelta, this.secondaryDelta, 0.5F);
        }

        public float getSkinCelciusUI() {
            float float0 = PZMath.clamp(this.getSkinCelcius(), 20.0F, 42.0F);
            if (float0 < 33.0F) {
                float0 = (float0 - 20.0F) / 13.0F * 0.5F;
            } else {
                float0 = 0.5F + (float0 - 33.0F) / 9.0F;
            }

            return float0;
        }

        public float getHeatDeltaUI() {
            return PZMath.clamp((this.heatDelta * 0.2F + 1.0F) / 2.0F, 0.0F, 1.0F);
        }

        public float getPrimaryDeltaUI() {
            return PZMath.clamp((this.primaryDelta + 1.0F) / 2.0F, 0.0F, 1.0F);
        }

        public float getSecondaryDeltaUI() {
            return PZMath.clamp((this.secondaryDelta + 1.0F) / 2.0F, 0.0F, 1.0F);
        }

        public float getInsulationUI() {
            return PZMath.clamp(this.insulation * this.insulationLayerMultiplierUI, 0.0F, 1.0F);
        }

        public float getWindresistUI() {
            return PZMath.clamp(this.windresist * this.insulationLayerMultiplierUI, 0.0F, 1.0F);
        }

        public float getClothingWetnessUI() {
            return PZMath.clamp(this.clothingWetness, 0.0F, 1.0F);
        }

        public float getBodyWetnessUI() {
            return PZMath.clamp(this.bodyWetness, 0.0F, 1.0F);
        }

        public float getBodyResponseUI() {
            return PZMath.clamp((this.getBodyResponse() + 1.0F) / 2.0F, 0.0F, 1.0F);
        }
    }
}
