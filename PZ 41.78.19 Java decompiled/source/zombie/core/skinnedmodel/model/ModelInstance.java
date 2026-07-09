// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.util.StringUtils;

/**
 * Created by LEMMYPC on 05/01/14.
 */
public class ModelInstance {
    public static float MODEL_LIGHT_MULT_OUTSIDE = 1.7F;
    public static float MODEL_LIGHT_MULT_ROOM = 1.7F;
    public Model model;
    public AnimationPlayer AnimPlayer;
    public SkinningData data;
    public Texture tex;
    public ModelInstanceTextureInitializer m_textureInitializer;
    public IsoGameCharacter character;
    public IsoMovingObject object;
    public float tintR = 1.0F;
    public float tintG = 1.0F;
    public float tintB = 1.0F;
    public ModelInstance parent;
    public int parentBone;
    public String parentBoneName = null;
    public float hue;
    public float depthBias;
    public ModelInstance matrixModel;
    public SoftwareModelMeshInstance softwareMesh;
    public final ArrayList<ModelInstance> sub = new ArrayList<>();
    private int instanceSkip;
    private ItemVisual itemVisual = null;
    public boolean bResetAfterRender = false;
    private Object m_owner = null;
    public int renderRefCount;
    private static final int INITIAL_SKIP_VALUE = Integer.MAX_VALUE;
    private int skipped = Integer.MAX_VALUE;
    public final Object m_lock = "ModelInstance Thread Lock";
    public ModelScript m_modelScript = null;
    public String attachmentNameSelf = null;
    public String attachmentNameParent = null;
    public float scale = 1.0F;
    public String maskVariableValue = null;
    public ModelInstance.PlayerData[] playerData;
    private static final ColorInfo tempColorInfo = new ColorInfo();
    private static final ColorInfo tempColorInfo2 = new ColorInfo();

    public ModelInstance init(Model _model, IsoGameCharacter _character, AnimationPlayer player) {
        this.data = (SkinningData)_model.Tag;
        this.model = _model;
        this.tex = _model.tex;
        if (!_model.bStatic && player == null) {
            player = AnimationPlayer.alloc(_model);
        }

        this.AnimPlayer = player;
        this.character = _character;
        this.object = _character;
        return this;
    }

    public boolean isRendering() {
        return this.renderRefCount > 0;
    }

    public void reset() {
        if (this.tex instanceof SmartTexture) {
            Texture texture = this.tex;
            RenderThread.queueInvokeOnRenderContext(texture::destroy);
        }

        this.AnimPlayer = null;
        this.character = null;
        this.data = null;
        this.hue = 0.0F;
        this.itemVisual = null;
        this.matrixModel = null;
        this.model = null;
        this.object = null;
        this.parent = null;
        this.parentBone = 0;
        this.parentBoneName = null;
        this.skipped = Integer.MAX_VALUE;
        this.sub.clear();
        this.softwareMesh = null;
        this.tex = null;
        if (this.m_textureInitializer != null) {
            this.m_textureInitializer.release();
            this.m_textureInitializer = null;
        }

        this.tintR = 1.0F;
        this.tintG = 1.0F;
        this.tintB = 1.0F;
        this.bResetAfterRender = false;
        this.renderRefCount = 0;
        this.scale = 1.0F;
        this.m_owner = null;
        this.m_modelScript = null;
        this.attachmentNameSelf = null;
        this.attachmentNameParent = null;
        this.maskVariableValue = null;
        if (this.playerData != null) {
            ModelInstance.PlayerData.pool.release(this.playerData);
            Arrays.fill(this.playerData, null);
        }
    }

    public void LoadTexture(String name) {
        if (name != null && name.length() != 0) {
            this.tex = Texture.getSharedTexture("media/textures/" + name + ".png");
            if (this.tex == null) {
                if (name.equals("Vest_White")) {
                    this.tex = Texture.getSharedTexture("media/textures/Shirt_White.png");
                } else if (name.contains("Hair")) {
                    this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
                } else if (name.contains("Beard")) {
                    this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
                } else {
                    DebugLog.log("ERROR: model texture \"" + name + "\" wasn't found");
                }
            }
        } else {
            this.tex = null;
        }
    }

    public void dismember(int bone) {
        this.AnimPlayer.dismember(bone);
    }

