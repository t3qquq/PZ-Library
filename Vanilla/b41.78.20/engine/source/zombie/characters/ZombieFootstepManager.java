// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

public final class ZombieFootstepManager extends BaseZombieSoundManager {
    public static final ZombieFootstepManager instance = new ZombieFootstepManager();

    public ZombieFootstepManager() {
        super(40, 500);
    }

    @Override
    public void playSound(IsoZombie zombie0) {
        zombie0.getEmitter().playFootsteps("ZombieFootstepsCombined", zombie0.getFootstepVolume());
    }

    @Override
    public void postUpdate() {
    }
}
