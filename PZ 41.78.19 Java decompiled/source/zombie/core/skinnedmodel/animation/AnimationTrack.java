// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.advancedanimation.PooledAnimBoneWeightArray;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledArrayObject;
import zombie.util.PooledFloatArrayObject;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.lambda.Consumers;
import zombie.util.list.PZArrayUtil;

/**
 * Created by LEMMYPC on 07/01/14.
 */
public final class AnimationTrack extends PooledObject {
    public boolean IsPlaying;
    protected AnimationClip CurrentClip;
    public int priority;
    private float currentTimeValue;
    private float previousTimeValue;
    public boolean SyncTrackingEnabled;
    public boolean reverse;
    private boolean bLooping;
    private final AnimationTrack.KeyframeSpan[] m_pose = new AnimationTrack.KeyframeSpan[60];
    private final AnimationTrack.KeyframeSpan m_deferredPoseSpan = new AnimationTrack.KeyframeSpan();
    public float SpeedDelta;
    public float BlendDelta;
    public float blendFieldWeight;
    public String name;
    public float earlyBlendOutTime;
    public boolean triggerOnNonLoopedAnimFadeOutEvent;
    private int m_layerIdx;
    private PooledArrayObject<AnimBoneWeight> m_boneWeightBindings;
    private PooledFloatArrayObject m_boneWeights;
    private final ArrayList<IAnimListener> listeners = new ArrayList<>();
    private final ArrayList<IAnimListener> listenersInvoking = new ArrayList<>();
    private SkinningBone m_deferredBone;
    private BoneAxis m_deferredBoneAxis;
    private boolean m_useDeferredRotation;
    private final AnimationTrack.DeferredMotionData m_deferredMotion = new AnimationTrack.DeferredMotionData();
    private static final Pool<AnimationTrack> s_pool = new Pool<>(AnimationTrack::new);

    public static AnimationTrack alloc() {
        return s_pool.alloc();
    }

    protected AnimationTrack() {
        PZArrayUtil.arrayPopulate(this.m_pose, AnimationTrack.KeyframeSpan::new);
        this.resetInternal();
    }

    private AnimationTrack resetInternal() {
        this.IsPlaying = false;
        this.CurrentClip = null;
        this.priority = 0;
        this.currentTimeValue = 0.0F;
        this.previousTimeValue = 0.0F;
        this.SyncTrackingEnabled = true;
        this.reverse = false;
        this.bLooping = false;
        PZArrayUtil.forEach(this.m_pose, AnimationTrack.KeyframeSpan::clear);
        this.m_deferredPoseSpan.clear();
        this.SpeedDelta = 1.0F;
        this.BlendDelta = 0.0F;
        this.blendFieldWeight = 0.0F;
        this.name = "!Empty!";
        this.earlyBlendOutTime = 0.0F;
        this.triggerOnNonLoopedAnimFadeOutEvent = false;
        this.m_layerIdx = -1;
        Pool.tryRelease(this.m_boneWeightBindings);
        this.m_boneWeightBindings = null;
        Pool.tryRelease(this.m_boneWeights);
        this.m_boneWeights = null;
        this.listeners.clear();
        this.listenersInvoking.clear();
        this.m_deferredBone = null;
        this.m_deferredBoneAxis = BoneAxis.Y;
        this.m_useDeferredRotation = false;
        this.m_deferredMotion.reset();
        return this;
    }

    public void get(int bone, Vector3f out_pos, Quaternion out_rot, Vector3f out_scale) {
        this.m_pose[bone].lerp(this.getCurrentTime(), out_pos, out_rot, out_scale);
    }

    private Keyframe getDeferredMovementFrameAt(int int0, float float0, Keyframe keyframe) {
        AnimationTrack.KeyframeSpan keyframeSpan = this.getKeyframeSpan(int0, float0, this.m_deferredPoseSpan);
        return keyframeSpan.lerp(float0, keyframe);
    }

