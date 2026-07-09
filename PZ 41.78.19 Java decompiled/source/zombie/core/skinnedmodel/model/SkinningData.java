// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.skinnedmodel.animation.AnimationClip;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class SkinningData {
    public HashMap<String, AnimationClip> AnimationClips;
    public List<Matrix4f> BindPose;
    public List<Matrix4f> InverseBindPose;
    public List<Matrix4f> BoneOffset = new ArrayList<>();
    public List<Integer> SkeletonHierarchy;
    public HashMap<String, Integer> BoneIndices;
    private SkinningBoneHierarchy m_boneHieararchy = null;

    public SkinningData(
        HashMap<String, AnimationClip> animationClips,
        List<Matrix4f> bindPose,
        List<Matrix4f> inverseBindPose,
        List<Matrix4f> skinOffset,
        List<Integer> skeletonHierarchy,
        HashMap<String, Integer> boneIndices
    ) {
        this.AnimationClips = animationClips;
        this.BindPose = bindPose;
        this.InverseBindPose = inverseBindPose;
        this.SkeletonHierarchy = skeletonHierarchy;

        for (int int0 = 0; int0 < skeletonHierarchy.size(); int0++) {
            Matrix4f matrix4f = (Matrix4f)skinOffset.get(int0);
            this.BoneOffset.add(matrix4f);
        }

        this.BoneIndices = boneIndices;
    }

    private void validateBoneHierarchy() {
        if (this.m_boneHieararchy == null) {
            this.m_boneHieararchy = new SkinningBoneHierarchy();
            this.m_boneHieararchy.buildBoneHiearchy(this);
        }
    }

    public int numBones() {
        return this.SkeletonHierarchy.size();
    }

    public int numRootBones() {
        return this.getBoneHieararchy().numRootBones();
    }

    public int getParentBoneIdx(int boneIdx) {
        return this.SkeletonHierarchy.get(boneIdx);
    }

    public SkinningBone getBoneAt(int boneIdx) {
        return this.getBoneHieararchy().getBoneAt(boneIdx);
    }

    public SkinningBone getBone(String boneName) {
        Integer integer = this.BoneIndices.get(boneName);
        return integer == null ? null : this.getBoneAt(integer);
    }

    public SkinningBone getRootBoneAt(int idx) {
        return this.getBoneHieararchy().getRootBoneAt(idx);
    }

    public SkinningBoneHierarchy getBoneHieararchy() {
        this.validateBoneHierarchy();
        return this.m_boneHieararchy;
    }
}
