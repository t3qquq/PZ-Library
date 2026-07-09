// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import java.util.ArrayList;
import java.util.List;
import zombie.ai.State;
import zombie.ai.StateMachine;
import zombie.characters.action.ActionState;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.iso.Vector3;
import zombie.util.list.PZArrayUtil;

public final class AnimationNodeRecordingFrame extends GenericNameWeightRecordingFrame {
    private String m_actionStateName;
    private final ArrayList<String> m_actionSubStateNames = new ArrayList<>();
    private String m_aiStateName;
    private String m_animStateName;
    private final ArrayList<String> m_animSubStateNames = new ArrayList<>();
    private final ArrayList<String> m_aiSubStateNames = new ArrayList<>();
    private final Vector3 m_characterToPlayerDiff = new Vector3();

    public AnimationNodeRecordingFrame(String fileKey) {
        super(fileKey);
    }

    public void logActionState(ActionState state, List<ActionState> childStates) {
        this.m_actionStateName = state != null ? state.getName() : null;
        PZArrayUtil.arrayConvert(this.m_actionSubStateNames, childStates, ActionState::getName);
    }

    public void logAIState(State state, List<StateMachine.SubstateSlot> subStates) {
        this.m_aiStateName = state != null ? state.getName() : null;
        PZArrayUtil.arrayConvert(this.m_aiSubStateNames, subStates, substateSlot -> !substateSlot.isEmpty() ? substateSlot.getState().getName() : "");
    }

    public void logAnimState(AnimState state) {
        this.m_animStateName = state != null ? state.m_Name : null;
    }

    public void logCharacterToPlayerDiff(Vector3 diff) {
        this.m_characterToPlayerDiff.set(diff);
    }

    @Override
    public void writeHeader(StringBuilder logLine) {
        appendCell(logLine, "toPlayer.x");
        appendCell(logLine, "toPlayer.y");
        appendCell(logLine, "actionState");
        appendCell(logLine, "actionState.sub[0]");
        appendCell(logLine, "actionState.sub[1]");
        appendCell(logLine, "aiState");
        appendCell(logLine, "aiState.sub[0]");
        appendCell(logLine, "aiState.sub[1]");
        appendCell(logLine, "animState");
        appendCell(logLine, "animState.sub[0]");
        appendCell(logLine, "animState.sub[1]");
        appendCell(logLine, "nodeWeights.begin");
        super.writeHeader(logLine);
    }

    @Override
    protected void writeData(StringBuilder stringBuilder) {
        appendCell(stringBuilder, this.m_characterToPlayerDiff.x);
        appendCell(stringBuilder, this.m_characterToPlayerDiff.y);
        appendCellQuot(stringBuilder, this.m_actionStateName);
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_actionSubStateNames, 0, ""));
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_actionSubStateNames, 1, ""));
        appendCellQuot(stringBuilder, this.m_aiStateName);
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_aiSubStateNames, 0, ""));
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_aiSubStateNames, 1, ""));
        appendCellQuot(stringBuilder, this.m_animStateName);
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_animSubStateNames, 0, ""));
        appendCellQuot(stringBuilder, PZArrayUtil.getOrDefault(this.m_animSubStateNames, 1, ""));
        appendCell(stringBuilder);
        super.writeData(stringBuilder);
    }
}
