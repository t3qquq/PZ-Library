// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import zombie.core.Color;
import zombie.core.SpriteRenderer;
import zombie.core.Styles.AbstractStyle;
import zombie.core.Styles.Style;
import zombie.core.Styles.TransparentStyle;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;

public abstract class GenericSpriteRenderState {
    public final int index;
    public TextureDraw[] sprite = new TextureDraw[2048];
    public Style[] style = new Style[2048];
    public int numSprites;
    public TextureFBO fbo;
    public boolean bRendered;
    private boolean m_isRendering;
    public final ArrayList<TextureDraw> postRender = new ArrayList<>();
    public AbstractStyle defaultStyle = TransparentStyle.instance;
    public boolean bCursorVisible = true;
    public static final byte UVCA_NONE = -1;
    public static final byte UVCA_CIRCLE = 1;
    public static final byte UVCA_NOCIRCLE = 2;
    private byte useVertColorsArray = -1;
    private int texture2_color0;
    private int texture2_color1;
    private int texture2_color2;
    private int texture2_color3;
    private SpriteRenderer.WallShaderTexRender wallShaderTexRender;
    private Texture texture1_cutaway;
    private int texture1_cutaway_x;
    private int texture1_cutaway_y;
    private int texture1_cutaway_w;
    private int texture1_cutaway_h;

    protected GenericSpriteRenderState(int int0) {
        this.index = int0;

        for (int int1 = 0; int1 < this.sprite.length; int1++) {
            this.sprite[int1] = new TextureDraw();
        }
    }

    public void onRendered() {
        this.m_isRendering = false;
        this.bRendered = true;
    }

    public void onRenderAcquired() {
        this.m_isRendering = true;
    }

    public boolean isRendering() {
        return this.m_isRendering;
    }

    public void onReady() {
        this.bRendered = false;
    }

    public boolean isReady() {
        return !this.bRendered;
    }

    public boolean isRendered() {
        return this.bRendered;
    }

    public void CheckSpriteSlots() {
        if (this.numSprites == this.sprite.length) {
            TextureDraw[] textureDraws = this.sprite;
            this.sprite = new TextureDraw[this.numSprites * 3 / 2 + 1];

            for (int int0 = this.numSprites; int0 < this.sprite.length; int0++) {
                this.sprite[int0] = new TextureDraw();
            }

            System.arraycopy(textureDraws, 0, this.sprite, 0, this.numSprites);
            Style[] styles = this.style;
            this.style = new Style[this.numSprites * 3 / 2 + 1];
            System.arraycopy(styles, 0, this.style, 0, this.numSprites);
        }
    }

    public static void clearSprites(List<TextureDraw> _postRender) {
        for (int int0 = 0; int0 < _postRender.size(); int0++) {
            ((TextureDraw)_postRender.get(int0)).postRender();
        }

        _postRender.clear();
    }

    public void clear() {
        clearSprites(this.postRender);
        this.numSprites = 0;
    }

