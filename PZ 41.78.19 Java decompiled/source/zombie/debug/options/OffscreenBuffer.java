// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class OffscreenBuffer extends OptionGroup {
    public final BooleanDebugOption Render = newDebugOnlyOption(this.Group, "Render", true);

    public OffscreenBuffer() {
        super("OffscreenBuffer");
    }
}
