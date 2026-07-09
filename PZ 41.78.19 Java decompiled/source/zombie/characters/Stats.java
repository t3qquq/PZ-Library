// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;

public final class Stats {
    public float Anger = 0.0F;
    public float boredom = 0.0F;
    public float endurance = 1.0F;
    public boolean enduranceRecharging = false;
    public float endurancelast = 1.0F;
    public float endurancedanger = 0.25F;
    public float endurancewarn = 0.5F;
    public float fatigue = 0.0F;
    public float fitness = 1.0F;
    public float hunger = 0.0F;
    public float idleboredom = 0.0F;
    public float morale = 0.5F;
    public float stress = 0.0F;
    public float Fear = 0.0F;
    public float Panic = 0.0F;
    public float Sanity = 1.0F;
    public float Sickness = 0.0F;
    public float Boredom = 0.0F;
    public float Pain = 0.0F;
    public float Drunkenness = 0.0F;
    public int NumVisibleZombies = 0;
    public int LastNumVisibleZombies = 0;
    public boolean Tripping = false;
    public float TrippingRotAngle = 0.0F;
    public float thirst = 0.0F;
    public int NumChasingZombies = 0;
    public int LastVeryCloseZombies = 0;
    public static int NumCloseZombies = 0;
    public int LastNumChasingZombies = 0;
    public float stressFromCigarettes = 0.0F;
    public float ChasingZombiesDanger;
    public int MusicZombiesVisible = 0;
    public int MusicZombiesTargeting = 0;

    public int getNumVisibleZombies() {
        return this.NumVisibleZombies;
    }

    public int getNumChasingZombies() {
        return this.LastNumChasingZombies;
    }

    public int getNumVeryCloseZombies() {
        return this.LastVeryCloseZombies;
    }

    public void load(DataInputStream input) throws IOException {
        this.Anger = input.readFloat();
        this.boredom = input.readFloat();
        this.endurance = input.readFloat();
        this.fatigue = input.readFloat();
        this.fitness = input.readFloat();
        this.hunger = input.readFloat();
        this.morale = input.readFloat();
        this.stress = input.readFloat();
        this.Fear = input.readFloat();
        this.Panic = input.readFloat();
        this.Sanity = input.readFloat();
        this.Sickness = input.readFloat();
        this.Boredom = input.readFloat();
        this.Pain = input.readFloat();
        this.Drunkenness = input.readFloat();
        this.thirst = input.readFloat();
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.Anger = input.getFloat();
        this.boredom = input.getFloat();
        this.endurance = input.getFloat();
        this.fatigue = input.getFloat();
        this.fitness = input.getFloat();
        this.hunger = input.getFloat();
        this.morale = input.getFloat();
        this.stress = input.getFloat();
        this.Fear = input.getFloat();
        this.Panic = input.getFloat();
        this.Sanity = input.getFloat();
        this.Sickness = input.getFloat();
        this.Boredom = input.getFloat();
        this.Pain = input.getFloat();
        this.Drunkenness = input.getFloat();
        this.thirst = input.getFloat();
        if (WorldVersion >= 97) {
            this.stressFromCigarettes = input.getFloat();
        }
    }

    public void save(DataOutputStream output) throws IOException {
        output.writeFloat(this.Anger);
        output.writeFloat(this.boredom);
        output.writeFloat(this.endurance);
        output.writeFloat(this.fatigue);
        output.writeFloat(this.fitness);
        output.writeFloat(this.hunger);
        output.writeFloat(this.morale);
        output.writeFloat(this.stress);
        output.writeFloat(this.Fear);
        output.writeFloat(this.Panic);
        output.writeFloat(this.Sanity);
        output.writeFloat(this.Sickness);
        output.writeFloat(this.Boredom);
        output.writeFloat(this.Pain);
        output.writeFloat(this.Drunkenness);
        output.writeFloat(this.thirst);
    }

