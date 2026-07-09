// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjglx.BufferUtils;
import zombie.GameProfiler;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.particle.MuzzleFlash;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.iso.IsoLightSource;
import zombie.iso.Vector3;
import zombie.iso.sprite.SkyBox;
import zombie.scripting.objects.ModelAttachment;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class Model extends Asset {
    public String Name;
    public final ModelMesh Mesh;
    public Shader Effect;
    public Object Tag;
    public boolean bStatic = false;
    public Texture tex = null;
    public SoftwareModelMesh softwareMesh;
    public static final FloatBuffer m_staticReusableFloatBuffer = BufferUtils.createFloatBuffer(128);
    private static final Matrix4f IDENTITY = new Matrix4f();
    public static final Color[] debugDrawColours = new Color[]{
        new Color(230, 25, 75),
        new Color(60, 180, 75),
        new Color(255, 225, 25),
        new Color(0, 130, 200),
        new Color(245, 130, 48),
        new Color(145, 30, 180),
        new Color(70, 240, 240),
        new Color(240, 50, 230),
        new Color(210, 245, 60),
        new Color(250, 190, 190),
        new Color(0, 128, 128),
        new Color(230, 190, 255),
        new Color(170, 110, 40),
        new Color(255, 250, 200),
        new Color(128, 0, 0),
        new Color(170, 255, 195),
        new Color(128, 128, 0),
        new Color(255, 215, 180),
        new Color(0, 0, 128),
        new Color(128, 128, 128),
        new Color(255, 255, 255),
        new Color(0, 0, 0)
    };
    public Model.ModelAssetParams assetParams;
    static Vector3 tempo = new Vector3();
    public static final AssetType ASSET_TYPE = new AssetType("Model");

    public Model(AssetPath path, AssetManager manager, Model.ModelAssetParams params) {
        super(path, manager);
        this.assetParams = params;
        this.bStatic = this.assetParams != null && this.assetParams.bStatic;
        ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
        meshAssetParams.bStatic = this.bStatic;
        meshAssetParams.animationsMesh = this.assetParams == null ? null : this.assetParams.animationsModel;
        this.Mesh = (ModelMesh)MeshAssetManager.instance.load(new AssetPath(params.meshName), meshAssetParams);
        if (!StringUtils.isNullOrWhitespace(params.textureName)) {
            if (params.textureName.contains("media/")) {
                this.tex = Texture.getSharedTexture(params.textureName, params.textureFlags);
            } else {
                this.tex = Texture.getSharedTexture("media/textures/" + params.textureName + ".png", params.textureFlags);
            }
        }

        if (!StringUtils.isNullOrWhitespace(params.shaderName)) {
            this.CreateShader(params.shaderName);
        }

        this.onCreated(this.Mesh.getState());
        this.addDependency(this.Mesh);
        if (this.isReady()) {
            this.Tag = this.Mesh.skinningData;
            this.softwareMesh = this.Mesh.softwareMesh;
            this.assetParams = null;
        }
    }

    public static void VectorToWorldCoords(IsoGameCharacter character, Vector3 vec) {
        AnimationPlayer animationPlayer = character.getAnimationPlayer();
        float float0 = animationPlayer.getRenderedAngle();
        vec.x = -vec.x;
        vec.rotatey(float0);
        float float1 = vec.y;
        vec.y = vec.z;
        vec.z = float1 * 0.6F;
        vec.x *= 1.5F;
        vec.y *= 1.5F;
        vec.x = vec.x + character.x;
        vec.y = vec.y + character.y;
        vec.z = vec.z + character.z;
    }

    public static void BoneToWorldCoords(IsoGameCharacter character, int boneIndex, Vector3 vec) {
        AnimationPlayer animationPlayer = character.getAnimationPlayer();
        vec.x = animationPlayer.modelTransforms[boneIndex].m03;
        vec.y = animationPlayer.modelTransforms[boneIndex].m13;
        vec.z = animationPlayer.modelTransforms[boneIndex].m23;
        VectorToWorldCoords(character, vec);
    }

    public static void BoneYDirectionToWorldCoords(IsoGameCharacter character, int boneIndex, Vector3 vec, float length) {
        AnimationPlayer animationPlayer = character.getAnimationPlayer();
        vec.x = animationPlayer.modelTransforms[boneIndex].m01 * length;
        vec.y = animationPlayer.modelTransforms[boneIndex].m11 * length;
        vec.z = animationPlayer.modelTransforms[boneIndex].m21 * length;
        vec.x = vec.x + animationPlayer.modelTransforms[boneIndex].m03;
        vec.y = vec.y + animationPlayer.modelTransforms[boneIndex].m13;
        vec.z = vec.z + animationPlayer.modelTransforms[boneIndex].m23;
        VectorToWorldCoords(character, vec);
    }

    public static void VectorToWorldCoords(ModelSlotRenderData slotData, Vector3 vec) {
        float float0 = slotData.animPlayerAngle;
        vec.x = -vec.x;
        vec.rotatey(float0);
        float float1 = vec.y;
        vec.y = vec.z;
        vec.z = float1 * 0.6F;
        vec.x *= 1.5F;
        vec.y *= 1.5F;
        vec.x = vec.x + slotData.x;
        vec.y = vec.y + slotData.y;
        vec.z = vec.z + slotData.z;
    }

    public static void BoneToWorldCoords(ModelSlotRenderData slotData, int boneIndex, Vector3 vec) {
        AnimationPlayer animationPlayer = slotData.animPlayer;
        vec.x = animationPlayer.modelTransforms[boneIndex].m03;
        vec.y = animationPlayer.modelTransforms[boneIndex].m13;
        vec.z = animationPlayer.modelTransforms[boneIndex].m23;
        VectorToWorldCoords(slotData, vec);
    }

    public static void CharacterModelCameraBegin(ModelSlotRenderData slotData) {
        ModelCamera.instance.Begin();
        if (slotData.bInVehicle) {
            GL11.glMatrixMode(5888);
            GL11.glTranslatef(0.0F, slotData.centerOfMassY, 0.0F);
            GL11.glMatrixMode(5888);
            GL11.glRotatef(slotData.vehicleAngleZ, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(slotData.vehicleAngleY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(slotData.vehicleAngleX, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            byte byte0 = -1;
            GL11.glTranslatef(slotData.inVehicleX, slotData.inVehicleY, slotData.inVehicleZ * byte0);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
        }
    }

    public static void CharacterModelCameraEnd() {
        ModelCamera.instance.End();
    }

    public void DrawChar(ModelSlotRenderData slotData, ModelInstanceRenderData instData) {
        if (!DebugOptions.instance.Character.Debug.Render.SkipCharacters.getValue()) {
            if (slotData.character == IsoPlayer.getInstance()) {
                boolean boolean0 = false;
            }

            if (!(slotData.alpha < 0.01F)) {
                if (slotData.animPlayer != null) {
                    if (Core.bDebug && GameKeyboard.isKeyDown(199)) {
                        this.Effect = null;
                    }

                    if (this.Effect == null) {
                        this.CreateShader("basicEffect");
                    }

                    Shader shader0 = this.Effect;
                    GL11.glEnable(2884);
                    GL11.glCullFace(1028);
                    GL11.glEnable(2929);
                    GL11.glEnable(3008);
                    GL11.glDepthFunc(513);
                    GL11.glAlphaFunc(516, 0.01F);
                    GL11.glBlendFunc(770, 771);
                    if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
                        GL11.glPolygonMode(1032, 6913);
                        GL11.glEnable(2848);
                        GL11.glLineWidth(0.75F);
                        Shader shader1 = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
                        if (shader1 != null) {
                            shader1.Start();
                            if (this.bStatic) {
                                shader1.setTransformMatrix(instData.xfrm, true);
                            } else {
                                shader1.setMatrixPalette(instData.matrixPalette, true);
                            }

                            this.Mesh.Draw(shader1);
                            shader1.End();
                        }

                        GL11.glPolygonMode(1032, 6914);
                        GL11.glDisable(2848);
                    } else {
                        if (shader0 != null) {
                            shader0.Start();
                            shader0.startCharacter(slotData, instData);
                        }

                        if (!DebugOptions.instance.DebugDraw_SkipDrawNonSkinnedModel.getValue()) {
                            GameProfiler.getInstance().invokeAndMeasure("Mesh.Draw.Call", shader0, this.Mesh, (shader, modelMesh) -> modelMesh.Draw(shader));
                        }

                        if (shader0 != null) {
                            shader0.End();
                        }

                        this.drawMuzzleFlash(instData);
                    }
                }
            }
        }
    }

    private void drawMuzzleFlash(ModelInstanceRenderData modelInstanceRenderData) {
        if (modelInstanceRenderData.m_muzzleFlash) {
            ModelAttachment modelAttachment = modelInstanceRenderData.modelInstance.getAttachmentById("muzzle");
            if (modelAttachment != null) {
                BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = BaseVehicle.TL_matrix4f_pool.get();
                Matrix4f matrix4f0 = matrix4fObjectPool.alloc().set(modelInstanceRenderData.xfrm);
                matrix4f0.transpose();
                Matrix4f matrix4f1 = modelInstanceRenderData.modelInstance.getAttachmentMatrix(modelAttachment, matrix4fObjectPool.alloc());
                matrix4f0.mul(matrix4f1, matrix4f1);
                MuzzleFlash.render(matrix4f1);
                matrix4fObjectPool.release(matrix4f0);
                matrix4fObjectPool.release(matrix4f1);
            }
        }
    }

    private void drawVehicleLights(ModelSlotRenderData modelSlotRenderData) {
        for (int int0 = 7; int0 >= 0; int0--) {
            GL13.glActiveTexture(33984 + int0);
            GL11.glDisable(3553);
        }

        GL11.glLineWidth(1.0F);
        GL11.glColor3f(1.0F, 1.0F, 0.0F);
        GL11.glDisable(2929);

        for (int int1 = 0; int1 < 3; int1++) {
            ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int1];
            if (!(effectLight.radius <= 0.0F)) {
                float float0 = effectLight.x;
                float float1 = effectLight.y;
                float float2 = effectLight.z;
                float0 *= -54.0F;
                float float3 = float2 * 54.0F;
                float2 = float1 * 54.0F;
                GL11.glBegin(1);
                GL11.glVertex3f(float0, float3, float2);
                GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                GL11.glEnd();
            }
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
    }

    public static void drawBoneMtx(org.lwjgl.util.vector.Matrix4f boneMtx) {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBegin(1);
        drawBoneMtxInternal(boneMtx);
        GL11.glEnd();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glEnable(2929);
    }

    private static void drawBoneMtxInternal(org.lwjgl.util.vector.Matrix4f matrix4f) {
        float float0 = 0.5F;
        float float1 = 0.15F;
        float float2 = 0.1F;
        float float3 = matrix4f.m03;
        float float4 = matrix4f.m13;
        float float5 = matrix4f.m23;
        float float6 = matrix4f.m00;
        float float7 = matrix4f.m10;
        float float8 = matrix4f.m20;
        float float9 = matrix4f.m01;
        float float10 = matrix4f.m11;
        float float11 = matrix4f.m21;
        float float12 = matrix4f.m02;
        float float13 = matrix4f.m12;
        float float14 = matrix4f.m22;
        drawArrowInternal(float3, float4, float5, float6, float7, float8, float12, float13, float14, float0, float1, float2, 1.0F, 0.0F, 0.0F);
        drawArrowInternal(float3, float4, float5, float9, float10, float11, float12, float13, float14, float0, float1, float2, 0.0F, 1.0F, 0.0F);
        drawArrowInternal(float3, float4, float5, float12, float13, float14, float6, float7, float8, float0, float1, float2, 0.0F, 0.0F, 1.0F);
    }

    private static void drawArrowInternal(
        float float5,
        float float6,
        float float7,
        float float11,
        float float10,
        float float8,
        float float15,
        float float14,
        float float12,
        float float9,
        float float1,
        float float13,
        float float2,
        float float3,
        float float4
    ) {
        float float0 = 1.0F - float1;
        GL11.glColor3f(float2, float3, float4);
        GL11.glVertex3f(float5, float6, float7);
        GL11.glVertex3f(float5 + float11 * float9, float6 + float10 * float9, float7 + float8 * float9);
        GL11.glVertex3f(float5 + float11 * float9, float6 + float10 * float9, float7 + float8 * float9);
        GL11.glVertex3f(
            float5 + (float11 * float0 + float15 * float13) * float9,
            float6 + (float10 * float0 + float14 * float13) * float9,
            float7 + (float8 * float0 + float12 * float13) * float9
        );
        GL11.glVertex3f(float5 + float11 * float9, float6 + float10 * float9, float7 + float8 * float9);
        GL11.glVertex3f(
            float5 + (float11 * float0 - float15 * float13) * float9,
            float6 + (float10 * float0 - float14 * float13) * float9,
            float7 + (float8 * float0 - float12 * float13) * float9
        );
    }

    public void debugDrawLightSource(IsoLightSource ls, float cx, float cy, float cz, float radians) {
        debugDrawLightSource(ls.x, ls.y, ls.z, cx, cy, cz, radians);
    }

    public static void debugDrawLightSource(float lx, float ly, float lz, float cx, float cy, float cz, float radians) {
        float float0 = lx - cx + 0.5F;
        float float1 = ly - cy + 0.5F;
        float float2 = lz - cz + 0.0F;
        float0 *= 0.67F;
        float1 *= 0.67F;
        float float3 = (float)(float0 * Math.cos(radians) - float1 * Math.sin(radians));
        float1 = (float)(float0 * Math.sin(radians) + float1 * Math.cos(radians));
        float0 = float3 * -1.0F;
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glBegin(1);
        GL11.glColor3f(1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(float0, float2, float1);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glVertex3f(float0, float2, float1);
        GL11.glVertex3f(float0 + 0.1F, float2, float1);
        GL11.glVertex3f(float0, float2, float1);
        GL11.glVertex3f(float0, float2 + 0.1F, float1);
        GL11.glVertex3f(float0, float2, float1);
        GL11.glVertex3f(float0, float2, float1 + 0.1F);
        GL11.glEnd();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
    }

    public void DrawVehicle(ModelSlotRenderData slotData, ModelInstanceRenderData instData) {
        if (!DebugOptions.instance.ModelRenderSkipVehicles.getValue()) {
            ModelInstance modelInstance = instData.modelInstance;
            float float0 = slotData.ambientR;
            Texture texture = instData.tex;
            float float1 = instData.tintR;
            float float2 = instData.tintG;
            float float3 = instData.tintB;
            PZGLUtil.checkGLErrorThrow("Model.drawVehicle Enter inst: %s, instTex: %s, slotData: %s", modelInstance, texture, slotData);
            GL11.glEnable(2884);
            GL11.glCullFace(modelInstance.m_modelScript != null && modelInstance.m_modelScript.invertX ? 1029 : 1028);
            GL11.glEnable(2929);
            GL11.glDepthFunc(513);
            ModelCamera.instance.Begin();
            GL11.glMatrixMode(5888);
            GL11.glTranslatef(0.0F, slotData.centerOfMassY, 0.0F);
            Shader shader = this.Effect;
            PZGLUtil.pushAndMultMatrix(5888, instData.xfrm);
            if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
                GL11.glPolygonMode(1032, 6913);
                GL11.glEnable(2848);
                GL11.glLineWidth(0.75F);
                shader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", this.bStatic);
                if (shader != null) {
                    shader.Start();
                    if (this.bStatic) {
                        shader.setTransformMatrix(IDENTITY, false);
                    } else {
                        shader.setMatrixPalette(instData.matrixPalette, true);
                    }

                    this.Mesh.Draw(shader);
                    shader.End();
                }

                GL11.glDisable(2848);
                PZGLUtil.popMatrix(5888);
                ModelCamera.instance.End();
            } else {
                if (shader != null) {
                    shader.Start();
                    this.setLights(slotData, 3);
                    if (shader.isVehicleShader()) {
                        VehicleModelInstance vehicleModelInstance = Type.tryCastTo(modelInstance, VehicleModelInstance.class);
                        if (modelInstance instanceof VehicleSubModelInstance) {
                            vehicleModelInstance = Type.tryCastTo(modelInstance.parent, VehicleModelInstance.class);
                        }

                        shader.setTexture(vehicleModelInstance.tex, "Texture0", 0);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureRust, "TextureRust", 1);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureMask, "TextureMask", 2);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureLights, "TextureLights", 3);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureDamage1Overlay, "TextureDamage1Overlay", 4);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureDamage1Shell, "TextureDamage1Shell", 5);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureDamage2Overlay, "TextureDamage2Overlay", 6);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        shader.setTexture(vehicleModelInstance.textureDamage2Shell, "TextureDamage2Shell", 7);
                        GL11.glTexEnvi(8960, 8704, 7681);

                        try {
                            if (Core.getInstance().getPerfReflectionsOnLoad()) {
                                shader.setTexture((Texture)SkyBox.getInstance().getTextureCurrent(), "TextureReflectionA", 8);
                                GL11.glTexEnvi(8960, 8704, 7681);
                                GL11.glGetError();
                            }
                        } catch (Throwable throwable0) {
                        }

                        try {
                            if (Core.getInstance().getPerfReflectionsOnLoad()) {
                                shader.setTexture((Texture)SkyBox.getInstance().getTexturePrev(), "TextureReflectionB", 9);
                                GL11.glTexEnvi(8960, 8704, 7681);
                                GL11.glGetError();
                            }
                        } catch (Throwable throwable1) {
                        }

                        shader.setReflectionParam(SkyBox.getInstance().getTextureShift(), vehicleModelInstance.refWindows, vehicleModelInstance.refBody);
                        shader.setTextureUninstall1(vehicleModelInstance.textureUninstall1);
                        shader.setTextureUninstall2(vehicleModelInstance.textureUninstall2);
                        shader.setTextureLightsEnables1(vehicleModelInstance.textureLightsEnables1);
                        shader.setTextureLightsEnables2(vehicleModelInstance.textureLightsEnables2);
                        shader.setTextureDamage1Enables1(vehicleModelInstance.textureDamage1Enables1);
                        shader.setTextureDamage1Enables2(vehicleModelInstance.textureDamage1Enables2);
                        shader.setTextureDamage2Enables1(vehicleModelInstance.textureDamage2Enables1);
                        shader.setTextureDamage2Enables2(vehicleModelInstance.textureDamage2Enables2);
                        shader.setMatrixBlood1(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2);
                        shader.setMatrixBlood2(vehicleModelInstance.matrixBlood2Enables1, vehicleModelInstance.matrixBlood2Enables2);
                        shader.setTextureRustA(vehicleModelInstance.textureRustA);
                        shader.setTexturePainColor(vehicleModelInstance.painColor, slotData.alpha);
                        if (this.bStatic) {
                            shader.setTransformMatrix(IDENTITY, false);
                        } else {
                            shader.setMatrixPalette(instData.matrixPalette, true);
                        }
                    } else if (modelInstance instanceof VehicleSubModelInstance) {
                        GL13.glActiveTexture(33984);
                        shader.setTexture(texture, "Texture", 0);
                        shader.setShaderAlpha(slotData.alpha);
                        if (this.bStatic) {
                            shader.setTransformMatrix(IDENTITY, false);
                        }
                    } else {
                        GL13.glActiveTexture(33984);
                        shader.setTexture(texture, "Texture", 0);
                    }

                    shader.setAmbient(float0);
                    shader.setTint(float1, float2, float3);
                    this.Mesh.Draw(shader);
                    shader.End();
                }

                if (Core.bDebug && DebugOptions.instance.ModelRenderLights.getValue() && instData == slotData.modelData.get(0)) {
                    this.drawVehicleLights(slotData);
                }

                PZGLUtil.popMatrix(5888);
                ModelCamera.instance.End();
                PZGLUtil.checkGLErrorThrow("Model.drawVehicle Exit inst: %s, instTex: %s, slotData: %s", modelInstance, texture, slotData);
            }
        }
    }

    public static void debugDrawAxis(float x, float y, float z, float length, float thickness) {
        for (int int0 = 0; int0 < 8; int0++) {
            GL13.glActiveTexture(33984 + int0);
            GL11.glDisable(3553);
        }

        GL11.glDisable(2929);
        GL11.glLineWidth(thickness);
        GL11.glBegin(1);
        GL11.glColor3f(1.0F, 0.0F, 0.0F);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x + length, y, z);
        GL11.glColor3f(0.0F, 1.0F, 0.0F);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, y + length, z);
        GL11.glColor3f(0.0F, 0.0F, 1.0F);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, y, z + length);
        GL11.glEnd();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glEnable(2929);
        GL13.glActiveTexture(33984);
        GL11.glEnable(3553);
    }

    private void setLights(ModelSlotRenderData modelSlotRenderData, int int1) {
        for (int int0 = 0; int0 < int1; int0++) {
            ModelInstance.EffectLight effectLight = modelSlotRenderData.effectLights[int0];
            this.Effect
                .setLight(
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

    public void CreateShader(String name) {
        if (!ModelManager.NoOpenGL) {
            Lambda.invoke(
                RenderThread::invokeOnRenderContext,
                this,
                name,
                (model, string) -> model.Effect = ShaderManager.instance.getOrCreateShader(string, model.bStatic)
            );
        }
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    @Override
    protected void onBeforeReady() {
        super.onBeforeReady();
        this.Tag = this.Mesh.skinningData;
        this.softwareMesh = this.Mesh.softwareMesh;
        this.assetParams = null;
    }

    public static final class ModelAssetParams extends AssetManager.AssetParams {
        public String meshName;
        public String textureName;
        public int textureFlags;
        public String shaderName;
        public boolean bStatic = false;
        public ModelMesh animationsModel;
    }
}
