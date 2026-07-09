// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;

public final class OpenWindowState extends State {
    private static final OpenWindowState _instance = new OpenWindowState();
    private static final Integer PARAM_WINDOW = 1;

    public static OpenWindowState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setHideWeaponModel(true);
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
        if (Core.bDebug
            && DebugOptions.instance.CheatWindowUnlock.getValue()
            && window.getSprite() != null
            && !window.getSprite().getProperties().Is("WindowLocked")) {
            window.Locked = false;
            window.PermaLocked = false;
        }

        if (window.north) {
            if (window.getSquare().getY() < owner.getY()) {
                owner.setDir(IsoDirections.N);
            } else {
                owner.setDir(IsoDirections.S);
            }
        } else if (window.getSquare().getX() < owner.getX()) {
            owner.setDir(IsoDirections.W);
        } else {
            owner.setDir(IsoDirections.E);
        }

        owner.setVariable("bOpenWindow", true);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (owner.getVariableBoolean("bOpenWindow")) {
            IsoPlayer player = (IsoPlayer)owner;
            if (!player.pressedMovement(false) && !player.pressedCancelAction()) {
                IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
                if (window == null || window.getObjectIndex() == -1) {
                    owner.setVariable("bOpenWindow", false);
                } else if (IsoPlayer.getInstance().ContextPanic > 5.0F) {
                    IsoPlayer.getInstance().ContextPanic = 0.0F;
                    owner.setVariable("bOpenWindow", false);
                    owner.smashWindow(window);
                    owner.getStateMachineParams(SmashWindowState.instance()).put(3, Boolean.TRUE);
                } else {
                    player.setCollidable(true);
                    player.updateLOS();
                    if (window.north) {
                        if (window.getSquare().getY() < owner.getY()) {
                            owner.setDir(IsoDirections.N);
                        } else {
                            owner.setDir(IsoDirections.S);
                        }
                    } else if (window.getSquare().getX() < owner.getX()) {
                        owner.setDir(IsoDirections.W);
                    } else {
                        owner.setDir(IsoDirections.E);
                    }

                    if (Core.bTutorial) {
                        if (owner.x != window.getX() + 0.5F && window.north) {
                            this.slideX(owner, window.getX() + 0.5F);
                        }

                        if (owner.y != window.getY() + 0.5F && !window.north) {
                            this.slideY(owner, window.getY() + 0.5F);
                        }
                    }
                }
            } else {
                owner.setVariable("bOpenWindow", false);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.clearVariable("bOpenWindow");
        owner.clearVariable("OpenWindowOutcome");
        owner.clearVariable("StopAfterAnimLooped");
        owner.setHideWeaponModel(false);
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (owner.getVariableBoolean("bOpenWindow")) {
            IsoWindow window = (IsoWindow)hashMap.get(PARAM_WINDOW);
            if (window == null) {
                owner.setVariable("bOpenWindow", false);
            } else {
                if (event.m_EventName.equalsIgnoreCase("WindowAnimLooped")) {
                    if ("start".equalsIgnoreCase(event.m_ParameterValue)) {
                        if (window.isPermaLocked() || window.Locked && owner.getCurrentSquare().Is(IsoFlagType.exterior)) {
                            owner.setVariable("OpenWindowOutcome", "struggle");
                        } else {
                            owner.setVariable("OpenWindowOutcome", "success");
                        }

                        return;
                    }

                    if (event.m_ParameterValue.equalsIgnoreCase(owner.getVariableString("StopAfterAnimLooped"))) {
                        owner.setVariable("bOpenWindow", false);
                    }
                }

                if (event.m_EventName.equalsIgnoreCase("WindowOpenAttempt")) {
                    this.onAttemptFinished(owner, window);
                } else if (event.m_EventName.equalsIgnoreCase("WindowOpenSuccess")) {
                    this.onSuccess(owner, window);
                } else if (event.m_EventName.equalsIgnoreCase("WindowStruggleSound") && "struggle".equals(owner.getVariableString("OpenWindowOutcome"))) {
                    owner.playSound("WindowIsLocked");
                }
            }
        }
    }

    /**
     * @return TRUE if this state handles the "Cancel Action" key or the B controller button.
     */
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }

