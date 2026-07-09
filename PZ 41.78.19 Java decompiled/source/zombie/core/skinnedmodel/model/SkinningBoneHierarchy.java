// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.Map.Entry;
import zombie.util.list.PZArrayUtil;

public final class SkinningBoneHierarchy {
    private boolean m_boneHieararchyValid = false;
    private SkinningBone[] m_allBones = null;
    private SkinningBone[] m_rootBones = null;

    public boolean isValid() {
        return this.m_boneHieararchyValid;
    }

    public void buildBoneHiearchy(SkinningData data) {
        this.m_rootBones = new SkinningBone[0];
        this.m_allBones = new SkinningBone[data.numBones()];
        PZArrayUtil.arrayPopulate(this.m_allBones, SkinningBone::new);

        for (Entry entry : data.BoneIndices.entrySet()) {
            int int0 = (Integer)entry.getValue();
            String string = (String)entry.getKey();
            SkinningBone skinningBone0 = this.m_allBones[int0];
            skinningBone0.Index = int0;
            skinningBone0.Name = string;
            skinningBone0.Children = new SkinningBone[0];
        }

        for (int int1 = 0; int1 < data.numBones(); int1++) {
            SkinningBone skinningBone1 = this.m_allBones[int1];
            int int2 = data.getParentBoneIdx(int1);
            if (int2 > -1) {
                skinningBone1.Parent = this.m_allBones[int2];
                skinningBone1.Parent.Children = PZArrayUtil.add(skinningBone1.Parent.Children, skinningBone1);
            } else {
                this.m_rootBones = PZArrayUtil.add(this.m_rootBones, skinningBone1);
            }
        }

        this.m_boneHieararchyValid = true;
    }

    public int numRootBones() {
        return this.m_rootBones.length;
    }

    public SkinningBone getBoneAt(int boneIdx) {
        return this.m_allBones[boneIdx];
    }

    public SkinningBone getRootBoneAt(int idx) {
        return this.m_rootBones[idx];
    }
}
