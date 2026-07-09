// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

public final class ActionProgressBar extends UIElement {
    Texture background;
    Texture foreground;
    float deltaValue = 1.0F;
    float animationProgress = 0.0F;
    public int delayHide = 0;
    private final int offsetX;
    private final int offsetY;

    public ActionProgressBar(int x, int y) {
        this.background = Texture.getSharedTexture("BuildBar_Bkg");
        this.foreground = Texture.getSharedTexture("BuildBar_Bar");
        this.offsetX = x;
        this.offsetY = y;
        this.width = this.background.getWidth();
        this.height = this.background.getHeight();
        this.followGameWorld = true;
    }

    @Override
    public void render() {
        if (this.isVisible() && UIManager.VisibleAllUI) {
            this.DrawUVSliceTexture(this.background, 0.0, 0.0, this.background.getWidth(), this.background.getHeight(), Color.white, 0.0, 0.0, 1.0, 1.0);
            if (this.deltaValue == Float.POSITIVE_INFINITY) {
                if (this.animationProgress < 0.5F) {
                    this.DrawUVSliceTexture(
                        this.foreground,
                        3.0,
                        0.0,
                        this.foreground.getWidth(),
                        this.foreground.getHeight(),
                        Color.white,
                        0.0,
                        0.0,
                        this.animationProgress * 2.0F,
                        1.0
                    );
                } else {
                    this.DrawUVSliceTexture(
                        this.foreground,
                        3.0,
                        0.0,
                        this.foreground.getWidth(),
                        this.foreground.getHeight(),
                        Color.white,
                        (this.animationProgress - 0.5F) * 2.0F,
                        0.0,
                        1.0,
                        1.0
                    );
                }
            } else {
                this.DrawUVSliceTexture(
                    this.foreground, 3.0, 0.0, this.foreground.getWidth(), this.foreground.getHeight(), Color.white, 0.0, 0.0, this.deltaValue, 1.0
                );
            }
        }
    }

    public void setValue(float delta) {
        this.deltaValue = delta;
    }

    public float getValue() {
        return this.deltaValue;
    }

    public void update(int nPlayer) {
        if (this.deltaValue == Float.POSITIVE_INFINITY) {
            this.animationProgress = this.animationProgress + 0.02F * (GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 60.0F);
            if (this.animationProgress > 1.0F) {
                this.animationProgress = 0.0F;
            }

            this.setVisible(true);
            this.updateScreenPos(nPlayer);
            this.delayHide = 2;
        } else {
            if (this.getValue() > 0.0F && this.getValue() < 1.0F) {
                this.setVisible(true);
                this.delayHide = 2;
            } else if (this.isVisible() && this.delayHide > 0 && --this.delayHide == 0) {
                this.setVisible(false);
            }

            if (!UIManager.VisibleAllUI) {
                this.setVisible(false);
            }

            if (this.isVisible()) {
                this.updateScreenPos(nPlayer);
            }
        }
    }

    private void updateScreenPos(int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player != null) {
            float float0 = IsoUtils.XToScreen(player.getX(), player.getY(), player.getZ(), 0);
            float float1 = IsoUtils.YToScreen(player.getX(), player.getY(), player.getZ(), 0);
            float0 = float0 - IsoCamera.getOffX() - player.offsetX;
            float1 = float1 - IsoCamera.getOffY() - player.offsetY;
            float1 -= 128 / (2 / Core.TileScale);
            float0 /= Core.getInstance().getZoom(int0);
            float1 /= Core.getInstance().getZoom(int0);
            float0 -= this.width / 2.0F;
            float1 -= this.height;
            if (player.getUserNameHeight() > 0) {
                float1 -= player.getUserNameHeight() + 2;
            }

            this.setX(float0 + this.offsetX);
            this.setY(float1 + this.offsetY);
        }
    }
}
