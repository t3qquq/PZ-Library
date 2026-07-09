// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;

public final class FakeDeadZombieState extends State {
    private static final FakeDeadZombieState _instance = new FakeDeadZombieState();

    public static FakeDeadZombieState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setVisibleToNPCs(false);
        owner.setCollidable(false);
        ((IsoZombie)owner).setFakeDead(true);
        owner.setOnFloor(true);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (owner.isDead()) {
            if (GameClient.bClient && owner instanceof IsoZombie) {
                GameClient.sendZombieDeath((IsoZombie)owner);
            }

            if (!GameClient.bClient) {
                new IsoDeadBody(owner);
            }
        } else if (Core.bLastStand) {
            ((IsoZombie)owner).setFakeDead(false);
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        ((IsoZombie)owner).setFakeDead(false);
    }
}
