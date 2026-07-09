// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.iso.IsoCamera;

public final class IsoDummyCameraCharacter extends IsoGameCharacter {
    public IsoDummyCameraCharacter(float x, float y, float z) {
        super(null, x, y, z);
        IsoCamera.CamCharacter = this;
    }

    @Override
    public void update() {
    }
}
