// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.input.Mouse;

public final class ScrollBar extends UIElement {
    public final Color BackgroundColour = new Color(255, 255, 255, 255);
    public final Color ButtonColour = new Color(255, 255, 255, 127);
    public final Color ButtonHighlightColour = new Color(255, 255, 255, 255);
    public boolean IsVerticle = true;
    private int FullLength = 114;
    private int InsideLength = 100;
    private int EndLength = 7;
    private float ButtonInsideLength = 30.0F;
    private int ButtonEndLength = 6;
    private int Thickness = 10;
    private int ButtonThickness = 9;
    private float ButtonOffset = 40.0F;
    private int MouseDragStartPos = 0;
    private float ButtonDragStartPos = 0.0F;
    private Texture BackVertical;
    private Texture TopVertical;
    private Texture BottomVertical;
    private Texture ButtonBackVertical;
    private Texture ButtonTopVertical;
    private Texture ButtonBottomVertical;
    private Texture BackHorizontal;
    private Texture LeftHorizontal;
    private Texture RightHorizontal;
    private Texture ButtonBackHorizontal;
    private Texture ButtonLeftHorizontal;
    private Texture ButtonRightHorizontal;
    private boolean mouseOver = false;
    private boolean BeingDragged = false;
    private UITextBox2 ParentTextBox = null;
    UIEventHandler messageParent;
    private String name;

    public ScrollBar(String _name, UIEventHandler messages, int x_pos, int y_pos, int Length, boolean IsVertical) {
        this.messageParent = messages;
        this.name = _name;
        this.x = x_pos;
        this.y = y_pos;
        this.FullLength = Length;
        this.InsideLength = Length - this.EndLength * 2;
        this.IsVerticle = true;
        this.width = this.Thickness;
        this.height = Length;
        this.ButtonInsideLength = this.height - this.ButtonEndLength * 2;
        this.ButtonOffset = 0.0F;
        this.BackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Middle.png");
        this.TopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Top.png");
        this.BottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Bottom.png");
        this.ButtonBackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Middle.png");
        this.ButtonTopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Top.png");
        this.ButtonBottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bottom.png");
        this.BackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Middle.png");
        this.LeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Bottom.png");
        this.RightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Top.png");
        this.ButtonBackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Middle.png");
        this.ButtonLeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bottom.png");
        this.ButtonRightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Top.png");
    }

    public void SetParentTextBox(UITextBox2 Parent) {
        this.ParentTextBox = Parent;
    }

    /**
     * 
     * @param height the height to set
     */
    @Override
    public void setHeight(double height) {
        super.setHeight(height);
        this.FullLength = (int)height;
        this.InsideLength = (int)height - this.EndLength * 2;
    }

    @Override
    public void render() {
        if (this.IsVerticle) {
            this.DrawTextureScaledCol(this.TopVertical, 0.0, 0.0, this.Thickness, this.EndLength, this.BackgroundColour);
            this.DrawTextureScaledCol(this.BackVertical, 0.0, 0 + this.EndLength, this.Thickness, this.InsideLength, this.BackgroundColour);
            this.DrawTextureScaledCol(this.BottomVertical, 0.0, 0 + this.EndLength + this.InsideLength, this.Thickness, this.EndLength, this.BackgroundColour);
            Color color;
            if (this.mouseOver) {
                color = this.ButtonHighlightColour;
            } else {
                color = this.ButtonColour;
            }

            this.DrawTextureScaledCol(this.ButtonTopVertical, 1.0, (int)this.ButtonOffset + 1, this.ButtonThickness, this.ButtonEndLength, color);
            this.DrawTextureScaledCol(
                this.ButtonBackVertical, 1.0, (int)this.ButtonOffset + 1 + this.ButtonEndLength, this.ButtonThickness, this.ButtonInsideLength, color
            );
            this.DrawTextureScaledCol(
                this.ButtonBottomVertical,
                1.0,
                (int)this.ButtonOffset + 1 + this.ButtonEndLength + this.ButtonInsideLength,
                this.ButtonThickness,
                this.ButtonEndLength,
                color
            );
        }
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        this.mouseOver = true;
        return Boolean.TRUE;
    }