    private void onAttemptFinished(IsoGameCharacter character, IsoWindow window) {
        HashMap hashMap = character.getStateMachineParams(this);
        this.exert(character);
        if (window.isPermaLocked()) {
            if (!character.getEmitter().isPlaying("WindowIsLocked")) {
            }

            character.setVariable("OpenWindowOutcome", "fail");
            character.setVariable("StopAfterAnimLooped", "fail");
        } else {
            byte byte0 = 10;
            if (character.Traits.Burglar.isSet()) {
                byte0 = 5;
            }

            if (window.Locked && character.getCurrentSquare().Is(IsoFlagType.exterior)) {
                if (Rand.Next(100) < byte0) {
                    character.getEmitter().playSound("BreakLockOnWindow", window);
                    window.setPermaLocked(true);
                    window.syncIsoObject(false, (byte)0, null, null);
                    hashMap.put(PARAM_WINDOW, null);
                    character.setVariable("OpenWindowOutcome", "fail");
                    character.setVariable("StopAfterAnimLooped", "fail");
                    return;
                }

                boolean boolean0 = false;
                if (character.getPerkLevel(PerkFactory.Perks.Strength) > 7 && Rand.Next(100) < 20) {
                    boolean0 = true;
                } else if (character.getPerkLevel(PerkFactory.Perks.Strength) > 5 && Rand.Next(100) < 10) {
                    boolean0 = true;
                } else if (character.getPerkLevel(PerkFactory.Perks.Strength) > 3 && Rand.Next(100) < 6) {
                    boolean0 = true;
                } else if (character.getPerkLevel(PerkFactory.Perks.Strength) > 1 && Rand.Next(100) < 4) {
                    boolean0 = true;
                } else if (Rand.Next(100) <= 1) {
                    boolean0 = true;
                }

                if (boolean0) {
                    character.setVariable("OpenWindowOutcome", "success");
                }
            } else {
                character.setVariable("OpenWindowOutcome", "success");
            }
        }
    }

    private void onSuccess(IsoGameCharacter character, IsoWindow window) {
        character.setVariable("StopAfterAnimLooped", "success");
        IsoPlayer.getInstance().ContextPanic = 0.0F;
        if (window.getObjectIndex() != -1 && !window.open) {
            window.ToggleWindow(character);
        }
    }

    private void exert(IsoGameCharacter character) {
        float float0 = GameTime.getInstance().getMultiplier() / 1.6F;
        switch (character.getPerkLevel(PerkFactory.Perks.Fitness)) {
            case 1:
                character.exert(0.01F * float0);
                break;
            case 2:
                character.exert(0.009F * float0);
                break;
            case 3:
                character.exert(0.008F * float0);
                break;
            case 4:
                character.exert(0.007F * float0);
                break;
            case 5:
                character.exert(0.006F * float0);
                break;
            case 6:
                character.exert(0.005F * float0);
                break;
            case 7:
                character.exert(0.004F * float0);
                break;
            case 8:
                character.exert(0.003F * float0);
                break;
            case 9:
                character.exert(0.0025F * float0);
                break;
            case 10:
                character.exert(0.002F * float0);
        }
    }

    private void slideX(IsoGameCharacter character, float float1) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = float1 > character.x ? Math.min(float0, float1 - character.x) : Math.max(-float0, float1 - character.x);
        character.x += float0;
        character.nx = character.x;
    }

    private void slideY(IsoGameCharacter character, float float1) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = float1 > character.y ? Math.min(float0, float1 - character.y) : Math.max(-float0, float1 - character.y);
        character.y += float0;
        character.ny = character.y;
    }

    public void setParams(IsoGameCharacter owner, IsoWindow window) {
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.clear();
        hashMap.put(PARAM_WINDOW, window);
    }
}
