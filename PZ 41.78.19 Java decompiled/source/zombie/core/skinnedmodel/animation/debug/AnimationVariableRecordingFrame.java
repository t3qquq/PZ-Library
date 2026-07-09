// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;

public final class AnimationVariableRecordingFrame extends GenericNameValueRecordingFrame {
    private String[] m_variableValues = new String[0];

    public AnimationVariableRecordingFrame(String fileKey) {
        super(fileKey, "_values");
    }

    public void logVariables(IAnimationVariableSource varSource) {
        for (IAnimationVariableSlot iAnimationVariableSlot : varSource.getGameVariables()) {
            this.logVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
        }
    }

    @Override
    protected void onColumnAdded() {
        this.m_variableValues = PZArrayUtil.add(this.m_variableValues, null);
    }

    public void logVariable(String name, String val) {
        int int0 = this.getOrCreateColumn(name);
        if (this.m_variableValues[int0] != null) {
            DebugLog.General.error("Value for %s already set: %f, new value: %f", name, this.m_variableValues[int0], val);
        }

        this.m_variableValues[int0] = val;
    }

    @Override
    public String getValueAt(int i) {
        return this.m_variableValues[i];
    }

    @Override
    public void reset() {
        int int0 = 0;

        for (int int1 = this.m_variableValues.length; int0 < int1; int0++) {
            this.m_variableValues[int0] = null;
        }
    }
}
