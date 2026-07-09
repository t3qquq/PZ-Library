// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;

public final class TISLogoState extends GameState {
    public float alpha = 0.0F;
    public float alphaStep = 0.02F;
    public float logoDisplayTime = 20.0F;
    public int screenNumber = 1;
    public int stage = 0;
    public float targetAlpha = 0.0F;
    private boolean bNoRender = false;
    private final TISLogoState.LogoElement logoTIS = new TISLogoState.LogoElement("media/ui/TheIndieStoneLogo_Lineart_White.png");
    private final TISLogoState.LogoElement logoFMOD = new TISLogoState.LogoElement("media/ui/FMODLogo.png");
    private final TISLogoState.LogoElement logoGA = new TISLogoState.LogoElement("media/ui/GA-1280-white.png");
    private final TISLogoState.LogoElement logoNW = new TISLogoState.LogoElement("media/ui/NW_Logo_Combined.png");
    private static final int SCREEN_TIS = 1;
    private static final int SCREEN_OTHER = 2;
    private static final int STAGE_FADING_IN_LOGO = 0;
    private static final int STAGE_HOLDING_LOGO = 1;
    private static final int STAGE_FADING_OUT_LOGO = 2;
    private static final int STAGE_EXIT = 3;

    @Override
    public void enter() {
        UIManager.bSuspend = true;
        this.alpha = 0.0F;
        this.targetAlpha = 1.0F;
    }

    @Override
    public void exit() {
        UIManager.bSuspend = false;
    }

    @Override
    public void render() {
        if (this.bNoRender) {
            Core.getInstance().StartFrame();
            SpriteRenderer.instance
                .renderi(null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, null);
            Core.getInstance().EndFrame();
        } else {
            Core.getInstance().StartFrame();
            Core.getInstance().EndFrame();
            boolean boolean0 = UIManager.useUIFBO;
            UIManager.useUIFBO = false;
            Core.getInstance().StartFrameUI();
            SpriteRenderer.instance
                .renderi(null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, null);
            if (this.screenNumber == 1) {
                this.logoTIS.centerOnScreen();
                this.logoTIS.render(this.alpha);
            }

            if (this.screenNumber == 2) {
                this.renderAttribution();
            }

            Core.getInstance().EndFrameUI();
            UIManager.useUIFBO = boolean0;
        }
    }

    private void renderAttribution() {
        int int0 = Core.getInstance().getScreenWidth();
        int int1 = Core.getInstance().getScreenHeight();
        byte byte0 = 50;
        byte byte1 = 3;
        int int2 = (int1 - (byte1 + 1) * byte0) / 3;
        Texture texture = this.logoGA.m_texture;
        if (texture != null && texture.isReady()) {
            int int3 = (int)((float)(texture.getWidth() * int2) / texture.getHeight());
            int int4 = (int0 - int3) / 2;
            this.logoGA.setPos(int4, byte0);
            this.logoGA.setSize(int3, int2);
            this.logoGA.render(this.alpha);
        }

        int int5 = byte0 + int2 + byte0;
        int5 = (int)(int5 + int2 * 0.15F);
        texture = this.logoNW.m_texture;
        if (texture != null && texture.isReady()) {
            float float0 = 0.5F;
            int int6 = (int)(texture.getWidth() * float0 * int2 / texture.getHeight());
            int int7 = (int)(int2 * float0);
            int int8 = (int0 - int6) / 2;
            int int9 = (int2 - int7) / 2;
            this.logoNW.setPos(int8, int5 + int9);
            this.logoNW.setSize(int6, int7);
            this.logoNW.render(this.alpha);
        }

        int5 += int2 + byte0;
        texture = this.logoFMOD.m_texture;
        if (texture != null && texture.isReady()) {
            float float1 = 0.35F;
            int int10 = TextManager.instance.getFontHeight(UIFont.Small);
            int int11 = (int)(int2 * float1 - 16.0F - int10);
            int int12 = (int)(texture.getWidth() * ((float)int11 / texture.getHeight()));
            int int13 = (int0 - int12) / 2;
            int int14 = (int2 - int11) / 2;
            int int15 = int5 + int14 + int11 + 16;
            this.logoFMOD.setPos(int13, int5 + int14);
            this.logoFMOD.setSize(int12, int11);
            this.logoFMOD.render(this.alpha);
            String string = "Made with FMOD Studio by Firelight Technologies Pty Ltd.";
            TextManager.instance.DrawStringCentre(int0 / 2.0, int15, string, 1.0, 1.0, 1.0, this.alpha);
        }
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
            this.stage = 3;
        }

        if (this.stage == 0) {
            this.targetAlpha = 1.0F;
            if (this.alpha == 1.0F) {
                this.stage = 1;
                this.logoDisplayTime = 20.0F;
            }
        }

        if (this.stage == 1) {
            this.logoDisplayTime = this.logoDisplayTime - GameTime.getInstance().getMultiplier() / 1.6F;
            if (this.logoDisplayTime <= 0.0F) {
                this.stage = 2;
            }
        }

        if (this.stage == 2) {
            this.targetAlpha = 0.0F;
            if (this.alpha == 0.0F) {
                if (this.screenNumber == 1) {
                    this.screenNumber = 2;
                    this.stage = 0;
                } else {
                    this.stage = 3;
                }
            }
        }

        if (this.stage == 3) {
            this.targetAlpha = 0.0F;
            if (this.alpha == 0.0F) {
                this.bNoRender = true;
                return GameStateMachine.StateAction.Continue;
            }
        }

        if (this.alpha < this.targetAlpha) {
            this.alpha = this.alpha + this.alphaStep * GameTime.getInstance().getMultiplier();
            if (this.alpha > this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        } else if (this.alpha > this.targetAlpha) {
            this.alpha = this.alpha - this.alphaStep * GameTime.getInstance().getMultiplier();
            if (this.stage == 3) {
                this.alpha = this.alpha - this.alphaStep * GameTime.getInstance().getMultiplier();
            }

            if (this.alpha < this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }

        return GameStateMachine.StateAction.Remain;
    }

    private static final class LogoElement {
        Texture m_texture;
        int m_x;
        int m_y;
        int m_width;
        int m_height;

        LogoElement(String string) {
            this.m_texture = Texture.getSharedTexture(string);
            if (this.m_texture != null) {
                this.m_width = this.m_texture.getWidth();
                this.m_height = this.m_texture.getHeight();
            }
        }

        void centerOnScreen() {
            this.m_x = (Core.getInstance().getScreenWidth() - this.m_width) / 2;
            this.m_y = (Core.getInstance().getScreenHeight() - this.m_height) / 2;
        }

        void setPos(int int0, int int1) {
            this.m_x = int0;
            this.m_y = int1;
        }

        void setSize(int int0, int int1) {
            this.m_width = int0;
            this.m_height = int1;
        }

        void render(float float0) {
            if (this.m_texture != null && this.m_texture.isReady()) {
                SpriteRenderer.instance.renderi(this.m_texture, this.m_x, this.m_y, this.m_width, this.m_height, 1.0F, 1.0F, 1.0F, float0, null);
            }
        }
    }
}
