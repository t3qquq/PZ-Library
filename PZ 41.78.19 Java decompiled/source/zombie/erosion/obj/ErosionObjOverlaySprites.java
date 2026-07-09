// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.obj;

import zombie.erosion.ErosionMain;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;

public final class ErosionObjOverlaySprites {
    public String name;
    public int stages;
    private ErosionObjOverlaySprites.Stage[] sprites;

    public ErosionObjOverlaySprites(int int0, String string) {
        this.name = string;
        this.stages = int0;
        this.sprites = new ErosionObjOverlaySprites.Stage[this.stages];

        for (int int1 = 0; int1 < this.stages; int1++) {
            this.sprites[int1] = new ErosionObjOverlaySprites.Stage();
        }
    }

    public IsoSprite getSprite(int int1, int int0) {
        return this.sprites[int1].seasons[int0].getSprite();
    }

    public IsoSpriteInstance getSpriteInstance(int int1, int int0) {
        return this.sprites[int1].seasons[int0].getInstance();
    }

    public void setSprite(int int1, String string, int int0) {
        this.sprites[int1].seasons[int0] = new ErosionObjOverlaySprites.Sprite(string);
    }

    private static final class Sprite {
        private final String sprite;

        public Sprite(String string) {
            this.sprite = string;
        }

        public IsoSprite getSprite() {
            return this.sprite != null ? ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite) : null;
        }

        public IsoSpriteInstance getInstance() {
            return this.sprite != null ? ErosionMain.getInstance().getSpriteManager().getSprite(this.sprite).newInstance() : null;
        }
    }

    private static class Stage {
        public ErosionObjOverlaySprites.Sprite[] seasons = new ErosionObjOverlaySprites.Sprite[6];
    }
}
