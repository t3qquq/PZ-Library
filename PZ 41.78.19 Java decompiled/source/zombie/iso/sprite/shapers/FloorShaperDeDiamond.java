// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;

public class FloorShaperDeDiamond extends FloorShaper {
    public static final FloorShaperDeDiamond instance = new FloorShaperDeDiamond();

    @Override
    public void accept(TextureDraw textureDraw) {
        int int0 = this.colTint;
        this.colTint = 0;
        super.accept(textureDraw);
        this.applyDeDiamondPadding(textureDraw);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
            int int1 = this.col[0];
            int int2 = this.col[1];
            int int3 = this.col[2];
            int int4 = this.col[3];
            int int5 = Color.lerpABGR(int1, int4, 0.5F);
            int int6 = Color.lerpABGR(int2, int1, 0.5F);
            int int7 = Color.lerpABGR(int3, int2, 0.5F);
            int int8 = Color.lerpABGR(int4, int3, 0.5F);
            textureDraw.col0 = Color.blendBGR(textureDraw.col0, int5);
            textureDraw.col1 = Color.blendBGR(textureDraw.col1, int6);
            textureDraw.col2 = Color.blendBGR(textureDraw.col2, int7);
            textureDraw.col3 = Color.blendBGR(textureDraw.col3, int8);
            if (int0 != 0) {
                textureDraw.col0 = Color.tintABGR(textureDraw.col0, int0);
                textureDraw.col1 = Color.tintABGR(textureDraw.col1, int0);
                textureDraw.col2 = Color.tintABGR(textureDraw.col2, int0);
                textureDraw.col3 = Color.tintABGR(textureDraw.col3, int0);
            }
        }
    }

    private void applyDeDiamondPadding(TextureDraw textureDraw) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingDeDiamond.getValue()) {
            FloorShaperDeDiamond.Settings settings = this.getSettings();
            FloorShaperDeDiamond.Settings.BorderSetting borderSetting = settings.getCurrentZoomSetting();
            float float0 = borderSetting.borderThicknessUp;
            float float1 = borderSetting.borderThicknessDown;
            float float2 = borderSetting.borderThicknessLR;
            float float3 = borderSetting.uvFraction;
            float float4 = textureDraw.x1 - textureDraw.x0;
            float float5 = textureDraw.y2 - textureDraw.y1;
            float float6 = textureDraw.u1 - textureDraw.u0;
            float float7 = textureDraw.v2 - textureDraw.v1;
            float float8 = float6 * float2 / float4;
            float float9 = float7 * float0 / float5;
            float float10 = float7 * float1 / float5;
            float float11 = float3 * float8;
            float float12 = float3 * float9;
            float float13 = float3 * float10;
            SpritePadding.applyPadding(textureDraw, float2, float0, float2, float1, float11, float12, float11, float13);
        }
    }

    private FloorShaperDeDiamond.Settings getSettings() {
        return SpritePaddingSettings.getSettings().FloorDeDiamond;
    }

    @XmlType(
        name = "FloorShaperDeDiamondSettings"
    )
    public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
        public FloorShaperDeDiamond.Settings.BorderSetting ZoomedIn = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 1.0F, 2.0F, 0.01F);
        public FloorShaperDeDiamond.Settings.BorderSetting NotZoomed = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 1.0F, 2.0F, 0.01F);
        public FloorShaperDeDiamond.Settings.BorderSetting ZoomedOut = new FloorShaperDeDiamond.Settings.BorderSetting(2.0F, 0.0F, 2.5F, 0.0F);

        public FloorShaperDeDiamond.Settings.BorderSetting getCurrentZoomSetting() {
            return getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }

        public static class BorderSetting {
            public float borderThicknessUp = 3.0F;
            public float borderThicknessDown = 3.0F;
            public float borderThicknessLR = 0.0F;
            public float uvFraction = 0.01F;

            public BorderSetting() {
            }

            public BorderSetting(float float0, float float1, float float2, float float3) {
                this.borderThicknessUp = float0;
                this.borderThicknessDown = float1;
                this.borderThicknessLR = float2;
                this.uvFraction = float3;
            }
        }
    }
}
