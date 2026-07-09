// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;

public final class ParameterRainIntensity extends FMODGlobalParameter {
    public ParameterRainIntensity() {
        super("RainIntensity");
    }

    @Override
    public float calculateCurrentValue() {
        return ClimateManager.getInstance().isRaining() ? ClimateManager.getInstance().getPrecipitationIntensity() : 0.0F;
    }
}
