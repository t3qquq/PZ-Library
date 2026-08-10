// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;

public class WallShaperWhole extends WallShaper {
    public static final WallShaperWhole instance = new WallShaperWhole();

    @Override
    public void accept(TextureDraw textureDraw) {
        super.accept(textureDraw);
        WallPaddingShaper.instance.accept(textureDraw);
    }
}
