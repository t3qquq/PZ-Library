// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.textures.Texture;

public class LoadingQueueUI extends UIElement {
    private String strLoadingQueue;
    private String strQueuePlace;
    private static int placeInQueue = -1;
    private Texture arrowBG = null;
    private Texture arrowFG = null;
    private double timerMultiplierAnim = 0.0;
    private int animOffset = -1;

    public LoadingQueueUI() {
        this.strLoadingQueue = Translator.getText("UI_GameLoad_LoadingQueue");
        this.strQueuePlace = Translator.getText("UI_GameLoad_PlaceInQueue");
        this.arrowBG = Texture.getSharedTexture("media/ui/ArrowRight_Disabled.png");
        this.arrowFG = Texture.getSharedTexture("media/ui/ArrowRight.png");
        placeInQueue = -1;
        this.onresize();
    }

    @Override
    public void update() {
    }

    @Override
    public void onresize() {
        this.x = 288.0;
        this.y = 101.0;
        this.width = (float)(Core.getInstance().getScreenWidth() - 2.0 * this.x);
        this.height = (float)(Core.getInstance().getScreenHeight() - 2.0 * this.y);
    }

    @Override
    public void render() {
        this.onresize();
        double double0 = 0.4F;
        double double1 = 0.4F;
        double double2 = 0.4F;
        double double3 = 1.0;
        this.DrawTextureScaledColor(null, 0.0, 0.0, 1.0, (double)this.height, double0, double1, double2, double3);
        this.DrawTextureScaledColor(null, 1.0, 0.0, this.width - 2.0, 1.0, double0, double1, double2, double3);
        this.DrawTextureScaledColor(null, this.width - 1.0, 0.0, 1.0, (double)this.height, double0, double1, double2, double3);
        this.DrawTextureScaledColor(null, 1.0, this.height - 1.0, this.width - 2.0, 1.0, double0, double1, double2, double3);
        this.DrawTextureScaledColor(null, 1.0, 1.0, this.width - 2.0, (double)(this.height - 2.0F), 0.0, 0.0, 0.0, 0.5);
        TextManager.instance.DrawStringCentre(UIFont.Large, this.x + this.width / 2.0F, this.y + 60.0, this.strLoadingQueue, 1.0, 1.0, 1.0, 1.0);
        this.DrawTextureColor(this.arrowBG, (this.width - this.arrowBG.getWidth()) / 2.0F - 15.0F, 120.0, 1.0, 1.0, 1.0, 1.0);
        this.DrawTextureColor(this.arrowBG, (this.width - this.arrowBG.getWidth()) / 2.0F, 120.0, 1.0, 1.0, 1.0, 1.0);
        this.DrawTextureColor(this.arrowBG, (this.width - this.arrowBG.getWidth()) / 2.0F + 15.0F, 120.0, 1.0, 1.0, 1.0, 1.0);
        this.timerMultiplierAnim = this.timerMultiplierAnim + UIManager.getMillisSinceLastRender();
        if (this.timerMultiplierAnim <= 500.0) {
            this.animOffset = Integer.MIN_VALUE;
        } else if (this.timerMultiplierAnim <= 1000.0) {
            this.animOffset = -15;
        } else if (this.timerMultiplierAnim <= 1500.0) {
            this.animOffset = 0;
        } else if (this.timerMultiplierAnim <= 2000.0) {
            this.animOffset = 15;
        } else {
            this.timerMultiplierAnim = 0.0;
        }

        if (this.animOffset != Integer.MIN_VALUE) {
            this.DrawTextureColor(this.arrowFG, (this.width - this.arrowBG.getWidth()) / 2.0F + this.animOffset, 120.0, 1.0, 1.0, 1.0, 1.0);
        }

        if (placeInQueue >= 0) {
            TextManager.instance
                .DrawStringCentre(
                    UIFont.Medium, this.x + this.width / 2.0F, this.y + 180.0, String.format(this.strQueuePlace, placeInQueue), 1.0, 1.0, 1.0, 1.0
                );
        }
    }

    public void setPlaceInQueue(int _placeInQueue) {
        placeInQueue = _placeInQueue;
    }
}
