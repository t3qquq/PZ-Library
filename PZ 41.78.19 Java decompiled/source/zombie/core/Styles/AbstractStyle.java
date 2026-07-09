// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

/**
 * Convenient base class implementation of
 */
public abstract class AbstractStyle implements Style {
    private static final long serialVersionUID = 1L;

    /**
     * Description copied from interface: Style
     * @return boolean
     */
    @Override
    public boolean getRenderSprite() {
        return false;
    }

    /**
     * @return the style's alpha operation
     */
    @Override
    public AlphaOp getAlphaOp() {
        return null;
    }

    /**
     * @return the style's ID, which affects its rendering order
     */
    @Override
    public int getStyleID() {
        return this.hashCode();
    }

    /**
     * Description copied from interface: Style
     */
    @Override
    public void resetState() {
    }

    /**
     * Description copied from interface: Style
     */
    @Override
    public void setupState() {
    }

    /**
     * Description copied from interface: Style
     * @return the vertex data, or null
     */
    @Override
    public GeometryData build() {
        return null;
    }

    /**
     * Description copied from interface: Style
     */
    @Override
    public void render(int vertexOffset, int indexOffset) {
    }
}
