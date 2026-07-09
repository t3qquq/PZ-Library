// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;

public class WorldMapPolygonStyleLayer extends WorldMapStyleLayer {
    public final ArrayList<WorldMapStyleLayer.ColorStop> m_fill = new ArrayList<>();
    public final ArrayList<WorldMapStyleLayer.TextureStop> m_texture = new ArrayList<>();
    public final ArrayList<WorldMapStyleLayer.FloatStop> m_scale = new ArrayList<>();

    public WorldMapPolygonStyleLayer(String string) {
        super(string);
    }

    @Override
    public String getTypeString() {
        return "Polygon";
    }

    @Override
    public void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs) {
        WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
        if (rGBAf.a < 0.01F) {
            WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
        } else {
            float float0 = this.evalFloat(renderArgs, this.m_scale);
            Texture texture = this.evalTexture(renderArgs, this.m_texture);
            if (texture != null && texture.isReady()) {
                renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, rGBAf, texture, float0);
            } else {
                renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, rGBAf);
            }

            WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
        }
    }
}
