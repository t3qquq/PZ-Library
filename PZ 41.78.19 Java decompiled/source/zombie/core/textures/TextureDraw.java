// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.util.function.Consumer;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import zombie.IndieGL;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.iso.IsoWorld;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;

public final class TextureDraw {
    public TextureDraw.Type type = TextureDraw.Type.glDraw;
    public int a = 0;
    public int b = 0;
    public float f1 = 0.0F;
    public float[] vars;
    public int c = 0;
    public int d = 0;
    public int col0;
    public int col1;
    public int col2;
    public int col3;
    public float x0;
    public float x1;
    public float x2;
    public float x3;
    public float y0;
    public float y1;
    public float y2;
    public float y3;
    public float u0;
    public float u1;
    public float u2;
    public float u3;
    public float v0;
    public float v1;
    public float v2;
    public float v3;
    public Texture tex;
    public Texture tex1;
    public byte useAttribArray;
    public float tex1_u0;
    public float tex1_u1;
    public float tex1_u2;
    public float tex1_u3;
    public float tex1_v0;
    public float tex1_v1;
    public float tex1_v2;
    public float tex1_v3;
    public int tex1_col0;
    public int tex1_col1;
    public int tex1_col2;
    public int tex1_col3;
    public boolean bSingleCol = false;
    public boolean flipped = false;
    public TextureDraw.GenericDrawer drawer;

    public static void glStencilFunc(TextureDraw texd, int _a, int _b, int _c) {
        texd.type = TextureDraw.Type.glStencilFunc;
        texd.a = _a;
        texd.b = _b;
        texd.c = _c;
    }

    public static void glBuffer(TextureDraw texd, int _a, int _b) {
        texd.type = TextureDraw.Type.glBuffer;
        texd.a = _a;
        texd.b = _b;
    }

    public static void glStencilOp(TextureDraw texd, int _a, int _b, int _c) {
        texd.type = TextureDraw.Type.glStencilOp;
        texd.a = _a;
        texd.b = _b;
        texd.c = _c;
    }

    public static void glDisable(TextureDraw texd, int _a) {
        texd.type = TextureDraw.Type.glDisable;
        texd.a = _a;
    }

    public static void glClear(TextureDraw texd, int _a) {
        texd.type = TextureDraw.Type.glClear;
        texd.a = _a;
    }

    public static void glClearColor(TextureDraw texd, int r, int g, int _b, int _a) {
        texd.type = TextureDraw.Type.glClearColor;
        texd.col0 = r;
        texd.col1 = g;
        texd.col2 = _b;
        texd.col3 = _a;
    }

    public static void glEnable(TextureDraw texd, int _a) {
        texd.type = TextureDraw.Type.glEnable;
        texd.a = _a;
    }

    public static void glAlphaFunc(TextureDraw texd, int _a, float _b) {
        texd.type = TextureDraw.Type.glAlphaFunc;
        texd.a = _a;
        texd.f1 = _b;
    }

    public static void glColorMask(TextureDraw texd, int _a, int _b, int _c, int _d) {
        texd.type = TextureDraw.Type.glColorMask;
        texd.a = _a;
        texd.b = _b;
        texd.c = _c;
        texd.x0 = _d;
    }

    public static void glStencilMask(TextureDraw texd, int _a) {
        texd.type = TextureDraw.Type.glStencilMask;
        texd.a = _a;
    }

    public static void glBlendFunc(TextureDraw texd, int _a, int _b) {
        texd.type = TextureDraw.Type.glBlendFunc;
        texd.a = _a;
        texd.b = _b;
    }

    public static void glBlendFuncSeparate(TextureDraw texd, int _a, int _b, int _c, int _d) {
        texd.type = TextureDraw.Type.glBlendFuncSeparate;
        texd.a = _a;
        texd.b = _b;
        texd.c = _c;
        texd.d = _d;
    }

    public static void glBlendEquation(TextureDraw texd, int _a) {
        texd.type = TextureDraw.Type.glBlendEquation;
        texd.a = _a;
    }

    public static void glDoEndFrame(TextureDraw texd) {
        texd.type = TextureDraw.Type.glDoEndFrame;
    }