    public void save(ByteBuffer output) throws IOException {
        output.putFloat(this.Anger);
        output.putFloat(this.boredom);
        output.putFloat(this.endurance);
        output.putFloat(this.fatigue);
        output.putFloat(this.fitness);
        output.putFloat(this.hunger);
        output.putFloat(this.morale);
        output.putFloat(this.stress);
        output.putFloat(this.Fear);
        output.putFloat(this.Panic);
        output.putFloat(this.Sanity);
        output.putFloat(this.Sickness);
        output.putFloat(this.Boredom);
        output.putFloat(this.Pain);
        output.putFloat(this.Drunkenness);
        output.putFloat(this.thirst);
        output.putFloat(this.stressFromCigarettes);
    }

    /**
     * @return the Anger
     */
    public float getAnger() {
        return this.Anger;
    }

    /**
     * 
     * @param _Anger the Anger to set
     */
    public void setAnger(float _Anger) {
        this.Anger = _Anger;
    }

    /**
     * @return the boredom
     */
    public float getBoredom() {
        return this.boredom;
    }

    /**
     * 
     * @param _boredom the boredom to set
     */
    public void setBoredom(float _boredom) {
        this.boredom = _boredom;
    }

    /**
     * @return the endurance
     */
    public float getEndurance() {
        return this.endurance;
    }

    /**
     * 
     * @param _endurance the endurance to set
     */
    public void setEndurance(float _endurance) {
        this.endurance = _endurance;
    }

    /**
     * @return the endurancelast
     */
    public float getEndurancelast() {
        return this.endurancelast;
    }

    /**
     * 
     * @param _endurancelast the endurancelast to set
     */
    public void setEndurancelast(float _endurancelast) {
        this.endurancelast = _endurancelast;
    }

    /**
     * @return the endurancedanger
     */
    public float getEndurancedanger() {
        return this.endurancedanger;
    }

    /**
     * 
     * @param _endurancedanger the endurancedanger to set
     */
    public void setEndurancedanger(float _endurancedanger) {
        this.endurancedanger = _endurancedanger;
    }

    /**
     * @return the endurancewarn
     */
    public float getEndurancewarn() {
        return this.endurancewarn;
    }

    /**
     * 
     * @param _endurancewarn the endurancewarn to set
     */
    public void setEndurancewarn(float _endurancewarn) {
        this.endurancewarn = _endurancewarn;
    }

    public boolean getEnduranceRecharging() {
        return this.enduranceRecharging;
    }

    /**
     * @return the fatigue
     */
    public float getFatigue() {
        return this.fatigue;
    }

    /**
     * 
     * @param _fatigue the fatigue to set
     */
    public void setFatigue(float _fatigue) {
        this.fatigue = _fatigue;
    }

    /**
     * @return the fitness
     */
    public float getFitness() {
        return this.fitness;
    }

    /**
     * 
     * @param _fitness the fitness to set
     */
    public void setFitness(float _fitness) {
        this.fitness = _fitness;
    }

    /**
     * @return the hunger
     */
    public float getHunger() {
        return this.hunger;
    }

    /**
     * 
     * @param _hunger the hunger to set
     */
    public void setHunger(float _hunger) {
        this.hunger = _hunger;
    }

    /**
     * @return the idleboredom
     */
    public float getIdleboredom() {
        return this.idleboredom;
    }

    /**
     * 
     * @param _idleboredom the idleboredom to set
     */
    public void setIdleboredom(float _idleboredom) {
        this.idleboredom = _idleboredom;
    }

    /**
     * @return the morale
     */
    public float getMorale() {
        return this.morale;
    }

    /**
     * 
     * @param _morale the morale to set
     */
    public void setMorale(float _morale) {
        this.morale = _morale;
    }

    /**
     * @return the stress
     */
    public float getStress() {
        return this.stress + this.getStressFromCigarettes();
    }

