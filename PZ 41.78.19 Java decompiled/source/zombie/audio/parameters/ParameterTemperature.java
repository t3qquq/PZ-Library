// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;

public final class ParameterTemperature extends FMODGlobalParameter {
    public ParameterTemperature() {
        super("Temperature");
    }

    @Override
    public float calculateCurrentValue() {
        return (int)(ClimateManager.getInstance().getTemperature() * 100.0F) / 100.0F;
    }
}
