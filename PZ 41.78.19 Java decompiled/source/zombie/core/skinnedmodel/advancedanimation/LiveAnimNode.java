// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.animation.IAnimListener;
import zombie.debug.DebugOptions;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

/**
 * The Live version of an AnimNode  The AnimNode represents the data.  The LiveAnimNode represents the playback of said data, its blend weights, timing, transitions etc.
 */
public class LiveAnimNode extends PooledObject implements IAnimListener {
    private AnimNode m_sourceNode;
    private AnimLayer m_animLayer;
    private boolean m_active;
    private boolean m_wasActive;
    boolean m_TransitioningOut;
    private float m_Weight;
    private float m_RawWeight;
    private boolean m_isNew;
    private int m_layerIdx;
    private final LiveAnimNode.TransitionIn m_transitionIn = new LiveAnimNode.TransitionIn();
    final List<AnimationTrack> m_AnimationTracks = new ArrayList<>();
    float m_NodeAnimTime;
    float m_PrevNodeAnimTime;
    private boolean m_blendingIn;
    private boolean m_blendingOut;
    private AnimTransition m_transitionOut;
    private static final Pool<LiveAnimNode> s_pool = new Pool<>(LiveAnimNode::new);

    protected LiveAnimNode() {
    }

    public static LiveAnimNode alloc(AnimLayer animLayer, AnimNode sourceNode, int layerIdx) {
        LiveAnimNode liveAnimNode = s_pool.alloc();
        liveAnimNode.reset();
        liveAnimNode.m_sourceNode = sourceNode;
        liveAnimNode.m_animLayer = animLayer;
        liveAnimNode.m_layerIdx = layerIdx;
        return liveAnimNode;
    }

    private void reset() {
        this.m_sourceNode = null;
        this.m_animLayer = null;
        this.m_active = false;
        this.m_wasActive = false;
        this.m_TransitioningOut = false;
        this.m_Weight = 0.0F;
        this.m_RawWeight = 0.0F;
        this.m_isNew = true;
        this.m_layerIdx = -1;
        this.m_transitionIn.reset();
        this.m_AnimationTracks.clear();
        this.m_NodeAnimTime = 0.0F;
        this.m_PrevNodeAnimTime = 0.0F;
        this.m_blendingIn = false;
        this.m_blendingOut = false;
        this.m_transitionOut = null;
    }

    @Override
    public void onReleased() {
        this.reset();
    }

    public String getName() {
        return this.m_sourceNode.m_Name;
    }

    public boolean isTransitioningIn() {
        return this.m_transitionIn.m_active && this.m_transitionIn.m_track != null;
    }

    public void startTransitionIn(LiveAnimNode transitionFrom, AnimTransition transitionIn, AnimationTrack track) {
        this.startTransitionIn(transitionFrom.getSourceNode(), transitionIn, track);
    }

    public void startTransitionIn(AnimNode transitionFrom, AnimTransition transitionIn, AnimationTrack track) {
        this.m_transitionIn.m_active = track != null;
        this.m_transitionIn.m_transitionedFrom = transitionFrom.m_Name;
        this.m_transitionIn.m_data = transitionIn;
        this.m_transitionIn.m_track = track;
        this.m_transitionIn.m_weight = 0.0F;
        this.m_transitionIn.m_rawWeight = 0.0F;
        this.m_transitionIn.m_blendingIn = true;
        this.m_transitionIn.m_blendingOut = false;
        this.m_transitionIn.m_time = 0.0F;
        if (this.m_transitionIn.m_track != null) {
            this.m_transitionIn.m_track.addListener(this);
        }

        this.setMainTracksPlaying(false);
    }

    public void setTransitionOut(AnimTransition transitionOut) {
        this.m_transitionOut = transitionOut;
    }

