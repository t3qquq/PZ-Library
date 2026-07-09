// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import org.lwjgl.opengl.ARBShaderObjects;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoCamera;
import zombie.iso.PlayerCamera;
import zombie.iso.SearchMode;

public class WeatherShader extends Shader {
    public int timeOfDay = 0;
    private int PixelOffset;
    private int PixelSize;
    private int bloom;
    private int timer;
    private int BlurStrength;
    private int TextureSize;
    private int Zoom;
    private int Light;
    private int LightIntensity;
    private int NightValue;
    private int Exterior;
    private int NightVisionGoggles;
    private int DesaturationVal;
    private int FogMod;
    private int SearchModeID;
    private int ScreenInfo;
    private int ParamInfo;
    private int VarInfo;
    private int timerVal;
    private boolean bAlt = false;
    private static final int texdVarsSize = 22;
    private static float[][] floatArrs = new float[5][];

    public WeatherShader(String string) {
        super(string);
    }

    @Override
    public void startMainThread(TextureDraw textureDraw, int int0) {
        if (int0 >= 0 && int0 < 4) {
            RenderSettings.PlayerRenderSettings playerRenderSettings = RenderSettings.getInstance().getPlayerSettings(int0);
            IsoPlayer player = IsoPlayer.players[int0];
            boolean boolean0 = playerRenderSettings.isExterior();
            float float0 = GameTime.instance.TimeOfDay / 12.0F - 1.0F;
            if (Math.abs(float0) > 0.8F && player != null && player.Traits.NightVision.isSet() && !player.isWearingNightVisionGoggles()) {
                float0 *= 0.8F;
            }

            int int1 = Core.getInstance().getOffscreenWidth(int0);
            int int2 = Core.getInstance().getOffscreenHeight(int0);
            if (textureDraw.vars == null) {
                textureDraw.vars = getFreeFloatArray();
                if (textureDraw.vars == null) {
                    textureDraw.vars = new float[22];
                }
            }

            textureDraw.vars[0] = playerRenderSettings.getBlendColor().r;
            textureDraw.vars[1] = playerRenderSettings.getBlendColor().g;
            textureDraw.vars[2] = playerRenderSettings.getBlendColor().b;
            textureDraw.vars[3] = playerRenderSettings.getBlendIntensity();
            textureDraw.vars[4] = playerRenderSettings.getDesaturation();
            textureDraw.vars[5] = playerRenderSettings.isApplyNightVisionGoggles() ? 1.0F : 0.0F;
            SearchMode.PlayerSearchMode playerSearchMode = SearchMode.getInstance().getSearchModeForPlayer(int0);
            textureDraw.vars[6] = playerSearchMode.getShaderBlur();
            textureDraw.vars[7] = playerSearchMode.getShaderRadius();
            textureDraw.vars[8] = IsoCamera.getOffscreenLeft(int0);
            textureDraw.vars[9] = IsoCamera.getOffscreenTop(int0);
            PlayerCamera playerCamera = IsoCamera.cameras[int0];
            textureDraw.vars[10] = IsoCamera.getOffscreenWidth(int0);
            textureDraw.vars[11] = IsoCamera.getOffscreenHeight(int0);
            textureDraw.vars[12] = playerCamera.RightClickX;
            textureDraw.vars[13] = playerCamera.RightClickY;
            textureDraw.vars[14] = Core.getInstance().getZoom(int0);
            textureDraw.vars[15] = Core.TileScale == 2 ? 64.0F : 32.0F;
            textureDraw.vars[16] = playerSearchMode.getShaderGradientWidth() * textureDraw.vars[15] / 2.0F;
            textureDraw.vars[17] = playerSearchMode.getShaderDesat();
            textureDraw.vars[18] = playerSearchMode.isShaderEnabled() ? 1.0F : 0.0F;
            textureDraw.vars[19] = playerSearchMode.getShaderDarkness();
            textureDraw.flipped = playerRenderSettings.isExterior();
            textureDraw.f1 = playerRenderSettings.getDarkness();
            textureDraw.col0 = int1;
            textureDraw.col1 = int2;
            textureDraw.col2 = Core.getInstance().getOffscreenTrueWidth();
            textureDraw.col3 = Core.getInstance().getOffscreenTrueHeight();
            textureDraw.bSingleCol = Core.getInstance().getZoom(int0) > 2.0F
                || Core.getInstance().getZoom(int0) < 2.0 && Core.getInstance().getZoom(int0) >= 1.75F;
        }
    }

