// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayList;
import java.util.Vector;
import se.krka.kahlua.vm.KahluaTable;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.input.Mouse;

public class UIElement {
    static final Color tempcol = new Color(0, 0, 0, 0);
    static final ArrayList<UIElement> toAdd = new ArrayList<>(0);
    static Texture white;
    static int StencilLevel = 0;
    public boolean capture = false;
    public boolean IgnoreLossControl = false;
    public String clickedValue = null;
    public final ArrayList<UIElement> Controls = new ArrayList<>();
    public boolean defaultDraw = true;
    public boolean followGameWorld = false;
    private int renderThisPlayerOnly = -1;
    public float height = 256.0F;
    public UIElement Parent = null;
    public boolean visible = true;
    public float width = 256.0F;
    public double x = 0.0;
    public double y = 0.0;
    public KahluaTable table;
    public boolean alwaysBack;
    public boolean bScrollChildren = false;
    public boolean bScrollWithParent = true;
    private boolean bRenderClippedChildren = true;
    public boolean anchorTop = true;
    public boolean anchorLeft = true;
    public boolean anchorRight = false;
    public boolean anchorBottom = false;
    public int playerContext = -1;
    boolean alwaysOnTop = false;
    int maxDrawHeight = -1;
    Double yScroll = 0.0;
    Double xScroll = 0.0;
    int scrollHeight = 0;
    double lastheight = -1.0;
    double lastwidth = -1.0;
    boolean bResizeDirty = false;
    boolean enabled = true;
    private final ArrayList<UIElement> toTop = new ArrayList<>(0);
    private boolean bConsumeMouseEvents = true;
    private long leftDownTime = 0L;
    private boolean clicked;
    private double clickX;
    private double clickY;
    private String uiname = "";
    private boolean bWantKeyEvents = false;
    private boolean bForceCursorVisible = false;

    public UIElement() {
    }

    public UIElement(KahluaTable _table) {
        this.table = _table;
    }

    public Double getMaxDrawHeight() {
        return BoxedStaticValues.toDouble(this.maxDrawHeight);
    }

    public void setMaxDrawHeight(double _height) {
        this.maxDrawHeight = (int)_height;
    }

    public void clearMaxDrawHeight() {
        this.maxDrawHeight = -1;
    }

    public Double getXScroll() {
        return this.xScroll;
    }

    public void setXScroll(double _x) {
        this.xScroll = _x;
    }

    public Double getYScroll() {
        return this.yScroll;
    }

    public void setYScroll(double _y) {
        this.yScroll = _y;
    }

    public void setAlwaysOnTop(boolean b) {
        this.alwaysOnTop = b;
    }

    public void backMost() {
        this.alwaysBack = true;
    }

    public void AddChild(UIElement el) {
        this.getControls().add(el);
        el.setParent(this);
    }

    public void RemoveChild(UIElement el) {
        this.getControls().remove(el);
        el.setParent(null);
    }

    public Double getScrollHeight() {
        return BoxedStaticValues.toDouble(this.scrollHeight);
    }

    public void setScrollHeight(double h) {
        this.scrollHeight = (int)h;
    }

    public boolean isConsumeMouseEvents() {
        return this.bConsumeMouseEvents;
    }

    public void setConsumeMouseEvents(boolean bConsume) {
        this.bConsumeMouseEvents = bConsume;
    }

    public void ClearChildren() {
        this.getControls().clear();
    }

    public void ButtonClicked(String name) {
        this.setClickedValue(name);
    }

    public void DrawText(UIFont font, String text, double _x, double _y, double zoom, double r, double g, double b, double alpha) {
        TextManager.instance
            .DrawString(font, _x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, (float)zoom, text, r, g, b, alpha);
    }

    public void DrawText(String text, double _x, double _y, double r, double g, double b, double alpha) {
        TextManager.instance.DrawString(_x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawText(String text, double _x, double _y, double _width, double _height, double r, double g, double b, double alpha) {
        TextManager.instance.DrawString(_x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawText(UIFont font, String text, double _x, double _y, double r, double g, double b, double alpha) {
        if (text != null) {
            int int0 = (int)(_y + this.getAbsoluteY() + this.yScroll);
            if (int0 + 100 >= 0 && int0 <= 4096) {
                TextManager.instance.DrawString(font, _x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
            }
        }
    }

    public void DrawTextUntrimmed(UIFont font, String text, double _x, double _y, double r, double g, double b, double alpha) {
        if (text != null) {
            TextManager.instance
                .DrawStringUntrimmed(font, _x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
        }
    }

    public void DrawTextCentre(String text, double _x, double _y, double r, double g, double b, double alpha) {
        TextManager.instance.DrawStringCentre(_x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawTextCentre(UIFont font, String text, double _x, double _y, double r, double g, double b, double alpha) {
        TextManager.instance.DrawStringCentre(font, _x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawTextRight(String text, double _x, double _y, double r, double g, double b, double alpha) {
        TextManager.instance.DrawStringRight(_x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawTextRight(UIFont font, String text, double _x, double _y, double r, double g, double b, double alpha) {
        TextManager.instance.DrawStringRight(font, _x + this.getAbsoluteX() + this.xScroll, _y + this.getAbsoluteY() + this.yScroll, text, r, g, b, alpha);
    }

    public void DrawTextureAngle(Texture tex, double centerX, double centerY, double angle, double r, double g, double b, double a) {
        if (this.isVisible()) {
            float float0 = tex.getWidth() / 2;
            float float1 = tex.getHeight() / 2;
            double double0 = Math.toRadians(180.0 + angle);
            double double1 = Math.cos(double0) * float0;
            double double2 = Math.sin(double0) * float0;
            double double3 = Math.cos(double0) * float1;
            double double4 = Math.sin(double0) * float1;
            double double5 = double1 - double4;
            double double6 = double3 + double2;
            double double7 = -double1 - double4;
            double double8 = double3 - double2;
            double double9 = -double1 + double4;
            double double10 = -double3 - double2;
            double double11 = double1 + double4;
            double double12 = -double3 + double2;
            double5 += this.getAbsoluteX() + centerX;
            double6 += this.getAbsoluteY() + centerY;
            double7 += this.getAbsoluteX() + centerX;
            double8 += this.getAbsoluteY() + centerY;
            double9 += this.getAbsoluteX() + centerX;
            double10 += this.getAbsoluteY() + centerY;
            double11 += this.getAbsoluteX() + centerX;
            double12 += this.getAbsoluteY() + centerY;
            SpriteRenderer.instance
                .render(
                    tex,
                    double5,
                    double6,
                    double7,
                    double8,
                    double9,
                    double10,
                    double11,
                    double12,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)a,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)a,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)a,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)a,
                    null
                );
        }
    }

    public void DrawTextureAngle(Texture tex, double centerX, double centerY, double angle) {
        this.DrawTextureAngle(tex, centerX, centerY, angle, 1.0, 1.0, 1.0, 1.0);
    }

    public void DrawTexture(
        Texture tex, double tlx, double tly, double trx, double try2, double brx, double bry, double blx, double bly, double r, double g, double b, double a
    ) {
        SpriteRenderer.instance
            .render(
                tex,
                tlx,
                tly,
                trx,
                try2,
                brx,
                bry,
                blx,
                bly,
                (float)r,
                (float)g,
                (float)b,
                (float)a,
                (float)r,
                (float)g,
                (float)b,
                (float)a,
                (float)r,
                (float)g,
                (float)b,
                (float)a,
                (float)r,
                (float)g,
                (float)b,
                (float)a,
                null
            );
    }

    public void DrawTexture(Texture tex, double _x, double _y, double alpha) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            double0 += tex.offsetX;
            double1 += tex.offsetY;
            int int0 = (int)(double1 + this.yScroll);
            if (int0 + tex.getHeight() >= 0 && int0 <= 4096) {
                SpriteRenderer.instance
                    .renderi(
                        tex,
                        (int)(double0 + this.xScroll),
                        (int)(double1 + this.yScroll),
                        tex.getWidth(),
                        tex.getHeight(),
                        1.0F,
                        1.0F,
                        1.0F,
                        (float)alpha,
                        null
                    );
            }
        }
    }

    public void DrawTextureCol(Texture tex, double _x, double _y, Color col) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            int int0 = 0;
            int int1 = 0;
            if (tex != null) {
                double0 += tex.offsetX;
                double1 += tex.offsetY;
                int0 = tex.getWidth();
                int1 = tex.getHeight();
            }

            int int2 = (int)(double1 + this.yScroll);
            if (int2 + int1 >= 0 && int2 <= 4096) {
                SpriteRenderer.instance
                    .renderi(tex, (int)(double0 + this.xScroll), (int)(double1 + this.yScroll), int0, int1, col.r, col.g, col.b, col.a, null);
            }
        }
    }

    public void DrawTextureScaled(Texture tex, double _x, double _y, double _width, double _height, double alpha) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            SpriteRenderer.instance
                .renderi(tex, (int)(double0 + this.xScroll), (int)(double1 + this.yScroll), (int)_width, (int)_height, 1.0F, 1.0F, 1.0F, (float)alpha, null);
        }
    }

    public void DrawTextureScaledUniform(Texture tex, double _x, double _y, double scale, double r, double g, double b, double alpha) {
        if (this.isVisible() && tex != null) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            double0 += tex.offsetX * scale;
            double1 += tex.offsetY * scale;
            SpriteRenderer.instance
                .renderi(
                    tex,
                    (int)(double0 + this.xScroll),
                    (int)(double1 + this.yScroll),
                    (int)(tex.getWidth() * scale),
                    (int)(tex.getHeight() * scale),
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)alpha,
                    null
                );
        }
    }

