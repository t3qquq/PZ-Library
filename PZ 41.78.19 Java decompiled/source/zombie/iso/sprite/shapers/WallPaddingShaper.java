// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.textures.TextureDraw;

public class WallPaddingShaper implements Consumer<TextureDraw> {
    public static final WallPaddingShaper instance = new WallPaddingShaper();

    public void accept(TextureDraw textureDraw) {
        SpritePadding.applyIsoPadding(textureDraw, SpritePaddingSettings.getSettings().IsoPadding);
    }
}
