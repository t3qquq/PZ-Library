// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import gnu.trove.list.array.TIntArrayList;
import java.util.Stack;
import org.lwjglx.input.Keyboard;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.math.PZMath;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.input.Mouse;

public class UITextBox2 extends UIElement {
    public static boolean ConsoleHasFocus = false;
    public Stack<String> Lines = new Stack<>();
    public UINineGrid Frame = null;
    public String Text = "";
    public boolean Centred = false;
    public Color StandardFrameColour = new Color(50, 50, 50, 212);
    public Color TextEntryFrameColour = new Color(50, 50, 127, 212);
    public Color TextEntryCursorColour = new Color(170, 170, 220, 240);
    public Color TextEntryCursorColour2 = new Color(100, 100, 220, 160);
    public Color NuetralColour = new Color(0, 0, 255, 33);
    public Color NuetralColour2 = new Color(127, 0, 255, 33);
    public Color BadColour = new Color(255, 0, 0, 33);
    public Color GoodColour = new Color(0, 255, 33);
    public boolean DoingTextEntry = false;
    public int TextEntryCursorPos = 0;
    public int TextEntryMaxLength = 2000;
    public boolean IsEditable = false;
    public boolean IsSelectable = false;
    public int CursorLine = 0;
    public boolean multipleLine = false;
    public TIntArrayList TextOffsetOfLineStart = new TIntArrayList();
    public int ToSelectionIndex = 0;
    public String internalText = "";
    public String maskChr = "*";
    public boolean bMask = false;
    public boolean ignoreFirst;
    UIFont font;
    int[] HighlightLines = new int[1000];
    boolean HasFrame = false;
    int NumVisibleLines = 0;
    int TopLineIndex = 0;
    int BlinkFramesOn = 12;
    int BlinkFramesOff = 8;
    float BlinkFrame = this.BlinkFramesOn;
    boolean BlinkState = true;
    private ColorInfo textColor = new ColorInfo();
    private int EdgeSize = 5;
    private boolean SelectingRange = false;
    private int maxTextLength = -1;
    private boolean forceUpperCase = false;
    private int XOffset = 0;
    private int maxLines = 1;
    private boolean onlyNumbers = false;
    private Texture clearButtonTexture;
    private boolean bClearButton = false;
    private UITransition clearButtonTransition;
    public boolean bAlwaysPaginate = true;
    public boolean bTextChanged = false;
    private int paginateWidth = -1;
    private UIFont paginateFont = null;

    public UITextBox2(UIFont _font, int x, int y, int width, int height, String text, boolean _HasFrame) {
        this.font = _font;
        this.x = x;
        this.y = y;
        this.SetText(text);
        this.width = width;
        this.height = height;
        this.NumVisibleLines = 10;
        this.TopLineIndex = 0;
        Core.CurrentTextEntryBox = this;

        for (int int0 = 0; int0 < 1000; int0++) {
            this.HighlightLines[int0] = 0;
        }

        this.HasFrame = _HasFrame;
        if (_HasFrame) {
            this.Frame = new UINineGrid(
                0,
                0,
                width,
                height,
                this.EdgeSize,
                this.EdgeSize,
                this.EdgeSize,
                this.EdgeSize,
                "media/ui/Box_TopLeft.png",
                "media/ui/Box_Top.png",
                "media/ui/Box_TopRight.png",
                "media/ui/Box_Left.png",
                "media/ui/Box_Center.png",
                "media/ui/Box_Right.png",
                "media/ui/Box_BottomLeft.png",
                "media/ui/Box_Bottom.png",
                "media/ui/Box_BottomRight.png"
            );
            this.AddChild(this.Frame);
        }

        this.Paginate();
        this.DoingTextEntry = false;
        this.TextEntryMaxLength = 2000;
        this.TextEntryCursorPos = 0;
        this.ToSelectionIndex = this.TextEntryCursorPos;
        this.IsEditable = false;
        Keyboard.enableRepeatEvents(true);
        this.clearButtonTexture = Texture.getSharedTexture("media/ui/Panel_Icon_Close.png");
    }

    public void ClearHighlights() {
        for (int int0 = 0; int0 < 1000; int0++) {
            this.HighlightLines[int0] = 0;
        }
    }

