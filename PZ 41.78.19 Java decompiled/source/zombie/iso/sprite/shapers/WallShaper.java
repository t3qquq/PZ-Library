// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;

public class WallShaper implements Consumer<TextureDraw> {
    public final int[] col = new int[4];
    protected int colTint = 0;

    public void setTintColor(int tintABGR) {
        this.colTint = tintABGR;
    }

    public void accept(TextureDraw texd) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Lighting.getValue()) {
            texd.col0 = Color.blendBGR(texd.col0, this.col[0]);
            texd.col1 = Color.blendBGR(texd.col1, this.col[1]);
            texd.col2 = Color.blendBGR(texd.col2, this.col[2]);
            texd.col3 = Color.blendBGR(texd.col3, this.col[3]);
        }

        if (this.colTint != 0) {
            texd.col0 = Color.tintABGR(texd.col0, this.colTint);
            texd.col1 = Color.tintABGR(texd.col1, this.colTint);
            texd.col2 = Color.tintABGR(texd.col2, this.colTint);
            texd.col3 = Color.tintABGR(texd.col3, this.colTint);
        }
    }
}
