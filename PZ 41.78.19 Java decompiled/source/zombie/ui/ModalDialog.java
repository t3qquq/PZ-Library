// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.gameStates.IngameState;

public final class ModalDialog extends NewWindow {
    public boolean bYes = false;
    public String Name;
    UIEventHandler handler = null;
    public boolean Clicked = false;

    public ModalDialog(String name, String help, boolean bYesNo) {
        super(Core.getInstance().getOffscreenWidth(0) / 2, Core.getInstance().getOffscreenHeight(0) / 2, 470, 10, false);
        this.Name = name;
        this.ResizeToFitY = false;
        this.IgnoreLossControl = true;
        TextBox textBox = new TextBox(UIFont.Medium, 0, 0, 450, help);
        textBox.Centred = true;
        textBox.ResizeParent = true;
        textBox.update();
        this.Nest(textBox, 20, 10, 20, 10);
        this.update();
        this.height *= 1.3F;
        if (bYesNo) {
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 - 40), (float)(this.getHeight().intValue() - 18), "Yes", "Yes"));
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 + 40), (float)(this.getHeight().intValue() - 18), "No", "No"));
        } else {
            this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2), (float)(this.getHeight().intValue() - 18), "Ok", "Ok"));
        }

        this.x = this.x - this.width / 2.0F;
        this.y = this.y - this.height / 2.0F;
    }

    @Override
    public void ButtonClicked(String name) {
        if (this.handler != null) {
            this.handler.ModalClick(this.Name, name);
            this.setVisible(false);
        } else {
            if (name.equals("Ok")) {
                UIManager.getSpeedControls().SetCurrentGameSpeed(4);
                this.Clicked(name);
                this.Clicked = true;
                this.bYes = true;
                this.setVisible(false);
                IngameState.instance.Paused = false;
            }

            if (name.equals("Yes")) {
                UIManager.getSpeedControls().SetCurrentGameSpeed(4);
                this.Clicked(name);
                this.Clicked = true;
                this.bYes = true;
                this.setVisible(false);
                IngameState.instance.Paused = false;
            }

            if (name.equals("No")) {
                UIManager.getSpeedControls().SetCurrentGameSpeed(4);
                this.Clicked(name);
                this.Clicked = true;
                this.bYes = false;
                this.setVisible(false);
                IngameState.instance.Paused = false;
            }
        }
    }

    public void Clicked(String name) {
        if (this.Name.equals("Sleep") && name.equals("Yes")) {
            float float0 = 12.0F * IsoPlayer.getInstance().getStats().fatigue;
            if (float0 < 7.0F) {
                float0 = 7.0F;
            }

            float0 += GameTime.getInstance().getTimeOfDay();
            if (float0 >= 24.0F) {
                float0 -= 24.0F;
            }

            IsoPlayer.getInstance().setForceWakeUpTime((int)float0);
            IsoPlayer.getInstance().setAsleepTime(0.0F);
            TutorialManager.instance.StealControl = true;
            IsoPlayer.getInstance().setAsleep(true);
            UIManager.setbFadeBeforeUI(true);
            UIManager.FadeOut(4.0);
            UIManager.getSpeedControls().SetCurrentGameSpeed(3);

            try {
                GameWindow.save(true);
            } catch (FileNotFoundException fileNotFoundException) {
                Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, null, fileNotFoundException);
            } catch (IOException iOException) {
                Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, null, iOException);
            }
        }

        UIManager.Modal.setVisible(false);
    }
}