    public void UpdateDir() {
        if (this.AnimPlayer != null) {
            this.AnimPlayer.UpdateDir(this.character);
        }
    }

    public void Update() {
        if (this.character != null) {
            float float0 = this.character.DistTo(IsoPlayer.getInstance());
            if (!this.character.amputations.isEmpty() && float0 > 0.0F && this.AnimPlayer != null) {
                this.AnimPlayer.dismembered.clear();
                ArrayList arrayList = this.character.amputations;

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    String string = (String)arrayList.get(int0);
                    this.AnimPlayer.dismember(this.AnimPlayer.getSkinningData().BoneIndices.get(string));
                }
            }

            if (Math.abs(this.character.speedMod - 0.5957F) < 1.0E-4F) {
                boolean boolean0 = false;
            }
        }

        this.instanceSkip = 0;
        if (this.AnimPlayer != null) {
            if (this.matrixModel == null) {
                if (this.skipped >= this.instanceSkip) {
                    if (this.skipped == Integer.MAX_VALUE) {
                        this.skipped = 1;
                    }

                    float float1 = GameTime.instance.getTimeDelta() * this.skipped;
                    this.AnimPlayer.Update(float1);
                } else {
                    this.AnimPlayer.DoAngles();
                }

                this.AnimPlayer.parentPlayer = null;
            } else {
                this.AnimPlayer.parentPlayer = this.matrixModel.AnimPlayer;
            }
        }

        if (this.skipped >= this.instanceSkip) {
            this.skipped = 0;
        }