    public void setMasked(boolean b) {
        if (this.bMask != b) {
            this.bMask = b;
            if (this.bMask) {
                this.Text = this.maskChr.repeat(this.internalText.length());
            } else {
                this.Text = this.internalText;
            }
        }
    }

    public boolean isMasked() {
        return this.bMask;
    }

    @Override
    public void onresize() {
        this.Paginate();
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !(this.Parent.maxDrawHeight <= this.y)) {
                if (this.bMask) {
                    if (this.internalText.length() != this.Text.length()) {
                        this.Text = this.maskChr.repeat(this.internalText.length());
                    }
                } else {
                    this.Text = this.internalText;
                }

                super.render();
                this.Paginate();
                int int0 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                int int1 = this.getInset();
                this.keepCursorVisible();
                int int2 = (int)this.width - int1;
                if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty()) {
                    int2 -= 2 + this.clearButtonTexture.getWidth() + 2;
                    float float0 = 0.5F;
                    if (!this.SelectingRange && this.isMouseOver() && Mouse.getXA() >= this.getAbsoluteX() + int2) {
                        float0 = 1.0F;
                    }

                    this.clearButtonTransition.setFadeIn(float0 == 1.0F);
                    this.clearButtonTransition.update();
                    this.DrawTexture(
                        this.clearButtonTexture,
                        this.width - int1 - 2.0F - this.clearButtonTexture.getWidth(),
                        int1 + (int0 - this.clearButtonTexture.getHeight()) / 2,
                        float0 * this.clearButtonTransition.fraction() + 0.35F * (1.0F - this.clearButtonTransition.fraction())
                    );
                }

                Double double0 = this.clampToParentX(this.getAbsoluteX().intValue() + int1);
                Double double1 = this.clampToParentX(this.getAbsoluteX().intValue() + int2);
                Double double2 = this.clampToParentY(this.getAbsoluteY().intValue() + int1);
                Double double3 = this.clampToParentY(this.getAbsoluteY().intValue() + (int)this.height - int1);
                this.setStencilRect(
                    double0.intValue() - this.getAbsoluteX().intValue(),
                    double2.intValue() - this.getAbsoluteY().intValue(),
                    double1.intValue() - double0.intValue(),
                    double3.intValue() - double2.intValue()
                );
                if (this.Lines.size() > 0) {
                    int int3 = int1;

                    for (int int4 = this.TopLineIndex; int4 < this.TopLineIndex + this.NumVisibleLines && int4 < this.Lines.size(); int4++) {
                        if (this.Lines.get(int4) != null) {
                            if (int4 >= 0 && int4 < this.HighlightLines.length) {
                                if (this.HighlightLines[int4] == 1) {
                                    this.DrawTextureScaledCol(null, int1 - 1, int3, this.getWidth().intValue() - int1 * 2 + 2, int0, this.NuetralColour);
                                } else if (this.HighlightLines[int4] == 2) {
                                    this.DrawTextureScaledCol(null, int1 - 1, int3, this.getWidth().intValue() - int1 * 2 + 2, int0, this.NuetralColour2);
                                } else if (this.HighlightLines[int4] == 3) {
                                    this.DrawTextureScaledCol(null, int1 - 1, int3, this.getWidth().intValue() - int1 * 2 + 2, int0, this.BadColour);
                                } else if (this.HighlightLines[int4] == 4) {
                                    this.DrawTextureScaledCol(null, int1 - 1, int3, this.getWidth().intValue() - int1 * 2 + 2, int0, this.GoodColour);
                                }
                            }

                            String string0 = this.Lines.get(int4);
                            if (this.Centred) {
                                TextManager.instance
                                    .DrawStringCentre(
                                        this.font,
                                        this.getAbsoluteX().intValue() + this.getWidth() / 2.0 + int1,
                                        this.getAbsoluteY().intValue() + int3,
                                        string0,
                                        this.textColor.r,
                                        this.textColor.g,
                                        this.textColor.b,
                                        1.0
                                    );
                            } else {
                                TextManager.instance
                                    .DrawString(
                                        this.font,
                                        -this.XOffset + this.getAbsoluteX().intValue() + int1,
                                        this.getAbsoluteY().intValue() + int3,
                                        string0,
                                        this.textColor.r,
                                        this.textColor.g,
                                        this.textColor.b,
                                        1.0
                                    );
                            }

                            int3 += int0;
                        }
                    }
                }

