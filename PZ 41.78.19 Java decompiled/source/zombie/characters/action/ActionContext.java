// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.action.conditions.CharacterVariableCondition;
import zombie.characters.action.conditions.EventNotOccurred;
import zombie.characters.action.conditions.EventOccurred;
import zombie.characters.action.conditions.LuaCall;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.GameClient;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public final class ActionContext {
    private final IAnimatable m_owner;
    private ActionGroup m_stateGroup;
    private ActionState m_currentState;
    private final ArrayList<ActionState> m_childStates = new ArrayList<>();
    private String m_previousStateName = null;
    private boolean m_statesChanged = false;
    public final ArrayList<IActionStateChanged> onStateChanged = new ArrayList<>();
    private final ActionContextEvents occurredEvents = new ActionContextEvents();

    public ActionContext(IAnimatable owner) {
        this.m_owner = owner;
    }

    public IAnimatable getOwner() {
        return this.m_owner;
    }

    public void update() {
        ActionContext.s_performance.update.invokeAndMeasure(this, ActionContext::updateInternal);
    }

    private void updateInternal() {
        if (this.m_currentState == null) {
            this.logCurrentState();
        } else {
            ActionContext.s_performance.evaluateCurrentStateTransitions.invokeAndMeasure(this, ActionContext::evaluateCurrentStateTransitions);
            ActionContext.s_performance.evaluateSubStateTransitions.invokeAndMeasure(this, ActionContext::evaluateSubStateTransitions);
            this.invokeAnyStateChangedEvents();
            this.logCurrentState();
        }
    }

    public ActionState getNextState() {
        ActionState actionState0 = null;

        for (int int0 = 0; int0 < this.m_currentState.transitions.size(); int0++) {
            ActionTransition actionTransition0 = this.m_currentState.transitions.get(int0);
            if (actionTransition0.passes(this, 0) && !StringUtils.isNullOrWhitespace(actionTransition0.transitionTo)) {
                ActionState actionState1 = this.m_stateGroup.get(actionTransition0.transitionTo);
                if (actionState1 != null && !this.hasChildState(actionState1)) {
                    if (!actionTransition0.asSubstate || !this.currentStateSupportsChildState(actionState1)) {
                        actionState0 = actionState1;
                        break;
                    }

                    this.tryAddChildState(actionState1);
                }
            }
        }

        for (int int1 = 0; int1 < this.childStateCount(); int1++) {
            ActionState actionState2 = null;
            ActionState actionState3 = this.getChildStateAt(int1);

            for (int int2 = 0; int2 < actionState3.transitions.size(); int2++) {
                ActionTransition actionTransition1 = actionState3.transitions.get(int2);
                if (actionTransition1.passes(this, 1)) {
                    if (actionTransition1.transitionOut) {
                        int1--;
                        break;
                    }

                    if (!StringUtils.isNullOrWhitespace(actionTransition1.transitionTo)) {
                        ActionState actionState4 = this.m_stateGroup.get(actionTransition1.transitionTo);
                        if (actionState4 != null && !this.hasChildState(actionState4)) {
                            if (this.currentStateSupportsChildState(actionState4)) {
                                break;
                            }

                            if (actionTransition1.forceParent) {
                                actionState2 = actionState4;
                                break;
                            }
                        }
                    }
                }
            }

            if (actionState2 != this.m_currentState && actionState2 != null) {
                actionState0 = actionState2;
            }
        }

        return actionState0;
    }

    private void evaluateCurrentStateTransitions() {
        for (int int0 = 0; int0 < this.m_currentState.transitions.size(); int0++) {
            ActionTransition actionTransition = this.m_currentState.transitions.get(int0);
            if (actionTransition.passes(this, 0)) {
                if (StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
                    DebugLog.ActionSystem.warn("%s> Transition's target state not specified: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                } else {
                    ActionState actionState = this.m_stateGroup.get(actionTransition.transitionTo);
                    if (actionState == null) {
                        DebugLog.ActionSystem.warn("%s> Transition's target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                    } else if (!this.hasChildState(actionState)) {
                        if (!actionTransition.asSubstate || !this.currentStateSupportsChildState(actionState)) {
                            if (this.m_owner instanceof IsoPlayer) {
                                DebugLog.log(
                                    DebugType.ActionSystem,
                                    "Player '"
                                        + ((IsoPlayer)this.m_owner).getUsername()
                                        + "' transits from "
                                        + this.m_currentState.getName()
                                        + " to "
                                        + actionTransition.transitionTo
                                );
                            }

                            this.setCurrentState(actionState);
                            break;
                        }

                        this.tryAddChildState(actionState);
                    }
                }
            }
        }
    }

    private void evaluateSubStateTransitions() {
        for (int int0 = 0; int0 < this.childStateCount(); int0++) {
            ActionState actionState0 = null;
            ActionState actionState1 = this.getChildStateAt(int0);

            for (int int1 = 0; int1 < actionState1.transitions.size(); int1++) {
                ActionTransition actionTransition = actionState1.transitions.get(int1);
                if (actionTransition.passes(this, 1)) {
                    if (actionTransition.transitionOut) {
                        this.removeChildStateAt(int0);
                        int0--;
                        break;
                    }

                    if (!StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
                        ActionState actionState2 = this.m_stateGroup.get(actionTransition.transitionTo);
                        if (actionState2 == null) {
                            DebugLog.ActionSystem
                                .warn("%s> Transition's target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
                        } else if (!this.hasChildState(actionState2)) {
                            if (this.currentStateSupportsChildState(actionState2)) {
                                this.m_childStates.set(int0, actionState2);
                                this.onStatesChanged();
                                break;
                            }

                            if (actionTransition.forceParent) {
                                actionState0 = actionState2;
                                break;
                            }
                        }
                    }
                }
            }

            if (actionState0 != this.m_currentState && actionState0 != null) {
                this.setCurrentState(actionState0);
            }
        }
    }

    protected boolean currentStateSupportsChildState(ActionState actionState) {
        return this.m_currentState == null ? false : this.m_currentState.canHaveSubState(actionState);
    }

    private boolean hasChildState(ActionState actionState) {
        int int0 = this.indexOfChildState(actionState0 -> actionState0 == actionState);
        return int0 > -1;
    }

    public void setPlaybackStateSnapshot(ActionStateSnapshot snapshot) {
        if (this.m_stateGroup != null) {
            if (snapshot.stateName == null) {
                DebugLog.General.warn("Snapshot not valid. Missing root state name.");
            } else {
                ActionState actionState0 = this.m_stateGroup.get(snapshot.stateName);
                this.setCurrentState(actionState0);
                if (PZArrayUtil.isNullOrEmpty(snapshot.childStateNames)) {
                    while (this.childStateCount() > 0) {
                        this.removeChildStateAt(0);
                    }
                } else {
                    for (int int0 = 0; int0 < this.childStateCount(); int0++) {
                        String string0 = this.getChildStateAt(int0).name;
                        boolean boolean0 = StringUtils.contains(snapshot.childStateNames, string0, StringUtils::equalsIgnoreCase);
                        if (!boolean0) {
                            this.removeChildStateAt(int0);
                            int0--;
                        }
                    }

                    for (int int1 = 0; int1 < snapshot.childStateNames.length; int1++) {
                        String string1 = snapshot.childStateNames[int1];
                        ActionState actionState1 = this.m_stateGroup.get(string1);
                        this.tryAddChildState(actionState1);
                    }
                }
            }
        }
    }

    public ActionStateSnapshot getPlaybackStateSnapshot() {
        if (this.m_currentState == null) {
            return null;
        } else {
            ActionStateSnapshot actionStateSnapshot = new ActionStateSnapshot();
            actionStateSnapshot.stateName = this.m_currentState.name;
            actionStateSnapshot.childStateNames = new String[this.m_childStates.size()];

            for (int int0 = 0; int0 < actionStateSnapshot.childStateNames.length; int0++) {
                actionStateSnapshot.childStateNames[int0] = this.m_childStates.get(int0).name;
            }

            return actionStateSnapshot;
        }
    }

    protected boolean setCurrentState(ActionState actionState0) {
        if (actionState0 == this.m_currentState) {
            return false;
        } else {
            this.m_previousStateName = this.m_currentState == null ? "" : this.m_currentState.getName();
            this.m_currentState = actionState0;

            for (int int0 = 0; int0 < this.m_childStates.size(); int0++) {
                ActionState actionState1 = this.m_childStates.get(int0);
                if (!this.m_currentState.canHaveSubState(actionState1)) {
                    this.removeChildStateAt(int0);
                    int0--;
                }
            }

            this.onStatesChanged();
            return true;
        }
    }

    protected boolean tryAddChildState(ActionState actionState) {
        if (this.hasChildState(actionState)) {
            return false;
        } else {
            this.m_childStates.add(actionState);
            this.onStatesChanged();
            return true;
        }
    }

    protected void removeChildStateAt(int int0) {
        this.m_childStates.remove(int0);
        this.onStatesChanged();
    }

    private void onStatesChanged() {
        this.m_statesChanged = true;
    }

    public void logCurrentState() {
        if (this.m_owner.isAnimationRecorderActive()) {
            this.m_owner.getAnimationPlayerRecorder().logActionState(this.m_currentState, this.m_childStates);
        }
    }

    private void invokeAnyStateChangedEvents() {
        if (this.m_statesChanged) {
            this.m_statesChanged = false;
            this.occurredEvents.clear();

            for (int int0 = 0; int0 < this.onStateChanged.size(); int0++) {
                IActionStateChanged iActionStateChanged = this.onStateChanged.get(int0);
                iActionStateChanged.actionStateChanged(this);
            }

            if (this.m_owner instanceof IsoZombie) {
                ((IsoZombie)this.m_owner).networkAI.extraUpdate();
            }
        }
    }

    public ActionState getCurrentState() {
        return this.m_currentState;
    }

    public void setGroup(ActionGroup group) {
        String string = this.m_currentState == null ? null : this.m_currentState.name;
        this.m_stateGroup = group;
        ActionState actionState = group.getInitialState();
        if (!StringUtils.equalsIgnoreCase(string, actionState.name)) {
            this.setCurrentState(actionState);
        } else {
            this.m_currentState = actionState;
        }
    }

    public ActionGroup getGroup() {
        return this.m_stateGroup;
    }

    public void reportEvent(String event) {
        this.reportEvent(-1, event);
    }

    public void reportEvent(int animLayer, String event) {
        this.occurredEvents.add(event, animLayer);
        if (GameClient.bClient && animLayer == -1 && this.m_owner instanceof IsoPlayer && ((IsoPlayer)this.m_owner).isLocalPlayer()) {
            GameClient.sendEvent((IsoPlayer)this.m_owner, event);
        }
    }

    public final boolean hasChildStates() {
        return this.childStateCount() > 0;
    }

    public final int childStateCount() {
        return this.m_childStates != null ? this.m_childStates.size() : 0;
    }

    public final void foreachChildState(Consumer<ActionState> consumer) {
        for (int int0 = 0; int0 < this.childStateCount(); int0++) {
            ActionState actionState = this.getChildStateAt(int0);
            consumer.accept(actionState);
        }
    }

    public final int indexOfChildState(Predicate<ActionState> predicate) {
        int int0 = -1;

        for (int int1 = 0; int1 < this.childStateCount(); int1++) {
            ActionState actionState = this.getChildStateAt(int1);
            if (predicate.test(actionState)) {
                int0 = int1;
                break;
            }
        }

        return int0;
    }

    public final ActionState getChildStateAt(int idx) {
        if (idx >= 0 && idx < this.childStateCount()) {
            return this.m_childStates.get(idx);
        } else {
            throw new IndexOutOfBoundsException(String.format("Index %d out of bounds. childCount: %d", idx, this.childStateCount()));
        }
    }

    public List<ActionState> getChildStates() {
        return this.m_childStates;
    }

    public String getCurrentStateName() {
        return this.m_currentState.name;
    }

    public String getPreviousStateName() {
        return this.m_previousStateName;
    }

    /**
     * Returns TRUE if an event has occurred on any layer.
     */
    public boolean hasEventOccurred(String eventName) {
        return this.hasEventOccurred(eventName, -1);
    }

    /**
     * Returns TRUE if an event has occurred on the specified layer.
     */
    public boolean hasEventOccurred(String eventName, int layerIdx) {
        return this.occurredEvents.contains(eventName, layerIdx);
    }

    public void clearEvent(String eventName) {
        this.occurredEvents.clearEvent(eventName);
    }

    static {
        CharacterVariableCondition.Factory factory = new CharacterVariableCondition.Factory();
        IActionCondition.registerFactory("isTrue", factory);
        IActionCondition.registerFactory("isFalse", factory);
        IActionCondition.registerFactory("compare", factory);
        IActionCondition.registerFactory("gtr", factory);
        IActionCondition.registerFactory("less", factory);
        IActionCondition.registerFactory("equals", factory);
        IActionCondition.registerFactory("lessEqual", factory);
        IActionCondition.registerFactory("gtrEqual", factory);
        IActionCondition.registerFactory("notEquals", factory);
        IActionCondition.registerFactory("eventOccurred", new EventOccurred.Factory());
        IActionCondition.registerFactory("eventNotOccurred", new EventNotOccurred.Factory());
        IActionCondition.registerFactory("lua", new LuaCall.Factory());
    }

    private static class s_performance {
        static final PerformanceProfileProbe update = new PerformanceProfileProbe("ActionContext.update");
        static final PerformanceProfileProbe evaluateCurrentStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateCurrentStateTransitions");
        static final PerformanceProfileProbe evaluateSubStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateSubStateTransitions");
    }
}
