// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoZombie;
import zombie.core.math.PZMath;

public final class ParameterPlayerDistance extends FMODLocalParameter {
    private final IsoZombie zombie;

    public ParameterPlayerDistance(IsoZombie _zombie) {
        super("PlayerDistance");
        this.zombie = _zombie;
    }

    @Override
    public float calculateCurrentValue() {
        return this.zombie.target == null ? 1000.0F : (int)PZMath.ceil(this.zombie.DistToProper(this.zombie.target));
    }
}
