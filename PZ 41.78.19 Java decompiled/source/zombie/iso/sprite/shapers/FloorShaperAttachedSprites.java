// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import javax.xml.bind.annotation.XmlType;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;

public class FloorShaperAttachedSprites extends FloorShaper {
    public static final FloorShaperAttachedSprites instance = new FloorShaperAttachedSprites();

    @Override
    public void accept(TextureDraw textureDraw) {
        super.accept(textureDraw);
        this.applyAttachedSpritesPadding(textureDraw);
    }

    private void applyAttachedSpritesPadding(TextureDraw textureDraw) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.IsoPaddingAttached.getValue()) {
            FloorShaperAttachedSprites.Settings settings = this.getSettings();
            FloorShaperAttachedSprites.Settings.ASBorderSetting aSBorderSetting = settings.getCurrentZoomSetting();
            float float0 = aSBorderSetting.borderThicknessUp;
            float float1 = aSBorderSetting.borderThicknessDown;
            float float2 = aSBorderSetting.borderThicknessLR;
            float float3 = aSBorderSetting.uvFraction;
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

    private FloorShaperAttachedSprites.Settings getSettings() {
        return SpritePaddingSettings.getSettings().AttachedSprites;
    }

    @XmlType(
        name = "FloorShaperAttachedSpritesSettings"
    )
    public static class Settings extends SpritePaddingSettings.GenericZoomBasedSettingGroup {
        public FloorShaperAttachedSprites.Settings.ASBorderSetting ZoomedIn = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 1.0F, 3.0F, 0.01F);
        public FloorShaperAttachedSprites.Settings.ASBorderSetting NotZoomed = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 1.0F, 3.0F, 0.01F);
        public FloorShaperAttachedSprites.Settings.ASBorderSetting ZoomedOut = new FloorShaperAttachedSprites.Settings.ASBorderSetting(2.0F, 0.0F, 2.5F, 0.0F);

        public FloorShaperAttachedSprites.Settings.ASBorderSetting getCurrentZoomSetting() {
            return getCurrentZoomSetting(this.ZoomedIn, this.NotZoomed, this.ZoomedOut);
        }

        public static class ASBorderSetting {
            public float borderThicknessUp;
            public float borderThicknessDown;
            public float borderThicknessLR;
            public float uvFraction;

            public ASBorderSetting() {
            }

            public ASBorderSetting(float float0, float float1, float float2, float float3) {
                this.borderThicknessUp = float0;
                this.borderThicknessDown = float1;
                this.borderThicknessLR = float2;
                this.uvFraction = float3;
            }
        }
    }
}
