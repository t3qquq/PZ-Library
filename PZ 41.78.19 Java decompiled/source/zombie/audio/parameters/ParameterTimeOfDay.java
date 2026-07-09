// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.GameTime;
import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;

public final class ParameterTimeOfDay extends FMODGlobalParameter {
    public ParameterTimeOfDay() {
        super("TimeOfDay");
    }

    @Override
    public float calculateCurrentValue() {
        ClimateManager.DayInfo dayInfo = ClimateManager.getInstance().getCurrentDay();
        if (dayInfo == null) {
            return 1.0F;
        } else {
            float float0 = dayInfo.season.getDawn();
            float float1 = dayInfo.season.getDusk();
            float float2 = dayInfo.season.getDayHighNoon();
            float float3 = GameTime.instance.getTimeOfDay();
            if (float3 >= float0 - 1.0F && float3 < float0 + 1.0F) {
                return 0.0F;
            } else if (float3 >= float0 + 1.0F && float3 < float0 + 2.0F) {
                return 1.0F;
            } else if (float3 >= float0 + 2.0F && float3 < float1 - 2.0F) {
                return 2.0F;
            } else if (float3 >= float1 - 2.0F && float3 < float1 - 1.0F) {
                return 3.0F;
            } else {
                return float3 >= float1 - 1.0F && float3 < float1 + 1.0F ? 4.0F : 5.0F;
            }
        }
    }
}
