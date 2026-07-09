// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.iso.sprite.SkyBox;

public final class PuddlesShader extends Shader {
    private int WaterGroundTex;
    private int PuddlesHM;
    private int WaterTextureReflectionA;
    private int WaterTextureReflectionB;
    private int WaterTime;
    private int WaterOffset;
    private int WaterViewport;
    private int WaterReflectionParam;
    private int PuddlesParams;

    public PuddlesShader(String string) {
        super(string);
    }

    @Override
    protected void onCompileSuccess(ShaderProgram shaderProgram) {
        int int0 = shaderProgram.getShaderID();
        this.WaterGroundTex = ARBShaderObjects.glGetUniformLocationARB(int0, "WaterGroundTex");
        this.WaterTextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(int0, "WaterTextureReflectionA");
        this.WaterTextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(int0, "WaterTextureReflectionB");
        this.PuddlesHM = ARBShaderObjects.glGetUniformLocationARB(int0, "PuddlesHM");
        this.WaterTime = ARBShaderObjects.glGetUniformLocationARB(int0, "WTime");
        this.WaterOffset = ARBShaderObjects.glGetUniformLocationARB(int0, "WOffset");
        this.WaterViewport = ARBShaderObjects.glGetUniformLocationARB(int0, "WViewport");
        this.WaterReflectionParam = ARBShaderObjects.glGetUniformLocationARB(int0, "WReflectionParam");
        this.PuddlesParams = ARBShaderObjects.glGetUniformLocationARB(int0, "PuddlesParams");
        this.Start();
        if (this.WaterGroundTex != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterGroundTex, 0);
        }

        if (this.WaterTextureReflectionA != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionA, 1);
        }

        if (this.WaterTextureReflectionB != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionB, 2);
        }

        if (this.PuddlesHM != -1) {
            ARBShaderObjects.glUniform1iARB(this.PuddlesHM, 3);
        }

        this.End();
    }

    public void updatePuddlesParams(int int0, int int1) {
        IsoPuddles puddles = IsoPuddles.getInstance();
        SkyBox skyBox = SkyBox.getInstance();
        PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int0);
        GL13.glActiveTexture(33985);
        skyBox.getTextureCurrent().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        GL13.glActiveTexture(33986);
        skyBox.getTexturePrev().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        GL13.glActiveTexture(33987);
        puddles.getHMTexture().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        ARBShaderObjects.glUniform1fARB(this.WaterTime, puddles.getShaderTime());
        Vector4f vector4f = puddles.getShaderOffset();
        ARBShaderObjects.glUniform4fARB(this.WaterOffset, vector4f.x - 90000.0F, vector4f.y - 640000.0F, vector4f.z, vector4f.w);
        ARBShaderObjects.glUniform4fARB(
            this.WaterViewport,
            IsoCamera.getOffscreenLeft(int0),
            IsoCamera.getOffscreenTop(int0),
            playerCamera.OffscreenWidth / playerCamera.zoom,
            playerCamera.OffscreenHeight / playerCamera.zoom
        );
        ARBShaderObjects.glUniform1fARB(this.WaterReflectionParam, skyBox.getTextureShift());
        ARBShaderObjects.glUniformMatrix4fvARB(this.PuddlesParams, true, puddles.getPuddlesParams(int1));
    }
}
