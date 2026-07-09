// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PARAMETER_ID;

public abstract class FMODParameter {
    private final String m_name;
    private final FMOD_STUDIO_PARAMETER_DESCRIPTION m_parameterDescription;
    private float m_currentValue = Float.NaN;

    public FMODParameter(String name) {
        this.m_name = name;
        this.m_parameterDescription = FMODManager.instance.getParameterDescription(name);
    }

    public String getName() {
        return this.m_name;
    }

    public FMOD_STUDIO_PARAMETER_DESCRIPTION getParameterDescription() {
        return this.m_parameterDescription;
    }

    public FMOD_STUDIO_PARAMETER_ID getParameterID() {
        return this.m_parameterDescription == null ? null : this.m_parameterDescription.id;
    }

    public float getCurrentValue() {
        return this.m_currentValue;
    }

    public void update() {
        float float0 = this.calculateCurrentValue();
        if (float0 != this.m_currentValue) {
            this.m_currentValue = float0;
            this.setCurrentValue(this.m_currentValue);
        }
    }

    public void resetToDefault() {
    }

    public abstract float calculateCurrentValue();

    public abstract void setCurrentValue(float value);

    public abstract void startEventInstance(long eventInstance);

    public abstract void stopEventInstance(long eventInstance);
}
