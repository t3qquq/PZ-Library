// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
