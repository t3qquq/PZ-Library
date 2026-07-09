// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureDraw;

final class SkyBoxShader extends Shader {
    private int SkyBoxTime;
    private int SkyBoxParamCloudCount;
    private int SkyBoxParamCloudSize;
    private int SkyBoxParamSunLight;
    private int SkyBoxParamSunColor;
    private int SkyBoxParamSkyHColour;
    private int SkyBoxParamSkyLColour;
    private int SkyBoxParamCloudLight;
    private int SkyBoxParamStars;
    private int SkyBoxParamFog;
    private int SkyBoxParamWind;

    public SkyBoxShader(String string) {
        super(string);
    }

    @Override
    public void startRenderThread(TextureDraw var1) {
        SkyBox skyBox = SkyBox.getInstance();
        ARBShaderObjects.glUniform1fARB(this.SkyBoxTime, skyBox.getShaderTime());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudCount, skyBox.getShaderCloudCount());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudSize, skyBox.getShaderCloudSize());
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunLight, skyBox.getShaderSunLight().x, skyBox.getShaderSunLight().y, skyBox.getShaderSunLight().z);
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunColor, skyBox.getShaderSunColor().r, skyBox.getShaderSunColor().g, skyBox.getShaderSunColor().b);
        ARBShaderObjects.glUniform3fARB(
            this.SkyBoxParamSkyHColour, skyBox.getShaderSkyHColour().r, skyBox.getShaderSkyHColour().g, skyBox.getShaderSkyHColour().b
        );
        ARBShaderObjects.glUniform3fARB(
            this.SkyBoxParamSkyLColour, skyBox.getShaderSkyLColour().r, skyBox.getShaderSkyLColour().g, skyBox.getShaderSkyLColour().b
        );
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudLight, skyBox.getShaderCloudLight());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamStars, skyBox.getShaderStars());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamFog, skyBox.getShaderFog());
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamWind, skyBox.getShaderWind().x, skyBox.getShaderWind().y, skyBox.getShaderWind().z);
    }

    @Override
    public void onCompileSuccess(ShaderProgram var1) {
        int int0 = this.getID();
        this.SkyBoxTime = ARBShaderObjects.glGetUniformLocationARB(int0, "SBTime");
        this.SkyBoxParamCloudCount = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamCloudCount");
        this.SkyBoxParamCloudSize = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamCloudSize");
        this.SkyBoxParamSunLight = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamSunLight");
        this.SkyBoxParamSunColor = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamSunColour");
        this.SkyBoxParamSkyHColour = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamSkyHColour");
        this.SkyBoxParamSkyLColour = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamSkyLColour");
        this.SkyBoxParamCloudLight = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamCloudLight");
        this.SkyBoxParamStars = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamStars");
        this.SkyBoxParamFog = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamFog");
        this.SkyBoxParamWind = ARBShaderObjects.glGetUniformLocationARB(int0, "SBParamWind");
    }
}
