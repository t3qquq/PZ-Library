// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

public class AnimationVariableHandle {
    private String m_name = null;
    private int m_varIndex = -1;

    AnimationVariableHandle() {
    }

    public static AnimationVariableHandle alloc(String name) {
        return AnimationVariableHandlePool.getOrCreate(name);
    }

    public String getVariableName() {
        return this.m_name;
    }

    public int getVariableIndex() {
        return this.m_varIndex;
    }

    void setVariableName(String string) {
        this.m_name = string;
    }

    void setVariableIndex(int int0) {
        this.m_varIndex = int0;
    }
}
