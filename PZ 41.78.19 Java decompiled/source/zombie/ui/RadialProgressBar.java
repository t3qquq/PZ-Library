// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;

/**
 * TurboTuTone.
 */
public final class RadialProgressBar extends UIElement {
    private static final boolean DEBUG = false;
    Texture radialTexture;
    float deltaValue = 1.0F;
    private static final RadialProgressBar.RadSegment[] segments = new RadialProgressBar.RadSegment[8];
    private final float PIx2 = 6.283185F;
    private final float PiOver2 = 1.570796F;

    public RadialProgressBar(KahluaTable table, Texture tex) {
        super(table);
        this.radialTexture = tex;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void render() {
        if (this.enabled) {
            if (this.isVisible()) {
                if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !(this.Parent.maxDrawHeight <= this.y)) {
                    if (this.radialTexture != null) {
                        float float0 = (float)(this.x + this.xScroll + this.getAbsoluteX() + this.radialTexture.offsetX);
                        float float1 = (float)(this.y + this.yScroll + this.getAbsoluteY() + this.radialTexture.offsetY);
                        float float2 = this.radialTexture.xStart;
                        float float3 = this.radialTexture.yStart;
                        float float4 = this.radialTexture.xEnd - this.radialTexture.xStart;
                        float float5 = this.radialTexture.yEnd - this.radialTexture.yStart;
                        float float6 = float0 + 0.5F * this.width;
                        float float7 = float1 + 0.5F * this.height;
                        float float8 = this.deltaValue;
                        float float9 = float8 * 6.283185F - 1.570796F;
                        Vector2 vector = new Vector2((float)Math.cos(float9), (float)Math.sin(float9));
                        float float10;
                        float float11;
                        if (Math.abs(this.width / 2.0F / vector.x) < Math.abs(this.height / 2.0F / vector.y)) {
                            float10 = Math.abs(this.width / 2.0F / vector.x);
                            float11 = Math.abs(0.5F / vector.x);
                        } else {
                            float10 = Math.abs(this.height / 2.0F / vector.y);
                            float11 = Math.abs(0.5F / vector.y);
                        }

                        float float12 = float6 + vector.x * float10;
                        float float13 = float7 + vector.y * float10;
                        float float14 = 0.5F + vector.x * float11;
                        float float15 = 0.5F + vector.y * float11;
                        int int0 = (int)(float8 * 8.0F);
                        if (float8 <= 0.0F) {
                            int0 = -1;
                        }

                        for (int int1 = 0; int1 < segments.length; int1++) {
                            RadialProgressBar.RadSegment radSegment = segments[int1];
                            if (radSegment != null && int1 <= int0) {
                                if (int1 != int0) {
                                    SpriteRenderer.instance
                                        .renderPoly(
                                            this.radialTexture,
                                            float0 + radSegment.vertex[0].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[0].y * this.radialTexture.getHeight(),
                                            float0 + radSegment.vertex[1].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[1].y * this.radialTexture.getHeight(),
                                            float0 + radSegment.vertex[2].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[2].y * this.radialTexture.getHeight(),
                                            float0 + radSegment.vertex[2].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[2].y * this.radialTexture.getHeight(),
                                            1.0F,
                                            1.0F,
                                            1.0F,
                                            1.0F,
                                            float2 + radSegment.uv[0].x * float4,
                                            float3 + radSegment.uv[0].y * float5,
                                            float2 + radSegment.uv[1].x * float4,
                                            float3 + radSegment.uv[1].y * float5,
                                            float2 + radSegment.uv[2].x * float4,
                                            float3 + radSegment.uv[2].y * float5,
                                            float2 + radSegment.uv[2].x * float4,
                                            float3 + radSegment.uv[2].y * float5
                                        );
                                } else {
                                    SpriteRenderer.instance
                                        .renderPoly(
                                            this.radialTexture,
                                            float0 + radSegment.vertex[0].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[0].y * this.radialTexture.getHeight(),
                                            float12,
                                            float13,
                                            float0 + radSegment.vertex[2].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[2].y * this.radialTexture.getHeight(),
                                            float0 + radSegment.vertex[2].x * this.radialTexture.getWidth(),
                                            float1 + radSegment.vertex[2].y * this.radialTexture.getHeight(),
                                            1.0F,
                                            1.0F,
                                            1.0F,
                                            1.0F,
                                            float2 + radSegment.uv[0].x * float4,
                                            float3 + radSegment.uv[0].y * float5,
                                            float2 + float14 * float4,
                                            float3 + float15 * float5,
                                            float2 + radSegment.uv[2].x * float4,
                                            float3 + radSegment.uv[2].y * float5,
                                            float2 + radSegment.uv[2].x * float4,
                                            float3 + radSegment.uv[2].y * float5
                                        );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setValue(float delta) {
        this.deltaValue = PZMath.clamp(delta, 0.0F, 1.0F);
    }

    public float getValue() {
        return this.deltaValue;
    }

    public void setTexture(Texture texture) {
        this.radialTexture = texture;
    }

    public Texture getTexture() {
        return this.radialTexture;
    }

    private void printTexture(Texture texture) {
        DebugLog.log("xStart = " + texture.xStart);
        DebugLog.log("yStart = " + texture.yStart);
        DebugLog.log("offX = " + texture.offsetX);
        DebugLog.log("offY = " + texture.offsetY);
        DebugLog.log("xEnd = " + texture.xEnd);
        DebugLog.log("yEnd = " + texture.yEnd);
        DebugLog.log("Width = " + texture.getWidth());
        DebugLog.log("Height = " + texture.getHeight());
        DebugLog.log("RealWidth = " + texture.getRealWidth());
        DebugLog.log("RealHeight = " + texture.getRealHeight());
        DebugLog.log("OrigWidth = " + texture.getWidthOrig());
        DebugLog.log("OrigHeight = " + texture.getHeightOrig());
    }

    static {
        segments[0] = new RadialProgressBar.RadSegment();
        segments[0].set(0.5F, 0.0F, 1.0F, 0.0F, 0.5F, 0.5F);
        segments[1] = new RadialProgressBar.RadSegment();
        segments[1].set(1.0F, 0.0F, 1.0F, 0.5F, 0.5F, 0.5F);
        segments[2] = new RadialProgressBar.RadSegment();
        segments[2].set(1.0F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F);
        segments[3] = new RadialProgressBar.RadSegment();
        segments[3].set(1.0F, 1.0F, 0.5F, 1.0F, 0.5F, 0.5F);
        segments[4] = new RadialProgressBar.RadSegment();
        segments[4].set(0.5F, 1.0F, 0.0F, 1.0F, 0.5F, 0.5F);
        segments[5] = new RadialProgressBar.RadSegment();
        segments[5].set(0.0F, 1.0F, 0.0F, 0.5F, 0.5F, 0.5F);
        segments[6] = new RadialProgressBar.RadSegment();
        segments[6].set(0.0F, 0.5F, 0.0F, 0.0F, 0.5F, 0.5F);
        segments[7] = new RadialProgressBar.RadSegment();
        segments[7].set(0.0F, 0.0F, 0.5F, 0.0F, 0.5F, 0.5F);
    }

    private static class RadSegment {
        Vector2[] vertex = new Vector2[3];
        Vector2[] uv = new Vector2[3];

        private RadialProgressBar.RadSegment set(int int0, float float0, float float1, float float2, float float3) {
            this.vertex[int0] = new Vector2(float0, float1);
            this.uv[int0] = new Vector2(float2, float3);
            return this;
        }

        private void set(float float0, float float1, float float2, float float3, float float4, float float5) {
            this.vertex[0] = new Vector2(float0, float1);
            this.vertex[1] = new Vector2(float2, float3);
            this.vertex[2] = new Vector2(float4, float5);
            this.uv[0] = new Vector2(float0, float1);
            this.uv[1] = new Vector2(float2, float3);
            this.uv[2] = new Vector2(float4, float5);
        }
    }
}
