// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugOptions;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.list.PZArrayUtil;

public class ModelTransformSampler extends PooledObject implements AnimTrackSampler {
    private AnimationPlayer m_sourceAnimPlayer;
    private AnimationTrack m_track;
    private float m_currentTime = 0.0F;
    private SkinningData m_skinningData;
    private BoneTransform[] m_boneTransforms;
    private Matrix4f[] m_boneModelTransforms;
    private static final Pool<ModelTransformSampler> s_pool = new Pool<>(ModelTransformSampler::new);

    private void init(AnimationPlayer animationPlayer, AnimationTrack animationTrack) {
        this.m_sourceAnimPlayer = animationPlayer;
        this.m_track = AnimationTrack.createClone(animationTrack, AnimationTrack::alloc);
        SkinningData skinningData = this.m_sourceAnimPlayer.getSkinningData();
        int int0 = skinningData.numBones();
        this.m_skinningData = skinningData;
        this.m_boneModelTransforms = PZArrayUtil.newInstance(Matrix4f.class, this.m_boneModelTransforms, int0, Matrix4f::new);
        this.m_boneTransforms = PZArrayUtil.newInstance(BoneTransform.class, this.m_boneTransforms, int0, BoneTransform::alloc);
    }

    public static ModelTransformSampler alloc(AnimationPlayer animationPlayer, AnimationTrack animationTrack) {
        ModelTransformSampler modelTransformSampler = s_pool.alloc();
        modelTransformSampler.init(animationPlayer, animationTrack);
        return modelTransformSampler;
    }

    @Override
    public void onReleased() {
        this.m_sourceAnimPlayer = null;
        this.m_track = Pool.tryRelease(this.m_track);
        this.m_skinningData = null;
        this.m_boneTransforms = Pool.tryRelease(this.m_boneTransforms);
    }

    @Override
    public float getTotalTime() {
        return this.m_track.getDuration();
    }

    @Override
    public boolean isLooped() {
        return this.m_track.isLooping();
    }

    @Override
    public void moveToTime(float float0) {
        this.m_currentTime = float0;
        this.m_track.setCurrentTimeValue(float0);
        this.m_track.Update(0.0F);

        for (int int0 = 0; int0 < this.m_boneTransforms.length; int0++) {
            this.updateBoneAnimationTransform(int0);
        }
    }

    private void updateBoneAnimationTransform(int int0) {
        Vector3f vector3f0 = ModelTransformSampler.L_updateBoneAnimationTransform.pos;
        Quaternion quaternion = ModelTransformSampler.L_updateBoneAnimationTransform.rot;
        Vector3f vector3f1 = ModelTransformSampler.L_updateBoneAnimationTransform.scale;
        Keyframe keyframe = ModelTransformSampler.L_updateBoneAnimationTransform.key;
        AnimationBoneBinding animationBoneBinding = this.m_sourceAnimPlayer.getCounterRotationBone();
        boolean boolean0 = animationBoneBinding != null && animationBoneBinding.getBone() != null && animationBoneBinding.getBone().Index == int0;
        keyframe.setIdentity();
        AnimationTrack animationTrack = this.m_track;
        this.getTrackTransform(int0, animationTrack, vector3f0, quaternion, vector3f1);
        if (boolean0 && animationTrack.getUseDeferredRotation()) {
            if (DebugOptions.instance.Character.Debug.Animate.ZeroCounterRotationBone.getValue()) {
                Vector3f vector3f2 = ModelTransformSampler.L_updateBoneAnimationTransform.rotAxis;
                Matrix4f matrix4f = ModelTransformSampler.L_updateBoneAnimationTransform.rotMat;
                matrix4f.setIdentity();
                vector3f2.set(0.0F, 1.0F, 0.0F);
                matrix4f.rotate((float) (-Math.PI / 2), vector3f2);
                vector3f2.set(1.0F, 0.0F, 0.0F);
                matrix4f.rotate((float) (-Math.PI / 2), vector3f2);
                HelperFunctions.getRotation(matrix4f, quaternion);
            } else {
                Vector3f vector3f3 = HelperFunctions.ToEulerAngles(quaternion, ModelTransformSampler.L_updateBoneAnimationTransform.rotEulers);
                HelperFunctions.ToQuaternion(vector3f3.x, vector3f3.y, (float) (Math.PI / 2), quaternion);
            }
        }

        boolean boolean1 = animationTrack.getDeferredMovementBoneIdx() == int0;
        if (boolean1) {
            Vector3f vector3f4 = animationTrack.getCurrentDeferredCounterPosition(ModelTransformSampler.L_updateBoneAnimationTransform.deferredPos);
            vector3f0.x = vector3f0.x + vector3f4.x;
            vector3f0.y = vector3f0.y + vector3f4.y;
            vector3f0.z = vector3f0.z + vector3f4.z;
        }

        keyframe.Position.set(vector3f0);
        keyframe.Rotation.set(quaternion);
        keyframe.Scale.set(vector3f1);
        this.m_boneTransforms[int0].set(keyframe.Position, keyframe.Rotation, keyframe.Scale);
    }

    private void getTrackTransform(int int0, AnimationTrack animationTrack, Vector3f vector3f0, Quaternion quaternion, Vector3f vector3f1) {
        animationTrack.get(int0, vector3f0, quaternion, vector3f1);
    }

    @Override
    public float getCurrentTime() {
        return this.m_currentTime;
    }

    @Override
    public void getBoneMatrix(int int0, Matrix4f matrix4f) {
        if (int0 == 0) {
            this.m_boneTransforms[0].getMatrix(this.m_boneModelTransforms[0]);
            matrix4f.load(this.m_boneModelTransforms[0]);
        } else {
            SkinningBone skinningBone0 = this.m_skinningData.getBoneAt(int0);
            SkinningBone skinningBone1 = skinningBone0.Parent;
            BoneTransform.mul(
                this.m_boneTransforms[skinningBone0.Index], this.m_boneModelTransforms[skinningBone1.Index], this.m_boneModelTransforms[skinningBone0.Index]
            );
            matrix4f.load(this.m_boneModelTransforms[skinningBone0.Index]);
        }
    }

    @Override
    public int getNumBones() {
        return this.m_skinningData.numBones();
    }

    public static class L_updateBoneAnimationTransform {
        public static final Vector3f pos = new Vector3f();
        public static final Quaternion rot = new Quaternion();
        public static final Vector3f scale = new Vector3f();
        public static final Keyframe key = new Keyframe(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F));
        public static final Vector3f rotAxis = new Vector3f();
        public static final Matrix4f rotMat = new Matrix4f();
        public static final Vector3f rotEulers = new Vector3f();
        public static final Vector3f deferredPos = new Vector3f();
    }
}
