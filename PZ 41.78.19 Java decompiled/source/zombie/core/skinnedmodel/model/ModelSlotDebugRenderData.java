// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.Color;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.debug.DebugOptions;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class ModelSlotDebugRenderData extends PooledObject {
    private static final Pool<ModelSlotDebugRenderData> s_pool = new Pool<>(ModelSlotDebugRenderData::new);
    private ModelSlotRenderData m_slotData;
    private final TFloatArrayList m_boneCoords = new TFloatArrayList();
    private final ArrayList<Matrix4f> m_boneMatrices = new ArrayList<>();
    private final TFloatArrayList m_squareLights = new TFloatArrayList();
    private org.joml.Matrix4f m_weaponMatrix;
    private float m_weaponLength;

    public static ModelSlotDebugRenderData alloc() {
        return s_pool.alloc();
    }

    public ModelSlotDebugRenderData init(ModelSlotRenderData slotData) {
        this.m_slotData = slotData;
        this.initBoneAxis();
        this.initSkeleton();
        this.initLights();
        this.initWeaponHitPoint();

        for (int int0 = 0; int0 < slotData.modelData.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = slotData.modelData.get(int0);
            modelInstanceRenderData.m_debugRenderData = ModelInstanceDebugRenderData.alloc().init(slotData, modelInstanceRenderData);
        }

        return this;
    }

    private void initBoneAxis() {
        for (int int0 = 0; int0 < this.m_boneMatrices.size(); int0++) {
            HelperFunctions.returnMatrix(this.m_boneMatrices.get(int0));
        }

        this.m_boneMatrices.clear();
        if (this.m_slotData.animPlayer != null && this.m_slotData.animPlayer.hasSkinningData()) {
            if (DebugOptions.instance.Character.Debug.Render.Bip01.getValue()) {
                this.initBoneAxis("Bip01");
            }

            if (DebugOptions.instance.Character.Debug.Render.PrimaryHandBone.getValue()) {
                this.initBoneAxis("Bip01_Prop1");
            }

            if (DebugOptions.instance.Character.Debug.Render.SecondaryHandBone.getValue()) {
                this.initBoneAxis("Bip01_Prop2");
            }

            if (DebugOptions.instance.Character.Debug.Render.TranslationData.getValue()) {
                this.initBoneAxis("Translation_Data");
            }
        }
    }

    private void initBoneAxis(String string) {
        Integer integer = this.m_slotData.animPlayer.getSkinningData().BoneIndices.get(string);
        if (integer != null) {
            Matrix4f matrix4f = HelperFunctions.getMatrix();
            matrix4f.load(this.m_slotData.animPlayer.modelTransforms[integer]);
            this.m_boneMatrices.add(matrix4f);
        }
    }

    private void initSkeleton() {
        this.m_boneCoords.clear();
        if (DebugOptions.instance.ModelRenderBones.getValue()) {
            this.initSkeleton(this.m_slotData.animPlayer);
            if (this.m_slotData.object instanceof BaseVehicle) {
                for (int int0 = 0; int0 < this.m_slotData.modelData.size(); int0++) {
                    ModelInstanceRenderData modelInstanceRenderData = this.m_slotData.modelData.get(int0);
                    VehicleSubModelInstance vehicleSubModelInstance = Type.tryCastTo(modelInstanceRenderData.modelInstance, VehicleSubModelInstance.class);
                    if (vehicleSubModelInstance != null) {
                        this.initSkeleton(vehicleSubModelInstance.AnimPlayer);
                    }
                }
            }
        }
    }

    private void initSkeleton(AnimationPlayer animationPlayer) {
        if (animationPlayer != null && animationPlayer.hasSkinningData() && !animationPlayer.isBoneTransformsNeedFirstFrame()) {
            Integer integer = animationPlayer.getSkinningData().BoneIndices.get("Translation_Data");

            for (int int0 = 0; int0 < animationPlayer.modelTransforms.length; int0++) {
                if (integer == null || int0 != integer) {
                    int int1 = animationPlayer.getSkinningData().SkeletonHierarchy.get(int0);
                    if (int1 >= 0) {
                        this.initSkeleton(animationPlayer.modelTransforms, int0);
                        this.initSkeleton(animationPlayer.modelTransforms, int1);
                    }
                }
            }
        }
    }

    private void initSkeleton(Matrix4f[] matrix4fs, int int0) {
        float float0 = matrix4fs[int0].m03;
        float float1 = matrix4fs[int0].m13;
        float float2 = matrix4fs[int0].m23;
        this.m_boneCoords.add(float0);
        this.m_boneCoords.add(float1);
        this.m_boneCoords.add(float2);
    }

    private void initLights() {
        this.m_squareLights.clear();
        if (DebugOptions.instance.ModelRenderLights.getValue()) {
            if (this.m_slotData.character != null) {
                if (this.m_slotData.character.getCurrentSquare() != null) {
                    int int0 = IsoCamera.frameState.playerIndex;
                    IsoGridSquare.ILighting iLighting = this.m_slotData.character.getCurrentSquare().lighting[int0];

                    for (int int1 = 0; int1 < iLighting.resultLightCount(); int1++) {
                        IsoGridSquare.ResultLight resultLight = iLighting.getResultLight(int1);
                        this.m_squareLights.add(resultLight.x);
                        this.m_squareLights.add(resultLight.y);
                        this.m_squareLights.add(resultLight.z);
                    }
                }
            }
        }
    }

    private void initWeaponHitPoint() {
        if (this.m_weaponMatrix != null) {
            BaseVehicle.TL_matrix4f_pool.get().release(this.m_weaponMatrix);
            this.m_weaponMatrix = null;
        }

        if (DebugOptions.instance.ModelRenderWeaponHitPoint.getValue()) {
            if (this.m_slotData.animPlayer != null && this.m_slotData.animPlayer.hasSkinningData()) {
                if (this.m_slotData.character != null) {
                    Integer integer = this.m_slotData.animPlayer.getSkinningData().BoneIndices.get("Bip01_Prop1");
                    if (integer != null) {
                        HandWeapon weapon = Type.tryCastTo(this.m_slotData.character.getPrimaryHandItem(), HandWeapon.class);
                        if (weapon != null) {
                            this.m_weaponLength = weapon.WeaponLength;
                            Matrix4f matrix4f = this.m_slotData.animPlayer.modelTransforms[integer];
                            this.m_weaponMatrix = BaseVehicle.TL_matrix4f_pool.get().alloc();
                            PZMath.convertMatrix(matrix4f, this.m_weaponMatrix);
                            this.m_weaponMatrix.transpose();
                        }
                    }
                }
            }
        }
    }

    public void render() {
        this.renderBonesAxis();
        this.renderSkeleton();
        this.renderLights();
        this.renderWeaponHitPoint();
    }

    private void renderBonesAxis() {
        for (int int0 = 0; int0 < this.m_boneMatrices.size(); int0++) {
            Model.drawBoneMtx(this.m_boneMatrices.get(int0));
        }
    }

    private void renderSkeleton() {
        if (!this.m_boneCoords.isEmpty()) {
            GL11.glDisable(2929);

            for (int int0 = 7; int0 >= 0; int0--) {
                GL13.glActiveTexture(33984 + int0);
                GL11.glDisable(3553);
            }

            GL11.glLineWidth(1.0F);
            GL11.glBegin(1);

            for (byte byte0 = 0; byte0 < this.m_boneCoords.size(); byte0 += 6) {
                Color color = Model.debugDrawColours[byte0 % Model.debugDrawColours.length];
                GL11.glColor3f(color.r, color.g, color.b);
                float float0 = this.m_boneCoords.get(byte0);
                float float1 = this.m_boneCoords.get(byte0 + 1);
                float float2 = this.m_boneCoords.get(byte0 + 2);
                GL11.glVertex3f(float0, float1, float2);
                float0 = this.m_boneCoords.get(byte0 + 3);
                float1 = this.m_boneCoords.get(byte0 + 4);
                float2 = this.m_boneCoords.get(byte0 + 5);
                GL11.glVertex3f(float0, float1, float2);
            }

            GL11.glEnd();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glEnable(2929);
        }
    }

    private void renderLights() {
        for (byte byte0 = 0; byte0 < this.m_squareLights.size(); byte0 += 3) {
            float float0 = this.m_squareLights.get(byte0);
            float float1 = this.m_squareLights.get(byte0 + 1);
            float float2 = this.m_squareLights.get(byte0 + 2);
            Model.debugDrawLightSource(float0, float1, float2, this.m_slotData.x, this.m_slotData.y, this.m_slotData.z, -this.m_slotData.animPlayerAngle);
        }
    }

    private void renderWeaponHitPoint() {
        if (this.m_weaponMatrix != null) {
            PZGLUtil.pushAndMultMatrix(5888, this.m_weaponMatrix);
            Model.debugDrawAxis(0.0F, this.m_weaponLength, 0.0F, 0.05F, 1.0F);
            PZGLUtil.popMatrix(5888);
        }
    }
}
