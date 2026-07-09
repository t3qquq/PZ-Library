// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

@XmlRootElement
public final class AnimNode {
    private static final Comparator<AnimEvent> s_eventsComparator = (animEvent1, animEvent0) -> Float.compare(animEvent1.m_TimePc, animEvent0.m_TimePc);
    public String m_Name = "";
    public int m_Priority = 5;
    public String m_AnimName = "";
    public String m_DeferredBoneName = "";
    public BoneAxis m_deferredBoneAxis = BoneAxis.Y;
    public boolean m_useDeferedRotation = false;
    public boolean m_Looped = true;
    public float m_BlendTime = 0.0F;
    public float m_BlendOutTime = -1.0F;
    public boolean m_StopAnimOnExit = false;
    public boolean m_EarlyTransitionOut = false;
    public String m_SpeedScale = "1.00";
    public String m_SpeedScaleVariable = null;
    public float m_SpeedScaleRandomMultiplierMin = 1.0F;
    public float m_SpeedScaleRandomMultiplierMax = 1.0F;
    @XmlTransient
    private float m_SpeedScaleF = Float.POSITIVE_INFINITY;
    public float m_randomAdvanceFraction = 0.0F;
    public float m_maxTorsoTwist = 15.0F;
    public String m_Scalar = "";
    public String m_Scalar2 = "";
    public boolean m_AnimReverse = false;
    public boolean m_SyncTrackingEnabled = true;
    public List<Anim2DBlend> m_2DBlends = new ArrayList<>();
    public List<AnimCondition> m_Conditions = new ArrayList<>();
    public List<AnimEvent> m_Events = new ArrayList<>();
    public List<Anim2DBlendTriangle> m_2DBlendTri = new ArrayList<>();
    public List<AnimTransition> m_Transitions = new ArrayList<>();
    public List<AnimBoneWeight> m_SubStateBoneWeights = new ArrayList<>();
    @XmlTransient
    public Anim2DBlendPicker m_picker;
    @XmlTransient
    public AnimState m_State = null;
    @XmlTransient
    private AnimTransition m_transitionOut;

    /**
     * Loads an AnimNode from the specified source.  The source can either be a file path, or a File GUID.
     * @return The deserialized AnimNode instance, or NULL if failed.
     */
    public static AnimNode Parse(String source) {
        try {
            AnimNode animNode = PZXmlUtil.parse(AnimNode.class, source);
            if (animNode.m_2DBlendTri.size() > 0) {
                animNode.m_picker = new Anim2DBlendPicker();
                animNode.m_picker.SetPickTriangles(animNode.m_2DBlendTri);
            }

            PZArrayUtil.forEach(animNode.m_Events, animEvent -> {
                if ("SetVariable".equalsIgnoreCase(animEvent.m_EventName)) {
                    String[] strings = animEvent.m_ParameterValue.split("=");
                    if (strings.length == 2) {
                        animEvent.m_SetVariable1 = strings[0];
                        animEvent.m_SetVariable2 = strings[1];
                    }
                }
            });
            animNode.m_Events.sort(s_eventsComparator);

            try {
                animNode.m_SpeedScaleF = Float.parseFloat(animNode.m_SpeedScale);
            } catch (NumberFormatException numberFormatException) {
                animNode.m_SpeedScaleVariable = animNode.m_SpeedScale;
            }

            if (animNode.m_SubStateBoneWeights.isEmpty()) {
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Spine1", 0.5F));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Neck", 1.0F));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_BackPack", 1.0F));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop1", 1.0F));
                animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop2", 1.0F));
            }

            for (int int0 = 0; int0 < animNode.m_SubStateBoneWeights.size(); int0++) {
                AnimBoneWeight animBoneWeight = animNode.m_SubStateBoneWeights.get(int0);
                animBoneWeight.boneName = JAssImpImporter.getSharedString(animBoneWeight.boneName, "AnimBoneWeight.boneName");
            }

            animNode.m_transitionOut = null;

            for (int int1 = 0; int1 < animNode.m_Transitions.size(); int1++) {
                AnimTransition animTransition = animNode.m_Transitions.get(int1);
                if (StringUtils.isNullOrWhitespace(animTransition.m_Target)) {
                    animNode.m_transitionOut = animTransition;
                }
            }

            return animNode;
        } catch (PZXmlParserException pZXmlParserException) {
            System.err.println("AnimNode.Parse threw an exception reading file: " + source);
            ExceptionLogger.logException(pZXmlParserException);
            return null;
        }
    }

    public boolean checkConditions(IAnimationVariableSource varSource) {
        List list = this.m_Conditions;
        return AnimCondition.pass(varSource, list);
    }

    public float getSpeedScale(IAnimationVariableSource varSource) {
        return this.m_SpeedScaleF != Float.POSITIVE_INFINITY ? this.m_SpeedScaleF : varSource.getVariableFloat(this.m_SpeedScale, 1.0F);
    }

    /**
     * Returns TRUE if this AnimNode represents an Idle animation.  TODO: Make this a flag in the AnimNode, instead of relying on the name
     */
    public boolean isIdleAnim() {
        return this.m_Name.contains("Idle");
    }

    public AnimTransition findTransitionTo(IAnimationVariableSource varSource, String targetName) {
        AnimTransition animTransition0 = null;
        int int0 = 0;

        for (int int1 = this.m_Transitions.size(); int0 < int1; int0++) {
            AnimTransition animTransition1 = this.m_Transitions.get(int0);
            if (StringUtils.equalsIgnoreCase(animTransition1.m_Target, targetName) && AnimCondition.pass(varSource, animTransition1.m_Conditions)) {
                animTransition0 = animTransition1;
                break;
            }
        }

        return animTransition0;
    }

    @Override
    public String toString() {
        return String.format("AnimNode{ Name: %s, AnimName: %s, Conditions: %s }", this.m_Name, this.m_AnimName, this.getConditionsString());
    }

    public String getConditionsString() {
        return PZArrayUtil.arrayToString(this.m_Conditions, AnimCondition::getConditionString, "( ", " )", ", ");
    }

    public boolean isAbstract() {
        return !StringUtils.isNullOrWhitespace(this.m_AnimName) ? false : this.m_2DBlends.isEmpty();
    }

    public float getBlendOutTime() {
        if (this.m_transitionOut != null) {
            return this.m_transitionOut.m_blendOutTime;
        } else {
            return this.m_BlendOutTime >= 0.0F ? this.m_BlendOutTime : this.m_BlendTime;
        }
    }

    public String getDeferredBoneName() {
        return StringUtils.isNullOrWhitespace(this.m_DeferredBoneName) ? "Translation_Data" : this.m_DeferredBoneName;
    }

    public BoneAxis getDeferredBoneAxis() {
        return this.m_deferredBoneAxis;
    }

    public int getPriority() {
        return this.m_Priority;
    }
}
