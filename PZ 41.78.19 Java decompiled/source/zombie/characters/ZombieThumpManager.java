// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.core.math.PZMath;

public final class ZombieThumpManager extends BaseZombieSoundManager {
    public static final ZombieThumpManager instance = new ZombieThumpManager();

    public ZombieThumpManager() {
        super(40, 100);
    }

    @Override
    public void playSound(IsoZombie zombie0) {
        long long0 = 0L;
        if (zombie0.thumpFlag == 1) {
            long0 = zombie0.getEmitter().playSoundImpl("ZombieThumpGeneric", null);
        } else if (zombie0.thumpFlag == 2) {
            zombie0.getEmitter().playSoundImpl("ZombieThumpGeneric", null);
            long0 = zombie0.getEmitter().playSoundImpl("ZombieThumpWindow", null);
        } else if (zombie0.thumpFlag == 3) {
            long0 = zombie0.getEmitter().playSoundImpl("ZombieThumpWindow", null);
        } else if (zombie0.thumpFlag == 4) {
            long0 = zombie0.getEmitter().playSoundImpl("ZombieThumpMetal", null);
        } else if (zombie0.thumpFlag == 5) {
            long0 = zombie0.getEmitter().playSoundImpl("ZombieThumpGarageDoor", null);
        }

        FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = FMODManager.instance.getParameterDescription("ObjectCondition");
        zombie0.getEmitter().setParameterValue(long0, fmod_studio_parameter_description, PZMath.ceil(zombie0.getThumpCondition() * 100.0F));
    }

    @Override
    public void postUpdate() {
        for (int int0 = 0; int0 < this.characters.size(); int0++) {
            this.characters.get(int0).setThumpFlag(0);
        }
    }
}