        this.skipped++;
    }

    public void SetForceDir(Vector2 dir) {
        if (this.AnimPlayer != null) {
            this.AnimPlayer.SetForceDir(dir);
        }
    }

    public void setInstanceSkip(int c) {
        this.instanceSkip = c;

        for (int int0 = 0; int0 < this.sub.size(); int0++) {
            ModelInstance modelInstance = this.sub.get(int0);
            modelInstance.instanceSkip = c;
        }
    }

    public void destroySmartTextures() {
        if (this.tex instanceof SmartTexture) {
            this.tex.destroy();
            this.tex = null;
        }

        for (int int0 = 0; int0 < this.sub.size(); int0++) {
            ModelInstance modelInstance1 = this.sub.get(int0);
            modelInstance1.destroySmartTextures();
        }
    }

    public void updateLights() {
        int int0 = IsoCamera.frameState.playerIndex;
        if (this.playerData == null) {
            this.playerData = new ModelInstance.PlayerData[4];
        }

        boolean boolean0 = this.playerData[int0] == null;
        if (this.playerData[int0] == null) {
            this.playerData[int0] = ModelInstance.PlayerData.pool.alloc();
        }

        this.playerData[int0].updateLights(this.character, boolean0);
    }

    public ItemVisual getItemVisual() {
        return this.itemVisual;
    }

    public void setItemVisual(ItemVisual _itemVisual) {
        this.itemVisual = _itemVisual;
    }

    public void applyModelScriptScale(String modelName) {
        this.m_modelScript = ScriptManager.instance.getModelScript(modelName);
        if (this.m_modelScript != null) {
            this.scale = this.m_modelScript.scale;
        }
    }

    public ModelAttachment getAttachment(int index) {
        return this.m_modelScript == null ? null : this.m_modelScript.getAttachment(index);
    }

    public ModelAttachment getAttachmentById(String id) {
        if (StringUtils.isNullOrWhitespace(id)) {
            return null;
        } else {
            return this.m_modelScript == null ? null : this.m_modelScript.getAttachmentById(id);
        }
    }

    public Matrix4f getAttachmentMatrix(ModelAttachment attachment, Matrix4f out) {
        out.translation(attachment.getOffset());
        Vector3f vector3f = attachment.getRotate();
        out.rotateXYZ(
            vector3f.x * (float) (java.lang.Math.PI / 180.0),
            vector3f.y * (float) (java.lang.Math.PI / 180.0),
            vector3f.z * (float) (java.lang.Math.PI / 180.0)
        );
        return out;
    }

    public Matrix4f getAttachmentMatrix(int index, Matrix4f out) {
        ModelAttachment modelAttachment = this.getAttachment(index);
        return modelAttachment == null ? out.identity() : this.getAttachmentMatrix(modelAttachment, out);
    }

    public Matrix4f getAttachmentMatrixById(String id, Matrix4f out) {
        ModelAttachment modelAttachment = this.getAttachmentById(id);
        return modelAttachment == null ? out.identity() : this.getAttachmentMatrix(modelAttachment, out);
    }

    public void setOwner(Object owner) {
        Objects.requireNonNull(owner);

        assert this.m_owner == null;

        this.m_owner = owner;
    }

    public void clearOwner(Object expectedOwner) {
        Objects.requireNonNull(expectedOwner);

        assert this.m_owner == expectedOwner;

        this.m_owner = null;
    }

    public Object getOwner() {
        return this.m_owner;
    }

    public void setTextureInitializer(ModelInstanceTextureInitializer textureInitializer) {
        this.m_textureInitializer = textureInitializer;
    }

    public ModelInstanceTextureInitializer getTextureInitializer() {
        return this.m_textureInitializer;
    }

    public boolean hasTextureCreator() {
        return this.m_textureInitializer != null && this.m_textureInitializer.isDirty();
    }

    public static final class EffectLight {
        public float x;
        public float y;
        public float z;
        public float r;
        public float g;
        public float b;
        public int radius;

        public void set(float _x, float _y, float _z, float _r, float _g, float _b, int _radius) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
            this.r = _r;
            this.g = _g;
            this.b = _b;
            this.radius = _radius;
        }
    }

    public static enum FrameLightBlendStatus {
        In,
        During,
        Out;
    }

    public static final class FrameLightInfo {
        public ModelInstance.FrameLightBlendStatus Stage;
        public int id;
        public int x;
        public int y;
        public int z;
        public float distSq;
        public int radius;
        public float r;
        public float g;
        public float b;
        public int flags;
        public final org.lwjgl.util.vector.Vector3f currentColor = new org.lwjgl.util.vector.Vector3f();
        public final org.lwjgl.util.vector.Vector3f targetColor = new org.lwjgl.util.vector.Vector3f();
        public boolean active;
        public boolean foundThisFrame;
    }

    public static final class PlayerData {
        ModelInstance.FrameLightInfo[] frameLights;
        ArrayList<IsoGridSquare.ResultLight> chosenLights;
        Vector3f targetAmbient;
        Vector3f currentAmbient;
        ModelInstance.EffectLight[] effectLightsMain;
        private static final ObjectPool<ModelInstance.PlayerData> pool = new ObjectPool<>(ModelInstance.PlayerData::new);

        private void registerFrameLight(IsoGridSquare.ResultLight resultLight) {
            this.chosenLights.add(resultLight);
        }

        private void initFrameLightsForFrame() {
            if (this.frameLights == null) {
                this.effectLightsMain = new ModelInstance.EffectLight[5];

                for (int int0 = 0; int0 < 5; int0++) {
                    this.effectLightsMain[int0] = new ModelInstance.EffectLight();
                }

                this.frameLights = new ModelInstance.FrameLightInfo[5];
                this.chosenLights = new ArrayList<>();
                this.targetAmbient = new Vector3f();
                this.currentAmbient = new Vector3f();
            }

            for (ModelInstance.EffectLight effectLight : this.effectLightsMain) {
                effectLight.radius = -1;
            }

            this.chosenLights.clear();
        }

        private void completeFrameLightsForFrame() {
            for (int int0 = 0; int0 < 5; int0++) {
                if (this.frameLights[int0] != null) {
                    this.frameLights[int0].foundThisFrame = false;
                }
            }

            for (int int1 = 0; int1 < this.chosenLights.size(); int1++) {
                IsoGridSquare.ResultLight resultLight = this.chosenLights.get(int1);
                boolean boolean0 = false;
                int int2 = 0;

                for (int int3 = 0; int3 < 5; int3++) {
                    if (this.frameLights[int3] != null
                        && this.frameLights[int3].active
                        && (
                            resultLight.id != -1
                                ? resultLight.id == this.frameLights[int3].id
                                : this.frameLights[int3].x == resultLight.x
                                    && this.frameLights[int3].y == resultLight.y
                                    && this.frameLights[int3].z == resultLight.z
                        )) {
                        boolean0 = true;
                        int2 = int3;
                        break;
                    }
                }

                if (boolean0) {
                    this.frameLights[int2].foundThisFrame = true;
                    this.frameLights[int2].x = resultLight.x;
                    this.frameLights[int2].y = resultLight.y;
                    this.frameLights[int2].z = resultLight.z;
                    this.frameLights[int2].flags = resultLight.flags;
                    this.frameLights[int2].radius = resultLight.radius;
                    this.frameLights[int2].targetColor.x = resultLight.r;
                    this.frameLights[int2].targetColor.y = resultLight.g;
                    this.frameLights[int2].targetColor.z = resultLight.b;
                    this.frameLights[int2].Stage = ModelInstance.FrameLightBlendStatus.In;
                } else {
                    for (int int4 = 0; int4 < 5; int4++) {
                        if (this.frameLights[int4] == null || !this.frameLights[int4].active) {
                            if (this.frameLights[int4] == null) {
                                this.frameLights[int4] = new ModelInstance.FrameLightInfo();
                            }

                            this.frameLights[int4].x = resultLight.x;
                            this.frameLights[int4].y = resultLight.y;
                            this.frameLights[int4].z = resultLight.z;
                            this.frameLights[int4].r = resultLight.r;
                            this.frameLights[int4].g = resultLight.g;
                            this.frameLights[int4].b = resultLight.b;
                            this.frameLights[int4].flags = resultLight.flags;
                            this.frameLights[int4].radius = resultLight.radius;
                            this.frameLights[int4].id = resultLight.id;
                            this.frameLights[int4].currentColor.x = 0.0F;
                            this.frameLights[int4].currentColor.y = 0.0F;
                            this.frameLights[int4].currentColor.z = 0.0F;
                            this.frameLights[int4].targetColor.x = resultLight.r;
                            this.frameLights[int4].targetColor.y = resultLight.g;
                            this.frameLights[int4].targetColor.z = resultLight.b;
                            this.frameLights[int4].Stage = ModelInstance.FrameLightBlendStatus.In;
                            this.frameLights[int4].active = true;
                            this.frameLights[int4].foundThisFrame = true;
                            break;
                        }
                    }
                }
            }

            float float0 = GameTime.getInstance().getMultiplier();

            for (int int5 = 0; int5 < 5; int5++) {
                ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int5];
                if (frameLightInfo != null && frameLightInfo.active) {
                    if (!frameLightInfo.foundThisFrame) {
                        frameLightInfo.targetColor.x = 0.0F;
                        frameLightInfo.targetColor.y = 0.0F;
                        frameLightInfo.targetColor.z = 0.0F;
                        frameLightInfo.Stage = ModelInstance.FrameLightBlendStatus.Out;
                    }

                    frameLightInfo.currentColor.x = this.step(
                        frameLightInfo.currentColor.x,
                        frameLightInfo.targetColor.x,
                        java.lang.Math.signum(frameLightInfo.targetColor.x - frameLightInfo.currentColor.x) / (60.0F * float0)
                    );
                    frameLightInfo.currentColor.y = this.step(
                        frameLightInfo.currentColor.y,
                        frameLightInfo.targetColor.y,
                        java.lang.Math.signum(frameLightInfo.targetColor.y - frameLightInfo.currentColor.y) / (60.0F * float0)
                    );
                    frameLightInfo.currentColor.z = this.step(
                        frameLightInfo.currentColor.z,
                        frameLightInfo.targetColor.z,
                        java.lang.Math.signum(frameLightInfo.targetColor.z - frameLightInfo.currentColor.z) / (60.0F * float0)
                    );
                    if (frameLightInfo.Stage == ModelInstance.FrameLightBlendStatus.Out
                        && frameLightInfo.currentColor.x < 0.01F
                        && frameLightInfo.currentColor.y < 0.01F
                        && frameLightInfo.currentColor.z < 0.01F) {
                        frameLightInfo.active = false;
                    }
                }
            }
        }

        private void sortLights(IsoGameCharacter character) {
            for (int int0 = 0; int0 < this.frameLights.length; int0++) {
                ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int0];
                if (frameLightInfo != null) {
                    if (!frameLightInfo.active) {
                        frameLightInfo.distSq = Float.MAX_VALUE;
                    } else {
                        frameLightInfo.distSq = IsoUtils.DistanceToSquared(
                            character.x, character.y, character.z, frameLightInfo.x + 0.5F, frameLightInfo.y + 0.5F, frameLightInfo.z
                        );
                    }
                }
            }

            Arrays.sort(
                this.frameLights,
                (frameLightInfo0, frameLightInfo1) -> {
                    boolean boolean0 = frameLightInfo0 == null || frameLightInfo0.radius == -1 || !frameLightInfo0.active;
                    boolean boolean1 = frameLightInfo1 == null || frameLightInfo1.radius == -1 || !frameLightInfo1.active;
                    if (boolean0 && boolean1) {
                        return 0;
                    } else if (boolean0) {
                        return 1;
                    } else if (boolean1) {
                        return -1;
                    } else if (frameLightInfo0.Stage.ordinal() < frameLightInfo1.Stage.ordinal()) {
                        return -1;
                    } else {
                        return frameLightInfo0.Stage.ordinal() > frameLightInfo1.Stage.ordinal()
                            ? 1
                            : (int)java.lang.Math.signum(frameLightInfo0.distSq - frameLightInfo1.distSq);
                    }
                }
            );
        }

        private void updateLights(IsoGameCharacter character, boolean boolean0) {
            this.initFrameLightsForFrame();
            if (character != null) {
                if (character.getCurrentSquare() != null) {
                    IsoGridSquare.ILighting iLighting = character.getCurrentSquare().lighting[IsoCamera.frameState.playerIndex];
                    int int0 = Math.min(iLighting.resultLightCount(), 4);

                    for (int int1 = 0; int1 < int0; int1++) {
                        IsoGridSquare.ResultLight resultLight = iLighting.getResultLight(int1);
                        this.registerFrameLight(resultLight);
                    }

                    if (boolean0) {
                        for (int int2 = 0; int2 < this.frameLights.length; int2++) {
                            if (this.frameLights[int2] != null) {
                                this.frameLights[int2].active = false;
                            }
                        }
                    }

                    this.completeFrameLightsForFrame();
                    character.getCurrentSquare().interpolateLight(ModelInstance.tempColorInfo, character.x % 1.0F, character.y % 1.0F);
                    this.targetAmbient.x = ModelInstance.tempColorInfo.r;
                    this.targetAmbient.y = ModelInstance.tempColorInfo.g;
                    this.targetAmbient.z = ModelInstance.tempColorInfo.b;
                    if (character.z - (int)character.z > 0.2F) {
                        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)character.x, (int)character.y, (int)character.z + 1);
                        if (square != null) {
                            ColorInfo colorInfo = ModelInstance.tempColorInfo2;
                            square.lighting[IsoCamera.frameState.playerIndex].lightInfo();
                            square.interpolateLight(colorInfo, character.x % 1.0F, character.y % 1.0F);
                            ModelInstance.tempColorInfo.interp(colorInfo, (character.z - ((int)character.z + 0.2F)) / 0.8F, ModelInstance.tempColorInfo);
                            this.targetAmbient.set(ModelInstance.tempColorInfo.r, ModelInstance.tempColorInfo.g, ModelInstance.tempColorInfo.b);
                        }
                    }

                    float float0 = GameTime.getInstance().getMultiplier();
                    this.currentAmbient.x = this.step(
                        this.currentAmbient.x, this.targetAmbient.x, (this.targetAmbient.x - this.currentAmbient.x) / (10.0F * float0)
                    );
                    this.currentAmbient.y = this.step(
                        this.currentAmbient.y, this.targetAmbient.y, (this.targetAmbient.y - this.currentAmbient.y) / (10.0F * float0)
                    );
                    this.currentAmbient.z = this.step(
                        this.currentAmbient.z, this.targetAmbient.z, (this.targetAmbient.z - this.currentAmbient.z) / (10.0F * float0)
                    );
                    if (boolean0) {
                        this.setCurrentToTarget();
                    }

                    this.sortLights(character);
                    float float1 = 0.7F;

                    for (int int3 = 0; int3 < 5; int3++) {
                        ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int3];
                        if (frameLightInfo != null && frameLightInfo.active) {
                            ModelInstance.EffectLight effectLight = this.effectLightsMain[int3];
                            if ((frameLightInfo.flags & 1) != 0) {
                                effectLight.set(
                                    character.x,
                                    character.y,
                                    (int)character.z + 1,
                                    frameLightInfo.currentColor.x * float1,
                                    frameLightInfo.currentColor.y * float1,
                                    frameLightInfo.currentColor.z * float1,
                                    frameLightInfo.radius
                                );
                            } else if ((frameLightInfo.flags & 2) != 0) {
                                if (character instanceof IsoPlayer) {
                                    if (GameClient.bClient) {
                                        int int4 = ((IsoPlayer)character).OnlineID + 1;
                                    } else {
                                        int int5 = ((IsoPlayer)character).PlayerIndex + 1;
                                    }

                                    int int6 = ((IsoPlayer)character).PlayerIndex;
                                    int int7 = int6 * 4 + 1;
                                    int int8 = int6 * 4 + 3 + 1;
                                    if (frameLightInfo.id < int7 || frameLightInfo.id > int8) {
                                        effectLight.set(
                                            frameLightInfo.x,
                                            frameLightInfo.y,
                                            frameLightInfo.z,
                                            frameLightInfo.currentColor.x,
                                            frameLightInfo.currentColor.y,
                                            frameLightInfo.currentColor.z,
                                            frameLightInfo.radius
                                        );
                                    }
                                } else {
                                    effectLight.set(
                                        frameLightInfo.x,
                                        frameLightInfo.y,
                                        frameLightInfo.z,
                                        frameLightInfo.currentColor.x * 2.0F,
                                        frameLightInfo.currentColor.y,
                                        frameLightInfo.currentColor.z,
                                        frameLightInfo.radius
                                    );
                                }
                            } else {
                                effectLight.set(
                                    frameLightInfo.x + 0.5F,
                                    frameLightInfo.y + 0.5F,
                                    frameLightInfo.z + 0.5F,
                                    frameLightInfo.currentColor.x * float1,
                                    frameLightInfo.currentColor.y * float1,
                                    frameLightInfo.currentColor.z * float1,
                                    frameLightInfo.radius
                                );
                            }
                        }
                    }

                    if (int0 <= 3 && character instanceof IsoPlayer && character.getTorchStrength() > 0.0F) {
                        this.effectLightsMain[2]
                            .set(
                                character.x + character.getForwardDirection().x * 0.5F,
                                character.y + character.getForwardDirection().y * 0.5F,
                                character.z + 0.25F,
                                1.0F,
                                1.0F,
                                1.0F,
                                2
                            );
                    }

                    float float2 = 0.0F;
                    float float3 = 1.0F;
                    float float4 = this.lerp(float2, float3, this.currentAmbient.x);
                    float float5 = this.lerp(float2, float3, this.currentAmbient.y);
                    float float6 = this.lerp(float2, float3, this.currentAmbient.z);
                    if (character.getCurrentSquare().isOutside()) {
                        float4 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                        float5 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                        float6 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
                        this.effectLightsMain[3]
                            .set(character.x - 2.0F, character.y - 2.0F, character.z + 1.0F, float4 / 4.0F, float5 / 4.0F, float6 / 4.0F, 5000);
                        this.effectLightsMain[4]
                            .set(character.x + 2.0F, character.y + 2.0F, character.z + 1.0F, float4 / 4.0F, float5 / 4.0F, float6 / 4.0F, 5000);
                    } else if (character.getCurrentSquare().getRoom() != null) {
                        float4 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
                        float5 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
                        float6 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
                        this.effectLightsMain[3]
                            .set(character.x - 2.0F, character.y - 2.0F, character.z + 1.0F, float4 / 4.0F, float5 / 4.0F, float6 / 4.0F, 5000);
                        this.effectLightsMain[4]
                            .set(character.x + 2.0F, character.y + 2.0F, character.z + 1.0F, float4 / 4.0F, float5 / 4.0F, float6 / 4.0F, 5000);
                    }
                }
            }
        }

        private float lerp(float float0, float float2, float float1) {
            return float0 + (float2 - float0) * float1;
        }

        private void setCurrentToTarget() {
            for (int int0 = 0; int0 < this.frameLights.length; int0++) {
                ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int0];
                if (frameLightInfo != null) {
                    frameLightInfo.currentColor.set(frameLightInfo.targetColor);
                }
            }

            this.currentAmbient.set(this.targetAmbient);
        }

        private float step(float float0, float float1, float float2) {
            if (float0 < float1) {
                return ClimateManager.clamp(0.0F, float1, float0 + float2);
            } else {
                return float0 > float1 ? ClimateManager.clamp(float1, 1.0F, float0 + float2) : float0;
            }
        }
    }
}