    public void DrawTextureScaledAspect(Texture tex, double _x, double _y, double _width, double _height, double r, double g, double b, double alpha) {
        if (this.isVisible() && tex != null) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            if (tex.getWidth() > 0 && tex.getHeight() > 0 && _width > 0.0 && _height > 0.0) {
                double double2 = Math.min(_width / tex.getWidthOrig(), _height / tex.getHeightOrig());
                double double3 = _width;
                double double4 = _height;
                _width = tex.getWidth() * double2;
                _height = tex.getHeight() * double2;
                double0 -= (_width - double3) / 2.0;
                double1 -= (_height - double4) / 2.0;
            }

            SpriteRenderer.instance
                .renderi(
                    tex,
                    (int)(double0 + this.xScroll),
                    (int)(double1 + this.yScroll),
                    (int)_width,
                    (int)_height,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)alpha,
                    null
                );
        }
    }

    public void DrawTextureScaledAspect2(Texture tex, double _x, double _y, double _width, double _height, double r, double g, double b, double alpha) {
        if (this.isVisible() && tex != null) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            if (tex.getWidth() > 0 && tex.getHeight() > 0 && _width > 0.0 && _height > 0.0) {
                double double2 = Math.min(_width / tex.getWidth(), _height / tex.getHeight());
                double double3 = _width;
                double double4 = _height;
                _width = tex.getWidth() * double2;
                _height = tex.getHeight() * double2;
                double0 -= (_width - double3) / 2.0;
                double1 -= (_height - double4) / 2.0;
            }

            SpriteRenderer.instance
                .render(
                    tex,
                    (int)(double0 + this.xScroll),
                    (int)(double1 + this.yScroll),
                    (int)_width,
                    (int)_height,
                    (float)r,
                    (float)g,
                    (float)b,
                    (float)alpha,
                    null
                );
        }
    }

    public void DrawTextureScaledCol(Texture tex, double _x, double _y, double _width, double _height, double r, double g, double b, double a) {
        if (tex != null) {
            boolean boolean0 = false;
        }

        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            int int0 = (int)(double1 + this.yScroll);
            if (!(int0 + _height < 0.0) && int0 <= 4096) {
                SpriteRenderer.instance
                    .renderi(
                        tex,
                        (int)(double0 + this.xScroll),
                        (int)(double1 + this.yScroll),
                        (int)_width,
                        (int)_height,
                        (float)r,
                        (float)g,
                        (float)b,
                        (float)a,
                        null
                    );
            }
        }
    }

    public void DrawTextureScaledCol(Texture tex, double _x, double _y, double _width, double _height, Color col) {
        if (tex != null) {
            boolean boolean0 = false;
        }

        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            SpriteRenderer.instance
                .render(tex, (int)(double0 + this.xScroll), (int)(double1 + this.yScroll), (int)_width, (int)_height, col.r, col.g, col.b, col.a, null);
        }
    }

    public void DrawTextureScaledColor(Texture tex, Double _x, Double _y, Double _width, Double _height, Double r, Double g, Double b, Double a) {
        this.DrawTextureScaledCol(tex, _x, _y, _width, _height, r, g, b, a);
    }

    public void DrawTextureColor(Texture tex, double _x, double _y, double r, double g, double b, double a) {
        tempcol.r = (float)r;
        tempcol.g = (float)g;
        tempcol.b = (float)b;
        tempcol.a = (float)a;
        this.DrawTextureCol(tex, _x, _y, tempcol);
    }

    public void DrawSubTextureRGBA(
        Texture tex, double subX, double subY, double subW, double subH, double _x, double _y, double w, double h, double r, double g, double b, double a
    ) {
        if (tex != null && this.isVisible() && !(subW <= 0.0) && !(subH <= 0.0) && !(w <= 0.0) && !(h <= 0.0)) {
            double double0 = _x + this.getAbsoluteX() + this.xScroll;
            double double1 = _y + this.getAbsoluteY() + this.yScroll;
            double0 += tex.offsetX;
            double1 += tex.offsetY;
            if (!(double1 + h < 0.0) && !(double1 > 4096.0)) {
                float float0 = PZMath.clamp((float)subX, 0.0F, (float)tex.getWidth());
                float float1 = PZMath.clamp((float)subY, 0.0F, (float)tex.getHeight());
                float float2 = PZMath.clamp((float)(float0 + subW), 0.0F, (float)tex.getWidth()) - float0;
                float float3 = PZMath.clamp((float)(float1 + subH), 0.0F, (float)tex.getHeight()) - float1;
                float float4 = float0 / tex.getWidth();
                float float5 = float1 / tex.getHeight();
                float float6 = (float0 + float2) / tex.getWidth();
                float float7 = (float1 + float3) / tex.getHeight();
                float float8 = tex.getXEnd() - tex.getXStart();
                float float9 = tex.getYEnd() - tex.getYStart();
                float4 = tex.getXStart() + float4 * float8;
                float6 = tex.getXStart() + float6 * float8;
                float5 = tex.getYStart() + float5 * float9;
                float7 = tex.getYStart() + float7 * float9;
                SpriteRenderer.instance
                    .render(
                        tex,
                        (float)double0,
                        (float)double1,
                        (float)w,
                        (float)h,
                        (float)r,
                        (float)g,
                        (float)b,
                        (float)a,
                        float4,
                        float5,
                        float6,
                        float5,
                        float6,
                        float7,
                        float4,
                        float7
                    );
            }
        }
    }

    public void DrawTextureTiled(Texture tex, double _x, double _y, double w, double h, double r, double g, double b, double a) {
        if (tex != null && this.isVisible() && !(w <= 0.0) && !(h <= 0.0)) {
            for (double double0 = _y; double0 < _y + h; double0 += tex.getHeight()) {
                for (double double1 = _x; double1 < _x + w; double1 += tex.getWidth()) {
                    double double2 = tex.getWidth();
                    double double3 = tex.getHeight();
                    if (double1 + double2 > _x + w) {
                        double2 = _x + w - double1;
                    }

                    if (double0 + tex.getHeight() > _y + h) {
                        double3 = _y + h - double0;
                    }

                    this.DrawSubTextureRGBA(tex, 0.0, 0.0, double2, double3, double1, double0, double2, double3, r, g, b, a);
                }
            }
        }
    }

    public void DrawTextureTiledX(Texture tex, double _x, double _y, double w, double h, double r, double g, double b, double a) {
        if (tex != null && this.isVisible() && !(w <= 0.0) && !(h <= 0.0)) {
            for (double double0 = _x; double0 < _x + w; double0 += tex.getWidth()) {
                double double1 = tex.getWidth();
                double double2 = tex.getHeight();
                if (double0 + double1 > _x + w) {
                    double1 = _x + w - double0;
                }

                this.DrawSubTextureRGBA(tex, 0.0, 0.0, double1, double2, double0, _y, double1, double2, r, g, b, a);
            }
        }
    }

    public void DrawTextureTiledY(Texture tex, double _x, double _y, double w, double h, double r, double g, double b, double a) {
        if (tex != null && this.isVisible() && !(w <= 0.0) && !(h <= 0.0)) {
            for (double double0 = _y; double0 < _y + h; double0 += tex.getHeight()) {
                double double1 = tex.getWidth();
                double double2 = tex.getHeight();
                if (double0 + tex.getHeight() > _y + h) {
                    double2 = _y + h - double0;
                }

                this.DrawSubTextureRGBA(tex, 0.0, 0.0, double1, double2, _x, double0, double1, double2, r, g, b, a);
            }
        }
    }

    public void DrawTextureIgnoreOffset(Texture tex, double _x, double _y, int _width, int _height, Color col) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            SpriteRenderer.instance
                .render(tex, (int)(double0 + this.xScroll), (int)(double1 + this.yScroll), _width, _height, col.r, col.g, col.b, col.a, null);
        }
    }

    public void DrawTexture_FlippedX(Texture tex, double _x, double _y, int _width, int _height, Color col) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            SpriteRenderer.instance
                .renderflipped(tex, (float)(double0 + this.xScroll), (float)(double1 + this.yScroll), _width, _height, col.r, col.g, col.b, col.a, null);
        }
    }

    public void DrawTexture_FlippedXIgnoreOffset(Texture tex, double _x, double _y, int _width, int _height, Color col) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            SpriteRenderer.instance
                .renderflipped(tex, (float)(double0 + this.xScroll), (float)(double1 + this.yScroll), _width, _height, col.r, col.g, col.b, col.a, null);
        }
    }

    public void DrawUVSliceTexture(
        Texture tex, double _x, double _y, double _width, double _height, Color col, double xStart, double yStart, double xEnd, double yEnd
    ) {
        if (this.isVisible()) {
            double double0 = _x + this.getAbsoluteX();
            double double1 = _y + this.getAbsoluteY();
            double0 += tex.offsetX;
            double1 += tex.offsetY;
            Texture.lr = col.r;
            Texture.lg = col.g;
            Texture.lb = col.b;
            Texture.la = col.a;
            double double2 = tex.getXStart();
            double double3 = tex.getYStart();
            double double4 = tex.getXEnd();
            double double5 = tex.getYEnd();
            double double6 = double4 - double2;
            double double7 = double5 - double3;
            double double8 = xEnd - xStart;
            double double9 = yEnd - yStart;
            double double10 = double8 / 1.0;
            double double11 = double9 / 1.0;
            double2 += xStart * double6;
            double3 += yStart * double7;
            double4 -= (1.0 - xEnd) * double6;
            double5 -= (1.0 - yEnd) * double7;
            double2 = (int)(double2 * 1000.0) / 1000.0F;
            double4 = (int)(double4 * 1000.0) / 1000.0F;
            double3 = (int)(double3 * 1000.0) / 1000.0F;
            double5 = (int)(double5 * 1000.0) / 1000.0F;
            double double12 = double0 + _width;
            double double13 = double1 + _height;
            double0 += xStart * _width;
            double1 += yStart * _height;
            double12 -= (1.0 - xEnd) * _width;
            double13 -= (1.0 - yEnd) * _height;
            SpriteRenderer.instance
                .render(
                    tex,
                    (float)double0 + this.getXScroll().intValue(),
                    (float)double1 + this.getYScroll().intValue(),
                    (float)(double12 - double0),
                    (float)(double13 - double1),
                    col.r,
                    col.g,
                    col.b,
                    col.a,
                    (float)double2,
                    (float)double3,
                    (float)double4,
                    (float)double3,
                    (float)double4,
                    (float)double5,
                    (float)double2,
                    (float)double5
                );
        }
    }

    public Boolean getScrollChildren() {
        return this.bScrollChildren ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setScrollChildren(boolean bScroll) {
        this.bScrollChildren = bScroll;
    }

    public Boolean getScrollWithParent() {
        return this.bScrollWithParent ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setScrollWithParent(boolean bScroll) {
        this.bScrollWithParent = bScroll;
    }

    public void setRenderClippedChildren(boolean b) {
        this.bRenderClippedChildren = b;
    }

    public Double getAbsoluteX() {
        if (this.getParent() != null) {
            return this.getParent().getScrollChildren() && this.getScrollWithParent()
                ? BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + this.getX().intValue() + this.getParent().getXScroll().intValue())
                : BoxedStaticValues.toDouble(this.getParent().getAbsoluteX() + this.getX().intValue());
        } else {
            return BoxedStaticValues.toDouble(this.getX().intValue());
        }
    }

    public Double getAbsoluteY() {
        if (this.getParent() != null) {
            return this.getParent().getScrollChildren() && this.getScrollWithParent()
                ? BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + this.getY().intValue() + this.getParent().getYScroll().intValue())
                : BoxedStaticValues.toDouble(this.getParent().getAbsoluteY() + this.getY().intValue());
        } else {
            return BoxedStaticValues.toDouble(this.getY().intValue());
        }
    }

    public String getClickedValue() {
        return this.clickedValue;
    }

    /**
     * 
     * @param _clickedValue the clickedValue to set
     */
    public void setClickedValue(String _clickedValue) {
        this.clickedValue = _clickedValue;
    }

    public void bringToTop() {
        UIManager.pushToTop(this);
        if (this.Parent != null) {
            this.Parent.addBringToTop(this);
        }
    }

    void onRightMouseUpOutside(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onRightMouseUpOutside") != null) {
            LuaManager.caller
                .protectedCallVoid(
                    UIManager.getDefaultThread(),
                    this.getTable().rawget("onRightMouseUpOutside"),
                    this.table,
                    BoxedStaticValues.toDouble(double1 - this.xScroll),
                    BoxedStaticValues.toDouble(double0 - this.yScroll)
                );
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement1 = this.getControls().get(int0);
            uIElement1.onRightMouseUpOutside(double1 - uIElement1.getXScrolled(this).intValue(), double0 - uIElement1.getYScrolled(this).intValue());
        }
    }

    void onRightMouseDownOutside(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onRightMouseDownOutside") != null) {
            LuaManager.caller
                .protectedCallVoid(
                    UIManager.getDefaultThread(),
                    this.getTable().rawget("onRightMouseDownOutside"),
                    this.table,
                    BoxedStaticValues.toDouble(double1 - this.xScroll),
                    BoxedStaticValues.toDouble(double0 - this.yScroll)
                );
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement1 = this.getControls().get(int0);
            uIElement1.onRightMouseDownOutside(double1 - uIElement1.getXScrolled(this).intValue(), double0 - uIElement1.getYScrolled(this).intValue());
        }
    }

    public void onMouseUpOutside(double _x, double _y) {
        if (this.getTable() != null && this.getTable().rawget("onMouseUpOutside") != null) {
            LuaManager.caller
                .protectedCallVoid(
                    UIManager.getDefaultThread(),
                    this.getTable().rawget("onMouseUpOutside"),
                    this.table,
                    BoxedStaticValues.toDouble(_x - this.xScroll),
                    BoxedStaticValues.toDouble(_y - this.yScroll)
                );
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = this.getControls().get(int0);
            uIElement.onMouseUpOutside(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue());
        }
    }

    void onMouseDownOutside(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onMouseDownOutside") != null) {
            LuaManager.caller
                .protectedCallVoid(
                    UIManager.getDefaultThread(),
                    this.getTable().rawget("onMouseDownOutside"),
                    this.table,
                    BoxedStaticValues.toDouble(double1 - this.xScroll),
                    BoxedStaticValues.toDouble(double0 - this.yScroll)
                );
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement1 = this.getControls().get(int0);
            uIElement1.onMouseDownOutside(double1 - uIElement1.getX().intValue(), double0 - uIElement1.getY().intValue());
        }
    }

    public Boolean onMouseDown(double _x, double _y) {
        if (this.clicked
            && UIManager.isDoubleClick((int)this.clickX, (int)this.clickY, (int)_x, (int)_y, this.leftDownTime)
            && this.getTable() != null
            && this.getTable().rawget("onMouseDoubleClick") != null) {
            this.clicked = false;
            return this.onMouseDoubleClick(_x, _y) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            this.clicked = true;
            this.clickX = _x;
            this.clickY = _y;
            this.leftDownTime = System.currentTimeMillis();
            if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= _y) {
                return Boolean.FALSE;
            } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= _y) {
                return Boolean.FALSE;
            } else if (!this.visible) {
                return Boolean.FALSE;
            } else {
                if (this.getTable() != null && this.getTable().rawget("onFocus") != null) {
                    LuaManager.caller
                        .protectedCallVoid(
                            UIManager.getDefaultThread(),
                            this.getTable().rawget("onFocus"),
                            this.table,
                            BoxedStaticValues.toDouble(_x - this.xScroll),
                            BoxedStaticValues.toDouble(_y - this.yScroll)
                        );
                }

                boolean boolean0 = false;

                for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
                    UIElement uIElement = this.getControls().get(int0);
                    if (!boolean0
                        && (
                            _x > uIElement.getXScrolled(this)
                                    && _y > uIElement.getYScrolled(this)
                                    && _x < uIElement.getXScrolled(this) + uIElement.getWidth()
                                    && _y < uIElement.getYScrolled(this) + uIElement.getHeight()
                                || uIElement.isCapture()
                        )) {
                        if (uIElement.onMouseDown(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue())) {
                            boolean0 = true;
                        }
                    } else if (uIElement.getTable() != null && uIElement.getTable().rawget("onMouseDownOutside") != null) {
                        LuaManager.caller
                            .protectedCallVoid(
                                UIManager.getDefaultThread(),
                                uIElement.getTable().rawget("onMouseDownOutside"),
                                uIElement.getTable(),
                                BoxedStaticValues.toDouble(_x - this.xScroll),
                                BoxedStaticValues.toDouble(_y - this.yScroll)
                            );
                    }
                }

                if (this.getTable() != null) {
                    if (boolean0) {
                        if (this.getTable().rawget("onMouseDownOutside") != null) {
                            Boolean boolean1 = LuaManager.caller
                                .protectedCallBoolean(
                                    UIManager.getDefaultThread(),
                                    this.getTable().rawget("onMouseDownOutside"),
                                    this.table,
                                    BoxedStaticValues.toDouble(_x - this.xScroll),
                                    BoxedStaticValues.toDouble(_y - this.yScroll)
                                );
                            if (boolean1 == null) {
                                return Boolean.TRUE;
                            }

                            if (boolean1 == Boolean.TRUE) {
                                return Boolean.TRUE;
                            }
                        }
                    } else if (this.getTable().rawget("onMouseDown") != null) {
                        Boolean boolean2 = LuaManager.caller
                            .protectedCallBoolean(
                                UIManager.getDefaultThread(),
                                this.getTable().rawget("onMouseDown"),
                                this.table,
                                BoxedStaticValues.toDouble(_x - this.xScroll),
                                BoxedStaticValues.toDouble(_y - this.yScroll)
                            );
                        if (boolean2 == null) {
                            return Boolean.TRUE;
                        }

                        if (boolean2 == Boolean.TRUE) {
                            return Boolean.TRUE;
                        }
                    }
                }

                return boolean0;
            }
        }
    }

    private Boolean onMouseDoubleClick(double double1, double double0) {
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        } else if (!this.visible) {
            return Boolean.FALSE;
        } else {
            if (this.getTable().rawget("onMouseDoubleClick") != null) {
                Boolean boolean0 = LuaManager.caller
                    .protectedCallBoolean(
                        UIManager.getDefaultThread(),
                        this.getTable().rawget("onMouseDoubleClick"),
                        this.table,
                        BoxedStaticValues.toDouble(double1 - this.xScroll),
                        BoxedStaticValues.toDouble(double0 - this.yScroll)
                    );
                if (boolean0 == null) {
                    return Boolean.TRUE;
                }

                if (boolean0 == Boolean.TRUE) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.TRUE;
        }
    }

    public Boolean onMouseWheel(double del) {
        int int0 = Mouse.getXA();
        int int1 = Mouse.getYA();
        if (this.getTable() != null && this.getTable().rawget("onMouseWheel") != null) {
            Boolean boolean0 = LuaManager.caller
                .protectedCallBoolean(UIManager.getDefaultThread(), this.getTable().rawget("onMouseWheel"), this.table, BoxedStaticValues.toDouble(del));
            if (boolean0 == Boolean.TRUE) {
                return Boolean.TRUE;
            }
        }

        for (int int2 = this.getControls().size() - 1; int2 >= 0; int2--) {
            UIElement uIElement = this.getControls().get(int2);
            if (uIElement.isVisible()
                && (
                    int0 >= uIElement.getAbsoluteX()
                            && int1 >= uIElement.getAbsoluteY()
                            && int0 < uIElement.getAbsoluteX() + uIElement.getWidth()
                            && int1 < uIElement.getAbsoluteY() + uIElement.getHeight()
                        || uIElement.isCapture()
                )
                && uIElement.onMouseWheel(del)) {
                return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
            }
        }

        return Boolean.FALSE;
    }

    public Boolean onMouseMove(double dx, double dy) {
        int int0 = Mouse.getXA();
        int int1 = Mouse.getYA();
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= this.y) {
            return Boolean.FALSE;
        } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= int1 - this.getAbsoluteY()) {
            return Boolean.FALSE;
        } else if (!this.visible) {
            return Boolean.FALSE;
        } else {
            if (this.getTable() != null && this.getTable().rawget("onMouseMove") != null) {
                LuaManager.caller
                    .protectedCallVoid(
                        UIManager.getDefaultThread(),
                        this.getTable().rawget("onMouseMove"),
                        this.table,
                        BoxedStaticValues.toDouble(dx),
                        BoxedStaticValues.toDouble(dy)
                    );
            }

            boolean boolean0 = false;

            for (int int2 = this.getControls().size() - 1; int2 >= 0; int2--) {
                UIElement uIElement = this.getControls().get(int2);
                if ((
                        !(int0 >= uIElement.getAbsoluteX())
                            || !(int1 >= uIElement.getAbsoluteY())
                            || !(int0 < uIElement.getAbsoluteX() + uIElement.getWidth())
                            || !(int1 < uIElement.getAbsoluteY() + uIElement.getHeight())
                    )
                    && !uIElement.isCapture()) {
                    uIElement.onMouseMoveOutside(dx, dy);
                } else if (!boolean0 && uIElement.onMouseMove(dx, dy)) {
                    boolean0 = true;
                }
            }

            return this.bConsumeMouseEvents ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public void onMouseMoveOutside(double dx, double dy) {
        if (this.getTable() != null && this.getTable().rawget("onMouseMoveOutside") != null) {
            LuaManager.caller
                .protectedCallVoid(
                    UIManager.getDefaultThread(),
                    this.getTable().rawget("onMouseMoveOutside"),
                    this.table,
                    BoxedStaticValues.toDouble(dx),
                    BoxedStaticValues.toDouble(dy)
                );
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement = this.getControls().get(int0);
            uIElement.onMouseMoveOutside(dx, dy);
        }
    }

    public Boolean onMouseUp(double _x, double _y) {
        if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else if (!this.visible) {
            return Boolean.FALSE;
        } else {
            boolean boolean0 = false;

            for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
                UIElement uIElement = this.getControls().get(int0);
                if (!boolean0
                    && (
                        _x >= uIElement.getXScrolled(this)
                                && _y >= uIElement.getYScrolled(this)
                                && _x < uIElement.getXScrolled(this) + uIElement.getWidth()
                                && _y < uIElement.getYScrolled(this) + uIElement.getHeight()
                            || uIElement.isCapture()
                    )) {
                    if (uIElement.onMouseUp(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue())) {
                        boolean0 = true;
                    }
                } else {
                    uIElement.onMouseUpOutside(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue());
                }

                int0 = PZMath.min(int0, this.getControls().size());
            }

            if (this.getTable() != null) {
                if (boolean0) {
                    if (this.getTable().rawget("onMouseUpOutside") != null) {
                        Boolean boolean1 = LuaManager.caller
                            .protectedCallBoolean(
                                UIManager.getDefaultThread(),
                                this.getTable().rawget("onMouseUpOutside"),
                                this.table,
                                BoxedStaticValues.toDouble(_x - this.xScroll),
                                BoxedStaticValues.toDouble(_y - this.yScroll)
                            );
                        if (boolean1 == null) {
                            return Boolean.TRUE;
                        }

                        if (boolean1 == Boolean.TRUE) {
                            return Boolean.TRUE;
                        }
                    }
                } else if (this.getTable().rawget("onMouseUp") != null) {
                    Boolean boolean2 = LuaManager.caller
                        .protectedCallBoolean(
                            UIManager.getDefaultThread(),
                            this.getTable().rawget("onMouseUp"),
                            this.table,
                            BoxedStaticValues.toDouble(_x - this.xScroll),
                            BoxedStaticValues.toDouble(_y - this.yScroll)
                        );
                    if (boolean2 == null) {
                        return Boolean.TRUE;
                    }

                    if (boolean2 == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }

            return boolean0 ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public void onresize() {
    }

    public void onResize() {
        if (this.Parent != null && this.Parent.bResizeDirty) {
            double double0 = this.Parent.getWidth() - this.Parent.lastwidth;
            double double1 = this.Parent.getHeight() - this.Parent.lastheight;
            if (!this.anchorTop && this.anchorBottom) {
                this.setY(this.getY() + double1);
            }

            if (this.anchorTop && this.anchorBottom) {
                this.setHeight(this.getHeight() + double1);
            }

            if (!this.anchorLeft && this.anchorRight) {
                this.setX(this.getX() + double0);
            }

            if (this.anchorLeft && this.anchorRight) {
                this.setWidth(this.getWidth() + double0);
            }
        }

        if (this.getTable() != null && this.getTable().rawget("onResize") != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("onResize"), this.table, this.getWidth(), this.getHeight());
        }

        for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
            UIElement uIElement1 = this.getControls().get(int0);
            if (uIElement1 == null) {
                this.getControls().remove(int0);
            } else {
                uIElement1.onResize();
            }
        }

        this.bResizeDirty = false;
        this.lastwidth = this.getWidth();
        this.lastheight = this.getHeight();
    }

    public Boolean onRightMouseDown(double _x, double _y) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else {
            boolean boolean0 = false;

            for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
                UIElement uIElement = this.getControls().get(int0);
                if (!boolean0
                    && (
                        _x >= uIElement.getXScrolled(this)
                                && _y >= uIElement.getYScrolled(this)
                                && _x < uIElement.getXScrolled(this) + uIElement.getWidth()
                                && _y < uIElement.getYScrolled(this) + uIElement.getHeight()
                            || uIElement.isCapture()
                    )) {
                    if (uIElement.onRightMouseDown(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue())) {
                        boolean0 = true;
                    }
                } else if (uIElement.getTable() != null && uIElement.getTable().rawget("onRightMouseDownOutside") != null) {
                    LuaManager.caller
                        .protectedCallVoid(
                            UIManager.getDefaultThread(),
                            uIElement.getTable().rawget("onRightMouseDownOutside"),
                            uIElement.getTable(),
                            BoxedStaticValues.toDouble(_x - this.xScroll),
                            BoxedStaticValues.toDouble(_y - this.yScroll)
                        );
                }
            }

            if (this.getTable() != null) {
                if (boolean0) {
                    if (this.getTable().rawget("onRightMouseDownOutside") != null) {
                        Boolean boolean1 = LuaManager.caller
                            .protectedCallBoolean(
                                UIManager.getDefaultThread(),
                                this.getTable().rawget("onRightMouseDownOutside"),
                                this.table,
                                BoxedStaticValues.toDouble(_x - this.xScroll),
                                BoxedStaticValues.toDouble(_y - this.yScroll)
                            );
                        if (boolean1 == null) {
                            return Boolean.TRUE;
                        }

                        if (boolean1 == Boolean.TRUE) {
                            return Boolean.TRUE;
                        }
                    }
                } else if (this.getTable().rawget("onRightMouseDown") != null) {
                    Boolean boolean2 = LuaManager.caller
                        .protectedCallBoolean(
                            UIManager.getDefaultThread(),
                            this.getTable().rawget("onRightMouseDown"),
                            this.table,
                            BoxedStaticValues.toDouble(_x - this.xScroll),
                            BoxedStaticValues.toDouble(_y - this.yScroll)
                        );
                    if (boolean2 == null) {
                        return Boolean.TRUE;
                    }

                    if (boolean2 == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }

            return boolean0 ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public Boolean onRightMouseUp(double _x, double _y) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else if (this.Parent != null && this.Parent.maxDrawHeight != -1 && this.Parent.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else if (this.maxDrawHeight != -1 && this.maxDrawHeight <= _y) {
            return Boolean.FALSE;
        } else {
            boolean boolean0 = false;

            for (int int0 = this.getControls().size() - 1; int0 >= 0; int0--) {
                UIElement uIElement = this.getControls().get(int0);
                if (!boolean0
                    && (
                        _x >= uIElement.getXScrolled(this)
                                && _y >= uIElement.getYScrolled(this)
                                && _x < uIElement.getXScrolled(this) + uIElement.getWidth()
                                && _y < uIElement.getYScrolled(this) + uIElement.getHeight()
                            || uIElement.isCapture()
                    )) {
                    if (uIElement.onRightMouseUp(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue())) {
                        boolean0 = true;
                    }
                } else {
                    uIElement.onRightMouseUpOutside(_x - uIElement.getXScrolled(this).intValue(), _y - uIElement.getYScrolled(this).intValue());
                }
            }

            if (this.getTable() != null) {
                if (boolean0) {
                    if (this.getTable().rawget("onRightMouseUpOutside") != null) {
                        Boolean boolean1 = LuaManager.caller
                            .protectedCallBoolean(
                                UIManager.getDefaultThread(),
                                this.getTable().rawget("onRightMouseUpOutside"),
                                this.table,
                                BoxedStaticValues.toDouble(_x - this.xScroll),
                                BoxedStaticValues.toDouble(_y - this.yScroll)
                            );
                        if (boolean1 == null) {
                            return Boolean.TRUE;
                        }

                        if (boolean1 == Boolean.TRUE) {
                            return Boolean.TRUE;
                        }
                    }
                } else if (this.getTable().rawget("onRightMouseUp") != null) {
                    Boolean boolean2 = LuaManager.caller
                        .protectedCallBoolean(
                            UIManager.getDefaultThread(),
                            this.getTable().rawget("onRightMouseUp"),
                            this.table,
                            BoxedStaticValues.toDouble(_x - this.xScroll),
                            BoxedStaticValues.toDouble(_y - this.yScroll)
                        );
                    if (boolean2 == null) {
                        return Boolean.TRUE;
                    }

                    if (boolean2 == Boolean.TRUE) {
                        return Boolean.TRUE;
                    }
                }
            }

            return boolean0 ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public void RemoveControl(UIElement el) {
        this.getControls().remove(el);
        el.setParent(null);
    }

    public void render() {
        if (this.enabled) {
            if (this.isVisible()) {
                if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !(this.Parent.maxDrawHeight <= this.y)) {
                    if (this.Parent != null && !this.Parent.bRenderClippedChildren) {
                        Double double0 = this.Parent.getAbsoluteY();
                        double double1 = this.getAbsoluteY();
                        if (double1 + this.getHeight() <= double0 || double1 >= double0 + this.getParent().getHeight()) {
                            return;
                        }
                    }

                    if (this.getTable() != null && this.getTable().rawget("prerender") != null) {
                        try {
                            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("prerender"), this.table);
                        } catch (Exception exception) {
                            boolean boolean0 = false;
                        }
                    }

                    for (int int0 = 0; int0 < this.getControls().size(); int0++) {
                        this.getControls().get(int0).render();
                    }

                    if (this.getTable() != null && this.getTable().rawget("render") != null) {
                        LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("render"), this.table);
                    }

                    if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
                        if (this.table != null && "ISScrollingListBox".equals(this.table.rawget("Type"))) {
                            this.repaintStencilRect(0.0, 0.0, (int)this.width, (int)this.height);
                        }

                        Double double2 = -this.getXScroll();
                        Double double3 = -this.getYScroll();
                        double double4 = 1.0;
                        if (this.isMouseOver()) {
                            double4 = 0.0;
                        }

                        double double5 = this.maxDrawHeight == -1 ? this.height : this.maxDrawHeight;
                        this.DrawTextureScaledColor(null, double2, double3, 1.0, double5, double4, 1.0, 1.0, 0.5);
                        this.DrawTextureScaledColor(null, double2 + 1.0, double3, this.width - 2.0, 1.0, double4, 1.0, 1.0, 0.5);
                        this.DrawTextureScaledColor(null, double2 + this.width - 1.0, double3, 1.0, double5, double4, 1.0, 1.0, 0.5);
                        this.DrawTextureScaledColor(null, double2 + 1.0, double3 + double5 - 1.0, this.width - 2.0, 1.0, double4, 1.0, 1.0, 0.5);
                    }
                }
            }
        }
    }

    public void update() {
        if (this.enabled) {
            for (int int0 = 0; int0 < this.Controls.size(); int0++) {
                if (this.toTop.contains(this.Controls.get(int0))) {
                    UIElement uIElement1 = this.Controls.remove(int0);
                    int0--;
                    toAdd.add(uIElement1);
                }
            }

            this.Controls.addAll(toAdd);
            toAdd.clear();
            this.toTop.clear();
            if (UIManager.doTick && this.getTable() != null && this.getTable().rawget("update") != null) {
                LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.getTable().rawget("update"), this.table);
            }

            if (this.bResizeDirty) {
                this.onResize();
                this.lastwidth = this.width;
                this.lastheight = this.height;
                this.bResizeDirty = false;
            }

            for (int int1 = 0; int1 < this.getControls().size(); int1++) {
                this.getControls().get(int1).update();
            }
        }
    }

    public void BringToTop(UIElement el) {
        this.getControls().remove(el);
        this.getControls().add(el);
    }

    /**
     * @return the capture
     */
    public Boolean isCapture() {
        return this.capture ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _capture the capture to set
     */
    public void setCapture(boolean _capture) {
        this.capture = _capture;
    }

    /**
     * @return the IgnoreLossControl
     */
    public Boolean isIgnoreLossControl() {
        return this.IgnoreLossControl ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _IgnoreLossControl the IgnoreLossControl to set
     */
    public void setIgnoreLossControl(boolean _IgnoreLossControl) {
        this.IgnoreLossControl = _IgnoreLossControl;
    }

    /**
     * @return the Controls
     */
    public ArrayList<UIElement> getControls() {
        return this.Controls;
    }

    /**
     * 
     * @param _Controls the Controls to set
     */
    public void setControls(Vector<UIElement> _Controls) {
        this.setControls(_Controls);
    }

    /**
     * @return the defaultDraw
     */
    public Boolean isDefaultDraw() {
        return this.defaultDraw ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _defaultDraw the defaultDraw to set
     */
    public void setDefaultDraw(boolean _defaultDraw) {
        this.defaultDraw = _defaultDraw;
    }

    /**
     * @return the followGameWorld
     */
    public Boolean isFollowGameWorld() {
        return this.followGameWorld ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _followGameWorld the followGameWorld to set
     */
    public void setFollowGameWorld(boolean _followGameWorld) {
        this.followGameWorld = _followGameWorld;
    }

    public int getRenderThisPlayerOnly() {
        return this.renderThisPlayerOnly;
    }

    public void setRenderThisPlayerOnly(int playerIndex) {
        this.renderThisPlayerOnly = playerIndex;
    }

    /**
     * @return the height
     */
    public Double getHeight() {
        return BoxedStaticValues.toDouble(this.height);
    }

    /**
     * 
     * @param _height the height to set
     */
    public void setHeight(double _height) {
        if (this.height != _height) {
            this.bResizeDirty = true;
        }

        this.lastheight = this.height;
        this.height = (float)_height;
    }

    /**
     * @return the Parent
     */
    public UIElement getParent() {
        return this.Parent;
    }

    /**
     * 
     * @param _Parent the Parent to set
     */
    public void setParent(UIElement _Parent) {
        this.Parent = _Parent;
    }

    /**
     * @return the visible
     */
    public Boolean isVisible() {
        return this.visible ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _visible the visible to set
     */
    public void setVisible(boolean _visible) {
        this.visible = _visible;
    }

    /**
     * @return the width
     */
    public Double getWidth() {
        return BoxedStaticValues.toDouble(this.width);
    }

    /**
     * 
     * @param _width the width to set
     */
    public void setWidth(double _width) {
        if (this.width != _width) {
            this.bResizeDirty = true;
        }

        this.lastwidth = this.width;
        this.width = (float)_width;
    }

    /**
     * @return the x
     */
    public Double getX() {
        return BoxedStaticValues.toDouble(this.x);
    }

    /**
     * 
     * @param _x the x to set
     */
    public void setX(double _x) {
        this.x = (float)_x;
    }

    public Double getXScrolled(UIElement parent) {
        return parent != null && parent.bScrollChildren && this.bScrollWithParent
            ? BoxedStaticValues.toDouble(this.x + parent.getXScroll())
            : BoxedStaticValues.toDouble(this.x);
    }

    public Double getYScrolled(UIElement parent) {
        return parent != null && parent.bScrollChildren && this.bScrollWithParent
            ? BoxedStaticValues.toDouble(this.y + parent.getYScroll())
            : BoxedStaticValues.toDouble(this.y);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean en) {
        this.enabled = en;
    }

    /**
     * @return the y
     */
    public Double getY() {
        return BoxedStaticValues.toDouble(this.y);
    }

    /**
     * 
     * @param _y the y to set
     */
    public void setY(double _y) {
        this.y = (float)_y;
    }

    public void suspendStencil() {
        IndieGL.disableStencilTest();
        IndieGL.disableAlphaTest();
    }

    public void resumeStencil() {
        IndieGL.enableStencilTest();
        IndieGL.enableAlphaTest();
    }

    public void setStencilRect(double _x, double _y, double _width, double _height) {
        _x += this.getAbsoluteX();
        _y += this.getAbsoluteY();
        IndieGL.glStencilMask(255);
        IndieGL.enableStencilTest();
        IndieGL.enableAlphaTest();
        StencilLevel++;
        IndieGL.glStencilFunc(519, StencilLevel, 255);
        IndieGL.glStencilOp(7680, 7680, 7681);
        IndieGL.glColorMask(false, false, false, false);
        SpriteRenderer.instance.renderi(null, (int)_x, (int)_y, (int)_width, (int)_height, 1.0F, 0.0F, 0.0F, 1.0F, null);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.glStencilOp(7680, 7680, 7680);
        IndieGL.glStencilFunc(514, StencilLevel, 255);
    }

    public void clearStencilRect() {
        if (StencilLevel > 0) {
            StencilLevel--;
        }

        if (StencilLevel > 0) {
            IndieGL.glStencilFunc(514, StencilLevel, 255);
        } else {
            IndieGL.glAlphaFunc(519, 0.0F);
            IndieGL.disableStencilTest();
            IndieGL.disableAlphaTest();
            IndieGL.glStencilFunc(519, 255, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
            IndieGL.glClear(1280);
        }
    }

    public void repaintStencilRect(double _x, double _y, double _width, double _height) {
        if (StencilLevel > 0) {
            _x += this.getAbsoluteX();
            _y += this.getAbsoluteY();
            IndieGL.glStencilFunc(519, StencilLevel, 255);
            IndieGL.glStencilOp(7680, 7680, 7681);
            IndieGL.glColorMask(false, false, false, false);
            SpriteRenderer.instance.renderi(null, (int)_x, (int)_y, (int)_width, (int)_height, 1.0F, 0.0F, 0.0F, 1.0F, null);
            IndieGL.glColorMask(true, true, true, true);
            IndieGL.glStencilOp(7680, 7680, 7680);
            IndieGL.glStencilFunc(514, StencilLevel, 255);
        }
    }

    /**
     * @return the table
     */
    public KahluaTable getTable() {
        return this.table;
    }

    /**
     * 
     * @param _table the table to set
     */
    public void setTable(KahluaTable _table) {
        this.table = _table;
    }

    public void setHeightSilent(double _height) {
        this.lastheight = this.height;
        this.height = (float)_height;
    }

    public void setWidthSilent(double _width) {
        this.lastwidth = this.width;
        this.width = (float)_width;
    }

    public void setHeightOnly(double _height) {
        this.height = (float)_height;
    }

    public void setWidthOnly(double _width) {
        this.width = (float)_width;
    }

    /**
     * @return the anchorTop
     */
    public boolean isAnchorTop() {
        return this.anchorTop;
    }

    /**
     * 
     * @param _anchorTop the anchorTop to set
     */
    public void setAnchorTop(boolean _anchorTop) {
        this.anchorTop = _anchorTop;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }

    public void ignoreWidthChange() {
        this.lastwidth = this.width;
    }

    public void ignoreHeightChange() {
        this.lastheight = this.height;
    }

    /**
     * @return the anchorLeft
     */
    public Boolean isAnchorLeft() {
        return this.anchorLeft ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _anchorLeft the anchorLeft to set
     */
    public void setAnchorLeft(boolean _anchorLeft) {
        this.anchorLeft = _anchorLeft;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }

    /**
     * @return the anchorRight
     */
    public Boolean isAnchorRight() {
        return this.anchorRight ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _anchorRight the anchorRight to set
     */
    public void setAnchorRight(boolean _anchorRight) {
        this.anchorRight = _anchorRight;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }

    /**
     * @return the anchorBottom
     */
    public Boolean isAnchorBottom() {
        return this.anchorBottom ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 
     * @param _anchorBottom the anchorBottom to set
     */
    public void setAnchorBottom(boolean _anchorBottom) {
        this.anchorBottom = _anchorBottom;
        this.lastwidth = this.width;
        this.lastheight = this.height;
    }

    private void addBringToTop(UIElement uIElement0) {
        this.toTop.add(uIElement0);
    }

    public int getPlayerContext() {
        return this.playerContext;
    }

    public void setPlayerContext(int nPlayer) {
        this.playerContext = nPlayer;
    }

    public String getUIName() {
        return this.uiname;
    }

    public void setUIName(String name) {
        this.uiname = name != null ? name : "";
    }

    public Double clampToParentX(double _x) {
        if (this.getParent() == null) {
            return BoxedStaticValues.toDouble(_x);
        } else {
            double double0 = this.getParent().clampToParentX(this.getParent().getAbsoluteX());
            double double1 = this.getParent().clampToParentX(double0 + this.getParent().getWidth().intValue());
            if (_x < double0) {
                _x = double0;
            }

            if (_x > double1) {
                _x = double1;
            }

            return BoxedStaticValues.toDouble(_x);
        }
    }

    public Double clampToParentY(double _y) {
        if (this.getParent() == null) {
            return _y;
        } else {
            double double0 = this.getParent().clampToParentY(this.getParent().getAbsoluteY());
            double double1 = this.getParent().clampToParentY(double0 + this.getParent().getHeight().intValue());
            if (_y < double0) {
                _y = double0;
            }

            if (_y > double1) {
                _y = double1;
            }

            return _y;
        }
    }

    public Boolean isPointOver(double screenX, double screenY) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            int int0 = this.getHeight().intValue();
            if (this.maxDrawHeight != -1) {
                int0 = Math.min(int0, this.maxDrawHeight);
            }

            double double0 = screenX - this.getAbsoluteX();
            double double1 = screenY - this.getAbsoluteY();
            if (double0 < 0.0 || double0 >= this.getWidth() || double1 < 0.0 || double1 >= int0) {
                return Boolean.FALSE;
            } else if (this.Parent == null) {
                ArrayList arrayList = UIManager.getUI();

                for (int int1 = arrayList.size() - 1; int1 >= 0; int1--) {
                    UIElement uIElement0 = (UIElement)arrayList.get(int1);
                    if (uIElement0 == this) {
                        break;
                    }

                    if (uIElement0.isPointOver(screenX, screenY)) {
                        return Boolean.FALSE;
                    }
                }

                return Boolean.TRUE;
            } else {
                for (int int2 = this.Parent.Controls.size() - 1; int2 >= 0; int2--) {
                    UIElement uIElement1 = this.Parent.Controls.get(int2);
                    if (uIElement1 == this) {
                        break;
                    }

                    if (uIElement1.isVisible()) {
                        int0 = uIElement1.getHeight().intValue();
                        if (uIElement1.maxDrawHeight != -1) {
                            int0 = Math.min(int0, uIElement1.maxDrawHeight);
                        }

                        double0 = screenX - uIElement1.getAbsoluteX();
                        double1 = screenY - uIElement1.getAbsoluteY();
                        if (double0 >= 0.0 && double0 < uIElement1.getWidth() && double1 >= 0.0 && double1 < int0) {
                            return Boolean.FALSE;
                        }
                    }
                }

                return this.Parent.isPointOver(screenX, screenY) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
    }

    public Boolean isMouseOver() {
        return this.isPointOver(Mouse.getXA(), Mouse.getYA()) ? Boolean.TRUE : Boolean.FALSE;
    }

    protected Object tryGetTableValue(String string) {
        return this.getTable() == null ? null : this.getTable().rawget(string);
    }

    public void setWantKeyEvents(boolean want) {
        this.bWantKeyEvents = want;
    }

    public boolean isWantKeyEvents() {
        return this.bWantKeyEvents;
    }

    public boolean isKeyConsumed(int key) {
        Object object = this.tryGetTableValue("isKeyConsumed");
        if (object == null) {
            return false;
        } else {
            Boolean boolean0 = LuaManager.caller.pcallBoolean(UIManager.getDefaultThread(), object, this.getTable(), BoxedStaticValues.toDouble(key));
            return boolean0 == null ? Boolean.FALSE : boolean0;
        }
    }

    public void onKeyPress(int key) {
        Object object = this.tryGetTableValue("onKeyPress");
        if (object != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), object, this.getTable(), BoxedStaticValues.toDouble(key));
        }
    }

    public void onKeyRepeat(int key) {
        Object object = this.tryGetTableValue("onKeyRepeat");
        if (object != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), object, this.getTable(), BoxedStaticValues.toDouble(key));
        }
    }

    public void onKeyRelease(int key) {
        Object object = this.tryGetTableValue("onKeyRelease");
        if (object != null) {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), object, this.getTable(), BoxedStaticValues.toDouble(key));
        }
    }

    public boolean isForceCursorVisible() {
        return this.bForceCursorVisible;
    }

    public void setForceCursorVisible(boolean force) {
        this.bForceCursorVisible = force;
    }
}
