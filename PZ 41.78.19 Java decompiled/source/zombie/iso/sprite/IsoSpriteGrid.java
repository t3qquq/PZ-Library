// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

/**
 * Turbo
 */
public final class IsoSpriteGrid {
    private IsoSprite[] sprites;
    private int width;
    private int height;

    public IsoSpriteGrid(int w, int h) {
        this.sprites = new IsoSprite[w * h];
        this.width = w;
        this.height = h;
    }

    public IsoSprite getAnchorSprite() {
        return this.sprites.length > 0 ? this.sprites[0] : null;
    }

    public IsoSprite getSprite(int x, int y) {
        return this.getSpriteFromIndex(y * this.width + x);
    }

    public void setSprite(int x, int y, IsoSprite sprite) {
        this.sprites[y * this.width + x] = sprite;
    }

    public int getSpriteIndex(IsoSprite sprite) {
        for (int int0 = 0; int0 < this.sprites.length; int0++) {
            IsoSprite _sprite = this.sprites[int0];
            if (_sprite != null && _sprite == sprite) {
                return int0;
            }
        }

        return -1;
    }

    public int getSpriteGridPosX(IsoSprite sprite) {
        int int0 = this.getSpriteIndex(sprite);
        return int0 >= 0 ? int0 % this.width : -1;
    }

    public int getSpriteGridPosY(IsoSprite sprite) {
        int int0 = this.getSpriteIndex(sprite);
        return int0 >= 0 ? int0 / this.width : -1;
    }

    public IsoSprite getSpriteFromIndex(int index) {
        return index >= 0 && index < this.sprites.length ? this.sprites[index] : null;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean validate() {
        for (int int0 = 0; int0 < this.sprites.length; int0++) {
            if (this.sprites[int0] == null) {
                return false;
            }
        }

        return true;
    }

    public int getSpriteCount() {
        return this.sprites.length;
    }

    public IsoSprite[] getSprites() {
        return this.sprites;
    }
}
