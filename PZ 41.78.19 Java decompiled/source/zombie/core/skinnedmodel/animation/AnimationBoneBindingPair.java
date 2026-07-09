// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.util.StringUtils;

public final class AnimationBoneBindingPair {
    public final AnimationBoneBinding boneBindingA;
    public final AnimationBoneBinding boneBindingB;

    public AnimationBoneBindingPair(String string0, String string1) {
        this.boneBindingA = new AnimationBoneBinding(string0);
        this.boneBindingB = new AnimationBoneBinding(string1);
    }

    public void setSkinningData(SkinningData skinningData) {
        this.boneBindingA.setSkinningData(skinningData);
        this.boneBindingB.setSkinningData(skinningData);
    }

    public SkinningBone getBoneA() {
        return this.boneBindingA.getBone();
    }

    public SkinningBone getBoneB() {
        return this.boneBindingB.getBone();
    }

    public boolean isValid() {
        return this.getBoneA() != null && this.getBoneB() != null;
    }

    public boolean matches(String string1, String string0) {
        return StringUtils.equalsIgnoreCase(this.boneBindingA.boneName, string1) && StringUtils.equalsIgnoreCase(this.boneBindingB.boneName, string0);
    }

    public int getBoneIdxA() {
        return getBoneIdx(this.getBoneA());
    }

    public int getBoneIdxB() {
        return getBoneIdx(this.getBoneB());
    }

    private static int getBoneIdx(SkinningBone skinningBone) {
        return skinningBone != null ? skinningBone.Index : -1;
    }

    @Override
    public String toString() {
        String string = System.lineSeparator();
        return this.getClass().getName()
            + string
            + "{"
            + string
            + "\tboneBindingA:"
            + StringUtils.indent(String.valueOf(this.boneBindingA))
            + string
            + "\tboneBindingB:"
            + StringUtils.indent(String.valueOf(this.boneBindingB))
            + string
            + "}";
    }
}