    private AnimationTrack.KeyframeSpan getKeyframeSpan(int int0, float float0, AnimationTrack.KeyframeSpan keyframeSpan) {
        if (!keyframeSpan.isBone(int0)) {
            keyframeSpan.clear();
        }

        Keyframe[] keyframes = this.CurrentClip.getBoneFramesAt(int0);
        if (keyframes.length == 0) {
            keyframeSpan.clear();
            return keyframeSpan;
        } else if (keyframeSpan.containsTime(float0)) {
            return keyframeSpan;
        } else {
            Keyframe keyframe0 = keyframes[keyframes.length - 1];
            if (float0 >= keyframe0.Time) {
                keyframeSpan.fromIdx = keyframes.length - 2;
                keyframeSpan.toIdx = keyframes.length - 1;
                keyframeSpan.from = keyframes[keyframeSpan.fromIdx];
                keyframeSpan.to = keyframes[keyframeSpan.toIdx];
                return keyframeSpan;
            } else {
                Keyframe keyframe1 = keyframes[0];
                if (float0 <= keyframe1.Time) {
                    keyframeSpan.clear();
                    keyframeSpan.toIdx = 0;
                    keyframeSpan.to = keyframe1;
                    return keyframeSpan;
                } else {
                    int int1 = 0;
                    if (keyframeSpan.isSpan() && keyframeSpan.to.Time <= float0) {
                        int1 = keyframeSpan.toIdx;
                    }

                    keyframeSpan.clear();

                    for (int int2 = int1; int2 < keyframes.length - 1; int2++) {
                        Keyframe keyframe2 = keyframes[int2];
                        Keyframe keyframe3 = keyframes[int2 + 1];
                        if (keyframe2.Time <= float0 && float0 <= keyframe3.Time) {
                            keyframeSpan.fromIdx = int2;
                            keyframeSpan.toIdx = int2 + 1;
                            keyframeSpan.from = keyframe2;
                            keyframeSpan.to = keyframe3;
                            break;
                        }
                    }

                    return keyframeSpan;
                }
            }
        }
    }

    public void removeListener(IAnimListener listener) {
        this.listeners.remove(listener);
    }

