// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.GameTime;
import zombie.SoundManager;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class SpeedControls extends UIElement {
    public static SpeedControls instance = null;
    public int CurrentSpeed = 1;
    public int SpeedBeforePause = 1;
    public float MultiBeforePause = 1.0F;
    float alpha = 1.0F;
    boolean MouseOver = false;
    public static HUDButton Play;
    public static HUDButton Pause;
    public static HUDButton FastForward;
    public static HUDButton FasterForward;
    public static HUDButton Wait;

    public SpeedControls() {
        this.x = 0.0;
        this.y = 0.0;
        byte byte0 = 2;
        Pause = new SpeedControls.SCButton("Pause", 1.0F, 0.0F, "media/ui/Time_Pause_Off.png", "media/ui/Time_Pause_On.png", this);
        Play = new SpeedControls.SCButton("Play", (float)(Pause.x + Pause.width + byte0), 0.0F, "media/ui/Time_Play_Off.png", "media/ui/Time_Play_On.png", this);
        FastForward = new SpeedControls.SCButton(
            "Fast Forward x 1", (float)(Play.x + Play.width + byte0), 0.0F, "media/ui/Time_FFwd1_Off.png", "media/ui/Time_FFwd1_On.png", this
        );
        FasterForward = new SpeedControls.SCButton(
            "Fast Forward x 2", (float)(FastForward.x + FastForward.width + byte0), 0.0F, "media/ui/Time_FFwd2_Off.png", "media/ui/Time_FFwd2_On.png", this
        );
        Wait = new SpeedControls.SCButton(
            "Wait", (float)(FasterForward.x + FasterForward.width + byte0), 0.0F, "media/ui/Time_Wait_Off.png", "media/ui/Time_Wait_On.png", this
        );
        this.width = (int)Wait.x + Wait.width;
        this.height = Wait.height;
        this.AddChild(Pause);
        this.AddChild(Play);
        this.AddChild(FastForward);
        this.AddChild(FasterForward);
        this.AddChild(Wait);
    }

    @Override
    public void ButtonClicked(String name) {
        GameTime.instance.setMultiplier(1.0F);
        if ("Pause".equals(name)) {
            if (this.CurrentSpeed > 0) {
                this.SetCurrentGameSpeed(0);
            } else {
                this.SetCurrentGameSpeed(5);
            }
        }

        if ("Play".equals(name)) {
            this.SetCurrentGameSpeed(1);
            GameTime.instance.setMultiplier(1.0F);
        }

        if ("Fast Forward x 1".equals(name)) {
            this.SetCurrentGameSpeed(2);
            GameTime.instance.setMultiplier(5.0F);
        }

        if ("Fast Forward x 2".equals(name)) {
            this.SetCurrentGameSpeed(3);
            GameTime.instance.setMultiplier(20.0F);
        }

        if ("Wait".equals(name)) {
            this.SetCurrentGameSpeed(4);
            GameTime.instance.setMultiplier(40.0F);
        }
    }

    public int getCurrentGameSpeed() {
        return !GameClient.bClient && !GameServer.bServer ? this.CurrentSpeed : 1;
    }

    public void SetCorrectIconStates() {
        if (this.CurrentSpeed == 0) {
            super.ButtonClicked("Pause");
        }

        if (this.CurrentSpeed == 1) {
            super.ButtonClicked("Play");
        }

        if (GameTime.instance.getTrueMultiplier() == 5.0F) {
            super.ButtonClicked("Fast Forward x 1");
        }

        if (GameTime.instance.getTrueMultiplier() == 20.0F) {
            super.ButtonClicked("Fast Forward x 2");
        }

        if (GameTime.instance.getTrueMultiplier() == 40.0F) {
            super.ButtonClicked("Wait");
        }
    }

    public void SetCurrentGameSpeed(int NewSpeed) {
        if (this.CurrentSpeed > 0 && NewSpeed == 0) {
            SoundManager.instance.pauseSoundAndMusic();
            SoundManager.instance.setMusicState("PauseMenu");
        } else if (this.CurrentSpeed == 0 && NewSpeed > 0) {
            SoundManager.instance.setMusicState("InGame");
            SoundManager.instance.resumeSoundAndMusic();
        }

        GameTime.instance.setMultiplier(1.0F);
        if (NewSpeed == 0) {
            this.SpeedBeforePause = this.CurrentSpeed;
            this.MultiBeforePause = GameTime.instance.getMultiplier();
        }

        if (NewSpeed == 5) {
            NewSpeed = this.SpeedBeforePause;
            GameTime.instance.setMultiplier(this.MultiBeforePause);
        }

        this.CurrentSpeed = NewSpeed;
        this.SetCorrectIconStates();
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        if (!this.isVisible()) {
            return false;
        } else {
            this.MouseOver = true;
            super.onMouseMove(dx, dy);
            this.SetCorrectIconStates();
            return Boolean.TRUE;
        }
    }

    @Override
    public void onMouseMoveOutside(double dx, double dy) {
        super.onMouseMoveOutside(dx, dy);
        this.MouseOver = false;
        this.SetCorrectIconStates();
    }

    @Override
    public void render() {
        super.render();
        if ("Tutorial".equals(Core.GameMode)) {
            Pause.setVisible(false);
            Play.setVisible(false);
            FastForward.setVisible(false);
            FasterForward.setVisible(false);
            Wait.setVisible(false);
        }

        this.SetCorrectIconStates();
    }

    @Override
    public void update() {
        super.update();
        this.SetCorrectIconStates();
    }

    public static final class SCButton extends HUDButton {
        private static final int BORDER = 3;

        public SCButton(String string0, float float1, float float0, String string1, String string2, UIElement uIElement) {
            super(string0, (double)float1, (double)float0, string1, string2, uIElement);
            this.width += 6.0F;
            this.height += 6.0F;
        }

        @Override
        public void render() {
            int int0 = 3;
            if (this.clicked) {
                int0++;
            }

            this.DrawTextureScaledCol(null, 0.0, this.clicked ? 1.0 : 0.0, this.width, this.height, 0.0, 0.0, 0.0, 0.5);
            if (!this.mouseOver && !this.name.equals(this.display.getClickedValue())) {
                this.DrawTextureScaled(this.texture, 3.0, int0, this.texture.getWidth(), this.texture.getHeight(), this.notclickedAlpha);
            } else {
                this.DrawTextureScaled(this.highlight, 3.0, int0, this.highlight.getWidth(), this.highlight.getHeight(), this.clickedalpha);
            }

            if (this.overicon != null) {
                this.DrawTextureScaled(this.overicon, 3.0, int0, this.overicon.getWidth(), this.overicon.getHeight(), 1.0);
            }
        }
    }
}
