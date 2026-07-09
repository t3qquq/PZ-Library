// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.network.GameClient;
import zombie.popman.ZombiePopulationManager;

public final class ZombieSittingState extends State {
    private static final ZombieSittingState _instance = new ZombieSittingState();

    public static ZombieSittingState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        if (GameClient.bClient && owner.getCurrentSquare() != null) {
            ZombiePopulationManager.instance.sitAgainstWall(zombie0, zombie0.getCurrentSquare());
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }
}