    public void update(float timeDelta) {
        this.m_isNew = false;
        if (this.m_active != this.m_wasActive) {
            this.m_blendingIn = this.m_active;
            this.m_blendingOut = !this.m_active;
            if (this.m_transitionIn.m_active) {
                this.m_transitionIn.m_blendingIn = this.m_active;
                this.m_transitionIn.m_blendingOut = !this.m_active;
            }

            this.m_wasActive = this.m_active;
        }

        boolean boolean0 = this.isMainAnimActive();
        if (this.isTransitioningIn()) {
            this.updateTransitioningIn(timeDelta);
        }

        boolean boolean1 = this.isMainAnimActive();
        if (boolean1) {
            if (this.m_blendingOut && this.m_sourceNode.m_StopAnimOnExit) {
                this.setMainTracksPlaying(false);
            } else {
                this.setMainTracksPlaying(true);
            }
        } else {
            this.setMainTracksPlaying(false);
        }

        if (boolean1) {
            boolean boolean2 = !boolean0;
            if (boolean2 && this.isLooped()) {
                float float0 = this.getMainInitialRewindTime();
                PZArrayUtil.forEach(this.m_AnimationTracks, Lambda.consumer(float0, AnimationTrack::scaledRewind));
            }

            if (this.m_blendingIn) {
                this.updateBlendingIn(timeDelta);
            } else if (this.m_blendingOut) {
                this.updateBlendingOut(timeDelta);
            }

            this.m_PrevNodeAnimTime = this.m_NodeAnimTime;
            this.m_NodeAnimTime += timeDelta;
            if (!this.m_transitionIn.m_active && this.m_transitionIn.m_track != null && this.m_transitionIn.m_track.BlendDelta <= 0.0F) {
                this.m_animLayer.getAnimationTrack().removeTrack(this.m_transitionIn.m_track);
                this.m_transitionIn.reset();
            }
        }
    }

    private void updateTransitioningIn(float float5) {
        float float0 = this.m_transitionIn.m_track.SpeedDelta;
        float float1 = this.m_transitionIn.m_track.getDuration();
        this.m_transitionIn.m_time = this.m_transitionIn.m_track.getCurrentTimeValue();
        if (this.m_transitionIn.m_time >= float1) {
            this.m_transitionIn.m_active = false;
            this.m_transitionIn.m_weight = 0.0F;
        } else {
            if (!this.m_transitionIn.m_blendingOut) {
                boolean boolean0 = AnimCondition.pass(this.m_animLayer.getVariableSource(), this.m_transitionIn.m_data.m_Conditions);
                if (!boolean0) {
                    this.m_transitionIn.m_blendingIn = false;
                    this.m_transitionIn.m_blendingOut = true;
                }
            }

            float float2 = this.getTransitionInBlendOutTime() * float0;
            if (this.m_transitionIn.m_time >= float1 - float2) {
                this.m_transitionIn.m_blendingIn = false;
                this.m_transitionIn.m_blendingOut = true;
            }

            if (this.m_transitionIn.m_blendingIn) {
                float float3 = this.getTransitionInBlendInTime() * float0;
                float float4 = this.incrementBlendTime(this.m_transitionIn.m_rawWeight, float3, float5 * float0);
                float float6 = PZMath.clamp(float4 / float3, 0.0F, 1.0F);
                this.m_transitionIn.m_rawWeight = float6;
                this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(float6);
                this.m_transitionIn.m_blendingIn = float4 < float3;
                this.m_transitionIn.m_active = float4 < float1;
            }

            if (this.m_transitionIn.m_blendingOut) {
                float float7 = this.getTransitionInBlendOutTime() * float0;
                float float8 = this.incrementBlendTime(1.0F - this.m_transitionIn.m_rawWeight, float7, float5 * float0);
                float float9 = PZMath.clamp(1.0F - float8 / float7, 0.0F, 1.0F);
                this.m_transitionIn.m_rawWeight = float9;
                this.m_transitionIn.m_weight = PZMath.lerpFunc_EaseOutInQuad(float9);
                this.m_transitionIn.m_blendingOut = float8 < float7;
                this.m_transitionIn.m_active = this.m_transitionIn.m_blendingOut;
            }
        }
    }

    public void addMainTrack(AnimationTrack track) {
        if (!this.isLooped() && !this.m_sourceNode.m_StopAnimOnExit && this.m_sourceNode.m_EarlyTransitionOut) {
            float float0 = this.getBlendOutTime();
            if (float0 > 0.0F && Float.isFinite(float0)) {
                track.earlyBlendOutTime = float0;
                track.triggerOnNonLoopedAnimFadeOutEvent = true;
            }
        }

        this.m_AnimationTracks.add(track);
    }

