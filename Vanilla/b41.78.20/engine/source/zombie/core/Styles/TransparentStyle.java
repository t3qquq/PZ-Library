// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

import zombie.IndieGL;

public final class TransparentStyle extends AbstractStyle {
    private static final long serialVersionUID = 1L;
    public static final TransparentStyle instance = new TransparentStyle();

    @Override
    public void setupState() {
        IndieGL.glBlendFuncA(770, 771);
    }

    @Override
    public void resetState() {
    }

    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }

    @Override
    public int getStyleID() {
        return 2;
    }

    @Override
    public boolean getRenderSprite() {
        return true;
    }
}
