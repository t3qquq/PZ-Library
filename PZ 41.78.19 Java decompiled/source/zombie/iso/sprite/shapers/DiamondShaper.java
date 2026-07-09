// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;

public class DiamondShaper implements Consumer<TextureDraw> {
    public static final DiamondShaper instance = new DiamondShaper();

    public void accept(TextureDraw textureDraw) {
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.MeshCutdown.getValue()) {
            float float0 = textureDraw.x0;
            float float1 = textureDraw.y0;
            float float2 = textureDraw.x1;
            float float3 = textureDraw.y1;
            float float4 = textureDraw.y2;
            float float5 = textureDraw.y3;
            float float6 = float2 - float0;
            float float7 = float4 - float3;
            float float8 = float0 + float6 * 0.5F;
            float float9 = float3 + float7 * 0.5F;
            float float10 = textureDraw.u0;
            float float11 = textureDraw.v0;
            float float12 = textureDraw.u1;
            float float13 = textureDraw.v1;
            float float14 = textureDraw.v2;
            float float15 = textureDraw.v3;
            float float16 = float12 - float10;
            float float17 = float14 - float11;
            float float18 = float10 + float16 * 0.5F;
            float float19 = float13 + float17 * 0.5F;
            textureDraw.x0 = float8;
            textureDraw.y0 = float1;
            textureDraw.u0 = float18;
            textureDraw.v0 = float11;
            textureDraw.x1 = float2;
            textureDraw.y1 = float9;
            textureDraw.u1 = float12;
            textureDraw.v1 = float19;
            textureDraw.x2 = float8;
            textureDraw.y2 = float5;
            textureDraw.u2 = float18;
            textureDraw.v2 = float15;
            textureDraw.x3 = float0;
            textureDraw.y3 = float9;
            textureDraw.u3 = float10;
            textureDraw.v3 = float19;
        }
    }
}
