// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderSettings;
import zombie.debug.LineDrawer;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.iso.weather.fx.SteppedUpdateFloat;

/**
 * TurboTuTone.
 */
public class WorldFlares {
    public static final boolean ENABLED = true;
    public static boolean DEBUG_DRAW = false;
    public static int NEXT_ID = 0;
    private static ArrayList<WorldFlares.Flare> flares = new ArrayList<>();

    public static void Clear() {
        flares.clear();
    }

    public static int getFlareCount() {
        return flares.size();
    }

    public static WorldFlares.Flare getFlare(int index) {
        return flares.get(index);
    }

    public static WorldFlares.Flare getFlareID(int id) {
        for (int int0 = 0; int0 < flares.size(); int0++) {
            if (flares.get(int0).id == id) {
                return flares.get(int0);
            }
        }

        return null;
    }

    public static void launchFlare(float lifetime, int x, int y, int range, float windSpeed, float r, float g, float b, float ri, float gi, float bi) {
        if (flares.size() > 100) {
            flares.remove(0);
        }

        WorldFlares.Flare flare = new WorldFlares.Flare();
        flare.id = NEXT_ID++;
        flare.x = x;
        flare.y = y;
        flare.range = range;
        flare.windSpeed = windSpeed;
        flare.color.setExterior(r, g, b, 1.0F);
        flare.color.setInterior(ri, gi, bi, 1.0F);
        flare.hasLaunched = true;
        flare.maxLifeTime = lifetime;
        flares.add(flare);
    }

    public static void update() {
        for (int int0 = flares.size() - 1; int0 >= 0; int0--) {
            flares.get(int0).update();
            if (!flares.get(int0).hasLaunched) {
                flares.remove(int0);
            }
        }
    }

    public static void applyFlaresForPlayer(RenderSettings.PlayerRenderSettings renderSettings, int plrIndex, IsoPlayer player) {
        for (int int0 = flares.size() - 1; int0 >= 0; int0--) {
            if (flares.get(int0).hasLaunched) {
                flares.get(int0).applyFlare(renderSettings, plrIndex, player);
            }
        }
    }

    public static void setDebugDraw(boolean b) {
        DEBUG_DRAW = b;
    }

    public static boolean getDebugDraw() {
        return DEBUG_DRAW;
    }

    public static void debugRender() {
        if (DEBUG_DRAW) {
            float float0 = 0.0F;

            for (int int0 = flares.size() - 1; int0 >= 0; int0--) {
                WorldFlares.Flare flare = flares.get(int0);
                float float1 = 0.5F;

                for (double double0 = 0.0; double0 < Math.PI * 2; double0 += Math.PI / 20) {
                    DrawIsoLine(
                        flare.x + flare.range * (float)Math.cos(double0),
                        flare.y + flare.range * (float)Math.sin(double0),
                        flare.x + flare.range * (float)Math.cos(double0 + (Math.PI / 20)),
                        flare.y + flare.range * (float)Math.sin(double0 + (Math.PI / 20)),
                        float0,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.25F,
                        1
                    );
                    DrawIsoLine(
                        flare.x + float1 * (float)Math.cos(double0),
                        flare.y + float1 * (float)Math.sin(double0),
                        flare.x + float1 * (float)Math.cos(double0 + (Math.PI / 20)),
                        flare.y + float1 * (float)Math.sin(double0 + (Math.PI / 20)),
                        float0,
                        1.0F,
                        1.0F,
                        1.0F,
                        0.25F,
                        1
                    );
                }
            }
        }
    }

    private static void DrawIsoLine(
        float float1, float float2, float float6, float float7, float float3, float float9, float float10, float float11, float float12, int int0
    ) {
        float float0 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
        float float4 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
        float float5 = IsoUtils.XToScreenExact(float6, float7, float3, 0);
        float float8 = IsoUtils.YToScreenExact(float6, float7, float3, 0);
        LineDrawer.drawLine(float0, float4, float5, float8, float9, float10, float11, float12, int0);
    }

