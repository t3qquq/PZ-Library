// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion;

import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSpriteManager;

public class ErosionClient {
    public static ErosionClient instance;

    public ErosionClient(IsoSpriteManager spriteManager, boolean var2) {
        instance = this;
        new ErosionIceQueen(spriteManager);
        ErosionRegions.init();
    }

    public static void Reset() {
        instance = null;
    }
}
