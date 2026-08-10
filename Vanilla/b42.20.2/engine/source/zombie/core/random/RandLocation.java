// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.core.random;

import zombie.iso.worldgen.WorldGenParams;

public class RandLocation extends RandAbstract {
    public RandLocation(int x, int y) {
        this.rand = WorldGenParams.INSTANCE.getRandom(x, y);
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException();
    }
}
