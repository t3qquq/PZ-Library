// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoFireplace;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class PlayerSitOnGroundState extends State {
    private static final PlayerSitOnGroundState _instance = new PlayerSitOnGroundState();
    private static final int RAND_EXT = 2500;
    private static final Integer PARAM_FIRE = 0;
    private static final Integer PARAM_SITGROUNDANIM = 1;
    private static final Integer PARAM_CHECK_FIRE = 2;
    private static final Integer PARAM_CHANGE_ANIM = 3;

    public static PlayerSitOnGroundState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.put(PARAM_FIRE, this.checkFire(owner));
        hashMap.put(PARAM_CHECK_FIRE, System.currentTimeMillis());
        hashMap.put(PARAM_CHANGE_ANIM, 0L);
        owner.setSitOnGround(true);
        if ((owner.getPrimaryHandItem() == null || !(owner.getPrimaryHandItem() instanceof HandWeapon))
            && (owner.getSecondaryHandItem() == null || !(owner.getSecondaryHandItem() instanceof HandWeapon))) {
            owner.setHideWeaponModel(true);
        }

        if (owner.getStateMachine().getPrevious() == IdleState.instance()) {
            owner.clearVariable("SitGroundStarted");
            owner.clearVariable("forceGetUp");
            owner.clearVariable("SitGroundAnim");
        }
    }

    private boolean checkFire(IsoGameCharacter character) {
        IsoGridSquare square0 = character.getCurrentSquare();

        for (int int0 = -4; int0 < 4; int0++) {
            for (int int1 = -4; int1 < 4; int1++) {
                IsoGridSquare square1 = square0.getCell().getGridSquare(square0.x + int0, square0.y + int1, square0.z);
                if (square1 != null) {
                    if (square1.haveFire()) {
                        return true;
                    }

                    for (int int2 = 0; int2 < square1.getObjects().size(); int2++) {
                        IsoFireplace fireplace = Type.tryCastTo(square1.getObjects().get(int2), IsoFireplace.class);
                        if (fireplace != null && fireplace.isLit()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoPlayer player = (IsoPlayer)owner;
        if (player.pressedMovement(false)) {
            owner.StopAllActionQueue();
            owner.setVariable("forceGetUp", true);
        }

        long long0 = System.currentTimeMillis();
        if (long0 > (Long)hashMap.get(PARAM_CHECK_FIRE) + 5000L) {
            hashMap.put(PARAM_FIRE, this.checkFire(owner));
            hashMap.put(PARAM_CHECK_FIRE, long0);
        }

        if (owner.hasTimedActions()) {
            hashMap.put(PARAM_FIRE, false);
            owner.setVariable("SitGroundAnim", "Idle");
        }

        boolean boolean0 = (Boolean)hashMap.get(PARAM_FIRE);
        if (boolean0) {
            boolean boolean1 = long0 > (Long)hashMap.get(PARAM_CHANGE_ANIM);
            if (boolean1) {
                if ("Idle".equals(owner.getVariableString("SitGroundAnim"))) {
                    owner.setVariable("SitGroundAnim", "WarmHands");
                } else if ("WarmHands".equals(owner.getVariableString("SitGroundAnim"))) {
                    owner.setVariable("SitGroundAnim", "Idle");
                }

                hashMap.put(PARAM_CHANGE_ANIM, long0 + Rand.Next(30000, 90000));
            }
        } else if (owner.getVariableBoolean("SitGroundStarted")) {
            owner.clearVariable("FireNear");
            owner.setVariable("SitGroundAnim", "Idle");
        }

        if ("WarmHands".equals(owner.getVariableString("SitGroundAnim")) && Rand.Next(Rand.AdjustForFramerate(2500)) == 0) {
            hashMap.put(PARAM_SITGROUNDANIM, owner.getVariableString("SitGroundAnim"));
            owner.setVariable("SitGroundAnim", "rubhands");
        }

        player.setInitiateAttack(false);
        player.attackStarted = false;
        player.setAttackType(null);
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setHideWeaponModel(false);
        if (StringUtils.isNullOrEmpty(owner.getVariableString("HitReaction"))) {
            owner.clearVariable("SitGroundStarted");
            owner.clearVariable("forceGetUp");
            owner.clearVariable("SitGroundAnim");
            owner.setIgnoreMovement(false);
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("SitGroundStarted")) {
            owner.setVariable("SitGroundStarted", true);
            boolean boolean0 = (Boolean)owner.getStateMachineParams(this).get(PARAM_FIRE);
            if (boolean0) {
                owner.setVariable("SitGroundAnim", "WarmHands");
            } else {
                owner.setVariable("SitGroundAnim", "Idle");
            }
        }

        if (event.m_EventName.equalsIgnoreCase("ResetSitOnGroundAnim")) {
            owner.setVariable("SitGroundAnim", (String)owner.getStateMachineParams(this).get(PARAM_SITGROUNDANIM));
        }
    }
}
