// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.sprite;

public final class SpriteRenderStateUI extends GenericSpriteRenderState {
    public boolean bActive;

    public SpriteRenderStateUI(int index) {
        super(index);
    }

    @Override
    public void clear() {
        try {
            this.bActive = true;
            super.clear();
        } finally {
            this.bActive = false;
        }
    }
}
