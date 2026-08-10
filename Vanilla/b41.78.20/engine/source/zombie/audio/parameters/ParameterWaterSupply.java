// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.audio.FMODGlobalParameter;

public final class ParameterWaterSupply extends FMODGlobalParameter {
    public ParameterWaterSupply() {
        super("Water");
    }

    @Override
    public float calculateCurrentValue() {
        return GameTime.instance.NightsSurvived < SandboxOptions.instance.getWaterShutModifier() ? 1.0F : 0.0F;
    }
}
