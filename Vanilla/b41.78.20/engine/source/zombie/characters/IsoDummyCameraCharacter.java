// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
