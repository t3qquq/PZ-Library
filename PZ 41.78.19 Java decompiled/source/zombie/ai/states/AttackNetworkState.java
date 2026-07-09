// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.util.StringUtils;

public class AttackNetworkState extends State {
    private static final AttackNetworkState s_instance = new AttackNetworkState();
    private String attackOutcome;

    public static AttackNetworkState instance() {
        return s_instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        HashMap hashMap = character.getStateMachineParams(this);
        hashMap.clear();
        hashMap.put(0, Boolean.FALSE);
        this.attackOutcome = character.getVariableString("AttackOutcome");
        character.setVariable("AttackOutcome", "start");
        character.clearVariable("AttackDidDamage");
        character.clearVariable("ZombieBiteDone");
        zombie0.setTargetSeenTime(1.0F);
        if (!zombie0.bCrawling) {
            zombie0.setVariable("AttackType", "bite");
        }
    }

    @Override
    public void execute(IsoGameCharacter character0) {
        IsoZombie zombie0 = (IsoZombie)character0;
        HashMap hashMap = character0.getStateMachineParams(this);
        IsoGameCharacter character1 = (IsoGameCharacter)zombie0.target;
        if (character1 == null || !"Chainsaw".equals(character1.getVariableString("ZombieHitReaction"))) {
            String string = character0.getVariableString("AttackOutcome");
            if ("success".equals(string)
                && !character0.getVariableBoolean("bAttack")
                && (character1 == null || !character1.isGodMod())
                && !character0.getVariableBoolean("AttackDidDamage")
                && character0.getVariableString("ZombieBiteDone") != "true") {
                character0.setVariable("AttackOutcome", "interrupted");
            }

            if (character1 == null || character1.isDead()) {
                zombie0.setTargetSeenTime(10.0F);
            }

            if (character1 != null
                && hashMap.get(0) == Boolean.FALSE
                && !"started".equals(string)
                && !StringUtils.isNullOrEmpty(character0.getVariableString("PlayerHitReaction"))) {
                hashMap.put(0, Boolean.TRUE);
            }

            zombie0.setShootable(true);
            if (zombie0.target != null && !zombie0.bCrawling) {
                if (!"fail".equals(string) && !"interrupted".equals(string)) {
                    zombie0.faceThisObject(zombie0.target);
                }

                zombie0.setOnFloor(false);
            }

            if (zombie0.target != null) {
                zombie0.target.setTimeSinceZombieAttack(0);
                zombie0.target.setLastTargettedBy(zombie0);
            }

            if (!zombie0.bCrawling) {
                zombie0.setVariable("AttackType", "bite");
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        character.clearVariable("AttackOutcome");
        character.clearVariable("AttackType");
        character.clearVariable("PlayerHitReaction");
        character.setStateMachineLocked(false);
        if (zombie0.target != null && zombie0.target.isOnFloor()) {
            zombie0.setEatBodyTarget(zombie0.target, true);
            zombie0.setTarget(null);
        }

        zombie0.AllowRepathDelay = 0.0F;
    }

    @Override
    public void animEvent(IsoGameCharacter character, AnimEvent animEvent) {
        IsoZombie zombie0 = (IsoZombie)character;
        if (GameClient.bClient && zombie0.isRemoteZombie()) {
            if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
                zombie0.setVariable("AttackOutcome", "fail".equals(this.attackOutcome) ? "fail" : "success");
            }

            if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && zombie0.target instanceof IsoPlayer player) {
                if (zombie0.scratch) {
                    zombie0.getEmitter().playSoundImpl("ZombieScratch", zombie0);
                } else if (zombie0.laceration) {
                    zombie0.getEmitter().playSoundImpl("ZombieScratch", zombie0);
                } else {
                    zombie0.getEmitter().playSoundImpl("ZombieBite", zombie0);
                    player.splatBloodFloorBig();
                    player.splatBloodFloorBig();
                    player.splatBloodFloorBig();
                }
            }

            if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
                character.setVariable("EatingStarted", true);
                ((IsoZombie)character).setEatBodyTarget(((IsoZombie)character).target, true);
                ((IsoZombie)character).setTarget(null);
            }
        }

        if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
            zombie0.parameterZombieState.setState(ParameterZombieState.State.Attack);
        }
    }

    @Override
    public boolean isAttacking(IsoGameCharacter var1) {
        return true;
    }
}
