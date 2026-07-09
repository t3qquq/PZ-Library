// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoZombieGiblets;

public final class ZombieHitReactionState extends State {
    private static final ZombieHitReactionState _instance = new ZombieHitReactionState();
    private static final int TURN_TO_PLAYER = 1;
    private static final int HIT_REACTION_TIMER = 2;

    public static ZombieHitReactionState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        zombie0.collideWhileHit = true;
        HashMap hashMap = character.getStateMachineParams(this);
        hashMap.put(1, Boolean.FALSE);
        hashMap.put(2, 0.0F);
        character.clearVariable("onknees");
        if (zombie0.isSitAgainstWall()) {
            character.setHitReaction(null);
        }
    }

    @Override
    public void execute(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        character.setOnFloor(((IsoZombie)character).isKnockedDown());
        hashMap.put(2, (Float)hashMap.get(2) + GameTime.getInstance().getMultiplier());
        if (hashMap.get(1) == Boolean.TRUE) {
            if (!character.isHitFromBehind()) {
                character.setDir(IsoDirections.reverse(IsoDirections.fromAngle(character.getHitDir())));
            } else {
                character.setDir(IsoDirections.fromAngle(character.getHitDir()));
            }
        } else if (character.hasAnimationPlayer()) {
            character.getAnimationPlayer().setTargetToAngle();
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        zombie0.collideWhileHit = true;
        if (zombie0.target != null) {
            zombie0.AllowRepathDelay = 0.0F;
            zombie0.spotted(zombie0.target, true);
        }

        zombie0.setStaggerBack(false);
        zombie0.setHitReaction("");
        zombie0.setEatBodyTarget(null, false);
        zombie0.setSitAgainstWall(false);
        zombie0.setShootable(true);
    }

    @Override
    public void animEvent(IsoGameCharacter character, AnimEvent animEvent) {
        HashMap hashMap = character.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)character;
        if (animEvent.m_EventName.equalsIgnoreCase("DoDeath") && Boolean.parseBoolean(animEvent.m_ParameterValue) && character.isAlive()) {
            character.Kill(character.getAttackedBy());
            if (zombie0.upKillCount && character.getAttackedBy() != null) {
                character.getAttackedBy().setZombieKills(character.getAttackedBy().getZombieKills() + 1);
            }
        }

        if (animEvent.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
            character.setDoDeathSound(false);
            character.playDeadSound();
        }

        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            character.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }

        if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
        }

        if (animEvent.m_EventName.equalsIgnoreCase("Collide") && ((IsoZombie)character).speedType == 1) {
            ((IsoZombie)character).collideWhileHit = false;
        }

        if (animEvent.m_EventName.equalsIgnoreCase("ZombieTurnToPlayer")) {
            boolean boolean0 = Boolean.parseBoolean(animEvent.m_ParameterValue);
            hashMap.put(1, boolean0 ? Boolean.TRUE : Boolean.FALSE);
        }

        if (animEvent.m_EventName.equalsIgnoreCase("CancelKnockDown")) {
            boolean boolean1 = Boolean.parseBoolean(animEvent.m_ParameterValue);
            if (boolean1) {
                ((IsoZombie)character).setKnockedDown(false);
            }
        }

        if (animEvent.m_EventName.equalsIgnoreCase("KnockDown")) {
            character.setOnFloor(true);
            ((IsoZombie)character).setKnockedDown(true);
        }

        if (animEvent.m_EventName.equalsIgnoreCase("SplatBlood")) {
            zombie0.addBlood(null, true, false, false);
            zombie0.addBlood(null, true, false, false);
            zombie0.addBlood(null, true, false, false);
            zombie0.playBloodSplatterSound();

            for (int int0 = 0; int0 < 10; int0++) {
                zombie0.getCurrentSquare()
                    .getChunk()
                    .addBloodSplat(zombie0.x + Rand.Next(-0.5F, 0.5F), zombie0.y + Rand.Next(-0.5F, 0.5F), zombie0.z, Rand.Next(8));
                if (Rand.Next(5) == 0) {
                    new IsoZombieGiblets(
                        IsoZombieGiblets.GibletType.B,
                        zombie0.getCell(),
                        zombie0.getX(),
                        zombie0.getY(),
                        zombie0.getZ() + 0.3F,
                        Rand.Next(-0.2F, 0.2F) * 1.5F,
                        Rand.Next(-0.2F, 0.2F) * 1.5F
                    );
                } else {
                    new IsoZombieGiblets(
                        IsoZombieGiblets.GibletType.A,
                        zombie0.getCell(),
                        zombie0.getX(),
                        zombie0.getY(),
                        zombie0.getZ() + 0.3F,
                        Rand.Next(-0.2F, 0.2F) * 1.5F,
                        Rand.Next(-0.2F, 0.2F) * 1.5F
                    );
                }
            }
        }

        if (animEvent.m_EventName.equalsIgnoreCase("SetState") && !zombie0.isDead()) {
            if (zombie0.getAttackedBy() != null && zombie0.getAttackedBy().getVehicle() != null && "Floor".equals(zombie0.getHitReaction())) {
                zombie0.parameterZombieState.setState(ParameterZombieState.State.RunOver);
                return;
            }

            zombie0.parameterZombieState.setState(ParameterZombieState.State.Hit);
        }
    }
}
