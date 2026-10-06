// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;

public final class ParameterWeatherEvent extends FMODGlobalParameter {
    private final ParameterWeatherEvent.Event event = ParameterWeatherEvent.Event.None;

    public ParameterWeatherEvent() {
        super("WeatherEvent");
    }

    @Override
    public float calculateCurrentValue() {
        return this.event.value;
    }

    public enum Event {
        None(0),
        FreshSnow(1);

        final int value;

        Event(final int value) {
            this.value = value;
        }
    }
}
