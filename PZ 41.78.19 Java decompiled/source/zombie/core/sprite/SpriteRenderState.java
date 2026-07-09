// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.sprite;

import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Styles.TransparentStyle;
import zombie.core.opengl.GLState;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.TextureFBO;
import zombie.input.Mouse;
import zombie.iso.PlayerCamera;

public final class SpriteRenderState extends GenericSpriteRenderState {
    public TextureFBO fbo = null;
    public long time;
    public final SpriteRenderStateUI stateUI;
    public int playerIndex;
    public final PlayerCamera[] playerCamera = new PlayerCamera[4];
    public final float[] playerAmbient = new float[4];
    public float maxZoomLevel = 0.0F;
    public float minZoomLevel = 0.0F;
    public final float[] zoomLevel = new float[4];

    public SpriteRenderState(int index) {
        super(index);

        for (int int0 = 0; int0 < 4; int0++) {
            this.playerCamera[int0] = new PlayerCamera(int0);
        }

        this.stateUI = new SpriteRenderStateUI(index);
    }

    @Override
    public void onRendered() {
        super.onRendered();
        this.stateUI.onRendered();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.stateUI.onReady();
    }

    @Override
    public void CheckSpriteSlots() {
        if (this.stateUI.bActive) {
            this.stateUI.CheckSpriteSlots();
        } else {
            super.CheckSpriteSlots();
        }
    }

    @Override
    public void clear() {
        this.stateUI.clear();
        super.clear();
    }

    /**
     * Returns either the UI state, or this state. Depends on the value of stateUI.bActive
     */
    public GenericSpriteRenderState getActiveState() {
        return (GenericSpriteRenderState)(this.stateUI.bActive ? this.stateUI : this);
    }

    public void prePopulating() {
        this.clear();
        this.fbo = Core.getInstance().getOffscreenBuffer();

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null) {
                this.playerCamera[int0].initFromIsoCamera(int0);
                this.playerAmbient[int0] = RenderSettings.getInstance().getAmbientForPlayer(int0);
                this.zoomLevel[int0] = Core.getInstance().getZoom(int0);
                this.maxZoomLevel = Core.getInstance().getMaxZoom();
                this.minZoomLevel = Core.getInstance().getMinZoom();
            }
        }

        this.defaultStyle = TransparentStyle.instance;
        this.bCursorVisible = Mouse.isCursorVisible();
        GLState.startFrame();
    }
}
