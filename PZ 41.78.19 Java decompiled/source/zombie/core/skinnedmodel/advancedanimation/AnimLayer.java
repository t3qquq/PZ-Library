// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import org.joml.Math;
import zombie.GameProfiler;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.animation.IAnimListener;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;

public final class AnimLayer implements IAnimListener {
    private final AnimLayer m_parentLayer;
    private final IAnimatable m_Character;
    private AnimState m_State = null;
    private AnimNode m_CurrentNode = null;
    private IAnimEventCallback m_AnimEventsCallback;
    private LiveAnimNode m_currentSyncNode;
    private AnimationTrack m_currentSyncTrack;
    private final List<AnimNode> m_reusableAnimNodes = new ArrayList<>();
    private final List<LiveAnimNode> m_liveAnimNodes = new ArrayList<>();
    private static final AnimEvent s_activeAnimLoopedEvent = new AnimEvent();
    private static final AnimEvent s_activeNonLoopedAnimFadeOutEvent = new AnimEvent();
    private static final AnimEvent s_activeAnimFinishingEvent = new AnimEvent();
    private static final AnimEvent s_activeNonLoopedAnimFinishedEvent = new AnimEvent();

    public AnimLayer(IAnimatable character, IAnimEventCallback animEventsCallback) {
        this(null, character, animEventsCallback);
    }

    public AnimLayer(AnimLayer parentLayer, IAnimatable character, IAnimEventCallback animEventsCallback) {
        this.m_parentLayer = parentLayer;
        this.m_Character = character;
        this.m_AnimEventsCallback = animEventsCallback;
    }

    public String getCurrentStateName() {
        return this.m_State == null ? null : this.m_State.m_Name;
    }

    public boolean hasState() {
        return this.m_State != null;
    }

    public boolean isStateless() {
        return this.m_State == null;
    }

    public boolean isSubLayer() {
        return this.m_parentLayer != null;
    }

    public boolean isCurrentState(String stateName) {
        return this.m_State != null && StringUtils.equals(this.m_State.m_Name, stateName);
    }

    public AnimationMultiTrack getAnimationTrack() {
        if (this.m_Character == null) {
            return null;
        } else {
            AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
            return animationPlayer == null ? null : animationPlayer.getMultiTrack();
        }
    }

    public IAnimationVariableSource getVariableSource() {
        return this.m_Character;
    }

    public LiveAnimNode getCurrentSyncNode() {
        return this.m_currentSyncNode;
    }

    public AnimationTrack getCurrentSyncTrack() {
        return this.m_currentSyncTrack;
    }

    @Override
    public void onAnimStarted(AnimationTrack track) {
    }

    @Override
    public void onLoopedAnim(AnimationTrack track) {
        this.invokeAnimEvent(track, s_activeAnimLoopedEvent, false);
    }

    @Override
    public void onNonLoopedAnimFadeOut(AnimationTrack track) {
        this.invokeAnimEvent(track, s_activeAnimFinishingEvent, true);
        this.invokeAnimEvent(track, s_activeNonLoopedAnimFadeOutEvent, true);
    }

    @Override
    public void onNonLoopedAnimFinished(AnimationTrack track) {
        this.invokeAnimEvent(track, s_activeAnimFinishingEvent, false);
        this.invokeAnimEvent(track, s_activeNonLoopedAnimFinishedEvent, true);
    }

    @Override
    public void onTrackDestroyed(AnimationTrack track) {
    }

