// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.sprite;

public final class SpriteRendererStates {
    private SpriteRenderState m_populating = new SpriteRenderState(0);
    private SpriteRenderState m_ready = null;
    private SpriteRenderState m_rendering = new SpriteRenderState(2);
    private SpriteRenderState m_rendered = new SpriteRenderState(1);

    public SpriteRenderState getPopulating() {
        return this.m_populating;
    }

    /**
     * Returns either the UI state, or populating state. Depends on the value of its stateUI.bActive
     */
    public GenericSpriteRenderState getPopulatingActiveState() {
        return this.m_populating.getActiveState();
    }

    public void setPopulating(SpriteRenderState populating) {
        this.m_populating = populating;
    }

    public SpriteRenderState getReady() {
        return this.m_ready;
    }

    public void setReady(SpriteRenderState ready) {
        this.m_ready = ready;
    }

    public SpriteRenderState getRendering() {
        return this.m_rendering;
    }

    /**
     * Returns either the UI state, or rendering state. Depends on the value of its stateUI.bActive
     */
    public GenericSpriteRenderState getRenderingActiveState() {
        return this.m_rendering.getActiveState();
    }

    public void setRendering(SpriteRenderState rendering) {
        this.m_rendering = rendering;
    }

    public SpriteRenderState getRendered() {
        return this.m_rendered;
    }

    public void setRendered(SpriteRenderState rendered) {
        this.m_rendered = rendered;
    }

    public void movePopulatingToReady() {
        this.m_ready = this.m_populating;
        this.m_populating = this.m_rendered;
        this.m_rendered = null;
        this.m_ready.time = System.nanoTime();
        this.m_ready.onReady();
    }

    public void moveReadyToRendering() {
        this.m_rendered = this.m_rendering;
        this.m_rendering = this.m_ready;
        this.m_ready = null;
        this.m_rendering.onRenderAcquired();
    }
}
