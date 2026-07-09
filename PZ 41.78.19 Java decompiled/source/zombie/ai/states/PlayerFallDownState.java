// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class PlayerFallDownState extends State {
    private static final PlayerFallDownState _instance = new PlayerFallDownState();

    public static PlayerFallDownState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.clearVariable("bKnockedDown");
        if (owner.isDead() && !GameServer.bServer && !GameClient.bClient) {
            owner.Kill(null);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.setOnFloor(true);
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (GameClient.bClient && event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            owner.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }
    }
}
