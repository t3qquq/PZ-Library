// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import zombie.core.math.PZMath;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.ui.UIManager;

public final class PerformanceSettings {
    public static int ManualFrameSkips = 0;
    private static int s_lockFPS = 60;
    private static boolean s_uncappedFPS = false;
    public static int LightingFrameSkip = 0;
    public static int WaterQuality = 0;
    public static int PuddlesQuality = 0;
    public static boolean NewRoofHiding = true;
    public static boolean LightingThread = true;
    public static int LightingFPS = 15;
    public static boolean auto3DZombies = false;
    public static final PerformanceSettings instance = new PerformanceSettings();
    public static boolean InterpolateAnims = true;
    public static int AnimationSkip = 1;
    public static boolean ModelLighting = true;
    public static int ZombieAnimationSpeedFalloffCount = 6;
    public static int ZombieBonusFullspeedFalloff = 3;
    public static int BaseStaticAnimFramerate = 60;
    public static boolean UseFBOs = false;
    public static int numberZombiesBlended = 20;
    public static int FogQuality = 0;

    public static int getLockFPS() {
        return s_lockFPS;
    }

    public static void setLockFPS(int lockFPS) {
        s_lockFPS = lockFPS;
    }

    public static boolean isUncappedFPS() {
        return s_uncappedFPS;
    }

    public static void setUncappedFPS(boolean uncappedFPS) {
        s_uncappedFPS = uncappedFPS;
    }

    public int getFramerate() {
        return getLockFPS();
    }

    public void setFramerate(int framerate) {
        setLockFPS(framerate);
    }

    public boolean isFramerateUncapped() {
        return isUncappedFPS();
    }

    public void setFramerateUncapped(boolean val) {
        setUncappedFPS(val);
    }

    public void setLightingQuality(int lighting) {
        LightingFrameSkip = lighting;
    }

    public int getLightingQuality() {
        return LightingFrameSkip;
    }

    public void setWaterQuality(int water) {
        WaterQuality = water;
        IsoWater.getInstance().applyWaterQuality();
    }

    public int getWaterQuality() {
        return WaterQuality;
    }

    public void setPuddlesQuality(int puddles) {
        PuddlesQuality = puddles;
        if (puddles > 2 || puddles < 0) {
            PuddlesQuality = 0;
        }

        IsoPuddles.getInstance().applyPuddlesQuality();
    }

    public int getPuddlesQuality() {
        return PuddlesQuality;
    }

    public void setNewRoofHiding(boolean enabled) {
        NewRoofHiding = enabled;
    }

    public boolean getNewRoofHiding() {
        return NewRoofHiding;
    }

    public void setLightingFPS(int fps) {
        fps = Math.max(1, Math.min(120, fps));
        LightingFPS = fps;
        System.out.println("LightingFPS set to " + LightingFPS);
    }

    public int getLightingFPS() {
        return LightingFPS;
    }

    public int getUIRenderFPS() {
        return UIManager.useUIFBO ? Core.OptionUIRenderFPS : s_lockFPS;
    }

    public int getFogQuality() {
        return FogQuality;
    }

    public void setFogQuality(int fogQuality) {
        FogQuality = PZMath.clamp(fogQuality, 0, 2);
    }
}
