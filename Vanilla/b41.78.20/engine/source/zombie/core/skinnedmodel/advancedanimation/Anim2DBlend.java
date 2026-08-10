// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

public final class Anim2DBlend {
    public String m_AnimName = "";
    public float m_XPos = 0.0F;
    public float m_YPos = 0.0F;
    public float m_SpeedScale = 1.0F;
    @XmlAttribute(
        name = "referenceID"
    )
    @XmlID
    public String m_referenceID;
}