    public static class Flare {
        private int id;
        private float x;
        private float y;
        private int range;
        private float windSpeed = 0.0F;
        private ClimateColorInfo color = new ClimateColorInfo(1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
        private boolean hasLaunched = false;
        private SteppedUpdateFloat intensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
        private float maxLifeTime;
        private float lifeTime;
        private int nextRandomTargetIntens = 10;
        private float perc = 0.0F;
        private WorldFlares.PlayerFlareLightInfo[] infos = new WorldFlares.PlayerFlareLightInfo[4];

        public Flare() {
            for (int int0 = 0; int0 < this.infos.length; int0++) {
                this.infos[int0] = new WorldFlares.PlayerFlareLightInfo();
            }
        }

        public int getId() {
            return this.id;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public int getRange() {
            return this.range;
        }

        public float getWindSpeed() {
            return this.windSpeed;
        }

        public ClimateColorInfo getColor() {
            return this.color;
        }

        public boolean isHasLaunched() {
            return this.hasLaunched;
        }

        public float getIntensity() {
            return this.intensity.value();
        }

        public float getMaxLifeTime() {
            return this.maxLifeTime;
        }

        public float getLifeTime() {
            return this.lifeTime;
        }

        public float getPercent() {
            return this.perc;
        }

        public float getIntensityPlayer(int index) {
            return this.infos[index].intensity;
        }

        public float getLerpPlayer(int index) {
            return this.infos[index].lerp;
        }

        public float getDistModPlayer(int index) {
            return this.infos[index].distMod;
        }

        public ClimateColorInfo getColorPlayer(int index) {
            return this.infos[index].flareCol;
        }

        public ClimateColorInfo getOutColorPlayer(int index) {
            return this.infos[index].outColor;
        }

        private int GetDistance(int int2, int int0, int int3, int int1) {
            return (int)Math.sqrt(Math.pow(int2 - int3, 2.0) + Math.pow(int0 - int1, 2.0));
        }

        private void update() {
            if (this.hasLaunched) {
                if (this.lifeTime > this.maxLifeTime) {
                    this.hasLaunched = false;
                    return;
                }

                this.perc = this.lifeTime / this.maxLifeTime;
                this.nextRandomTargetIntens = (int)(this.nextRandomTargetIntens - GameTime.instance.getMultiplier());
                if (this.nextRandomTargetIntens <= 0) {
                    this.intensity.setTarget(Rand.Next(0.8F, 1.0F));
                    this.nextRandomTargetIntens = Rand.Next(5, 30);
                }

                this.intensity.update(GameTime.instance.getMultiplier());
                if (this.windSpeed > 0.0F) {
                    Vector2 vector = new Vector2(
                        this.windSpeed
                            / 60.0F
                            * ClimateManager.getInstance().getWindIntensity()
                            * (float)Math.sin(ClimateManager.getInstance().getWindAngleRadians()),
                        this.windSpeed
                            / 60.0F
                            * ClimateManager.getInstance().getWindIntensity()
                            * (float)Math.cos(ClimateManager.getInstance().getWindAngleRadians())
                    );
                    this.x = this.x + vector.x * GameTime.instance.getMultiplier();
                    this.y = this.y + vector.y * GameTime.instance.getMultiplier();
                }

                for (int int0 = 0; int0 < 4; int0++) {
                    WorldFlares.PlayerFlareLightInfo playerFlareLightInfo = this.infos[int0];
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player == null) {
                        playerFlareLightInfo.intensity = 0.0F;
                    } else {
                        int int1 = this.GetDistance((int)this.x, (int)this.y, (int)player.getX(), (int)player.getY());
                        if (int1 > this.range) {
                            playerFlareLightInfo.intensity = 0.0F;
                            playerFlareLightInfo.lerp = 1.0F;
                        } else {
                            playerFlareLightInfo.distMod = 1.0F - (float)int1 / this.range;
                            if (this.perc < 0.75F) {
                                playerFlareLightInfo.lerp = 0.0F;
                            } else {
                                playerFlareLightInfo.lerp = (this.perc - 0.75F) / 0.25F;
                            }

                            playerFlareLightInfo.intensity = this.intensity.value();
                        }

                        float float0 = (1.0F - playerFlareLightInfo.lerp) * playerFlareLightInfo.distMod * playerFlareLightInfo.intensity;
                        ClimateManager.ClimateFloat climateFloat = ClimateManager.getInstance().dayLightStrength;
                        climateFloat.finalValue = climateFloat.finalValue + (1.0F - ClimateManager.getInstance().dayLightStrength.finalValue) * float0;
                        if (player != null) {
                            player.dirtyRecalcGridStackTime = 1.0F;
                        }
                    }
                }

                this.lifeTime = this.lifeTime + GameTime.instance.getMultiplier();
            }
        }

        private void applyFlare(RenderSettings.PlayerRenderSettings playerRenderSettings, int int0, IsoPlayer var3) {
            WorldFlares.PlayerFlareLightInfo playerFlareLightInfo = this.infos[int0];
            if (playerFlareLightInfo.distMod > 0.0F) {
                float float0 = 1.0F - playerRenderSettings.CM_DayLightStrength;
                float0 = playerRenderSettings.CM_NightStrength > float0 ? playerRenderSettings.CM_NightStrength : float0;
                float0 = PZMath.clamp(float0 * 2.0F, 0.0F, 1.0F);
                float float1 = 1.0F - playerFlareLightInfo.lerp;
                float1 *= playerFlareLightInfo.distMod;
                ClimateColorInfo climateColorInfo = playerRenderSettings.CM_GlobalLight;
                playerFlareLightInfo.outColor.setTo(climateColorInfo);
                Color colorx = playerFlareLightInfo.outColor.getExterior();
                float float2 = float0 * float1 * playerFlareLightInfo.intensity;
                colorx.g = playerFlareLightInfo.outColor.getExterior().g * (1.0F - float2 * 0.5F);
                colorx = playerFlareLightInfo.outColor.getInterior();
                float2 = float0 * float1 * playerFlareLightInfo.intensity;
                colorx.g = playerFlareLightInfo.outColor.getInterior().g * (1.0F - float2 * 0.5F);
                colorx = playerFlareLightInfo.outColor.getExterior();
                float2 = float0 * float1 * playerFlareLightInfo.intensity;
                colorx.b = playerFlareLightInfo.outColor.getExterior().b * (1.0F - float2 * 0.8F);
                colorx = playerFlareLightInfo.outColor.getInterior();
                float2 = float0 * float1 * playerFlareLightInfo.intensity;
                colorx.b = playerFlareLightInfo.outColor.getInterior().b * (1.0F - float2 * 0.8F);
                playerFlareLightInfo.flareCol.setTo(this.color);
                playerFlareLightInfo.flareCol.scale(float0);
                playerFlareLightInfo.flareCol.getExterior().a = 1.0F;
                playerFlareLightInfo.flareCol.getInterior().a = 1.0F;
                playerFlareLightInfo.outColor.getExterior().r = playerFlareLightInfo.outColor.getExterior().r > playerFlareLightInfo.flareCol.getExterior().r
                    ? playerFlareLightInfo.outColor.getExterior().r
                    : playerFlareLightInfo.flareCol.getExterior().r;
                playerFlareLightInfo.outColor.getExterior().g = playerFlareLightInfo.outColor.getExterior().g > playerFlareLightInfo.flareCol.getExterior().g
                    ? playerFlareLightInfo.outColor.getExterior().g
                    : playerFlareLightInfo.flareCol.getExterior().g;
                playerFlareLightInfo.outColor.getExterior().b = playerFlareLightInfo.outColor.getExterior().b > playerFlareLightInfo.flareCol.getExterior().b
                    ? playerFlareLightInfo.outColor.getExterior().b
                    : playerFlareLightInfo.flareCol.getExterior().b;
                playerFlareLightInfo.outColor.getExterior().a = playerFlareLightInfo.outColor.getExterior().a > playerFlareLightInfo.flareCol.getExterior().a
                    ? playerFlareLightInfo.outColor.getExterior().a
                    : playerFlareLightInfo.flareCol.getExterior().a;
                playerFlareLightInfo.outColor.getInterior().r = playerFlareLightInfo.outColor.getInterior().r > playerFlareLightInfo.flareCol.getInterior().r
                    ? playerFlareLightInfo.outColor.getInterior().r
                    : playerFlareLightInfo.flareCol.getInterior().r;
                playerFlareLightInfo.outColor.getInterior().g = playerFlareLightInfo.outColor.getInterior().g > playerFlareLightInfo.flareCol.getInterior().g
                    ? playerFlareLightInfo.outColor.getInterior().g
                    : playerFlareLightInfo.flareCol.getInterior().g;
                playerFlareLightInfo.outColor.getInterior().b = playerFlareLightInfo.outColor.getInterior().b > playerFlareLightInfo.flareCol.getInterior().b
                    ? playerFlareLightInfo.outColor.getInterior().b
                    : playerFlareLightInfo.flareCol.getInterior().b;
                playerFlareLightInfo.outColor.getInterior().a = playerFlareLightInfo.outColor.getInterior().a > playerFlareLightInfo.flareCol.getInterior().a
                    ? playerFlareLightInfo.outColor.getInterior().a
                    : playerFlareLightInfo.flareCol.getInterior().a;
                float float3 = 1.0F - float1 * playerFlareLightInfo.intensity;
                playerFlareLightInfo.outColor.interp(climateColorInfo, float3, climateColorInfo);
                float float4 = ClimateManager.lerp(float3, 0.35F, playerRenderSettings.CM_Ambient);
                playerRenderSettings.CM_Ambient = playerRenderSettings.CM_Ambient > float4 ? playerRenderSettings.CM_Ambient : float4;
                float float5 = ClimateManager.lerp(float3, 0.6F * playerFlareLightInfo.intensity, playerRenderSettings.CM_DayLightStrength);
                playerRenderSettings.CM_DayLightStrength = playerRenderSettings.CM_DayLightStrength > float5
                    ? playerRenderSettings.CM_DayLightStrength
                    : float5;
                if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                    float float6 = ClimateManager.lerp(float3, 1.0F * float0, playerRenderSettings.CM_Desaturation);
                    playerRenderSettings.CM_Desaturation = playerRenderSettings.CM_Desaturation > float6 ? playerRenderSettings.CM_Desaturation : float6;
                }
            }
        }
    }

    private static class PlayerFlareLightInfo {
        private float intensity;
        private float lerp;
        private float distMod;
        private ClimateColorInfo flareCol = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
        private ClimateColorInfo outColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