    public static void glDoEndFrameFx(TextureDraw texd, int player) {
        texd.type = TextureDraw.Type.glDoEndFrameFx;
        texd.c = player;
    }

    public static void glIgnoreStyles(TextureDraw texd, boolean _b) {
        texd.type = TextureDraw.Type.glIgnoreStyles;
        texd.a = _b ? 1 : 0;
    }

    public static void glDoStartFrame(TextureDraw texd, int w, int h, float zoom, int player) {
        glDoStartFrame(texd, w, h, zoom, player, false);
    }

    public static void glDoStartFrame(TextureDraw texd, int w, int h, float zoom, int player, boolean isTextFrame) {
        if (isTextFrame) {
            texd.type = TextureDraw.Type.glDoStartFrameText;
        } else {
            texd.type = TextureDraw.Type.glDoStartFrame;
        }

        texd.a = w;
        texd.b = h;
        texd.f1 = zoom;
        texd.c = player;
    }

    public static void glDoStartFrameFx(TextureDraw texd, int w, int h, int player) {
        texd.type = TextureDraw.Type.glDoStartFrameFx;
        texd.a = w;
        texd.b = h;
        texd.c = player;
    }

    public static void glTexParameteri(TextureDraw texd, int _a, int _b, int _c) {
        texd.type = TextureDraw.Type.glTexParameteri;
        texd.a = _a;
        texd.b = _b;
        texd.c = _c;
    }

    public static void drawModel(TextureDraw texd, ModelManager.ModelSlot model) {
        texd.type = TextureDraw.Type.DrawModel;
        texd.a = model.ID;
        texd.drawer = ModelSlotRenderData.alloc().init(model);
    }

    public static void drawSkyBox(TextureDraw texd, Shader shader, int userId, int apiId, int bufferId) {
        texd.type = TextureDraw.Type.DrawSkyBox;
        texd.a = shader.getID();
        texd.b = userId;
        texd.c = apiId;
        texd.d = bufferId;
        texd.drawer = null;
    }

    public static void drawWater(TextureDraw texd, Shader shader, int userId, int apiId, boolean bShore) {
        texd.type = TextureDraw.Type.DrawWater;
        texd.a = shader.getID();
        texd.b = userId;
        texd.c = apiId;
        texd.d = bShore ? 1 : 0;
        texd.drawer = null;
    }

    public static void drawPuddles(TextureDraw texd, Shader shader, int userId, int apiId, int z) {
        texd.type = TextureDraw.Type.DrawPuddles;
        texd.a = shader.getID();
        texd.b = userId;
        texd.c = apiId;
        texd.d = z;
        texd.drawer = null;
    }

    public static void drawParticles(TextureDraw texd, int userId, int var1, int var2) {
        texd.type = TextureDraw.Type.DrawParticles;
        texd.b = userId;
        texd.c = var1;
        texd.d = var2;
        texd.drawer = null;
    }

    public static void StartShader(TextureDraw texd, int iD) {
        texd.type = TextureDraw.Type.StartShader;
        texd.a = iD;
    }

    public static void ShaderUpdate1i(TextureDraw texd, int shaderID, int uniform, int uniformValue) {
        texd.type = TextureDraw.Type.ShaderUpdate;
        texd.a = shaderID;
        texd.b = uniform;
        texd.c = -1;
        texd.d = uniformValue;
    }

    public static void ShaderUpdate1f(TextureDraw texd, int shaderID, int uniform, float uniformValue) {
        texd.type = TextureDraw.Type.ShaderUpdate;
        texd.a = shaderID;
        texd.b = uniform;
        texd.c = 1;
        texd.u0 = uniformValue;
    }

    public static void ShaderUpdate2f(TextureDraw texd, int shaderID, int uniform, float value1, float value2) {
        texd.type = TextureDraw.Type.ShaderUpdate;
        texd.a = shaderID;
        texd.b = uniform;
        texd.c = 2;
        texd.u0 = value1;
        texd.u1 = value2;
    }

