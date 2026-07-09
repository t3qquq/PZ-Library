// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;

public class SpritePadding {
    public static void applyPadding(
        TextureDraw textureDraw, float float16, float float17, float float20, float float22, float float18, float float19, float float21, float float23
    ) {
        float float0 = textureDraw.x0;
        float float1 = textureDraw.y0;
        float float2 = textureDraw.x1;
        float float3 = textureDraw.y1;
        float float4 = textureDraw.x2;
        float float5 = textureDraw.y2;
        float float6 = textureDraw.x3;
        float float7 = textureDraw.y3;
        float float8 = textureDraw.u0;
        float float9 = textureDraw.v0;
        float float10 = textureDraw.u1;
        float float11 = textureDraw.v1;
        float float12 = textureDraw.u2;
        float float13 = textureDraw.v2;
        float float14 = textureDraw.u3;
        float float15 = textureDraw.v3;
        textureDraw.x0 = float0 - float16;
        textureDraw.y0 = float1 - float17;
        textureDraw.u0 = float8 - float18;
        textureDraw.v0 = float9 - float19;
        textureDraw.x1 = float2 + float20;
        textureDraw.y1 = float3 - float17;
        textureDraw.u1 = float10 + float21;
        textureDraw.v1 = float11 - float19;
        textureDraw.x2 = float4 + float20;
        textureDraw.y2 = float5 + float22;
        textureDraw.u2 = float12 + float21;
        textureDraw.v2 = float13 + float23;
        textureDraw.x3 = float6 - float16;
        textureDraw.y3 = float7 + float22;
        textureDraw.u3 = float14 - float18;
        textureDraw.v3 = float15 + float23;
    }

    public static void applyPaddingBorder(TextureDraw textureDraw, float float5, float float8) {
        float float0 = textureDraw.x1 - textureDraw.x0;
        float float1 = textureDraw.y2 - textureDraw.y1;
        float float2 = textureDraw.u1 - textureDraw.u0;
        float float3 = textureDraw.v2 - textureDraw.v1;
        float float4 = float2 * float5 / float0;
        float float6 = float3 * float5 / float1;
        float float7 = float8 * float4;
        float float9 = float8 * float6;
        applyPadding(textureDraw, float5, float5, float5, float5, float7, float9, float7, float9);
    }

    public static void applyIsoPadding(TextureDraw textureDraw, SpritePadding.IsoPaddingSettings paddingSettings) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPadding.getValue()) {
            SpritePadding.IsoPaddingSettings.IsoBorderSetting borderSetting = paddingSettings.getCurrentZoomSetting();
            float float0 = borderSetting.borderThickness;
            float float1 = borderSetting.uvFraction;
            applyPaddingBorder(textureDraw, float0, float1);
        }
    }

    public static class IsoPaddingSettings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
        public SpritePadding.IsoPaddingSettings.IsoBorderSetting ZoomedIn = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(1.0F, 0.99F);
        public SpritePadding.IsoPaddingSettings.IsoBorderSetting NotZoomed = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(1.0F, 0.99F);
        public SpritePadding.IsoPaddingSettings.IsoBorderSetting ZoomedOut = new SpritePadding.IsoPaddingSettings.IsoBorderSetting(2.0F, 0.01F);

        public SpritePadding.IsoPaddingSettings.IsoBorderSetting getCurrentZoomSetting() {
            return getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }

        public static class IsoBorderSetting {
            public float borderThickness;
            public float uvFraction;

            public IsoBorderSetting() {
            }

            public IsoBorderSetting(float float0, float float1) {
                this.borderThickness = float0;
                this.uvFraction = float1;
            }
        }
    }
}
