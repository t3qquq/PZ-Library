// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import zombie.ai.permission.DefaultStatePermissions;
import zombie.ai.permission.IStatePermissions;
import zombie.characters.IsoGameCharacter;
import zombie.characters.MoveDeltaModifiers;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public abstract class State {
    public void enter(IsoGameCharacter owner) {
    }

    public void execute(IsoGameCharacter owner) {
    }

    public void exit(IsoGameCharacter owner) {
    }

    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }

    /**
     * Return TRUE if the owner is currently attacking.   Defaults to FALSE
     */
    public boolean isAttacking(IsoGameCharacter owner) {
        return false;
    }

    /**
     * Return TRUE if the owner is currently moving.   Defaults to FALSE
     */
    public boolean isMoving(IsoGameCharacter owner) {
        return false;
    }

    /**
     * @return TRUE if this state handles the "Cancel Action" key or the B controller button.
     */
    public boolean isDoingActionThatCanBeCancelled() {
        return false;
    }

    public void getDeltaModifiers(IsoGameCharacter owner, MoveDeltaModifiers modifiers) {
    }

    /**
     * Return TRUE if the owner should ignore collisions when passing between two squares.  Defaults to FALSE
     */
    public boolean isIgnoreCollide(IsoGameCharacter owner, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return false;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public IStatePermissions getStatePermissions() {
        return DefaultStatePermissions.Instance;
    }
}
