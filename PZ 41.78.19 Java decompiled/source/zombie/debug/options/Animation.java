// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Animation extends OptionGroup {
    public final BooleanDebugOption Debug = newDebugOnlyOption(this.Group, "Debug", false);
    public final BooleanDebugOption AllowEarlyTransitionOut = newDebugOnlyOption(this.Group, "AllowEarlyTransitionOut", true);
    public final Animation.AnimLayerOG AnimLayer = new Animation.AnimLayerOG(this.Group);
    public final Animation.SharedSkelesOG SharedSkeles = new Animation.SharedSkelesOG(this.Group);
    public final BooleanDebugOption AnimRenderPicker = newDebugOnlyOption(this.Group, "Render.Picker", false);
    public final BooleanDebugOption BlendUseFbx = newDebugOnlyOption(this.Group, "BlendUseFbx", false);

    public Animation() {
        super("Animation");
    }

    public static final class AnimLayerOG extends OptionGroup {
        public final BooleanDebugOption LogStateChanges = newDebugOnlyOption(this.Group, "Debug.LogStateChanges", false);
        public final BooleanDebugOption AllowAnimNodeOverride = newDebugOnlyOption(this.Group, "Debug.AllowAnimNodeOverride", false);

        AnimLayerOG(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "AnimLayer");
        }
    }

    public static final class SharedSkelesOG extends OptionGroup {
        public final BooleanDebugOption Enabled = newDebugOnlyOption(this.Group, "Enabled", true);
        public final BooleanDebugOption AllowLerping = newDebugOnlyOption(this.Group, "AllowLerping", true);

        SharedSkelesOG(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "SharedSkeles");
        }
    }
}
