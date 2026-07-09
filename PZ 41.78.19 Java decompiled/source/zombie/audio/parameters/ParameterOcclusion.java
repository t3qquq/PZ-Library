// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import fmod.fmod.FMODSoundEmitter;
import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;

public final class ParameterOcclusion extends FMODLocalParameter {
    private final FMODSoundEmitter emitter;
    private float currentValue = Float.NaN;

    public ParameterOcclusion(FMODSoundEmitter _emitter) {
        super("Occlusion");
        this.emitter = _emitter;
    }

    @Override
    public float calculateCurrentValue() {
        float float0 = 1.0F;

        for (int int0 = 0; int0 < 4; int0++) {
            float float1 = this.calculateValueForPlayer(int0);
            float0 = PZMath.min(float0, float1);
        }

        this.currentValue = float0;
        return (int)(this.currentValue * 1000.0F) / 1000.0F;
    }

    @Override
    public void resetToDefault() {
        this.currentValue = Float.NaN;
    }

    private float calculateValueForPlayer(int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player == null) {
            return 1.0F;
        } else {
            IsoGridSquare square0 = player.getCurrentSquare();
            IsoGridSquare square1 = IsoWorld.instance.getCell().getGridSquare((double)this.emitter.x, (double)this.emitter.y, (double)this.emitter.z);
            if (square1 == null) {
                boolean boolean0 = true;
            }

            float float0 = 0.0F;
            if (square0 != null && square1 != null && !square1.isCouldSee(int0)) {
                float0 = 1.0F;
            }

            return float0;
        }
    }
}