    protected void invokeAnimEvent(AnimationTrack animationTrack, AnimEvent animEvent, boolean boolean0) {
        if (this.m_AnimEventsCallback != null) {
            int int0 = 0;

            for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
                LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
                if ((!liveAnimNode.m_TransitioningOut || boolean0)
                    && liveAnimNode.getSourceNode().m_State == this.m_State
                    && liveAnimNode.m_AnimationTracks.contains(animationTrack)) {
                    this.invokeAnimEvent(animEvent);
                    break;
                }
            }
        }
    }

    protected void invokeAnimEvent(AnimEvent animEvent) {
        if (this.m_AnimEventsCallback == null) {
            DebugLog.Animation.warn("invokeAnimEvent. No listener. %s", animEvent.toDetailsString());
        } else {
            this.m_AnimEventsCallback.OnAnimEvent(this, animEvent);
        }
    }

    public String GetDebugString() {
        String string0 = this.m_Character.getAdvancedAnimator().animSet.m_Name;
        if (this.m_State != null) {
            string0 = string0 + "/" + this.m_State.m_Name;
            if (this.m_CurrentNode != null) {
                string0 = string0 + "/" + this.m_CurrentNode.m_Name + ": " + this.m_CurrentNode.m_AnimName;
            }
        }

        String string1 = "State: " + string0;

        for (LiveAnimNode liveAnimNode : this.m_liveAnimNodes) {
            string1 = string1 + "\n  Node: " + liveAnimNode.getSourceNode().m_Name;
        }

        AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
        if (animationMultiTrack != null) {
            string1 = string1 + "\n  AnimTrack:";

            for (AnimationTrack animationTrack : animationMultiTrack.getTracks()) {
                string1 = string1 + "\n    Anim: " + animationTrack.name + " Weight: " + animationTrack.BlendDelta;
            }
        }

        return string1;
    }

    public void Reset() {
        AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();

        for (int int0 = this.m_liveAnimNodes.size() - 1; int0 >= 0; int0--) {
            LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
            liveAnimNode.setActive(false);
            if (animationMultiTrack != null) {
                animationMultiTrack.removeTracks(liveAnimNode.m_AnimationTracks);
            }

            this.m_liveAnimNodes.remove(int0).release();
        }

        this.m_State = null;
    }

    public boolean TransitionTo(AnimState newState, boolean force) {
        AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
        if (animationMultiTrack == null) {
            if (this.m_Character == null) {
                DebugLog.General.error("AnimationTrack is null. Character is null.");
                this.m_State = null;
                return false;
            } else if (this.m_Character.getAnimationPlayer() == null) {
                DebugLog.General.error("AnimationTrack is null. Character ModelInstance.AnimPlayer is null.");
                this.m_State = null;
                return false;
            } else {
                DebugLog.General.error("AnimationTrack is null. Unknown reason.");
                return false;
            }
        } else if (newState == this.m_State && !force) {
            return true;
        } else {
            if (DebugOptions.instance.Animation.AnimLayer.LogStateChanges.getValue()) {
                String string0 = this.m_parentLayer == null ? "" : AnimState.getStateName(this.m_parentLayer.m_State) + " | ";
                String string1 = String.format("State: %s%s => %s", string0, AnimState.getStateName(this.m_State), AnimState.getStateName(newState));
                DebugLog.General.debugln(string1);
                if (this.m_Character instanceof IsoGameCharacter) {
                    ((IsoGameCharacter)this.m_Character).setSayLine(string1);
                }
            }

            this.m_State = newState;

            for (int int0 = 0; int0 < this.m_liveAnimNodes.size(); int0++) {
                LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
                liveAnimNode.m_TransitioningOut = true;
            }

            return true;
        }
    }

    public void Update() {
        GameProfiler.getInstance().invokeAndMeasure("AnimLayer.Update", this, AnimLayer::updateInternal);
    }

    private void updateInternal() {
        float float0 = GameTime.instance.getTimeDelta();
        this.removeFadedOutNodes();
        this.updateNodeActiveFlags();
        LiveAnimNode liveAnimNode0 = this.getHighestLiveNode();
        this.m_currentSyncNode = liveAnimNode0;
        this.m_currentSyncTrack = null;
        if (liveAnimNode0 != null) {
            int int0 = 0;

            for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
                LiveAnimNode liveAnimNode1 = this.m_liveAnimNodes.get(int0);
                liveAnimNode1.update(float0);
            }

            IAnimatable iAnimatable = this.m_Character;
            this.updateMaximumTwist(iAnimatable);
            boolean boolean0 = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue()
                && iAnimatable.getVariableBoolean("dbgForceAnim")
                && iAnimatable.getVariableBoolean("dbgForceAnimScalars");
            String string0 = boolean0 ? iAnimatable.getVariableString("dbgForceAnimNodeName") : null;
            AnimationTrack animationTrack0 = this.findSyncTrack(liveAnimNode0);
            this.m_currentSyncTrack = animationTrack0;
            float float1 = animationTrack0 != null ? animationTrack0.getCurrentTimeFraction() : -1.0F;
            int int2 = 0;

            for (int int3 = this.m_liveAnimNodes.size(); int2 < int3; int2++) {
                LiveAnimNode liveAnimNode2 = this.m_liveAnimNodes.get(int2);
                float float2 = 1.0F;
                int int4 = 0;

                for (int int5 = liveAnimNode2.getPlayingTrackCount(); int4 < int5; int4++) {
                    AnimationTrack animationTrack1 = liveAnimNode2.getPlayingTrackAt(int4);
                    if (animationTrack1.IsPlaying) {
                        if (animationTrack0 != null && animationTrack1.SyncTrackingEnabled && animationTrack1.isLooping() && animationTrack1 != animationTrack0
                            )
                         {
                            animationTrack1.moveCurrentTimeValueToFraction(float1);
                        }

                        if (animationTrack1.name.equals(liveAnimNode2.getSourceNode().m_AnimName)) {
                            float2 = animationTrack1.getDuration();
                            liveAnimNode2.m_NodeAnimTime = animationTrack1.getCurrentTimeValue();
                        }
                    }
                }

                if (this.m_AnimEventsCallback != null && liveAnimNode2.getSourceNode().m_Events.size() > 0) {
                    float float3 = liveAnimNode2.m_NodeAnimTime / float2;
                    float float4 = liveAnimNode2.m_PrevNodeAnimTime / float2;
                    List list = liveAnimNode2.getSourceNode().m_Events;
                    int int6 = 0;

                    for (int int7 = list.size(); int6 < int7; int6++) {
                        AnimEvent animEvent = (AnimEvent)list.get(int6);
                        if (animEvent.m_Time == AnimEvent.AnimEventTime.Percentage) {
                            float float5 = animEvent.m_TimePc;
                            if (float4 < float5 && float5 <= float3) {
                                this.invokeAnimEvent(animEvent);
                            } else {
                                if (!liveAnimNode2.isLooped() && float3 < float5) {
                                    break;
                                }

                                if (liveAnimNode2.isLooped() && float4 > float3) {
                                    if (float4 < float5 && float5 <= float3 + 1.0F) {
                                        this.invokeAnimEvent(animEvent);
                                    } else if (float4 > float5 && float5 <= float3) {
                                        this.invokeAnimEvent(animEvent);
                                    }
                                }
                            }
                        }
                    }
                }

                if (liveAnimNode2.getPlayingTrackCount() != 0) {
                    boolean boolean1 = boolean0 && StringUtils.equalsIgnoreCase(liveAnimNode2.getSourceNode().m_Name, string0);
                    String string1 = boolean1 ? "dbgForceScalar" : liveAnimNode2.getSourceNode().m_Scalar;
                    String string2 = boolean1 ? "dbgForceScalar2" : liveAnimNode2.getSourceNode().m_Scalar2;
                    float float6 = liveAnimNode2.getTransitionInWeight();
                    liveAnimNode2.setTransitionInBlendDelta(float6);
                    if (liveAnimNode2.m_AnimationTracks.size() > 1) {
                        float6 = iAnimatable.getVariableFloat(string1, 0.0F);
                        float float7 = iAnimatable.getVariableFloat(string2, 0.0F);
                        this.applyBlendField(liveAnimNode2, float6, float7);
                    } else if (!liveAnimNode2.m_AnimationTracks.isEmpty()) {
                        float6 = liveAnimNode2.getWeight();
                        float float8 = iAnimatable.getVariableFloat(string1, 1.0F);
                        liveAnimNode2.m_AnimationTracks.get(0).BlendDelta = float6 * Math.abs(float8);
                    }
                }
            }

            if (this.isRecording()) {
                this.logBlendWeights();
                this.logCurrentState();
            }
        }
    }

    private void updateMaximumTwist(IAnimationVariableSource iAnimationVariableSource) {
        IAnimationVariableSlot iAnimationVariableSlot = iAnimationVariableSource.getVariable("maxTwist");
        if (iAnimationVariableSlot != null) {
            float float0 = iAnimationVariableSlot.getValueFloat();
            float float1 = 0.0F;
            float float2 = 1.0F;

            for (int int0 = this.m_liveAnimNodes.size() - 1; int0 >= 0; int0--) {
                LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
                float float3 = liveAnimNode.getWeight();
                if (float2 <= 0.0F) {
                    break;
                }

                float float4 = PZMath.clamp(float3, 0.0F, float2);
                float2 -= float4;
                float float5 = PZMath.clamp(liveAnimNode.getSourceNode().m_maxTorsoTwist, 0.0F, 70.0F);
                float1 += float5 * float4;
            }

            if (float2 > 0.0F) {
                float1 += float0 * float2;
            }

            iAnimationVariableSlot.setValue(float1);
        }
    }

    public void updateNodeActiveFlags() {
        for (int int0 = 0; int0 < this.m_liveAnimNodes.size(); int0++) {
            LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
            liveAnimNode.setActive(false);
        }

        AnimState animState = this.m_State;
        IAnimatable iAnimatable = this.m_Character;
        if (animState != null && !iAnimatable.getVariableBoolean("AnimLocked")) {
            List list = animState.getAnimNodes(iAnimatable, this.m_reusableAnimNodes);
            int int1 = 0;

            for (int int2 = list.size(); int1 < int2; int1++) {
                AnimNode animNode = (AnimNode)list.get(int1);
                this.getOrCreateLiveNode(animNode);
            }
        }

        this.updateNewNodeTransitions();
    }

    private void updateNewNodeTransitions() {
        GameProfiler.getInstance().invokeAndMeasure("updateNewNodeTransitions", this, AnimLayer::updateNewNodeTransitionsInternal);
    }

    private void updateNewNodeTransitionsInternal() {
        IAnimatable iAnimatable = this.m_Character;
        int int0 = 0;

        for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
            LiveAnimNode liveAnimNode0 = this.m_liveAnimNodes.get(int0);
            if (liveAnimNode0.isNew() && liveAnimNode0.wasActivated()) {
                LiveAnimNode liveAnimNode1 = this.findTransitionToNewNode(liveAnimNode0, false);
                if (liveAnimNode1 != null) {
                    AnimTransition animTransition = liveAnimNode1.findTransitionTo(iAnimatable, liveAnimNode0.getName());
                    float float0 = animTransition.m_speedScale;
                    if (float0 == Float.POSITIVE_INFINITY) {
                        float0 = liveAnimNode0.getSpeedScale(this.m_Character);
                    }

                    AnimationTrack animationTrack = null;
                    if (!StringUtils.isNullOrWhitespace(animTransition.m_AnimName)) {
                        AnimLayer.StartAnimTrackParameters startAnimTrackParameters = AnimLayer.StartAnimTrackParameters.alloc();
                        startAnimTrackParameters.subLayerBoneWeights = liveAnimNode1.getSubStateBoneWeights();
                        startAnimTrackParameters.speedScale = float0;
                        startAnimTrackParameters.deferredBoneName = liveAnimNode1.getDeferredBoneName();
                        startAnimTrackParameters.deferredBoneAxis = liveAnimNode1.getDeferredBoneAxis();
                        startAnimTrackParameters.priority = liveAnimNode1.getPriority();
                        animationTrack = this.startAnimTrack(animTransition.m_AnimName, startAnimTrackParameters);
                        startAnimTrackParameters.release();
                        if (animationTrack == null) {
                            if (DebugLog.isEnabled(DebugType.Animation)) {
                                DebugLog.Animation
                                    .println(
                                        "  TransitionTo failed to play transition track: %s -> %s -> %s",
                                        liveAnimNode1.getName(),
                                        animTransition.m_AnimName,
                                        liveAnimNode0.getName()
                                    );
                            }
                            continue;
                        }

                        if (DebugLog.isEnabled(DebugType.Animation)) {
                            DebugLog.Animation
                                .println("  TransitionTo found: %s -> %s -> %s", liveAnimNode1.getName(), animTransition.m_AnimName, liveAnimNode0.getName());
                        }
                    } else if (DebugLog.isEnabled(DebugType.Animation)) {
                        DebugLog.Animation.println("  TransitionTo found: %s -> <no anim> -> %s", liveAnimNode1.getName(), liveAnimNode0.getName());
                    }

                    liveAnimNode0.startTransitionIn(liveAnimNode1, animTransition, animationTrack);
                    liveAnimNode1.setTransitionOut(animTransition);
                }
            }
        }
    }

    public LiveAnimNode findTransitionToNewNode(LiveAnimNode newNode, boolean isSubNode) {
        LiveAnimNode liveAnimNode0 = null;
        int int0 = 0;

        for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
            LiveAnimNode liveAnimNode1 = this.m_liveAnimNodes.get(int0);
            if (liveAnimNode1 != newNode && (isSubNode || liveAnimNode1.wasDeactivated())) {
                AnimNode animNode = liveAnimNode1.getSourceNode();
                AnimTransition animTransition = animNode.findTransitionTo(this.m_Character, newNode.getName());
                if (animTransition != null) {
                    liveAnimNode0 = liveAnimNode1;
                    break;
                }
            }
        }

        if (liveAnimNode0 == null && this.isSubLayer()) {
            liveAnimNode0 = this.m_parentLayer.findTransitionToNewNode(newNode, true);
        }

        return liveAnimNode0;
    }

    public void removeFadedOutNodes() {
        for (int int0 = this.m_liveAnimNodes.size() - 1; int0 >= 0; int0--) {
            LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
            if (!liveAnimNode.isActive()
                && (!liveAnimNode.isTransitioningIn() || !(liveAnimNode.getTransitionInWeight() > 0.01F))
                && !(liveAnimNode.getWeight() > 0.01F)) {
                this.removeLiveNodeAt(int0);
            }
        }
    }

    public void render() {
        IAnimatable iAnimatable = this.m_Character;
        boolean boolean0 = DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue()
            && iAnimatable.getVariableBoolean("dbgForceAnim")
            && iAnimatable.getVariableBoolean("dbgForceAnimScalars");
        String string0 = boolean0 ? iAnimatable.getVariableString("dbgForceAnimNodeName") : null;
        int int0 = 0;

        for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
            LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
            if (liveAnimNode.m_AnimationTracks.size() > 1) {
                boolean boolean1 = boolean0 && StringUtils.equalsIgnoreCase(liveAnimNode.getSourceNode().m_Name, string0);
                String string1 = boolean1 ? "dbgForceScalar" : liveAnimNode.getSourceNode().m_Scalar;
                String string2 = boolean1 ? "dbgForceScalar2" : liveAnimNode.getSourceNode().m_Scalar2;
                float float0 = iAnimatable.getVariableFloat(string1, 0.0F);
                float float1 = iAnimatable.getVariableFloat(string2, 0.0F);
                if (liveAnimNode.isActive()) {
                    liveAnimNode.getSourceNode().m_picker.render(float0, float1);
                }
            }
        }
    }

    private void logBlendWeights() {
        AnimationPlayerRecorder animationPlayerRecorder = this.m_Character.getAnimationPlayer().getRecorder();
        int int0 = 0;

        for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
            LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
            animationPlayerRecorder.logAnimNode(liveAnimNode);
        }
    }

    private void logCurrentState() {
        AnimationPlayerRecorder animationPlayerRecorder = this.m_Character.getAnimationPlayer().getRecorder();
        animationPlayerRecorder.logAnimState(this.m_State);
    }

    private void removeLiveNodeAt(int int0) {
        LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
        AnimationMultiTrack animationMultiTrack = this.getAnimationTrack();
        animationMultiTrack.removeTracks(liveAnimNode.m_AnimationTracks);
        animationMultiTrack.removeTrack(liveAnimNode.getTransitionInTrack());
        this.m_liveAnimNodes.remove(int0).release();
    }

    private void applyBlendField(LiveAnimNode liveAnimNode, float float0, float float1) {
        if (liveAnimNode.isActive()) {
            AnimNode animNode = liveAnimNode.getSourceNode();
            Anim2DBlendPicker anim2DBlendPicker = animNode.m_picker;
            Anim2DBlendPicker.PickResults pickResults = anim2DBlendPicker.Pick(float0, float1);
            Anim2DBlend anim2DBlend0 = pickResults.node1;
            Anim2DBlend anim2DBlend1 = pickResults.node2;
            Anim2DBlend anim2DBlend2 = pickResults.node3;
            if (Float.isNaN(pickResults.scale1)) {
                pickResults.scale1 = 0.5F;
            }

            if (Float.isNaN(pickResults.scale2)) {
                pickResults.scale2 = 0.5F;
            }

            if (Float.isNaN(pickResults.scale3)) {
                pickResults.scale3 = 0.5F;
            }

            float float2 = pickResults.scale1;
            float float3 = pickResults.scale2;
            float float4 = pickResults.scale3;

            for (int int0 = 0; int0 < liveAnimNode.m_AnimationTracks.size(); int0++) {
                Anim2DBlend anim2DBlend3 = animNode.m_2DBlends.get(int0);
                AnimationTrack animationTrack0 = liveAnimNode.m_AnimationTracks.get(int0);
                if (anim2DBlend3 == anim2DBlend0) {
                    animationTrack0.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack0.blendFieldWeight, float2, 0.15F);
                } else if (anim2DBlend3 == anim2DBlend1) {
                    animationTrack0.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack0.blendFieldWeight, float3, 0.15F);
                } else if (anim2DBlend3 == anim2DBlend2) {
                    animationTrack0.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack0.blendFieldWeight, float4, 0.15F);
                } else {
                    animationTrack0.blendFieldWeight = AnimationPlayer.lerpBlendWeight(animationTrack0.blendFieldWeight, 0.0F, 0.15F);
                }

                if (animationTrack0.blendFieldWeight < 1.0E-4F) {
                    animationTrack0.blendFieldWeight = 0.0F;
                }

                animationTrack0.blendFieldWeight = PZMath.clamp(animationTrack0.blendFieldWeight, 0.0F, 1.0F);
            }
        }

        float float5 = liveAnimNode.getWeight();

        for (int int1 = 0; int1 < liveAnimNode.m_AnimationTracks.size(); int1++) {
            AnimationTrack animationTrack1 = liveAnimNode.m_AnimationTracks.get(int1);
            animationTrack1.BlendDelta = animationTrack1.blendFieldWeight * float5;
        }
    }

    private void getOrCreateLiveNode(AnimNode animNode) {
        LiveAnimNode liveAnimNode = this.findLiveNode(animNode);
        if (liveAnimNode != null) {
            liveAnimNode.setActive(true);
        } else {
            liveAnimNode = LiveAnimNode.alloc(this, animNode, this.getDepth());
            if (animNode.m_2DBlends.size() > 0) {
                int int0 = 0;

                for (int int1 = animNode.m_2DBlends.size(); int0 < int1; int0++) {
                    Anim2DBlend anim2DBlend = animNode.m_2DBlends.get(int0);
                    this.startAnimTrack(anim2DBlend.m_AnimName, liveAnimNode);
                }
            } else {
                this.startAnimTrack(animNode.m_AnimName, liveAnimNode);
            }

            liveAnimNode.setActive(true);
            this.m_liveAnimNodes.add(liveAnimNode);
        }
    }

    private LiveAnimNode findLiveNode(AnimNode animNode) {
        LiveAnimNode liveAnimNode0 = null;
        int int0 = 0;

        for (int int1 = this.m_liveAnimNodes.size(); int0 < int1; int0++) {
            LiveAnimNode liveAnimNode1 = this.m_liveAnimNodes.get(int0);
            if (!liveAnimNode1.m_TransitioningOut) {
                if (liveAnimNode1.getSourceNode() == animNode) {
                    liveAnimNode0 = liveAnimNode1;
                    break;
                }

                if (liveAnimNode1.getSourceNode().m_State == animNode.m_State && liveAnimNode1.getSourceNode().m_Name.equals(animNode.m_Name)) {
                    liveAnimNode0 = liveAnimNode1;
                    break;
                }
            }
        }

        return liveAnimNode0;
    }

    private void startAnimTrack(String string, LiveAnimNode liveAnimNode) {
        AnimNode animNode = liveAnimNode.getSourceNode();
        float float0 = animNode.getSpeedScale(this.m_Character);
        float float1 = Rand.Next(0.0F, 1.0F);
        float float2 = animNode.m_SpeedScaleRandomMultiplierMin;
        float float3 = animNode.m_SpeedScaleRandomMultiplierMax;
        float float4 = PZMath.lerp(float2, float3, float1);
        AnimLayer.StartAnimTrackParameters startAnimTrackParameters = AnimLayer.StartAnimTrackParameters.alloc();
        startAnimTrackParameters.subLayerBoneWeights = animNode.m_SubStateBoneWeights;
        startAnimTrackParameters.syncTrackingEnabled = animNode.m_SyncTrackingEnabled;
        startAnimTrackParameters.speedScale = float0 * float4;
        startAnimTrackParameters.initialWeight = liveAnimNode.getWeight();
        startAnimTrackParameters.isLooped = liveAnimNode.isLooped();
        startAnimTrackParameters.isReversed = animNode.m_AnimReverse;
        startAnimTrackParameters.deferredBoneName = animNode.getDeferredBoneName();
        startAnimTrackParameters.deferredBoneAxis = animNode.getDeferredBoneAxis();
        startAnimTrackParameters.useDeferredRotation = animNode.m_useDeferedRotation;
        startAnimTrackParameters.priority = animNode.getPriority();
        AnimationTrack animationTrack = this.startAnimTrack(string, startAnimTrackParameters);
        startAnimTrackParameters.release();
        if (animationTrack != null) {
            animationTrack.addListener(liveAnimNode);
            liveAnimNode.addMainTrack(animationTrack);
        }
    }

    private AnimationTrack startAnimTrack(String string, AnimLayer.StartAnimTrackParameters startAnimTrackParameters) {
        AnimationPlayer animationPlayer = this.m_Character.getAnimationPlayer();
        if (!animationPlayer.isReady()) {
            return null;
        } else {
            AnimationTrack animationTrack = animationPlayer.play(string, startAnimTrackParameters.isLooped);
            if (animationTrack == null) {
                return null;
            } else {
                SkinningData skinningData = animationPlayer.getSkinningData();
                if (this.isSubLayer()) {
                    animationTrack.setBoneWeights(startAnimTrackParameters.subLayerBoneWeights);
                    animationTrack.initBoneWeights(skinningData);
                } else {
                    animationTrack.setBoneWeights(null);
                }

                SkinningBone skinningBone = skinningData.getBone(startAnimTrackParameters.deferredBoneName);
                if (skinningBone == null) {
                    DebugLog.Animation.error("Deferred bone not found: \"%s\"", startAnimTrackParameters.deferredBoneName);
                }

                animationTrack.SpeedDelta = startAnimTrackParameters.speedScale;
                animationTrack.SyncTrackingEnabled = startAnimTrackParameters.syncTrackingEnabled;
                animationTrack.setDeferredBone(skinningBone, startAnimTrackParameters.deferredBoneAxis);
                animationTrack.setUseDeferredRotation(startAnimTrackParameters.useDeferredRotation);
                animationTrack.BlendDelta = startAnimTrackParameters.initialWeight;
                animationTrack.setLayerIdx(this.getDepth());
                animationTrack.reverse = startAnimTrackParameters.isReversed;
                animationTrack.priority = startAnimTrackParameters.priority;
                animationTrack.addListener(this);
                return animationTrack;
            }
        }
    }

    /**
     * The layer's depth, how many layer ancestors (parent, grandparent, great-grandparent, etc) does this layer have.
     */
    public int getDepth() {
        return this.m_parentLayer != null ? this.m_parentLayer.getDepth() + 1 : 0;
    }

    private LiveAnimNode getHighestLiveNode() {
        if (this.m_liveAnimNodes.isEmpty()) {
            return null;
        } else {
            LiveAnimNode liveAnimNode0 = this.m_liveAnimNodes.get(0);

            for (int int0 = this.m_liveAnimNodes.size() - 1; int0 >= 0; int0--) {
                LiveAnimNode liveAnimNode1 = this.m_liveAnimNodes.get(int0);
                if (liveAnimNode1.getWeight() > liveAnimNode0.getWeight()) {
                    liveAnimNode0 = liveAnimNode1;
                }
            }

            return liveAnimNode0;
        }
    }

    private AnimationTrack findSyncTrack(LiveAnimNode liveAnimNode) {
        AnimationTrack animationTrack0 = null;
        if (this.m_parentLayer != null) {
            animationTrack0 = this.m_parentLayer.getCurrentSyncTrack();
            if (animationTrack0 != null) {
                return animationTrack0;
            }
        }

        int int0 = 0;

        for (int int1 = liveAnimNode.getPlayingTrackCount(); int0 < int1; int0++) {
            AnimationTrack animationTrack1 = liveAnimNode.getPlayingTrackAt(int0);
            if (animationTrack1.SyncTrackingEnabled
                && animationTrack1.hasClip()
                && (animationTrack0 == null || animationTrack1.BlendDelta > animationTrack0.BlendDelta)) {
                animationTrack0 = animationTrack1;
            }
        }

        return animationTrack0;
    }

    public String getDebugNodeName() {
        String string = this.m_Character.getAdvancedAnimator().animSet.m_Name;
        if (this.m_State != null) {
            string = string + "/" + this.m_State.m_Name;
            if (this.m_CurrentNode != null) {
                string = string + "/" + this.m_CurrentNode.m_Name + ": " + this.m_CurrentNode.m_AnimName;
            } else if (!this.m_liveAnimNodes.isEmpty()) {
                for (int int0 = 0; int0 < this.m_liveAnimNodes.size(); int0++) {
                    LiveAnimNode liveAnimNode = this.m_liveAnimNodes.get(int0);
                    if (this.m_State.m_Nodes.contains(liveAnimNode.getSourceNode())) {
                        string = string + "/" + liveAnimNode.getName();
                        break;
                    }
                }
            }
        }

        return string;
    }

    public List<LiveAnimNode> getLiveAnimNodes() {
        return this.m_liveAnimNodes;
    }

    public boolean isRecording() {
        return this.m_Character.getAdvancedAnimator().isRecording();
    }

    static {
        s_activeAnimLoopedEvent.m_TimePc = 1.0F;
        s_activeAnimLoopedEvent.m_EventName = "ActiveAnimLooped";
        s_activeNonLoopedAnimFadeOutEvent.m_TimePc = 1.0F;
        s_activeNonLoopedAnimFadeOutEvent.m_EventName = "NonLoopedAnimFadeOut";
        s_activeAnimFinishingEvent.m_Time = AnimEvent.AnimEventTime.End;
        s_activeAnimFinishingEvent.m_EventName = "ActiveAnimFinishing";
        s_activeNonLoopedAnimFinishedEvent.m_Time = AnimEvent.AnimEventTime.End;
        s_activeNonLoopedAnimFinishedEvent.m_EventName = "ActiveAnimFinished";
    }

    private static class StartAnimTrackParameters extends PooledObject {
        public int priority;
        List<AnimBoneWeight> subLayerBoneWeights;
        boolean syncTrackingEnabled;
        float speedScale;
        float initialWeight;
        boolean isLooped;
        boolean isReversed;
        String deferredBoneName;
        BoneAxis deferredBoneAxis;
        boolean useDeferredRotation;
        private static final Pool<AnimLayer.StartAnimTrackParameters> s_pool = new Pool<>(AnimLayer.StartAnimTrackParameters::new);

        private void reset() {
            this.priority = 0;
            this.subLayerBoneWeights = null;
            this.syncTrackingEnabled = false;
            this.speedScale = 1.0F;
            this.initialWeight = 0.0F;
            this.isLooped = false;
            this.isReversed = false;
            this.deferredBoneName = null;
            this.deferredBoneAxis = BoneAxis.Y;
            this.useDeferredRotation = false;
        }

        @Override
        public void onReleased() {
            this.reset();
        }

        protected StartAnimTrackParameters() {
        }

        public static AnimLayer.StartAnimTrackParameters alloc() {
            return s_pool.alloc();
        }
    }
}
