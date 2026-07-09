// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import zombie.GameTime;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugOptions;
import zombie.interfaces.ITexture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;

public class SkyBox extends IsoObject {
    private static SkyBox instance;
    public IsoSpriteInstance def = null;
    private TextureFBO textureFBOA;
    private TextureFBO textureFBOB;
    private boolean isCurrentA;
    public Shader Effect;
    private final UpdateLimit renderLimit = new UpdateLimit(1000L);
    private boolean isUpdated = false;
    private int SkyBoxTime;
    private float SkyBoxParamCloudCount;
    private float SkyBoxParamCloudSize;
    private final Vector3f SkyBoxParamSunLight = new Vector3f();
    private final Color SkyBoxParamSunColor = new Color(1.0F, 1.0F, 1.0F);
    private final Color SkyBoxParamSkyHColour = new Color(1.0F, 1.0F, 1.0F);
    private final Color SkyBoxParamSkyLColour = new Color(1.0F, 1.0F, 1.0F);
    private float SkyBoxParamCloudLight;
    private float SkyBoxParamStars;
    private float SkyBoxParamFog;
    private final Vector3f SkyBoxParamWind;
    private boolean isSetAVG = false;
    private float SkyBoxParamCloudCountAVG;
    private float SkyBoxParamCloudSizeAVG;
    private final Vector3f SkyBoxParamSunLightAVG = new Vector3f();
    private final Color SkyBoxParamSunColorAVG = new Color(1.0F, 1.0F, 1.0F);
    private final Color SkyBoxParamSkyHColourAVG = new Color(1.0F, 1.0F, 1.0F);
    private final Color SkyBoxParamSkyLColourAVG = new Color(1.0F, 1.0F, 1.0F);
    private float SkyBoxParamCloudLightAVG;
    private float SkyBoxParamStarsAVG;
    private float SkyBoxParamFogAVG;
    private final Vector3f SkyBoxParamWindINT;
    private Texture texAM;
    private Texture texPM;
    private final Color SkyHColourDay = new Color(0.1F, 0.1F, 0.4F);
    private final Color SkyHColourDawn = new Color(0.2F, 0.2F, 0.3F);
    private final Color SkyHColourDusk = new Color(0.2F, 0.2F, 0.3F);
    private final Color SkyHColourNight = new Color(0.01F, 0.01F, 0.04F);
    private final Color SkyLColourDay = new Color(0.1F, 0.45F, 0.7F);
    private final Color SkyLColourDawn = new Color(0.1F, 0.4F, 0.6F);
    private final Color SkyLColourDusk = new Color(0.1F, 0.4F, 0.6F);
    private final Color SkyLColourNight = new Color(0.01F, 0.045F, 0.07F);
    private int apiId;

    public static synchronized SkyBox getInstance() {
        if (instance == null) {
            instance = new SkyBox();
        }

        return instance;
    }

    public void update(ClimateManager cm) {
        if (!this.isUpdated) {
            this.isUpdated = true;
            GameTime gameTime = GameTime.getInstance();
            ClimateManager.DayInfo dayInfo = cm.getCurrentDay();
            float float0 = dayInfo.season.getDawn();
            float float1 = dayInfo.season.getDusk();
            float float2 = dayInfo.season.getDayHighNoon();
            float float3 = gameTime.getTimeOfDay();
            if (float3 < float0 || float3 > float1) {
                float float4 = 24.0F - float1 + float0;
                if (float3 > float1) {
                    float float5 = (float3 - float1) / float4;
                    this.SkyHColourDusk.interp(this.SkyHColourDawn, float5, this.SkyBoxParamSkyHColour);
                    this.SkyLColourDusk.interp(this.SkyLColourDawn, float5, this.SkyBoxParamSkyLColour);
                    this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
                    this.SkyBoxParamSunLight.normalize();
                    this.SkyBoxParamSunLight.mul(Math.min(1.0F, float5 * 5.0F));
                } else {
                    float float6 = (24.0F - float1 + float3) / float4;
                    this.SkyHColourDusk.interp(this.SkyHColourDawn, float6, this.SkyBoxParamSkyHColour);
                    this.SkyLColourDusk.interp(this.SkyLColourDawn, float6, this.SkyBoxParamSkyLColour);
                    this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
                    this.SkyBoxParamSunLight.normalize();
                    this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float6) * 5.0F));
                }

