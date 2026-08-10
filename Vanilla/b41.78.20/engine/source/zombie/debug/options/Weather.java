// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Weather extends OptionGroup {
    public final BooleanDebugOption Fx = newDebugOnlyOption(this.Group, "Fx", true);
    public final BooleanDebugOption Snow = newDebugOnlyOption(this.Group, "Snow", true);
    public final BooleanDebugOption WaterPuddles = newDebugOnlyOption(this.Group, "WaterPuddles", true);

    public Weather() {
        super("Weather");
    }
}