    public void glDepthMask(boolean b) {
        this.CheckSpriteSlots();
        TextureDraw.glDepthMask(this.sprite[this.numSprites], b);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void renderflipped(Texture tex, float x, float y, float width, float height, float r, float g, float b, float a, Consumer<TextureDraw> texdModifier) {
        this.render(tex, x, y, width, height, r, g, b, a, texdModifier);
        this.sprite[this.numSprites - 1].flipped = true;
    }

    public void drawSkyBox(Shader shader, int playerIndex, int apiId, int bufferId) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.drawSkyBox(this.sprite[this.numSprites], shader, playerIndex, apiId, bufferId);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void drawWater(Shader shader, int playerIndex, int apiId, boolean bShore) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        shader.startMainThread(this.sprite[this.numSprites], playerIndex);
        TextureDraw.drawWater(this.sprite[this.numSprites], shader, playerIndex, apiId, bShore);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void drawPuddles(Shader shader, int playerIndex, int apiId, int z) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.drawPuddles(this.sprite[this.numSprites], shader, playerIndex, apiId, z);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void drawParticles(int playerIndex, int var1, int var2) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.drawParticles(this.sprite[this.numSprites], playerIndex, var1, var2);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void glDisable(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glDisable(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void glEnable(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glEnable(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glStencilMask(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glStencilMask(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glClear(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glClear(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glClearColor(int r, int g, int b, int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glClearColor(this.sprite[this.numSprites], r, g, b, a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glStencilFunc(int a, int b, int c) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glStencilFunc(this.sprite[this.numSprites], a, b, c);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glStencilOp(int a, int b, int c) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glStencilOp(this.sprite[this.numSprites], a, b, c);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glColorMask(int a, int b, int c, int d) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glColorMask(this.sprite[this.numSprites], a, b, c, d);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glAlphaFunc(int a, float b) {
        if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            TextureDraw.glAlphaFunc(this.sprite[this.numSprites], a, b);
            this.style[this.numSprites] = TransparentStyle.instance;
            this.numSprites++;
        }
    }

    public void glBlendFunc(int a, int b) {
        if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            TextureDraw.glBlendFunc(this.sprite[this.numSprites], a, b);
            this.style[this.numSprites] = TransparentStyle.instance;
            this.numSprites++;
        }
    }

    public void glBlendFuncSeparate(int a, int b, int c, int d) {
        if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            TextureDraw.glBlendFuncSeparate(this.sprite[this.numSprites], a, b, c, d);
            this.style[this.numSprites] = TransparentStyle.instance;
            this.numSprites++;
        }
    }

    public void glBlendEquation(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glBlendEquation(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void render(
        Texture tex,
        double x1,
        double y1,
        double x2,
        double y2,
        double x3,
        double y3,
        double x4,
        double y4,
        float r,
        float g,
        float b,
        float a,
        Consumer<TextureDraw> texdModifier
    ) {
        this.render(tex, x1, y1, x2, y2, x3, y3, x4, y4, r, g, b, a, r, g, b, a, r, g, b, a, r, g, b, a, texdModifier);
    }

    public void render(
        Texture tex,
        double x1,
        double y1,
        double x2,
        double y2,
        double x3,
        double y3,
        double x4,
        double y4,
        float r1,
        float g1,
        float b1,
        float a1,
        float r2,
        float g2,
        float b2,
        float a2,
        float r3,
        float g3,
        float b3,
        float a3,
        float r4,
        float g4,
        float b4,
        float a4,
        Consumer<TextureDraw> texdModifier
    ) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].reset();
        TextureDraw.Create(
            this.sprite[this.numSprites],
            tex,
            (float)x1,
            (float)y1,
            (float)x2,
            (float)y2,
            (float)x3,
            (float)y3,
            (float)x4,
            (float)y4,
            r1,
            g1,
            b1,
            a1,
            r2,
            g2,
            b2,
            a2,
            r3,
            g3,
            b3,
            a3,
            r4,
            g4,
            b4,
            a4,
            texdModifier
        );
        if (this.useVertColorsArray != -1) {
            TextureDraw textureDraw = this.sprite[this.numSprites];
            textureDraw.useAttribArray = this.useVertColorsArray;
            textureDraw.tex1_col0 = this.texture2_color0;
            textureDraw.tex1_col1 = this.texture2_color1;
            textureDraw.tex1_col2 = this.texture2_color2;
            textureDraw.tex1_col3 = this.texture2_color3;
        }

        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void setUseVertColorsArray(byte whichShader, int c0, int c1, int c2, int c3) {
        this.useVertColorsArray = whichShader;
        this.texture2_color0 = c0;
        this.texture2_color1 = c1;
        this.texture2_color2 = c2;
        this.texture2_color3 = c3;
    }

    public void clearUseVertColorsArray() {
        this.useVertColorsArray = -1;
    }

    public void renderdebug(
        Texture tex,
        float x1,
        float y1,
        float x2,
        float y2,
        float x3,
        float y3,
        float x4,
        float y4,
        float r1,
        float g1,
        float b1,
        float a1,
        float r2,
        float g2,
        float b2,
        float a2,
        float r3,
        float g3,
        float b3,
        float a3,
        float r4,
        float g4,
        float b4,
        float a4,
        Consumer<TextureDraw> texdModifier
    ) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].reset();
        TextureDraw.Create(
            this.sprite[this.numSprites], tex, x1, y1, x2, y2, x3, y3, x4, y4, r1, g1, b1, a1, r2, g2, b2, a2, r3, g3, b3, a3, r4, g4, b4, a4, texdModifier
        );
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void renderline(Texture tex, float x1, float y1, float x2, float y2, float r, float g, float b, float a, int thickness) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].reset();
        if (x1 <= x2 && y1 <= y2) {
            TextureDraw.Create(
                this.sprite[this.numSprites],
                tex,
                x1 + thickness,
                y1 - thickness,
                x2 + thickness,
                y2 - thickness,
                x2 - thickness,
                y2 + thickness,
                x1 - thickness,
                y1 + thickness,
                r,
                g,
                b,
                a
            );
        } else if (x1 >= x2 && y1 >= y2) {
            TextureDraw.Create(
                this.sprite[this.numSprites],
                tex,
                x1 + thickness,
                y1 - thickness,
                x1 - thickness,
                y1 + thickness,
                x2 - thickness,
                y2 + thickness,
                x2 + thickness,
                y2 - thickness,
                r,
                g,
                b,
                a
            );
        } else if (x1 >= x2 && y1 <= y2) {
            TextureDraw.Create(
                this.sprite[this.numSprites],
                tex,
                x2 - thickness,
                y2 - thickness,
                x1 - thickness,
                y1 - thickness,
                x1 + thickness,
                y1 + thickness,
                x2 + thickness,
                y2 + thickness,
                r,
                g,
                b,
                a
            );
        } else if (x1 <= x2 && y1 >= y2) {
            TextureDraw.Create(
                this.sprite[this.numSprites],
                tex,
                x1 - thickness,
                y1 - thickness,
                x1 + thickness,
                y1 + thickness,
                x2 + thickness,
                y2 + thickness,
                x2 - thickness,
                y2 - thickness,
                r,
                g,
                b,
                a
            );
        }

        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void renderline(Texture tex, int x1, int y1, int x2, int y2, float r, float g, float b, float a) {
        this.renderline(tex, x1, y1, x2, y2, r, g, b, a, 1);
    }

    public void render(Texture tex, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int c1, int c2, int c3, int c4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], tex, x1, y1, x2, y2, x3, y3, x4, y4, c1, c2, c3, c4);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void render(Texture tex, float x, float y, float width, float height, float r, float g, float b, float a, Consumer<TextureDraw> texdModifier) {
        if (tex == null || tex.isReady()) {
            if (a != 0.0F) {
                if (this.numSprites == this.sprite.length) {
                    this.CheckSpriteSlots();
                }

                this.sprite[this.numSprites].reset();
                int int0 = Color.colorToABGR(r, g, b, a);
                float float0 = x + width;
                float float1 = y + height;
                TextureDraw textureDraw;
                if (this.wallShaderTexRender == null) {
                    textureDraw = TextureDraw.Create(
                        this.sprite[this.numSprites], tex, x, y, float0, y, float0, float1, x, float1, int0, int0, int0, int0, texdModifier
                    );
                } else {
                    textureDraw = TextureDraw.Create(
                        this.sprite[this.numSprites], tex, this.wallShaderTexRender, x, y, float0 - x, float1 - y, r, g, b, a, texdModifier
                    );
                }

                if (this.useVertColorsArray != -1) {
                    textureDraw.useAttribArray = this.useVertColorsArray;
                    textureDraw.tex1_col0 = this.texture2_color0;
                    textureDraw.tex1_col1 = this.texture2_color1;
                    textureDraw.tex1_col2 = this.texture2_color2;
                    textureDraw.tex1_col3 = this.texture2_color3;
                }

                if (this.texture1_cutaway != null) {
                    textureDraw.tex1 = this.texture1_cutaway;
                    float float2 = this.texture1_cutaway.xEnd - this.texture1_cutaway.xStart;
                    float float3 = this.texture1_cutaway.yEnd - this.texture1_cutaway.yStart;
                    float float4 = (float)this.texture1_cutaway_x / this.texture1_cutaway.getWidth();
                    float float5 = (float)(this.texture1_cutaway_x + this.texture1_cutaway_w) / this.texture1_cutaway.getWidth();
                    float float6 = (float)this.texture1_cutaway_y / this.texture1_cutaway.getHeight();
                    float float7 = (float)(this.texture1_cutaway_y + this.texture1_cutaway_h) / this.texture1_cutaway.getHeight();
                    textureDraw.tex1_u0 = textureDraw.tex1_u3 = this.texture1_cutaway.xStart + float4 * float2;
                    textureDraw.tex1_v0 = textureDraw.tex1_v1 = this.texture1_cutaway.yStart + float6 * float3;
                    textureDraw.tex1_u1 = textureDraw.tex1_u2 = this.texture1_cutaway.xStart + float5 * float2;
                    textureDraw.tex1_v2 = textureDraw.tex1_v3 = this.texture1_cutaway.yStart + float7 * float3;
                }

                this.style[this.numSprites] = this.defaultStyle;
                this.numSprites++;
            }
        }
    }

    public void renderRect(int x, int y, int width, int height, float r, float g, float b, float a) {
        if (a != 0.0F) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            this.sprite[this.numSprites].reset();
            TextureDraw.Create(this.sprite[this.numSprites], null, x, y, width, height, r, g, b, a, null);
            this.style[this.numSprites] = this.defaultStyle;
            this.numSprites++;
        }
    }

    public void renderPoly(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float r, float g, float b, float a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].reset();
        TextureDraw.Create(this.sprite[this.numSprites], null, x1, y1, x2, y2, x3, y3, x4, y4, r, g, b, a);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void renderPoly(Texture tex, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float r, float g, float b, float a) {
        if (tex == null || tex.isReady()) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            this.sprite[this.numSprites].reset();
            TextureDraw.Create(this.sprite[this.numSprites], tex, x1, y1, x2, y2, x3, y3, x4, y4, r, g, b, a);
            if (tex != null) {
                float float0 = tex.getXEnd();
                float float1 = tex.getXStart();
                float float2 = tex.getYEnd();
                float float3 = tex.getYStart();
                TextureDraw textureDraw = this.sprite[this.numSprites];
                textureDraw.u0 = float1;
                textureDraw.u1 = float0;
                textureDraw.u2 = float0;
                textureDraw.u3 = float1;
                textureDraw.v0 = float3;
                textureDraw.v1 = float3;
                textureDraw.v2 = float2;
                textureDraw.v3 = float2;
            }

            this.style[this.numSprites] = this.defaultStyle;
            this.numSprites++;
        }
    }

    public void renderPoly(
        Texture tex,
        float x1,
        float y1,
        float x2,
        float y2,
        float x3,
        float y3,
        float x4,
        float y4,
        float r,
        float g,
        float b,
        float a,
        float u1,
        float v1,
        float u2,
        float v2,
        float u3,
        float v3,
        float u4,
        float v4
    ) {
        if (tex == null || tex.isReady()) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            this.sprite[this.numSprites].reset();
            TextureDraw.Create(this.sprite[this.numSprites], tex, x1, y1, x2, y2, x3, y3, x4, y4, r, g, b, a);
            if (tex != null) {
                TextureDraw textureDraw = this.sprite[this.numSprites];
                textureDraw.u0 = u1;
                textureDraw.u1 = u2;
                textureDraw.u2 = u3;
                textureDraw.u3 = u4;
                textureDraw.v0 = v1;
                textureDraw.v1 = v2;
                textureDraw.v2 = v3;
                textureDraw.v3 = v4;
            }

            this.style[this.numSprites] = this.defaultStyle;
            this.numSprites++;
        }
    }

    public void render(
        Texture tex,
        float x,
        float y,
        float width,
        float height,
        float r,
        float g,
        float b,
        float a,
        float u1,
        float v1,
        float u2,
        float v2,
        float u3,
        float v3,
        float u4,
        float v4,
        Consumer<TextureDraw> texdModifier
    ) {
        if (a != 0.0F) {
            if (this.numSprites == this.sprite.length) {
                this.CheckSpriteSlots();
            }

            this.sprite[this.numSprites].reset();
            TextureDraw.Create(this.sprite[this.numSprites], tex, x, y, width, height, r, g, b, a, u1, v1, u2, v2, u3, v3, u4, v4, texdModifier);
            this.style[this.numSprites] = this.defaultStyle;
            this.numSprites++;
        }
    }

    public void glBuffer(int i, int p) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glBuffer(this.sprite[this.numSprites], i, p);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glDoStartFrame(int w, int h, float zoom, int player) {
        this.glDoStartFrame(w, h, zoom, player, false);
    }

    public void glDoStartFrame(int w, int h, float zoom, int player, boolean isTextFrame) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glDoStartFrame(this.sprite[this.numSprites], w, h, zoom, player, isTextFrame);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glDoStartFrameFx(int w, int h, int player) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glDoStartFrameFx(this.sprite[this.numSprites], w, h, player);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glIgnoreStyles(boolean b) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glIgnoreStyles(this.sprite[this.numSprites], b);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glDoEndFrame() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glDoEndFrame(this.sprite[this.numSprites]);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glDoEndFrameFx(int player) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glDoEndFrameFx(this.sprite[this.numSprites], player);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void doCoreIntParam(int id, float val) {
        this.CheckSpriteSlots();
        TextureDraw.doCoreIntParam(this.sprite[this.numSprites], id, val);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glTexParameteri(int a, int b, int c) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glTexParameteri(this.sprite[this.numSprites], a, b, c);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void setCutawayTexture(Texture tex, int x, int y, int w, int h) {
        this.texture1_cutaway = tex;
        this.texture1_cutaway_x = x;
        this.texture1_cutaway_y = y;
        this.texture1_cutaway_w = w;
        this.texture1_cutaway_h = h;
    }

    public void clearCutawayTexture() {
        this.texture1_cutaway = null;
    }

    public void setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender wallTexRender) {
        this.wallShaderTexRender = wallTexRender;
    }

    public void ShaderUpdate1i(int shaderID, int uniform, int uniformValue) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.ShaderUpdate1i(this.sprite[this.numSprites], shaderID, uniform, uniformValue);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void ShaderUpdate1f(int shaderID, int uniform, float uniformValue) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.ShaderUpdate1f(this.sprite[this.numSprites], shaderID, uniform, uniformValue);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void ShaderUpdate2f(int shaderID, int uniform, float value1, float value2) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.ShaderUpdate2f(this.sprite[this.numSprites], shaderID, uniform, value1, value2);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void ShaderUpdate3f(int shaderID, int uniform, float value1, float value2, float value3) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.ShaderUpdate3f(this.sprite[this.numSprites], shaderID, uniform, value1, value2, value3);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void ShaderUpdate4f(int shaderID, int uniform, float value1, float value2, float value3, float value4) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.ShaderUpdate4f(this.sprite[this.numSprites], shaderID, uniform, value1, value2, value3, value4);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glLoadIdentity() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glLoadIdentity(this.sprite[this.numSprites]);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glGenerateMipMaps(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glGenerateMipMaps(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void glBind(int a) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glBind(this.sprite[this.numSprites], a);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void glViewport(int x, int y, int width, int height) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.glViewport(this.sprite[this.numSprites], x, y, width, height);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
    }

    public void drawModel(ModelManager.ModelSlot model) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.drawModel(this.sprite[this.numSprites], model);

        assert this.sprite[this.numSprites].drawer != null;

        ArrayList arrayList = this.postRender;
        arrayList.add(this.sprite[this.numSprites]);
        this.style[this.numSprites] = this.defaultStyle;
        this.numSprites++;
        model.renderRefCount++;
    }

    public void drawGeneric(TextureDraw.GenericDrawer gd) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        this.sprite[this.numSprites].type = TextureDraw.Type.DrawModel;
        this.sprite[this.numSprites].drawer = gd;
        this.style[this.numSprites] = this.defaultStyle;
        ArrayList arrayList = this.postRender;
        arrayList.add(this.sprite[this.numSprites]);
        this.numSprites++;
    }

    public void StartShader(int iD, int playerIndex) {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.StartShader(this.sprite[this.numSprites], iD);
        if (iD != 0 && Shader.ShaderMap.containsKey(iD)) {
            Shader.ShaderMap.get(iD).startMainThread(this.sprite[this.numSprites], playerIndex);
            ArrayList arrayList = this.postRender;
            arrayList.add(this.sprite[this.numSprites]);
        }

        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }

    public void EndShader() {
        if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
        }

        TextureDraw.StartShader(this.sprite[this.numSprites], 0);
        this.style[this.numSprites] = TransparentStyle.instance;
        this.numSprites++;
    }
}