                this.SkyBoxParamSunColor.set(cm.getGlobalLight().getExterior());
                this.SkyBoxParamSunColor.scale(cm.getNightStrength());
            } else if (float3 < float2) {
                float float7 = (float3 - float0) / (float2 - float0);
                this.SkyHColourDawn.interp(this.SkyHColourDay, float7, this.SkyBoxParamSkyHColour);
                this.SkyLColourDawn.interp(this.SkyLColourDay, float7, this.SkyBoxParamSkyLColour);
                this.SkyBoxParamSunLight.set(4.0F * float7 - 4.0F, 0.22F, 0.3F);
                this.SkyBoxParamSunLight.normalize();
                this.SkyBoxParamSunLight.mul(Math.min(1.0F, float7 * 10.0F));
                this.SkyBoxParamSunColor.set(cm.getGlobalLight().getExterior());
            } else {
                float float8 = (float3 - float2) / (float1 - float2);
                this.SkyHColourDay.interp(this.SkyHColourDusk, float8, this.SkyBoxParamSkyHColour);
                this.SkyLColourDay.interp(this.SkyLColourDusk, float8, this.SkyBoxParamSkyLColour);
                this.SkyBoxParamSunLight.set(4.0F * float8, 0.22F, 0.3F);
                this.SkyBoxParamSunLight.normalize();
                this.SkyBoxParamSunLight.mul(Math.min(1.0F, (1.0F - float8) * 10.0F));
                this.SkyBoxParamSunColor.set(cm.getGlobalLight().getExterior());
            }

            this.SkyBoxParamSkyHColour.interp(this.SkyHColourNight, cm.getNightStrength(), this.SkyBoxParamSkyHColour);
            this.SkyBoxParamSkyLColour.interp(this.SkyLColourNight, cm.getNightStrength(), this.SkyBoxParamSkyLColour);
            this.SkyBoxParamCloudCount = Math.min(Math.max(cm.getCloudIntensity(), cm.getPrecipitationIntensity() * 2.0F), 0.999F);
            this.SkyBoxParamCloudSize = 0.02F + cm.getTemperature() / 70.0F;
            this.SkyBoxParamFog = cm.getFogIntensity();
            this.SkyBoxParamStars = cm.getNightStrength();
            this.SkyBoxParamCloudLight = (float)(1.0 - (1.0 - 1.0 * Math.pow(1000.0, -cm.getPrecipitationIntensity() - cm.getNightStrength())));
            float float9 = (1.0F - (cm.getWindAngleIntensity() + 1.0F) * 0.5F + 0.25F) % 1.0F;
            float9 *= 360.0F;
            this.SkyBoxParamWind.set((float)Math.cos(Math.toRadians(float9)), 0.0F, (float)Math.sin(Math.toRadians(float9)));
            this.SkyBoxParamWind.mul(cm.getWindIntensity());
            if (!this.isSetAVG) {
                this.isSetAVG = true;
                this.SkyBoxParamCloudCountAVG = this.SkyBoxParamCloudCount;
                this.SkyBoxParamCloudSizeAVG = this.SkyBoxParamCloudSize;
                this.SkyBoxParamSunLightAVG.set(this.SkyBoxParamSunLight);
                this.SkyBoxParamSunColorAVG.set(this.SkyBoxParamSunColor);
                this.SkyBoxParamSkyHColourAVG.set(this.SkyBoxParamSkyHColour);
                this.SkyBoxParamSkyLColourAVG.set(this.SkyBoxParamSkyLColour);
                this.SkyBoxParamCloudLightAVG = this.SkyBoxParamCloudLight;
                this.SkyBoxParamStarsAVG = this.SkyBoxParamStars;
                this.SkyBoxParamFogAVG = this.SkyBoxParamFog;
                this.SkyBoxParamWindINT.set(this.SkyBoxParamWind);
            } else {
                this.SkyBoxParamCloudCountAVG = this.SkyBoxParamCloudCountAVG + (this.SkyBoxParamCloudCount - this.SkyBoxParamCloudCountAVG) * 0.1F;
                this.SkyBoxParamCloudSizeAVG = this.SkyBoxParamCloudSizeAVG + (this.SkyBoxParamCloudSizeAVG + this.SkyBoxParamCloudSize) * 0.1F;
                this.SkyBoxParamSunLightAVG.lerp(this.SkyBoxParamSunLight, 0.1F);
                this.SkyBoxParamSunColorAVG.interp(this.SkyBoxParamSunColor, 0.1F, this.SkyBoxParamSunColorAVG);
                this.SkyBoxParamSkyHColourAVG.interp(this.SkyBoxParamSkyHColour, 0.1F, this.SkyBoxParamSkyHColourAVG);
                this.SkyBoxParamSkyLColourAVG.interp(this.SkyBoxParamSkyLColour, 0.1F, this.SkyBoxParamSkyLColourAVG);
                this.SkyBoxParamCloudLightAVG = this.SkyBoxParamCloudLightAVG + (this.SkyBoxParamCloudLight - this.SkyBoxParamCloudLightAVG) * 0.1F;
                this.SkyBoxParamStarsAVG = this.SkyBoxParamStarsAVG + (this.SkyBoxParamStars - this.SkyBoxParamStarsAVG) * 0.1F;
                this.SkyBoxParamFogAVG = this.SkyBoxParamFogAVG + (this.SkyBoxParamFog - this.SkyBoxParamFogAVG) * 0.1F;
                this.SkyBoxParamWindINT.add(this.SkyBoxParamWind);
            }
        }
    }

    public int getShaderTime() {
        return this.SkyBoxTime;
    }

    public float getShaderCloudCount() {
        return this.SkyBoxParamCloudCount;
    }

    public float getShaderCloudSize() {
        return this.SkyBoxParamCloudSize;
    }

    public Vector3f getShaderSunLight() {
        return this.SkyBoxParamSunLight;
    }

    public Color getShaderSunColor() {
        return this.SkyBoxParamSunColor;
    }

    public Color getShaderSkyHColour() {
        return this.SkyBoxParamSkyHColour;
    }

    public Color getShaderSkyLColour() {
        return this.SkyBoxParamSkyLColour;
    }

    public float getShaderCloudLight() {
        return this.SkyBoxParamCloudLight;
    }

    public float getShaderStars() {
        return this.SkyBoxParamStars;
    }

    public float getShaderFog() {
        return this.SkyBoxParamFog;
    }

    public Vector3f getShaderWind() {
        return this.SkyBoxParamWindINT;
    }

    public SkyBox() {
        this.texAM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
        this.texPM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");

        try {
            Texture texture0 = new Texture(512, 512, 16);
            Texture texture1 = new Texture(512, 512, 16);
            this.textureFBOA = new TextureFBO(texture0);
            this.textureFBOB = new TextureFBO(texture1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.def = IsoSpriteInstance.get(this.sprite);
        this.SkyBoxTime = 0;
        this.SkyBoxParamSunLight.set(0.35F, 0.22F, 0.3F);
        this.SkyBoxParamSunColor.set(1.0F, 0.86F, 0.7F, 1.0F);
        this.SkyBoxParamSkyHColour.set(0.1F, 0.1F, 0.4F, 1.0F);
        this.SkyBoxParamSkyLColour.set(0.1F, 0.45F, 0.7F, 1.0F);
        this.SkyBoxParamCloudLight = 0.99F;
        this.SkyBoxParamCloudCount = 0.3F;
        this.SkyBoxParamCloudSize = 0.2F;
        this.SkyBoxParamFog = 0.0F;
        this.SkyBoxParamStars = 0.0F;
        this.SkyBoxParamWind = new Vector3f(0.0F);
        this.SkyBoxParamWindINT = new Vector3f(0.0F);
        RenderThread.invokeOnRenderContext(() -> {
            if (Core.getInstance().getPerfSkybox() == 0) {
                this.Effect = new SkyBoxShader("skybox_hires");
            } else {
                this.Effect = new SkyBoxShader("skybox");
            }

            if (GL.getCapabilities().OpenGL30) {
                this.apiId = 1;
            }

            if (GL.getCapabilities().GL_ARB_framebuffer_object) {
                this.apiId = 2;
            }

            if (GL.getCapabilities().GL_EXT_framebuffer_object) {
                this.apiId = 3;
            }
        });
    }

    public ITexture getTextureCurrent() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return this.texAM;
        } else {
            return this.isCurrentA ? this.textureFBOA.getTexture() : this.textureFBOB.getTexture();
        }
    }

    public ITexture getTexturePrev() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return this.texPM;
        } else {
            return this.isCurrentA ? this.textureFBOB.getTexture() : this.textureFBOA.getTexture();
        }
    }

    public TextureFBO getTextureFBOPrev() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return null;
        } else {
            return this.isCurrentA ? this.textureFBOB : this.textureFBOA;
        }
    }

    public float getTextureShift() {
        return Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2
            ? (float)this.renderLimit.getTimePeriod()
            : 1.0F - GameTime.getInstance().getNight();
    }

    public void swapTextureFBO() {
        this.renderLimit.updateTimePeriod();
        this.isCurrentA = !this.isCurrentA;
    }

    public void render() {
        if (Core.getInstance().getUseShaders() && Core.getInstance().getPerfSkybox() != 2) {
            if (!this.renderLimit.Check()) {
                if (GameTime.getInstance().getMultiplier() >= 20.0F) {
                    this.SkyBoxTime++;
                }
            } else {
                this.SkyBoxTime++;
                int int0 = IsoCamera.frameState.playerIndex;
                int int1 = IsoCamera.getOffscreenLeft(int0);
                int int2 = IsoCamera.getOffscreenTop(int0);
                int int3 = IsoCamera.getOffscreenWidth(int0);
                int int4 = IsoCamera.getOffscreenHeight(int0);
                SpriteRenderer.instance.drawSkyBox(this.Effect, int0, this.apiId, this.getTextureFBOPrev().getBufferId());
                this.isUpdated = false;
            }
        }
    }

    public void draw() {
        if (Core.bDebug && DebugOptions.instance.SkyboxShow.getValue()) {
            ((Texture)this.getTextureCurrent()).render(0.0F, 0.0F, 512.0F, 512.0F, 1.0F, 1.0F, 1.0F, 1.0F, null);
        }
    }
}