                ConsoleHasFocus = this.DoingTextEntry;
                if (this.TextEntryCursorPos > this.Text.length()) {
                    this.TextEntryCursorPos = this.Text.length();
                }

                if (this.ToSelectionIndex > this.Text.length()) {
                    this.ToSelectionIndex = this.Text.length();
                }

                this.CursorLine = this.toDisplayLine(this.TextEntryCursorPos);
                if (this.DoingTextEntry) {
                    AngelCodeFont angelCodeFont = TextManager.instance.getFontFromEnum(this.font);
                    if (this.BlinkState) {
                        int int5 = 0;
                        if (this.Lines.size() > 0) {
                            int int6 = this.TextEntryCursorPos - this.TextOffsetOfLineStart.get(this.CursorLine);
                            int6 = Math.min(int6, this.Lines.get(this.CursorLine).length());
                            int5 = angelCodeFont.getWidth(this.Lines.get(this.CursorLine), 0, int6 - 1, true);
                            if (int5 > 0) {
                                int5--;
                            }
                        }

                        this.DrawTextureScaledCol(
                            Texture.getWhite(), -this.XOffset + int1 + int5, int1 + this.CursorLine * int0, 1.0, int0, this.TextEntryCursorColour
                        );
                    }

                    if (this.Lines.size() > 0 && this.ToSelectionIndex != this.TextEntryCursorPos) {
                        int int7 = Math.min(this.TextEntryCursorPos, this.ToSelectionIndex);
                        int int8 = Math.max(this.TextEntryCursorPos, this.ToSelectionIndex);
                        int int9 = this.toDisplayLine(int7);
                        int int10 = this.toDisplayLine(int8);

                        for (int int11 = int9; int11 <= int10; int11++) {
                            int int12 = this.TextOffsetOfLineStart.get(int11);
                            int int13 = int12 + this.Lines.get(int11).length();
                            int12 = Math.max(int12, int7);
                            int13 = Math.min(int13, int8);
                            String string1 = this.Lines.get(int11);
                            int int14 = angelCodeFont.getWidth(string1, 0, int12 - this.TextOffsetOfLineStart.get(int11) - 1, true);
                            int int15 = angelCodeFont.getWidth(string1, 0, int13 - this.TextOffsetOfLineStart.get(int11) - 1, true);
                            this.DrawTextureScaledCol(null, -this.XOffset + int1 + int14, int1 + int11 * int0, int15 - int14, int0, this.TextEntryCursorColour2);
                        }
                    }
                }

