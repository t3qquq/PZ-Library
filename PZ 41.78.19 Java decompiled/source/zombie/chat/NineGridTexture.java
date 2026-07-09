// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

/**
 * Turbo
 */
public class NineGridTexture {
    private Texture topLeft;
    private Texture topMid;
    private Texture topRight;
    private Texture left;
    private Texture mid;
    private Texture right;
    private Texture botLeft;
    private Texture botMid;
    private Texture botRight;
    private int outer;

    public NineGridTexture(String base, int _outer) {
        this.outer = _outer;
        this.topLeft = Texture.getSharedTexture(base + "_topleft");
        this.topMid = Texture.getSharedTexture(base + "_topmid");
        this.topRight = Texture.getSharedTexture(base + "_topright");
        this.left = Texture.getSharedTexture(base + "_left");
        this.mid = Texture.getSharedTexture(base + "_mid");
        this.right = Texture.getSharedTexture(base + "_right");
        this.botLeft = Texture.getSharedTexture(base + "_botleft");
        this.botMid = Texture.getSharedTexture(base + "_botmid");
        this.botRight = Texture.getSharedTexture(base + "_botright");
    }

    public void renderInnerBased(int x, int y, int w, int h, float r, float g, float b, float a) {
        y += 5;
        h -= 7;
        SpriteRenderer.instance.renderi(this.topLeft, x - this.outer, y - this.outer, this.outer, this.outer, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.topMid, x, y - this.outer, w, this.outer, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.topRight, x + w, y - this.outer, this.outer, this.outer, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.left, x - this.outer, y, this.outer, h, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.mid, x, y, w, h, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.right, x + w, y, this.outer, h, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.botLeft, x - this.outer, y + h, this.outer, this.outer, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.botMid, x, y + h, w, this.outer, r, g, b, a, null);
        SpriteRenderer.instance.renderi(this.botRight, x + w, y + h, this.outer, this.outer, r, g, b, a, null);
    }
}