    public void Update(float time) {
        try {
            this.UpdateKeyframes(time);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void UpdateKeyframes(float dt) {
        AnimationTrack.s_performance.updateKeyframes.invokeAndMeasure(this, dt, AnimationTrack::updateKeyframesInternal);
    }

    private void updateKeyframesInternal(float float0) {
        if (this.CurrentClip == null) {
            throw new RuntimeException("AnimationPlayer.Update was called before startClip");
        } else {
            if (float0 > 0.0F) {
                this.TickCurrentTime((float)float0);
            }

            if (!GameServer.bServer || ServerGUI.isCreated()) {
                this.updatePose();
            }

            this.updateDeferredValues();
        }
    }

    private void updatePose() {
        AnimationTrack.s_performance.updatePose.invokeAndMeasure(this, AnimationTrack::updatePoseInternal);
    }

    private void updatePoseInternal() {
        float float0 = this.getCurrentTime();

        for (int int0 = 0; int0 < 60; int0++) {
            this.getKeyframeSpan(int0, float0, this.m_pose[int0]);
        }
    }

    private void updateDeferredValues() {
        AnimationTrack.s_performance.updateDeferredValues.invokeAndMeasure(this, AnimationTrack::updateDeferredValuesInternal);
    }

    private void updateDeferredValuesInternal() {
        if (this.m_deferredBone != null) {
            AnimationTrack.DeferredMotionData deferredMotionData = this.m_deferredMotion;
            deferredMotionData.m_deferredRotationDiff = 0.0F;
            deferredMotionData.m_deferredMovementDiff.set(0.0F, 0.0F);
            deferredMotionData.m_counterRotatedMovementDiff.set(0.0F, 0.0F);
            float float0 = this.getReversibleTimeValue(this.previousTimeValue);
            float float1 = this.getReversibleTimeValue(this.currentTimeValue);
            if (this.isLooping() && float0 > float1) {
                float float2 = this.getDuration();
                this.appendDeferredValues(deferredMotionData, float0, float2);
                float0 = 0.0F;
            }

            this.appendDeferredValues(deferredMotionData, float0, float1);
        }
    }

    private void appendDeferredValues(AnimationTrack.DeferredMotionData deferredMotionData, float float0, float float1) {
        Keyframe keyframe0 = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, float0, AnimationTrack.L_updateDeferredValues.prevKeyFrame);
        Keyframe keyframe1 = this.getDeferredMovementFrameAt(this.m_deferredBone.Index, float1, AnimationTrack.L_updateDeferredValues.keyFrame);
        if (!GameServer.bServer) {
            deferredMotionData.m_prevDeferredRotation = this.getDeferredTwistRotation(keyframe0.Rotation);
            deferredMotionData.m_targetDeferredRotationQ.set(keyframe1.Rotation);
            deferredMotionData.m_targetDeferredRotation = this.getDeferredTwistRotation(keyframe1.Rotation);
            float float2 = PZMath.getClosestAngle(deferredMotionData.m_prevDeferredRotation, deferredMotionData.m_targetDeferredRotation);
            deferredMotionData.m_deferredRotationDiff += float2;
        }

        this.getDeferredMovement(keyframe0.Position, deferredMotionData.m_prevDeferredMovement);
        deferredMotionData.m_targetDeferredPosition.set(keyframe1.Position);
        this.getDeferredMovement(keyframe1.Position, deferredMotionData.m_targetDeferredMovement);
        Vector2 vector0 = AnimationTrack.L_updateDeferredValues.diff
            .set(
                deferredMotionData.m_targetDeferredMovement.x - deferredMotionData.m_prevDeferredMovement.x,
                deferredMotionData.m_targetDeferredMovement.y - deferredMotionData.m_prevDeferredMovement.y
            );
        Vector2 vector1 = AnimationTrack.L_updateDeferredValues.crDiff.set(vector0);
        if (this.getUseDeferredRotation()) {
            float float3 = vector1.normalize();
            vector1.rotate(-(deferredMotionData.m_targetDeferredRotation + (float) (Math.PI / 2)));
            vector1.scale(-float3);
        }

        deferredMotionData.m_deferredMovementDiff.x = deferredMotionData.m_deferredMovementDiff.x + vector0.x;
        deferredMotionData.m_deferredMovementDiff.y = deferredMotionData.m_deferredMovementDiff.y + vector0.y;
        deferredMotionData.m_counterRotatedMovementDiff.x = deferredMotionData.m_counterRotatedMovementDiff.x + vector1.x;
        deferredMotionData.m_counterRotatedMovementDiff.y = deferredMotionData.m_counterRotatedMovementDiff.y + vector1.y;
    }

    public float getDeferredTwistRotation(Quaternion boneRotation) {
        if (this.m_deferredBoneAxis == BoneAxis.Z) {
            return HelperFunctions.getRotationZ(boneRotation);
        } else if (this.m_deferredBoneAxis == BoneAxis.Y) {
            return HelperFunctions.getRotationY(boneRotation);
        } else {
            DebugLog.Animation.error("BoneAxis unhandled: %s", String.valueOf(this.m_deferredBoneAxis));
            return 0.0F;
        }
    }

    public Vector2 getDeferredMovement(Vector3f bonePos, Vector2 out_deferredPos) {
        if (this.m_deferredBoneAxis == BoneAxis.Y) {
            out_deferredPos.set(bonePos.x, -bonePos.z);
        } else {
            out_deferredPos.set(bonePos.x, bonePos.y);
        }

        return out_deferredPos;
    }

    public Vector3f getCurrentDeferredCounterPosition(Vector3f out_result) {
        this.getCurrentDeferredPosition(out_result);
        if (this.m_deferredBoneAxis == BoneAxis.Y) {
            out_result.set(-out_result.x, 0.0F, out_result.z);
        } else {
            out_result.set(-out_result.x, -out_result.y, 0.0F);
        }

        return out_result;
    }

    public float getCurrentDeferredRotation() {
        return this.m_deferredMotion.m_targetDeferredRotation;
    }

    public Vector3f getCurrentDeferredPosition(Vector3f out_result) {
        out_result.set(this.m_deferredMotion.m_targetDeferredPosition);
        return out_result;
    }

    public int getDeferredMovementBoneIdx() {
        return this.m_deferredBone != null ? this.m_deferredBone.Index : -1;
    }

    public float getCurrentTime() {
        return this.getReversibleTimeValue(this.currentTimeValue);
    }

    public float getPreviousTime() {
        return this.getReversibleTimeValue(this.previousTimeValue);
    }

    private float getReversibleTimeValue(float float0) {
        return this.reverse ? this.getDuration() - float0 : float0;
    }

    protected void TickCurrentTime(float float0) {
        AnimationTrack.s_performance.tickCurrentTime.invokeAndMeasure(this, float0, AnimationTrack::tickCurrentTimeInternal);
    }

    private void tickCurrentTimeInternal(float float0) {
        float0 *= this.SpeedDelta;
        if (!this.IsPlaying) {
            float0 = 0.0F;
        }

        float float1 = this.getDuration();
        this.previousTimeValue = this.currentTimeValue;
        this.currentTimeValue += float0;
        if (this.bLooping) {
            if (this.previousTimeValue == 0.0F && this.currentTimeValue > 0.0F) {
                this.invokeOnAnimStartedEvent();
            }

            if (this.currentTimeValue >= float1) {
                this.invokeOnLoopedAnimEvent();
                this.currentTimeValue %= float1;
                this.invokeOnAnimStartedEvent();
            }
        } else {
            if (this.currentTimeValue < 0.0F) {
                this.currentTimeValue = 0.0F;
            }

            if (this.previousTimeValue == 0.0F && this.currentTimeValue > 0.0F) {
                this.invokeOnAnimStartedEvent();
            }

            if (this.triggerOnNonLoopedAnimFadeOutEvent) {
                float float2 = float1 - this.earlyBlendOutTime;
                if (this.previousTimeValue < float2 && float2 <= this.currentTimeValue) {
                    this.invokeOnNonLoopedAnimFadeOutEvent();
                }
            }

            if (this.currentTimeValue > float1) {
                this.currentTimeValue = float1;
            }

            if (this.previousTimeValue < float1 && this.currentTimeValue >= float1) {
                this.invokeOnLoopedAnimEvent();
                this.invokeOnNonLoopedAnimFinishedEvent();
            }
        }
    }

    public float getDuration() {
        return this.hasClip() ? this.CurrentClip.Duration : 0.0F;
    }

    private void invokeListeners(Consumer<IAnimListener> consumer) {
        if (!this.listeners.isEmpty()) {
            this.listenersInvoking.clear();
            this.listenersInvoking.addAll(this.listeners);

            for (int int0 = 0; int0 < this.listenersInvoking.size(); int0++) {
                IAnimListener iAnimListener = this.listenersInvoking.get(int0);
                consumer.accept(iAnimListener);
            }
        }
    }

    private <T1> void invokeListeners(T1 object, Consumers.Params1.ICallback<IAnimListener, T1> iCallback) {
        Lambda.capture(
            this,
            object,
            iCallback,
            (genericStack, animationTrack, objectx, iCallbackx) -> animationTrack.invokeListeners(genericStack.consumer(objectx, iCallbackx))
        );
    }

    protected void invokeOnAnimStartedEvent() {
        this.invokeListeners(this, IAnimListener::onAnimStarted);
    }

    protected void invokeOnLoopedAnimEvent() {
        this.invokeListeners(this, IAnimListener::onLoopedAnim);
    }

    protected void invokeOnNonLoopedAnimFadeOutEvent() {
        this.invokeListeners(this, IAnimListener::onNonLoopedAnimFadeOut);
    }

    protected void invokeOnNonLoopedAnimFinishedEvent() {
        this.invokeListeners(this, IAnimListener::onNonLoopedAnimFinished);
    }

    /**
     * onDestroyed  Called by AnimationPlayer's ObjectPool, when this track has been released.
     */
    @Override
    public void onReleased() {
        if (!this.listeners.isEmpty()) {
            this.listenersInvoking.clear();
            this.listenersInvoking.addAll(this.listeners);

            for (int int0 = 0; int0 < this.listenersInvoking.size(); int0++) {
                IAnimListener iAnimListener = this.listenersInvoking.get(int0);
                iAnimListener.onTrackDestroyed(this);
            }

            this.listeners.clear();
            this.listenersInvoking.clear();
        }

        this.reset();
    }

    public Vector2 getDeferredMovementDiff(Vector2 out_result) {
        out_result.set(this.m_deferredMotion.m_counterRotatedMovementDiff);
        return out_result;
    }

    public float getDeferredRotationDiff() {
        return this.m_deferredMotion.m_deferredRotationDiff;
    }

    public float getClampedBlendDelta() {
        return PZMath.clamp(this.BlendDelta, 0.0F, 1.0F);
    }

    public void addListener(IAnimListener listener) {
        this.listeners.add(listener);
    }

    public void startClip(AnimationClip clip, boolean loop) {
        if (clip == null) {
            throw new NullPointerException("Supplied clip is null.");
        } else {
            this.reset();
            this.IsPlaying = true;
            this.bLooping = loop;
            this.CurrentClip = clip;
        }
    }

    public AnimationTrack reset() {
        return this.resetInternal();
    }

    public void setBoneWeights(List<AnimBoneWeight> boneWeights) {
        this.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(boneWeights);
        this.m_boneWeights = null;
    }

    public void initBoneWeights(SkinningData skinningData) {
        if (!this.hasBoneMask()) {
            if (this.m_boneWeightBindings != null) {
                if (this.m_boneWeightBindings.isEmpty()) {
                    this.m_boneWeights = PooledFloatArrayObject.alloc(0);
                } else {
                    this.m_boneWeights = PooledFloatArrayObject.alloc(skinningData.numBones());
                    PZArrayUtil.arraySet(this.m_boneWeights.array(), 0.0F);

                    for (int int0 = 0; int0 < this.m_boneWeightBindings.length(); int0++) {
                        AnimBoneWeight animBoneWeight = this.m_boneWeightBindings.get(int0);
                        this.initWeightBinding(skinningData, animBoneWeight);
                    }
                }
            }
        }
    }

    protected void initWeightBinding(SkinningData skinningData, AnimBoneWeight animBoneWeight) {
        if (animBoneWeight != null && !StringUtils.isNullOrEmpty(animBoneWeight.boneName)) {
            String string = animBoneWeight.boneName;
            SkinningBone skinningBone = skinningData.getBone(string);
            if (skinningBone == null) {
                DebugLog.Animation.error("Bone not found: %s", string);
            } else {
                float float0 = animBoneWeight.weight;
                this.assignBoneWeight(float0, skinningBone.Index);
                if (animBoneWeight.includeDescendants) {
                    Lambda.forEach(
                        skinningBone::forEachDescendant,
                        this,
                        float0,
                        (skinningBonex, animationTrack, float0x) -> animationTrack.assignBoneWeight(float0x, skinningBonex.Index)
                    );
                }
            }
        }
    }

    private void assignBoneWeight(float float1, int int0) {
        if (!this.hasBoneMask()) {
            throw new NullPointerException("Bone weights array not initialized.");
        } else {
            float float0 = this.m_boneWeights.get(int0);
            this.m_boneWeights.set(int0, Math.max(float1, float0));
        }
    }

    public float getBoneWeight(int boneIdx) {
        if (!this.hasBoneMask()) {
            return 1.0F;
        } else {
            return DebugOptions.instance.Character.Debug.Animate.NoBoneMasks.getValue()
                ? 1.0F
                : PZArrayUtil.getOrDefault(this.m_boneWeights.array(), boneIdx, 0.0F);
        }
    }

    public float getDeferredBoneWeight() {
        return this.m_deferredBone == null ? 0.0F : this.getBoneWeight(this.m_deferredBone.Index);
    }

    public void setLayerIdx(int layerIdx) {
        this.m_layerIdx = layerIdx;
    }

    public int getLayerIdx() {
        return this.m_layerIdx;
    }

    public boolean hasBoneMask() {
        return this.m_boneWeights != null;
    }

    public boolean isLooping() {
        return this.bLooping;
    }

    public void setDeferredBone(SkinningBone bone, BoneAxis axis) {
        this.m_deferredBone = bone;
        this.m_deferredBoneAxis = axis;
    }

    public void setUseDeferredRotation(boolean val) {
        this.m_useDeferredRotation = val;
    }

    public boolean getUseDeferredRotation() {
        return this.m_useDeferredRotation;
    }

    public boolean isFinished() {
        return !this.bLooping && this.getDuration() > 0.0F && this.currentTimeValue >= this.getDuration();
    }

    public float getCurrentTimeValue() {
        return this.currentTimeValue;
    }

    public void setCurrentTimeValue(float _currentTimeValue) {
        this.currentTimeValue = _currentTimeValue;
    }

    public float getPreviousTimeValue() {
        return this.previousTimeValue;
    }

    public void setPreviousTimeValue(float _previousTimeValue) {
        this.previousTimeValue = _previousTimeValue;
    }

    public void rewind(float rewindAmount) {
        this.advance(-rewindAmount);
    }

    public void scaledRewind(float rewindAmount) {
        this.scaledAdvance(-rewindAmount);
    }

    public void scaledAdvance(float advanceAmount) {
        this.advance(advanceAmount * this.SpeedDelta);
    }

    public void advance(float advanceAmount) {
        this.currentTimeValue = PZMath.wrap(this.currentTimeValue + advanceAmount, 0.0F, this.getDuration());
        this.previousTimeValue = PZMath.wrap(this.previousTimeValue + advanceAmount, 0.0F, this.getDuration());
    }

    public void advanceFraction(float advanceFraction) {
        this.advance(this.getDuration() * advanceFraction);
    }

    public void moveCurrentTimeValueTo(float target) {
        float float0 = target - this.currentTimeValue;
        this.advance(float0);
    }

    public void moveCurrentTimeValueToFraction(float fraction) {
        float float0 = this.getDuration() * fraction;
        this.moveCurrentTimeValueTo(float0);
    }

    public float getCurrentTimeFraction() {
        return this.hasClip() ? this.currentTimeValue / this.getDuration() : 0.0F;
    }

    public boolean hasClip() {
        return this.CurrentClip != null;
    }

    public AnimationClip getClip() {
        return this.CurrentClip;
    }

    public int getPriority() {
        return this.priority;
    }

    public static AnimationTrack createClone(AnimationTrack source, Supplier<AnimationTrack> allocator) {
        AnimationTrack animationTrack = (AnimationTrack)allocator.get();
        animationTrack.IsPlaying = source.IsPlaying;
        animationTrack.CurrentClip = source.CurrentClip;
        animationTrack.priority = source.priority;
        animationTrack.currentTimeValue = source.currentTimeValue;
        animationTrack.previousTimeValue = source.previousTimeValue;
        animationTrack.SyncTrackingEnabled = source.SyncTrackingEnabled;
        animationTrack.reverse = source.reverse;
        animationTrack.bLooping = source.bLooping;
        animationTrack.SpeedDelta = source.SpeedDelta;
        animationTrack.BlendDelta = source.BlendDelta;
        animationTrack.blendFieldWeight = source.blendFieldWeight;
        animationTrack.name = source.name;
        animationTrack.earlyBlendOutTime = source.earlyBlendOutTime;
        animationTrack.triggerOnNonLoopedAnimFadeOutEvent = source.triggerOnNonLoopedAnimFadeOutEvent;
        animationTrack.m_layerIdx = source.m_layerIdx;
        animationTrack.m_boneWeightBindings = PooledAnimBoneWeightArray.toArray(source.m_boneWeightBindings);
        animationTrack.m_boneWeights = PooledFloatArrayObject.toArray(source.m_boneWeights);
        animationTrack.m_deferredBone = source.m_deferredBone;
        animationTrack.m_deferredBoneAxis = source.m_deferredBoneAxis;
        animationTrack.m_useDeferredRotation = source.m_useDeferredRotation;
        return animationTrack;
    }

    private static class DeferredMotionData {
        float m_targetDeferredRotation;
        float m_prevDeferredRotation;
        final Quaternion m_targetDeferredRotationQ = new Quaternion();
        final Vector3f m_targetDeferredPosition = new Vector3f();
        final Vector2 m_prevDeferredMovement = new Vector2();
        final Vector2 m_targetDeferredMovement = new Vector2();
        float m_deferredRotationDiff;
        final Vector2 m_deferredMovementDiff = new Vector2();
        final Vector2 m_counterRotatedMovementDiff = new Vector2();

        public void reset() {
            this.m_deferredRotationDiff = 0.0F;
            this.m_targetDeferredRotation = 0.0F;
            this.m_prevDeferredRotation = 0.0F;
            this.m_targetDeferredRotationQ.setIdentity();
            this.m_targetDeferredMovement.set(0.0F, 0.0F);
            this.m_targetDeferredPosition.set(0.0F, 0.0F, 0.0F);
            this.m_prevDeferredMovement.set(0.0F, 0.0F);
            this.m_deferredMovementDiff.set(0.0F, 0.0F);
            this.m_counterRotatedMovementDiff.set(0.0F, 0.0F);
        }
    }

    private static class KeyframeSpan {
        Keyframe from;
        Keyframe to;
        int fromIdx = -1;
        int toIdx = -1;

        void clear() {
            this.from = null;
            this.to = null;
            this.fromIdx = -1;
            this.toIdx = -1;
        }

        Keyframe lerp(float float0, Keyframe keyframe) {
            keyframe.setIdentity();
            if (this.from == null && this.to == null) {
                return keyframe;
            } else if (this.to == null) {
                keyframe.set(this.from);
                return keyframe;
            } else if (this.from == null) {
                keyframe.set(this.to);
                return keyframe;
            } else {
                return Keyframe.lerp(this.from, this.to, float0, keyframe);
            }
        }

        void lerp(float float0, Vector3f vector3f0, Quaternion quaternion, Vector3f vector3f1) {
            if (this.from == null && this.to == null) {
                Keyframe.setIdentity(vector3f0, quaternion, vector3f1);
            } else if (this.to == null) {
                this.from.get(vector3f0, quaternion, vector3f1);
            } else if (this.from == null) {
                this.to.get(vector3f0, quaternion, vector3f1);
            } else if (!PerformanceSettings.InterpolateAnims) {
                this.to.get(vector3f0, quaternion, vector3f1);
            } else {
                Keyframe.lerp(this.from, this.to, float0, vector3f0, quaternion, vector3f1);
            }
        }

        boolean isSpan() {
            return this.from != null && this.to != null;
        }

        boolean isPost() {
            return (this.from == null || this.to == null) && this.from != this.to;
        }

        boolean isEmpty() {
            return this.from == null && this.to == null;
        }

        boolean containsTime(float float0) {
            return this.isSpan() && this.from.Time <= float0 && float0 <= this.to.Time;
        }

        public boolean isBone(int arg0) {
            return this.from != null && this.from.Bone == arg0 || this.to != null && this.to.Bone == arg0;
        }
    }

    private static class L_updateDeferredValues {
        static final Keyframe keyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
        static final Keyframe prevKeyFrame = new Keyframe(new Vector3f(), new Quaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
        static final Vector2 crDiff = new Vector2();
        static final Vector2 diff = new Vector2();
    }

    private static class l_getDeferredMovementFrameAt {
        static final AnimationTrack.KeyframeSpan span = new AnimationTrack.KeyframeSpan();
    }

    private static class l_updatePoseInternal {
        static final AnimationTrack.KeyframeSpan span = new AnimationTrack.KeyframeSpan();
    }

    private static class s_performance {
        static final PerformanceProfileProbe tickCurrentTime = new PerformanceProfileProbe("AnimationTrack.tickCurrentTime");
        static final PerformanceProfileProbe updateKeyframes = new PerformanceProfileProbe("AnimationTrack.updateKeyframes");
        static final PerformanceProfileProbe updateDeferredValues = new PerformanceProfileProbe("AnimationTrack.updateDeferredValues");
        static final PerformanceProfileProbe updatePose = new PerformanceProfileProbe("AnimationTrack.updatePose");
    }
}
