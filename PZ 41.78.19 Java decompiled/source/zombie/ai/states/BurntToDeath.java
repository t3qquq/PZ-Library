// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;

public final class BurntToDeath extends State {
    private static final BurntToDeath _instance = new BurntToDeath();

    public static BurntToDeath instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        if (owner instanceof IsoSurvivor) {
            owner.getDescriptor().bDead = true;
        }

        if (!(owner instanceof IsoZombie)) {
            owner.PlayAnimUnlooped("Die");
        } else {
            owner.PlayAnimUnlooped("ZombieDeath");
        }

        owner.def.AnimFrameIncrease = 0.25F;
        owner.setStateMachineLocked(true);
        String string = owner.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
        owner.getEmitter().playVocals(string);
        if (GameServer.bServer && owner instanceof IsoZombie) {
            GameServer.sendZombieSound(IsoZombie.ZombieSound.Burned, (IsoZombie)owner);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if ((int)owner.def.Frame == owner.sprite.CurrentAnim.Frames.size() - 1) {
            if (owner == TutorialManager.instance.wife) {
                owner.dir = IsoDirections.S;
            }

            owner.RemoveAttachedAnims();
            if (GameClient.bClient && owner instanceof IsoZombie) {
                GameClient.sendZombieDeath((IsoZombie)owner);
            }

            if (!GameClient.bClient) {
                new IsoDeadBody(owner);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }
}
