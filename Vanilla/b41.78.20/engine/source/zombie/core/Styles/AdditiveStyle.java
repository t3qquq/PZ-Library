// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

import zombie.IndieGL;

public final class AdditiveStyle extends AbstractStyle {
    private static final long serialVersionUID = 1L;
    public static final AdditiveStyle instance = new AdditiveStyle();

    @Override
    public void setupState() {
        IndieGL.glBlendFuncA(1, 771);
    }

    @Override
    public void resetState() {
        IndieGL.glBlendFuncA(770, 771);
    }

    @Override
    public AlphaOp getAlphaOp() {
        return AlphaOp.KEEP;
    }

    @Override
    public int getStyleID() {
        return 3;
    }

    @Override
    public boolean getRenderSprite() {
        return true;
    }
}
