// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class PlayerKnockedDown extends State {
    private static final PlayerKnockedDown _instance = new PlayerKnockedDown();

    public static PlayerKnockedDown instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        ((IsoPlayer)owner).setBlockMovement(true);
        owner.setHitReaction("");
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (owner.isDead()) {
            if (!GameServer.bServer && !GameClient.bClient) {
                owner.Kill(null);
            }
        } else {
            owner.setReanimateTimer(owner.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("FallOnBack")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("setSitOnGround")) {
            owner.setSitOnGround(Boolean.parseBoolean(event.m_ParameterValue));
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        ((IsoPlayer)owner).setBlockMovement(false);
        ((IsoPlayer)owner).setKnockedDown(false);
        owner.setOnFloor(true);
    }
}
