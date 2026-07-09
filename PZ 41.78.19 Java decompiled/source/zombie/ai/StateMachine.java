// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import java.util.ArrayList;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;

public final class StateMachine {
    private boolean m_isLocked = false;
    public int activeStateChanged = 0;
    private State m_currentState;
    private State m_previousState;
    private final IsoGameCharacter m_owner;
    private final List<StateMachine.SubstateSlot> m_subStates = new ArrayList<>();

    public StateMachine(IsoGameCharacter owner) {
        this.m_owner = owner;
    }

    public void changeState(State newState, Iterable<State> subStates) {
        this.changeState(newState, subStates, false);
    }

    public void changeState(State newState, Iterable<State> subStates, boolean restart) {
        if (!this.m_isLocked) {
            this.changeRootState(newState, restart);
            PZArrayUtil.forEach(this.m_subStates, substateSlot -> substateSlot.shouldBeActive = false);
            PZArrayUtil.forEach(subStates, Lambda.consumer(this, (state, stateMachine) -> {
                if (state != null) {
                    stateMachine.ensureSubstateActive(state);
                }
            }));
            Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this, (substateSlot, stateMachine) -> {
                if (!substateSlot.shouldBeActive && !substateSlot.isEmpty()) {
                    stateMachine.removeSubstate(substateSlot);
                }
            });
        }
    }

    private void changeRootState(State state0, boolean boolean0) {
        if (this.m_currentState == state0) {
            if (boolean0) {
                this.stateEnter(this.m_currentState);
            }
        } else {
            State state1 = this.m_currentState;
            if (state1 != null) {
                this.stateExit(state1);
            }

            this.m_previousState = state1;
            this.m_currentState = state0;
            if (state0 != null) {
                this.stateEnter(state0);
            }

            LuaEventManager.triggerEvent("OnAIStateChange", this.m_owner, this.m_currentState, this.m_previousState);
        }
    }

    private void ensureSubstateActive(State state) {
        StateMachine.SubstateSlot substateSlot0 = this.getExistingSlot(state);
        if (substateSlot0 != null) {
            substateSlot0.shouldBeActive = true;
        } else {
            StateMachine.SubstateSlot substateSlot1 = PZArrayUtil.find(this.m_subStates, StateMachine.SubstateSlot::isEmpty);
            if (substateSlot1 != null) {
                substateSlot1.setState(state);
                substateSlot1.shouldBeActive = true;
            } else {
                StateMachine.SubstateSlot substateSlot2 = new StateMachine.SubstateSlot(state);
                this.m_subStates.add(substateSlot2);
            }

            this.stateEnter(state);
        }
    }

    private StateMachine.SubstateSlot getExistingSlot(State state) {
        return PZArrayUtil.find(this.m_subStates, Lambda.predicate(state, (substateSlot, statex) -> substateSlot.getState() == statex));
    }

    private void removeSubstate(State state) {
        StateMachine.SubstateSlot substateSlot = this.getExistingSlot(state);
        if (substateSlot != null) {
            this.removeSubstate(substateSlot);
        }
    }

    private void removeSubstate(StateMachine.SubstateSlot substateSlot) {
        State state = substateSlot.getState();
        substateSlot.setState(null);
        if (state != this.m_currentState || state != SwipeStatePlayer.instance()) {
            this.stateExit(state);
        }
    }

    public boolean isSubstate(State substate) {
        return PZArrayUtil.contains(this.m_subStates, Lambda.predicate(substate, (substateSlot, statex) -> substateSlot.getState() == statex));
    }

    public State getCurrent() {
        return this.m_currentState;
    }

    public State getPrevious() {
        return this.m_previousState;
    }

    public int getSubStateCount() {
        return this.m_subStates.size();
    }

    public State getSubStateAt(int idx) {
        return this.m_subStates.get(idx).getState();
    }

    public void revertToPreviousState(State sender) {
        if (this.isSubstate(sender)) {
            this.removeSubstate(sender);
        } else if (this.m_currentState != sender) {
            DebugLog.ActionSystem.warn("The sender $s is not an active state in this state machine.", String.valueOf(sender));
        } else {
            this.changeRootState(this.m_previousState, false);
        }
    }

    public void update() {
        if (this.m_currentState != null) {
            this.m_currentState.execute(this.m_owner);
        }

        Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this.m_owner, (substateSlot, character) -> {
            if (!substateSlot.isEmpty()) {
                substateSlot.state.execute(character);
            }
        });
        this.logCurrentState();
    }

    private void logCurrentState() {
        if (this.m_owner.isAnimationRecorderActive()) {
            this.m_owner.getAnimationPlayerRecorder().logAIState(this.m_currentState, this.m_subStates);
        }
    }

    private void stateEnter(State state) {
        state.enter(this.m_owner);
    }

    private void stateExit(State state) {
        state.exit(this.m_owner);
    }

    public final void stateAnimEvent(int stateLayer, AnimEvent event) {
        if (stateLayer == 0) {
            if (this.m_currentState != null) {
                this.m_currentState.animEvent(this.m_owner, event);
            }
        } else {
            Lambda.forEachFrom(PZArrayUtil::forEach, this.m_subStates, this.m_owner, event, (substateSlot, character, animEvent) -> {
                if (!substateSlot.isEmpty()) {
                    substateSlot.state.animEvent(character, animEvent);
                }
            });
        }
    }

    public boolean isLocked() {
        return this.m_isLocked;
    }

    public void setLocked(boolean lock) {
        this.m_isLocked = lock;
    }

    public static class SubstateSlot {
        private State state;
        boolean shouldBeActive;

        SubstateSlot(State statex) {
            this.state = statex;
            this.shouldBeActive = true;
        }

        public State getState() {
            return this.state;
        }

        void setState(State statex) {
            this.state = statex;
        }

        public boolean isEmpty() {
            return this.state == null;
        }
    }
}
