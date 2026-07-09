// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.BodyDamage;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class Nutrition {
    private IsoPlayer parent;
    private float carbohydrates = 0.0F;
    private float lipids = 0.0F;
    private float proteins = 0.0F;
    private float calories = 0.0F;
    private float carbohydratesDecreraseFemale = 0.0032F;
    private float carbohydratesDecreraseMale = 0.0035F;
    private float lipidsDecreraseFemale = 7.0E-4F;
    private float lipidsDecreraseMale = 0.00113F;
    private float proteinsDecreraseFemale = 7.0E-4F;
    private float proteinsDecreraseMale = 8.6E-4F;
    private float caloriesDecreraseFemaleNormal = 0.016F;
    private float caloriesDecreaseMaleNormal = 0.016F;
    private float caloriesDecreraseFemaleExercise = 0.13F;
    private float caloriesDecreaseMaleExercise = 0.13F;
    private float caloriesDecreraseFemaleSleeping = 0.003F;
    private float caloriesDecreaseMaleSleeping = 0.003F;
    private int caloriesToGainWeightMale = 1100;
    private int caloriesToGainWeightMaxMale = 4000;
    private int caloriesToGainWeightFemale = 1000;
    private int caloriesToGainWeightMaxFemale = 3000;
    private int caloriesDecreaseMax = 2500;
    private float weightGain = 1.3E-5F;
    private float weightLoss = 8.5E-6F;
    private double weight = 60.0;
    private int updatedWeight = 0;
    private boolean isFemale = false;
    private int syncWeightTimer = 0;
    private float caloriesMax = 0.0F;
    private float caloriesMin = 0.0F;
    private boolean incWeight = false;
    private boolean incWeightLot = false;
    private boolean decWeight = false;

    public Nutrition(IsoPlayer _parent) {
        this.parent = _parent;
        if (this.isFemale) {
            this.setWeight(60.0);
        } else {
            this.setWeight(80.0);
        }

        this.setCalories(800.0F);
    }

    public void update() {
        if (!GameServer.bServer) {
            if (SandboxOptions.instance.Nutrition.getValue()) {
                if (this.parent != null && !this.parent.isDead()) {
                    if (!GameClient.bClient || this.parent.isLocalPlayer()) {
                        this.setCarbohydrates(
                            this.getCarbohydrates()
                                - (this.isFemale ? this.carbohydratesDecreraseFemale : this.carbohydratesDecreraseMale)
                                    * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
                        );
                        this.setLipids(
                            this.getLipids()
                                - (this.isFemale ? this.lipidsDecreraseFemale : this.lipidsDecreraseMale)
                                    * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
                        );
                        this.setProteins(
                            this.getProteins()
                                - (this.isFemale ? this.proteinsDecreraseFemale : this.proteinsDecreraseMale)
                                    * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
                        );
                        this.updateCalories();
                        this.updateWeight();
                    }
                }
            }
        }
    }

    private void updateCalories() {
        float float0 = 1.0F;
        if (!this.parent.getCharacterActions().isEmpty()) {
            float0 = this.parent.getCharacterActions().get(0).caloriesModifier;
        }

        if (this.parent.isCurrentState(SwipeStatePlayer.instance())
            || this.parent.isCurrentState(ClimbOverFenceState.instance())
            || this.parent.isCurrentState(ClimbThroughWindowState.instance())) {
            float0 = 8.0F;
        }

        float float1 = 1.0F;
        if (this.parent.getBodyDamage() != null && this.parent.getBodyDamage().getThermoregulator() != null) {
            float1 = (float)this.parent.getBodyDamage().getThermoregulator().getEnergyMultiplier();
        }

        float float2 = (float)(this.getWeight() / 80.0);
        if (this.parent.IsRunning() && this.parent.isPlayerMoving()) {
            float0 = 1.0F;
            this.setCalories(
                this.getCalories()
                    - (this.isFemale ? this.caloriesDecreraseFemaleExercise : this.caloriesDecreaseMaleExercise)
                        * float0
                        * float2
                        * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
            );
        } else if (this.parent.isSprinting() && this.parent.isPlayerMoving()) {
            float0 = 1.3F;
            this.setCalories(
                this.getCalories()
                    - (this.isFemale ? this.caloriesDecreraseFemaleExercise : this.caloriesDecreaseMaleExercise)
                        * float0
                        * float2
                        * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
            );
        } else if (this.parent.isPlayerMoving()) {
            float0 = 0.6F;
            this.setCalories(
                this.getCalories()
                    - (this.isFemale ? this.caloriesDecreraseFemaleExercise : this.caloriesDecreaseMaleExercise)
                        * float0
                        * float2
                        * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
            );
        } else if (this.parent.isAsleep()) {
            this.setCalories(
                this.getCalories()
                    - (this.isFemale ? this.caloriesDecreraseFemaleSleeping : this.caloriesDecreaseMaleSleeping)
                        * float0
                        * float1
                        * float2
                        * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
            );
        } else {
            this.setCalories(
                this.getCalories()
                    - (this.isFemale ? this.caloriesDecreraseFemaleNormal : this.caloriesDecreaseMaleNormal)
                        * float0
                        * float1
                        * float2
                        * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate()
            );
        }

        if (this.getCalories() > this.caloriesMax) {
            this.caloriesMax = this.getCalories();
        }

        if (this.getCalories() < this.caloriesMin) {
            this.caloriesMin = this.getCalories();
        }
    }

    private void updateWeight() {
        this.setIncWeight(false);
        this.setIncWeightLot(false);
        this.setDecWeight(false);
        float float0 = this.caloriesToGainWeightMale;
        float float1 = this.caloriesToGainWeightMaxMale;
        float float2 = 0.0F;
        if (this.isFemale) {
            float0 = this.caloriesToGainWeightFemale;
            float1 = this.caloriesToGainWeightMaxFemale;
        }

        float float3 = (float)((this.getWeight() - 80.0) * 40.0);
        float0 = 1600.0F + float3;
        float2 = (float)((this.getWeight() - 70.0) * 30.0);
        if (float2 > 0.0F) {
            float2 = 0.0F;
        }

        if (this.getCalories() > float0) {
            this.setIncWeight(true);
            float float4 = this.getCalories() / float1;
            if (float4 > 1.0F) {
                float4 = 1.0F;
            }

            float float5 = this.weightGain;
            if (this.getCarbohydrates() > 700.0F || this.getLipids() > 700.0F) {
                float5 *= 3.0F;
                this.setIncWeightLot(true);
            } else if (this.getCarbohydrates() > 400.0F || this.getLipids() > 400.0F) {
                float5 *= 2.0F;
                this.setIncWeightLot(true);
            }

            this.setWeight(this.getWeight() + float5 * float4 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        } else if (this.getCalories() < float2) {
            this.setDecWeight(true);
            float float6 = Math.abs(this.getCalories()) / this.caloriesDecreaseMax;
            if (float6 > 1.0F) {
                float6 = 1.0F;
            }

            this.setWeight(this.getWeight() - this.weightLoss * float6 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }

        this.updatedWeight++;
        if (this.updatedWeight >= 2000) {
            this.applyTraitFromWeight();
            this.updatedWeight = 0;
        }

        if (GameClient.bClient) {
            this.syncWeightTimer++;
            if (this.syncWeightTimer >= 5000) {
                GameClient.sendWeight(this.parent);
                this.syncWeightTimer = 0;
            }
        }
    }

    public void syncWeight() {
        GameClient.sendWeight(this.parent);
    }

    public void save(ByteBuffer output) {
        output.putFloat(this.getCalories());
        output.putFloat(this.getProteins());
        output.putFloat(this.getLipids());
        output.putFloat(this.getCarbohydrates());
        output.putFloat((float)this.getWeight());
    }

    public void load(ByteBuffer input) {
        this.setCalories(input.getFloat());
        this.setProteins(input.getFloat());
        this.setLipids(input.getFloat());
        this.setCarbohydrates(input.getFloat());
        this.setWeight(input.getFloat());
    }

    public void applyWeightFromTraits() {
        if (this.parent.getTraits() != null && !this.parent.getTraits().isEmpty()) {
            if (this.parent.Traits.Emaciated.isSet()) {
                this.setWeight(50.0);
            }

            if (this.parent.Traits.VeryUnderweight.isSet()) {
                this.setWeight(60.0);
            }

            if (this.parent.Traits.Underweight.isSet()) {
                this.setWeight(70.0);
            }

            if (this.parent.Traits.Overweight.isSet()) {
                this.setWeight(95.0);
            }

            if (this.parent.Traits.Obese.isSet()) {
                this.setWeight(105.0);
            }
        }
    }

    /**
     * > 100 obese 85 to 100 over weight 75 to 85 normal 65 to 75 underweight 50 to  65 very underweight <= 50 emaciated
     */
    public void applyTraitFromWeight() {
        this.parent.getTraits().remove("Underweight");
        this.parent.getTraits().remove("Very Underweight");
        this.parent.getTraits().remove("Emaciated");
        this.parent.getTraits().remove("Overweight");
        this.parent.getTraits().remove("Obese");
        if (this.getWeight() >= 100.0) {
            this.parent.getTraits().add("Obese");
        }

        if (this.getWeight() >= 85.0 && this.getWeight() < 100.0) {
            this.parent.getTraits().add("Overweight");
        }

        if (this.getWeight() > 65.0 && this.getWeight() <= 75.0) {
            this.parent.getTraits().add("Underweight");
        }

        if (this.getWeight() > 50.0 && this.getWeight() <= 65.0) {
            this.parent.getTraits().add("Very Underweight");
        }

        if (this.getWeight() <= 50.0) {
            this.parent.getTraits().add("Emaciated");
        }
    }

    public boolean characterHaveWeightTrouble() {
        return this.parent.Traits.Emaciated.isSet()
            || this.parent.Traits.Obese.isSet()
            || this.parent.Traits.VeryUnderweight.isSet()
            || this.parent.Traits.Underweight.isSet()
            || this.parent.Traits.Overweight.isSet();
    }

    /**
     * You gain xp only if you're in good shape As underweight or overweight you can  still be "fit"
     */
    public boolean canAddFitnessXp() {
        if (this.parent.getPerkLevel(PerkFactory.Perks.Fitness) >= 9 && this.characterHaveWeightTrouble()) {
            return false;
        } else {
            return this.parent.getPerkLevel(PerkFactory.Perks.Fitness) < 6
                ? true
                : !this.parent.Traits.Emaciated.isSet() && !this.parent.Traits.Obese.isSet() && !this.parent.Traits.VeryUnderweight.isSet();
        }
    }

    public float getCarbohydrates() {
        return this.carbohydrates;
    }

    public void setCarbohydrates(float _carbohydrates) {
        if (_carbohydrates < -500.0F) {
            _carbohydrates = -500.0F;
        }

        if (_carbohydrates > 1000.0F) {
            _carbohydrates = 1000.0F;
        }

        this.carbohydrates = _carbohydrates;
    }

    public float getProteins() {
        return this.proteins;
    }

    public void setProteins(float _proteins) {
        if (_proteins < -500.0F) {
            _proteins = -500.0F;
        }

        if (_proteins > 1000.0F) {
            _proteins = 1000.0F;
        }

        this.proteins = _proteins;
    }

    public float getCalories() {
        return this.calories;
    }

    public void setCalories(float _calories) {
        if (_calories < -2200.0F) {
            _calories = -2200.0F;
        }

        if (_calories > 3700.0F) {
            _calories = 3700.0F;
        }

        this.calories = _calories;
    }

    public float getLipids() {
        return this.lipids;
    }

    public void setLipids(float _lipids) {
        if (_lipids < -500.0F) {
            _lipids = -500.0F;
        }

        if (_lipids > 1000.0F) {
            _lipids = 1000.0F;
        }

        this.lipids = _lipids;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double _weight) {
        if (_weight < 35.0) {
            _weight = 35.0;
            float float0 = this.parent.getBodyDamage().getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
            this.parent.getBodyDamage().ReduceGeneralHealth(float0);
            LuaEventManager.triggerEvent("OnPlayerGetDamage", this.parent, "LOWWEIGHT", float0);
        }

        this.weight = _weight;
    }

    public boolean isIncWeight() {
        return this.incWeight;
    }

    public void setIncWeight(boolean _incWeight) {
        this.incWeight = _incWeight;
    }

    public boolean isIncWeightLot() {
        return this.incWeightLot;
    }

    public void setIncWeightLot(boolean _incWeightLot) {
        this.incWeightLot = _incWeightLot;
    }

    public boolean isDecWeight() {
        return this.decWeight;
    }

    public void setDecWeight(boolean _decWeight) {
        this.decWeight = _decWeight;
    }
}
