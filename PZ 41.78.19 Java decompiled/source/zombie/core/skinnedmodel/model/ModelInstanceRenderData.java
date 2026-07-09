// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjglx.BufferUtils;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.ModelAttachment;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class ModelInstanceRenderData {
    private static final Vector3f tempVector3f = new Vector3f();
    public Model model;
    public Texture tex;
    public float depthBias;
    public float hue;
    public float tintR;
    public float tintG;
    public float tintB;
    public int parentBone;
    public FloatBuffer matrixPalette;
    public final Matrix4f xfrm = new Matrix4f();
    public SoftwareModelMeshInstance softwareMesh;
    public ModelInstance modelInstance;
    public boolean m_muzzleFlash = false;
    protected ModelInstanceDebugRenderData m_debugRenderData;
    private static final ObjectPool<ModelInstanceRenderData> pool = new ObjectPool<>(ModelInstanceRenderData::new);

    public ModelInstanceRenderData init(ModelInstance _modelInstance) {
        this.model = _modelInstance.model;
        this.tex = _modelInstance.tex;
        this.depthBias = _modelInstance.depthBias;
        this.hue = _modelInstance.hue;
        this.parentBone = _modelInstance.parentBone;

        assert _modelInstance.character == null || _modelInstance.AnimPlayer != null;

        this.m_muzzleFlash = false;
        this.xfrm.identity();
        if (_modelInstance.AnimPlayer != null && !this.model.bStatic) {
            SkinningData skinningData = (SkinningData)this.model.Tag;
            if (Core.bDebug && skinningData == null) {
                DebugLog.General.warn("skinningData is null, matrixPalette may be invalid");
            }

            org.lwjgl.util.vector.Matrix4f[] matrix4fs = _modelInstance.AnimPlayer.getSkinTransforms(skinningData);
            if (this.matrixPalette == null || this.matrixPalette.capacity() < matrix4fs.length * 16) {
                this.matrixPalette = BufferUtils.createFloatBuffer(matrix4fs.length * 16);
            }

            this.matrixPalette.clear();

            for (int int0 = 0; int0 < matrix4fs.length; int0++) {
                matrix4fs[int0].store(this.matrixPalette);
            }

            this.matrixPalette.flip();
        }

        VehicleSubModelInstance vehicleSubModelInstance = Type.tryCastTo(_modelInstance, VehicleSubModelInstance.class);
        if (_modelInstance instanceof VehicleModelInstance || vehicleSubModelInstance != null) {
            if (_modelInstance instanceof VehicleModelInstance) {
                this.xfrm.set(((BaseVehicle)_modelInstance.object).renderTransform);
            } else {
                this.xfrm.set(vehicleSubModelInstance.modelInfo.renderTransform);
            }

            if (_modelInstance.model.Mesh != null && _modelInstance.model.Mesh.isReady() && _modelInstance.model.Mesh.m_transform != null) {
                _modelInstance.model.Mesh.m_transform.transpose();
                this.xfrm.mul(_modelInstance.model.Mesh.m_transform);
                _modelInstance.model.Mesh.m_transform.transpose();
            }
        }

        this.softwareMesh = _modelInstance.softwareMesh;
        this.modelInstance = _modelInstance;
        _modelInstance.renderRefCount++;
        if (_modelInstance.getTextureInitializer() != null) {
            _modelInstance.getTextureInitializer().renderMain();
        }

        return this;
    }

    public void renderDebug() {
        if (this.m_debugRenderData != null) {
            this.m_debugRenderData.render();
        }
    }

    public void RenderCharacter(ModelSlotRenderData slotData) {
        this.tintR = this.modelInstance.tintR;
        this.tintG = this.modelInstance.tintG;
        this.tintB = this.modelInstance.tintB;
        this.tex = this.modelInstance.tex;
        if (this.tex != null || this.modelInstance.model.tex != null) {
            this.model.DrawChar(slotData, this);
        }
    }

    public void RenderVehicle(ModelSlotRenderData slotData) {
        this.tintR = this.modelInstance.tintR;
        this.tintG = this.modelInstance.tintG;
        this.tintB = this.modelInstance.tintB;
        this.tex = this.modelInstance.tex;
        if (this.tex != null || this.modelInstance.model.tex != null) {
            this.model.DrawVehicle(slotData, this);
        }
    }

    public static Matrix4f makeAttachmentTransform(ModelAttachment attachment, Matrix4f attachmentXfrm) {
        attachmentXfrm.translation(attachment.getOffset());
        org.joml.Vector3f vector3f = attachment.getRotate();
        attachmentXfrm.rotateXYZ(vector3f.x * (float) (Math.PI / 180.0), vector3f.y * (float) (Math.PI / 180.0), vector3f.z * (float) (Math.PI / 180.0));
        return attachmentXfrm;
    }

    public static void applyBoneTransform(ModelInstance parentInstance, String boneName, Matrix4f transform) {
        if (parentInstance != null && parentInstance.AnimPlayer != null) {
            if (!StringUtils.isNullOrWhitespace(boneName)) {
                int int0 = parentInstance.AnimPlayer.getSkinningBoneIndex(boneName, -1);
                if (int0 != -1) {
                    org.lwjgl.util.vector.Matrix4f matrix4f0 = parentInstance.AnimPlayer.GetPropBoneMatrix(int0);
                    Matrix4f matrix4f1 = BaseVehicle.TL_matrix4f_pool.get().alloc();
                    PZMath.convertMatrix(matrix4f0, matrix4f1);
                    matrix4f1.transpose();
                    transform.mul(matrix4f1);
                    BaseVehicle.TL_matrix4f_pool.get().release(matrix4f1);
                }
            }
        }
    }

    public ModelInstanceRenderData transformToParent(ModelInstanceRenderData parentData) {
        if (this.modelInstance instanceof VehicleModelInstance || this.modelInstance instanceof VehicleSubModelInstance) {
            return this;
        } else if (parentData == null) {
            return this;
        } else {
            this.xfrm.set(parentData.xfrm);
            this.xfrm.transpose();
            Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
            ModelAttachment modelAttachment0 = parentData.modelInstance.getAttachmentById(this.modelInstance.attachmentNameParent);
            if (modelAttachment0 == null) {
                if (this.modelInstance.parentBoneName != null && parentData.modelInstance.AnimPlayer != null) {
                    applyBoneTransform(parentData.modelInstance, this.modelInstance.parentBoneName, this.xfrm);
                }
            } else {
                applyBoneTransform(parentData.modelInstance, modelAttachment0.getBone(), this.xfrm);
                makeAttachmentTransform(modelAttachment0, matrix4f);
                this.xfrm.mul(matrix4f);
            }

            ModelAttachment modelAttachment1 = this.modelInstance.getAttachmentById(this.modelInstance.attachmentNameSelf);
            if (modelAttachment1 != null) {
                makeAttachmentTransform(modelAttachment1, matrix4f);
                matrix4f.invert();
                this.xfrm.mul(matrix4f);
            }

            if (this.modelInstance.model.Mesh != null && this.modelInstance.model.Mesh.isReady() && this.modelInstance.model.Mesh.m_transform != null) {
                this.xfrm.mul(this.modelInstance.model.Mesh.m_transform);
            }

            if (this.modelInstance.scale != 1.0F) {
                this.xfrm.scale(this.modelInstance.scale);
            }

            this.xfrm.transpose();
            BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
            return this;
        }
    }

    private void testOnBackItem(ModelInstance modelInstancex) {
        if (modelInstancex.parent != null && modelInstancex.parent.m_modelScript != null) {
            AnimationPlayer animationPlayer = modelInstancex.parent.AnimPlayer;
            ModelAttachment modelAttachment0 = null;

            for (int int0 = 0; int0 < modelInstancex.parent.m_modelScript.getAttachmentCount(); int0++) {
                ModelAttachment modelAttachment1 = modelInstancex.parent.getAttachment(int0);
                if (modelAttachment1.getBone() != null && this.parentBone == animationPlayer.getSkinningBoneIndex(modelAttachment1.getBone(), 0)) {
                    modelAttachment0 = modelAttachment1;
                    break;
                }
            }

            if (modelAttachment0 != null) {
                Matrix4f matrix4f = BaseVehicle.TL_matrix4f_pool.get().alloc();
                makeAttachmentTransform(modelAttachment0, matrix4f);
                this.xfrm.transpose();
                this.xfrm.mul(matrix4f);
                this.xfrm.transpose();
                ModelAttachment modelAttachment2 = modelInstancex.getAttachmentById(modelAttachment0.getId());
                if (modelAttachment2 != null) {
                    makeAttachmentTransform(modelAttachment2, matrix4f);
                    matrix4f.invert();
                    this.xfrm.transpose();
                    this.xfrm.mul(matrix4f);
                    this.xfrm.transpose();
                }

                BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
            }
        }
    }

    public static ModelInstanceRenderData alloc() {
        return pool.alloc();
    }

    public static void release(ArrayList<ModelInstanceRenderData> objs) {
        for (int int0 = 0; int0 < objs.size(); int0++) {
            ModelInstanceRenderData modelInstanceRenderData = (ModelInstanceRenderData)objs.get(int0);
            if (modelInstanceRenderData.modelInstance.getTextureInitializer() != null) {
                modelInstanceRenderData.modelInstance.getTextureInitializer().postRender();
            }

            ModelManager.instance.derefModelInstance(modelInstanceRenderData.modelInstance);
            modelInstanceRenderData.modelInstance = null;
            modelInstanceRenderData.model = null;
            modelInstanceRenderData.tex = null;
            modelInstanceRenderData.softwareMesh = null;
            modelInstanceRenderData.m_debugRenderData = Pool.tryRelease(modelInstanceRenderData.m_debugRenderData);
        }

        pool.release(objs);
    }
}
