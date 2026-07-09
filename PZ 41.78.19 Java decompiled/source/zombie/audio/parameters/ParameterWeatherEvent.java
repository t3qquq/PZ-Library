// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.SandboxOptions;
import zombie.audio.FMODGlobalParameter;
import zombie.iso.weather.ClimateManager;

public final class ParameterWeatherEvent extends FMODGlobalParameter {
    private ParameterWeatherEvent.Event event = ParameterWeatherEvent.Event.None;

    public ParameterWeatherEvent() {
        super("WeatherEvent");
    }

    @Override
    public float calculateCurrentValue() {
        float float0 = ClimateManager.getInstance().getSnowFracNow();
        if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
            float0 = 0.0F;
        }

        return this.event.value;
    }

    public static enum Event {
        None(0),
        FreshSnow(1);

        final int value;

        private Event(int int1) {
            this.value = int1;
        }
    }
}
