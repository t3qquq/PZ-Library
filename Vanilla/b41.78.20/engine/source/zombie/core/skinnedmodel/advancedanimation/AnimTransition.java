// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class AnimTransition {
    public String m_Target;
    public String m_AnimName;
    public float m_SyncAdjustTime = 0.0F;
    public float m_blendInTime = Float.POSITIVE_INFINITY;
    public float m_blendOutTime = Float.POSITIVE_INFINITY;
    public float m_speedScale = Float.POSITIVE_INFINITY;
    public List<AnimCondition> m_Conditions = new ArrayList<>();
}
