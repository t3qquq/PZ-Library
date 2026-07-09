// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.sprite.IsoSprite;

public class IsoRadio extends IsoWaveSignal {
    public IsoRadio(IsoCell cell) {
        super(cell);
    }

    public IsoRadio(IsoCell cell, IsoGridSquare sq, IsoSprite spr) {
        super(cell, sq, spr);
    }

    @Override
    public String getObjectName() {
        return "Radio";
    }

    @Override
    protected void init(boolean boolean0) {
        super.init(boolean0);
    }
}