    public static void ShaderUpdate3f(TextureDraw texd, int shaderID, int uniform, float value1, float value2, float value3) {
        texd.type = TextureDraw.Type.ShaderUpdate;
        texd.a = shaderID;
        texd.b = uniform;
        texd.c = 3;
        texd.u0 = value1;
        texd.u1 = value2;
        texd.u2 = value3;
    }

    public static void ShaderUpdate4f(TextureDraw texd, int shaderID, int uniform, float value1, float value2, float value3, float value4) {
        texd.type = TextureDraw.Type.ShaderUpdate;
        texd.a = shaderID;
        texd.b = uniform;
        texd.c = 4;
        texd.u0 = value1;
        texd.u1 = value2;
        texd.u2 = value3;
        texd.u3 = value4;
    }

    public void run() {
        switch (this.type) {
            case StartShader:
                ARBShaderObjects.glUseProgramObjectARB(this.a);
                if (Shader.ShaderMap.containsKey(this.a)) {
                    Shader.ShaderMap.get(this.a).startRenderThread(this);
                }

                if (this.a == 0) {
                    SpriteRenderer.ringBuffer.checkShaderChangedTexture1();
                }
                break;
            case ShaderUpdate:
                if (this.c == 1) {
                    ARBShaderObjects.glUniform1fARB(this.b, this.u0);
                }

                if (this.c == 2) {
                    ARBShaderObjects.glUniform2fARB(this.b, this.u0, this.u1);
                }

                if (this.c == 3) {
                    ARBShaderObjects.glUniform3fARB(this.b, this.u0, this.u1, this.u2);
                }

                if (this.c == 4) {
                    ARBShaderObjects.glUniform4fARB(this.b, this.u0, this.u1, this.u2, this.u3);
                }

                if (this.c == -1) {
                    ARBShaderObjects.glUniform1iARB(this.b, this.d);
                }
                break;
            case BindActiveTexture:
                GL13.glActiveTexture(this.a);
                if (this.b != -1) {
                    GL11.glBindTexture(3553, this.b);
                }

                GL13.glActiveTexture(33984);
                break;
            case DrawModel:
                if (this.drawer != null) {
                    this.drawer.render();
                }
                break;
            case DrawSkyBox:
                try {
                    ModelManager.instance.RenderSkyBox(this, this.a, this.b, this.c, this.d);
                } catch (Exception exception3) {
                    exception3.printStackTrace();
                }
                break;
            case DrawWater:
                try {
                    ModelManager.instance.RenderWater(this, this.a, this.b, this.d == 1);
                } catch (Exception exception2) {
                    exception2.printStackTrace();
                }
                break;
            case DrawPuddles:
                try {
                    ModelManager.instance.RenderPuddles(this.a, this.b, this.d);
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
                break;
            case DrawParticles:
                try {
                    ModelManager.instance.RenderParticles(this, this.b, this.c);
                } catch (Exception exception0) {
                    exception0.printStackTrace();
                }
                break;
            case glClear:
                IndieGL.glClearA(this.a);
                break;
            case glClearColor:
                GL11.glClearColor(this.col0 / 255.0F, this.col1 / 255.0F, this.col2 / 255.0F, this.col3 / 255.0F);
                break;
            case glBlendFunc:
                IndieGL.glBlendFuncA(this.a, this.b);
                break;
            case glBlendFuncSeparate:
                GL14.glBlendFuncSeparate(this.a, this.b, this.c, this.d);
                break;
            case glColorMask:
                IndieGL.glColorMaskA(this.a == 1, this.b == 1, this.c == 1, this.x0 == 1.0F);
                break;
            case glTexParameteri:
                IndieGL.glTexParameteriActual(this.a, this.b, this.c);
                break;
            case glStencilMask:
                IndieGL.glStencilMaskA(this.a);
                break;
            case glDoEndFrame:
                Core.getInstance().DoEndFrameStuff(this.a, this.b);
                break;
            case glDoEndFrameFx:
                Core.getInstance().DoEndFrameStuffFx(this.a, this.b, this.c);
                break;
            case glDoStartFrame:
                Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c);
                break;
            case glDoStartFrameText:
                Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c, true);
                break;
            case glDoStartFrameFx:
                Core.getInstance().DoStartFrameStuffSmartTextureFx(this.a, this.b, this.c);
                break;
            case glStencilFunc:
                IndieGL.glStencilFuncA(this.a, this.b, this.c);
                break;
            case glBuffer:
                if (Core.getInstance().supportsFBO()) {
                    if (this.a == 1) {
                        SpriteRenderer.instance.getRenderingState().fbo.startDrawing(false, false);
                    } else if (this.a == 2) {
                        UIManager.UIFBO.startDrawing(true, true);
                    } else if (this.a == 3) {
                        UIManager.UIFBO.endDrawing();
                    } else if (this.a == 4) {
                        WeatherFxMask.getFboMask().startDrawing(true, true);
                    } else if (this.a == 5) {
                        WeatherFxMask.getFboMask().endDrawing();
                    } else if (this.a == 6) {
                        WeatherFxMask.getFboParticles().startDrawing(true, true);
                    } else if (this.a == 7) {
                        WeatherFxMask.getFboParticles().endDrawing();
                    } else {
                        SpriteRenderer.instance.getRenderingState().fbo.endDrawing();
                    }
                }
                break;
            case glStencilOp:
                IndieGL.glStencilOpA(this.a, this.b, this.c);
                break;
            case glLoadIdentity:
                GL11.glLoadIdentity();
                break;
            case glBind:
                GL11.glBindTexture(3553, this.a);
                Texture.lastlastTextureID = Texture.lastTextureID;
                Texture.lastTextureID = this.a;
                break;
            case glViewport:
                GL11.glViewport(this.a, this.b, this.c, this.d);
                break;
            case drawTerrain:
                IsoWorld.instance.renderTerrain();
                break;
            case doCoreIntParam:
                Core.getInstance().FloatParamMap.put(this.a, this.f1);
                break;
            case glDepthMask:
                GL11.glDepthMask(this.a == 1);
            case glGenerateMipMaps:
            default:
                break;
            case glAlphaFunc:
                IndieGL.glAlphaFuncA(this.a, this.f1);
                break;
            case glEnable:
                IndieGL.glEnableA(this.a);
                break;
            case glDisable:
                IndieGL.glDisableA(this.a);
                break;
            case glBlendEquation:
                GL14.glBlendEquation(this.a);
                break;
            case glIgnoreStyles:
                SpriteRenderer.RingBuffer.IGNORE_STYLES = this.a == 1;
        }
    }

    public static void glDepthMask(TextureDraw textureDraw, boolean _b) {
        textureDraw.type = TextureDraw.Type.glDepthMask;
        textureDraw.a = _b ? 1 : 0;
    }

    public static void doCoreIntParam(TextureDraw textureDraw, int id, float val) {
        textureDraw.type = TextureDraw.Type.doCoreIntParam;
        textureDraw.a = id;
        textureDraw.f1 = val;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
            + "{ "
            + this.type
            + ", a:"
            + this.a
            + ", b:"
            + this.b
            + ", f1:"
            + this.f1
            + ", vars:"
            + (this.vars != null ? PZArrayUtil.arrayToString(this.vars, "{", "}", ", ") : "null")
            + ", c:"
            + this.c
            + ", d:"
            + this.d
            + ", col0:"
            + this.col0
            + ", col1:"
            + this.col1
            + ", col2:"
            + this.col2
            + ", col3:"
            + this.col3
            + ", x0:"
            + this.x0
            + ", x1:"
            + this.x1
            + ", x2:"
            + this.x2
            + ", x3:"
            + this.x3
            + ", x0:"
            + this.x0
            + ", x1:"
            + this.x1
            + ", x2:"
            + this.x2
            + ", x3:"
            + this.x3
            + ", y0:"
            + this.y0
            + ", y1:"
            + this.y1
            + ", y2:"
            + this.y2
            + ", y3:"
            + this.y3
            + ", u0:"
            + this.u0
            + ", u1:"
            + this.u1
            + ", u2:"
            + this.u2
            + ", u3:"
            + this.u3
            + ", v0:"
            + this.v0
            + ", v1:"
            + this.v1
            + ", v2:"
            + this.v2
            + ", v3:"
            + this.v3
            + ", tex:"
            + this.tex
            + ", tex1:"
            + this.tex1
            + ", useAttribArray:"
            + this.useAttribArray
            + ", tex1_u0:"
            + this.tex1_u0
            + ", tex1_u1:"
            + this.tex1_u1
            + ", tex1_u2:"
            + this.tex1_u2
            + ", tex1_u3:"
            + this.tex1_u3
            + ", tex1_u0:"
            + this.tex1_u0
            + ", tex1_u1:"
            + this.tex1_u1
            + ", tex1_u2:"
            + this.tex1_u2
            + ", tex1_u3:"
            + this.tex1_u3
            + ", tex1_col0:"
            + this.tex1_col0
            + ", tex1_col1:"
            + this.tex1_col1
            + ", tex1_col2:"
            + this.tex1_col2
            + ", tex1_col3:"
            + this.tex1_col3
            + ", bSingleCol:"
            + this.bSingleCol
            + " }";
    }

    public static TextureDraw Create(
        TextureDraw texd, Texture _tex, float x, float y, float width, float height, float r, float g, float _b, float _a, Consumer<TextureDraw> texdModifier
    ) {
        int int0 = Color.colorToABGR(r, g, _b, _a);
        Create(texd, _tex, x, y, x + width, y, x + width, y + height, x, y + height, int0, int0, int0, int0, texdModifier);
        return texd;
    }

    public static TextureDraw Create(
        TextureDraw texd,
        Texture _tex,
        SpriteRenderer.WallShaderTexRender wallSection,
        float x,
        float y,
        float width,
        float height,
        float r,
        float g,
        float _b,
        float _a,
        Consumer<TextureDraw> texdModifier
    ) {
        int int0 = Color.colorToABGR(r, g, _b, _a);
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 1.0F;
        float float3 = 0.0F;
        float float4 = 1.0F;
        float float5 = 1.0F;
        float float6 = 0.0F;
        float float7 = 1.0F;
        float float8;
        float float9;
        float float10;
        float float11;
        float float12;
        float float13;
        float float14;
        float float15;
        switch (wallSection) {
            case LeftOnly:
                float14 = x;
                float8 = x;
                float11 = y;
                float9 = y;
                float10 = float12 = x + width / 2.0F;
                float13 = float15 = y + height;
                if (_tex != null) {
                    float float25 = _tex.getXEnd();
                    float float26 = _tex.getXStart();
                    float float27 = _tex.getYEnd();
                    float float28 = _tex.getYStart();
                    float float29 = 0.5F * (float25 - float26);
                    float0 = float26;
                    float2 = float26 + float29;
                    float4 = float26 + float29;
                    float6 = float26;
                    float1 = float28;
                    float3 = float28;
                    float5 = float27;
                    float7 = float27;
                }
                break;
            case RightOnly:
                float8 = float14 = x + width / 2.0F;
                float11 = y;
                float9 = y;
                float10 = float12 = x + width;
                float13 = float15 = y + height;
                if (_tex != null) {
                    float float20 = _tex.getXEnd();
                    float float21 = _tex.getXStart();
                    float float22 = _tex.getYEnd();
                    float float23 = _tex.getYStart();
                    float float24 = 0.5F * (float20 - float21);
                    float0 = float21 + float24;
                    float2 = float20;
                    float4 = float20;
                    float6 = float21 + float24;
                    float1 = float23;
                    float3 = float23;
                    float5 = float22;
                    float7 = float22;
                }
                break;
            case All:
            default:
                float14 = x;
                float8 = x;
                float11 = y;
                float9 = y;
                float10 = float12 = x + width;
                float13 = float15 = y + height;
                if (_tex != null) {
                    float float16 = _tex.getXEnd();
                    float float17 = _tex.getXStart();
                    float float18 = _tex.getYEnd();
                    float float19 = _tex.getYStart();
                    float0 = float17;
                    float2 = float16;
                    float4 = float16;
                    float6 = float17;
                    float1 = float19;
                    float3 = float19;
                    float5 = float18;
                    float7 = float18;
                }
        }

        Create(
            texd,
            _tex,
            float8,
            float9,
            float10,
            float11,
            float12,
            float13,
            float14,
            float15,
            int0,
            int0,
            int0,
            int0,
            float0,
            float1,
            float2,
            float3,
            float4,
            float5,
            float6,
            float7,
            texdModifier
        );
        return texd;
    }

    public static TextureDraw Create(
        TextureDraw texd,
        Texture _tex,
        float x,
        float y,
        float width,
        float height,
        float r,
        float g,
        float _b,
        float _a,
        float _u1,
        float _v1,
        float _u2,
        float _v2,
        float _u3,
        float _v3,
        float u4,
        float v4,
        Consumer<TextureDraw> texdModifier
    ) {
        int int0 = Color.colorToABGR(r, g, _b, _a);
        Create(texd, _tex, x, y, x + width, y, x + width, y + height, x, y + height, int0, int0, int0, int0, _u1, _v1, _u2, _v2, _u3, _v3, u4, v4, texdModifier);
        return texd;
    }

    public static void Create(
        TextureDraw texd,
        Texture _tex,
        float _x1,
        float _y1,
        float _x2,
        float _y2,
        float _x3,
        float _y3,
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
        int int0 = Color.colorToABGR(r1, g1, b1, a1);
        int int1 = Color.colorToABGR(r2, g2, b2, a2);
        int int2 = Color.colorToABGR(r3, g3, b3, a3);
        int int3 = Color.colorToABGR(r4, g4, b4, a4);
        Create(texd, _tex, _x1, _y1, _x2, _y2, _x3, _y3, x4, y4, int0, int1, int2, int3, texdModifier);
    }

    public static void Create(
        TextureDraw texd,
        Texture _tex,
        float _x1,
        float _y1,
        float _x2,
        float _y2,
        float _x3,
        float _y3,
        float x4,
        float y4,
        float r1,
        float g1,
        float b1,
        float a1
    ) {
        int int0 = Color.colorToABGR(r1, g1, b1, a1);
        Create(texd, _tex, _x1, _y1, _x2, _y2, _x3, _y3, x4, y4, int0, int0, int0, int0, null);
    }

    public static void Create(
        TextureDraw texd, Texture _tex, float _x1, float _y1, float _x2, float _y2, float _x3, float _y3, float x4, float y4, int c1, int c2, int c3, int c4
    ) {
        Create(texd, _tex, _x1, _y1, _x2, _y2, _x3, _y3, x4, y4, c1, c2, c3, c4, null);
    }

    public static TextureDraw Create(
        TextureDraw texd,
        Texture _tex,
        float _x1,
        float _y1,
        float _x2,
        float _y2,
        float _x3,
        float _y3,
        float x4,
        float y4,
        int c1,
        int c2,
        int c3,
        int c4,
        Consumer<TextureDraw> texdModifier
    ) {
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 1.0F;
        float float3 = 0.0F;
        float float4 = 1.0F;
        float float5 = 1.0F;
        float float6 = 0.0F;
        float float7 = 1.0F;
        if (_tex != null) {
            float float8 = _tex.getXEnd();
            float float9 = _tex.getXStart();
            float float10 = _tex.getYEnd();
            float float11 = _tex.getYStart();
            float0 = float9;
            float1 = float11;
            float2 = float8;
            float3 = float11;
            float4 = float8;
            float5 = float10;
            float6 = float9;
            float7 = float10;
        }

        return Create(
            texd, _tex, _x1, _y1, _x2, _y2, _x3, _y3, x4, y4, c1, c2, c3, c4, float0, float1, float2, float3, float4, float5, float6, float7, texdModifier
        );
    }

    public static TextureDraw Create(
        TextureDraw texd,
        Texture _tex,
        float _x0,
        float _y0,
        float _x1,
        float _y1,
        float _x2,
        float _y2,
        float _x3,
        float _y3,
        int c0,
        int c1,
        int c2,
        int c3,
        float _u0,
        float _v0,
        float _u1,
        float _v1,
        float _u2,
        float _v2,
        float _u3,
        float _v3,
        Consumer<TextureDraw> texdModifier
    ) {
        texd.bSingleCol = c0 == c1 && c0 == c2 && c0 == c3;
        texd.tex = _tex;
        texd.x0 = _x0;
        texd.y0 = _y0;
        texd.x1 = _x1;
        texd.y1 = _y1;
        texd.x2 = _x2;
        texd.y2 = _y2;
        texd.x3 = _x3;
        texd.y3 = _y3;
        texd.col0 = c0;
        texd.col1 = c1;
        texd.col2 = c2;
        texd.col3 = c3;
        texd.u0 = _u0;
        texd.u1 = _u1;
        texd.u2 = _u2;
        texd.u3 = _u3;
        texd.v0 = _v0;
        texd.v1 = _v1;
        texd.v2 = _v2;
        texd.v3 = _v3;
        if (_tex != null) {
            texd.flipped = _tex.flip;
        }

        if (texdModifier != null) {
            texdModifier.accept(texd);
            texd.bSingleCol = texd.col0 == texd.col1 && texd.col0 == texd.col2 && texd.col0 == texd.col3;
        }

        return texd;
    }

    public int getColor(int i) {
        if (this.bSingleCol) {
            return this.col0;
        } else if (i == 0) {
            return this.col0;
        } else if (i == 1) {
            return this.col1;
        } else if (i == 2) {
            return this.col2;
        } else {
            return i == 3 ? this.col3 : this.col0;
        }
    }

    public void reset() {
        this.type = TextureDraw.Type.glDraw;
        this.flipped = false;
        this.tex = null;
        this.tex1 = null;
        this.useAttribArray = -1;
        this.col0 = -1;
        this.col1 = -1;
        this.col2 = -1;
        this.col3 = -1;
        this.bSingleCol = true;
        this.x0 = this.x1 = this.x2 = this.x3 = this.y0 = this.y1 = this.y2 = this.y3 = -1.0F;
        this.drawer = null;
    }

    public static void glLoadIdentity(TextureDraw textureDraw) {
        textureDraw.type = TextureDraw.Type.glLoadIdentity;
    }

    public static void glGenerateMipMaps(TextureDraw textureDraw, int _a) {
        textureDraw.type = TextureDraw.Type.glGenerateMipMaps;
        textureDraw.a = _a;
    }

    public static void glBind(TextureDraw textureDraw, int _a) {
        textureDraw.type = TextureDraw.Type.glBind;
        textureDraw.a = _a;
    }

    public static void glViewport(TextureDraw textureDraw, int x, int y, int width, int height) {
        textureDraw.type = TextureDraw.Type.glViewport;
        textureDraw.a = x;
        textureDraw.b = y;
        textureDraw.c = width;
        textureDraw.d = height;
    }

    public void postRender() {
        if (this.type == TextureDraw.Type.StartShader) {
            Shader shader = Shader.ShaderMap.get(this.a);
            if (shader != null) {
                shader.postRender(this);
            }
        }

        if (this.drawer != null) {
            this.drawer.postRender();
            this.drawer = null;
        }
    }

    public abstract static class GenericDrawer {
        public abstract void render();

        public void postRender() {
        }
    }

    public static enum Type {
        glDraw,
        glBuffer,
        glStencilFunc,
        glAlphaFunc,
        glStencilOp,
        glEnable,
        glDisable,
        glColorMask,
        glStencilMask,
        glClear,
        glBlendFunc,
        glDoStartFrame,
        glDoStartFrameText,
        glDoEndFrame,
        glTexParameteri,
        StartShader,
        glLoadIdentity,
        glGenerateMipMaps,
        glBind,
        glViewport,
        DrawModel,
        DrawSkyBox,
        DrawWater,
        DrawPuddles,
        DrawParticles,
        ShaderUpdate,
        BindActiveTexture,
        glBlendEquation,
        glDoStartFrameFx,
        glDoEndFrameFx,
        glIgnoreStyles,
        glClearColor,
        glBlendFuncSeparate,
        glDepthMask,
        doCoreIntParam,
        drawTerrain;
    }
}
