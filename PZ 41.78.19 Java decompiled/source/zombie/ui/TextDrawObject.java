// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.network.GameServer;

public final class TextDrawObject {
    private String[] validImages = new String[]{"Icon_music_notes", "media/ui/CarKey.png", "media/ui/ArrowUp.png", "media/ui/ArrowDown.png"};
    private String[] validFonts = new String[]{"Small", "Dialogue", "Medium", "Code", "Large", "Massive"};
    private final ArrayList<TextDrawObject.DrawLine> lines = new ArrayList<>();
    private int width = 0;
    private int height = 0;
    private int maxCharsLine = -1;
    private UIFont defaultFontEnum = UIFont.Dialogue;
    private AngelCodeFont defaultFont = null;
    private String original = "";
    private String unformatted = "";
    private TextDrawObject.DrawLine currentLine;
    private TextDrawObject.DrawElement currentElement;
    private boolean hasOpened = false;
    private boolean drawBackground = false;
    private boolean allowImages = true;
    private boolean allowChatIcons = true;
    private boolean allowColors = true;
    private boolean allowFonts = true;
    private boolean allowBBcode = true;
    private boolean allowAnyImage = false;
    private boolean allowLineBreaks = true;
    private boolean equalizeLineHeights = false;
    private boolean enabled = true;
    private int visibleRadius = -1;
    private float scrambleVal = 0.0F;
    private float outlineR = 0.0F;
    private float outlineG = 0.0F;
    private float outlineB = 0.0F;
    private float outlineA = 1.0F;
    private float defaultR = 1.0F;
    private float defaultG = 1.0F;
    private float defaultB = 1.0F;
    private float defaultA = 1.0F;
    private int hearRange = -1;
    private float internalClock = 0.0F;
    private String customTag = "default";
    private int customImageMaxDim = 18;
    private TextDrawHorizontal defaultHorz = TextDrawHorizontal.Center;
    private int drawMode = 0;
    private static ArrayList<TextDrawObject.RenderBatch> renderBatch = new ArrayList<>();
    private static ArrayDeque<TextDrawObject.RenderBatch> renderBatchPool = new ArrayDeque<>();
    private String elemText;

    public TextDrawObject() {
        this(255, 255, 255, true, true, true, true, true, false);
    }

    public TextDrawObject(int r, int g, int b, boolean _allowBBcode) {
        this(r, g, b, _allowBBcode, true, true, true, true, false);
    }

    public TextDrawObject(
        int r,
        int g,
        int b,
        boolean _allowBBcode,
        boolean _allowImages,
        boolean _allowChatIcons,
        boolean _allowColors,
        boolean _allowFonts,
        boolean _equalizeLineHeights
    ) {
        this.setSettings(_allowBBcode, _allowImages, _allowChatIcons, _allowColors, _allowFonts, _equalizeLineHeights);
        this.setDefaultColors(r, g, b);
    }

