// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlIDREF;

public final class Anim2DBlendTriangle {
    @XmlIDREF
    public Anim2DBlend node1;
    @XmlIDREF
    public Anim2DBlend node2;
    @XmlIDREF
    public Anim2DBlend node3;

    public static double sign(float float5, float float0, float float2, float float4, float float3, float float1) {
        return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
    }

    static boolean PointInTriangle(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
        boolean boolean0 = sign(float0, float1, float2, float3, float4, float5) < 0.0;
        boolean boolean1 = sign(float0, float1, float4, float5, float6, float7) < 0.0;
        boolean boolean2 = sign(float0, float1, float6, float7, float2, float3) < 0.0;
        return boolean0 == boolean1 && boolean1 == boolean2;
    }

    public boolean Contains(float float0, float float1) {
        return PointInTriangle(float0, float1, this.node1.m_XPos, this.node1.m_YPos, this.node2.m_XPos, this.node2.m_YPos, this.node3.m_XPos, this.node3.m_YPos);
    }
}
