// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.function.Consumer;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.model.SkinningBone;

public class AnimationBoneWeightBinding extends AnimationBoneBinding {
    private float m_weight = 1.0F;
    private boolean m_includeDescendants = true;

    public AnimationBoneWeightBinding(AnimBoneWeight animBoneWeight) {
        this(animBoneWeight.boneName, animBoneWeight.weight, animBoneWeight.includeDescendants);
    }

    public AnimationBoneWeightBinding(String string, float float0, boolean boolean0) {
        super(string);
        this.m_weight = float0;
        this.m_includeDescendants = boolean0;
    }

    public float getWeight() {
        return this.m_weight;
    }

    public void setWeight(float float0) {
        this.m_weight = float0;
    }

    public boolean getIncludeDescendants() {
        return this.m_includeDescendants;
    }

    public void setIncludeDescendants(boolean boolean0) {
        this.m_includeDescendants = boolean0;
    }

    public void forEachDescendant(Consumer<SkinningBone> consumer) {
        if (this.m_includeDescendants) {
            SkinningBone skinningBone = this.getBone();
            if (skinningBone != null) {
                skinningBone.forEachDescendant(consumer);
            }
        }
    }
}
