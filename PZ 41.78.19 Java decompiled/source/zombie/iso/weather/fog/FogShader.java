// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.fog;

import org.lwjgl.opengl.ARBShaderObjects;
import zombie.IndieGL;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.ShaderProgram;

public class FogShader {
    public static final FogShader instance = new FogShader();
    private ShaderProgram shaderProgram;
    private int noiseTexture;
    private int screenInfo;
    private int textureInfo;
    private int rectangleInfo;
    private int worldOffset;
    private int scalingInfo;
    private int colorInfo;
    private int paramInfo;
    private int cameraInfo;

    public void initShader() {
        this.shaderProgram = ShaderProgram.createShaderProgram("fog", false, true);
        if (this.shaderProgram.isCompiled()) {
            this.noiseTexture = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "NoiseTexture");
            this.screenInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "screenInfo");
            this.textureInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "textureInfo");
            this.rectangleInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "rectangleInfo");
            this.scalingInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "scalingInfo");
            this.colorInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "colorInfo");
            this.worldOffset = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "worldOffset");
            this.paramInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "paramInfo");
            this.cameraInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "cameraInfo");
            ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
            ARBShaderObjects.glUseProgramObjectARB(0);
        }
    }

    public void setScreenInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.screenInfo, float0, float1, float2, float3);
    }

    public void setTextureInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.textureInfo, float0, float1, float2, float3);
    }

    public void setRectangleInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.rectangleInfo, float0, float1, float2, float3);
    }

    public void setScalingInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.scalingInfo, float0, float1, float2, float3);
    }

    public void setColorInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.colorInfo, float0, float1, float2, float3);
    }

    public void setWorldOffset(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.worldOffset, float0, float1, float2, float3);
    }

    public void setParamInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.paramInfo, float0, float1, float2, float3);
    }

    public void setCameraInfo(float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.cameraInfo, float0, float1, float2, float3);
    }

    public boolean StartShader() {
        if (this.shaderProgram == null) {
            RenderThread.invokeOnRenderContext(this::initShader);
        }

        if (this.shaderProgram.isCompiled()) {
            IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
            return true;
        } else {
            return false;
        }
    }

    protected void reloadShader() {
        if (this.shaderProgram != null) {
            this.shaderProgram = null;
        }
    }
}
