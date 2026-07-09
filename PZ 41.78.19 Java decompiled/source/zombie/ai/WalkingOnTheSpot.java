// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import zombie.GameTime;
import zombie.iso.IsoUtils;

public final class WalkingOnTheSpot {
    private float x;
    private float y;
    private float time;

    public boolean check(float x1, float y1) {
        if (IsoUtils.DistanceToSquared(this.x, this.y, x1, y1) < 0.010000001F) {
            this.time = this.time + GameTime.getInstance().getMultiplier();
        } else {
            this.x = x1;
            this.y = y1;
            this.time = 0.0F;
        }

        return this.time > 400.0F;
    }

    public void reset(float x1, float y1) {
        this.x = x1;
        this.y = y1;
        this.time = 0.0F;
    }
}
