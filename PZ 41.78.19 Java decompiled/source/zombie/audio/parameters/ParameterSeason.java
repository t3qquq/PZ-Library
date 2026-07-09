// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;

public final class ParameterSeason extends FMODGlobalParameter {
    public ParameterSeason() {
        super("Season");
    }

    @Override
    public float calculateCurrentValue() {
        ClimateManager.DayInfo dayInfo = ClimateManager.getInstance().getCurrentDay();
        if (dayInfo == null) {
            return 0.0F;
        } else {
            return switch (dayInfo.season.getSeason()) {
                case 1 -> 0.0F;
                case 2, 3 -> 1.0F;
                case 4 -> 2.0F;
                case 5 -> 3.0F;
                default -> 1.0F;
            };
        }
    }
}
