// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

import org.lwjgl.opengl.GL14;

public final class UIFBOStyle extends AbstractStyle {
    public static final UIFBOStyle instance = new UIFBOStyle();

    @Override
    public void setupState() {
        GL14.glBlendFuncSeparate(770, 771, 1, 771);
    }

    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }

    @Override
    public int getStyleID() {
        return 1;
    }

    @Override
    public boolean getRenderSprite() {
        return true;
    }
}