    private void setMainTracksPlaying(boolean boolean0) {
        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_AnimationTracks, boolean0, (animationTrack, boolean0x) -> animationTrack.IsPlaying = boolean0x);
    }

    private void updateBlendingIn(float float2) {
        float float0 = this.getBlendInTime();
        if (float0 <= 0.0F) {
            this.m_Weight = 1.0F;
            this.m_RawWeight = 1.0F;
            this.m_blendingIn = false;
        } else {
            float float1 = this.incrementBlendTime(this.m_RawWeight, float0, float2);
            float float3 = PZMath.clamp(float1 / float0, 0.0F, 1.0F);
            this.m_RawWeight = float3;
            this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(float3);
            this.m_blendingIn = float1 < float0;
        }
    }

    private void updateBlendingOut(float float2) {
        float float0 = this.getBlendOutTime();
        if (float0 <= 0.0F) {
            this.m_Weight = 0.0F;
            this.m_RawWeight = 0.0F;
            this.m_blendingOut = false;
        } else {
            float float1 = this.incrementBlendTime(1.0F - this.m_RawWeight, float0, float2);
            float float3 = PZMath.clamp(1.0F - float1 / float0, 0.0F, 1.0F);
            this.m_RawWeight = float3;
            this.m_Weight = PZMath.lerpFunc_EaseOutInQuad(float3);
            this.m_blendingOut = float1 < float0;
        }
    }

    private float incrementBlendTime(float float1, float float2, float float3) {
        float float0 = float1 * float2;
        return float0 + float3;
    }

    public float getTransitionInBlendInTime() {
        return this.m_transitionIn.m_data != null && this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY
            ? this.m_transitionIn.m_data.m_blendInTime
            : 0.0F;
    }

    public float getMainInitialRewindTime() {
        float float0 = 0.0F;
        if (this.m_sourceNode.m_randomAdvanceFraction > 0.0F) {
            float float1 = Rand.Next(0.0F, this.m_sourceNode.m_randomAdvanceFraction);
            float0 = float1 * this.getMaxDuration();
        }

        if (this.m_transitionIn.m_data == null) {
            return 0.0F - float0;
        } else {
            float float2 = this.getTransitionInBlendOutTime();
            float float3 = this.m_transitionIn.m_data.m_SyncAdjustTime;
            return this.m_transitionIn.m_track != null ? float2 - float3 : float2 - float3 - float0;
        }
    }

    private float getMaxDuration() {
        float float0 = 0.0F;
        int int0 = 0;

        for (int int1 = this.m_AnimationTracks.size(); int0 < int1; int0++) {
            AnimationTrack animationTrack = this.m_AnimationTracks.get(int0);
            float float1 = animationTrack.getDuration();
            float0 = PZMath.max(float1, float0);
        }

        return float0;
    }

    public float getTransitionInBlendOutTime() {
        return this.getBlendInTime();
    }

    public float getBlendInTime() {
        if (this.m_transitionIn.m_data == null) {
            return this.m_sourceNode.m_BlendTime;
        } else if (this.m_transitionIn.m_track != null && this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
            return this.m_transitionIn.m_data.m_blendOutTime;
        } else {
            if (this.m_transitionIn.m_track == null) {
                if (this.m_transitionIn.m_data.m_blendInTime != Float.POSITIVE_INFINITY) {
                    return this.m_transitionIn.m_data.m_blendInTime;
                }

                if (this.m_transitionIn.m_data.m_blendOutTime != Float.POSITIVE_INFINITY) {
                    return this.m_transitionIn.m_data.m_blendOutTime;
                }
            }

            return this.m_sourceNode.m_BlendTime;
        }
    }

    public float getBlendOutTime() {
        if (this.m_transitionOut == null) {
            return this.m_sourceNode.getBlendOutTime();
        } else if (!StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName) && this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
            return this.m_transitionOut.m_blendInTime;
        } else {
            if (StringUtils.isNullOrWhitespace(this.m_transitionOut.m_AnimName)) {
                if (this.m_transitionOut.m_blendOutTime != Float.POSITIVE_INFINITY) {
                    return this.m_transitionOut.m_blendOutTime;
                }

                if (this.m_transitionOut.m_blendInTime != Float.POSITIVE_INFINITY) {
                    return this.m_transitionOut.m_blendInTime;
                }
            }

            return this.m_sourceNode.getBlendOutTime();
        }
    }

    @Override
    public void onAnimStarted(AnimationTrack track) {
        this.invokeAnimStartTimeEvent();
    }

    @Override
    public void onLoopedAnim(AnimationTrack track) {
        if (!this.m_TransitioningOut) {
            this.invokeAnimEndTimeEvent();
        }
    }

    @Override
    public void onNonLoopedAnimFadeOut(AnimationTrack track) {
        if (DebugOptions.instance.Animation.AllowEarlyTransitionOut.getValue()) {
            this.invokeAnimEndTimeEvent();
            this.m_TransitioningOut = true;
        }
    }

    @Override
    public void onNonLoopedAnimFinished(AnimationTrack track) {
        if (!this.m_TransitioningOut) {
            this.invokeAnimEndTimeEvent();
        }
    }

    @Override
    public void onTrackDestroyed(AnimationTrack track) {
        this.m_AnimationTracks.remove(track);
        if (this.m_transitionIn.m_track == track) {
            this.m_transitionIn.m_track = null;
            this.m_transitionIn.m_active = false;
            this.m_transitionIn.m_weight = 0.0F;
            this.setMainTracksPlaying(true);
        }
    }

    private void invokeAnimStartTimeEvent() {
        this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.Start);
    }

    private void invokeAnimEndTimeEvent() {
        this.invokeAnimTimeEvent(AnimEvent.AnimEventTime.End);
    }

    private void invokeAnimTimeEvent(AnimEvent.AnimEventTime animEventTime) {
        List list = this.getSourceNode().m_Events;
        int int0 = 0;

        for (int int1 = list.size(); int0 < int1; int0++) {
            AnimEvent animEvent = (AnimEvent)list.get(int0);
            if (animEvent.m_Time == animEventTime) {
                this.m_animLayer.invokeAnimEvent(animEvent);
            }
        }
    }

    public AnimNode getSourceNode() {
        return this.m_sourceNode;
    }

    /**
     * Returns TRUE if this Live node is currently Active, and if the source AnimNode is an Idle animation.
     */
    public boolean isIdleAnimActive() {
        return this.m_active && this.m_sourceNode.isIdleAnim();
    }

    public boolean isActive() {
        return this.m_active;
    }

    public void setActive(boolean active) {
        this.m_active = active;
    }

    public boolean isLooped() {
        return this.m_sourceNode.m_Looped;
    }

    public float getWeight() {
        return this.m_Weight;
    }

    public float getTransitionInWeight() {
        return this.m_transitionIn.m_weight;
    }

    public boolean wasActivated() {
        return this.m_active != this.m_wasActive && this.m_active;
    }

    public boolean wasDeactivated() {
        return this.m_active != this.m_wasActive && this.m_wasActive;
    }

    public boolean isNew() {
        return this.m_isNew;
    }

    public int getPlayingTrackCount() {
        int int0 = 0;
        if (this.isMainAnimActive()) {
            int0 += this.m_AnimationTracks.size();
        }

        if (this.isTransitioningIn()) {
            int0++;
        }

        return int0;
    }

    public boolean isMainAnimActive() {
        return !this.isTransitioningIn() || this.m_transitionIn.m_blendingOut;
    }

    public AnimationTrack getPlayingTrackAt(int trackIdx) {
        int int0 = this.getPlayingTrackCount();
        if (trackIdx < 0 || trackIdx >= int0) {
            throw new IndexOutOfBoundsException("TrackIdx out of bounds 0 - " + this.getPlayingTrackCount());
        } else {
            return this.isTransitioningIn() && trackIdx == int0 - 1 ? this.m_transitionIn.m_track : this.m_AnimationTracks.get(trackIdx);
        }
    }

    public String getTransitionFrom() {
        return this.m_transitionIn.m_transitionedFrom;
    }

    public void setTransitionInBlendDelta(float blendDelta) {
        if (this.m_transitionIn.m_track != null) {
            this.m_transitionIn.m_track.BlendDelta = blendDelta;
        }
    }

    public AnimationTrack getTransitionInTrack() {
        return this.m_transitionIn.m_track;
    }

    public int getTransitionLayerIdx() {
        return this.m_transitionIn.m_track != null ? this.m_transitionIn.m_track.getLayerIdx() : -1;
    }

    public int getLayerIdx() {
        return this.m_layerIdx;
    }

    public int getPriority() {
        return this.m_sourceNode.getPriority();
    }

    public String getDeferredBoneName() {
        return this.m_sourceNode.getDeferredBoneName();
    }

    public BoneAxis getDeferredBoneAxis() {
        return this.m_sourceNode.getDeferredBoneAxis();
    }

    public List<AnimBoneWeight> getSubStateBoneWeights() {
        return this.m_sourceNode.m_SubStateBoneWeights;
    }

    public AnimTransition findTransitionTo(IAnimationVariableSource varSource, String name) {
        return this.m_sourceNode.findTransitionTo(varSource, name);
    }

    public float getSpeedScale(IAnimationVariableSource varSource) {
        return this.m_sourceNode.getSpeedScale(varSource);
    }

    private static class TransitionIn {
        private float m_time;
        private String m_transitionedFrom;
        private boolean m_active;
        private AnimationTrack m_track;
        private AnimTransition m_data;
        private float m_weight;
        private float m_rawWeight;
        private boolean m_blendingIn;
        private boolean m_blendingOut;

        private void reset() {
            this.m_time = 0.0F;
            this.m_transitionedFrom = null;
            this.m_active = false;
            this.m_track = null;
            this.m_data = null;
            this.m_weight = 0.0F;
            this.m_rawWeight = 0.0F;
            this.m_blendingIn = false;
            this.m_blendingOut = false;
        }
    }
}