    @Override
    public void onMouseMoveOutside(double dx, double dy) {
        this.mouseOver = false;
    }

    @Override
    public Boolean onMouseUp(double x, double y) {
        this.BeingDragged = false;
        return Boolean.FALSE;
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        boolean boolean0 = false;
        if (y >= this.ButtonOffset && y <= this.ButtonOffset + this.ButtonInsideLength + this.ButtonEndLength * 2) {
            boolean0 = true;
        }

        if (boolean0) {
            this.BeingDragged = true;
            this.MouseDragStartPos = Mouse.getY();
            this.ButtonDragStartPos = this.ButtonOffset;
        } else {
            this.ButtonOffset = (float)(y - (this.ButtonInsideLength + this.ButtonEndLength * 2) / 2.0F);
        }

        if (this.ButtonOffset < 0.0F) {
            this.ButtonOffset = 0.0F;
        }

        if (this.ButtonOffset > this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 1.0F) {
            this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 1.0F;
        }

        return Boolean.FALSE;
    }

    @Override
    public void update() {
        super.update();
        if (this.BeingDragged) {
            int int0 = this.MouseDragStartPos - Mouse.getY();
            this.ButtonOffset = this.ButtonDragStartPos - int0;
            if (this.ButtonOffset < 0.0F) {
                this.ButtonOffset = 0.0F;
            }

            if (this.ButtonOffset > this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0F) {
                this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0F;
            }

            if (!Mouse.isButtonDown(0)) {
                this.BeingDragged = false;
            }
        }

        if (this.ParentTextBox != null) {
            int int1 = TextManager.instance.getFontFromEnum(this.ParentTextBox.font).getLineHeight();
            if (this.ParentTextBox.Lines.size() > this.ParentTextBox.NumVisibleLines) {
                if (this.ParentTextBox.Lines.size() > 0) {
                    int int2 = this.ParentTextBox.NumVisibleLines;
                    if (int2 * int1 > this.ParentTextBox.getHeight().intValue() - this.ParentTextBox.getInset() * 2) {
                        int2--;
                    }

                    float float0 = (float)int2 / this.ParentTextBox.Lines.size();
                    this.ButtonInsideLength = (int)(this.getHeight().intValue() * float0) - this.ButtonEndLength * 2;
                    this.ButtonInsideLength = Math.max(this.ButtonInsideLength, 0.0F);
                    float float1 = this.ButtonInsideLength + this.ButtonEndLength * 2;
                    if (this.ButtonOffset < 0.0F) {
                        this.ButtonOffset = 0.0F;
                    }

                    if (this.ButtonOffset > this.getHeight().intValue() - float1 - 0.0F) {
                        this.ButtonOffset = this.getHeight().intValue() - float1 - 0.0F;
                    }

                    float float2 = this.ButtonOffset / this.getHeight().intValue();
                    this.ParentTextBox.TopLineIndex = (int)(this.ParentTextBox.Lines.size() * float2);
                    int int3 = this.getHeight().intValue();
                    int int4 = int3 - (int)float1;
                    int int5 = int1 * (this.ParentTextBox.Lines.size() - int2);
                    float float3 = (float)int4 / int5;
                    float float4 = this.ButtonOffset / float3;
                    this.ParentTextBox.TopLineIndex = (int)(float4 / int1);
                } else {
                    this.ButtonOffset = 0.0F;
                    this.ButtonInsideLength = this.getHeight().intValue() - this.ButtonEndLength * 2;
                    this.ParentTextBox.TopLineIndex = 0;
                }
            } else {
                this.ButtonOffset = 0.0F;
                this.ButtonInsideLength = this.getHeight().intValue() - this.ButtonEndLength * 2;
                this.ParentTextBox.TopLineIndex = 0;
            }
        }
    }

    public void scrollToBottom() {
        this.ButtonOffset = this.getHeight().intValue() - (this.ButtonInsideLength + this.ButtonEndLength * 2) - 0.0F;
    }
}
