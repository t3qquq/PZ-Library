// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public class Character extends OptionGroup {
    public final BooleanDebugOption CreateAllOutfits = newOption(this.Group, "Create.AllOutfits", false);
    public final Character.DebugOG Debug = new Character.DebugOG(this.Group);

    public Character() {
        super("Character");
    }

    public static final class DebugOG extends OptionGroup {
        public final Character.DebugOG.RenderOG Render = new Character.DebugOG.RenderOG(this.Group);
        public final Character.DebugOG.AnimateOG Animate = new Character.DebugOG.AnimateOG(this.Group);
        public final BooleanDebugOption RegisterDebugVariables = newDebugOnlyOption(this.Group, "DebugVariables", false);
        public final BooleanDebugOption AlwaysTripOverFence = newDebugOnlyOption(this.Group, "AlwaysTripOverFence", false);
        public final BooleanDebugOption PlaySoundWhenInvisible = newDebugOnlyOption(this.Group, "PlaySoundWhenInvisible", false);
        public final BooleanDebugOption UpdateAlpha = newDebugOnlyOption(this.Group, "UpdateAlpha", true);
        public final BooleanDebugOption UpdateAlphaEighthSpeed = newDebugOnlyOption(this.Group, "UpdateAlphaEighthSpeed", false);

        public DebugOG(IDebugOptionGroup parentGroup) {
            super(parentGroup, "Debug");
        }

        public static final class AnimateOG extends OptionGroup {
            public final BooleanDebugOption DeferredRotationOnly = newDebugOnlyOption(this.Group, "DeferredRotationsOnly", false);
            public final BooleanDebugOption NoBoneMasks = newDebugOnlyOption(this.Group, "NoBoneMasks", false);
            public final BooleanDebugOption NoBoneTwists = newDebugOnlyOption(this.Group, "NoBoneTwists", false);
            public final BooleanDebugOption ZeroCounterRotationBone = newDebugOnlyOption(this.Group, "ZeroCounterRotation", false);

            public AnimateOG(IDebugOptionGroup parentGroup) {
                super(parentGroup, "Animate");
            }
        }

        public static final class RenderOG extends OptionGroup {
            public final BooleanDebugOption AimCone = newDebugOnlyOption(this.Group, "AimCone", false);
            public final BooleanDebugOption Angle = newDebugOnlyOption(this.Group, "Angle", false);
            public final BooleanDebugOption TestDotSide = newDebugOnlyOption(this.Group, "TestDotSide", false);
            public final BooleanDebugOption DeferredMovement = newDebugOnlyOption(this.Group, "DeferredMovement", false);
            public final BooleanDebugOption DeferredAngles = newDebugOnlyOption(this.Group, "DeferredRotation", false);
            public final BooleanDebugOption TranslationData = newDebugOnlyOption(this.Group, "Translation_Data", false);
            public final BooleanDebugOption Bip01 = newDebugOnlyOption(this.Group, "Bip01", false);
            public final BooleanDebugOption PrimaryHandBone = newDebugOnlyOption(this.Group, "HandBones.Primary", false);
            public final BooleanDebugOption SecondaryHandBone = newDebugOnlyOption(this.Group, "HandBones.Secondary", false);
            public final BooleanDebugOption SkipCharacters = newDebugOnlyOption(this.Group, "SkipCharacters", false);
            public final BooleanDebugOption Vision = newDebugOnlyOption(this.Group, "Vision", false);
            public final BooleanDebugOption DisplayRoomAndZombiesZone = newDebugOnlyOption(this.Group, "DisplayRoomAndZombiesZone", false);
            public final BooleanDebugOption FMODRoomType = newDebugOnlyOption(this.Group, "FMODRoomType", false);

            public RenderOG(IDebugOptionGroup parentGroup) {
                super(parentGroup, "Render");
            }
        }
    }
}