                this.clearStencilRect();
                if (StencilLevel > 0) {
                    this.repaintStencilRect(
                        double0.intValue() - this.getAbsoluteX().intValue(),
                        double2.intValue() - this.getAbsoluteY().intValue(),
                        double1.intValue() - double0.intValue(),
                        double3.intValue() - double2.intValue()
                    );
                }
            }
        }
    }

    public float getFrameAlpha() {
        return this.Frame.getAlpha();
    }

    public void setFrameAlpha(float alpha) {
        this.Frame.setAlpha(alpha);
    }

    public void setTextColor(ColorInfo newColor) {
        this.textColor = newColor;
    }

    private void keepCursorVisible() {
        if (!this.Lines.isEmpty() && this.DoingTextEntry && !this.multipleLine) {
            if (this.TextEntryCursorPos > this.Text.length()) {
                this.TextEntryCursorPos = this.Text.length();
            }

            String string = this.Lines.get(0);
            int int0 = TextManager.instance.MeasureStringX(this.font, string);
            int int1 = this.getInset();
            int int2 = this.getWidth().intValue() - int1 * 2;
            if (this.bClearButton && this.clearButtonTexture != null) {
                int2 -= 2 + this.clearButtonTexture.getWidth() + 2;
            }

            if (int0 <= int2) {
                this.XOffset = 0;
            } else if (-this.XOffset + int0 < int2) {
                this.XOffset = int0 - int2;
            }

            int int3 = TextManager.instance.MeasureStringX(this.font, string.substring(0, this.TextEntryCursorPos));
            int int4 = -this.XOffset + int1 + int3 - 1;
            if (int4 < int1) {
                this.XOffset = int3;
            } else if (int4 >= int1 + int2) {
                this.XOffset = 0;
                int int5 = this.getCursorPosFromX(int3 - int2);
                this.XOffset = TextManager.instance.MeasureStringX(this.font, string.substring(0, int5));
                int4 = -this.XOffset + int1 + int3 - 1;
                if (int4 >= int1 + int2) {
                    this.XOffset = TextManager.instance.MeasureStringX(this.font, string.substring(0, int5 + 1));
                }

                if (-this.XOffset + int0 < int2) {
                    this.XOffset = int0 - int2;
                }
            }
        } else {
            this.XOffset = 0;
        }
    }

    public String getText() {
        return this.Text;
    }

    public String getInternalText() {
        return this.internalText;
    }

    @Override
    public void update() {
        if (this.maxTextLength > -1 && this.internalText.length() > this.maxTextLength) {
            this.internalText = this.internalText.substring(this.maxTextLength);
        }

        if (this.forceUpperCase) {
            this.internalText = this.internalText.toUpperCase();
        }

        if (this.bMask) {
            if (this.internalText.length() != this.Text.length()) {
                String string = "";

                for (int int0 = 0; int0 < this.internalText.length(); int0++) {
                    string = string + this.maskChr;
                }

                if (this.DoingTextEntry && this.Text != string) {
                    this.resetBlink();
                }

                this.Text = string;
            }
        } else {
            if (this.DoingTextEntry && this.Text != this.internalText) {
                this.resetBlink();
            }

            this.Text = this.internalText;
        }

        this.Paginate();
        int int1 = this.getInset();
        int int2 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
        if (int2 + int1 * 2 > this.getHeight()) {
            this.setHeight(int2 + int1 * 2);
        }

        if (this.Frame != null) {
            this.Frame.setHeight(this.getHeight());
        }

        this.NumVisibleLines = (int)(this.getHeight() - int1 * 2) / int2;
        if (this.BlinkFrame > 0.0F) {
            this.BlinkFrame = this.BlinkFrame - GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 30.0F;
        } else {
            this.BlinkState = !this.BlinkState;
            if (this.BlinkState) {
                this.BlinkFrame = this.BlinkFramesOn;
            } else {
                this.BlinkFrame = this.BlinkFramesOff;
            }
        }

        if (this.NumVisibleLines * int2 + int1 * 2 < this.getHeight().intValue()) {
            if (this.NumVisibleLines < this.Lines.size()) {
                this.setScrollHeight((this.Lines.size() + 1) * int2);
            }

            this.NumVisibleLines++;
        } else {
            this.setScrollHeight(this.Lines.size() * int2);
        }

        if (UIDebugConsole.instance == null || this != UIDebugConsole.instance.OutputLog) {
            this.TopLineIndex = (int)(-this.getYScroll() + int1) / int2;
        }

        this.setYScroll(-this.TopLineIndex * int2);
    }

    private void Paginate() {
        boolean boolean0 = this.bAlwaysPaginate;
        if (!this.bAlwaysPaginate) {
            if (this.paginateFont != this.font) {
                this.paginateFont = this.font;
                boolean0 = true;
            }

            if (this.paginateWidth != this.getWidth().intValue()) {
                this.paginateWidth = this.getWidth().intValue();
                boolean0 = true;
            }

            if (this.bTextChanged) {
                this.bTextChanged = false;
                boolean0 = true;
            }

            if (!boolean0) {
                return;
            }
        }

        this.Lines.clear();
        this.TextOffsetOfLineStart.resetQuick();
        if (!this.Text.isEmpty()) {
            if (!this.multipleLine) {
                this.Lines.add(this.Text);
                this.TextOffsetOfLineStart.add(0);
            } else {
                String[] strings = this.Text.split("\n", -1);
                int int0 = 0;

                for (String string0 : strings) {
                    int int1 = 0;
                    if (string0.length() == 0) {
                        this.Lines.add(this.multipleLine ? "" : " ");
                        this.TextOffsetOfLineStart.add(int0);
                        int0++;
                    } else {
                        while (true) {
                            int int2 = string0.indexOf(" ", int1 + 1);
                            int int3 = int2;
                            if (int2 == -1) {
                                int3 = string0.length();
                            }

                            int int4 = TextManager.instance.MeasureStringX(this.font, string0.substring(0, int3));
                            byte byte0 = 17;
                            if (int4 >= this.getWidth() - this.getInset() * 2 - byte0 && int1 > 0) {
                                String string1 = string0.substring(0, int1);
                                string0 = string0.substring(int1 + 1);
                                this.Lines.add(string1);
                                this.TextOffsetOfLineStart.add(int0);
                                int0 += string1.length() + 1;
                                int2 = 0;
                            } else if (int2 == -1) {
                                this.Lines.add(string0);
                                this.TextOffsetOfLineStart.add(int0);
                                int0 += string0.length() + 1;
                                break;
                            }

                            int1 = int2;
                            if (string0.length() <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getInset() {
        int int0 = 2;
        if (this.HasFrame) {
            int0 = this.EdgeSize;
        }

        return int0;
    }

    public void setEditable(boolean b) {
        this.IsEditable = b;
    }

    public boolean isEditable() {
        return this.IsEditable;
    }

    public void setSelectable(boolean b) {
        this.IsSelectable = b;
    }

    public boolean isSelectable() {
        return this.IsSelectable;
    }

    @Override
    public Boolean onMouseUp(double x, double y) {
        if (!this.isVisible()) {
            return false;
        } else {
            super.onMouseUp(x, y);
            this.SelectingRange = false;
            return Boolean.TRUE;
        }
    }

    @Override
    public void onMouseUpOutside(double x, double y) {
        if (this.isVisible()) {
            super.onMouseUpOutside(x, y);
            this.SelectingRange = false;
        }
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        int int0 = Mouse.getXA();
        int int1 = Mouse.getYA();
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            boolean boolean0 = this.isConsumeMouseEvents();
            this.setConsumeMouseEvents(false);
            Boolean boolean1 = super.onMouseMove(dx, dy);
            this.setConsumeMouseEvents(boolean0);
            if (boolean1) {
                return Boolean.TRUE;
            } else if ((this.IsEditable || this.IsSelectable) && this.SelectingRange) {
                if (this.multipleLine) {
                    int int2 = this.getInset();
                    int int3 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                    this.CursorLine = (int1 - this.getAbsoluteY().intValue() - int2 - this.getYScroll().intValue()) / int3;
                    if (this.CursorLine > this.Lines.size() - 1) {
                        this.CursorLine = this.Lines.size() - 1;
                    }
                }

                this.TextEntryCursorPos = this.getCursorPosFromX((int)(int0 - this.getAbsoluteX()));
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    @Override
    public void onMouseMoveOutside(double dx, double dy) {
        int int0 = Mouse.getXA();
        int int1 = Mouse.getYA();
        if (!Mouse.isButtonDown(0)) {
            this.SelectingRange = false;
        }

        if (this.isVisible()) {
            super.onMouseMoveOutside(dx, dy);
            if ((this.IsEditable || this.IsSelectable) && this.SelectingRange) {
                if (this.multipleLine) {
                    int int2 = this.getInset();
                    int int3 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                    this.CursorLine = (int1 - this.getAbsoluteY().intValue() - int2 - this.getYScroll().intValue()) / int3;
                    if (this.CursorLine < 0) {
                        this.CursorLine = 0;
                    }

                    if (this.CursorLine > this.Lines.size() - 1) {
                        this.CursorLine = this.Lines.size() - 1;
                    }
                }

                this.TextEntryCursorPos = this.getCursorPosFromX((int)(int0 - this.getAbsoluteX()));
            }
        }
    }

    public void focus() {
        if (!this.DoingTextEntry) {
        }

        this.DoingTextEntry = true;
        Core.CurrentTextEntryBox = this;
    }

    public void unfocus() {
        this.DoingTextEntry = false;
        if (Core.CurrentTextEntryBox == this) {
            Core.CurrentTextEntryBox = null;
        }
    }

    public void ignoreFirstInput() {
        this.ignoreFirst = true;
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            if (!this.getControls().isEmpty()) {
                for (int int0 = 0; int0 < this.getControls().size(); int0++) {
                    UIElement uIElement = this.getControls().get(int0);
                    if (uIElement != this.Frame && uIElement.isMouseOver()) {
                        return uIElement.onMouseDown(x - uIElement.getXScrolled(this).intValue(), y - uIElement.getYScrolled(this).intValue())
                            ? Boolean.TRUE
                            : Boolean.FALSE;
                    }
                }
            }

            if (this.bClearButton && this.clearButtonTexture != null && !this.Lines.isEmpty()) {
                int int1 = this.getWidth().intValue() - this.getInset();
                int1 -= 2 + this.clearButtonTexture.getWidth() + 2;
                if (x >= int1) {
                    this.clearInput();
                    return Boolean.TRUE;
                }
            }

            if (this.multipleLine) {
                int int2 = this.getInset();
                int int3 = TextManager.instance.getFontFromEnum(this.font).getLineHeight();
                this.CursorLine = ((int)y - int2 - this.getYScroll().intValue()) / int3;
                if (this.CursorLine > this.Lines.size() - 1) {
                    this.CursorLine = this.Lines.size() - 1;
                }
            }

            if (!this.IsEditable && !this.IsSelectable) {
                if (this.Frame != null) {
                    this.Frame.Colour = this.StandardFrameColour;
                }

                this.DoingTextEntry = false;
                return Boolean.FALSE;
            } else {
                if (Core.CurrentTextEntryBox != this) {
                    if (Core.CurrentTextEntryBox != null) {
                        Core.CurrentTextEntryBox.DoingTextEntry = false;
                        if (Core.CurrentTextEntryBox.Frame != null) {
                            Core.CurrentTextEntryBox.Frame.Colour = this.StandardFrameColour;
                        }
                    }

                    Core.CurrentTextEntryBox = this;
                    Core.CurrentTextEntryBox.SelectingRange = true;
                }

                if (!this.DoingTextEntry) {
                    this.focus();
                    this.TextEntryCursorPos = this.getCursorPosFromX((int)x);
                    this.ToSelectionIndex = this.TextEntryCursorPos;
                    if (this.Frame != null) {
                        this.Frame.Colour = this.TextEntryFrameColour;
                    }
                } else {
                    this.TextEntryCursorPos = this.getCursorPosFromX((int)x);
                    this.ToSelectionIndex = this.TextEntryCursorPos;
                }

                return Boolean.TRUE;
            }
        }
    }

    private int getCursorPosFromX(int int0) {
        if (this.Lines.isEmpty()) {
            return 0;
        } else {
            String string0 = this.Lines.get(this.CursorLine);
            if (string0.length() == 0) {
                return this.TextOffsetOfLineStart.get(this.CursorLine);
            } else if (int0 + this.XOffset < 0) {
                return this.TextOffsetOfLineStart.get(this.CursorLine);
            } else {
                for (int int1 = 0; int1 <= string0.length(); int1++) {
                    String string1 = "";
                    if (int1 > 0) {
                        string1 = string0.substring(0, int1);
                    }

                    int int2 = TextManager.instance.MeasureStringX(this.font, string1);
                    if (int2 > int0 + this.XOffset && int1 >= 0) {
                        return this.TextOffsetOfLineStart.get(this.CursorLine) + int1 - 1;
                    }
                }

                return this.TextOffsetOfLineStart.get(this.CursorLine) + string0.length();
            }
        }
    }

    public void updateText() {
        if (this.bMask) {
            String string = "";

            for (int int0 = 0; int0 < this.internalText.length(); int0++) {
                string = string + this.maskChr;
            }

            this.Text = string;
        } else {
            this.Text = this.internalText;
        }
    }

    public void SetText(String text) {
        this.internalText = text;
        if (this.bMask) {
            text = "";

            for (int int0 = 0; int0 < this.internalText.length(); int0++) {
                text = text + this.maskChr;
            }

            this.Text = text;
        } else {
            this.Text = text;
        }

        this.TextEntryCursorPos = text.length();
        this.ToSelectionIndex = this.TextEntryCursorPos;
        this.update();
        this.TextEntryCursorPos = this.ToSelectionIndex = 0;
        if (!this.Lines.isEmpty()) {
            int int1 = this.Lines.size() - 1;
            this.TextEntryCursorPos = this.ToSelectionIndex = this.TextOffsetOfLineStart.get(int1) + this.Lines.get(int1).length();
        }
    }

    public void clearInput() {
        this.Text = "";
        this.internalText = "";
        this.TextEntryCursorPos = 0;
        this.ToSelectionIndex = 0;
        this.update();
        this.onTextChange();
    }

    public void onPressUp() {
        if (this.getTable() != null && this.getTable().rawget("onPressUp") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPressUp"), this.getTable());
        }
    }

    public void onPressDown() {
        if (this.getTable() != null && this.getTable().rawget("onPressDown") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPressDown"), this.getTable());
        }
    }

    public void onCommandEntered() {
        if (this.getTable() != null && this.getTable().rawget("onCommandEntered") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onCommandEntered"), this.getTable());
        }
    }

    public void onTextChange() {
        if (this.getTable() != null && this.getTable().rawget("onTextChange") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onTextChange"), this.getTable());
        }
    }

    public void onOtherKey(int key) {
        if (this.getTable() != null && this.getTable().rawget("onOtherKey") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onOtherKey"), this.getTable(), key);
        }
    }

    public int getMaxTextLength() {
        return this.maxTextLength;
    }

    public void setMaxTextLength(int maxtextLength) {
        this.maxTextLength = maxtextLength;
    }

    public boolean getForceUpperCase() {
        return this.forceUpperCase;
    }

    public void setForceUpperCase(boolean _forceUpperCase) {
        this.forceUpperCase = _forceUpperCase;
    }

    public void setHasFrame(boolean hasFrame) {
        if (this.HasFrame != hasFrame) {
            this.HasFrame = hasFrame;
            if (this.HasFrame) {
                this.Frame = new UINineGrid(
                    0,
                    0,
                    (int)this.width,
                    (int)this.height,
                    this.EdgeSize,
                    this.EdgeSize,
                    this.EdgeSize,
                    this.EdgeSize,
                    "media/ui/Box_TopLeft.png",
                    "media/ui/Box_Top.png",
                    "media/ui/Box_TopRight.png",
                    "media/ui/Box_Left.png",
                    "media/ui/Box_Center.png",
                    "media/ui/Box_Right.png",
                    "media/ui/Box_BottomLeft.png",
                    "media/ui/Box_Bottom.png",
                    "media/ui/Box_BottomRight.png"
                );
                this.Frame.setAnchorRight(true);
                this.AddChild(this.Frame);
            } else {
                this.RemoveChild(this.Frame);
                this.Frame = null;
            }
        }
    }

    public void setClearButton(boolean hasButton) {
        this.bClearButton = hasButton;
        if (this.bClearButton && this.clearButtonTransition == null) {
            this.clearButtonTransition = new UITransition();
        }
    }

    public int toDisplayLine(int textOffset) {
        for (int int0 = 0; int0 < this.Lines.size(); int0++) {
            if (textOffset >= this.TextOffsetOfLineStart.get(int0) && textOffset <= this.TextOffsetOfLineStart.get(int0) + this.Lines.get(int0).length()) {
                return int0;
            }
        }

        return 0;
    }

    public void setMultipleLine(boolean multiple) {
        this.multipleLine = multiple;
    }

    public boolean isMultipleLine() {
        return this.multipleLine;
    }

    public int getCursorLine() {
        return this.CursorLine;
    }

    public void setCursorLine(int line) {
        this.CursorLine = line;
    }

    public int getCursorPos() {
        return this.TextEntryCursorPos;
    }

    public void setCursorPos(int charIndex) {
        if (this.multipleLine) {
            if (this.CursorLine >= 0 && this.CursorLine < this.Lines.size()) {
                this.TextEntryCursorPos = PZMath.clamp(charIndex, 0, this.Lines.get(this.CursorLine).length());
            }
        } else {
            this.TextEntryCursorPos = PZMath.clamp(charIndex, 0, this.internalText.length());
        }

        this.ToSelectionIndex = this.TextEntryCursorPos;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setMaxLines(int _maxLines) {
        this.maxLines = _maxLines;
    }

    public boolean isFocused() {
        return this.DoingTextEntry;
    }

    public boolean isOnlyNumbers() {
        return this.onlyNumbers;
    }

    public void setOnlyNumbers(boolean _onlyNumbers) {
        this.onlyNumbers = _onlyNumbers;
    }

    public void resetBlink() {
        this.BlinkState = true;
        this.BlinkFrame = this.BlinkFramesOn;
    }

    public void selectAll() {
        this.TextEntryCursorPos = this.internalText.length();
        this.ToSelectionIndex = 0;
    }
}
