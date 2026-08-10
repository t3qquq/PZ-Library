// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class ParticleModelCamera extends ModelCamera {
    @Override
    public void Begin() {
        Core.getInstance().DoPushIsoParticleStuff(this.m_x, this.m_y, this.m_z);
    }

    @Override
    public void End() {
        Core.getInstance().DoPopIsoStuff();
    }
}
