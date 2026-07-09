// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.awt.Rectangle;
import java.util.Stack;
import zombie.core.Color;
import zombie.core.textures.Texture;

public final class UIDialoguePanel extends UIElement {
    float alpha = 1.0F;
    Texture dialogBottomLeft = null;
    Texture dialogBottomMiddle = null;
    Texture dialogBottomRight = null;
    Texture dialogLeft = null;
    Texture dialogMiddle = null;
    Texture dialogRight = null;
    Texture titleLeft = null;
    Texture titleMiddle = null;
    Texture titleRight = null;
    public float clientH = 0.0F;
    public float clientW = 0.0F;
    public Stack<Rectangle> nestedItems = new Stack<>();

    public UIDialoguePanel(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.width = float2;
        this.height = float3;
        this.titleLeft = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Left.png");
        this.titleMiddle = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Middle.png");
        this.titleRight = Texture.getSharedTexture("media/ui/Dialog_Titlebar_Right.png");
        this.dialogLeft = Texture.getSharedTexture("media/ui/Dialog_Left.png");
        this.dialogMiddle = Texture.getSharedTexture("media/ui/Dialog_Middle.png");
        this.dialogRight = Texture.getSharedTexture("media/ui/Dialog_Right.png");
        this.dialogBottomLeft = Texture.getSharedTexture("media/ui/Dialog_Bottom_Left.png");
        this.dialogBottomMiddle = Texture.getSharedTexture("media/ui/Dialog_Bottom_Middle.png");
        this.dialogBottomRight = Texture.getSharedTexture("media/ui/Dialog_Bottom_Right.png");
        this.clientW = float2;
        this.clientH = float3;
    }

    public void Nest(UIElement uIElement, int int1, int int2, int int3, int int0) {
        this.AddChild(uIElement);
        this.nestedItems.add(new Rectangle(int0, int1, int2, int3));
        uIElement.setX(int0);
        uIElement.setY(int1);
        uIElement.update();
    }

    @Override
    public void render() {
        this.DrawTextureScaledCol(this.titleLeft, 0.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.titleMiddle, 28.0, 0.0, this.getWidth() - 56.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.titleRight, 0.0 + this.getWidth() - 28.0, 0.0, 28.0, 28.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogLeft, 0.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogMiddle, 78.0, 28.0, this.getWidth() - 156.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogRight, 0.0 + this.getWidth() - 78.0, 28.0, 78.0, this.getHeight() - 100.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogBottomLeft, 0.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(this.dialogBottomMiddle, 78.0, 0.0 + this.getHeight() - 72.0, this.getWidth() - 156.0, 72.0, new Color(255, 255, 255, 100));
        this.DrawTextureScaledCol(
            this.dialogBottomRight, 0.0 + this.getWidth() - 78.0, 0.0 + this.getHeight() - 72.0, 78.0, 72.0, new Color(255, 255, 255, 100)
        );
        super.render();
    }

    @Override
    public void update() {
        super.update();
        int int0 = 0;

        for (Rectangle rectangle : this.nestedItems) {
            UIElement uIElement = this.getControls().get(int0);
            uIElement.setX((float)rectangle.getX());
            uIElement.setY((float)rectangle.getY());
            uIElement.setWidth((int)(this.clientW - (rectangle.getX() + rectangle.getWidth())));
            uIElement.setHeight((int)(this.clientH - (rectangle.getY() + rectangle.getHeight())));
            uIElement.onresize();
            int0++;
        }
    }
}