    public void setEnabled(boolean _enabled) {
        this.enabled = _enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setVisibleRadius(int radius) {
        this.visibleRadius = radius;
    }

    public int getVisibleRadius() {
        return this.visibleRadius;
    }

    public void setDrawBackground(boolean draw) {
        this.drawBackground = draw;
    }

    public void setAllowImages(boolean _allowImages) {
        this.allowImages = _allowImages;
    }

    public void setAllowChatIcons(boolean _allowChatIcons) {
        this.allowChatIcons = _allowChatIcons;
    }

    public void setAllowColors(boolean _allowColors) {
        this.allowColors = _allowColors;
    }

    public void setAllowFonts(boolean _allowFonts) {
        this.allowFonts = _allowFonts;
    }

    public void setAllowBBcode(boolean _allowBBcode) {
        this.allowBBcode = _allowBBcode;
    }

    public void setAllowAnyImage(boolean _allowAnyImage) {
        this.allowAnyImage = _allowAnyImage;
    }

    public void setAllowLineBreaks(boolean _allowLineBreaks) {
        this.allowLineBreaks = _allowLineBreaks;
    }

    public void setEqualizeLineHeights(boolean _equalizeLineHeights) {
        this.equalizeLineHeights = _equalizeLineHeights;
        this.calculateDimensions();
    }

    public void setSettings(
        boolean _allowBBcode, boolean _allowImages, boolean _allowChatIcons, boolean _allowColors, boolean _allowFonts, boolean _equalizeLineHeights
    ) {
        this.allowImages = _allowImages;
        this.allowChatIcons = _allowChatIcons;
        this.allowColors = _allowColors;
        this.allowFonts = _allowFonts;
        this.allowBBcode = _allowBBcode;
        this.equalizeLineHeights = _equalizeLineHeights;
    }

    public void setCustomTag(String tag) {
        this.customTag = tag;
    }

    public String getCustomTag() {
        return this.customTag;
    }

    public void setValidImages(String[] strings) {
        this.validImages = strings;
    }

    public void setValidFonts(String[] strings) {
        this.validFonts = strings;
    }

    public void setMaxCharsPerLine(int charsperline) {
        if (charsperline > 0) {
            this.ReadString(this.original, charsperline);
        }
    }

    public void setCustomImageMaxDimensions(int dim) {
        if (dim >= 1) {
            this.customImageMaxDim = dim;
            this.calculateDimensions();
        }
    }

    public void setOutlineColors(int r, int g, int b) {
        this.setOutlineColors(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
    }

    public void setOutlineColors(int r, int g, int b, int a) {
        this.setOutlineColors(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
    }

    public void setOutlineColors(float r, float g, float b) {
        this.setOutlineColors(r, g, b, 1.0F);
    }

    public void setOutlineColors(float r, float g, float b, float a) {
        this.outlineR = r;
        this.outlineG = g;
        this.outlineB = b;
        this.outlineA = a;
    }

    public void setDefaultColors(int r, int g, int b) {
        this.setDefaultColors(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
    }

    public void setDefaultColors(int r, int g, int b, int a) {
        this.setDefaultColors(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
    }

    public void setDefaultColors(float r, float g, float b) {
        this.setDefaultColors(r, g, b, 1.0F);
    }

    public void setDefaultColors(float r, float g, float b, float a) {
        this.defaultR = r;
        this.defaultG = g;
        this.defaultB = b;
        this.defaultA = a;
    }

    public void setHorizontalAlign(String horz) {
        if (horz.equals("left")) {
            this.defaultHorz = TextDrawHorizontal.Left;
        } else if (horz.equals("center")) {
            this.defaultHorz = TextDrawHorizontal.Center;
        }

        if (horz.equals("right")) {
            this.defaultHorz = TextDrawHorizontal.Right;
        }
    }

    public void setHorizontalAlign(TextDrawHorizontal horz) {
        this.defaultHorz = horz;
    }

    public TextDrawHorizontal getHorizontalAlign() {
        return this.defaultHorz;
    }

    public String getOriginal() {
        return this.original;
    }

    public String getUnformatted() {
        if (!(this.scrambleVal > 0.0F)) {
            return this.unformatted;
        } else {
            String string = "";

            for (TextDrawObject.DrawLine drawLine : this.lines) {
                for (TextDrawObject.DrawElement drawElement : drawLine.elements) {
                    if (!drawElement.isImage) {
                        string = string + drawElement.scrambleText;
                    }
                }
            }

            return string;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public UIFont getDefaultFontEnum() {
        return this.defaultFontEnum;
    }

    public boolean isNullOrZeroLength() {
        return this.original == null || this.original.length() == 0;
    }

    public float getInternalClock() {
        return this.internalClock;
    }

    public void setInternalTickClock(float ticks) {
        if (ticks > 0.0F) {
            this.internalClock = ticks;
        }
    }

    public float updateInternalTickClock() {
        return this.updateInternalTickClock(1.25F * GameTime.getInstance().getMultiplier());
    }

    public float updateInternalTickClock(float delta) {
        if (this.internalClock <= 0.0F) {
            return 0.0F;
        } else {
            this.internalClock -= delta;
            if (this.internalClock <= 0.0F) {
                this.internalClock = 0.0F;
            }

            return this.internalClock;
        }
    }

    public void setScrambleVal(float value) {
        if (this.scrambleVal != value) {
            this.scrambleVal = value;
            if (this.scrambleVal > 0.0F) {
                for (TextDrawObject.DrawLine drawLine : this.lines) {
                    for (TextDrawObject.DrawElement drawElement : drawLine.elements) {
                        if (!drawElement.isImage) {
                            drawElement.scrambleText(this.scrambleVal);
                        }
                    }
                }
            }
        }
    }

    public float getScrambleVal() {
        return this.scrambleVal;
    }

    public void setHearRange(int range) {
        if (range < 0) {
            this.hearRange = 0;
        } else {
            this.hearRange = range;
        }
    }

    public int getHearRange() {
        return this.hearRange;
    }

    private boolean isValidFont(String string1) {
        for (String string0 : this.validFonts) {
            if (string1.equals(string0) && UIFont.FromString(string1) != null) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidImage(String string1) {
        for (String string0 : this.validImages) {
            if (string1.equals(string0)) {
                return true;
            }
        }

        return false;
    }

    private int tryColorInt(String string) {
        if (string.length() > 0 && string.length() <= 3) {
            try {
                int int0 = Integer.parseInt(string);
                return int0 >= 0 && int0 < 256 ? int0 : -1;
            } catch (NumberFormatException numberFormatException) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private String readTagValue(char[] chars, int int0) {
        if (chars[int0] == '=') {
            String string = "";

            for (int int1 = int0 + 1; int1 < chars.length; int1++) {
                char char0 = chars[int1];
                if (char0 == ']') {
                    return string;
                }

                string = string + char0;
            }
        }

        return null;
    }

    public void Clear() {
        this.original = "";
        this.unformatted = "";
        this.reset();
    }

    private void reset() {
        this.lines.clear();
        this.currentLine = new TextDrawObject.DrawLine();
        this.lines.add(this.currentLine);
        this.currentElement = new TextDrawObject.DrawElement();
        this.currentLine.addElement(this.currentElement);
        this.enabled = true;
        this.scrambleVal = 0.0F;
    }

    private void addNewLine() {
        this.currentLine = new TextDrawObject.DrawLine();
        this.lines.add(this.currentLine);
        this.currentElement = this.currentElement.softclone();
        this.currentLine.addElement(this.currentElement);
    }

    private void addText(String string) {
        this.currentElement.addText(string);
        this.currentLine.charW = this.currentLine.charW + string.length();
    }

    private void addWord(String string) {
        if (this.maxCharsLine > 0 && this.currentLine.charW + string.length() >= this.maxCharsLine) {
            for (int int0 = 0; int0 < string.length() / this.maxCharsLine + 1; int0++) {
                int int1 = int0 * this.maxCharsLine;
                int int2 = int1 + this.maxCharsLine < string.length() ? int1 + this.maxCharsLine : string.length();
                if (string.substring(int1, int2).length() > 0) {
                    if (int0 > 0 || this.currentLine.charW != 0) {
                        this.addNewLine();
                    }

                    this.addText(string.substring(int1, int2));
                }
            }
        } else {
            this.addText(string);
        }
    }

    private void addNewElement() {
        if (this.currentElement.text.length() == 0) {
            this.currentElement.reset();
        } else {
            this.currentElement = new TextDrawObject.DrawElement();
            this.currentLine.addElement(this.currentElement);
        }
    }

    private int readTag(char[] chars, int int0, String string0) {
        if (this.allowFonts && string0.equals("fnt")) {
            String string1 = this.readTagValue(chars, int0);
            if (string1 != null && this.isValidFont(string1)) {
                this.addNewElement();
                this.currentElement.f = UIFont.FromString(string1);
                this.currentElement.useFont = true;
                this.currentElement.font = TextManager.instance.getFontFromEnum(this.currentElement.f);
                this.hasOpened = true;
                return int0 + string1.length() + 1;
            }
        } else if ((this.allowImages || this.allowChatIcons) && string0.equals("img")) {
            String string2 = this.readTagValue(chars, int0);
            if (string2 != null && string2.trim().length() > 0) {
                this.addNewElement();
                int int1 = string2.length();
                String[] strings0 = string2.split(",");
                if (strings0.length > 1) {
                    string2 = strings0[0];
                }

                this.currentElement.isImage = true;
                this.currentElement.text = string2.trim();
                if (this.currentElement.text.equals("music")) {
                    this.currentElement.text = "Icon_music_notes";
                }

                if (this.allowChatIcons && this.isValidImage(this.currentElement.text)) {
                    this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
                    this.currentElement.isTextImage = true;
                } else if (this.allowImages) {
                    this.currentElement.tex = Texture.getSharedTexture("Item_" + this.currentElement.text);
                    if (this.currentElement.tex == null) {
                        this.currentElement.tex = Texture.getSharedTexture("media/ui/Container_" + this.currentElement.text);
                    }

                    if (this.currentElement.tex != null) {
                        this.currentElement.isTextImage = false;
                        this.currentElement.text = "Item_" + this.currentElement.text;
                    }
                }

                if (this.allowAnyImage && this.currentElement.tex == null) {
                    this.currentElement.tex = Texture.getSharedTexture(this.currentElement.text);
                    if (this.currentElement.tex != null) {
                        this.currentElement.isTextImage = false;
                    }
                }

                if (strings0.length == 4) {
                    int int2 = this.tryColorInt(strings0[1]);
                    int int3 = this.tryColorInt(strings0[2]);
                    int int4 = this.tryColorInt(strings0[3]);
                    if (int2 != -1 && int3 != -1 && int4 != -1) {
                        this.currentElement.useColor = true;
                        this.currentElement.R = int2 / 255.0F;
                        this.currentElement.G = int3 / 255.0F;
                        this.currentElement.B = int4 / 255.0F;
                    }
                }

                this.addNewElement();
                return int0 + int1 + 1;
            }
        } else if (this.allowColors && string0.equals("col")) {
            String string3 = this.readTagValue(chars, int0);
            if (string3 != null) {
                String[] strings1 = string3.split(",");
                if (strings1.length == 3) {
                    int int5 = this.tryColorInt(strings1[0]);
                    int int6 = this.tryColorInt(strings1[1]);
                    int int7 = this.tryColorInt(strings1[2]);
                    if (int5 != -1 && int6 != -1 && int7 != -1) {
                        this.addNewElement();
                        this.currentElement.useColor = true;
                        this.currentElement.R = int5 / 255.0F;
                        this.currentElement.G = int6 / 255.0F;
                        this.currentElement.B = int7 / 255.0F;
                        this.hasOpened = true;
                        return int0 + string3.length() + 1;
                    }
                }
            }
        } else if (string0.equals("cdt")) {
            String string4 = this.readTagValue(chars, int0);
            if (string4 != null) {
                float float0 = this.internalClock;

                try {
                    float0 = Float.parseFloat(string4);
                    float0 *= 60.0F;
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                }

                this.internalClock = float0;
                return int0 + string4.length() + 1;
            }
        }

        return -1;
    }

    public void setDefaultFont(UIFont f) {
        if (!f.equals(this.defaultFontEnum)) {
            this.ReadString(f, this.original, this.maxCharsLine);
        }
    }

    private void setDefaultFontInternal(UIFont uIFont) {
        if (this.defaultFont == null || !uIFont.equals(this.defaultFontEnum)) {
            this.defaultFontEnum = uIFont;
            this.defaultFont = TextManager.instance.getFontFromEnum(uIFont);
        }
    }

    public void ReadString(String str) {
        this.ReadString(this.defaultFontEnum, str, this.maxCharsLine);
    }

    public void ReadString(String str, int maxLineWidth) {
        this.ReadString(this.defaultFontEnum, str, maxLineWidth);
    }

    public void ReadString(UIFont font, String str, int maxLineWidth) {
        if (str == null) {
            str = "";
        }

        this.reset();
        this.setDefaultFontInternal(font);
        if (this.defaultFont != null) {
            this.maxCharsLine = maxLineWidth;
            this.original = str;
            char[] chars = str.toCharArray();
            this.hasOpened = false;
            String string0 = "";

            for (int int0 = 0; int0 < chars.length; int0++) {
                char char0 = chars[int0];
                if (this.allowBBcode && char0 == '[') {
                    if (string0.length() > 0) {
                        this.addWord(string0);
                        string0 = "";
                    }

                    if (int0 + 4 < chars.length) {
                        String string1 = ("" + chars[int0 + 1] + chars[int0 + 2] + chars[int0 + 3]).toLowerCase();
                        if (this.allowLineBreaks && string1.equals("br/")) {
                            this.addNewLine();
                            int0 += 4;
                            continue;
                        }

                        if (!this.hasOpened) {
                            int int1 = this.readTag(chars, int0 + 4, string1);
                            if (int1 >= 0) {
                                int0 = int1;
                                continue;
                            }
                        }
                    }

                    if (this.hasOpened && int0 + 2 < chars.length && chars[int0 + 1] == '/' && chars[int0 + 2] == ']') {
                        this.hasOpened = false;
                        this.addNewElement();
                        int0 += 2;
                        continue;
                    }
                }

                if (Character.isWhitespace(char0) && int0 > 0 && !Character.isWhitespace(chars[int0 - 1])) {
                    this.addWord(string0);
                    string0 = "";
                }

                string0 = string0 + char0;
                this.unformatted = this.unformatted + char0;
            }

            if (string0.length() > 0) {
                this.addWord(string0);
            }

            this.calculateDimensions();
        }
    }

    public void calculateDimensions() {
        this.width = 0;
        this.height = 0;
        int int0 = 0;

        for (int int1 = 0; int1 < this.lines.size(); int1++) {
            TextDrawObject.DrawLine drawLine0 = this.lines.get(int1);
            drawLine0.h = 0;
            drawLine0.w = 0;

            for (int int2 = 0; int2 < drawLine0.elements.size(); int2++) {
                TextDrawObject.DrawElement drawElement = drawLine0.elements.get(int2);
                drawElement.w = 0;
                drawElement.h = 0;
                if (drawElement.isImage && drawElement.tex != null) {
                    if (drawElement.isTextImage) {
                        drawElement.w = drawElement.tex.getWidth();
                        drawElement.h = drawElement.tex.getHeight();
                    } else {
                        drawElement.w = (int)(drawElement.tex.getWidth() * 0.75F);
                        drawElement.h = (int)(drawElement.tex.getHeight() * 0.75F);
                    }
                } else if (drawElement.useFont && drawElement.font != null) {
                    drawElement.w = drawElement.font.getWidth(drawElement.text);
                    drawElement.h = drawElement.font.getHeight(drawElement.text);
                } else if (this.defaultFont != null) {
                    drawElement.w = this.defaultFont.getWidth(drawElement.text);
                    drawElement.h = this.defaultFont.getHeight(drawElement.text);
                }

                drawLine0.w = drawLine0.w + drawElement.w;
                if (drawElement.h > drawLine0.h) {
                    drawLine0.h = drawElement.h;
                }
            }

            if (drawLine0.w > this.width) {
                this.width = drawLine0.w;
            }

            this.height = this.height + drawLine0.h;
            if (drawLine0.h > int0) {
                int0 = drawLine0.h;
            }
        }

        if (this.equalizeLineHeights) {
            this.height = 0;

            for (int int3 = 0; int3 < this.lines.size(); int3++) {
                TextDrawObject.DrawLine drawLine1 = this.lines.get(int3);
                drawLine1.h = int0;
                this.height += int0;
            }
        }
    }

    public void Draw(double x, double y) {
        this.Draw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, this.defaultA, false);
    }

    public void Draw(double x, double y, boolean drawOutlines) {
        this.Draw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, this.defaultA, drawOutlines);
    }

    public void Draw(double x, double y, boolean drawOutlines, float alpha) {
        this.Draw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, alpha, drawOutlines);
    }

    public void Draw(double x, double y, double r, double g, double b, double a, boolean drawOutlines) {
        this.Draw(this.defaultHorz, x, y, r, g, b, a, drawOutlines);
    }

    public void Draw(TextDrawHorizontal horz, double x, double y, double r, double g, double b, double a, boolean drawOutlines) {
        this.DrawRaw(horz, x, y, (float)r, (float)g, (float)b, (float)a, drawOutlines);
    }

    public void AddBatchedDraw(double x, double y) {
        this.AddBatchedDraw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, this.defaultA, false);
    }

    public void AddBatchedDraw(double x, double y, boolean drawOutlines) {
        this.AddBatchedDraw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, this.defaultA, drawOutlines);
    }

    public void AddBatchedDraw(double x, double y, boolean drawOutlines, float alpha) {
        this.AddBatchedDraw(this.defaultHorz, x, y, this.defaultR, this.defaultG, this.defaultB, alpha, drawOutlines);
    }

    public void AddBatchedDraw(double x, double y, double r, double g, double b, double a, boolean drawOutlines) {
        this.AddBatchedDraw(this.defaultHorz, x, y, r, g, b, a, drawOutlines);
    }

    public void AddBatchedDraw(TextDrawHorizontal horz, double x, double y, double r, double g, double b, double a, boolean drawOutlines) {
        if (!GameServer.bServer) {
            TextDrawObject.RenderBatch renderBatchx = renderBatchPool.isEmpty() ? new TextDrawObject.RenderBatch() : renderBatchPool.pop();
            renderBatchx.playerNum = IsoPlayer.getPlayerIndex();
            renderBatchx.element = this;
            renderBatchx.horz = horz;
            renderBatchx.x = x;
            renderBatchx.y = y;
            renderBatchx.r = (float)r;
            renderBatchx.g = (float)g;
            renderBatchx.b = (float)b;
            renderBatchx.a = (float)a;
            renderBatchx.drawOutlines = drawOutlines;
            renderBatch.add(renderBatchx);
        }
    }

    public static void RenderBatch(int playerNum) {
        if (renderBatch.size() > 0) {
            for (int int0 = 0; int0 < renderBatch.size(); int0++) {
                TextDrawObject.RenderBatch renderBatchx = renderBatch.get(int0);
                if (renderBatchx.playerNum == playerNum) {
                    renderBatchx.element
                        .DrawRaw(
                            renderBatchx.horz,
                            renderBatchx.x,
                            renderBatchx.y,
                            renderBatchx.r,
                            renderBatchx.g,
                            renderBatchx.b,
                            renderBatchx.a,
                            renderBatchx.drawOutlines
                        );
                    renderBatchPool.add(renderBatchx);
                    renderBatch.remove(int0--);
                }
            }
        }
    }

    public static void NoRender(int playerNum) {
        for (int int0 = 0; int0 < renderBatch.size(); int0++) {
            TextDrawObject.RenderBatch renderBatchx = renderBatch.get(int0);
            if (renderBatchx.playerNum == playerNum) {
                renderBatchPool.add(renderBatchx);
                renderBatch.remove(int0--);
            }
        }
    }

    public void DrawRaw(TextDrawHorizontal horz, double x, double y, float r, float g, float b, float a, boolean drawOutlines) {
        double double0 = x;
        double double1 = y;
        double double2 = 0.0;
        int int0 = Core.getInstance().getScreenWidth();
        int int1 = Core.getInstance().getScreenHeight();
        byte byte0 = 20;
        if (horz == TextDrawHorizontal.Center) {
            double0 = x - this.getWidth() / 2;
        } else if (horz == TextDrawHorizontal.Right) {
            double0 = x - this.getWidth();
        }

        if (!(double0 - byte0 >= int0) && !(double0 + this.getWidth() + byte0 <= 0.0) && !(y - byte0 >= int1) && !(y + this.getHeight() + byte0 <= 0.0)) {
            if (this.drawBackground && ChatElement.backdropTexture != null) {
                ChatElement.backdropTexture.renderInnerBased((int)double0, (int)y, this.getWidth(), this.getHeight(), 0.0F, 0.0F, 0.0F, 0.4F * a);
            }

            float float0 = this.outlineA;
            if (drawOutlines && a < 1.0F) {
                float0 = this.outlineA * a;
            }

            for (int int2 = 0; int2 < this.lines.size(); int2++) {
                TextDrawObject.DrawLine drawLine = this.lines.get(int2);
                double0 = x;
                if (horz == TextDrawHorizontal.Center) {
                    double0 = x - drawLine.w / 2;
                } else if (horz == TextDrawHorizontal.Right) {
                    double0 = x - drawLine.w;
                }

                for (int int3 = 0; int3 < drawLine.elements.size(); int3++) {
                    TextDrawObject.DrawElement drawElement = drawLine.elements.get(int3);
                    double2 = drawLine.h / 2 - drawElement.h / 2;
                    this.elemText = this.scrambleVal > 0.0F ? drawElement.scrambleText : drawElement.text;
                    if (drawElement.isImage && drawElement.tex != null) {
                        if (drawOutlines && drawElement.isTextImage) {
                            SpriteRenderer.instance
                                .renderi(
                                    drawElement.tex,
                                    (int)(double0 - 1.0),
                                    (int)(double1 + double2 - 1.0),
                                    drawElement.w,
                                    drawElement.h,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0,
                                    null
                                );
                            SpriteRenderer.instance
                                .renderi(
                                    drawElement.tex,
                                    (int)(double0 + 1.0),
                                    (int)(double1 + double2 + 1.0),
                                    drawElement.w,
                                    drawElement.h,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0,
                                    null
                                );
                            SpriteRenderer.instance
                                .renderi(
                                    drawElement.tex,
                                    (int)(double0 - 1.0),
                                    (int)(double1 + double2 + 1.0),
                                    drawElement.w,
                                    drawElement.h,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0,
                                    null
                                );
                            SpriteRenderer.instance
                                .renderi(
                                    drawElement.tex,
                                    (int)(double0 + 1.0),
                                    (int)(double1 + double2 - 1.0),
                                    drawElement.w,
                                    drawElement.h,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0,
                                    null
                                );
                        }

                        if (drawElement.useColor) {
                            SpriteRenderer.instance
                                .renderi(
                                    drawElement.tex,
                                    (int)double0,
                                    (int)(double1 + double2),
                                    drawElement.w,
                                    drawElement.h,
                                    drawElement.R,
                                    drawElement.G,
                                    drawElement.B,
                                    a,
                                    null
                                );
                        } else if (drawElement.isTextImage) {
                            SpriteRenderer.instance
                                .renderi(drawElement.tex, (int)double0, (int)(double1 + double2), drawElement.w, drawElement.h, r, g, b, a, null);
                        } else {
                            SpriteRenderer.instance
                                .renderi(drawElement.tex, (int)double0, (int)(double1 + double2), drawElement.w, drawElement.h, 1.0F, 1.0F, 1.0F, a, null);
                        }
                    } else if (drawElement.useFont && drawElement.font != null) {
                        if (drawOutlines) {
                            drawElement.font
                                .drawString(
                                    (float)(double0 - 1.0),
                                    (float)(double1 + double2 - 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            drawElement.font
                                .drawString(
                                    (float)(double0 + 1.0),
                                    (float)(double1 + double2 + 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            drawElement.font
                                .drawString(
                                    (float)(double0 - 1.0),
                                    (float)(double1 + double2 + 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            drawElement.font
                                .drawString(
                                    (float)(double0 + 1.0),
                                    (float)(double1 + double2 - 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                        }

                        drawElement.font.drawString((float)double0, (float)(double1 + double2), this.elemText, r, g, b, a);
                    } else if (this.defaultFont != null) {
                        if (drawOutlines) {
                            this.defaultFont
                                .drawString(
                                    (float)(double0 - 1.0),
                                    (float)(double1 + double2 - 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            this.defaultFont
                                .drawString(
                                    (float)(double0 + 1.0),
                                    (float)(double1 + double2 + 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            this.defaultFont
                                .drawString(
                                    (float)(double0 - 1.0),
                                    (float)(double1 + double2 + 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                            this.defaultFont
                                .drawString(
                                    (float)(double0 + 1.0),
                                    (float)(double1 + double2 - 1.0),
                                    this.elemText,
                                    this.outlineR,
                                    this.outlineG,
                                    this.outlineB,
                                    float0
                                );
                        }

                        if (drawElement.useColor) {
                            this.defaultFont
                                .drawString((float)double0, (float)(double1 + double2), this.elemText, drawElement.R, drawElement.G, drawElement.B, a);
                        } else {
                            this.defaultFont.drawString((float)double0, (float)(double1 + double2), this.elemText, r, g, b, a);
                        }
                    }

                    double0 += drawElement.w;
                }

                double1 += drawLine.h;
            }
        }
    }

    private static final class DrawElement {
        private String text = "";
        private String scrambleText = "";
        private float currentScrambleVal = 0.0F;
        private UIFont f = UIFont.AutoNormSmall;
        private AngelCodeFont font = null;
        private float R = 1.0F;
        private float G = 1.0F;
        private float B = 1.0F;
        private int w = 0;
        private int h = 0;
        private boolean isImage = false;
        private boolean useFont = false;
        private boolean useColor = false;
        private Texture tex = null;
        private boolean isTextImage = false;
        private int charWidth = 0;

        private void reset() {
            this.text = "";
            this.scrambleText = "";
            this.f = UIFont.AutoNormSmall;
            this.font = null;
            this.R = 1.0F;
            this.G = 1.0F;
            this.B = 1.0F;
            this.w = 0;
            this.h = 0;
            this.isImage = false;
            this.useFont = false;
            this.useColor = false;
            this.tex = null;
            this.isTextImage = false;
            this.charWidth = 0;
        }

        private void addText(String string) {
            this.text = this.text + string;
            this.charWidth = this.text.length();
        }

        private void scrambleText(float float0) {
            if (float0 != this.currentScrambleVal) {
                this.currentScrambleVal = float0;
                int int0 = (int)(float0 * 100.0F);
                String[] strings = this.text.split("\\s+");
                this.scrambleText = "";

                for (String string : strings) {
                    int int1 = Rand.Next(100);
                    if (int1 > int0) {
                        this.scrambleText = this.scrambleText + string + " ";
                    } else {
                        char[] chars = new char[string.length()];
                        Arrays.fill(chars, ".".charAt(0));
                        this.scrambleText = this.scrambleText + new String(chars) + " ";
                    }
                }
            }
        }

        private void trim() {
            this.text = this.text.trim();
        }

        private TextDrawObject.DrawElement softclone() {
            TextDrawObject.DrawElement drawElement0 = new TextDrawObject.DrawElement();
            if (this.useColor) {
                drawElement0.R = this.R;
                drawElement0.G = this.G;
                drawElement0.B = this.B;
                drawElement0.useColor = this.useColor;
            }

            if (this.useFont) {
                drawElement0.f = this.f;
                drawElement0.font = this.font;
                drawElement0.useFont = this.useFont;
            }

            return drawElement0;
        }
    }

    private static final class DrawLine {
        private final ArrayList<TextDrawObject.DrawElement> elements = new ArrayList<>();
        private int h = 0;
        private int w = 0;
        private int charW = 0;

        private void addElement(TextDrawObject.DrawElement drawElement) {
            this.elements.add(drawElement);
        }
    }

    private static final class RenderBatch {
        int playerNum;
        TextDrawObject element;
        TextDrawHorizontal horz;
        double x;
        double y;
        float r;
        float g;
        float b;
        float a;
        boolean drawOutlines;
    }
}
