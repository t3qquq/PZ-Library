// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import zombie.GameProfiler;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.core.math.PZMath;
import zombie.core.math.Vector3;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationRepository;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationTrack;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;
import zombie.network.MPStatistic;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class AnimationPlayer extends PooledObject {
    private Model model;
    private final Matrix4f propTransforms = new Matrix4f();
    private boolean m_boneTransformsNeedFirstFrame = true;
    private TwistableBoneTransform[] m_boneTransforms;
    public Matrix4f[] modelTransforms;
    private AnimationPlayer.SkinTransformData m_skinTransformData = null;
    private AnimationPlayer.SkinTransformData m_skinTransformDataPool = null;
    private SkinningData m_skinningData;
    private SharedSkeleAnimationRepository m_sharedSkeleAnimationRepo = null;
    private SharedSkeleAnimationTrack m_currentSharedTrack;
    private AnimationClip m_currentSharedTrackClip;
    private float m_angle;
    private float m_targetAngle;
    private float m_twistAngle;
    private float m_shoulderTwistAngle;
    private float m_targetTwistAngle;
    private float m_maxTwistAngle = PZMath.degToRad(70.0F);
    private float m_excessTwist = 0.0F;
    private static final float angleStepBase = 0.15F;
    public float angleStepDelta = 1.0F;
    public float angleTwistDelta = 1.0F;
    public boolean bDoBlending = true;
    public boolean bUpdateBones = true;
    private final Vector2 m_lastSetDir = new Vector2();
    private final ArrayList<AnimationBoneBindingPair> m_reparentedBoneBindings = new ArrayList<>();
    private final List<AnimationBoneBinding> m_twistBones = new ArrayList<>();
    private AnimationBoneBinding m_counterRotationBone = null;
    public final ArrayList<Integer> dismembered = new ArrayList<>();
    private final float m_minimumValidAnimWeight = 0.001F;
    private final int m_animBlendIndexCacheSize = 32;
    private final int[] m_animBlendIndices = new int[32];
    private final float[] m_animBlendWeights = new float[32];
    private final int[] m_animBlendLayers = new int[32];
    private final int[] m_animBlendPriorities = new int[32];
    private final int m_maxLayers = 4;
    private final int[] m_layerBlendCounts = new int[4];
    private final float[] m_layerWeightTotals = new float[4];
    private int m_totalAnimBlendCount = 0;
    public AnimationPlayer parentPlayer;
    private final Vector2 m_deferredMovement = new Vector2();
    private float m_deferredRotationWeight = 0.0F;
    private float m_deferredAngleDelta = 0.0F;
    private AnimationPlayerRecorder m_recorder = null;
    private static final AnimationTrack[] tempTracks = new AnimationTrack[0];
    private static final Vector2 tempo = new Vector2();
    private static final Pool<AnimationPlayer> s_pool = new Pool<>(AnimationPlayer::new);
    private final AnimationMultiTrack m_multiTrack = new AnimationMultiTrack();

    private AnimationPlayer() {
    }

    public static AnimationPlayer alloc(Model _model) {
        AnimationPlayer animationPlayer = s_pool.alloc();
        animationPlayer.setModel(_model);
        return animationPlayer;
    }

    /**
     * 
     * @param from
     * @param to
     * @param fadeTimeTo1 The time to go from 0
     */
    public static float lerpBlendWeight(float from, float to, float fadeTimeTo1) {
        if (PZMath.equal(from, to, 1.0E-4F)) {
            return to;
        } else {
            float float0 = 1.0F / fadeTimeTo1;
            float float1 = GameTime.getInstance().getTimeDelta();
            float float2 = to - from;
            float float3 = PZMath.sign(float2);
            float float4 = from + float3 * float0 * float1;
            float float5 = to - float4;
            float float6 = PZMath.sign(float5);
            if (float6 != float3) {
                float4 = to;
            }

            return float4;
        }
    }

    public void setModel(Model _model) {
        Objects.requireNonNull(_model);
        if (_model != this.model) {
            this.model = _model;
            this.initSkinningData();
        }
    }

    public Model getModel() {
        return this.model;
    }

    private void initSkinningData() {
        if (this.model.isReady()) {
            SkinningData skinningData = (SkinningData)this.model.Tag;
            if (skinningData != null) {
                if (this.m_skinningData != skinningData) {
                    if (this.m_skinningData != null) {
                        this.m_skinningData = null;
                        this.m_multiTrack.reset();
                    }

                    this.m_skinningData = skinningData;
                    Lambda.forEachFrom(PZArrayUtil::forEach, this.m_reparentedBoneBindings, this.m_skinningData, AnimationBoneBindingPair::setSkinningData);
                    Lambda.forEachFrom(PZArrayUtil::forEach, this.m_twistBones, this.m_skinningData, AnimationBoneBinding::setSkinningData);
                    if (this.m_counterRotationBone != null) {
                        this.m_counterRotationBone.setSkinningData(this.m_skinningData);
                    }

                    int int0 = skinningData.numBones();
                    this.modelTransforms = PZArrayUtil.newInstance(Matrix4f.class, this.modelTransforms, int0, Matrix4f::new);
                    this.m_boneTransforms = PZArrayUtil.newInstance(TwistableBoneTransform.class, this.m_boneTransforms, int0);

                    for (int int1 = 0; int1 < int0; int1++) {
                        if (this.m_boneTransforms[int1] == null) {
                            this.m_boneTransforms[int1] = TwistableBoneTransform.alloc();
                        }

                        this.m_boneTransforms[int1].setIdentity();
                    }

                    this.m_boneTransformsNeedFirstFrame = true;
                }
            }
        }
    }

    public boolean isReady() {
        this.initSkinningData();
        return this.hasSkinningData();
    }

    public boolean hasSkinningData() {
        return this.m_skinningData != null;
    }

    public void addBoneReparent(String boneName, String newParentBone) {
        if (!PZArrayUtil.contains(this.m_reparentedBoneBindings, Lambda.predicate(boneName, newParentBone, AnimationBoneBindingPair::matches))) {
            AnimationBoneBindingPair animationBoneBindingPair = new AnimationBoneBindingPair(boneName, newParentBone);
            animationBoneBindingPair.setSkinningData(this.m_skinningData);
            this.m_reparentedBoneBindings.add(animationBoneBindingPair);
        }
    }

    public void setTwistBones(String... bones) {
        ArrayList arrayList = AnimationPlayer.L_setTwistBones.boneNames;
        PZArrayUtil.listConvert(this.m_twistBones, arrayList, animationBoneBinding -> animationBoneBinding.boneName);
        if (!PZArrayUtil.sequenceEqual(bones, arrayList, PZArrayUtil.Comparators::equalsIgnoreCase)) {
            this.m_twistBones.clear();
            Lambda.forEachFrom(PZArrayUtil::forEach, bones, this, (object, animationPlayer) -> {
                AnimationBoneBinding animationBoneBinding = new AnimationBoneBinding((String)object);
                animationBoneBinding.setSkinningData(animationPlayer.m_skinningData);
                animationPlayer.m_twistBones.add(animationBoneBinding);
            });
        }
    }

    public void setCounterRotationBone(String boneName) {
        if (this.m_counterRotationBone != null && StringUtils.equals(this.m_counterRotationBone.boneName, boneName)) {
        }

        this.m_counterRotationBone = new AnimationBoneBinding(boneName);
        this.m_counterRotationBone.setSkinningData(this.m_skinningData);
    }

    public AnimationBoneBinding getCounterRotationBone() {
        return this.m_counterRotationBone;
    }

    public void reset() {
        this.m_multiTrack.reset();
    }

    @Override
    public void onReleased() {
        this.model = null;
        this.m_skinningData = null;
        this.propTransforms.setIdentity();
        this.m_boneTransformsNeedFirstFrame = true;
        IPooledObject.tryReleaseAndBlank(this.m_boneTransforms);
        PZArrayUtil.forEach(this.modelTransforms, Matrix::setIdentity);
        this.resetSkinTransforms();
        this.setAngle(0.0F);
        this.setTargetAngle(0.0F);
        this.m_twistAngle = 0.0F;
        this.m_shoulderTwistAngle = 0.0F;
        this.m_targetTwistAngle = 0.0F;
        this.m_maxTwistAngle = PZMath.degToRad(70.0F);
        this.m_excessTwist = 0.0F;
        this.angleStepDelta = 1.0F;
        this.angleTwistDelta = 1.0F;
        this.bDoBlending = true;
        this.bUpdateBones = true;
        this.m_lastSetDir.set(0.0F, 0.0F);
        this.m_reparentedBoneBindings.clear();
        this.m_twistBones.clear();
        this.m_counterRotationBone = null;
        this.dismembered.clear();
        Arrays.fill(this.m_animBlendIndices, 0);
        Arrays.fill(this.m_animBlendWeights, 0.0F);
        Arrays.fill(this.m_animBlendLayers, 0);
        Arrays.fill(this.m_layerBlendCounts, 0);
        Arrays.fill(this.m_layerWeightTotals, 0.0F);
        this.m_totalAnimBlendCount = 0;
        this.parentPlayer = null;
        this.m_deferredMovement.set(0.0F, 0.0F);
        this.m_deferredRotationWeight = 0.0F;
        this.m_deferredAngleDelta = 0.0F;
        this.m_recorder = null;
        this.m_multiTrack.reset();
    }

    public SkinningData getSkinningData() {
        return this.m_skinningData;
    }

    public HashMap<String, Integer> getSkinningBoneIndices() {
        return this.m_skinningData != null ? this.m_skinningData.BoneIndices : null;
    }

    public int getSkinningBoneIndex(String boneName, int defaultVal) {
        HashMap hashMap = this.getSkinningBoneIndices();
        return hashMap != null ? (Integer)hashMap.get(boneName) : defaultVal;
    }

    private synchronized AnimationPlayer.SkinTransformData getSkinTransformData(SkinningData skinningData) {
        for (AnimationPlayer.SkinTransformData skinTransformData0 = this.m_skinTransformData;
            skinTransformData0 != null;
            skinTransformData0 = skinTransformData0.m_next
        ) {
            if (skinningData == skinTransformData0.m_skinnedTo) {
                return skinTransformData0;
            }
        }

        AnimationPlayer.SkinTransformData skinTransformData1;
        if (this.m_skinTransformDataPool != null) {
            skinTransformData1 = this.m_skinTransformDataPool;
            skinTransformData1.setSkinnedTo(skinningData);
            skinTransformData1.dirty = true;
            this.m_skinTransformDataPool = this.m_skinTransformDataPool.m_next;
        } else {
            skinTransformData1 = AnimationPlayer.SkinTransformData.alloc(skinningData);
        }

        skinTransformData1.m_next = this.m_skinTransformData;
        this.m_skinTransformData = skinTransformData1;
        return skinTransformData1;
    }

    private synchronized void resetSkinTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("resetSkinTransforms", this, AnimationPlayer::resetSkinTransformsInternal);
    }

    private void resetSkinTransformsInternal() {
        if (this.m_skinTransformDataPool != null) {
            AnimationPlayer.SkinTransformData skinTransformData = this.m_skinTransformDataPool;

            while (skinTransformData.m_next != null) {
                skinTransformData = skinTransformData.m_next;
            }

            skinTransformData.m_next = this.m_skinTransformData;
        } else {
            this.m_skinTransformDataPool = this.m_skinTransformData;
        }

        this.m_skinTransformData = null;
    }

    public Matrix4f GetPropBoneMatrix(int bone) {
        this.propTransforms.load(this.modelTransforms[bone]);
        return this.propTransforms;
    }

    private AnimationTrack startClip(AnimationClip animationClip, boolean boolean0) {
        if (animationClip == null) {
            throw new NullPointerException("Supplied clip is null.");
        } else {
            AnimationTrack animationTrack = AnimationTrack.alloc();
            animationTrack.startClip(animationClip, boolean0);
            animationTrack.name = animationClip.Name;
            animationTrack.IsPlaying = true;
            this.m_multiTrack.addTrack(animationTrack);
            return animationTrack;
        }
    }

    public static void releaseTracks(List<AnimationTrack> tracks) {
        AnimationTrack[] animationTracks = tracks.toArray(tempTracks);
        PZArrayUtil.forEach(animationTracks, PooledObject::release);
    }

    public AnimationTrack play(String animName, boolean looped) {
        if (this.m_skinningData == null) {
            return null;
        } else {
            AnimationClip animationClip = this.m_skinningData.AnimationClips.get(animName);
            if (animationClip == null) {
                DebugLog.General.warn("Anim Clip not found: %s", animName);
                return null;
            } else {
                return this.startClip(animationClip, looped);
            }
        }
    }

    public void Update() {
        this.Update(GameTime.instance.getTimeDelta());
    }

    public void Update(float deltaT) {
        MPStatistic.getInstance().AnimationPlayerUpdate.Start();
        GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.Update", this, deltaT, AnimationPlayer::updateInternal);
        MPStatistic.getInstance().AnimationPlayerUpdate.End();
    }

    private void updateInternal(float float0) {
        if (this.isReady()) {
            this.m_multiTrack.Update((float)float0);
            if (!this.bUpdateBones) {
                this.updateAnimation_NonVisualOnly();
            } else if (this.m_multiTrack.getTrackCount() > 0) {
                SharedSkeleAnimationTrack sharedSkeleAnimationTrack = this.determineCurrentSharedSkeleTrack();
                if (sharedSkeleAnimationTrack != null) {
                    float float1 = this.m_multiTrack.getTrackAt(0).getCurrentTime();
                    this.updateAnimation_SharedSkeleTrack(sharedSkeleAnimationTrack, float1);
                } else {
                    this.updateAnimation_StandardAnimation();
                }
            }
        }
    }

    private SharedSkeleAnimationTrack determineCurrentSharedSkeleTrack() {
        if (this.m_sharedSkeleAnimationRepo == null) {
            return null;
        } else if (this.bDoBlending) {
            return null;
        } else if (!DebugOptions.instance.Animation.SharedSkeles.Enabled.getValue()) {
            return null;
        } else if (this.m_multiTrack.getTrackCount() != 1) {
            return null;
        } else if (!PZMath.equal(this.m_twistAngle, 0.0F, 114.59155F)) {
            return null;
        } else if (this.parentPlayer != null) {
            return null;
        } else {
            AnimationTrack animationTrack = this.m_multiTrack.getTrackAt(0);
            float float0 = animationTrack.blendFieldWeight;
            if (!PZMath.equal(float0, 0.0F, 0.1F)) {
                return null;
            } else {
                AnimationClip animationClip = animationTrack.getClip();
                if (animationClip == this.m_currentSharedTrackClip) {
                    return this.m_currentSharedTrack;
                } else {
                    SharedSkeleAnimationTrack sharedSkeleAnimationTrack = this.m_sharedSkeleAnimationRepo.getTrack(animationClip);
                    if (sharedSkeleAnimationTrack == null) {
                        DebugLog.Animation.debugln("Caching SharedSkeleAnimationTrack: %s", animationTrack.name);
                        sharedSkeleAnimationTrack = new SharedSkeleAnimationTrack();
                        ModelTransformSampler modelTransformSampler = ModelTransformSampler.alloc(this, animationTrack);

                        try {
                            sharedSkeleAnimationTrack.set(modelTransformSampler, 5.0F);
                        } finally {
                            modelTransformSampler.release();
                        }

                        this.m_sharedSkeleAnimationRepo.setTrack(animationClip, sharedSkeleAnimationTrack);
                    }

                    this.m_currentSharedTrackClip = animationClip;
                    this.m_currentSharedTrack = sharedSkeleAnimationTrack;
                    return sharedSkeleAnimationTrack;
                }
            }
        }
    }

    private void updateAnimation_NonVisualOnly() {
        this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
        this.DoAngles();
        this.calculateDeferredMovement();
    }

    public void setSharedAnimRepo(SharedSkeleAnimationRepository repo) {
        this.m_sharedSkeleAnimationRepo = repo;
    }

    private void updateAnimation_SharedSkeleTrack(SharedSkeleAnimationTrack sharedSkeleAnimationTrack, float float0) {
        this.updateMultiTrackBoneTransforms_DeferredMovementOnly();
        this.DoAngles();
        this.calculateDeferredMovement();
        sharedSkeleAnimationTrack.moveToTime(float0);

        for (int int0 = 0; int0 < this.modelTransforms.length; int0++) {
            sharedSkeleAnimationTrack.getBoneMatrix(int0, this.modelTransforms[int0]);
        }

        this.UpdateSkinTransforms();
    }

    private void updateAnimation_StandardAnimation() {
        if (this.parentPlayer == null) {
            this.updateMultiTrackBoneTransforms();
        } else {
            this.copyBoneTransformsFromParentPlayer();
        }

        this.DoAngles();
        this.calculateDeferredMovement();
        this.updateTwistBone();
        this.applyBoneReParenting();
        this.updateModelTransforms();
        this.UpdateSkinTransforms();
    }

    private void copyBoneTransformsFromParentPlayer() {
        this.m_boneTransformsNeedFirstFrame = false;

        for (int int0 = 0; int0 < this.m_boneTransforms.length; int0++) {
            this.m_boneTransforms[int0].set(this.parentPlayer.m_boneTransforms[int0]);
        }
    }

    public static float calculateAnimPlayerAngle(Vector2 dir) {
        return dir.getDirection();
    }

    public void SetDir(Vector2 dir) {
        if (this.m_lastSetDir.x != dir.x || this.m_lastSetDir.y != dir.y) {
            this.setTargetAngle(calculateAnimPlayerAngle(dir));
            this.m_targetTwistAngle = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
            float float0 = PZMath.clamp(this.m_targetTwistAngle, -this.m_maxTwistAngle, this.m_maxTwistAngle);
            this.m_excessTwist = PZMath.getClosestAngle(float0, this.m_targetTwistAngle);
            this.m_lastSetDir.set(dir);
        }
    }

    public void SetForceDir(Vector2 dir) {
        this.setTargetAngle(calculateAnimPlayerAngle(dir));
        this.setAngleToTarget();
        this.m_targetTwistAngle = 0.0F;
        this.m_lastSetDir.set(dir);
    }

    public void UpdateDir(IsoGameCharacter character) {
        if (character != null) {
            this.SetDir(character.getForwardDirection());
        }
    }

    public void DoAngles() {
        GameProfiler.getInstance().invokeAndMeasure("AnimationPlayer.doAngles", this, AnimationPlayer::doAnglesInternal);
    }

    private void doAnglesInternal() {
        float float0 = 0.15F * GameTime.instance.getMultiplier();
        this.interpolateBodyAngle(float0);
        this.interpolateBodyTwist(float0);
        this.interpolateShoulderTwist(float0);
    }

    private void interpolateBodyAngle(float float3) {
        float float0 = PZMath.getClosestAngle(this.m_angle, this.m_targetAngle);
        if (PZMath.equal(float0, 0.0F, 0.001F)) {
            this.setAngleToTarget();
            this.m_targetTwistAngle = 0.0F;
        } else {
            float float1 = PZMath.sign(float0);
            float float2 = float3 * float1 * this.angleStepDelta;
            float float4;
            if (DebugOptions.instance.Character.Debug.Animate.DeferredRotationOnly.getValue()) {
                float4 = this.m_deferredAngleDelta;
            } else if (this.m_deferredRotationWeight > 0.0F) {
                float4 = this.m_deferredAngleDelta;
            } else {
                float4 = float2;
            }

            float float5 = PZMath.sign(float4);
            float float6 = this.m_angle;
            float float7 = float6 + float4;
            float float8 = PZMath.getClosestAngle(float7, this.m_targetAngle);
            float float9 = PZMath.sign(float8);
            if (float9 != float1 && float5 == float1) {
                this.setAngleToTarget();
                this.m_targetTwistAngle = 0.0F;
            } else {
                this.setAngle(float7);
                this.m_targetTwistAngle = float8;
            }
        }
    }

    private void interpolateBodyTwist(float float5) {
        float float0 = PZMath.wrap(this.m_targetTwistAngle, (float) -Math.PI, (float) Math.PI);
        float float1 = PZMath.clamp(float0, -this.m_maxTwistAngle, this.m_maxTwistAngle);
        this.m_excessTwist = PZMath.getClosestAngle(float1, float0);
        float float2 = PZMath.getClosestAngle(this.m_twistAngle, float1);
        if (PZMath.equal(float2, 0.0F, 0.001F)) {
            this.m_twistAngle = float1;
        } else {
            float float3 = PZMath.sign(float2);
            float float4 = float5 * float3 * this.angleTwistDelta;
            float float6 = this.m_twistAngle;
            float float7 = float6 + float4;
            float float8 = PZMath.getClosestAngle(float7, float1);
            float float9 = PZMath.sign(float8);
            if (float9 == float3) {
                this.m_twistAngle = float7;
            } else {
                this.m_twistAngle = float1;
            }
        }
    }

    private void interpolateShoulderTwist(float float4) {
        float float0 = PZMath.wrap(this.m_twistAngle, (float) -Math.PI, (float) Math.PI);
        float float1 = PZMath.getClosestAngle(this.m_shoulderTwistAngle, float0);
        if (PZMath.equal(float1, 0.0F, 0.001F)) {
            this.m_shoulderTwistAngle = float0;
        } else {
            float float2 = PZMath.sign(float1);
            float float3 = float4 * float2 * this.angleTwistDelta * 0.55F;
            float float5 = this.m_shoulderTwistAngle;
            float float6 = float5 + float3;
            float float7 = PZMath.getClosestAngle(float6, float0);
            float float8 = PZMath.sign(float7);
            if (float8 == float2) {
                this.m_shoulderTwistAngle = float6;
            } else {
                this.m_shoulderTwistAngle = float0;
            }
        }
    }

    private void updateTwistBone() {
        GameProfiler.getInstance().invokeAndMeasure("updateTwistBone", this, AnimationPlayer::updateTwistBoneInternal);
    }

    private void updateTwistBoneInternal() {
        if (!this.m_twistBones.isEmpty()) {
            float float0 = PZMath.degToRad(1.0F);
            if (!PZMath.equal(this.m_twistAngle, 0.0F, float0)) {
                if (!DebugOptions.instance.Character.Debug.Animate.NoBoneTwists.getValue()) {
                    int int0 = this.m_twistBones.size();
                    int int1 = int0 - 1;
                    float float1 = -this.m_shoulderTwistAngle;
                    float float2 = float1 / int1;

                    for (int int2 = 0; int2 < int1; int2++) {
                        SkinningBone skinningBone0 = this.m_twistBones.get(int2).getBone();
                        this.applyTwistBone(skinningBone0, float2);
                    }

                    float float3 = -this.m_twistAngle;
                    float float4 = PZMath.getClosestAngle(float1, float3);
                    if (PZMath.abs(float4) > 1.0E-4F) {
                        SkinningBone skinningBone1 = this.m_twistBones.get(int1).getBone();
                        this.applyTwistBone(skinningBone1, float4);
                    }
                }
            }
        }
    }

    private void applyTwistBone(SkinningBone skinningBone, float float1) {
        if (skinningBone != null) {
            int int0 = skinningBone.Index;
            int int1 = skinningBone.Parent.Index;
            Matrix4f matrix4f0 = this.getBoneModelTransform(int1, AnimationPlayer.L_applyTwistBone.twistParentBoneTrans);
            Matrix4f matrix4f1 = Matrix4f.invert(matrix4f0, AnimationPlayer.L_applyTwistBone.twistParentBoneTransInv);
            if (matrix4f1 != null) {
                Matrix4f matrix4f2 = this.getBoneModelTransform(int0, AnimationPlayer.L_applyTwistBone.twistBoneTrans);
                Quaternion quaternion0 = AnimationPlayer.L_applyTwistBone.twistBoneTargetRot;
                Matrix4f matrix4f3 = AnimationPlayer.L_applyTwistBone.twistRotDiffTrans;
                matrix4f3.setIdentity();
                AnimationPlayer.L_applyTwistBone.twistRotDiffTransAxis.set(0.0F, 1.0F, 0.0F);
                float float0 = PZMath.getClosestAngle(this.m_boneTransforms[int0].Twist, float1);
                this.m_boneTransforms[int0].Twist = float1;
                matrix4f3.rotate(float0, AnimationPlayer.L_applyTwistBone.twistRotDiffTransAxis);
                Matrix4f matrix4f4 = AnimationPlayer.L_applyTwistBone.twistBoneTargetTrans;
                Matrix4f.mul(matrix4f2, matrix4f3, matrix4f4);
                HelperFunctions.getRotation(matrix4f4, quaternion0);
                Quaternion quaternion1 = AnimationPlayer.L_applyTwistBone.twistBoneNewRot;
                quaternion1.set(quaternion0);
                Vector3f vector3f0 = HelperFunctions.getPosition(matrix4f2, AnimationPlayer.L_applyTwistBone.twistBonePos);
                Vector3f vector3f1 = AnimationPlayer.L_applyTwistBone.twistBoneScale;
                vector3f1.set(1.0F, 1.0F, 1.0F);
                Matrix4f matrix4f5 = AnimationPlayer.L_applyTwistBone.twistBoneNewTrans;
                HelperFunctions.CreateFromQuaternionPositionScale(vector3f0, quaternion1, vector3f1, matrix4f5);
                this.m_boneTransforms[int0].mul(matrix4f5, matrix4f1);
            }
        }
    }

    public void resetBoneModelTransforms() {
        if (this.m_skinningData != null && this.modelTransforms != null) {
            this.m_boneTransformsNeedFirstFrame = true;
            int int0 = this.m_boneTransforms.length;

            for (int int1 = 0; int1 < int0; int1++) {
                this.m_boneTransforms[int1].BlendWeight = 0.0F;
                this.m_boneTransforms[int1].setIdentity();
                this.modelTransforms[int1].setIdentity();
            }
        }
    }

    public boolean isBoneTransformsNeedFirstFrame() {
        return this.m_boneTransformsNeedFirstFrame;
    }

    private void updateMultiTrackBoneTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("updateMultiTrackBoneTransforms", this, AnimationPlayer::updateMultiTrackBoneTransformsInternal);
    }

    private void updateMultiTrackBoneTransformsInternal() {
        for (int int0 = 0; int0 < this.modelTransforms.length; int0++) {
            this.modelTransforms[int0].setIdentity();
        }

        this.updateLayerBlendWeightings();
        if (this.m_totalAnimBlendCount != 0) {
            if (this.isRecording()) {
                this.m_recorder.logAnimWeights(this.m_multiTrack.getTracks(), this.m_animBlendIndices, this.m_animBlendWeights, this.m_deferredMovement);
            }

            for (int int1 = 0; int1 < this.m_boneTransforms.length; int1++) {
                if (!this.isBoneReparented(int1)) {
                    this.updateBoneAnimationTransform(int1, null);
                }
            }

            this.m_boneTransformsNeedFirstFrame = false;
        }
    }

    private void updateLayerBlendWeightings() {
        List list = this.m_multiTrack.getTracks();
        int int0 = list.size();
        PZArrayUtil.arraySet(this.m_animBlendIndices, -1);
        PZArrayUtil.arraySet(this.m_animBlendWeights, 0.0F);
        PZArrayUtil.arraySet(this.m_animBlendLayers, -1);
        PZArrayUtil.arraySet(this.m_animBlendPriorities, 0);

        for (int int1 = 0; int1 < int0; int1++) {
            AnimationTrack animationTrack = (AnimationTrack)list.get(int1);
            float float0 = animationTrack.BlendDelta;
            int int2 = animationTrack.getLayerIdx();
            int int3 = animationTrack.getPriority();
            if (int2 >= 0 && int2 < 4) {
                if (!(float0 < 0.001F) && (int2 <= 0 || !animationTrack.isFinished())) {
                    int int4 = -1;

                    for (int int5 = 0; int5 < this.m_animBlendIndices.length; int5++) {
                        if (this.m_animBlendIndices[int5] == -1) {
                            int4 = int5;
                            break;
                        }

                        if (int2 <= this.m_animBlendLayers[int5]) {
                            if (int2 < this.m_animBlendLayers[int5]) {
                                int4 = int5;
                                break;
                            }

                            if (int3 <= this.m_animBlendPriorities[int5]) {
                                if (int3 < this.m_animBlendPriorities[int5]) {
                                    int4 = int5;
                                    break;
                                }

                                if (float0 < this.m_animBlendWeights[int5]) {
                                    int4 = int5;
                                    break;
                                }
                            }
                        }
                    }

                    if (int4 < 0) {
                        DebugLog.General
                            .error(
                                "Buffer overflow. Insufficient anim blends in cache. More than %d animations are being blended at once. Will be truncated to %d.",
                                this.m_animBlendIndices.length,
                                this.m_animBlendIndices.length
                            );
                    } else {
                        PZArrayUtil.insertAt(this.m_animBlendIndices, int4, int1);
                        PZArrayUtil.insertAt(this.m_animBlendWeights, int4, float0);
                        PZArrayUtil.insertAt(this.m_animBlendLayers, int4, int2);
                        PZArrayUtil.insertAt(this.m_animBlendPriorities, int4, int3);
                    }
                }
            } else {
                DebugLog.General.error("Layer index is out of range: %d. Range: 0 - %d", int2, 3);
            }
        }

        PZArrayUtil.arraySet(this.m_layerBlendCounts, 0);
        PZArrayUtil.arraySet(this.m_layerWeightTotals, 0.0F);
        this.m_totalAnimBlendCount = 0;

        for (int int6 = 0; int6 < this.m_animBlendIndices.length && this.m_animBlendIndices[int6] >= 0; int6++) {
            int int7 = this.m_animBlendLayers[int6];
            this.m_layerWeightTotals[int7] = this.m_layerWeightTotals[int7] + this.m_animBlendWeights[int6];
            this.m_layerBlendCounts[int7]++;
            this.m_totalAnimBlendCount++;
        }

        if (this.m_totalAnimBlendCount != 0) {
            if (this.m_boneTransformsNeedFirstFrame) {
                int int8 = this.m_animBlendLayers[0];
                int int9 = this.m_layerBlendCounts[0];
                float float1 = this.m_layerWeightTotals[0];
                if (float1 < 1.0F) {
                    for (int int10 = 0; int10 < this.m_totalAnimBlendCount; int10++) {
                        int int11 = this.m_animBlendLayers[int10];
                        if (int11 != int8) {
                            break;
                        }

                        if (float1 > 0.0F) {
                            this.m_animBlendWeights[int10] = this.m_animBlendWeights[int10] / float1;
                        } else {
                            this.m_animBlendWeights[int10] = 1.0F / int9;
                        }
                    }
                }
            }
        }
    }

    private void calculateDeferredMovement() {
        GameProfiler.getInstance().invokeAndMeasure("calculateDeferredMovement", this, AnimationPlayer::calculateDeferredMovementInternal);
    }

    private void calculateDeferredMovementInternal() {
        List list = this.m_multiTrack.getTracks();
        this.m_deferredMovement.set(0.0F, 0.0F);
        this.m_deferredAngleDelta = 0.0F;
        this.m_deferredRotationWeight = 0.0F;
        float float0 = 1.0F;

        for (int int0 = this.m_totalAnimBlendCount - 1; int0 >= 0 && !(float0 <= 0.001F); int0--) {
            int int1 = this.m_animBlendIndices[int0];
            AnimationTrack animationTrack = (AnimationTrack)list.get(int1);
            if (!animationTrack.isFinished()) {
                float float1 = animationTrack.getDeferredBoneWeight();
                if (!(float1 <= 0.001F)) {
                    float float2 = this.m_animBlendWeights[int0] * float1;
                    if (!(float2 <= 0.001F)) {
                        float float3 = PZMath.clamp(float2, 0.0F, float0);
                        float0 -= float2;
                        float0 = org.joml.Math.max(0.0F, float0);
                        Vector2.addScaled(this.m_deferredMovement, animationTrack.getDeferredMovementDiff(tempo), float3, this.m_deferredMovement);
                        if (animationTrack.getUseDeferredRotation()) {
                            this.m_deferredAngleDelta = this.m_deferredAngleDelta + animationTrack.getDeferredRotationDiff() * float3;
                            this.m_deferredRotationWeight += float3;
                        }
                    }
                }
            }
        }

        this.applyRotationToDeferredMovement(this.m_deferredMovement);
        this.m_deferredMovement.x = this.m_deferredMovement.x * AdvancedAnimator.s_MotionScale;
        this.m_deferredMovement.y = this.m_deferredMovement.y * AdvancedAnimator.s_MotionScale;
        this.m_deferredAngleDelta = this.m_deferredAngleDelta * AdvancedAnimator.s_RotationScale;
    }

    private void applyRotationToDeferredMovement(Vector2 vector) {
        float float0 = vector.normalize();
        float float1 = this.getRenderedAngle();
        vector.rotate(float1);
        vector.setLength(-float0);
    }

    private void applyBoneReParenting() {
        GameProfiler.getInstance().invokeAndMeasure("applyBoneReParenting", this, AnimationPlayer::applyBoneReParentingInternal);
    }

    private void applyBoneReParentingInternal() {
        int int0 = 0;

        for (int int1 = this.m_reparentedBoneBindings.size(); int0 < int1; int0++) {
            AnimationBoneBindingPair animationBoneBindingPair = this.m_reparentedBoneBindings.get(int0);
            if (!animationBoneBindingPair.isValid()) {
                DebugLog.Animation.warn("Animation binding pair is not valid: %s", animationBoneBindingPair);
            } else {
                this.updateBoneAnimationTransform(animationBoneBindingPair.getBoneIdxA(), animationBoneBindingPair);
            }
        }
    }

    private void updateBoneAnimationTransform(int int0, AnimationBoneBindingPair animationBoneBindingPair) {
        this.updateBoneAnimationTransform_Internal(int0, animationBoneBindingPair);
    }

    private void updateBoneAnimationTransform_Internal(int int1, AnimationBoneBindingPair animationBoneBindingPair) {
        List list = this.m_multiTrack.getTracks();
        Vector3f vector3f0 = AnimationPlayer.L_updateBoneAnimationTransform.pos;
        Quaternion quaternion = AnimationPlayer.L_updateBoneAnimationTransform.rot;
        Vector3f vector3f1 = AnimationPlayer.L_updateBoneAnimationTransform.scale;
        Keyframe keyframe = AnimationPlayer.L_updateBoneAnimationTransform.key;
        int int0 = this.m_totalAnimBlendCount;
        AnimationBoneBinding animationBoneBinding = this.m_counterRotationBone;
        boolean boolean0 = animationBoneBinding != null && animationBoneBinding.getBone() != null && animationBoneBinding.getBone().Index == int1;
        keyframe.setIdentity();
        float float0 = 0.0F;
        boolean boolean1 = true;
        float float1 = 1.0F;

        for (int int2 = int0 - 1; int2 >= 0 && float1 > 0.0F && !(float1 <= 0.001F); int2--) {
            int int3 = this.m_animBlendIndices[int2];
            AnimationTrack animationTrack = (AnimationTrack)list.get(int3);
            float float2 = animationTrack.getBoneWeight(int1);
            if (!(float2 <= 0.001F)) {
                float float3 = this.m_animBlendWeights[int2] * float2;
                if (!(float3 <= 0.001F)) {
                    float float4 = PZMath.clamp(float3, 0.0F, float1);
                    float1 -= float3;
                    float1 = org.joml.Math.max(0.0F, float1);
                    this.getTrackTransform(int1, animationTrack, animationBoneBindingPair, vector3f0, quaternion, vector3f1);
                    if (boolean0 && animationTrack.getUseDeferredRotation()) {
                        if (DebugOptions.instance.Character.Debug.Animate.ZeroCounterRotationBone.getValue()) {
                            Vector3f vector3f2 = AnimationPlayer.L_updateBoneAnimationTransform.rotAxis;
                            Matrix4f matrix4f = AnimationPlayer.L_updateBoneAnimationTransform.rotMat;
                            matrix4f.setIdentity();
                            vector3f2.set(0.0F, 1.0F, 0.0F);
                            matrix4f.rotate((float) (-Math.PI / 2), vector3f2);
                            vector3f2.set(1.0F, 0.0F, 0.0F);
                            matrix4f.rotate((float) (-Math.PI / 2), vector3f2);
                            HelperFunctions.getRotation(matrix4f, quaternion);
                        } else {
                            Vector3f vector3f3 = HelperFunctions.ToEulerAngles(quaternion, AnimationPlayer.L_updateBoneAnimationTransform.rotEulers);
                            HelperFunctions.ToQuaternion(vector3f3.x, vector3f3.y, (float) (Math.PI / 2), quaternion);
                        }
                    }

                    boolean boolean2 = animationTrack.getDeferredMovementBoneIdx() == int1;
                    if (boolean2) {
                        Vector3f vector3f4 = animationTrack.getCurrentDeferredCounterPosition(AnimationPlayer.L_updateBoneAnimationTransform.deferredPos);
                        vector3f0.x = vector3f0.x + vector3f4.x;
                        vector3f0.y = vector3f0.y + vector3f4.y;
                        vector3f0.z = vector3f0.z + vector3f4.z;
                    }

                    if (boolean1) {
                        Vector3.setScaled(vector3f0, float4, keyframe.Position);
                        keyframe.Rotation.set(quaternion);
                        float0 = float4;
                        boolean1 = false;
                    } else {
                        float float5 = float4 / (float4 + float0);
                        float0 += float4;
                        Vector3.addScaled(keyframe.Position, vector3f0, float4, keyframe.Position);
                        PZMath.slerp(keyframe.Rotation, keyframe.Rotation, quaternion, float5);
                    }
                }
            }
        }

        if (float1 > 0.0F && !this.m_boneTransformsNeedFirstFrame) {
            this.m_boneTransforms[int1].getPRS(vector3f0, quaternion, vector3f1);
            Vector3.addScaled(keyframe.Position, vector3f0, float1, keyframe.Position);
            PZMath.slerp(keyframe.Rotation, quaternion, keyframe.Rotation, float0);
            PZMath.lerp(keyframe.Scale, vector3f1, keyframe.Scale, float0);
        }

        this.m_boneTransforms[int1].set(keyframe.Position, keyframe.Rotation, keyframe.Scale);
        this.m_boneTransforms[int1].BlendWeight = float0;
        this.m_boneTransforms[int1].Twist *= 1.0F - float0;
    }

    private void getTrackTransform(
        int int0,
        AnimationTrack animationTrack,
        AnimationBoneBindingPair animationBoneBindingPair,
        Vector3f vector3f0,
        Quaternion quaternion,
        Vector3f vector3f1
    ) {
        if (animationBoneBindingPair == null) {
            animationTrack.get(int0, vector3f0, quaternion, vector3f1);
        } else {
            Matrix4f matrix4f0 = AnimationPlayer.L_getTrackTransform.result;
            SkinningBone skinningBone0 = animationBoneBindingPair.getBoneA();
            Matrix4f matrix4f1 = getUnweightedBoneTransform(animationTrack, skinningBone0.Index, AnimationPlayer.L_getTrackTransform.Pa);
            SkinningBone skinningBone1 = skinningBone0.Parent;
            SkinningBone skinningBone2 = animationBoneBindingPair.getBoneB();
            Matrix4f matrix4f2 = this.getBoneModelTransform(skinningBone1.Index, AnimationPlayer.L_getTrackTransform.mA);
            Matrix4f matrix4f3 = Matrix4f.invert(matrix4f2, AnimationPlayer.L_getTrackTransform.mAinv);
            Matrix4f matrix4f4 = this.getBoneModelTransform(skinningBone2.Index, AnimationPlayer.L_getTrackTransform.mB);
            Matrix4f matrix4f5 = this.getUnweightedModelTransform(animationTrack, skinningBone1.Index, AnimationPlayer.L_getTrackTransform.umA);
            Matrix4f matrix4f6 = this.getUnweightedModelTransform(animationTrack, skinningBone2.Index, AnimationPlayer.L_getTrackTransform.umB);
            Matrix4f matrix4f7 = Matrix4f.invert(matrix4f6, AnimationPlayer.L_getTrackTransform.umBinv);
            Matrix4f.mul(matrix4f1, matrix4f5, matrix4f0);
            Matrix4f.mul(matrix4f0, matrix4f7, matrix4f0);
            Matrix4f.mul(matrix4f0, matrix4f4, matrix4f0);
            Matrix4f.mul(matrix4f0, matrix4f3, matrix4f0);
            HelperFunctions.getPosition(matrix4f0, vector3f0);
            HelperFunctions.getRotation(matrix4f0, quaternion);
            vector3f1.set(1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isBoneReparented(int boneIdx) {
        return PZArrayUtil.contains(
            this.m_reparentedBoneBindings, Lambda.predicate(boneIdx, (animationBoneBindingPair, integer) -> animationBoneBindingPair.getBoneIdxA() == integer)
        );
    }

    public void updateMultiTrackBoneTransforms_DeferredMovementOnly() {
        this.m_deferredMovement.set(0.0F, 0.0F);
        if (this.parentPlayer == null) {
            this.updateLayerBlendWeightings();
            if (this.m_totalAnimBlendCount != 0) {
                int[] ints = AnimationPlayer.updateMultiTrackBoneTransforms_DeferredMovementOnly.boneIndices;
                int int0 = 0;
                List list = this.m_multiTrack.getTracks();
                int int1 = list.size();

                for (int int2 = 0; int2 < int1; int2++) {
                    AnimationTrack animationTrack = (AnimationTrack)list.get(int2);
                    int int3 = animationTrack.getDeferredMovementBoneIdx();
                    if (int3 != -1 && !PZArrayUtil.contains(ints, int0, int3)) {
                        ints[int0++] = int3;
                    }
                }

                for (int int4 = 0; int4 < int0; int4++) {
                    this.updateBoneAnimationTransform(ints[int4], null);
                }
            }
        }
    }

    public boolean isRecording() {
        return this.m_recorder != null && this.m_recorder.isRecording();
    }

    public void setRecorder(AnimationPlayerRecorder recorder) {
        this.m_recorder = recorder;
    }

    public AnimationPlayerRecorder getRecorder() {
        return this.m_recorder;
    }

    public void dismember(int bone) {
        this.dismembered.add(bone);
    }

    private void updateModelTransforms() {
        GameProfiler.getInstance().invokeAndMeasure("updateModelTransforms", this, AnimationPlayer::updateModelTransformsInternal);
    }

    private void updateModelTransformsInternal() {
        this.m_boneTransforms[0].getMatrix(this.modelTransforms[0]);

        for (int int0 = 1; int0 < this.modelTransforms.length; int0++) {
            SkinningBone skinningBone0 = this.m_skinningData.getBoneAt(int0);
            SkinningBone skinningBone1 = skinningBone0.Parent;
            BoneTransform.mul(this.m_boneTransforms[skinningBone0.Index], this.modelTransforms[skinningBone1.Index], this.modelTransforms[skinningBone0.Index]);
        }
    }

    /**
     * Get the bone's transform, in the model space.   That is, relative to the model's origin.
     */
    public Matrix4f getBoneModelTransform(int boneIdx, Matrix4f out_modelTransform) {
        Matrix4f matrix4f = AnimationPlayer.L_getBoneModelTransform.boneTransform;
        out_modelTransform.setIdentity();
        SkinningBone skinningBone0 = this.m_skinningData.getBoneAt(boneIdx);

        for (SkinningBone skinningBone1 = skinningBone0; skinningBone1 != null; skinningBone1 = skinningBone1.Parent) {
            this.getBoneTransform(skinningBone1.Index, matrix4f);
            Matrix4f.mul(out_modelTransform, matrix4f, out_modelTransform);
        }

        return out_modelTransform;
    }

    /**
     * Get the bone's transform, in its local space.   That is, relative to its parent bone.
     */
    public Matrix4f getBoneTransform(int boneIdx, Matrix4f out_boneTransform) {
        this.m_boneTransforms[boneIdx].getMatrix(out_boneTransform);
        return out_boneTransform;
    }

    public Matrix4f getUnweightedModelTransform(AnimationTrack track, int boneIdx, Matrix4f out_modelTransform) {
        Matrix4f matrix4f = AnimationPlayer.L_getUnweightedModelTransform.boneTransform;
        matrix4f.setIdentity();
        out_modelTransform.setIdentity();
        SkinningBone skinningBone0 = this.m_skinningData.getBoneAt(boneIdx);

        for (SkinningBone skinningBone1 = skinningBone0; skinningBone1 != null; skinningBone1 = skinningBone1.Parent) {
            getUnweightedBoneTransform(track, skinningBone1.Index, matrix4f);
            Matrix4f.mul(out_modelTransform, matrix4f, out_modelTransform);
        }

        return out_modelTransform;
    }

    public static Matrix4f getUnweightedBoneTransform(AnimationTrack track, int boneIdx, Matrix4f out_boneTransform) {
        Vector3f vector3f0 = AnimationPlayer.L_getUnweightedBoneTransform.pos;
        Quaternion quaternion = AnimationPlayer.L_getUnweightedBoneTransform.rot;
        Vector3f vector3f1 = AnimationPlayer.L_getUnweightedBoneTransform.scale;
        track.get(boneIdx, vector3f0, quaternion, vector3f1);
        HelperFunctions.CreateFromQuaternionPositionScale(vector3f0, quaternion, vector3f1, out_boneTransform);
        return out_boneTransform;
    }

    public void UpdateSkinTransforms() {
        this.resetSkinTransforms();
    }

    public Matrix4f[] getSkinTransforms(SkinningData skinningData) {
        if (skinningData == null) {
            return this.modelTransforms;
        } else {
            AnimationPlayer.SkinTransformData skinTransformData = this.getSkinTransformData(skinningData);
            Matrix4f[] matrix4fs = skinTransformData.transforms;
            if (skinTransformData.dirty) {
                for (int int0 = 0; int0 < this.modelTransforms.length; int0++) {
                    if (skinningData.BoneOffset != null && skinningData.BoneOffset.get(int0) != null) {
                        Matrix4f.mul(skinningData.BoneOffset.get(int0), this.modelTransforms[int0], matrix4fs[int0]);
                    } else {
                        matrix4fs[int0].setIdentity();
                    }
                }

                skinTransformData.dirty = false;
            }

            return matrix4fs;
        }
    }

    public void getDeferredMovement(Vector2 out_result) {
        out_result.set(this.m_deferredMovement);
    }

    public float getDeferredAngleDelta() {
        return this.m_deferredAngleDelta;
    }

    public float getDeferredRotationWeight() {
        return this.m_deferredRotationWeight;
    }

    public AnimationMultiTrack getMultiTrack() {
        return this.m_multiTrack;
    }

    public void setRecording(boolean val) {
        this.m_recorder.setRecording(val);
    }

    public void discardRecording() {
        if (this.m_recorder != null) {
            this.m_recorder.discardRecording();
        }
    }

    public float getRenderedAngle() {
        return this.m_angle + (float) (Math.PI / 2);
    }

    public float getAngle() {
        return this.m_angle;
    }

    public void setAngle(float angle) {
        this.m_angle = angle;
    }

    public void setAngleToTarget() {
        this.setAngle(this.getTargetAngle());
    }

    public void setTargetToAngle() {
        float float0 = this.getAngle();
        this.setTargetAngle(float0);
    }

    public float getTargetAngle() {
        return this.m_targetAngle;
    }

    public void setTargetAngle(float targetAngle) {
        this.m_targetAngle = targetAngle;
    }

    /**
     * Returns the maximum twist angle, in radians.
     */
    public float getMaxTwistAngle() {
        return this.m_maxTwistAngle;
    }

    /**
     * Set the maximum twist angle, in radians
     */
    public void setMaxTwistAngle(float radians) {
        this.m_maxTwistAngle = radians;
    }

    public float getExcessTwistAngle() {
        return this.m_excessTwist;
    }

    public float getTwistAngle() {
        return this.m_twistAngle;
    }

    public float getShoulderTwistAngle() {
        return this.m_shoulderTwistAngle;
    }

    /**
     * The lookAt bearing, in radians. The difference between angle and targetAngle.   The twist target, not clamped at all.   All twists aim for this target, and are clamped by maxTwist.
     */
    public float getTargetTwistAngle() {
        return this.m_targetTwistAngle;
    }

    private static class L_applyTwistBone {
        static final Matrix4f twistParentBoneTrans = new Matrix4f();
        static final Matrix4f twistParentBoneTransInv = new Matrix4f();
        static final Matrix4f twistBoneTrans = new Matrix4f();
        static final Quaternion twistBoneRot = new Quaternion();
        static final Quaternion twistBoneTargetRot = new Quaternion();
        static final Matrix4f twistRotDiffTrans = new Matrix4f();
        static final Vector3f twistRotDiffTransAxis = new Vector3f(0.0F, 1.0F, 0.0F);
        static final Matrix4f twistBoneTargetTrans = new Matrix4f();
        static final Quaternion twistBoneNewRot = new Quaternion();
        static final Vector3f twistBonePos = new Vector3f();
        static final Vector3f twistBoneScale = new Vector3f();
        static final Matrix4f twistBoneNewTrans = new Matrix4f();
    }

    private static class L_getBoneModelTransform {
        static final Matrix4f boneTransform = new Matrix4f();
    }

    private static final class L_getTrackTransform {
        static final Matrix4f Pa = new Matrix4f();
        static final Matrix4f mA = new Matrix4f();
        static final Matrix4f mB = new Matrix4f();
        static final Matrix4f umA = new Matrix4f();
        static final Matrix4f umB = new Matrix4f();
        static final Matrix4f mAinv = new Matrix4f();
        static final Matrix4f umBinv = new Matrix4f();
        static final Matrix4f result = new Matrix4f();
    }

    private static class L_getUnweightedBoneTransform {
        static final Vector3f pos = new Vector3f();
        static final Quaternion rot = new Quaternion();
        static final Vector3f scale = new Vector3f();
    }

    private static class L_getUnweightedModelTransform {
        static final Matrix4f boneTransform = new Matrix4f();
    }

    private static final class L_setTwistBones {
        static final ArrayList<String> boneNames = new ArrayList<>();
    }

    private static final class L_updateBoneAnimationTransform {
        static final Quaternion rot = new Quaternion();
        static final Vector3f pos = new Vector3f();
        static final Vector3f scale = new Vector3f();
        static final Keyframe key = new Keyframe(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F));
        static final Matrix4f boneMat = new Matrix4f();
        static final Matrix4f rotMat = new Matrix4f();
        static final Vector3f rotAxis = new Vector3f(1.0F, 0.0F, 0.0F);
        static final Quaternion crRot = new Quaternion();
        static final Vector4f crRotAA = new Vector4f();
        static final Matrix4f crMat = new Matrix4f();
        static final Vector3f rotEulers = new Vector3f();
        static final Vector3f deferredPos = new Vector3f();
    }

    private static class SkinTransformData extends PooledObject {
        public Matrix4f[] transforms;
        private SkinningData m_skinnedTo;
        public boolean dirty;
        private AnimationPlayer.SkinTransformData m_next;
        private static Pool<AnimationPlayer.SkinTransformData> s_pool = new Pool<>(AnimationPlayer.SkinTransformData::new);

        public void setSkinnedTo(SkinningData arg0) {
            if (this.m_skinnedTo != arg0) {
                this.dirty = true;
                this.m_skinnedTo = arg0;
                this.transforms = PZArrayUtil.newInstance(Matrix4f.class, this.transforms, arg0.numBones(), Matrix4f::new);
            }
        }

        public static AnimationPlayer.SkinTransformData alloc(SkinningData arg0) {
            AnimationPlayer.SkinTransformData skinTransformData = s_pool.alloc();
            skinTransformData.setSkinnedTo(arg0);
            skinTransformData.dirty = true;
            return skinTransformData;
        }
    }

    private static final class updateMultiTrackBoneTransforms_DeferredMovementOnly {
        static int[] boneIndices = new int[60];
    }
}
