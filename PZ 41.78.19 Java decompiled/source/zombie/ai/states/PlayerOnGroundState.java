// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;

public final class PlayerOnGroundState extends State {
    private static final PlayerOnGroundState _instance = new PlayerOnGroundState();

    public static PlayerOnGroundState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        ((IsoPlayer)owner).setBlockMovement(true);
        owner.setVariable("bAnimEnd", false);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (!GameServer.bServer && owner.isDead()) {
            owner.die();
        } else {
            owner.setReanimateTimer(owner.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        ((IsoPlayer)owner).setBlockMovement(false);
    }
}
