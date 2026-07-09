// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameServer;

public final class PlayerHitReactionPVPState extends State {
    private static final PlayerHitReactionPVPState _instance = new PlayerHitReactionPVPState();

    public static PlayerHitReactionPVPState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        if (!owner.getCharacterActions().isEmpty()) {
            owner.getCharacterActions().get(0).forceStop();
        }

        owner.setSitOnGround(false);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.setHitReaction("");
        owner.setVariable("hitpvp", false);
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
            owner.getAttackedBy().setHitForce(0.03F);
            if (owner.getAttackedBy() instanceof IsoZombie) {
                ((IsoZombie)owner.getAttackedBy()).setPlayerAttackPosition(null);
                ((IsoZombie)owner.getAttackedBy()).setStaggerBack(true);
            }
        }

        if (event.m_EventName.equalsIgnoreCase("Defend")) {
            owner.getAttackedBy().setHitReaction("BiteDefended");
        }

        if (event.m_EventName.equalsIgnoreCase("DeathSound")) {
            if (owner.isPlayingDeathSound()) {
                return;
            }

            owner.setPlayingDeathSound(true);
            String string = "Male";
            if (owner.isFemale()) {
                string = "Female";
            }

            string = string + "BeingEatenDeath";
            owner.playSound(string);
        }

        if (event.m_EventName.equalsIgnoreCase("Death")) {
            owner.setOnFloor(true);
            if (!GameServer.bServer) {
                owner.Kill(owner.getAttackedBy());
            }
        }
    }
}
