// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSpriteManager;

public class IsoBrokenGlass extends IsoObject {
    public IsoBrokenGlass(IsoCell cell) {
        super(cell);
        int int0 = Rand.Next(4);
        this.sprite = IsoSpriteManager.instance.getSprite("brokenglass_1_" + int0);
    }

    @Override
    public String getObjectName() {
        return "IsoBrokenGlass";
    }

    @Override
    public void load(ByteBuffer bb, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(bb, WorldVersion, IS_DEBUG_SAVE);
    }

    @Override
    public void save(ByteBuffer bb, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(bb, IS_DEBUG_SAVE);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
    }

    @Override
    public void removeFromWorld() {
        super.removeFromWorld();
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        super.render(x, y, z, col, bDoChild, bWallLightingPass, shader);
    }

    @Override
    public void renderObjectPicker(float x, float y, float z, ColorInfo lightInfo) {
    }
}
