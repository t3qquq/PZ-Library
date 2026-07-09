// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.Stack;
import org.lwjgl.util.Rectangle;
import zombie.core.textures.Texture;

public class NewWindow extends UIElement {
    public int clickX = 0;
    public int clickY = 0;
    public int clientH = 0;
    public int clientW = 0;
    public boolean Movable = true;
    public boolean moving = false;
    public int ncclientH = 0;
    public int ncclientW = 0;
    public Stack<Rectangle> nestedItems = new Stack<>();
    public boolean ResizeToFitY = true;
    float alpha = 1.0F;
    Texture dialogBottomLeft = null;
    Texture dialogBottomMiddle = null;
    Texture dialogBottomRight = null;
    Texture dialogLeft = null;
    Texture dialogMiddle = null;
    Texture dialogRight = null;
    Texture titleCloseIcon = null;
    Texture titleLeft = null;
    Texture titleMiddle = null;
    Texture titleRight = null;
    HUDButton closeButton = null;

    public NewWindow(int x, int y, int width, int height, boolean bHasClose) {
        this.x = x;
        this.y = y;
        if (width < 156) {
            width = 156;
        }

        if (height < 78) {
            height = 78;
        }

        this.width = width;
        this.height = height;
        this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
        this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
        this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
        this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
        this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
        this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
        this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
        this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
        this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
        if (bHasClose) {
            this.closeButton = new HUDButton(
                "close",
                (float)(width - 16),
                2.0F,
                "media/ui/Dialog_Titlebar_CloseIcon.png",
                "media/ui/Dialog_Titlebar_CloseIcon.png",
                "media/ui/Dialog_Titlebar_CloseIcon.png",
                this
            );
            this.AddChild(this.closeButton);
        }

        this.clientW = width;
        this.clientH = height;
    }

    public void Nest(UIElement el, int t, int r, int b, int l) {
        this.AddChild(el);
        this.nestedItems.add(new Rectangle(l, t, r, b));
        el.setX(l);
        el.setY(t);
        el.update();
    }

    @Override
    public void ButtonClicked(String name) {
        super.ButtonClicked(name);
        if (name.equals("close")) {
            this.setVisible(false);
        }
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            super.onMouseDown(x, y);
            if (y < 18.0) {
                this.clickX = (int)x;
                this.clickY = (int)y;
                if (this.Movable) {
                    this.moving = true;
                }

                this.setCapture(true);
            }

            return Boolean.TRUE;
        }
    }

    public void setMovable(boolean bMoveable) {
        this.Movable = bMoveable;
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            super.onMouseMove(dx, dy);
            if (this.moving) {
                this.setX(this.getX() + dx);
                this.setY(this.getY() + dy);
            }

            return Boolean.FALSE;
        }
    }

    @Override
    public void onMouseMoveOutside(double dx, double dy) {
        if (this.isVisible()) {
            super.onMouseMoveOutside(dx, dy);
            if (this.moving) {
                this.setX(this.getX() + dx);
                this.setY(this.getY() + dy);
            }
        }
    }

    @Override
    public Boolean onMouseUp(double x, double y) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            super.onMouseUp(x, y);
            this.moving = false;
            this.setCapture(false);
            return Boolean.TRUE;
        }
    }

    @Override
    public void render() {
        float float0 = 0.8F * this.alpha;
        byte byte0 = 0;
        int int0 = 0;
        this.DrawTexture(this.titleLeft, byte0, int0, float0);
        this.DrawTexture(this.titleRight, this.getWidth() - this.titleRight.getWidth(), int0, float0);
        this.DrawTextureScaled(
            this.titleMiddle, this.titleLeft.getWidth(), int0, this.getWidth() - this.titleLeft.getWidth() * 2, this.titleMiddle.getHeight(), float0
        );
        int0 += this.titleRight.getHeight();
        this.DrawTextureScaled(
            this.dialogLeft, byte0, int0, this.dialogLeft.getWidth(), this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(), float0
        );
        this.DrawTextureScaled(
            this.dialogMiddle,
            this.dialogLeft.getWidth(),
            int0,
            this.getWidth() - this.dialogRight.getWidth() * 2,
            this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(),
            float0
        );
        this.DrawTextureScaled(
            this.dialogRight,
            this.getWidth() - this.dialogRight.getWidth(),
            int0,
            this.dialogLeft.getWidth(),
            this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight(),
            float0
        );
        int0 = (int)(int0 + (this.getHeight() - this.titleLeft.getHeight() - this.dialogBottomLeft.getHeight()));
        this.DrawTextureScaled(
            this.dialogBottomMiddle,
            this.dialogBottomLeft.getWidth(),
            int0,
            this.getWidth() - this.dialogBottomLeft.getWidth() * 2,
            this.dialogBottomMiddle.getHeight(),
            float0
        );
        this.DrawTexture(this.dialogBottomLeft, byte0, int0, float0);
        this.DrawTexture(this.dialogBottomRight, this.getWidth() - this.dialogBottomRight.getWidth(), int0, float0);
        super.render();
    }

    @Override
    public void update() {
        super.update();
        if (this.closeButton != null) {
            this.closeButton.setX(4.0);
            this.closeButton.setY(3.0);
        }

        int int0 = 0;
        if (!this.ResizeToFitY) {
            for (Rectangle rectangle0 : this.nestedItems) {
                UIElement uIElement0 = this.getControls().get(int0);
                if (uIElement0 != this.closeButton) {
                    uIElement0.setX(rectangle0.getX());
                    uIElement0.setY(rectangle0.getY());
                    uIElement0.setWidth(this.clientW - (rectangle0.getX() + rectangle0.getWidth()));
                    uIElement0.setHeight(this.clientH - (rectangle0.getY() + rectangle0.getHeight()));
                    uIElement0.onresize();
                    int0++;
                }
            }
        } else {
            int int1 = 100000;
            int int2 = 100000;
            float float0 = 0.0F;
            float float1 = 0.0F;

            for (Rectangle rectangle1 : this.nestedItems) {
                UIElement uIElement1 = this.getControls().get(int0);
                if (uIElement1 != this.closeButton) {
                    if (int1 > uIElement1.getAbsoluteX()) {
                        int1 = uIElement1.getAbsoluteX().intValue();
                    }

                    if (int2 > uIElement1.getAbsoluteX()) {
                        int2 = uIElement1.getAbsoluteX().intValue();
                    }

                    if (float0 < uIElement1.getWidth()) {
                        float0 = uIElement1.getWidth().intValue();
                    }

                    if (float1 < uIElement1.getHeight()) {
                        float1 = uIElement1.getHeight().intValue();
                    }

                    int0++;
                }
            }

            float1 += 50.0F;
            this.height = float1;
        }
    }
}
