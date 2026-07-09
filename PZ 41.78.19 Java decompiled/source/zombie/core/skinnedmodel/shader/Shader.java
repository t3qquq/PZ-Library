// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.shader;

import java.nio.FloatBuffer;
import org.joml.Math;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.BufferUtils;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.ShaderProgram;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.iso.IsoMovingObject;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.vehicles.BaseVehicle;

public final class Shader {
    private int HueChange;
    private int LightingAmount;
    private int MirrorXID;
    private int TransformMatrixID = 0;
    final String name;
    private final ShaderProgram m_shaderProgram;
    private int MatrixID = 0;
    private int Light0Direction;
    private int Light0Colour;
    private int Light1Direction;
    private int Light1Colour;
    private int Light2Direction;
    private int Light2Colour;
    private int Light3Direction;
    private int Light3Colour;
    private int Light4Direction;
    private int Light4Colour;
    private int TintColour;
    private int Texture0;
    private int TexturePainColor;
    private int TextureRust;
    private int TextureRustA;
    private int TextureMask;
    private int TextureLights;
    private int TextureDamage1Overlay;
    private int TextureDamage1Shell;
    private int TextureDamage2Overlay;
    private int TextureDamage2Shell;
    private int TextureUninstall1;
    private int TextureUninstall2;
    private int TextureLightsEnables1;
    private int TextureLightsEnables2;
    private int TextureDamage1Enables1;
    private int TextureDamage1Enables2;
    private int TextureDamage2Enables1;
    private int TextureDamage2Enables2;
    private int MatBlood1Enables1;
    private int MatBlood1Enables2;
    private int MatBlood2Enables1;
    private int MatBlood2Enables2;
    private int Alpha;
    private int TextureReflectionA;
    private int TextureReflectionB;
    private int ReflectionParam;
    public int BoneIndicesAttrib;
    public int BoneWeightsAttrib;
    private int UVScale;
    final boolean bStatic;
    private static FloatBuffer floatBuffer;
    private static final int MAX_BONES = 64;
    private static final Vector3f tempVec3f = new Vector3f();
    private final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);

    public Shader(String _name, boolean _bStatic) {
        this.name = _name;
        this.m_shaderProgram = ShaderProgram.createShaderProgram(_name, _bStatic, false);
        this.m_shaderProgram.addCompileListener(this::onProgramCompiled);
        this.bStatic = _bStatic;
        this.compile();
    }

    public boolean isStatic() {
        return this.bStatic;
    }

    public ShaderProgram getShaderProgram() {
        return this.m_shaderProgram;
    }

    private void onProgramCompiled(ShaderProgram var1) {
        this.Start();
        int int0 = this.m_shaderProgram.getShaderID();
        if (!this.bStatic) {
            this.MatrixID = ARBShaderObjects.glGetUniformLocationARB(int0, "MatrixPalette");
        } else {
            this.TransformMatrixID = ARBShaderObjects.glGetUniformLocationARB(int0, "transform");
        }

        this.HueChange = ARBShaderObjects.glGetUniformLocationARB(int0, "HueChange");
        this.LightingAmount = ARBShaderObjects.glGetUniformLocationARB(int0, "LightingAmount");
        this.Light0Colour = ARBShaderObjects.glGetUniformLocationARB(int0, "Light0Colour");
        this.Light0Direction = ARBShaderObjects.glGetUniformLocationARB(int0, "Light0Direction");
        this.Light1Colour = ARBShaderObjects.glGetUniformLocationARB(int0, "Light1Colour");
        this.Light1Direction = ARBShaderObjects.glGetUniformLocationARB(int0, "Light1Direction");
        this.Light2Colour = ARBShaderObjects.glGetUniformLocationARB(int0, "Light2Colour");
        this.Light2Direction = ARBShaderObjects.glGetUniformLocationARB(int0, "Light2Direction");
        this.Light3Colour = ARBShaderObjects.glGetUniformLocationARB(int0, "Light3Colour");
        this.Light3Direction = ARBShaderObjects.glGetUniformLocationARB(int0, "Light3Direction");
        this.Light4Colour = ARBShaderObjects.glGetUniformLocationARB(int0, "Light4Colour");
        this.Light4Direction = ARBShaderObjects.glGetUniformLocationARB(int0, "Light4Direction");
        this.TintColour = ARBShaderObjects.glGetUniformLocationARB(int0, "TintColour");
        this.Texture0 = ARBShaderObjects.glGetUniformLocationARB(int0, "Texture0");
        this.TexturePainColor = ARBShaderObjects.glGetUniformLocationARB(int0, "TexturePainColor");
        this.TextureRust = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureRust");
        this.TextureMask = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureMask");
        this.TextureLights = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureLights");
        this.TextureDamage1Overlay = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage1Overlay");
        this.TextureDamage1Shell = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage1Shell");
        this.TextureDamage2Overlay = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage2Overlay");
        this.TextureDamage2Shell = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage2Shell");
        this.TextureRustA = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureRustA");
        this.TextureUninstall1 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureUninstall1");
        this.TextureUninstall2 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureUninstall2");
        this.TextureLightsEnables1 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureLightsEnables1");
        this.TextureLightsEnables2 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureLightsEnables2");
        this.TextureDamage1Enables1 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage1Enables1");
        this.TextureDamage1Enables2 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage1Enables2");
        this.TextureDamage2Enables1 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage2Enables1");
        this.TextureDamage2Enables2 = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureDamage2Enables2");
        this.MatBlood1Enables1 = ARBShaderObjects.glGetUniformLocationARB(int0, "MatBlood1Enables1");
        this.MatBlood1Enables2 = ARBShaderObjects.glGetUniformLocationARB(int0, "MatBlood1Enables2");
        this.MatBlood2Enables1 = ARBShaderObjects.glGetUniformLocationARB(int0, "MatBlood2Enables1");
        this.MatBlood2Enables2 = ARBShaderObjects.glGetUniformLocationARB(int0, "MatBlood2Enables2");
        this.Alpha = ARBShaderObjects.glGetUniformLocationARB(int0, "Alpha");
        this.TextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureReflectionA");
        this.TextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(int0, "TextureReflectionB");
        this.ReflectionParam = ARBShaderObjects.glGetUniformLocationARB(int0, "ReflectionParam");
        this.UVScale = ARBShaderObjects.glGetUniformLocationARB(int0, "UVScale");
        this.m_shaderProgram.setSamplerUnit("Texture", 0);
        if (this.Texture0 != -1) {
            ARBShaderObjects.glUniform1iARB(this.Texture0, 0);
        }

        if (this.TextureRust != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureRust, 1);
        }

        if (this.TextureMask != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureMask, 2);
        }

        if (this.TextureLights != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureLights, 3);
        }

        if (this.TextureDamage1Overlay != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage1Overlay, 4);
        }

        if (this.TextureDamage1Shell != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage1Shell, 5);
        }

        if (this.TextureDamage2Overlay != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage2Overlay, 6);
        }

        if (this.TextureDamage2Shell != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureDamage2Shell, 7);
        }

        if (this.TextureReflectionA != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureReflectionA, 8);
        }

        if (this.TextureReflectionB != -1) {
            ARBShaderObjects.glUniform1iARB(this.TextureReflectionB, 9);
        }

        this.MirrorXID = ARBShaderObjects.glGetUniformLocationARB(int0, "MirrorX");
        this.BoneIndicesAttrib = GL20.glGetAttribLocation(int0, "boneIndices");
        this.BoneWeightsAttrib = GL20.glGetAttribLocation(int0, "boneWeights");
        this.End();
    }

    private void compile() {
        this.m_shaderProgram.compile();
    }

    public void setTexture(Texture tex, String unitName, int textureUnit) {
        this.m_shaderProgram.setValue(unitName, tex, textureUnit);
    }

    private void setUVScale(float float0, float float1) {
        if (this.UVScale > 0) {
            this.m_shaderProgram.setVector2(this.UVScale, float0, float1);
        }
    }

    public int getID() {
        return this.m_shaderProgram.getShaderID();
    }

    public void Start() {
        this.m_shaderProgram.Start();
    }

    public void End() {
        this.m_shaderProgram.End();
    }

    public void startCharacter(ModelSlotRenderData slotData, ModelInstanceRenderData instData) {
        if (this.bStatic) {
            this.setTransformMatrix(instData.xfrm, true);
        } else {
            this.setMatrixPalette(instData.matrixPalette);
        }

        float float0 = slotData.ambientR * 0.45F;
        float float1 = slotData.ambientG * 0.45F;
        float float2 = slotData.ambientB * 0.45F;
        this.setLights(slotData, 5);
        Texture texture0 = instData.tex != null ? instData.tex : instData.model.tex;
        if (DebugOptions.instance.IsoSprite.CharacterMipmapColors.getValue()) {
            Texture texture1 = texture0 instanceof SmartTexture ? ((SmartTexture)texture0).result : texture0;
            if (texture1 != null && texture1.getTextureId() != null && texture1.getTextureId().hasMipMaps()) {
                texture0 = Texture.getEngineMipmapTexture();
            }
        }

        this.setTexture(texture0, "Texture", 0);
        this.setDepthBias(instData.depthBias / 50.0F);
        this.setAmbient(float0, float1, float2);
        this.setLightingAmount(1.0F);
        this.setHueShift(instData.hue);
        this.setTint(instData.tintR, instData.tintG, instData.tintB);
        this.setAlpha(slotData.alpha);
    }

    private void setLights(ModelSlotRenderData modelSlotRenderData, int int1) {
        for (int int0 = 0; int0 < int1; int0++) {
            ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int0];
            if (GameServer.bServer && ServerGUI.isCreated()) {
                effectLight.r = effectLight.g = effectLight.b = 1.0F;
            }

            this.setLight(
                int0,
                effectLight.x,
                effectLight.y,
                effectLight.z,
                effectLight.r,
                effectLight.g,
                effectLight.b,
                effectLight.radius,
                modelSlotRenderData.animPlayerAngle,
                modelSlotRenderData.x,
                modelSlotRenderData.y,
                modelSlotRenderData.z,
                modelSlotRenderData.object
            );
        }
    }

    public void updateAlpha(IsoGameCharacter chr, int playerIndex) {
        if (chr != null) {
            this.setAlpha(chr.getAlpha(playerIndex));
        }
    }

    public void setAlpha(float alpha) {
        ARBShaderObjects.glUniform1fARB(this.Alpha, alpha);
    }

    public void updateParams() {
    }

    public void setMatrixPalette(Matrix4f[] matrix4fs) {
        if (!this.bStatic) {
            if (floatBuffer == null) {
                floatBuffer = BufferUtils.createFloatBuffer(1024);
            }

            floatBuffer.clear();

            for (Matrix4f matrix4f : matrix4fs) {
                matrix4f.store(floatBuffer);
            }

            floatBuffer.flip();
            ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, floatBuffer);
        }
    }

    public void setMatrixPalette(FloatBuffer matrixPalette) {
        this.setMatrixPalette(matrixPalette, true);
    }

    public void setMatrixPalette(FloatBuffer matrixPalette, boolean transpose) {
        if (!this.bStatic) {
            ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, transpose, matrixPalette);
        }
    }

    public void setMatrixPalette(org.joml.Matrix4f[] matrix4fs) {
        if (!this.bStatic) {
            if (floatBuffer == null) {
                floatBuffer = BufferUtils.createFloatBuffer(1024);
            }

            floatBuffer.clear();

            for (org.joml.Matrix4f matrix4f : matrix4fs) {
                matrix4f.get(floatBuffer);
                floatBuffer.position(floatBuffer.position() + 16);
            }

            floatBuffer.flip();
            ARBShaderObjects.glUniformMatrix4fvARB(this.MatrixID, true, floatBuffer);
        }
    }

    public void setTint(float x, float y, float z) {
        ARBShaderObjects.glUniform3fARB(this.TintColour, x, y, z);
    }

    public void setTextureRustA(float a) {
        ARBShaderObjects.glUniform1fARB(this.TextureRustA, a);
    }

    public void setTexturePainColor(float x, float y, float z, float a) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, x, y, z, a);
    }

    public void setTexturePainColor(org.joml.Vector3f vec, float a) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vec.x(), vec.y(), vec.z(), a);
    }

    public void setTexturePainColor(Vector4f vec) {
        ARBShaderObjects.glUniform4fARB(this.TexturePainColor, vec.x(), vec.y(), vec.z(), vec.w());
    }

    public void setReflectionParam(float timesOfDay, float refWindows, float refBody) {
        ARBShaderObjects.glUniform3fARB(this.ReflectionParam, timesOfDay, refWindows, refBody);
    }

    public void setTextureUninstall1(float[] floats) {
        this.setMatrix(this.TextureUninstall1, floats);
    }

    public void setTextureUninstall2(float[] floats) {
        this.setMatrix(this.TextureUninstall2, floats);
    }

    public void setTextureLightsEnables1(float[] floats) {
        this.setMatrix(this.TextureLightsEnables1, floats);
    }

    public void setTextureLightsEnables2(float[] floats) {
        this.setMatrix(this.TextureLightsEnables2, floats);
    }

    public void setTextureDamage1Enables1(float[] floats) {
        this.setMatrix(this.TextureDamage1Enables1, floats);
    }

    public void setTextureDamage1Enables2(float[] floats) {
        this.setMatrix(this.TextureDamage1Enables2, floats);
    }

    public void setTextureDamage2Enables1(float[] floats) {
        this.setMatrix(this.TextureDamage2Enables1, floats);
    }

    public void setTextureDamage2Enables2(float[] floats) {
        this.setMatrix(this.TextureDamage2Enables2, floats);
    }

    public void setMatrixBlood1(float[] floats0, float[] floats1) {
        if (this.MatBlood1Enables1 != -1 && this.MatBlood1Enables2 != -1) {
            this.setMatrix(this.MatBlood1Enables1, floats0);
            this.setMatrix(this.MatBlood1Enables2, floats1);
        }
    }

    public void setMatrixBlood2(float[] floats0, float[] floats1) {
        if (this.MatBlood2Enables1 != -1 && this.MatBlood2Enables2 != -1) {
            this.setMatrix(this.MatBlood2Enables1, floats0);
            this.setMatrix(this.MatBlood2Enables2, floats1);
        }
    }

    public void setShaderAlpha(float a) {
        ARBShaderObjects.glUniform1fARB(this.Alpha, a);
    }

    public void setLight(int index, float x, float y, float z, float r, float g, float b, float rad, float animPlayerAngle, ModelInstance inst) {
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;
        IsoMovingObject movingObject = inst.object;
        if (movingObject != null) {
            float0 = movingObject.x;
            float1 = movingObject.y;
            float2 = movingObject.z;
        }

        this.setLight(index, x, y, z, r, g, b, rad, animPlayerAngle, float0, float1, float2, movingObject);
    }

    public void setLight(
        int index,
        float x,
        float y,
        float z,
        float r,
        float g,
        float b,
        float rad,
        float animPlayerAngle,
        float offsetX,
        float offsetY,
        float offsetZ,
        IsoMovingObject instObject
    ) {
        PZGLUtil.checkGLError(true);
        int int0 = this.Light0Direction;
        int int1 = this.Light0Colour;
        if (index == 1) {
            int0 = this.Light1Direction;
            int1 = this.Light1Colour;
        }

        if (index == 2) {
            int0 = this.Light2Direction;
            int1 = this.Light2Colour;
        }

        if (index == 3) {
            int0 = this.Light3Direction;
            int1 = this.Light3Colour;
        }

        if (index == 4) {
            int0 = this.Light4Direction;
            int1 = this.Light4Colour;
        }

        if (r + g + b != 0.0F && !(rad <= 0.0F)) {
            Vector3f vector3f = tempVec3f;
            if (!Float.isNaN(animPlayerAngle)) {
                vector3f.set(x, y, z);
                vector3f.x -= offsetX;
                vector3f.y -= offsetY;
                vector3f.z -= offsetZ;
            } else {
                vector3f.set(x, y, z);
            }

            float float0 = vector3f.length();
            if (float0 < 1.0E-4F) {
                vector3f.set(0.0F, 0.0F, 1.0F);
            } else {
                vector3f.normalise();
            }

            if (!Float.isNaN(animPlayerAngle)) {
                float float1 = -animPlayerAngle;
                float float2 = vector3f.x;
                float float3 = vector3f.y;
                vector3f.x = float2 * Math.cos(float1) - float3 * Math.sin(float1);
                vector3f.y = float2 * Math.sin(float1) + float3 * Math.cos(float1);
            }

            float float4 = vector3f.y;
            vector3f.y = vector3f.z;
            vector3f.z = float4;
            if (vector3f.length() < 1.0E-4F) {
                vector3f.set(0.0F, 1.0F, 0.0F);
            }

            vector3f.normalise();
            float float5 = 1.0F - float0 / rad;
            if (float5 < 0.0F) {
                float5 = 0.0F;
            }

            if (float5 > 1.0F) {
                float5 = 1.0F;
            }

            r *= float5;
            g *= float5;
            b *= float5;
            r = PZMath.clamp(r, 0.0F, 1.0F);
            g = PZMath.clamp(g, 0.0F, 1.0F);
            b = PZMath.clamp(b, 0.0F, 1.0F);
            if (instObject instanceof BaseVehicle) {
                this.doVector3(int0, -vector3f.x, vector3f.y, vector3f.z);
            } else {
                this.doVector3(int0, -vector3f.x, vector3f.y, vector3f.z);
            }

            if (instObject instanceof IsoPlayer) {
                boolean boolean0 = false;
            }

            this.doVector3(int1, r, g, b);
            PZGLUtil.checkGLErrorThrow("Shader.setLightInternal.");
        } else {
            this.doVector3(int0, 0.0F, 1.0F, 0.0F);
            this.doVector3(int1, 0.0F, 0.0F, 0.0F);
        }
    }

    private void doVector3(int int0, float float0, float float1, float float2) {
        this.m_shaderProgram.setVector3(int0, float0, float1, float2);
    }

    public void setHueShift(float hue) {
        if (this.HueChange > 0) {
            this.m_shaderProgram.setValue("HueChange", hue);
        }
    }

    public void setLightingAmount(float lighting) {
        if (this.LightingAmount > 0) {
            this.m_shaderProgram.setValue("LightingAmount", lighting);
        }
    }

    public void setDepthBias(float bias) {
        this.m_shaderProgram.setValue("DepthBias", bias / 300.0F);
    }

    public void setAmbient(float amb) {
        this.m_shaderProgram.setVector3("AmbientColour", amb, amb, amb);
    }

    public void setAmbient(float ambr, float ambg, float ambb) {
        this.m_shaderProgram.setVector3("AmbientColour", ambr, ambg, ambb);
    }

    public void setTransformMatrix(Matrix4f matrix4f, boolean transpose) {
        if (floatBuffer == null) {
            floatBuffer = BufferUtils.createFloatBuffer(1024);
        }

        floatBuffer.clear();
        matrix4f.store(floatBuffer);
        floatBuffer.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, transpose, floatBuffer);
    }

    public void setTransformMatrix(org.joml.Matrix4f matrix4f, boolean transpose) {
        this.floatBuffer2.clear();
        matrix4f.get(this.floatBuffer2);
        this.floatBuffer2.position(16);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(this.TransformMatrixID, transpose, this.floatBuffer2);
    }

    public void setMatrix(int location, org.joml.Matrix4f matrix4f) {
        this.floatBuffer2.clear();
        matrix4f.get(this.floatBuffer2);
        this.floatBuffer2.position(16);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(location, true, this.floatBuffer2);
    }

    public void setMatrix(int int0, float[] floats) {
        this.floatBuffer2.clear();
        this.floatBuffer2.put(floats);
        this.floatBuffer2.flip();
        ARBShaderObjects.glUniformMatrix4fvARB(int0, true, this.floatBuffer2);
    }

    public boolean isVehicleShader() {
        return this.TextureRust != -1;
    }
}
