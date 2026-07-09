// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import zombie.GameProfiler;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.iso.IsoCamera;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector3;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Pool;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class ModelSlotRenderData extends TextureDraw.GenericDrawer {
    public IsoGameCharacter character;
    public IsoMovingObject object;
    private ModelManager.ModelSlot modelSlot;
    public final ModelInstanceRenderDataList modelData = new ModelInstanceRenderDataList();
    private final ModelInstanceRenderDataList readyModelData = new ModelInstanceRenderDataList();
    public ModelInstanceTextureCreator textureCreator;
    public AnimationPlayer animPlayer;
    public float animPlayerAngle;
    public float x;
    public float y;
    public float z;
    public float ambientR;
    public float ambientG;
    public float ambientB;
    public boolean bOutside;
    public final Matrix4f vehicleTransform = new Matrix4f();
    public boolean bInVehicle;
    public float inVehicleX;
    public float inVehicleY;
    public float inVehicleZ;
    public float vehicleAngleX;
    public float vehicleAngleY;
    public float vehicleAngleZ;
    public float alpha;
    private boolean bRendered;
    private boolean bReady;
    public final ModelInstance.EffectLight[] effectLights = new ModelInstance.EffectLight[5];
    public float centerOfMassY;
    public boolean RENDER_TO_TEXTURE;
    private static Shader solidColor;
    private static Shader solidColorStatic;
    private boolean bCharacterOutline = false;
    private final ColorInfo outlineColor = new ColorInfo(1.0F, 0.0F, 0.0F, 1.0F);
    private ModelSlotDebugRenderData m_debugRenderData;
    private static final ObjectPool<ModelSlotRenderData> pool = new ObjectPool<>(ModelSlotRenderData::new);

    public ModelSlotRenderData() {
        for (int int0 = 0; int0 < this.effectLights.length; int0++) {
            this.effectLights[int0] = new ModelInstance.EffectLight();
        }
    }

    public ModelSlotRenderData init(ModelManager.ModelSlot _modelSlot) {
        int int0 = IsoCamera.frameState.playerIndex;
        this.modelSlot = _modelSlot;
        this.object = _modelSlot.model.object;
        this.x = this.object.x;
        this.y = this.object.y;
        this.z = this.object.z;
        this.character = _modelSlot.character;
        BaseVehicle vehicle0 = Type.tryCastTo(this.object, BaseVehicle.class);
        if (vehicle0 != null) {
            this.textureCreator = null;
            this.animPlayer = vehicle0.getAnimationPlayer();
            this.animPlayerAngle = Float.NaN;
            this.centerOfMassY = vehicle0.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
            if (BaseVehicle.RENDER_TO_TEXTURE) {
                this.centerOfMassY = 0.0F - BaseVehicle.CENTER_OF_MASS_MAGIC;
            }

            this.alpha = this.object.getAlpha(int0);
            VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)_modelSlot.model;
            IsoLightSource[] lightSources = vehicleModelInstance.getLights();

            for (int int1 = 0; int1 < this.effectLights.length; int1++) {
                this.effectLights[int1].set(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0);
            }

            Vector3f vector3f0 = BaseVehicle.TL_vector3f_pool.get().alloc();

            for (int int2 = 0; int2 < lightSources.length; int2++) {
                IsoLightSource lightSource = lightSources[int2];
                if (lightSource != null) {
                    Vector3f vector3f1 = vehicle0.getLocalPos(lightSource.x + 0.5F, lightSource.y + 0.5F, lightSource.z + 0.75F, vector3f0);
                    vehicle0.fixLightbarModelLighting(lightSource, vector3f0);
                    this.effectLights[int2].set(vector3f1.x, vector3f1.y, vector3f1.z, lightSource.r, lightSource.g, lightSource.b, lightSource.radius);
                }
            }

            BaseVehicle.TL_vector3f_pool.get().release(vector3f0);
            float float0 = 1.0F - Math.min(RenderSettings.getInstance().getPlayerSettings(int0).getDarkness() * 0.6F, 0.8F);
            float0 *= 0.9F;
            this.ambientR = this.ambientG = this.ambientB = float0;
            this.vehicleTransform.set(vehicle0.vehicleTransform);
        } else {
            this.textureCreator = this.character.getTextureCreator();
            if (this.textureCreator != null && this.textureCreator.isRendered()) {
                this.textureCreator = null;
            }

            ModelInstance.PlayerData playerData = _modelSlot.model.playerData[int0];
            this.animPlayer = this.character.getAnimationPlayer();
            this.animPlayerAngle = this.animPlayer.getRenderedAngle();

            for (int int3 = 0; int3 < this.effectLights.length; int3++) {
                ModelInstance.EffectLight effectLight = playerData.effectLightsMain[int3];
                this.effectLights[int3].set(effectLight.x, effectLight.y, effectLight.z, effectLight.r, effectLight.g, effectLight.b, effectLight.radius);
            }

            this.ambientR = playerData.currentAmbient.x;
            this.ambientG = playerData.currentAmbient.y;
            this.ambientB = playerData.currentAmbient.z;
            this.bOutside = this.character.getCurrentSquare() != null && this.character.getCurrentSquare().isOutside();
            this.alpha = this.character.getAlpha(int0);
            if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                this.ambientR = this.ambientG = this.ambientB = 1.0F;
            }

            if (GameServer.bServer && ServerGUI.isCreated()) {
                this.ambientR = this.ambientG = this.ambientB = 1.0F;
            }

            this.bCharacterOutline = this.character.bOutline[int0];
            if (this.bCharacterOutline) {
                this.outlineColor.set(this.character.outlineColor[int0]);
            }

            this.bInVehicle = this.character.isSeatedInVehicle();
            if (this.bInVehicle) {
                this.animPlayerAngle = 0.0F;
                BaseVehicle vehicle1 = this.character.getVehicle();
                this.centerOfMassY = vehicle1.jniTransform.origin.y - BaseVehicle.CENTER_OF_MASS_MAGIC;
                this.x = vehicle1.x;
                this.y = vehicle1.y;
                this.z = vehicle1.z;
                Vector3f vector3f2 = BaseVehicle.TL_vector3f_pool.get().alloc();
                vehicle1.getPassengerLocalPos(vehicle1.getSeat(this.character), vector3f2);
                this.inVehicleX = vector3f2.x;
                this.inVehicleY = vector3f2.y;
                this.inVehicleZ = vector3f2.z;
                BaseVehicle.TL_vector3f_pool.get().release(vector3f2);
                Vector3f vector3f3 = vehicle1.vehicleTransform.getEulerAnglesZYX(BaseVehicle.TL_vector3f_pool.get().alloc());
                this.vehicleAngleZ = (float)java.lang.Math.toDegrees(vector3f3.z);
                this.vehicleAngleY = (float)java.lang.Math.toDegrees(vector3f3.y);
                this.vehicleAngleX = (float)java.lang.Math.toDegrees(vector3f3.x);
                BaseVehicle.TL_vector3f_pool.get().release(vector3f3);
            }
        }

        this.RENDER_TO_TEXTURE = BaseVehicle.RENDER_TO_TEXTURE;
        this.modelData.clear();
        ModelInstanceRenderData modelInstanceRenderData0 = null;
        if (_modelSlot.model.model.isReady() && (_modelSlot.model.AnimPlayer == null || _modelSlot.model.AnimPlayer.isReady())) {
            modelInstanceRenderData0 = ModelInstanceRenderData.alloc().init(_modelSlot.model);
            this.modelData.add(modelInstanceRenderData0);
            if (_modelSlot.sub.size() != _modelSlot.model.sub.size()) {
                boolean boolean0 = true;
            }
        }

        this.initRenderData(_modelSlot.model.sub, modelInstanceRenderData0);
        boolean boolean1 = false;

        for (int int4 = 0; int4 < this.modelData.size(); int4++) {
            ModelInstanceRenderData modelInstanceRenderData1 = this.modelData.get(int4);
            if (this.character != null && modelInstanceRenderData1.modelInstance == this.character.primaryHandModel && this.character.isMuzzleFlash()) {
                modelInstanceRenderData1.m_muzzleFlash = true;
            }

            if (modelInstanceRenderData1.modelInstance != null && modelInstanceRenderData1.modelInstance.hasTextureCreator()) {
                boolean1 = true;
            }
        }

        if (this.textureCreator != null) {
            this.textureCreator.renderRefCount++;
        }

        if (this.character != null && (this.textureCreator != null || boolean1)) {
            assert this.readyModelData.isEmpty();

            ModelInstanceRenderData.release(this.readyModelData);
            this.readyModelData.clear();

            for (int int5 = 0; int5 < this.character.getReadyModelData().size(); int5++) {
                ModelInstance modelInstance = this.character.getReadyModelData().get(int5);
                ModelInstanceRenderData modelInstanceRenderData2 = ModelInstanceRenderData.alloc().init(modelInstance);
                modelInstanceRenderData2.transformToParent(this.getParentData(modelInstance));
                this.readyModelData.add(modelInstanceRenderData2);
            }
        }

        if (Core.bDebug) {
            this.m_debugRenderData = ModelSlotDebugRenderData.alloc().init(this);
        }

        this.bRendered = false;
        return this;
    }

    private ModelInstanceRenderData getParentData(ModelInstance modelInstance) {
        for (int int0 = 0; int0 < this.readyModelData.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = this.readyModelData.get(int0);
            if (modelInstanceRenderData.modelInstance == modelInstance.parent) {
                return modelInstanceRenderData;
            }
        }

        return null;
    }

    private ModelInstanceRenderData initRenderData(ModelInstance modelInstance, ModelInstanceRenderData modelInstanceRenderData1) {
        ModelInstanceRenderData modelInstanceRenderData0 = ModelInstanceRenderData.alloc().init(modelInstance);
        modelInstanceRenderData0.transformToParent(modelInstanceRenderData1);
        this.modelData.add(modelInstanceRenderData0);
        this.initRenderData(modelInstance.sub, modelInstanceRenderData0);
        return modelInstanceRenderData0;
    }

    private void initRenderData(ArrayList<ModelInstance> arrayList, ModelInstanceRenderData modelInstanceRenderData) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ModelInstance modelInstance = (ModelInstance)arrayList.get(int0);
            if (modelInstance.model.isReady() && (modelInstance.AnimPlayer == null || modelInstance.AnimPlayer.isReady())) {
                this.initRenderData(modelInstance, modelInstanceRenderData);
            }
        }
    }

    @Override
    public void render() {
        if (this.character == null) {
            this.renderVehicle();
        } else {
            this.renderCharacter();
        }
    }

    public void renderDebug() {
        if (this.m_debugRenderData != null) {
            this.m_debugRenderData.render();
        }
    }

    private void renderCharacter() {
        this.bReady = true;
        if (this.textureCreator != null && !this.textureCreator.isRendered()) {
            this.textureCreator.render();
            if (!this.textureCreator.isRendered()) {
                this.bReady = false;
            }
        }

        for (int int0 = 0; int0 < this.modelData.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = this.modelData.get(int0);
            ModelInstanceTextureInitializer modelInstanceTextureInitializer = modelInstanceRenderData.modelInstance.getTextureInitializer();
            if (modelInstanceTextureInitializer != null && !modelInstanceTextureInitializer.isRendered()) {
                modelInstanceTextureInitializer.render();
                if (!modelInstanceTextureInitializer.isRendered()) {
                    this.bReady = false;
                }
            }
        }

        if (this.bReady || !this.readyModelData.isEmpty()) {
            if (this.bCharacterOutline) {
                ModelCamera.instance.bDepthMask = false;
                GameProfiler.getInstance().invokeAndMeasure("performRenderCharacterOutline", this, ModelSlotRenderData::performRenderCharacterOutline);
            }

            ModelCamera.instance.bDepthMask = true;
            GameProfiler.getInstance().invokeAndMeasure("renderCharacter", this, ModelSlotRenderData::performRenderCharacter);
            int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
            IsoPlayer player = Type.tryCastTo(this.character, IsoPlayer.class);
            if (player != null && !this.bCharacterOutline && player == IsoPlayer.players[int1]) {
                ModelOutlines.instance.setPlayerRenderData(this);
            }

            this.bRendered = this.bReady;
        }
    }

    private void renderVehicleDebug() {
        if (Core.bDebug) {
            Vector3 vector = Model.tempo;
            ModelCamera.instance.Begin();
            GL11.glMatrixMode(5888);
            GL11.glTranslatef(0.0F, this.centerOfMassY, 0.0F);
            if (this.m_debugRenderData != null && !this.modelData.isEmpty()) {
                PZGLUtil.pushAndMultMatrix(5888, this.modelData.get(0).xfrm);
                this.m_debugRenderData.render();
                PZGLUtil.popMatrix(5888);
            }

            if (DebugOptions.instance.ModelRenderAttachments.getValue()) {
                BaseVehicle vehicle0 = (BaseVehicle)this.object;
                ModelInstanceRenderData modelInstanceRenderData = this.modelData.get(0);
                PZGLUtil.pushAndMultMatrix(5888, this.vehicleTransform);
                float float0 = vehicle0.getScript().getModelScale();
                float float1 = modelInstanceRenderData.modelInstance.scale;
                Matrix4f matrix4f0 = BaseVehicle.TL_matrix4f_pool.get().alloc();
                matrix4f0.scaling(1.0F / float0);
                Matrix4f matrix4f1 = BaseVehicle.TL_matrix4f_pool.get().alloc();

                for (int int0 = 0; int0 < vehicle0.getScript().getAttachmentCount(); int0++) {
                    ModelAttachment modelAttachment = vehicle0.getScript().getAttachment(int0);
                    modelInstanceRenderData.modelInstance.getAttachmentMatrix(modelAttachment, matrix4f1);
                    matrix4f0.mul(matrix4f1, matrix4f1);
                    PZGLUtil.pushAndMultMatrix(5888, matrix4f1);
                    Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 2.0F);
                    PZGLUtil.popMatrix(5888);
                }

                BaseVehicle.TL_matrix4f_pool.get().release(matrix4f1);
                BaseVehicle.TL_matrix4f_pool.get().release(matrix4f0);
                PZGLUtil.popMatrix(5888);
            }

            if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue() && !this.modelData.isEmpty()) {
                BaseVehicle vehicle1 = (BaseVehicle)this.object;
                GL11.glMatrixMode(5888);
                Vector3f vector3f = this.vehicleTransform.getEulerAnglesZYX(BaseVehicle.TL_vector3f_pool.get().alloc());
                GL11.glRotatef((float)java.lang.Math.toDegrees(vector3f.z), 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float)java.lang.Math.toDegrees(vector3f.y), 0.0F, 1.0F, 0.0F);
                GL11.glRotatef((float)java.lang.Math.toDegrees(vector3f.x), 1.0F, 0.0F, 0.0F);
                BaseVehicle.TL_vector3f_pool.get().release(vector3f);
                Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 4.0F);

                for (int int1 = 1; int1 < this.modelData.size(); int1++) {
                    VehicleSubModelInstance vehicleSubModelInstance = Type.tryCastTo(this.modelData.get(int1).modelInstance, VehicleSubModelInstance.class);
                    if (vehicleSubModelInstance != null && vehicleSubModelInstance.modelInfo.wheelIndex >= 0) {
                        float float2 = 1.0F;
                        VehicleScript.Wheel wheel = vehicle1.getScript().getWheel(vehicleSubModelInstance.modelInfo.wheelIndex);
                        byte byte0 = -1;
                        vector.set(
                            wheel.offset.x * byte0,
                            vehicle1.getScript().getModel().offset.y + wheel.offset.y + vehicle1.getScript().getSuspensionRestLength(),
                            wheel.offset.z
                        );
                        Model.debugDrawAxis(
                            vector.x / float2, vector.y / float2, vector.z / float2, vehicle1.getScript().getSuspensionRestLength() / float2, 2.0F
                        );
                    }
                }
            }

            ModelCamera.instance.End();
        }
    }

    private void performRenderCharacter() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glEnable(2929);
        GL11.glDisable(3089);
        ModelInstanceRenderDataList modelInstanceRenderDataList = this.modelData;
        if (this.character != null && !this.bReady) {
            modelInstanceRenderDataList = this.readyModelData;
        }

        Model.CharacterModelCameraBegin(this);

        for (int int0 = 0; int0 < modelInstanceRenderDataList.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData0 = modelInstanceRenderDataList.get(int0);
            modelInstanceRenderData0.RenderCharacter(this);
        }

        if (Core.bDebug) {
            this.renderDebug();

            for (int int1 = 0; int1 < modelInstanceRenderDataList.size(); int1++) {
                ModelInstanceRenderData modelInstanceRenderData1 = modelInstanceRenderDataList.get(int1);
                modelInstanceRenderData1.renderDebug();
            }
        }

        Model.CharacterModelCameraEnd();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
    }

    protected void performRenderCharacterOutline() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glEnable(2929);
        GL11.glDisable(3089);
        ModelInstanceRenderDataList modelInstanceRenderDataList = this.modelData;
        if (this.character != null && !this.bReady) {
            modelInstanceRenderDataList = this.readyModelData;
        }

        if (solidColor == null) {
            solidColor = new Shader("aim_outline_solid", false);
            solidColorStatic = new Shader("aim_outline_solid", true);
        }

        solidColor.Start();
        solidColor.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
        solidColor.End();
        solidColorStatic.Start();
        solidColorStatic.getShaderProgram().setVector4("u_color", this.outlineColor.r, this.outlineColor.g, this.outlineColor.b, this.outlineColor.a);
        solidColorStatic.End();
        boolean boolean0 = ModelOutlines.instance.beginRenderOutline(this.outlineColor);
        ModelOutlines.instance.m_fboA.startDrawing(boolean0, true);
        Model.CharacterModelCameraBegin(this);

        for (int int0 = 0; int0 < modelInstanceRenderDataList.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = modelInstanceRenderDataList.get(int0);
            Shader shader = modelInstanceRenderData.model.Effect;

            try {
                modelInstanceRenderData.model.Effect = modelInstanceRenderData.model.bStatic ? solidColorStatic : solidColor;
                modelInstanceRenderData.RenderCharacter(this);
            } finally {
                modelInstanceRenderData.model.Effect = shader;
            }
        }

        Model.CharacterModelCameraEnd();
        ModelOutlines.instance.m_fboA.endDrawing();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
    }

    private void renderVehicle() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        if (this.RENDER_TO_TEXTURE) {
            GL11.glClear(256);
        }

        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glDisable(3089);
        if (this.RENDER_TO_TEXTURE) {
            ModelManager.instance.bitmap.startDrawing(true, true);
            GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
        }

        for (int int0 = 0; int0 < this.modelData.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = this.modelData.get(int0);
            modelInstanceRenderData.RenderVehicle(this);
        }

        this.renderVehicleDebug();
        if (this.RENDER_TO_TEXTURE) {
            ModelManager.instance.bitmap.endDrawing();
        }

        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glEnable(3553);
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
    }

    private void doneWithTextureCreator(ModelInstanceTextureCreator modelInstanceTextureCreator) {
        if (modelInstanceTextureCreator != null) {
            if (modelInstanceTextureCreator.testNotReady > 0) {
                modelInstanceTextureCreator.testNotReady--;
            }

            if (modelInstanceTextureCreator.renderRefCount <= 0) {
                if (modelInstanceTextureCreator.isRendered()) {
                    modelInstanceTextureCreator.postRender();
                    if (modelInstanceTextureCreator == this.character.getTextureCreator()) {
                        this.character.setTextureCreator(null);
                    }
                } else if (modelInstanceTextureCreator != this.character.getTextureCreator()) {
                    modelInstanceTextureCreator.postRender();
                }
            }
        }
    }

    @Override
    public void postRender() {
        assert this.modelSlot.renderRefCount > 0;

        this.modelSlot.renderRefCount--;
        if (this.textureCreator != null) {
            this.textureCreator.renderRefCount--;
            this.doneWithTextureCreator(this.textureCreator);
            this.textureCreator = null;
        }

        ModelInstanceRenderData.release(this.readyModelData);
        this.readyModelData.clear();
        if (this.bRendered) {
            ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
            this.character.getReadyModelData().clear();

            for (int int0 = 0; int0 < this.modelData.size(); int0++) {
                ModelInstance modelInstance = this.modelData.get(int0).modelInstance;
                modelInstance.renderRefCount++;
                this.character.getReadyModelData().add(modelInstance);
            }
        }

        this.character = null;
        this.object = null;
        this.animPlayer = null;
        this.m_debugRenderData = Pool.tryRelease(this.m_debugRenderData);
        ModelInstanceRenderData.release(this.modelData);
        pool.release(this);
    }

    public static ModelSlotRenderData alloc() {
        return pool.alloc();
    }
}