    /**
     * 
     * @param _stress the stress to set
     */
    public void setStress(float _stress) {
        this.stress = _stress;
    }

    public float getStressFromCigarettes() {
        return this.stressFromCigarettes;
    }

    public void setStressFromCigarettes(float _stressFromCigarettes) {
        this.stressFromCigarettes = PZMath.clamp(_stressFromCigarettes, 0.0F, this.getMaxStressFromCigarettes());
    }

    public float getMaxStressFromCigarettes() {
        return 0.51F;
    }

    /**
     * @return the Fear
     */
    public float getFear() {
        return this.Fear;
    }

    /**
     * 
     * @param _Fear the Fear to set
     */
    public void setFear(float _Fear) {
        this.Fear = _Fear;
    }

    /**
     * @return the Panic
     */
    public float getPanic() {
        return this.Panic;
    }

    /**
     * 
     * @param _Panic the Panic to set
     */
    public void setPanic(float _Panic) {
        this.Panic = _Panic;
    }

    /**
     * @return the Sanity
     */
    public float getSanity() {
        return this.Sanity;
    }

    /**
     * 
     * @param _Sanity the Sanity to set
     */
    public void setSanity(float _Sanity) {
        this.Sanity = _Sanity;
    }

    /**
     * @return the Sickness
     */
    public float getSickness() {
        return this.Sickness;
    }

    /**
     * 
     * @param _Sickness the Sickness to set
     */
    public void setSickness(float _Sickness) {
        this.Sickness = _Sickness;
    }

    /**
     * @return the Pain
     */
    public float getPain() {
        return this.Pain;
    }

    /**
     * 
     * @param _Pain the Pain to set
     */
    public void setPain(float _Pain) {
        this.Pain = _Pain;
    }

    /**
     * @return the Drunkenness
     */
    public float getDrunkenness() {
        return this.Drunkenness;
    }

    /**
     * 
     * @param _Drunkenness the Drunkenness to set
     */
    public void setDrunkenness(float _Drunkenness) {
        this.Drunkenness = _Drunkenness;
    }

    /**
     * @return the NumVisibleZombies
     */
    public int getVisibleZombies() {
        return this.NumVisibleZombies;
    }

    /**
     * 
     * @param _NumVisibleZombies the NumVisibleZombies to set
     */
    public void setNumVisibleZombies(int _NumVisibleZombies) {
        this.NumVisibleZombies = _NumVisibleZombies;
    }

    /**
     * @return the Tripping
     */
    public boolean isTripping() {
        return this.Tripping;
    }

    /**
     * 
     * @param _Tripping the Tripping to set
     */
    public void setTripping(boolean _Tripping) {
        this.Tripping = _Tripping;
    }

    /**
     * @return the TrippingRotAngle
     */
    public float getTrippingRotAngle() {
        return this.TrippingRotAngle;
    }

    /**
     * 
     * @param _TrippingRotAngle the TrippingRotAngle to set
     */
    public void setTrippingRotAngle(float _TrippingRotAngle) {
        this.TrippingRotAngle = _TrippingRotAngle;
    }

    /**
     * @return the thirst
     */
    public float getThirst() {
        return this.thirst;
    }

    /**
     * 
     * @param _thirst the thirst to set
     */
    public void setThirst(float _thirst) {
        this.thirst = _thirst;
    }

    public void resetStats() {
        this.Anger = 0.0F;
        this.boredom = 0.0F;
        this.fatigue = 0.0F;
        this.hunger = 0.0F;
        this.idleboredom = 0.0F;
        this.morale = 0.5F;
        this.stress = 0.0F;
        this.Fear = 0.0F;
        this.Panic = 0.0F;
        this.Sanity = 1.0F;
        this.Sickness = 0.0F;
        this.Boredom = 0.0F;
        this.Pain = 0.0F;
        this.Drunkenness = 0.0F;
        this.thirst = 0.0F;
    }
}