    @Override
    public void startRenderThread(TextureDraw textureDraw) {
        float float0 = textureDraw.f1;
        boolean boolean0 = textureDraw.flipped;
        int int0 = textureDraw.col0;
        int int1 = textureDraw.col1;
        int int2 = textureDraw.col2;
        int int3 = textureDraw.col3;
        float float1 = textureDraw.bSingleCol ? 1.0F : 0.0F;
        ARBShaderObjects.glUniform1fARB(this.width, int0);
        ARBShaderObjects.glUniform1fARB(this.height, int1);
        ARBShaderObjects.glUniform3fARB(this.Light, textureDraw.vars[0], textureDraw.vars[1], textureDraw.vars[2]);
        ARBShaderObjects.glUniform1fARB(this.LightIntensity, textureDraw.vars[3]);
        ARBShaderObjects.glUniform1fARB(this.NightValue, float0);
        ARBShaderObjects.glUniform1fARB(this.DesaturationVal, textureDraw.vars[4]);
        ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, textureDraw.vars[5]);
        ARBShaderObjects.glUniform1fARB(this.Exterior, boolean0 ? 1.0F : 0.0F);
        ARBShaderObjects.glUniform1fARB(this.timer, this.timerVal / 2);
        if (PerformanceSettings.getLockFPS() >= 60) {
            if (this.bAlt) {
                this.timerVal++;
            }

            this.bAlt = !this.bAlt;
        } else {
            this.timerVal += 2;
        }

        float float2 = 0.0F;
        float float3 = 0.0F;
        float float4 = 1.0F / int0;
        float float5 = 1.0F / int1;
        ARBShaderObjects.glUniform2fARB(this.TextureSize, int2, int3);
        ARBShaderObjects.glUniform1fARB(this.Zoom, float1);
        ARBShaderObjects.glUniform4fARB(this.SearchModeID, textureDraw.vars[6], textureDraw.vars[7], textureDraw.vars[8], textureDraw.vars[9]);
        ARBShaderObjects.glUniform4fARB(this.ScreenInfo, textureDraw.vars[10], textureDraw.vars[11], textureDraw.vars[12], textureDraw.vars[13]);
        ARBShaderObjects.glUniform4fARB(this.ParamInfo, textureDraw.vars[14], textureDraw.vars[15], textureDraw.vars[16], textureDraw.vars[17]);
        ARBShaderObjects.glUniform4fARB(this.VarInfo, textureDraw.vars[18], textureDraw.vars[19], textureDraw.vars[20], textureDraw.vars[21]);
    }

    @Override
    public void onCompileSuccess(ShaderProgram var1) {
        int int0 = this.getID();
        this.timeOfDay = ARBShaderObjects.glGetUniformLocationARB(int0, "TimeOfDay");
        this.bloom = ARBShaderObjects.glGetUniformLocationARB(int0, "BloomVal");
        this.PixelOffset = ARBShaderObjects.glGetUniformLocationARB(int0, "PixelOffset");
        this.PixelSize = ARBShaderObjects.glGetUniformLocationARB(int0, "PixelSize");
        this.BlurStrength = ARBShaderObjects.glGetUniformLocationARB(int0, "BlurStrength");
        this.width = ARBShaderObjects.glGetUniformLocationARB(int0, "bgl_RenderedTextureWidth");
        this.height = ARBShaderObjects.glGetUniformLocationARB(int0, "bgl_RenderedTextureHeight");
        this.timer = ARBShaderObjects.glGetUniformLocationARB(int0, "timer");
        this.TextureSize = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureSize");
        this.Zoom = ARBShaderObjects.glGetUniformLocationARB(int0, "Zoom");
        this.Light = ARBShaderObjects.glGetUniformLocationARB(int0, "Light");
        this.LightIntensity = ARBShaderObjects.glGetUniformLocationARB(int0, "LightIntensity");
        this.NightValue = ARBShaderObjects.glGetUniformLocationARB(int0, "NightValue");
        this.Exterior = ARBShaderObjects.glGetUniformLocationARB(int0, "Exterior");
        this.NightVisionGoggles = ARBShaderObjects.glGetUniformLocationARB(int0, "NightVisionGoggles");
        this.DesaturationVal = ARBShaderObjects.glGetUniformLocationARB(int0, "DesaturationVal");
        this.FogMod = ARBShaderObjects.glGetUniformLocationARB(int0, "FogMod");
        this.SearchModeID = ARBShaderObjects.glGetUniformLocationARB(int0, "SearchMode");
        this.ScreenInfo = ARBShaderObjects.glGetUniformLocationARB(int0, "ScreenInfo");
        this.ParamInfo = ARBShaderObjects.glGetUniformLocationARB(int0, "ParamInfo");
        this.VarInfo = ARBShaderObjects.glGetUniformLocationARB(int0, "VarInfo");
    }

    @Override
    public void postRender(TextureDraw textureDraw) {
        if (textureDraw.vars != null) {
            returnFloatArray(textureDraw.vars);
            textureDraw.vars = null;
        }
    }

    private static float[] getFreeFloatArray() {
        for (int int0 = 0; int0 < floatArrs.length; int0++) {
            if (floatArrs[int0] != null) {
                float[] floats = floatArrs[int0];
                floatArrs[int0] = null;
                return floats;
            }
        }

        return new float[22];
    }

    private static void returnFloatArray(float[] floats) {
        for (int int0 = 0; int0 < floatArrs.length; int0++) {
            if (floatArrs[int0] == null) {
                floatArrs[int0] = floats;
                break;
            }
        }
    }
}
